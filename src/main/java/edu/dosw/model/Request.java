package edu.dosw.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@AllArgsConstructor
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
        this.destinationGroupId= destinationGroup;
    }

}