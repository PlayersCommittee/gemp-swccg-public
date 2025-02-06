package com.gempukku.swccgo.cards.set303.dark;

import com.gempukku.swccgo.cards.AbstractJediTest;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.CompleteJediTestEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
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
 * Set: Shadow Academy
 * Type: Sith Test
 * Title: Evil's Domain
 */
public class Card303_016 extends AbstractJediTest {
    public Card303_016() {
        super(Side.DARK, 3, "Evil's Domain", ExpansionSet.SA, Rarity.C);
        setGameText("Deploy on Shadow Academy: Dueling Platform or Shadow Academy: Training Grounds. Target a mentor at the Shadow Academy and an apprentice who has completed Sith Test #2. Attempt when apprentice is present at the end of your turn and none of your cards participated in battles, Force drains or Sith Tests during that turn. Draw training destiny. If destiny + apprentice's ability > 14, test completed. Place on apprentice. You may subtract 1 from each of opponent's destiny draws.");
        addKeyword(Keyword.SITH_TEST_3);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.Shadow_Academy_Training_Grounds, Filters.Shadow_Academy_Dueling_Platform);
    }

    @Override
    protected Filter getGameTextValidMentorFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget) {
        return Filters.on(Title.Arx);
    }

    @Override
    protected Filter getGameTextValidApprenticeFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, PhysicalCard mentor, boolean isDeployFromHand) {
        return Filters.apprenticeTargetedByJediTest(Filters.and(Filters.completed_Jedi_Test, Filters.SITH_TEST_2));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isEndOfYourTurn(game, effectResult, self)
                && !GameConditions.isJediTestCompleted(game, self)) {
            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
            if (modifiersQuerying.getNumBattlesInitiatedThisTurn(playerId) == 0
                    && modifiersQuerying.getNumForceDrainsInitiatedThisTurn() == 0
                    && !modifiersQuerying.hasAttemptedJediTests()) {
                final PhysicalCard apprentice = Filters.findFirstActive(game, self, Filters.and(Filters.mayAttemptJediTest(self), Filters.present(self)));
                if (apprentice != null) {

                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Attempt Sith Test #3");
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
                                                int numSites = Filters.countTopLocationsOnTable(game, Filters.and(Filters.Shadow_Academy_location, Filters.not(Filters.generic)));
                                                if (numSites > 0) {

                                                    OptionalRuleTriggerAction action1 = new OptionalRuleTriggerAction(new JediTestAttemptRule(), self);
                                                    action1.setText("Add 1 to destiny for each Shadow Academy site");
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

                                    if (total > 14) {
                                        gameState.sendMessage("Result: Succeeded");
                                        action.appendEffect(
                                                new CompleteJediTestEffect(action, self));
                                    } else {
                                        gameState.sendMessage("Result: Failed");
                                    }
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isJediTestCompleted(game, self)
                && TriggerConditions.isDestinyJustDrawnBy(game, effectResult, opponent)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Subtract 1 from destiny");
            // Perform result(s)
            action.appendEffect(
                    new ModifyDestinyEffect(action, -1));
            return Collections.singletonList(action);
        }

        return null;
    }
}