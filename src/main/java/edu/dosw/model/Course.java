package edu.dosw.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Document(collection = "courses")
public class Course {
    @Id
    private String id;
    @NotBlank
    private String code;
    @NotBlank
    private String name;
    private List<Group> groups;

    public Course() {}

    public Course(String code, String name, List<Group> groups) {
        this.code = code;
        this.name = name;
        this.groups = groups;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Group> getGroups() { return groups; }
    public void setGroups(List<Group> groups) { this.groups = groups; }
}