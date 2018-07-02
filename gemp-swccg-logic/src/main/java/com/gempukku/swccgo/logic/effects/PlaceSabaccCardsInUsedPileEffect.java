package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.DrawCardResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * An effect that draws cards into sabacc hand.
 */
public class PlaceSabaccCardsInUsedPileEffect extends AbstractSubActionEffect {
    private String _playerId;
    private final int _count;
    private List<PhysicalCard> _cardsDrawn = new ArrayList<PhysicalCard>();

    /**
     * Creates an effect that causes the specified player to draw the specified number of cards into sabacc hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param count the number of cards to draw
     */
    public PlaceSabaccCardsInUsedPileEffect(Action action, String playerId, int count) {
        super(action);
        _playerId = playerId;
        _count = count;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action);

        final List<DrawOneSabaccCardEffect> drawEffects = new LinkedList<DrawOneSabaccCardEffect>();
        for (int i = 0; i < _count; i++) {
            final DrawOneSabaccCardEffect effect = new DrawOneSabaccCardEffect(subAction, _playerId);
            subAction.appendEffect(effect);
            drawEffects.add(effect);
        }
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        for (DrawOneSabaccCardEffect drawEffect : drawEffects) {
                            if (drawEffect.wasCarriedOut()) {
                                _cardsDrawn.add(drawEffect.getCardDrawn());
                            }
                        }
                    }
                }
        );
        return subAction;
    }


    /**
     * A private effect that draws one card into sabacc hand.
     */
    private class DrawOneSabaccCardEffect extends AbstractStandardEffect {
        private String _playerId;
        private PhysicalCard _cardDrawn;

        /**
         * Creates an effect that draws one card into sabacc hand.
         * @param action the action performing this effect
         * @param playerId the player to draw the card
         */
        public DrawOneSabaccCardEffect(Action action, String playerId) {
            super(action);
            _playerId = playerId;
        }

        @Override
        public boolean isPlayableInFull(SwccgGame game) {
            return !game.getGameState().isZoneEmpty(_playerId, Zone.RESERVE_DECK);
        }

        @Override
        protected FullEffectResult playEffectReturningResult(SwccgGame game) {
            if (!game.getGameState().isZoneEmpty(_playerId, Zone.RESERVE_DECK)) {

                GameState gameState = game.getGameState();
                PhysicalCard card = gameState.getTopOfCardPile(_playerId, Zone.RESERVE_DECK);
                _cardDrawn = card;
                gameState.removeCardsFromZone(Collections.singleton(card));
                gameState.addCardToZone(card, Zone.SABACC_HAND, _playerId);
                game.getGameState().sendMessage(_playerId + " draws a card into sabacc hand from Reserve Deck");

                // Submit an effect result that a card was drawn from Reserve Deck
                game.getActionsEnvironment().emitEffectResult(new DrawCardResult(_playerId, Zone.RESERVE_DECK));

                return new FullEffectResult(true);
            }

            return new FullEffectResult(false);
        }

        /**
         * Gets the card drawn.
         * @return the card drawn
         */
        public PhysicalCard getCardDrawn() {
            return _cardDrawn;
        }
    }

    /**
     * Gets the cards drawn.
     * @return the cards drawn
     */
    public List<PhysicalCard> getCardsDrawn() {
        return _cardsDrawn;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _cardsDrawn.size() >= _count;
    }
}
