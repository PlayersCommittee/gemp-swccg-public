package com.gempukku.swccgo.cards.set222.dark;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.UseWeaponEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.TargetTheMainGeneratorState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextEpicEventTriggerAction;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.BlowAwayEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.MovedResult;
import com.gempukku.swccgo.logic.timing.results.MovingResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 22
 * Type: Epic Event
 * Title: Target The Main Generator (V)
 */
public class Card222_013 extends AbstractEpicEventDeployable {
    public Card222_013() {
        super(Side.DARK, PlayCardZoneOption.ATTACHED, Title.Target_The_Main_Generator, Uniqueness.UNRESTRICTED, ExpansionSet.SET_22, Rarity.V);
        setVirtualSuffix(true);
        setGameText("Deploy on Ice Plains. " +
                "Once per turn, a player who controls this site may have this card follow their vehicle moving from here to an adjacent marker site. " +
                "At the start of your deploy phase, if at 3rd Marker or lower, your AT-AT Cannon here may fire (if within range of Main Power Generators) as follows: Draw destiny. " +
                "Add 1 for each marker site you occupy (2 if you control). If total destiny > 8, Main Power Generators 'blown away.'");
        addIcons(Icon.HOTH, Icon.VIRTUAL_SET_22);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Ice_Plains;
    }

