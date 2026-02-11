package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;

/**
 * An effect that causes the topmost race destinies on the specified Podracers to be exchanged.
 */
public class ExchangeTopmostRaceDestiniesEffect extends AbstractSubActionEffect {
    private final String _playerId;
    private PhysicalCard _stackedOn1;
    private PhysicalCard _stackedOn2;

    /**
     * Creates an effect that causes the player the topmost race destinies on the specified Podracers to be exchanged.
     * @param action the action performing this effect
     * @param stackedOn1 a Podracer
     * @param stackedOn2 a Podracer
     */
    public ExchangeTopmostRaceDestiniesEffect(Action action, PhysicalCard stackedOn1, PhysicalCard stackedOn2) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _stackedOn1 = stackedOn1;
        _stackedOn2 = stackedOn2;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
            new PassthruEffect(subAction) {
                @Override
                protected void doPlayEffect(final SwccgGame game) {
                    Collection<PhysicalCard> topmostRaceDestinies1 = Filters.filter(gameState.getStackedCards(_stackedOn1), game, Filters.topRaceDestinyForPlayer(_stackedOn1.getOwner()));
                    Collection<PhysicalCard> topmostRaceDestinies2 = Filters.filter(gameState.getStackedCards(_stackedOn2), game, Filters.topRaceDestinyForPlayer(_stackedOn2.getOwner()));
                    if (!topmostRaceDestinies1.isEmpty() && !topmostRaceDestinies2.isEmpty()) {
                        PhysicalCard topmostRaceDestiny1 = topmostRaceDestinies1.iterator().next();
                        PhysicalCard topmostRaceDestiny2 = topmostRaceDestinies2.iterator().next();
                        gameState.sendMessage(_playerId + " exchanges " + GameUtils.getCardLink(topmostRaceDestiny1) + " stacked on " + GameUtils.getCardLink(_stackedOn1) + " with " + GameUtils.getCardLink(topmostRaceDestiny2) + " stacked on " + GameUtils.getCardLink(_stackedOn2));
                        gameState.removeCardFromZone(topmostRaceDestiny1);
                        gameState.removeCardFromZone(topmostRaceDestiny2);
                        gameState.stackCard(topmostRaceDestiny1, _stackedOn2, false, false, false);
                        gameState.stackCard(topmostRaceDestiny2, _stackedOn1, false, false, false);
                        topmostRaceDestiny1.setRaceDestinyForPlayer(_stackedOn2.getOwner());
                        topmostRaceDestiny2.setRaceDestinyForPlayer(_stackedOn1.getOwner());
                    }
                }
            }
        );
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
