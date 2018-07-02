package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Vedain
 */
public class Card6_127 extends AbstractAlien {
    public Card6_127() {
        super(Side.DARK, 2, 2, 2, 1, 2, "Vedain", Uniqueness.UNIQUE);
        setLore("Kajain'sa'Nikto scout. Sold by his family into slavery to Jabba. Gambler. Plays sabacc with his fellow Nikto.");
        setGameText("Deploys only on Tatooine. During your control phase, if at a Jabba's Palace site, may use 2 Force to make a 'wager'. Both players draw one destiny. Player with lower number loses 1 Force.");
        addIcons(Icon.JABBAS_PALACE);
        addKeywords(Keyword.SCOUT, Keyword.GAMBLER);
        setSpecies(Species.NIKTO);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.isAtLocation(game, self, Filters.Jabbas_Palace_site)
                && GameConditions.canUseForce(game, playerId, 2)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Make a 'wager'");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Perform result(s)
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float playersTotalDestiny) {
                            action.appendEffect(
                                    new DrawDestinyEffect(action, opponent) {
                                        @Override
                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, final List<Float> destinyDrawValues, Float opponentsTotalDestiny) {
                                            final GameState gameState = game.getGameState();

                                            gameState.sendMessage(playerId + "'s destiny: " + (playersTotalDestiny != null ? GuiUtils.formatAsString(playersTotalDestiny) : "Failed destiny draw"));
                                            gameState.sendMessage(opponent + "'s destiny: " + (opponentsTotalDestiny != null ? GuiUtils.formatAsString(opponentsTotalDestiny) : "Failed destiny draw"));

                                            final String loser = ((playersTotalDestiny != null ? playersTotalDestiny : 0) > (opponentsTotalDestiny != null ? opponentsTotalDestiny : 0)) ? opponent :
                                                    (((opponentsTotalDestiny != null ? opponentsTotalDestiny : 0) > (playersTotalDestiny != null ? playersTotalDestiny : 0)) ? playerId : null);

                                            if (loser != null) {
                                                gameState.sendMessage("Result: " + loser + " loses the 'wager'");
                                                action.appendEffect(
                                                        new LoseForceEffect(action, loser, 1));
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
