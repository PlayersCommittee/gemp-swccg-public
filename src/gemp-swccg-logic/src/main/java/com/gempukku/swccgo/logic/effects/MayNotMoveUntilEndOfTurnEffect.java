package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveModifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersEnvironment;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to prevent a card from moving until the end of the turn.
 */
public class MayNotMoveUntilEndOfTurnEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _targetCard;

    /**
     * Creates an effect that prevents a card from moving until the end of the turn.
     * @param action the action performing this effect
     * @param targetCard the card affected
     */
    public MayNotMoveUntilEndOfTurnEffect(Action action, PhysicalCard targetCard) {
        super(action);
        _targetCard = targetCard;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        PhysicalCard source = _action.getActionSource();

        gameState.sendMessage(GameUtils.getCardLink(_targetCard) + " may not move until end of the turn");
        gameState.cardAffectsCard(_action.getPerformingPlayer(), source, _targetCard);

        // Filter for same card while it is in play
        Filter cardFilter = Filters.and(Filters.sameCardId(_targetCard), Filters.in_play);

        modifiersEnvironment.addUntilEndOfTurnModifier(
                new MayNotMoveModifier(source, cardFilter));
    }
}
