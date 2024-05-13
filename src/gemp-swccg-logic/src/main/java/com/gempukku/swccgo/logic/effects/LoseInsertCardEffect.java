package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.LostCardFromOffTableResult;

import java.util.Collections;

/**
 * An effect that causes the specified 'insert' card to be lost.
 */
public class LoseInsertCardEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _insertCard;

    /**
     * Creates an effect that causes the causes the specified 'insert' card to be lost.
     * @param action the action performing this effect
     * @param insertCard the 'insert' card
     */
    public LoseInsertCardEffect(Action action, PhysicalCard insertCard) {
        super(action);
        _insertCard = insertCard;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        if (!_insertCard.isInsertCardRevealed())
            return;

        GameState gameState = game.getGameState();

        // Places the card in the Lost Pile
        game.getGameState().sendMessage(_insertCard.getOwner() + " puts " + GameUtils.getCardLink(_insertCard) + " on " + Zone.LOST_PILE.getHumanReadable());
        gameState.removeCardsFromZone(Collections.singleton(_insertCard));
        gameState.addCardToTopOfZone(_insertCard, Zone.LOST_PILE, _insertCard.getOwner());

        // Emits effect result that a card was just lost
        game.getActionsEnvironment().emitEffectResult(new LostCardFromOffTableResult(_action, _insertCard));
    }
}
