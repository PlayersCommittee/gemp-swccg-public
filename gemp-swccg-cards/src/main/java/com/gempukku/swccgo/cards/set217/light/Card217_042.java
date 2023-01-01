package com.gempukku.swccgo.cards.set217.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Interrupt
 * Subtype: Used
 * Title: Now, This Is Podracing!
 */
public class Card217_042 extends AbstractUsedInterrupt {
    public Card217_042() {
        super(Side.LIGHT, 4, "Now, This Is Podracing!", Uniqueness.UNIQUE, ExpansionSet.SET_17, Rarity.V);
        setGameText("During battle, add 1 to opponent's just drawn destiny (or to your just drawn weapon destiny). OR If Your Thoughts Dwell On Your Mother on table, [upload] Night Club or Skywalker Hut.");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_17);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, opponent)
                || TriggerConditions.isWeaponDestinyJustDrawnBy(game, effectResult, playerId))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add 1 to destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ModifyDestinyEffect(action, 1));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.GO_BACK_TO_YOUR_DRINKS__UPLOAD_SITE;

        if (GameConditions.canSpot(game, self, Filters.title("Your Thoughts Dwell On Your Mother"))
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take site into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take Skywalker Hut or Nightclub into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.title("Tatooine: Skywalker Hut"), Filters.Nightclub), true));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}