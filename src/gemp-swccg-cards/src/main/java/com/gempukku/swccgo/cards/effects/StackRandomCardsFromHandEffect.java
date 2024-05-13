package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

public class StackRandomCardsFromHandEffect extends AbstractStandardEffect {
    private String _playerId;
    private String _zoneOwner;
    private PhysicalCard _stackOn;
    private boolean _faceDown;
    private int _max;

    public StackRandomCardsFromHandEffect(Action action, String playerId, String zoneOwner, PhysicalCard stackOn, boolean faceDown, int maxToStack) {
        super(action);
        _playerId = playerId;
        _zoneOwner = zoneOwner;
        _stackOn = stackOn;
        _faceDown = faceDown;
        _max = maxToStack;
    }

    @Override
    public String getText(SwccgGame game) {
        return null;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return !game.getGameState().getHand(_zoneOwner).isEmpty();
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        if (isPlayableInFull(game)) {
            GameState gameState = game.getGameState();
            final List<PhysicalCard> randomCards = GameUtils.getRandomCards(gameState.getHand(_zoneOwner), Math.min(_max, game.getGameState().getHand(_zoneOwner).size()));
            for (PhysicalCard randomCard : randomCards) {
                gameState.removeCardsFromZone(Collections.singleton(randomCard));
                game.getGameState().stackCard(randomCard, _stackOn, _faceDown, false, false);
            }
            String playerNameForMsg = _playerId.equals(_zoneOwner) ? "" : _zoneOwner + "'s ";
            if (_faceDown)
                gameState.sendMessage(_playerId + " stacks " + randomCards.size() +  " card" + GameUtils.s(randomCards) + " at random from " + playerNameForMsg + " hand face down on " + GameUtils.getCardLink(_stackOn));
            else
                gameState.sendMessage(_playerId + " stacks " + GameUtils.getAppendedNames(randomCards) + " at random from " + playerNameForMsg + "hand on " + GameUtils.getCardLink(_stackOn));

            return new FullEffectResult(true);
        }
        return new FullEffectResult(false);
    }
}
