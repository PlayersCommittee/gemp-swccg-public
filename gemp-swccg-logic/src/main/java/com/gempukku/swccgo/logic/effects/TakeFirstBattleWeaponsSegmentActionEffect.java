package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the specified player to take the first weapons segment action of the battle.
 */
public class TakeFirstBattleWeaponsSegmentActionEffect extends AbstractSuccessfulEffect {
    private String _playerId;

    /**
     * Creates an effect that causes the specified player to take the first weapons segment action of the battle.
     * @param action the action performing this effect
     * @param playerId the player
     */
    public TakeFirstBattleWeaponsSegmentActionEffect(Action action, String playerId) {
        super(action);
        _playerId = playerId;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        PhysicalCard source = _action.getActionSource();

        game.getGameState().sendMessage(GameUtils.getCardLink(source) + " causes " + _playerId + " to take the first weapons segment action in battle");
        game.getModifiersEnvironment().addUntilEndOfBattleModifier(
                new SpecialFlagModifier(source, ModifierFlag.TAKES_FIRST_BATTLE_WEAPONS_SEGMENT_ACTION, _playerId));
    }
}
