package edu.dosw.services.Validators;

import edu.dosw.dto.RequestPeriodDTO;
import edu.dosw.exception.BusinessException;
import edu.dosw.services.PeriodService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class RequestPeriodValidator {
  private final Logger logger = LoggerFactory.getLogger(RequestPeriodValidator.class);
  private final PeriodService periodService;

  public void currentYearValidator(RequestPeriodDTO periodDTO) {
    if (!periodService.getYear().equals(periodDTO.year())) {
      logger.error("This year is invalid because is not the current year: {}", periodDTO.year());
      throw new BusinessException(
          "This year is invalid because is not the current year: " + periodDTO.year());
    }
  }

  public void currentPeriodValidator(RequestPeriodDTO periodDTO) {
    if (!periodService.getPeriod().equals(periodDTO.period())) {
      logger.error(
          "This period is invalid because is not the current period: {}", periodDTO.period());
      throw new BusinessException(
          "This period is invalid because is not the current period: " + periodDTO.period());
    }
  }

  public void initialDateValidator(RequestPeriodDTO periodDTO) {
    if (periodDTO.initialDate().isAfter(periodDTO.finalDate())) {
      logger.error("The initial date is after the final date");
      throw new BusinessException("The initial date is after the final date");
    }
    if (periodDTO.initialDate().getYear() != Integer.parseInt(periodService.getYear())) {
      logger.error("The initial date is not in the current year");
      throw new BusinessException("The initial date is not in the current year");
    }
  }

  public void finalDateValidator(RequestPeriodDTO periodDTO) {
    if (periodDTO.finalDate().getYear() != Integer.parseInt(periodService.getYear())) {
      logger.error("The final date is not in the current year");
      throw new BusinessException("The final date is not in the current year");
    }
  }

  public void createRequestPeriod(RequestPeriodDTO requestPeriodDTO) {
    currentYearValidator(requestPeriodDTO);
    currentPeriodValidator(requestPeriodDTO);
    initialDateValidator(requestPeriodDTO);
    finalDateValidator(requestPeriodDTO);
  }

  public void updateRequestPeriod(RequestPeriodDTO requestPeriodDTO) {
    currentYearValidator(requestPeriodDTO);
    currentPeriodValidator(requestPeriodDTO);
    initialDateValidator(requestPeriodDTO);
    finalDateValidator(requestPeriodDTO);
  }
}
