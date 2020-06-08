package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Full Scale Alert
 */
public class Card1_246 extends AbstractLostInterrupt {
    public Card1_246() {
        super(Side.DARK, 3, Title.Full_Scale_Alert);
        setLore("Imperial stormtroopers adopt strict security measures. Excellent communications and sheer numbers can hinder Rebel movement across entire territories.");
        setGameText("Use 2 Force. Draw Destiny. If destiny < number of Stormtroopers on the table, all opponent's movement is blocked for the remainder of this turn (except for smugglers and the starships they are aboard).");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)
                && GameConditions.canSpot(game, self, Filters.stormtrooper)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Block opponent's movement");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Allow response(s)
            action.allowResponses("Block opponent's movement (except for smugglers and the starships they are aboard)",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
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

                                            int numberOfStormtroopers = Filters.countActive(game, self, Filters.stormtrooper);
                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                            gameState.sendMessage("Number of stormtroopers: " + numberOfStormtroopers);

                                            if (totalDestiny < numberOfStormtroopers) {
                                                gameState.sendMessage("Result: Succeeded");
                                                action.appendEffect(
                                                        new AddUntilEndOfTurnModifierEffect(action,
                                                                new MayNotMoveModifier(self, Filters.and(Filters.opponents(self),
                                                                        Filters.not(Filters.or(Filters.smuggler, Filters.and(Filters.starship, Filters.hasAboard(self, Filters.smuggler)))))),
                                                                "Blocks opponent's movement (except for smugglers and the starships they are aboard)"));
                                            }
                                            else {
                                                gameState.sendMessage("Result: Failed");
                                            }
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