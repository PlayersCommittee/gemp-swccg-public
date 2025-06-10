package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnActionProxyEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.ResetPowerUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Character
 * Subtype: Imperial
 * Title: Lieutenant Dedra Meero
 */
public class Card221_025 extends AbstractImperial {
    public Card221_025() {
        super(Side.DARK, 3, 3, 3, 3, 5, "Lieutenant Dedra Meero", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setLore("Female ISB leader.");
        setGameText("When deployed, may target an opponent's spy present to be power = 0 until end of turn. Once per turn, if an ISB agent on table, may place a card from hand on Used Pile; the next ISB agent you deploy this turn is deploy -1.");
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_21);
        addKeywords(Keyword.FEMALE, Keyword.LEADER);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            Filter filter = Filters.and(Filters.opponents(self), Filters.spy, Filters.presentAt(Filters.here(self)));
            if (GameConditions.canTarget(game, self, filter)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Make a spy power = 0");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose an opponent's spy present", filter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Allow response(s)
                                action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " power = 0 until end of turn",
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new ResetPowerUntilEndOfTurnEffect(action, finalTarget, 0));
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }

        return null;
    }


    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        final List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.canTarget(game, self, Filters.ISB_agent)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasHand(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place card from hand on Used Pile");
            action.setActionMsg("Make the next ISB agent they deploy this turn deploy -1");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new PutCardFromHandOnUsedPileEffect(action, playerId));

            final int permanentCardId = self.getPermanentCardId();
            // Perform result(s)
            action.appendEffect(
                    new AddUntilEndOfTurnActionProxyEffect(action,
                            new AbstractActionProxy() {
                                @Override
                                public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                    List<TriggerAction> actions = new LinkedList<>();

                                    // don't actually need to return an action
                                    // only need to track that an ISB agent was deployed to increment the limit counter so the modifier is turned off
                                    if (TriggerConditions.justDeployed(game, effectResult, playerId, Filters.and(Filters.your(playerId), Filters.ISB_agent))) {
                                        PhysicalCard card = game.findCardByPermanentId(permanentCardId);
                                        for (String title : card.getTitles()) {
                                            game.getModifiersQuerying().getUntilEndOfTurnForCardTitleLimitCounter(title, GameTextActionId.OTHER_CARD_ACTION_DEFAULT).incrementToLimit(1, 1);
                                        }
                                    }
                                    return actions;
                                }
                            }
                    ));

            // this can't use EndOfTurnLimitCounterNotReachedCondition just in case she leaves the table because that resets the cardId that it uses to find the limit
            Condition turnLimitCounterNotReachedCondition = new Condition() {
                @Override
                public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                    PhysicalCard card = gameState.findCardByPermanentId(permanentCardId);
                    for (String title : card.getTitles()) {
                        if (modifiersQuerying.getUntilEndOfTurnForCardTitleLimitCounter(title, GameTextActionId.OTHER_CARD_ACTION_DEFAULT).getUsedLimit() >= 1)
                            return false;
                    }
                    return true;
                }
            };
            action.appendEffect(
                    new AddUntilEndOfTurnModifierEffect(action, new DeployCostModifier(self, Filters.and(Filters.your(self), Filters.ISB_agent, Filters.not(Filters.onTable)),
                            turnLimitCounterNotReachedCondition,-1), "Reduces the cost of your next ISB agent you deploy this turn by 1"));
            actions.add(action);
        }

        return actions;
    }
}
