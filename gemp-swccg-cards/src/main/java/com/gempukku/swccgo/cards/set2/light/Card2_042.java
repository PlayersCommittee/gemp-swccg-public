package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.AttackRunState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelEpicEventGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.CalculatingEpicEventTotalResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Epic Event
 * Title: Attack Run
 */
public class Card2_042 extends AbstractEpicEventDeployable {
    public Card2_042() {
        super(Side.LIGHT, PlayCardZoneOption.ATTACHED, Title.Attack_Run, Uniqueness.UNIQUE);
        setGameText("Deploy on Death Star: Trench. During your move phase, you may make an Attack Run as follows: Enter Trench: Move up to 3 of your starfighters into trench (for free). Dark Side may immediately follow with up to 3 TIEs (for free). Provide Cover: Identify your lead starfighter (Proton Torpedoes* required) and wingmen (if any). Turbolaser Batteries and TIEs with weapons may now target your starfighters (wingmen first, then lead if no wingmen remaining). Hit starfighters are immediately lost. It's Away!: Draw two destiny. Pull Up!: All starfighters now move to Death Star system (for free). If (total destiny + X + Y - Z) > 15, Death Star is 'blown away.' X = ability of lead pilot or 3 if Targeting Computer is present. Y = total sites at largest Rebel Base (Yavin 4 or Hoth). Z = highest ability of TIE pilots in trench. *Your Proton Torpedoes are immune to Overload.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Death_Star_Trench;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToTargetModifier(self, Filters.Death_Star_Trench));
        return modifiers;
    }

    @Override
    protected List<TopLevelEpicEventGameTextAction> getEpicEventGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        Filter yourStarfighterToMoveIntoTrench = Filters.and(Filters.your(self), Filters.starfighter, Filters.canMoveAtStartOfAttackRun(playerId));
        Filter validLeadStarfighter = Filters.and(yourStarfighterToMoveIntoTrench, Filters.hasAttached(Filters.Proton_Torpedoes));

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.MOVE)
                && GameConditions.canDrawDestiny(game, playerId)
                && GameConditions.canSpot(game, self, validLeadStarfighter)) {
            final GameState gameState = game.getGameState();
            final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
            final AttackRunState epicEventState = new AttackRunState(self);

            final TopLevelEpicEventGameTextAction action = new TopLevelEpicEventGameTextAction(self, gameTextSourceCardId);
            action.setText("Attempt to 'blow away' Death Star");
            action.setEpicEventState(epicEventState);
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            // 1) Enter Trench
            action.appendEffect(
                    new EnterTrenchDuringAttackRunEffect(action));
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            // Check if your piloted starfighter with Proton Torpedoes is in the trench
                            if (GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.piloted, Filters.starfighter,
                                    Filters.hasAttached(Filters.Proton_Torpedoes), Filters.at(Filters.Death_Star_Trench)))) {
                                // 2) Provide Cover
                                action.appendEffect(
                                        new ProvideCoverDuringAttackRunEffect(action));
                                action.appendEffect(
                                        new PassthruEffect(action) {
                                            @Override
                                            protected void doPlayEffect(SwccgGame game) {
                                                // Check if there is still a lead starfighter
                                                if (epicEventState.getLeadStarfighter() != null) {
                                                    // 3) It's Away!
                                                    action.appendEffect(
                                                            new DrawDestinyEffect(action, playerId, 2, DestinyType.EPIC_EVENT_DESTINY) {
                                                                @Override
                                                                protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float totalDestiny) {
                                                                    final PhysicalCard leadStarfighter = epicEventState.getLeadStarfighter();
                                                                    if (leadStarfighter != null) {

                                                                        // Emit effect result that Attack Run total is being calculated
                                                                        action.appendEffect(
                                                                                new TriggeringResultEffect(action, new CalculatingEpicEventTotalResult(playerId, self)));
                                                                        action.appendEffect(
                                                                                new PassthruEffect(action) {
                                                                                    @Override
                                                                                    protected void doPlayEffect(SwccgGame game) {
                                                                                        final float valueForX;
                                                                                        if (Filters.hasAttached(Filters.Targeting_Computer).accepts(game, leadStarfighter))
                                                                                            valueForX = 3;
                                                                                        else
                                                                                            valueForX = modifiersQuerying.getVariableValue(gameState, self, Variable.X, modifiersQuerying.getHighestAbilityPiloting(gameState, leadStarfighter, false));

                                                                                        int hothSites = Filters.countTopLocationsOnTable(game, Filters.and(Filters.Hoth_site, Filters.notIgnoredDuringEpicEventCalculation));
                                                                                        int yavin4Sites = Filters.countTopLocationsOnTable(game, Filters.and(Filters.Yavin_4_site, Filters.notIgnoredDuringEpicEventCalculation));
                                                                                        final float valueForY = modifiersQuerying.getVariableValue(gameState, self, Variable.Y, Math.max(hothSites, yavin4Sites));

                                                                                        float highestAbility = 0;
                                                                                        for (PhysicalCard tieInTrench : Filters.filterActive(game, self, Filters.and(Filters.TIE, Filters.at(Filters.Death_Star_Trench)))) {
                                                                                            highestAbility = Math.max(highestAbility, modifiersQuerying.getHighestAbilityPiloting(gameState, tieInTrench, false));
                                                                                        }
                                                                                        final float valueForZ = modifiersQuerying.getVariableValue(gameState, self, Variable.Z, highestAbility);

                                                                                        final float total = modifiersQuerying.getEpicEventCalculationTotal(gameState, self, (totalDestiny != null ? totalDestiny : 0) + valueForX + valueForY - valueForZ);

                                                                                        // 4) Pull Up
                                                                                        action.appendEffect(
                                                                                                new PullUpDuringAttackRunEffect(action));
                                                                                        action.appendEffect(
                                                                                                new PassthruEffect(action) {
                                                                                                    @Override
                                                                                                    protected void doPlayEffect(SwccgGame game) {
                                                                                                        gameState.sendMessage("Destiny: " + (totalDestiny != null ? GuiUtils.formatAsString(totalDestiny) : "Failed destiny draw"));
                                                                                                        gameState.sendMessage("X: " + GuiUtils.formatAsString(valueForX));
                                                                                                        gameState.sendMessage("Y: " + GuiUtils.formatAsString(valueForY));
                                                                                                        gameState.sendMessage("Z: " + GuiUtils.formatAsString(valueForZ));
                                                                                                        gameState.sendMessage("Epic Event Total: " + GuiUtils.formatAsString(total));

                                                                                                        if (total > 15) {
                                                                                                            gameState.sendMessage("Result: Succeeded");
                                                                                                            PhysicalCard deathStar = Filters.findFirstFromTopLocationsOnTable(game, Filters.Death_Star_system);
                                                                                                            if (deathStar != null) {
                                                                                                                action.appendEffect(
                                                                                                                        new BlowAwayEffect(action, deathStar));
                                                                                                            }
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
                                                                    else {
                                                                        // 4) Pull Up!
                                                                        action.appendEffect(
                                                                                new PullUpDuringAttackRunEffect(action));
                                                                    }
                                                                }
                                                            }
                                                    );
                                                }
                                                else {
                                                    // 4) Pull Up!
                                                    action.appendEffect(
                                                            new PullUpDuringAttackRunEffect(action));
                                                }
                                            }
                                        }
                                );
                            }
                            else {
                                // 4) Pull Up!
                                action.appendEffect(
                                        new PullUpDuringAttackRunEffect(action));
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.your(self), Filters.Proton_Torpedoes), Title.Overload));
        return modifiers;
    }
}