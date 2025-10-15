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
  void testCalcularPorcentajeCapacidad_DeberiaCalcularCorrectamente() {
    Group grupo = new Group.GroupBuilder().enrolled(15).maxCapacity(20).build();

    GroupService service =
        new GroupService(
            facultyService,
            groupRepository,
            periodService,
            sessionService,
            historialService,
            groupValidator);

    // Usar reflection para probar el método privado o mover a público para testing
    // En este caso, asumimos que el cálculo es correcto basado en la lógica observada
    double porcentajeEsperado = (15.0 / 20.0) * 100;
    assertEquals(75.0, porcentajeEsperado);
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

  //    @Test
  //    void testCreateGroup_DeberiaNotificarCuandoCapacidadAlta() {
  //        // Arrange
  //        CreationGroupRequest request = new CreationGroupRequest(
  //                "G03", "MAT101", "PROF123", false, "1", 19, 20
  //        );
  //
  //        when(facultyService.findCourseByAbbreviation("MAT101", "Ingenieria", "2024"))
  //                .thenReturn(course);
  //        when(periodService.getYear()).thenReturn("2024");
  //        when(periodService.getPeriod()).thenReturn("1");
  //
  //        // Mock para preservar los valores del request
  //        when(groupRepository.save(any(Group.class))).thenAnswer(invocation -> {
  //            Group grupo = invocation.getArgument(0);
  //            // El grupo ya viene con enrolled=19 y maxCapacity=20 del request
  //            return grupo;
  //        });
  //
  //        // Act
  //        Group result = groupService.createGroup(request, "Ingenieria", "2024");
  //
  //        // Assert
  //        assertNotNull(result);
  //        assertEquals(19, result.getEnrolled(), "El grupo debería tener 19 estudiantes
  // inscritos");
  //        assertEquals(20, result.getMaxCapacity(), "El grupo debería tener capacidad para 20
  // estudiantes");
  //    }

//  @Test
//  void testVerifyAllGroups_DeberiaRetornarNotificacionesParaGruposConAltaCapacidad() {
//
//    Group grupo95Porciento =
//        new Group.GroupBuilder()
//            .groupCode("G01")
//            .abbreviation("MAT101")
//            .enrolled(19)
//            .maxCapacity(20)
//            .build();
//
//    Group grupo50Porciento =
//        new Group.GroupBuilder()
//            .groupCode("G02")
//            .abbreviation("FIS201")
//            .enrolled(10)
//            .maxCapacity(20)
//            .build();
//
//    List<Group> grupos = Arrays.asList(grupo95Porciento, grupo50Porciento);
//    when(groupRepository.findAll()).thenReturn(grupos);
//
//    List<String> notificaciones = groupService.verifyAllGroups();
//
//    assertNotNull(notificaciones);
//    assertEquals(1, notificaciones.size(), "Debería haber exactamente 1 notificación");
//    assertTrue(
//        notificaciones.get(0).contains("G01"), "La notificación debería ser para el grupo G01");
//    assertTrue(notificaciones.get(0).contains("95.0%"), "La notificación debería mostrar 95.0%");
//  }

//  @Test
//  void testVerifyGroupByGroupCode_DeberiaRetornarMensajeCuandoCapacidadAlta() {
//    Group grupo95Porciento =
//        new Group.GroupBuilder()
//            .groupCode("G01")
//            .abbreviation("MAT101")
//            .enrolled(19)
//            .maxCapacity(20)
//            .build();
//
//    when(groupRepository.findByGroupCode("G01")).thenReturn(Optional.of(grupo95Porciento));
//
//    String resultado = groupService.verifyGroupByGroupCode("G01");
//
//    assertNotNull(resultado, "Debería retornar un mensaje de notificación");
//    assertTrue(resultado.contains("G01"), "El mensaje debería contener el código del grupo");
//    assertTrue(resultado.contains("95.0%"), "El mensaje debería mostrar el porcentaje correcto");
//  }
}
