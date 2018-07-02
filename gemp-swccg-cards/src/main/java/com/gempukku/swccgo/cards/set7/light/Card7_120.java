package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Location
 * Subtype: Site
 * Title: Farm
 */
public class Card7_120 extends AbstractSite {
    public Card7_120() {
        super(Side.LIGHT, "Farm", Uniqueness.DIAMOND_1);
        setLocationDarkSideGameText("May not be deployed to Bespin, Coruscant, Dagobah, Hoth, Kessel.");
        setLocationLightSideGameText("May not be deployed to Bespin, Coruscant, Dagobah, Hoth, Kessel. During your deploy phase, Hydroponics Station or Vaporator may deploy here from Reserve Deck; reshuffle.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.SPECIAL_EDITION, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeyword(Keyword.FARM);
        addMayNotBePartOfSystem(Title.Bespin, Title.Coruscant, Title.Dagobah, Title.Hoth, Title.Kessel);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.FARM__DOWNLOAD_HYDROPONICS_STATION_OR_VAPORATOR;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnLightSideOfLocation, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy a Hydroponics Station or Vaporator from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.or(Filters.Hydroponics_Station, Filters.Vaporator), Filters.here(self), DeploymentRestrictionsOption.ignoreLocationDeploymentRestrictions(), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}