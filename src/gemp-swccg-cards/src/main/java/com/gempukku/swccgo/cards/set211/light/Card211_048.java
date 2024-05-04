package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Location
 * Subtype: System
 * Title: Ahch-To
 */
public class Card211_048 extends AbstractSystem {
    public Card211_048() {
        super(Side.LIGHT, Title.Ahch_To, 9, ExpansionSet.SET_11, Rarity.V);
        setLocationDarkSideGameText("While you occupy an Ahch-To site, its game text is canceled. Players may not Force drain here.");
        setLocationLightSideGameText("Once during any deploy phase, may use 1 Force to relocate Luke between any two Ahch-To sites.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.EPISODE_VII, Icon.PLANET, Icon.VIRTUAL_SET_11);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        Filter ahchToSitesYouOccupy = Filters.and(
                Filters.AhchTo_site,
                Filters.occupies(playerOnDarkSideOfLocation)
        );

        modifiers.add(new CancelsGameTextModifier(self, ahchToSitesYouOccupy));
        modifiers.add(new MayNotForceDrainAtLocationModifier(self));

        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        Filter lukeOnAhchTo = Filters.and(
                Filters.Luke,
                Filters.at(Filters.AhchTo_site)
        );
        Filter lukesSite = Filters.and(Filters.sameSiteAs(self, Filters.Luke));
        final Filter otherAhchToSite = Filters.and(Filters.AhchTo_site, Filters.not(lukesSite));

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringEitherPlayersPhase(game, Phase.DEPLOY)
                && GameConditions.canSpot(game, self, 1, lukeOnAhchTo)
                && GameConditions.canSpot(game, self, 1, otherAhchToSite)
                && GameConditions.canUseForce(game, playerOnLightSideOfLocation, 1)
                && GameConditions.canPerformMovementUsingLocationText(playerOnLightSideOfLocation, game, Filters.Luke, lukesSite, otherAhchToSite, false, 1)
                )
        {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Relocate Luke");
            action.setActionMsg("Relocate Luke to another Ahch-To site");

            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));

            // Target Luke to move
            action.appendTargeting(new TargetCardOnTableEffect(action, playerOnLightSideOfLocation, "Target Luke", Filters.Luke) {
                @Override
                protected void cardTargeted(final int lukeTargetGroupId, final PhysicalCard targetedCharacter) {
                    if (targetedCharacter != null) {

                        // Target the location to relocate Luke to
                        action.appendTargeting(new TargetCardOnTableEffect(action, playerOnLightSideOfLocation, "Select other Ahch-To site", otherAhchToSite) {
                            @Override
                            protected void cardTargeted(final int siteTargetGroupId, PhysicalCard selectedSite) {
                                if (selectedSite != null) {

                                    action.allowResponses("Relocate " + GameUtils.getCardLink(targetedCharacter) + " to " + GameUtils.getCardLink(selectedSite),
                                            new RespondableEffect(action) {
                                                @Override
                                                protected void performActionResults(Action targetingAction) {

                                                    // Get the targeted card(s) from the action using the targetGroupId.
                                                    // This needs to be done in case the target(s) were changed during the responses.
                                                    PhysicalCard finalCharacter = action.getPrimaryTargetCard(lukeTargetGroupId);
                                                    PhysicalCard finalSite = action.getPrimaryTargetCard(siteTargetGroupId);

                                                    if ((finalCharacter == null) || (finalSite == null)) {
                                                        return;
                                                    }

                                                    // Pay Costs
                                                    action.appendCost(new UseForceEffect(action, playerOnLightSideOfLocation, 1));

                                                    // Perform result(s)
                                                    action.appendEffect(
                                                            new RelocateBetweenLocationsEffect(action, finalCharacter, finalSite));
                                                }
                                            }
                                    );

                                }
                            }
                        });
                    }
                }
            });

            return Collections.singletonList(action);
        }
        return null;
    }
}