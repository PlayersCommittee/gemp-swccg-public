package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;

public class MouseDroidTargetModifier extends AbstractModifier {
    public MouseDroidTargetModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Targeted by Mouse Droid", affectFilter, ModifierType.MOUSE_DROID_TARGET);
    }
}
