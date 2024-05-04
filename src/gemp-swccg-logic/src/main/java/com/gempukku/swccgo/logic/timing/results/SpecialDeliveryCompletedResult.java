package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.GameTextAction;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when a Special Delivery is completed by an escort.
 */
public class SpecialDeliveryCompletedResult extends EffectResult {
    private GameTextAction _gameTextAction;
    private PhysicalCard _specialDelivery;
    private PhysicalCard _escort;

    /**
     * Creates an effect result that is triggered when an Utinni Effect is completed.
     * @param performingPlayerId the player that completed the Utinni Effect
     * @param gameTextAction the game text action
     * @param specialDelivery the Special Delivery card
     * @param escort the escort that completed the Special Delivery
     */
    public SpecialDeliveryCompletedResult(String performingPlayerId, GameTextAction gameTextAction, PhysicalCard specialDelivery, PhysicalCard escort) {
        super(Type.SPECIAL_DELIVERY_COMPLETED, performingPlayerId);
        _gameTextAction = gameTextAction;
        _specialDelivery = specialDelivery;
        _escort = escort;
    }

    /**
     * Gets the game text action for completing Special Delivery.
     * @return the game text action
     */
    public GameTextAction getGameTextAction() {
        return _gameTextAction;
    }

    /**
     * Gets the Special Delivery card.
     * @return the Special Delivery card
     */
    public PhysicalCard getSpecialDelivery() {
        return _specialDelivery;
    }

    /**
     * Gets the escort that completed the Special Delivery.
     * @return the escort
     */
    public PhysicalCard getEscort() {
        return _escort;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_specialDelivery) + " is completed by " + GameUtils.getCardLink(_escort);
    }
}
