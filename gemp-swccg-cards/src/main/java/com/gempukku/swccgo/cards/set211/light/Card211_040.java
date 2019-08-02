package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 11
 * Type: Location
 * Subtype: Site
 * Title: Maz's Castle: Antechamber
 */

public class Card211_040 extends AbstractSite {
    public Card211_040() {
        super(Side.LIGHT, Title.Antechamber, Title.Takodana);
        setLocationLightSideGameText("Once per game, may \\/ an [Episode VII] alien here.");
        setLocationDarkSideGameText("For drain -1 here.  While you control, Dobra Doompa is suspended.");
        addKeyword(Keyword.MAZS_CASTLE_LOCATION);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.PLANET, Icon.INTERIOR_SITE, Icon.EPISODE_VII, Icon.VIRTUAL_SET_11);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId)
    {
        GameTextActionId gameTextActionId = GameTextActionId.ANTE_CHAMBER__DOWNLOAD_EPISODE_7_ALIEN;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
            && GameConditions.canDeployCardFromReserveDeck(game, playerOnLightSideOfLocation, self, gameTextActionId))
        {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Download an Episode VII Alien");
            action.setActionMsg("Download an Episode VII Alien");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new DeployCardToLocationFromReserveDeckEffect(action, Filters.and(Filters.alien, Icon.EPISODE_VII), Filters.here(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self,  -1, playerOnDarkSideOfLocation));
        modifiers.add(new SuspendsCardModifier(self, Filters.Dopra_Doompa));
        return modifiers;
    }
}