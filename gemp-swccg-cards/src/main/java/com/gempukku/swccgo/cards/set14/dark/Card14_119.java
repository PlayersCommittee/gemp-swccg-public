package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;

import java.util.Collections;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Starship
 * Subtype: Capital
 * Title: Trade Federation Landing Craft
 */
public class Card14_119 extends AbstractCapitalStarship {
    public Card14_119() {
        super(Side.DARK, 2, 1, 2, 4, null, null, 3, "Trade Federation Landing Craft");
        setLore("C-9979 landing craft designed to transport and unload Multi Troop Transports and its battle droid cargo as rapidly as possible.");
        setGameText("May add 2 pilots and 6 vehicles. Deploys and moves like a starfighter. While at a site, once during your deploy phase may take an MTT into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.TRADE_FEDERATION, Icon.PILOT, Icon.NAV_COMPUTER, Icon.PRESENCE);
        addKeywords(Keyword.NO_HYPERDRIVE);
        addModelType(ModelType.TRADE_FEDERATION_LANDING_CRAFT);
        setPilotCapacity(2);
        setVehicleCapacity(6);
    }

    @Override
    public boolean isDeploysAndMovesLikeStarfighter() {
        return true;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot() {});
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.TRADE_FEDERATION_LANDING_CRAFT__UPLOAD_MTT;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.isAtLocation(game, self, Filters.site)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take an MTT into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.MTT, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
