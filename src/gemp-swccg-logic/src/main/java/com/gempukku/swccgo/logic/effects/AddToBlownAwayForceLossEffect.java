package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BlowAwayState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.BlownAwayForceLossModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersEnvironment;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that adds to the calculated Force loss against the specified player during the current blow away action.
 */
public class AddToBlownAwayForceLossEffect extends AbstractSuccessfulEffect {
    private String _playerId;
    private float _amount;
    private boolean _forceLossMayNotBeReduced;

    /**
     * Creates an effect that adds to the calculated Force loss against the specified player during the current blow away action.
     * @param action the action performing this effect
     * @param playerId the player with calculated Force loss
     * @param amount the amount to add to Force loss
     */
    public AddToBlownAwayForceLossEffect(Action action, String playerId, float amount) {
        this(action, playerId, amount, false);
    }

    /**
     * Creates an effect that adds to the calculated Force loss against the specified player during the current blow away action.
     * @param action the action performing this effect
     * @param playerId the player with calculated Force loss
     * @param amount the amount to add to Force loss
     */
    public AddToBlownAwayForceLossEffect(Action action, String playerId, float amount, boolean mayNotBeReduced) {
        super(action);
        _playerId = playerId;
        _amount = amount;
        _forceLossMayNotBeReduced = mayNotBeReduced;
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        BlowAwayState blowAwayState = gameState.getTopBlowAwayState();

        if (blowAwayState != null) {
            // gameState.sendMessage(GuiUtils.formatAsString(_amount) + " is added to calculated 'blown away' Force loss against " + _playerId);
            modifiersEnvironment.addUntilEndOfBlowAwayModifier(
                    new BlownAwayForceLossModifier(_action.getActionSource(), blowAwayState.getId(), _amount, _playerId, _forceLossMayNotBeReduced));
        }
    }
}
