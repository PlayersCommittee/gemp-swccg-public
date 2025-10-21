package com.gempukku.swccgo.cards.set222.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeRevivedModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 22
 * Type: Interrupt
 * Subtype: Lost
 * Title: Your Powers Are Weak, Old Man (V)
 */
public class Card222_018 extends AbstractLostInterrupt {
    public Card222_018() {
        super(Side.DARK, 5, "Your Powers Are Weak, Old Man", Uniqueness.UNRESTRICTED, ExpansionSet.SET_22, Rarity.V);
        setVirtualSuffix(true);
        setLore("'You should not have come back.'");
        setGameText("If opponent just lost a battle, they lose 2 Force. " +
                "OR During battle, if Obi-Wan is participating (or 'communing'), " +
                "add one battle destiny; characters may not be 'revived' this turn. " +
                "OR Cancel Clash Of Sabers targeting your Dark Jedi.");
        addIcon(Icon.VIRTUAL_SET_22);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.isDuringBattle(game) &&
                (GameConditions.isDuringBattleWithParticipant(game, Filters.ObiWan)
                        || GameConditions.canSpot(game, self, Filters.and(Filters.Communing, Filters.hasStacked(Filters.ObiWan))))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add one battle destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddBattleDestinyEffect(action, 1));
                            action.appendEffect(
                                    new AddUntilEndOfTurnModifierEffect(action,
                                            new MayNotBeRevivedModifier(self, Filters.opponents(playerId)), "" +
                                            "Prevents characters from being 'revived' until end of turn)")
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, final SwccgGame game, Effect effect, final PhysicalCard self) {

        // Check condition(s)
        if (TriggerConditions.isPlayingCardTargeting(game, effect, Filters.Clash_Of_Sabers, Filters.and(Filters.your(playerId), Filters.Dark_Jedi))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.lostBattle(game, effectResult, opponent)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make opponent lose 2 Force");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new LoseForceEffect(action, opponent, 2));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}