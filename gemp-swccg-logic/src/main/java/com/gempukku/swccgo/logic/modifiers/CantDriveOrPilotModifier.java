package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

public class CantDriveOrPilotModifier extends AbstractModifier {
    public CantDriveOrPilotModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Can't drive or pilot", affectFilter, ModifierType.CANT_DRIVE_OR_PILOT);
    }

    public CantDriveOrPilotModifier(PhysicalCard source, Condition condition, Filterable affectFilter) {
        super(source, "Can't drive or pilot", affectFilter, condition, ModifierType.CANT_DRIVE_OR_PILOT);
    }
}
