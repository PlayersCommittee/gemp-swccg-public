package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.MayNotAllowPlayerToDownloadCardsModifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersEnvironment;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * An effect to prevent a card from allowing a specified player to download cards until end of turn.
 */
public class MayNotAllowPlayerToDownloadCardsUntilEndOfTurnEffect extends AbstractSuccessfulEffect {
    private Collection<PhysicalCard> _targetCards;
    private String _playerId;

    /**
     * Creates an effect that prevents a card from allowing a specified player to download cards until end of turn.
     * @param action the action performing this effect
     * @param targetCards the cards affected
     * @param playerId the player affected
     */
    public MayNotAllowPlayerToDownloadCardsUntilEndOfTurnEffect(Action action, Collection<PhysicalCard> targetCards, String playerId) {
        super(action);
        _targetCards = targetCards;
        _playerId = playerId;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        PhysicalCard source = _action.getActionSource();

        gameState.sendMessage(GameUtils.getAppendedNames(_targetCards) + " may not allow " + _playerId + " to [download] cards until end of the turn");
        gameState.cardAffectsCards(_action.getPerformingPlayer(), source, _targetCards);

        for (PhysicalCard targetCard : _targetCards) {
            // Filter for same card while it is in play
            Filter cardFilter = Filters.and(Filters.sameCardId(targetCard), Filters.in_play);

            modifiersEnvironment.addUntilEndOfTurnModifier(
                    new MayNotAllowPlayerToDownloadCardsModifier(source, cardFilter, _playerId));
        }
    }
}
