package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;
import com.gempukku.swccgo.logic.timing.results.RemovedFromCardPileResult;

import java.util.*;

/**
 * An effect to play a card.
 */
public class DeploySingleCardEffect extends AbstractSubActionEffect implements PlayCardEffect {
    private String _performingPlayerId;
    private PhysicalCard _cardToPlay;
    private Zone _playedFromZone;
    private String _playedFromZoneOwner;
    private PhysicalCard _playedFromStackedOn;
    private Zone _playedToZone;
    private boolean _playedToOpponentsZone;
    private PhysicalCard _attachTo;
    private boolean _attachAsPilot;
    private boolean _attachAsPassenger;
    private PhysicalCard _playedToLocation;
    private DeployAsCaptiveOption _deployAsCaptiveOption;
    private boolean _asReact;
    private PlayCardOptionId _playCardOptionId;
    private boolean _deployInVehicleSlot;
    private boolean _reshuffle;
    private boolean _cardWasPlayed;

    /**
     * Creates an effect that plays a card to the specified zone.
     * @param action the action performing this effect
     * @param cardToPlay the card to play
     * @param playedToZone the zone to play the card to
     * @param playedToOpponentsZone true if card is played to zone owned by opponent
     * @param playCardOptionId the play card option id
     * @param reshuffle true if pile the card is played from is reshuffled, otherwise false
     */
    public DeploySingleCardEffect(Action action, PhysicalCard cardToPlay, Zone playedToZone, boolean playedToOpponentsZone, PlayCardOptionId playCardOptionId, boolean reshuffle) {
        super(action);
        _performingPlayerId = _action.getPerformingPlayer();
        _cardToPlay = cardToPlay;
        _playedFromZone = GameUtils.getZoneFromZoneTop(cardToPlay.getZone());
        _playedFromZoneOwner = cardToPlay.getZoneOwner();
        if (_playedFromZone == Zone.STACKED) {
            _playedFromStackedOn = cardToPlay.getStackedOn();
        }
        _playedToZone = playedToZone;
        _playedToOpponentsZone = playedToOpponentsZone;
        _deployAsCaptiveOption = null;
        _playCardOptionId = playCardOptionId;
        _reshuffle = reshuffle;
    }

    /**
     * Creates an effect to play a card as attached to the specified card.
     * @param action the action performing this effect
     * @param cardToPlay the card to play
     * @param deployInVehicleCapacitySlot true if deploying into vehicle capacity slot of cargo bay, otherwise false
     * @param attachTo the card to attach the played card to
     * @param deployAsCaptiveOption specifies the way to deploy the card as a captive, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @param playCardOptionId the play card option id
     * @param reshuffle true if pile the card is played from is reshuffled, otherwise false
     */
    public DeploySingleCardEffect(Action action, PhysicalCard cardToPlay, boolean deployInVehicleCapacitySlot, PhysicalCard attachTo, DeployAsCaptiveOption deployAsCaptiveOption, ReactActionOption reactActionOption, PlayCardOptionId playCardOptionId, boolean reshuffle) {
        super(action);
        _performingPlayerId = _action.getPerformingPlayer();
        _cardToPlay = cardToPlay;
        _playedFromZone = GameUtils.getZoneFromZoneTop(cardToPlay.getZone());
        _playedFromZoneOwner = cardToPlay.getZoneOwner();
        if (_playedFromZone == Zone.STACKED) {
            _playedFromStackedOn = cardToPlay.getStackedOn();
        }
        _playedToZone = Zone.ATTACHED;
        _deployInVehicleSlot = deployInVehicleCapacitySlot;
        _attachTo = attachTo;
        _deployAsCaptiveOption = deployAsCaptiveOption;
        _asReact = reactActionOption != null;
        _playCardOptionId = playCardOptionId;
        _reshuffle = reshuffle;
    }

