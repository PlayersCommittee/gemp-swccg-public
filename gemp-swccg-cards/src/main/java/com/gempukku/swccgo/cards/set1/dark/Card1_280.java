package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
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
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Your Powers Are Weak, Old Man
 */
public class Card1_280 extends AbstractLostInterrupt {
    public Card1_280() {
        super(Side.DARK, 5, "Your Powers Are Weak, Old Man", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.R1);
        setLore("'You should not have come back.'");
        setGameText("During any battle involving both Vader and Obi-Wan, you may add one battle destiny. OR Destroy (cancel) either: Return Of A Jedi, Jedi Lightsaber, Jedi Presence, or Old Ben.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.Vader)
                && GameConditions.canAddBattleDestinyDraws(game, self)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.ObiWan)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add one battle destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddBattleDestinyEffect(action, 1));
                        }
                    }
            );
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Return_Of_A_Jedi)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Return_Of_A_Jedi, Title.Return_Of_A_Jedi);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Jedi_Lightsaber)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Jedi_Lightsaber, Title.Jedi_Lightsaber);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Jedi_Presence)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Jedi_Presence, Title.Jedi_Presence);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Old_Ben)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Old_Ben, Title.Old_Ben);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, final SwccgGame game, Effect effect, final PhysicalCard self) {
        Filter filter = Filters.or(Filters.Return_Of_A_Jedi, Filters.Jedi_Lightsaber, Filters.Jedi_Presence, Filters.Old_Ben);

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