package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;

/**
 * An effect that captures the specified character and has the character escape to Used Pile.
 */
public class CaptureWithEscapeEffect extends AbstractSubActionEffect {
    private PhysicalCard _captive;
    private boolean _wasUndercover;
    private boolean _wasMissing;
    private PhysicalCard _cardFiringWeapon;

    /**
     * Creates an effect that captures the specified character and has the character escape to Used Pile.
     * @param action the action performing this effect
     * @param captive the captive
     * @param wasUndercover true if the character was 'undercover' when captured, otherwise false
     * @param wasMissing true if the character was 'missing' when captured, otherwise false
     * @param cardFiringWeapon the card that fired weapon that caused capture, or null
     */
    public CaptureWithEscapeEffect(Action action, PhysicalCard captive, boolean wasUndercover, boolean wasMissing, PhysicalCard cardFiringWeapon) {
        super(action);
        _captive = captive;
        _wasUndercover = wasUndercover;
        _wasMissing = wasMissing;
        _cardFiringWeapon = cardFiringWeapon;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action);

        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        String performingPlayer = _action.getPerformingPlayer();
                        PhysicalCard source = _action.getActionSource();
                        GameState gameState = game.getGameState();

                        StringBuilder msgText = new StringBuilder(performingPlayer);
                        if (_wasUndercover) {
                            msgText.append(" breaks ").append(GameUtils.getCardLink(_captive)).append("'s cover and");
                        }
                        msgText.append(" causes ").append(GameUtils.getCardLink(_captive)).append(" to be captured and allowed to 'escape' using ");
                        msgText.append(GameUtils.getCardLink(_action.getActionSource()));
                        gameState.sendMessage(msgText.toString());
                        gameState.cardAffectsCard(performingPlayer, source, _captive);
                    }
                });
        subAction.appendEffect(
                new PlaceCardsInCardPileFromTableSimultaneouslyEffect(subAction, Collections.singleton(_captive), Zone.USED_PILE, false, false, Zone.LOST_PILE, true, _wasUndercover, _wasMissing, _cardFiringWeapon, false));

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
