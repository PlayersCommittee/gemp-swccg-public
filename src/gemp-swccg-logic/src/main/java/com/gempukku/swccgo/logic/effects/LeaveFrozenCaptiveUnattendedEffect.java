package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.LeaveFrozenCaptiveUnattendedResult;

/**
 * The effect to leave a 'frozen' captive as 'unattended'.
 */
public class LeaveFrozenCaptiveUnattendedEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _escort;
    private PhysicalCard _captive;
    private PhysicalCard _site;

    /**
     * Create an effect to leave a 'frozen' captive as 'unattended' at a site.
     * @param action the action performing this effect
     * @param escort the escort
     * @param captive the captive
     * @param site the site
     */
    public LeaveFrozenCaptiveUnattendedEffect(Action action, PhysicalCard escort, PhysicalCard captive, PhysicalCard site) {
        super(action);
        _escort = escort;
        _captive = captive;
        _site = site;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        gameState.sendMessage(GameUtils.getCardLink(_escort) + " leaves 'frozen' captive " + GameUtils.getCardLink(_captive) + " 'unattended' at " + GameUtils.getCardLink(_site));
        gameState.moveCardToLocation(_captive, _site, _captive.getOwner().equals(game.getDarkPlayer()));
        //Something like this needs to be applied to fix the unattended captive no longer showing the escort
        // as a target in the UI.
        //_captive.clearTargetedCards();

        // Emit the result effect that can trigger other cards
        game.getActionsEnvironment().emitEffectResult(new LeaveFrozenCaptiveUnattendedResult(_escort, _captive, _site));
    }
}
