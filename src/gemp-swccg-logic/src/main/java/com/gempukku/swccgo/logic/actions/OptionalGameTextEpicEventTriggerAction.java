package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.EpicEventState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.Effect;


public class OptionalGameTextEpicEventTriggerAction extends OptionalGameTextTriggerAction {

    private boolean _beginEpicEventState;
    private EpicEventState _epicEventState;


    public OptionalGameTextEpicEventTriggerAction(PhysicalCard self, int gameTextSourceCardId, GameTextActionId gameTextActionId, EpicEventState epicEventState) {
        super(self, gameTextSourceCardId, gameTextActionId);
        _epicEventState = epicEventState;
    }

    @Override
    public Effect nextEffect(SwccgGame game) {

        // Begin Epic Event (if not started yet)
        if (!_beginEpicEventState) {
            _beginEpicEventState = true;
            if (_epicEventState == null) {
                throw new UnsupportedOperationException(GameUtils.getFullName(_physicalCard) + " does not have set Epic Event state");
            }
            game.getGameState().beginEpicEvent(_epicEventState);
        }

        // Let the base class do it's thing
        Effect nextEffect = super.nextEffect(game);
        if (nextEffect != null) {
            return nextEffect;
        }

        // After all effects are done, clear up the Epic Event state

        // Finish Epic Event
        game.getGameState().finishEpicEvent();

        return null;

    }

}
