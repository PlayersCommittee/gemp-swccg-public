package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.CloseSpaceSlugMouthResult;

/**
 * An effect that closes the specified Space Slug's mouth.
 */
public class CloseSpaceSlugMouthEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _spaceSlug;

    /**
     * Creates an effect that closes the specified Space Slug's mouth.
     * @param action the action performing this effect
     * @param spaceSlug the Space Slug
     */
    public CloseSpaceSlugMouthEffect(Action action, PhysicalCard spaceSlug) {
        super(action);
        _spaceSlug = spaceSlug;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        if (!_spaceSlug.isMouthClosed()) {
            String performingPlayerId = _action.getPerformingPlayer();
            game.getGameState().sendMessage(performingPlayerId + " closes " + GameUtils.getCardLink(_spaceSlug) + "'s mouth");
            _spaceSlug.setMouthClosed(true);
            game.getGameState().cardAffectsCard(null, _action.getActionSource(), _spaceSlug);
            game.getActionsEnvironment().emitEffectResult(new CloseSpaceSlugMouthResult(_spaceSlug, performingPlayerId));
        }
    }
}
