package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.TargetingReason;
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
import com.gempukku.swccgo.logic.timing.results.LookedAtCardsInCardPileResult;
import com.gempukku.swccgo.logic.timing.results.VerifiedCardPileResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * An effect that causes the specified player to choose cards from the specified card pile.
 *
 * Note: The choosing of cards provided by this effect does not involve persisting the cards selected or any targeting
 * reasons. This is just choosing cards, and calling the cardsSelected method with the collection of cards chosen.
 */
public abstract class ChooseCardsFromPileEffect extends AbstractStandardEffect implements TargetingEffect {
    protected String _playerId;
    private Zone _zone;
    private String _zoneOwner;
    private int _minimum;
    private int _maximum;
    private int _maximumAcceptsCount;
    private boolean _matchPartialModelType;
    private boolean _topmost;
    private Filterable _filter;

    /**
     * Creates an effect that causes the player to choose cards accepted by the specified filter from the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param zone the card pile
     * @param zoneOwner the player owning the card pile
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param maximumAcceptsCount the maximum number of times cards may be accepted by the filter, which will further limit
     *                            cards that can be selected when cards with multiple model types accept filter multiple times
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param topmost true if only the topmost cards should be chosen from, otherwise false
     * @param filters the filter
     */
    protected ChooseCardsFromPileEffect(Action action, String playerId, Zone zone, String zoneOwner, int minimum, int maximum, int maximumAcceptsCount, boolean matchPartialModelType, boolean topmost, Filterable filters) {
        super(action);
        _playerId = playerId;
        _zone = zone;
        _zoneOwner = zoneOwner;
        _minimum = minimum;
        _maximum = maximum;
        _maximumAcceptsCount = maximumAcceptsCount;
        _matchPartialModelType = matchPartialModelType;
        _topmost = topmost;
        _filter = filters;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return !game.getGameState().getCardPile(_zoneOwner, _zone).isEmpty();
    }

    public String getChoiceText(int numCardsToChoose) {
        return "Choose card" + GameUtils.s(numCardsToChoose) + " from " + _zone.getHumanReadable();
    }

    public boolean isPerformedEvenIfMinimumNotReached() {
        return false;
    }

    public boolean isForStealAndDeploy() {
        return false;
    }

    public boolean isSkipTriggerPlayerLookedAtCardsInPile() {
        return false;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(final SwccgGame game) {
        GameState gameState = game.getGameState();

        // Determine the cards to choose from
        List<PhysicalCard> cardPile = gameState.getCardPile(_zoneOwner, _zone);

        Collection<PhysicalCard> selectableCards = cardPile;

        // If this is for steal and deploy, then temporarily change the owner of the cards to searching player before checking filters
        if (isForStealAndDeploy()) {
            selectableCards = Filters.filter(selectableCards, game, Filters.canBeTargetedBy(_action.getActionSource(), TargetingReason.TO_BE_STOLEN));
            for (PhysicalCard cardInPile : cardPile) {
                cardInPile.setOwner(_playerId);
            }
        }

        // Check if cards with multiple model types are only accepted if all model types match
        if (_topmost)
            selectableCards = Filters.filterCount(selectableCards, game, _maximum, _matchPartialModelType, _filter);
        else
            selectableCards = Filters.filter(selectableCards, game, _matchPartialModelType, _filter);

        // Filter cards by accounting for cards with multiple classes
        int acceptsCountSoFar = 0;
        List<PhysicalCard> validCards = new LinkedList<PhysicalCard>();
        for (PhysicalCard selectableCard : selectableCards) {
            int acceptsCount = Filters.and(_filter).acceptsCount(game, selectableCard);
            if (acceptsCount > 0 && acceptsCount <= _maximumAcceptsCount) {
                validCards.add(selectableCard);
                acceptsCountSoFar += acceptsCount;
            }
        }
        selectableCards = validCards;

        // If this is for steal and deploy, then change owner back
        if (isForStealAndDeploy()) {
            for (PhysicalCard cardInPile : cardPile) {
                cardInPile.setOwner(_zoneOwner);
            }
        }

        // Make sure at least the minimum number of cards can be found
        final boolean success = acceptsCountSoFar >= _minimum;

        // Adjust the min and max card counts
        int maximum = Math.min(_maximum, selectableCards.size());
        int minimum = Math.min(_minimum, maximum);

        // If start of game, only show selectable cards to make it easier to find starting cards (and even auto-select it if only 1 selectable card)
        // But not if the player choosing cards is the opponent of the owner of the card initiating the action (to avoid potentially revealing information about the deck if there is only 1 selectable card)
        boolean onlyShowSelectable = success && gameState.getCurrentPhase()== Phase.PLAY_STARTING_CARDS;
        if (onlyShowSelectable && (minimum == 0 || minimum == 1) && (selectableCards.size() == minimum)
                && (_action.getActionSource()==null || (_action.getActionSource()!= null && _action.getActionSource().getOwner().equals(_playerId)))) {
            cardsSelected(game, selectableCards);
            return new FullEffectResult(true);
        }

        if (success || isPerformedEvenIfMinimumNotReached()) {
            game.getUserFeedback().sendAwaitingDecision(_playerId,
                    new ArbitraryCardsSelectionDecision(getChoiceText(maximum), onlyShowSelectable ? selectableCards : cardPile, selectableCards, minimum, _maximum) {
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
                game.getModifiersEnvironment().addUntilEndOfTurnModifier(
                        new CantSearchCardPileModifier(Filters.sameTitle(sourceCard), _playerId, _zone, _zoneOwner, _action.getGameTextActionId()));
            }

            gameState.sendMessage(game.getOpponent(_playerId) + " verifies " + _zoneOwner + "'s " + _zone.getHumanReadable());

            if (!isPerformedEvenIfMinimumNotReached()) {
                game.getUserFeedback().sendAwaitingDecision(_playerId,
                        new ArbitraryCardsSelectionDecision("Verify " + _zone.getHumanReadable() + " after unsuccessful attempt to '" + getChoiceText(_maximum) + "'", cardPile, Collections.<PhysicalCard>emptyList(), 0, 0) {
                            @Override
                            public void decisionMade(String result) {
                            }
                        });
            }
            game.getUserFeedback().sendAwaitingDecision(game.getOpponent(_playerId),
                    new ArbitraryCardsSelectionDecision("Verify " + _zone.getHumanReadable() + " after unsuccessful attempt to '" + getChoiceText(_maximum) + "'", cardPile, Collections.<PhysicalCard>emptyList(), 0, 0) {
                        @Override
                        public void decisionMade(String result) {
                        }
                    });
            if (!isPerformedEvenIfMinimumNotReached()) {
                cardsSelected(game, Collections.<PhysicalCard>emptyList());
            }

            // Emit effect result that card pile was verified by opponent
            game.getActionsEnvironment().emitEffectResult(
                    new VerifiedCardPileResult(game.getOpponent(_playerId), _zoneOwner, _zone));
        }

        // Check if player looked at cards in own card pile
        if (!isSkipTriggerPlayerLookedAtCardsInPile()) {
            game.getActionsEnvironment().emitEffectResult(
                    new LookedAtCardsInCardPileResult(_playerId, _zoneOwner, _zone, _action.getActionSource()));
        }

        return new FullEffectResult(success);
    }

    protected abstract void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards);
}
