package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ReleaseOption;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * An effect that carries out the releasing of a single captured starship on table.
 */
class ReleaseOneCapturedStarshipEffect extends AbstractSubActionEffect {
    private String _performingPlayerId;
    private PhysicalCard _starshipToRelease;

    /**
     * Creates an effect that carries out the releasing of a single captured starship on table.
     * @param action the action performing this effect
     * @param starshipToRelease the captive to release
     */
    public ReleaseOneCapturedStarshipEffect(Action action, PhysicalCard starshipToRelease) {
        super(action);
        _performingPlayerId = starshipToRelease.getOwner();
        _starshipToRelease = starshipToRelease;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    private PhysicalCard getLocation(SwccgGame game, PhysicalCard starshipToRelease) {
        PhysicalCard currentLocation = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), starshipToRelease);
        if (currentLocation.getBlueprint().getCardSubtype() == CardSubtype.SITE) {
            if (game.getModifiersQuerying().isAtStarshipSite(game.getGameState(), starshipToRelease)) {
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
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        final PhysicalCard location = getLocation(game, _starshipToRelease);


        final boolean launchOnly = false; //TODO

        final SubAction subAction = new SubAction(_action);

        //
        // 1) Determine which options are available: 'Escape' or 'Launch'
        //
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Check for valid places for starship to 'Launch' to
                        final List<PhysicalCard> validLaunchPoints = new ArrayList<PhysicalCard>();
                        if (location != null && Filters.system_or_sector.accepts(gameState, modifiersQuerying, location)) {
                            validLaunchPoints.add(location);

                            validLaunchPoints.addAll(Filters.filterActive(game, null, Filters.and(Filters.owner(_starshipToRelease.getOwner()),
                                    Filters.or(Filters.starship, Filters.vehicle), Filters.at(location),
                                    Filters.or(Filters.hasAvailableStarfighterOrTIECapacity(_starshipToRelease), Filters.hasAvailableCapitalStarshipCapacity(_starshipToRelease)))));
                        }

                        if (validLaunchPoints.isEmpty()) {
                            // Release with 'Escape' is only option
                            subAction.appendEffect(
                                    new ReleaseWithEscapeEffect(subAction, _starshipToRelease));
                        }
                        else if (launchOnly) {
                            // Release with 'Launch' is only option

                            // Choose where the starship will launch to
                            subAction.appendEffect(
                                    new ChooseCardOnTableEffect(subAction, _performingPlayerId, "Choose where to launch " + GameUtils.getCardLink(_starshipToRelease), validLaunchPoints) {
                                        @Override
                                        protected void cardSelected(final PhysicalCard launchPoint) {

                                            // Check if rallying to location
                                            if (launchPoint.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
                                                subAction.appendEffect(
                                                        new ReleaseWithLaunchEffect(subAction, _starshipToRelease, launchPoint));
                                            }
                                            else {
                                                // Need to determine capacity slot for starship
                                                boolean canGoInStarfighterCapacity = Filters.hasAvailableStarfighterOrTIECapacity(_starshipToRelease).accepts(gameState, modifiersQuerying, launchPoint);
                                                boolean canGoInCapitalCapacity = Filters.hasAvailableCapitalStarshipCapacity(_starshipToRelease).accepts(gameState, modifiersQuerying, launchPoint);

                                                if (canGoInStarfighterCapacity && canGoInCapitalCapacity) {
                                                    String[] seatChoices = new String[]{"Starfighter", "Capital"};

                                                    // Ask player to choose starfighter or capital capacity slot
                                                    subAction.appendEffect(
                                                            new PlayoutDecisionEffect(subAction, _performingPlayerId,
                                                                    new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getCardLink(_starshipToRelease) + " aboard " + GameUtils.getCardLink(launchPoint), seatChoices) {
                                                                        @Override
                                                                        protected void validDecisionMade(int index, String result) {
                                                                            boolean rallyAsStarfighter = (index == 0);

                                                                            // Capacity slot chosen, release starship.
                                                                            subAction.appendEffect(
                                                                                    new ReleaseWithLaunchEffect(subAction, _starshipToRelease, launchPoint, rallyAsStarfighter));
                                                                        }
                                                                    }));
                                                } else {
                                                    // If both capacity slots were not available, launch starship to available slot.
                                                    subAction.appendEffect(
                                                            new ReleaseWithLaunchEffect(subAction, _starshipToRelease, launchPoint));
                                                }
                                            }
                                        }
                                    }
                            );
                        }
                        else {
                            List<String> choices = new ArrayList<String>();
                            choices.add(ReleaseOption.ESCAPE.getHumanReadable());
                            choices.add(ReleaseOption.LAUNCH.getHumanReadable());
                            String[] choiceArray = new String[choices.size()];
                            choices.toArray(choiceArray);

                            subAction.appendEffect(
                                    new PlayoutDecisionEffect(subAction, _performingPlayerId,
                                            new MultipleChoiceAwaitingDecision("Choose release option for " + GameUtils.getCardLink(_starshipToRelease), choiceArray) {
                                                @Override
                                                protected void validDecisionMade(int index, String result) {
                                                    if (result.equals(ReleaseOption.LAUNCH.getHumanReadable())) {
                                                        // Choose where the starship will launch to
                                                        subAction.appendEffect(
                                                                new ChooseCardOnTableEffect(subAction, _performingPlayerId, "Choose where to launch " + GameUtils.getCardLink(_starshipToRelease), validLaunchPoints) {
                                                                    @Override
                                                                    protected void cardSelected(final PhysicalCard launchPoint) {

                                                                        // Check if launching to location
                                                                        if (launchPoint.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
                                                                            subAction.appendEffect(
                                                                                    new ReleaseWithLaunchEffect(subAction, _starshipToRelease, launchPoint));
                                                                        }
                                                                        else {
                                                                            // Need to determine capacity slot for character
                                                                            boolean canGoInStarfighterCapacity = Filters.hasAvailableStarfighterOrTIECapacity(_starshipToRelease).accepts(gameState, modifiersQuerying, launchPoint);
                                                                            boolean canGoInCapitalCapacity = Filters.hasAvailableCapitalStarshipCapacity(_starshipToRelease).accepts(gameState, modifiersQuerying, launchPoint);

                                                                            if (canGoInStarfighterCapacity && canGoInCapitalCapacity) {
                                                                                String[] seatChoices = new String[]{"Starfighter", "Capital"};

                                                                                // Ask player to choose starfighter or capital capacity slot
                                                                                subAction.appendEffect(
                                                                                        new PlayoutDecisionEffect(subAction, _performingPlayerId,
                                                                                                new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getCardLink(_starshipToRelease) + " aboard " + GameUtils.getCardLink(launchPoint), seatChoices) {
                                                                                                    @Override
                                                                                                    protected void validDecisionMade(int index, String result) {
                                                                                                        boolean rallyAsStarfighter = (index == 0);

                                                                                                        // Capacity slot chosen, release starship.
                                                                                                        subAction.appendEffect(
                                                                                                                new ReleaseWithLaunchEffect(subAction, _starshipToRelease, launchPoint, rallyAsStarfighter));
                                                                                                    }
                                                                                                }));
                                                                            } else {
                                                                                // If both capacity slots were not available, launch starship to available slot.
                                                                                subAction.appendEffect(
                                                                                        new ReleaseWithLaunchEffect(subAction, _starshipToRelease, launchPoint));
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                        );
                                                    }
                                                    else {
                                                        subAction.appendEffect(
                                                                new ReleaseWithEscapeEffect(subAction, _starshipToRelease));
                                                    }
                                                }
                                            }
                                    )
                            );
                        }
                    }
                }
        );

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
