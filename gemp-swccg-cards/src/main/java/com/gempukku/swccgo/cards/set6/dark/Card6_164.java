package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerCaptiveEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DeliveredCaptiveToPrisonResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Set: Jabba's Palace
 * Type: Location
 * Subtype: Site
 * Title: Jabba's Palace: Dungeon
 */
public class Card6_164 extends AbstractSite {
    public Card6_164() {
        super(Side.DARK, Title.Dungeon, Title.Tatooine);
        setLocationDarkSideGameText("Whenever a bounty hunter delivers a captive here, retrieve 2 Force (once per captive).");
        setLocationLightSideGameText("If you control, may use 3 Force to release an imprisoned captive here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.JABBAS_PALACE, Icon.INTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.JABBAS_PALACE_SITE, Keyword.PRISON);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredAfterTriggers(final String playerOnDarkSideOfLocation, final SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.DUNGEON__RETRIEVE_FORCE;

        // Check condition(s)
        if (TriggerConditions.captiveDeliveredToPrisonBy(game, effectResult, Filters.bounty_hunter, self)) {
            final PhysicalCard captive = ((DeliveredCaptiveToPrisonResult) effectResult).getCaptive();
            if (GameConditions.isOncePerCaptive(game, self, captive, gameTextActionId)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerCaptiveEffect(action, captive));
                // Perform result(s)
                action.appendEffect(
                        new RetrieveForceEffect(action, playerOnDarkSideOfLocation, 2) {
                            @Override
                            public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                                return Collections.singletonList(captive);
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter targetFilter = Filters.imprisonedIn(self);
        Map<InactiveReason, Boolean> spotOverride = SpotOverride.INCLUDE_CAPTIVE;

        // Check condition(s)
        if (GameConditions.canUseForce(game, playerOnLightSideOfLocation, 3)
                && GameConditions.controls(game, playerOnLightSideOfLocation, self)
                && GameConditions.canTarget(game, self, spotOverride, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId);
            action.setText("Release an imprisoned captive");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerOnLightSideOfLocation, "Target captive", spotOverride, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerOnLightSideOfLocation, 3));
                            // Allow response(s)
                            action.allowResponses("Release " + GameUtils.getCardLink(cardTargeted),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ReleaseCaptiveEffect(action, cardTargeted));
                                        }
                                    });
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}