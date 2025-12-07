package com.gempukku.swccgo.cards.set226.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 26
 * Type: Location
 * Subtype: Site
 * Title: Jabiim: Path Operations Center
 */
public class Card226_015 extends AbstractSite {
    public Card226_015() {
        super(Side.LIGHT, Title.Path_Operations_Center, Title.Jabiim, Uniqueness.UNIQUE, ExpansionSet.SET_26, Rarity.V);
        setLocationLightSideGameText("Once per turn, may [download] a lightsaber on your character here.");
        setLocationDarkSideGameText("");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 0);
        addIcons(Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_26);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.PATH_OPERATIONS_CENTER__DOWNLOAD_LIGHTSABER;

        if (GameConditions.isOncePerTurn(game, self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnLightSideOfLocation, self, gameTextActionId)
                && GameConditions.isHere(game, self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.character))) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a lightsaber from Reserve Deck");
            action.setActionMsg("Deploy a lightsaber from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.lightsaber, Filters.here(self), true));

            return Collections.singletonList(action);
        }

        return null;
    }
}
