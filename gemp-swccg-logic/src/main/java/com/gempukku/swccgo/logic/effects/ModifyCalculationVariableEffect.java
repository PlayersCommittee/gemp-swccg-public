package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.CalculationVariableModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersEnvironment;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * An effect to modify the value of a calculation variable without a specified duration.
 */
public class ModifyCalculationVariableEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _cardToModify;
    private Variable _variable;
    private float _modifierAmount;

    /**
     * Creates an effect that modifies the value of a calculation variable without a specified duration.
     * @param action the action performing this effect
     * @param cardToModify the card whose power is modified
     * @param variable the variable to modify
     * @param modifierAmount the amount of the modifier
     */
    public ModifyCalculationVariableEffect(Action action, PhysicalCard cardToModify, Variable variable, float modifierAmount) {
        super(action);
        _cardToModify = cardToModify;
        _variable = variable;
        _modifierAmount = modifierAmount;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        PhysicalCard source = _action.getActionSource();

        String actionMsg;
        if (_modifierAmount < 0)
            actionMsg = "reduces " + _variable + " on " + GameUtils.getCardLink(_cardToModify) + " by " + GuiUtils.formatAsString(-_modifierAmount);
        else
            actionMsg = "adds " + GuiUtils.formatAsString(_modifierAmount) + " to " + _variable + " on " + GameUtils.getCardLink(_cardToModify);

        if (_action.getPerformingPlayer() == null)
            gameState.sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " " + actionMsg);
        else
            gameState.sendMessage(_action.getPerformingPlayer() + " " + actionMsg + " using " + GameUtils.getCardLink(_action.getActionSource()));

        // Filter for same card while it is in play
        Filter cardFilter = Filters.and(Filters.sameCardId(_cardToModify), Filters.in_play);

        Modifier modifier = new CalculationVariableModifier(source, cardFilter, _modifierAmount, _variable);
        modifier.skipSettingNotRemovedOnRestoreToNormal();

        // If during battle and the source if the action is not a weapon, then modify until end of the battle, otherwise
        // lasts for remainder of game (until card leaves play).
        if (gameState.isDuringBattle()
                && !Filters.weapon_or_character_with_permanent_weapon.accepts(gameState, modifiersQuerying, source)) {
            modifiersEnvironment.addUntilEndOfBattleModifier(modifier);
        }
        else {
            modifiersEnvironment.addUntilEndOfGameModifier(modifier);
        }
    }
}
