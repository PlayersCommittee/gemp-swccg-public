package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;

/**
 * A modifier to prohibit a specified card pile from being searched.
 */
public class MayNotSearchCardPileModifier extends AbstractModifier {
    private Zone _cardPile;
    private String _cardPileOwner;

    /**
     * Creates a modifier that prohibits a specified card pile from being searched.
     *
     * @param affectFilter  the filter for cards affected by this modifier
     * @param playerId      the player not allowed to search
     * @param cardPile      the card pile
     * @param cardPileOwner the owner of the card pile
     */
    public MayNotSearchCardPileModifier(Filterable affectFilter, String playerId, Zone cardPile, String cardPileOwner) {
        super(null, null, affectFilter, null, ModifierType.MAY_NOT_SEARCH_CARD_PILE, true);
        _playerId = playerId;
        _cardPile = cardPile;
        _cardPileOwner = cardPileOwner;
    }


    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May not search " + _cardPile.getHumanReadable();
    }

    @Override
    public boolean isProhibitedFromSearchingCardPile(GameState gameState, ModifiersQuerying modifiersQuerying, String playerId,
                                                     Zone cardPile, String cardPileOwner) {
        return playerId.equals(_playerId) && cardPile == _cardPile && cardPileOwner.equals(_cardPileOwner);
    }
}
