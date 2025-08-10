package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier to the number of cards drawn in starting hand.
 */
public class NumCardsDrawnInStartingHandModifier extends AbstractModifier {
    private int _numCards;

    /**
     * Creates a modifier to the number of cards drawn in specified player's starting hand.
     * @param source the source of the modifier
     * @param numCards the number of cards
     * @param playerId the player
     */
    public NumCardsDrawnInStartingHandModifier(PhysicalCard source, int numCards, String playerId) {
        super(source, null, null, null, ModifierType.NUM_CARDS_DRAWN_IN_STARTING_HAND, true);
        _numCards = numCards;
        _playerId = playerId;
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _numCards;
    }
}
