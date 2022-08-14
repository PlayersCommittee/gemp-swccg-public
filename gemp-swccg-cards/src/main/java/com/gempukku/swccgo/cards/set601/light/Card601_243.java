package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.MayNotTargetToBeCapturedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotTargetToBeLostModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToCaptureCardResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Block 2
 * Type: Interrupt
 * Subtype: Used
 * Title: Put That Down (V)
 */
public class Card601_243 extends AbstractUsedInterrupt {
    public Card601_243() {
        super(Side.LIGHT, 4, "Put That Down", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Lord Vader interceded between the trigger happy mercenary and his target.");
        setGameText("If your character or starship is about to be captured or lost during the weapons segment of a battle, it is hit instead and may not be captured or lost until the damage segment. OR If opponent just played an Interrupt during battle, activate 2 Force.");
        addIcons(Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_2);
        setAsLegacy(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        Filter yourCharacterOrStarship = Filters.and(Filters.your(self), Filters.or(Filters.character, Filters.starship), Filters.canBeTargetedBy(self, TargetingReason.TO_BE_HIT));
        PreventableCardEffect cardEffect = null;
        PhysicalCard cardToBeLostOrCaptured = null;

        // This only works during the weapon's segment of the battle.
        // Although the conditional is not 100% correct, there isn't any other way for a character
        // to be lost in a battle unless in either the weapon or damage segment of the battle (nothing else
        // would allow top-level actions). This is far easier than adding a new "isWeaponSegmentOfBattle" to the code.
        if (!GameConditions.isDuringBattle(game) && !GameConditions.isDamageSegmentOfBattle(game)) {
            return null;
        }
        if (game.getGameState().getBattleState().isReachedPowerSegment()) {
            return null;
        }

        if (TriggerConditions.isAboutToBeLost(game, effectResult, yourCharacterOrStarship)) {
            final AboutToLoseCardFromTableResult result = (AboutToLoseCardFromTableResult) effectResult;
            cardEffect = result.getPreventableCardEffect();
            cardToBeLostOrCaptured = result.getCardToBeLost();
        }
        else if (TriggerConditions.isAboutToBeCaptured(game, effectResult, yourCharacterOrStarship)) {
            final AboutToCaptureCardResult result = (AboutToCaptureCardResult) effectResult;
            cardEffect = result.getPreventableCardEffect();
            cardToBeLostOrCaptured = result.getCardToBeCaptured();
        }

        if (cardEffect != null && cardToBeLostOrCaptured != null) {
            final PreventableCardEffect preventableCardEffect = cardEffect;

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("'Hit' " + GameUtils.getFullName(cardToBeLostOrCaptured) + " instead");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose target", TargetingReason.TO_BE_HIT, cardToBeLostOrCaptured) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("'Hit' " + GameUtils.getFullName(targetedCard) + " instead",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            preventableCardEffect.preventEffectOnCard(finalTarget);
                                            action.appendEffect(
                                                    new AddUntilDamageSegmentOfBattleModifierEffect(action,
                                                            new MayNotTargetToBeCapturedModifier(self, finalTarget),
                                                            "Makes " + GameUtils.getCardLink(finalTarget) + " not able to be captured"));
                                            action.appendEffect(
                                                    new AddUntilDamageSegmentOfBattleModifierEffect(action,
                                                            new MayNotTargetToBeLostModifier(self, finalTarget),
                                                            "Makes " + GameUtils.getCardLink(finalTarget) + " not able to be lost"));
                                            action.appendEffect(
                                                    new HitCardEffect(action, finalTarget, self));
                                        }
                                    }
                            );
                        }
                        @Override
                        protected boolean getUseShortcut() {
                            return true;
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, opponent, Filters.Interrupt)
                && GameConditions.isDuringBattle(game)
                && GameConditions.canActivateForce(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Activate 2 Force");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ActivateForceEffect(action, playerId, 2));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}