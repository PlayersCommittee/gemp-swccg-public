package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * An effect to choose from the specified cards on the table one at a time.
 */
public abstract class ChooseCardsOnTableOneAtATimeEffect extends AbstractStandardEffect {
    private String _playerToChoose;
    private String _choiceText;
    private Collection<PhysicalCard> _cardsToChoose;
    private Set<PhysicalCard> _alreadyChosen = new HashSet<PhysicalCard>();

    /**
     * Creates an effect to choose from the specified cards on the table one at a time.
     * @param action the action (must be a SubAction) performing this effect
     * @param playerToChoose the player to choose
     * @param choiceText the choice text
     * @param cardsToChoose the cards to choose from
     */
    public ChooseCardsOnTableOneAtATimeEffect(SubAction action, String playerToChoose, String choiceText, Collection<PhysicalCard> cardsToChoose) {
        super(action);
        _playerToChoose = playerToChoose;
        _choiceText = choiceText;
        _cardsToChoose = Collections.unmodifiableCollection(cardsToChoose);
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(final SwccgGame game) {
        final ChooseCardsOnTableOneAtATimeEffect that = this;

        _action.appendEffect(
                new ChooseCardOnTableEffect(_action, _playerToChoose, _choiceText, _cardsToChoose, Filters.not(Filters.in(_alreadyChosen))) {
                    @Override
                    protected void cardSelected(PhysicalCard selectedCard) {
                        _alreadyChosen.add(selectedCard);
                        that.cardSelected(game, selectedCard);

                        if (!Filters.filterCount(_cardsToChoose, game, 1, Filters.not(Filters.in(_alreadyChosen))).isEmpty()) {
                            that.playEffect(game);
                        }
                    }
                });
        return new FullEffectResult(true);
    }

    /**
     * Called each time a card is selected.
     * @param game the game
     * @param card the card selected
     */
    protected abstract void cardSelected(SwccgGame game, PhysicalCard card);
}
