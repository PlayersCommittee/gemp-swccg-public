package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.DrawsNoMoreThanBattleDestinyEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Interrupt
 * Subtype: Used
 * Title: Rebel Leadership
 */
public class Card9_054 extends AbstractUsedInterrupt {
    public Card9_054() {
        super(Side.LIGHT, 4, "Rebel Leadership", Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.R);
        setLore("In times of greatest need, the Rebellion relies on the brilliant leadership provided by commanders fighting for freedom.");
        setGameText("Each of your admirals and generals is forfeit +1 for remainder of turn. OR If your admiral is in battle at a system (or your general, except Obi-Wan, is in battle at a site), you may either add one battle destiny or prevent opponent from drawing more than one battle destiny.");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        final Filter admiralsAndGeneralsFilter = Filters.and(Filters.your(self), Filters.or(Filters.admiral, Filters.general));

        // Check condition(s)
        if (GameConditions.canSpot(game, self, admiralsAndGeneralsFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make admirals and generals forfeit +1");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddUntilEndOfTurnModifierEffect(action,
                                            new ForfeitModifier(self, admiralsAndGeneralsFilter, 1),
                                            "Makes admirals and generals forfeit +1"));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.admiral, Filters.at(Filters.system)))
                || GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.general, Filters.except(Filters.ObiWan), Filters.at(Filters.site)))) {
            final String opponent = game.getOpponent(playerId);
            if (GameConditions.canAddBattleDestinyDraws(game, self)) {

                final PlayInterruptAction action1 = new PlayInterruptAction(game, self);
                action1.setText("Add one battle destiny");
                // Allow response(s)
                action1.allowResponses(
                        new RespondablePlayCardEffect(action1) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action1.appendEffect(
                                        new AddBattleDestinyEffect(action1, 1));
                            }
                        }
                );
                actions.add(action1);
            }

            final PlayInterruptAction action2 = new PlayInterruptAction(game, self);
            action2.setText("Limit opponent to one battle destiny");
            // Allow response(s)
            action2.allowResponses("Prevent opponent from drawing more than one battle destiny",
                    new RespondablePlayCardEffect(action2) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action2.appendEffect(
                                    new DrawsNoMoreThanBattleDestinyEffect(action2, opponent, 1));
                        }
                    }
            );
            actions.add(action2);
        }
        return actions;
    }
}