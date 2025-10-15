package edu.dosw.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import edu.dosw.dto.CreationGroupRequest;
import edu.dosw.dto.UpdateGroupRequest;
import edu.dosw.model.Group;
import edu.dosw.services.GroupService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class GroupControllerNotificationTest {

  @Mock private GroupService groupService;

  @InjectMocks private GroupController groupController;

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
            .enrolled(18) // 90% de capacidad
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
            .enrolled(10) // 50% de capacidad
            .maxCapacity(20)
            .build();
  }

  @Test
  void testGetGroupByCode_DeberiaRetornarGrupoYEjecutarVerificacion() {

    when(groupService.getGroupByGroupCode("G01")).thenReturn(grupoConAltaCapacidad);

    ResponseEntity<Group> response = groupController.getGroupByCode("G01");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("G01", response.getBody().getGroupCode());
  }

  @Test
  void testCreateGroup_DeberiaCrearGrupoYEjecutarVerificacion() {

    CreationGroupRequest request =
        new CreationGroupRequest("G03", "MAT101", "PROF123", false, "1", 19, 20);
    when(groupService.createGroup(any(CreationGroupRequest.class), anyString(), anyString()))
        .thenReturn(grupoConAltaCapacidad);

    ResponseEntity<Group> response = groupController.createGroup(request, "Ingenieria", "2024");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void testUpdateGroup_DeberiaActualizarGrupoYEjecutarVerificacion() {

    UpdateGroupRequest request = new UpdateGroupRequest(null, null, null, null, 19);
    when(groupService.updateGroup(anyString(), any(UpdateGroupRequest.class)))
        .thenReturn(grupoConAltaCapacidad);

    ResponseEntity<Group> response = groupController.updateGroup("G01", request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void testAddStudent_DeberiaAgregarEstudianteYEjecutarVerificacion() {

    when(groupService.addStudent("G01", "STU123")).thenReturn(grupoConAltaCapacidad);

    ResponseEntity<Group> response = groupController.addStudent("G01", "STU123");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void testRemoveStudent_DeberiaEliminarEstudianteYEjecutarVerificacion() {
    when(groupService.deleteStudent("G01", "STU123")).thenReturn(grupoConAltaCapacidad);

    ResponseEntity<Group> response = groupController.removeStudent("G01", "STU123");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void testVerifyAllGroups_DeberiaRetornarListaDeNotificaciones() {
    List<String> notificacionesMock =
        Arrays.asList(
            " Grupo G01 - MAT101: Capacidad al 90.0% (18/20 estudiantes)",
            " Grupo G03 - QUIM101: Capacidad al 95.0% (19/20 estudiantes)");
    when(groupService.verifyAllGroups()).thenReturn(notificacionesMock);

    ResponseEntity<List<String>> response = groupController.VerifyAllGroups();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().size());
    assertTrue(response.getBody().get(0).contains("G01"));
  }

  @Test
  void testVerifyAllGroups_DeberiaManejarErrorInterno() {

    when(groupService.verifyAllGroups()).thenThrow(new RuntimeException("Error de base de datos"));

    ResponseEntity<List<String>> response = groupController.VerifyAllGroups();

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().get(0).contains("error"));
  }

  @Test
  void testVerifyGroupByGroupCode_DeberiaRetornarNotificacionCuandoCapacidadAlta() {

    String notificacionMock = " Grupo G01 - MAT101: Capacidad al 90.0% (18/20 estudiantes)";
    when(groupService.verifyGroupByGroupCode("G01")).thenReturn(notificacionMock);

    ResponseEntity<String> response = groupController.verifyGroupByGroupCode("G01");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().contains("90.0%"));
  }

  @Test
  void testVerifyGroupByGroupCode_DeberiaRetornarMensajeCuandoGrupoNoExiste() {

    when(groupService.verifyGroupByGroupCode("G99"))
        .thenReturn("Grupo no encontrado con código: G99");

    ResponseEntity<String> response = groupController.verifyGroupByGroupCode("G99");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().contains("no encontrado"));
  }

  @Test
  void testGetGroupCapacity_DeberiaRetornarMetricasConAlertaTrue() {

    when(groupService.getGroupByGroupCode("G01")).thenReturn(grupoConAltaCapacidad);

    ResponseEntity<Map<String, Object>> response = groupController.GetGroupCapacity("G01");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());

    Map<String, Object> metricas = response.getBody();
    assertEquals("G01", metricas.get("grupo"));
    assertEquals("MAT101", metricas.get("materia"));
    assertEquals(18, metricas.get("inscritos"));
    assertEquals(20, metricas.get("capacidadMaxima"));
    assertEquals(90.0, metricas.get("porcentaje"));
    assertTrue((Boolean) metricas.get("alerta"));
  }

  @Test
  void testGetGroupCapacity_DeberiaRetornarMetricasConAlertaFalse() {

    when(groupService.getGroupByGroupCode("G02")).thenReturn(grupoConBajaCapacidad);

    ResponseEntity<Map<String, Object>> response = groupController.GetGroupCapacity("G02");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());

    Map<String, Object> metricas = response.getBody();
    assertEquals("G02", metricas.get("grupo"));
    assertEquals(50.0, metricas.get("porcentaje"));
    assertFalse((Boolean) metricas.get("alerta"));
  }

  @Test
  void testGetGroupCapacity_DeberiaRetornarNotFoundCuandoGrupoNoExiste() {

    when(groupService.getGroupByGroupCode("G99")).thenReturn(null);

    ResponseEntity<Map<String, Object>> response = groupController.GetGroupCapacity("G99");

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }
}
