package edu.dosw.services;

import edu.dosw.dto.CreateRequestDto;
import edu.dosw.dto.UpdateRequestDto;
import edu.dosw.dto.UserCredentialsDto;
import edu.dosw.exception.BusinessException;
import edu.dosw.model.Faculty;
import edu.dosw.model.Group;
import edu.dosw.model.Request;
import edu.dosw.model.Student;
import edu.dosw.model.enums.RequestType;
import edu.dosw.model.enums.Status;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@AllArgsConstructor
@Service
public class ValidatorService {
    private GroupService groupService;
    private StudentService studentService;
    private AuthenticationService authenticationService;
    private FacultyService facultyService;
    private static final Logger logger = LoggerFactory.getLogger(ValidatorService.class);

    public void validateRequest(CreateRequestDto request) {
        Student studentRequesting  = studentService.getStudentById(request.studentId());
        if (studentRequesting == null){
            logger.error("Student does not exist");
            throw new IllegalArgumentException("Student doing the request not found: " + request.originGroupId());
        }

        Group origin = groupService.findByGroupCode(request.originGroupId());
        if (!request.type().equals(RequestType.JOIN) && origin == null) {
            throw new IllegalArgumentException("Origin group not found: " + request.originGroupId());
        }

        Group destination = groupService.findByGroupCode(request.destinationGroupId());
        if (!request.type().equals(RequestType.CANCELLATION) && destination == null) {
            throw new IllegalArgumentException("Destination group not found: " + request.destinationGroupId());
        }
    }

    public void validateUpdateRequest(String userId, Request request, UpdateRequestDto updateRequestDto) {
        UserCredentialsDto user = authenticationService.findByUserId(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        if (request == null) {
            throw new RuntimeException("Request not found with id: " + updateRequestDto.requestId());
        }

        if(updateRequestDto.status() == null || updateRequestDto.status() == request.getStatus()) {
            throw new RuntimeException("Request status cannot be the same as the current status");
        }

        if(request.getStatus() != Status.PENDING && updateRequestDto.status() == Status.PENDING) {
            throw new RuntimeException("Request cannot be changed to PENDING");
        }
    }

    public void validateFacultyName(String facultyName) {
        Map<String, String> faculties = facultyService.getAllFacultyNames();
        if (!faculties.containsKey(facultyName)){
            throw new BusinessException("Faculty "+facultyName +" does not exist in "+ faculties.keySet().toString());
        }
    }
}
