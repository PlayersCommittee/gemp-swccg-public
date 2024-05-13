package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when a Force is lost.
 */
public class LostForceResult extends EffectResult {
    private PhysicalCard _source;
    private PhysicalCard _cardLost;
    private Zone _fromZone;
    private int _amountLostSoFar;
    private boolean _isFromForceDrain;
    private boolean _isBattleDamage;
    private PhysicalCard _stackFaceDownOn;

    /**
     * Creates an effect result that is triggered when a Force is lost.
     * @param sourceCard the source card of the Force loss, or null
     * @param playerId the player that lost a Force
     * @param cardLost the unit of Force lost
     * @param fromZone the zone the card was lost from
     * @param amountLostSoFar the amount of Force lost so far in the current Force loss process
     * @param isFromForceDrain true if the Force is lost from Force Drain, otherwise, false
     * @param isBattleDamage true if the Force is lost to battle damage, otherwise, false
     * @param stackFaceDownOn the card the Force is stacked face down on, or null
     */
    public LostForceResult(PhysicalCard sourceCard, String playerId, PhysicalCard cardLost, Zone fromZone, int amountLostSoFar, boolean isFromForceDrain, boolean isBattleDamage, PhysicalCard stackFaceDownOn) {
        super(Type.FORCE_LOST, playerId);
        _source = sourceCard;
        _cardLost = cardLost;
        _fromZone = fromZone;
        _amountLostSoFar = amountLostSoFar;
        _isFromForceDrain = isFromForceDrain;
        _isBattleDamage = isBattleDamage;
        _stackFaceDownOn = stackFaceDownOn;
    }

    /**
     * Gets the source card of the Force loss.
     * @return the source card, or null
     */
    public PhysicalCard getSourceCard() {
        return _source;
    }

    /**
     * Gets the unit of Force that was lost.
     * @return the card
     */
    public PhysicalCard getCardLost() {
        return _cardLost;
    }

    /**
     * Gets the zone the Force was lost from.
     * @return the zone
     */
    public Zone getZone() {
        return _fromZone;
    }

    /**
     * Gets the amount of Force lost in the current Force loss process.
     * @return the amount of Force lost
     */
    public int getAmountOfForceLost() {
        return _amountLostSoFar;
    }

    /**
     * Determines if the Force loss is from a Force drain.
     * @return true if Force loss is from a Force drain, otherwise false
     */
    public boolean isFromForceDrain() {
        return _isFromForceDrain;
    }

    /**
     * Determines if the Force loss is from battle damage.
     * @return true if Force loss is from battle damage, otherwise false
     */
    public boolean isBattleDamage() {
        return _isBattleDamage;
    }

    /**
     * Gets the card the lost Force was stacked on, or null if it wasn't stacked.
     * @return the card the lost Force was stacked on, or null
     */
    public PhysicalCard getForceStackedOn() {
        return _stackFaceDownOn;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Just lost " + getAmountOfForceLost() + " Force";
    }
}
