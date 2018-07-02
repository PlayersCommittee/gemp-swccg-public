package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractJediTest;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.JediTestCompletedCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.OptionalRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.CompleteJediTestEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.CancelOpponentsForceDrainBonusesModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.rules.JediTestAttemptRule;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Jedi Test
 * Title: Great Warrior
 */
public class Card4_077 extends AbstractJediTest {
    public Card4_077() {
        super(Side.LIGHT, 1, Title.Great_Warrior);
        setGameText("Deploy on a Dagobah site. Target a mentor here. Also, target or deploy (regardless of location deployment restrictions) an apprentice here. Attempt when targets are present at the beginning of your control phase. Draw training destiny. If destiny + apprentice's ability > 12, test completed: Place on apprentice. All opponent's Force drain bonuses are canceled. (Mentor: one of your characters of ability > 2. Apprentice: one of your non-droid, non-Jedi characters of lesser ability than mentor. Each time you complete any Jedi Test, you may exchange one card in hand for one Jedi Test in your Lost Pile.)");
        addIcons(Icon.DAGOBAH);
        addKeyword(Keyword.JEDI_TEST_1);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Dagobah_site;
    }

    @Override
    protected Filter getGameTextValidMentorFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget) {
        return Filters.here(deployTarget);
    }

    @Override
    protected Filter getGameTextValidApprenticeFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, PhysicalCard mentor, boolean isDeployFromHand) {
        return isDeployFromHand ? Filters.any : Filters.here(deployTarget);
    }

    @Override
    protected boolean mayDeployApprenticeToSameLocationFromHandDuringTargeting() {
        return true;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isStartOfYourPhase(game, self, effectResult, Phase.CONTROL)) {
            GameState gameState = game.getGameState();
            if (!GameConditions.isJediTestCompleted(game, self)
                    && GameConditions.canSpot(game, self, Filters.and(self.getTargetedCard(gameState, TargetId.JEDI_TEST_MENTOR), Filters.present(self)))) {
                final PhysicalCard apprentice = Filters.findFirstActive(game, self, Filters.and(Filters.mayAttemptJediTest(self), Filters.present(self)));
                if (apprentice != null) {

                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Attempt Jedi Test #1");
                    // Update usage limit(s)
                    action.appendUsage(
                            new PassthruEffect(action) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    game.getModifiersQuerying().attemptedJediTest(self, apprentice);
                                }
                            });
                    // Perform result(s)
                    action.appendEffect(
                            new DrawDestinyEffect(action, playerId, 1, DestinyType.TRAINING_DESTINY) {
                                @Override
                                protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                    return Collections.singletonList(apprentice);
                                }
                                @Override
                                protected List<ActionProxy> getDrawDestinyActionProxies(SwccgGame game, final DrawDestinyState drawDestinyState) {
                                    ActionProxy actionProxy = new AbstractActionProxy() {
                                        @Override
                                        public List<TriggerAction> getOptionalAfterTriggers(String playerId2, SwccgGame game, EffectResult effectResult) {
                                            List<TriggerAction> actions = new LinkedList<TriggerAction>();

                                            // Check condition(s)
                                            if (TriggerConditions.isDestinyJustDrawn(game, effectResult, drawDestinyState)
                                                    && playerId2.equals(playerId)) {
                                                int numSites = Filters.countTopLocationsOnTable(game, Filters.and(Filters.Dagobah_site, Filters.not(Filters.generic)));
                                                if (numSites > 0) {

                                                    OptionalRuleTriggerAction action1 = new OptionalRuleTriggerAction(new JediTestAttemptRule(), self);
                                                    action1.setText("Add 1 to destiny for each Dagobah site");
                                                    // Perform result(s)
                                                    action1.appendEffect(
                                                            new ModifyDestinyEffect(action1, numSites));
                                                    actions.add(action1);
                                                }
                                            }
                                            return actions;
                                        }
                                    };
                                    return Collections.singletonList(actionProxy);
                                }
                                @Override
                                protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                    GameState gameState = game.getGameState();
                                    ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                    if (totalDestiny == null) {
                                        gameState.sendMessage("Result: Failed due to failed destiny draw");
                                        return;
                                    }

                                    gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                    float ability = modifiersQuerying.getAbility(gameState, apprentice);
                                    gameState.sendMessage("Apprentice's ability: " + GuiUtils.formatAsString(ability));
                                    float total = totalDestiny + ability;
                                    gameState.sendMessage("Total: " + GuiUtils.formatAsString(total));

                                    if (total > 12) {
                                        gameState.sendMessage("Result: Succeeded");
                                        action.appendEffect(
                                                new CompleteJediTestEffect(action, self));
                                    }
                                    else {
                                        gameState.sendMessage("Result: Failed");
                                    }
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelOpponentsForceDrainBonusesModifier(self, new JediTestCompletedCondition(self)));
        return modifiers;
    }
}