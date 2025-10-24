package edu.dosw.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import edu.dosw.services.GroupService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class GroupControllerNotificationTest {

  private MockMvc mockMvc;

  @Mock private GroupService groupService;

  @InjectMocks private GroupController groupController;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(groupController).build();
  }

  @Test
  void testGetCapacityNotifications_DeberiaRetornarListaDeNotificaciones() throws Exception {
    List<String> notificacionesEsperadas =
        Arrays.asList(
            "Grupo G01 - MAT101: Capacidad al 95.0% (19/20 estudiantes)",
            "Grupo G02 - FIS201: Capacidad al 92.5% (37/40 estudiantes)");

    when(groupService.getCapacityNotifications()).thenReturn(notificacionesEsperadas);

    mockMvc
        .perform(get("/group/notifications").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0]").value(notificacionesEsperadas.get(0)))
        .andExpect(jsonPath("$[1]").value(notificacionesEsperadas.get(1)));

    verify(groupService).getCapacityNotifications();
  }

  @Test
  void testGetCapacityNotifications_DeberiaRetornarListaVacia() throws Exception {
    when(groupService.getCapacityNotifications()).thenReturn(Arrays.asList());

    mockMvc
        .perform(get("/group/notifications").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));

    verify(groupService).getCapacityNotifications();
  }

  @Test
  void testClearCapacityNotifications_DeberiaRetornar204() throws Exception {
    doNothing().when(groupService).clearCapacityNotifications();

    mockMvc
        .perform(delete("/group/notifications").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    verify(groupService).clearCapacityNotifications();
  }

  @Test
  void testClearCapacityNotifications_DeberiaEjecutarseCuandoNoHayNotificaciones()
      throws Exception {
    doNothing().when(groupService).clearCapacityNotifications();

    mockMvc
        .perform(delete("/group/notifications").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    verify(groupService).clearCapacityNotifications();
  }

  @Test
  void testNotificationEndpoints_DeberiaTenerContentTypeAdecuado() throws Exception {
    when(groupService.getCapacityNotifications()).thenReturn(Arrays.asList());

    mockMvc
        .perform(get("/group/notifications"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    doNothing().when(groupService).clearCapacityNotifications();

    mockMvc.perform(delete("/group/notifications")).andExpect(status().isNoContent());
  }
}
