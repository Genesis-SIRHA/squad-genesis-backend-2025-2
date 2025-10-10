package edu.dosw.services;

import edu.dosw.dto.CreateRequestDto;
import edu.dosw.dto.HistorialDTO;
import edu.dosw.dto.UpdateRequestDto;
import edu.dosw.dto.UserCredentialsDto;
import edu.dosw.exception.BusinessException;
import edu.dosw.model.Group;
import edu.dosw.model.Request;
import edu.dosw.model.enums.HistorialStatus;
import edu.dosw.model.enums.RequestType;
import edu.dosw.model.enums.RequestStatus;
import edu.dosw.services.UserServices.StudentService;
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
    private PeriodService periodService;
    private static final Logger logger = LoggerFactory.getLogger(ValidatorService.class);


    public void historialCreationValidator(HistorialDTO historialDTO) {
        studentService.getStudentById(historialDTO.studentId());

        Group group = groupService.findByGroupCode(historialDTO.groupCode());

        if (!group.getYear().equals(periodService.getYear()) || !group.getPeriod().equals(periodService.getPeriod())){
            logger.error("The historial period and year does not match the one from the group: {} != {}", group.getPeriod(),periodService.getPeriod());
            throw new IllegalArgumentException("The historial period and year does not match the one from the group"+ group.getPeriod()+" != "+periodService.getPeriod());
        }
    }

    public void historialUpdateValidator(HistorialStatus oldStatus, HistorialStatus newStatus) {

        if (oldStatus == newStatus){
            logger.error("Trying to change to the same status");
            throw new IllegalArgumentException("there are no changes in STATUS");
        }
    }

    public void validateCreateRequest(CreateRequestDto request) {
        studentService.getStudentById(request.studentId());

        if (!request.type().equals(RequestType.JOIN) && request.originGroupId() == null) {
            throw new IllegalArgumentException("Invalid Request: There is not an originGroupId");
        }

        if (!request.type().equals(RequestType.CANCELLATION) && request.destinationGroupId() == null) {
            throw new IllegalArgumentException("Invalid Request: There is not a destinationGroupId");
        }

        if(!request.type().equals(RequestType.JOIN)){
            groupService.findByGroupCode(request.originGroupId());
        }
        if(!request.type().equals(RequestType.CANCELLATION)){
            groupService.findByGroupCode(request.destinationGroupId());
        }

        // TODO: realizar validacion de que el estudiante si quiere cancelar o hacer swap el este en el grupo de origen
        // TODO: realizar validacion de que no exista una solicitud con el mismo estudiante, grupo origen y grupo destino
        // TODO: realizar validacion de que el grupo destino en caso de ser SWAP O JOIN el destinationGroup no sea de una clase ya vista y terminada
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

        if(request.getStatus() != RequestStatus.PENDING && updateRequestDto.status() == RequestStatus.PENDING) {
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
