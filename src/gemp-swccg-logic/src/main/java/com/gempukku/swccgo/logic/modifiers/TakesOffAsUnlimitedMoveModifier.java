package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes specified cards to take off as an unlimited move
 */
public class TakesOffAsUnlimitedMoveModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes the source card to take off as an unlimited move.
     * @param source the card that is the source of the modifier and takes off as an unlimited move
     */
    public TakesOffAsUnlimitedMoveModifier(PhysicalCard source) {
        this(source, source, null);
    }

    /**
     * Creates a modifier that causes affected cards to take off as an unlimited move.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that take off as an unlimited move
     */
    public TakesOffAsUnlimitedMoveModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that causes the source card to take off as an unlimited move.
     * @param source the card that is the source of the modifier and takes off as an unlimited move
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public TakesOffAsUnlimitedMoveModifier(PhysicalCard source, Condition condition) {
        this(source, source, condition);
    }

    /**
     * Creates a modifier that causes affected cards to take off as an unlimited move.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that take off as an unlimited move
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public TakesOffAsUnlimitedMoveModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, null, Filters.and(Filters.in_play, affectFilter), condition, ModifierType.TAKES_OFF_AS_UNLIMITED_MOVE, true);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Takes off as an unlimited move";
    }
}
