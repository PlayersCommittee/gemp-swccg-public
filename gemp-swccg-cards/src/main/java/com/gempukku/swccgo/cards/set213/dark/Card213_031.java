package com.gempukku.swccgo.cards.set213.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.cards.conditions.DuringBattleWithParticipantCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Objective
 * Title: Hunt Down And Destroy The Jedi / Their Fire Has Gone Out Of The Universe (V)
 */
public class Card213_031 extends AbstractObjective {
    public Card213_031() {
        super(Side.DARK, 0, Title.Hunt_Down_And_Destroy_The_Jedi);
        setFrontOfDoubleSidedCard(true);
        setVirtualSuffix(true);
        setGameText("Deploy Vader's Castle and [Set 13] Visage Of The Emperor. " +
                "For remainder of game, you may not deploy characters except bounty hunters, droids, and Imperials. Where you have an Inquisitor, your total battle destiny is +1 (+2 if with a 'Hatred' card). Inquisitors are destiny +2. " +
                "While this side up, once per turn, may deploy a [Cloud City] or Malachor battleground site from Reserve Deck; reshuffle. " +
                "Flip this card if Vader is at a battleground site unless Luke, a Jedi, or a Padawan at a battleground site.");
        addIcons(Icon.SPECIAL_EDITION, Icon.VIRTUAL_SET_13);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.title(Title.Vaders_Castle), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Vader's Castle to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Visage_Of_The_Emperor, Filters.icon(Icon.VIRTUAL_SET_13)), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Visage Of The Emperor to deploy";
                    }
                });
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.HUNT_DOWN_V__DOWNLOAD_LOCATION;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a location from Reserve Deck");
            action.setActionMsg("Deploy a location from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.battleground_site, Filters.or(Filters.partOfSystem(Title.Malachor), Filters.icon(Icon.CLOUD_CITY))), true));
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotPlayModifier(self, Filters.and(Filters.character, Filters.not(Filters.or(Filters.droid, Filters.Imperial, Filters.bounty_hunter))), self.getOwner()));
        modifiers.add(new DestinyModifier(self, Filters.inquisitor, 2));
        modifiers.add(new TotalBattleDestinyModifier(self, Filters.sameLocationAs(self, Filters.inquisitor), new ConditionEvaluator(1, 2, new OrCondition(new DuringBattleAtCondition(Filters.hasStacked(Filters.hatredCard)), new DuringBattleWithParticipantCondition(Filters.hasStacked(Filters.hatredCard)))), self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.HUNT_DOWN_V__TAKE_VADER_INTO_HAND;
        if (TriggerConditions.cardFlipped(game, effectResult, self)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canSpot(game, self, Filters.and(Filters.site, Filters.controlsWith(playerId, self, Filters.Vader)))) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Vader into Hand");
            action.setActionMsg("Take Vader into Hand");
            action.appendUsage(
                    new OncePerGameEffect(action)
            );
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Vader to take into hand", Filters.Vader) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Take " + GameUtils.getCardLink(targetedCard) + " into hand",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ReturnCardToHandFromTableEffect(action, targetedCard, Zone.HAND, Zone.LOST_PILE));
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
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.Vader, Filters.at(Filters.battleground_site)))
                && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.opponents(self), Filters.or(Filters.Jedi, Filters.padawan, Filters.Luke), Filters.at(Filters.battleground_site)))) {

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
