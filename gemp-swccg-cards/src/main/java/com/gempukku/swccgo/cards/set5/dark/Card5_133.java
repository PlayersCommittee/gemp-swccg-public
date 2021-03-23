package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.effects.MayNotUseAbilityTowardDrawingBattleDestinyUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Lost
 * Title: Brief Loss Of Control
 */
public class Card5_133 extends AbstractLostInterrupt {
    public Card5_133() {
        super(Side.DARK, 3, "Brief Loss Of Control", Uniqueness.UNIQUE);
        setLore("Eliciting fear from the opponent gives the dark side a powerful advantage.");
        setGameText("Just after the weapons phase of a battle, use 2 Force to cause each player to target one of that player's characters involved in the battle. Target characters cannot apply their ability toward drawing battle destiny this turn.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        final Filter yourCharacterFilter = Filters.and(Filters.your(self), Filters.character, Filters.participatingInBattle);
        final Filter opponentsCharacterFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.participatingInBattle);

        // Check condition(s)
        if (TriggerConditions.justAfterWeaponsSegmentOfBattle(game, effectResult)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)
                && GameConditions.canTarget(game, self, yourCharacterFilter)
                && GameConditions.canTarget(game, self, opponentsCharacterFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target characters");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character to not apply ability toward drawing battle destiny", yourCharacterFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard yourCharacter) {
                            // After this point do not allow action to be aborted
                            action.setAllowAbort(false);
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, opponent, "Choose character to not apply ability toward drawing battle destiny", opponentsCharacterFilter) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId2, PhysicalCard opponentsCharacter) {
                                            action.addAnimationGroup(yourCharacter, opponentsCharacter);
                                            // Pay cost(s)
                                            action.appendCost(
                                                    new UseForceEffect(action, playerId, 2));
                                            // Allow response(s)
                                            action.allowResponses("Prevent " + GameUtils.getCardLink(yourCharacter) + " and " + GameUtils.getCardLink(opponentsCharacter) + " from applying ability toward drawing battle destiny",
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            final PhysicalCard yourFinalTarget = targetingAction.getPrimaryTargetCard(targetGroupId1);
                                                            final PhysicalCard opponentsFinalTarget = targetingAction.getPrimaryTargetCard(targetGroupId2);

                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new MayNotUseAbilityTowardDrawingBattleDestinyUntilEndOfTurnEffect(action, Arrays.asList(yourFinalTarget, opponentsFinalTarget)));
                                                        }
                                                    }
                                            );
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