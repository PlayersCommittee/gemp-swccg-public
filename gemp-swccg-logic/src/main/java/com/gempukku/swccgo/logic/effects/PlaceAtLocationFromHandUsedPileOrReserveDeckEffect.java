package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandOrCardPilesEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Arrays;

/**
 * An effect that causes the specified player to choose a card from hand, Used Pile, or Reserve Deck and place it at a location.
 */
public class PlaceAtLocationFromHandUsedPileOrReserveDeckEffect extends AbstractSubActionEffect {
    private String _playerId;
    private Filterable _cardFilter;
    private Filterable _locationFilter;
    private boolean _reshuffle;

    /**
     * Creates an effect that causes the specified player to choose a card from hand, Used Pile, or Reserve Deck and place it at a location.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public PlaceAtLocationFromHandUsedPileOrReserveDeckEffect(Action action, String playerId, Filterable cardFilter, Filterable locationFilter, boolean reshuffle) {
        super(action);
        _playerId = playerId;
        _cardFilter = cardFilter;
        _locationFilter = locationFilter;
        _reshuffle = reshuffle;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        // Choose the card (only characters, devices, or weapons can be placed at a location)
        Filter validCardToChoose = Filters.and(_cardFilter, Filters.canBePlacedAtLocation(Filters.and(_locationFilter)));

        final SubAction subAction = new SubAction(_action, _playerId);
        subAction.appendTargeting(
                new ChooseCardFromHandOrCardPilesEffect(subAction, _playerId, Arrays.asList(Zone.USED_PILE, Zone.RESERVE_DECK), _playerId, validCardToChoose, false, false) {
                    @Override
                    protected void cardSelected(SwccgGame game, final PhysicalCard selectedCard) {
                        // Choose where to place card at location
                        Filter placeFilter = Filters.and(Filters.locationAndCardsAtLocation(Filters.and(_locationFilter)), selectedCard.getBlueprint().getValidPlaceCardTargetFilter(game, selectedCard));

                        subAction.appendTargeting(
                                new TargetCardOnTableEffect(subAction, subAction.getPerformingPlayer(), "Place " + GameUtils.getFullName(selectedCard) + ". Choose where to place", TargetingReason.TO_BE_DEPLOYED_ON, placeFilter) {
                                    @Override
                                    protected void cardTargeted(int targetGroupId, final PhysicalCard target) {
                                        subAction.allowResponses(
                                                new UnrespondableEffect(subAction) {
                                                    @Override
                                                    protected void performActionResults(Action targetingAction) {
                                                        subAction.appendEffect(
                                                                new PassthruEffect(subAction) {
                                                                    @Override
                                                                    protected void doPlayEffect(SwccgGame game) {
                                                                        if (Filters.location.accepts(gameState, modifiersQuerying, target)) {
                                                                            if (selectedCard.getBlueprint().getSinglePlayCardZoneOption() == PlayCardZoneOption.ATTACHED) {
                                                                                // Place card as attached to location.
                                                                                subAction.appendEffect(
                                                                                        new PlaceCardInPlayEffect(subAction, selectedCard, target, false, _reshuffle));
                                                                            }
                                                                            else {
                                                                                // Place card as at location.
                                                                                subAction.appendEffect(
                                                                                        new PlaceCardInPlayEffect(subAction, selectedCard, target, _reshuffle));
                                                                            }
                                                                        }
                                                                        else {
                                                                            boolean canBePilot = Filters.hasAvailablePilotCapacity(selectedCard).accepts(gameState, modifiersQuerying, target);
                                                                            boolean canBePassenger = Filters.hasAvailablePassengerCapacity(selectedCard).accepts(gameState, modifiersQuerying, target);

                                                                            if (canBePilot && canBePassenger) {
                                                                                String[] seatChoices;
                                                                                if (Filters.transport_vehicle.accepts(game.getGameState(), game.getModifiersQuerying(), target))
                                                                                    seatChoices = new String[]{"Driver", "Passenger"};
                                                                                else
                                                                                    seatChoices = new String[]{"Pilot", "Passenger"};

                                                                                // Ask player to choose pilot/driver or passenger capacity slot
                                                                                subAction.appendEffect(
                                                                                        new PlayoutDecisionEffect(subAction, subAction.getPerformingPlayer(),
                                                                                                new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getFullName(selectedCard) + " aboard " + GameUtils.getFullName(target), seatChoices) {
                                                                                                    @Override
                                                                                                    protected void validDecisionMade(int index, String result) {
                                                                                                        boolean placeAsPilot = (index == 0);

                                                                                                        // Capacity slot chosen, place card.
                                                                                                        subAction.appendEffect(
                                                                                                                new PlaceCardInPlayEffect(subAction, selectedCard, target, placeAsPilot, _reshuffle));
                                                                                                    }
                                                                                                }
                                                                                        )
                                                                                );
                                                                            }
                                                                            else if (canBePilot) {
                                                                                // Only pilot slot available, place card.
                                                                                subAction.appendEffect(
                                                                                        new PlaceCardInPlayEffect(subAction, selectedCard, target, true, _reshuffle));
                                                                            }
                                                                            else {
                                                                                // No pilot slot available, place card.
                                                                                subAction.appendEffect(
                                                                                        new PlaceCardInPlayEffect(subAction, selectedCard, target, false, _reshuffle));
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                        );
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                });
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
