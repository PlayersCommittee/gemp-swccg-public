package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * This effect records the specified card(s) as being played for the purposes of the game keeping track of which cards
 * have been played that turn, etc.
 */
public class RecordCardsBeingPlayedEffect extends AbstractSuccessfulEffect {
    private Collection<PhysicalCard> _cardsPlayed;

    /**
     * Creates an effect that records the specified card(s) as being played for the purposes of the game keeping track
     * of which cards have been played that turn, etc.
     * @param action the action performing this effect
     * @param cardsPlayed the cards being played
     */
    public RecordCardsBeingPlayedEffect(Action action, Collection<PhysicalCard> cardsPlayed) {
        super(action);
        _cardsPlayed = cardsPlayed;
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        for (PhysicalCard cardPlayed : _cardsPlayed) {
            modifiersQuerying.cardBeingPlayed(cardPlayed);

            // Increment card title played per turn
            for (String title : cardPlayed.getTitles()) {
                modifiersQuerying.getCardTitlePlayedTurnLimitCounter(title).incrementToLimit(Integer.MAX_VALUE, 1);
            }
        }
    }
}
