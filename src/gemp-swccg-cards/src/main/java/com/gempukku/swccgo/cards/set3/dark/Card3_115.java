package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractEpicEventPlayable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.UseWeaponEffect;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.TargetTheMainGeneratorState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayEpicEventAction;
import com.gempukku.swccgo.logic.effects.BlowAwayEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.List;


/**
 * Set: Hoth
 * Type: Epic Event
 * Title: Target The Main Generator
 */
public class Card3_115 extends AbstractEpicEventPlayable {
    public Card3_115() {
        super(Side.DARK, Title.Target_The_Main_Generator, ExpansionSet.HOTH, Rarity.R2);
        setGameText("During your control phase, fire your AT-AT Cannon (if within range of the Main Power Generators) as follows: Prepare To Target The Main Generator: Draw destiny. Maximum Firepower!: If (destiny + X + Y) > 8, Main Power Generators site is 'blown away' and this card is lost. Otherwise, this card is used. X = ability of one of your AT-AT's pilots. Y = total Hoth sites you control.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayEpicEventAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final int numDestiny = 1;

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)
                && GameConditions.canDrawDestiny(game, playerId)) {
            final PhysicalCard mainPowerGenerators = Filters.findFirstFromTopLocationsOnTable(game, Filters.Main_Power_Generators);
            if (mainPowerGenerators != null) {
                Filter weaponToFireFilter = Filters.and(Filters.your(self), Filters.AT_AT_Cannon, Filters.attachedTo(Filters.and(Filters.AT_AT, Filters.piloted)), Filters.canBeFiredAtLocationInRange(mainPowerGenerators));
                if (GameConditions.canSpot(game, self, weaponToFireFilter)) {
                    final TargetTheMainGeneratorState epicEventState = new TargetTheMainGeneratorState(self);

                    final PlayEpicEventAction action = new PlayEpicEventAction(self);
                    action.setText("Attempt to 'blow away' Main Power Generators");
                    action.setEpicEventState(epicEventState);
                    // Choose target(s)
                    action.appendTargeting(
                            new ChooseCardOnTableEffect(action, playerId, "Choose AT-AT Cannon", weaponToFireFilter) {
                                @Override
                                protected void cardSelected(final PhysicalCard weapon) {
                                    final PhysicalCard atat = weapon.getAttachedTo();
                                    Filter pilotFilter = Filters.or(Filters.and(atat, Filters.hasPermanentPilot), Filters.piloting(atat));
                                    action.appendTargeting(
                                            new ChooseCardOnTableEffect(action, playerId, "Choose AT-AT pilot", pilotFilter) {
                                                @Override
                                                protected void cardSelected(final PhysicalCard pilot) {
                                                    // Update usage limit(s)
                                                    action.appendUsage(
                                                            new UseWeaponEffect(action, atat, weapon));
                                                    action.addAnimationGroup(pilot);
                                                    action.addAnimationGroup(weapon);
                                                    action.addAnimationGroup(mainPowerGenerators);
                                                    // Update Epic Event State
                                                    epicEventState.setAtat(atat);
                                                    epicEventState.setAtatCannon(weapon);
                                                    epicEventState.setAtatPilot(pilot);
                                                    String actionText = "Have " + (Filters.character.accepts(game, pilot) ? GameUtils.getCardLink(pilot) : "Permanent pilot")
                                                            + " aboard " + GameUtils.getCardLink(atat) + " fire " + GameUtils.getCardLink(weapon) + " at " + GameUtils.getCardLink(mainPowerGenerators);
                                                    // Allow response(s)
                                                    action.allowResponses(actionText,
                                                            new RespondablePlayCardEffect(action) {
                                                                @Override
                                                                protected void performActionResults(Action targetingAction) {
                                                                    final GameState gameState = game.getGameState();
                                                                    final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                                    // Begin weapon firing
                                                                    action.appendEffect(
                                                                            new PassthruEffect(action) {
                                                                                @Override
                                                                                protected void doPlayEffect(SwccgGame game) {
                                                                                    gameState.sendMessage((Filters.character.accepts(game, pilot) ? GameUtils.getCardLink(pilot) : "Permanent pilot")
                                                                                            + " aboard " + GameUtils.getCardLink(atat) + " fires " + GameUtils.getCardLink(weapon) + " at " + GameUtils.getCardLink(mainPowerGenerators));
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

                                                                                    float valueForX;
                                                                                    if (Filters.character.accepts(game, pilot))
                                                                                        valueForX = modifiersQuerying.getVariableValue(gameState, self, Variable.X, modifiersQuerying.getAbility(gameState, pilot));
                                                                                    else
                                                                                        valueForX = modifiersQuerying.getVariableValue(gameState, self, Variable.X, modifiersQuerying.getHighestAbilityPiloting(gameState, pilot, true, false));

                                                                                    boolean modifyX = modifiersQuerying.hasGameTextModification(gameState, self, ModifyGameTextType.TARGET_THE_MAIN_GENERATOR__MODIFY_X);

                                                                                    if (modifyX) {
                                                                                        PhysicalCard location = modifiersQuerying.getLocationThatCardIsAt(gameState, weapon);
                                                                                        int markerNumber = 0;
                                                                                        if (Filters.First_Marker.accepts(gameState, modifiersQuerying, location)) {
                                                                                            markerNumber = 1;
                                                                                        } else if (Filters.Second_Marker.accepts(gameState, modifiersQuerying, location)) {
                                                                                            markerNumber = 2;
                                                                                        } else if (Filters.Third_Marker.accepts(gameState, modifiersQuerying, location)) {
                                                                                            markerNumber = 3;
                                                                                        } else if (Filters.Fourth_Marker.accepts(gameState, modifiersQuerying, location)) {
                                                                                            markerNumber = 4;
                                                                                        } else if (Filters.Fifth_Marker.accepts(gameState, modifiersQuerying, location)) {
                                                                                            markerNumber = 5;
                                                                                        } else if (Filters.Sixth_Marker.accepts(gameState, modifiersQuerying, location)) {
                                                                                            markerNumber = 6;
                                                                                        } else if (Filters.Seventh_Marker.accepts(gameState, modifiersQuerying, location)) {
                                                                                            markerNumber = 7;
                                                                                        }

                                                                                        if (markerNumber > 3) {
                                                                                            valueForX = valueForX - 2;
                                                                                        }

                                                                                        if (valueForX > 3) {
                                                                                            valueForX = 3;
                                                                                        }
                                                                                    }
                                                                                    float valueForY = modifiersQuerying.getVariableValue(gameState, self, Variable.Y, Filters.countTopLocationsOnTable(game,
                                                                                            Filters.and(Filters.Hoth_site, Filters.notIgnoredDuringEpicEventCalculation(true), Filters.controls(playerId))));

                                                                                    gameState.sendMessage("X: " + GuiUtils.formatAsString(valueForX));
                                                                                    gameState.sendMessage("Y: " + GuiUtils.formatAsString(valueForY));

                                                                                    float total = modifiersQuerying.getEpicEventCalculationTotal(gameState, self, (totalDestiny != null ? totalDestiny : 0) + valueForX + valueForY);
                                                                                    gameState.sendMessage("Epic Event Total: " + GuiUtils.formatAsString(total));

                                                                                    if (total > 8) {
                                                                                        gameState.sendMessage("Result: Succeeded");
                                                                                        action.appendEffect(
                                                                                                new BlowAwayEffect(action, mainPowerGenerators));
                                                                                        action.appendEffect(
                                                                                                new PutCardFromVoidInLostPileEffect(action, playerId, self));
                                                                                    }
                                                                                    else {
                                                                                        gameState.sendMessage("Result: Failed");
                                                                                        action.appendEffect(
                                                                                                new PutCardFromVoidInUsedPileEffect(action, playerId, self));
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