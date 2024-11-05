package com.gempukku.swccgo.cards.set302.dark;

import com.gempukku.swccgo.cards.AbstractMobileEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.MoveMobileEffectEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.MovementDirection;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseStartingDirectionEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dark Jedi Brotherhood Core
 * Type: Effect
 * Title: Crystal Mist
 */
public class Card302_038 extends AbstractMobileEffect {
    public Card302_038() {
        super(Side.LIGHT, 4, Title.Crystal_Mist, Uniqueness.RESTRICTED_2, ExpansionSet.DJB_CORE, Rarity.V);
        setLore("The Children of Mortis spread their terror throughout the systems of the Brotherhood in the form of their crystal mist. People infected had an unfortunate habit of mutating into mindless beasts.");
        setGameText("Deploy on an exterior site. Specify starting direction. During your control phase, moves to next adjacent exterior site (reversing direction as necessary). During battle here, if your Crystal Creature or Children of Mortis character here, may add one battle destiny.");
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.exterior_site;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.skipInitialMessageAndAnimation();
            action.setText("Choose starting direction");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new ChooseStartingDirectionEffect(action, playerId, self));
            return Collections.singletonList(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        // Check if reached end of owner's control phase and Effect was not moved yet.
        if (TriggerConditions.isEndOfYourPhase(game, self, effectResult, Phase.CONTROL)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && !GameConditions.mayNotMove(game, self)) {

            // Determine next site and movement direction
            final PhysicalCard currentSite = self.getAttachedTo();
            MovementDirection nextMovementDirection = self.getMovementDirection();
            PhysicalCard nextLocation = game.getGameState().getLocationFromMovementDirection(currentSite, nextMovementDirection);
            if (nextLocation == null || !Filters.and(Filters.adjacentSite(currentSite), Filters.exterior_site).accepts(game, nextLocation)) {
                nextMovementDirection = nextMovementDirection.getReversedDirection();
                nextLocation = game.getGameState().getLocationFromMovementDirection(currentSite, nextMovementDirection);
                if (nextLocation != null && !Filters.and(Filters.adjacentSite(currentSite), Filters.exterior_site).accepts(game, nextLocation)) {
                    nextLocation = null;
                }
            }
            if (nextLocation != null) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setPerformingPlayer(playerId);
                action.setText("Move to next site");
                action.setActionMsg("Move " + GameUtils.getCardLink(self) + " to next site");
                // Perform result(s)
                action.appendEffect(
                        new MoveMobileEffectEffect(action, self, nextLocation, nextMovementDirection));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && !GameConditions.mayNotMove(game, self)) {

            // Determine next site and movement direction
            final PhysicalCard currentSite = self.getAttachedTo();
            MovementDirection nextMovementDirection = self.getMovementDirection();
            PhysicalCard nextLocation = game.getGameState().getLocationFromMovementDirection(currentSite, nextMovementDirection);
            if (nextLocation == null || !Filters.and(Filters.adjacentSite(currentSite), Filters.exterior_site).accepts(game, nextLocation)) {
                nextMovementDirection = nextMovementDirection.getReversedDirection();
                nextLocation = game.getGameState().getLocationFromMovementDirection(currentSite, nextMovementDirection);
                if (nextLocation != null && !Filters.and(Filters.adjacentSite(currentSite), Filters.exterior_site).accepts(game, nextLocation)) {
                    nextLocation = null;
                }
            }
            if (nextLocation != null) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Move to next site");
                action.setActionMsg("Move " + GameUtils.getCardLink(self) + " to next site");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new MoveMobileEffectEffect(action, self, nextLocation, nextMovementDirection));
                actions.add(action);
            }
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringBattleAt(game, Filters.here(self))
                && (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.or(Filters.Crystal_Creature, Filters.Children_Of_Mortis)))
				|| GameConditions.isPresentWith(game, self, Filters.Crystal_Creature))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Add one battle destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new AddBattleDestinyEffect(action, 1));
            actions.add(action);
        }
        return actions;
    }
}