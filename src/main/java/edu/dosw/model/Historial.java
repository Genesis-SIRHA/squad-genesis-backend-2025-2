package edu.dosw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "historial")
public class Historial {
    private String studentId;
    private String groupCode;
    private String status;
    private String year;
    private String period;

}
