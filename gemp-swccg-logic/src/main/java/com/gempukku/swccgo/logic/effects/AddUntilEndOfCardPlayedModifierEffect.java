package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that adds a modifier until the playing of the specified card is completed.
 */
public class AddUntilEndOfCardPlayedModifierEffect extends AddModifierWithDurationEffect {
    private PhysicalCard _card;

    /**
     * Creates an effect that adds a modifier until the playing of the specified card is completed.
     * @param action the action performing this effect
     * @param card the card
     * @param modifier the modifier
     */
    public AddUntilEndOfCardPlayedModifierEffect(Action action, PhysicalCard card, Modifier modifier, String actionMsg) {
        super(action, modifier, actionMsg);
        _card = card;
    }

    @Override
    protected String getMsgText(SwccgGame game) {
        if (getActionMsg() == null)
            return null;

        if (_action.getPerformingPlayer() == null) {
            return GameUtils.getCardLink(_action.getActionSource()) + " " + getActionMsg() + " during " + (_card.getBlueprint().isCardTypeDeployed() ? "deployment of " : "playing of ") + GameUtils.getCardLink(_card);
        }
        else {
            return _action.getPerformingPlayer() + " " + getActionMsg() + " during " + (_card.getBlueprint().isCardTypeDeployed() ? "deployment of " : "playing of ") + GameUtils.getCardLink(_card) + " using " + GameUtils.getCardLink(_action.getActionSource());
        }
    }

    @Override
    public final void doPlayEffect(SwccgGame game) {
        sendMsg(game);
        game.getModifiersEnvironment().addUntilEndOfCardPlayedModifier(_modifier, _card);
    }
}
