package com.gempukku.swccgo.cards.set209.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 9
 * Type: Location
 * Subtype: Site
 * Title: Xizor's Palace: Sewer
 */
public class Card209_051 extends AbstractSite {
    public Card209_051() {
        super(Side.DARK, Title.Sewer, Title.Xizors_Palace);
        setLocationDarkSideGameText("Once per game, if you occupy three battlegrounds, may retrieve a Black Sun agent into hand.");
        setLocationLightSideGameText("Once per game, if you control, may retrieve [Reflections II] Dash into hand.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(final String playerOnDarkSideOfLocation, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {

        GameTextActionId gameTextActionId = GameTextActionId.XIZORS_PALACE_SEWER__DOWNLOAD_BLACK_SUN_AGENT_FROM_LOST_PILE;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canSearchLostPile(game, playerOnDarkSideOfLocation, self, gameTextActionId, true)
                && GameConditions.occupies(game, playerOnDarkSideOfLocation, 3, Filters.battleground)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve Black Sun agent into hand");
            action.setActionMsg("Retrieve a Black Sun agent into hand");
            // Update usage limit(s)
            action.appendUsage(new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardIntoHandEffect(action, playerOnDarkSideOfLocation, Filters.Black_Sun_agent));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {

        GameTextActionId gameTextActionId = GameTextActionId.XIZORS_PALACE_SEWER__DOWNLOAD_DASH_FROM_LOST_PILE;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canSearchLostPile(game, playerOnLightSideOfLocation, self, gameTextActionId, true)
                && GameConditions.controls(game, playerOnLightSideOfLocation, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve [Reflections II] Dash into hand");
            action.setActionMsg("Retrieve [Reflections II] Dash into hand");
            // Update usage limit(s)
            action.appendUsage(new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardIntoHandEffect(action, playerOnLightSideOfLocation, Filters.and(Filters.Dash, Icon.REFLECTIONS_II)));
            return Collections.singletonList(action);
        }
        return null;
    }
}