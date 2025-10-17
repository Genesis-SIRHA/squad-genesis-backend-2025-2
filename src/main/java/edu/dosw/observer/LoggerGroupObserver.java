package edu.dosw.observer;

import edu.dosw.model.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggerGroupObserver implements GroupObserver {
  private final Logger logger = LoggerFactory.getLogger(LoggerGroupObserver.class);

  @Override
  public void update(Group group, double capacityPercentage) {
    logger.warn(
        "Notificación de capacidad: Grupo {} - {}: Capacidad al {:.1f}% ({}/{})",
        group.getGroupCode(),
        group.getAbbreviation(),
        capacityPercentage,
        group.getEnrolled(),
        group.getMaxCapacity());
  }
}
