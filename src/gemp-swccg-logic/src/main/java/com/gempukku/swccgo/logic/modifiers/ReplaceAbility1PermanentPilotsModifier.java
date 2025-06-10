package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier that replaces the ability-1 permanent pilots of affected cards with permanent pilots with a specified amount
 * of ability.
 */
public class ReplaceAbility1PermanentPilotsModifier extends AbstractModifier {
    private float _ability;

    /**
     * Creates a modifier that replaces the ability-1 permanent pilots of affected cards with permanent pilots with a
     * specified amount of ability.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param ability the ability of replacement permanent pilots
     */
    public ReplaceAbility1PermanentPilotsModifier(PhysicalCard source, Filterable affectFilter, float ability) {
        super(source, null, affectFilter, null, ModifierType.REPLACE_ABILITY_1_PERMANENT_PILOTS, true);
        _ability = ability;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Replaced ability-1 permanent pilots with ability-" + GuiUtils.formatAsString(_ability) + " permanent pilots";
    }

    @Override
    public float getReplacementPermanentPilotAbility(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _ability;
    }
}
