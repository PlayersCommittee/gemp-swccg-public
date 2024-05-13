package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.InsertCardRevealedResult;

/**
 * An effect that causes the 'insert' card to be revealed.
 */
public class RevealInsertCardEffect extends AbstractSubActionEffect {
    private PhysicalCard _card;

    /**
     * Creates an effect that causes the 'insert' card to be revealed.
     * @param action the action performing this effect
     * @param card the 'insert' card
     */
    public RevealInsertCardEffect(Action action, PhysicalCard card) {
        super(action);
        _card = card;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final SubAction subAction = new SubAction(_action);

        // 1) Reveal 'insert' card and emit effect result
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (_card.isInserted() && !_card.isInsertCardRevealed()) {
                            _card.setInsertCardRevealed(true);
                            game.getGameState().sendMessage("'Insert' card " + GameUtils.getCardLink(_card) + " is revealed");
                            game.getGameState().activatedCard(null, _card);

                            game.getActionsEnvironment().emitEffectResult(
                                    new InsertCardRevealedResult(_card));

                        }
                    }
                }
        );

        // 2) If 'insert' card is still revealed, then carry out the effects from the 'insert' card
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (_card.isInsertCardRevealed()) {
                            Action insertCardRevealedAction = _card.getBlueprint().getInsertCardRevealedAction(game, _card);
                            if (insertCardRevealedAction != null) {
                                subAction.appendEffect(
                                        new StackActionEffect(subAction, insertCardRevealedAction));
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
