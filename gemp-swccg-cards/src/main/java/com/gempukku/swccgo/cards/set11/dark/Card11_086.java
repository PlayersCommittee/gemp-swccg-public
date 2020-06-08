package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardToLoseFromTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Sith Fury
 */
public class Card11_086 extends AbstractUsedOrLostInterrupt {
    public Card11_086() {
        super(Side.DARK, 4, Title.Sith_Fury, Uniqueness.UNIQUE);
        setLore("At his peak, no one could stand up to the Dark Lord of the Sith. His superior tactics devastated those who opposed him.");
        setGameText("USED: During a battle, lose one of your lightsabers in that battle to cause all non-unique aliens present to be lost. LOST: If a duel was just initiated, draw 2 destiny. Opponent draws 3 destiny. If the player with the lower total loses the duel, that player loses 6 Force.");
        addIcons(Icon.TATOOINE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        Filter lightsaberFilter = Filters.and(Filters.your(self), Filters.lightsaber, Filters.participatingInBattle);
        final Filter alienFilter = Filters.and(Filters.non_unique, Filters.alien, Filters.presentInBattle);

        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && GameConditions.isDuringBattleWithParticipant(game, alienFilter)
                && GameConditions.canTarget(game, self, lightsaberFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Lose lightsaber to make non-unique aliens lost");
            // Pay cost(s)
            action.appendCost(
                    new ChooseCardToLoseFromTableEffect(action, playerId, true, lightsaberFilter));
            // Allow response(s)
            action.allowResponses("Make all non-unique aliens present lost",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            final Collection<PhysicalCard> aliens = Filters.filterActive(game, self, alienFilter);
                            if (!aliens.isEmpty()) {
                                action.appendEffect(
                                        new LoseCardsFromTableEffect(action, aliens, true));
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        final int cardId = self.getCardId();

        // Check condition(s)
        if (TriggerConditions.duelInitiated(game, effectResult)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Make both players draw destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DrawDestinyEffect(action, playerId, 2) {
                                        @Override
                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> playersDestinyCardDraws, List<Float> playersDestinyDrawValues, final Float playersTotalDestiny) {
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, opponent, 3) {
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> opponentsDestinyCardDraws, List<Float> opponentsDestinyDrawValues, Float opponentsTotalDestiny) {
                                                            final GameState gameState = game.getGameState();

                                                            gameState.sendMessage(playerId + "'s destiny: " + (playersTotalDestiny != null ? GuiUtils.formatAsString(playersTotalDestiny) : "Failed destiny draw"));
                                                            gameState.sendMessage(opponent + "'s destiny: " + (opponentsTotalDestiny != null ? GuiUtils.formatAsString(opponentsTotalDestiny) : "Failed destiny draw"));

                                                            final String lowerTotalPlayer = (playersTotalDestiny != null && (opponentsTotalDestiny == null || playersTotalDestiny > opponentsTotalDestiny)) ? opponent :
                                                                    ((opponentsTotalDestiny != null && (playersTotalDestiny == null || opponentsTotalDestiny > playersTotalDestiny)) ? playerId : null);

                                                            if (lowerTotalPlayer != null) {
                                                                game.getGameState().sendMessage("Result: " + lowerTotalPlayer + " has lower total");
                                                                action.appendEffect(
                                                                        new AddUntilEndOfDuelActionProxyEffect(action,
                                                                                new AbstractActionProxy() {
                                                                                    @Override
                                                                                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                                                                        // Check condition(s)
                                                                                        if (TriggerConditions.lostDuel(game, effectResult, lowerTotalPlayer)) {

                                                                                            final RequiredGameTextTriggerAction action1 = new RequiredGameTextTriggerAction(self, cardId);
                                                                                            action1.setText("Make " + lowerTotalPlayer + " lose 6 Force");
                                                                                            // Perform result(s)
                                                                                            action1.appendEffect(
                                                                                                    new LoseForceEffect(action1, lowerTotalPlayer, 6));
                                                                                            return Collections.singletonList((TriggerAction) action1);
                                                                                        }
                                                                                        return null;
                                                                                    }
                                                                                }
                                                                        ));
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
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}