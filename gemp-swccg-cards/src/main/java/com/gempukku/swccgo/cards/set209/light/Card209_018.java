package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Set: Set 9
 * Type: Effect
 * Title: Stardust
 */
public class Card209_018 extends AbstractNormalEffect {
    public Card209_018() {
        super(Side.LIGHT, 6, PlayCardZoneOption.ATTACHED, Title.Stardust, Uniqueness.UNIQUE);
        setLore("");
        setGameText("Deploy on Data Vault. At any time, may relocate Stardust to your spy present. During your control phase, if on your spy at a battleground you occupy, opponent loses 2 Force. If about to leave table, relocate to Data Vault (if possible). [Immune to Alter.]");
        addIcons(Icon.VIRTUAL_SET_9);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.DataVault;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        Filter yourSpyFilter = Filters.and(Filters.your(self), Filters.spy, Filters.presentWith(self), Filters.not(Filters.hasAttached(Filters.Stardust)));

        // As far as I can tell, this Reason text does not get used anywhere, but it's needed for the SpotOverride
        Set<TargetingReason> targetingReasonSet = new HashSet<TargetingReason>();
        targetingReasonSet.add(TargetingReason.TO_RELOCATE_STARDUST_TO);

        // Check condition(s)
        if (GameConditions.canTarget(game, self, false, SpotOverride.INCLUDE_UNDERCOVER, yourSpyFilter)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Relocate to your spy");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", SpotOverride.INCLUDE_UNDERCOVER, targetingReasonSet, yourSpyFilter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Relocate " + GameUtils.getCardLink(self) + " to " + GameUtils.getCardLink(targetedCard),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new AttachCardFromTableEffect(action, self, targetedCard));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {
            int NUM_FORCE = 2;
            if (damageConditionsSatisfied(game, self, playerId)) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Make opponent lose " + NUM_FORCE + " Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, game.getOpponent(playerId), NUM_FORCE));
                actions.add(action);
            }
        }

        return actions;
    }

    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        // Check if reached end of each control phase and action was not performed yet.
        if (TriggerConditions.isEndOfYourPhase(game, effectResult, Phase.CONTROL, playerId)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {
            int NUM_FORCE = 2;
            if (damageConditionsSatisfied(game, self, playerId)) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setPerformingPlayer(playerId);
                action.setText("Make opponent lose " + NUM_FORCE + " Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, opponent, NUM_FORCE));
                actions.add(action);
            }
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.isAboutToLeaveTableExcludingAllCards(game, effectResult, self)) {
            PhysicalCard dataVault = Filters.findFirstFromTopLocationsOnTable(game, Filters.DataVault);
            if (dataVault != null) {
                final AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Relocate to Data Vault");
                action.setActionMsg("Relocate " + GameUtils.getCardLink(self) + " to " + GameUtils.getCardLink(dataVault));
                action.addAnimationGroup(dataVault);
                // Perform result(s)
                // Per the rules team, this should not relocate in an all cards situation (e.g. Overwhelmed)
                //   This needs fixed to adhere to that.
                action.appendEffect(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                result.getPreventableCardEffect().preventEffectOnCard(self);
                                for (PhysicalCard attachedCards : game.getGameState().getAllAttachedRecursively(self)) {
                                    result.getPreventableCardEffect().preventEffectOnCard(attachedCards);
                                }
                            }
                        });
                action.appendEffect(
                        new AttachCardFromTableEffect(action, self, dataVault));
                actions.add(action);
            }
        }

        return actions;
    }

    private boolean damageConditionsSatisfied(SwccgGame game, PhysicalCard self, String playerId) {
        Filter yourSpyThatHasStardust = Filters.and(Filters.your(playerId), Filters.spy, Filters.hasAttached(Filters.Stardust));
        boolean occupiesBattlegroundWithStardust = Filters.canSpot(game, self, Filters.and(Filters.battleground, Filters.occupiesWith(playerId, self, Filters.Stardust)));
        return (Filters.canSpot(game, self, SpotOverride.INCLUDE_UNDERCOVER, yourSpyThatHasStardust) && occupiesBattlegroundWithStardust);
    }
}
