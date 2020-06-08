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
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromPileEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToRemoveJustLostCardFromLostPileResult;

import java.util.HashSet;
import java.util.Set;

/**
 * An effect that causes the specified player to choose a character, device, or weapon from Lost Pile and place it at a location.
 */
public class PlaceAtLocationFromLostPileEffect extends AbstractSubActionEffect implements PreventableCardEffect {
    private String _playerId;
    private Filterable _cardFilter;
    private Set<PhysicalCard> _preventedCards = new HashSet<PhysicalCard>();
    private Filterable _locationFilter;
    private boolean _reshuffle;
    private boolean _justLost;
    private PlaceAtLocationFromLostPileEffect _that;

    /**
     * Creates an effect that causes the specified player to choose a card from Lost Pile and place it at a location.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     * @param justLost true if cards were just lost, otherwise false
     */
    public PlaceAtLocationFromLostPileEffect(Action action, String playerId, Filterable cardFilter, Filterable locationFilter, boolean reshuffle, boolean justLost) {
        super(action);
        _playerId = playerId;
        _cardFilter = cardFilter;
        _locationFilter = locationFilter;
        _reshuffle = reshuffle;
        _justLost = justLost;
        _that = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return !game.getGameState().getLostPile(_playerId).isEmpty();
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action);
        subAction.setPerformingPlayer(_playerId);

        if (isPlayableInFull(game)) {
            subAction.appendEffect(
                    new ChooseAndPlaceCardAtLocation(subAction));
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _preventedCards.isEmpty();
    }

    /**
     * Prevents the specified card from being affected by the effect.
     * @param card the card
     */
    @Override
    public void preventEffectOnCard(PhysicalCard card) {
        _preventedCards.add(card);
    }

    /**
     * Determines if the specified card was prevented from being affected by the effect.
     * @param card the card
     * @return true or false
     */
    @Override
    public boolean isEffectOnCardPrevented(PhysicalCard card) {
        return _preventedCards.contains(card);
    }

    /**
     * A private effect that chooses and places a character, device, or weapon at the location.
     */
    private class ChooseAndPlaceCardAtLocation extends AbstractSubActionEffect {

        /**
         * Creates an effect that chooses and places a character, device, or weapon at the location.
         * @param action the action performing this effect
         */
        public ChooseAndPlaceCardAtLocation(Action action) {
            super(action);
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

            final SubAction subAction = new SubAction(_action);
            subAction.appendTargeting(
                    new ChooseCardFromPileEffect(subAction, subAction.getPerformingPlayer(), Zone.LOST_PILE, subAction.getPerformingPlayer(), validCardToChoose) {
                        @Override
                        protected void cardSelected(final SwccgGame game, final PhysicalCard selectedCard) {

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
                                                            // Trigger is "about to remove just-lost card from Lost Pile" if for just-lost cards.
                                                            // When responding to the trigger, the preventEffectOnCard method can be called to prevent specified cards from being removed from Lost Pile.
                                                            if (_justLost) {
                                                                // Emit effect result that attempting to remove a just lost card from Lost Pile
                                                                game.getActionsEnvironment().emitEffectResult(
                                                                        new AboutToRemoveJustLostCardFromLostPileResult(subAction, _playerId, selectedCard, _that));
                                                            }

                                                            subAction.appendEffect(
                                                                    new PassthruEffect(subAction) {
                                                                        @Override
                                                                        protected void doPlayEffect(SwccgGame game) {
                                                                            if (!isEffectOnCardPrevented(selectedCard)) {
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
}
