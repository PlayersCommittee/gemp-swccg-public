package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.ChooseEffectOrderEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Beru Stew
 */
public class Card1_072 extends AbstractLostInterrupt {
    public Card1_072() {
        super(Side.LIGHT, 4, Title.Beru_Stew, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.U2);
        setLore("Moisture farmers grow enough food to sustain Tatooine's population. Beru Lars has devised many dishes using herbs and roots naturally found in Tatooine's desert.");
        setGameText("Each player must immediately activate 2 Force. Also, you may activate 1 additional Force for each Beru Lars, Owen Lars or Hydroponics Station on table.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.canActivateForce(game, playerId)
                && GameConditions.canActivateForce(game, opponent)) {
            final int numberOnCard1 = game.getModifiersQuerying().isDoubled(game.getGameState(), self) ? 2 : 1;
            final int numberOnCard2 = game.getModifiersQuerying().isDoubled(game.getGameState(), self) ? 4 : 2;

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make each player activate " + numberOnCard2 + " Force");
            // Choose target(s)
            action.appendTargeting(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(final SwccgGame game) {
                            // Set secondary target filter(s)
                            action.addSecondaryTargetFilter(Filters.or(Filters.Beru, Filters.Owen, Filters.Hydroponics_Station));
                            // Allow response(s)
                            action.allowResponses(
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            List<StandardEffect> effectsToOrder = new ArrayList<StandardEffect>();

                                            int forceToActivateSelf = Math.min(numberOnCard2, game.getGameState().getReserveDeckSize(playerId));
                                            int forceToActivateOpp = Math.min(numberOnCard2, game.getGameState().getReserveDeckSize(opponent));

                                            effectsToOrder.add(new ActivateForceEffect(action, playerId, forceToActivateSelf));
                                            effectsToOrder.add(new ActivateForceEffect(action, opponent, forceToActivateOpp));
                                            action.appendEffect(
                                                    new ChooseEffectOrderEffect(action, effectsToOrder));
                                            action.appendEffect(
                                                    new PassthruEffect(action) {
                                                        @Override
                                                        protected void doPlayEffect(final SwccgGame game) {
                                                            if (GameConditions.canActivateForce(game, playerId)) {
                                                                int deckSize = game.getGameState().getReserveDeckSize(playerId);
                                                                if (deckSize > 0) {
                                                                    int numAdditional = Math.min(Filters.countActive(game, self,
                                                                            Filters.and(Filters.or(Filters.Beru, Filters.Owen, Filters.Hydroponics_Station), Filters.canBeTargetedBy(self))) * numberOnCard1, deckSize);
                                                                    if (numAdditional > 0)
                                                                        action.appendEffect(
                                                                                new PlayoutDecisionEffect(action, playerId,
                                                                                        new IntegerAwaitingDecision("Choose number of additional Force to activate", 0, numAdditional, numAdditional) {
                                                                                            @Override
                                                                                            public void decisionMade(int result) throws DecisionResultInvalidException {
                                                                                                if (result > 0) {
                                                                                                    action.appendEffect(
                                                                                                            new ActivateForceEffect(action, playerId, result));
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                )
                                                                        );
                                                                }
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
            return Collections.singletonList(action);
        }
        return null;
    }
}