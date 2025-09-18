package edu.dosw;

import java.time.LocalDate;

public class Solicitud {
    private String id;
    private Estudiante estudiante;
    private Grupo grupoAnterior;
    private Grupo grupoNuevo;
    private LocalDate fechaSolicitud;
    private String estado; 

    public Solicitud(String id, Estudiante estudiante,
                     Grupo grupoAnterior, Grupo grupoNuevo,
                     LocalDate fechaSolicitud, String estado) {
        this.id = id;
        this.estudiante = estudiante;
        this.grupoAnterior = grupoAnterior;
        this.grupoNuevo = grupoNuevo;
        this.fechaSolicitud = fechaSolicitud;
        this.estado = estado;
    }

    public String getId() { return id; }
    public Estudiante getEstudiante() { return estudiante; }
    public Grupo getGrupoAnterior() { return grupoAnterior; }
    public Grupo getGrupoNuevo() { return grupoNuevo; }
    public LocalDate getFechaSolicitud() { return fechaSolicitud; }
    public String getEstado() { return estado; }

    public void setEstado(String estado) { this.estado = estado; }
}
