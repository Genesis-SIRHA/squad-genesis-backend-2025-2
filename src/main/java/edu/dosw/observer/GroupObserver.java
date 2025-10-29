package edu.dosw.observer;

import edu.dosw.model.Group;

public interface GroupObserver {
  void update(Group group, double capacityPercentage);
}
