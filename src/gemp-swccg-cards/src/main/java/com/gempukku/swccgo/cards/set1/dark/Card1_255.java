package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Limited Resources
 */
public class Card1_255 extends AbstractLostInterrupt {
    public Card1_255() {
        super(Side.DARK, 5, Title.Limited_Resources, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.U2);
        setLore("The Empire dominates consumption of resources. Despite being efficient, Rebel logistical and maintenance officers often are compelled to expend emergency reserves.");
        setGameText("If the opponent has two cards or less in hand, opponent must immediately lose 2 Force (4 Force if it is your turn). If the opponent has Fusion Generator Supply Tanks aboard a starship, loss is reduced by 2.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.numCardsInHand(game, opponent) <= 2) {
            int amount = (GameConditions.isDuringYourTurn(game, playerId) ? 4 : 2);
            if (GameConditions.canSpot(game, self, Filters.and(Filters.opponents(self), Filters.Fusion_Generator_Supply_Tanks, Filters.attachedTo(Filters.starship)))) {
                amount -= 2;
            }
            final int forceToLose = amount;

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make opponent lose " + forceToLose + " Force");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new LoseForceEffect(action, opponent, forceToLose));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}