package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractJediTest;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.JediTestCompletedCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.CompleteJediTestEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
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
 * Title: Size Matters Not
 */
public class Card4_079 extends AbstractJediTest {
    public Card4_079() {
        super(Side.LIGHT, 4, "Size Matters Not", ExpansionSet.DAGOBAH, Rarity.R);
        setGameText("Deploy on Dagobah: Swamp or Dagobah: Bog Clearing. Target a mentor on Dagobah and an apprentice who has completed Jedi Test #3. Attempt when apprentice is present at the beginning of your control phase. Draw training destiny. If destiny + apprentice's ability > 15, test completed: Place on apprentice. Immune to attrition < 3. Once during each of your control phases, you may use 2 Force to search your Reserve Deck and take any one card you find there into hand. Shuffle, cut and replace.");
        addIcons(Icon.DAGOBAH);
        addKeyword(Keyword.JEDI_TEST_4);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.Dagobah_Swamp, Filters.Dagobah_Bog_Clearing);
    }

    @Override
    protected Filter getGameTextValidMentorFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget) {
        return Filters.on(Title.Dagobah);
    }

    @Override
    protected Filter getGameTextValidApprenticeFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, PhysicalCard mentor, boolean isDeployFromHand) {
        Filter apprenticeFilter = Filters.apprenticeTargetedByJediTest(Filters.and(Filters.completed_Jedi_Test, Filters.Jedi_Test_3));

        if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.JEDI_TESTS__ONLY_LUKE_MAY_BE_APPRENTICE))
        {
            apprenticeFilter = Filters.and(apprenticeFilter, Filters.Luke);
        }

        return apprenticeFilter;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        boolean normalTiming = TriggerConditions.isStartOfYourPhase(game, self, effectResult, Phase.CONTROL);
        boolean specialTiming = TriggerConditions.isStartOfOpponentsPhase(game, self, effectResult, Phase.DEPLOY) && GameConditions.hasGameTextModification(game, self, ModifyGameTextType.JEDI_TESTS__MAY_ATTEMPT_IN_OPPONENTS_DEPLOY_PHASE);
        boolean timingSatisfied = normalTiming || specialTiming;
        
        // Check condition(s)
        if (timingSatisfied
                && !GameConditions.isJediTestCompleted(game, self)) {
            final PhysicalCard apprentice = Filters.findFirstActive(game, self, Filters.and(Filters.mayAttemptJediTest(self), Filters.present(self)));
            if (apprentice != null) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Attempt Jedi Test #4");
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

                                if (total > 15) {
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
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.apprenticeTargetedByJediTest(self), new JediTestCompletedCondition(self), 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.SIZE_MATTERS_NOT__UPLOAD_CARD;

        boolean isFree = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.JEDI_TEST_4__SEARCHES_FOR_FREE);
        int forceToUse = isFree ? 0 : 2;

        // Check condition(s)
        if (GameConditions.isJediTestCompleted(game, self)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && (isFree || GameConditions.canUseForce(game, playerId, forceToUse))
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a card into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Pay cost(s)
            if (!isFree)
                action.appendCost(
                        new UseForceEffect(action, playerId, forceToUse));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}