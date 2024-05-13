package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.RelocateCardToSideOfTableResult;

/**
 * An effect that relocates a card on table to the specified player's side of table.
 */
public class RelocateCardToSideOfTableEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _cardToRelocate;
    private String _zoneOwner;

    /**
     * Creates an effect that attaches a card on table to another card on table.
     * @param action the action performing this effect
     * @param cardToRelocate the card to be relocated
     * @param zoneOwner the player whose side of table to relocate the card to
     */
    public RelocateCardToSideOfTableEffect(Action action, PhysicalCard cardToRelocate, String zoneOwner) {
        super(action);
        _cardToRelocate = cardToRelocate;
        _zoneOwner = zoneOwner;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        if (_action.getPerformingPlayer() == null)
            gameState.sendMessage(GameUtils.getCardLink(_cardToRelocate) + " is relocated to " + _zoneOwner + "'s side of table");
        else
            gameState.sendMessage(_action.getPerformingPlayer() + " relocates " + GameUtils.getCardLink(_cardToRelocate) + " to " + _zoneOwner + "'s side of table");
        gameState.relocateCardToSideOfTable(_cardToRelocate, _zoneOwner);
        game.getActionsEnvironment().emitEffectResult(new RelocateCardToSideOfTableResult(_action.getPerformingPlayer(), _cardToRelocate));
    }
}
