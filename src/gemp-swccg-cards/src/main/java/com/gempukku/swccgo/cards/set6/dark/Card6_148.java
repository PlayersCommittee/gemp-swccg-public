package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractMobileEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.MoveMobileEffectEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.MovementDirection;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetingReason;
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
import com.gempukku.swccgo.logic.effects.GoMissingEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStartingDirectionEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Effect
 * Title: Sandwhirl
 */
public class Card6_148 extends AbstractMobileEffect {
    public Card6_148() {
        super(Side.DARK, 4, Title.Sandwhirl, Uniqueness.DIAMOND_1, ExpansionSet.JABBAS_PALACE, Rarity.U);
        setLore("Called 'Teeth Of The Wind' by Tusken Raiders. Only those familiar with Tatooine's vast deserts can navigate successfully during its furious onslaught.");
        setGameText("Deploy on a desert. Specify starting direction. All characters (except Jawas and Tusken Raiders) present at same non-interior site are missing. During your control phase, moves to next adjacent site (reversing directions as necessary), but lost if at an interior site.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.desert;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

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
            actions.add(action);
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
            if (nextLocation == null || !Filters.adjacentSite(currentSite).accepts(game, nextLocation)) {
                nextMovementDirection = nextMovementDirection.getReversedDirection();
                nextLocation = game.getGameState().getLocationFromMovementDirection(currentSite, nextMovementDirection);
                if (nextLocation != null && !Filters.adjacentSite(currentSite).accepts(game, nextLocation)) {
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
                actions.add(action);
            }
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.isAtLocation(game, self, Filters.non_interior_site)) {
            Collection<PhysicalCard> presentCharacters = Filters.filterActive(game, self,
                    SpotOverride.INCLUDE_ALL, Filters.and(Filters.character, Filters.except(Filters.or(Filters.Jawa, Filters.Tusken_Raider)),
                            Filters.present(self), Filters.not(Filters.missing), Filters.canBeTargetedBy(self, TargetingReason.TO_BE_MISSING)));
            if (!presentCharacters.isEmpty()) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setSingletonTrigger(true);
                action.setText("Make characters missing");
                action.setActionMsg("Make " + GameUtils.getAppendedNames(presentCharacters) + " missing");
                // Perform result(s)
                action.appendEffect(
                        new GoMissingEffect(action, presentCharacters));
                actions.add(action);
            }
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_3;

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.isAtLocation(game, self, Filters.interior_site)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setSingletonTrigger(true);
            action.setText("Make lost");
            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");
            // Perform result(s)
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            actions.add(action);
        }

        return actions;
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
            if (nextLocation == null || !Filters.adjacentSite(currentSite).accepts(game, nextLocation)) {
                nextMovementDirection = nextMovementDirection.getReversedDirection();
                nextLocation = game.getGameState().getLocationFromMovementDirection(currentSite, nextMovementDirection);
                if (nextLocation != null && !Filters.adjacentSite(currentSite).accepts(game, nextLocation)) {
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

        return actions;
    }
}