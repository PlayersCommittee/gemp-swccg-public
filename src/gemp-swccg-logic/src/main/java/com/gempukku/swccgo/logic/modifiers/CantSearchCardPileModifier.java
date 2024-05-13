package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;

/**
 * A modifier to prohibit a specified card pile from being searched using a previously failed search function.
 */
public class CantSearchCardPileModifier extends AbstractModifier {
    private Zone _cardPile;
    private String _cardPileOwner;
    private GameTextActionId _gameTextActionId;

    /**
     * Creates a modifier that prohibits a specified card pile from being searched using a previously failed search function.
     * @param affectFilter the filter for cards affected by this modifier
     * @param playerId the player not allowed to search
     * @param cardPile the card pile
     * @param cardPileOwner the owner of the card pile
     * @param gameTextActionId the game text action id
     */
    public CantSearchCardPileModifier(Filterable affectFilter, String playerId, Zone cardPile, String cardPileOwner, GameTextActionId gameTextActionId) {
        super(null, null, affectFilter, null, ModifierType.CANT_SEARCH_CARD_PILE, true);
        _playerId = playerId;
        _cardPile = cardPile;
        _cardPileOwner = cardPileOwner;
        _gameTextActionId = gameTextActionId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May not search " + _cardPile.getHumanReadable() + " with previously failed search function";
    }

    @Override
    public boolean isProhibitedFromSearchingCardPile(GameState gameState, ModifiersQuerying modifiersQuerying, String playerId,
                                                     Zone cardPile, String cardPileOwner, GameTextActionId gameTextActionId) {
        return playerId.equals(_playerId) && cardPile == _cardPile && cardPileOwner.equals(_cardPileOwner)
                && gameTextActionId == _gameTextActionId;
    }
}
