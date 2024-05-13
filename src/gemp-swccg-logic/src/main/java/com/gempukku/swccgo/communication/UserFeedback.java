package com.gempukku.swccgo.communication;

import com.gempukku.swccgo.logic.decisions.AwaitingDecision;

public interface UserFeedback {
    void sendAwaitingDecision(String playerId, AwaitingDecision awaitingDecision);

    void sendWarning(String playerId, String warning);

    boolean hasPendingDecisions();
}
