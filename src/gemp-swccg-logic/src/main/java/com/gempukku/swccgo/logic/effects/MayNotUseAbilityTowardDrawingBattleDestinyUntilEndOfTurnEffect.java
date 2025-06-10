package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.MayNotApplyAbilityForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersEnvironment;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.Collections;

/**
 * An effect to prevent cards from using ability toward drawing battle destiny until the end of the turn.
 */
public class MayNotUseAbilityTowardDrawingBattleDestinyUntilEndOfTurnEffect extends AbstractSuccessfulEffect {
    private Collection<PhysicalCard> _targetCards;

    /**
     * Creates an effect that prevents a card from using ability toward drawing battle destiny until the end of the turn.
     * @param action the action performing this effect
     * @param targetCard the card affected
     */
    public MayNotUseAbilityTowardDrawingBattleDestinyUntilEndOfTurnEffect(Action action, PhysicalCard targetCard) {
        this(action, Collections.singleton(targetCard));
    }

    /**
     * Creates an effect that prevents cards from using ability toward drawing battle destiny until the end of the turn.
     * @param action the action performing this effect
     * @param targetCards the cards affected
     */
    public MayNotUseAbilityTowardDrawingBattleDestinyUntilEndOfTurnEffect(Action action, Collection<PhysicalCard> targetCards) {
        super(action);
        _targetCards = targetCards;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        PhysicalCard source = _action.getActionSource();

        gameState.sendMessage(GameUtils.getAppendedNames(_targetCards) + " may not use ability toward drawing battle destiny until end of the turn");
        gameState.cardAffectsCards(_action.getPerformingPlayer(), source, _targetCards);

        for (PhysicalCard targetCard : _targetCards) {
            // Filter for same card while it is in play
            Filter cardFilter = Filters.and(Filters.sameCardId(targetCard), Filters.in_play);
            modifiersEnvironment.addUntilEndOfTurnModifier(
                    new MayNotApplyAbilityForBattleDestinyModifier(source, cardFilter));
        }
    }
}
