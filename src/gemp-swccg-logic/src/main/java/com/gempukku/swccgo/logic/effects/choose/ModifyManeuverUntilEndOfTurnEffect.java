package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersEnvironment;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.ResetOrModifyCardAttributeResult;

/**
 * An effect to modify the maneuver of a card until end of the turn.
 */
public class ModifyManeuverUntilEndOfTurnEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _cardToModify;
    private float _modifierAmount;

    /**
     * Creates an effect that modifies the maneuver of a card until end of the turn.
     * @param action the action performing this effect
     * @param cardToModify the card whose maneuver is modified
     * @param modifierAmount the amount of the modifier
     */
    public ModifyManeuverUntilEndOfTurnEffect(Action action, PhysicalCard cardToModify, float modifierAmount) {
        super(action);
        _cardToModify = cardToModify;
        _modifierAmount = modifierAmount;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        PhysicalCard source = _action.getActionSource();

        String actionMsg;
        if (_modifierAmount < 0)
            actionMsg = "reduces " + GameUtils.getCardLink(_cardToModify) + "'s maneuver by " + GuiUtils.formatAsString(-_modifierAmount) + " until end of the turn";
        else
            actionMsg = "adds " + GuiUtils.formatAsString(_modifierAmount) + " to " + GameUtils.getCardLink(_cardToModify) + "'s maneuver until end of the turn";

        if (_action.getPerformingPlayer() == null)
            gameState.sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " " + actionMsg);
        else
            gameState.sendMessage(_action.getPerformingPlayer() + " " + actionMsg + " using " + GameUtils.getCardLink(_action.getActionSource()));

        // Filter for same card while it is in play
        Filter cardFilter = Filters.and(Filters.sameCardId(_cardToModify), Filters.in_play);

        modifiersEnvironment.addUntilEndOfTurnModifier(
                new ManeuverModifier(source, cardFilter, _modifierAmount));

        actionsEnvironment.emitEffectResult(new ResetOrModifyCardAttributeResult(_action.getPerformingPlayer(), _cardToModify));
    }
}
