package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
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
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: The Great Hutt Expansion
 * Type: Interrupt
 * Subtype: Lost
 * Title: It's My Throne
 */
public class Card304_137 extends AbstractLostInterrupt {
    public Card304_137() {
        super(Side.DARK, 4, "It's My Throne", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Kamjin may not be a natural politician but he will not suffer any attempts to seize his throne while he is Emperor.");
        setGameText("If Thran at Monolith Throne Room, take Kamjin into hand from a location you control (cards on him go to owner's hand). OR During your control phase, if Kamjin escorting Locita or Kai at a site you control, relocate Kamjin (with captive) to Monolith Docking Bay. (Immune to Sense.)");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Filter kamjinFilter1 = Filters.and(Filters.Kamjin, Filters.at(Filters.controls(playerId)));

        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.and(Filters.Thran, Filters.on(Title.Monolith_Throne_Room)))
                && GameConditions.canTarget(game, self, kamjinFilter1)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setImmuneTo(Title.Sense);
            action.setText("Take Kamjin into hand");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Kamjin to take into hand", kamjinFilter1) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Take " + GameUtils.getCardLink(targetedCard) + " into hand",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ReturnCardToHandFromTableEffect(action, targetedCard, Zone.HAND));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        Filter kamjinFilter2 = Filters.and(Filters.Kamjin, Filters.escorting(Filters.or(Filters.Locita, Filters.Kai)),
                Filters.at(Filters.and(Filters.site, Filters.controls(playerId))), Filters.canBeRelocatedToLocation(Filters.Monolith_Docking_Bay, false, true, false, 0, false));

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)
                && GameConditions.canTarget(game, self, kamjinFilter2)) {
            final PhysicalCard monolithDockingBay = Filters.findFirstFromTopLocationsOnTable(game, Filters.Monolith_Docking_Bay);
            if (monolithDockingBay != null) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setImmuneTo(Title.Sense);
                action.setText("Relocate Kamjin to Seraph: Monolith Docking Bay");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Kamjin to relocate", kamjinFilter2) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                action.addAnimationGroup(monolithDockingBay);
                                // Pay cost(s)
                                action.appendCost(
                                        new PayRelocateBetweenLocationsCostEffect(action, playerId, targetedCard, monolithDockingBay, 0));
                                // Allow response(s)
                                action.allowResponses("Relocate " + GameUtils.getCardLink(targetedCard) + " to " + GameUtils.getCardLink(monolithDockingBay),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new RelocateBetweenLocationsEffect(action, targetedCard, monolithDockingBay));
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }
}