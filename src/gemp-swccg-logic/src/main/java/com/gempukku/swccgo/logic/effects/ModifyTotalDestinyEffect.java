package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/** Modifies only the current draw process's total, with ordinary non-cumulative title matching. */
public class ModifyTotalDestinyEffect extends AbstractSuccessfulEffect {
    private final float _amount;

    public ModifyTotalDestinyEffect(Action action, float amount) {
        super(action);
        _amount = amount;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        DrawDestinyState state = game.getGameState().getTopDrawDestinyState();
        if (state == null || state.getDrawDestinyEffect().getTotalDestiny(game) == null) return;
        DrawDestinyEffect draw = state.getDrawDestinyEffect();
        if (draw.getDestinyType() == DestinyType.BATTLE_DESTINY
                && (game.getModifiersQuerying().mayNotModifyTotalBattleDestiny(game.getGameState(), draw.getPlayerDrawingDestiny(), _action.getPerformingPlayer())
                || _amount > 0 && game.getModifiersQuerying().mayNotIncreaseTotalBattleDestiny(game.getGameState(), draw.getPlayerDrawingDestiny(), _action.getPerformingPlayer()))) {
            return;
        }
        PhysicalCard source = _action.getActionSource();
        draw.modifyTotalDestiny(source != null ? source.getTitles() : null, _action.getGameTextActionId(), _amount, false);
        game.getGameState().sendMessage(_action.getPerformingPlayer() + " modifies total "
                + draw.getDestinyType().getHumanReadable() + " by " + GuiUtils.formatAsString(_amount)
                + " to " + GuiUtils.formatAsString(draw.getTotalDestiny(game)));
    }
}
