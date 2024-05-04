package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

import java.util.Collection;

/**
 * An evaluator that returns the ability of the highest ability character present at the location the specified card is
 * "at" (or the location itself if the specified card is a location) that is accepted by the specified filter.
 */
public class AbilityOfHighestAbilityCharacterPresentEvaluator extends BaseEvaluator {
    private int _permSourceCardId;
    private Filter _filters;

    /**
     * Creates an evaluator that returns the ability of the highest ability character present at the location the specified
     * card is "at" (or the location itself if the specified card is a location) that is accepted by the specified filter.
     * @param card the card
     * @param filters the filter
     */
    public AbilityOfHighestAbilityCharacterPresentEvaluator(PhysicalCard card, Filterable filters) {
        _permSourceCardId = card.getPermanentCardId();
        _filters = Filters.and(filters);
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);

        Collection<PhysicalCard> characters = Filters.filterActive(gameState.getGame(), source,
                Filters.and(_filters, Filters.character, Filters.hasAbility, Filters.notExcludedFromBeingHighestAbilityCharacter(source), Filters.present(source)));
        float highestAbility = 0;
        for (PhysicalCard character : characters) {
            float ability = modifiersQuerying.getAbility(gameState, character);
            if (ability > highestAbility)
                highestAbility = ability;
        }
        return highestAbility;
    }
}
