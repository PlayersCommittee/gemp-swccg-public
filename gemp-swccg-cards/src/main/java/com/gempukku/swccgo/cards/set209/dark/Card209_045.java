package com.gempukku.swccgo.cards.set209.dark;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.UseWeaponEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.actions.CommencePrimaryIgnitionV9State;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.*;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.*;
import com.gempukku.swccgo.logic.timing.results.CalculatingEpicEventTotalResult;

import java.util.*;

/**
 * Set: Set 9
 * Type: Epic Event
 * Title: Commence Primary Ignition (V)
 */
public class Card209_045 extends AbstractEpicEventDeployable {
    public Card209_045() {
        super(Side.DARK, PlayCardZoneOption.ATTACHED, Title.Commence_Primary_Ignition);
        setVirtualSuffix(true);
        setGameText("Once per game, deploy on Death Star; it is hyperspeed = 2. Superlaser may not target planet systems. Once during your control phase, may fire Superlaser as follows: Prepare Single Reactor Ignition: If Death Star orbiting Jedha or Scarif, target your related unique (•) battleground site, even if converted (regardless of objective restrictions). Fire!: Draw destiny. It's Beautiful: If destiny +X > 8, site is 'blown away,' and opponent loses 3 Force. X = number of Death Star sites on table.");
        addIcons(Icon.A_NEW_HOPE, Icon.VIRTUAL_SET_9);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Death_Star_system;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetHyperspeedModifier(self, Filters.Death_Star_system, 2));
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.planet_system, Filters.or(Filters.Superlaser, Filters.Commence_Primary_Ignition)));
        modifiers.add(new MayNotDeployModifier(self, Filters.and(Filters.Commence_Primary_Ignition, Icon.VIRTUAL_SET_9), playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelEpicEventGameTextAction> getEpicEventGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);
        final GameState gameState = game.getGameState();
        Filter systemFilter = Filters.and(Filters.system, Filters.isOrbitedBy(Filters.Death_Star_system), Filters.or(Filters.title(Title.Jedha), Filters.title(Title.Scarif)));

        final Filter yourSiteEvenIfConverted = Filters.and(Filters.your(self), Filters.unique, Filters.battleground_site,
                Filters.relatedSiteTo(self, Filters.and(Filters.system, Filters.isOrbitedBy(Filters.Death_Star_system))));

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canDrawDestiny(game, playerId)
                && GameConditions.canSpotLocation(game, systemFilter)
                && GameConditions.canTarget(game, self, Filters.Superlaser)
                && GameConditions.canTarget(game, self, yourSiteEvenIfConverted)) {
            final CommencePrimaryIgnitionV9State epicEventState = new CommencePrimaryIgnitionV9State(self);
            final TopLevelEpicEventGameTextAction action = new TopLevelEpicEventGameTextAction(self, gameTextSourceCardId);
            final PhysicalCard superlaser = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Superlaser));
            final PhysicalCard deathStar = superlaser.getAttachedTo();

            action.setText("Attempt to 'blow away' site");
            action.setEpicEventState(epicEventState);
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));

            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose site to target", yourSiteEvenIfConverted) {
                        @Override
                        protected void cardSelected(final PhysicalCard targetedSite) {

                            action.addAnimationGroup(superlaser);
                            action.addAnimationGroup(targetedSite);
                            epicEventState.setSuperlaser(superlaser);
                            epicEventState.setSite(targetedSite);
                            String actionText = "Have " + GameUtils.getCardLink(deathStar) + " fire " + GameUtils.getCardLink(superlaser) + " at " + GameUtils.getCardLink(targetedSite);
                            // Update usage limit(s)
                            action.appendUsage(
                                    new UseWeaponEffect(action, deathStar, superlaser));
                            // Allow response(s)
                            action.allowResponses(actionText,
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                                            action.appendEffect(
                                                    new FireWeaponEffect(action, superlaser, false, Filters.sameCardId(targetedSite)));
                                            // 2) Fire!
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId, 1, DestinyType.EPIC_EVENT_AND_WEAPON_DESTINY) {
                                                        @Override
                                                        protected void destinyDraws(final SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float totalDestiny) {
                                                            // 3) It's Beautiful
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }
                                                            gameState.sendMessage("Destiny Draw: " + GuiUtils.formatAsString(totalDestiny));

                                                            // Emit effect result that total is being calculated
                                                            action.appendEffect(
                                                                    new TriggeringResultEffect(action, new CalculatingEpicEventTotalResult(playerId, self)));

                                                            action.appendEffect(
                                                                    new PassthruEffect(action) {
                                                                        @Override
                                                                        protected void doPlayEffect(SwccgGame game) {
                                                                            final float initialEpicEventTotal = modifiersQuerying.getEpicEventCalculationTotal(gameState, self, totalDestiny);
                                                                            gameState.sendMessage("Total Destiny: " + GuiUtils.formatAsString(initialEpicEventTotal));

                                                                            final float valueForX = modifiersQuerying.getVariableValue(gameState, self, Variable.X, Filters.countTopLocationsOnTable(game, Filters.and(Filters.Death_Star_site, Filters.notIgnoredDuringEpicEventCalculation)));
                                                                            gameState.sendMessage("X: " + GuiUtils.formatAsString(valueForX));

                                                                            //compute total
                                                                            float finalEpicEventTotal = modifiersQuerying.getEpicEventCalculationTotal(gameState, self, totalDestiny + valueForX);
                                                                            gameState.sendMessage("Epic Event Total: " + GuiUtils.formatAsString(finalEpicEventTotal));

                                                                            if (finalEpicEventTotal > 8) {
                                                                                gameState.sendMessage("Result: Succeeded");
                                                                                action.appendEffect(
                                                                                        new BlowAwayEffect(action, targetedSite, true));
                                                                                action.appendEffect(
                                                                                        new LoseForceEffect(action, opponent, 3));
                                                                            } else {
                                                                                gameState.sendMessage("Result: Failed");
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
        return null;
    }

}
