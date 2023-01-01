package com.gempukku.swccgo.cards.set208.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 8
 * Type: Interrupt
 * Subtype: Used
 * Title: Walker Sighting (V)
 */
public class Card208_022 extends AbstractUsedInterrupt {
    public Card208_022() {
        super(Side.LIGHT, 3, "Walker Sighting", Uniqueness.UNIQUE, ExpansionSet.SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Echo station, 3TA. We have spotted Imperial walkers.' A Rebel tactic is to put as much ground as possible between walkers and Rebel troops, allowing time to prepare a defense.");
        setGameText("Cancel Too Cold For Speeders. OR Unless opponent's AT-AT or AT-ST at a battleground site, retrieve 1 Force for each Rebel Base location you control (limit 3). OR Your Rebels and T-47s at sites where opponent has an AT-AT or AT-ST are power +2 for remainder of turn.");
        addIcons(Icon.HOTH, Icon.VIRTUAL_SET_8);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

       // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Too_Cold_For_Speeders)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Too_Cold_For_Speeders, Title.Too_Cold_For_Speeders);
            actions.add(action);
        }
        
        // Check condition(s)
        if (!GameConditions.canSpot(game, self, Filters.and(Filters.opponents(self), Filters.or(Filters.AT_AT, Filters.AT_ST), Filters.at(Filters.battleground_site)))) {
            final int numToRetrieve = Math.min(3, Filters.countTopLocationsOnTable(game, Filters.and(Filters.Rebel_Base_location, Filters.controls(playerId), Filters.canBeTargetedBy(self))));
            if (numToRetrieve > 0) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Retrieve " + numToRetrieve + " Force");
                action.addSecondaryTargetFilter(Filters.and(Filters.Rebel_Base_location, Filters.controls(playerId)));
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new RetrieveForceEffect(action, playerId, numToRetrieve));
                            }
                        }
                );
                actions.add(action);
            }
        }

        final Filter rebelsAndT47sFilter = Filters.and(Filters.your(self), Filters.or(Filters.Rebel, Filters.T_47),
                Filters.at(Filters.and(Filters.sameSiteAs(self, Filters.and(Filters.opponents(self), Filters.or(Filters.AT_AT,
                        Filters.AT_ST), Filters.canBeTargetedBy(self))), Filters.canBeTargetedBy(self))));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, rebelsAndT47sFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make Rebels and T-47s power +2");
            // Choose target(s)
            action.appendTargeting(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(final SwccgGame game) {
                            Collection<PhysicalCard> rebelsAndT47s = Filters.filterActive(game, self, rebelsAndT47sFilter);
                            action.addAnimationGroup(rebelsAndT47s);
                            action.addSecondaryTargetFilter(rebelsAndT47sFilter);
                            action.addSecondaryTargetFilter(Filters.and(Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.or(Filters.Rebel, Filters.T_47))),
                                    Filters.sameSiteAs(self, Filters.and(Filters.opponents(self), Filters.or(Filters.AT_AT, Filters.AT_ST)))));
                            action.addSecondaryTargetFilter(Filters.and(Filters.opponents(self), Filters.or(Filters.AT_AT, Filters.AT_ST),
                                    Filters.at(Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.or(Filters.Rebel, Filters.T_47))))));
                            // Allow response(s)
                            action.allowResponses("Make " + GameUtils.getAppendedNames(rebelsAndT47s) + " power +2",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            final Collection<PhysicalCard> rebelsAndT47s = Filters.filterActive(game, self, rebelsAndT47sFilter);
                                            if (!rebelsAndT47s.isEmpty()) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new AddUntilEndOfTurnModifierEffect(action,
                                                                new PowerModifier(self, Filters.in(rebelsAndT47s), 2),
                                                                "Makes " + GameUtils.getAppendedNames(rebelsAndT47s) + " power +2"));
                                            }
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Too_Cold_For_Speeders)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}