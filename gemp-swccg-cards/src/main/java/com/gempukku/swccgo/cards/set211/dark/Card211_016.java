package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.ModifyTotalBattleDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelWeaponDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Interrupt
 * Subtype: Used
 * Title: Vader's Anger (V)
 */
public class Card211_016 extends AbstractUsedInterrupt {
    public Card211_016() {
        super(Side.DARK, 5, "Vader's Anger", Uniqueness.UNIQUE);
        setLore("Anger and aggression fuel the dark side of the Force.");
        setGameText("For remainder of turn, opponent may not cancel your lightsaber weapon (or ‘choke’) destiny draws. OR If Vader in battle alone, your total battle destiny is +1 for each character in battle. OR Cancel It’s A Trap.");
        addIcons(Icon.TATOOINE, Icon.VIRTUAL_SET_11);
        setVirtualSuffix(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Its_A_Trap)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Its_A_Trap, Title.Its_A_Trap);
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.Vader, Filters.alone))) {
            final int count = Filters.countActive(game, self, Filters.and(Filters.character, Filters.participatingInBattle));
            if (count > 0) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Add " + count + " to total battle destiny");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new ModifyTotalBattleDestinyEffect(action, playerId, count));
                            }
                        }
                );
                actions.add(action);
            }
        }

        final PlayInterruptAction action2 = new PlayInterruptAction(game, self);
        action2.setText("Prevent Lightsaber weapon and 'choke' destinies from being cancelled for remainder of turn.");

        action2.allowResponses(
                new RespondablePlayCardEffect(action2) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action2.appendEffect(
                                new AddUntilEndOfTurnModifierEffect(action2,
                                        new MayNotCancelWeaponDestinyModifier(self, game.getOpponent(playerId), Filters.and(Filters.your(playerId), Filters.lightsaber)), "")
                        );
                    }
                }
        );
        actions.add(action2);

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Its_A_Trap)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        return actions;
    }
}