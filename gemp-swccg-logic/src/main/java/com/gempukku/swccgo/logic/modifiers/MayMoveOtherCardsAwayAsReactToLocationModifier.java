package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier which causes the source card to allow other specified cards to move away as a 'react' to specified locations.
 */
public class MayMoveOtherCardsAwayAsReactToLocationModifier extends AbstractModifier {
    private String _actionText;
    private Filter _cardFilter;
    private Filter _locationFilter;
    private float _changeInCost;

    /**
     * Creates a modifier which causes the source card to allow other cards accepted by the card filter to move away as a 'react'
     * to locations accepted by the target filter.
     * @param source the source of the modifier
     * @param actionText the text to show for the action on the User Interface
     * @param playerId the player that may move cards as a 'react', or null if either player may move cards away as a 'react'
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     */
    public MayMoveOtherCardsAwayAsReactToLocationModifier(PhysicalCard source, String actionText, String playerId, Filterable cardFilter, Filterable locationFilter) {
        this(source, actionText, null, playerId, cardFilter, locationFilter, 0);
    }

    /**
     * Creates a modifier which causes the source card to allow other cards accepted by the card filter to move away as a 'react'
     * to locations accepted by the target filter.
     * @param source the source of the modifier
     * @param actionText the text to show for the action on the User Interface
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player that may move cards as a 'react', or null if either player may move cards away as a 'react'
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     */
    public MayMoveOtherCardsAwayAsReactToLocationModifier(PhysicalCard source, String actionText, Condition condition, String playerId, Filterable cardFilter, Filterable locationFilter) {
        this(source, actionText, condition, playerId, cardFilter, locationFilter, 0);
    }

    /**
     * Creates a modifier which causes the source card to allow other cards accepted by the card filter to move away as a 'react'
     * to locations accepted by the target filter.
     * @param source the source of the modifier
     * @param actionText the text to show for the action on the User Interface
     * @param playerId the player that may move cards as a 'react', or null if either player may move cards away as a 'react'
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     * @param changeInCost change in amount of Force (can be positive or negative) required
     */
    public MayMoveOtherCardsAwayAsReactToLocationModifier(PhysicalCard source, String actionText, String playerId, Filterable cardFilter, Filterable locationFilter, float changeInCost) {
        this(source, actionText, null, playerId, cardFilter, locationFilter, changeInCost);
    }

    /**
     * Creates a modifier which causes the source card to allow other cards accepted by the card filter to move away as a 'react'
     * to locations accepted by the target filter.
     * @param source the source of the modifier
     * @param actionText the text to show for the action on the User Interface
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player that may move cards as a 'react', or null if either player may move cards away as a 'react'
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     * @param changeInCost change in amount of Force (can be positive or negative) required
     */
    public MayMoveOtherCardsAwayAsReactToLocationModifier(PhysicalCard source, String actionText, Condition condition, String playerId, Filterable cardFilter, Filterable locationFilter, float changeInCost) {
        super(source, null, source, condition, ModifierType.MAY_MOVE_OTHER_CARD_AWAY_AS_REACT_TO_LOCATION, true);
        _actionText = actionText;
        _playerId = playerId;
        _cardFilter = Filters.and(cardFilter, Filters.in_play, Filters.or(Filters.character, Filters.starship, Filters.vehicle));
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
