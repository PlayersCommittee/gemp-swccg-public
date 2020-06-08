package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

import java.util.Collection;

/**
 * An evaluator that returns the power of the highest forfeit character for the specified player at the location the specified
 * card is "at" (or the location itself if the specified card is a location).
 */
public class PowerOfHighestForfeitCharacterHereEvaluator extends BaseEvaluator {
    private int _permSourceCardId;
    private String _playerId;

    /**
     * Creates an evaluator that returns the power of the highest forfeit character for the specified player at the location the specified
     * card is "at" (or the location itself if the specified card is a location).
     * @param card the card
     * @param playerId the player
     */
    public PowerOfHighestForfeitCharacterHereEvaluator(PhysicalCard card, String playerId) {
        _permSourceCardId = card.getPermanentCardId();
        _playerId = playerId;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);

        float powerToUse = 0;
        float highestForfeit = 0;
        Collection<PhysicalCard> characters = Filters.filterActive(gameState.getGame(), source, Filters.and(Filters.owner(_playerId), Filters.character, Filters.here(source)));
        for (PhysicalCard character : characters) {
            float forfeit = modifiersQuerying.getForfeit(gameState, character);
            if (forfeit >= highestForfeit) {
                float power = modifiersQuerying.getPower(gameState, character);
                if (forfeit > highestForfeit) {
                    highestForfeit = forfeit;
                    powerToUse = power;
                }
                else {
                    powerToUse = Math.max(powerToUse, power);
                }
            }
        }

        return powerToUse;
    }
}
