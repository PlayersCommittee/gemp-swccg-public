package com.gempukku.swccgo.cards.set215.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ReleaseCaptiveEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 15
 * Type: Location
 * Subtype: Site
 * Title: Death Star: Detention Block Corridor (V)
 */
public class Card215_007 extends AbstractSite {
    public Card215_007() {
        super(Side.LIGHT, Title.Detention_Block_Corridor, Title.Death_Star, Uniqueness.UNIQUE, ExpansionSet.SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("Once per game, may [download] a non-[Maintenance] Imperial trooper here.");
        setLocationLightSideGameText("If you control with a spy, may use 2 Force to release Leia here (retrieve 1 Force).");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.SPECIAL_EDITION, Icon.INTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_15);
        addKeywords(Keyword.PRISON);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.DETENTION_BLOCK_CORRIDOR__DOWNLOAD_TROOPER;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnDarkSideOfLocation, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a trooper from Reserve Deck");
            action.setActionMsg("Deploy a non-[Maintenance] Imperial Trooper from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.and(Filters.not(Icon.MAINTENANCE), Filters.Imperial, Filters.trooper), Filters.here(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        Filter captiveLeiaHere = Filters.and(Filters.Leia, Filters.here(self), Filters.captive);

        if (GameConditions.controlsWith(game, playerOnLightSideOfLocation, self, Filters.spy)
                && GameConditions.canTarget(game, self, SpotOverride.INCLUDE_CAPTIVE, captiveLeiaHere)
                && GameConditions.canUseForce(game, playerOnLightSideOfLocation, 2)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId);
            action.setText("Use 2 Force to release Leia here");
            action.appendCost(
                    new UseForceEffect(action, playerOnLightSideOfLocation, 2)
            );
            action.appendEffect(
                    new ReleaseCaptiveEffect(action, Filters.findFirstActive(game, self, SpotOverride.INCLUDE_CAPTIVE, captiveLeiaHere))
            );
            action.appendEffect(
                    new RetrieveForceEffect(action, playerOnLightSideOfLocation, 1)
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
