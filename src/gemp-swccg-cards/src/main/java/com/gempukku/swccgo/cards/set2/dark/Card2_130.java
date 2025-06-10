package com.gempukku.swccgo.cards.set2.dark;

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
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.actions.CommencePrimaryIgnitionState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayEpicEventAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.BlowAwayEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardToLoseFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.CalculatingEpicEventTotalResult;
import com.gempukku.swccgo.logic.timing.results.FiredWeaponResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Epic Event
 * Title: Commence Primary Ignition
 */
public class Card2_130 extends AbstractEpicEventPlayable {
    public Card2_130() {
        super(Side.DARK, Title.Commence_Primary_Ignition, ExpansionSet.A_NEW_HOPE, Rarity.R2);
        setGameText("During your control phase, fire superlaser as follows: Name the System: Use X Force to target a planet system Death Star is orbiting. You May Fire When Ready: Draw destiny. Stand By: If (destiny + Y - Z) > 8, target system is 'blown away' and this card is lost. Otherwise, this card is used and one Death Star Gunner on table is lost (your choice). X = total sites at target. Y = total Death Star sites where opponent has no presence. Z = opponent's choice of X or total sites at one Rebel Base (Yavin 4 or Hoth).");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<PlayEpicEventAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)
                && GameConditions.canDrawDestiny(game, playerId)) {
            final PhysicalCard planetSystem = Filters.findFirstFromTopLocationsOnTable(game, Filters.and(Filters.planet_system, Filters.isOrbitedBy(Filters.Death_Star_system), Filters.canBeTargetedBy(self)));
            if (planetSystem != null) {
                final GameState gameState = game.getGameState();
                final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                final float valueForX = modifiersQuerying.getVariableValue(gameState, self, Variable.X, Filters.countTopLocationsOnTable(game, Filters.and(Filters.site, Filters.notIgnoredDuringEpicEventCalculation(true), Filters.partOfSystem(planetSystem.getTitle()))));
                if (GameConditions.canUseForce(game, playerId, valueForX)) {
                    final PhysicalCard superlaser = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.superlaserThatCanFireAtPlanetSystem(planetSystem)));
                    if (superlaser != null) {
                        final PhysicalCard deathStar = superlaser.getAttachedTo();
                        final CommencePrimaryIgnitionState epicEventState = new CommencePrimaryIgnitionState(self);

                        final PlayEpicEventAction action = new PlayEpicEventAction(self);
                        action.setText("Attempt to 'blow away' " + GameUtils.getFullName(planetSystem));
                        action.setEpicEventState(epicEventState);
                        // Choose target(s)
                        action.appendTargeting(
                                new PassthruEffect(action) {
                                    @Override
                                    protected void doPlayEffect(final SwccgGame game) {
                                        // 1) Name The System
                                        // Update usage limit(s)
                                        action.appendUsage(
                                                new UseWeaponEffect(action, deathStar, superlaser));
                                        action.addAnimationGroup(superlaser);
                                        action.addAnimationGroup(planetSystem);
                                        // Update Epic Event State
                                        epicEventState.setSuperlaser(superlaser);
                                        epicEventState.setPlanetSystem(planetSystem);
                                        String actionText = "Have " + GameUtils.getCardLink(deathStar) + " fire " + GameUtils.getCardLink(superlaser) + " at " + GameUtils.getCardLink(planetSystem);
                                        // Pay cost(s)
                                        action.appendCost(
                                                new UseForceEffect(action, playerId, valueForX));
                                        // Allow response(s)
                                        action.allowResponses(actionText,
                                                new RespondablePlayCardEffect(action) {
                                                    @Override
                                                    protected void performActionResults(Action targetingAction) {
                                                        // Begin weapon firing
                                                        action.appendEffect(
                                                                new PassthruEffect(action) {
                                                                    @Override
                                                                    protected void doPlayEffect(SwccgGame game) {
                                                                        gameState.sendMessage(GameUtils.getCardLink(deathStar) + " fires " + GameUtils.getCardLink(superlaser) + " at " + GameUtils.getCardLink(planetSystem));
                                                                        gameState.activatedCard(playerId, superlaser);
                                                                        gameState.cardAffectsCard(playerId, superlaser, planetSystem);
                                                                        gameState.beginWeaponFiring(superlaser, null);
                                                                        gameState.getWeaponFiringState().setCardFiringWeapon(deathStar);
                                                                        gameState.getWeaponFiringState().setTarget(planetSystem);
                                                                        // Finish weapon firing
                                                                        action.appendAfterEffect(
                                                                                new PassthruEffect(action) {
                                                                                    @Override
                                                                                    protected void doPlayEffect(SwccgGame game) {
                                                                                        game.getActionsEnvironment().emitEffectResult(new FiredWeaponResult(game, superlaser, null, self, false, Collections.singletonList(planetSystem)));
                                                                                        gameState.finishWeaponFiring();
                                                                                    }
                                                                                }
                                                                        );
                                                                    }
                                                                }
                                                        );
                                                        // 2) You May Fire When Ready
                                                        action.appendEffect(
                                                                new DrawDestinyEffect(action, playerId, 1, DestinyType.EPIC_EVENT_AND_WEAPON_DESTINY) {
                                                                    @Override
                                                                    protected void destinyDraws(final SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float totalDestiny) {
                                                                        // 3) Stand By
                                                                        // Emit effect result that Attack Run total is being calculated
                                                                        action.appendEffect(
                                                                                new TriggeringResultEffect(action, new CalculatingEpicEventTotalResult(playerId, self)));
                                                                        action.appendEffect(
                                                                                new PassthruEffect(action) {
                                                                                    @Override
                                                                                    protected void doPlayEffect(final SwccgGame game) {
                                                                                        gameState.sendMessage("Destiny: " + (totalDestiny != null ? GuiUtils.formatAsString(totalDestiny) : "Failed destiny draw"));

                                                                                        final float valueForY = modifiersQuerying.getVariableValue(gameState, self, Variable.Y, Filters.countTopLocationsOnTable(game,
                                                                                                Filters.and(Filters.Death_Star_site, Filters.notIgnoredDuringEpicEventCalculation(true), Filters.not(Filters.occupies(opponent)))));

                                                                                        gameState.sendMessage("X: " + GuiUtils.formatAsString(valueForX));
                                                                                        gameState.sendMessage("Y: " + GuiUtils.formatAsString(valueForY));

                                                                                        final int totalHothSites = Filters.countTopLocationsOnTable(game, Filters.and(Filters.Hoth_site, Filters.notIgnoredDuringEpicEventCalculation(true)));
                                                                                        final int totalYavin4Sites = Filters.countTopLocationsOnTable(game, Filters.and(Filters.Yavin_4_site, Filters.notIgnoredDuringEpicEventCalculation(true)));
                                                                                        if (valueForX == totalHothSites && valueForX == totalYavin4Sites) {
                                                                                            gameState.sendMessage(GuiUtils.formatAsString(valueForX) + " is the only choice as value for Z");
                                                                                            float valueForZ = modifiersQuerying.getVariableValue(gameState, self, Variable.Z, valueForX);
                                                                                            gameState.sendMessage("Z: " + GuiUtils.formatAsString(valueForZ));
                                                                                            float total = modifiersQuerying.getEpicEventCalculationTotal(gameState, self, (totalDestiny != null ? totalDestiny : 0) + valueForY - valueForZ);
                                                                                            gameState.sendMessage("Epic Event Total: " + GuiUtils.formatAsString(total));

                                                                                            if (total > 8) {
                                                                                                gameState.sendMessage("Result: Succeeded");
                                                                                                action.appendEffect(
                                                                                                        new BlowAwayEffect(action, planetSystem, true));
                                                                                                action.appendEffect(
                                                                                                        new PutCardFromVoidInLostPileEffect(action, playerId, self));
                                                                                            } else {
                                                                                                gameState.sendMessage("Result: Failed");
                                                                                                action.appendEffect(
                                                                                                        new PutCardFromVoidInUsedPileEffect(action, playerId, self));
                                                                                                if (GameConditions.canSpot(game, self, Filters.Death_Star_Gunner)) {
                                                                                                    action.appendEffect(
                                                                                                            new ChooseCardToLoseFromTableEffect(action, playerId, Filters.Death_Star_Gunner));
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                        else {
                                                                                            action.appendEffect(
                                                                                                    new PlayoutDecisionEffect(action, opponent,
                                                                                                            new MultipleChoiceAwaitingDecision("Choose value for Z", new String[]{"X: " + valueForX, "Total sites at Hoth: " + totalHothSites, "Total sites at Yavin 4: " + totalYavin4Sites}) {
                                                                                                                @Override
                                                                                                                protected void validDecisionMade(int index, String result) {
                                                                                                                    float valueForZ;
                                                                                                                    if (index == 0) {
                                                                                                                        valueForZ = valueForX;
                                                                                                                    } else if (index == 1) {
                                                                                                                        valueForZ = totalHothSites;
                                                                                                                    } else {
                                                                                                                        valueForZ = totalYavin4Sites;
                                                                                                                    }
                                                                                                                    gameState.sendMessage(opponent + " chooses " + GuiUtils.formatAsString(valueForZ) + " as value for Z");
                                                                                                                    gameState.sendMessage("Z: " + GuiUtils.formatAsString(valueForZ));
                                                                                                                    float total = modifiersQuerying.getEpicEventCalculationTotal(gameState, self, (totalDestiny != null ? totalDestiny : 0) + valueForY - valueForZ);
                                                                                                                    gameState.sendMessage("Epic Event Total: " + GuiUtils.formatAsString(total));

                                                                                                                    if (total > 8) {
                                                                                                                        gameState.sendMessage("Result: Succeeded");
                                                                                                                        action.appendEffect(
                                                                                                                                new BlowAwayEffect(action, planetSystem, true));
                                                                                                                        action.appendEffect(
                                                                                                                                new PutCardFromVoidInLostPileEffect(action, playerId, self));
                                                                                                                    } else {
                                                                                                                        gameState.sendMessage("Result: Failed");
                                                                                                                        action.appendEffect(
                                                                                                                                new PutCardFromVoidInUsedPileEffect(action, playerId, self));
                                                                                                                        if (GameConditions.canSpot(game, self, Filters.Death_Star_Gunner)) {
                                                                                                                            action.appendEffect(
                                                                                                                                    new ChooseCardToLoseFromTableEffect(action, playerId, Filters.Death_Star_Gunner));
                                                                                                                        }
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                    )
                                                                                            );
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
        }
        return null;
    }
}