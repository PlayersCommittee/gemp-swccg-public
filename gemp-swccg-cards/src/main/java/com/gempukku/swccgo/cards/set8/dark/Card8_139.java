package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.ResetForfeitUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Lost
 * Title: Dead Ewok
 */
public class Card8_139 extends AbstractLostInterrupt {
    public Card8_139() {
        super(Side.DARK, 5, Title.Dead_Ewok, Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.C);
        setLore("Many Ewoks gave their lives in the Battle of Endor.");
        setGameText("Reduce any alien's forfeit to 0 for remainder of turn. OR If you have two Imperials present at a site, draw destiny. Add 1 for each of your blasters present. If total destiny > number of Ewoks present at same site, those Ewoks are lost.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Filter alienFilter = Filters.alien;

        // Check condition(s)
        if (GameConditions.canTarget(game, self, alienFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Reset alien's forfeit to 0");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose alien", alienFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard alien) {
                            action.addAnimationGroup(alien);
                            // Allow response(s)
                            action.allowResponses("Reset " + GameUtils.getCardLink(alien) + "'s forfeit to 0",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ResetForfeitUntilEndOfTurnEffect(action, finalTarget, 0));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        final TargetingReason targetingReason = TargetingReason.TO_BE_LOST;
        List<PhysicalCard> validLocations = new LinkedList<PhysicalCard>();
        Collection<PhysicalCard> locations = Filters.filterTopLocationsOnTable(game,
                Filters.and(Filters.site, Filters.wherePresent(self, Filters.Ewok)));
        for (PhysicalCard location : locations) {
            if (GameConditions.canSpot(game, self, 2, Filters.and(Filters.your(self), Filters.Imperial, Filters.present(location)))) {
                validLocations.add(location);
            }
        }

        if (!validLocations.isEmpty()) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make Ewoks lost");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose location with Imperials and Ewoks", Filters.in(locations)) {
                        @Override
                        protected void cardSelected(final PhysicalCard location) {
                            action.addAnimationGroup(location);
                            // Allow response(s)
                            action.allowResponses("Make Ewoks present at " + GameUtils.getCardLink(location) + " lost",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected List<Modifier> getDrawDestinyModifiers(SwccgGame game, DrawDestinyState drawDestinyState) {
                                                            Modifier modifier = new TotalDestinyModifier(self, drawDestinyState.getId(), new PresentEvaluator(self, location, Filters.and(Filters.your(self), Filters.blaster)));
                                                            return Collections.singletonList(modifier);
                                                        }
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }

                                                            Collection<PhysicalCard> ewoks = Filters.filterActive(game, self, Filters.and(Filters.Ewok, Filters.present(location)));
                                                            int numEwoks = ewoks.size();
                                                            gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                            gameState.sendMessage("Number of Ewoks: " + numEwoks);

                                                            if (totalDestiny > numEwoks) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new LoseCardsFromTableEffect(action, Filters.filter(ewoks, game, Filters.canBeTargetedBy(self, targetingReason))));
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