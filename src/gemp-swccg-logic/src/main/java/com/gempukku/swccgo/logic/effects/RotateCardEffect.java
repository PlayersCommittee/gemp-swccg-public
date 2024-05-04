package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.RotateCardResult;

/**
 * An effect that causes the specified card to be rotated.
 */
public class RotateCardEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _cardToRotate;
    private boolean _upsideDown;

    /**
     * Creates an effect that causes the specified card to be rotated.
     * @param action the action performing the effect
     * @param cardToRotate the card to rotate
     * @param upsideDown true if the card is rotated to be upside-down, otherwise it is rotated to be right-side-up
     */
    public RotateCardEffect(Action action, PhysicalCard cardToRotate, boolean upsideDown) {
        super(action);
        _cardToRotate = cardToRotate;
        _upsideDown = upsideDown;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        if (_cardToRotate.isInverted() != _upsideDown) {
            if (_cardToRotate.getBlueprint().getCardCategory()== CardCategory.LOCATION)
                gameState.sendMessage(GameUtils.getCardLink(_cardToRotate) + " is rotated");
            else if (_upsideDown)
                gameState.sendMessage(GameUtils.getCardLink(_cardToRotate) + " is turned upside-down");
            else
                gameState.sendMessage(GameUtils.getCardLink(_cardToRotate) + " is turned right side up");

            gameState.invertCard(game, _cardToRotate, _upsideDown);

            if (_cardToRotate.getBlueprint().getCardCategory()== CardCategory.LOCATION) {
                gameState.reapplyAffectingForCard(game, _cardToRotate);
            }
        }

        game.getActionsEnvironment().emitEffectResult(new RotateCardResult(_action.getPerformingPlayer(), _cardToRotate));
    }
}
