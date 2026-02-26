package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.cards.effects.PayMoveUsingLocationTextCostEffect;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.MoveUsingLocationTextEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.TargetingEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * An action to perform a movement using location text.
 */
public class MoveUsingLocationTextAction extends TopLevelGameTextAction {
    private String _playerId;
    private boolean _fromCardChosen;
    private TargetingEffect _chooseFromCardEffect;
    private boolean _destinationChosen;
    private TargetingEffect _chooseDestinationEffect;
    private PhysicalCard _destination;
    private boolean _cardToMoveChosen;
    private TargetingEffect _chooseCardToMoveEffect;
    private PhysicalCard _cardToMove;
    private boolean _capacitySlotChosen;
    private PlayoutDecisionEffect _chooseCapacitySlotEffect;
    private boolean _moveAsPilot;
    private boolean _moveAsVehicle;
    private Effect _moveCardEffect;
    private boolean _useForceCostApplied;
    private boolean _cardMoved;
    private boolean _forFree;
    private float _baseCost;
    private Action _that;

    /**
     * Creates an action to perform a movement using location text.
     * @param playerId the player
     * @param game the game
     * @param location the location
     * @param gameTextSourceCardId the card id of the card the game text is originally from
     * @param cardToMoveFilter the filter for card to move
     * @param fromCardFilter the filter for card to move from
     * @param toCardFilter the filter for card to move to
     * @param forFree true if moving for free, otherwise false
     */
    public MoveUsingLocationTextAction(final String playerId, final SwccgGame game, PhysicalCard location, int gameTextSourceCardId, final Filterable cardToMoveFilter, Filterable fromCardFilter, final Filterable toCardFilter, boolean forFree) {
        this(playerId, game, location, gameTextSourceCardId, cardToMoveFilter, fromCardFilter, toCardFilter, forFree, 1);
    }

    /**
     * Creates an action to perform a movement using location text.
     * @param playerId the player
     * @param game the game
     * @param location the location
     * @param gameTextSourceCardId the card id of the card the game text is originally from
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of the cardToMoveFilter or null
     * @param cardToMoveFilter the filter for card to move
     * @param fromCardFilter the filter for card to move from
     * @param toCardFilter the filter for card to move to
     * @param forFree true if moving for free, otherwise false
     */
    public MoveUsingLocationTextAction(final String playerId, final SwccgGame game, PhysicalCard location, int gameTextSourceCardId, Map<InactiveReason, Boolean> spotOverrides, final Filterable cardToMoveFilter, Filterable fromCardFilter, final Filterable toCardFilter, boolean forFree) {
        this(playerId, game, location, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT, spotOverrides, cardToMoveFilter, fromCardFilter, toCardFilter, forFree, 1);
    }

    /**
     * Creates an action to perform a movement using location text.
     * @param playerId the player
     * @param game the game
     * @param location the location
     * @param gameTextSourceCardId the card id of the card the game text is originally from
     * @param cardToMoveFilter the filter for card to move
     * @param fromCardFilter the filter for card to move from
     * @param toCardFilter the filter for card to move to
     * @param forFree true if moving for free, otherwise false
     * @param baseCost base cost in amount of Force required to perform the movement
     */
    public MoveUsingLocationTextAction(final String playerId, final SwccgGame game, PhysicalCard location, int gameTextSourceCardId, final Filterable cardToMoveFilter, Filterable fromCardFilter, final Filterable toCardFilter, boolean forFree, float baseCost) {
        this(playerId, game, location, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT, cardToMoveFilter, fromCardFilter, toCardFilter, forFree, baseCost);
    }

    /**
     * Creates an action to perform a movement using location text.
     * @param playerId the player
     * @param game the game
     * @param location the location
     * @param gameTextSourceCardId the card id of the card the game text is originally from
     * @param cardToMoveFilter the filter for card to move
     * @param fromCardFilter the filter for card to move from
     * @param toCardFilter the filter for card to move to
     * @param forFree true if moving for free, otherwise false
     * @param baseCost base cost in amount of Force required to perform the movement
     */
    public MoveUsingLocationTextAction(final String playerId, final SwccgGame game, PhysicalCard location, int gameTextSourceCardId, GameTextActionId gameTextActionId, final Filterable cardToMoveFilter, Filterable fromCardFilter, final Filterable toCardFilter, boolean forFree, float baseCost) {
        this(playerId, game, location, gameTextSourceCardId, gameTextActionId, null, cardToMoveFilter, fromCardFilter, toCardFilter, forFree, baseCost);
    }

