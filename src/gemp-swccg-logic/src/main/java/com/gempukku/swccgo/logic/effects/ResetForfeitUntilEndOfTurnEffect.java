package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersEnvironment;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ResetForfeitModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.ResetOrModifyCardAttributeResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An effect to resets the forfeit value of a card until end of the turn.
 */
public class ResetForfeitUntilEndOfTurnEffect extends AbstractSuccessfulEffect {
    private List<PhysicalCard> _cardToResets = new ArrayList<>();
    private float _resetValue;

    /**
     * Creates an effect that resets the forfeit value of a card until end of the turn.
     * @param action the action performing this effect
     * @param cardToReset the card whose forfeit value is reset
     * @param resetValue the reset value
     */
    public ResetForfeitUntilEndOfTurnEffect(Action action, PhysicalCard cardToReset, float resetValue) {
        super(action);
        _cardToResets.add(cardToReset);
        _resetValue = resetValue;
    }

    /**
     * Creates an effect that resets the forfeit value of cards until end of the turn.
     * @param action the action performing this effect
     * @param cardsToReset the cards whose forfeit values are to be reset
     * @param resetValue the reset value
     */
    public ResetForfeitUntilEndOfTurnEffect(Action action, Collection<PhysicalCard> cardsToReset, float resetValue) {
        super(action);
        _cardToResets.addAll(cardsToReset);
        _resetValue = resetValue;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        PhysicalCard source = _action.getActionSource();
        List<PhysicalCard> cardsReset = new ArrayList<>();

        for (PhysicalCard cardToReset : _cardToResets) {

            // Check if card's forfeit may not be reduced
            float currentForfeit = modifiersQuerying.getForfeit(gameState, cardToReset);
            if (_resetValue < currentForfeit && modifiersQuerying.isProhibitedFromHavingForfeitReduced(gameState, cardToReset)) {
                gameState.sendMessage(GameUtils.getCardLink(cardToReset) + "'s forfeit is prevented from being reduced to " + _resetValue);
                continue;
            }
            cardsReset.add(cardToReset);

            ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
            ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();

            gameState.sendMessage(GameUtils.getCardLink(cardToReset) + "'s forfeit is reset to " + GuiUtils.formatAsString(_resetValue) + " until end of the turn");

            // Filter for same card while it is in play
            Filter cardFilter = Filters.and(Filters.sameCardId(cardToReset), Filters.in_play);

            modifiersEnvironment.addUntilEndOfTurnModifier(
                    new ResetForfeitModifier(source, cardFilter, _resetValue));

            actionsEnvironment.emitEffectResult(new ResetOrModifyCardAttributeResult(_action.getPerformingPlayer(), cardToReset));
        }

        gameState.cardAffectsCards(_action.getPerformingPlayer(), source, cardsReset);
    }
}
