package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a player is about to draw a card for destiny.
 */
public class AboutToDrawDestinyCardResult extends EffectResult {
    private DrawDestinyEffect _drawDestinyEffect;

    /**
     * Creates an effect result that is emitted when a player is about to draw a card for destiny.
     * @param action the action
     * @param drawDestinyEffect the draw destiny effect
    */
    public AboutToDrawDestinyCardResult(Action action, DrawDestinyEffect drawDestinyEffect) {
        super(Type.ABOUT_TO_DRAW_DESTINY_CARD, action.getPerformingPlayer());
        _drawDestinyEffect = drawDestinyEffect;
    }

    /**
     * Determines if performing a "draw X and choose Y"
     * @return true or false
     */
    public boolean isDrawAndChoose() {
        return _drawDestinyEffect.isDrawAndChoose();
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
     * Gets the source card causing the destiny draw.
     * @return the source card causing the destiny draw.
     */
    public PhysicalCard getActionSource() {
        return _drawDestinyEffect.getAction().getActionSource();
    }

    /**
     * Determines the card to stack race destiny on.
     * @return the card to stack race destiny on, or null if not race destiny
     */
    public PhysicalCard getStackRaceDestinyOn() {
        return _drawDestinyEffect.getStackRaceDestinyOn();
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "About to draw " + getDestinyType().getHumanReadable();
    }
}
