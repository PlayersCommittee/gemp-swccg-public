package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsOnTableEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.SquadronReplacementResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * The effect to replace cards with another card.
 */
public class ReplacementEffect extends AbstractSubActionEffect {
    private PhysicalCard _location;
    private Collection<PhysicalCard> _cardsToReplace;
    private PhysicalCard _card;

    /**
     * Create an effect to replace cards with another card.
     * @param action the action performing this effect
     * @param location the location where the starfighers are present
     * @param cardsToReplace the cards to be replaced
     * @param card the card
     */
    public ReplacementEffect(Action action, PhysicalCard location, Collection<PhysicalCard> cardsToReplace, PhysicalCard card) {
        super(action);
        _location = location;
        _cardsToReplace = cardsToReplace;
        _card = card;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final String performingPlayerId = _action.getPerformingPlayer();

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Place the squadron at the location
                        gameState.removeCardFromZone(_card);
                        gameState.playCardToLocation(_card, _location, _card.getOwner());
                        gameState.sendMessage(performingPlayerId + " replaces " + GameUtils.getAppendedNames(_cardsToReplace) + " with " + GameUtils.getCardLink(_card));

                        // Check if the player wants to transfer any cards from the replaced cards to the squadron
                        checkTransferCardsToNewCard(subAction, performingPlayerId, game);

                        subAction.appendEffect(
                                new PassthruEffect(subAction) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        // Get the card IDs of the replaced cards and determine cards to place in Used Pile
                                        List<Integer> prevCardIds = new ArrayList<Integer>();
                                        List<PhysicalCard> cardsToPlaceInUsedPile = new ArrayList<PhysicalCard>();
                                        for (PhysicalCard starfighter : _cardsToReplace) {
                                            prevCardIds.add(starfighter.getCardId());
                                            cardsToPlaceInUsedPile.add(starfighter);
                                            cardsToPlaceInUsedPile.addAll(gameState.getAttachedCards(starfighter, true));
                                            cardsToPlaceInUsedPile.addAll(gameState.getStackedCards(starfighter));
                                        }

                                        // Move cards to void (before placing in Used Pile)
                                        gameState.removeCardsFromZone(cardsToPlaceInUsedPile);
                                        for (PhysicalCard cardToPlaceInUsedPile : cardsToPlaceInUsedPile) {
                                            gameState.addCardToTopOfZone(cardToPlaceInUsedPile, Zone.VOID, cardToPlaceInUsedPile.getOwner());
                                            if (_cardsToReplace.contains(cardToPlaceInUsedPile)) {
                                                gameState.assignNewCardId(cardToPlaceInUsedPile);
                                            }
                                        }

                                        // Give squadron the card IDs of the replaced cards
                                        gameState.assignAdditionalCardIds(_card, prevCardIds);

                                        gameState.reapplyAffectingForCard(game, _card);
                                        for (PhysicalCard attachedCard : gameState.getAttachedCards(_card, true)) {
                                            gameState.reapplyAffectingForCard(game, attachedCard);
                                        }

                                        // Put cards in Used Pile
                                        subAction.appendEffect(
                                                new PutCardsInCardPileEffect(subAction, game, cardsToPlaceInUsedPile, Zone.USED_PILE));
                                        subAction.appendEffect(
                                                new TriggeringResultEffect(subAction, new SquadronReplacementResult(performingPlayerId, _card)));

                                    }
                                }
                        );
                    }
                }
        );
        return subAction;
    }

    /**
     * Checks if player wants to transfer cards from the card to the new card.
     * @param subAction the sub-action
     * @param playerId the player
     * @param game the game
     */
    private void checkTransferCardsToNewCard(final SubAction subAction, final String playerId, final SwccgGame game) {
        final GameState gameState = game.getGameState();

        List<PhysicalCard> validToTransfer = new ArrayList<PhysicalCard>();

        // Characters valid to transfer
        Collection<PhysicalCard> characters = Filters.filterActive(game, _card, Filters.and(Filters.your(_card), Filters.character, Filters.attachedTo(Filters.in(_cardsToReplace))));
        for (PhysicalCard character : characters) {

            // Determine if there is pilot/passenger capacity for the character
            if (Filters.and(Filters.or(Filters.hasAvailablePilotCapacity(character), Filters.hasAvailablePassengerCapacity(character)), Filters.notProhibitedFromTarget(character)).accepts(game, _card)) {
                validToTransfer.add(character);
            }
        }

        // Devices and weapons valid to transfer
        Collection<PhysicalCard> devicesAndWeapons = Filters.filterActive(game, _card, Filters.and(Filters.your(_card), Filters.or(Filters.device, Filters.weapon), Filters.attachedTo(Filters.in(_cardsToReplace))));
        for (PhysicalCard deviceOrWeapon : devicesAndWeapons) {

            // Determine if squadron is a valid transfer target
            if (deviceOrWeapon.getBlueprint().getValidTransferDeviceOrWeaponTargetFilter(playerId, game,
                    deviceOrWeapon, new PlayCardOption(deviceOrWeapon.getPlayCardOptionId(), PlayCardZoneOption.ATTACHED, null),
                    true, Filters.sameCardId(_card)).accepts(game, _card)) {
                validToTransfer.add(deviceOrWeapon);
            }
        }

        if (validToTransfer.isEmpty()) {
            return;
        }

        // Choose card to transfer to squadron
        subAction.insertEffect(
                new ChooseCardsOnTableEffect(subAction, playerId, "Choose cards to transfer to " + GameUtils.getCardLink(_card), 0, 1, validToTransfer) {
                    @Override
                    protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
                        if (selectedCards.isEmpty()) {
                            return;
                        }
                        final PhysicalCard cardToTransfer = selectedCards.iterator().next();
                        final PhysicalCard transferredFrom = cardToTransfer.getAttachedTo();

                        if (Filters.character.accepts(game, cardToTransfer)) {
                            // Need to determine capacity slot for character
                            boolean canBePilot = Filters.hasAvailablePilotCapacity(cardToTransfer).accepts(game, _card);
                            boolean canBePassenger = Filters.hasAvailablePassengerCapacity(cardToTransfer).accepts(game, _card);

                            if (canBePilot && canBePassenger) {
                                // Ask player to choose pilot/driver or passenger capacity slot
                                subAction.insertEffect(
                                        new PlayoutDecisionEffect(subAction, playerId,
                                                new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getCardLink(cardToTransfer) + " aboard " + GameUtils.getCardLink(_card), new String[]{"Pilot", "Passenger"}) {
                                                    @Override
                                                    protected void validDecisionMade(int index, String result) {
                                                        boolean transferAsPilot = (index == 0);
                                                        if (transferAsPilot) {
                                                            gameState.sendMessage(playerId + " transfers " + GameUtils.getCardLink(cardToTransfer) + " from " + GameUtils.getCardLink(transferredFrom) + " to " + GameUtils.getCardLink(_card) + " as pilot");
                                                            gameState.moveCardToAttachedInPilotCapacitySlot(cardToTransfer, _card);
                                                        } else {
                                                            gameState.sendMessage(playerId + " transfers " + GameUtils.getCardLink(cardToTransfer) + " from " + GameUtils.getCardLink(transferredFrom) + " to " + GameUtils.getCardLink(_card) + " as passenger");
                                                            gameState.moveCardToAttachedInPassengerCapacitySlot(cardToTransfer, _card);
                                                        }
                                                    }
                                                }
                                        )
                                );
                            } else {
                                if (canBePilot) {
                                    gameState.sendMessage(playerId + " transfers " + GameUtils.getCardLink(cardToTransfer) + " from " + GameUtils.getCardLink(transferredFrom) + " to " + GameUtils.getCardLink(_card) + " as pilot");
                                    gameState.moveCardToAttachedInPilotCapacitySlot(cardToTransfer, _card);
                                } else {
                                    gameState.sendMessage(playerId + " transfers " + GameUtils.getCardLink(cardToTransfer) + " from " + GameUtils.getCardLink(transferredFrom) + " to " + GameUtils.getCardLink(_card) + " as passenger");
                                    gameState.moveCardToAttachedInPassengerCapacitySlot(cardToTransfer, _card);
                                }
                            }
                        } else {
                            gameState.sendMessage(playerId + " transfers " + GameUtils.getCardLink(cardToTransfer) + " from " + GameUtils.getCardLink(transferredFrom) + " to " + GameUtils.getCardLink(_card));
                            gameState.moveCardToAttached(cardToTransfer, _card);
                        }
                    }
                },
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        checkTransferCardsToNewCard(subAction, playerId, game);
                    }
                }
        );
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
