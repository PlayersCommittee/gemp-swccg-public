package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromHandEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Rescue In The Clouds
 */
public class Card5_067 extends AbstractUsedOrLostInterrupt {
    public Card5_067() {
        super(Side.LIGHT, 5, Title.Rescue_In_The_Clouds, Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.C);
        setLore("'I know where Luke is.'");
        setGameText("USED: If you have a character on Weather Vane, place that character on your Used Pile. LOST: Deploy one or more vehicles, starfighters and pilots (at normal use of the Force) as a 'react' to a cloud sector.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Filter yourCharacterOnWeatherVane = Filters.and(Filters.your(self), Filters.character, Filters.stackedOn(self, Filters.Weather_Vane));

        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.and(Filters.Weather_Vane, Filters.hasStacked(yourCharacterOnWeatherVane)))) {
            final PhysicalCard weatherVane = Filters.findFirstActive(game, self, Filters.Weather_Vane);
            if (weatherVane != null) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Place character from Weather Vane in Used Pile");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseStackedCardEffect(action, playerId, weatherVane, yourCharacterOnWeatherVane) {
                            @Override
                            protected void cardSelected(final PhysicalCard selectedCard) {
                                action.addAnimationGroup(selectedCard);
                                // Allow response(s)
                                action.allowResponses("Place " + GameUtils.getCardLink(selectedCard) + " from Weather Vane in Used Pile",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new PutStackedCardInUsedPileEffect(action, playerId, selectedCard, false));
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        final Filter deployFilter = Filters.or(Filters.vehicle, Filters.starfighter, Filters.pilot);

        // Check condition(s)
        if ((TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.cloud_sector)
                || TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent, Filters.cloud_sector))
                && GameConditions.canSpotLocation(game, Filters.cloud_sector)
                && GameConditions.hasInHand(game, playerId, deployFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Deploy as a 'react' to a cloud sector");
            // Allow response(s)
            action.allowResponses("Deploy vehicles, starfighters, and pilots as a 'react' to a cloud sector",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            appendDeployAsReactFromHand(action, playerId, game, self, deployFilter, true);
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    private void appendDeployAsReactFromHand(final PlayInterruptAction action, final String playerId, final SwccgGame game, final PhysicalCard self, final Filter deployFilter, final boolean required) {
        if (!GameConditions.hasInHand(game, playerId, deployFilter)) {
            return;
        }

        action.appendEffect(
                new DeployCardToLocationFromHandEffect(action, playerId, deployFilter, Filters.cloud_sector, false, true) {
                    @Override
                    public String getChoiceText() {
                        if (required) {
                            return "Choose vehicle, starfighter, or pilot to deploy as a 'react'";
                        }
                        return "Choose another vehicle, starfighter, or pilot to deploy as a 'react'";
                    }

                    @Override
                    protected void cardDeployed(PhysicalCard card) {
                        appendOptionalAdditionalReactDeploys(action, playerId, game, self, deployFilter);
                    }
                }
        );
    }

    private void appendOptionalAdditionalReactDeploys(final PlayInterruptAction action, final String playerId, final SwccgGame game, final PhysicalCard self, final Filter deployFilter) {
        if (!GameConditions.hasInHand(game, playerId, deployFilter)) {
            return;
        }

        action.appendEffect(
                new PlayoutDecisionEffect(action, playerId,
                        new MultipleChoiceAwaitingDecision("Deploy another vehicle, starfighter, or pilot as a 'react'?", new String[]{"Yes", "No"}) {
                            @Override
                            protected void validDecisionMade(int index, String result) {
                                if (index == 0) {
                                    appendDeployAsReactFromHand(action, playerId, game, self, deployFilter, false);
                                }
                            }
                        }
                )
        );
    }
}
