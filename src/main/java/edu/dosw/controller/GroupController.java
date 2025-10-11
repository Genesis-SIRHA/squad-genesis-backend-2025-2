package edu.dosw.controller;

import edu.dosw.dto.CreationGroupRequest;
import edu.dosw.dto.SessionDTO;
import edu.dosw.dto.UpdateGroupRequest;
import edu.dosw.model.Group;
import edu.dosw.model.Session;
import edu.dosw.services.GroupService;
import edu.dosw.services.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/group")
@Tag(name = "Group Controller", description = "APIs for group management")
public class GroupController {
    private final GroupService groupService;
    private final SessionService sessionService;

    @GetMapping("/{groupCode}")
    @Operation( description = "Get group by code")
    public ResponseEntity<Group> getGroupByCode(@PathVariable String groupCode) {
        return ResponseEntity.ok(groupService.getGroupByGroupCode(groupCode));
    }

    @PostMapping("/{facultyName}")
    @Operation(description = "Create group")
    public ResponseEntity<Group> createGroup(@RequestBody CreationGroupRequest groupRequest,
                                             @PathVariable String facultyName,
                                             @RequestParam String plan) {
        return ResponseEntity.ok(groupService.createGroup(groupRequest, facultyName, plan));
    }

    @PutMapping("/{groupCode}")
    @Operation(description = "Update group")
    public ResponseEntity<Group> updateGroup(@PathVariable String groupCode, @RequestBody UpdateGroupRequest groupRequest) {
        return ResponseEntity.ok(groupService.updateGroup(groupCode, groupRequest));
    }

    @DeleteMapping("/{groupCode}")
    @Operation(description = "Delete group")
    public ResponseEntity<Group> deleteGroup(@PathVariable String groupCode) {
        return ResponseEntity.ok(groupService.deleteGroup(groupCode));
    }

    //Endpoints de sessiones :3

    @GetMapping("/sessions/{groupCode}")
    @Operation(description = "Get sessions by group code")
    public ResponseEntity<List<Session>> getSessionsByStudentIdAndGroupCode(@PathVariable String groupCode) {
        return ResponseEntity.ok(sessionService.getSessionsByGroupCode(groupCode));
    }

    @GetMapping("/session/{sessionId}")
    @Operation(description = "Get session by session id")
    public ResponseEntity<Session> getSessionBySessionId(@PathVariable String sessionId) {
        return ResponseEntity.ok(sessionService.getSessionBySessionId(sessionId));
    }

    @PostMapping("/session")
    @Operation(description = "Create session")
    public ResponseEntity<Session> createSession(@RequestBody SessionDTO sessiondto) {
        return ResponseEntity.ok(sessionService.createSession(sessiondto));
    }

    @PatchMapping("/sessions/{sessionId}")
    @Operation(description = "Update session")
    public ResponseEntity<Session> updateSession(@PathVariable String sessionId, @RequestBody SessionDTO sessiondto) {
        return ResponseEntity.ok(sessionService.updateSession(sessionId, sessiondto));
    }

    @DeleteMapping("/sessions/{sessionId}")
    @Operation(description = "Delete session")
    public ResponseEntity<Session> deleteSession(@PathVariable String sessionId) {
        return ResponseEntity.ok(sessionService.deleteSession(sessionId));
    }

}
