package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Alien
 * Title: Figrin D'an
 */
public class Card1_009 extends AbstractAlien {
    public Card1_009() {
        super(Side.LIGHT, 2, 2, 1, 1, 4, Title.Figrin_Dan, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.U2);
        setLore("A male Bith. Musician who leads the Mos Eisley Cantina band, Figrin D'an and the Modal Nodes. Expert gambler and card shark.");
        setGameText("Once each turn during your control phase, you may use 1 Force to make a 'wager.' Draw three destiny. Opponent draws two destiny. Player with highest total may randomly select one card to be retrieved from that player's Lost Pile.");
        addKeywords(Keyword.MUSICIAN, Keyword.GAMBLER);
        setSpecies(Species.BITH);
        addPersona(Persona.FIGRIN_DAN);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canUseForce(game, playerId, 1)
                && (GameConditions.hasReserveDeck(game, playerId) || GameConditions.hasReserveDeck(game, opponent))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Make a 'wager'");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId, 3) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> playersDestinyCardDraws, List<Float> playersDestinyDrawValues, final Float playersTotalDestiny) {
                            action.appendEffect(
                                    new DrawDestinyEffect(action, opponent, 2) {
                                        @Override
                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> opponentsDestinyCardDraws, List<Float> opponentsDestinyDrawValues, Float opponentsTotalDestiny) {
                                            final GameState gameState = game.getGameState();

                                            gameState.sendMessage(playerId + "'s destiny: " + (playersTotalDestiny != null ? GuiUtils.formatAsString(playersTotalDestiny) : "Failed destiny draw"));
                                            gameState.sendMessage(opponent + "'s destiny: " + (opponentsTotalDestiny != null ? GuiUtils.formatAsString(opponentsTotalDestiny) : "Failed destiny draw"));

                                            final String winner = (playersTotalDestiny != null && (opponentsTotalDestiny == null || playersTotalDestiny > opponentsTotalDestiny)) ? playerId :
                                                    ((opponentsTotalDestiny != null && (playersTotalDestiny == null || opponentsTotalDestiny > playersTotalDestiny)) ? opponent : null);

                                            if (winner != null) {
                                                gameState.sendMessage("Result: " + winner + " wins the 'wager'");
                                                if (!Filters.mayContributeToForceRetrieval.accepts(game, self)) {
                                                    action.appendEffect(
                                                            new SendMessageEffect(action, "Force retrieval not allowed due to including cards not allowed to contribute to Force retrieval"));
                                                }
                                                else {
                                                    action.appendEffect(
                                                            new PlayoutDecisionEffect(action, winner,
                                                                    new YesNoDecision("Do you want to retrieve a random card from your Lost Pile?") {
                                                                        @Override
                                                                        protected void yes() {
                                                                            gameState.sendMessage(winner + " chooses to retrieve a random card from Lost Pile");
                                                                            action.appendEffect(
                                                                                    new RetrieveForceEffect(action, winner, 1, true));
                                                                        }

                                                                        @Override
                                                                        protected void no() {
                                                                            gameState.sendMessage(winner + " chooses to not retrieve a random card from Lost Pile");
                                                                        }
                                                                    }));
                                                }
                                            }
                                            else {
                                                gameState.sendMessage("Result: 'Wager' ends in tie");
                                            }
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
