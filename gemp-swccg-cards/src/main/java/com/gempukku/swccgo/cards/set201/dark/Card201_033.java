package com.gempukku.swccgo.cards.set201.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleDestinyUnlessRedrawnModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotModifyBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 1
 * Type: Interrupt
 * Subtype: Used
 * Title: A Dark Time For The Rebellion (V)
 */
public class Card201_033 extends AbstractUsedInterrupt {
    public Card201_033() {
        super(Side.DARK, 4, "A Dark Time For The Rebellion", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Absolute control wielded by the Emperor enables the Imperial forces to dominate planetary systems before the Rebel Alliance can gain a foothold.");
        setGameText("If opponent's starting location was Massassi Throne Room, opponent loses 1 Force when you play this Interrupt. Add or subtract 1 from opponent's just drawn destiny. OR Activate 1 Force. OR Until end of turn, battle destiny draws may not be modified or canceled (unless being redrawn).");
        addIcons(Icon.HOTH, Icon.VIRTUAL_SET_1);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredInterruptPlayedTriggers(SwccgGame game, Effect effect, PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        final PhysicalCard startingLocation = game.getModifiersQuerying().getStartingLocation(opponent);
        if (startingLocation != null && Filters.Massassi_Throne_Room.accepts(game, startingLocation)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, self.getCardId());
            action.setText("Make " + opponent + " lose 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, opponent)) {

            final PlayInterruptAction action1 = new PlayInterruptAction(game, self);
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

            final PlayInterruptAction action2 = new PlayInterruptAction(game, self);
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

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canActivateForce(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
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
            actions.add(action);
        }

        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Affect battle destiny draws");
        action.setActionMsg("Prevent all battle destiny draws from being modified or canceled (unless being redrawn)");
        // Allow response(s)
        action.allowResponses(
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                                new AddUntilEndOfTurnModifierEffect(action,
                                        new MayNotModifyBattleDestinyModifier(self), "Prevents all battle destiny draws from being modified"));
                        action.appendEffect(
                                new AddUntilEndOfTurnModifierEffect(action,
                                        new MayNotCancelBattleDestinyUnlessRedrawnModifier(self), "Prevents all battle destiny draws from being canceled (unless being redrawn)"));
                    }
                }
        );
        actions.add(action);

        return actions;
    }
}