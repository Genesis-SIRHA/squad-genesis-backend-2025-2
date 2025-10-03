package edu.dosw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document(collection = "administratives")
public class Administrative extends User {
    private String facultyName;
}
