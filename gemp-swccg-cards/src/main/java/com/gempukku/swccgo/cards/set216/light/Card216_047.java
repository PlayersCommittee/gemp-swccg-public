package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.CancelTargetingEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnForCardTitleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.TargetingActionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Set 16
 * Type: Interrupt
 * Subtype: Used
 * Title: Wookiee Roar (V)
 */
public class Card216_047 extends AbstractUsedInterrupt {
    public Card216_047() {
        super(Side.LIGHT, 3, Title.Wookiee_Roar, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("'GHRRRRAARRRRHG!'");
        setGameText("When drawn for destiny, once per turn, choose one of your Wookiees to be power +1 for remainder of turn. " +
                "If a battle was just initiated involving your Wookiee, add one destiny to total power. OR Cancel an attempt by opponent to target your Wookiee to be lost or captured.");
        addIcons(Icon.A_NEW_HOPE, Icon.EPISODE_I, Icon.VIRTUAL_SET_16);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredDrawnAsDestinyTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.WOOKIEE_ROAR_V__ADD_POWER;

        if (GameConditions.isDestinyCardMatchTo(game, self)
                && GameConditions.isOncePerTurnForCardTitle(game, self, gameTextActionId)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.Wookiee, Filters.canBeTargetedBy(self)))) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Add 1 to power of your Wookiee");
            action.setActionMsg("Choose your Wookiee to be power +1 for remainder of turn");
            action.appendUsage(new OncePerTurnForCardTitleEffect(action));
            action.addAnimationGroup(self);
            action.appendTargeting(new TargetCardOnTableEffect(action, self.getOwner(), "Choose your Wookiee to be power +1 for remainder of turn",
                    Filters.and(Filters.your(self), Filters.Wookiee, Filters.canBeTargetedBy(self))) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    action.allowResponses(new RespondableEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                            action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                                    new PowerModifier(self, finalTarget, 1)
                                    , GameUtils.getCardLink(finalTarget) + " is power +1 for remainder of turn"));
                        }
                    });
                }
            });
            return Collections.singletonList(action);
        }

        return null;
    }

    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.Wookiee))
                && GameConditions.canAddDestinyDrawsToPower(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add one destiny to power");
            // Allow response(s)
            action.allowResponses("Add one destiny to power",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new AddDestinyToTotalPowerEffect(action, 1, playerId));
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
        Filter filter = Filters.and(Filters.your(self), Filters.Wookiee, Filters.canBeTargetedBy(self));
        Collection<TargetingReason> targetingReasons = Arrays.asList(TargetingReason.TO_BE_LOST, TargetingReason.TO_BE_CAPTURED, TargetingReason.TO_BE_CHOKED);

        // Check condition(s)
        if (TriggerConditions.isTargetedForReason(game, effect, opponent, filter, targetingReasons)) {
            final RespondableEffect respondableEffect = (RespondableEffect) effect;
            final List<PhysicalCard> cardsTargeted = TargetingActionUtils.getCardsTargetedForReason(game, respondableEffect.getTargetingAction(), targetingReasons, filter);
            if (!cardsTargeted.isEmpty()) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Cancel targeting");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Wookiee", Filters.in(cardsTargeted)) {
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard wookieeTargeted) {
                                action.addAnimationGroup(wookieeTargeted);
                                // Allow response(s)
                                action.allowResponses("Cancel targeting of " + GameUtils.getCardLink(wookieeTargeted),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new CancelTargetingEffect(action, respondableEffect));
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