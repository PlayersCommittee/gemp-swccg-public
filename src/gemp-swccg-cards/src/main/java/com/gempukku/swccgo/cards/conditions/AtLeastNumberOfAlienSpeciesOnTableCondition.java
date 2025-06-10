package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

public class AtLeastNumberOfAlienSpeciesOnTableCondition implements Condition {
    private SwccgGame _game;
    private PhysicalCard _self;
    private int _compareValue;

    /**
     * Creates a condition that is fulfilled when any the specified conditions are fulfilled.
     */
    public AtLeastNumberOfAlienSpeciesOnTableCondition(SwccgGame game, PhysicalCard self, int compareValue) {
        _game = game;
        _self = self;
        _compareValue = compareValue;
    }


    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return GameConditions.countSpeciesOnTable(_game, _self.getOwner()) >= _compareValue;
    }
}
