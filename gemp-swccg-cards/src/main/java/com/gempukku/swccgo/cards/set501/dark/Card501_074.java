package com.gempukku.swccgo.cards.set501.dark;


import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 13
 * Type: Location
 * Subtype: Site
 * Title: Scarif: Citadel Tower
 */
public class Card501_074 extends AbstractSite {
    public Card501_074() {
        super(Side.DARK, Title.Scarif_Citadel_Tower, Title.Scarif);
        setLocationLightSideGameText("If a player just Force drained here, they may raise a converted Scarif location to the top.");
        setLocationDarkSideGameText("If you occupy, may use 1 Force to deploy a Scarif site from Reserve deck; reshuffle.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PLANET, Icon.INTERIOR_SITE, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_13);
        setTestingText("Scarif: Citadel Tower");
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(final String playerOnDarkSideOfLocation, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.CITADEL_TOWER__DOWNLOAD_SCARIF_SITE;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnDarkSideOfLocation, self, gameTextActionId)
                && GameConditions.canUseForce(game, playerOnDarkSideOfLocation, 1)
                && GameConditions.occupies(game, playerOnDarkSideOfLocation, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Scarif site from Reserve Deck");
            action.setActionMsg("Deploy a Scarif site from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerOnDarkSideOfLocation, 1));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Scarif_site, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLightSideOptionalAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        Filter scarifSite = Filters.and(Filters.Scarif_site, Filters.or(Filters.canBeConvertedByRaisingLocationToTop(playerOnLightSideOfLocation), Filters.canBeConvertedByRaisingLocationToTop(game.getOpponent(playerOnLightSideOfLocation))));

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedAt(game, effectResult, self)
                && GameConditions.canTarget(game, self, scarifSite)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerOnLightSideOfLocation, gameTextSourceCardId);
            action.setText("Raise a converted Scarif site");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerOnLightSideOfLocation, "Target site to convert", scarifSite) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Convert " + GameUtils.getCardLink(targetedCard),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ConvertLocationByRaisingToTopEffect(action, targetedCard, false));
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
