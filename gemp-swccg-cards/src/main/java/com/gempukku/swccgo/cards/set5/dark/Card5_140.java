package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Lost
 * Title: Flight Escort
 */
public class Card5_140 extends AbstractLostInterrupt {
    public Card5_140() {
        super(Side.DARK, 4, "Flight Escort", Uniqueness.UNIQUE);
        setLore("'Rather touchy, aren't they?'");
        setGameText("During your move phase, if you have at least two combat vehicles at same cloud sector, relocate one starfighter present to a related docking bay or system. OR If one of your combat vehicles is defending a battle at a cloud sector, add one battle destiny.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Filter starfighterFilter = Filters.and(Filters.starfighter, Filters.canBeTargetedBy(self), Filters.presentAt(Filters.and(Filters.cloud_sector,
                Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.combat_vehicle, Filters.with(self, Filters.and(Filters.your(self), Filters.combat_vehicle)))))));

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE)) {
            Collection<PhysicalCard> starfighters = Filters.filterActive(game, self, starfighterFilter);
            if (!starfighters.isEmpty()) {
                // Figure out which starfighters can be relocated to related docking bay or system
                List<PhysicalCard> validStarfighters = new LinkedList<PhysicalCard>();
                for (PhysicalCard starfighter : starfighters) {
                    if (Filters.canBeRelocatedToLocation(Filters.and(Filters.relatedLocation(starfighter), Filters.or(Filters.docking_bay, Filters.system)), true, 0).accepts(game, starfighter)) {
                        validStarfighters.add(starfighter);
                    }
                }
                if (!validStarfighters.isEmpty()) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self);
                    action.setText("Relocate starfighter");
                    // Choose target(s)
                    action.appendTargeting(
                            new TargetCardOnTableEffect(action, playerId, "Choose starfighter", Filters.in(validStarfighters)) {
                                @Override
                                protected void cardTargeted(final int targetGroupId, final PhysicalCard starfighter) {
                                    action.addAnimationGroup(starfighter);
                                    Collection<PhysicalCard> otherLocations = Filters.filterTopLocationsOnTable(game, Filters.and(Filters.and(Filters.relatedLocation(starfighter), Filters.or(Filters.docking_bay, Filters.system), Filters.locationCanBeRelocatedTo(starfighter, false, false, true, 0, false))));
                                    action.appendTargeting(
                                            new ChooseCardOnTableEffect(action, playerId, "Choose related docking bay or system", otherLocations) {
                                                @Override
                                                protected void cardSelected(final PhysicalCard location) {
                                                    action.addAnimationGroup(location);
                                                    // Allow response(s)
                                                    action.allowResponses("Relocate " + GameUtils.getCardLink(starfighter) + " to " + GameUtils.getCardLink(location),
                                                            new RespondablePlayCardEffect(action) {
                                                                @Override
                                                                protected void performActionResults(Action targetingAction) {
                                                                    // Perform result(s)
                                                                    action.appendEffect(
                                                                            new RelocateBetweenLocationsEffect(action, starfighter, location));
                                                                }
                                                            }
                                                    );
                                                }
                                            }
                                    );
                                }
                            }
                    );
                    actions.add(action);
                }
            }
        }
        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.cloud_sector)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.combat_vehicle, Filters.defendingBattle))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add one battle destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddBattleDestinyEffect(action, 1));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}