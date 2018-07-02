package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Interrupt
 * Subtype: Lost
 * Title: Dioxis
 */
public class Card12_147 extends AbstractLostInterrupt {
    public Card12_147() {
        super(Side.DARK, 3, "Dioxis", Uniqueness.UNIQUE);
        setLore("Green gas that is lethal to most carbon-based life forms.");
        setGameText("Use 2 Force to target two opponent's Jedi at same interior battleground. Targets may not participate in battle for remainder of turn. OR Target opponent's Jedi. Draw destiny. If destiny > 2, target's game text is canceled for remainder of turn.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Filter locationFilter = Filters.and(Filters.interior_site, Filters.battleground, Filters.sameSiteAs(self, Filters.and(Filters.opponents(self), Filters.Jedi, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_EXCLUDED_FROM_BATTLE),
                Filters.with(self, Filters.and(Filters.opponents(self), Filters.Jedi, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_EXCLUDED_FROM_BATTLE))))));

        // Check condition(s)
        if (GameConditions.canSpotLocation(game, locationFilter)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target two opponent's Jedi");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose location with two opponent's Jedi", locationFilter) {
                        @Override
                        protected void cardSelected(final PhysicalCard location) {
                            Filter jediAtLocationFilter = Filters.and(Filters.opponents(self), Filters.Jedi, Filters.at(location));
                            // Choose target(s)
                            action.appendTargeting(
                                    new TargetCardsOnTableEffect(action, playerId, "Choose two opponent's Jedi", 2, 2, TargetingReason.TO_BE_EXCLUDED_FROM_BATTLE, jediAtLocationFilter) {
                                        @Override
                                        protected void cardsTargeted(final int targetGroupId, Collection<PhysicalCard> targetedCards) {
                                            action.addAnimationGroup(targetedCards);
                                            // Pay cost(s)
                                            action.appendCost(
                                                    new UseForceEffect(action, playerId, 2));
                                            // Allow response(s)
                                            action.allowResponses("Have " + GameUtils.getAppendedNames(targetedCards) + " not participate in battle",
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            final Collection<PhysicalCard> finalTargets = action.getPrimaryTargetCards(targetGroupId);

                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new MayNotBattleUntilEndOfTurnEffect(action, finalTargets));
                                                        }
                                                    }
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        Filter jediFilter = Filters.and(Filters.opponents(self), Filters.Jedi);

        // Check condition(s)
        if (GameConditions.canTarget(game, self, jediFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel Jedi's game text");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Jedi", jediFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s game text",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }

                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                            if (totalDestiny > 2) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new CancelGameTextUntilEndOfTurnEffect(action, finalTarget));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                    }
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}