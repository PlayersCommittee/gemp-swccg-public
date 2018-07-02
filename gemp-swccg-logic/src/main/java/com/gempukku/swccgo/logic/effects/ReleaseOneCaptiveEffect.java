package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.CardCategory;
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
 * An effect that carries out the capturing of a single character on table.
 */
class ReleaseOneCaptiveEffect extends AbstractSubActionEffect {
    private String _performingPlayerId;
    private PhysicalCard _captiveToRelease;

    /**
     * Creates an effect that carries out the releasing of a single captive on table.
     * @param action the action performing this effect
     * @param captiveToRelease the captive to release
     */
    public ReleaseOneCaptiveEffect(Action action, PhysicalCard captiveToRelease) {
        super(action);
        _performingPlayerId = captiveToRelease.getOwner();
        _captiveToRelease = captiveToRelease;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        final PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsAt(gameState, _captiveToRelease);
        final boolean isImprisonedOrFrozen = _captiveToRelease.isImprisoned() || _captiveToRelease.isFrozen();

        final SubAction subAction = new SubAction(_action);

        //
        // 1) Determine which options are available: 'Escape' or 'Rally'
        //
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Check for valid places for captive to 'Rally' to
                        final List<PhysicalCard> validRallyPoints = new ArrayList<PhysicalCard>();
                        if (location != null && Filters.site.accepts(gameState, modifiersQuerying, location)) {
                            validRallyPoints.add(location);
                            // Check if any ships or vehicles at location have capacity for captive to rally to
                            validRallyPoints.addAll(Filters.filterActive(game, null, Filters.and(Filters.owner(_captiveToRelease.getOwner()),
                                    Filters.or(Filters.starship, Filters.vehicle), Filters.at(location),
                                    Filters.or(Filters.hasAvailablePilotCapacity(_captiveToRelease), Filters.hasAvailablePassengerCapacity(_captiveToRelease)))));
                        }

                        if (validRallyPoints.isEmpty()) {
                            // Release with 'Escape' is only option
                            subAction.appendEffect(
                                    new ReleaseWithEscapeEffect(subAction, _captiveToRelease));
                        }
                        else if (isImprisonedOrFrozen) {
                            // Release with 'Rally' is only option

                            // Choose where the captive will rally to
                            subAction.appendEffect(
                                    new ChooseCardOnTableEffect(subAction, _performingPlayerId, "Choose where to rally " + GameUtils.getCardLink(_captiveToRelease), validRallyPoints) {
                                        @Override
                                        protected void cardSelected(final PhysicalCard rallyPoint) {

                                            // Check if rallying to location
                                            if (rallyPoint.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
                                                subAction.appendEffect(
                                                        new ReleaseWithRallyEffect(subAction, _captiveToRelease, rallyPoint, false));
                                            }
                                            else {
                                                // Need to determine capacity slot for character
                                                boolean canBePilot = Filters.hasAvailablePilotCapacity(_captiveToRelease).accepts(gameState, modifiersQuerying, rallyPoint);
                                                boolean canBePassenger = Filters.hasAvailablePassengerCapacity(_captiveToRelease).accepts(gameState, modifiersQuerying, rallyPoint);

                                                if (canBePilot && canBePassenger) {
                                                    String[] seatChoices;
                                                    if (Filters.transport_vehicle.accepts(gameState, modifiersQuerying, rallyPoint))
                                                        seatChoices = new String[]{"Driver", "Passenger"};
                                                    else
                                                        seatChoices = new String[]{"Pilot", "Passenger"};

                                                    // Ask player to choose pilot/driver or passenger capacity slot
                                                    subAction.appendEffect(
                                                            new PlayoutDecisionEffect(subAction, _performingPlayerId,
                                                                    new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getCardLink(_captiveToRelease) + " aboard " + GameUtils.getCardLink(rallyPoint), seatChoices) {
                                                                        @Override
                                                                        protected void validDecisionMade(int index, String result) {
                                                                            boolean rallyAsPilot = (index == 0);

                                                                            // Capacity slot chosen, release character.
                                                                            subAction.appendEffect(
                                                                                    new ReleaseWithRallyEffect(subAction, _captiveToRelease, rallyPoint, rallyAsPilot));
                                                                        }
                                                                    }));
                                                } else {
                                                    // If both capacity slots were not available, rally character to available slot.
                                                    subAction.appendEffect(
                                                            new ReleaseWithRallyEffect(subAction, _captiveToRelease, rallyPoint, canBePilot));
                                                }
                                            }
                                        }
                                    }
                            );
                        }
                        else {
                            List<String> choices = new ArrayList<String>();
                            choices.add(ReleaseOption.ESCAPE.getHumanReadable());
                            choices.add(ReleaseOption.RALLY.getHumanReadable());
                            String[] choiceArray = new String[choices.size()];
                            choices.toArray(choiceArray);

                            subAction.appendEffect(
                                    new PlayoutDecisionEffect(subAction, _performingPlayerId,
                                            new MultipleChoiceAwaitingDecision("Choose release option for " + GameUtils.getCardLink(_captiveToRelease), choiceArray) {
                                                @Override
                                                protected void validDecisionMade(int index, String result) {
                                                    if (result.equals(ReleaseOption.RALLY.getHumanReadable())) {
                                                        // Choose where the captive will rally to
                                                        subAction.appendEffect(
                                                                new ChooseCardOnTableEffect(subAction, _performingPlayerId, "Choose where to rally " + GameUtils.getCardLink(_captiveToRelease), validRallyPoints) {
                                                                    @Override
                                                                    protected void cardSelected(final PhysicalCard rallyPoint) {

                                                                        // Check if rallying to location
                                                                        if (rallyPoint.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
                                                                            subAction.appendEffect(
                                                                                    new ReleaseWithRallyEffect(subAction, _captiveToRelease, rallyPoint, false));
                                                                        }
                                                                        else {
                                                                            // Need to determine capacity slot for character
                                                                            boolean canBePilot = Filters.hasAvailablePilotCapacity(_captiveToRelease).accepts(gameState, modifiersQuerying, rallyPoint);
                                                                            boolean canBePassenger = Filters.hasAvailablePassengerCapacity(_captiveToRelease).accepts(gameState, modifiersQuerying, rallyPoint);

                                                                            if (canBePilot && canBePassenger) {
                                                                                String[] seatChoices;
                                                                                if (Filters.transport_vehicle.accepts(gameState, modifiersQuerying, rallyPoint))
                                                                                    seatChoices = new String[]{"Driver", "Passenger"};
                                                                                else
                                                                                    seatChoices = new String[]{"Pilot", "Passenger"};

                                                                                // Ask player to choose pilot/driver or passenger capacity slot
                                                                                subAction.appendEffect(
                                                                                        new PlayoutDecisionEffect(subAction, _performingPlayerId,
                                                                                                new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getCardLink(_captiveToRelease) + " aboard " + GameUtils.getCardLink(rallyPoint), seatChoices) {
                                                                                                    @Override
                                                                                                    protected void validDecisionMade(int index, String result) {
                                                                                                        boolean rallyAsPilot = (index == 0);

                                                                                                        // Capacity slot chosen, release character.
                                                                                                        subAction.appendEffect(
                                                                                                                new ReleaseWithRallyEffect(subAction, _captiveToRelease, rallyPoint, rallyAsPilot));
                                                                                                    }
                                                                                                }));
                                                                            } else {
                                                                                // If both capacity slots were not available, rally character to available slot.
                                                                                subAction.appendEffect(
                                                                                        new ReleaseWithRallyEffect(subAction, _captiveToRelease, rallyPoint, canBePilot));
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                        );
                                                    }
                                                    else {
                                                        subAction.appendEffect(
                                                                new ReleaseWithEscapeEffect(subAction, _captiveToRelease));
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
