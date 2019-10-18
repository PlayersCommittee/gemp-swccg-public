package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 11
 * Type: Location
 * Subtype: Site
 * Title: Takodana: Maz's Castle
 */
public class Card211_038 extends AbstractSite {
    public Card211_038() {
        super(Side.LIGHT, Title.Mazs_Castle, Title.Takodana);
        setLocationDarkSideGameText("If you control, opponent must use 1 Force to use their Takodana: Maz's Castle game text.");
        setLocationLightSideGameText("Oce per turn, may [download] a Maz's Castle site.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.VIRTUAL_SET_11, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.EPISODE_VII, Icon.VIRTUAL_SET_11);
        addKeyword(Keyword.MAZS_CASTLE_LOCATION);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.MAZS_CASTLE__DOWNLOAD_MAZS_CASTLE_SITE;
        String playerOnDarkSideOfLocation = game.getOpponent(playerOnLightSideOfLocation);
        int cost;
        if (GameConditions.controls(game, playerOnDarkSideOfLocation, self))
            cost = 1;
        else
            cost = 0;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnLightSideOfLocation, self, gameTextActionId)
                && GameConditions.isOnceDuringYourTurn(game, self, self.getOwner(), gameTextSourceCardId, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);

            if (cost == 0)
                action.setText("Deploy a Maz's Castle site from Reserve Deck");
            else
                action.setText("Use 1 force to deploy a Maz's Castle site from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendCost(new UseForceEffect(action, self.getOwner(), cost));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Mazs_Castle_Location, Filters.site), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}