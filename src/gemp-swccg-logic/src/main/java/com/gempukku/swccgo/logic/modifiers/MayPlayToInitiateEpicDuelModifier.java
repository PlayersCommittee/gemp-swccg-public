package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

public class MayPlayToInitiateEpicDuelModifier extends AbstractModifier {
    public MayPlayToInitiateEpicDuelModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "May play to initiate epic duel", affectFilter, ModifierType.MAY_PLAY_TO_INITIATE_EPIC_DUEL);
    }

    public MayPlayToInitiateEpicDuelModifier(PhysicalCard source, Condition condition, Filterable affectFilter) {
        super(source, "May play to initiate epic duel", affectFilter, condition, ModifierType.MAY_PLAY_TO_INITIATE_EPIC_DUEL);
    }
}
