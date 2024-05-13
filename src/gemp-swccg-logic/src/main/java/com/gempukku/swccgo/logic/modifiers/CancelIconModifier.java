package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

public class CancelIconModifier extends AbstractModifier {
    private Icon _icon;

    public CancelIconModifier(PhysicalCard physicalCard, Filterable affectFilter, Icon icon) {
        this(physicalCard, affectFilter, null, icon);
    }

    public CancelIconModifier(PhysicalCard physicalCard, Condition condition, Icon icon) {
        this(physicalCard, physicalCard, condition, icon);
    }

    public CancelIconModifier(PhysicalCard physicalCard, Filterable affectFilter, Condition condition, Icon icon) {
        super(physicalCard, icon.getHumanReadable() + " icon(s) canceled", affectFilter, condition, ModifierType.CANCEL_ICONS, true);
        _icon = icon;
    }

    @Override
    public Icon getIcon() {
        return _icon;
    }
}
