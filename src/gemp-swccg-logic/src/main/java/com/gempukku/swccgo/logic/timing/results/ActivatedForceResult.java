package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * An effect result that is emitted when unit of Force is activated.
 */
public class ActivatedForceResult extends EffectResult {
    private boolean _fromForceGeneration;
    private PhysicalCard _card;

    /**
     * Creates an effect result that is emitted when unit of Force is activated.
     * @param card the card that was activated as a unit of Force
     * @param fromForceGeneration true if the activate was due to Force generation, otherwise false
     */
    public ActivatedForceResult(PhysicalCard card, boolean fromForceGeneration) {
        super(EffectResult.Type.FORCE_ACTIVATED, card.getOwner());
        _card = card;
        _fromForceGeneration = fromForceGeneration;
    }

    /**
     * Gets the card that was activated as a unit of Force.
     * @return the card
     */
    public PhysicalCard getCard() {
        return _card;
    }

    /**
     * Determines if the Force was activate due to Force generation.
     * @return true or false
     */
    public boolean fromForceGeneration() {
        return _fromForceGeneration;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Force just activated";
    }
}
