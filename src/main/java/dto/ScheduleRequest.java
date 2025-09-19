package dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class ScheduleRequest {
    @NotBlank(message = "El nombre del horario es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String name;
    
    @NotNull(message = "La lista de sesiones no puede ser nula")
    @Size(min = 1, message = "Debe haber al menos una sesión")
    private List<@Valid SessionDTO> sessions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SessionDTO> getSessions() {
        return sessions;
    }

    public void setSessions(List<SessionDTO> sessions) {
        this.sessions = sessions;
    }
}
