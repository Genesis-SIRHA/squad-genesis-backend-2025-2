package edu.dosw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document (collection = "universityMembers")
public class User {
    @Id
    private final String userId;
    private String type;
    private String name;
    private String plan;
    private String facultyId;

}
