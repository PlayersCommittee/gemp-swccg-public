package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the Light Side to go first this game.
 */
public class LightSideGoesFirstEffect extends AbstractSuccessfulEffect {

    /**
     * Creates an effect that causes the Light Side to go first this game.
     * @param action the action performing this effect
     */
    public LightSideGoesFirstEffect(Action action) {
        super(action);
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        PhysicalCard source = _action.getActionSource();

        if (!game.getModifiersQuerying().hasFlagActive(game.getGameState(), ModifierFlag.LIGHT_SIDE_GOES_FIRST)) {
            game.getGameState().sendMessage(GameUtils.getCardLink(source) + " causes Light Side to go first");
            game.getModifiersEnvironment().addUntilEndOfGameModifier(
                    new SpecialFlagModifier(source, ModifierFlag.LIGHT_SIDE_GOES_FIRST));
        }
    }
}
