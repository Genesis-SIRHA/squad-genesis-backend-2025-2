package edu.dosw.services.Validators;

import edu.dosw.dto.HistorialDTO;
import edu.dosw.model.enums.HistorialStatus;
import edu.dosw.exception.BusinessException;
import edu.dosw.services.UserServices.StudentService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class HistorialValidator {
  private final Logger logger = LoggerFactory.getLogger(HistorialValidator.class);
  private StudentService studentService;

  public void validateHistorialCreation(HistorialDTO historialDTO) {
    studentService.getStudentById(historialDTO.studentId());
  }

    public void historialUpdateValidator(HistorialStatus oldStatus, HistorialStatus newStatus) {
        if (oldStatus == newStatus) {
            logger.error("Trying to change to the same status");
            throw new BusinessException("There are no changes in STATUS");
        }
    }
}
