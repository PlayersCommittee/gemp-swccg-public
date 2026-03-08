package com.gempukku.swccgo.cards.set216.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
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
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 16
 * Type: Location
 * Subtype: Site
 * Title: Scarif: Command Center
 */
public class Card216_016 extends AbstractSite {
    public Card216_016() {
        super(Side.DARK, "Scarif: Command Center", Title.Scarif, Uniqueness.UNIQUE, ExpansionSet.SET_16, Rarity.V);
        setLocationDarkSideGameText("May [download] Krennic here.");
        setLocationLightSideGameText("If a player just Force drained here, they may raise a converted Scarif location to the top.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.VIRTUAL_SET_16, Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.SCARIF_COMMAND_CENTER__DOWNLOAD_KRENNIC;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerOnDarkSideOfLocation, self, gameTextActionId, Persona.KRENNIC)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Krennic from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.Krennic, Filters.here(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLightSideOptionalAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        Filter scarifLocation = Filters.and(Filters.Scarif_location, Filters.or(Filters.canBeConvertedByRaisingLocationToTop(playerOnLightSideOfLocation), Filters.canBeConvertedByRaisingLocationToTop(game.getOpponent(playerOnLightSideOfLocation))));

        // Check condition(s)
        if (TriggerConditions.forceDrainCompleted(game, effectResult, playerOnLightSideOfLocation, self)
                && GameConditions.canTarget(game, self, scarifLocation)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerOnLightSideOfLocation, gameTextSourceCardId);
            action.setText("Raise a converted Scarif location");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerOnLightSideOfLocation, "Target site to convert", scarifLocation) {
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

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLightSideOpponentsActionOptionalAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        Filter scarifLocation = Filters.and(Filters.Scarif_location, Filters.or(Filters.canBeConvertedByRaisingLocationToTop(playerOnLightSideOfLocation), Filters.canBeConvertedByRaisingLocationToTop(game.getOpponent(playerOnLightSideOfLocation))));

        // Check condition(s)
        if (TriggerConditions.forceDrainCompleted(game, effectResult, playerOnLightSideOfLocation, self)
                && GameConditions.canTarget(game, self, scarifLocation)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerOnLightSideOfLocation, gameTextSourceCardId);
            action.setText("Raise a converted Scarif location");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerOnLightSideOfLocation, "Target site to convert", scarifLocation) {
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
