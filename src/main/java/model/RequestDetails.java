package model;

import java.time.LocalDate;

public class RequestDetails {

    private String requestId;
    private String createdAt;
    private LocalDate answerAt;
    private String managedBy;
    private String answer;

    public RequestDetails(String id, LocalDate fechaRespuesta, String observaciones) {
        this.requestId = id;
        this.answerAt = fechaRespuesta;
        this.answer = observaciones;
    }

}
