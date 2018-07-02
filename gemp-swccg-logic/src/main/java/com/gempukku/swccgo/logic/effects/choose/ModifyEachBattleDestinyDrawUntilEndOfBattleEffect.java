package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * An effect that modifies each of player's battle destinies until end of the battle.
 */
public class ModifyEachBattleDestinyDrawUntilEndOfBattleEffect extends AbstractSuccessfulEffect {
    private String _playerId;
    private float _modifierAmount;

    /**
     * Creates an effect that modifies each of player's battle destinies until end of the battle.
     * @param action the action performing this effect.
     * @param playerId the player whose battle destinies are modified
     * @param modifierAmount the amount to modify
     */
    public ModifyEachBattleDestinyDrawUntilEndOfBattleEffect(Action action, String playerId, float modifierAmount) {
        super(action);
        _playerId = playerId;
        _modifierAmount = modifierAmount;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        BattleState battleState = gameState.getBattleState();
        if (battleState != null) {
            PhysicalCard source = _action.getActionSource();

            game.getModifiersEnvironment().addUntilEndOfBattleModifier(
                    new EachBattleDestinyModifier(source, _modifierAmount, _playerId));
            if (_modifierAmount > 0) {
                gameState.sendMessage(GameUtils.getCardLink(source) + " adds " + GuiUtils.formatAsString(_modifierAmount) + " to each of " + _playerId + "'s battle destiny draws until end of the battle");
            }
            else if (_modifierAmount < 0) {
                gameState.sendMessage(GameUtils.getCardLink(source) + " subtracts " + GuiUtils.formatAsString(-_modifierAmount) + " from each of " + _playerId + "'s battle destiny draws until end of the battle");
            }
        }
    }
}
