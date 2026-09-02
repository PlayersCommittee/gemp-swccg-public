package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.layout.RearrangeSites;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * An effect that lets the player click matching sites on the table in the new
 * left-to-right order, then rearranges them once all are chosen.
 * Uses the existing ChooseCardOnTable / CardsSelection UI (highlighted cards,
 * click in order). The filter is the row to rearrange, so Death Star interiors
 * and Bespin / Cloud City interiors share this path. Rearrange is atomic: the
 * table does not change until every site has been chosen.
 */
public class ChooseAndRearrangeRelatedSitesEffect extends AbstractSubActionEffect {
    private String _playerId;
    private Filter _filter;

    /**
     * Lets the player click sites accepted by siteFilter in the new left-to-right order.
     * @param action the action performing this effect
     * @param playerId the player clicking sites
     * @param siteFilter location filter for the row, typically RearrangeSites.interiorSitesOfSystem
     */
    public ChooseAndRearrangeRelatedSitesEffect(Action action, String playerId, Filter siteFilter) {
        super(action);
        _playerId = playerId;
        _filter = siteFilter;
    }

    /**
     * Same as the filter constructor, using interior-only sites of the given system.
     * @param action the action performing this effect
     * @param playerId the player clicking sites
     * @param systemName the system title, for example Title.Death_Star or Title.Bespin
     */
    public ChooseAndRearrangeRelatedSitesEffect(Action action, String playerId, String systemName) {
        this(action, playerId, RearrangeSites.interiorSitesOfSystem(systemName));
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return !matchingSites(game).isEmpty();
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final SubAction subAction = new SubAction(_action, _playerId);
        List<PhysicalCard> remaining = matchingSites(game);
        if (remaining.isEmpty()) {
            return subAction;
        }
        subAction.appendEffect(new ChooseNextSiteEffect(subAction, remaining, new ArrayList<PhysicalCard>()));
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }

    private List<PhysicalCard> matchingSites(SwccgGame game) {
        return new ArrayList<PhysicalCard>(Filters.filter(game.getGameState().getLocationsInOrder(), game, _filter));
    }

    /**
     * Sequential ChooseCardOnTable: remaining sites stay highlighted, each click
     * is the next slot from the left. The last remaining site is auto-chosen.
     * RearrangeRelatedSitesEffect runs only after the full order is known.
     */
    private class ChooseNextSiteEffect extends ChooseCardOnTableEffect {
        private final SubAction _subAction;
        private final List<PhysicalCard> _remaining;
        private final List<PhysicalCard> _chosenOrder;

        private ChooseNextSiteEffect(SubAction subAction, List<PhysicalCard> remaining, List<PhysicalCard> chosenOrder) {
            super(subAction, _playerId,
                    chosenOrder.isEmpty() ? "Click leftmost site" : "Click next site from left",
                    remaining);
            _subAction = subAction;
            _remaining = remaining;
            _chosenOrder = chosenOrder;
        }

        @Override
        protected void cardSelected(PhysicalCard selectedCard) {
            _chosenOrder.add(selectedCard);
            List<PhysicalCard> stillRemaining = new ArrayList<PhysicalCard>();
            for (PhysicalCard card : _remaining) {
                if (card.getCardId() != selectedCard.getCardId()) {
                    stillRemaining.add(card);
                }
            }
            if (stillRemaining.isEmpty()) {
                _subAction.appendEffect(new RearrangeRelatedSitesEffect(_subAction, _filter, _chosenOrder));
            }
            else {
                _subAction.appendEffect(new ChooseNextSiteEffect(_subAction, stillRemaining, _chosenOrder));
            }
        }
    }
}
