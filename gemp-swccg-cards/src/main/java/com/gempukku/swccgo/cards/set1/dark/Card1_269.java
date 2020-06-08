package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.SwitchBattleDestinyNumbersEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Takeel
 */
public class Card1_269 extends AbstractLostInterrupt {
    public Card1_269() {
        super(Side.DARK, 3, Title.Takeel);
        setLore("Takeel, a burned-out Snivvian mercenary known as a double-crosser. Spice addicted. Frequents the Cantina looking for work, but has also turned lawbreakers over to the Empire.");
        setGameText("If both players just drew one battle destiny, use 1 Force to switch numbers.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isBattleDestinyDrawingJustCompletedForBothPlayers(game, effectResult)
                && GameConditions.didBothPlayersDrawOneBattleDestiny(game)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Switch battle destiny numbers");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new SwitchBattleDestinyNumbersEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}