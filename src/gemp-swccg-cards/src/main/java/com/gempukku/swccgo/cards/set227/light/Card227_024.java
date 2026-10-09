package com.gempukku.swccgo.cards.set227.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromBottomOfForcePileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeDestinyCardIntoHandEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 27
 * Type: Interrupt
 * Subtype: Used
 * Title: The Force Will Be With You, Always
 */
public class Card227_024 extends AbstractUsedInterrupt {
    public Card227_024() {
        super(Side.LIGHT, 4, "The Force Will Be With You, Always", Uniqueness.UNIQUE, ExpansionSet.SET_27, Rarity.V);
        setLore("You can't win Darth. If you strike me down I shall become more powerful than you can possibly imagine.' Obi-Wan's sacrifice gave the Rebels time to escape.");
        setGameText("If a Jedi 'communing,' choose: [Upload] Coruscant: Jedi Temple. OR Once per game, draw bottom card of your Force Pile. OR Once per game, take your just drawn destiny into hand to cancel and redraw that destiny.");
        addIcons(Icon.VIRTUAL_SET_27);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.THE_FORCE_WILL_BE_WITH_YOU_ALWAYS__UPLOAD_JEDI_TEMPLE;

        // If a non-Anakin Jedi is Communing...
        if (game.getModifiersQuerying().isCommuning(game.getGameState(), Filters.Jedi)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take Jedi Temple into hand from Reserve Deck");
            action.setActionMsg("Take Coruscant: Jedi Temple into hand from Reserve Deck");
            action.allowResponses(
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                            new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Coruscant_Jedi_Temple, true));
                    }
                }
            );
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.THE_FORCE_WILL_BE_WITH_YOU_ALWAYS__DRAW_BOTTOM_CARD_OF_FORCE_PILE;

        // If a non-Anakin Jedi is Communing...
        if (game.getModifiersQuerying().isCommuning(game.getGameState(), Filters.Jedi)
            && GameConditions.isOncePerGame(game, self, gameTextActionId)
            && GameConditions.hasForcePile(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Draw bottom card of Force Pile");
            action.setActionMsg("Draw bottom card of Force Pile");
            // Update usage limit(s)
            action.appendUsage(
                new OncePerGameEffect(action));
            // Allow responses
            action.allowResponses(
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                            new DrawCardIntoHandFromBottomOfForcePileEffect(action, playerId));
                    }
                }
            );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.THE_FORCE_WILL_BE_WITH_YOU_ALWAYS__REDRAW_DESTINY;

        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
            && game.getModifiersQuerying().isCommuning(game.getGameState(), Filters.Jedi) // If a non-Anakin Jedi is Communing...
            && GameConditions.isOncePerGame(game, self, gameTextActionId)
            && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)
            && GameConditions.canTakeDestinyCardIntoHand(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take destiny card into hand and cause re-draw");
            // Update usage limit(s)
            action.appendUsage(
                new OncePerGameEffect(action));
            // Allow responses
            action.allowResponses(
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                            new TakeDestinyCardIntoHandEffect(action));
                        action.appendAfterEffect(
                            new CancelDestinyAndCauseRedrawEffect(action));
                    }
                }
            );

            return Collections.singletonList(action);
        }
        return null;
    }
}
