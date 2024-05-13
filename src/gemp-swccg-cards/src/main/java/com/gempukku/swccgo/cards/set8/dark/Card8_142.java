package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Used
 * Title: Endor Occupation
 */
public class Card8_142 extends AbstractUsedInterrupt {
    public Card8_142() {
        super(Side.DARK, 5, Title.Endor_Occupation, Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLore("Each planet controlled by the Empire drains potential resources from the Rebel Alliance.");
        setGameText("Cancel Tatooine Celebration, Cloud City Celebration or Coruscant Celebration. (Immune to Sense.) OR During your activate phase, activate 1 Force for each Endor battleground you occupy.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Tatooine_Celebration)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Tatooine_Celebration, Title.Tatooine_Celebration);
            action.setImmuneTo(Title.Sense);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Cloud_City_Celebration)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Cloud_City_Celebration, Title.Cloud_City_Celebration);
            action.setImmuneTo(Title.Sense);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Coruscant_Celebration)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Coruscant_Celebration, Title.Coruscant_Celebration);
            action.setImmuneTo(Title.Sense);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.ACTIVATE)
                && GameConditions.canActivateForce(game, playerId)) {
            int numToActivate = Filters.countTopLocationsOnTable(game, Filters.and(Filters.Endor_location, Filters.battleground, Filters.occupies(playerId)));
            if (numToActivate > 0) {
                final int maxForceToActivate = Math.min(numToActivate, game.getGameState().getReserveDeckSize(playerId));
                if (maxForceToActivate > 0) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self);
                    action.setText("Activate " + maxForceToActivate + " Force");
                    // Allow response(s)
                    action.allowResponses(
                            new RespondablePlayCardEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    // Perform result(s)
                                    action.appendEffect(
                                            new ActivateForceEffect(action, playerId, maxForceToActivate));
                                }
                            }
                    );
                    actions.add(action);
                }
            }
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Tatooine_Celebration, Filters.Cloud_City_Celebration, Filters.Coruscant_Celebration))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.setImmuneTo(Title.Sense);
            actions.add(action);
        }
        return actions;
    }
}