package edu.dosw.services.strategy.AnswerStrategies;

import edu.dosw.exception.BusinessException;
import edu.dosw.model.enums.RequestType;
import edu.dosw.services.GroupService;
import edu.dosw.services.HistorialService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class AnswerStrategyFactory {
    private GroupService groupService;
    private HistorialService historialService;

    public AnswerStrategy getStrategy(RequestType type) {
        return switch (type) {
            case JOIN -> new JoinRequestAnswer(groupService, historialService);
            case CANCELLATION -> new CancellationRequestAnswer(groupService, historialService);
            case SWAP -> new SwapRequestAnswer(groupService, historialService);
            default -> throw new BusinessException("Invalid request type");
        };
    }
}