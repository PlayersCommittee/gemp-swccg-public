package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.TripBuriedMinesEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.MovedResult;
import com.gempukku.swccgo.logic.timing.results.MovedUsingLandspeedResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;
import com.gempukku.swccgo.common.Zone;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Appendix C Mining Droid Rules — Burying Mines:
 * When any character, vehicle or starship deploys or moves to or across a site with buried cards,
 * those cards are tripped (revealed).
 */
public class BuryingMinesRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private Rule _that;

    public BuryingMinesRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
        _that = this;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        List<TriggerAction> actions = new LinkedList<TriggerAction>();

                        Filter tripperFilter = Filters.or(Filters.character, Filters.vehicle, Filters.starship);

                        // Deploy to site
                        if (TriggerConditions.justDeployedToLocation(game, effectResult, tripperFilter, Filters.site)) {
                            PhysicalCard played = ((PlayCardResult) effectResult).getPlayedCard();
                            PhysicalCard site = ((PlayCardResult) effectResult).getToLocation();
                            if (played != null && site != null && Filters.and(tripperFilter).accepts(game, played)) {
                                addTripAction(game, actions, site, played);
                            }
                        }

                        // Move to / across site
                        if (effectResult instanceof MovedResult) {
                            MovedResult movedResult = (MovedResult) effectResult;
                            if (!movedResult.isMoveComplete()) {
                                return actions;
                            }
                            Collection<PhysicalCard> movedCards = Filters.filter(movedResult.getMovedCards(), game, tripperFilter);
                            if (movedCards.isEmpty()) {
                                return actions;
                            }

                            Set<PhysicalCard> sitesToTrip = new HashSet<PhysicalCard>();
                            PhysicalCard to = movedResult.getMovedTo();
                            if (to != null && Filters.site.accepts(game, to)) {
                                sitesToTrip.add(to);
                            }
                            // Across: landspeed path sites (excluding origin)
                            if (effectResult instanceof MovedUsingLandspeedResult) {
                                Collection<PhysicalCard> path = ((MovedUsingLandspeedResult) effectResult).getLocationsAlongPath();
                                if (path != null) {
                                    for (PhysicalCard loc : path) {
                                        if (loc != null && Filters.site.accepts(game, loc) && (to == null || loc.getCardId() != to.getCardId())) {
                                            // path typically includes from; skip from
                                            PhysicalCard from = movedResult.getMovedFrom();
                                            if (from == null || loc.getCardId() != from.getCardId()) {
                                                sitesToTrip.add(loc);
                                            }
                                        }
                                    }
                                }
                            }

                            for (PhysicalCard site : sitesToTrip) {
                                // One tripper for targeting: prefer first moved card present at site after move
                                PhysicalCard tripper = movedCards.iterator().next();
                                addTripAction(game, actions, site, tripper);
                            }
                        }

                        return actions;
                    }
                }
        );
    }

    private void addTripAction(SwccgGame game, List<TriggerAction> actions, PhysicalCard site, PhysicalCard tripper) {
        List<PhysicalCard> buried = new LinkedList<PhysicalCard>();
        for (PhysicalCard stacked : game.getGameState().getStackedCards(site)) {
            if (stacked.isBuriedMine() && (stacked.getZone() == Zone.STACKED || stacked.getZone() == Zone.STACKED_FACE_DOWN)) {
                buried.add(stacked);
            }
        }
        if (buried.isEmpty()) {
            return;
        }
        RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that, site);
        action.setText("Trip buried mines");
        action.appendEffect(new TripBuriedMinesEffect(action, site, tripper, buried));
        actions.add(action);
    }
}
