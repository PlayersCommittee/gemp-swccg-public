package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 11
 * Type: Interrupt
 * Subtype: Lost
 * Title: Always Two There Are
 */

public class Card211_012 extends AbstractLostInterrupt {
    public Card211_012() {
        super(Side.DARK, 2, Title.Always_Two_There_Are);
        setLore("At last we will reveal ourselves to the Jedi.");
        setGameText("If you just lost a Dark Jedi and have exactly one Dark Jedi on table, take a Dark Jedi into hand from Reserve Deck; reshuffle. OR Once per game, if you have exactly two Dark Jedi on table, choose: Opponent loses 2 Force OR make a just drawn destiny = 2.");
        addIcons(Icon.VIRTUAL_SET_11, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.ALWAYS_TWO_THERE_ARE__LOSE_2_OR_DESTINY_EQUALS_2;
        final String opponent = game.getOpponent(playerId);

        int numDarkJedisOnTable = Filters.countActive(game, self, Filters.Dark_Jedi);
        if ((numDarkJedisOnTable == 2) && GameConditions.isOncePerGame(game, self, GameTextActionId.ALWAYS_TWO_THERE_ARE__LOSE_2_OR_DESTINY_EQUALS_2))
        {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Opponent loses 2 Force");
            action.appendUsage(new OncePerGameEffect(action));
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new LoseForceEffect(action, opponent, 2));
                        }
                    }
            );
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.ALWAYS_TWO_THERE_ARE__LOSE_2_OR_DESTINY_EQUALS_2;

        if (TriggerConditions.isDestinyJustDrawn(game, effectResult)
            && GameConditions.isOncePerGame(game, self, GameTextActionId.ALWAYS_TWO_THERE_ARE__LOSE_2_OR_DESTINY_EQUALS_2))
        {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Set destiny to 2");
            action.appendUsage(new OncePerGameEffect(action));
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new ResetDestinyEffect(action, 2));
                }
            });
            return Collections.singletonList(action);
        }


        if (TriggerConditions.justLost(game, effectResult, Filters.and(Filters.your(self), Filters.Dark_Jedi))
            && (Filters.countActive(game, self, Filters.Dark_Jedi) == 1))
        {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Take a Dark Jedi into hand from reserve");
            // no append usage here because this is not once per game.
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Dark_Jedi, true));
                }
            });
            return Collections.singletonList(action);
        }

        return null;
    }
}