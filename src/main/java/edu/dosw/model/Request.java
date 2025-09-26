package edu.dosw.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "requests")
public class Request {
    @Id
    private String requestId;
    @NotBlank(message = "Student ID is required")
    private String studentId;
    @NotNull(message = "Created at date is required")
    private LocalDateTime createdAt;
    private String status = "PENDING";
    private String type;
    private Boolean isExceptional = false;
    private String destinationGroupId;
    private String originGroupId;
    private String description;
    private String gestedBy;
    private LocalDate answerAt;
    private String answer;

    public Request() {
        this.requestId = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }

    public Request(String studentId, String description, String type, String originGroupId, String destinationGroup) {
        this();
        this.studentId = studentId;
        this.description = description;
        this.type = type;
        this.originGroupId = originGroupId;
        this.destinationGroupId = destinationGroup;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getExceptional() {
        return isExceptional;
    }

    public void setExceptional(Boolean exceptional) {
        isExceptional = exceptional;
    }

    public String getDestinationGroupId() {
        return destinationGroupId;
    }

    public void setDestinationGroupId(String destinationGroupId) {
        this.destinationGroupId = destinationGroupId;
    }

    public String getOriginGroupId() {
        return originGroupId;
    }

    public void setOriginGroupId(String originGroupId) {
        this.originGroupId = originGroupId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGestedBy() {
        return gestedBy;
    }

    public void setGestedBy(String gestedBy) {
        this.gestedBy = gestedBy;
    }

    public LocalDate getAnswerAt() {
        return answerAt;
    }

    public void setAnswerAt(LocalDate answerAt) {
        this.answerAt = answerAt;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}