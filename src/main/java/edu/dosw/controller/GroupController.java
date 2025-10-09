package edu.dosw.controller;

import edu.dosw.dto.CreationGroupRequest;
import edu.dosw.dto.UpdateGroupRequest;
import edu.dosw.model.Group;
import edu.dosw.services.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
}
