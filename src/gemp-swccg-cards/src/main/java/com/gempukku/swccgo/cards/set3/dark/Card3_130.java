package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelTargetingEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.TurnOffBinaryDroidUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeStolenModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotTargetToBeHitModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotTargetToBeLostModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.TargetingActionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Used
 * Title: Oh, Switch Off
 */
public class Card3_130 extends AbstractUsedInterrupt {
    public Card3_130() {
        super(Side.DARK, 6, Title.Oh_Switch_Off, Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.C2);
        setLore("Mindless philosophy for an overweight glob of grease.");
        setGameText("Cancel an attempt by opponent to target your droid to be stolen, 'hit' or lost. Droid is protected from all such attempts for remainder of turn. OR Switch OFF any binary droid for remainder of turn.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);
        Filter filter = Filters.and(Filters.your(self), Filters.droid);
        Collection<TargetingReason> targetingReasons = Arrays.asList(TargetingReason.TO_BE_STOLEN, TargetingReason.TO_BE_HIT, TargetingReason.TO_BE_LOST);

        // Check condition(s)
        if (TriggerConditions.isTargetedForReason(game, effect, opponent, filter, targetingReasons)) {
            final RespondableEffect respondableEffect = (RespondableEffect) effect;
            final List<PhysicalCard> cardsTargeted = TargetingActionUtils.getCardsTargetedForReason(game, respondableEffect.getTargetingAction(), targetingReasons, filter);

            // make sure the targeting action isn't immune to this card
            if (respondableEffect.getTargetingAction() == null
                    || !respondableEffect.getTargetingAction().isImmuneTo(self.getTitle())) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Cancel targeting");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new CancelTargetingEffect(action, respondableEffect));
                                action.appendEffect(
                                        new AddUntilEndOfTurnModifierEffect(action,
                                                new MayNotBeStolenModifier(self, Filters.in(cardsTargeted)), null));
                                action.appendEffect(
                                        new AddUntilEndOfTurnModifierEffect(action,
                                                new MayNotTargetToBeHitModifier(self, Filters.in(cardsTargeted)), null));
                                action.appendEffect(
                                        new AddUntilEndOfTurnModifierEffect(action,
                                                new MayNotTargetToBeLostModifier(self, Filters.in(cardsTargeted)), null));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.binary_droid;

        // Check condition(s)
        if (GameConditions.canTarget(game, self, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Turn off binary droid");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose binary droid", filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Turn off " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new TurnOffBinaryDroidUntilEndOfTurnEffect(action, finalTarget));
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