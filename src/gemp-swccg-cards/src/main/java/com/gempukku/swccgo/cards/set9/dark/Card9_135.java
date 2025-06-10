package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.evaluators.AddEvaluator;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
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
import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.AttemptToBlowAwayDeathStarIIState;
import com.gempukku.swccgo.game.state.BlowAwayState;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelEpicEventGameTextAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.BlowAwayEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.EscapeDeathStarIIEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.BlownAwayForceLossMultiplierModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ResetHyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.TotalDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Epic Event
 * Title: That Thing's Operational
 */
public class Card9_135 extends AbstractEpicEventDeployable {
    public Card9_135() {
        super(Side.DARK, PlayCardZoneOption.ATTACHED, Title.That_Things_Operational, Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.R);
        setGameText("Deploy on Reactor Core. Death Star II may move (hyperspeed = 3). " +
                "While Death Star II orbits a system you occupy (except Endor), once during each of your control phases, " +
                "opponent loses X Force, where X = 2 plus number of related battleground sites you occupy. " +
                "Once during each of opponent's control phases, opponent's piloted starfighter here may attempt to 'blow away' Death Star II. " +
                "Draw destiny. Add 3 if armed with torpedoes or missiles. Add pilot's ability. If total destiny > 8, " +
                "starships here may attempt to 'escape' and Death Star II is 'blown away' (lose double Force).");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Reactor_Core;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetHyperspeedModifier(self, Filters.Death_Star_II_system, 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        String opponent = game.getOpponent(playerId);
        Filter systemFilter = Filters.and(Filters.system, Filters.except(Filters.Endor_system), Filters.isOrbitedBy(Filters.Death_Star_II_system), Filters.occupies(playerId));

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canSpotLocation(game, systemFilter)) {
            GameState gameState = game.getGameState();
            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
            int numSites = Filters.countTopLocationsOnTable(game, Filters.and(Filters.battleground_site, Filters.notIgnoredDuringEpicEventCalculation(false), Filters.occupies(playerId), Filters.relatedSiteTo(self, systemFilter)));
            float numForce = modifiersQuerying.getVariableValue(gameState, self, Variable.X, 2 + numSites);
            if (numForce > 0) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Make opponent lose " + GuiUtils.formatAsString(numForce) + " Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, opponent, numForce));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;
        Filter starfighterFilter = Filters.and(Filters.your(playerId), Filters.piloted, Filters.starfighter, Filters.here(self));
        final PhysicalCard reactorCore = self.getAttachedTo();

        // Check condition(s)
        if (reactorCore != null
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canDrawDestiny(game, playerId)
                && GameConditions.canSpot(game, self, starfighterFilter)) {
            final AttemptToBlowAwayDeathStarIIState epicEventState = new AttemptToBlowAwayDeathStarIIState(self);

            final TopLevelEpicEventGameTextAction action = new TopLevelEpicEventGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Attempt to 'blow away' Death Star II");
            action.setEpicEventState(epicEventState);
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose starfighter", starfighterFilter) {
                        @Override
                        protected void cardSelected(final PhysicalCard starfighter) {
                            Filter pilotFilter = Filters.or(Filters.and(starfighter, Filters.hasPermanentPilot), Filters.piloting(starfighter));
                            action.appendTargeting(
                                    new ChooseCardOnTableEffect(action, playerId, "Choose starfighter pilot", pilotFilter) {
                                        @Override
                                        protected void cardSelected(final PhysicalCard pilot) {
                                            // Update usage limit(s)
                                            action.addAnimationGroup(pilot);
                                            action.addAnimationGroup(reactorCore);
                                            // Update Epic Event State
                                            epicEventState.setStarfighter(starfighter);
                                            epicEventState.setPilot(pilot);
                                            final PhysicalCard deathStarII = Filters.findFirstFromTopLocationsOnTable(game, Filters.Death_Star_II_system);
                                            String actionText = "Have " + (Filters.character.accepts(game, pilot) ? "Permanent pilot" : GameUtils.getCardLink(pilot))
                                                    + " aboard " + GameUtils.getCardLink(starfighter) + " attempt to 'blow away' " + GameUtils.getCardLink(deathStarII);
                                            // Allow response(s)
                                            action.allowResponses(actionText,
                                                    new RespondableEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            final GameState gameState = game.getGameState();
                                                            final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new DrawDestinyEffect(action, playerId, 1, DestinyType.EPIC_EVENT_DESTINY) {
                                                                        @Override
                                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                                            if (Filters.character.accepts(game, pilot))
                                                                                return Collections.singletonList(pilot);
                                                                            else
                                                                                return Collections.emptyList();
                                                                        }
                                                                        @Override
                                                                        protected List<Modifier> getDrawDestinyModifiers(SwccgGame game, DrawDestinyState drawDestinyState) {
                                                                            float pilotAbility;
                                                                            if (Filters.character.accepts(game, pilot))
                                                                                pilotAbility = modifiersQuerying.getAbility(gameState, pilot);
                                                                            else
                                                                                pilotAbility = modifiersQuerying.getHighestAbilityPiloting(gameState, pilot, true, false);
                                                                            Modifier modifier = new TotalDestinyModifier(self, drawDestinyState.getId(), new AddEvaluator(pilotAbility, new ConditionEvaluator(0, 3, new ArmedWithCondition(starfighter, Filters.or(Filters.torpedo, Filters.missile)))));
                                                                            return Collections.singletonList(modifier);
                                                                        }
                                                                        @Override
                                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                            if (totalDestiny == null) {
                                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                                return;
                                                                            }
                                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));

                                                                            float total = modifiersQuerying.getEpicEventCalculationTotal(gameState, self, totalDestiny);
                                                                            gameState.sendMessage("Epic Event Total: " + GuiUtils.formatAsString(total));

                                                                            if (total > 8) {
                                                                                gameState.sendMessage("Result: Succeeded");
                                                                                action.appendEffect(
                                                                                        new EscapeDeathStarIIEffect(action));
                                                                                action.appendEffect(
                                                                                        new BlowAwayEffect(action, deathStarII) {
                                                                                            @Override
                                                                                            protected List<Modifier> getBlowAwayModifiers(SwccgGame game, BlowAwayState blowAwayState) {
                                                                                                Modifier modifier = new BlownAwayForceLossMultiplierModifier(self, blowAwayState.getId(), 2, game.getDarkPlayer());
                                                                                                return Collections.singletonList(modifier);
                                                                                            }
                                                                                        }
                                                                                );
                                                                            }
                                                                            else {
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
            return Collections.singletonList((TopLevelGameTextAction) action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter systemFilter = Filters.and(Filters.system, Filters.except(Filters.Endor_system), Filters.isOrbitedBy(Filters.Death_Star_II_system), Filters.occupies(playerId));

        // Check condition(s)
        // Check if reached end of each control phase and action was not performed yet.
        if (TriggerConditions.isEndOfYourPhase(game, effectResult, Phase.CONTROL, playerId)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canSpotLocation(game, systemFilter)) {
            GameState gameState = game.getGameState();
            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
            int numSites = Filters.countTopLocationsOnTable(game, Filters.and(Filters.battleground_site, Filters.notIgnoredDuringEpicEventCalculation(false), Filters.occupies(playerId), Filters.relatedSiteTo(self, systemFilter)));
            float numForce = modifiersQuerying.getVariableValue(gameState, self, Variable.X, 2 + numSites);
            if (numForce > 0) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Make opponent lose " + GuiUtils.formatAsString(numForce) + " Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, opponent, numForce));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}