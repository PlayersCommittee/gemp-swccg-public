package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
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
 * Title: Endor Celebration
 */
public class Card8_046 extends AbstractUsedInterrupt {
    public Card8_046() {
        super(Side.LIGHT, 5, Title.Endor_Celebration, Uniqueness.UNIQUE);
        setLore("The Rebel presence on Endor meant that the Ewoks would be able to live free from the Empire's tyranny.");
        setGameText("Cancel Tatooine Occupation, Cloud City Occupation or Rebel Base Occupation. (Immune to Sense.) OR During your activate phase, activate 1 Force for each Endor battleground you occupy.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Tatooine_Occupation)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Tatooine_Occupation, Title.Tatooine_Occupation);
            action.setImmuneTo(Title.Sense);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Cloud_City_Occupation)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Cloud_City_Occupation, Title.Cloud_City_Occupation);
            action.setImmuneTo(Title.Sense);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Rebel_Base_Occupation)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Rebel_Base_Occupation, Title.Rebel_Base_Occupation);
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
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Tatooine_Occupation, Filters.Cloud_City_Occupation, Filters.Rebel_Base_Occupation))
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