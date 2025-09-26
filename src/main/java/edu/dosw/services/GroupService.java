package edu.dosw.services;

import edu.dosw.dto.GroupRequest;
import edu.dosw.model.Group;
import edu.dosw.repositories.GroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupService {
    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public List<Group> getAllGroupsByCourseAbbreviation(String abbreviation) {
        return groupRepository.findAllByCourseId(abbreviation);
    }

    public Optional<Group> getGroupByGroupCode(String groupCode) {
        return groupRepository.findById(groupCode);
    }


    public Group createGroup(GroupRequest groupRequest) {
        Group group = groupRequest.toEntity();
        return groupRepository.save(group);
    }
}
