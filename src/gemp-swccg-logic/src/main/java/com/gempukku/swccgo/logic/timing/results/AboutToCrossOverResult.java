package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a character is about to cross-over.
 */
public class AboutToCrossOverResult extends EffectResult {
    private PhysicalCard _characterToCrossOver;
    private PreventableCardEffect _effect;

    /**
     * Creates an effect result that is emitted when the specified character is about to cross-over.
     * @param performingPlayerId the performing player
     * @param characterToCrossOver the character to cross-over
     * @param effect the effect that can be used to prevent the card from crossing-over
    */
    public AboutToCrossOverResult(String performingPlayerId, PhysicalCard characterToCrossOver, PreventableCardEffect effect) {
        super(Type.ABOUT_TO_CROSS_OVER, performingPlayerId);
        _characterToCrossOver = characterToCrossOver;
        _effect = effect;
    }

    /**
     * Gets the character to cross-over.
     * @return the character
     */
    public PhysicalCard getCharacterToCrossOver() {
        return _characterToCrossOver;
    }

    /**
     * Gets the interface that can be used to prevent the card from crossing-over.
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
        return "About to cross " + GameUtils.getCardLink(_characterToCrossOver) + " over to the " + (game.getSide(game.getOpponent(_characterToCrossOver.getOwner()))).getHumanReadable() + " Side";
    }
}
