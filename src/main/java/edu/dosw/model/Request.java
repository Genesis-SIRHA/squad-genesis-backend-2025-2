package edu.dosw.model;

import edu.dosw.dto.GroupResponse;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Document(collection = "requests")
public class Request {

    @Id
    private String id;

    @NotBlank
    private String studentId;

    private String type;
    private Boolean isExceptional;
    private String status;
    private String description;

    private GroupResponse originGroup;
    private GroupResponse destinationGroup;

    private String answer;
    private String managedBy;

    private LocalDateTime createdAt;
    private LocalDateTime answerAt;

    public Request() {
        this.isExceptional = false;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    public Request(String studentId, String description, String type,
                   Group originGroup, Group destinationGroup) {
        this.studentId = studentId;
        this.description = description;
        this.type = type;
        this.originGroup = new GroupResponse(
                originGroup.getGroupCode(),
                originGroup.getProfessor(),
                originGroup.getCapacity(),
                originGroup.getEnrolled()
        );
        this.destinationGroup = new GroupResponse(
                destinationGroup.getGroupCode(),
                destinationGroup.getProfessor(),
                destinationGroup.getCapacity(),
                destinationGroup.getEnrolled()
        );
        this.isExceptional = false;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Boolean getIsExceptional() { return isExceptional; }
    public void setIsExceptional(Boolean isExceptional) { this.isExceptional = isExceptional; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public GroupResponse getOriginGroup() { return originGroup; }
    public void setOriginGroup(GroupResponse originGroup) { this.originGroup = originGroup; }

    public GroupResponse getDestinationGroup() { return destinationGroup; }
    public void setDestinationGroup(GroupResponse destinationGroup) { this.destinationGroup = destinationGroup; }


    public String getOriginGroupId() {
        return originGroup != null ? originGroup.getGroupCode() : null;
    }

    public String getDestinationGroupId() {
        return destinationGroup != null ? destinationGroup.getGroupCode(): null;
    }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public String getManagedBy() { return managedBy; }
    public void setManagedBy(String managedBy) { this.managedBy = managedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getAnswerAt() { return answerAt; }
    public void setAnswerAt(LocalDateTime answerAt) { this.answerAt = answerAt; }
}
