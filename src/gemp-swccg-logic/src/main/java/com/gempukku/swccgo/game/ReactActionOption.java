package com.gempukku.swccgo.game;


import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;

/**
 * This class is used to represent a 'react' action option.
 */
public class ReactActionOption {
    private PhysicalCard _source;
    private boolean _forFree;
    private float _changeInCost;
    private boolean _reactAway;
    private String _actionText;
    private Filterable _cardToReactFilter;
    private Filter _forFreeCardFilter;
    private Filter _targetFilter;
    private Filter _deployWithPilotOrDriverFilter;
    private boolean _grantDeployToTarget;

    /**
     * Creates a 'react' option.
     * @param source the source card of the 'react' action
     * @param forFree true if 'react' is free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param reactAway true if a 'react' away, otherwise false
     * @param actionText the text to show on User Interface to identify the action choice
     * @param cardToReactFilter the filter for cards that can 'react'
     * @param targetFilter the filter for where cards can 'react' to
     * @param deployWithPilotOrDriverFilter the filter for card that must deploy as pilot or driver during 'react'
     * @param grantDeployToTarget true if card deployed as a 'react' is granted to deploy to target, otherwise false
     */
    public ReactActionOption(PhysicalCard source, boolean forFree, float changeInCost, boolean reactAway, String actionText, Filterable cardToReactFilter, Filter targetFilter, Filter deployWithPilotOrDriverFilter, boolean grantDeployToTarget) {
        _source = source;
        _forFree = forFree;
        _changeInCost = changeInCost;
        _reactAway = reactAway;
        _actionText = actionText;
        _cardToReactFilter = cardToReactFilter;
        _targetFilter = targetFilter;
        _deployWithPilotOrDriverFilter = deployWithPilotOrDriverFilter;
        _grantDeployToTarget = grantDeployToTarget;
    }

    /**
     * Gets the source card.
     * @return the source card
     */
    public PhysicalCard getSource() {
        return _source;
    }

    /**
     * Sets if the 'react' is free.
     * @param forFree true the 'react' is free, otherwise false
     */
    public void setForFree(boolean forFree) {
        _forFree = forFree;
    }

    /**
     * Determines if the 'react' is free.
     * @return true or false
     */
    public boolean isForFree() {
        return _forFree;
    }

    /**
     * Gets the change in cost (positive or negative) to 'react'.
     * @return the change in cost to 'react'
     */
    public float getChangeInCost() {
        return _changeInCost;
    }

    /**
     * Determines if a move as 'react' away.
     * @return true or false
     */
    public boolean isReactAway() {
        return _reactAway;
    }

    /**
     * Gets the text show on User Interface to identify the action choice.
     * @return the text
     */
    public String getActionText() {
        return _actionText;
    }

    /**
     * Gets filter for the cards that can 'react'.
     * @return the filter
     */
    public Filterable getCardToReactFilter() {
        return _cardToReactFilter;
    }

    /**
     * Sets filter for the cards that can 'react'.
     * @param filter the filter
     */
    public void setCardToReactFilter(Filterable filter) {
        _cardToReactFilter = filter;
    }

    /**
     * Gets filter for the cards that 'react' for free even when isForFree is false.
     * @return the filter
     */
    public Filter getForFreeCardFilter() {
        return _forFreeCardFilter;
    }

    /**
     * Sets filter for the cards that 'react' for free even when isForFree is false.
     * @param filter the filter
     */
    public void setForFreeCardFilter(Filter filter) {
        _forFreeCardFilter = filter;
    }

    /**
     * Gets filter for where cards that can 'react' to.
     * @return the filter
     */
    public Filter getTargetFilter() {
        return _targetFilter;
    }

    /**
     * Sets filter for where cards can 'react' to.
     * @param filter the filter
     */
    public void setTargetFilter(Filter filter) {
        _targetFilter = filter;
    }

    /**
     * Gets filter for pilot or driver that card must deploy as 'react' with, or null if no pilot or driver required.
     * @return the filter, or null
     */
    public Filter getDeployWithPilotOrDriverFilter() {
        return _deployWithPilotOrDriverFilter;
    }

    /**
     * Determines if the modifier also grants deployment to the target.
     * @return true or false
     */
    public boolean isGrantedDeployToTarget() {
        return _grantDeployToTarget;
    }
}
