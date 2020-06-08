package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.CaptureOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.CaptureCharacterResult;

/**
 * An effect that captures the specified character and imprisons the character in the prison the character is at.
 */
public class CaptureWithImprisonmentEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _captive;
    private boolean _wasUndercover;
    private boolean _wasMissing;
    private PhysicalCard _cardFiringWeapon;

    /**
     * Creates an effect that captures the specified character and imprisons the character in the prison the character is at.
     * @param action the action performing this effect
     * @param captive the captive
     * @param wasUndercover true if the character was 'undercover' when captured, otherwise false
     * @param wasMissing true if the character was 'missing' when captured, otherwise false
     * @param cardFiringWeapon the card that fired weapon that caused capture, or null
     */
    public CaptureWithImprisonmentEffect(Action action, PhysicalCard captive, boolean wasUndercover, boolean wasMissing, PhysicalCard cardFiringWeapon) {
        super(action);
        _captive = captive;
        _wasUndercover = wasUndercover;
        _wasMissing = wasMissing;
        _cardFiringWeapon = cardFiringWeapon;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        String performingPlayer = _action.getPerformingPlayer();
        PhysicalCard source = _action.getActionSource();
        GameState gameState = game.getGameState();
        PhysicalCard prison = game.getModifiersQuerying().getLocationThatCardIsAt(gameState, _captive);

        StringBuilder msgText = new StringBuilder(performingPlayer);
        if (_wasUndercover) {
            msgText.append(" breaks ").append(GameUtils.getCardLink(_captive)).append("'s cover and");
        }
        msgText.append(" causes ").append(GameUtils.getCardLink(_captive)).append(" to be");
        if (_captive.isFrozen()) {
            msgText.append(" 'frozen'");
        }
        else {
            msgText.append(" captured");
        }
        msgText.append(" and 'imprisoned' in ").append(GameUtils.getCardLink(prison)).append(" using ").append(GameUtils.getCardLink(_action.getActionSource()));
        gameState.sendMessage(msgText.toString());
        gameState.cardAffectsCard(performingPlayer, source, _captive);
        gameState.imprisonCharacter(game, _captive, prison);

        // Emit effect result that character was captured
        game.getActionsEnvironment().emitEffectResult(new CaptureCharacterResult(performingPlayer, source, _cardFiringWeapon, _captive, _wasUndercover, _wasMissing, CaptureOption.IMPRISONMENT));
    }
}
