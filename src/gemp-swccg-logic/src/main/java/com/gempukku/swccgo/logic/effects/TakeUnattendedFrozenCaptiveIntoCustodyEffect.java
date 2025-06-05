package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.TookUnattendedFrozenCaptiveIntoCustodyResult;

/**
 * The effect to take an 'unattended frozen' captive into custody.
 */
public class TakeUnattendedFrozenCaptiveIntoCustodyEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _escort;
    private PhysicalCard _captive;
    private boolean _thawCaptive;

    /**
     * Create an effect to have the escort take the 'unattended frozen' captive into custody from the site.
     * @param action the action performing this effect
     * @param escort the escort
     * @param captive the captive
     * @param thawCaptive true if captive becomes non-frozen captive, otherwise false
     */
    public TakeUnattendedFrozenCaptiveIntoCustodyEffect(Action action, PhysicalCard escort, PhysicalCard captive, boolean thawCaptive) {
        super(action);
        _escort = escort;
        _captive = captive;
        _thawCaptive = thawCaptive;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        PhysicalCard site = game.getModifiersQuerying().getLocationThatCardIsPresentAt(game.getGameState(), _escort);

        gameState.sendMessage(GameUtils.getCardLink(_escort) + " takes unattended frozen captive " + GameUtils.getCardLink(_captive) + " at " + GameUtils.getCardLink(site) + " into custody" + (_thawCaptive ? " as non-frozen captive" : ""));
        if (_thawCaptive) {
            _captive.setFrozen(false);
        }
        gameState.seizeCharacter(game, _captive, _escort);

        // Emit the result effect that can trigger other cards
        game.getActionsEnvironment().emitEffectResult(new TookUnattendedFrozenCaptiveIntoCustodyResult(_escort, _captive, site));
    }
}
