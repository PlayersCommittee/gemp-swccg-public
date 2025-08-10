package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTurnedOnModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersEnvironment;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.BinaryTurnedOffResult;

/**
 * An effect to turn off (a binary droid) until the end of the turn.
 */
public class TurnOffBinaryDroidUntilEndOfTurnEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _binaryDroid;

    /**
     * Creates an effect that turns off (a binary droid) until the end of the turn.
     * @param action the action performing this effect
     * @param binaryDroid the binary droid that is turned off
     */
    public TurnOffBinaryDroidUntilEndOfTurnEffect(Action action, PhysicalCard binaryDroid) {
        super(action);
        _binaryDroid = binaryDroid;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        PhysicalCard source = _action.getActionSource();

        gameState.sendMessage(GameUtils.getCardLink(_binaryDroid) + " is turned off until end of the turn");
        if (source.getCardId() != _binaryDroid.getCardId()) {
            gameState.cardAffectsCard(_action.getPerformingPlayer(), source, _binaryDroid);
        }
        gameState.turnOffBinaryDroid(_binaryDroid);

        // Filter for same card while it is in play
        Filter cardFilter = Filters.and(Filters.sameCardId(_binaryDroid), Filters.in_play);
        modifiersEnvironment.addUntilEndOfTurnModifier(
                new MayNotBeTurnedOnModifier(source, cardFilter));

        actionsEnvironment.emitEffectResult(new BinaryTurnedOffResult(_action.getPerformingPlayer(), _binaryDroid));
    }
}
