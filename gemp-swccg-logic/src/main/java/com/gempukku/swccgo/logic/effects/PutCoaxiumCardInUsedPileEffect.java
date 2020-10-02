package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.RemovedCoaxiumCardResult;

/**
 * An effect that removes a 'coaxium' card
 */
public class PutCoaxiumCardInUsedPileEffect extends AbstractSubActionEffect {

    private PhysicalCard _card;

    /**
     * An effect that removes a 'coaxium' card
     *
     * @param action the action performing this effect
     * @param card   the card to remove
     */
    public PutCoaxiumCardInUsedPileEffect(Action action, PhysicalCard card) {
        super(action);
        _card = card;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final SubAction subAction = new SubAction(_action);
        final String playerId = _action.getPerformingPlayer();

        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        new PutStackedCardInUsedPileEffect(subAction, playerId, _card, false);
                    }
                }
        );

        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        game.getActionsEnvironment().emitEffectResult(new RemovedCoaxiumCardResult(playerId, _card, playerId, Zone.USED_PILE));
                    }
                }
        );

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }
}
