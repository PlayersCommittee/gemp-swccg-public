package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardsOutOfPlayFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Used
 * Title: Probe Telemetry
 */
public class Card3_132 extends AbstractUsedInterrupt {
    public Card3_132() {
        super(Side.DARK, 4, Title.Probe_Telemetry, Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.C2);
        setLore("Probe droids use electromagnetic, seismic, acoustic, olfactory and optical sensors. They report their findings using an omnisignal unicode.");
        setGameText("Use 2 Force to search any Lost Pile and place X non-unique cards there out of play, where X = number of Probe Droids on table. OR If your only character on a planet is a Probe Droid, your cards are deploy -1 to related sites for remainder of turn.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        String opponent = game.getOpponent(playerId);
        Filter yourProbeDroid = Filters.and(Filters.your(self), Filters.probe_droid);

        GameTextActionId gameTextActionId = GameTextActionId.PROBE_TELEMETRY__SEARCH_LOST_PILE;

        // Check condition(s)
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)) {
            final int numProbeDroids = Filters.countActive(game, self, yourProbeDroid);
            if (numProbeDroids > 0) {
                boolean canSearchLostPile = GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId);
                boolean canSearchOpponentsLostPile = GameConditions.canSearchOpponentsLostPile(game, playerId, self, gameTextActionId);
                if (canSearchLostPile || canSearchOpponentsLostPile) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self);
                    action.setText("Search a Lost Pile");
                    // Choose target(s)
                    action.appendTargeting(
                            new ChooseExistingCardPileEffect(action, playerId, (canSearchLostPile && canSearchOpponentsLostPile) ? null : (canSearchLostPile ? playerId : opponent), Zone.LOST_PILE) {
                                @Override
                                protected void pileChosen(SwccgGame game, final String cardPileOwner, Zone cardPile) {
                                    // Pay cost(s)
                                    action.appendCost(
                                            new UseForceEffect(action, playerId, 2));
                                    // Allow response(s)
                                    action.allowResponses("Search " + cardPileOwner + "'s Lost Pile and place " + numProbeDroids + " non-unique card" + GameUtils.s(numProbeDroids) + " out of play",
                                            new RespondablePlayCardEffect(action) {
                                                @Override
                                                protected void performActionResults(Action targetingAction) {
                                                    // Perform result(s)
                                                    action.appendEffect(
                                                            new PlaceCardsOutOfPlayFromLostPileEffect(action, playerId, cardPileOwner, numProbeDroids, numProbeDroids, Filters.non_unique, false));
                                                }
                                            }
                                    );
                                }
                            });
                    actions.add(action);
                }
            }
        }

        // Check condition(s)
        Collection<PhysicalCard> probeDroids = Filters.filterActive(game, self, yourProbeDroid);
        final List<PhysicalCard> locations = new LinkedList<PhysicalCard>();
        for (PhysicalCard probeDroid : probeDroids) {
            PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), probeDroid);
            if (location != null && Filters.or(Filters.planet_site, Filters.cloud_sector).accepts(game, location)) {
                String planetName = location.getPartOfSystem();
                if (planetName != null && !Filters.canSpot(game, self, Filters.and(Filters.your(self), Filters.character, Filters.not(probeDroid), Filters.onSamePlanet(probeDroid)))
                    && Filters.canSpot(game, self, Filters.relatedSite(location))) {
                    locations.add(location);
                }
            }
        }
        if (!locations.isEmpty()) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make cards deploy -1 to related sites");
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose a probe droid", Filters.and(Filters.in(probeDroids), Filters.at(Filters.in(locations)))) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    // Allow response(s)
                    PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), targetedCard);
                    action.allowResponses("Make your cards deploy -1 to sites related to " + GameUtils.getCardLink(location),
                            new RespondablePlayCardEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                    PhysicalCard finalLocation = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), finalTarget);
                                    // Perform result(s)
                                    action.appendEffect(
                                            new AddUntilEndOfTurnModifierEffect(action,
                                                    new DeployCostToLocationModifier(self, Filters.owner(playerId), -1, Filters.relatedSiteTo(self, Filters.and(finalLocation))),
                                                    "Makes " + playerId + "'s cards deploy -1 to sites related to " + GameUtils.getCardLink(finalLocation)));
                                }
                            }
                    );
                }
            });

            actions.add(action);
        }

        return actions;
    }
}