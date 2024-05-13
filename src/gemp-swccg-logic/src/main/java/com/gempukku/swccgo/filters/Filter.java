package com.gempukku.swccgo.filters;

import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * Represents a filter that each card (or each card built-in) is either accepted by or not.
 *
 * Each filter that implements this interface defines which cards (or card built-in) the filter accepts with the accepts method.
 */
public abstract class Filter implements FilterInterface  {
    public final boolean accepts(SwccgGame game, PhysicalCard physicalCard) {
        return accepts(game.getGameState(), game.getModifiersQuerying(), physicalCard);
    }

    public abstract boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);

    public final boolean accepts(SwccgGame game, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
        return accepts(game.getGameState(), game.getModifiersQuerying(), builtInCardBlueprint);
    }

    public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
        return false;
    }

    public boolean acceptsIgnoringOwner(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return accepts(gameState, modifiersQuerying, physicalCard);
    }

    public boolean acceptsSingleModelType(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, ModelType modelTypeToCheck) {
        return accepts(gameState, modifiersQuerying, physicalCard);
    }

    public int acceptsCount(SwccgGame game, PhysicalCard physicalCard) {
        return acceptsCount(game.getGameState(), game.getModifiersQuerying(), physicalCard);
    }

    public int acceptsCount(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return accepts(gameState, modifiersQuerying, physicalCard) ? 1 : 0;
    }
}
