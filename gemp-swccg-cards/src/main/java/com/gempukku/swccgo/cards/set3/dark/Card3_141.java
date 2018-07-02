package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Lost
 * Title: Wall Of Fire
 */
public class Card3_141 extends AbstractLostInterrupt {
    public Card3_141() {
        super(Side.DARK, 5, "Wall Of Fire");
        setLore("Walkers are capable of incinerating entire infantry units in seconds. Rebel troops refer to the deadly barrage as the 'wall of fire.'");
        setGameText("If you have a piloted AT-AT present at a site, target any number of opponent's troopers present at same or adjacent exterior site. Draw destiny. If destiny > number of troopers targeted, they are lost.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final TargetingReason targetingReason = TargetingReason.TO_BE_LOST;
        Filter filter = Filters.and(Filters.your(self), Filters.piloted, Filters.AT_AT, Filters.at(Filters.sameOrAdjacentSiteAs(self, Filters.and(Filters.exterior_site,
                        Filters.wherePresent(self, Filters.and(Filters.opponents(self), Filters.trooper, Filters.canBeTargetedBy(self, targetingReason)))))));

        // Check condition(s)
        if (GameConditions.canSpot(game, self, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target opponent's troopers");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose AT-AT", filter) {
                        @Override
                        protected void cardSelected(PhysicalCard atat) {
                            action.appendTargeting(
                                    new TargetCardsOnTableEffect(action, playerId, "Choose troopers", 1, Integer.MAX_VALUE, targetingReason,
                                            Filters.and(Filters.opponents(self), Filters.trooper, Filters.presentAt(Filters.and(Filters.exterior_site, Filters.sameOrAdjacentSite(atat))))) {
                                        @Override
                                        protected void cardsTargeted(final int targetGroupId, Collection<PhysicalCard> targetedCards) {
                                            action.addAnimationGroup(targetedCards);
                                            // Allow response(s)
                                            action.allowResponses("Make " + GameUtils.getAppendedNames(targetedCards) + " lost",
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            final Collection<PhysicalCard> finalTargets = action.getPrimaryTargetCards(targetGroupId);

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

                                                                            int numTargets = finalTargets.size();
                                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                            gameState.sendMessage("Number of troopers targeted: " + numTargets);

                                                                            if (totalDestiny > numTargets) {
                                                                                gameState.sendMessage("Result: Succeeded");
                                                                                action.appendEffect(
                                                                                        new LoseCardsFromTableEffect(action, finalTargets));
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
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}