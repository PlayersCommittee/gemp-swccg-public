package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;


public class LoseCardsFromUsedPileEffect extends AbstractStandardEffect {
    private String _performingPlayer;
    private String _zoneOwner;
    private Filterable _filters;

    public LoseCardsFromUsedPileEffect(Action action, String zoneOwner, Filterable filters) {
        super(action);
        _performingPlayer = action.getPerformingPlayer();
        _zoneOwner = zoneOwner;
        _filters = filters;
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public String getText(SwccgGame game) {
        return null;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        GameState gameState = game.getGameState();
        Collection<PhysicalCard> cardsToLose = Filters.filter(gameState.getUsedPile(_zoneOwner), game, Filters.and(_filters));
        if (cardsToLose.isEmpty())
            return new FullEffectResult(false);

        gameState.sendMessage(_performingPlayer + " causes " + GameUtils.getAppendedNames(cardsToLose) + " be lost from " + _zoneOwner + "'s Used Pile");

        // Choose order that cards are placed in Lost Pile
        SubAction subAction = new SubAction(_action);
        subAction.appendEffect(new PutCardsInCardPileEffect(subAction, game, cardsToLose, Zone.LOST_PILE));
        game.getActionsEnvironment().addActionToStack(subAction);

        return new FullEffectResult(true);
    }
}
