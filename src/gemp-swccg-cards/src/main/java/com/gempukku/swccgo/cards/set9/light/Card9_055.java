package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractJediTest;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.JediTestCompletedCondition;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.effects.CancelBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.evaluators.AbilityEvaluator;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.CompleteJediTestEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.DuelDirections;
import com.gempukku.swccgo.logic.effects.DuelEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Jedi Test
 * Title: You Must Confront Vader
 */
public class Card9_055 extends AbstractJediTest {
    public Card9_055() {
        super(Side.LIGHT, 6, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.You_Must_Confront_Vader, ExpansionSet.DEATH_STAR_II, Rarity.R);
        setGameText("Deploy on table.  Target a Skywalker who has completed Jedi Test #5.  While target on Dagobah, 'move phase' on Save You It Can may be treated as 'deploy phase.'  Until Jedi Test completed, battles you initiate at Vader's site are canceled and opponent loses 2 Force at start of your turn unless Vader is at a battleground site.  Attempt during your move phase when Vader with target (even as a non-frozen captive).  Vader and target duel: Each player draws destiny.  Add ability.  Highest total wins.  If target wins, test completed:  Leave on table.  Add one battle destiny in every battle.  Also, target is immune to attrition < 5.");
        addIcons(Icon.DEATH_STAR_II);
        addKeyword(Keyword.JEDI_TEST_6);
    }

