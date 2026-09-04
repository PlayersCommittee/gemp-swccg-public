package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromHandEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Lost
 * Title: TIE Sentry Ships
 */
public class Card5_158 extends AbstractLostInterrupt {
    public Card5_158() {
        super(Side.DARK, 5, Title.TIE_Sentry_Ships, Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.C);
        setLore("Several TIEs were assigned to patrol Cloud City prior to the Imperial occupation of Bespin. Their instructions were to herd any vessels attempting to escape toward the Executor.");
        setGameText("If opponent just initiated a Force drain at a system, cloud sector or asteroid sector, you may 'react' by deploying TIEs and pilots to that location (at normal use of the Force).");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        final Filter locationFilter = Filters.or(Filters.system, Filters.cloud_sector, Filters.asteroid_sector);
        final Filter deployFilter = Filters.or(Filters.TIE, Filters.pilot);

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent, locationFilter)
                && GameConditions.hasInHand(game, playerId, deployFilter)) {

            final PhysicalCard forceDrainLocation = game.getGameState().getForceDrainLocation();
            if (forceDrainLocation == null) {
                return null;
            }

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Deploy TIEs and pilots as a 'react'");
            // Allow response(s)
            action.allowResponses("Deploy TIEs and pilots as a 'react' to " + forceDrainLocation.getTitle(),
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            appendDeployAsReactFromHand(action, playerId, game, deployFilter, Filters.sameCardId(forceDrainLocation), true);
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    private void appendDeployAsReactFromHand(final PlayInterruptAction action, final String playerId, final SwccgGame game, final Filter deployFilter, final Filter locationFilter, final boolean required) {
        if (!GameConditions.hasInHand(game, playerId, deployFilter)) {
            return;
        }

        if (required) {
            action.appendEffect(
                    new DeployCardToLocationFromHandEffect(action, playerId, deployFilter, locationFilter, false, true) {
                        @Override
                        public String getChoiceText() {
                            return "Choose a TIE or pilot to deploy as a 'react'";
                        }

                        @Override
                        protected void cardDeployed(PhysicalCard card) {
                            appendOptionalAdditionalReactDeploys(action, playerId, game, deployFilter, locationFilter);
                        }
                    }
            );
            return;
        }

        action.appendEffect(
                new ChooseCardsFromHandEffect(action, playerId, playerId, 0, 1, deployFilter, true, false) {
                    @Override
                    public String getChoiceText(int numCardsToChoose) {
                        return "Choose another TIE or pilot to deploy as a 'react', or click 'Done' to stop";
                    }

                    @Override
                    protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                        if (selectedCards.isEmpty()) {
                            return;
                        }
                        PhysicalCard selectedCard = selectedCards.iterator().next();
                        action.appendEffect(
                                new DeployCardToLocationFromHandEffect(action, selectedCard, locationFilter, false, true) {
                                    @Override
                                    protected void cardDeployed(PhysicalCard card) {
                                        appendOptionalAdditionalReactDeploys(action, playerId, game, deployFilter, locationFilter);
                                    }
                                }
                        );
                    }
                }
        );
    }

    private void appendOptionalAdditionalReactDeploys(final PlayInterruptAction action, final String playerId, final SwccgGame game, final Filter deployFilter, final Filter locationFilter) {
        appendDeployAsReactFromHand(action, playerId, game, deployFilter, locationFilter, false);
    }
}
