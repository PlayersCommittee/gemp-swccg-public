package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerCaptiveEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.InactiveReason;
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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ReleaseCaptiveEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DeliveredCaptiveToPrisonResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Set: The Great Hutt Expansion
 * Type: Location
 * Subtype: Site
 * Title: Claudius's Palace: Dungeon
 */
public class Card304_083 extends AbstractSite {
    public Card304_083() {
        super(Side.LIGHT, Title.Claudius_Dungeon, Title.Koudooine, Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLocationDarkSideGameText("If you control, may use 3 Force to release an imprisoned captive here.");
        setLocationLightSideGameText("Whenever a bounty hunter delivers a captive here, retrieve 2 Force (once per captive).");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
        addKeywords(Keyword.CLAUDIUS_PALACE_SITE, Keyword.PRISON);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(final String playerOnLightSideOfLocation, final SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
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
                        new RetrieveForceEffect(action, playerOnLightSideOfLocation, 2) {
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
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(final String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter targetFilter = Filters.imprisonedIn(self);
        Map<InactiveReason, Boolean> spotOverride = SpotOverride.INCLUDE_CAPTIVE;

        // Check condition(s)
        if (GameConditions.canUseForce(game, playerOnDarkSideOfLocation, 3)
                && GameConditions.controls(game, playerOnDarkSideOfLocation, self)
                && GameConditions.canTarget(game, self, spotOverride, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId);
            action.setText("Release an imprisoned captive");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerOnDarkSideOfLocation, "Target captive", spotOverride, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerOnDarkSideOfLocation, 3));
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
