package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.EachDuelDestinyModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * An effect that modifies each of player's duel destinies until end of the duel.
 */
public class ModifyEachDuelDestinyUntilEndOfDuelEffect extends AbstractSuccessfulEffect {
    private String _playerId;
    private float _modifierAmount;

    /**
     * Creates an effect that modifies each of player's duel destinies until end of the duel.
     * @param action the action performing this effect.
     * @param playerId the player whose duel destinies are modified
     * @param modifierAmount the amount to modify
     */
    public ModifyEachDuelDestinyUntilEndOfDuelEffect(Action action, String playerId, float modifierAmount) {
        super(action);
        _playerId = playerId;
        _modifierAmount = modifierAmount;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        DuelState duelState = gameState.getDuelState();
        if (duelState != null) {
            PhysicalCard source = _action.getActionSource();

            game.getModifiersEnvironment().addUntilEndOfDuelModifier(
                    new EachDuelDestinyModifier(source, _modifierAmount, _playerId));
            if (_modifierAmount > 0) {
                gameState.sendMessage(GameUtils.getCardLink(source) + " adds " + GuiUtils.formatAsString(_modifierAmount) + " to each of " + _playerId + "'s duel destiny draws until end of the duel");
            }
            else if (_modifierAmount < 0) {
                gameState.sendMessage(GameUtils.getCardLink(source) + " subtracts " + GuiUtils.formatAsString(-_modifierAmount) + " from each of " + _playerId + "'s duel destiny draws until end of the duel");
            }
        }
    }
}
