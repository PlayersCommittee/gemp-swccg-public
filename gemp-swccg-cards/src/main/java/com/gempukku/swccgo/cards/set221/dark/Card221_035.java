package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnForCardTitleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Imperial Landing Site
 */
public class Card221_035 extends AbstractSite {
    public Card221_035() {
        super(Side.DARK, "Tatooine: Imperial Landing Site", Title.Tatooine, Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setLocationDarkSideGameText("Once per turn, if your Imperial here, may [download] a [Hoth] device.");
        setLocationLightSideGameText("Once per turn, a player who controls this site may raise their converted related location to the top.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.A_NEW_HOPE, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_21);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.IMPERIAL_LANDING_SITE__DEPLOY_DEVICE;
        // Check condition(s)
        if (GameConditions.isHere(game, self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.Imperial))
                && GameConditions.isOncePerTurn(game, self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnDarkSideOfLocation, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy [Hoth] device");
            action.setActionMsg("Deploy [Hoth] device from Reserve Deck");

            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Icon.HOTH, Filters.device), true));
            return Collections.singletonList(action);
        }

        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter location = Filters.and(Filters.Tatooine_location, Filters.canBeConvertedByRaisingYourLocationToTop(playerOnLightSideOfLocation));

        GameTextActionId gameTextActionId = GameTextActionId.IMPERIAL_LANDING_SITE__RAISE_CONVERTED_LOCATION;
        // Check condition(s)
        if (GameConditions.controls(game, playerOnLightSideOfLocation, self)
                && GameConditions.isOncePerTurnForCardTitle(game, self, gameTextActionId)
                && GameConditions.canTarget(game, self, location)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Raise your converted Tatooine location");

            action.appendUsage(
                    new OncePerTurnForCardTitleEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerOnLightSideOfLocation, "Target location to convert", location) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Convert " + GameUtils.getCardLink(targetedCard),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ConvertLocationByRaisingToTopEffect(action, finalTarget, true));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideOpponentsTopLevelActions(final String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter location = Filters.and(Filters.Tatooine_location, Filters.canBeConvertedByRaisingYourLocationToTop(playerOnLightSideOfLocation));

        GameTextActionId gameTextActionId = GameTextActionId.IMPERIAL_LANDING_SITE__RAISE_CONVERTED_LOCATION;
        // Check condition(s)
        if (GameConditions.controls(game, playerOnLightSideOfLocation, self)
                && GameConditions.isOncePerTurnForCardTitle(game, self, gameTextActionId)
                && GameConditions.canTarget(game, self, location)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Raise your converted Tatooine location");

            action.appendUsage(
                    new OncePerTurnForCardTitleEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerOnLightSideOfLocation, "Target location to convert", location) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Convert " + GameUtils.getCardLink(targetedCard),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ConvertLocationByRaisingToTopEffect(action, finalTarget, true));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}