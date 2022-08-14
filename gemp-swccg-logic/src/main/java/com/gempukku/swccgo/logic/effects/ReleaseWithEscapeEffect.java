package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.CardType;
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
 * An effect that releases the specified character and has the character 'escape' to Used Pile.
 */
public class ReleaseWithEscapeEffect extends AbstractSubActionEffect {
    private PhysicalCard _captive;

    /**
     * Creates an effect that releases the specified character and has the character 'escape' to Used Pile.
     * @param action the action performing this effect
     * @param captive the captive
     */
    public ReleaseWithEscapeEffect(Action action, PhysicalCard captive) {
        super(action);
        _captive = captive;
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

                        gameState.sendMessage(GameUtils.getCardLink(_captive) + " is released and 'escapes' to Used Pile");
                        if (source != null) {
                            gameState.cardAffectsCard(performingPlayer, source, _captive);
                        }
                        game.getModifiersEnvironment().removeEndOfCaptivity(_captive);
                    }
                });
        //cards aboard a captured starship go to used when the starship escapes
        if(game.getModifiersQuerying().getCardTypes(game.getGameState(), _captive).contains(CardType.STARSHIP))
            subAction.appendEffect(
                    new PlaceCardsInCardPileFromTableSimultaneouslyEffect(subAction, Collections.singleton(_captive), Zone.USED_PILE, false, false, Zone.USED_PILE, false, false, false, null, true));
        else
            subAction.appendEffect(
                    new PlaceCardsInCardPileFromTableSimultaneouslyEffect(subAction, Collections.singleton(_captive), Zone.USED_PILE, false, false, Zone.LOST_PILE, false, false, false, null, true));

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
