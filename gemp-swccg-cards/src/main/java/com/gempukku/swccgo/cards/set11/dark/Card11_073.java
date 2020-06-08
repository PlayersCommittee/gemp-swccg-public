package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LookAtLostPileEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardsOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Effect
 * Title: Ni Chuba Na??
 */
public class Card11_073 extends AbstractNormalEffect {
    public Card11_073() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Ni Chuba Na??", Uniqueness.UNIQUE);
        setLore("'Your buddy here was about to be turned into orange goo. He picked a fight with a Dug. An especially dangerous Dug called Sebulba.'");
        setGameText("Deploy on table. Once per turn may use 1 Force to look through opponent's Lost Pile and place all docking bays you find there out of play. Once per game may deploy Sebulba from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.hasLostPile(game, opponent)
                && GameConditions.canUseForce(game, playerId, 1)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Look at opponent's Lost Pile");
            action.setActionMsg("Look at opponent's Lost Pile and place any docking bays found out of play");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new LookAtLostPileEffect(action, playerId, opponent) {
                        @Override
                        protected void cardsInCardPile(List<PhysicalCard> cardsInCardPile) {
                            Collection<PhysicalCard> dockingBays = Filters.filter(cardsInCardPile, game, Filters.docking_bay);
                            if (!dockingBays.isEmpty()) {
                                action.appendEffect(
                                        new PlaceCardsOutOfPlayFromOffTableEffect(action, dockingBays));
                            }
                        }
                    });
            actions.add(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.NI_CHUBA_NA__DOWNLOAD_SEBULBA;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Sebulba)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Sebulba from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Sebulba, true));
            actions.add(action);
        }
        return actions;
    }
}