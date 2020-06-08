package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Alien
 * Title: Wioslea
 */
public class Card1_033 extends AbstractAlien {
    public Card1_033() {
        super(Side.LIGHT, 5, 1, 2, 1, 2, Title.Wioslea, Uniqueness.UNIQUE);
        setLore("Bought Luke's landspeeder for Spaceport Speeders. A Vuvrian female. Gambler who owes Jabba 1,000 credits in wagering debts. Speaks many languages.");
        setGameText("During your control phase, may use 1 Force to target an opponent's unoccupied transport vehicle or droid present. Draw destiny. If destiny > target's destiny number, use Force equal to target's deploy cost to 'purchase' target (use as if stolen).");
        addKeywords(Keyword.FEMALE, Keyword.GAMBLER);
        setSpecies(Species.VUVRIAN);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter targetFilter = Filters.and(Filters.opponents(playerId), Filters.or(Filters.transport_vehicle, Filters.droid), Filters.present(self));
        TargetingReason targetingReason = TargetingReason.TO_BE_PURCHASED;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTarget(game, self, targetingReason, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("'Purchase' transport vehicle or droid");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Target transport vehicle or droid to 'purchase'", targetingReason, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 1));
                            // Allow response(s)
                            action.allowResponses("'Purchase' " + GameUtils.getCardLink(cardTargeted),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected void destinyDraws(final SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float totalDestiny) {
                                                            final GameState gameState = game.getGameState();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }

                                                            action.appendEffect(
                                                                    new RefreshPrintedDestinyValuesEffect(action, Collections.singletonList(cardTargeted)) {
                                                                        @Override
                                                                        protected void refreshedPrintedDestinyValues() {
                                                                            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                                            float targetsDestiny = modifiersQuerying.getDestiny(gameState, cardTargeted);
                                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                            gameState.sendMessage("Target's destiny: " + GuiUtils.formatAsString(targetsDestiny));

                                                                            if (totalDestiny > targetsDestiny) {
                                                                                float costToPurchase = modifiersQuerying.getDeployCost(gameState, cardTargeted);

                                                                                if (GameConditions.canUseForce(game, playerId, costToPurchase)) {
                                                                                    gameState.sendMessage("Result: Succeeded");
                                                                                    action.appendEffect(
                                                                                            new UseForceEffect(action, playerId, costToPurchase));
                                                                                    action.appendEffect(
                                                                                            new PurchaseEffect(action, cardTargeted));
                                                                                } else {
                                                                                    gameState.sendMessage("Result: Succeeded, but not enough Force available to 'purchase' " + GameUtils.getCardLink(cardTargeted));
                                                                                }
                                                                            }
                                                                            else {
                                                                                gameState.sendMessage("Result: Failed");
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    });
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
