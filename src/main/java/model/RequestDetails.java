import java.time.LocalDate;
import java.util.Date;

public class RequestDetails {
    private String requestId;
    private String createdAt;
    private LocalDate answerAt;
    private String managedBy;
    private String answer;

    public RequestDetails() {
        this.createdAt = new Date().toString();
    }

    public RequestDetails(String requestId, LocalDate answerAt, String answer) {
        this.requestId = requestId;
        this.createdAt = new Date().toString();
        this.answerAt = answerAt;
        this.answer = answer;
    }

    public void insert() {
        // insertar en base de datos
    }


    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public LocalDate getAnswerAt() { return answerAt; }
    public void setAnswerAt(LocalDate answerAt) { this.answerAt = answerAt; }

    public String getManagedBy() { return managedBy; }
    public void setManagedBy(String managedBy) { this.managedBy = managedBy; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}