    /**
     * Creates an effect to play a (character) card as attached to the specified card.
     * @param action the action performing this effect
     * @param cardToPlay the card to play
     * @param attachTo the card to attach the played card to
     * @param attachAsPilot true if the card is played as a pilot, otherwise false
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @param playCardOptionId the play card option id
     * @param reshuffle true if pile the card is played from is reshuffled, otherwise false
     */
    public DeploySingleCardEffect(Action action, PhysicalCard cardToPlay, PhysicalCard attachTo, boolean attachAsPilot, ReactActionOption reactActionOption, PlayCardOptionId playCardOptionId, boolean reshuffle) {
        super(action);
        _performingPlayerId = _action.getPerformingPlayer();
        _cardToPlay = cardToPlay;
        _playedFromZone = GameUtils.getZoneFromZoneTop(cardToPlay.getZone());
        _playedFromZoneOwner = cardToPlay.getZoneOwner();
        if (_playedFromZone == Zone.STACKED) {
            _playedFromStackedOn = cardToPlay.getStackedOn();
        }
        _playedToZone = Zone.ATTACHED;
        _attachTo = attachTo;
        if (cardToPlay.getBlueprint().getCardCategory() != CardCategory.CHARACTER) {
            throw new UnsupportedOperationException(GameUtils.getFullName(cardToPlay) + " should not specify if being played as pilot");
        }
        _attachAsPilot = attachAsPilot;
        _attachAsPassenger = !attachAsPilot;
        _deployAsCaptiveOption = null;
        _asReact = reactActionOption != null;
        _playCardOptionId = playCardOptionId;
        _reshuffle = reshuffle;
    }

    /**
     * Creates an effect to play a card to the specified location.
     * @param action the action performing this effect
     * @param cardToPlay the card to play
     * @param playedToLocation the location the card is played to
     * @param playedToOpponentsZone true if card is played to zone owned by opponent
     * @param deployAsCaptiveOption specifies the way to deploy the card as a captive, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @param playCardOptionId the play card option id
     * @param reshuffle true if pile the card is played from is reshuffled, otherwise false
     */
    public DeploySingleCardEffect(Action action, PhysicalCard cardToPlay, PhysicalCard playedToLocation, boolean playedToOpponentsZone, DeployAsCaptiveOption deployAsCaptiveOption, ReactActionOption reactActionOption, PlayCardOptionId playCardOptionId, boolean reshuffle) {
        super(action);
        _performingPlayerId = _action.getPerformingPlayer();
        _cardToPlay = cardToPlay;
        _playedFromZone = GameUtils.getZoneFromZoneTop(cardToPlay.getZone());
        _playedFromZoneOwner = cardToPlay.getZoneOwner();
        if (_playedFromZone == Zone.STACKED) {
            _playedFromStackedOn = cardToPlay.getStackedOn();
        }
        _playedToZone = Zone.AT_LOCATION;
        _playedToLocation = playedToLocation;
        _playedToOpponentsZone = playedToOpponentsZone;
        _deployAsCaptiveOption = deployAsCaptiveOption;
        _asReact = reactActionOption != null;
        _playCardOptionId = playCardOptionId;
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
                new RecordCardsBeingPlayedEffect(subAction, Collections.singleton(_cardToPlay)));
        // Put card being played in the "void", and emit effect result to responses that can cancel it, or re-target it.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(final SwccgGame game) {
                        final String asReactText = _asReact ? " as a 'react'" : "";
                        final String fromText = _playedFromStackedOn != null ? GameUtils.getCardLink(_playedFromStackedOn) : _playedFromZone.getHumanReadable();
                        final String playedToZoneOwner = _playedToOpponentsZone ? game.getOpponent(_cardToPlay.getOwner()) : _cardToPlay.getOwner();

                        // Remove the card from where it is being played from and add to the void
                        gameState.removeCardsFromZone(Collections.singleton(_cardToPlay));
                        gameState.addCardToZone(_cardToPlay, Zone.VOID, _cardToPlay.getOwner());

                        // If the card is being played as a Dejarik, mark it so now, so Sense & Alter are unable to cancel as being played.
                        _cardToPlay.setDejarikHologramAtHolosite(_playCardOptionId == PlayCardOptionId.PLAY_AS_DEJARIK);

