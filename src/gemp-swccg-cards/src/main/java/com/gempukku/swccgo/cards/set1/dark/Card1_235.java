package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: Boring Conversation Anyway
 */
public class Card1_235 extends AbstractUsedInterrupt {
    public Card1_235() {
        super(Side.DARK, 5, Title.Boring_Conversation_Anyway, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.R1);
        setLore("'Uh...had a slight weapons malfunction. But, uh, everything's perfectly all right now. We're fine. We're all fine here, now, thank you. How are you?'");
        setGameText("Cancel either: Report To Lord Vader, Scomp Link Access, Rebel Planners, Rebel Reinforcements, Gift of the Mentor, Panic, Don't Get Cocky, Skywalkers, Demotion, Combined Attack or Surprise Assault.");
    }

    // TODO: Troy start here...

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Report_To_Lord_Vader)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Report_To_Lord_Vader, Title.Report_To_Lord_Vader);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Scomp_Link_Access)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Scomp_Link_Access, Title.Scomp_Link_Access);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Rebel_Planners)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Rebel_Planners, Title.Rebel_Planners);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Rebel_Reinforcements)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Rebel_Reinforcements, Title.Rebel_Reinforcements);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Gift_Of_The_Mentor)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Gift_Of_The_Mentor, Title.Gift_Of_The_Mentor);
            actions.add(action);
        }
        if (GameConditions.canTargetToCancel(game, self, Filters.Panic)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Panic, Title.Panic);
            actions.add(action);
        }
        if (GameConditions.canTargetToCancel(game, self, Filters.Dont_Get_Cocky)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Dont_Get_Cocky, Title.Dont_Get_Cocky);
            actions.add(action);
        }
        if (GameConditions.canTargetToCancel(game, self, Filters.Skywalkers)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Skywalkers, Title.Skywalkers);
            actions.add(action);
        }
        if (GameConditions.canTargetToCancel(game, self, Filters.Demotion)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Demotion, Title.Demotion);
            actions.add(action);
        }
        if (GameConditions.canTargetToCancel(game, self, Filters.Combined_Attack)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Combined_Attack, Title.Combined_Attack);
            actions.add(action);
        }
        if (GameConditions.canTargetToCancel(game, self, Filters.Surprise_Assault)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Surprise_Assault, Title.Surprise_Assault);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, final SwccgGame game, Effect effect, final PhysicalCard self) {
        Filter filter = Filters.or(Filters.Report_To_Lord_Vader, Filters.Scomp_Link_Access, Filters.Rebel_Planners, Filters.Rebel_Reinforcements,
                Filters.Gift_Of_The_Mentor, Filters.Panic, Filters.Dont_Get_Cocky, Filters.Skywalkers, Filters.Demotion, Filters.Combined_Attack, Filters.Surprise_Assault);

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, filter)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}