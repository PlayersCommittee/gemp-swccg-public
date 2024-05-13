package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

public class SpecialFlagModifier extends AbstractModifier {
    private ModifierFlag _modifierFlag;
    private Condition _condition;

    // Since all the SPECIAL_FLAG modifiers are retrieved together very frequently and some may
    // have "expensive" conditions to check, defer checking the condition until we know this is the
    // specific special flag we are interested in.  That is why super() is passed null for the condition,
    // and we instead store the condition locally and check it last during hasFlagActive.

    public SpecialFlagModifier(PhysicalCard source, ModifierFlag modifierFlag) {
        this(source, modifierFlag, null);
    }

    public SpecialFlagModifier(PhysicalCard source, ModifierFlag modifierFlag, String playerId) {
        this(source, null, modifierFlag, playerId);
    }

    public SpecialFlagModifier(PhysicalCard source, Condition condition, ModifierFlag modifierFlag) {
        this(source, condition, modifierFlag, null);
    }

    public SpecialFlagModifier(PhysicalCard source, Condition condition, ModifierFlag modifierFlag, String playerId) {
        super(source, "Special flag set", null, null, ModifierType.SPECIAL_FLAG, true);
        _modifierFlag = modifierFlag;
        _playerId = playerId;
        _condition = condition;
    }

    @Override
    public boolean hasFlagActive(GameState gameState, ModifiersQuerying modifiersQuerying, ModifierFlag modifierFlag, String playerId) {
        if (_playerId!=null && playerId!=null && !_playerId.equals(playerId))
            return false;

        if (modifierFlag != _modifierFlag)
            return false;

        return (_condition == null || _condition.isFulfilled(gameState, modifiersQuerying));
    }
}
