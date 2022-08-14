package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutStackedCardsInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: Escape Pod
 */
public class Card1_079 extends AbstractUsedInterrupt {
    public Card1_079() {
        super(Side.LIGHT, 6, Title.Escape_Pod);
        setLore("Capital starships have emergency escape pods. Equipped with food, water, flares, medpacs, hunting blaster and tracking beacon (R2-D2 deactivated this one's beacon).");
        setGameText("If your capital starship is about to be lost, relocate your characters aboard to any one planet site.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final Filter destinationFilter = Filters.planet_site;

        // Check condition(s)
        if ((TriggerConditions.isAboutToBeLost(game, effectResult, Filters.and(Filters.your(self), Filters.capital_starship))
                || TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, Filters.and(Filters.your(self), Filters.capital_starship)))
                && GameConditions.canSpot(game, self, destinationFilter)) {

            final AboutToLeaveTableResult aboutToLeaveTableResult = (AboutToLeaveTableResult) effectResult;
            final PhysicalCard cardToBeLost = aboutToLeaveTableResult.getCardAboutToLeaveTable();
            final Filter aboardFilter = Filters.and(Filters.your(self), Filters.character, Filters.aboard(cardToBeLost), Filters.canBeTargetedBy(self));

            Collection<PhysicalCard> possibleSites = new HashSet<>();
            for (PhysicalCard card : Filters.filterActive(game, self, aboardFilter)) {
                possibleSites.addAll(Filters.filterTopLocationsOnTable(game, Filters.and(destinationFilter, Filters.locationCanBeRelocatedTo(card, 0))));
            }

            if (!possibleSites.isEmpty()) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Relocate characters to a planet site");

                action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose planet site to relocate characters", Filters.in(possibleSites)) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                        // Allow response(s)
                        action.allowResponses(
                                new RespondablePlayCardEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Perform result(s)
                                        PhysicalCard destination = action.getPrimaryTargetCard(targetGroupId);
                                        Collection<PhysicalCard> toRelocate = Filters.filterActive(game, self, Filters.and(aboardFilter, Filters.canBeRelocatedToLocation(destination, 0)));
                                        action.addAnimationGroup(toRelocate);
                                        action.appendEffect(new RelocateBetweenLocationsEffect(action, toRelocate, destination));
                                    }
                                }
                        );
                    }
                });
                return Collections.singletonList(action);
            }
        }

        return null;
    }
}