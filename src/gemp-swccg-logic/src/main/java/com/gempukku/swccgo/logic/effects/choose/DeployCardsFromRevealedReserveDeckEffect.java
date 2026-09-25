package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.ChooseArbitraryCardsEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Shared helper for Panic / Emergency Deployment / Go For Help / 3B3-21 / similar cards:
 * among a previously revealed set still in Reserve Deck, optionally deploy matching cards
 * (typically for free), then handle undeployed leftovers per {@link LeftoverMode}.
 * <p>
 * Revealed cards remain in Reserve Deck in the same order. If a response draws destiny (or
 * otherwise moves a revealed card) mid-resolution, that card leaves the revealed set normally;
 * remaining revealed cards stay deployable; leftovers are lost or left on top depending on mode.
 * If the Reserve is shuffled while cards are revealed, GameState ends revealed-state on the
 * act of shuffling (even if order happens to stay the same): they stay in Reserve and are
 * not leftover-lost.
 * <p>
 * Unpiloted ships/vehicles deploy via the normal play-card path, which may pair a pilot/driver
 * from hand (normal cost) but not another revealed Reserve card.
 * <p>
 * Revealed-state lives on GameState (mark at RevealTopCards, clear on shuffle / leave-pile)
 * so any reveal can share it. Deploy is still Bruckman-style from the remaining revealed set.
 * Named for the reveal/deploy path; leftover handling is {@link LeftoverMode} (lose or leave on top),
 * not always a lose.
 */
public class DeployCardsFromRevealedReserveDeckEffect extends AbstractSubActionEffect {

    /**
     * What to do with revealed cards that remain undeployed (and still in Reserve Deck) at the end.
     */
    public enum LeftoverMode {
        /** Lose remaining revealed cards still in Reserve Deck (Panic / Emergency Deployment / 3B3-21). */
        LOSE,
        /** Leave remaining revealed cards on top of Reserve Deck in the same order (Go For Help!). */
        LEAVE_ON_TOP
    }

    private final String _playerId;
    private final List<PhysicalCard> _remainingCards;
    private final Filterable _deployableTypesFilter;
    private final Filter _locationFilter;
    private final boolean _forFree;
    private final LeftoverMode _leftoverMode;

    /**
     * Deploy matching revealed cards anywhere (for free by default for Panic-family), then lose the rest.
     */
    public DeployCardsFromRevealedReserveDeckEffect(Action action, Collection<PhysicalCard> revealedCards,
                                                          Filterable deployableTypesFilter, boolean forFree) {
        this(action, revealedCards, deployableTypesFilter, null, forFree, LeftoverMode.LOSE);
    }

    /**
     * Deploy matching revealed cards to a location accepted by locationFilter (or anywhere if null), then lose the rest.
     */
    public DeployCardsFromRevealedReserveDeckEffect(Action action, Collection<PhysicalCard> revealedCards,
                                                          Filterable deployableTypesFilter, Filter locationFilter,
                                                          boolean forFree) {
        this(action, revealedCards, deployableTypesFilter, locationFilter, forFree, LeftoverMode.LOSE);
    }

    /**
     * Deploy matching revealed cards anywhere, then handle leftovers per leftoverMode.
     */
    public DeployCardsFromRevealedReserveDeckEffect(Action action, Collection<PhysicalCard> revealedCards,
                                                          Filterable deployableTypesFilter, boolean forFree,
                                                          LeftoverMode leftoverMode) {
        this(action, revealedCards, deployableTypesFilter, null, forFree, leftoverMode);
    }

    /**
     * Deploy matching revealed cards to a location accepted by locationFilter (or anywhere if null),
     * then handle leftovers per leftoverMode.
     * <p>
     * LEAVE_ON_TOP is a no-op for leftovers when cards never left Reserve (proper reveal path):
     * undeployed revealed cards already sit on top in original order.
     */
    public DeployCardsFromRevealedReserveDeckEffect(Action action, Collection<PhysicalCard> revealedCards,
                                                          Filterable deployableTypesFilter, Filter locationFilter,
                                                          boolean forFree, LeftoverMode leftoverMode) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _remainingCards = new LinkedList<PhysicalCard>(revealedCards);
        _deployableTypesFilter = deployableTypesFilter;
        _locationFilter = locationFilter;
        _forFree = forFree;
        _leftoverMode = leftoverMode != null ? leftoverMode : LeftoverMode.LOSE;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    /**
     * AR: revealed cards that leave Reserve drop out of the set. The act of shuffling the
     * Reserve ends revealed-state on GameState even if remaining cards happen to stay in
     * the same order.
     */
    private void dropCardsNoLongerRevealed(SwccgGame game) {
        GameState gameState = game.getGameState();
        List<PhysicalCard> pile = gameState.getCardPile(_playerId, Zone.RESERVE_DECK);
        if (pile == null) {
            _remainingCards.clear();
            return;
        }
        List<PhysicalCard> stillRevealed = new LinkedList<PhysicalCard>();
        for (PhysicalCard card : _remainingCards) {
            if (pile.contains(card) && gameState.isCardRevealedFromPile(card)) {
                stillRevealed.add(card);
            }
        }
        _remainingCards.clear();
        _remainingCards.addAll(stillRevealed);
    }

