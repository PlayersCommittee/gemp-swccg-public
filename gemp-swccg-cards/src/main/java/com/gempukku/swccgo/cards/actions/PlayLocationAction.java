package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.common.LocationPlacementDirection;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.layout.LocationPlacement;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractPlayCardAction;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.DeployLocationEffect;
import com.gempukku.swccgo.logic.effects.PlayCardEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.*;

/**
 * The action to deploy a location.
 */
public class PlayLocationAction extends AbstractPlayCardAction {
    private Action _that;
    private boolean _actionInitiated;
    private boolean _actionComplete;
    private PhysicalCard _location;
    private List<LocationPlacement> _placements;
    private boolean _checkedMainPowerGeneratorsRule;
    private boolean _parentChosen;
    private String _parentName;
    private Persona _parentPersona;
    private PhysicalCard _parentStarshipOrVehicle;
    private boolean _placementChosen;
    private LocationPlacement _placement;
    private boolean _directionChosen;
    private boolean _cardPlayed;
    private PlayCardEffect _playLocationEffect;

    /**
     * Creates an action to deploy the specified location.
     * @param sourceCard the card to initiate the deployment
     * @param location the location
     * @param placements the valid placements to deploy the location
     */
    public PlayLocationAction(PhysicalCard sourceCard, final PhysicalCard location, final List<LocationPlacement> placements) {
        super(location, sourceCard);
        setPerformingPlayer(location.getOwner());
        _that = this;
        _location = location;
        _placements = placements;
        _text = "Deploy";
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        final GameState gameState = game.getGameState();

        if (!_actionInitiated) {
            _actionInitiated = true;
            gameState.beginPlayCard(this);
        }

        // Verify no costs have failed
        if (!isAnyCostFailed()) {
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            // Main Power Generators deployment rule
            if (!_checkedMainPowerGeneratorsRule) {
                _checkedMainPowerGeneratorsRule = true;

                if (Filters.Main_Power_Generators.accepts(game, _location)
                        && !Filters.canSpotFromTopLocationsOnTable(game, Filters.or(Filters.Fourth_Marker, Filters.Fifth_Marker, Filters.Sixth_Marker))) {

                    gameState.sendMessage(_that.getPerformingPlayer() + " targets to deploy " + GameUtils.getCardLink(_location) + ", but must first deploy 4th Marker from Reserve Deck since no 4th, 5th, or 6th Marker on table");

                    // Player must deploy 4th Marker from Reserve Deck, otherwise Main Power Generators cannot be deployed. If the 4th Marker fails to deploy,
                    // then the sub-action will not be "carried out", so the cost will fail, which will cancel this PlayLocationAction.
                    SubAction deploy4thMarkerAction = new SubAction(_that);
                    final DeployCardFromReserveDeckEffect deploy4thMarkerEffect = new DeployCardFromReserveDeckEffect(deploy4thMarkerAction, Filters.Fourth_Marker, true) {
                        @Override
                        public String getChoiceText() {
                            return "Choose 4th Marker to deploy from Reserve Deck";
                        }
                    };
                    deploy4thMarkerAction.appendEffect(deploy4thMarkerEffect);
                    deploy4thMarkerAction.appendEffect(
                            new PassthruEffect(deploy4thMarkerAction) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    if (!deploy4thMarkerEffect.wasCarriedOut()) {
                                        gameState.sendMessage(_that.getPerformingPlayer() + " may not deploy " + GameUtils.getCardLink(_location) + " since no 4th, 5th, or 6th Marker on table and no 4th Marker was found in " + _that.getPerformingPlayer() + "'s Reserve Deck");
                                    }
                                    else {
                                        // Update valid placements after 4th Marker has been deployed
                                        _placements = gameState.getLocationPlacement(game, _location, null, null);
                                    }
                                }
                            }
                    );
                    appendTargeting(
                            new StackActionEffect(_that, deploy4thMarkerAction));
                    return getNextCost();
                }
            }

