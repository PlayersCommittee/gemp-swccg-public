package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.choose.TakeStackedCardIntoHandEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.RemovedCoaxiumCardResult;

/**
 * An effect that removes a 'coaxium' card
 */
public class TakeCoaxiumCardInHandEffect extends AbstractSubActionEffect {

    private Filterable _stackedOn;

    /**
     * An effect that removes a 'coaxium' card
     *
     * @param action the action performing this effect
     */
    public TakeCoaxiumCardInHandEffect(Action action, Filterable stackedOn) {
        super(action);
        this._stackedOn = stackedOn;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final SubAction subAction = new SubAction(_action);
        final String playerId = _action.getPerformingPlayer();

        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        new TakeStackedCardIntoHandEffect(subAction, playerId, _stackedOn, Filters.coaxiumCard);
                    }
                }
        );

        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        game.getActionsEnvironment().emitEffectResult(new RemovedCoaxiumCardResult(playerId, null, playerId, Zone.HAND));
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
