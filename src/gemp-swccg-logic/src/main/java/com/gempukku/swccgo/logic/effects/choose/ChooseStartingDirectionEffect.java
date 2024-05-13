package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.MovementDirection;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the specified player to choose a direction for the specified Mobile Effect.
 */
public class ChooseStartingDirectionEffect extends AbstractSuccessfulEffect {
    private String _playerToChooseDirection;
    private PhysicalCard _mobileEffect;

    /**
     * Creates an effect that causes the specified player to choose a player by choosing a side of the Force.
     * @param action the action performing this effect
     * @param playerId the player to choose direction
     */
    public ChooseStartingDirectionEffect(Action action, String playerId, PhysicalCard mobileEffect) {
        super(action);
        _playerToChooseDirection = playerId;
        _mobileEffect = mobileEffect;
    }

    @Override
    protected void doPlayEffect(final SwccgGame game) {
        game.getUserFeedback().sendAwaitingDecision(_playerToChooseDirection,
                new MultipleChoiceAwaitingDecision("Choose starting direction", new String[]{"Left", "Right"}) {
                    @Override
                    protected void validDecisionMade(int index, String result) {
                        _mobileEffect.setMovementDirection((index == 0) ? MovementDirection.LEFT : MovementDirection.RIGHT);
                        game.getGameState().sendMessage("Starting movement direction of " + GameUtils.getCardLink(_mobileEffect) + " is " + _mobileEffect.getMovementDirection().getHumanReadable().toLowerCase());
                    }
                });
    }
}
