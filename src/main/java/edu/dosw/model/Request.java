package edu.dosw.model;

import edu.dosw.model.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "requests")
public class Request {
  @Id
  private String id;
  private String requestId;
  @NotBlank(message = "Student ID is required")
  private String studentId;
  @NotNull(message = "Created at date is required")
  private LocalDateTime createdAt;
  private Status status;
  private String type;
  private Boolean isExceptional;
  private String destinationGroupId;
  private String originGroupId;
  private String description;
  private String gestedBy;
  private LocalDate updatedAt;
  private String answer;

  public Request() {
    this.requestId = UUID.randomUUID().toString();
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDate.now();
    this.isExceptional = false;
    this.status = Status.PENDING;
    this.gestedBy = null;
    this.answer = null;
  }

  public Request(RequestBuilder builder) {
    this();
    this.studentId = builder.studentId;
    this.description = builder.description;
    this.type = builder.type;
    this.originGroupId = builder.originGroupId;
    this.destinationGroupId = builder.destinationGroupId;
  }

  public static class RequestBuilder{
      private String studentId;
      private String description;
      private String type;
      private String originGroupId;
      private String destinationGroupId;

      public RequestBuilder studentId(String studentId) {
        this.studentId = studentId;
        return this;
      }

      public RequestBuilder description(String description) {
        this.description = description;
        return this;
      }

      public RequestBuilder type(String type) {
        this.type = type;
        return this;
      }

      public RequestBuilder originGroupId(String originGroupId) {
        this.originGroupId = originGroupId;
        return this;
      }

      public RequestBuilder destinationGroupId(String destinationGroupId) {
        this.destinationGroupId = destinationGroupId;
        return this;
      }

      public Request build() {
        return new Request(this);
      }
  }
}
