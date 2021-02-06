package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.CaptureOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.*;
import com.gempukku.swccgo.logic.timing.results.AboutToCaptureCardResult;
import com.gempukku.swccgo.logic.timing.results.CaptureStarshipResult;

import java.util.*;

/**
 * An effect that captures the specified starship and attaches it to a specific card based on the tractor beam that captured it
 */
public class CaptureStarshipEffect extends AbstractSubActionEffect implements PreventableCardEffect  {
    private PhysicalCard _tractorBeam;
    private PhysicalCard _starship;
    private PreventableCardEffect _that;
    private Set<PhysicalCard> _preventedCards = new HashSet<PhysicalCard>();

    /**
     * Creates an effect that captures the specified starship and attaches it to a specific card based on the tractor beam that captured it
     * @param action the action performing this effect
     * @param tractorBeam the tractor beam capturing the starship
     * @param starship the starship to capture
     */
    public CaptureStarshipEffect(Action action, PhysicalCard starship, PhysicalCard tractorBeam) {
        super(action);
        _tractorBeam = tractorBeam;
        _starship = starship;
        _that = this;
    }

    @Override
    public void preventEffectOnCard(PhysicalCard card) {
        _preventedCards.add(card);
    }

    @Override
    public boolean isEffectOnCardPrevented(PhysicalCard card) {
        return _preventedCards.contains(card);
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final String performingPlayer = game.getDarkPlayer();
        final PhysicalCard source = _action.getActionSource();
        final GameState gameState = game.getGameState();

        final SubAction subAction = new SubAction(_action);


        List<EffectResult> effectResults = new ArrayList<EffectResult>();
        effectResults.add(new AboutToCaptureCardResult(_action, _starship, _that));
        subAction.appendEffect(new TriggeringResultsEffect(subAction, effectResults));


        subAction.appendEffect(new PassthruEffect(subAction) {
                                   @Override
                                   protected void doPlayEffect(final SwccgGame game) {
                                       if (!isEffectOnCardPrevented(_starship)) {
                                           StringBuilder msgText = new StringBuilder(performingPlayer);
                                           msgText.append(" causes ").append(GameUtils.getCardLink(_starship)).append(" to be captured using ");
                                           msgText.append(GameUtils.getCardLink(_tractorBeam));
                                           gameState.sendMessage(msgText.toString());


                                           Collection<PhysicalCard> _attachTo = game.getModifiersQuerying().getDestinationForCapturedStarships(game.getGameState(), _tractorBeam);

                                           subAction.appendEffect(new ChooseCardOnTableEffect(subAction, game.getGameState().getCurrentPlayerId(), "Capture starship under which card", _attachTo) {
                                               @Override
                                               protected void cardSelected(PhysicalCard selectedCard) {
                                                   gameState.cardAffectsCard(performingPlayer, _tractorBeam, _starship);
                                                   gameState.captureStarship(game, _starship, selectedCard);
                                               }
                                           });

                                           subAction.appendEffect(new PassthruEffect(subAction) {
                                               @Override
                                               protected void doPlayEffect(SwccgGame game) {
                                                   // Emit effect result that starship was captured
                                                   game.getActionsEnvironment().emitEffectResult(new CaptureStarshipResult(performingPlayer, source, _starship, CaptureOption.SEIZE));
                                               }
                                           });
                                       }
                                   }
                               }
        );

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _preventedCards.isEmpty();
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    public String getText(SwccgGame game) {
        return "Captured " + GameUtils.getCardLink(_starship);
    }

}
