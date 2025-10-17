package edu.dosw.config;

import edu.dosw.observer.GroupCapacityNotifier;
import edu.dosw.observer.LoggerGroupObserver;
import edu.dosw.observer.MessageGroupObserver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObserverConfig {

  @Bean
  public GroupCapacityNotifier groupCapacityNotifier(
      LoggerGroupObserver loggerObserver, MessageGroupObserver messageObserver) {
    GroupCapacityNotifier notifier = new GroupCapacityNotifier();
    notifier.addObserver(loggerObserver);
    notifier.addObserver(messageObserver);
    return notifier;
  }
}
