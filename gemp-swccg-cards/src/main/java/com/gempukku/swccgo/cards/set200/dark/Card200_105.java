package com.gempukku.swccgo.cards.set200.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckSimultaneouslyWithCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.*;

/**
 * Set: Set 0
 * Type: Effect
 * Title: Combat Response (V)
 */
public class Card200_105 extends AbstractNormalEffect {
    public Card200_105() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Combat_Response, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Being stationed at Endor during the construction of the second Death Star allows Imperial pilots time to train in the latest starfighter combat techniques.");
        setGameText("Deploy on table. Tallon Roll is canceled. Once per turn, may reveal an unpiloted starfighter from hand to [upload] its matching pilot character (or vice versa) and deploy both simultaneously. Your unique (•) TIEs (except Scythe Squadron TIEs) at systems may use 1 Force to relocate (as a regular move) to system within 3 parsecs. [Immune to Alter]");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_0);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Tallon_Roll)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        final Filter unpilotedStarfighter = Filters.and(Filters.unpiloted, Filters.or(Filters.starfighter, Filters.mayDeployInsteadOfStarfighterUsingCombatResponse));
        Filter filter = Filters.and(Filters.or(Filters.pilot, unpilotedStarfighter), Filters.isUniquenessOnTableNotReached);

        GameTextActionId gameTextActionId = GameTextActionId.COMBAT_RESPONSE__DEPLOY_MATCHING_STARFIGHTER_OR_PILOT;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.hasInHand(game, playerId, filter)
                && GameConditions.canSearchReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal pilot or unpiloted starfighter from hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardFromHandEffect(action, playerId, filter) {
                        @Override
                        protected void cardSelected(SwccgGame game, final PhysicalCard selectedCard) {
                            final Filter searchFilter;
                            if (Filters.character.accepts(game, selectedCard)) {
                                action.setActionMsg("Take " + GameUtils.getCardLink(selectedCard) + "'s matching unpiloted starfighter from Reserve Deck and deploy both simultaneously");
                                searchFilter = Filters.or(Filters.and(unpilotedStarfighter, Filters.matchingStarship(selectedCard)), Filters.mayDeployWithInsteadOfMatchingStarfighterUsingCombatResponse(selectedCard));
                            }
                            else {
                                action.setActionMsg("Take " + GameUtils.getCardLink(selectedCard) + "'s matching pilot from Reserve Deck and deploy both simultaneously");
                                searchFilter = Filters.matchingPilot(selectedCard);
                            }
                            // Perform result(s)
                            action.appendEffect(
                                    new ShowCardOnScreenEffect(action, selectedCard));
                            action.appendEffect(
                                    new DeployCardFromReserveDeckSimultaneouslyWithCardEffect(action, selectedCard, searchFilter, true));
                        }
                    });
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.MOVE)) {
            Collection<PhysicalCard> ties = Filters.filterActive(game, self,
                    Filters.and(Filters.your(self), Filters.unique, Filters.TIE, Filters.piloted, Filters.except(Filters.Scythe_Squadron), Filters.at(Filters.system), Filters.hasNotPerformedRegularMove));
            List<PhysicalCard> validTiesToRelocate = new ArrayList<PhysicalCard>();
            for (PhysicalCard tie : ties) {
                if (GameConditions.canSpotLocation(game, Filters.and(Filters.system, Filters.withinParsecsOf(tie, 3), Filters.locationCanBeRelocatedTo(tie, 1)))) {
                    validTiesToRelocate.add(tie);
                }
            }
            if (!validTiesToRelocate.isEmpty()) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Relocate a TIE");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose TIE", Integer.MAX_VALUE, false, Filters.in(validTiesToRelocate)) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard tieToRelocate) {
                                Filter systemToRelocateTo = Filters.and(Filters.system, Filters.withinParsecsOf(tieToRelocate, 3), Filters.locationCanBeRelocatedTo(tieToRelocate, 1));
                                action.appendTargeting(
                                        new ChooseCardOnTableEffect(action, playerId, "Choose system to relocate " + GameUtils.getCardLink(tieToRelocate) + " to", systemToRelocateTo) {
                                            @Override
                                            protected void cardSelected(final PhysicalCard systemSelected) {
                                                action.addAnimationGroup(tieToRelocate);
                                                action.addAnimationGroup(systemSelected);
                                                // Pay cost(s)
                                                action.appendCost(
                                                        new PayRelocateBetweenLocationsCostEffect(action, playerId, tieToRelocate, systemSelected, 1));
                                                // Allow response(s)
                                                action.allowResponses("Relocate " + GameUtils.getCardLink(tieToRelocate) + " to " + GameUtils.getCardLink(systemSelected),
                                                        new UnrespondableEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new RelocateBetweenLocationsEffect(action, tieToRelocate, systemSelected, true));
                                                            }
                                                        });
                                            }
                                        });
                            }
                        });
                actions.add(action);
            }
        }

        return actions;
    }
}