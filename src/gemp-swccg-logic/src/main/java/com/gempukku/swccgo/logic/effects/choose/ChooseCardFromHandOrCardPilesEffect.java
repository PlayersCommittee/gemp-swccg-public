package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.modifiers.CantSearchCardPileModifier;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.TargetingEffect;
import com.gempukku.swccgo.logic.timing.results.VerifiedCardPileResult;

import java.util.*;

/**
 * An effect that causes the specified player to choose a card from hand and/or the specified card piles.
 *
 * Note: The choosing of a card provided by this effect does not involve persisting the card selected or any targeting
 * reasons. This is just choosing a card, and calling the cardsSelected method with the card chosen.
 */
public abstract class ChooseCardFromHandOrCardPilesEffect extends AbstractStandardEffect implements TargetingEffect {
    protected String _playerId;
    private List<Zone> _zones;
    private String _zoneOwner;
    private int _minimum;
    private int _maximum;
    private Filterable _filter;
    private boolean _forDeployment;

    /**
     * Creates an effect that causes the player to choose cards accepted by the specified filter from the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param zones the card piles
     * @param zoneOwner the player owning the card pile
     * @param filters the filter
     * @param forDeployment true if include deployable as if from hand, otherwise false
     * @param isOptional true if choosing a card is optional, otherwise false
     */
    public ChooseCardFromHandOrCardPilesEffect(Action action, String playerId, List<Zone> zones, String zoneOwner, Filterable filters, boolean forDeployment, boolean isOptional) {
        super(action);
        _playerId = playerId;
        _zones = zones;
        _zoneOwner = zoneOwner;
        _minimum = isOptional ? 0 : 1;
        _maximum = 1;
        _filter = filters;
        _forDeployment = forDeployment;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    public String getChoiceText(int numCardsToChoose) {
        StringBuilder stringBuilder = new StringBuilder("Choose card" + GameUtils.s(numCardsToChoose) + " from hand");
        if (_zones.size() == 1) {
            stringBuilder.append(" or ").append(_zones.iterator().next().getHumanReadable());
        }
        else {
            for (int i = 0; i < _zones.size(); i++) {
                stringBuilder.append(", ");
                Zone zone = _zones.get(i);
                if (i == (_zones.size() - 1)) {
                    stringBuilder.append("or ");
                }
                stringBuilder.append(zone.getHumanReadable());
            }
        }
        return stringBuilder.toString();
    }

    public boolean isPerformedEvenIfMinimumNotReached() {
        return false;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(final SwccgGame game) {
        GameState gameState = game.getGameState();

        // Determine the cards to choose from
        List<PhysicalCard> cardsToChooseFrom = new LinkedList<PhysicalCard>(gameState.getHand(_zoneOwner));
        if (_forDeployment) {
            cardsToChooseFrom.addAll(Filters.filter(gameState.getAllStackedCards(), game, Filters.and(Filters.owner(_playerId), Filters.canDeployAsIfFromHand)));
        }
        List<PhysicalCard> cardsInCardPiles = new ArrayList<PhysicalCard>();
        for (Zone zone : _zones) {
            Collection<PhysicalCard> cardPile = gameState.getCardPile(_zoneOwner, zone);
            cardsInCardPiles.addAll(cardPile);
            cardsToChooseFrom.addAll(cardPile);
        }

        Collection<PhysicalCard> selectableCards = Filters.filter(cardsToChooseFrom, game, _filter);

        // Make sure at least the minimum number of cards can be found
        final boolean success = selectableCards.size() >= _minimum;

        // Adjust the min and max card counts
        int maximum = Math.min(_maximum, selectableCards.size());
        int minimum = Math.min(_minimum, maximum);

        // If start of game, only show selectable cards to make it easier to find starting cards (and even auto-select it if only 1 selectable card)
        boolean onlyShowSelectable = success && gameState.getCurrentPhase()== Phase.PLAY_STARTING_CARDS;
        if (onlyShowSelectable && (minimum == 0 || minimum == 1) && (selectableCards.size() == minimum)) {
            cardsSelected(game, selectableCards);
            return new FullEffectResult(true);
        }

        if (success || isPerformedEvenIfMinimumNotReached()) {
            game.getUserFeedback().sendAwaitingDecision(_playerId,
                    new ArbitraryCardsSelectionDecision(getChoiceText(maximum), onlyShowSelectable ? selectableCards : cardsToChooseFrom, selectableCards, minimum, _maximum) {
                        @Override
                        public void decisionMade(String result) throws DecisionResultInvalidException {
                            cardsSelected(game, getSelectedCardsByResponse(result));
                        }
                    });
        }

        // If not success, show both players entire card pile to "verify".
        if (!success) {
            // Game rule: Since this search failed, prohibit the same search function from being used again on any cards
            // with same title for the remainder of the turn.
            if (_action.isFromGameText()
                    || _action.isFromPlayingInterrupt()) {
                PhysicalCard sourceCard = _action.getActionSource();
                for (Zone zone : _zones) {
                    game.getModifiersEnvironment().addUntilEndOfTurnModifier(
                            new CantSearchCardPileModifier(Filters.sameTitle(sourceCard), _playerId, zone, _zoneOwner, _action.getGameTextActionId()));
                }
            }

            StringBuilder cardPileStringBuilder = new StringBuilder();
            if (_zones.size() == 1) {
                cardPileStringBuilder.append(_zones.iterator().next().getHumanReadable());
            }
            else {
                for (int i = 0; i < _zones.size(); i++) {
                    if (_zones.size() > 1) {
                        cardPileStringBuilder.append(",");
                    }
                    Zone zone = _zones.get(i);
                    if (i == (_zones.size() - 1)) {
                        cardPileStringBuilder.append(" and");
                    }
                    cardPileStringBuilder.append(" ").append(zone.getHumanReadable());
                }
            }
            String cardPileNames = cardPileStringBuilder.toString();

            gameState.sendMessage(game.getOpponent(_playerId) + " verifies " + _zoneOwner + "'s " + cardPileNames);

            if (!isPerformedEvenIfMinimumNotReached()) {
                game.getUserFeedback().sendAwaitingDecision(_playerId,
                        new ArbitraryCardsSelectionDecision("Verify " + cardPileNames + " after unsuccessful attempt to '" + getChoiceText(_maximum) + "'", cardsInCardPiles, Collections.<PhysicalCard>emptyList(), 0, 0) {
                            @Override
                            public void decisionMade(String result) throws DecisionResultInvalidException {
                            }
                        });
            }
            game.getUserFeedback().sendAwaitingDecision(game.getOpponent(_playerId),
                    new ArbitraryCardsSelectionDecision("Verify " + cardPileNames + " after unsuccessful attempt to '" + getChoiceText(_maximum) + "'", cardsInCardPiles, Collections.<PhysicalCard>emptyList(), 0, 0) {
                        @Override
                        public void decisionMade(String result) throws DecisionResultInvalidException {
                        }
                    });
            if (!isPerformedEvenIfMinimumNotReached()) {
                cardsSelected(game, Collections.<PhysicalCard>emptyList());
            }

            // Emit effect result that card pile was verified by opponent
            for (Zone zone : _zones) {
                game.getActionsEnvironment().emitEffectResult(
                        new VerifiedCardPileResult(game.getOpponent(_playerId), _zoneOwner, zone));
            }
        }

        return new FullEffectResult(success);
    }

    private void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
        if (selectedCards.size() == 1) {
            cardSelected(game, selectedCards.iterator().next());
        }
    }

    protected abstract void cardSelected(SwccgGame game, PhysicalCard selectedCard);
}
