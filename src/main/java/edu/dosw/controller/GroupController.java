package edu.dosw.controller;

import edu.dosw.dto.CreationGroupRequest;
import edu.dosw.dto.SessionDTO;
import edu.dosw.dto.UpdateGroupRequest;
import edu.dosw.model.Group;
import edu.dosw.model.Session;
import edu.dosw.services.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/group")
@Tag(name = "Group Controller", description = "APIs for group management")
public class GroupController {
  private final GroupService groupService;

  @GetMapping("/{groupCode}")
  @Operation(
      summary = "Get group by code",
      description =
          "Retrieves detailed information about a specific group using its unique group code")
  public ResponseEntity<Group> getGroupByCode(@PathVariable String groupCode) {
    return ResponseEntity.ok(groupService.getGroupByGroupCode(groupCode));
  }

  @PostMapping("/{facultyName}")
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

  @PutMapping("/{groupCode}")
  @Operation(
      summary = "Update group information",
      description = "Updates the details of an existing group identified by its group code")
  public ResponseEntity<Group> updateGroup(
      @PathVariable String groupCode, @RequestBody UpdateGroupRequest groupRequest) {
    return ResponseEntity.ok(groupService.updateGroup(groupCode, groupRequest));
  }

  @DeleteMapping("/{groupCode}")
  @Operation(
      summary = "Delete a group",
      description = "Deletes a group and all its associated data using the group code")
  public ResponseEntity<Group> deleteGroup(@PathVariable String groupCode) {
    return ResponseEntity.ok(groupService.deleteGroup(groupCode));
  }

  // Endpoints de sessiones :3

  @GetMapping("/sessions/{groupCode}")
  @Operation(
      summary = "Get all sessions for a group",
      description = "Retrieves a list of all sessions associated with the specified group code")
  public ResponseEntity<List<Session>> getSessionsByStudentIdAndGroupCode(
      @PathVariable String groupCode) {
    return ResponseEntity.ok(groupService.getSessionsByGroupCode(groupCode));
  }

  @GetMapping("/session/{sessionId}")
  @Operation(
      summary = "Get session by ID",
      description =
          "Retrieves detailed information about a specific session using its unique session ID")
  public ResponseEntity<Session> getSessionBySessionId(@PathVariable String sessionId) {
    return ResponseEntity.ok(groupService.getSessionBySessionId(sessionId));
  }

  @PostMapping("/session")
  @Operation(
      summary = "Create a new session",
      description =
          "Creates a new session with the provided details and associates it with a group")
  public ResponseEntity<Session> createSession(@RequestBody SessionDTO sessiondto) {
    return ResponseEntity.ok(groupService.addSession(sessiondto));
  }

  @PatchMapping("/sessions/{sessionId}")
  @Operation(
      summary = "Update session details",
      description = "Updates the details of an existing session identified by its session ID")
  public ResponseEntity<Session> updateSession(
      @PathVariable String sessionId, @RequestBody SessionDTO sessiondto) {
    return ResponseEntity.ok(groupService.updateSession(sessionId, sessiondto));
  }

  @DeleteMapping("/sessions/{sessionId}")
  @Operation(
      summary = "Delete a session",
      description = "Deletes a specific session using its session ID")
  public ResponseEntity<Session> deleteSession(@PathVariable String sessionId) {
    return ResponseEntity.ok(groupService.deleteSession(sessionId));
  }

  @DeleteMapping("/sessions/{groupId}")
  @Operation(
      summary = "Delete all sessions for a group",
      description = "Removes all sessions associated with the specified group ID")
  public ResponseEntity<Group> deleteSessions(@PathVariable String groupId) {
    return ResponseEntity.ok(groupService.deleteSessionsFromGroup(groupId));
  }

  // Endpoints de funcionalidades extra :D (De historial)

  @PostMapping("/student/{groupCode}/{studentId}")
  @Operation(
      summary = "Add a student to a group",
      description = "Enrolls a student in the specified group and creates a new enrollment record")
  public ResponseEntity<Group> addStudent(
      @PathVariable String groupCode, @PathVariable String studentId) {
    return ResponseEntity.ok(groupService.addStudent(groupCode, studentId));
  }

  @DeleteMapping("/student/{groupCode}/{studentId}")
  @Operation(
      summary = "Remove a student from a group",
      description =
          "Withdraws a student from the specified group and updates the enrollment status")
  public ResponseEntity<Group> removeStudent(
      @PathVariable String groupCode, @PathVariable String studentId) {
    return ResponseEntity.ok(groupService.deleteStudent(groupCode, studentId));
  }

  @GetMapping("/notifications")
  @Operation(
      summary = "Get all capacity notifications",
      description =
          "Retrieves a list of all capacity notifications for groups that have reached 90% or more capacity")
  public ResponseEntity<List<String>> getCapacityNotifications() {
    return ResponseEntity.ok(groupService.getCapacityNotifications());
  }

  @DeleteMapping("/notifications")
  @Operation(
      summary = "Clear all capacity notifications",
      description = "Clears the list of capacity notifications")
  public ResponseEntity<Void> clearCapacityNotifications() {
    groupService.clearCapacityNotifications();
    return ResponseEntity.noContent().build();
  }
}
