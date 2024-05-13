package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for not being able to have game text canceled.
 */
public class MayNotHaveGameTextCanceledModifier extends AbstractModifier {

    /**
     * Creates a modifier for not being able to have game text canceled.
     * @param source the card that is the source of the modifier and whose game text may not be canceled
     */
    public MayNotHaveGameTextCanceledModifier(PhysicalCard source) {
        this(source, source);
    }

    /**
     * Creates a modifier for not being able to have game text canceled.
     * @param source the card that is the source of the modifier and whose game text may not be canceled
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotHaveGameTextCanceledModifier(PhysicalCard source, Condition condition) {
        this(source, source, condition);
    }

    /**
     * Creates a modifier for not being able to have game text canceled.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not have game text canceled
     */
    public MayNotHaveGameTextCanceledModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier not being able to have game text canceled.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not have game text canceled
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotHaveGameTextCanceledModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not have game text canceled", affectFilter, condition, ModifierType.MAY_NOT_HAVE_GAME_TEXT_CANCELED, true);
    }
}
