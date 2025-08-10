package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.ReactActionOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;
import com.gempukku.swccgo.logic.timing.results.RemovedFromCardPileResult;

import java.util.*;

/**
 * An effect to play two cards simultaneously. One card is played as attached to the other.
 */
public class DeployMultipleCardsSimultaneouslyEffect extends AbstractSubActionEffect implements PlayCardEffect {
    private String _performingPlayerId;
    private PhysicalCard _cardToPlay1;
    private Zone _playedFromZone1;
    private String _playedFromZoneOwner1;
    private PhysicalCard _playedFromStackedOn1;
    private PhysicalCard _cardToPlay2;
    private Zone _playedFromZone2;
    private String _playedFromZoneOwner2;
    private PhysicalCard _playedFromStackedOn2;
    private boolean _playedToOpponentsZone;
    private PhysicalCard _attachTo;
    private boolean _attachAsPilot1;
    private boolean _attachAsPassenger1;
    private boolean _attachAsPilot2;
    private boolean _attachAsPassenger2;
    private PhysicalCard _playedToLocation;
    private boolean _deployInVehicleSlot;
    private boolean _asReact;
    private boolean _reshuffle;
    private boolean _cardsWerePlayed;

    /**
     * Creates an effect to play two cards simultaneously as attached to the specified card.
     * @param action the action performing this effect
     * @param cardToPlay1 the card to play
     * @param attachTo the card to attach the played card to
     * @param cardToPlay2 the second card to play which will play as attached to cardToPlay1
     * @param attachAsPilot2 true if the second card is played as a pilot of cardToPlay1, otherwise false
     * @param deployInVehicleCapacitySlot true if deploying into vehicle capacity slot of cargo bay, otherwise false
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @param reshuffle true if pile the card is played from is reshuffled, otherwise false
     */
    public DeployMultipleCardsSimultaneouslyEffect(Action action, PhysicalCard cardToPlay1, PhysicalCard attachTo, PhysicalCard cardToPlay2, boolean attachAsPilot2,
                                                   boolean deployInVehicleCapacitySlot, ReactActionOption reactActionOption, boolean reshuffle) {
        super(action);
        _performingPlayerId = _action.getPerformingPlayer();
        _cardToPlay1 = cardToPlay1;
        _playedFromZone1 = GameUtils.getZoneFromZoneTop(cardToPlay1.getZone());
        _playedFromZoneOwner1 = cardToPlay1.getZoneOwner();
        if (_playedFromZone1 == Zone.STACKED) {
            _playedFromStackedOn1 = cardToPlay1.getStackedOn();
        }
        _cardToPlay2 = cardToPlay2;
        _playedFromZone2 = GameUtils.getZoneFromZoneTop(cardToPlay2.getZone());
        _playedFromZoneOwner2 = cardToPlay2.getZoneOwner();
        if (_playedFromZone2 == Zone.STACKED) {
            _playedFromStackedOn2 = cardToPlay2.getStackedOn();
        }
        if (cardToPlay2.getBlueprint().getCardCategory() != CardCategory.CHARACTER) {
            throw new UnsupportedOperationException(GameUtils.getFullName(cardToPlay2) + " should not specify if being played as pilot");
        }
        _attachAsPilot2 = attachAsPilot2;
        _attachAsPassenger2 = !attachAsPilot2;
        _attachTo = attachTo;
        _deployInVehicleSlot = deployInVehicleCapacitySlot;
        _asReact = reactActionOption != null;
        _reshuffle = reshuffle;
    }

