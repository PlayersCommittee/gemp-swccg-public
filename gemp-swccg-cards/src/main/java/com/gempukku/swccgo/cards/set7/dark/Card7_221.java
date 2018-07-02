package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractMobileEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.MoveMobileEffectEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
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
import com.gempukku.swccgo.logic.effects.choose.ChooseStartingDirectionEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Effect
 * Title: Bantha Herd
 */
public class Card7_221 extends AbstractMobileEffect {
    public Card7_221() {
        super(Side.DARK, 4, "Bantha Herd", Uniqueness.RESTRICTED_2);
        setLore("While banthas are found on many worlds, the largest herds are found on Tatooine. The Sand People of that planet learned to tame the beasts.");
        setGameText("Deploy on an exterior Tatooine site. Specify starting direction. During your control phase, moves to next adjacent site (reversing direction as necessary). During battle, if your tusken raider or bantha here may add one battle destiny.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.exterior_Tatooine_site;
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
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.or(Filters.Tusken_Raider, Filters.bantha)))
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