package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractSector;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringAttemptToBlowAwayDeathStarII;
import com.gempukku.swccgo.cards.evaluators.ManeuverEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DrawMovementDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalMovementDestinyModifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.MovingResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Location
 * Subtype: Sector
 * Title: Death Star II: Coolant Shaft
 */
public class Card9_144 extends AbstractSector {
    public Card9_144() {
        super(Side.DARK, Title.Coolant_Shaft, Title.Death_Star_II, Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.C);
        setLocationDarkSideGameText("Deploys only if Death Star II system on table. Your TIEs at Death Star II locations may not Tallon Roll. Your non-Objective cards with 'Occupation' in title are canceled.");
        setLocationLightSideGameText("When your starship moves from here, draw movement destiny. Add maneuver. If total destiny < 1, starship is lost.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.DEATH_STAR_II, Icon.MOBILE);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.canSpotFromTopLocationsOnTable(game, Filters.Death_Star_II_system);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation),
                Filters.TIE, Filters.at(Filters.Death_Star_II_location)), Filters.Tallon_Roll));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredBeforeTriggers(String playerOnDarkSideOfLocation, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        Filter filter = Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.not(Filters.Objective), Filters.titleContains("Occupation"));

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, filter)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        Filter filter = Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.not(Filters.Objective), Filters.titleContains("Occupation"));

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, filter)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, filter, "non-Objective card with 'Occupation' in title");
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalMovementDestinyModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation),
                Filters.starship, Filters.here(self)), new ManeuverEvaluator()));
        // Only affect Dark side starships when escaping during attempt to 'blow away' Death Star II
        modifiers.add(new TotalMovementDestinyModifier(self, Filters.and(Filters.opponents(playerOnLightSideOfLocation),
                Filters.starship, Filters.here(self)), new DuringAttemptToBlowAwayDeathStarII(), new ManeuverEvaluator()));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        Filter starshipFilter = Filters.starship;
        // Only affect Dark side starships when escaping during attempt to 'blow away' Death Star II
        if (!GameConditions.isDuringAttemptToBlowAwayDeathStarII(game)) {
            starshipFilter = Filters.and(Filters.your(playerOnLightSideOfLocation), starshipFilter);
        }

        // Check condition(s)
        if (TriggerConditions.movingFromLocation(game, effectResult, starshipFilter, self)) {
            final PhysicalCard starship = ((MovingResult) effectResult).getCardMoving();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Draw movement destiny for " + GameUtils.getFullName(starship));
            action.setActionMsg("Draw movement destiny for " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new DrawMovementDestinyEffect(action, starship.getOwner(), starship) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            GameState gameState = game.getGameState();
                            if (totalDestiny == null) {
                                gameState.sendMessage("Result: Starship lost due to failed destiny draw");
                                action.appendEffect(
                                        new LoseCardFromTableEffect(action, starship));
                                return;
                            }

                            gameState.sendMessage("Total movement destiny: " + GuiUtils.formatAsString(totalDestiny));
                            if (totalDestiny < 1) {
                                gameState.sendMessage("Result: Starship lost");
                                action.appendEffect(
                                        new LoseCardFromTableEffect(action, starship));
                            }
                            else {
                                gameState.sendMessage("Result: Starship not lost");
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}