package com.gempukku.swccgo.cards.set205.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.MoveAsReactEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnBottomOfUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 5
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Point Man (V)
 */
public class Card205_020 extends AbstractUsedOrLostInterrupt {
    public Card205_020() {
        super(Side.DARK, 5, "Point Man", Uniqueness.UNRESTRICTED, ExpansionSet.SET_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("In a military situation, on a regional or galactic scale, commands sometimes get misinterpreted. A local commander giving orders is far more reliable.");
        setGameText("USED: Move your trooper as a 'react' to a battle where you have a leader. OR During any draw phase, place a card from hand under your Used Pile to take any one card into hand from Force Pile; reshuffle. LOST: During a battle at a site, cancel Keep Your Eyes Open.");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_5);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.leader)))) {
            Filter trooperFilter = Filters.and(Filters.your(self), Filters.trooper, Filters.canMoveAsReactAsActionFromOtherCard(self, false, 0, false));
            if (GameConditions.canTarget(game, self, trooperFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Move trooper as 'react'");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose trooper", trooperFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Allow response(s)
                                action.allowResponses("Move " + GameUtils.getCardLink(targetedCard) + " as a 'react'",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                PhysicalCard finalCharacter = action.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new MoveAsReactEffect(action, finalCharacter, false));
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
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isEitherPlayersPhase(game, Phase.DRAW)
                && GameConditions.hasInHand(game, playerId, Filters.not(self))
                && GameConditions.hasForcePile(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Place card from hand under Used Pile");
            // Pay cost(s)
            action.appendCost(
                    new PutCardFromHandOnBottomOfUsedPileEffect(action, playerId, Filters.not(self), true));
            // Allow response(s)
            action.allowResponses("Take a card from Force Pile into hand",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromForcePileEffect(action,  playerId, true));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Keep_Your_Eyes_Open)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.isDuringBattleAt(game, Filters.site)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}