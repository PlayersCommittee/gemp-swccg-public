package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.cards.effects.PayDeployCostEffect;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.AbstractPlayCardAction;
import com.gempukku.swccgo.logic.effects.DeploySingleCardEffect;
import com.gempukku.swccgo.logic.effects.PayExtraCostToDeployCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChoosePlayerEffect;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.StandardEffect;
import com.gempukku.swccgo.logic.timing.TargetingEffect;

import java.util.List;


/**
 * This action plays a card to a zone based on the specified play card option.
 */
public class PlayCardToZoneAction extends AbstractPlayCardAction {
    private AbstractPlayCardAction _that;
    private boolean _actionInitiated;
    private boolean _actionComplete;
    private Zone _playedToZone;
    private PlayCardOption _playCardOption;
    private String _playedToZoneOwner;
    private boolean _extraCostAdded;
    private boolean _forFree;
    private float _changeInCost;
    private boolean _zoneOwnerStep1Complete;
    private boolean _zoneOwnerStep2Complete;
    private ChoosePlayerEffect _chooseZoneOwnerEffect;
    private boolean _additionalTargetingPerformed;
    private boolean _forceCostApplied;
    private DeploySingleCardEffect _playCardEffect;
    private boolean _cardPlayed;

    /**
     * Creates an action that plays a card to a zone based on the specified play card option.
     * @param sourceCard the card to initiate the deployment
     * @param cardToPlay the card to play
     * @param playCardOption the play card option
     * @param forFree true if playing card for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     */
    public PlayCardToZoneAction(PhysicalCard sourceCard, PhysicalCard cardToPlay, PlayCardOption playCardOption, boolean forFree, float changeInCost) {
        super(cardToPlay, sourceCard);
        setPerformingPlayer(cardToPlay.getOwner());
        _that = this;
        _cardToPlay = cardToPlay;
        _playedToZone = playCardOption.getZone();
        _playCardOption = playCardOption;
        _forFree = forFree;
        _changeInCost = changeInCost;
        _text = _cardToPlay.getBlueprint().isCardTypeDeployed() ? "Deploy" : "Play";
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
                List<TargetingEffect> additionalTargetingEffects = _cardToPlay.getBlueprint().getTargetCardsWhenDeployedEffects(_that, getPerformingPlayer(), game, _cardToPlay, null, _playCardOption);
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
                appendBeforeCost(new PayExtraCostToDeployCardEffect(this, _cardToPlay, null, _forFree));
                return getNextCost();
            }

            // Pay the deploy cost(s)
            if (!_forceCostApplied) {
                _forceCostApplied = true;

                if (!_forFree) {
                    // Check if card has special cost (e.g. lose Force to play)
                    StandardEffect specialDeployCostEffect = _cardToPlay.getBlueprint().getSpecialDeployCostEffect(_that, getPerformingPlayer(), game, _cardToPlay, null, _playCardOption);
                    if (specialDeployCostEffect != null)
                        appendCost(specialDeployCostEffect);
                    else
                        appendCost(new PayDeployCostEffect(_that, _cardToPlay, null, _playCardOption, _changeInCost, null));
                    return getNextCost();
                }
            }

            // Play the card
            if (!_cardPlayed) {
                _cardPlayed = true;

                _playCardEffect = new DeploySingleCardEffect(_that, _cardToPlay, _playedToZone, !_cardToPlay.getOwner().equals(_playedToZoneOwner), _playCardOption.getId(), _reshuffle);
                return _playCardEffect;
            }
        }

        if (!_actionComplete) {
            _actionComplete = true;
            gameState.endPlayCard();
        }

        return null;
    }

    public boolean wasCarriedOut() {
        return _cardPlayed && _playCardEffect.wasCarriedOut();
    }
}
