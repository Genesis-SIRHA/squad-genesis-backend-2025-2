package edu.dosw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "deans")
public class Dean extends User {
    private String facultyName;

    public Dean(DeanBuilder builder) {
        super(builder.userId, builder.identityDocument, builder.email, builder.fullName);
        this.facultyName = builder.facultyName;
    }

    public static class DeanBuilder{
        private String userId;
        private String fullName;
        private String email;
        private String identityDocument;
        private String facultyName;

        public DeanBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }
        public DeanBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }
        public DeanBuilder email(String email) {
            this.email = email;
            return this;
        }
        public DeanBuilder identityDocument(String identityDocument) {
            this.identityDocument = identityDocument;
            return this;
        }

        public DeanBuilder facultyName(String facultyName) {
            this.facultyName = facultyName;
            return this;
        }

        public Dean build() {
            return new Dean(this);
        }

    }
}
