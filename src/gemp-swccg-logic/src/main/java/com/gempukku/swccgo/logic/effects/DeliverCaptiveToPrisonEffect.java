package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.DeliveredCaptiveToPrisonResult;

/**
 * The effect to deliver a captive to a prison.
 */
public class DeliverCaptiveToPrisonEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _escort;
    private PhysicalCard _captive;
    private PhysicalCard _prison;

    /**
     * Create an effect to have the escort deliver the captive to the prison.
     * @param action the action performing this effect
     * @param escort the escort
     * @param captive the captive
     * @param prison the prison
     */
    public DeliverCaptiveToPrisonEffect(Action action, PhysicalCard escort, PhysicalCard captive, PhysicalCard prison) {
        super(action);
        _escort = escort;
        _captive = captive;
        _prison = prison;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        // Get the forfeit value of the captive before being delivered, since that value is needed by DeliverCaptiveToPrisonResult
        float forfeitValue = game.getModifiersQuerying().getForfeit(game.getGameState(), _captive);

        gameState.sendMessage(GameUtils.getCardLink(_escort) + " delivers " + GameUtils.getCardLink(_captive) + " to " + GameUtils.getCardLink(_prison) + " as imprisoned captive");
        gameState.imprisonCharacter(game, _captive, _prison);

        // Emit the result effect that can trigger other cards
        game.getActionsEnvironment().emitEffectResult(new DeliveredCaptiveToPrisonResult(_escort, _captive, forfeitValue, _prison));
    }
}
