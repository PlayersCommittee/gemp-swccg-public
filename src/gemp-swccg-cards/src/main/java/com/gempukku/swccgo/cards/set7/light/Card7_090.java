package com.gempukku.swccgo.cards.set7.light;

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
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardsEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Lost
 * Title: Harvest
 */
public class Card7_090 extends AbstractLostInterrupt {
    public Card7_090() {
        super(Side.LIGHT, 5, Title.Harvest, Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("'Harvest is when I need you the most. It's only one season more. This year we'll make enough on the harvest that I'll be able to hire some more hands.'");
        setGameText("If Luke is in a battle with Owen or Beru, add one battle destiny (two if both). OR If Owen or Beru is on table, retrieve any one card (two if both, four if both present at Lars' Moisture Farm).");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.Luke)
                && GameConditions.canAddBattleDestinyDraws(game, self)) {
            boolean inBattleWithOwen = GameConditions.isDuringBattleWithParticipant(game, Filters.Owen);
            boolean inBattleWithBeru = GameConditions.isDuringBattleWithParticipant(game, Filters.Beru);
            if (inBattleWithOwen || inBattleWithBeru) {
                final int numToAdd = (inBattleWithOwen && inBattleWithBeru) ? 2 : 1;

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                if (numToAdd == 2)
                    action.setText("Add two battle destiny");
                else
                    action.setText("Add one battle destiny");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new AddBattleDestinyEffect(action, numToAdd));
                            }
                        }
                );
                actions.add(action);
            }
        }

        // Check condition(s)
        boolean owenOnTable = GameConditions.canSpot(game, self, Filters.Owen);
        boolean beruOnTable = GameConditions.canSpot(game, self, Filters.Beru);
        boolean bothPresentAtFarm = false;
        if (owenOnTable && beruOnTable) {
            bothPresentAtFarm = (GameConditions.canSpot(game, self, Filters.and(Filters.Owen, Filters.presentAt(Filters.Lars_Moisture_Farm), Filters.mayContributeToForceRetrieval))
                    && GameConditions.canSpot(game, self, Filters.and(Filters.Beru, Filters.presentAt(Filters.Lars_Moisture_Farm), Filters.mayContributeToForceRetrieval)));
        }
        if (owenOnTable || beruOnTable) {
            final int amountToRetrieve = (owenOnTable && beruOnTable) ? (bothPresentAtFarm ? 4 : 2) : 1;

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            if (amountToRetrieve == 4)
                action.setText("Retrieve four cards");
            else if (amountToRetrieve == 2)
                action.setText("Retrieve two cards");
            else
                action.setText("Retrieve one card");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RetrieveCardsEffect(action, playerId, amountToRetrieve));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}