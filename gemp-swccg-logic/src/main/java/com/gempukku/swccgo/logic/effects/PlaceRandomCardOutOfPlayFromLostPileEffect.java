package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;

/**
 * An effect that causes a random card to be placed out of play from the specified player's Lost Pile.
 */
public class PlaceRandomCardOutOfPlayFromLostPileEffect extends AbstractSubActionEffect {
    private String _cardPileOwner;
    private Zone _cardPile;

    /**
     * Creates an effect that causes a random card to be placed out of play from the specified player's Lost Pile.
     * @param action the action performing this effect
     * @param cardPileOwner the card pile owner
     */
    public PlaceRandomCardOutOfPlayFromLostPileEffect(Action action, String cardPileOwner) {
        super(action);
        _cardPileOwner = cardPileOwner;
        _cardPile = Zone.LOST_PILE;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Perform result(s)
                        Collection<PhysicalCard> randomCards = GameUtils.getRandomCards(game.getGameState().getCardPile(_cardPileOwner, _cardPile), 1);
                        if (!randomCards.isEmpty()) {
                            subAction.appendEffect(
                                    new PlaceCardOutOfPlayFromOffTableEffect(subAction, randomCards.iterator().next()));
                            subAction.appendEffect(
                                    new ShufflePileEffect(subAction, subAction.getActionSource(), _action.getPerformingPlayer(), _cardPileOwner, _cardPile, true));
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
