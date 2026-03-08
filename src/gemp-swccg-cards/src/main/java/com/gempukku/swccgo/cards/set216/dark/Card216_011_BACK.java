package com.gempukku.swccgo.cards.set216.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Objective 
 * Title: On The Verge Of Greatness / Taking Control Of The Weapon
 */
public class Card216_011_BACK extends AbstractObjective {
    public Card216_011_BACK() {
        super(Side.DARK, 7, Title.Taking_Control_Of_The_Weapon, ExpansionSet.SET_16, Rarity.V);
        setGameText("While this side up, A Bright Center To The Universe cancels opponent's Force drain modifiers everywhere. Vader may make a regular move to a battle just initiated. At Death Star, system it orbits, and sites related to either, your total battle destiny is +1 (+2 if your non-unique card with ability in battle). During your draw phase, may retrieve a non-unique card with ability. Flip this card if you do not have a leader at a Scarif battleground site. Place out of play if Death Star or Shield Gate not on table.");
        addIcons(Icon.VIRTUAL_SET_16);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();

        Filter battleDestinyBonusSystems = Filters.or(Filters.Death_Star_system, Filters.isOrbitedBy(Filters.Death_Star_system));
        Filter battleDestinyBonusSites = Filters.and(Filters.site, Filters.relatedSiteTo(self, battleDestinyBonusSystems));
        Filter battleDestinyBonusLocations = Filters.or(battleDestinyBonusSystems, battleDestinyBonusSites);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.A_Bright_Center_To_The_Universe, ModifyGameTextType.A_BRIGHT_CENTER_TO_THE_UNIVERSE__CANCELS_OPPONENTS_FORCE_DRAIN_MODIFIERS_EVERYWHERE));
        modifiers.add(new TotalBattleDestinyModifier(self, battleDestinyBonusLocations,
                new ConditionEvaluator(1, 2, new InBattleCondition(self, Filters.and(Filters.your(playerId), Filters.non_unique, Filters.hasAbilityOrHasPermanentPilotWithAbility))), playerId));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();
        
        Filter vaderFilter = Filters.and(Filters.Vader, Filters.movableAsRegularMove(playerId, false, 0, false, Filters.locationAndCardsAtLocation(Filters.battleLocation)));

        if (TriggerConditions.battleInitiated(game, effectResult)
                && GameConditions.canTarget(game, self, vaderFilter)) {
            final PhysicalCard battleLocation = game.getGameState().getBattleLocation();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(playerId);
            action.setText("Move Vader to the battle");
            action.setActionMsg("Move Vader as a regular move to a battle just initiated");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Vader", vaderFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.addAnimationGroup(battleLocation);
                            // Allow response(s)
                            action.allowResponses("Move " + GameUtils.getCardLink(targetedCard) + " to " + GameUtils.getCardLink(battleLocation),
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new MoveCardAsRegularMoveEffect(action, playerId, finalTarget, false, false, Filters.locationAndCardsAtLocation(Filters.battleLocation)));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.ON_THE_VERGE_OF_GREATNESS__DEPLOY_SCARIF_BATTLEGROUND;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a Scarif battleground");
            action.setActionMsg("Deploy a Scarif battleground from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Scarif_location, Filters.battleground), true));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.TAKING_CONTROL_OF_THE_WEAPON__RETRIEVE_NON_UNIQUE_CARD_WITH_ABILITY;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DRAW)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve a non-unique card with ability");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardEffect(action, playerId, Filters.and(Filters.non_unique, Filters.hasAbilityOrHasPermanentPilotWithAbility)));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && (!GameConditions.canSpot(game, self, Filters.Death_Star_system)
                || !GameConditions.canSpot(game, self, Filters.Shield_Gate))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play");
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            return Collections.singletonList(action);
        }

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.your(self), Filters.leader, Filters.at(Filters.Scarif_battleground_site)))) {

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