    private Filter currentlyDeployableFilter(SwccgGame game) {
        dropCardsNoLongerRevealed(game);
        // Only cards still in this player's Reserve Deck remain in the revealed deployable set
        // (a mid-resolution destiny draw / move / lose drops a card out of the set automatically).
        Filter stillInReserve = Filters.and(Filters.in(_remainingCards), Filters.zoneOfPlayer(Zone.RESERVE_DECK, _playerId), _deployableTypesFilter);
        if (_locationFilter != null) {
            return Filters.and(stillInReserve, Filters.deployableToLocation(_action.getActionSource(), _locationFilter, _forFree, 0));
        }
        return Filters.and(stillInReserve, Filters.deployable(_action.getActionSource(), null, _forFree, 0));
    }

    /**
     * Lose undeployed leftovers only after the deploy loop has actually ended. A sibling leftover
     * effect would run as soon as the first choose decision was posted, before a mid-choice shuffle.
     */
    private void loseUndeployedLeftovers(SubAction subAction, SwccgGame game) {
        dropCardsNoLongerRevealed(game);
        if (_leftoverMode == LeftoverMode.LEAVE_ON_TOP) {
            return;
        }
        if (!_remainingCards.isEmpty()) {
            subAction.appendEffect(new LoseCardsFromReserveDeckEffect(subAction, new LinkedList<PhysicalCard>(_remainingCards)));
        }
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        Collection<PhysicalCard> deployableCards = Filters.filter(_remainingCards, game, currentlyDeployableFilter(game));
                        if (!deployableCards.isEmpty()) {
                            subAction.insertEffect(getChooseOneCardToDeployEffect(subAction, game, deployableCards));
                        } else {
                            loseUndeployedLeftovers(subAction, game);
                        }
                    }
                }
        );
        return subAction;
    }

    private StandardEffect getChooseOneCardToDeployEffect(final SubAction subAction, final SwccgGame game, final Collection<PhysicalCard> deployableCards) {
        // min 0 => player may stop deploying even when more matching cards remain (remainder handled by leftover mode)
        return new ChooseArbitraryCardsEffect(subAction, _playerId, "Choose card to deploy" + GameUtils.s(1) + " (or none)",
                _remainingCards, currentlyDeployableFilter(game), 0, 1) {
            @Override
            protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                dropCardsNoLongerRevealed(game);
                if (selectedCards.isEmpty()) {
                    loseUndeployedLeftovers(subAction, game);
                    return;
                }
                final PhysicalCard selectedCard = selectedCards.iterator().next();
                // Mid-resolution destiny/move/shuffle may have already ended this card's reveal
                if (!_remainingCards.contains(selectedCard)
                        || !Filters.zoneOfPlayer(Zone.RESERVE_DECK, _playerId).accepts(game, selectedCard)) {
                    _remainingCards.remove(selectedCard);
                    Collection<PhysicalCard> more = Filters.filter(_remainingCards, game, currentlyDeployableFilter(game));
                    if (!more.isEmpty()) {
                        subAction.insertEffect(getChooseOneCardToDeployEffect(subAction, game, more));
                    } else {
                        loseUndeployedLeftovers(subAction, game);
                    }
                    return;
                }
                _remainingCards.remove(selectedCard);
                if (_locationFilter != null) {
                    subAction.insertEffect(
                            new DeployCardToLocationFromReserveDeckEffect(subAction, selectedCard, _locationFilter, _forFree, false, false),
                            new PassthruEffect(subAction) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    Collection<PhysicalCard> more = Filters.filter(_remainingCards, game, currentlyDeployableFilter(game));
                                    if (!more.isEmpty()) {
                                        subAction.insertEffect(getChooseOneCardToDeployEffect(subAction, game, more));
                                    } else {
                                        loseUndeployedLeftovers(subAction, game);
                                    }
                                }
                            }
                    );
                } else {
                    subAction.insertEffect(
                            new DeployCardFromReserveDeckEffect(subAction, selectedCard, _forFree, false),
                            new PassthruEffect(subAction) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    Collection<PhysicalCard> more = Filters.filter(_remainingCards, game, currentlyDeployableFilter(game));
                                    if (!more.isEmpty()) {
                                        subAction.insertEffect(getChooseOneCardToDeployEffect(subAction, game, more));
                                    } else {
                                        loseUndeployedLeftovers(subAction, game);
                                    }
                                }
                            }
                    );
                }
            }
        };
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}