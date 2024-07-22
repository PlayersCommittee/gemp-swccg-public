package com.gempukku.swccgo.cards.set223.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelBattleEffect;
import com.gempukku.swccgo.cards.effects.StackCardFromVoidEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 23
 * Type: Interrupt
 * Subtype: Used
 * Title: Scomp Link Access (V)
 */

public class Card223_047 extends AbstractUsedInterrupt {
    public Card223_047() {
        super(Side.LIGHT, 3, Title.Scomp_Link_Access, Uniqueness.UNRESTRICTED, ExpansionSet.SET_23, Rarity.V);
        setVirtualSuffix(true);
        setGameText("Once per game, 'BEEP-BOOP' (a [Grabber] card stacks a just played opponent's Interrupt; this does not count towards any [Grabber] card's once per game limit). OR Once per game, 'WHAAOW!' (lose your droid at a Scomp link to cancel a battle just initiated at same or related interior site).");
        addIcons(Icon.VIRTUAL_SET_23);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.SCOMP_LINK_ACCESS__STACK_CARD;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && TriggerConditions.isPlayingCard(game, effect, Filters.and(Filters.opponents(self), Filters.Interrupt))
                && GameConditions.canTarget(game, self, Filters.grabber)) {
            final PhysicalCard interrupt = ((RespondablePlayingCardEffect)effect).getCard();

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("BEEP-BOOP (Stack card on a grabber)");

            action.appendUsage(
                    new OncePerGameEffect(action));

            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose grabber to stack " + GameUtils.getFullName(interrupt) + " on", Filters.grabber) {
                @Override
                protected void cardTargeted(int targetGroupId, PhysicalCard targetedCard) {
                    action.allowResponses("Stack " + GameUtils.getFullName(interrupt) + " on " + GameUtils.getCardLink(targetedCard), new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            PhysicalCard finalGrabber = action.getPrimaryTargetCard(targetGroupId);

                            action.appendEffect(
                                    new SendMessageEffect(action, "BEEP-BOOP"));
                            action.appendEffect(
                                    new StackCardFromVoidEffect(action, interrupt, finalGrabber));
                        }
                    });
                }
            });

            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.SCOMP_LINK_ACCESS__CANCEL_BATTLE;

        Filter yourDroidAtAScompLink = Filters.and(Filters.your(self), Filters.droid, Filters.at_Scomp_Link, Filters.at(Filters.site), Filters.canBeTargetedBy(self, TargetingReason.TO_BE_LOST));

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && TriggerConditions.battleInitiatedAt(game, effectResult, Filters.and(Filters.interior_site, Filters.sameOrRelatedSiteAs(self, yourDroidAtAScompLink)))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("WHAAOW! (Cancel battle)");

            action.appendUsage(
                    new OncePerGameEffect(action));

            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose droid to be lost", yourDroidAtAScompLink) {
                @Override
                protected void cardTargeted(int targetGroupId, PhysicalCard targetedCard) {

                    action.appendCost(
                            new SendMessageEffect(action, GameUtils.getFullName(targetedCard) + ": WHAAOW!"));
                    action.appendCost(
                            new LoseCardFromTableEffect(action, targetedCard));

                    // Allow response(s)
                    action.allowResponses("Cancel battle",
                            new RespondablePlayCardEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    // Perform result(s)
                                    action.appendEffect(
                                            new CancelBattleEffect(action));
                                }
                            }
                    );
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }
}
