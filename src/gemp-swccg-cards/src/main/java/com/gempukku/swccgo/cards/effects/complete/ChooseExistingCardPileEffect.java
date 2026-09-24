package com.gempukku.swccgo.cards.effects.complete;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.decisions.CardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * An effect that causes the specified player to choose a card pile.
 * The card piles chosen from are required to have cards in them.
 */
public abstract class ChooseExistingCardPileEffect extends AbstractChoosePileEffect {
    private String _playerId;
    private String _zoneOwner;
    private Filterable _pileFilter;
    private boolean _includeFrozenAssetsAsForcePileTop;

    /**
     * Creates an effect that causes the player to choose an existing card pile.
     * @param action the action performing this effect
     * @param playerId the player to make the choice
     */
    public ChooseExistingCardPileEffect(Action action, String playerId) {
        this(action, playerId, null, Filters.or(Zone.RESERVE_DECK, Zone.FORCE_PILE, Zone.USED_PILE, Zone.LOST_PILE), false);
    }

    /**
     * Creates an effect that causes the player to choose an existing card pile.
     * @param action the action performing this effect
     * @param playerId the player to make the choice
     * @param includeFrozenAssetsAsForcePileTop true if Frozen Assets on Frozen Pile counts as the top of an empty Force Pile
     */
    public ChooseExistingCardPileEffect(Action action, String playerId, boolean includeFrozenAssetsAsForcePileTop) {
        this(action, playerId, null, Filters.or(Zone.RESERVE_DECK, Zone.FORCE_PILE, Zone.USED_PILE, Zone.LOST_PILE), includeFrozenAssetsAsForcePileTop);
    }

    /**
     * Creates an effect that causes the player to choose one of the specified existing card piles.
     * @param action the action performing this effect
     * @param playerId the player to make the choice
     * @param pileFilter the piles to choose from (by using Zone filters)
     */
    public ChooseExistingCardPileEffect(Action action, String playerId, Filterable pileFilter) {
        this(action, playerId, null, pileFilter, false);
    }

    /**
     * Creates an effect that causes the player to choose an existing card pile owned by the specified player.
     * @param action the action performing this effect
     * @param playerId the player to make the choice
     * @param zoneOwner the card pile owner
     */
    public ChooseExistingCardPileEffect(Action action, String playerId, String zoneOwner) {
        this(action, playerId, zoneOwner, Filters.or(Zone.RESERVE_DECK, Zone.FORCE_PILE, Zone.USED_PILE, Zone.LOST_PILE), false);
    }

    /**
     * Creates an effect that causes the player to choose one of the specified existing card piles owned by the specified
     * player.
     * @param action the action performing this effect
     * @param playerId the player to make the choice
     * @param zoneOwner the card pile owner
     * @param pileFilter the piles to choose from (by using Zone filters)
     */
    public ChooseExistingCardPileEffect(Action action, String playerId, String zoneOwner, Filterable pileFilter) {
        this(action, playerId, zoneOwner, pileFilter, false);
    }

    private ChooseExistingCardPileEffect(Action action, String playerId, String zoneOwner, Filterable pileFilter, boolean includeFrozenAssetsAsForcePileTop) {
        super(action);
        _playerId = playerId;
        _zoneOwner = zoneOwner;
        _pileFilter = pileFilter;
        _includeFrozenAssetsAsForcePileTop = includeFrozenAssetsAsForcePileTop;
    }

    @Override
    protected void doPlayEffect(final SwccgGame game) {
        Collection<PhysicalCard> topOfPiles = new ArrayList<PhysicalCard>(Filters.filter(game.getGameState().getTopCardsOfPiles(_zoneOwner), game, _pileFilter));
        if (_includeFrozenAssetsAsForcePileTop) {
            addConceptualFrozenAssetsForcePileTops(game, topOfPiles);
        }
        if (!topOfPiles.isEmpty()) {
            if (topOfPiles.size() == 1) {
                PhysicalCard topCard = topOfPiles.iterator().next();
                pileChosen(game, topCard.getZoneOwner(), pileZoneForTopCard(topCard));
            }
            else {
                game.getUserFeedback().sendAwaitingDecision(_playerId,
                        new CardsSelectionDecision("Choose card pile", topOfPiles, 1, 1) {
                            @Override
                            public void decisionMade(String result) throws DecisionResultInvalidException {
                                List<PhysicalCard> topOfPileCardsSelected = getSelectedCardsByResponse(result);
                                PhysicalCard topCard = topOfPileCardsSelected.get(0);
                                pileChosen(game, topCard.getZoneOwner(), pileZoneForTopCard(topCard));
                            }
                        });
            }
        }
    }

    private void addConceptualFrozenAssetsForcePileTops(SwccgGame game, Collection<PhysicalCard> topOfPiles) {
        GameState gameState = game.getGameState();
        for (String playerId : Arrays.asList(gameState.getDarkPlayer(), gameState.getLightPlayer())) {
            if (_zoneOwner != null && !_zoneOwner.equals(playerId)) {
                continue;
            }
            if (!gameState.getForcePile(playerId).isEmpty() || !gameState.hasFrozenAssetsMarker(playerId)) {
                continue;
            }
            PhysicalCard fa = gameState.getTopOfFrozenPile(playerId);
            if (fa != null && !topOfPiles.contains(fa)) {
                topOfPiles.add(fa);
            }
        }
    }

    private Zone pileZoneForTopCard(PhysicalCard topCard) {
        Zone zone = GameUtils.getZoneFromZoneTop(topCard.getZone());
        if (_includeFrozenAssetsAsForcePileTop && Title.Frozen_Assets.equals(topCard.getTitle()) && zone == Zone.FROZEN_PILE) {
            return Zone.FORCE_PILE;
        }
        return zone;
    }

    protected abstract void pileChosen(SwccgGame game, String cardPileOwner, Zone cardPile);
}
