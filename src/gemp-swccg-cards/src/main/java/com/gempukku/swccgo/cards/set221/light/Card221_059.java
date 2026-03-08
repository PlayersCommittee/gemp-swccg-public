package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Interrupt
 * Subtype: Used
 * Title: Eventually You'll Lose (V)
 */
public class Card221_059 extends AbstractUsedInterrupt {
    public Card221_059() {
        super(Side.LIGHT, 4, "Eventually You'll Lose", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setVirtualSuffix(true);
        setLore("In the end, Watto finally came to understand the agony of defeat.");
        setGameText("If Podrace Arena on table, [upload] Jar Jar, Padme, or Skywalker Hut. OR During your control phase, if you have won a Podrace and [Tatooine] Anakin is present at a battleground site, opponent loses 1 Force for each [Dark Side] at that site.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I, Icon.VIRTUAL_SET_21);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.EVENTUALLY_YOULL_LOSE_V__UPLOAD_CARD;

        if (GameConditions.canSpot(game, self, Filters.Podrace_Arena)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take Jar Jar, Padme, or Skywalker Hut into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Amidala, Filters.Jar_Jar, Filters.Skywalker_Hut), true));
                        }
                    }
            );
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        Filter tatooineAnakinPresentAtBGSite = Filters.and(Icon.TATOOINE, Filters.Anakin, Filters.presentAt(Filters.battleground_site));
        String opponent = game.getOpponent(playerId);

        if (GameConditions.isDuringYourPhase(game, playerId, Phase.CONTROL)
                && GameConditions.hasWonPodrace(game, playerId)
                && GameConditions.canTarget(game, self, tatooineAnakinPresentAtBGSite)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            
            int totalDarkIconsAccumulator = 0;
            PhysicalCard tatooineAnakin = Filters.findFirstActive(game, self, Filters.and(Icon.TATOOINE, Filters.Anakin));
            for (PhysicalCard card : Filters.filterActive(game, self, Filters.here(tatooineAnakin))) {
                totalDarkIconsAccumulator += game.getModifiersQuerying().getIconCount(game.getGameState(), card, Icon.DARK_FORCE);
            }
            final int totalDarkIconsHere = totalDarkIconsAccumulator;

            action.setText("Make opponent lose Force");
            // Allow response(s)
            action.allowResponses("Opponent loses 1 Force for each [Dark Side] at Anakin's site",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(final Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new LoseForceEffect(action, opponent, totalDarkIconsHere));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}
