package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections III
 * Type: Objective
 * Title: Let Them Make The First Move / At Last We Will Have Revenge
 */
public class Card13_073 extends AbstractObjective {
    public Card13_073() {
        super(Side.DARK, 0, Title.Let_Them_Make_The_First_Move, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Theed Palace Generator Core, Theed Palace Generator, and Deep Hatred. While this side up, opponent may not Force drain where opponent has a Jedi, and your Force drains at Naboo sites where you have a Dark Jedi may not be canceled. Once during your control phase, may target one opponent's character present with your Dark Jedi at an interior Theed Palace site. Target is lost. Flip this card if an opponent's Jedi present at an interior Naboo battleground site.");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Theed_Palace_Generator_Core, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Theed Palace Generator Core to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Theed_Palace_Generator, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Theed Palace Generator to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Deep_Hatred, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Deep Hatred to deploy";
                    }
                });
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, Filters.sameLocationAs(self, Filters.and(Filters.opponents(self), Filters.Jedi)), opponent));
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, Filters.and(Filters.Naboo_site, Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.Dark_Jedi))), playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        Filter targetFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.presentWith(self,
                Filters.and(Filters.your(self), Filters.Dark_Jedi, Filters.at(Filters.interior_Theed_Palace_site))));
        if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.LET_THEM_MAKE_THE_FIRST_MOVE__ONLY_TARGET_UNDERCOVER_SPIES_AND_R2D2)) {
            targetFilter = Filters.and(targetFilter, Filters.or(Filters.undercover_spy, Filters.R2D2));
        }
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canTarget(game, self, targetingReason, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Target character");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", targetingReason, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " lost",
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new LoseCardFromTableEffect(action, finalTarget));
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
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.opponents(self),
                Filters.Jedi, Filters.presentAt(Filters.and(Filters.interior_Naboo_site, Filters.battleground_site))))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}