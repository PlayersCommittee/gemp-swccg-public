package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.decisions.CardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.List;

/**
 * An effect that causes the specified player to choose a player by choosing an existing card pile of the specified zone.
 */
public class ChoosePlayerByExistingCardPileEffect extends AbstractChoosePlayerEffect {
    private Zone _zone;

    /**
     * Creates an effect that causes the specified player to choose a player an existing card pile.
     * @param action the action performing this effect
     * @param playerId the player to make the choice
     * @param zone the card pile
     */
    public ChoosePlayerByExistingCardPileEffect(Action action, String playerId, Zone zone) {
        super(action, playerId);
        _zone = zone;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(final SwccgGame game) {
        Collection<PhysicalCard> topOfCardPiles = Filters.filter(game.getGameState().getTopCardsOfPiles(), game, GameUtils.getZoneTopFromZone(_zone));

        if (topOfCardPiles.size() == 1 && !_action.isAllowAbort()) {
            setPlayerChosen(game, topOfCardPiles.iterator().next().getZoneOwner());
        }
        game.getUserFeedback().sendAwaitingDecision(_playerToMakeChoice,
                new CardsSelectionDecision("Choose " + _zone.getHumanReadable() + (_action.isAllowAbort() ? ", or click 'Done' to cancel" : ""), topOfCardPiles, _action.isAllowAbort() ? 0 : 1, 1) {
                    @Override
                    public void decisionMade(String result) throws DecisionResultInvalidException {
                        List<PhysicalCard> topOfPileCardsSelected = getSelectedCardsByResponse(result);
                        if (topOfPileCardsSelected.isEmpty() && _action.isAllowAbort()) {
                            return;
                        }

                        setPlayerChosen(game, topOfPileCardsSelected.get(0).getZoneOwner());
                    }
                });
        return new FullEffectResult(true);
    }

}
