package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.TookImprisonedCaptiveIntoCustodyResult;

/**
 * The effect to take an imprisoned captive into custody.
 */
public class TakeImprisonedCaptiveCustodyEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _escort;
    private PhysicalCard _captive;
    private PhysicalCard _prison;

    /**
     * Create an effect to have the escort take the imprisoned captive into custody from the prison.
     * @param action the action performing this effect
     * @param escort the escort
     * @param captive the captive
     * @param prison the prison
     */
    public TakeImprisonedCaptiveCustodyEffect(Action action, PhysicalCard escort, PhysicalCard captive, PhysicalCard prison) {
        super(action);
        _escort = escort;
        _captive = captive;
        _prison = prison;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        gameState.sendMessage(GameUtils.getCardLink(_escort) + " takes imprisoned captive " + GameUtils.getCardLink(_captive) + " from " + GameUtils.getCardLink(_prison) + " into custody");
        _captive.setCaptiveEscort(_escort);
        _captive.setImprisoned(false);
        gameState.moveCardToAttached(_captive, _escort);

        // Emit the result effect that can trigger other cards
        game.getActionsEnvironment().emitEffectResult(new TookImprisonedCaptiveIntoCustodyResult(_escort, _captive, _prison));
    }
}
