package edu.dosw.services.strategy.AnswerStrategies;

import edu.dosw.model.Request;

/** Strategy interface for handling request answers with different business logic implementations */
public interface AnswerStrategy {
  /**
   * Processes and answers a request according to specific business rules
   *
   * @param request The request to be processed and answered
   */
  void answerRequest(Request request);
}
