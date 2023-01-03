package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.List;


/**
 * Set: Reflections III
 * Type: Character
 * Subtype: Alien
 * Title: Jabba Desilijic Tiure
 */
public class Card13_071 extends AbstractAlien {
    public Card13_071() {
        super(Side.DARK, 3, 3, 3, 3, 5, "Jabba Desilijic Tiure", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("Gangster and leader in control of Tatooine. The amound of credits Jabba earns from Podracing is said to be vast, but only Bib Fortuna knows the exact number.");
        setGameText("While at Podrace Arena, once during your control phase may 'wager'. Both players draw destiny and add 2 if they have won a Podrace. Loser (lowest total) lose 1 force. If you just won the wager, and Bib Fortuna is present, you may retrieve 1 force.");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
        addPersona(Persona.JABBA);
        setSpecies(Species.HUTT);
        addKeywords(Keyword.GANGSTER, Keyword.LEADER);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.isAtLocation(game, self, Filters.Podrace_Arena)
                && GameConditions.canDrawDestiny(game, playerId)
                && GameConditions.canDrawDestiny(game, game.getOpponent(playerId))) {
            final String opponent = game.getOpponent(playerId);
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setActionMsg("Wager");
            action.setText("Wager");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float playersDestiny) {
                            action.appendEffect(
                                    new DrawDestinyEffect(action, opponent) {
                                        @Override
                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float opponentsDestiny) {
                                            final GameState gameState = game.getGameState();
                                            Float playersTotalDestiny = playersDestiny;
                                            Float opponentsTotalDestiny = opponentsDestiny;

                                            if (GameConditions.hasWonPodrace(game, playerId)) {
                                                playersTotalDestiny = playersTotalDestiny + 2;
                                            }

                                            if (GameConditions.hasWonPodrace(game, opponent)) {
                                                opponentsTotalDestiny = opponentsDestiny + 2;
                                            }

                                            gameState.sendMessage(playerId + "'s 'Wager' destiny: " + (playersTotalDestiny != null ? GuiUtils.formatAsString(playersTotalDestiny) : "Failed destiny draw"));
                                            gameState.sendMessage(opponent + "'s 'Wager' destiny: " + (opponentsTotalDestiny != null ? GuiUtils.formatAsString(opponentsTotalDestiny) : "Failed destiny draw"));


                                            final String loser = (playersTotalDestiny != null && (opponentsTotalDestiny == null || playersTotalDestiny < opponentsTotalDestiny)) ? playerId :
                                                    ((opponentsTotalDestiny != null && (playersTotalDestiny == null || opponentsTotalDestiny < playersTotalDestiny)) ? opponent : null);

                                            if (loser != null) {
                                                action.appendEffect(
                                                        new LoseForceEffect(action, loser, 1)
                                                );
                                                if (!playerId.equals(loser)
                                                        && GameConditions.isPresentWith(game, self, Filters.Bib)) {
                                                    action.appendEffect(
                                                            new RetrieveForceEffect(action, playerId, 1)
                                                    );
                                                }
                                            } else {
                                                gameState.sendMessage("Result: 'Wager' ends in tie");
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