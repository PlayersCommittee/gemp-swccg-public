package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;


/**
 * An evaluator that returns the result of the specified evaluator
 * multiplied by the number of different TIE Models within the evaluated card.
 */
public class PerTIESubtypeEvaluator extends BaseEvaluator {
    private int _amountPerTIESubtype;
    private Filterable _tiesToIncludeFilter;


    /**
     * Creates an evaluator that returns the result of the specified evaluator multiplied by the number of TIEs within
     * the evaluated card.
     *
     * @param amountPerTIESubtype the amount per TIE
     * @param tiesToIncludeFilter TIES to include in the calculation (filtered by location, etc)
     */
    public PerTIESubtypeEvaluator(int amountPerTIESubtype, Filterable tiesToIncludeFilter) {
        _amountPerTIESubtype = amountPerTIESubtype;
        _tiesToIncludeFilter = tiesToIncludeFilter;
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {

        // Sum of all of the different classes of TIES
        int totalClasses = 0;

        if (GameConditions.canSpot(gameState.getGame(), self, Filters.and(_tiesToIncludeFilter, Filters.TIE_Advanced_x1))) {
            totalClasses += 1;
        }
        if (GameConditions.canSpot(gameState.getGame(), self, Filters.and(_tiesToIncludeFilter, Filters.TIE_Avenger))) {
            totalClasses += 1;
        }
        // Important: TIE_Bomber is an alias for TIE_SA, so don't count that twice!
        //            Leaving this line in here for caution to it's clearly not an omission
        // if (Filters.TIE_Bomber.accepts(gameState, modifiersQuerying, self)) {
        //     totalClasses += 1;
        // }
        if (GameConditions.canSpot(gameState.getGame(), self, Filters.and(_tiesToIncludeFilter, Filters.TIE_Defender))) {
            totalClasses += 1;
        }
        if (GameConditions.canSpot(gameState.getGame(), self, Filters.and(_tiesToIncludeFilter, Filters.TIE_Interceptor))) {
            totalClasses += 1;
        }
        if (GameConditions.canSpot(gameState.getGame(), self, Filters.and(_tiesToIncludeFilter, Filters.TIE_ln))) {
            totalClasses += 1;
        }
        if (GameConditions.canSpot(gameState.getGame(), self, Filters.and(_tiesToIncludeFilter, Filters.TIE_rc))) {
            totalClasses += 1;
        }
        if (GameConditions.canSpot(gameState.getGame(), self, Filters.and(_tiesToIncludeFilter, Filters.TIE_sa))) {
            totalClasses += 1;
        }
        if (GameConditions.canSpot(gameState.getGame(), self, Filters.and(_tiesToIncludeFilter, Filters.TIE_sr))) {
            totalClasses += 1;
        }
        if (GameConditions.canSpot(gameState.getGame(), self, Filters.and(_tiesToIncludeFilter, Filters.TIE_vn))) {
            totalClasses += 1;
        }

        return _amountPerTIESubtype * totalClasses;
    }

}

