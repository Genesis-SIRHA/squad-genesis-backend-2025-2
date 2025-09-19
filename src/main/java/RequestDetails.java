import java.time.LocalDate;

public class RequestDetails {

    private String requestId;
    private String createdAt;
    private LocalDate answerAt;
    private String managedBy;
    private String answer;

    public RequestDetails(String id, java.time.LocalDate fechaRespuesta, String observaciones) {
        this.requestId = id;

        this.answerAt = fechaRespuesta;
        this.answer = observaciones;
    }

    public void insert() {
        // insertar en base de datos
    }
}
