package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Interrupt
 * Subtype: Used
 * Title: You Will Take Me To Jabba Now
 */
public class Card6_080 extends AbstractUsedInterrupt {
    public Card6_080() {
        super(Side.LIGHT, 5, "You Will Take Me To Jabba Now", Uniqueness.UNIQUE);
        setLore("'Et tu taka bu Jabba now.'");
        setGameText("If Jabba is at a Jabba's Palace site, relocate one of your characters to that location from a related site. OR During your deploy phase, deploy one alien (at normal use of the Force) from Reserve Deck to the Audience Chamber; reshuffle.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        final PhysicalCard locationWithJabba = Filters.findFirstFromTopLocationsOnTable(game, Filters.and(Filters.Jabbas_Palace_site, Filters.sameLocationAs(self, Filters.Jabba)));
        if (locationWithJabba != null) {
            final Filter filter = Filters.and(Filters.your(self), Filters.character, Filters.at(Filters.relatedSite(locationWithJabba)), Filters.canBeRelocatedToLocation(locationWithJabba, 0));
            if (GameConditions.canTarget(game, self, filter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Relocate character to Jabba's site");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character", filter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                action.addAnimationGroup(locationWithJabba);
                                // Allow response(s)
                                action.allowResponses("Relocate " + GameUtils.getCardLink(targetedCard) + " to " + GameUtils.getCardLink(locationWithJabba),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new RelocateBetweenLocationsEffect(action, finalTarget, locationWithJabba));
                                             }
                                        }
                                );
                            }
                        });
                actions.add(action);
            }
        }

        GameTextActionId gameTextActionId = GameTextActionId.YOU_WILL_TAKE_ME_TO_JABBA_NOW__DOWNLOAD_ALIEN;

        // Check condition(s)
        if (GameConditions.canSpotLocation(game, Filters.Audience_Chamber)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Deploy alien from Reserve Deck");
            action.setActionMsg("Deploy an alien to Audience Chamber from Reserve Deck");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.alien, Filters.Audience_Chamber, true));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}