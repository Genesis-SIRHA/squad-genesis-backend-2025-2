package edu.dosw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.dosw.dto.CourseRequest;
import edu.dosw.dto.GroupRequest;
import edu.dosw.model.Course;
import edu.dosw.model.Group;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import edu.dosw.services.CourseService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseService courseService;

    @Test
    void should_create_course() throws Exception {
        CourseRequest request = new CourseRequest(
                "CS101",
                "Intro a la Programación",
                List.of(new GroupRequest("G1", "Profesor A", 30, 0))
        );

        when(courseService.createCourse(any(CourseRequest.class)))
                .thenReturn(new Course(
                        "CS101",
                        "Intro a la Programación",
                        List.of(new Group("G1", "Profesor A", 30, 0))
                ));

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("CS101"))
                .andExpect(jsonPath("$.name").value("Intro a la Programación"));
    }

    @Test
    void should_get_all_courses() throws Exception {
        when(courseService.getAllCourses())
                .thenReturn(List.of(
                        new Course(
                                "CS101",
                                "Intro a la Programación",
                                List.of(new Group("G1", "Profesor A", 30, 0))
                        ),
                        new Course(
                                "CS102",
                                "Estructuras de Datos",
                                List.of(new Group("G1", "Profesor A", 30, 0))
                        )
                ));



        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].code").value("CS101"))
                .andExpect(jsonPath("$[1].code").value("CS102"));
    }

    @Test
    void should_get_course_by_id() throws Exception {
        when(courseService.getCourseById("12345"))
                .thenReturn(Optional.of(new Course(
                        "CS101",
                        "Intro a la Programación",
                        List.of()
                )));

        mockMvc.perform(get("/api/courses/12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("CS101"))
                .andExpect(jsonPath("$.name").value("Intro a la Programación"));
    }

    @Test
    void should_return_404_when_course_not_found() throws Exception {
        when(courseService.getCourseById("999"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/courses/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_update_course() throws Exception {
        CourseRequest updateRequest = new CourseRequest(
                "CS101",
                "Programación Avanzada",
                List.of(new GroupRequest("G1", "Profesor A", 30, 0))
        );

        when(courseService.updateCourse(any(String.class), any(CourseRequest.class)))
                .thenReturn(Optional.of(new Course(
                        "CS101",
                        "Programación Avanzada",
                        List.of()
                )));

        mockMvc.perform(put("/api/courses/12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Programación Avanzada"));
    }

    @Test
    void should_return_404_when_updating_nonexistent_course() throws Exception {
        CourseRequest updateRequest = new CourseRequest(
                "CS999",
                "Curso Inexistente",
                List.of()
        );

        when(courseService.updateCourse(any(String.class), any(CourseRequest.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/courses/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_delete_course() throws Exception {
        mockMvc.perform(delete("/api/courses/12345"))
                .andExpect(status().isNoContent());
    }

    @Test
    void should_add_group_to_course() throws Exception {
        GroupRequest groupRequest = new GroupRequest("G2", "Profesor B", 25, 0);

        when(courseService.addGroupToCourse(eq("12345"), eq(groupRequest)))
                .thenReturn(Optional.of(new Course(
                        "CS101",
                        "Intro a la Programación",
                        List.of(new Group("G2", "Profesor B", 25, 0))
                )));



        mockMvc.perform(post("/api/courses/12345/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(groupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groups[0].groupCode").value("G2"));
    }

    @Test
    void should_return_404_when_adding_group_to_nonexistent_course() throws Exception {
        GroupRequest groupRequest = new GroupRequest("G2", "Profesor B", 25, 0);

        when(courseService.addGroupToCourse(any(String.class), any(GroupRequest.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/courses/999/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(groupRequest)))
                .andExpect(status().isNotFound());
    }
}
