package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Objective
 * Title: Mind What You Have Learned / Save You It Can
 */
public class Card7_138_BACK extends AbstractObjective {
    public Card7_138_BACK() {
        super(Side.LIGHT, 7, Title.Save_You_It_Can);
        setGameText("Immediately retrieve 10 Force and place destiny card from Jedi Test #5 on that Jedi Test. While this side up, during your move phase, may use 3 Force to take Luke into hand from a location you control (cards on Luke go to owner's Used Pile). Luke's Jedi Test are suspended (not lost) whenever Luke not on table. Luke may ignore location deployment restrictions. Opponent may not play Sense or Alter. Place out of play if you Force drain at Dagobah or if Luke is placed out of play. Cancel Luke's Jedi Tests.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.cardFlipped(game, effectResult, self)) {
            int amountToRetrieve = 10;

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve 10 Force");
            action.setActionMsg("Have " + playerId + " retrieve " + amountToRetrieve + " Force");
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, amountToRetrieve));
            return Collections.singletonList(action);
        }

        // Check condition(s)
        final Filter lukeOrLeia = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.MIND_WHAT_YOU_HAVE_LEARNED_SAVE_YOU_IT_CAN__TARGETS_LEIA_INSTEAD_OF_LUKE) ? Filters.Leia : Filters.Luke;
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, Filters.Dagobah_location)
                || TriggerConditions.justPlacedOutOfPlayFromTable(game, effectResult, lukeOrLeia)) {
            Collection<PhysicalCard> jediTests = Filters.filterAllOnTable(game, Filters.jediTestTargetingApprentice(lukeOrLeia));

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play");
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            action.appendEffect(
                    new CancelCardsOnTableEffect(action, jediTests));
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        boolean targetsLeiaInsteadOfLuke = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.MIND_WHAT_YOU_HAVE_LEARNED_SAVE_YOU_IT_CAN__TARGETS_LEIA_INSTEAD_OF_LUKE);
        final Filter filter = Filters.and(targetsLeiaInsteadOfLuke ? Filters.Leia : Filters.Luke, Filters.at(Filters.controls(playerId)));

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if ((GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.MOVE)
                || (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.DEPLOY)
                    && GameConditions.hasGameTextModification(game, self, ModifyGameTextType.SAVE_YOU_IT_CAN__MOVE_PHASE_MAY_BE_TREATED_AS_DEPLOY_PHASE)))
                && GameConditions.canUseForce(game, playerId, 3)
                && GameConditions.canTarget(game, self, filter)) {
            String lukeOrLeiaText = targetsLeiaInsteadOfLuke ? "Leia" : "Luke";

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take " + lukeOrLeiaText + " into hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose " + lukeOrLeiaText + " to take into hand", filter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 3));
                            // Allow response(s)
                            action.allowResponses("Take " + GameUtils.getCardLink(targetedCard) + " into hand",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ReturnCardToHandFromTableEffect(action, targetedCard, Zone.USED_PILE));
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
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Condition targetsLeiaInsteadOfLuke = new GameTextModificationCondition(self, ModifyGameTextType.MIND_WHAT_YOU_HAVE_LEARNED_SAVE_YOU_IT_CAN__TARGETS_LEIA_INSTEAD_OF_LUKE);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.Jedi_Test_5, ModifyGameTextType.IT_IS_THE_FUTURE_YOU_SEE__STACK_DESTINY_CARD_ON_JEDI_TEST_5));
        modifiers.add(new JediTestSuspendedInsteadOfLostModifier(self, Filters.jediTestTargetingApprentice(Filters.Luke), new NotCondition(targetsLeiaInsteadOfLuke)));
        modifiers.add(new JediTestSuspendedInsteadOfLostModifier(self, Filters.jediTestTargetingApprentice(Filters.Leia), targetsLeiaInsteadOfLuke));
        modifiers.add(new IgnoresLocationDeploymentRestrictionsWhenDeployingToLocationModifier(self, Filters.Luke, new NotCondition(targetsLeiaInsteadOfLuke), Filters.any, true));
        modifiers.add(new IgnoresLocationDeploymentRestrictionsWhenDeployingToLocationModifier(self, Filters.Leia, targetsLeiaInsteadOfLuke, Filters.any, true));
        modifiers.add(new MayNotPlayModifier(self, Filters.or(Filters.Sense, Filters.Alter), opponent));
        return modifiers;
    }
}