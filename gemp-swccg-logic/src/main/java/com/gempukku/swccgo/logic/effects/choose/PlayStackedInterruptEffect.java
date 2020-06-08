package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.timing.*;

/**
 * An effect that causes the player performing the action to play an Interrupt stacked on the specified card.
 */
public class PlayStackedInterruptEffect extends AbstractSubActionEffect {
    private String _playerId;
    private PhysicalCard _interrupt;
    private Effect _effect;
    private EffectResult _effectResult;
    private boolean _placeOutOfPlay;

    /**
     * Creates an effect that causes the player performing the action to play an Interrupt stacked on the specified card.
     * @param action the action performing this effect
     * @param interrupt the Interrupt to play
     * @param effect the effect to response to, or null
     * @param effectResult result the effect result to response to, or null
     * @param placeOutOfPlay true if Interrupt is placed out of play, otherwise false
     */
    public PlayStackedInterruptEffect(Action action, PhysicalCard interrupt, Effect effect, EffectResult effectResult, boolean placeOutOfPlay) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _interrupt = interrupt;
        _effect = effect;
        _effectResult = effectResult;
        _placeOutOfPlay = placeOutOfPlay;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {

        final SubAction subAction = new SubAction(_action, _playerId);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        PlayCardAction playCardAction;

                        if (_effect != null) {
                            playCardAction = _interrupt.getBlueprint().getPlayInterruptAsResponseAction(subAction.getPerformingPlayer(), game, _interrupt, _action.getActionSource(), _effect, null);
                        }
                        else if (_effectResult != null) {
                            playCardAction = _interrupt.getBlueprint().getPlayInterruptAsResponseAction(subAction.getPerformingPlayer(), game, _interrupt, _action.getActionSource(), null, _effectResult);
                        }
                        else {
                            playCardAction = _interrupt.getBlueprint().getPlayCardAction(subAction.getPerformingPlayer(), game, _interrupt, _action.getActionSource(), false, 0, null, null, null, null, null, false, 0, null, null);
                        }
                        playCardAction.setPlaceOutOfPlay(_placeOutOfPlay);
                        subAction.insertEffect(
                                new StackActionEffect(subAction, playCardAction));
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
