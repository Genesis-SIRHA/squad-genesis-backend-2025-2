package model;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.UUID;
@Document(collection = "request_details")
public class RequestDetails {
    @Id
    private String id;
    @NotNull(message = "Request id is required")
    private String requestId;
    @NotNull(message = "Origin group is required")
    private LocalDate createdAt;
    private LocalDate answerAt;
    private String managedBy;
    private String answer;
    private String description;
    private Group originGroup;
    private Group destinationGroup;

    public RequestDetails(String requestId, String description, Group originGroup, Group destinationGroup) {
        this.id = UUID.randomUUID().toString();
        this.requestId = requestId;
        this.createdAt = LocalDate.now();
        this.description = description;
        this.answer = answer;
    }
    
    public RequestDetails() {
        this.id = UUID.randomUUID().toString();
        this.requestId = this.id;
        this.createdAt = LocalDate.now();
    }

    public String getId() {
        return id;
    }

    public String getRequestId() {
        return requestId;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public LocalDate getAnswerAt() {
        return answerAt;
    }

    public String getManagedBy() {
        return managedBy;
    }

    public void setManagedBy(String managedBy) {
        this.managedBy = managedBy;
    }

    public String getAnswer() {
        return answer;
    }

    public String getGestedBy() {
        return managedBy;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getAnswerDate() {
        return answerAt;
    }

    public void setAnswerDate(LocalDate answerDate) {
        this.answerAt = answerDate;
    }

    public Group getOriginGroup() {
        return originGroup;
    }

    public Group getDestinationGroup() {
        return destinationGroup;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
