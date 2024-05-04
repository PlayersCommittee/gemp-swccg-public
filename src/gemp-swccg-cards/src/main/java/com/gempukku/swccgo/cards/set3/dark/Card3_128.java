package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Imperial Supply
 */
public class Card3_128 extends AbstractUsedOrLostInterrupt {
    public Card3_128() {
        super(Side.DARK, 3, Title.Imperial_Supply, Uniqueness.UNIQUE, ExpansionSet.HOTH, Rarity.C1);
        setLore("The Empire's efficient, fear-driven logistical systems can quickly divert weapons and equipment from one conflict to another, focusing on regions of highest strategic priority.");
        setGameText("USED: Lose 1 Force to activate 2 Force. LOST: Lose 1 Force to activate 4 Force.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canActivateForce(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Activate 2 Force");
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ActivateForceEffect(action, playerId, 2));
                        }
                    }
            );
            actions.add(action);

            final PlayInterruptAction action2 = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action2.setText("Activate 4 Force");
            // Pay cost(s)
            action2.appendCost(
                    new LoseForceEffect(action2, playerId, 1, true));
            // Allow response(s)
            action2.allowResponses(
                    new RespondablePlayCardEffect(action2) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action2.appendEffect(
                                    new ActivateForceEffect(action2, playerId, 4));
                        }
                    }
            );
            actions.add(action2);
        }
        return actions;
    }
}