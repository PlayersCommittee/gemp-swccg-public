package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * An effect result that is emitted when a card is Disarmed.
 */
public class IonizedResult extends EffectResult {
    private PhysicalCard _cardIonized;

    /**
     * Creates an effect result that is emitted when a card is Ionized.
     * @param performingPlayerId the player performing the action
     * @param cardIonized the card that was Ionized
     */
    public IonizedResult(String performingPlayerId, PhysicalCard cardIonized) {
        super(Type.IONIZED, performingPlayerId);
        _cardIonized = cardIonized;
    }

    /**
     * Gets the card that was Ionized.
     * @return the card
     */
    public PhysicalCard getCardIonized() {
        return _cardIonized;
    }


    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Ionized " + GameUtils.getCardLink(_cardIonized);
    }
}
