package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyAboutToBeDrawnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromTopOfForcePileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToUseCombatCardInsteadOfDestinyDrawResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections III
 * Type: Interrupt
 * Subtype: Used
 * Title: Strike Blocked
 */
public class Card13_043 extends AbstractUsedInterrupt {
    public Card13_043() {
        super(Side.LIGHT, 3, "Strike Blocked", Uniqueness.UNIQUE);
        setLore("At times it was calculated defense, at others it was simply survival.");
        setGameText("Take top card of Force Pile into hand. OR If opponent just revealed a combat card, it is canceled (place in opponent's Used Pile) and opponent must draw destiny instead (that destiny is reduced by 1). OR Cancel Maul Strikes, Dark Strike, or You Are Beaten.");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.hasForcePile(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Take top card of Force Pile into hand");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromTopOfForcePileEffect(action, playerId));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Maul_Strikes)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Maul_Strikes, Title.Maul_Strikes);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Dark_Strike)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Dark_Strike, Title.Dark_Strike);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.You_Are_Beaten)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.You_Are_Beaten, Title.You_Are_Beaten);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Maul_Strikes, Filters.Dark_Strike, Filters.You_Are_Beaten))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isAboutToUseCombatCardInsteadOfDestinyDraw(game, effectResult, opponent)) {
            final AboutToUseCombatCardInsteadOfDestinyDrawResult result = (AboutToUseCombatCardInsteadOfDestinyDrawResult) effectResult;
            final PhysicalCard cardToUse = result.getCardToUseInstead();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel attempt to use combat card");
            // Allow response(s)
            action.allowResponses("Cancel attempt to use combat card, " + GameUtils.getCardLink(cardToUse) + ", as destiny draw",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            result.getPreventableCardEffect().preventEffectOnCard(cardToUse);
                            action.appendEffect(
                                    new SendMessageEffect(action, playerId + " canceled attempt to use combat card, " + GameUtils.getCardLink(cardToUse) + ", as destiny draw"));
                            action.appendEffect(
                                    new ModifyDestinyAboutToBeDrawnEffect(action, -1));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}