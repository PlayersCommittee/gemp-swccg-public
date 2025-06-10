package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersEnvironment;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ResetPoliticsModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.ResetOrModifyCardAttributeResult;

/**
 * An effect to cancel the game text and reset politics of a card until the end of the turn.
 */
public class CancelGameTextAndResetPoliticsUntilEndOfTurnEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _targetCard;
    private float _politicsResetValue;

    /**
     * Creates an effect that cancels the game text and reset politics of a card until end of the turn.
     * @param action the action performing this effect
     * @param targetCard the card whose game text is canceled
     * @param politicsResetValue the reset value
     */
    public CancelGameTextAndResetPoliticsUntilEndOfTurnEffect(Action action, PhysicalCard targetCard, float politicsResetValue) {
        super(action);
        _targetCard = targetCard;
        _politicsResetValue = politicsResetValue;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        PhysicalCard source = _action.getActionSource();

        // Check if card's game text may not be canceled
        boolean cancelGameText = true;
        if (modifiersQuerying.isProhibitedFromHavingGameTextCanceled(gameState, _targetCard)) {
            gameState.sendMessage(GameUtils.getCardLink(_targetCard) + "'s game text is not allowed to be canceled");
            cancelGameText = false;
        }

        StringBuilder sb = new StringBuilder(GameUtils.getCardLink(_targetCard)).append("'s");
        if (cancelGameText) {
            sb.append(" game text is canceled and its");
        }
        sb.append(" politics is reset to ").append(GuiUtils.formatAsString(_politicsResetValue));
        sb.append(" until end of the turn");
        gameState.sendMessage(sb.toString());

        gameState.cardAffectsCard(_action.getPerformingPlayer(), source, _targetCard);

        // Filter for same card while it is in play
        Filter cardFilter = Filters.and(Filters.sameCardId(_targetCard), Filters.in_play);

        if (cancelGameText) {
            _targetCard.setGameTextCanceled(true);
            modifiersEnvironment.addUntilEndOfTurnModifier(
                    new CancelsGameTextModifier(source, cardFilter));
        }
        modifiersEnvironment.addUntilEndOfTurnModifier(
                new ResetPoliticsModifier(source, cardFilter, _politicsResetValue));

        actionsEnvironment.emitEffectResult(new ResetOrModifyCardAttributeResult(_action.getPerformingPlayer(), _targetCard));
    }
}
