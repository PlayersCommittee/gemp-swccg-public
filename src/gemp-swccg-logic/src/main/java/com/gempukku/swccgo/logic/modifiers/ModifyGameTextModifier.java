package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A game text modifier. This is used when a card changes the game text of another card in a very specific way. These
 * should only be used if there is not a reasonable way to have the change be handled in a more general way by the game
 * engine.
 */
public class ModifyGameTextModifier extends AbstractModifier {
    private ModifyGameTextType _type;

    /**
     * Creates a game text modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose game text is modified
     * @param type the game text modification
     */
    public ModifyGameTextModifier(PhysicalCard source, Filterable affectFilter, ModifyGameTextType type) {
        this(source, affectFilter, null, type);
    }

    /**
     * Creates a game text modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose game text is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param type the game text modification
     */
    public ModifyGameTextModifier(PhysicalCard source, Filterable affectFilter, Condition condition, ModifyGameTextType type) {
        super(source, null, affectFilter, condition, ModifierType.MODIFY_GAME_TEXT, true);
        _type = type;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return _type.getHumanReadable();
    }

    @Override
    public ModifyGameTextType getModifyGameTextType(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _type;
    }
}
