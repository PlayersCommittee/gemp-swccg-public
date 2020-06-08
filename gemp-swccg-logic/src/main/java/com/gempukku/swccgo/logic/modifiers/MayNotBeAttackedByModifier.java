package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;

public class MayNotBeAttackedByModifier extends MayNotBeAttackedModifier {

    public MayNotBeAttackedByModifier(PhysicalCard source, Filterable attackedByFilter) {
        this(source, source, attackedByFilter);
    }

    public MayNotBeAttackedByModifier(PhysicalCard source, Filterable acceptFilter, Filterable attackedByFilter) {
        super(source, null, acceptFilter, attackedByFilter);
    }

}
