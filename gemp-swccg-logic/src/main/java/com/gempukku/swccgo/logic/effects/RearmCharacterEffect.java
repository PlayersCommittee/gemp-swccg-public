package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;

/**
 * An effect to make a character no longer Disarmed.
 */
public class RearmCharacterEffect extends AbstractSubActionEffect {
    private PhysicalCard _cardRearmed;

    /**
     * Creates an effect to make a character no longer Disarmed.
     * @param action the action performing this effect
     * @param cardRearmed the card that is re-armed
     */
    public RearmCharacterEffect(Action action, PhysicalCard cardRearmed) {
        super(action);
        _cardRearmed = cardRearmed;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        gameState.sendMessage(GameUtils.getCardLink(_cardRearmed) + " is no longer Disarmed");
                        _cardRearmed.setDisarmed(false);

                        Collection<PhysicalCard> disarmingCards = Filters.filter(_cardRearmed.getCardsAttached(), game, Filters.disarming_card);
                        subAction.appendEffect(
                                new CancelCardsOnTableSimultaneouslyEffect(subAction, disarmingCards));
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
