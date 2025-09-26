package edu.dosw.services;

import edu.dosw.dto.GroupRequest;
import edu.dosw.model.Group;
import edu.dosw.repositories.GroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class that handles business logic related to groups.
 * Provides methods for retrieving and managing group information.
 */
@Service
public class GroupService {
    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

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
    public Optional<Group> getGroupByGroupCode(String groupCode) {
        return groupRepository.findById(groupCode);
    }


    /**
     * Creates a new group using the provided group request data.
     *
     * @param groupRequest the group data transfer object containing group information
     * @return the newly created group entity
     */
    public Group createGroup(GroupRequest groupRequest) {
        Group group = groupRequest.toEntity();
        return groupRepository.save(group);
    }
}
