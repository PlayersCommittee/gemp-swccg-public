package com.gempukku.swccgo.logic.timing;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SystemQueueAction;

import java.util.HashSet;
import java.util.Set;

/**
 * An action for playing out effect results.
 */
public class PlayOutEffectResults extends SystemQueueAction {
    private Set<EffectResult> _effectResults = new HashSet<>();
    private boolean _initialized;

    /**
     * Needed to generate snapshot.
     */
    public PlayOutEffectResults() {
    }

    @Override
    public void generateSnapshot(Action selfSnapshot, SnapshotData snapshotData) {
        super.generateSnapshot(selfSnapshot, snapshotData);
        PlayOutEffectResults snapshot = (PlayOutEffectResults) selfSnapshot;

        // Set each field
        for (EffectResult effectResult : _effectResults) {
            snapshot._effectResults.add(snapshotData.getDataForSnapshot(effectResult));
        }
        snapshot._initialized = _initialized;
    }

    /**
     * Creates an action for playing out effect results.
     * @param effectResults the effect results
     */
    public PlayOutEffectResults(Set<EffectResult> effectResults) {
        _effectResults = effectResults;
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        // The first time this is called, need to gather the required and optional actions that can respond to the
        // effect results.
        if (!_initialized) {
            _initialized = true;

            appendEffect(new CheckForExpiredModifiersEffect(this, _effectResults));

            appendEffect(new PlayoutRequiredAfterResponsesEffect(this, _effectResults));

            // Opponent of player that performed the results gets the first response (unless effect result specifies that performing player gets first response),
            // or current player if system-generated effect result. All effect results will be from the same player or system-generated.
            EffectResult effectResult = _effectResults.iterator().next();
            String playerCausingEffectResult = effectResult.getPerformingPlayerId();
            boolean performingPlayerRespondsFirst = effectResult.isPerformingPlayerRespondsFirst();
            String playerWithFirstResponse = playerCausingEffectResult != null ? (performingPlayerRespondsFirst ? playerCausingEffectResult : game.getOpponent(playerCausingEffectResult)) : game.getGameState().getCurrentPlayerId();
            appendEffect(new PlayoutOptionalAfterResponsesEffect(this, game.getGameState().getPlayerOrder().getPlayOrder(playerWithFirstResponse, true), 0, _effectResults));

            // Remove an modifiers that after responses to the effect results are complete.
            appendEffect(new PassthruEffect(this) {
                @Override
                protected void doPlayEffect(SwccgGame game) {
                     for (EffectResult curEffectResult : _effectResults) {
                         game.getModifiersEnvironment().removeEndOfEffectResult(curEffectResult);
                     }
                }
            });
        }
        return getNextEffect();
    }
}