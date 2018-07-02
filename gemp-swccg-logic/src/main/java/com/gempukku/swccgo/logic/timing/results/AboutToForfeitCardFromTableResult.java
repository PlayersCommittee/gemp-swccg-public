package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.ForfeitCardsFromTableSimultaneouslyEffect;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a card is about to be forfeited from table after satisfying battle damage and
 * attrition, but before card leaves table.
 */
public class AboutToForfeitCardFromTableResult extends EffectResult implements AboutToLeaveTableResult {
    private PhysicalCard _cardToBeForfeited;
    private ForfeitCardsFromTableSimultaneouslyEffect _effect;

    /**
     * Creates an effect result that is emitted when the specified card is about to be forfeited from table after
     * satisfying battle damage and attrition, but before card leaves table.
     * @param action the action
     * @param cardToBeForfeited the card to be forfeited
     * @param effect the effect that is forfeiting the card
     */
    public AboutToForfeitCardFromTableResult(Action action, PhysicalCard cardToBeForfeited, ForfeitCardsFromTableSimultaneouslyEffect effect) {
        super(Type.ABOUT_TO_BE_FORFEITED_TO_FROM_TABLE, action.getPerformingPlayer());
        _cardToBeForfeited = cardToBeForfeited;
        _effect = effect;
    }

    /**
     * Gets the card to be forfeited.
     * @return the card
     */
    public PhysicalCard getCardToBeForfeited() {
        return _cardToBeForfeited;
    }

    /**
     * Gets the effect that is causing the card to be forfeited.
     * @return the effect
     */
    public ForfeitCardsFromTableSimultaneouslyEffect getForfeitCardEffect() {
        return _effect;
    }

    /**
     * Gets the card about to leave table.
     * @return the card
     */
    public PhysicalCard getCardAboutToLeaveTable() {
        return _cardToBeForfeited;
    }


    /**
     * Gets the interface that can be used to prevent the card from being returned to hand.
     * @return the interface
     */
    public PreventableCardEffect getPreventableCardEffect() {
        return _effect;
    }

    /**
     * Determine if card is to be forfeited to Lost Pile.
     * @return true or false
     */
    public boolean isToBeForfeitedToLostPile() {
        return _effect.isForfeitToLostPile();
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "About to forfeit " + GameUtils.getCardLink(_cardToBeForfeited);
    }
}
