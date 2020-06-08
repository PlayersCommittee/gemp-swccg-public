package com.gempukku.swccgo.cards.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromHandEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

public class ChooseAndLoseCardsFromHandEffect extends ChooseCardsFromHandEffect {
    private SubAction _resultSubAction;

    public ChooseAndLoseCardsFromHandEffect(Action action, String playerId, int minimum, int maximum, Filterable filters) {
        super(action, playerId, minimum, maximum, filters);
    }

    @Override
    public String getText(SwccgGame game) {
        return "Choose cards to lose";
    }

    @Override
    protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
        if (selectedCards.isEmpty()) {
            return;
        }

        _resultSubAction = new SubAction(_action);
        _resultSubAction.appendEffect(new LoseCardsFromHandEffect(_action, _action.getPerformingPlayer(), Filters.in(selectedCards)));
        game.getActionsEnvironment().addActionToStack(_resultSubAction);
    }

    @Override
    public boolean wasCarriedOut() {
        return super.wasCarriedOut() && _resultSubAction != null && _resultSubAction.wasCarriedOut();
    }
}
