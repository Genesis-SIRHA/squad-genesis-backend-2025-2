package edu.dosw.controller;

import edu.dosw.dto.CreationGroupRequest;
import edu.dosw.dto.SessionDTO;
import edu.dosw.dto.UpdateGroupRequest;
import edu.dosw.model.Course;
import edu.dosw.model.Group;
import edu.dosw.model.Session;
import edu.dosw.services.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/groups")
@Tag(name = "Group Controller", description = "APIs for group management")
public class GroupController {
  private final GroupService groupService;

  /**
   * Retrieves a group by its unique group code
   *
   * @param groupCode The unique code identifying the group
   * @return ResponseEntity containing the group details
   */
  @GetMapping("/{groupCode}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DEAN', 'PROFESSOR', 'STUDENT')")
  @Operation(
      summary = "Get group by code",
      description =
          "Retrieves detailed information about a specific group using its unique group code")
  public ResponseEntity<Group> getGroupByCode(@PathVariable String groupCode) {
    return ResponseEntity.ok(groupService.getGroupByGroupCode(groupCode));
  }

  /**
   * Retrieves all groups associated with a specific course abbreviation
   *
   * @param courseAbbreviation The abbreviation of the course
   * @return ResponseEntity containing a list of groups for the course
   */
  @GetMapping("/{courseAbbreviation}")
  @Operation(
      summary = "Get group by course abbreviation",
      description = "Retrieves detailed information about a all groups by course abbreviation")
  public ResponseEntity<List<Group>> getGroupByCourseAbbreviation(
      @PathVariable String courseAbbreviation) {
    return ResponseEntity.ok(groupService.getAllGroupsByCourseAbbreviation(courseAbbreviation));
  }

  /**
   * Retrieves course information associated with a specific group code
   *
   * @param groupCode The unique code identifying the group
   * @return ResponseEntity containing the course details
   */
  @GetMapping("/{groupCode}/course")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DEAN', 'PROFESSOR', 'STUDENT')")
  @Operation(
      summary = "Get course by group code",
      description = "Retrieves detailed information about a course by its group code")
  public ResponseEntity<Course> getCourseByGroupCode(@PathVariable String groupCode) {
    Course course = groupService.getCourseByGroupCode(groupCode);
    return ResponseEntity.ok(course);
  }

  /**
   * Creates a new group within a specific faculty and academic plan
   *
   * @param groupRequest The DTO containing group creation data
   * @param facultyName The name of the faculty where the group will be created
   * @param plan The academic plan identifier
   * @return ResponseEntity containing the created group
   */
  @PostMapping("/{facultyName}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DEAN')")
  @Operation(
      summary = "Create a new group",
      description =
          "Creates a new group with the provided details under the specified faculty and academic plan")
  public ResponseEntity<Group> createGroup(
      @RequestBody CreationGroupRequest groupRequest,
      @PathVariable String facultyName,
      @RequestParam String plan) {
    return ResponseEntity.ok(groupService.createGroup(groupRequest, facultyName, plan));
  }

  /**
   * Updates an existing group with new information
   *
   * @param groupCode The unique code identifying the group to update
   * @param groupRequest The DTO containing updated group data
   * @return ResponseEntity containing the updated group
   */
  @PutMapping("/{groupCode}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DEAN')")
  @Operation(
      summary = "Update group information",
      description = "Updates the details of an existing group identified by its group code")
  public ResponseEntity<Group> updateGroup(
      @PathVariable String groupCode, @RequestBody UpdateGroupRequest groupRequest) {
    return ResponseEntity.ok(groupService.updateGroup(groupCode, groupRequest));
  }

  /**
   * Deletes a group by its group code
   *
   * @param groupCode The unique code identifying the group to delete
   * @return ResponseEntity containing the deleted group
   */
  @DeleteMapping("/{groupCode}")
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  @Operation(
      summary = "Delete a group",
      description = "Deletes a group and all its associated data using the group code")
  public ResponseEntity<Group> deleteGroup(@PathVariable String groupCode) {
    return ResponseEntity.ok(groupService.deleteGroup(groupCode));
  }

