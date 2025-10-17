package edu.dosw.observer;

import edu.dosw.model.Group;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GroupCapacityNotifier implements GroupObservable {
  private final List<GroupObserver> observers = new ArrayList<>();
  private static final double UMBRAL_NOTIFICATION = 90.0;

  @Override
  public void addObserver(GroupObserver observer) {
    observers.add(observer);
  }

  @Override
  public void removeObserver(GroupObserver observer) {
    observers.remove(observer);
  }

  @Override
  public void notifyObservers(Group group, double capacityPercentage) {
    if (capacityPercentage >= UMBRAL_NOTIFICATION) {
      for (GroupObserver observer : observers) {
        observer.update(group, capacityPercentage);
      }
    }
  }

  public void checkAndNotify(Group group) {
    double percentage = calculateCapacityPercentage(group);
    notifyObservers(group, percentage);
  }

  private double calculateCapacityPercentage(Group group) {
    if (group.getMaxCapacity() == 0) return 0;
    return ((double) group.getEnrolled() / group.getMaxCapacity()) * 100;
  }
}
