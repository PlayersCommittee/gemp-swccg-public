package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealUsedPileEffect;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Look Sir, Droids
 */
public class Card1_259 extends AbstractLostInterrupt {
    public Card1_259() {
        super(Side.DARK, 3, Title.Look_Sir_Droids);
        setLore("Trooper 1124, Davin Felth, discovered droid plating dropped by R2-D2 and C-3PO when exiting escape pod.");
        setGameText("Use 1 Force to search through the opponent's Used Pile and move any droids you find there to opponent's Lost Pile. OR Use X Force to destroy any one droid on table where X = droid's deploy cost.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.hasUsedPile(game, opponent)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Search opponent's Used Pile");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RevealUsedPileEffect(action, opponent) {
                                        @Override
                                        protected void cardsRevealed(List<PhysicalCard> revealedCards) {
                                            Collection<PhysicalCard> cardsToMakeLost = Filters.filter(revealedCards, game, Filters.droid);
                                            if (!cardsToMakeLost.isEmpty()) {
                                                action.appendEffect(
                                                        new PutCardsFromUsedPileInLostPileEffect(action, opponent, Filters.in(cardsToMakeLost)));
                                            }
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        Filter filter = Filters.and(Filters.droid, Filters.deployCostLessThanOrEqualTo(GameConditions.forceAvailableToUseToPlayInterrupt(game, playerId, self)));
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;

        // Check condition(s)
        if (GameConditions.canTarget(game, self, targetingReason, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Destroy a droid");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose droid", targetingReason, filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard droid) {
                            action.addAnimationGroup(droid);
                            // Pay cost(s)
                            float deployCost = game.getModifiersQuerying().getDeployCost(game.getGameState(), droid);
                            if (deployCost > 0) {
                                action.appendCost(
                                        new UseForceEffect(action, playerId, deployCost));
                            }
                            // Allow response(s)
                            action.allowResponses("Destroy " + GameUtils.getCardLink(droid),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalDroid = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new LoseCardFromTableEffect(action, finalDroid));
                                        }
                                    }
                            );
                        }
                    });
            actions.add(action);
        }
        return actions;
    }
}