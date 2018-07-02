package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.common.*;
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
 * Set: Death Star II
 * Type: Interrupt
 * Subtype: Lost
 * Title: Rise, My Friend
 */
public class Card9_140 extends AbstractLostInterrupt {
    public Card9_140() {
        super(Side.DARK, 4, "Rise, My Friend", Uniqueness.UNIQUE);
        setLore("'I sense you wish to continue your search for young Skywalker.'");
        setGameText("If Emperor on Death Star II, take Vader into hand from a location you control (cards on him go to owner's hand). OR During your control phase, if Vader escorting Luke or Leia at a site you control, relocate Vader (with captive) to Death Star II: Docking Bay. (Immune to Sense.)");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Filter vaderFilter1 = Filters.and(Filters.Vader, Filters.at(Filters.controls(playerId)));

        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.and(Filters.Emperor, Filters.on(Title.Death_Star_II)))
                && GameConditions.canTarget(game, self, vaderFilter1)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setImmuneTo(Title.Sense);
            action.setText("Take Vader into hand");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Vader to take into hand", vaderFilter1) {
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

        Filter vaderFilter2 = Filters.and(Filters.Vader, Filters.escorting(Filters.or(Filters.Luke, Filters.Leia)),
                Filters.at(Filters.and(Filters.site, Filters.controls(playerId))), Filters.canBeRelocatedToLocation(Filters.Death_Star_II_Docking_Bay, false, true, false, 0));

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)
                && GameConditions.canTarget(game, self, vaderFilter2)) {
            final PhysicalCard deathStarIIDockingBay = Filters.findFirstFromTopLocationsOnTable(game, Filters.Death_Star_II_Docking_Bay);
            if (deathStarIIDockingBay != null) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setImmuneTo(Title.Sense);
                action.setText("Relocate Vader to Death Star II: Docking Bay");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Vader to relocate", vaderFilter2) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                action.addAnimationGroup(deathStarIIDockingBay);
                                // Pay cost(s)
                                action.appendCost(
                                        new PayRelocateBetweenLocationsCostEffect(action, playerId, targetedCard, deathStarIIDockingBay, 0));
                                // Allow response(s)
                                action.allowResponses("Relocate " + GameUtils.getCardLink(targetedCard) + " to " + GameUtils.getCardLink(deathStarIIDockingBay),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new RelocateBetweenLocationsEffect(action, targetedCard, deathStarIIDockingBay));
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