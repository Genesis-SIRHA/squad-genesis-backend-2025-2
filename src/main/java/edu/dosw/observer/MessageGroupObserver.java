package edu.dosw.observer;

import edu.dosw.model.Group;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MessageGroupObserver implements GroupObserver {
  private final List<String> notifications = new ArrayList<>();

  @Override
  public void update(Group group, double capacityPercentage) {
    String message =
        String.format(
            "Grupo %s - %s: Capacidad al %.1f%% (%d/%d estudiantes)",
            group.getGroupCode(),
            group.getAbbreviation(),
            capacityPercentage,
            group.getEnrolled(),
            group.getMaxCapacity());

    notifications.add(message);
  }

  public List<String> getNotifications() {
    return new ArrayList<>(notifications);
  }

  public void clearNotifications() {
    notifications.clear();
  }
}
