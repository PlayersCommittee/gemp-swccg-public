package com.gempukku.swccgo.cards.set219.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyTotalBattleDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ArtworkCardRevealedResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Interrupt
 * Subtype: Used or Lost
 * Title: History, Philosophy, And Art
 */
public class Card219_009 extends AbstractUsedOrLostInterrupt {
    public Card219_009() {
        super(Side.DARK, 5, "History, Philosophy, And Art", Uniqueness.UNIQUE, ExpansionSet.SET_19, Rarity.V);
        setGameText("USED: During battle, add 1 to your total battle destiny for each artwork card on table. " +
                    "LOST: Once per game, if your [Set 19] objective just 'studied' an Interrupt, you may take one Interrupt into hand from Lost Pile.");
        addIcons(Icon.VIRTUAL_SET_19);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.and(Filters.Thrawns_Art_Collection, Filters.hasStacked(Filters.any)))
                && GameConditions.isDuringBattle(game)) {

            final int num = Filters.countStacked(game, Filters.stackedOn(self, Filters.Thrawns_Art_Collection));
            if (num>0) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Add "+num+" to total battle destiny");
                // Choose target(s)
                action.allowResponses("Add "+num+" to total battle destiny", new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(new ModifyTotalBattleDestinyEffect(action, playerId, num));
                    }
                });
                actions.add(action);
            }
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {

        GameTextActionId gameTextActionId = GameTextActionId.HISTORY_PHILOSOPHY_AND_ART__STOP_INTERRUPTS_AND_WEAPONS;

        if (effectResult.getType() == EffectResult.Type.ARTWORK_CARD_REVEALED
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isDuringBattle(game)
                && GameConditions.hasLostPile(game, playerId)) {

            PhysicalCard artwork = ((ArtworkCardRevealedResult) effectResult).getCard();

            if (artwork != null
                    && Filters.Interrupt.accepts(game, artwork)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
                action.setText("Take Interrupt into hand from Lost Pile");

                action.appendUsage(
                        new OncePerGameEffect(action));

                action.allowResponses("Take Interrupt into hand from Lost Pile", new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(
                                new TakeCardIntoHandFromLostPileEffect(action, playerId, Filters.Interrupt, false)
                        );
                    }
                });

                return Collections.singletonList(action);
            }
        }

        return null;
    }
}
