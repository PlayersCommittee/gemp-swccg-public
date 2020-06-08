package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.MoveAsReactEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyLandspeedUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used
 * Title: Dash
 */
public class Card5_043 extends AbstractUsedInterrupt {
    public Card5_043() {
        super(Side.LIGHT, 4, "Dash", Uniqueness.UNIQUE);
        setLore("See Luke run. Run Luke, run!");
        setGameText("Use 2 Force to increase a character's or creature vehicle's landspeed by 1 for remainder of turn. OR Use 3 Force to move one of your characters as a 'react' (free if character is a scout).");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.or(Filters.character, Filters.creature_vehicle);

        // Check condition(s)
        if (GameConditions.canTarget(game, self, filter)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add 1 to landspeed");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character or creature vehicle", filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 2));
                            // Allow response(s)
                            action.allowResponses("Add 1 to " + GameUtils.getCardLink(targetedCard) + "'s landspeed",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ModifyLandspeedUntilEndOfTurnEffect(action, finalTarget, 1));
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

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent)
                || TriggerConditions.battleInitiated(game, effectResult, opponent)) {
            boolean scoutOnly = !GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 3);
            Filter characterFilter;
            if (scoutOnly) {
                characterFilter = Filters.and(Filters.your(self), Filters.character, Filters.scout, Filters.canMoveAsReactAsActionFromOtherCard(self, true, 0, false));
            }
            else {
                characterFilter = Filters.and(Filters.your(self), Filters.character, Filters.canMoveAsReactAsActionFromOtherCard(self, true, 0, false));
            }
            if (GameConditions.canTarget(game, self, characterFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Move character as 'react'");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character", characterFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                if (!Filters.scout.accepts(game, targetedCard)) {
                                    // Pay cost(s)
                                    action.appendCost(
                                            new UseForceEffect(action, playerId, 3));
                                }
                                // Allow response(s)
                                action.allowResponses("Move " + GameUtils.getCardLink(targetedCard) + " as a 'react'",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                PhysicalCard finalCharacter = action.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new MoveAsReactEffect(action, finalCharacter, true));
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