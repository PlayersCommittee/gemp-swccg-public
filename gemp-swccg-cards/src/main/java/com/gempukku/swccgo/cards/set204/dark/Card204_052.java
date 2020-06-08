package com.gempukku.swccgo.cards.set204.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 4
 * Type: Location
 * Subtype: Site
 * Title: Jakku: Landing Site
 */
public class Card204_052 extends AbstractSite {
    public Card204_052() {
        super(Side.DARK, Title.Jakku_Landing_Site, Title.Jakku);
        setLocationDarkSideGameText("Once per turn, if your First Order character here, may [download] a Jakku battleground.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.EPISODE_VII, Icon.VIRTUAL_SET_4);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.JAKKU_LANDING_SITE__DOWNLOAD_JAKKU_BATTLEGROUND;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isHere(game, self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.First_Order_character))
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnDarkSideOfLocation, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a Jakku battleground from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Jakku_location, Filters.battleground, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}