package com.gempukku.swccgo.cards.set217.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Interrupt
 * Subtype: Used
 * Title: Everything We Need
 */
public class Card217_035 extends AbstractUsedInterrupt {
    public Card217_035() {
        super(Side.LIGHT, 4, "Everything We Need", Uniqueness.UNIQUE, ExpansionSet.SET_17, Rarity.V);
        setGameText("If My Parents Were Strong on table, choose: [upload] Saddle or a Kef Bir site. OR During battle, add X to your total power, where X = number of your cards out of play.");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_17);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.EVERYTHING_WE_NEED__UPLOAD_SITE;

        if (GameConditions.canSpot(game, self, Filters.title("My Parents Were Strong"))) {
            if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
                action.setText("Take site into hand from Reserve Deck");
                // Allow response(s)
                action.allowResponses("Take Saddle or a Kef Bir site into hand from Reserve Deck",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.title(Title.Saddle), Filters.Kef_Bir_site), true));
                            }
                        }
                );
                actions.add(action);
            }

            if (GameConditions.isDuringBattle(game)) {

                final int outOfPlay = Filters.filter(game.getGameState().getAllOutOfPlayCards(), game, Filters.your(self)).size();

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add " + outOfPlay + " to power");

                action.allowResponses(new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(
                                new ModifyTotalPowerUntilEndOfBattleEffect(action, outOfPlay, playerId, "Adds " + outOfPlay + " to total power"));
                    }
                });

                actions.add(action);

            }
        }
        return actions;
    }
}