            // Check if the parent needs to be chosen.
            if (!_parentChosen) {

                // If location if a non-unique starship or vehicle site, check if there are multiple parent card options
                if (Filters.and(Filters.non_unique, Filters.or(Filters.starship_site, Filters.vehicle_site)).accepts(game, _location)) {

                    // If multiple placements have different "parent systems" for location, then ask player to select which planet to deploy
                    Set<PhysicalCard> parentStarshipOrVehicle = new HashSet<PhysicalCard>();
                    for (LocationPlacement placement : _placements) {
                        if (placement.getParentStarshipOrVehicleCard() != null)
                            parentStarshipOrVehicle.add(placement.getParentStarshipOrVehicleCard());
                        else if (placement.getParentStarshipOrVehiclePersona() != null) {
                            PhysicalCard starshipOrVehicle = Filters.findFirstActive(game, null, Filters.persona(placement.getParentStarshipOrVehiclePersona()));
                            if (starshipOrVehicle != null) {
                                parentStarshipOrVehicle.add(starshipOrVehicle);
                            }
                        }
                    }

                    // Check if only one option for parent starship or vehicle
                    if (parentStarshipOrVehicle.size() == 1) {
                        _parentChosen = true;
                        _parentStarshipOrVehicle = parentStarshipOrVehicle.iterator().next();
                    }
                    else {
                        String msgText;
                        if (Filters.starship_site.accepts(game, _location))
                            msgText = "Choose a starship to relate " + GameUtils.getCardLink(_location);
                        else
                            msgText = "Choose a vehicle to relate " + GameUtils.getCardLink(_location);

                        // Choose the parent starship or vehicle
                        appendTargeting(new ChooseCardOnTableEffect(_that, getPerformingPlayer(), msgText, Filters.in(parentStarshipOrVehicle)) {
                            @Override
                            protected void cardSelected(PhysicalCard parentCard) {
                                _parentChosen = true;
                                _parentStarshipOrVehicle = parentCard;
                            }
                        });
                        return getNextCost();
                    }
                }
                // If location is per system uniqueness, need to choose a parent system
                else if (Filters.perSystemUniqueness.accepts(game, _location)) {

                    // If multiple placements have different "parent systems" for location, then ask player to select which planet to deploy to
                    Set<String> parentPlanetNames = new HashSet<String>();
                    for (LocationPlacement placement : _placements) {
                        parentPlanetNames.add(placement.getParentSystem());
                    }

                    if (parentPlanetNames.size() == 1) {
                        _parentChosen = true;
                        _parentName = parentPlanetNames.iterator().next();
                    }
                    else {
                        // For each planet system represented on the table, get the possible places in the
                        // location layout where the location could be deployed as part of that planet system.
                        Filter chooseParentFilter = Filters.none;
                        for (String systemName : parentPlanetNames) {
                            Filter filter = Filters.and(Filters.planet_system, Filters.title(systemName));
                            if (Filters.site.accepts(game, _location)) {
                                filter = Filters.or(filter, Filters.and(Filters.planet_site, Filters.not(Filters.perSystemUniqueness), Filters.partOfSystem(systemName)));
                            }
                            else if (Filters.asteroid_sector.accepts(game, _location)) {
                                filter = Filters.or(filter, Filters.and(Filters.asteroid_sector, Filters.isOrbiting(systemName)));
                            }
                            else if (Filters.cloud_sector.accepts(game, _location)) {
                                filter = Filters.or(filter, Filters.and(Filters.cloud_sector, Filters.partOfSystem(systemName)));
                            }
                            // Special case for asteroid cave (only related to Big One)
                            else if (Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly.accepts(game, _location)) {
                                filter = Filters.and(Filters.Big_One, Filters.isOrbiting(systemName));
                            }

                            chooseParentFilter = Filters.or(chooseParentFilter, filter);
                        }

                        appendTargeting(new ChooseCardOnTableEffect(_that, getPerformingPlayer(), "Choose the system (or location related to the system) to deploy " + GameUtils.getCardLink(_location) + " as a related location", chooseParentFilter) {
                            @Override
                            protected void cardSelected(PhysicalCard relatedLocation) {
                                _parentChosen = true;
                                _parentName = relatedLocation.getPartOfSystem() != null ? relatedLocation.getPartOfSystem() : relatedLocation.getSystemOrbited();
                            }
                        });
                        return getNextCost();
                    }
                }
                // Otherwise the placement should have either a parent system or parent starship/vehicle persona set in its blueprint.
                else {
                    _parentChosen = true;
                    _parentName = _location.getBlueprint().getSystemName();
                    _parentPersona = _location.getBlueprint().getRelatedStarshipOrVehiclePersona();
                }
            }

