package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.CaptureOption;
import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.CaptureCharacterResult;
import com.gempukku.swccgo.logic.timing.results.CaptureStarshipResult;

import java.util.Collection;

/**
 * An effect that captures the specified character and has a specified escort 'seize' that captive.
 */
public class CaptureStarshipEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _tractorBeam;
    private PhysicalCard _starship;

    /**
     * Creates an effect that captures the specified character and has a specified escort 'seize' that captive.
     * @param action the action performing this effect
     * @param tractorBeam the tractor beam capturing the starship
     * @param starship the starship to capture
     */
    public CaptureStarshipEffect(Action action, PhysicalCard starship, PhysicalCard tractorBeam) {
        super(action);
        _tractorBeam = tractorBeam;
        _starship = starship;
    }

    @Override
    protected void doPlayEffect(final SwccgGame game) {
        final String performingPlayer = game.getDarkPlayer();
        final PhysicalCard source = _action.getActionSource();
        final GameState gameState = game.getGameState();

        StringBuilder msgText = new StringBuilder(performingPlayer);
        msgText.append(" causes ").append(GameUtils.getCardLink(_starship)).append(" to be captured using ");
        msgText.append(GameUtils.getCardLink(_tractorBeam));
        gameState.sendMessage(msgText.toString());


        Collection<PhysicalCard> _attachTo = game.getModifiersQuerying().getDestinationForCapturedStarships(game.getGameState(), _tractorBeam);

        _action.appendEffect(new ChooseCardOnTableEffect(_action, game.getGameState().getCurrentPlayerId(), "Capture starship under which card", _attachTo) {
            @Override
            protected void cardSelected(PhysicalCard selectedCard) {
                gameState.cardAffectsCard(performingPlayer, _tractorBeam, _starship);
                gameState.captureStarship(game, _starship, selectedCard);
            }
        });

        _action.appendEffect(new PassthruEffect(_action) {
            @Override
            protected void doPlayEffect(SwccgGame game) {
                // Emit effect result that starship was captured
                game.getActionsEnvironment().emitEffectResult(new CaptureStarshipResult(performingPlayer, source, _starship, CaptureOption.SEIZE));
            }
        });
    }
}
