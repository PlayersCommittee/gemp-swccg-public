package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

public class EatenResult extends EffectResult {
    private PhysicalCard _cardEaten;
    private PhysicalCard _location;
    private Float _power;
    private Float _ferocity;
    private float _forfeitValue;
    private boolean _captive;
    private PhysicalCard _eatenByCard;

    public EatenResult(PhysicalCard cardEaten, Float power, Float ferocity, float forfeitValue, boolean captive, PhysicalCard eatenByCard, PhysicalCard location) {
        super(Type.EATEN, eatenByCard.getOwner());
        _cardEaten = cardEaten;
        _power = power;
        _ferocity = ferocity;
        _forfeitValue = forfeitValue;
        _captive = captive;
        _eatenByCard = eatenByCard;
        _location = location;
    }

    public PhysicalCard getCardEaten() {
        return _cardEaten;
    }

    public PhysicalCard getEatenAtLocation() {
        return _location;
    }

    public Float getPower() {
        return _power;
    }

    public Float getFerocity() {
        return _ferocity;
    }

    public float getForfeitValue() {
        return _forfeitValue;
    }

    public boolean wasCaptive() {
        return _captive;
    }

    public PhysicalCard getEatenByCard() {
        return _eatenByCard;
    }


    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_cardEaten) + " just eaten by " + GameUtils.getCardLink(_eatenByCard);
    }
}
