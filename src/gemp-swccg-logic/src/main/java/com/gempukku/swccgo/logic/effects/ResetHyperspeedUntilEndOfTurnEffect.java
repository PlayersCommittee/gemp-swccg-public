package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersEnvironment;
import com.gempukku.swccgo.logic.modifiers.ResetLandspeedModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.ResetOrModifyCardAttributeResult;

/**
 * An effect to resets the hyperspeed of a card until end of the turn.
 */
public class ResetHyperspeedUntilEndOfTurnEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _card;
    private float _resetValue;

    /**
     * Creates an effect that resets the hyperspeed of a card until end of the turn.
     * @param action the action performing this effect
     * @param card the card whose hyperspeed is reset
     * @param resetValue the reset value
     */
    public ResetHyperspeedUntilEndOfTurnEffect(Action action, PhysicalCard card, float resetValue) {
        super(action);
        _card = card;
        _resetValue = resetValue;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        PhysicalCard source = _action.getActionSource();

        gameState.sendMessage(GameUtils.getCardLink(_card) + "'s hyperspeed is reset to " + GuiUtils.formatAsString(_resetValue) + " until end of the turn");
        gameState.cardAffectsCard(_action.getPerformingPlayer(), source, _card);

        // Filter for same card while it is in play
        Filter cardFilter = Filters.and(Filters.sameCardId(_card), Filters.in_play);
        modifiersEnvironment.addUntilEndOfTurnModifier(
                new ResetLandspeedModifier(source, cardFilter, _resetValue));

        actionsEnvironment.emitEffectResult(new ResetOrModifyCardAttributeResult(_action.getPerformingPlayer(), _card));
    }
}
