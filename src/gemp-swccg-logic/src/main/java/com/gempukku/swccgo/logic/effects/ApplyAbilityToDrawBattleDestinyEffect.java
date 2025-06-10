package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.AbilityForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersEnvironment;
import com.gempukku.swccgo.logic.modifiers.TotalAbilityForBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * An effect that causes the specified card to apply its ability to draw battle destiny in the current battle.
 */
public class ApplyAbilityToDrawBattleDestinyEffect extends AbstractSuccessfulEffect {
    private String _playerId;
    private PhysicalCard _card;
    private Float _amountToApply;

    /**
     * Creates an effect that causes the specified card to apply its ability to draw battle destiny in the current battle.
     * @param action the action performing this effect
     * @param playerId the player to apply ability to draw battle destiny
     * @param card the card whose ability is applied
     */
    public ApplyAbilityToDrawBattleDestinyEffect(Action action, String playerId, PhysicalCard card) {
        super(action);
        _playerId = playerId;
        _card = card;
    }

    /**
     * Creates an effect that causes the specified card to apply a specified amount of its ability to draw battle destiny
     * in the current battle.
     * @param action the action performing this effect
     * @param playerId the player to apply ability to draw battle destiny
     * @param card the card whose ability is applied
     * @param amountToApply the amount of ability to apply
     */
    public ApplyAbilityToDrawBattleDestinyEffect(Action action, String playerId, PhysicalCard card, float amountToApply) {
        super(action);
        _playerId = playerId;
        _card = card;
        _amountToApply = amountToApply;
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        PhysicalCard source = _action.getActionSource();
        BattleState battleState = gameState.getBattleState();
        if (battleState != null) {
            float maxAmountToApply = game.getModifiersQuerying().getAbilityForBattleDestiny(game.getGameState(), _card);
            if (_amountToApply == null) {
                _amountToApply = maxAmountToApply;
            }
            else {
                _amountToApply = Math.min(_amountToApply, maxAmountToApply);
            }
            if (_amountToApply > 0) {
                gameState.sendMessage(_playerId + " applies " + GuiUtils.formatAsString(_amountToApply) + " of " + GameUtils.getCardLink(_card) + "'s ability to draw battle destiny");
                modifiersEnvironment.addUntilEndOfBattleModifier(
                                new TotalAbilityForBattleDestinyModifier(source, Filters.battleLocation, _amountToApply, _playerId));
                modifiersEnvironment.addUntilEndOfTurnModifier(
                                new AbilityForBattleDestinyModifier(source, _card, -_amountToApply));
            }
        }
    }
}
