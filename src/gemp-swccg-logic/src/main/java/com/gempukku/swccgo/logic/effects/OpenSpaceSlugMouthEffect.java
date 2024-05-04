package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.OpenSpaceSlugMouthResult;

/**
 * An effect that opens the specified Space Slug's mouth.
 */
public class OpenSpaceSlugMouthEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _spaceSlug;

    /**
     * Creates an effect that opens the specified Space Slug's mouth.
     * @param action the action performing this effect
     * @param spaceSlug the Space Slug
     */
    public OpenSpaceSlugMouthEffect(Action action, PhysicalCard spaceSlug) {
        super(action);
        _spaceSlug = spaceSlug;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        if (_spaceSlug.isMouthClosed()) {
            String performingPlayerId = _action.getPerformingPlayer();
            game.getGameState().sendMessage(performingPlayerId + " opens " + GameUtils.getCardLink(_spaceSlug) + "'s mouth");
            _spaceSlug.setMouthClosed(false);
            game.getGameState().cardAffectsCard(null, _action.getActionSource(), _spaceSlug);
            game.getActionsEnvironment().emitEffectResult(new OpenSpaceSlugMouthResult(_spaceSlug, performingPlayerId));
        }
    }
}
