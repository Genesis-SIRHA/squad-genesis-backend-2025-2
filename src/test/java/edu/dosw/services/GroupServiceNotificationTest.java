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
            .enrolled(18)
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
            .enrolled(10)
            .maxCapacity(20)
            .build();
  }

  // Tests existentes...
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
            .year("2023")
            .period("2")
            .build();

    when(groupRepository.findByGroupCode("G01")).thenReturn(Optional.of(grupoPeriodoIncorrecto));
    when(periodService.getYear()).thenReturn("2024");
    when(periodService.getPeriod()).thenReturn("1");

    assertThrows(IllegalArgumentException.class, () -> groupService.deleteStudent("G01", "STU123"));

    verify(groupRepository, never()).save(any());
    verify(groupCapacityNotifier, never()).checkAndNotify(any());
  }

  @Test
  void testGetCapacityNotifications_DeberiaRetornarListaDeNotificaciones() {
    List<String> notificacionesEsperadas =
        Arrays.asList(
            "Grupo G01 - MAT101: Capacidad al 95.0% (19/20 estudiantes)",
            "Grupo G02 - FIS201: Capacidad al 92.5% (37/40 estudiantes)");

    when(messageGroupObserver.getNotifications()).thenReturn(notificacionesEsperadas);

    List<String> resultado = groupService.getCapacityNotifications();

    assertNotNull(resultado);
    assertEquals(2, resultado.size());
    assertEquals(notificacionesEsperadas, resultado);
    verify(messageGroupObserver).getNotifications();
  }

  @Test
  void testGetCapacityNotifications_DeberiaRetornarListaVaciaCuandoNoHayNotificaciones() {

    when(messageGroupObserver.getNotifications()).thenReturn(Arrays.asList());

    List<String> resultado = groupService.getCapacityNotifications();

    assertNotNull(resultado);
    assertTrue(resultado.isEmpty());
    verify(messageGroupObserver).getNotifications();
  }

  @Test
  void testClearCapacityNotifications_DeberiaLimpiarNotificaciones() {
    doNothing().when(messageGroupObserver).clearNotifications();

    groupService.clearCapacityNotifications();

    verify(messageGroupObserver).clearNotifications();
  }

  @Test
  void testClearCapacityNotifications_DeberiaEjecutarseSinErroresCuandoNoHayNotificaciones() {
    doNothing().when(messageGroupObserver).clearNotifications();

    assertDoesNotThrow(() -> groupService.clearCapacityNotifications());
    verify(messageGroupObserver).clearNotifications();
  }
}
