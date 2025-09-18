package edu.dosw;

import java.time.LocalDate;
import java.util.UUID;

public class SolicitudBuilder {
    private Estudiante estudiante;
    private Grupo grupoAnterior;
    private Grupo grupoNuevo;
    private final SolicitudService service;

    public SolicitudBuilder(SolicitudService service) {
        this.service = service;
    }

    public SolicitudBuilder withEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
        return this;
    }

    public SolicitudBuilder withGrupoAnterior(Grupo grupoAnterior) {
        this.grupoAnterior = grupoAnterior;
        return this;
    }

    public SolicitudBuilder withGrupoNuevo(Grupo grupoNuevo) {
        this.grupoNuevo = grupoNuevo;
        return this;
    }

    public void buildAndSave() {
        Solicitud solicitud = new Solicitud(
            UUID.randomUUID().toString(),
            estudiante,
            grupoAnterior,
            grupoNuevo,
            LocalDate.now(),
            "PENDIENTE"
        );
        service.crearSolicitud(solicitud);
    }
}
