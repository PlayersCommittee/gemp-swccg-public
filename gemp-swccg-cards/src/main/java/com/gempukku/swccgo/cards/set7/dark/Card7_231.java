package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Effect
 * Title: Jabba's Influence
 */
public class Card7_231 extends AbstractNormalEffect {
    public Card7_231() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, Title.Jabbas_Influence, Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Jabba makes offers one cannot refuse. Smugglers, thieves and competitors who do not acquiesce have been rumored to wake up with a bantha's head in their bed.");
        setGameText("Deploy on one of your gangsters or bounty hunters. Once during each of your control phases, if present with an opponent's smuggler or character of ability = 1, may use 3 Force. Opponent must use 5 Force or that character returns to opponent's hand.");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(Filters.gangster, Filters.bounty_hunter));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);
        Filter targetFilter = Filters.and(Filters.opponents(self), Filters.or(Filters.smuggler, Filters.and(Filters.character, Filters.abilityEqualTo(1))), Filters.presentWith(self));

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canUseForce(game, playerId, 3)
                && GameConditions.canTarget(game, self, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Target a character");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", targetFilter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 3));
                            // Allow response(s)
                            action.allowResponses("Make opponent use 5 Force or return " + GameUtils.getCardLink(targetedCard) + " to hand",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            if (GameConditions.canUseForce(game, opponent, 5)) {
                                                action.appendEffect(
                                                        new PlayoutDecisionEffect(action, opponent,
                                                                new MultipleChoiceAwaitingDecision("Choose effect", new String[]{"Use 5 Force", "Return " + GameUtils.getCardLink(targetedCard) + " to hand"}) {
                                                                    @Override
                                                                    protected void validDecisionMade(int index, String result) {
                                                                        if (index == 0) {
                                                                            game.getGameState().sendMessage(opponent + " chooses to use 5 Force");
                                                                            action.appendEffect(
                                                                                    new UseForceEffect(action, opponent, 5));
                                                                        } else {
                                                                            game.getGameState().sendMessage(opponent + " chooses to return " + GameUtils.getCardLink(targetedCard) + " to hand");
                                                                            action.appendEffect(
                                                                                    new ReturnCardToHandFromTableEffect(action, targetedCard));
                                                                        }
                                                                    }
                                                                }
                                                        )
                                                );
                                            }
                                            else {
                                                game.getGameState().sendMessage("Only available option is to return " + GameUtils.getCardLink(targetedCard) + " to hand");
                                                action.appendEffect(
                                                        new ReturnCardToHandFromTableEffect(action, targetedCard));
                                            }
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}