            // Check if the other card for placement needs to be chosen.
            if (!_placementChosen) {

                // Check if there is only one placement option.
                if (_placements.size() == 1) {
                    _placement = _placements.get(0);
                    _placementChosen = true;
                } else {

                    // Only include choices that that include the same parent
                    final Map<PhysicalCard, LocationPlacement> otherCards = new HashMap<PhysicalCard, LocationPlacement>();
                    for (LocationPlacement placement : _placements) {
                        if (_parentName != null && _parentName.equals(placement.getParentSystem()))
                            otherCards.put(placement.getOtherCard(), placement);
                        else if ((_parentPersona != null && _parentPersona == placement.getParentStarshipOrVehiclePersona())
                                || (_parentStarshipOrVehicle != null && placement.getParentStarshipOrVehiclePersona() != null
                                && _parentStarshipOrVehicle.getBlueprint().hasPersona(placement.getParentStarshipOrVehiclePersona())))
                            otherCards.put(placement.getOtherCard(), placement);
                        else if (_parentStarshipOrVehicle != null && _parentStarshipOrVehicle == placement.getParentStarshipOrVehicleCard())
                            otherCards.put(placement.getOtherCard(), placement);
                    }

                    // Check if there was only one valid placement
                    if (otherCards.keySet().size() == 1) {
                        _placement = otherCards.values().iterator().next();
                        _placementChosen = true;
                    }
                    else {
                        // Choose the card to deploy next to (or convert)
                        appendTargeting(new ChooseCardOnTableEffect(_that, getPerformingPlayer(), "Choose a location to deploy " + GameUtils.getCardLink(_location) + " next to (or convert)", Filters.in(otherCards.keySet())) {
                            @Override
                            protected void cardSelected(PhysicalCard otherCard) {
                                _placement = otherCards.get(otherCard);
                                _placementChosen = true;
                            }
                        });
                        return getNextCost();
                    }
                }
            }

            // Check if the direction in relation to the other card needs to be chosen.
            if (!_directionChosen) {

                if (_placement.getDirection().isOneDirection()) {
                    _directionChosen = true;
                }
                else {
                    // Ask player "left, convert, or right".
                    List<String> choices = new ArrayList<String>();
                    if (_placement.getDirection().isLeftOf())
                        choices.add("Left");
                    if (_placement.getDirection().isReplace()) {
                        if (_placement.getOtherCard().isCollapsed())
                            choices.add("Rebuild");
                        else
                            choices.add("Convert");
                    }
                    if (_placement.getDirection().isRightOf())
                        choices.add("Right");

                    appendTargeting(new PlayoutDecisionEffect(_that, getPerformingPlayer(),
                            new MultipleChoiceAwaitingDecision("On which side of " + GameUtils.getCardLink(_placement.getOtherCard()) + " do you want to deploy " + GameUtils.getCardLink(_location), choices.toArray(new String[choices.size()])) {
                                @Override
                                protected void validDecisionMade(int index, String result) {
                                    _directionChosen = true;
                                    if ("Left".equals(result))
                                        _placement.setDirection(LocationPlacementDirection.LEFT_OF);
                                    else if ("Convert".equals(result) || "Rebuild".equals(result))
                                        _placement.setDirection(LocationPlacementDirection.REPLACE);
                                    else
                                        _placement.setDirection(LocationPlacementDirection.RIGHT_OF);
                                }
                            }));
                    return getNextCost();
                }
            }

            // Play the location
            if (!_cardPlayed) {
                _cardPlayed = true;
                _playLocationEffect = new DeployLocationEffect(_that, _location, _placement, _reshuffle);
                return _playLocationEffect;
            }
        }

        if (!_actionComplete) {
            _actionComplete = true;
            gameState.endPlayCard();
        }

        return null;
    }

    public boolean wasCarriedOut() {
        return _cardPlayed && _playLocationEffect.wasCarriedOut();
    }
}
