package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.cards.effects.PayDeployCostEffect;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractPlayCardAction;
import com.gempukku.swccgo.logic.effects.DeploySingleCardEffect;
import com.gempukku.swccgo.logic.effects.PayExtraCostToDeployCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Effect;


/**
 * The action to play a card to a location.
 */
public class PlayDejarikAction extends AbstractPlayCardAction {
    private PhysicalCard _cardToPlay;
    private boolean _actionInitiated;
    private boolean _actionComplete;
    private boolean _extraCostAdded;
    private boolean _forFree;
    private PhysicalCard _target;
    private boolean _forceCostApplied;
    private boolean _cardPlayed;
    private Effect _playCardEffect;

    /**
     * Creates an action to play the specified card using 'Dejarik Rules' to a holosite.
     * @param sourceCard the card to initiate the deployment
     * @param cardToDeploy the card to deploy
     * @param forFree true if deploying card for free, otherwise false
     */
    public PlayDejarikAction(final PhysicalCard sourceCard, final PhysicalCard cardToDeploy, boolean forFree) {
        super(cardToDeploy, sourceCard);
        setPerformingPlayer(cardToDeploy.getOwner());
        _cardToPlay = cardToDeploy;
        _forFree = forFree;
        _text = "Deploy to holosite";

        appendTargeting(
                new ChooseCardOnTableEffect(this, getPerformingPlayer(), "Choose where to deploy " + GameUtils.getCardLink(cardToDeploy), Filters.holosite) {
                    @Override
                    protected void cardSelected(PhysicalCard target) {
                        _target = target;
                    }
                });
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
                    appendCost(new PayDeployCostEffect(this, _cardToPlay, _target, null, 0, null, true));
                    return getNextCost();
                }
            }

            // Play the card
            if (!_cardPlayed) {
                _cardPlayed = true;

                _playCardEffect = new DeploySingleCardEffect(this, _cardToPlay, _target, false, null, null, PlayCardOptionId.PLAY_AS_DEJARIK, _reshuffle);
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
