package edu.dosw.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "courses")
public class Course {
    @Id
    private String id;

    @NotBlank(message = "El código del curso es obligatorio")
    private String code;

    @NotBlank(message = "El nombre del curso es obligatorio")
    private String name;

    private List<Group> groups = new ArrayList<>();

    public Course() {}

    public Course(String code, String name, List<Group> groups) {
        this.code = code;
        this.name = name;
        this.groups = (groups != null) ? groups : new ArrayList<>();
    }


    public String getId() { return id; }
    public void setAbbreviation(String id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setCourseName(String name) { this.name = name; }

    public List<Group> getGroups() { return groups; }
    public void setGroups(List<Group> groups) {
        this.groups = (groups != null) ? groups : new ArrayList<>();
    }
}
