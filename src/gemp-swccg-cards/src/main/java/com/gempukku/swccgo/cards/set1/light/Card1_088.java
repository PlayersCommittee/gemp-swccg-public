package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetAllCardsAtSameLocationEffect;
import com.gempukku.swccgo.logic.effects.choose.MoveCardsAwayEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: Hyper Escape
 */
public class Card1_088 extends AbstractUsedInterrupt {
    public Card1_088() {
        super(Side.LIGHT, 5, Title.Hyper_Escape, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("'We'll be safe enough once we make the jump to hyperspace.' A starship in hyperspace cannot be tracked unless a homing beacon has been hidden aboard.");
        setGameText("If a battle was just initiated at any system or sector, move all your starships and vehicles there away.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final Filter starshipVehicleFilter = Filters.and(Filters.your(self), Filters.or(Filters.starship, Filters.vehicle),
                Filters.participatingInBattle, Filters.movableAsMoveAway(playerId, false, 0, Filters.any));

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.and(Filters.system_or_sector, Filters.canBeTargetedBy(self)))
                && GameConditions.canTarget(game, self, starshipVehicleFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Move starships and vehicles away");
            // Choose target(s)
            action.appendTargeting(
                    new TargetAllCardsAtSameLocationEffect(action, playerId, "Choose starships and vehicles to move away", starshipVehicleFilter) {
                        @Override
                        protected boolean getUseShortcut() {
                            return true;
                        }
                        @Override
                        protected void cardsTargeted(final int targetGroupId1, Collection<PhysicalCard> targetedStarshipsAndVehicles) {
                            action.addAnimationGroup(targetedStarshipsAndVehicles);
                            // Set secondary target filter(s)
                            action.addSecondaryTargetFilter(Filters.battleLocation);
                            // Allow response(s)
                            action.allowResponses("Move all starships and vehicles away",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the final targeted card(s)
                                            Collection<PhysicalCard> finalStarshipsAndVehicles = action.getPrimaryTargetCards(targetGroupId1);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new MoveCardsAwayEffect(action, playerId, Filters.in(finalStarshipsAndVehicles)));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}