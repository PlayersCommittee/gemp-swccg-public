package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersEnvironment;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.CanceledGameTextResult;

/**
 * An effect to cancel the game text of a card.
 */
public class CancelGameTextEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _targetCard;

    /**
     * Creates an effect that cancels the game text of a card.
     * @param action the action performing this effect
     * @param targetCard the card whose game text is canceled
     */
    public CancelGameTextEffect(Action action, PhysicalCard targetCard) {
        super(action);
        _targetCard = targetCard;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        // Check if card's game text may not be canceled
        if (modifiersQuerying.isProhibitedFromHavingGameTextCanceled(gameState, _targetCard)) {
            gameState.sendMessage(GameUtils.getCardLink(_targetCard) + "'s game text is not allowed to be canceled");
            return;
        }

        ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        PhysicalCard source = _action.getActionSource();

        // If during battle and the source if the action is not a weapon, then cancel until end of the battle, otherwise
        // lasts for remainder of game (until card leaves play).
        if (gameState.isDuringBattle()
                && !Filters.weapon_or_character_with_permanent_weapon.accepts(gameState, modifiersQuerying, source)) {
            gameState.sendMessage(GameUtils.getCardLink(_targetCard) + "'s game text is canceled until end of the turn");
        } else {
            gameState.sendMessage(GameUtils.getCardLink(_targetCard) + "'s game text is canceled");
        }
        gameState.cardAffectsCard(_action.getPerformingPlayer(), source, _targetCard);

        // Filter for same card while it is in play
        Filter cardFilter = Filters.and(Filters.sameCardId(_targetCard), Filters.in_play);

        _targetCard.setGameTextCanceled(true);
        Modifier modifier = new CancelsGameTextModifier(source, cardFilter);
        modifier.skipSettingNotRemovedOnRestoreToNormal();

        // If during battle and the source if the action is not a weapon, then cancel until end of the battle, otherwise
        // lasts for remainder of game (until card leaves play).
        if (gameState.isDuringBattle()
                && !Filters.weapon_or_character_with_permanent_weapon.accepts(gameState, modifiersQuerying, source)) {
            modifiersEnvironment.addUntilEndOfBattleModifier(modifier);
        }
        else {
            modifiersEnvironment.addUntilEndOfGameModifier(modifier);
        }

        actionsEnvironment.emitEffectResult(new CanceledGameTextResult(_action.getPerformingPlayer(), _targetCard));
    }
}
