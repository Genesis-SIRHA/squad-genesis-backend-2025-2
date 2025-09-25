package edu.dosw.services;

import edu.dosw.model.Member;
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

    public Member listById(String id) {
        return membersRepository.findById(id).orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
    }

    public String getFaculty(String id) {
        return  membersRepository.findById(id).get().getFacultyId();
    }
}
