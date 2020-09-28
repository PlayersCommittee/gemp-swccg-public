package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.InsteadOfForceDrainingEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 11
 * Type: Location
 * Subtype: Site
 * Title: Maz's Castle: Hidden Recess
 */

public class Card211_039 extends AbstractSite {
    public Card211_039() {
        super(Side.LIGHT, Title.Hidden_Recess, Title.Takodana);
        setLocationDarkSideGameText("Instead of Force draining here, may retrieve a weapon or device");
        setLocationLightSideGameText("Once per game, if you control, may retrieve a weapon or device into hand");
        addKeyword(Keyword.MAZS_CASTLE_LOCATION);
        addIcon(Icon.DARK_FORCE, 0);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PLANET, Icon.INTERIOR_SITE, Icon.UNDERGROUND, Icon.EPISODE_VII, Icon.VIRTUAL_SET_11);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId)
    {
        GameTextActionId gameTextActionId = GameTextActionId.HIDDEN_RECESS__RETRIEVE_A_WEAPON_OR_DEVICE_INTO_HAND;

        if (GameConditions.controls(game, playerOnLightSideOfLocation, self)
            && GameConditions.canSearchLostPile(game, playerOnLightSideOfLocation, self, gameTextActionId)
            && GameConditions.isOncePerGame(game, self, gameTextActionId))
        {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve a weapon or device into hand");
            action.setActionMsg("Retrieve a weapon or device into hand");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new RetrieveCardIntoHandEffect(action, self.getOwner(), Filters.or(Filters.weapon, Filters.device)));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId)
    {
        if (GameConditions.canInsteadOfForceDrainingAtLocation(game, playerOnDarkSideOfLocation, self)
                && GameConditions.hasLostPile(game, playerOnDarkSideOfLocation)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId);
            action.setText("Retrieve a weapon or device instead of force draining");
            action.setActionMsg("Retrieve a weapon or device instead of force draining");
            action.appendEffect(new InsteadOfForceDrainingEffect(action, self, new RetrieveCardEffect(action, playerOnDarkSideOfLocation, Filters.or(Filters.weapon, Filters.device))));
            return Collections.singletonList(action);
        }
        return null;
    }
}