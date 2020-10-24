package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

import java.util.Collection;

/**
 * An evaluator that returns the number of Force icons (Dark and/or Light) "present" at location the specified card is
 * "at" (or "present" at the location, if the specified card is a location).
 * Includes icons on the location itself as well as Jedi Master/Dark Jedi Master "present" at the location.
 */
public class ForceIconsPresentEvaluator extends BaseEvaluator {
    private int _permSourceCardId;
    private boolean _darkForce;
    private boolean _lightForce;

    /**
     * Creates an evaluator that returns the number of Force icons (Dark and/or Light) "present" at location the specified
     * card is "at" (or "present" at the location, if the specified card is a location).
     * @param card the card
     * @param darkForce true if Dark Force icons should be counted, otherwise false
     * @param lightForce true if Light Force icons should be counted, otherwise false
     */
    public ForceIconsPresentEvaluator(PhysicalCard card, boolean darkForce, boolean lightForce) {
        _permSourceCardId = card.getPermanentCardId();
        _darkForce = darkForce;
        _lightForce = lightForce;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);

        PhysicalCard location = modifiersQuerying.getLocationHere(gameState, source);
        if (location == null)
            return 0;

        if (!Filters.presentAt(Filters.sameCardId(location)).accepts(gameState.getGame(), source)) {
            return 0;
        }

        Collection<PhysicalCard> cards = Filters.filterActive(gameState.getGame(), source, Filters.present(location));

        int count = 0;
        if (_darkForce) {
            count += modifiersQuerying.getIconCount(gameState, location, Icon.DARK_FORCE);
            for (PhysicalCard card : cards) {
                count += modifiersQuerying.getIconCount(gameState, card, Icon.DARK_FORCE);
            }
        }
        if (_lightForce) {
            count += modifiersQuerying.getIconCount(gameState, location, Icon.LIGHT_FORCE);
            for (PhysicalCard card : cards) {
                count += modifiersQuerying.getIconCount(gameState, card, Icon.LIGHT_FORCE);
            }
        }

        return count;
    }
}