    /**
         * Creates an action to perform a movement using location text.
         * @param playerId the player
         * @param game the game
         * @param location the location
         * @param gameTextSourceCardId the card id of the card the game text is originally from
         * @param gameTextActionId the game text action ID for the source card
         * @param spotOverrides overrides which cards can be seen as "active" for the purposes of the cardToMoveFilter or null
         * @param cardToMoveFilter the filter for card to move
         * @param fromCardFilter the filter for card to move from
         * @param toCardFilter the filter for card to move to
         * @param forFree true if moving for free, otherwise false
         * @param baseCost base cost in amount of Force required to perform the movement
         */
    public MoveUsingLocationTextAction(final String playerId, final SwccgGame game, PhysicalCard location, int gameTextSourceCardId, GameTextActionId gameTextActionId, Map<InactiveReason, Boolean> spotOverrides, final Filterable cardToMoveFilter, Filterable fromCardFilter, final Filterable toCardFilter, boolean forFree, float baseCost) {
        super(location, playerId, gameTextSourceCardId, gameTextActionId);
        setText("Move using location text");
        _playerId = playerId;
        _forFree = forFree;
        _baseCost = baseCost;
        _that = this;

        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        final Collection<PhysicalCard> fromCards = Filters.filterActive(game, null, fromCardFilter);
        final Collection<PhysicalCard> toCards = Filters.filterActive(game, null, toCardFilter);

        final List<PhysicalCard> validFromCards = new ArrayList<PhysicalCard>();

        // Figure out which from locations (or starships/vehicles) contain any of the cards can move to valid to locations (or starship/vehicles)
        for (PhysicalCard fromCard : fromCards) {
            final Collection<PhysicalCard> cardsToMoveFromCard = Filters.filterActive(game, null, spotOverrides,
                    Filters.and(Filters.owner(playerId), cardToMoveFilter, Filters.hasNotPerformedRegularMove, Filters.or(Filters.atLocation(fromCard), Filters.aboardExceptRelatedSites(fromCard))));

            for (PhysicalCard cardToMove : cardsToMoveFromCard) {
                for (PhysicalCard toCard : toCards) {
                    // Check if card can move to destination card
                    if (Filters.canMoveToUsingLocationText(cardToMove, _forFree, _baseCost, 0).accepts(gameState, modifiersQuerying, toCard)) {
                        validFromCards.add(fromCard);
                        break;
                    }
                }
            }
        }

        // Choose card to move from
        _chooseFromCardEffect = new ChooseCardOnTableEffect(_that, _playerId, "Choose card to move from", validFromCards) {
            @Override
            protected void cardSelected(PhysicalCard fromCard) {
                _fromCardChosen = true;

                final Collection<PhysicalCard> cardsToMove = Filters.filterActive(game, null, spotOverrides,
                        Filters.and(Filters.owner(playerId), cardToMoveFilter, Filters.hasNotPerformedRegularMove, Filters.or(Filters.atLocation(fromCard), Filters.aboardExceptRelatedSites(fromCard))));

                final LinkedList<PhysicalCard> validToCards = new LinkedList<PhysicalCard>();
                // Figure out which other cards any of the cards can move to
                for (PhysicalCard cardToMove : cardsToMove) {
                    for (PhysicalCard toCard : toCards) {
                        // Check if card can move to destination card
                        if (Filters.canMoveToUsingLocationText(cardToMove, _forFree, _baseCost, 0).accepts(gameState, modifiersQuerying, toCard)) {
                            validToCards.add(toCard);
                        }
                    }
                }

                // Choose destination
                _chooseDestinationEffect = new ChooseCardOnTableEffect(_that, _playerId, "Choose card to move to", validToCards) {
                    @Override
                    protected void cardSelected(PhysicalCard toCard) {
                        _destinationChosen = true;
                        _destination = toCard;

                        LinkedList<PhysicalCard> validToMove = new LinkedList<PhysicalCard>();
                        for (PhysicalCard cardToMove : cardsToMove) {
                            // Check if card can move to destination card
                            if (Filters.canMoveToUsingLocationText(cardToMove, _forFree, _baseCost, 0).accepts(gameState, modifiersQuerying, _destination)) {
                                validToMove.add(cardToMove);
                            }
                        }

                        _chooseCardToMoveEffect = new ChooseCardOnTableEffect(_that, _playerId, "Choose card to move to " + GameUtils.getCardLink(_destination), validToMove) {
                            @Override
                            protected void cardSelected(PhysicalCard cardToMove) {
                                _cardToMoveChosen = true;
                                _cardToMove = cardToMove;

                                // Check if moving to location
                                if (_destination.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
                                    _capacitySlotChosen = true;
                                    _moveCardEffect = new MoveUsingLocationTextEffect(_that, _cardToMove, _destination, false, false);
                                }
                                else {
                                    if (_cardToMove.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                                        // Need to determine capacity slot for character
                                        boolean canBePilot = Filters.hasAvailablePilotCapacity(_cardToMove).accepts(gameState, modifiersQuerying, _destination);
                                        boolean canBePassenger = Filters.hasAvailablePassengerCapacity(_cardToMove).accepts(gameState, modifiersQuerying, _destination);

                                        if (canBePilot && canBePassenger) {
                                            String[] seatChoices;
                                            if (Filters.transport_vehicle.accepts(gameState, modifiersQuerying, _destination))
                                                seatChoices = new String[]{"Driver", "Passenger"};
                                            else
                                                seatChoices = new String[]{"Pilot", "Passenger"};

                                            // Ask player to choose pilot/driver or passenger capacity slot
                                            _chooseCapacitySlotEffect = new PlayoutDecisionEffect(_that, getPerformingPlayer(),
                                                    new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getCardLink(_cardToMove) + " aboard " + GameUtils.getCardLink(_destination), seatChoices) {
                                                        @Override
                                                        protected void validDecisionMade(int index, String result) {
                                                            _capacitySlotChosen = true;
                                                            _moveAsPilot = (index == 0);

                                                            // Capacity slot chosen, move character.
                                                            _moveCardEffect = new MoveUsingLocationTextEffect(_that, _cardToMove, _destination, _moveAsPilot, false);
                                                        }
                                                    });
                                        }
                                        else {
                                            _capacitySlotChosen = true;
                                            _moveAsPilot = canBePilot;

                                            // If both capacity slots were not available, move character to available slot.
                                            _moveCardEffect = new MoveUsingLocationTextEffect(_that, _cardToMove, _destination, _moveAsPilot, false);
                                        }
                                    }
                                    else if (_cardToMove.getBlueprint().isMovesLikeCharacter()) {
                                        _capacitySlotChosen = true;
                                        _moveCardEffect = new MoveUsingLocationTextEffect(_that, _cardToMove, _destination, false, false);
                                    }
                                    else {
                                        // Need to determine capacity slot for starship/vehicle
                                        boolean canGoInVehicleSlot = Filters.hasAvailableVehicleCapacity(_cardToMove).accepts(gameState, modifiersQuerying, _destination);
                                        boolean canGoInStarshipSlot = Filters.hasAvailableStarfighterOrTIECapacity(_cardToMove).accepts(gameState, modifiersQuerying, _destination);

                                        if (canGoInVehicleSlot && canGoInStarshipSlot) {
                                            // Ask player to choose vehicle or starship capacity slot
                                            _chooseCapacitySlotEffect = new PlayoutDecisionEffect(_that, getPerformingPlayer(),
                                                    new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getCardLink(_cardToMove) + " in cargo bay of " + GameUtils.getCardLink(_destination), new String[]{"Vehicle", "Starship"}) {
                                                        @Override
                                                        protected void validDecisionMade(int index, String result) {
                                                            _capacitySlotChosen = true;
                                                            _moveAsVehicle = (index == 0);

                                                            // Capacity slot chosen, move starship/vehicle.
                                                            _moveCardEffect = new MoveUsingLocationTextEffect(_that, _cardToMove, _destination, false, _moveAsVehicle);
                                                        }
                                                    });
                                        } else {
                                            _capacitySlotChosen = true;
                                            _moveAsVehicle = canGoInVehicleSlot;

                                            // If both capacity slots were not available, move starship/vehicle to available slot.
                                            _moveCardEffect = new MoveUsingLocationTextEffect(_that, _cardToMove, _destination, false, _moveAsVehicle);
                                        }
                                    }
                                }
                            }
                        };
                    }
                };
            }
        };
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        if (!isAnyCostFailed()) {

            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_fromCardChosen) {
                _fromCardChosen = true;
                appendTargeting(_chooseFromCardEffect);
                return getNextCost();
            }

            if (!_destinationChosen) {
                _destinationChosen = true;
                appendTargeting(_chooseDestinationEffect);
                return getNextCost();
            }

            if (!_cardToMoveChosen) {
                _cardToMoveChosen = true;
                appendTargeting(_chooseCardToMoveEffect);
                return getNextCost();
            }

            if (!_capacitySlotChosen) {
                _capacitySlotChosen = true;
                appendCost(_chooseCapacitySlotEffect);
                return getNextCost();
            }

            if (!_useForceCostApplied) {
                _useForceCostApplied = true;
                if (!_forFree) {
                    appendCost(new PayMoveUsingLocationTextCostEffect(this, getPerformingPlayer(), _cardToMove, _destination, _baseCost, 0));
                    return getNextCost();
                }
            }

            if (!_cardMoved) {
                _cardMoved = true;
                return _moveCardEffect;
            }

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }

        return null;
    }

    @Override
    public boolean wasCarriedOut() {
        return _cardMoved && _moveCardEffect.wasCarriedOut();
    }
}

