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
import com.gempukku.swccgo.logic.timing.results.DisarmedResult;

import java.util.Collection;

/**
 * An effect to make a character Disarmed.
 */
public class DisarmCharacterEffect extends AbstractSubActionEffect {
    private PhysicalCard _cardDisarmed;
    private PhysicalCard _disarmedByCard;

    /**
     * Creates an effect to make a character Disarmed.
     * @param action the action performing this effect
     * @param cardDisarmed the card that is Disarmed
     * @param disarmedByCard the card the card was Disarmed by
     */
    public DisarmCharacterEffect(Action action, PhysicalCard cardDisarmed, PhysicalCard disarmedByCard) {
        super(action);
        _cardDisarmed = cardDisarmed;
        _disarmedByCard = disarmedByCard;
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
                        gameState.sendMessage(GameUtils.getCardLink(_cardDisarmed) + " is Disarmed by " + GameUtils.getCardLink(_disarmedByCard));
                        _cardDisarmed.setDisarmed(true);

                        Collection<PhysicalCard> weapons = Filters.filter(_cardDisarmed.getCardsAttached(), game, Filters.weapon);
                        subAction.appendEffect(
                                new LoseCardsFromTableSimultaneouslyEffect(subAction, weapons, true, false));
                        subAction.appendEffect(
                                new TriggeringResultEffect(subAction, new DisarmedResult(_disarmedByCard.getOwner(), _cardDisarmed)));
                    }
                });

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
