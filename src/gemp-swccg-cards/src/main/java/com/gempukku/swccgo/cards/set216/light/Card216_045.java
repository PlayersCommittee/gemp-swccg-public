package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.MoveAsReactEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 16
 * Type: Interrupt
 * Subtype: Used
 * Title: Wookiee Guide (V)
 */
public class Card216_045 extends AbstractUsedInterrupt {
    public Card216_045() {
        super(Side.LIGHT, 4, "Wookiee Guide", Uniqueness.UNIQUE, ExpansionSet.SET_16, Rarity.V);
        setVirtualSuffix(true);
        setLore("Chewie felt right at home in the forests of Endor, which closely resemble the environment on his homeworld of Kashyyyk.");
        setGameText("If opponent just initiated battle at an Endor or Kashyyyk site (or any forest), move your non-unique Wookiee there as a 'react.' OR [download] a Wookiee to a Kashyyyk site. OR Activate 1 Force for each Kashyyyk location you occupy.");
        addIcons(Icon.ENDOR, Icon.EPISODE_I, Icon.VIRTUAL_SET_16);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        //If opponent just initiated battle at an Endor or Kashyyyk site (or any forest), move your non-unique Wookiee there as a 'react.'
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.or(Filters.Endor_site, Filters.Kashyyyk_site, Filters.forest))) {
            Filter nonuniqueWookieeFilter = Filters.and(Filters.your(self), Filters.and(Filters.non_unique, Filters.Wookiee), Filters.canMoveAsReactAsActionFromOtherCard(self, false, 0, false));
            if (GameConditions.canTarget(game, self, nonuniqueWookieeFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Move non-unique Wookiee as 'react'");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Wookiee", nonuniqueWookieeFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Allow response(s)
                                action.allowResponses("Move " + GameUtils.getCardLink(targetedCard) + " as a 'react'",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                PhysicalCard finalCharacter = action.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new MoveAsReactEffect(action, finalCharacter, false));
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.WOOKIEE_GUIDE_V__DOWNLOAD_WOOKIEE;

        //Deploy a Wookiee to a Kashyyyk site from Reserve Deck; reshuffle.
        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canSpot(game, self, Filters.Kashyyyk_site)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Deploy a Wookiee from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Deploy a Wookiee from Reserve Deck to a Kashyyyk site",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.Wookiee, Filters.Kashyyyk_site, true));
                        }
                    }
            );
            actions.add(action);
        }

        //Activate 1 Force for each Kashyyyk location you occupy.
        if (GameConditions.canActivateForce(game, playerId)) {
            final int kashyyykLocationsYouOccupy = Filters.countTopLocationsOnTable(game, Filters.and(Filters.Kashyyyk_location, Filters.occupies(playerId)));
            if (kashyyykLocationsYouOccupy > 0) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Activate " + kashyyykLocationsYouOccupy + " Force");
                action.allowResponses("Activate " + kashyyykLocationsYouOccupy + " Force",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new ActivateForceEffect(action, playerId, kashyyykLocationsYouOccupy));
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }
}