package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.GameTextCanceledCondition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that adds a modifier until the end of battle.
 */
public class MindscanCharacterUntilEndOfBattleEffect extends AddModifierWithDurationEffect {
    private PhysicalCard _self;
    private PhysicalCard _mindscanned;
    /**
     * Creates an effect that adds a modifier until the end of battle.
     * @param action the action adding the modifier
     * @param modifier the modifier
     * @param actionMsg the action message
     */
    public MindscanCharacterUntilEndOfBattleEffect(Action action, Modifier modifier, String actionMsg, PhysicalCard self, PhysicalCard mindscanned) {
        super(action, modifier, actionMsg);
        _self = self;
        _mindscanned = mindscanned;
    }

    @Override
    public final void doPlayEffect(SwccgGame game) {
        sendMsg(game);
        game.getModifiersEnvironment().addUntilEndOfBattleModifier(_modifier);

        if(game.getModifiersQuerying().hasMindscannedCharacter(game.getGameState(), _self)) {
            boolean mindscannedCharacterHadGameTextCanceled = _mindscanned.isGameTextCanceled();

            // if mindscanned character is a maintenance card copy the maintenance icon
            if(!mindscannedCharacterHadGameTextCanceled && _mindscanned.getBlueprint().hasIcon(Icon.MAINTENANCE)) {
                game.getModifiersEnvironment().addUntilEndOfBattleModifier(new IconModifier(_self, new NotCondition(new GameTextCanceledCondition(_self)), Icon.MAINTENANCE));
            }

            // copy modifiers
            if(!mindscannedCharacterHadGameTextCanceled) {
                for (Modifier m : _mindscanned.getBlueprint().getWhileInPlayModifiers(game, _self)) {
                    m.appendCondition(new NotCondition(new GameTextCanceledCondition(_self)));
                    game.getModifiersEnvironment().addUntilEndOfBattleModifier(m);
                }
            }

            // modifiers that are always on even if game text is canceled
            for (Modifier m : _mindscanned.getBlueprint().getAlwaysOnModifiers(game, _self)) {
                m.appendCondition(new NotCondition(new GameTextCanceledCondition(_self)));
                game.getModifiersEnvironment().addUntilEndOfBattleModifier(m);
            }
        }

    }
}
