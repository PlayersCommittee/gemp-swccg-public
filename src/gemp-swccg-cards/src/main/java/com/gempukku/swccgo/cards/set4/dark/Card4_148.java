package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddToAttritionEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Used
 * Title: Something Hit Us!
 */
public class Card4_148 extends AbstractUsedInterrupt {
    public Card4_148() {
        super(Side.DARK, 3, "Something Hit Us!", Uniqueness.UNRESTRICTED, ExpansionSet.DAGOBAH, Rarity.U);
        setLore("'That was no laser blast...'");
        setGameText("During a battle, lose 1 Force. Cumulatively adds 1 to attrition against opponent.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isInitialAttritionJustCalculated(game, effectResult)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add 1 to attrition against opponent");
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
                                    new AddToAttritionEffect(action, opponent, 1, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}