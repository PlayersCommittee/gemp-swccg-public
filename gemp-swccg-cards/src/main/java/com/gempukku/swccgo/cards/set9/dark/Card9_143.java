package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractSector;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringAttemptToBlowAwayDeathStarII;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.evaluators.ManeuverEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawMovementDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalMovementDestinyModifier;
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
 * Title: Death Star II: Capacitors
 */
public class Card9_143 extends AbstractSector {
    public Card9_143() {
        super(Side.DARK, Title.Capacitors, Title.Death_Star_II);
        setLocationDarkSideGameText("Deploys only if Coolant Shaft on table. While That Thing's Operational on table, you may add one battle destiny during battles at Death Star II system and system it orbits.");
        setLocationLightSideGameText("When your starship moves from here, draw movement destiny. Add maneuver. If total destiny < 2, starship is lost.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.DEATH_STAR_II, Icon.MOBILE);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.canSpotFromTopLocationsOnTable(game, Filters.Coolant_Shaft);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerOnDarkSideOfLocation, gameTextSourceCardId)
                && GameConditions.canSpot(game, self, Filters.That_Things_Operational)
                && GameConditions.isDuringBattleAt(game, Filters.or(Filters.Death_Star_II_system, Filters.isOrbitedBy(Filters.Death_Star_II_system)))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId);
            action.setText("Add one battle destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new AddBattleDestinyEffect(action, 1));
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
                            if (totalDestiny < 2) {
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