    @Override
    protected Filter getGameTextValidApprenticeFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, PhysicalCard mentor, boolean isDeployFromHand) {
        Filter apprenticeFilter = Filters.and(Filters.Skywalker, Filters.apprenticeTargetedByJediTest(Filters.and(Filters.completed_Jedi_Test, Filters.Jedi_Test_5)));

        if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.JEDI_TESTS__ONLY_LUKE_MAY_BE_APPRENTICE))
        {
            apprenticeFilter = Filters.and(apprenticeFilter, Filters.Luke);
        }

        return apprenticeFilter;
    }

    @Override
    protected boolean targetsMentor() {
        return false;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition jediTestCompleted = new JediTestCompletedCondition(self);
        Condition apprenticeOnDagobah = new OnCondition(self, Filters.apprenticeTargetedByJediTest(self), Title.Dagobah);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.Save_You_It_Can, apprenticeOnDagobah, ModifyGameTextType.SAVE_YOU_IT_CAN__MOVE_PHASE_MAY_BE_TREATED_AS_DEPLOY_PHASE));
        modifiers.add(new AddsBattleDestinyModifier(self, jediTestCompleted, 1, playerId));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.targetedByCompletedJediTest(self), jediTestCompleted, 5));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        if (GameConditions.isJediTestCompleted(game, self)) {
            return null;
        }

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, playerId, Filters.sameSiteAs(self, Filters.Vader))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel battle");
            // Perform result(s)
            action.appendEffect(
                    new CancelBattleEffect(action));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.isStartOfYourTurn(game, effectResult, self)
                && !GameConditions.canSpot(game, self, Filters.and(Filters.Vader, Filters.at(Filters.battleground_site)))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Make " + opponent + " lose 2 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 2));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.isJediTestCompleted(game, self)) {
            return null;
        }

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.MOVE)) {
            GameState gameState = game.getGameState();
            final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
            final PhysicalCard apprentice = Filters.findFirstActive(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(self.getTargetedCard(gameState, TargetId.JEDI_TEST_APPRENTICE), Filters.not(Filters.frozenCaptive)));
            if (apprentice != null) {
                final PhysicalCard vader = Filters.findFirstActive(game, self, Filters.and(Filters.Vader, Filters.with(apprentice), Filters.canBeTargetedBy(self, TargetingReason.TO_BE_DUELED)));
                if (vader != null) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                    action.setText("Attempt Jedi Test #6");
                    // Update usage limit(s)
                    action.appendUsage(
                            new PassthruEffect(action) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    modifiersQuerying.attemptedJediTest(self, apprentice);
                                }
                            });
                    action.addAnimationGroup(apprentice, vader);
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerPhaseEffect(action));
                    // Perform result(s)
                    action.appendEffect(
                            new DuelEffect(action, vader, apprentice, new DuelDirections() {
                                @Override
                                public boolean isEpicDuel() {
                                    return false;
                                }

                                @Override
                                public boolean isCrossOverToDarkSideAttempt() {
                                    return false;
                                }

                                @Override
                                public Evaluator getBaseDuelTotal(final String playerId, final DuelState duelState) {
                                    return new AbilityEvaluator(duelState.getCharacter(playerId));
                                }

                                @Override
                                public int getBaseNumDuelDestinyDraws(String playerId, DuelState duelState) {
                                    return 1;
                                }

                                @Override
                                public void performDuelDirections(final Action duelAction, SwccgGame game, final DuelState duelState) {
                                    duelAction.appendEffect(
                                            new DrawDestinyEffect(duelAction, game.getDarkPlayer(), game.getModifiersQuerying().getNumDuelDestinyDraws(game.getGameState(), game.getDarkPlayer()), DestinyType.DUEL_DESTINY) {
                                                @Override
                                                protected void destinyDraws(SwccgGame game, final List<PhysicalCard> darkDestinyCardDraws, List<Float> darkDestinyDrawValues, final Float darkTotalDestiny) {
                                                    if (darkTotalDestiny != null) {
                                                        duelState.increaseTotalDuelDestinyFromDraws(game.getDarkPlayer(), darkTotalDestiny, darkDestinyCardDraws.size());
                                                    }
                                                    duelAction.appendEffect(
                                                            new DrawDestinyEffect(duelAction, game.getLightPlayer(), game.getModifiersQuerying().getNumDuelDestinyDraws(game.getGameState(), game.getLightPlayer()), DestinyType.DUEL_DESTINY) {
                                                                @Override
                                                                protected void destinyDraws(SwccgGame game, List<PhysicalCard> lightDestinyCardDraws, List<Float> lightDestinyDrawValues, Float lightTotalDestiny) {
                                                                    if (lightTotalDestiny != null) {
                                                                        duelState.increaseTotalDuelDestinyFromDraws(game.getLightPlayer(), lightTotalDestiny, lightDestinyCardDraws.size());
                                                                    }
                                                                }
                                                            }
                                                    );
                                                }
                                            }
                                    );
                                }

                                @Override
                                public void performDuelResults(Action duelAction, SwccgGame game, DuelState duelState) {
                                    PhysicalCard winningCharacter = duelState.getWinningCharacter();
                                    if (winningCharacter == null) {
                                        return;
                                    }

                                    // If apprentice wins, test completed.
                                    if (Filters.apprenticeTargetedByJediTest(self).accepts(game, winningCharacter)) {
                                        action.appendEffect(
                                                new CompleteJediTestEffect(action, self));
                                    }
                                }
                            })
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, final int gameTextSourceCardId) {
        if (GameConditions.isJediTestCompleted(game, self)) {
            return null;
        }

        boolean specialTiming = TriggerConditions.isStartOfOpponentsPhase(game, self, effectResult, Phase.DEPLOY) && GameConditions.hasGameTextModification(game, self, ModifyGameTextType.JEDI_TESTS__MAY_ATTEMPT_IN_OPPONENTS_DEPLOY_PHASE);

        // Check condition(s)
        if (specialTiming) {
            GameState gameState = game.getGameState();
            final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
            final PhysicalCard apprentice = Filters.findFirstActive(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(self.getTargetedCard(gameState, TargetId.JEDI_TEST_APPRENTICE), Filters.not(Filters.frozenCaptive)));
            if (apprentice != null) {
                final PhysicalCard vader = Filters.findFirstActive(game, self, Filters.and(Filters.Vader, Filters.with(apprentice), Filters.canBeTargetedBy(self, TargetingReason.TO_BE_DUELED)));
                if (vader != null) {

                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Attempt Jedi Test #6");
                    // Update usage limit(s)
                    action.appendUsage(
                            new PassthruEffect(action) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    modifiersQuerying.attemptedJediTest(self, apprentice);
                                }
                            });
                    action.addAnimationGroup(apprentice, vader);
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerPhaseEffect(action));
                    // Perform result(s)
                    action.appendEffect(
                            new DuelEffect(action, vader, apprentice, new DuelDirections() {
                                @Override
                                public boolean isEpicDuel() {
                                    return false;
                                }

                                @Override
                                public boolean isCrossOverToDarkSideAttempt() {
                                    return false;
                                }

                                @Override
                                public Evaluator getBaseDuelTotal(final String playerId, final DuelState duelState) {
                                    return new AbilityEvaluator(duelState.getCharacter(playerId));
                                }

                                @Override
                                public int getBaseNumDuelDestinyDraws(String playerId, DuelState duelState) {
                                    return 1;
                                }

                                @Override
                                public void performDuelDirections(final Action duelAction, SwccgGame game, final DuelState duelState) {
                                    duelAction.appendEffect(
                                            new DrawDestinyEffect(duelAction, game.getDarkPlayer(), game.getModifiersQuerying().getNumDuelDestinyDraws(game.getGameState(), game.getDarkPlayer()), DestinyType.DUEL_DESTINY) {
                                                @Override
                                                protected void destinyDraws(SwccgGame game, final List<PhysicalCard> darkDestinyCardDraws, List<Float> darkDestinyDrawValues, final Float darkTotalDestiny) {
                                                    if (darkTotalDestiny != null) {
                                                        duelState.increaseTotalDuelDestinyFromDraws(game.getDarkPlayer(), darkTotalDestiny, darkDestinyCardDraws.size());
                                                    }
                                                    duelAction.appendEffect(
                                                            new DrawDestinyEffect(duelAction, game.getLightPlayer(), game.getModifiersQuerying().getNumDuelDestinyDraws(game.getGameState(), game.getLightPlayer()), DestinyType.DUEL_DESTINY) {
                                                                @Override
                                                                protected void destinyDraws(SwccgGame game, List<PhysicalCard> lightDestinyCardDraws, List<Float> lightDestinyDrawValues, Float lightTotalDestiny) {
                                                                    if (lightTotalDestiny != null) {
                                                                        duelState.increaseTotalDuelDestinyFromDraws(game.getLightPlayer(), lightTotalDestiny, lightDestinyCardDraws.size());
                                                                    }
                                                                }
                                                            }
                                                    );
                                                }
                                            }
                                    );
                                }

                                @Override
                                public void performDuelResults(Action duelAction, SwccgGame game, DuelState duelState) {
                                    PhysicalCard winningCharacter = duelState.getWinningCharacter();
                                    if (winningCharacter == null) {
                                        return;
                                    }

                                    // If apprentice wins, test completed.
                                    if (Filters.apprenticeTargetedByJediTest(self).accepts(game, winningCharacter)) {
                                        action.appendEffect(
                                                new CompleteJediTestEffect(action, self));
                                    }
                                }
                            })
                    );
                    return Collections.singletonList(action);
                }
            }
        }

        return null;
    }
}