package edu.dosw.observer;

import edu.dosw.model.Group;

public interface GroupObservable {
  void addObserver(GroupObserver observer);

  void removeObserver(GroupObserver observer);

  void notifyObservers(Group group, double capacityPercentage);
}
