package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a card is about to 'hide' from battle.
 */
public class AboutToHideFromBattleResult extends EffectResult {
    private PhysicalCard _source;
    private PhysicalCard _cardToHideFromBattle;
    private PreventableCardEffect _effect;

    /**
     * Creates an effect result that is emitted when the specified card is about to be lost from table.
     * @param action the action
     * @param performingPlayerId the performing player
     * @param cardToHideFromBattle the card to be lost
     * @param effect the effect that can be used to prevent the card from being lost
    */
    public AboutToHideFromBattleResult(Action action, String performingPlayerId, PhysicalCard cardToHideFromBattle, PreventableCardEffect effect) {
        super(Type.ABOUT_TO_HIDE_FROM_BATTLE, performingPlayerId);
        _source = action.getActionSource();
        _cardToHideFromBattle = cardToHideFromBattle;
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
     * Gets the card to 'hide' from battle.
     * @return the card
     */
    public PhysicalCard getCardToHideFromBattle() {
        return _cardToHideFromBattle;
    }

    /**
     * Gets the interface that can be used to prevent the card from being lost.
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
        return "About to 'hide' " + GameUtils.getCardLink(_cardToHideFromBattle) + " from battle";
    }
}
