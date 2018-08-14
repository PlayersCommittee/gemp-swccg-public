package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.results.ProbedSystemResult;
import com.gempukku.swccgo.logic.timing.results.StackedFromHandResult;

import java.util.Collections;

public class StackOneCardFromHandEffect extends AbstractStandardEffect {
    private PhysicalCard _card;
    private PhysicalCard _stackOn;
    private boolean _faceDown;
    private boolean _isProbeCard;
    private boolean _isBluffCard;
    private boolean _isCombatCard;

    public StackOneCardFromHandEffect(Action action, PhysicalCard card, PhysicalCard stackOn, boolean faceDown) {
        this(action, card, stackOn, faceDown, false, false, false);
    }

    public StackOneCardFromHandEffect(Action action, PhysicalCard card, PhysicalCard stackOn, boolean faceDown, boolean isProbeCard, boolean isBluffCard, boolean isCombatCard) {
        super(action);
        _card = card;
        _stackOn = stackOn;
        _faceDown = faceDown;
        _isProbeCard = isProbeCard;
        _isBluffCard = isBluffCard;
        _isCombatCard = isCombatCard;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        if (isPlayableInFull(game)) {
            if (_isBluffCard) {
                game.getGameState().sendMessage(_action.getPerformingPlayer() + " places a 'bluff card' from hand face down under " + GameUtils.getCardLink(_stackOn));
                game.getGameState().activatedCard(_action.getPerformingPlayer(), _stackOn);
                game.getModifiersQuerying().bluffCardStacked();
            }
            else if (_isCombatCard) {
                game.getGameState().sendMessage(_action.getPerformingPlayer() + " places a combat card from hand face down under " + GameUtils.getCardLink(_stackOn));
            }
            else if (_isProbeCard) {
                game.getGameState().sendMessage(_action.getPerformingPlayer() + " 'probes' " + GameUtils.getCardLink(_stackOn) + " by placing a 'probe' card from hand face down under " + GameUtils.getCardLink(_stackOn));
            }
            else if (_faceDown)
                game.getGameState().sendMessage(_action.getPerformingPlayer() + " stacks a card from hand face down on " + GameUtils.getCardLink(_stackOn));
            else
                game.getGameState().sendMessage(_action.getPerformingPlayer() + " stacks " + GameUtils.getCardLink(_card) + " from hand on " + GameUtils.getCardLink(_stackOn));

            game.getGameState().removeCardsFromZone(Collections.singleton(_card));
            _card.setProbeCard(_isProbeCard);
            _card.setBluffCard(_isBluffCard);
            _card.setCombatCard(_isCombatCard);
            game.getGameState().stackCard(_card, _stackOn, _faceDown, false, false);

            if (_isProbeCard) {
                // Emit effect result if system was just probed
                game.getActionsEnvironment().emitEffectResult(new ProbedSystemResult(_action.getPerformingPlayer(), _stackOn));
            }

            game.getActionsEnvironment().emitEffectResult(
                    new StackedFromHandResult(_action, _card, _stackOn));

            return new FullEffectResult(true);
        }
        return new FullEffectResult(false);
    }

    @Override
    public String getText(SwccgGame game) {
        return null;
    }

    @Override
    public Effect.Type getType() {
        return null;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return _card.getZone() == Zone.HAND;
    }
}