    /**
     * Creates an effect to play a (character) card as attached to the specified card (with the second card simultaneously
     * played as attached to that character).
     * @param action the action performing this effect
     * @param cardToPlay1 the card to play
     * @param attachTo the card to attach the played card to
     * @param attachAsPilot1 true if the cardToPlay1 is played as a pilot, otherwise false
     * @param cardToPlay2 the second card to play which will play as attached to cardToPlay1
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @param reshuffle true if pile the card is played from is reshuffled, otherwise false
     */
    public DeployMultipleCardsSimultaneouslyEffect(Action action, PhysicalCard cardToPlay1, PhysicalCard attachTo, boolean attachAsPilot1, PhysicalCard cardToPlay2,
                                                   ReactActionOption reactActionOption, boolean reshuffle) {
        super(action);
        _performingPlayerId = _action.getPerformingPlayer();
        _cardToPlay1 = cardToPlay1;
        _playedFromZone1 = GameUtils.getZoneFromZoneTop(cardToPlay1.getZone());
        _playedFromZoneOwner1 = cardToPlay1.getZoneOwner();
        if (_playedFromZone1 == Zone.STACKED) {
            _playedFromStackedOn1 = cardToPlay1.getStackedOn();
        }
        if (cardToPlay1.getBlueprint().getCardCategory() != CardCategory.CHARACTER) {
            throw new UnsupportedOperationException(GameUtils.getFullName(cardToPlay1) + " should not specify if being played as pilot");
        }
        _attachAsPilot1 = attachAsPilot1;
        _attachAsPassenger1 = !attachAsPilot1;
        _cardToPlay2 = cardToPlay2;
        _playedFromZone2 = GameUtils.getZoneFromZoneTop(cardToPlay2.getZone());
        _playedFromZoneOwner2 = cardToPlay2.getZoneOwner();
        if (_playedFromZone2 == Zone.STACKED) {
            _playedFromStackedOn2 = cardToPlay2.getStackedOn();
        }
        _attachTo = attachTo;
        _asReact = reactActionOption != null;
        _reshuffle = reshuffle;
    }

    /**
     * Creates an effect to play two cards simultaneously to the specified location (with the second card attached to the first).
     * @param action the action performing this effect
     * @param cardToPlay1 the card to play
     * @param cardToPlay2 the second card to play which will play as attached to cardToPlay
     * @param playedToLocation the location the card is played to
     * @param playedToOpponentsZone true if card is played to zone owned by opponent
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @param reshuffle true if pile the card is played from is reshuffled, otherwise false
     */
    public DeployMultipleCardsSimultaneouslyEffect(Action action, PhysicalCard cardToPlay1, PhysicalCard cardToPlay2, PhysicalCard playedToLocation,
                                                   boolean playedToOpponentsZone, ReactActionOption reactActionOption, boolean reshuffle) {
        super(action);
        _performingPlayerId = _action.getPerformingPlayer();
        _cardToPlay1 = cardToPlay1;
        _playedFromZone1 = GameUtils.getZoneFromZoneTop(cardToPlay1.getZone());
        _playedFromZoneOwner1 = cardToPlay1.getZoneOwner();
        if (_playedFromZone1 == Zone.STACKED) {
            _playedFromStackedOn1 = cardToPlay1.getStackedOn();
        }
        _cardToPlay2 = cardToPlay2;
        _playedFromZone2 = GameUtils.getZoneFromZoneTop(cardToPlay2.getZone());
        _playedFromZoneOwner2 = cardToPlay2.getZoneOwner();
        if (_playedFromZone2 == Zone.STACKED) {
            _playedFromStackedOn2 = cardToPlay2.getStackedOn();
        }
        _playedToLocation = playedToLocation;
        _playedToOpponentsZone = playedToOpponentsZone;
        _asReact = reactActionOption != null;
        _reshuffle = reshuffle;
    }

