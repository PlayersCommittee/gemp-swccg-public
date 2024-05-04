package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.ReactActionOption;
import com.gempukku.swccgo.logic.effects.MovingAsReactEffect;

import java.util.ArrayList;
import java.util.Collection;


// This class contains the state information for a
// move as 'react' action within a game of Gemp-Swccg.
//
public class MoveAsReactState {
    private PhysicalCard _cardReacting;
    private ReactActionOption _reactActionOption;
    private Collection<PhysicalCard> _cardsInReact = new ArrayList<PhysicalCard>();
    private MovingAsReactEffect _effect;
    private boolean _reachedPreMovements;
    private boolean _reachedRegularMovement;
    private boolean _reachedPostMovements;
    private boolean _canceled;

    public MoveAsReactState(PhysicalCard card, ReactActionOption reactActionOption) {
        _cardReacting = card;
        _reactActionOption = reactActionOption;
        addCardParticipatingInReact(card);
    }

    /**
     * Sets the moving as 'react' effect.
     * @param effect the effect
     */
    public void setMovingAsReactEffect(MovingAsReactEffect effect) {
        _effect = effect;
    }

    /**
     * Gets the moving as 'react' effect.
     * @return the effect
     */
    public MovingAsReactEffect getMovingAsReactEffect() {
        return _effect;
    }

    public PhysicalCard getReactingCard() {
        return _cardReacting;
    }

    public ReactActionOption getReactActionOption() {
        return _reactActionOption;
    }

    public Collection<PhysicalCard> getCardsParticipatingInReact() {
        return _cardsInReact;
    }

    public void addCardParticipatingInReact(PhysicalCard card) {
        _cardsInReact.add(card);
    }

    public boolean canContinue() {
        return !_canceled && _cardReacting.getZone().isInPlay();
    }

    public void reachedPreMovements() {
        _reachedPreMovements = true;
    }

    public boolean isDuringPreMovements() {
        return !_canceled && (_reachedPreMovements && !_reachedRegularMovement);
    }

    public void reachedRegularMovement() {
        _reachedRegularMovement = true;
    }

    public boolean isDuringRegularMovement() {
        return !_canceled && (_reachedRegularMovement && !_reachedPostMovements);
    }

    public void reachedPostMovements() {
        _reachedPostMovements = true;
    }

    public boolean isDuringPostMovements() {
        return !_canceled && (_reachedPostMovements);
    }

    /**
     * Cancels the move as a 'react' effect and specifies the card that is the source of the canceling action.
     * @param canceledByCard the card
     */
    public void cancel(PhysicalCard canceledByCard) {
        _canceled = true;
        if (_effect != null) {
            _effect.cancel(canceledByCard);
        }
    }
}
