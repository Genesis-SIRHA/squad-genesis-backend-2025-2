package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.HistorialDTO;
import edu.dosw.exception.BusinessException;
import edu.dosw.model.Group;
import edu.dosw.model.enums.HistorialStatus;
import edu.dosw.observer.GroupCapacityNotifier;
import edu.dosw.observer.MessageGroupObserver;
import edu.dosw.repositories.GroupRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroupServiceNotificationTest {

  @Mock private GroupRepository groupRepository;
  @Mock private PeriodService periodService;
  @Mock private HistorialService historialService;
  @Mock private GroupValidator groupValidator;
  @Mock private GroupCapacityNotifier groupCapacityNotifier;
  @Mock private MessageGroupObserver messageGroupObserver;

  @InjectMocks private GroupService groupService;

  private Group grupoConAltaCapacidad;
  private Group grupoConBajaCapacidad;

  @BeforeEach
  void setUp() {
    grupoConAltaCapacidad =
        new Group.GroupBuilder()
            .groupCode("G01")
            .abbreviation("MAT101")
            .year("2024")
            .period("1")
            .professorId("PROF123")
            .isLab(false)
            .groupNum("1")
            .enrolled(18) // 90%
            .maxCapacity(20)
            .build();

    grupoConBajaCapacidad =
        new Group.GroupBuilder()
            .groupCode("G02")
            .abbreviation("FIS201")
            .year("2024")
            .period("1")
            .professorId("PROF456")
            .isLab(false)
            .groupNum("1")
            .enrolled(10) // 50%
            .maxCapacity(20)
            .build();
  }

  @Test
  void testAddStudent_DeberiaNotificarCuandoAlcanzaUmbral() {
    Group grupoAntes =
        new Group.GroupBuilder()
            .groupCode("G01")
            .abbreviation("MAT101")
            .enrolled(17)
            .maxCapacity(20)
            .build();

    when(groupRepository.findByGroupCode("G01")).thenReturn(Optional.of(grupoAntes));
    when(groupRepository.save(any(Group.class)))
        .thenAnswer(
            invocation -> {
              Group savedGroup = invocation.getArgument(0);
              savedGroup.setEnrolled(18);
              return savedGroup;
            });
    doNothing().when(groupValidator).validateAddStudentToGroup(any(Group.class), anyString());
    when(historialService.addHistorial(any(HistorialDTO.class))).thenReturn(null);
    doNothing().when(groupCapacityNotifier).checkAndNotify(any(Group.class));

    Group result = groupService.addStudent("G01", "STU123");

    assertNotNull(result);
    verify(groupCapacityNotifier).checkAndNotify(any(Group.class));
  }

  @Test
  void testDeleteStudent_DeberiaNotificarSiSigueEnUmbral() {
    Group grupoAntes =
        new Group.GroupBuilder()
            .groupCode("G01")
            .abbreviation("MAT101")
            .enrolled(19)
            .maxCapacity(20)
            .year("2024")
            .period("1")
            .build();

    when(groupRepository.findByGroupCode("G01")).thenReturn(Optional.of(grupoAntes));
    when(periodService.getYear()).thenReturn("2024");
    when(periodService.getPeriod()).thenReturn("1");
    when(groupRepository.save(any(Group.class)))
        .thenAnswer(
            invocation -> {
              Group savedGroup = invocation.getArgument(0);
              savedGroup.setEnrolled(18);
              return savedGroup;
            });
    when(historialService.updateHistorial(anyString(), anyString(), any(HistorialStatus.class)))
        .thenReturn(null);
    doNothing().when(groupCapacityNotifier).checkAndNotify(any(Group.class));

    Group result = groupService.deleteStudent("G01", "STU123");

    assertNotNull(result);
    verify(groupCapacityNotifier).checkAndNotify(any(Group.class));
  }

  @Test
  void testVerifyGroupByGroupCode_DeberiaRetornarNullCuandoCapacidadBaja() {
    when(groupRepository.findByGroupCode("G02")).thenReturn(Optional.of(grupoConBajaCapacidad));
    doNothing().when(groupCapacityNotifier).checkAndNotify(grupoConBajaCapacidad);
    when(messageGroupObserver.getNotifications()).thenReturn(List.of());

    String resultado = groupService.verifyGroupByGroupCode("G02");

    assertNull(resultado);
    verify(messageGroupObserver).clearNotifications();
    verify(groupCapacityNotifier).checkAndNotify(grupoConBajaCapacidad);
  }

  @Test
  void testVerifyGroupByGroupCode_DeberiaRetornarMensajeErrorCuandoGrupoNoExiste() {
    when(groupRepository.findByGroupCode("G99")).thenReturn(Optional.empty());

    String resultado = groupService.verifyGroupByGroupCode("G99");

    assertNotNull(resultado);
    assertTrue(resultado.contains("Grupo no encontrado"));
    verify(messageGroupObserver, never()).clearNotifications();
    verify(groupCapacityNotifier, never()).checkAndNotify(any());
  }

  @Test
  void testVerifyGroupByGroupCode_DeberiaRetornarMensajeCuandoCapacidadAlta() {
    when(groupRepository.findByGroupCode("G01")).thenReturn(Optional.of(grupoConAltaCapacidad));
    doNothing().when(groupCapacityNotifier).checkAndNotify(grupoConAltaCapacidad);
    when(messageGroupObserver.getNotifications())
        .thenReturn(List.of(" Grupo G01 - MAT101: Capacidad al 90.0% (18/20 estudiantes)"));

    String resultado = groupService.verifyGroupByGroupCode("G01");

    assertNotNull(resultado);
    assertTrue(resultado.contains("G01"));
    assertTrue(resultado.contains("90.0%"));

    verify(messageGroupObserver).clearNotifications();
    verify(groupCapacityNotifier).checkAndNotify(grupoConAltaCapacidad);
  }

  @Test
  void testVerifyAllGroups_DeberiaRetornarNotificacionesParaGruposConAltaCapacidad() {
    List<Group> grupos = Arrays.asList(grupoConAltaCapacidad, grupoConBajaCapacidad);
    when(groupRepository.findAll()).thenReturn(grupos);
    doNothing().when(groupCapacityNotifier).checkAndNotify(any(Group.class));
    when(messageGroupObserver.getNotifications())
        .thenReturn(List.of(" Grupo G01 - MAT101: Capacidad al 90.0% (18/20 estudiantes)"));

    List<String> notificaciones = groupService.verifyAllGroups();

    assertNotNull(notificaciones);
    assertEquals(1, notificaciones.size());
    assertTrue(notificaciones.get(0).contains("G01"));

    verify(messageGroupObserver).clearNotifications();
    verify(groupCapacityNotifier, times(2)).checkAndNotify(any(Group.class));
  }

  @Test
  void testVerifyAllGroups_DeberiaRetornarListaVaciaCuandoNoHayNotificaciones() {
    List<Group> grupos = Arrays.asList(grupoConBajaCapacidad);
    when(groupRepository.findAll()).thenReturn(grupos);
    doNothing().when(groupCapacityNotifier).checkAndNotify(any(Group.class));
    when(messageGroupObserver.getNotifications()).thenReturn(List.of());

    List<String> notificaciones = groupService.verifyAllGroups();

    assertNotNull(notificaciones);
    assertTrue(notificaciones.isEmpty());

    verify(messageGroupObserver).clearNotifications();
    verify(groupCapacityNotifier).checkAndNotify(grupoConBajaCapacidad);
  }

  @Test
  void testAddStudent_DeberiaLanzarExcepcionCuandoValidacionFalla() {
    when(groupRepository.findByGroupCode("G01")).thenReturn(Optional.of(grupoConAltaCapacidad));
    doThrow(new BusinessException("Validación fallida"))
        .when(groupValidator)
        .validateAddStudentToGroup(any(Group.class), anyString());

    assertThrows(BusinessException.class, () -> groupService.addStudent("G01", "STU123()"));

    verify(groupRepository, never()).save(any());
    verify(groupCapacityNotifier, never()).checkAndNotify(any());
  }

  @Test
  void testDeleteStudent_DeberiaLanzarExcepcionCuandoPeriodoNoCoincide() {
    Group grupoPeriodoIncorrecto =
        new Group.GroupBuilder()
            .groupCode("G01")
            .abbreviation("MAT101")
            .enrolled(19)
            .maxCapacity(20)
            .year("2023") // Año diferente
            .period("2") // Periodo diferente
            .build();

    when(groupRepository.findByGroupCode("G01")).thenReturn(Optional.of(grupoPeriodoIncorrecto));
    when(periodService.getYear()).thenReturn("2024");
    when(periodService.getPeriod()).thenReturn("1");

    assertThrows(IllegalArgumentException.class, () -> groupService.deleteStudent("G01", "STU123"));

    verify(groupRepository, never()).save(any());
    verify(groupCapacityNotifier, never()).checkAndNotify(any());
  }

  @Test
  void testMultipleObservers_DeberiaFuncionarCorrectamenteConVariosObservers() {
    when(groupRepository.findByGroupCode("G01")).thenReturn(Optional.of(grupoConAltaCapacidad));
    doNothing().when(groupCapacityNotifier).checkAndNotify(grupoConAltaCapacidad);
    when(messageGroupObserver.getNotifications())
        .thenReturn(List.of("Notificación 1", "Notificación 2"));

    String resultado = groupService.verifyGroupByGroupCode("G01");

    assertNotNull(resultado);
    assertEquals("Notificación 1", resultado);
  }
}