    /**
     * Creates an effect to play two cards simultaneously to the specified location (with the second card attached to the first).
     * @param action the action performing this effect
     * @param cardToPlay1 the card to play
     * @param cardToPlay2 the second card to play which will play as attached to cardToPlay
     * @param attachAsPilot2 true if the cardToPlay2 is played as a pilot, otherwise false
     * @param playedToLocation the location the card is played to
     * @param playedToOpponentsZone true if card is played to zone owned by opponent
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @param reshuffle true if pile the card is played from is reshuffled, otherwise false
     */
    public DeployMultipleCardsSimultaneouslyEffect(Action action, PhysicalCard cardToPlay1, PhysicalCard cardToPlay2, boolean attachAsPilot2,
                                                   PhysicalCard playedToLocation, boolean playedToOpponentsZone, ReactActionOption reactActionOption, boolean reshuffle) {
        super(action);
        _performingPlayerId = _action.getPerformingPlayer();
        _cardToPlay1 = cardToPlay1;
        _playedFromZone1 = GameUtils.getZoneFromZoneTop(cardToPlay1.getZone());
        _playedFromZoneOwner1 = cardToPlay1.getZoneOwner();
        if (_playedFromZone1 == Zone.STACKED) {
            _playedFromStackedOn1 = cardToPlay1.getStackedOn();
        }
        _cardToPlay2 = cardToPlay2;
        _playedFromZone2 = GameUtils.getZoneFromZoneTop(cardToPlay2.getZone());
        _playedFromZoneOwner2 = cardToPlay2.getZoneOwner();
        if (_playedFromZone2 == Zone.STACKED) {
            _playedFromStackedOn2 = cardToPlay2.getStackedOn();
        }
        if (cardToPlay2.getBlueprint().getCardCategory() != CardCategory.CHARACTER) {
            throw new UnsupportedOperationException(GameUtils.getFullName(cardToPlay2) + " should not specify if being played as pilot");
        }
        _attachAsPilot2 = attachAsPilot2;
        _attachAsPassenger2 = !attachAsPilot2;
        _playedToLocation = playedToLocation;
        _playedToOpponentsZone = playedToOpponentsZone;
        _asReact = reactActionOption != null;
        _reshuffle = reshuffle;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        final ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();

        final SubAction subAction = new SubAction(_action);
        // Record the card being played
        subAction.appendEffect(
                new RecordCardsBeingPlayedEffect(subAction, new ArrayList<PhysicalCard>(Arrays.asList(_cardToPlay1, _cardToPlay2))));

        // Put cards being played in the "void", and emit effect result to responses that can cancel it, or re-target it.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(final SwccgGame game) {
                        String asReactText = _asReact ? " as a 'react'" : "";
                        final String deployTextBeginning;
                        String deployFrom1 = _playedFromStackedOn1 != null ? GameUtils.getCardLink(_playedFromStackedOn1) : (_playedFromZoneOwner1.equals(_performingPlayerId) ? "" : (_playedFromZoneOwner1 + "'s ")) + _playedFromZone1.getHumanReadable();
                        String deployFrom2 = _playedFromStackedOn2 != null ? GameUtils.getCardLink(_playedFromStackedOn2) : (_playedFromZoneOwner2.equals(_performingPlayerId) ? "" : (_playedFromZoneOwner2 + "'s ")) + _playedFromZone2.getHumanReadable();
                        if (deployFrom1.equals(deployFrom2)) {
                            deployTextBeginning = GameUtils.getCardLink(_cardToPlay1) + " and " + GameUtils.getCardLink(_cardToPlay2) + (_attachAsPilot2 ? (Filters.transport_vehicle.accepts(game, _cardToPlay1) ? " as driver" : " as pilot") : (_attachAsPassenger2 ? " as passenger" : "")) + " from " +
                                    deployFrom1 + " simultaneously " + asReactText;
                        }
                        else {
                            deployTextBeginning = GameUtils.getCardLink(_cardToPlay1) + " from " + deployFrom1 + " and " +
                                    GameUtils.getCardLink(_cardToPlay2) + (_attachAsPilot2 ? (Filters.transport_vehicle.accepts(game, _cardToPlay1) ? " as driver" : " as pilot") : (_attachAsPassenger2 ? " as passenger" : "")) + " from " + deployFrom2 + " simultaneously " + asReactText;
                        }

                        // Remove the card from where it is being played from and add to the void
                        game.getGameState().removeCardsFromZone(Collections.singleton(_cardToPlay1));
                        game.getGameState().removeCardsFromZone(Collections.singleton(_cardToPlay2));
                        gameState.addCardToZone(_cardToPlay1, Zone.VOID, _cardToPlay1.getOwner());
                        gameState.addCardToZone(_cardToPlay2, Zone.VOID, _cardToPlay2.getOwner());

                        // Shuffle the card pile(s)
                        if (_reshuffle) {
                            if (_playedFromZone1.isCardPile())
                                subAction.appendEffect(
                                        new ShufflePileEffect(subAction, null, _performingPlayerId, _playedFromZoneOwner1, _playedFromZone1, true));
                            if (_playedFromZone2.isCardPile()
                                    && (!_playedFromZoneOwner2.equals(_playedFromZoneOwner1) || _playedFromZone2 != _playedFromZone1))
                                subAction.appendEffect(
                                        new ShufflePileEffect(subAction, null, _performingPlayerId, _playedFromZoneOwner2, _playedFromZone2, true));
                        }
                        else {
                            if (_playedFromZone1.isCardPile() || _playedFromZone2.isCardPile()) {
                                actionsEnvironment.emitEffectResult(
                                        new RemovedFromCardPileResult(subAction));
                            }
                        }

                        // Perform the rest of the action
                        subAction.appendEffect(
                                new PassthruEffect(subAction) {
                                    @Override
                                    protected void doPlayEffect(final SwccgGame game) {
                                        // Increment count of times card title was deployed (per game)
                                        for (String title : _cardToPlay1.getTitles()) {
                                            modifiersQuerying.getUntilEndOfGameLimitCounter(title, GameTextActionId.DEPLOY_CARD_ACTION).incrementToLimit(Integer.MAX_VALUE, 1);
                                        }
                                        for (String title : _cardToPlay2.getTitles()) {
                                            modifiersQuerying.getUntilEndOfGameLimitCounter(title, GameTextActionId.DEPLOY_CARD_ACTION).incrementToLimit(Integer.MAX_VALUE, 1);
                                        }

                                        // If the cards being played are being played as a 'react'
                                        if (_asReact) {
                                            final StringBuilder playCardText = new StringBuilder();
                                            PhysicalCard destinationCard;

                                            // Played as attached.
                                            if (_attachTo != null) {
                                                destinationCard = _attachTo;

                                                if (_attachAsPilot1) {
                                                    if (Filters.transport_vehicle.accepts(gameState, modifiersQuerying, destinationCard))
                                                        playCardText.append(" on ").append(GameUtils.getCardLink(destinationCard)).append(" with ").append(GameUtils.getCardLink(_cardToPlay1)).append(" as driver");
                                                    else
                                                        playCardText.append(" on ").append(GameUtils.getCardLink(destinationCard)).append(" with ").append(GameUtils.getCardLink(_cardToPlay1)).append(" as pilot");
                                                } else if (_attachAsPassenger1) {
                                                    playCardText.append(" on ").append(GameUtils.getCardLink(destinationCard)).append(" with ").append(GameUtils.getCardLink(_cardToPlay1)).append(" as passenger");
                                                } else if (Filters.starship.accepts(gameState, modifiersQuerying, destinationCard)
                                                        && !Filters.or(Filters.Effect_of_any_Kind, Filters.starship_weapon).accepts(gameState, modifiersQuerying, _cardToPlay1)) {
                                                    playCardText.append(" into cargo hold of ").append(GameUtils.getCardLink(destinationCard));
                                                } else {
                                                    playCardText.append(" on ").append(GameUtils.getCardLink(destinationCard));
                                                }
                                            }
                                            // Played to location.
                                            else if (_playedToLocation != null) {
                                                destinationCard = _playedToLocation;

                                                if (!_playedToOpponentsZone && Filters.character.accepts(gameState, modifiersQuerying, _cardToPlay1)) {
                                                    playCardText.append(" to ").append(GameUtils.getCardLink(destinationCard)).append(" with ").append(GameUtils.getCardLink(_cardToPlay1)).append(" as an Undercover spy");
                                                } else {
                                                    playCardText.append(" to ").append(GameUtils.getCardLink(destinationCard));
                                                }
                                            } else {
                                                throw new UnsupportedOperationException(GameUtils.getFullName(_cardToPlay1) + " and " + GameUtils.getFullName(_cardToPlay2) + " must play as attached or to location");
                                            }

                                            // Show animations
                                            gameState.showCardOnScreen(_cardToPlay1);
                                            gameState.showCardOnScreen(_cardToPlay2);
                                            gameState.cardAffectsCard(_performingPlayerId, _cardToPlay1, destinationCard);

                                            // Send message
                                            gameState.sendMessage(_performingPlayerId + " targets to deploy " + deployTextBeginning + playCardText);

                                            // Update targeting information to support re-targeting
                                            _cardToPlay1.getBlueprint().updateTargetFiltersAfterTargetsChosen(subAction.getParentAction(), game, _cardToPlay1);
                                            _cardToPlay2.getBlueprint().updateTargetFiltersAfterTargetsChosen(subAction.getParentAction(), game, _cardToPlay2);
                                        }

                                        // Perform the respondable effect (which can be responded to cancel or re-target the cards being played)
                                        RespondableDeployAsReactEffect respondableDeployAsReactEffect =
                                                new RespondableDeployMultipleCardsSimultaneouslyEffect(subAction, subAction.getParentAction(), _cardToPlay1, _playedFromZone1, _playedFromStackedOn1, _cardToPlay2, _playedFromZone2, _playedFromStackedOn2, _asReact) {
                                                    @Override
                                                    protected void performActionResults(Action targetingAction) {
                                                        // If the card would be attached to a card, see if it was re-targeted to be attached to a different card
                                                        PhysicalCard attachTo = null;
                                                        if (_attachTo != null) {
                                                            if (_cardToPlay1.getBlueprint().isCardTypeMayNotBeCanceled()) {
                                                                attachTo = _attachTo;
                                                            }
                                                            else {
                                                                for (Map<PhysicalCard, Set<TargetingReason>> targetedCardMap : subAction.getParentAction().getAllPrimaryTargetCards().values()) {
                                                                    for (PhysicalCard targetedCard : targetedCardMap.keySet()) {
                                                                        if (targetedCardMap.get(targetedCard).contains(TargetingReason.TO_BE_DEPLOYED_ON)) {
                                                                            attachTo = targetedCard;
                                                                            break;
                                                                        }
                                                                    }
                                                                    if (attachTo != null) {
                                                                        break;
                                                                    }
                                                                }
                                                                if (attachTo == null)
                                                                    throw new UnsupportedOperationException(GameUtils.getFullName(_cardToPlay1) + " did not target a card to be attached to");
                                                            }
                                                        }

                                                        // Before actually putting card in play check if the site the card is being played to did not have presence or Force icons for player,
                                                        // or if site was controlled by opponent.
                                                        boolean playedToSiteWithoutPresenceOrForceIcons = false;
                                                        boolean playedToSiteControlledByOpponent = false;
                                                        PhysicalCard locationCardIsAt = (attachTo != null) ? modifiersQuerying.getLocationThatCardIsAt(gameState, attachTo) : _playedToLocation;
                                                        if (locationCardIsAt != null && Filters.site.accepts(gameState, modifiersQuerying, locationCardIsAt)) {
                                                            Icon icon = (game.getSide(_performingPlayerId) == Side.DARK) ? Icon.DARK_FORCE : Icon.LIGHT_FORCE;
                                                            if (!modifiersQuerying.hasIcon(gameState, locationCardIsAt, icon)
                                                                    && !modifiersQuerying.hasPresenceAt(gameState, _performingPlayerId, locationCardIsAt, false, null, null)) {
                                                                playedToSiteWithoutPresenceOrForceIcons = true;
                                                            }
                                                            playedToSiteControlledByOpponent = Filters.controls(game.getOpponent(_performingPlayerId)).accepts(game, locationCardIsAt);
                                                        }

                                                        // Remove cards from void
                                                        gameState.removeCardsFromZone(Collections.singleton(_cardToPlay1));
                                                        gameState.removeCardsFromZone(Collections.singleton(_cardToPlay2));

                                                        final StringBuilder playCardText = new StringBuilder();

                                                        if (attachTo != null) {

                                                            if (_attachAsPilot1) {
                                                                gameState.attachCardInPilotCapacitySlot(_cardToPlay1, _attachTo);
                                                                gameState.attachCard(_cardToPlay2, _cardToPlay1);

                                                                if (Filters.transport_vehicle.accepts(gameState, modifiersQuerying, attachTo))
                                                                    playCardText.append(" on ").append(GameUtils.getCardLink(attachTo)).append(" with ").append(GameUtils.getCardLink(_cardToPlay1)).append(" as driver");
                                                                else
                                                                    playCardText.append(" on ").append(GameUtils.getCardLink(attachTo)).append(" with ").append(GameUtils.getCardLink(_cardToPlay1)).append(" as pilot");
                                                            } else if (_attachAsPassenger1) {
                                                                gameState.attachCardInPassengerCapacitySlot(_cardToPlay1, attachTo);
                                                                gameState.attachCard(_cardToPlay2, _cardToPlay1);

                                                                playCardText.append(" on ").append(GameUtils.getCardLink(attachTo)).append(" with ").append(GameUtils.getCardLink(_cardToPlay1)).append(" as passenger");
                                                            } else if (Filters.starship.accepts(gameState, modifiersQuerying, attachTo)
                                                                    && !Filters.or(Filters.Effect_of_any_Kind, Filters.starship_weapon).accepts(gameState, modifiersQuerying, _cardToPlay1)) {
                                                                if (_deployInVehicleSlot)
                                                                    gameState.attachCardInVehicleCapacitySlot(_cardToPlay1, attachTo);
                                                                else if (Filters.capital_starship.accepts(gameState, modifiersQuerying, _cardToPlay1))
                                                                    gameState.attachCardInCapitalStarshipCapacitySlot(_cardToPlay1, _attachTo);
                                                                else
                                                                    gameState.attachCardInStarfighterOrTIECapacitySlot(_cardToPlay1, attachTo);

                                                                if (_attachAsPilot2)
                                                                    gameState.attachCardInPilotCapacitySlot(_cardToPlay2, _cardToPlay1);
                                                                else if (_attachAsPassenger2)
                                                                    gameState.attachCardInPassengerCapacitySlot(_cardToPlay2, _cardToPlay1);
                                                                else
                                                                    gameState.attachCard(_cardToPlay2, _cardToPlay1);

                                                                playCardText.append(" into cargo hold of ").append(GameUtils.getCardLink(attachTo));
                                                            } else {
                                                                gameState.attachCard(_cardToPlay1, attachTo);
                                                                if (_attachAsPilot2)
                                                                    gameState.attachCardInPilotCapacitySlot(_cardToPlay2, _cardToPlay1);
                                                                else if (_attachAsPassenger2)
                                                                    gameState.attachCardInPassengerCapacitySlot(_cardToPlay2, _cardToPlay1);
                                                                else
                                                                    gameState.attachCard(_cardToPlay2, _cardToPlay1);

                                                                playCardText.append(" on ").append(GameUtils.getCardLink(attachTo));
                                                            }
                                                        }
                                                        // Played to location.
                                                        else if (_playedToLocation != null) {

                                                            gameState.playCardToLocation(_cardToPlay1, _playedToLocation, _playedToOpponentsZone ? game.getOpponent(_cardToPlay1.getOwner()) : _cardToPlay1.getOwner());
                                                            if (_attachAsPilot2)
                                                                gameState.attachCardInPilotCapacitySlot(_cardToPlay2, _cardToPlay1);
                                                            else if (_attachAsPassenger2)
                                                                gameState.attachCardInPassengerCapacitySlot(_cardToPlay2, _cardToPlay1);
                                                            else
                                                                gameState.attachCard(_cardToPlay2, _cardToPlay1);

                                                            if (_playedToOpponentsZone && Filters.character.accepts(gameState, modifiersQuerying, _cardToPlay1)) {
                                                                _cardToPlay1.setUndercover(true);
                                                                playCardText.append(" to ").append(GameUtils.getCardLink(_playedToLocation)).append(" with ").append(GameUtils.getCardLink(_cardToPlay1)).append(" as an Undercover spy");
                                                            } else {
                                                                playCardText.append(" to ").append(GameUtils.getCardLink(_playedToLocation));
                                                            }
                                                        }
                                                        else {
                                                            throw new UnsupportedOperationException(GameUtils.getFullName(_cardToPlay1) + " and " + GameUtils.getFullName(_cardToPlay2) + " must play as attached or to location");
                                                        }

                                                        _cardsWerePlayed = true;

                                                        // Record the cards just deployed
                                                        modifiersQuerying.cardJustDeployed(_cardToPlay1);
                                                        modifiersQuerying.cardJustDeployed(_cardToPlay2);

                                                        // Send message
                                                        gameState.sendMessage(_performingPlayerId + " deploys " + deployTextBeginning + playCardText);

                                                        // Update targeting information to support re-targeting
                                                        _cardToPlay1.getBlueprint().updateTargetFiltersAfterOnTable(game, _cardToPlay1);
                                                        _cardToPlay2.getBlueprint().updateTargetFiltersAfterOnTable(game, _cardToPlay2);

                                                        // Emit the effect results
                                                        PlayCardResult playCardResult = new PlayCardResult(_performingPlayerId, _cardToPlay1, _playedFromZone1, attachTo, _playedToLocation, locationCardIsAt, true, _asReact);
                                                        PlayCardResult playCardResult2 = new PlayCardResult(_performingPlayerId, _cardToPlay2, _playedFromZone2, _cardToPlay1, null, locationCardIsAt, true, _asReact);
                                                        playCardResult.setOtherPlayedCard(_cardToPlay2);
                                                        playCardResult2.setOtherPlayedCard(_cardToPlay1);
                                                        playCardResult.setPlayedToSiteWithoutPresenceOrForceIcons(playedToSiteWithoutPresenceOrForceIcons);
                                                        playCardResult2.setPlayedToSiteWithoutPresenceOrForceIcons(playedToSiteWithoutPresenceOrForceIcons);
                                                        playCardResult.setPlayedToSiteControlledByOpponent(playedToSiteControlledByOpponent);
                                                        playCardResult2.setPlayedToSiteControlledByOpponent(playedToSiteControlledByOpponent);
                                                        actionsEnvironment.emitEffectResult(playCardResult);
                                                        actionsEnvironment.emitEffectResult(playCardResult2);

                                                        if (_asReact) {
                                                            // End deploy as 'react' state
                                                            gameState.finishDeployAsReact();
                                                        }
                                                    }
                                                };
                                        if (_asReact) {
                                            // Start deploy as 'react' state
                                            gameState.beginDeployAsReact(respondableDeployAsReactEffect);
                                        }
                                        subAction.appendEffect(respondableDeployAsReactEffect);
                                    }
                                }
                        );
                    }
                }
        );
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _cardsWerePlayed;
    }
}
