package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.TargetingEffect;

public class PlayoutDecisionEffect extends AbstractSuccessfulEffect implements TargetingEffect {
    private String _playerId;
    private AwaitingDecision _decision;

    public PlayoutDecisionEffect(Action action, String playerId, AwaitingDecision decision) {
        super(action);
        _playerId = playerId;
        _decision = decision;
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        game.getUserFeedback().sendAwaitingDecision(_playerId, _decision);
    }
}
