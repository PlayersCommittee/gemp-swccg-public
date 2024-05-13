package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An effect to put stacked cards into their owners' hands.
 */
public class PutStackedCardsInHandEffect extends AbstractSubActionEffect {
    private PhysicalCard _stackedOn;
    private Collection<PhysicalCard> _stackedFaceUp = new ArrayList<PhysicalCard>();
    private Collection<PhysicalCard> _stackedFaceDown = new ArrayList<PhysicalCard>();

    /**
     * Creates an effect that causes the cards stacked on the specified card to be placed in their owners' hands.
     * @param action the action performing this effect
     * @param stackedOn the card that the cards are stacked on
     */
    public PutStackedCardsInHandEffect(Action action, PhysicalCard stackedOn) {
        super(action);
        _stackedOn = stackedOn;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        Collection<PhysicalCard> stackedCards = gameState.getStackedCards(_stackedOn);
                        _stackedFaceUp.addAll(Filters.filter(stackedCards, game, Zone.STACKED));
                        _stackedFaceDown.addAll(Filters.filter(stackedCards, game, Zone.STACKED_FACE_DOWN));
                        gameState.removeCardsFromZone(_stackedFaceUp);
                        gameState.removeCardsFromZone(_stackedFaceDown);
                        if (!_stackedFaceUp.isEmpty()) {
                            gameState.sendMessage(GameUtils.getAppendedNames(_stackedFaceUp) + " are returned to hand from stacked on " + GameUtils.getCardLink(_stackedOn));
                            for (PhysicalCard card : _stackedFaceUp) {
                                gameState.addCardToZone(card, Zone.HAND, card.getOwner());
                            }
                        }
                        if (!_stackedFaceDown.isEmpty()) {
                            gameState.sendMessage(_stackedFaceDown.size() + " card" + GameUtils.s(_stackedFaceDown) + " " + GameUtils.be(_stackedFaceDown) + " returned to hand from stacked on " + GameUtils.getCardLink(_stackedOn));
                            for (PhysicalCard card : _stackedFaceDown) {
                                gameState.addCardToZone(card, Zone.HAND, card.getOwner());
                            }
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
}
