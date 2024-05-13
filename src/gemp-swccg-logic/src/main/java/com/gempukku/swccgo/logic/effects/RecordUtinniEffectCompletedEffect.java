package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.UtinniEffectCompletedResult;

/**
 * This effect records the specified Utinni Effect as being 'completed' for the purposes of the game keeping track of which
 * Utinni Effects have been completed during the game.
 */
public class RecordUtinniEffectCompletedEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _utinniEffect;

    /**
     * Creates an effect that records the specified Utinni Effect as being 'completed' for the purposes of the game keeping
     * track of which Utinni Effects have been completed during the game.
     * @param action the action performing this effect
     * @param utinniEffect the Utinni Effect
     */
    public RecordUtinniEffectCompletedEffect(Action action, PhysicalCard utinniEffect) {
        super(action);
        _utinniEffect = utinniEffect;
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        // Send message for Utinni Effect completed
        game.getGameState().sendMessage(GameUtils.getCardLink(_utinniEffect) + " is 'completed'");
        game.getModifiersQuerying().completedUtinniEffect(_utinniEffect.getOwner(), _utinniEffect);

        // Emit effect result that Utinni Effect was completed
        game.getActionsEnvironment().emitEffectResult(new UtinniEffectCompletedResult(_utinniEffect.getOwner(), _utinniEffect));
    }
}
