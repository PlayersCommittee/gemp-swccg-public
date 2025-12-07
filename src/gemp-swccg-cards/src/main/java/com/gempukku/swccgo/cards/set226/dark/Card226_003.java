package com.gempukku.swccgo.cards.set226.dark;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.PutCardsFromHandOnForcePileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

/**
 * Set: Set 26
 * Type: Interrupt
 * Subtype: Used
 * Title: Endless Legions
 */
public class Card226_003 extends AbstractUsedInterrupt {
    public Card226_003() {
        super(Side.DARK, 4, "Endless Legions", Uniqueness.UNIQUE, ExpansionSet.SET_26, Rarity.V);
        setLore("");
        setGameText("If your stormtroopers occupy three battleground and/or Rebel Base locations, choose: Draw three cards from Force Pile, then place two cards from hand on Force Pile. OR Once per game, if you just lost a non-[Maintenance] stormtrooper, take it into hand.");
        addIcons(Icon.VIRTUAL_SET_26);
    }
    
    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.occupiesWith(game, self, playerId, 3, Filters.battleground, Filters.stormtrooper)
                && GameConditions.hasForcePile(game, playerId)
                && GameConditions.hasHand(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);

            action.setText("Draw three cards from Force Pile");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new DrawCardsIntoHandFromForcePileEffect(action, playerId, 3) {
                                @Override
                                protected void cardsDrawnIntoHand(Collection<PhysicalCard> cards) {
                                    if (cards.size()>=3) {
                                        action.appendEffect(new PutCardsFromHandOnForcePileEffect(action, playerId, 2, 2));
                                    }
                                    else {
                                        action.appendEffect(new SendMessageEffect(action, playerId + " failed to draw three cards."));
                                    }
                                }
                            });
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.ENDLESS_LEGIONS__TAKE_JUST_LOST_STORMTROOPER_INTO_HAND;

        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, Filters.and(Filters.your(self), Filters.not(Icon.MAINTENANCE), Filters.stormtrooper))
                && GameConditions.occupiesWith(game, self, playerId, 3, Filters.battleground, Filters.stormtrooper)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)) {

            final PhysicalCard justLostCard = ((LostFromTableResult) effectResult).getCard();

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take " + GameUtils.getFullName(justLostCard) + " into hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Allow response(s)
            action.allowResponses("Take " + GameUtils.getCardLink(justLostCard) + " into hand",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromLostPileEffect(action, playerId, justLostCard, false, true));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
