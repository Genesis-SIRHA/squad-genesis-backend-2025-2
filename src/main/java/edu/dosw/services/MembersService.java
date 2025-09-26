package edu.dosw.services;

import edu.dosw.model.User;
import edu.dosw.repositories.MembersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MembersService {
    private final MembersRepository membersRepository;

    @Autowired
    public MembersService(MembersRepository membersRepository) {
        this.membersRepository = membersRepository;
    }

    public User listById(String id) {
        return membersRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public String getFaculty(String id) {
        return  membersRepository.findById(id).get().getFacultyName();
    }

    public String getPlan(String studentId) {
        return membersRepository.findById(studentId).get().getPlan();
    }

}
