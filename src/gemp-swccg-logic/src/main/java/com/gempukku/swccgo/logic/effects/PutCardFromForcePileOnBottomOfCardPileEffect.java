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

public class PutCardFromForcePileOnBottomOfCardPileEffect extends AbstractStandardEffect {
    private PhysicalCard _card;
    private Zone _cardPile;
    private boolean _hidden;

    // TODO: Update these effects to be as generic as needed

    public PutCardFromForcePileOnBottomOfCardPileEffect(Action action, PhysicalCard card, Zone cardPile, boolean hidden) {
        super(action);
        _card = card;
        _cardPile = cardPile;
        _hidden = hidden;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return GameUtils.getZoneFromZoneTop(_card.getZone()) == Zone.FORCE_PILE;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        if (!isPlayableInFull(game))
            return new FullEffectResult(false);

        if (_hidden) {
            _hidden = !game.getGameState().isCardPileFaceUp(_card.getZoneOwner(), _cardPile)
                    || (game.getGameState().getCardPile(_card.getZoneOwner(), _cardPile).size() > 1);
        }

        String cardInfo = _hidden ? "a card" : GameUtils.getCardLink(_card);
        GameState gameState = game.getGameState();
        gameState.sendMessage(_action.getPerformingPlayer() + " puts " + cardInfo + " from Force Pile to the bottom of " + _cardPile.getHumanReadable());
        gameState.removeCardsFromZone(Collections.singleton(_card));
        gameState.addCardToZone(_card, _cardPile, _card.getOwner());

        game.getActionsEnvironment().emitEffectResult(
                new PutCardInCardPileFromOffTableResult(_action, _card, _card.getZoneOwner(), _cardPile, false));

        return new FullEffectResult(true);
    }
}
