package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.GuiUtils;

public class EpicEventDestinyDrawModifier extends AbstractModifier {
    private Evaluator _evaluator;

    public EpicEventDestinyDrawModifier(PhysicalCard source, String playerId, int epicEventDestinyModifier) {
        this(source, playerId, Filters.Epic_Event, epicEventDestinyModifier);
    }

    public EpicEventDestinyDrawModifier(PhysicalCard source, String playerId, Filterable affectFilter, int epicEventDestinyModifier) {
        this(source, playerId, affectFilter, null, epicEventDestinyModifier);
    }

    public EpicEventDestinyDrawModifier(PhysicalCard source, String playerId, Filterable affectFilter, Condition condition, int epicEventDestinyModifier) {
        this(source, playerId, affectFilter, condition, new ConstantEvaluator(epicEventDestinyModifier));
    }

    public EpicEventDestinyDrawModifier(PhysicalCard source, String playerId, Filterable affectFilter, Condition condition, Evaluator evaluator) {
        super(source, null, affectFilter, condition, ModifierType.EACH_EPIC_EVENT_DESTINY_DRAW, false);
        _playerId = playerId;
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        String sideText;
        if (gameState.getDarkPlayer().equals(_playerId))
            sideText = "Each dark side";
        else
            sideText = "Each light side";

        if (value >= 0)
            return sideText + " epic event destiny +" + GuiUtils.formatAsString(value);
        else
            return sideText + " epic event destiny " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getEpicEventDestinyModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard epicEvent) {
        if (playerId.equals(_playerId))
            return _evaluator.evaluateExpression(gameState, modifiersQuerying, epicEvent);
        else
            return 0;
    }
}
