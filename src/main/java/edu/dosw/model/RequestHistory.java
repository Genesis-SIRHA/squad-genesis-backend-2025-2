package edu.dosw.model;

import edu.dosw.model.enums.RequestStatus;
import edu.dosw.model.enums.RequestType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "requestHistory")
public class RequestHistory extends Request{

    public RequestHistory(RequestHistoryBuilder builder) {
        super(builder.id,
                builder.requestId,
                builder.studentId,
                builder.createdAt,
                builder.status,
                builder.type,
                builder.isExceptional,
                builder.destinationGroupId,
                builder.originGroupId,
                builder.description,
                builder.gestedBy,
                builder.updatedAt,
                builder.answer);
    }

    public static class RequestHistoryBuilder {
        private String id;
        private String requestId;
        private String studentId;
        private LocalDate createdAt;
        private RequestStatus status;
        private RequestType type;
        private Boolean isExceptional;
        private String destinationGroupId;
        private String originGroupId;
        private String description;
        private String gestedBy;
        private LocalDate updatedAt;
        private String answer;

        public RequestHistory.RequestHistoryBuilder id() {
            this.id = UUID.randomUUID().toString();
            return this;
        }

        public RequestHistory.RequestHistoryBuilder requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public RequestHistory.RequestHistoryBuilder studentId(String studentId) {
            this.studentId = studentId;
            return this;
        }

        public RequestHistory.RequestHistoryBuilder createdAt(LocalDate createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public RequestHistory.RequestHistoryBuilder status(RequestStatus status) {
            this.status = status;
            return this;
        }

        public RequestHistory.RequestHistoryBuilder type(RequestType type) {
            this.type = type;
            return this;
        }

        public RequestHistory.RequestHistoryBuilder isExceptional(Boolean isExceptional) {
            this.isExceptional = isExceptional;
            return this;
        }

        public RequestHistory.RequestHistoryBuilder destinationGroupId(String destinationGroupId) {
            this.destinationGroupId = destinationGroupId;
            return this;
        }

        public RequestHistory.RequestHistoryBuilder originGroupId(String originGroupId) {
            this.originGroupId = originGroupId;
            return this;
        }

        public RequestHistory.RequestHistoryBuilder description(String description) {
            this.description = description;
            return this;
        }

        public RequestHistory.RequestHistoryBuilder gestedBy(String gestedBy) {
            this.gestedBy = gestedBy;
            return this;
        }

        public RequestHistory.RequestHistoryBuilder updatedAt(LocalDate updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public RequestHistory.RequestHistoryBuilder answer(String answer) {
            this.answer = answer;
            return this;
        }

        public RequestHistory build() {
            return new RequestHistory(this);
        }
    }
}
