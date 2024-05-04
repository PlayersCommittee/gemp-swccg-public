package com.gempukku.swccgo.cards.set221.light;

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
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 21
 * Type: Location
 * Subtype: Site
 * Title: Clone Command Center
 */
public class Card221_054 extends AbstractSite {
    public Card221_054() {
        super(Side.LIGHT, Title.Clone_Command_Center, Uniqueness.DIAMOND_1, ExpansionSet.SET_21, Rarity.V);
        setLocationDarkSideGameText("");
        setLocationLightSideGameText("Deploys only if a [Clone Army] objective on table. Once per turn, may [download] a Kamino site or [Clone Army] battleground.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.EPISODE_I, Icon.SCOMP_LINK, Icon.CLONE_ARMY, Icon.VIRTUAL_SET_21);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self) {
        return GameConditions.canSpot(game, self, Filters.and(Icon.CLONE_ARMY, Filters.Objective));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.CLONE_COMMAND_CENTER__DEPLOY_LOCATION;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnLightSideOfLocation, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a location");
            action.setActionMsg("Deploy a Kamino site or a [Clone Army] battleground from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));

            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Kamino_site, Filters.and(Icon.CLONE_ARMY, Filters.battleground)), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}