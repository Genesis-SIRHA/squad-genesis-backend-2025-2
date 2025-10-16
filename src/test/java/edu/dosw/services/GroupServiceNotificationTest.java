package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import edu.dosw.dto.HistorialDTO;
import edu.dosw.dto.UpdateGroupRequest;
import edu.dosw.exception.BusinessException;
import edu.dosw.model.Course;
import edu.dosw.model.Group;
import edu.dosw.model.enums.HistorialStatus;
import edu.dosw.repositories.GroupRepository;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
class GroupServiceNotificationTest {

  @Mock private FacultyService facultyService;

  @Mock private GroupRepository groupRepository;

  @Mock private PeriodService periodService;

  @Mock private SessionService sessionService;

  @Mock private HistorialService historialService;

  @Mock private GroupValidator groupValidator;

  @Mock private Logger logger;

  @InjectMocks private GroupService groupService;

  @Captor private ArgumentCaptor<Group> groupCaptor;

  private Group grupoConAltaCapacidad;
  private Group grupoConBajaCapacidad;
  private Course course;

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
            .enrolled(10)
            .maxCapacity(20)
            .build();

    course = new Course();
    course.setAbbreviation("MAT101");
  }

  @Test
  void testGetGroupByGroupCode_DeberiaNotificarCuandoCapacidadAlta() {
    when(groupRepository.findByGroupCode("G01")).thenReturn(Optional.of(grupoConAltaCapacidad));

    Group result = groupService.getGroupByGroupCode("G01");

    assertNotNull(result);
    assertEquals("G01", result.getGroupCode());
  }

  @Test
  void testGetGroupByGroupCode_NoDeberiaNotificarCuandoCapacidadBaja() {
    // Arrange
    when(groupRepository.findByGroupCode("G02")).thenReturn(Optional.of(grupoConBajaCapacidad));

    Group result = groupService.getGroupByGroupCode("G02");

    assertNotNull(result);
    assertEquals("G02", result.getGroupCode());
  }

  @Test
  void testUpdateGroup_DeberiaNotificarCuandoCapacidadSuperaUmbral() {
    Group grupoActual =
        new Group.GroupBuilder()
            .groupCode("G01")
            .abbreviation("MAT101")
            .enrolled(15)
            .maxCapacity(20)
            .build();

    UpdateGroupRequest request = new UpdateGroupRequest(null, null, null, null, 19); // 95%

    when(groupRepository.findByGroupCode("G01")).thenReturn(Optional.of(grupoActual));
    when(groupRepository.save(any(Group.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Group result = groupService.updateGroup("G01", request);

    assertNotNull(result);
    assertEquals(19, result.getEnrolled());
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

    Group result = groupService.addStudent("G01", "STU123");

    assertNotNull(result);
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

    Group result = groupService.deleteStudent("G01", "STU123");

    assertNotNull(result);
  }

  @Test
  void testVerifyGroupByGroupCode_DeberiaRetornarNullCuandoCapacidadBaja() {

    when(groupRepository.findByGroupCode("G02")).thenReturn(Optional.of(grupoConBajaCapacidad));

    String resultado = groupService.verifyGroupByGroupCode("G02");

    assertNull(resultado);
  }

  @Test
  void testVerifyGroupByGroupCode_DeberiaRetornarMensajeErrorCuandoGrupoNoExiste() {
    when(groupRepository.findByGroupCode("G99")).thenReturn(Optional.empty());

    String resultado = groupService.verifyGroupByGroupCode("G99");

    assertNotNull(resultado);
    assertTrue(resultado.contains("Grupo no encontrado"));
  }

  @Test
  void testCalcularPorcentajeCapacidad_DeberiaCalcularCorrectamente() throws Exception {
    Group grupo =
        new Group.GroupBuilder()
            .groupCode("TEST")
            .abbreviation("TEST")
            .enrolled(15)
            .maxCapacity(20)
            .build();

    Method method =
        GroupService.class.getDeclaredMethod("calcularPorcentajeCapacidad", Group.class);
    method.setAccessible(true);

    double porcentaje = (double) method.invoke(groupService, grupo);


    assertEquals(75.0, porcentaje, 0.01, "Debería calcular 75.0% para 15/20 estudiantes");
  }

  @Test
  void testCalcularPorcentajeCapacidad_DeberiaManejarCeroCapacidad() throws Exception {
    Group grupo =
        new Group.GroupBuilder()
            .groupCode("TEST")
            .abbreviation("TEST")
            .enrolled(10)
            .maxCapacity(0) // División por cero
            .build();

    Method method =
        GroupService.class.getDeclaredMethod("calcularPorcentajeCapacidad", Group.class);
    method.setAccessible(true);

    double porcentaje = (double) method.invoke(groupService, grupo);

    assertEquals(0.0, porcentaje, 0.01, "Debería retornar 0 cuando maxCapacity es 0");
  }

  @Test
  void testCalcularPorcentajeCapacidad_DeberiaCalcular100Porciento() throws Exception {
    Group grupo =
        new Group.GroupBuilder()
            .groupCode("TEST")
            .abbreviation("TEST")
            .enrolled(20)
            .maxCapacity(20)
            .build();

    Method method =
        GroupService.class.getDeclaredMethod("calcularPorcentajeCapacidad", Group.class);
    method.setAccessible(true);

    double porcentaje = (double) method.invoke(groupService, grupo);

    assertEquals(100.0, porcentaje, 0.01, "Debería calcular 100.0% para 20/20 estudiantes");
  }

  @Test
  void testAddStudent_DeberiaLanzarExcepcionCuandoValidacionFalla() {
    when(groupRepository.findByGroupCode("G01")).thenReturn(Optional.of(grupoConAltaCapacidad));
    doThrow(new BusinessException("Validación fallida"))
        .when(groupValidator)
        .validateAddStudentToGroup(any(Group.class), anyString());

    assertThrows(
        BusinessException.class,
        () -> {
          groupService.addStudent("G01", "STU123");
        });
  }

  @Test
  void testVerifyAllGroups_DeberiaRetornarNotificacionesParaGruposConAltaCapacidad() {

    List<Group> grupos = Arrays.asList(grupoConAltaCapacidad, grupoConBajaCapacidad);
    when(groupRepository.findAll()).thenReturn(grupos);

    List<String> notificaciones = groupService.verifyAllGroups();

    assertNotNull(notificaciones);
    assertEquals(1, notificaciones.size());
    assertTrue(notificaciones.get(0).contains("G01"));

    boolean tienePorcentajeCorrecto = notificaciones.get(0).contains("90,0%");
    assertTrue(tienePorcentajeCorrecto, "Debería contener el porcentaje 90%");
  }

  @Test
  void testVerifyGroupByGroupCode_DeberiaRetornarMensajeCuandoCapacidadAlta() {
    when(groupRepository.findByGroupCode("G01")).thenReturn(Optional.of(grupoConAltaCapacidad));

    String resultado = groupService.verifyGroupByGroupCode("G01");

    assertNotNull(resultado);
    assertTrue(resultado.contains("G01"));

    boolean tienePorcentajeCorrecto = resultado.contains("90,0%");
    assertTrue(tienePorcentajeCorrecto, "Debería contener el porcentaje 90%");
  }
}
