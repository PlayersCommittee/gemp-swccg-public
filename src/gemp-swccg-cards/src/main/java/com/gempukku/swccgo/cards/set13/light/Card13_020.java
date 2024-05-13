package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromMultiplePilesEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeOneCardIntoHandFromOffTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections III
 * Type: Interrupt
 * Subtype: Used
 * Title: Fall Of A Jedi
 */
public class Card13_020 extends AbstractLostInterrupt {
    public Card13_020() {
        super(Side.LIGHT, 6, "Fall Of A Jedi", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("The blow, when it came, was lightning swift and fatal. But Qui-Gon's death gave new life to his former Padawan.");
        setGameText("If your Jedi was just defeated in lightsaber combat, you may either: Reveal a Dark Jedi's combat cards and place one in opponent's Lost Pile (return others). OR Place Qui-Gon out of play and take [Episode I] Obi-Wan into hand from Lost Pile or Reserve Deck; reshuffle.");
        addIcons(Icon.EPISODE_I, Icon.REFLECTIONS_III);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        if (TriggerConditions.wonLightsaberCombatAgainst(game, effectResult, Filters.any, Filters.and(Filters.your(self), Filters.Jedi))) {

            if (GameConditions.canSpot(game, self, Filters.and(Filters.Dark_Jedi, Filters.hasStacked(Filters.combatCard)))) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Reveal opponent's combat cards");

                action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target Dark Jedi", Filters.and(Filters.Dark_Jedi, Filters.hasStacked(Filters.combatCard))) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                        action.allowResponses("Reveal combat cards of " + GameUtils.getCardLink(targetedCard) + " and choose one to be lost", new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                final PhysicalCard darkJedi = action.getPrimaryTargetCard(targetGroupId);

                                action.appendEffect(new ChooseStackedCardEffect(action, playerId, darkJedi, Filters.combatCard) {
                                    @Override
                                    protected void cardSelected(PhysicalCard selectedCard) {
                                        action.appendEffect(new PutStackedCardInLostPileEffect(action, playerId, selectedCard, false));
                                    }
                                });
                            }
                        });
                    }
                });

                actions.add(action);
            }


            final GameTextActionId gameTextActionId = GameTextActionId.FALL_OF_A_JEDI__TAKE_OBIWAN_INTO_HAND;

            if (GameConditions.canTarget(game, self, TargetingReason.TO_BE_PLACED_OUT_OF_PLAY, Filters.QuiGon)
                    && (GameConditions.canSearchReserveDeck(game, playerId, self, gameTextActionId)
                    || GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId))) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
                action.setText("Search Lost Pile or Reserve Deck");

                action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose Qui-Gon to place out of play", TargetingReason.TO_BE_PLACED_OUT_OF_PLAY, Filters.QuiGon) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                        action.allowResponses("Take [Episode I] Obi-Wan into hand from Lost Pile or Reserve Deck", new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                action.appendCost(new PlaceCardOutOfPlayFromTableEffect(action, targetedCard));
                                boolean canSearchReserveDeck = GameConditions.canSearchReserveDeck(game, playerId, self, gameTextActionId);
                                boolean canSearchLostPile = GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId);
                                if (canSearchReserveDeck
                                        && canSearchLostPile) {
                                    action.appendEffect(new PlayoutDecisionEffect(action, playerId, new YesNoDecision("You will search your Lost Pile. Do you also want to search your Reserve Deck?") {
                                        @Override
                                        protected void yes() {
                                            List<Zone> zones = new LinkedList<>();
                                            zones.add(Zone.LOST_PILE);
                                            zones.add(Zone.RESERVE_DECK);

                                            action.appendEffect(new ChooseCardsFromMultiplePilesEffect(action, playerId, zones, playerId,1, 1, 1, false, false, Filters.and(Icon.EPISODE_I, Filters.ObiWan)) {
                                                @Override
                                                protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                                                    if (selectedCards.size()>0) {
                                                        action.appendEffect(new TakeOneCardIntoHandFromOffTableEffect(action, playerId, selectedCards.iterator().next(), "Take Obi-Wan into hand") {
                                                            @Override
                                                            protected void afterCardTakenIntoHand() {

                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                            action.appendEffect(
                                                    new ShuffleReserveDeckEffect(action, playerId));
                                        }

                                        @Override
                                        protected void no() {
                                            action.appendEffect(
                                                    new TakeCardIntoHandFromLostPileEffect(action, playerId, Filters.and(Icon.EPISODE_I, Filters.ObiWan), false));
                                        }
                                    }));
                                } else if (canSearchReserveDeck
                                        && !canSearchLostPile) {
                                    action.appendEffect(
                                            new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Icon.EPISODE_I, Filters.ObiWan), true));
                                } else if (canSearchLostPile
                                        && !canSearchReserveDeck) {
                                    action.appendEffect(
                                            new TakeCardIntoHandFromLostPileEffect(action, playerId, Filters.and(Icon.EPISODE_I, Filters.ObiWan), false));
                                }
                            }
                        });
                    }

                    @Override
                    protected boolean getUseShortcut() {
                        return true;
                    }
                });

                actions.add(action);
            }
        }

        return actions;
    }
}