    private List<OptionalGameTextTriggerAction> followVehicleMovingFromHere(String playerMoving, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        Filter vehiclesMoving = Filters.and(Filters.vehicle, Filters.owner(playerMoving));
        Filter sameSite = Filters.sameSite(self);
        Filter adjacentMarkerSite = Filters.and(Filters.adjacentSite(self), Filters.marker_site);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (TriggerConditions.movingFromLocation(game, effectResult, vehiclesMoving, sameSite)
                && TriggerConditions.movingToLocation(game, effectResult, vehiclesMoving, adjacentMarkerSite)
                && GameConditions.controls(game, playerMoving, sameSite)) {
            MovingResult movingResult = (MovingResult) effectResult;
            self.setWhileInPlayData(new WhileInPlayData(movingResult.getCardMoving()));
        }

        if (GameConditions.cardHasWhileInPlayDataSet(self)) {
            PhysicalCard cardMoved = self.getWhileInPlayData().getPhysicalCard();
            if (cardMoved != null
                    && TriggerConditions.moved(game, effectResult, playerMoving, cardMoved)
                    && GameConditions.isOncePerTurn(game, self, playerMoving, gameTextSourceCardId, gameTextActionId)
                    && GameConditions.canSpot(game, self, cardMoved)) {
                // Check condition(s)
                MovedResult movedResult = (MovedResult) effectResult;

                if (movedResult.isMoveComplete()
                        && adjacentMarkerSite.accepts(game, movedResult.getMovedTo())) {
                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerMoving, gameTextSourceCardId, gameTextActionId);
                    action.setText("Follow vehicle moving from same site");
                    action.setActionMsg("Have " + GameUtils.getCardLink(self) + " follow " + GameUtils.getCardLink(movedResult.getMovedCards().iterator().next()));
                    // Perform result(s)
                    action.appendUsage(
                            new OncePerTurnEffect(action));
                    action.appendEffect(
                            new AttachCardFromTableEffect(action, self, movedResult.getMovedTo()));
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();
        final int numDestiny = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.TARGET_THE_MAIN_GENERATOR__ADDS_ONE_DESTINY) ? 2 : 1;


        List<OptionalGameTextTriggerAction> followActions = followVehicleMovingFromHere(playerId, game, effectResult, self, gameTextSourceCardId);
        if (followActions != null)
            actions.addAll(followActions);


        if (TriggerConditions.isStartOfYourPhase(game, effectResult, Phase.DEPLOY, playerId)
                && GameConditions.isAtLocation(game, self, Filters.or(Filters.First_Marker, Filters.Second_Marker, Filters.Third_Marker))) {

            final PhysicalCard mainPowerGenerators = Filters.findFirstFromTopLocationsOnTable(game, Filters.Main_Power_Generators);
            if (mainPowerGenerators != null) {
                Filter weaponToFireFilter = Filters.and(Filters.your(self), Filters.here(self), Filters.AT_AT_Cannon, Filters.attachedTo(Filters.and(Filters.AT_AT, Filters.piloted)), Filters.canBeFiredAtLocationInRange(mainPowerGenerators));
                if (GameConditions.canSpot(game, self, weaponToFireFilter)) {
                    final TargetTheMainGeneratorState epicEventState = new TargetTheMainGeneratorState(self);

                    final OptionalGameTextEpicEventTriggerAction action = new OptionalGameTextEpicEventTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_2, epicEventState);
                    action.setText("Attempt to 'blow away' Main Power Generators");
                    // Choose target(s)
                    action.appendTargeting(
                            new ChooseCardOnTableEffect(action, playerId, "Choose AT-AT Cannon", weaponToFireFilter) {
                                @Override
                                protected void cardSelected(final PhysicalCard weapon) {
                                    final PhysicalCard atat = weapon.getAttachedTo();

                                    // Update usage limit(s)
                                    action.appendUsage(
                                            new UseWeaponEffect(action, atat, weapon));
                                    action.addAnimationGroup(weapon);
                                    action.addAnimationGroup(mainPowerGenerators);
                                    // Update Epic Event State
                                    epicEventState.setAtat(atat);
                                    epicEventState.setAtatCannon(weapon);
                                    String actionText = "Fire " + GameUtils.getCardLink(weapon) + " at " + GameUtils.getCardLink(mainPowerGenerators);
                                    // Allow response(s)
                                    action.allowResponses(actionText,
                                            new RespondableEffect(action) {
                                                @Override
                                                protected void performActionResults(Action targetingAction) {
                                                    final GameState gameState = game.getGameState();
                                                    final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                    // Begin weapon firing
                                                    action.appendEffect(
                                                            new PassthruEffect(action) {
                                                                @Override
                                                                protected void doPlayEffect(SwccgGame game) {
                                                                    gameState.sendMessage(playerId + " fires " + GameUtils.getCardLink(weapon) + " at " + GameUtils.getCardLink(mainPowerGenerators));
                                                                    gameState.activatedCard(playerId, weapon);
                                                                    gameState.cardAffectsCard(playerId, weapon, mainPowerGenerators);
                                                                    gameState.beginWeaponFiring(weapon, null);
                                                                    gameState.getWeaponFiringState().setCardFiringWeapon(atat);
                                                                    gameState.getWeaponFiringState().setTarget(mainPowerGenerators);
                                                                    // Finish weapon firing
                                                                    action.appendAfterEffect(
                                                                            new PassthruEffect(action) {
                                                                                @Override
                                                                                protected void doPlayEffect(SwccgGame game) {
                                                                                    gameState.finishWeaponFiring();
                                                                                }
                                                                            }
                                                                    );
                                                                }
                                                            }
                                                    );
                                                    // 1) Prepare To Target The Main Generator
                                                    action.appendEffect(
                                                            new DrawDestinyEffect(action, playerId, numDestiny, DestinyType.EPIC_EVENT_AND_WEAPON_DESTINY) {
                                                                @Override
                                                                protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                    // 2) Maximum Firepower!
                                                                    gameState.sendMessage("Destiny: " + (totalDestiny != null ? GuiUtils.formatAsString(totalDestiny) : "Failed destiny draw"));

                                                                    if (totalDestiny == null) {
                                                                        gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                    } else {

                                                                        int markerSiteYouOccupyButNotControl = Filters.countTopLocationsOnTable(game, Filters.and(Filters.marker_site, Filters.occupies(playerId), Filters.not(Filters.controls(playerId))));
                                                                        int markerSiteYouControl = Filters.countTopLocationsOnTable(game, Filters.and(Filters.marker_site, Filters.controls(playerId)));


                                                                        float total = modifiersQuerying.getEpicEventCalculationTotal(gameState, self, (totalDestiny != null ? totalDestiny : 0) + markerSiteYouOccupyButNotControl + 2 * markerSiteYouControl);
                                                                        gameState.sendMessage("Epic Event Total: " + GuiUtils.formatAsString(total));

                                                                        if (total > 8) {
                                                                            gameState.sendMessage("Result: Succeeded");
                                                                            action.appendEffect(
                                                                                    new BlowAwayEffect(action, mainPowerGenerators));
                                                                        } else {
                                                                            gameState.sendMessage("Result: Failed");
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                    );
                                                }
                                            }
                                    );


                                }
                            }
                    );
                    actions.add(action);
                }
            }
        }


        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        return followVehicleMovingFromHere(playerId, game, effectResult, self, gameTextSourceCardId);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // reset at the beginning of each turn
        if (TriggerConditions.isStartOfEachTurn(game, effectResult)) {
            self.setWhileInPlayData(null);
        }
        return null;
    }

}
