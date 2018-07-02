package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * This effect records the specified card(s) as being 'blown away' for the purposes of the game keeping track of which cards
 * have been 'blown away' during the game.
 */
class RecordCardsBlownAwayEffect extends AbstractSuccessfulEffect {
    private Collection<PhysicalCard> _cardsBlownAway;

    /**
     * Creates an effect that records the specified card(s) as being 'blown away' for the purposes of the game keeping track of which cards
     * have been 'blown away' during the game.
     * @param action the action performing this effect
     * @param cardsBlownAway the cards 'blown away'
     */
    public RecordCardsBlownAwayEffect(Action action, Collection<PhysicalCard> cardsBlownAway) {
        super(action);
        _cardsBlownAway = cardsBlownAway;
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        // Send message for cards 'blown away'
        game.getGameState().sendMessage(GameUtils.getAppendedNames(_cardsBlownAway) + " " + GameUtils.be(_cardsBlownAway) + " 'blown away'");

        // Remember cards that were 'blown away'
        for (PhysicalCard card : _cardsBlownAway) {
            game.getModifiersQuerying().blownAway(card);
        }
    }
}
