package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.BattleResultDeterminedResult;

/**
 * An effect that determines the winner of the current battle.
 */
public class DetermineBattleWinnerEffect extends AbstractSuccessfulEffect {
    public enum Result {
        TIE, DARK_WINS, LIGHT_WINS
    }

    /**
     * Creates an effect that determines the winner of the current battle.
     * @param action the action performing this effect
     */
    public DetermineBattleWinnerEffect(Action action) {
        super(action);
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        BattleState battleState = game.getGameState().getBattleState();
        // Check if battle can continue
        if (!battleState.canContinue(game))
            return;

        Result result = getUpcomingResult(game);

        battleState.setCardsParticipatingWhenResultDetermined();

        if (result == Result.TIE) {
            game.getGameState().sendMessage("Battle results in a tie");
            game.getActionsEnvironment().emitEffectResult(
                    new BattleResultDeterminedResult(null, null, battleState.getBattleLocation()));
        }
        else if (result == Result.DARK_WINS) {
            battleState.setWinner(game.getDarkPlayer());
            battleState.setLoser(game.getLightPlayer());
            game.getGameState().sendMessage("Dark side wins battle");
            game.getActionsEnvironment().emitEffectResult(
                    new BattleResultDeterminedResult(game.getDarkPlayer(), game.getLightPlayer(), battleState.getBattleLocation()));
        }
        else {
            battleState.setWinner(game.getLightPlayer());
            battleState.setLoser(game.getDarkPlayer());
            game.getGameState().sendMessage("Light side wins battle");
            game.getActionsEnvironment().emitEffectResult(
                    new BattleResultDeterminedResult(game.getLightPlayer(), game.getDarkPlayer(), battleState.getBattleLocation()));
        }
    }

    /**
     * Determines the result of the current battle and sets the base battle damage.
     * @param game the game
     * @return the result
     */
    private Result getUpcomingResult(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        BattleState battleState = gameState.getBattleState();
        float darkSideTotalPower = GuiUtils.getBattleTotalPower(game, game.getDarkPlayer());
        float lightSideTotalPower = GuiUtils.getBattleTotalPower(game, game.getLightPlayer());
        gameState.sendMessage("Dark side total power: " + GuiUtils.formatAsString(darkSideTotalPower));
        gameState.sendMessage("Light side total power: " + GuiUtils.formatAsString(lightSideTotalPower));

        // Set battle damage and return result
        if (darkSideTotalPower == lightSideTotalPower) {
            return Result.TIE;
        }
        else if (darkSideTotalPower > lightSideTotalPower) {
            String lightPlayer = game.getLightPlayer();
            if (!modifiersQuerying.isTakesNoBattleDamage(gameState, lightPlayer)) {
                battleState.setBaseBattleDamage(lightPlayer, darkSideTotalPower - lightSideTotalPower);
            }
            return Result.DARK_WINS;
        }
        else {
            String darkPlayer = game.getDarkPlayer();
            if (!modifiersQuerying.isTakesNoBattleDamage(gameState, darkPlayer)) {
                battleState.setBaseBattleDamage(darkPlayer, lightSideTotalPower - darkSideTotalPower);
            }
            return Result.LIGHT_WINS;
        }
    }
}
