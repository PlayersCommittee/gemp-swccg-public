package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotParticipateInBattleModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersEnvironment;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.ResetOrModifyCardAttributeResult;

import java.util.Collection;
import java.util.Collections;

/**
 * An effect to prevent a card from moving or battling until the end of the turn.
 */
public class MayNotMoveOrBattleUntilEndOfTurnEffect extends AbstractSuccessfulEffect {
    private Collection<PhysicalCard> _targetCards;

    /**
     * Creates an effect that prevents a card from moving or battling until the end of the turn.
     * @param action the action performing this effect
     * @param targetCard the card affected
     */
    public MayNotMoveOrBattleUntilEndOfTurnEffect(Action action, PhysicalCard targetCard) {
        this(action, Collections.singleton(targetCard));
    }

    /**
     * Creates an effect that prevents cards from moving or battling until the end of the turn.
     * @param action the action performing this effect
     * @param targetCards the cards affected
     */
    public MayNotMoveOrBattleUntilEndOfTurnEffect(Action action, Collection<PhysicalCard> targetCards) {
        super(action);
        _targetCards = targetCards;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        PhysicalCard source = _action.getActionSource();

        gameState.sendMessage(GameUtils.getAppendedNames(_targetCards) + " may not move or battle until end of the turn");
        gameState.cardAffectsCards(_action.getPerformingPlayer(), source, _targetCards);

        for (PhysicalCard targetCard : _targetCards) {
            // Filter for same card while it is in play
            Filter cardFilter = Filters.and(Filters.sameCardId(targetCard), Filters.in_play);

            modifiersEnvironment.addUntilEndOfTurnModifier(
                    new MayNotMoveModifier(source, cardFilter));
            modifiersEnvironment.addUntilEndOfTurnModifier(
                    new MayNotParticipateInBattleModifier(source, cardFilter));

            actionsEnvironment.emitEffectResult(new ResetOrModifyCardAttributeResult(_action.getPerformingPlayer(), targetCard));
        }
    }
}
