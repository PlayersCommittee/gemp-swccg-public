package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.ModifiersEnvironment;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ResetLandspeedModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.ResetOrModifyCardAttributeResult;

/**
 * An effect to resets the landspeed of a card until end of the turn.
 */
public class ResetLandspeedUntilEndOfTurnEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _card;
    private float _landspeedResetValue;

    /**
     * Creates an effect that resets the landspeed of a card until end of the turn.
     * @param action the action performing this effect
     * @param card the card whose landspeed is reset
     * @param landspeedResetValue the reset value
     */
    public ResetLandspeedUntilEndOfTurnEffect(Action action, PhysicalCard card, float landspeedResetValue) {
        super(action);
        _card = card;
        _landspeedResetValue = landspeedResetValue;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        PhysicalCard source = _action.getActionSource();

        // Check if card's landspeed may not be increased
        float currentLandspeed = modifiersQuerying.getLandspeed(gameState, _card);
        if (_landspeedResetValue > currentLandspeed && modifiersQuerying.isProhibitedFromHavingLandspeedIncreased(gameState, _card)) {
            gameState.sendMessage(GameUtils.getCardLink(_card) + "'s landspeed is prevented from being increased");
            return;
        }

        gameState.sendMessage(GameUtils.getCardLink(_card) + "'s landspeed is reset to " + GuiUtils.formatAsString(_landspeedResetValue) + " until end of the turn");
        gameState.cardAffectsCard(_action.getPerformingPlayer(), source, _card);

        // Filter for same card while it is in play
        Filter cardFilter = Filters.and(Filters.sameCardId(_card), Filters.in_play);
        modifiersEnvironment.addUntilEndOfTurnModifier(
                new ResetLandspeedModifier(source, cardFilter, _landspeedResetValue));

        actionsEnvironment.emitEffectResult(new ResetOrModifyCardAttributeResult(_action.getPerformingPlayer(), _card));
    }
}
