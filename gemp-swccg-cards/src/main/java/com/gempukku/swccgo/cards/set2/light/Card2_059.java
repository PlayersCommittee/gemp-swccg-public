package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.AttackRunState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfEpicEventModifierEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.AttackRunTotalModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Interrupt
 * Subtype: Used
 * Title: You're All Clear Kid!
 */
public class Card2_059 extends AbstractUsedInterrupt {
    public Card2_059() {
        super(Side.LIGHT, 3, Title.Youre_All_Clear_Kid, Uniqueness.UNIQUE);
        setLore("'Now let's blow this thing and go home!'");
        setGameText("Cancel I'm On The Leader. (Immune to Sense.)  OR  Use 1 Force during an Attack Run.  Move one TIE in Death Star: Trench (your choice) to Death Star system for free.  Add 1 to total of Attack Run if lead starfighter has matching pilot aboard.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Im_On_The_Leader)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Im_On_The_Leader, Title.Im_On_The_Leader);
            action.setImmuneTo(Title.Sense);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Im_On_The_Leader)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.setImmuneTo(Title.Sense);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelAttackRunActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        Filter tieInTrench = Filters.and(Filters.opponents(self), Filters.TIE, Filters.at(Filters.Death_Star_Trench), Filters.canBeRelocatedToLocation(Filters.Death_Star_system, true, 0));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, tieInTrench)) {
            final PhysicalCard deathStarSystem = Filters.findFirstFromTopLocationsOnTable(game, Filters.Death_Star_system);
            if (deathStarSystem != null) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Move TIE out of trench");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose TIE", 1, false, tieInTrench) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Allow response(s)
                                action.allowResponses("Move " + GameUtils.getCardLink(targetedCard) + " to " + GameUtils.getCardLink(deathStarSystem),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                                AttackRunState attackRunState = (AttackRunState) game.getGameState().getEpicEventState();

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new RelocateBetweenLocationsEffect(action, finalTarget, deathStarSystem));
                                                if (Filters.hasMatchingPilotAboard(self).accepts(game, attackRunState.getLeadStarfighter())) {
                                                    action.appendEffect(
                                                            new AddUntilEndOfEpicEventModifierEffect(action,
                                                                    new AttackRunTotalModifier(self, 1), "Adds 1 to Attack Run total"));
                                                }
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}