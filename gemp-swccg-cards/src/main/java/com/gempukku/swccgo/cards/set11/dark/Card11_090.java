package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.ResetTotalBattleDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Interrupt
 * Subtype: Used
 * Title: Watto's Chance Cube
 */
public class Card11_090 extends AbstractUsedInterrupt {
    public Card11_090() {
        super(Side.DARK, 5, Title.Wattos_Chance_Cube, Uniqueness.UNIQUE);
        setLore("'We'll let fate decide, huh?'");
        setGameText("If Watto is in a battle at a site, draw destiny. If destiny is odd, your total battle destiny = 0 this battle. If destiny is even, add two battle destiny.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.site)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Watto)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Draw destiny");
            // Allow response(s)
            action.allowResponses(
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
                                                gameState.sendMessage("Result: Failed destiny draw");
                                                return;
                                            }

                                            if ((totalDestiny % 2 == 1)) {
                                                gameState.sendMessage("Result: Destiny is odd");
                                                action.appendEffect(
                                                        new ResetTotalBattleDestinyEffect(action, playerId, 0));
                                            }
                                            else if ((totalDestiny % 2 == 0)
                                                    && GameConditions.canAddBattleDestinyDraws(game, self)) {
                                                gameState.sendMessage("Result: Destiny is even");
                                                action.appendEffect(
                                                        new AddBattleDestinyEffect(action, 2));
                                            }
                                            else {
                                                gameState.sendMessage("Result: No result");
                                            }
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