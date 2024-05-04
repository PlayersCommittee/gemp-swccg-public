package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

public class RemoveKeywordModifier extends AbstractModifier implements KeywordAffectingModifier {
    private Keyword _keyword;
    private Evaluator _evaluator;

    public RemoveKeywordModifier(PhysicalCard source, Keyword keyword) {
        this(source, source, keyword, 1);
    }

    public RemoveKeywordModifier(PhysicalCard source, Filterable affectFilter, Keyword keyword) {
        this(source, affectFilter, keyword, 1);
    }

    public RemoveKeywordModifier(PhysicalCard source, Filterable affectFilter, Keyword keyword, int count) {
        this(source, affectFilter, null, keyword, count);
    }

    public RemoveKeywordModifier(PhysicalCard source, Condition condition, Keyword keyword) {
        this(source, source, condition, keyword, 1);
    }

    public RemoveKeywordModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Keyword keyword) {
        this(source, affectFilter, condition, keyword, 1);
    }

    public RemoveKeywordModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Keyword keyword, int count) {
        this(source, affectFilter, condition, keyword, new ConstantEvaluator(count));
    }

    public RemoveKeywordModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Keyword keyword, Evaluator evaluator) {
        super(source, null, affectFilter, condition, ModifierType.REMOVE_KEYWORD, true);
        _keyword = keyword;
        _evaluator = evaluator;
    }

    @Override
    public Keyword getKeyword() {
        return _keyword;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Not a "+_keyword.getHumanReadable();
    }

    @Override
    public boolean hasKeyword(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, Keyword keyword) {
        return (keyword == _keyword && _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard) > 0);
    }
}
