package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.CreationGroupRequest;
import edu.dosw.model.Group;
import edu.dosw.repositories.GroupRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//
///** Unit tests for {@link GroupService}. */
//class GroupServiceTest {
//
//  private GroupRepository groupRepository;
//  private GroupService groupService;
//
//  @BeforeEach
//  void setUp() {
//    groupRepository = mock(GroupRepository.class);
//    groupService = new GroupService(groupRepository);
//  }
//
//  @Test
//  void getAllGroupsByCourseAbbreviation_shouldReturnGroups() {
//    Group g1 = new Group();
//    g1.setGroupCode("G1");
//    Group g2 = new Group();
//    g2.setGroupCode("G2");
//
//    when(groupRepository.findAllByCourseId("CS101")).thenReturn(List.of(g1, g2));
//
//    List<Group> result = groupService.getAllGroupsByCourseAbbreviation("CS101");
//
//    assertEquals(2, result.size());
//    assertEquals("G1", result.get(0).getGroupCode());
//    verify(groupRepository).findAllByCourseId("CS101");
//  }
//
//  @Test
//  void getGroupByGroupCode_shouldReturnGroupWhenExists() {
//    Group group = new Group();
//    group.setGroupCode("G1");
//
//    when(groupRepository.findById("G1")).thenReturn(Optional.of(group));
//
//    Optional<Group> result = groupService.getGroupByGroupCode("G1");
//
//    assertTrue(result.isPresent());
//    assertEquals("G1", result.get().getGroupCode());
//    verify(groupRepository).findById("G1");
//  }
//
//  @Test
//  void getGroupByGroupCode_shouldReturnEmptyWhenNotFound() {
//    when(groupRepository.findById("G1")).thenReturn(Optional.empty());
//
//    Optional<Group> result = groupService.getGroupByGroupCode("G1");
//
//    assertTrue(result.isEmpty());
//    verify(groupRepository).findById("G1");
//  }

//  @Test
//  void createGroup_shouldConvertRequestAndSave() {
//    CreationGroupRequest request = new CreationGroupRequest("G1", "CS101", "2025", "1", "T123", true, 1, 30, 0);
//
//    Group entity = request.toEntity();
//    entity.setGroupCode("G1");
//
//    when(groupRepository.save(any(Group.class))).thenReturn(entity);
//
//    Group result = groupService.createGroup(request);
//
//    assertNotNull(result);
//    assertEquals("G1", result.getGroupCode());
//    assertEquals("CS101", result.getAbbreviation());
//    assertTrue(result.isLab());
//    verify(groupRepository).save(any(Group.class));
//  }
//}
