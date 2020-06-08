package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfEpicEventModifierEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.modifiers.AttackRunTotalModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Used
 * Title: Old Times
 */
public class Card7_095 extends AbstractUsedInterrupt {
    public Card7_095() {
        super(Side.LIGHT, 5, "Old Times", Uniqueness.UNIQUE);
        setLore("'I'll be right up there with you! And, have I got stories to tell you...'");
        setGameText("If Luke and Biggs are defending a battle together (or are in any battle together at Tatooine), add one battle destiny. OR If Luke and Biggs are making an Attack Run, add one destiny to your Attack Run total.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.Luke, Filters.or(Filters.defendingBattle, Filters.at(Title.Tatooine))))
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.Biggs, Filters.or(Filters.defendingBattle, Filters.at(Title.Tatooine))))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

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
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelAttackRunActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringAttackRunWithParticipant(game, Filters.Luke)
                && GameConditions.isDuringAttackRunWithParticipant(game, Filters.Biggs)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add one destiny to Attack Run total");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DrawDestinyEffect(action, playerId) {
                                        @Override
                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                            if (totalDestiny == null) {
                                                return;
                                            }
                                            action.appendEffect(
                                                    new AddUntilEndOfEpicEventModifierEffect(action,
                                                            new AttackRunTotalModifier(self, totalDestiny), "Adds " + GuiUtils.formatAsString(totalDestiny) + " to Attack Run total"));
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