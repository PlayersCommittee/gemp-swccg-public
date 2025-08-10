package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.ResetOrModifyCardAttributeResult;

import java.util.List;


/**
 * An effect to restore a card's forfeit to normal.
 */
public class RestoreForfeitToNormalEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _cardToRestore;

    /**
     * Creates an effect that restores a card's forfeit to normal.
     * @param action the action performing this effect
     * @param cardToRestore the card to restore
     */
    public RestoreForfeitToNormalEffect(Action action, PhysicalCard cardToRestore) {
        super(action);
        _cardToRestore = cardToRestore;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        gameState.sendMessage(GameUtils.getCardLink(_cardToRestore) + "'s forfeit is restored to normal by " + GameUtils.getCardLink(_action.getActionSource()));
        game.getGameState().cardAffectsCard(_action.getActionSource().getOwner(), _action.getActionSource(), _cardToRestore);

        // Get all the persistent modifiers that can affect the card's forfeit and exclude this card from those modifiers.
        List<Modifier> modifiers = modifiersQuerying.getPersistentModifiersAffectingCard(gameState, _cardToRestore);
        for (Modifier modifier : modifiers) {
            if (modifier.getModifierType() == ModifierType.FORFEIT_VALUE || modifier.getModifierType() == ModifierType.UNMODIFIABLE_FORFEIT_VALUE) {
                if (!modifier.isNotRemovedOnRestoreToNormal()) {
                    modifiersQuerying.excludeFromBeingAffected(modifier, _cardToRestore);
                }
            }
        }

        game.getActionsEnvironment().emitEffectResult(new ResetOrModifyCardAttributeResult(_action.getPerformingPlayer(), _cardToRestore));
    }
}
