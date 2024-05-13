package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.cards.effects.PayDeployCostEffect;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.ReactActionOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractPlayCardAction;
import com.gempukku.swccgo.logic.effects.DeploySingleCardEffect;
import com.gempukku.swccgo.logic.effects.PayExtraCostToDeployCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChoosePlayerEffect;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.StandardEffect;
import com.gempukku.swccgo.logic.timing.TargetingEffect;

import java.util.List;


/**
 * The action to play a card to a location.
 */
public class PlayCardToLocationAction extends AbstractPlayCardAction {
    private AbstractPlayCardAction _that;
    private boolean _actionInitiated;
    private boolean _actionComplete;
    private PlayCardOption _playCardOption;
    private boolean _extraCostAdded;
    private boolean _forFree;
    private float _changeInCost;
    private ReactActionOption _reactActionOption;
    private String _playedToZoneOwner;
    private PhysicalCard _target;
    private boolean _zoneOwnerStep1Complete;
    private boolean _zoneOwnerStep2Complete;
    private ChoosePlayerEffect _chooseZoneOwnerEffect;
    private boolean _additionalTargetingPerformed;
    private boolean _forceCostApplied;
    private boolean _cardPlayed;
    private DeploySingleCardEffect _playCardEffect;

    /**
     * Creates an action to play the specified card to a location.
     * @param sourceCard the card to initiate the deployment
     * @param cardToDeploy the card to deploy
     * @param playCardOption the play card option chosen
     * @param forFree true if deploying character for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @param deployTargetFilter the filter for where the card can be deployed
     */
    public PlayCardToLocationAction(PhysicalCard sourceCard, final PhysicalCard cardToDeploy, PlayCardOption playCardOption, boolean forFree, float changeInCost, ReactActionOption reactActionOption, final Filter deployTargetFilter) {
        super(cardToDeploy, sourceCard);
        setPerformingPlayer(cardToDeploy.getOwner());
        _that = this;
        _playCardOption = playCardOption;
        _forFree = forFree;
        _changeInCost = changeInCost;
        _reactActionOption = reactActionOption;
        _text = _cardToPlay.getBlueprint().isCardTypeDeployed() ? "Deploy" : "Play";
        if (_reactActionOption != null) {
            _text = _text + " as a 'react'";
        }

        appendTargeting(
                new ChooseCardOnTableEffect(_that, getPerformingPlayer(), "Choose location where to " + _text.toLowerCase() + " " + GameUtils.getCardLink(cardToDeploy), deployTargetFilter) {
                    @Override
                    protected void cardSelected(PhysicalCard target) {
                        _target = target;
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

            // Determine the zone owner
            if (!_zoneOwnerStep2Complete) {
                if (!_zoneOwnerStep1Complete) {
                    _zoneOwnerStep1Complete = true;

                    _playedToZoneOwner = _playCardOption.getZoneOwner(getPerformingPlayer(), game.getGameState());
                    if (_playedToZoneOwner != null) {
                        _zoneOwnerStep2Complete = true;
                    } else {
                        _chooseZoneOwnerEffect = _playCardOption.getChoosePlayerZoneEffect(_that, getPerformingPlayer(), game.getGameState());
                        appendTargeting(_chooseZoneOwnerEffect);
                        return getNextCost();
                    }
                } else {
                    _zoneOwnerStep2Complete = true;
                    _playedToZoneOwner = _chooseZoneOwnerEffect.getPlayerChosen();
                }
            }

            // Perform any additional targeting
            if (!_additionalTargetingPerformed) {
                _additionalTargetingPerformed = true;

                // Check if card has additional targeting when being deployed
                List<TargetingEffect> additionalTargetingEffects = _cardToPlay.getBlueprint().getTargetCardsWhenDeployedEffects(_that, getPerformingPlayer(), game, _cardToPlay, _target, _playCardOption);
                if (additionalTargetingEffects != null && !additionalTargetingEffects.isEmpty()) {
                    for (TargetingEffect additionalTargetingEffect : additionalTargetingEffects) {
                        appendTargeting(additionalTargetingEffect);
                    }
                    return getNextCost();
                }
            }

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
                    // Check if card has special cost (e.g. lose Force to play)
                    StandardEffect specialDeployCostEffect = _cardToPlay.getBlueprint().getSpecialDeployCostEffect(_that, getPerformingPlayer(), game, _cardToPlay, _target, _playCardOption);
                    if (specialDeployCostEffect != null)
                        appendCost(specialDeployCostEffect);
                    else
                        appendCost(new PayDeployCostEffect(_that, _cardToPlay, _target, _playCardOption, _changeInCost, _reactActionOption));
                    return getNextCost();
                }
            }

            // Play the card
            if (!_cardPlayed) {
                _cardPlayed = true;

                _playCardEffect = new DeploySingleCardEffect(_that, _cardToPlay, _target, !_cardToPlay.getOwner().equals(_playedToZoneOwner), null, _reactActionOption, _playCardOption.getId(), _reshuffle);
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
