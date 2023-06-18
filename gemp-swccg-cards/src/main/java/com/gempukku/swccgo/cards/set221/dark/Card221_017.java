package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelReactEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Interrupt
 * Subtype: Lost
 * Title: Dark Collaboration (V)
 */
public class Card221_017 extends AbstractLostInterrupt {
    public Card221_017() {
        super(Side.DARK, 5, Title.Dark_Collaboration, Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setVirtualSuffix(true);
        setLore("Imperial leaders are ruthlessly effective when working together. Tarkin, Motti and Tagge presented a deadly combination as they plotted to 'crush the Rebellion.'");
        setGameText("Cancel Rebel Barrier. OR If your non-Ozzel ISB agent leader in battle, cancel It's A Trap! or opponent's 'react' away from that battle. OR If three non-Ozzel ISB agent leaders are in battle together, add two battle destiny.");
        addIcons(Icon.VIRTUAL_SET_21);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && GameConditions.canTarget(game, self, 3, Filters.and(Filters.ISB_agent, Filters.leader, Filters.participatingInBattle, Filters.not(Filters.Ozzel)))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add two battle destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddBattleDestinyEffect(action, 2));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Rebel_Barrier)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Rebel_Barrier, Title.Rebel_Barrier);
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Its_A_Trap)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.ISB_agent, Filters.leader, Filters.not(Filters.Ozzel)))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Its_A_Trap, Title.Its_A_Trap);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Rebel_Barrier)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Its_A_Trap)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.ISB_agent, Filters.leader, Filters.not(Filters.Ozzel)))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }

        if (TriggerConditions.isReact(game, effect)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.ISB_agent, Filters.leader, Filters.not(Filters.Ozzel)))
                && TriggerConditions.isMovingAsReact(game, effect, Filters.and(Filters.opponents(self), Filters.at(Filters.battleLocation)))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel 'react'");
            // Allow responses
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform results
                            action.appendEffect(new CancelReactEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}