package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier which allows the source card to deploy other specified cards as a 'react' to specified targets.
 */
public class MayDeployOtherCardsAsReactToTargetModifier extends AbstractModifier {
    private String _actionText;
    private Filter _deployFilter;
    private Filter _targetFilter;
    private float _changeInCost;
    private boolean _grantDeployToTarget;

    /**
     * Creates a modifier which allows the source card to deploy cards accepted by the deploy filter as a 'react' to targets
     * accepted by the target filter.
     * @param source the source of the modifier
     * @param actionText the text to show for the action on the User Interface
     * @param playerId the player that may deploy cards as a 'react', or null if either player may deploy cards as a 'react'
     * @param deployFilter the deploy filter
     * @param targetFilter the target filter
     */
    public MayDeployOtherCardsAsReactToTargetModifier(PhysicalCard source, String actionText, String playerId, Filterable deployFilter, Filterable targetFilter) {
        this(source, actionText, null, playerId, deployFilter, targetFilter, 0, false);
    }

    /**
     * Creates a modifier which allows the source card to deploy cards accepted by the deploy filter as a 'react' to targets
     * accepted by the target filter.
     * @param source the source of the modifier
     * @param actionText the text to show for the action on the User Interface
     * @param playerId the player that may deploy cards as a 'react', or null if either player may deploy cards as a 'react'
     * @param deployFilter the deploy filter
     * @param targetFilter the target filter
     * @param grantDeployToTarget true if card deployed as a 'react' is granted to deploy to target, otherwise false
     */
    public MayDeployOtherCardsAsReactToTargetModifier(PhysicalCard source, String actionText, String playerId, Filterable deployFilter, Filterable targetFilter, boolean grantDeployToTarget) {
        this(source, actionText, null, playerId, deployFilter, targetFilter, 0, grantDeployToTarget);
    }

    /**
     * Creates a modifier which allows the source card to deploy cards accepted by the deploy filter as a 'react' to targets
     * accepted by the target filter.
     * @param source the source of the modifier
     * @param actionText the text to show for the action on the User Interface
     * @param playerId the player that may deploy cards as a 'react', or null if either player may deploy cards as a 'react'
     * @param deployFilter the deploy filter
     * @param targetFilter the target filter
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param grantDeployToTarget true if card deployed as a 'react' is granted to deploy to target, otherwise false
     */
    public MayDeployOtherCardsAsReactToTargetModifier(PhysicalCard source, String actionText, String playerId, Filterable deployFilter, Filterable targetFilter, float changeInCost, boolean grantDeployToTarget) {
        this(source, actionText, null, playerId, deployFilter, targetFilter, changeInCost, grantDeployToTarget);
    }

    /**
     * Creates a modifier which allows the source card to deploy cards accepted by the deploy filter as a 'react' to targets
     * accepted by the target filter.
     * @param source the source of the modifier
     * @param actionText the text to show for the action on the User Interface
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player that may deploy cards as a 'react', or null if either player may deploy cards as a 'react'
     * @param deployFilter the deploy filter
     * @param targetFilter the target filter
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param grantDeployToTarget true if card deployed as a 'react' is granted to deploy to target, otherwise false
     */
    public MayDeployOtherCardsAsReactToTargetModifier(PhysicalCard source, String actionText, Condition condition, String playerId, Filterable deployFilter, Filterable targetFilter, float changeInCost, boolean grantDeployToTarget) {
        super(source, null, source, condition, ModifierType.MAY_DEPLOY_OTHER_CARD_AS_REACT_TO_TARGET, true);
        _actionText = actionText;
        _playerId = playerId;
        _deployFilter = Filters.and(deployFilter, Filters.or(Filters.In_Hand, Filters.canDeployAsIfFromHand),
                Filters.or(Filters.character, Filters.device, Filters.starship, Filters.vehicle, Filters.weapon));
        _targetFilter = Filters.and(targetFilter, Filters.in_play);
        _changeInCost = changeInCost;
        _grantDeployToTarget = grantDeployToTarget;
    }

    @Override
    public String getActionText() {
        return _actionText;
    }

    @Override
    public Filter getCardToReactFilter() {
        return _deployFilter;
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return Filters.and(_targetFilter).accepts(gameState, modifiersQuerying, target);
    }

    @Override
    public Filter getTargetFilter() {
        return _targetFilter;
    }

    @Override
    public float getChangeInCost() {
        return _changeInCost;
    }

    @Override
    public boolean isGrantedToDeployToTarget() {
        return _grantDeployToTarget;
    }
}
