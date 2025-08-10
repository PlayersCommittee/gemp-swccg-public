package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier to have card placed in Used Pile (instead of Lost Pile) when canceled.
 */
public class PlaceInUsedPileWhenCanceledModifier extends AbstractModifier {
    private String _canceledByPlayerId;
    private Filter _canceledByFilter;

    /**
     * Creates modifier to have source card placed in Used Pile (instead of Lost Pile) when canceled.
     * @param source the source of the modifier
     */
    public PlaceInUsedPileWhenCanceledModifier(PhysicalCard source) {
        this(source, source, null, null, Filters.any);
    }


    /**
     * Creates modifier to have card placed in Used Pile (instead of Lost Pile) when canceled for any reason)
     * @param source the source of the modifier
     * @param affectFilter the filter for cards placed in Used Pile when canceled
     */
    public PlaceInUsedPileWhenCanceledModifier(PhysicalCard source, Filterable affectFilter)
    {
        this (source, affectFilter, null, null, Filters.any);
    }

    /**
     * Creates modifier to have card placed in Used Pile (instead of Lost Pile) when canceled by specified player.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards placed in Used Pile when canceled by specified player and card
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param canceledByPlayerId the player
     */
    public PlaceInUsedPileWhenCanceledModifier(PhysicalCard source, Filterable affectFilter, Condition condition, String canceledByPlayerId) {
        this(source, affectFilter, condition, canceledByPlayerId, Filters.any);
    }

    /**
     * Creates modifier to have card placed in Used Pile (instead of Lost Pile) when canceled by specified player with a
     * card accepted by the canceled by card filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards placed in Used Pile when canceled by specified player and card
     * @param canceledByPlayerId the player
     * @param canceledByCardFilter the canceled by card filter
     */
    public PlaceInUsedPileWhenCanceledModifier(PhysicalCard source, Filterable affectFilter, String canceledByPlayerId, Filterable canceledByCardFilter) {
        this(source, affectFilter, null, canceledByPlayerId, canceledByCardFilter);
    }

    /**
     * Creates modifier to have card placed in Used Pile (instead of Lost Pile) when canceled by specified player with a
     * card accepted by the canceled by card filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards placed in Used Pile when canceled by specified player and card
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param canceledByPlayerId the player
     * @param canceledByCardFilter the canceled by card filter
     */
    public PlaceInUsedPileWhenCanceledModifier(PhysicalCard source, Filterable affectFilter, Condition condition, String canceledByPlayerId, Filterable canceledByCardFilter) {
        super(source, null, affectFilter, condition, ModifierType.PLACE_IN_USED_PILE_WHEN_CANCELED, true);
        _canceledByPlayerId = canceledByPlayerId;
        _canceledByFilter = Filters.and(canceledByCardFilter);
    }

    @Override
    public boolean isPlacedInUsedPileWhenCanceled(GameState gameState, ModifiersQuerying modifiersQuerying, String canceledByPlayerId, PhysicalCard canceledByCard) {
        return (_canceledByPlayerId == null || _canceledByPlayerId.equals(canceledByPlayerId))
                && canceledByCard != null
                && Filters.and(_canceledByFilter).accepts(gameState, modifiersQuerying, canceledByCard);
    }
}
