package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when a card is relocated from an Effect (such as Lost In Space or Weather Vane) to a location.
 */
public class RelocateFromLostInSpaceOrWeatherVaneToLocationResult extends EffectResult {
    private PhysicalCard _card;
    private PhysicalCard _location;

    /**
     * Creates an effect result that is triggered when a card is relocated from an Effect (such as Lost In Space or Weather Vane) to a location.
     * @param performingPlayer the player performing the action
     * @param card the card
     * @param location the location the card was relocated to
     */
    public RelocateFromLostInSpaceOrWeatherVaneToLocationResult(String performingPlayer, PhysicalCard card, PhysicalCard location) {
        super(Type.RELOCATED_FROM_LOST_IN_SPACE_OR_WEATHER_VANE_TO_LOCATION, performingPlayer);
        _card = card;
        _location = location;
    }

    /**
     * Gets the card relocated.
     * @return the card
     */
    public PhysicalCard getCard() {
        return _card;
    }

    /**
     * Gets the location the card was relocated to.
     * @return the location the card was relocated to
     */
    public PhysicalCard getLocation() {
        return _location;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Relocated " + GameUtils.getCardLink(_card) + " to " + GameUtils.getCardLink(_location);
    }
}
