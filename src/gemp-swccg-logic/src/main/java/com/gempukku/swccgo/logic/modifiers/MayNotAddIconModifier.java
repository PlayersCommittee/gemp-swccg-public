package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

public class MayNotAddIconModifier extends AbstractModifier {
    private Icon _icon;
    private Evaluator _evaluator;

    public MayNotAddIconModifier(PhysicalCard source, Filterable affectFilter, Icon icon) {
        this(source, affectFilter, icon, 1);
    }

    public MayNotAddIconModifier(PhysicalCard source, Filterable affectFilter, Icon icon, int count) {
        this(source, affectFilter, null, icon, count);
    }

    public MayNotAddIconModifier(PhysicalCard source, Condition condition, Icon icon) {
        this(source, source, condition, icon, 1);
    }

    public MayNotAddIconModifier(PhysicalCard source, Condition condition, Icon icon, int count) {
        this(source, source, condition, icon, count);
    }

    public MayNotAddIconModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Icon icon) {
        this(source, affectFilter, condition, icon, 1);
    }

    public MayNotAddIconModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Icon icon, int count) {
        this(source, affectFilter, condition, icon, new ConstantEvaluator(count));
    }

    public MayNotAddIconModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Icon icon, Evaluator evaluator) {
        super(source, "May not add "+ icon.getHumanReadable() + " icons", affectFilter, condition, ModifierType.MAY_NOT_ADD_ICON, false);
        _icon = icon;
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May not add " + _icon.getHumanReadable() + " icons";
    }

    @Override
    public Icon getIcon() {
        return _icon;
    }
}
