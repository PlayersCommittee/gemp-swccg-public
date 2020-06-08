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
 * The effect to replace starfighters with a squadron.
 */
public class SquadronReplacementEffect extends AbstractSubActionEffect {
    private PhysicalCard _location;
    private Collection<PhysicalCard> _starfightersToReplace;
    private PhysicalCard _squadron;

    /**
     * Create an effect to replace starfighters with a squadron.
     * @param action the action performing this effect
     * @param location the location where the starfighers are present
     * @param starfightersToReplace the starfighters to be replaced
     * @param squadron the squadron
     */
    public SquadronReplacementEffect(Action action, PhysicalCard location, Collection<PhysicalCard> starfightersToReplace, PhysicalCard squadron) {
        super(action);
        _location = location;
        _starfightersToReplace = starfightersToReplace;
        _squadron = squadron;
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
                        gameState.removeCardFromZone(_squadron);
                        gameState.playCardToLocation(_squadron, _location, _squadron.getOwner());
                        gameState.sendMessage(performingPlayerId + " replaces " + GameUtils.getAppendedNames(_starfightersToReplace) + " with " + GameUtils.getCardLink(_squadron));

                        // Check if the player wants to transfer any cards from the replaced starfighters to the squadron
                        checkTransferCardsToSquadron(subAction, performingPlayerId, game);

                        subAction.appendEffect(
                                new PassthruEffect(subAction) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        // Get the card IDs of the replaced starfighters and determine cards to place in Used Pile
                                        List<Integer> prevCardIds = new ArrayList<Integer>();
                                        List<PhysicalCard> cardsToPlaceInUsedPile = new ArrayList<PhysicalCard>();
                                        for (PhysicalCard starfighter : _starfightersToReplace) {
                                            prevCardIds.add(starfighter.getCardId());
                                            cardsToPlaceInUsedPile.add(starfighter);
                                            cardsToPlaceInUsedPile.addAll(gameState.getAttachedCards(starfighter, true));
                                            cardsToPlaceInUsedPile.addAll(gameState.getStackedCards(starfighter));
                                        }

                                        // Move cards to void (before placing in Used Pile)
                                        gameState.removeCardsFromZone(cardsToPlaceInUsedPile);
                                        for (PhysicalCard cardToPlaceInUsedPile : cardsToPlaceInUsedPile) {
                                            gameState.addCardToTopOfZone(cardToPlaceInUsedPile, Zone.VOID, cardToPlaceInUsedPile.getOwner());
                                            if (_starfightersToReplace.contains(cardToPlaceInUsedPile)) {
                                                gameState.assignNewCardId(cardToPlaceInUsedPile);
                                            }
                                        }

                                        // Give squadron the card IDs of the replaced starfighters
                                        gameState.assignAdditionalCardIds(_squadron, prevCardIds);

                                        gameState.reapplyAffectingForCard(game, _squadron);
                                        for (PhysicalCard attachedCard : gameState.getAttachedCards(_squadron, true)) {
                                            gameState.reapplyAffectingForCard(game, attachedCard);
                                        }

                                        // Put cards in Used Pile
                                        subAction.appendEffect(
                                                new PutCardsInCardPileEffect(subAction, game, cardsToPlaceInUsedPile, Zone.USED_PILE));
                                        subAction.appendEffect(
                                                new TriggeringResultEffect(subAction, new SquadronReplacementResult(performingPlayerId, _squadron)));

                                    }
                                }
                        );
                    }
                }
        );
        return subAction;
    }

    /**
     * Checks if player wants to transfer cards from the starfighters to the squadron.
     * @param subAction the sub-action
     * @param playerId the player
     * @param game the game
     */
    private void checkTransferCardsToSquadron(final SubAction subAction, final String playerId, final SwccgGame game) {
        final GameState gameState = game.getGameState();

        List<PhysicalCard> validToTransfer = new ArrayList<PhysicalCard>();

        // Characters valid to transfer
        Collection<PhysicalCard> characters = Filters.filterActive(game, _squadron, Filters.and(Filters.your(_squadron), Filters.character, Filters.attachedTo(Filters.in(_starfightersToReplace))));
        for (PhysicalCard character : characters) {

            // Determine if there is pilot/passenger capacity for the character
            if (Filters.and(Filters.or(Filters.hasAvailablePilotCapacity(character), Filters.hasAvailablePassengerCapacity(character)), Filters.notProhibitedFromTarget(character)).accepts(game, _squadron)) {
                validToTransfer.add(character);
            }
        }

        // Devices and weapons valid to transfer
        Collection<PhysicalCard> devicesAndWeapons = Filters.filterActive(game, _squadron, Filters.and(Filters.your(_squadron), Filters.or(Filters.device, Filters.weapon), Filters.attachedTo(Filters.in(_starfightersToReplace))));
        for (PhysicalCard deviceOrWeapon : devicesAndWeapons) {

            // Determine if squadron is a valid transfer target
            if (deviceOrWeapon.getBlueprint().getValidTransferDeviceOrWeaponTargetFilter(playerId, game,
                    deviceOrWeapon, new PlayCardOption(deviceOrWeapon.getPlayCardOptionId(), PlayCardZoneOption.ATTACHED, null),
                    true, Filters.sameCardId(_squadron)).accepts(game, _squadron)) {
                validToTransfer.add(deviceOrWeapon);
            }
        }

        if (validToTransfer.isEmpty()) {
            return;
        }

        // Choose card to transfer to squadron
        subAction.insertEffect(
                new ChooseCardsOnTableEffect(subAction, playerId, "Choose cards to transfer to " + GameUtils.getCardLink(_squadron), 0, 1, validToTransfer) {
                    @Override
                    protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
                        if (selectedCards.isEmpty()) {
                            return;
                        }
                        final PhysicalCard cardToTransfer = selectedCards.iterator().next();
                        final PhysicalCard transferredFrom = cardToTransfer.getAttachedTo();

                        if (Filters.character.accepts(game, cardToTransfer)) {
                            // Need to determine capacity slot for character
                            boolean canBePilot = Filters.hasAvailablePilotCapacity(cardToTransfer).accepts(game, _squadron);
                            boolean canBePassenger = Filters.hasAvailablePassengerCapacity(cardToTransfer).accepts(game, _squadron);

                            if (canBePilot && canBePassenger) {
                                // Ask player to choose pilot/driver or passenger capacity slot
                                subAction.insertEffect(
                                        new PlayoutDecisionEffect(subAction, playerId,
                                                new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getCardLink(cardToTransfer) + " aboard " + GameUtils.getCardLink(_squadron), new String[]{"Pilot", "Passenger"}) {
                                                    @Override
                                                    protected void validDecisionMade(int index, String result) {
                                                        boolean transferAsPilot = (index == 0);
                                                        if (transferAsPilot) {
                                                            gameState.sendMessage(playerId + " transfers " + GameUtils.getCardLink(cardToTransfer) + " from " + GameUtils.getCardLink(transferredFrom) + " to " + GameUtils.getCardLink(_squadron) + " as pilot");
                                                            gameState.moveCardToAttachedInPilotCapacitySlot(cardToTransfer, _squadron);
                                                        } else {
                                                            gameState.sendMessage(playerId + " transfers " + GameUtils.getCardLink(cardToTransfer) + " from " + GameUtils.getCardLink(transferredFrom) + " to " + GameUtils.getCardLink(_squadron) + " as passenger");
                                                            gameState.moveCardToAttachedInPassengerCapacitySlot(cardToTransfer, _squadron);
                                                        }
                                                    }
                                                }
                                        )
                                );
                            } else {
                                if (canBePilot) {
                                    gameState.sendMessage(playerId + " transfers " + GameUtils.getCardLink(cardToTransfer) + " from " + GameUtils.getCardLink(transferredFrom) + " to " + GameUtils.getCardLink(_squadron) + " as pilot");
                                    gameState.moveCardToAttachedInPilotCapacitySlot(cardToTransfer, _squadron);
                                } else {
                                    gameState.sendMessage(playerId + " transfers " + GameUtils.getCardLink(cardToTransfer) + " from " + GameUtils.getCardLink(transferredFrom) + " to " + GameUtils.getCardLink(_squadron) + " as passenger");
                                    gameState.moveCardToAttachedInPassengerCapacitySlot(cardToTransfer, _squadron);
                                }
                            }
                        } else {
                            gameState.sendMessage(playerId + " transfers " + GameUtils.getCardLink(cardToTransfer) + " from " + GameUtils.getCardLink(transferredFrom) + " to " + GameUtils.getCardLink(_squadron));
                            gameState.moveCardToAttached(cardToTransfer, _squadron);
                        }
                    }
                },
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        checkTransferCardsToSquadron(subAction, playerId, game);
                    }
                }
        );
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
