package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealTopCardsOfReserveDeckEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckAndLoseTheRestEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Used
 * Title: Go For Help!
 */
public class Card8_144 extends AbstractUsedInterrupt {
    public Card8_144() {
        super(Side.DARK, 5, Title.Go_For_Help, Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.C);
        setLore("When confronted with enemy troops, biker scouts are instructed to immediately call for reinforcements.");
        setGameText("If opponent just initiated a battle at an exterior site with double your total power, reveal top 3 cards of your Reserve Deck. If any of those cards are scouts or speeder bikes, deploy them for free to that battle (replace others on top of Reserve Deck in same order).");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.exterior_site)
                && GameConditions.hasReserveDeck(game, playerId)) {
            float playersPower = GameConditions.getBattlePower(game, playerId);
            float opponentsPower = GameConditions.getBattlePower(game, opponent);
            // "with double your total power" => opponent has at least twice your power
            if (opponentsPower >= (2 * playersPower)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Reveal top 3 cards of Reserve Deck");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                final int numToReveal = Math.min(3, game.getGameState().getReserveDeckSize(playerId));
                                // Proper reveal (cards stay in Reserve / same order); both players see.
                                // Undeployed leftovers stay on top via LEAVE_ON_TOP shared leftover mode.
                                action.appendEffect(
                                        new RevealTopCardsOfReserveDeckEffect(action, playerId, numToReveal) {
                                            @Override
                                            protected void cardsRevealed(List<PhysicalCard> cards) {
                                                action.appendEffect(
                                                        new DeployCardsFromReserveDeckAndLoseTheRestEffect(action, cards,
                                                                Filters.or(Filters.scout, Filters.speeder_bike),
                                                                Filters.battleLocation,
                                                                true,
                                                                DeployCardsFromReserveDeckAndLoseTheRestEffect.LeftoverMode.LEAVE_ON_TOP));
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}