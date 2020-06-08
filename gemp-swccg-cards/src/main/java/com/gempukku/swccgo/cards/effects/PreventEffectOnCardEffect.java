package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that prevents a specified preventable card effect from affecting a specified card.
 */
public class PreventEffectOnCardEffect extends AbstractSuccessfulEffect {
    private PreventableCardEffect _preventableCardEffect;
    private PhysicalCard _card;
    private String _msgText;

    /**
     * Creates an effect that prevents a specified preventable card effect from affecting a specified card.
     * @param action the action performing this effect
     * @param preventableCardEffect the preventable card effect
     * @param card the card
     * @param msgText the message text
     */
    public PreventEffectOnCardEffect(Action action, PreventableCardEffect preventableCardEffect, PhysicalCard card, String msgText) {
        super(action);
        _preventableCardEffect = preventableCardEffect;
        _card = card;
        _msgText = msgText;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        sendMsg(game);
        _preventableCardEffect.preventEffectOnCard(_card);
    }

    private String getMsgText() {
        if (getActionMsg() == null)
            return null;

        if (_action.getPerformingPlayer() == null) {
            return GameUtils.getCardLink(_action.getActionSource()) + " " + getActionMsg();
        }
        else {
            return _action.getPerformingPlayer() + " " + getActionMsg() + " using " + GameUtils.getCardLink(_action.getActionSource());
        }
    }

    private String getActionMsg() {
        if (_msgText == null)
            return null;

        return _msgText.length() > 2 ? (_msgText.substring(0, 2).toLowerCase() + _msgText.substring(2)) : _msgText.toLowerCase();
    }

    private void sendMsg(SwccgGame game) {
        String msgText = getMsgText();
        if (msgText != null) {
            game.getGameState().sendMessage(msgText);
        }
    }
}
