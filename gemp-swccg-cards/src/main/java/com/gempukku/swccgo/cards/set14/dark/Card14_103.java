package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.ResetLandspeedUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Interrupt
 * Subtype: Used
 * Title: Halt!
 */
public class Card14_103 extends AbstractUsedInterrupt {
    public Card14_103() {
        super(Side.DARK, 6, Title.Halt, Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.C);
        setLore("'Coruscant?'");
        setGameText("During any deploy phase, target a character at same site as your battle droid. Target is landspeed = 0 for remainder of turn. OR If opponent just deployed a character to same site as your battle droid, opponent must use 1 Force or place that character in hand.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.character, Filters.at(Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.battle_droid))));

        // Check condition(s)
        if (GameConditions.isDuringEitherPlayersPhase(game, Phase.DEPLOY)
                && GameConditions.canTarget(game, self, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Reset character's landspeed to 0");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard character) {
                            action.addAnimationGroup(character);
                            // Allow response(s)
                            action.allowResponses("Reset " + GameUtils.getCardLink(character) + "'s landspeed to 0",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ResetLandspeedUntilEndOfTurnEffect(action, finalTarget, 0));
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

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.justDeployedToLocation(game, effectResult, opponent, Filters.and(Filters.character, Filters.canBeTargetedBy(self)), Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.battle_droid)))) {
            final PhysicalCard cardPlayed = ((PlayCardResult) effectResult).getPlayedCard();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make opponent use Force or place " + GameUtils.getFullName(cardPlayed) + " in hand");
            action.addAnimationGroup(cardPlayed);
            // Allow response(s)
            action.allowResponses("Make opponent use 1 Force or return " + GameUtils.getCardLink(cardPlayed) + " to hand",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            if (GameConditions.canUseForce(game, opponent, 1)) {
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, opponent,
                                                new MultipleChoiceAwaitingDecision("Choose effect", new String[]{"Use 1 Force", "Place " + GameUtils.getFullName(cardPlayed) + " in hand"}) {
                                                    @Override
                                                    protected void validDecisionMade(int index, String result) {
                                                        if (index == 0) {
                                                            game.getGameState().sendMessage(opponent + " chooses to use 1 Force");
                                                            action.appendEffect(
                                                                    new UseForceEffect(action, opponent, 1));
                                                        }
                                                        else {
                                                            game.getGameState().sendMessage(opponent + " chooses to place " + GameUtils.getCardLink(cardPlayed) + " in hand");
                                                            action.appendEffect(
                                                                    new ReturnCardToHandFromTableEffect(action, cardPlayed));
                                                        }
                                                    }
                                                }
                                        )
                                );
                            }
                            else {
                                game.getGameState().sendMessage("Placing " + GameUtils.getCardLink(cardPlayed) + " in hand is the only available choice");
                                action.appendEffect(
                                        new ReturnCardToHandFromTableEffect(action, cardPlayed));
                            }
                        }
                   }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}