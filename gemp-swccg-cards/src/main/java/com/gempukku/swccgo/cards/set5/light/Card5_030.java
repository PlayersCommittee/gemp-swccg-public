package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RelocateFromLostInSpaceOrWeatherVaneToStarshipOrVehicle;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.CaptureCharacterFromLostInSpaceOrWeatherVaneEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromOffTableSimultaneouslyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.RelocateToWeatherVaneResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Effect
 * Title: Weather Vane
 */
public class Card5_030 extends AbstractNormalEffect {
    public Card5_030() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Weather_Vane, Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.U);
        setLore("The metal rods extending from the bottom of Cloud City are part of the city's flotation system. Sensors detect the velocity of wind and the content of local clouds.");
        setGameText("Deploy on table. Any character here may be captured or rescued by a player's starship or vehicle controlling Cloud City during that player's control phase. Character here lost if new character arrives. Effect lost if Cloud City lost. (Immune to Alter.)");
        addIcons(Icon.CLOUD_CITY);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justRelocatedToWeatherVane(game, effectResult, Filters.character)) {
            PhysicalCard newCharacter = ((RelocateToWeatherVaneResult) effectResult).getCard();
            Collection<PhysicalCard> charactersToLose = Filters.filter(game.getGameState().getStackedCards(self), game, Filters.not(newCharacter));
            if (!charactersToLose.isEmpty()) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make character lost");
                action.setActionMsg("Make " + GameUtils.getAppendedNames(charactersToLose) + " lost");
                // Perform result(s)
                action.appendEffect(
                        new LoseCardsFromOffTableSimultaneouslyEffect(action, charactersToLose, false));
                return Collections.singletonList(action);
            }
        }

        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, Filters.Bespin_Cloud_City)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make lost");
            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");
            // Perform result(s)
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        return getRescueOrCaptureActions(playerId, game, self, gameTextSourceCardId);
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        return getRescueOrCaptureActions(playerId, game, self, gameTextSourceCardId);
    }

    /**
     * Gets the top-level actions to rescue or capture the character on the Weather Vane.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the actions
     */
    private List<TopLevelGameTextAction> getRescueOrCaptureActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.CONTROL)) {
            if (GameConditions.hasStackedCards(game, self, Filters.and(Filters.your(playerId), Filters.character))) {
                final PhysicalCard attachedCharacter = game.getGameState().getStackedCards(self).get(0);
                Filter starshipOrVehicleFilter = Filters.and(Filters.your(playerId), Filters.or(Filters.starship, Filters.vehicle),
                        Filters.piloted, Filters.at(Filters.and(Filters.Bespin_Cloud_City, Filters.controls(playerId))),
                        Filters.or(Filters.hasAvailablePilotCapacity(attachedCharacter), Filters.hasAvailablePassengerCapacity(attachedCharacter)));
                if (GameConditions.canSpot(game, self, starshipOrVehicleFilter)) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                    action.setText("Rescue " + GameUtils.getFullName(attachedCharacter));
                    // Choose target(s)
                    action.appendTargeting(
                            new ChooseCardOnTableEffect(action, playerId, "Choose starship or vehicle to rescue " + GameUtils.getCardLink(attachedCharacter), starshipOrVehicleFilter) {
                                @Override
                                protected void cardSelected(final PhysicalCard starshipOrVehicle) {
                                    action.addAnimationGroup(attachedCharacter);
                                    action.addAnimationGroup(starshipOrVehicle);
                                    // Need to determine capacity slot for character
                                    final boolean canBePilot = Filters.hasAvailablePilotCapacity(attachedCharacter).accepts(game, starshipOrVehicle);
                                    boolean canBePassenger = Filters.hasAvailablePassengerCapacity(attachedCharacter).accepts(game, starshipOrVehicle);

                                    if (canBePilot && canBePassenger) {
                                        String[] seatChoices;
                                        if (Filters.transport_vehicle.accepts(game, starshipOrVehicle))
                                            seatChoices = new String[]{"Driver", "Passenger"};
                                        else
                                            seatChoices = new String[]{"Pilot", "Passenger"};

                                        // Ask player to choose pilot/driver or passenger capacity slot
                                        action.appendTargeting(
                                                new PlayoutDecisionEffect(action, playerId,
                                                        new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getCardLink(attachedCharacter) + " aboard " + GameUtils.getCardLink(starshipOrVehicle), seatChoices) {
                                                            @Override
                                                            protected void validDecisionMade(int index, String result) {
                                                                final boolean asPilot = (index == 0);

                                                                // Allow response(s)
                                                                action.allowResponses("Have " + GameUtils.getCardLink(starshipOrVehicle) + " rescue " + GameUtils.getCardLink(attachedCharacter),
                                                                        new UnrespondableEffect(action) {
                                                                            @Override
                                                                            protected void performActionResults(Action targetingAction) {
                                                                                // Perform result(s)
                                                                                action.appendEffect(
                                                                                        new RelocateFromLostInSpaceOrWeatherVaneToStarshipOrVehicle(action, attachedCharacter, starshipOrVehicle, asPilot));
                                                                            }
                                                                        }
                                                                );
                                                            }
                                                        }
                                                )
                                        );
                                    }
                                    else {
                                        // Allow response(s)
                                        action.allowResponses("Have " + GameUtils.getCardLink(starshipOrVehicle) + " rescue " + GameUtils.getCardLink(attachedCharacter),
                                                new UnrespondableEffect(action) {
                                                    @Override
                                                    protected void performActionResults(Action targetingAction) {
                                                        // Perform result(s)
                                                        action.appendEffect(
                                                                new RelocateFromLostInSpaceOrWeatherVaneToStarshipOrVehicle(action, attachedCharacter, starshipOrVehicle, canBePilot));
                                                    }
                                                }
                                        );
                                    }
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
            // Check condition(s)
            if (playerId.equals(game.getDarkPlayer())
                    && GameConditions.hasStackedCards(game, self, Filters.and(Filters.opponents(playerId), Filters.character))) {
                final PhysicalCard attachedCharacter = game.getGameState().getStackedCards(self).get(0);
                Filter starshipOrVehicleFilter = Filters.and(Filters.your(playerId), Filters.or(Filters.starship, Filters.vehicle),
                        Filters.piloted, Filters.at(Filters.and(Filters.Bespin_Cloud_City, Filters.controls(playerId))));
                if (GameConditions.canSpot(game, self, starshipOrVehicleFilter)) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                    action.setText("Capture " + GameUtils.getFullName(attachedCharacter));
                    action.addAnimationGroup(attachedCharacter);
                    // Allow response(s)
                    action.allowResponses("Capture " + GameUtils.getCardLink(attachedCharacter),
                            new UnrespondableEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    final PhysicalCard cloudCity = Filters.findFirstFromTopLocationsOnTable(game, Filters.Bespin_Cloud_City);
                                    // Perform result(s)
                                    action.appendEffect(
                                            new CaptureCharacterFromLostInSpaceOrWeatherVaneEffect(action, attachedCharacter, cloudCity));
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}