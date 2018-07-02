package com.gempukku.swccgo.cards.effects.complete;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.TargetingEffect;

/**
 * An effect for choosing a card pile.
 */
abstract class AbstractChoosePileEffect extends AbstractSuccessfulEffect implements TargetingEffect {

    protected AbstractChoosePileEffect(Action action) {
        super(action);
    }

    protected abstract void pileChosen(SwccgGame game, String cardPileOwner, Zone cardPile);
}
