package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.LookedAtCardsInOwnCardPileResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RevealCardPileEffect extends AbstractStandardEffect {
    private String _playerId;
    private String _zoneOwner;
    private Zone _zone;
    private String _text;

    public RevealCardPileEffect(Action action, String playerId, String zoneOwner, Zone zone) {
        super(action);
        _playerId = playerId;
        _zoneOwner = zoneOwner;
        _zone = zone;
        _text = zoneOwner + "'s " + zone.getHumanReadable();
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return !game.getGameState().isZoneEmpty(_zoneOwner, _zone);
    }

    @Override
    public FullEffectResult playEffectReturningResult(final SwccgGame game) {
        String opponent = game.getOpponent(_playerId);
        List<PhysicalCard> cardsInZone;
        if (_zone==Zone.HAND)
            cardsInZone = new LinkedList<PhysicalCard>(game.getGameState().getHand(_zoneOwner));
        else
            cardsInZone = new LinkedList<PhysicalCard>(game.getGameState().getCardPile(_zoneOwner, _zone));

        if (_zone!=Zone.HAND || !_zoneOwner.equals(_playerId)) {
            game.getGameState().sendMessage(_playerId + " reveals " + _zoneOwner + "'s " + _zone.getHumanReadable());
            game.getUserFeedback().sendAwaitingDecision(_playerId,
                    new ArbitraryCardsSelectionDecision(_text, cardsInZone, Collections.<PhysicalCard>emptyList(), 0, 0) {
                        @Override
                        public void decisionMade(String result) throws DecisionResultInvalidException {
                            // Check if player looked at cards in own card pile
                            if (_zoneOwner.equals(_playerId) && _zone != Zone.HAND) {
                                _action.appendAfterEffect(new TriggeringResultEffect(_action, new LookedAtCardsInOwnCardPileResult(_zoneOwner, _zone)));
                            }
                        }
                    });
        }

        if (_zone!=Zone.HAND || !_zoneOwner.equals(opponent)) {
            game.getUserFeedback().sendAwaitingDecision(opponent,
                    new ArbitraryCardsSelectionDecision(_text, cardsInZone, Collections.<PhysicalCard>emptyList(), 0, 0) {
                        @Override
                        public void decisionMade(String result) throws DecisionResultInvalidException {
                        }
                    });
        }
        cardsRevealed(cardsInZone);

        return new FullEffectResult(true);
    }

    protected void cardsRevealed(List<PhysicalCard> revealedCards) {
    }
}
