package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.evaluators.AtSameSiteEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalDestinyModifier;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Barquin D'an
 */
public class Card7_166 extends AbstractAlien {
    public Card7_166() {
        super(Side.DARK, 2, 2, 2, 1, 2, "Barquin D'an", Uniqueness.UNIQUE);
        setLore("Bith musician and gambler. Estranged older brother of Figrin D'an. Plays kloo horn, but not as well as his brother. Briefly jammed with Max Rebo's band.");
        setGameText("Once during each of your control phases, may use 1 Force to make a 'wager.' Both players draw two destiny (add 1 to your total destiny for each of your gamblers at same site). Player with highest total may retrieve 1 Force.");
        addIcons(Icon.SPECIAL_EDITION);
        addKeywords(Keyword.MUSICIAN, Keyword.GAMBLER);
        setSpecies(Species.BITH);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canUseForce(game, playerId, 1)
                && (GameConditions.canDrawDestiny(game, playerId) || GameConditions.canDrawDestiny(game, opponent))) {

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
                    new DrawDestinyEffect(action, playerId, 2) {
                        @Override
                        protected List<Modifier> getDrawDestinyModifiers(SwccgGame game, DrawDestinyState drawDestinyState) {
                            Modifier modifier = new TotalDestinyModifier(self, drawDestinyState.getId(), new AtSameSiteEvaluator(self, Filters.and(Filters.your(self), Filters.gambler)));
                            return Collections.singletonList(modifier);
                        }
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
                                                                            gameState.sendMessage(winner + " chooses to retrieve 1 Force");
                                                                            action.appendEffect(
                                                                                    new RetrieveForceEffect(action, winner, 1));
                                                                        }

                                                                        @Override
                                                                        protected void no() {
                                                                            gameState.sendMessage(winner + " chooses to not retrieve 1 Force");
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
