package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier which causes the source card to allow other specified cards to move away as a 'react' from a creature attack.
 * FLAG(Chief): new attack-react modifier type used by R2 Sensor Array (3_31). Parallel to battle/Force drain react paths.
 */
public class MayMoveOtherCardsAsReactFromAttackModifier extends AbstractModifier {
    private String _actionText;
    private Filter _cardFilter;
    private Filter _locationFilter;
    private float _changeInCost;

    public MayMoveOtherCardsAsReactFromAttackModifier(PhysicalCard source, String actionText, String playerId, Filterable cardFilter) {
        this(source, actionText, null, playerId, cardFilter, Filters.location, 0);
    }

    public MayMoveOtherCardsAsReactFromAttackModifier(PhysicalCard source, String actionText, Condition condition, String playerId, Filterable cardFilter) {
        this(source, actionText, condition, playerId, cardFilter, Filters.location, 0);
    }

    public MayMoveOtherCardsAsReactFromAttackModifier(PhysicalCard source, String actionText, Condition condition, String playerId, Filterable cardFilter, Filterable locationFilter, float changeInCost) {
        super(source, null, source, condition, ModifierType.MAY_MOVE_OTHER_CARD_AS_REACT_FROM_ATTACK, true);
        _actionText = actionText;
        _playerId = playerId;
        _cardFilter = Filters.and(cardFilter, Filters.in_play, Filters.character);
        _locationFilter = Filters.and(Filters.location, locationFilter);
        _changeInCost = changeInCost;
    }

    @Override
    public String getActionText() {
        return _actionText;
    }

    @Override
    public Filter getCardToReactFilter() {
        return _cardFilter;
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return Filters.and(_locationFilter).accepts(gameState, modifiersQuerying, target);
    }

    @Override
    public Filter getTargetFilter() {
        return _locationFilter;
    }

    @Override
    public float getChangeInCost() {
        return _changeInCost;
    }
}
