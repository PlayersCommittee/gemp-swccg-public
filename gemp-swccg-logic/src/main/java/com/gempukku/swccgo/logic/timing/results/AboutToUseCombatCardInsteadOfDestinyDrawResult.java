package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when the player is about to use the specified combat card instead of a destiny draw.
 */
public class AboutToUseCombatCardInsteadOfDestinyDrawResult extends EffectResult {
    private PhysicalCard _source;
    private PhysicalCard _cardToUseInstead;
    private PreventableCardEffect _effect;

    /**
     * Creates an effect result that is emitted when the player is about to use the specified combat card instead of a destiny draw.
     * @param action the action
     * @param performingPlayerId the performing player
     * @param cardToUseInstead the card to use instead of normal destiny draw
     * @param effect the effect that can be used to prevent the card from being lost
    */
    public AboutToUseCombatCardInsteadOfDestinyDrawResult(Action action, String performingPlayerId, PhysicalCard cardToUseInstead, PreventableCardEffect effect) {
        super(Type.ABOUT_TO_USE_COMBAT_CARD_INSTEAD_OF_DESTINY_DRAW, performingPlayerId);
        _source = action.getActionSource();
        _cardToUseInstead = cardToUseInstead;
        _effect = effect;
    }

    /**
     * Gets the source card of the action.
     * @return the source card
     */
    public PhysicalCard getSourceCard() {
        return _source;
    }

    /**
     * Gets the card to use instead of normal destiny draw.
     * @return the card
     */
    public PhysicalCard getCardToUseInstead() {
        return _cardToUseInstead;
    }

    /**
     * Gets the interface that can be used to prevent the card from being used instead of destiny draw.
     * @return the interface
     */
    public PreventableCardEffect getPreventableCardEffect() {
        return _effect;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "About to use combat card, " + GameUtils.getCardLink(_cardToUseInstead) + ", instead of destiny draw";
    }
}
