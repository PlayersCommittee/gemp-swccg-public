package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;


/**
 * An effect that causes the specified player to retrieve a specified amount of Force.
 */
public class RetrieveForceEffect extends ForceRetrievalEffect {

    /**
     * Creates an effect that causes the specified player to retrieve a specified amount of Force.
     *
     * @param action   the action performing this effect
     * @param playerId the player to retrieve Force
     * @param amount   the amount of Force to retrieve
     */
    public RetrieveForceEffect(PhysicalCard sourceCard, Action action, String playerId, float amount) {
        super(sourceCard, action, playerId, Zone.USED_PILE, amount, false);
    }

    /**
     * Creates an effect that causes the specified player to retrieve a specified amount of Force.
     * @param action the action performing this effect
     * @param playerId the player to retrieve Force
     * @param amount the amount of Force to retrieve
     */
    public RetrieveForceEffect(Action action, String playerId, float amount) {
        this(action, playerId, amount, false);
    }

    /**
     * Creates an effect that causes the specified player to retrieve a specified amount of Force.
     * @param action the action performing this effect
     * @param playerId the player to retrieve Force
     * @param amount the amount of Force to retrieve
     * @param randomly true if cards are retrieved randomly, otherwise false
     */
    public RetrieveForceEffect(Action action, String playerId, float amount, boolean randomly) {
        super(action, playerId, Zone.USED_PILE, amount, randomly);
    }

    /**
     * A callback method for the cards retrieved.
     * @param retrievedCards the cards retrieved
     */
    @Override
    protected final void cardsRetrieved(Collection<PhysicalCard> retrievedCards) {
        if (retrievedCards.size() == 1) {
            cardRetrieved(retrievedCards.iterator().next());
        }
    }

    /**
     * A callback method for the card retrieved.
     * @param retrievedCard the card retrieved
     */
    protected void cardRetrieved(PhysicalCard retrievedCard) {
    }
}
