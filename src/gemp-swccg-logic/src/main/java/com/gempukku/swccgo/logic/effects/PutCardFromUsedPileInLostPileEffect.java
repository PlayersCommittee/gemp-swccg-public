package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.PutCardInCardPileFromOffTableResult;

import java.util.Collections;

/*
 This action is for putting a card from Used Pile in Lost Pile.
 */
public class PutCardFromUsedPileInLostPileEffect extends AbstractStandardEffect {
    private String _playerId;
    private PhysicalCard _card;

    // TODO: Update these effects to be as generic as needed

    public PutCardFromUsedPileInLostPileEffect(Action action, String playerId, PhysicalCard card) {
        super(action);
        _playerId = playerId;
        _card = card;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return GameUtils.getZoneFromZoneTop(_card.getZone())==Zone.USED_PILE;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        if (isPlayableInFull(game)) {
            GameState gameState = game.getGameState();
            gameState.sendMessage(_playerId + " places " + GameUtils.getCardLink(_card) + " in Lost Pile");
            gameState.removeCardsFromZone(Collections.singleton(_card));
            gameState.addCardToTopOfZone(_card, Zone.LOST_PILE, _card.getOwner());

            game.getActionsEnvironment().emitEffectResult(
                    new PutCardInCardPileFromOffTableResult(_action, _card, _card.getZoneOwner(), Zone.LOST_PILE, false));

            return new FullEffectResult(true);
        }

        return new FullEffectResult(false);
    }
}
