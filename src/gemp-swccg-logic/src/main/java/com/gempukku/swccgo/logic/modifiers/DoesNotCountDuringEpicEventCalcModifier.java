package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

public class DoesNotCountDuringEpicEventCalcModifier extends AbstractModifier {
    public DoesNotCountDuringEpicEventCalcModifier(PhysicalCard source) {
        this(source, source);
    }

    public DoesNotCountDuringEpicEventCalcModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Does not count when calculating Epic Events", affectFilter, ModifierType.IGNORE_DURING_EPIC_EVENT_CALCULATION);
    }

    public DoesNotCountDuringEpicEventCalcModifier(PhysicalCard source, Condition condition, Filterable affectFilter) {
        super(source, "Does not count when calculating Epic Events", affectFilter, condition, ModifierType.IGNORE_DURING_EPIC_EVENT_CALCULATION);
    }
}
