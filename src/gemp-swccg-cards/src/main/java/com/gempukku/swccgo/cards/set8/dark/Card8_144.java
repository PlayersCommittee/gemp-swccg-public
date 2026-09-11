package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
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
                                // Reveal by drawing into hand (shown), deploy eligible to battle, put rest back on top in order.
                                // Shared Panic/Emergency proper-reveal fix deferred pending Chief clearance.
                                action.appendEffect(
                                        new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, numToReveal) {
                                            @Override
                                            protected void cardsDrawnIntoHand(Collection<PhysicalCard> cards) {
                                                final List<PhysicalCard> revealedInOrder = new ArrayList<PhysicalCard>(cards);
                                                for (PhysicalCard revealed : revealedInOrder) {
                                                    action.appendEffect(new ShowCardOnScreenEffect(action, revealed));
                                                }
                                                action.appendEffect(
                                                        new DeployRevealedScoutsAndSpeederBikesFromHandEffect(action, self, playerId, revealedInOrder));
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

    /**
     * Deploys scouts/speeder bikes among the revealed (drawn) cards for free to the battle, then puts any remaining
     * revealed cards back on top of Reserve Deck preserving relative order.
     */
    private static class DeployRevealedScoutsAndSpeederBikesFromHandEffect extends AbstractSubActionEffect {
        private final PhysicalCard _source;
        private final String _playerId;
        private final List<PhysicalCard> _revealedInOrder;

        private DeployRevealedScoutsAndSpeederBikesFromHandEffect(Action action, PhysicalCard source, String playerId, List<PhysicalCard> revealedInOrder) {
            super(action);
            _source = source;
            _playerId = playerId;
            _revealedInOrder = new LinkedList<PhysicalCard>(revealedInOrder);
        }

        @Override
        public boolean isPlayableInFull(SwccgGame game) {
            return true;
        }

        @Override
        protected SubAction getSubAction(final SwccgGame game) {
            final SubAction subAction = new SubAction(_action);
            subAction.appendEffect(getChooseAndDeployEffect(subAction));
            return subAction;
        }

        private void appendPutRemainingBackOnTop(final SubAction subAction) {
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            List<PhysicalCard> remaining = new ArrayList<PhysicalCard>();
                            for (PhysicalCard card : _revealedInOrder) {
                                if (card.getZone() == Zone.HAND && _playerId.equals(card.getOwner())) {
                                    remaining.add(card);
                                }
                            }
                            // Place last remaining first so earliest remaining ends on top (same relative order).
                            for (int i = remaining.size() - 1; i >= 0; --i) {
                                PhysicalCard card = remaining.get(i);
                                game.getGameState().removeCardsFromZone(java.util.Collections.singleton(card));
                                game.getGameState().addCardToTopOfZone(card, Zone.RESERVE_DECK, _playerId);
                                game.getGameState().sendMessage(_playerId + " puts " + com.gempukku.swccgo.logic.GameUtils.getCardLink(card) + " on top of Reserve Deck");
                            }
                        }
                    }
            );
        }

        private StandardEffect getChooseAndDeployEffect(final SubAction subAction) {
            return new PassthruEffect(subAction) {
                @Override
                protected void doPlayEffect(final SwccgGame game) {
                    Collection<PhysicalCard> deployable = Filters.filter(_revealedInOrder, game,
                            Filters.and(Filters.inHand(_playerId), Filters.or(Filters.scout, Filters.speeder_bike)));
                    if (deployable.isEmpty()) {
                        appendPutRemainingBackOnTop(subAction);
                        return;
                    }

                    subAction.insertEffect(
                            new ChooseCardEffect(subAction, _playerId, "Choose scout or speeder bike to deploy to battle", deployable) {
                                @Override
                                protected void cardSelected(PhysicalCard selectedCard) {
                                    subAction.insertEffect(
                                            new DeployCardToLocationFromHandEffect(subAction, selectedCard, Filters.battleLocation, true, false),
                                            new PassthruEffect(subAction) {
                                                @Override
                                                protected void doPlayEffect(SwccgGame game) {
                                                    subAction.insertEffect(getChooseAndDeployEffect(subAction));
                                                }
                                            }
                                    );
                                }
                            }
                    );
                }
            };
        }

        @Override
        protected boolean wasActionCarriedOut() {
            return true;
        }
    }
}
