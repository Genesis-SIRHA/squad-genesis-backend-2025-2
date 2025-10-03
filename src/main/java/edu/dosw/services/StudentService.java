package edu.dosw.services;

import edu.dosw.model.Student;
import edu.dosw.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student listById(String studentId) {
        return studentRepository.findById(studentId).orElse(null);
    }
}
