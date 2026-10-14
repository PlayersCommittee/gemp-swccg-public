package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
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
 * <p>
 * Unpiloted ships/vehicles deploy via the normal play-card path, which may pair a pilot/driver
 * from hand (normal cost) but not another revealed Reserve card.
 * <p>
 * Does not introduce persistent revealed-state engine tracking; uses existing reveal UI
 * plus deploy-from-reserve of specific cards (Sergeant Bruckman-style).
 */
public class DeployCardsFromReserveDeckAndLoseTheRestEffect extends AbstractSubActionEffect {

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
    public DeployCardsFromReserveDeckAndLoseTheRestEffect(Action action, Collection<PhysicalCard> revealedCards,
                                                          Filterable deployableTypesFilter, boolean forFree) {
        this(action, revealedCards, deployableTypesFilter, null, forFree, LeftoverMode.LOSE);
    }

    /**
     * Deploy matching revealed cards to a location accepted by locationFilter (or anywhere if null), then lose the rest.
     */
    public DeployCardsFromReserveDeckAndLoseTheRestEffect(Action action, Collection<PhysicalCard> revealedCards,
                                                          Filterable deployableTypesFilter, Filter locationFilter,
                                                          boolean forFree) {
        this(action, revealedCards, deployableTypesFilter, locationFilter, forFree, LeftoverMode.LOSE);
    }

    /**
     * Deploy matching revealed cards anywhere, then handle leftovers per leftoverMode.
     */
    public DeployCardsFromReserveDeckAndLoseTheRestEffect(Action action, Collection<PhysicalCard> revealedCards,
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
    public DeployCardsFromReserveDeckAndLoseTheRestEffect(Action action, Collection<PhysicalCard> revealedCards,
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

    private Filter currentlyDeployableFilter() {
        // Only cards still in this player's Reserve Deck remain in the revealed deployable set
        // (a mid-resolution destiny draw / move / lose drops a card out of the set automatically).
        Filter stillInReserve = Filters.and(Filters.in(_remainingCards), Filters.zoneOfPlayer(Zone.RESERVE_DECK, _playerId), _deployableTypesFilter);
        if (_locationFilter != null) {
            return Filters.and(stillInReserve, Filters.deployableToLocation(_action.getActionSource(), _locationFilter, _forFree, 0));
        }
        return Filters.and(stillInReserve, Filters.deployable(_action.getActionSource(), null, _forFree, 0));
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        Collection<PhysicalCard> deployableCards = Filters.filter(_remainingCards, game, currentlyDeployableFilter());
                        if (!deployableCards.isEmpty()) {
                            subAction.insertEffect(getChooseOneCardToDeployEffect(subAction, deployableCards));
                        }
                    }
                }
        );
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (_leftoverMode == LeftoverMode.LEAVE_ON_TOP) {
                            // Proper reveal leaves cards in Reserve in original order; nothing to put back.
                            return;
                        }
                        Collection<PhysicalCard> cardsToLose = Filters.filter(_remainingCards, game,
                                Filters.zoneOfPlayer(Zone.RESERVE_DECK, _playerId));
                        if (!cardsToLose.isEmpty()) {
                            subAction.appendEffect(new LoseCardsFromReserveDeckEffect(subAction, cardsToLose));
                        }
                    }
                }
        );
        return subAction;
    }

    private StandardEffect getChooseOneCardToDeployEffect(final SubAction subAction, final Collection<PhysicalCard> deployableCards) {
        // min 0 => player may stop deploying even when more matching cards remain (remainder handled by leftover mode)
        return new ChooseArbitraryCardsEffect(subAction, _playerId, "Choose card to deploy" + GameUtils.s(1) + " (or none)",
                _remainingCards, currentlyDeployableFilter(), 0, 1) {
            @Override
            protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                if (selectedCards.isEmpty()) {
                    return;
                }
                final PhysicalCard selectedCard = selectedCards.iterator().next();
                // Mid-resolution destiny/move may have already removed this card from Reserve
                if (!Filters.zoneOfPlayer(Zone.RESERVE_DECK, _playerId).accepts(game, selectedCard)) {
                    _remainingCards.remove(selectedCard);
                    Collection<PhysicalCard> more = Filters.filter(_remainingCards, game, currentlyDeployableFilter());
                    if (!more.isEmpty()) {
                        subAction.insertEffect(getChooseOneCardToDeployEffect(subAction, more));
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
                                    Collection<PhysicalCard> more = Filters.filter(_remainingCards, game, currentlyDeployableFilter());
                                    if (!more.isEmpty()) {
                                        subAction.insertEffect(getChooseOneCardToDeployEffect(subAction, more));
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
                                    Collection<PhysicalCard> more = Filters.filter(_remainingCards, game, currentlyDeployableFilter());
                                    if (!more.isEmpty()) {
                                        subAction.insertEffect(getChooseOneCardToDeployEffect(subAction, more));
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