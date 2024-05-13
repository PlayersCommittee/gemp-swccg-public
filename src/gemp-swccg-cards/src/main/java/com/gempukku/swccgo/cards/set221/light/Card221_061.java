package com.gempukku.swccgo.cards.set221.light;

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
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ArmorModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.TargetingActionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Interrupt
 * Subtype: Used
 * Title: Generation One Armor Always Holds Up
 */
public class Card221_061 extends AbstractUsedInterrupt {
    public Card221_061() {
        super(Side.LIGHT, 4, "Generation One Armor Always Holds Up", Uniqueness.UNRESTRICTED, ExpansionSet.SET_21, Rarity.V);
        setGameText("Your clones are armor +2 for remainder of turn. OR Cancel You Are Beaten. OR If your clone in battle, cancel an attempt by opponent to target your character to be lost. [Immune to Sense.] OR If you just drew a clone for destiny, add 1 to that destiny draw.");
        addIcons(Icon.EPISODE_I, Icon.CLONE_ARMY, Icon.VIRTUAL_SET_21);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        if (GameConditions.canTarget(game, self, Filters.and(Filters.your(self), Filters.clone))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add 2 to armor of your clones");
            action.setImmuneTo(Title.Sense);

            // Allow response(s)
            action.allowResponses(null,
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddUntilEndOfTurnModifierEffect(action, new ArmorModifier(self, Filters.and(Filters.your(self), Filters.clone), 2), "Makes your clones armor +2"));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.You_Are_Beaten)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.You_Are_Beaten, Title.You_Are_Beaten);
            action.setImmuneTo(Title.Sense);
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.You_Are_Beaten)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.setImmuneTo(Title.Sense);
            actions.add(action);
        }


        String opponent = game.getOpponent(playerId);
        Filter filter = Filters.and(Filters.your(self), Filters.character, Filters.canBeTargetedBy(self));
        Collection<TargetingReason> targetingReasons = Arrays.asList(TargetingReason.TO_BE_LOST, TargetingReason.TO_BE_CHOKED);

        // Check condition(s)
        if (TriggerConditions.isTargetedForReason(game, effect, opponent, filter, targetingReasons)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.clone))) {
            final RespondableEffect respondableEffect = (RespondableEffect) effect;
            final List<PhysicalCard> cardsTargeted = TargetingActionUtils.getCardsTargetedForReason(game, respondableEffect.getTargetingAction(), targetingReasons, filter);
            if (!cardsTargeted.isEmpty()) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Cancel targeting");
                action.setImmuneTo(Title.Sense);

                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character", Filters.in(cardsTargeted)) {
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard characterTargeted) {
                                action.addAnimationGroup(characterTargeted);
                                // Allow response(s)
                                action.allowResponses("Cancel targeting of " + GameUtils.getCardLink(characterTargeted),
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
                actions.add(action);
            }
        }

        return actions;
    }


    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.isDestinyCardMatchTo(game, Filters.clone)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add 1 to destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ModifyDestinyEffect(action, 1));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}