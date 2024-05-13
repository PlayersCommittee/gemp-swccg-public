package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;


public class LoseCardsFromForcePileEffect extends AbstractStandardEffect {
    private String _performingPlayer;
    private String _zoneOwner;
    private Filterable _filters;

    public LoseCardsFromForcePileEffect(Action action, String performingPlayer, String zoneOwner, Filterable filters) {
        super(action);
        _performingPlayer = performingPlayer;
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
        Collection<PhysicalCard> cardsToLose = Filters.filter(gameState.getForcePile(_zoneOwner), game, _filters);
        if (cardsToLose.isEmpty())
            return new FullEffectResult(false);

        game.getGameState().sendMessage(_performingPlayer + " causes " + GameUtils.getAppendedNames(cardsToLose) + " be lost from " + _zoneOwner + "'s Force Pile");

        // Choose order that cards are placed in Lost Pile
        SubAction subAction = new SubAction(_action);
        subAction.appendEffect(new LoseCardsFromOffTableSimultaneouslyEffect(subAction, cardsToLose, false));
        game.getActionsEnvironment().addActionToStack(subAction);

        return new FullEffectResult(true);
    }
}