                        // Shuffle the card pile
                        if (_playedFromZone.isCardPile()) {
                            if (_reshuffle) {
                                subAction.appendEffect(
                                        new ShufflePileEffect(subAction, null, _performingPlayerId, _playedFromZoneOwner, _playedFromZone, true));
                            }
                            else {
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
                                        for (String title : _cardToPlay.getTitles()) {
                                            modifiersQuerying.getUntilEndOfGameLimitCounter(title, GameTextActionId.DEPLOY_CARD_ACTION).incrementToLimit(Integer.MAX_VALUE, 1);
                                        }

                                        // If the card being played is being played as a 'react', or the card is a card type that can be canceled
                                        final boolean respondable = _asReact || !_cardToPlay.getBlueprint().isCardTypeMayNotBeCanceled();

                                        if (respondable) {
                                            final StringBuilder playCardText = new StringBuilder();
                                            PhysicalCard destinationCard = null;

                                            // Played as attached.
                                            if (_attachTo != null) {
                                                destinationCard = _attachTo;

                                                if (_deployAsCaptiveOption != null && _deployAsCaptiveOption.getCaptureOption() == CaptureOption.SEIZE) {
                                                    playCardText.append(GameUtils.getCardLink(_cardToPlay)).append(asReactText).append(" from ").append(fromText).append(" to ").append(GameUtils.getCardLink(destinationCard)).append(" as an escorted captive");
                                                }
                                                else if (_attachAsPilot) {
                                                    if (Filters.transport_vehicle.accepts(gameState, modifiersQuerying, destinationCard))
                                                        playCardText.append(GameUtils.getCardLink(_cardToPlay)).append(asReactText).append(" from ").append(fromText).append(" on ").append(GameUtils.getCardLink(destinationCard)).append(" as driver");
                                                    else
                                                        playCardText.append(GameUtils.getCardLink(_cardToPlay)).append(asReactText).append(" from ").append(fromText).append(" on ").append(GameUtils.getCardLink(destinationCard)).append(" as pilot");
                                                }
                                                else if (_attachAsPassenger) {
                                                    playCardText.append(GameUtils.getCardLink(_cardToPlay)).append(asReactText).append(" from ").append(fromText).append(" on ").append(GameUtils.getCardLink(destinationCard)).append(" as passenger");
                                                }
                                                else if (Filters.starship.accepts(gameState, modifiersQuerying, destinationCard)
                                                        && !Filters.or(Filters.creature, Filters.Epic_Event, Filters.Effect_of_any_Kind, Filters.starship_weapon, Filters.device).accepts(gameState, modifiersQuerying, _cardToPlay)) {
                                                    playCardText.append(GameUtils.getCardLink(_cardToPlay)).append(asReactText).append(" from ").append(fromText).append(" into cargo hold of ").append(GameUtils.getCardLink(destinationCard));
                                                }
                                                else {
                                                    playCardText.append(GameUtils.getCardLink(_cardToPlay)).append(asReactText).append(" from ").append(fromText).append(" on ").append(GameUtils.getCardLink(destinationCard));
                                                }
                                            }
                                            // Played to location.
                                            else if (_playedToLocation != null) {
                                                destinationCard = _playedToLocation;

                                                if (_deployAsCaptiveOption != null && _deployAsCaptiveOption.getCaptureOption() == CaptureOption.IMPRISONMENT) {
                                                    if (_deployAsCaptiveOption.isFrozenCaptive()) {
                                                        playCardText.append(GameUtils.getCardLink(_cardToPlay)).append(asReactText).append(" from ").append(fromText).append(" into ").append(GameUtils.getCardLink(destinationCard)).append(" as an 'imprisoned' and 'frozen' captive");
                                                    }
                                                    else {
                                                        playCardText.append(GameUtils.getCardLink(_cardToPlay)).append(asReactText).append(" from ").append(fromText).append(" into ").append(GameUtils.getCardLink(destinationCard)).append(" as an 'imprisoned' captive");
                                                    }
                                                }
                                                else if (_deployAsCaptiveOption != null && _deployAsCaptiveOption.getCaptureOption() == CaptureOption.LEAVE_UNATTENDED) {
                                                    playCardText.append(GameUtils.getCardLink(_cardToPlay)).append(asReactText).append(" from ").append(fromText).append(" to ").append(GameUtils.getCardLink(destinationCard)).append(" as an unattended 'frozen' captive");
                                                }
                                                else if (_playedToOpponentsZone && Filters.character.accepts(gameState, modifiersQuerying, _cardToPlay)) {
                                                    playCardText.append(GameUtils.getCardLink(_cardToPlay)).append(asReactText).append(" from ").append(fromText).append(" to ").append(GameUtils.getCardLink(destinationCard)).append(" as an Undercover spy");
                                                }
                                                else {
                                                    playCardText.append(GameUtils.getCardLink(_cardToPlay)).append(asReactText).append(" from ").append(fromText).append(" to ").append(GameUtils.getCardLink(destinationCard));
                                                }
                                            }
                                            // Played as 'insert' card.
                                            else if (_playCardOptionId == PlayCardOptionId.PLAY_AS_INSERT_CARD) {
                                                playCardText.append(GameUtils.getCardLink(_cardToPlay)).append(" from ").append(fromText).append(" into ").append(playedToZoneOwner).append("'s ").append(_playedToZone.getHumanReadable());
                                            }
                                            // Played to other zone.
                                            else {
                                                playCardText.append(GameUtils.getCardLink(_cardToPlay)).append(" from ").append(fromText);
                                            }

                                            // Get other cards targeted
                                            Collection<PhysicalCard> otherTargetedCards = new HashSet<PhysicalCard>();
                                            for (Map<PhysicalCard, Set<TargetingReason>> targetedCardMap : subAction.getParentAction().getAllPrimaryTargetCards().values()) {
                                                otherTargetedCards.addAll(targetedCardMap.keySet());
                                            }
                                            if (destinationCard != null) {
                                                otherTargetedCards = Filters.filter(otherTargetedCards, game, Filters.not(Filters.sameCardId(destinationCard)));
                                            }
                                            if (!otherTargetedCards.isEmpty()) {
                                                playCardText.append(" targeting ").append(GameUtils.getAppendedNames(otherTargetedCards));
                                            }

                                            // Show animations
                                            gameState.showCardOnScreen(_cardToPlay);
                                            if (destinationCard != null) {
                                                gameState.cardAffectsCard(_performingPlayerId, _cardToPlay, destinationCard);
                                            }

                                            if (!otherTargetedCards.isEmpty()) {
                                                gameState.cardAffectsCards(_performingPlayerId, _cardToPlay, otherTargetedCards);
                                            }

                                            // Send message
                                            StringBuilder aboutToDeployText = new StringBuilder(_performingPlayerId).append(" targets to");

                                            if (_playCardOptionId == PlayCardOptionId.PLAY_AS_INSERT_CARD)
                                                aboutToDeployText.append(" 'insert' ");
                                            else if (_playCardOptionId == PlayCardOptionId.PLAY_AS_DEJARIK || _cardToPlay.getBlueprint().isCardTypeDeployed())
                                                aboutToDeployText.append(" deploy ");
                                            else
                                                aboutToDeployText.append(" play ");

                                            gameState.sendMessage(aboutToDeployText.toString() + playCardText);

                                            // Update targeting information to support re-targeting
                                            _cardToPlay.getBlueprint().updateTargetFiltersAfterTargetsChosen(subAction.getParentAction(), game, _cardToPlay);
                                        }

                                        // Perform the respondable effect (which can be responded to cancel or re-target the card being played)
                                        RespondableDeploySingleCardEffect respondableDeploySingleCardEffect =
                                                new RespondableDeploySingleCardEffect(subAction, subAction.getParentAction(), _cardToPlay, _playedFromZone, _playedFromStackedOn, _asReact, _playCardOptionId == PlayCardOptionId.PLAY_AS_INSERT_CARD) {
                                                    @Override
                                                    protected void performActionResults(Action targetingAction) {
                                                        // If the card would be attached to a card, see if it was re-targeted to be attached to a different card
                                                        PhysicalCard attachTo = null;
                                                        if (_attachTo != null) {
                                                            if (_cardToPlay.getBlueprint().isCardTypeMayNotBeCanceled()) {
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
                                                                    throw new UnsupportedOperationException(GameUtils.getFullName(_cardToPlay) + " did not target a card to be attached to");
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

                                                        // Remove card from void
                                                        gameState.removeCardsFromZone(Collections.singleton(_cardToPlay));

                                                        final StringBuilder playCardText = new StringBuilder();
                                                        PhysicalCard destinationCard = null;

                                                        // Played as attached.
                                                        if (attachTo != null) {
                                                            destinationCard = attachTo;

                                                            if (_deployAsCaptiveOption != null && _deployAsCaptiveOption.getCaptureOption() == CaptureOption.SEIZE) {
                                                                _cardToPlay.setCaptive(true);
                                                                gameState.attachCard(_cardToPlay, attachTo);
                                                                playCardText.append(GameUtils.getCardLink(_cardToPlay)).append(asReactText).append(" from ").append(fromText).append(" to ").append(GameUtils.getCardLink(attachTo)).append(" as an escorted captive");
                                                            }
                                                            else if (_attachAsPilot) {
                                                                gameState.attachCardInPilotCapacitySlot(_cardToPlay, attachTo);

                                                                if (Filters.transport_vehicle.accepts(gameState, modifiersQuerying, attachTo))
                                                                    playCardText.append(GameUtils.getCardLink(_cardToPlay)).append(asReactText).append(" from ").append(fromText).append(" on ").append(GameUtils.getCardLink(attachTo)).append(" as driver");
                                                                else
                                                                    playCardText.append(GameUtils.getCardLink(_cardToPlay)).append(asReactText).append(" from ").append(fromText).append(" on ").append(GameUtils.getCardLink(attachTo)).append(" as pilot");
                                                            }
                                                            else if (_attachAsPassenger) {
                                                                gameState.attachCardInPassengerCapacitySlot(_cardToPlay, attachTo);
                                                                playCardText.append(GameUtils.getCardLink(_cardToPlay)).append(asReactText).append(" from ").append(fromText).append(" on ").append(GameUtils.getCardLink(attachTo)).append(" as passenger");
                                                            }
                                                            else if (Filters.starship.accepts(gameState, modifiersQuerying, attachTo)
                                                                    && !Filters.or(Filters.creature, Filters.Epic_Event, Filters.Effect_of_any_Kind, Filters.starship_weapon, Filters.device).accepts(gameState, modifiersQuerying, _cardToPlay)) {
                                                                if (_deployInVehicleSlot)
                                                                    gameState.attachCardInVehicleCapacitySlot(_cardToPlay, attachTo);
                                                                else if (Filters.capital_starship.accepts(gameState, modifiersQuerying, _cardToPlay))
                                                                    gameState.attachCardInCapitalStarshipCapacitySlot(_cardToPlay, attachTo);
                                                                else
                                                                    gameState.attachCardInStarfighterOrTIECapacitySlot(_cardToPlay, attachTo);

                                                                playCardText.append(GameUtils.getCardLink(_cardToPlay)).append(asReactText).append(" from ").append(fromText).append(" into cargo hold of ").append(GameUtils.getCardLink(attachTo));
                                                            }
                                                            else {
                                                                gameState.attachCard(_cardToPlay, attachTo);
                                                                playCardText.append(GameUtils.getCardLink(_cardToPlay)).append(asReactText).append(" from ").append(fromText).append(" on ").append(GameUtils.getCardLink(attachTo));
                                                            }
                                                        }
                                                        // Played to location.
                                                        else if (_playedToLocation != null) {
                                                            destinationCard = _playedToLocation;
                                                            _cardToPlay.setDejarikHologramAtHolosite(_playCardOptionId == PlayCardOptionId.PLAY_AS_DEJARIK);

                                                            if (_deployAsCaptiveOption != null && _deployAsCaptiveOption.getCaptureOption() == CaptureOption.IMPRISONMENT) {
                                                                _cardToPlay.setCaptive(true);
                                                                _cardToPlay.setImprisoned(true);
                                                                _cardToPlay.setFrozen(_deployAsCaptiveOption.isFrozenCaptive());
                                                                gameState.attachCard(_cardToPlay, destinationCard);
                                                                playCardText.append(GameUtils.getCardLink(_cardToPlay)).append(asReactText).append(" from ").append(fromText).append(" into ").append(GameUtils.getCardLink(destinationCard));
                                                                if (_deployAsCaptiveOption.isFrozenCaptive()) {
                                                                    playCardText.append(" as an 'imprisoned' and 'frozen' captive");
                                                                }
                                                                else {
                                                                    playCardText.append(" as an 'imprisoned' captive");
                                                                }
                                                            }
                                                            else if (_deployAsCaptiveOption != null && _deployAsCaptiveOption.getCaptureOption() == CaptureOption.LEAVE_UNATTENDED) {
                                                                _cardToPlay.setCaptive(true);
                                                                _cardToPlay.setFrozen(true);
                                                                gameState.playCardToLocation(_cardToPlay, destinationCard, game.getDarkPlayer());
                                                                playCardText.append(GameUtils.getCardLink(_cardToPlay)).append(asReactText).append(" from ").append(fromText).append(" to ").append(GameUtils.getCardLink(destinationCard)).append(" as an unattended 'frozen' captive");
                                                            }
                                                            else if (_playedToOpponentsZone && Filters.character.accepts(gameState, modifiersQuerying, _cardToPlay)) {
                                                                _cardToPlay.setUndercover(true);
                                                                gameState.playCardToLocation(_cardToPlay, destinationCard, playedToZoneOwner);
                                                                playCardText.append(GameUtils.getCardLink(_cardToPlay)).append(asReactText).append(" from ").append(fromText).append(" to ").append(GameUtils.getCardLink(destinationCard)).append(" as an Undercover spy");
                                                            }
                                                            else {
                                                                gameState.playCardToLocation(_cardToPlay, destinationCard, playedToZoneOwner);
                                                                playCardText.append(GameUtils.getCardLink(_cardToPlay)).append(asReactText).append(" from ").append(fromText).append(" to ").append(GameUtils.getCardLink(destinationCard));
                                                            }
                                                        }
                                                        // Played as 'insert' card.
                                                        else if (_playCardOptionId == PlayCardOptionId.PLAY_AS_INSERT_CARD) {
                                                            _cardToPlay.setInserted(true);
                                                            gameState.addCardToZone(_cardToPlay, _playedToZone, playedToZoneOwner);
                                                            gameState.cardAffectsCard(_performingPlayerId, _cardToPlay, gameState.getTopOfCardPile(playedToZoneOwner, _playedToZone));
                                                            gameState.shufflePile(playedToZoneOwner, _playedToZone);
                                                            playCardText.append(GameUtils.getCardLink(_cardToPlay)).append(" from ").append(fromText).append(" into ").append(playedToZoneOwner).append("'s ").append(_playedToZone.getHumanReadable()).append(" and shuffles ").append(playedToZoneOwner).append("'s ").append(_playedToZone.getHumanReadable());
                                                        }
                                                        // Played to other zone.
                                                        else {
                                                            gameState.addCardToZone(_cardToPlay, _playedToZone, playedToZoneOwner);
                                                            playCardText.append(GameUtils.getCardLink(_cardToPlay)).append(" from ").append(fromText);
                                                        }

                                                        _cardToPlay.setPlayCardOptionId(_playCardOptionId);
                                                        _cardWasPlayed = true;

                                                        // Record the card just deployed
                                                        modifiersQuerying.cardJustDeployed(_cardToPlay);

                                                        // Get other cards targeted
                                                        Collection<PhysicalCard> otherTargetedCards = new HashSet<PhysicalCard>();
                                                        for (Map<PhysicalCard, Set<TargetingReason>> targetedCardMap : subAction.getParentAction().getAllPrimaryTargetCards().values()) {
                                                            otherTargetedCards.addAll(targetedCardMap.keySet());
                                                        }
                                                        if (destinationCard != null) {
                                                            otherTargetedCards = Filters.filter(otherTargetedCards, game, Filters.not(Filters.sameCardId(destinationCard)));
                                                        }
                                                        if (!otherTargetedCards.isEmpty()) {
                                                            playCardText.append(" targeting ").append(GameUtils.getAppendedNames(otherTargetedCards));
                                                        }

                                                        // Show animations if any cards targeted
                                                        if (!otherTargetedCards.isEmpty()) {
                                                            gameState.cardAffectsCards(_performingPlayerId, _cardToPlay, otherTargetedCards);
                                                        }

                                                        // Send message
                                                        boolean isDeploy = true;
                                                        StringBuilder deployedText = new StringBuilder(_performingPlayerId);

                                                        if (_playCardOptionId == PlayCardOptionId.PLAY_AS_INSERT_CARD) {
                                                            deployedText.append(" 'inserts' ");
                                                        } else if (_playCardOptionId == PlayCardOptionId.PLAY_AS_DEJARIK || _cardToPlay.getBlueprint().isCardTypeDeployed()) {
                                                            deployedText.append(" deploys ");
                                                        } else {
                                                            deployedText.append(" plays ");
                                                            isDeploy = false;
                                                        }

                                                        gameState.sendMessage(deployedText.toString() + playCardText);

                                                        // Update targeting information to support re-targeting
                                                        _cardToPlay.getBlueprint().updateTargetFiltersAfterOnTable(game, _cardToPlay);

                                                        // Emit the effect result
                                                        PlayCardResult playCardResult = new PlayCardResult(_performingPlayerId, _cardToPlay, _playedFromZone, attachTo, _playedToLocation, locationCardIsAt, isDeploy, _asReact);
                                                        playCardResult.setPlayedToSiteWithoutPresenceOrForceIcons(playedToSiteWithoutPresenceOrForceIcons);
                                                        playCardResult.setPlayedToSiteControlledByOpponent(playedToSiteControlledByOpponent);
                                                        actionsEnvironment.emitEffectResult(playCardResult);

                                                        if (_asReact) {
                                                            // End deploy as 'react' state
                                                            gameState.finishDeployAsReact();
                                                        }
                                                    }
                                                };
                                        if (_asReact) {
                                            // Start deploy as 'react' state
                                            gameState.beginDeployAsReact(respondableDeploySingleCardEffect);
                                        }
                                        subAction.appendEffect(respondableDeploySingleCardEffect);
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
        return _cardWasPlayed;
    }
}
