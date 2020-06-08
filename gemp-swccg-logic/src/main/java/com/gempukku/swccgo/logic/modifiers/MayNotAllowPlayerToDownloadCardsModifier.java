package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for not being able to allow player to download cards.
 */
public class MayNotAllowPlayerToDownloadCardsModifier extends AbstractModifier {

    /**
     * Creates a modifier that prohibits cards accepted by the input filter from allowing the specified player to download cards.
     * @param source the source card of the modifier
     * @param affectFilter the filter
     * @param playerId the player not allowed to download cards
     */
    public MayNotAllowPlayerToDownloadCardsModifier(PhysicalCard source, Filterable affectFilter, String playerId) {
        this(source, affectFilter, null, playerId);
    }

    /**
     * Creates a modifier that prohibits cards accepted by the input filter from allowing the specified player to download cards.
     * @param source the source card of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player not allowed to download cards
     */
    private MayNotAllowPlayerToDownloadCardsModifier(PhysicalCard source, Filterable affectFilter, Condition condition, String playerId) {
        super(source, "May not allow " + playerId + " to [download] cards", affectFilter, condition, ModifierType.MAY_NOT_ALLOW_PLAYER_TO_DOWNLOAD_CARDS, true);
        _playerId = playerId;
    }
}
