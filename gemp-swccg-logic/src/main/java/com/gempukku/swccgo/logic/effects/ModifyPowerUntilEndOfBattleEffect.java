package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.ModifiersEnvironment;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.ResetOrModifyCardAttributeResult;

/**
 * An effect to modify the power of card until end of battle.
 */
public class ModifyPowerUntilEndOfBattleEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _cardToModify;
    private float _modifierAmount;

    /**
     * Creates an effect that modifies the power of a card until end of battle.
     * @param action the action performing this effect
     * @param cardToModify the card whose attribute is modified
     * @param modifierAmount the amount of the modifier
     */
    public ModifyPowerUntilEndOfBattleEffect(Action action, PhysicalCard cardToModify, float modifierAmount) {
        super(action);
        _cardToModify = cardToModify;
        _modifierAmount = modifierAmount;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        String performingPlayerId = _action.getPerformingPlayer();
        PhysicalCard actionSourceCard = _action.getActionSource();

        // Check if card's power may not be reduced
        if (_modifierAmount < 0 && modifiersQuerying.isProhibitedFromHavingPowerReduced(gameState, _cardToModify, performingPlayerId)) {
            gameState.sendMessage(GameUtils.getCardLink(_cardToModify) + "'s power is prevented from being reduced");
            return;
        }

        // Check if card's power may not be increased
        if (actionSourceCard != null &&_modifierAmount > 0 && modifiersQuerying.isProhibitedFromHavingPowerIncreasedByCard(gameState, _cardToModify, performingPlayerId, actionSourceCard)) {
            gameState.sendMessage(GameUtils.getCardLink(_cardToModify) + "is prevented from having its power increased by " + GameUtils.getCardLink(actionSourceCard));
            return;
        }

        ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        PhysicalCard source = _action.getActionSource();

        String actionMsg;
        if (_modifierAmount < 0)
            actionMsg = "reduces " + GameUtils.getCardLink(_cardToModify) + "'s power by " + GuiUtils.formatAsString(-_modifierAmount) + " until end of battle";
        else
            actionMsg = "adds " + GuiUtils.formatAsString(_modifierAmount) + " to " + GameUtils.getCardLink(_cardToModify) + "'s power until end of battle";

        if (performingPlayerId == null)
            gameState.sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " " + actionMsg);
        else
            gameState.sendMessage(performingPlayerId + " " + actionMsg + " using " + GameUtils.getCardLink(_action.getActionSource()));

        // Filter for same card while it is in play
        Filter cardFilter = Filters.and(Filters.sameCardId(_cardToModify), Filters.in_play);

        modifiersEnvironment.addUntilEndOfBattleModifier(
                new PowerModifier(source, cardFilter, _modifierAmount));

        actionsEnvironment.emitEffectResult(new ResetOrModifyCardAttributeResult(performingPlayerId, _cardToModify));
    }
}
