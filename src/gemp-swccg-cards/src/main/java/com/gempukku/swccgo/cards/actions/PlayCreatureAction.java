package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.cards.effects.PayDeployCostEffect;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractPlayCardAction;
import com.gempukku.swccgo.logic.effects.DeploySingleCardEffect;
import com.gempukku.swccgo.logic.effects.PayExtraCostToDeployCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

/**
 * The action to deploy a creature.
 */
public class PlayCreatureAction extends AbstractPlayCardAction {
    private Action _that;
    private boolean _actionInitiated;
    private boolean _actionComplete;
    private boolean _extraCostAdded;
    private PhysicalCard _creature;
    private boolean _forFree;
    private float _changeInCost;
    private PhysicalCard _target;
    private boolean _forceCostApplied;
    private Effect _playCardEffect;
    private boolean _cardPlayed;

    /**
     * Creates an action to deploy the specified creature.
     * @param game the game
     * @param sourceCard the card to initiate the deployment
     * @param creature the creature to deploy
     * @param forFree true if deploying creature for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param deployTargetFilter the filter for where the creature can be deployed
     */
    public PlayCreatureAction(final SwccgGame game, final PhysicalCard sourceCard, final PhysicalCard creature, boolean forFree, float changeInCost, final Filter deployTargetFilter) {
        super(creature, sourceCard);
        setPerformingPlayer(creature.getOwner());
        _that = this;
        _creature = creature;
        _forFree = forFree;
        _changeInCost = changeInCost;
        _text = "Deploy";

        appendTargeting(
                new ChooseCardOnTableEffect(_that, getPerformingPlayer(), "Choose where to deploy " + GameUtils.getCardLink(creature), deployTargetFilter) {
                    @Override
                    protected void cardSelected(PhysicalCard target) {
                        _target = target;

                        // If deploying at location, deploy creature.
                        if (Filters.location.accepts(game.getGameState(), game.getModifiersQuerying(), _target)) {
                            _playCardEffect = new DeploySingleCardEffect(_that, _creature, _target, false, null, null, PlayCardOptionId.PLAY_CARD_OPTION_1, _reshuffle);
                        }
                        // If attaching to a card, deploy creature.
                        else {
                            _playCardEffect = new DeploySingleCardEffect(_that, _creature, false, _target, null, null, PlayCardOptionId.PLAY_CARD_OPTION_1, _reshuffle);
                        }
                    }
                }
        );
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        if (!_actionInitiated) {
            _actionInitiated = true;
            gameState.beginPlayCard(this);
        }

        // Verify no costs have failed
        if (!isAnyCostFailed()) {

            // Perform any costs in the queue
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            // Add any extra cost to deploy
            if (!_extraCostAdded) {
                _extraCostAdded = true;
                appendBeforeCost(new PayExtraCostToDeployCardEffect(this, _cardToPlay, _target, _forFree));
                return getNextCost();
            }

            // Pay the deploy cost(s)
            if (!_forceCostApplied) {
                _forceCostApplied = true;

                if (!_forFree) {
                    appendCost(new PayDeployCostEffect(_that, _creature, _target, null, _changeInCost, null));
                    return getNextCost();
                }
            }

            // Play the card
            if (!_cardPlayed) {
                _cardPlayed = true;

                return _playCardEffect;
            }
        }

        if (!_actionComplete) {
            _actionComplete = true;
            gameState.endPlayCard();
        }

        return null;
    }

    @Override
    public boolean wasCarriedOut() {
        return _cardPlayed && _playCardEffect.wasCarriedOut();
    }
}
