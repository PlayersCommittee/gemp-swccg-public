package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes matching captives to be considered active for the duration of a battle.  See Captive Fury.
 */
public class CaptiveMayParticipateInBattleModifier extends AbstractModifier {

    /**
     * Creates a modifier that indicates that one or more captives should be treated as active for the duration of a battle.
     * @param source the card source of the modifier
     * @param affectFilter a filter describing which cards are affected by this modifier
     */
    public CaptiveMayParticipateInBattleModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Captive may participate in battle", affectFilter, null, ModifierType.CAPTIVE_MAY_PARTICIPATE_IN_BATTLE);
    }

}
