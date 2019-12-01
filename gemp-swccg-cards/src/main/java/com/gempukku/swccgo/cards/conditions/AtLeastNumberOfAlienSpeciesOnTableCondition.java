package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

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
        return getNumberOfSpecies() >= _compareValue;
    }

    private int getNumberOfSpecies() {
        int numSpecies = 0;
        for (Species species : Species.values()) {
            if (GameConditions.canSpot(_game, _self, 1, false, Filters.and(Filters.species(species), Filters.alien, Filters.your(_self.getOwner())))) {
                numSpecies++;
            }
        }
        return numSpecies;
    }
}
