package edu.dosw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document(collection = "students")
public class Student extends User {
    private String plan;
    private String facultyName;
}
