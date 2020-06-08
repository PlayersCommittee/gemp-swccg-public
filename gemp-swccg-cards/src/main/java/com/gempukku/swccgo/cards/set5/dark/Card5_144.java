package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TransferEscortedCaptiveToNewEscortEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: He's All Yours, Bounty Hunter
 */
public class Card5_144 extends AbstractUsedOrLostInterrupt {
    public Card5_144() {
        super(Side.DARK, 4, "He's All Yours, Bounty Hunter");
        setLore("Once Skywalker had taken the bait, Han was of no use to the Empire.");
        setGameText("USED: Cancel Captive Pursuit. LOST: During your move phase, transfer an escorted captive to another bounty hunter or warrior present.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Captive_Pursuit)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Captive_Pursuit)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Captive_Pursuit, Title.Captive_Pursuit);
            actions.add(action);
        }

        Filter filter = Filters.and(Filters.canEscortCaptiveCurrentlyEscortedByPresentWith(self), Filters.or(Filters.bounty_hunter, Filters.warrior));

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE)
                && GameConditions.canSpot(game, self, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Transfer an escorted captive");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose new escort", filter) {
                        @Override
                        protected void cardSelected(final PhysicalCard newEscort) {
                            action.appendTargeting(
                                    new ChooseCardOnTableEffect(action, playerId, "Choose captive to transfer", SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.canBeEscortedBy(newEscort), Filters.captive, Filters.at(Filters.wherePresent(newEscort)))) {
                                        @Override
                                        protected void cardSelected(final PhysicalCard captive) {
                                            action.addAnimationGroup(captive);
                                            action.addAnimationGroup(newEscort);
                                            // Allow response(s)
                                            action.allowResponses("Transfer " + GameUtils.getCardLink(captive) + " to " + GameUtils.getCardLink(newEscort),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new TransferEscortedCaptiveToNewEscortEffect(action, captive, newEscort));
                                                        }
                                                    }
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}