package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Interrupt
 * Subtype: Lost
 * Title: Double Agent
 */
public class Card2_048 extends AbstractLostInterrupt {
    public Card2_048() {
        super(Side.LIGHT, 6, Title.Double_Agent);
        setLore("Typical of impersonators, Shada double-crossed everyone. Her true affiliations still remain a mystery.");
        setGameText("If both players have a spy at same site, draw destiny. Add 2 if opponent's spy is Undercover. Opponent's spy is lost if destiny > 2. OR Opponent's Tonnika Sisters present at a site cross to your side.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Filter filter = Filters.and(Filters.opponents(self), Filters.spy, Filters.at(Filters.sameSiteAs(self, SpotOverride.INCLUDE_UNDERCOVER, Filters.and(Filters.your(self), Filters.spy))));
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;

        // Check condition(s)
        if (GameConditions.canTarget(game, self, SpotOverride.INCLUDE_UNDERCOVER, targetingReason, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make opponent's spy lost");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose opponent's spy", SpotOverride.INCLUDE_UNDERCOVER, targetingReason, filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard opponentsSpy) {
                            action.addAnimationGroup(opponentsSpy);
                            // Allow response(s)
                            action.allowResponses("Make " + GameUtils.getCardLink(opponentsSpy) + " lost",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalOpponentsSpy = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected List<Modifier> getDrawDestinyModifiers(SwccgGame game, DrawDestinyState drawDestinyState) {
                                                            if (Filters.undercover_spy.accepts(game, finalOpponentsSpy)) {
                                                                Modifier modifier = new TotalDestinyModifier(self, drawDestinyState.getId(), 2);
                                                                return Collections.singletonList(modifier);
                                                            }
                                                            return null;
                                                        }
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
                                                                        new LoseCardFromTableEffect(action, finalOpponentsSpy));
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

        Filter filter2 = Filters.and(Filters.opponents(self), Filters.Tonnika_Sisters, Filters.presentAt(Filters.site));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, filter2)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Convert Tonnika Sisters");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Tonnika Sisters", filter2) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Convert " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ConvertCharacterEffect(action, finalTarget));
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