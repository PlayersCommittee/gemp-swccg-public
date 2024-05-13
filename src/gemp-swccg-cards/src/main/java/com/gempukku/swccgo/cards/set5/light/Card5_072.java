package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.ModifyTotalWeaponDestinyBeforeDrawingDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used
 * Title: Swing-And-A-Miss
 */
public class Card5_072 extends AbstractUsedInterrupt {
    public Card5_072() {
        super(Side.LIGHT, 3, Title.Swing_And_A_Miss, Uniqueness.RESTRICTED_3, ExpansionSet.CLOUD_CITY, Rarity.U);
        setLore("It's the bottom of the ninth level of Cloud City. Vader steps up to the platform. Here's the delivery. . . oooh, he took a big cut there! The Force sure wasn't with him on that one!");
        setGameText("If opponent just used a lightsaber to target, draw one destiny and subtract that amount from opponent's weapon destiny. OR Cancel one Mostly Armless card just played. OR Cancel Dark Strike, End This Destructive Conflict or Focused Attack.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isFiringWeapon(game, effect, opponent, Filters.lightsaber)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Subtract from weapon destiny");
            // Allow response(s)
            action.allowResponses("Draw one destiny and subtract from opponent's weapon destiny",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DrawDestinyEffect(action, playerId) {
                                        @Override
                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                            if (totalDestiny != null && totalDestiny > 0) {
                                                action.appendEffect(
                                                        new ModifyTotalWeaponDestinyBeforeDrawingDestinyEffect(action, -totalDestiny));
                                            }
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Mostly_Armless, Filters.Dark_Strike, Filters.End_This_Destructive_Conflict, Filters.Focused_Attack))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Dark_Strike)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Dark_Strike, Title.Dark_Strike);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.End_This_Destructive_Conflict)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.End_This_Destructive_Conflict, Title.End_This_Destructive_Conflict);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Focused_Attack)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Focused_Attack, Title.Focused_Attack);
            actions.add(action);
        }
        return actions;
    }
}