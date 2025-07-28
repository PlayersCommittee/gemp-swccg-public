package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;

/**
 * An effect to capture the specified characters.
 */
public class CaptureCharactersOnTableEffect extends AbstractSubActionEffect {
    private Collection<PhysicalCard> _remainingCards;
    private boolean _freezeCharacters;
    private PhysicalCard _cardFiringWeapon;
    private boolean _seizeEvenIfNotPossible;

    /**
     * Creates an effect to capture the specified characters.
     * @param action the action performing this effect
     * @param characters the characters to capture
     */
    public CaptureCharactersOnTableEffect(Action action, Collection<PhysicalCard> characters) {
        this(action, characters, false, null);
    }

    /**
     * Creates an effect to capture the specified characters.
     * @param action the action performing this effect
     * @param characters the characters to capture
     * @param freezeCharacters true if the characters are 'frozen' when captured, otherwise false
     * @param cardFiringWeapon the card that fired weapon that caused capture, or null
     */
    protected CaptureCharactersOnTableEffect(Action action, Collection<PhysicalCard> characters, boolean freezeCharacters, PhysicalCard cardFiringWeapon) {
        this(action, characters, freezeCharacters, cardFiringWeapon, false);
    }

    /**
     * Creates an effect to capture the specified characters.
     * @param action the action performing this effect
     * @param characters the characters to capture
     * @param freezeCharacters true if the characters are 'frozen' when captured, otherwise false
     * @param cardFiringWeapon the card that fired weapon that caused capture, or null
     * @param seizeEvenIfNotPossible true if seizing the captive will be facilitated by immediately disembarking a starship/vehicle or releasing another captive
     */
    protected CaptureCharactersOnTableEffect(Action action, Collection<PhysicalCard> characters, boolean freezeCharacters, PhysicalCard cardFiringWeapon, boolean seizeEvenIfNotPossible) {
        super(action);
        _remainingCards = characters;
        _freezeCharacters = freezeCharacters;
        _cardFiringWeapon = cardFiringWeapon;
        _seizeEvenIfNotPossible = seizeEvenIfNotPossible;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final SubAction subAction = new SubAction(_action, game.getDarkPlayer());
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        _remainingCards = Filters.filter(_remainingCards, game, Filters.and(Filters.character, Filters.onTable));

                        if (!_remainingCards.isEmpty()) {

                            // Special Rule for Luke's Backpack (if character carrying it is captured, then character in it is also captured, and vice versa)
                            if (Filters.canSpot(_remainingCards, game, Filters.and(Filters.character, Filters.hasAttached(Filters.Lukes_Backpack)))) {
                                _remainingCards.addAll(Filters.filterAllOnTable(game, Filters.and(Filters.not(Filters.in(_remainingCards)), Filters.character, Filters.attachedTo(Filters.Lukes_Backpack))));
                            }
                            Collection<PhysicalCard> inLukesBackpack = Filters.filter(_remainingCards, game, Filters.and(Filters.character, Filters.attachedTo(Filters.Lukes_Backpack)));
                            if (!inLukesBackpack.isEmpty()) {
                                _remainingCards.addAll(Filters.filterAllOnTable(game, Filters.and(Filters.not(Filters.in(_remainingCards)), Filters.character, Filters.hasAttached(Filters.Lukes_Backpack))));
                                for (PhysicalCard inBackpack : inLukesBackpack) {
                                    subAction.appendEffect(
                                            new DisembarkEffect(subAction, inBackpack, game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), inBackpack), false, false));
                                }
                            }
                            subAction.appendEffect(
                                    new ChooseNextCharacterToCapture(subAction, _remainingCards));
                        }
                    }
                }
        );
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }

    /**
     * A private effect for choosing the next character to be captured.
     */
    private class ChooseNextCharacterToCapture extends ChooseCardOnTableEffect {
        private SubAction _subAction;

        /**
         * Creates an effect for choosing the next character to be captured.
         * @param subAction the action
         * @param remainingCards the remaining cards to choose from to be captured
         */
        public ChooseNextCharacterToCapture(SubAction subAction, Collection<PhysicalCard> remainingCards) {
            super(subAction, subAction.getPerformingPlayer(), "Choose character to capture", remainingCards);
            _subAction = subAction;
        }

        @Override
        protected void cardSelected(final PhysicalCard selectedCard) {
            // Perform the CaptureOneCharacterOnTableEffect on the selected card
            _subAction.appendEffect(
                    new CaptureOneCharacterOnTableEffect(_subAction, selectedCard, _freezeCharacters, _cardFiringWeapon, _seizeEvenIfNotPossible));
            _subAction.appendEffect(
                    new PassthruEffect(_subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            _remainingCards.remove(selectedCard);
                            _remainingCards = Filters.filter(_remainingCards, game, Filters.and(Filters.character, Filters.onTable));
                            if (!_remainingCards.isEmpty()) {
                                _subAction.appendEffect(
                                        new ChooseNextCharacterToCapture(_subAction, _remainingCards));
                            }
                        }
                    }
            );
        }
    }
}
