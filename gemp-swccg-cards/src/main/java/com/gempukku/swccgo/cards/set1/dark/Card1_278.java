package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: We're All Gonna Be A Lot Thinner!
 */
public class Card1_278 extends AbstractLostInterrupt {
    public Card1_278() {
        super(Side.DARK, 6, Title.Were_All_Gonna_Be_A_Lot_Thinner);
        setLore("Trash compactors crush waste before it is jettisoned into space. Magnetically sealed to prevent leakage. R2-D2 saved the day by shutting down compactor 3263827.");
        setGameText("Everything in Trash Compactor is crushed (lost).");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        PhysicalCard trashCompactor = Filters.findFirstFromTopLocationsOnTable(game, Filters.Trash_Compactor);
        if (trashCompactor != null) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make everything in Trash Compactor lost");
            action.addAnimationGroup(trashCompactor);
            // Allow response(s)
            action.allowResponses("Make everything in " + GameUtils.getCardLink(trashCompactor) + " lost",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            Collection<PhysicalCard> cardsToMakeLost = Filters.filterAllOnTable(game,
                                    Filters.or(Filters.and(Filters.not(Filters.Epic_Event), Filters.not(Filters.Effect_of_any_Kind), Filters.at(Filters.Trash_Compactor))));
                            if (!cardsToMakeLost.isEmpty()) {
                                // The cards are all lost
                                action.appendEffect(
                                        new LoseCardsFromTableEffect(action, cardsToMakeLost, true));
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}