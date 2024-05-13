package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;

/**
 * This effect result is triggered when a destiny is drawn.
 */
public class DestinyDrawnResult extends EffectResult {
    private DrawDestinyEffect _drawDestinyEffect;

    /**
     * Creates an effect result that is triggered when a destiny is drawn.
     * @param action the action
     * @param drawDestinyEffect the draw destiny effect
     */
    public DestinyDrawnResult(Action action, DrawDestinyEffect drawDestinyEffect) {
        super(Type.DESTINY_DRAWN, action.getPerformingPlayer());
        _drawDestinyEffect = drawDestinyEffect;
    }

    /**
     * Gets the source action for drawing destiny.
     * @return the source action
     */
    public Action getSourceAction() {
        return _drawDestinyEffect.getAction();
    }

    /**
     * Gets the card drawn for destiny, or null if substituted destiny.
     * @return the card, or null
     */
    public PhysicalCard getCard() {
        return _drawDestinyEffect.getDrawnDestinyCard();
    }

    /**
     * Determines if the drawn destiny is canceled.
     * @return true if canceled, otherwise false
     */
    public boolean isCanceled() {
        return _drawDestinyEffect.isDestinyCanceled();
    }

    /**
     * Determines if the drawn destiny is substituted.
     * @return true if substituted, otherwise false
     */
    public boolean isSubstituteDestiny() {
        return _drawDestinyEffect.getSubstituteDestiny() != null;
    }

    /**
     * Gets the destiny type.
     * @return the destiny type
     */
    public DestinyType getDestinyType() {
        return _drawDestinyEffect.getDestinyType();
    }

    /**
     * Gets the cards whose ability, maneuver, or defense value is being targeted for comparison with the result of this destiny draw.
     * @return the cards whose whose ability, maneuver, or defense value is being targeted
     */
    public Collection<PhysicalCard> getAbilityManeuverOrDefenseValueTargeted() {
        return _drawDestinyEffect.getAbilityManeuverOrDefenseValueTargeted();
    }

    /**
     * Gets the number of destiny drawn so far.
     * @return the number of destiny drawn so far
     */
    public int getNumDestinyDrawnSoFar() {
        return _drawDestinyEffect.getNumDestinyDrawnSoFar();
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        if (isSubstituteDestiny()) {
            return "Just drew " + getDestinyType().getHumanReadable();
        }
        else {
            return "Just drew " + GameUtils.getCardLink(getCard()) + " for " + getDestinyType().getHumanReadable();
        }
    }
}
