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
    private String id;
    
    @NotBlank(message = "Student ID is required")
    private String studentId;
    
    private String description;
    private String status = "PENDING";
    
    @NotNull(message = "Created at date is required")
    private LocalDateTime createdAt;
    
    private String type;
    private Boolean isExceptional = false;

    private LocalDate answerAt;
    private String managedBy;
    private String answer;
    private Group originGroup;
    private Group destinationGroup;
    private String gestedBy;
    private LocalDate answerDate;

    public Request() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }
    
    public Request(String studentId, String description, String type, Group originGroup, Group destinationGroup) {
        this();
        this.studentId = studentId;
        this.description = description;
        this.type = type;
        this.originGroup = originGroup;
        this.destinationGroup = destinationGroup;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsExceptional() {
        return isExceptional;
    }
    public void setIsExceptional(Boolean isExceptional) {
        this.isExceptional = isExceptional;
    }

    public LocalDate getAnswerAt() {
        return answerAt;
    }

    public void setAnswerAt(LocalDate answerAt) {
        this.answerAt = answerAt;
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

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Group getOriginGroup() {
        return originGroup;
    }

    public void setOriginGroup(Group originGroup) {
        this.originGroup = originGroup;
    }

    public Group getDestinationGroup() {
        return destinationGroup;
    }

    public void setDestinationGroup(Group destinationGroup) {
        this.destinationGroup = destinationGroup;
    }

    public String getGestedBy() {
        return gestedBy;
    }

    public void setGestedBy(String gestedBy) {
        this.gestedBy = gestedBy;
    }

    public void setAnswerDate(LocalDate answerDate) {
        this.answerDate = answerDate;
    }
}
