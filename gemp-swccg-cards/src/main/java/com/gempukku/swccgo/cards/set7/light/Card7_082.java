package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Used
 * Title: All Wings Report In
 */
public class Card7_082 extends AbstractUsedInterrupt {
    public Card7_082() {
        super(Side.LIGHT, 5, Title.All_Wings_Report_In, Uniqueness.UNIQUE);
        setLore("'Red 10 standing by.' 'Red 7 standing by.' 'Red 3 standing by.' 'Red 6 standing by.' 'Red 9 standing by.' 'Red 2 standing by.' 'Red 11 standing by.' 'Red 5 standing by.'");
        setGameText("Once per game, target one non-unique starfighter on table. Draw destiny. If destiny < X, retrieve X Force, where X = number of copies of that card you have on table. (Immune to Sense.) OR Take one non-unique starfighter into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        final Filter filter = Filters.and(Filters.non_unique, Filters.starfighter);
        GameTextActionId gameTextActionId = GameTextActionId.ALL_WINGS_REPORT_IN__RETRIEVE_FORCE;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTarget(game, self, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Target non-unique starfighter");
            action.setImmuneTo(Title.Sense);
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose starfighter", filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Target " + GameUtils.getCardLink(targetedCard),
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
                                                            int numCopies = Filters.countActive(game, self, Filters.and(Filters.your(self), Filters.sameTitle(finalTarget), Filters.mayContributeToForceRetrieval));
                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                            gameState.sendMessage("Value for X: " + numCopies);

                                                            if (totalDestiny < numCopies) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new RetrieveForceEffect(action, playerId, numCopies));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                            );
                        }
                    });
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.ALL_WINGS_REPORT_IN__UPLOAD_NON_UNIQUE_STARFIGHTER;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a non-unique starfighter into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.non_unique, Filters.starfighter), true));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}