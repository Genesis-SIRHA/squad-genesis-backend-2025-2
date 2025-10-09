package edu.dosw.services;

import edu.dosw.dto.CreationGroupRequest;
import edu.dosw.dto.UpdateGroupRequest;
import edu.dosw.exception.BusinessException;
import edu.dosw.model.Course;
import edu.dosw.model.Group;
import edu.dosw.repositories.GroupRepository;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service class that handles business logic related to groups. Provides methods for retrieving and
 * managing group information.
 */
@AllArgsConstructor
@Service
public class GroupService {
    private final Logger logger = LoggerFactory.getLogger(GroupService.class);
    private final FacultyService facultyService;
    private final GroupRepository groupRepository;
    private final PeriodService periodService;


    /**
     * Retrieves all groups associated with a specific course abbreviation.
     *
     * @param abbreviation the course abbreviation to filter groups by
     * @return a list of groups associated with the given course abbreviation
     */
    public List<Group> getAllGroupsByCourseAbbreviation(String abbreviation) {
      return groupRepository.findAllByCourseId(abbreviation);
    }

    /**
     * Retrieves a group by its unique group code.
     *
     * @param groupCode the unique code of the group to retrieve
     * @return an Optional containing the group if found, or empty if not found
     */
    public Group getGroupByGroupCode(String groupCode) {
        Group group = groupRepository.findByGroupCode(groupCode).orElse(null);
        if (group == null) {
            logger.error("Group not found");
            throw new BusinessException("Group not found");
        }
      return group;
    }

    /**
     * Creates a new group using the provided group request data.
     *
     * @param groupRequest the group data transfer object containing group information
     * @return the newly created group entity
     */
    public Group createGroup(CreationGroupRequest groupRequest, String facultyName, String plan) {
        Course course = facultyService.findCourseByAbbreviation(groupRequest.abbreviation(),facultyName,plan);
        if (course == null) {
            logger.error("Faculty not found: " + groupRequest.abbreviation());
            throw new BusinessException("Faculty not found: " + groupRequest.abbreviation());
        }

      Group group = new Group.GroupBuilder()
                  .groupCode(groupRequest.groupCode())
                  .abbreviation(groupRequest.abbreviation())
                  .year(periodService.getYear())
                  .period(periodService.getPeriod())
                  .professorId(groupRequest.teacherId())
                  .isLab(groupRequest.isLab())
                  .groupNum(groupRequest.groupNum())
                  .enrolled(groupRequest.enrolled())
                  .maxCapacity(groupRequest.maxCapacity())
                  .build();

      return groupRepository.save(group);
    }

    public Group findByGroupCode(String code) {
      try {
        return groupRepository.findByGroupCode(code).orElse(null);
      } catch(Exception e){
        throw new RuntimeException("Failed to find group by group code: " + e.getMessage());
      }
    }

    public Group updateGroup(String groupCode, UpdateGroupRequest groupRequest) {
        Group group = groupRepository.findByGroupCode(groupCode).orElse(null);
        if (group == null) {
            logger.error("Group not found");
            throw new BusinessException("Group not found");
        }
        if (groupRequest.teacherId() != null) group.setProfessorId(groupRequest.teacherId());
        if(groupRequest.isLab() != null) group.setLab(groupRequest.isLab());
        if (groupRequest.groupNum() != null) group.setGroupNum(groupRequest.groupNum());
        if(groupRequest.maxCapacity() != null) group.setMaxCapacity(groupRequest.maxCapacity());
        if(groupRequest.enrolled() != null) group.setEnrolled(groupRequest.enrolled());

        return groupRepository.save(group);
    }

    public Group deleteGroup(String groupCode) {
        Group group = groupRepository.findByGroupCode(groupCode).orElse(null);
        if (group == null) {
            logger.error("Group not found");
            throw new BusinessException("Group not found");
        }
        try {
            groupRepository.delete(group);
            return group;
        }catch (Exception e){
            logger.error("Failed to delete group: " + e.getMessage());
            throw new BusinessException("Failed to delete group");
        }

    }
}
