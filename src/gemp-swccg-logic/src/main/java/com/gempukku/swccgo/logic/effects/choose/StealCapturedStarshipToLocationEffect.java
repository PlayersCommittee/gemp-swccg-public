package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToBeStolenResult;
import com.gempukku.swccgo.logic.timing.results.StolenResult;

import java.util.*;

/**
 * An effect that carries out the stealing of a single captured starship at a location to the new owner's side of the location.
 */
class StealCapturedStarshipToLocationEffect extends AbstractSubActionEffect implements PreventableCardEffect {
    private String _playerId;
    private PhysicalCard _cardToBeStolen;
    private Set<PhysicalCard> _preventedCards = new HashSet<PhysicalCard>();
    private StealCapturedStarshipToLocationEffect _that;

    /**
     * Creates an effect that carries out the stealing of a single captured starship at a location to the new owner's side of the location.
     * @param action the action performing this effect
     * @param cardToSteal the card to steal
     */
    public StealCapturedStarshipToLocationEffect(Action action, PhysicalCard cardToSteal) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _cardToBeStolen = cardToSteal;
        _that = this;
    }

    private PhysicalCard getLocation(SwccgGame game, PhysicalCard starshipToSteal) {
        PhysicalCard currentLocation = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), starshipToSteal);
        if (currentLocation.getBlueprint().getCardSubtype() == CardSubtype.SITE) {
            if (game.getModifiersQuerying().isAtStarshipSite(game.getGameState(), starshipToSteal)) {
                PhysicalCard starship = Filters.findFirstFromAllOnTable(game, Filters.relatedStarshipOrVehicle(currentLocation));
                PhysicalCard location = game.getModifiersQuerying().getLocationHere(game.getGameState(), starship);
                return location;
            }

            PhysicalCard relatedSystem = Filters.findFirstFromTopLocationsOnTable(game, Filters.relatedSystem(currentLocation));
            return relatedSystem;
        }
        return currentLocation;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        final SubAction subAction = new SubAction(_action);

        // 1) Trigger is "about to be stolen" for cards specified cards to be stolen.
        // When responding to the trigger, the preventEffectOnCard method can be called to prevent specified cards from being stolen.
        List<EffectResult> effectResults = new ArrayList<EffectResult>();
        effectResults.add(new AboutToBeStolenResult(subAction, _cardToBeStolen, _that));
        subAction.appendEffect(new TriggeringResultsEffect(subAction, effectResults));

        // 2) Steal the card
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(final SwccgGame game) {
                        if (!isEffectOnCardPrevented(_cardToBeStolen)) {

                            List<PhysicalCard> allCardsStolen = new LinkedList<PhysicalCard>();
                            allCardsStolen.add(_cardToBeStolen);
                            //steal all cargo aboard as well
                            allCardsStolen.addAll(gameState.getAllAttachedRecursively(_cardToBeStolen));

                            gameState.sendMessage(_playerId + " steals " + GameUtils.getCardLink(_cardToBeStolen)
                                    + (_action.getActionSource()==null?"":" using " + GameUtils.getCardLink(_action.getActionSource())));

                            // Update owner and zone owner of each card, change it so it's not a captured starship, then attach card
                            for (PhysicalCard card : allCardsStolen) {
                                card.setOwner(_playerId);
                                card.setZoneOwner(_playerId);
                                if (card.isCapturedStarship()) {
                                    card.setCapturedStarship(false);
                                    card.clearTargetedCards();
                                }
                            }

                            PhysicalCard currentLocation = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), _cardToBeStolen);
                            //TODO have it go to the appropriate system/sector instead of staying at a site
                            PhysicalCard stealToLocation = getLocation(game, _cardToBeStolen);

                            //TODO let it be stolen directly into a cargo bay
                            final List<PhysicalCard> validStealPoints = new LinkedList<PhysicalCard>();
                            validStealPoints.add(stealToLocation);
                            validStealPoints.addAll(Filters.filterActive(game, null, Filters.and(Filters.owner(_cardToBeStolen.getOwner()),
                                    Filters.or(Filters.starship, Filters.vehicle), Filters.at(stealToLocation),
                                    Filters.or(Filters.hasAvailableStarfighterOrTIECapacity(_cardToBeStolen),
                                            Filters.hasAvailableCapitalStarshipCapacity(_cardToBeStolen),
                                            Filters.hasAvailableVehicleCapacity(_cardToBeStolen)))));


                            subAction.appendEffect(new ChooseCardOnTableEffect(subAction, _playerId, "Choose where to steal "+GameUtils.getCardLink(_cardToBeStolen), validStealPoints) {
                                @Override
                                protected void cardSelected(final PhysicalCard targetCard) {
                                    if (targetCard.getBlueprint().getCardCategory() == CardCategory.LOCATION)
                                        game.getGameState().moveCardToLocation(_cardToBeStolen, targetCard);
                                    else {
                                        boolean canGoInStarfighterCapacity = Filters.hasAvailableStarfighterOrTIECapacity(_cardToBeStolen).accepts(gameState, modifiersQuerying, targetCard);
                                        boolean canGoInCapitalCapacity = Filters.hasAvailableCapitalStarshipCapacity(_cardToBeStolen).accepts(gameState, modifiersQuerying, targetCard);
                                        boolean canGoInVehicleCapacity = Filters.hasAvailableVehicleCapacity(_cardToBeStolen).accepts(gameState, modifiersQuerying, targetCard);

                                        List<String> choices = new LinkedList<>();
                                        if (canGoInCapitalCapacity)
                                            choices.add("Capital");
                                        if (canGoInStarfighterCapacity)
                                            choices.add("Starfighter");
                                        if (canGoInVehicleCapacity)
                                            choices.add("Vehicle");

                                        final String[] capacityChoices = new String[choices.size()];
                                        choices.toArray(capacityChoices);

                                        if (capacityChoices.length <= 1) {
                                            //only one available slot
                                            switch (capacityChoices[0]) {
                                                case "Capital":
                                                    game.getGameState().moveCardToAttachedInCapitalStarshipCapacitySlot(_cardToBeStolen, targetCard);
                                                    break;
                                                case "Starfighter":
                                                    game.getGameState().moveCardToAttachedInStarfighterOrTIECapacitySlot(_cardToBeStolen, targetCard);
                                                    break;
                                                case "Vehicle":
                                                    game.getGameState().moveCardToAttachedInVehicleCapacitySlot(_cardToBeStolen, targetCard);
                                                    break;
                                            }
                                        } else {
                                            subAction.appendEffect(
                                                    new PlayoutDecisionEffect(subAction, _playerId,
                                                            new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getCardLink(_cardToBeStolen) + " aboard " + GameUtils.getCardLink(targetCard), capacityChoices) {
                                                                @Override
                                                                protected void validDecisionMade(int index, String result) {
                                                                    switch (result) {
                                                                        case "Capital":
                                                                            game.getGameState().moveCardToAttachedInCapitalStarshipCapacitySlot(_cardToBeStolen, targetCard);
                                                                            break;
                                                                        case "Starfighter":
                                                                            game.getGameState().moveCardToAttachedInStarfighterOrTIECapacitySlot(_cardToBeStolen, targetCard);
                                                                            break;
                                                                        case "Vehicle":
                                                                            game.getGameState().moveCardToAttachedInVehicleCapacitySlot(_cardToBeStolen, targetCard);
                                                                            break;
                                                                    }
                                                                }
                                                            }));
                                        }
                                    }
                                }
                            });

                            for (PhysicalCard card : allCardsStolen) {
                                gameState.reapplyAffectingForCard(game, card);
                            }
                            // Emit effect result for each stolen card
                            game.getActionsEnvironment().emitEffectResult(new StolenResult(_playerId, _cardToBeStolen, currentLocation));
                        }
                    }
                }
        );

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
}
