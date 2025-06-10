package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.MayNotParticipateInBattleModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersEnvironment;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to make a card 'cloak' (not participate in battles) until end of the next turn.
 */
public class CloakUntilEndOfNextTurnEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _cardToCloak;

    /**
     * Creates an effect makes a card 'cloak' (not participate in battles) until end of the next turn.
     * @param action the action performing this effect
     * @param cardToCloak the card to 'cloak'
     */
    public CloakUntilEndOfNextTurnEffect(Action action, PhysicalCard cardToCloak) {
        super(action);
        _cardToCloak = cardToCloak;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        PhysicalCard source = _action.getActionSource();

        gameState.sendMessage(GameUtils.getCardLink(_cardToCloak) + " 'cloaks' until end of next turn");

        // Filter for same card while it is in play
        Filter cardFilter = Filters.and(Filters.sameCardId(_cardToCloak), Filters.in_play);

        modifiersEnvironment.addUntilEndOfNextTurnModifier(
                new MayNotParticipateInBattleModifier(source, cardFilter));
    }
}
