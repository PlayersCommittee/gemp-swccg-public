package com.gempukku.swccgo.cards.set207.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Used or Lost
 * Title: They're Tracking Us (V)
 */
public class Card207_014 extends AbstractUsedOrLostInterrupt {
    public Card207_014() {
        super(Side.LIGHT, 4, "They're Tracking Us", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("'Not this ship, sister.'");
        setGameText("USED: Add or subtract 1 from opponent's just drawn destiny. OR Activate 1 Force. LOST: Cancel Close Call, Gravity Shadow, Overwhelmed, or They've Shut Down The Main Reactor. [Immune to Sense]");
        addIcons(Icon.SPECIAL_EDITION, Icon.VIRTUAL_SET_7);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Close_Call, Filters.Gravity_Shadow, Filters.Overwhelmed, Filters.Theyve_Shut_Down_The_Main_Reactor))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.setImmuneTo(Title.Sense);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.canActivateForce(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Activate 1 Force");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ActivateForceEffect(action, playerId, 1));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }


    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, opponent)) {

            final PlayInterruptAction action1 = new PlayInterruptAction(game, self, CardSubtype.USED);
            action1.setText("Add 1 to destiny");
            // Allow response(s)
            action1.allowResponses(
                    new RespondablePlayCardEffect(action1) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action1.appendEffect(
                                    new ModifyDestinyEffect(action1, 1));
                        }
                    }
            );
            actions.add(action1);

            final PlayInterruptAction action2 = new PlayInterruptAction(game, self, CardSubtype.USED);
            action2.setText("Subtract 1 from destiny");
            // Allow response(s)
            action2.allowResponses(
                    new RespondablePlayCardEffect(action2) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action2.appendEffect(
                                    new ModifyDestinyEffect(action2, -1));
                        }
                    }
            );
            actions.add(action2);

        }
        return actions;
    }
}
