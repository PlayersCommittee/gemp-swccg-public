package com.gempukku.swccgo.cards.set207.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.AddToForceDrainEffect;
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
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 7
 * Type: Interrupt
 * Subtype: Used
 * Title: I Think I Can Handle Myself
 */
public class Card207_013 extends AbstractUsedInterrupt {
    public Card207_013() {
        super(Side.LIGHT, 4, "I Think I Can Handle Myself", Uniqueness.UNIQUE);
        setGameText("If your female character is defending a battle alone at a site, add one destiny to total power (if Rey, she is also immune to attrition). OR Cancel a weapon destiny targeting your female character. OR Add 1 to your Force drain where you have a female character.");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_7);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Filter targetFilter = Filters.and(Filters.your(self), Filters.female, Filters.defendingBattle, Filters.alone);

        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.site)
                && GameConditions.canAddDestinyDrawsToPower(game, playerId)
                && GameConditions.canTarget(game, self, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add one destiny to total power");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose female character", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            String msgText = "Add one destiny to total power";
                            if (Filters.Rey.accepts(game, targetedCard)) {
                                msgText += (" and make " + GameUtils.getCardLink(targetedCard) + " immune to attrition");
                            }
                            // Allow response(s)
                            action.allowResponses(msgText,
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new AddDestinyToTotalPowerEffect(action, 1));
                                            if (Filters.Rey.accepts(game, finalTarget)) {
                                                action.appendEffect(
                                                        new AddUntilEndOfBattleModifierEffect(action,
                                                                new ImmuneToAttritionModifier(self, finalTarget),
                                                                "Makes " + GameUtils.getCardLink(finalTarget) + " immune to attrition"));
                                            }
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
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        Filter yourFemaleCharacter = Filters.and(Filters.your(self), Filters.female, Filters.character);

        // Check condition(s)
        if (TriggerConditions.isWeaponDestinyJustDrawnTargeting(game, effectResult, Filters.any, yourFemaleCharacter)
                && GameConditions.canCancelDestiny(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel weapon destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelDestinyEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedAt(game, effectResult, Filters.sameLocationAs(self, yourFemaleCharacter))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add 1 to Force drain");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddToForceDrainEffect(action, 1));
                        }
                    }
            );
            return Collections.singletonList(action);
        }

        return null;
    }
}