  /**
   * Retrieves all sessions associated with a specific group
   *
   * @param groupCode The unique code identifying the group
   * @return ResponseEntity containing a list of sessions for the group
   */
  @GetMapping("/sessions/{groupCode}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DEAN', 'PROFESSOR', 'STUDENT')")
  @Operation(
      summary = "Get all sessions for a group",
      description = "Retrieves a list of all sessions associated with the specified group code")
  public ResponseEntity<List<Session>> getSessionsByStudentIdAndGroupCode(
      @PathVariable String groupCode) {
    return ResponseEntity.ok(groupService.getSessionsByGroupCode(groupCode));
  }

  /**
   * Retrieves a specific session by its unique identifier
   *
   * @param sessionId The unique identifier of the session
   * @return ResponseEntity containing the session details
   */
  @GetMapping("/session/{sessionId}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DEAN', 'PROFESSOR', 'STUDENT')")
  @Operation(
      summary = "Get session by ID",
      description =
          "Retrieves detailed information about a specific session using its unique session ID")
  public ResponseEntity<Session> getSessionBySessionId(@PathVariable String sessionId) {
    return ResponseEntity.ok(groupService.getSessionBySessionId(sessionId));
  }

  /**
   * Creates a new session and associates it with a group
   *
   * @param sessiondto The DTO containing session creation data
   * @return ResponseEntity containing the created session
   */
  @PostMapping("/session")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DEAN')")
  @Operation(
      summary = "Create a new session",
      description =
          "Creates a new session with the provided details and associates it with a group")
  public ResponseEntity<Session> createSession(@RequestBody SessionDTO sessiondto) {
    return ResponseEntity.ok(groupService.addSession(sessiondto));
  }

  /**
   * Updates an existing session with new information
   *
   * @param sessionId The unique identifier of the session to update
   * @param sessiondto The DTO containing updated session data
   * @return ResponseEntity containing the updated session
   */
  @PatchMapping("/sessions/{sessionId}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DEAN')")
  @Operation(
      summary = "Update session details",
      description = "Updates the details of an existing session identified by its session ID")
  public ResponseEntity<Session> updateSession(
      @PathVariable String sessionId, @RequestBody SessionDTO sessiondto) {
    return ResponseEntity.ok(groupService.updateSession(sessionId, sessiondto));
  }

  /**
   * Deletes a specific session by its identifier
   *
   * @param sessionId The unique identifier of the session to delete
   * @return ResponseEntity containing the deleted session
   */
  @DeleteMapping("/sessions/{sessionId}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DEAN')")
  @Operation(
      summary = "Delete a session",
      description = "Deletes a specific session using its session ID")
  public ResponseEntity<Session> deleteSession(@PathVariable String sessionId) {
    return ResponseEntity.ok(groupService.deleteSession(sessionId));
  }

  /**
   * Deletes all sessions associated with a specific group
   *
   * @param groupId The unique identifier of the group
   * @return ResponseEntity containing the updated group without sessions
   */
  @DeleteMapping("/sessions/{groupId}")
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  @Operation(
      summary = "Delete all sessions for a group",
      description = "Removes all sessions associated with the specified group ID")
  public ResponseEntity<Group> deleteSessions(@PathVariable String groupId) {
    return ResponseEntity.ok(groupService.deleteSessionsFromGroup(groupId));
  }

  /**
   * Enrolls a student in a specific group
   *
   * @param groupCode The unique code identifying the group
   * @param studentId The unique identifier of the student to enroll
   * @return ResponseEntity containing the updated group with the new student
   */
  @PostMapping("/student/{groupCode}/{studentId}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DEAN', 'PROFESSOR')")
  @Operation(
      summary = "Add a student to a group",
      description = "Enrolls a student in the specified group and creates a new enrollment record")
  public ResponseEntity<Group> addStudent(
      @PathVariable String groupCode, @PathVariable String studentId) {
    return ResponseEntity.ok(groupService.addStudent(groupCode, studentId));
  }

  /**
   * Removes a student from a specific group
   *
   * @param groupCode The unique code identifying the group
   * @param studentId The unique identifier of the student to remove
   * @return ResponseEntity containing the updated group without the student
   */
  @DeleteMapping("/student/{groupCode}/{studentId}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DEAN', 'PROFESSOR')")
  @Operation(
      summary = "Remove a student from a group",
      description =
          "Withdraws a student from the specified group and updates the enrollment status")
  public ResponseEntity<Group> removeStudent(
      @PathVariable String groupCode, @PathVariable String studentId) {
    return ResponseEntity.ok(groupService.deleteStudent(groupCode, studentId));
  }
}
