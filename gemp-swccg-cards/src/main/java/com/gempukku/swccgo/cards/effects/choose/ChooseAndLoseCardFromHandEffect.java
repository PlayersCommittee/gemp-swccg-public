package com.gempukku.swccgo.cards.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;

public class ChooseAndLoseCardFromHandEffect extends ChooseAndLoseCardsFromHandEffect {

    public ChooseAndLoseCardFromHandEffect(Action action, String playerId, Filterable filters) {
        super(action, playerId, 1, 1, filters);
    }

    @Override
    public String getText(SwccgGame game) {
        return "Choose card to lose";
    }
}
