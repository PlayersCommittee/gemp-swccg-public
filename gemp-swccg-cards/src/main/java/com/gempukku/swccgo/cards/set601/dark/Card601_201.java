package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;


/**
 * Set: Block 1
 * Type: Interrupt
 * Subtype: Used Or Starting
 * Title: Surface Defense (V)
 */
public class Card601_201 extends AbstractUsedOrStartingInterrupt {
    public Card601_201() {
        super(Side.DARK, 5, "Surface Defense");
        setVirtualSuffix(true);
        setLore("'The battle station is heavily shielded and carries a firepower greater than half the starfleet.'");
        setGameText("USED: Use 1 Force to [download] an Effect that deploys for free on table (or your side of table) and is always immune to Alter. STARTING: When drawing your starting hand, draw up to 12 cards instead of 8. Place this Interrupt in Reserve Deck.");
        addIcons(Icon.SPECIAL_EDITION, Icon.LEGACY_BLOCK_1);
        setAsLegacy(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.SURFACE_DEFENSE__DOWNLOAD_EFFECT;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Deploy an Effect from Reserve Deck");
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
                                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.deploysForFree, Filters.always_immune_to_Alter,
                                            Filters.or(Filters.gameTextContains("deploy on table"), Filters.gameTextContains("deploy on your side of table"))), true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, SwccgGame game, final PhysicalCard self) {
        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
        action.setText("Choose number of cards to draw in starting hand");
        // Choose target(s)
        action.appendTargeting(
                new PlayoutDecisionEffect(action, playerId,
                        new IntegerAwaitingDecision("Choose number of cards to draw in starting hand", 1, 12, 12) {
                            @Override
                            public void decisionMade(final int result) {
                                // Allow response(s)
                                action.allowResponses("Draw " + result + " cards in starting hand",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new ModifyNumCardsDrawnInStartingHandEffect(action, playerId, result));
                                                action.appendEffect(
                                                        new PutCardFromVoidInReserveDeckEffect(action, playerId, self));
                                            }
                                        }
                                );
                            }
                        }
                )
        );
        return action;
    }
}