package edu.dosw.services.Validators;

import edu.dosw.dto.SessionDTO;
import edu.dosw.model.Session;
import edu.dosw.services.GroupService;
import edu.dosw.services.PeriodService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class SessionValidator {
  private final Logger logger = LoggerFactory.getLogger(GroupService.class);
  private PeriodService periodService;

  public void validateCreateSession(SessionDTO sessiondto) {
    if (sessiondto.slot() == null) {
      logger.error("The slot cannot be null");
      throw new IllegalArgumentException("The slot cannot be null");
    }
    if (sessiondto.slot() <= 0 || sessiondto.slot() >= 9) {
      logger.error("The slot is invalid");
      throw new IllegalArgumentException("The slot is invalid");
    }
  }

  public void validateUpdateSession(SessionDTO sessiondto, String year, String period) {
    if (!year.equals(periodService.getYear()) || !period.equals(periodService.getPeriod())) {
      logger.error("The year or period are invalid");
      throw new RuntimeException("The year or period are invalid");
    }
    if (sessiondto.slot() != null) {
      if (sessiondto.slot() <= 0 || sessiondto.slot() >= 9) {
        logger.error("The slot is invalid");
        throw new IllegalArgumentException("The slot is invalid");
      }
    }
  }

  public void validateDeleteSession(Session session) {
    if (!session.getYear().equals(periodService.getYear())
        || !session.getPeriod().equals(periodService.getPeriod())) {
      logger.error("The year or period are invalid");
      throw new RuntimeException("The year or period are invalid");
    }
  }
}
