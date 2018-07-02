package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractMobileEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.MoveMobileEffectEffect;
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
import com.gempukku.swccgo.logic.effects.GoMissingEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Effect
 * Subtype: Mobile
 * Title: Ice Storm
 */
public class Card3_104 extends AbstractMobileEffect {
    public Card3_104() {
        super(Side.DARK, 3, Title.Ice_Storm, Uniqueness.UNIQUE);
        setLore("Among the gravest dangers in the harsh environment of Hoth are frequent quick-moving ice storms.");
        setGameText("Deploy at outermost marker. All characters present at same exterior site are missing. Each turn, during your control phase, storm moves to next marker, reversing direction if at innermost marker. Mobile Effect lost when moved beyond outermost marker.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.outermostMarker(false);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            PhysicalCard currentSite = self.getAttachedTo();
            if (!Filters.innermostMarker.accepts(game, currentSite)) {
                PhysicalCard innerMostMarker = Filters.findFirstFromTopLocationsOnTable(game, Filters.innermostMarker);
                PhysicalCard locationLeft = game.getGameState().getLocationFromMovementDirection(currentSite, MovementDirection.LEFT);
                if (locationLeft != null && Filters.toward(currentSite, innerMostMarker).accepts(game, locationLeft)) {
                    self.setMovementDirection(MovementDirection.LEFT);
                }
                else {
                    PhysicalCard locationRight = game.getGameState().getLocationFromMovementDirection(currentSite, MovementDirection.RIGHT);
                    if (locationRight != null && Filters.toward(currentSite, innerMostMarker).accepts(game, locationRight)) {
                        self.setMovementDirection(MovementDirection.RIGHT);
                    }
                }
            }
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
            if (nextMovementDirection != MovementDirection.LEFT && nextMovementDirection != MovementDirection.RIGHT) {
                if (!Filters.innermostMarker.accepts(game, currentSite)) {
                    PhysicalCard innerMostMarker = Filters.findFirstFromTopLocationsOnTable(game, Filters.innermostMarker);
                    PhysicalCard locationLeft = game.getGameState().getLocationFromMovementDirection(currentSite, MovementDirection.LEFT);
                    if (locationLeft != null && Filters.toward(currentSite, innerMostMarker).accepts(game, locationLeft)) {
                        nextMovementDirection = MovementDirection.LEFT;
                    }
                    else {
                        PhysicalCard locationRight = game.getGameState().getLocationFromMovementDirection(currentSite, MovementDirection.RIGHT);
                        if (locationRight != null && Filters.toward(currentSite, innerMostMarker).accepts(game, locationRight)) {
                            nextMovementDirection = MovementDirection.RIGHT;
                        }
                    }
                }
            }
            else if (Filters.innermostMarker.accepts(game, currentSite)) {
                nextMovementDirection = nextMovementDirection.getReversedDirection();
            }
            PhysicalCard nextLocation = (nextMovementDirection != null) ? game.getGameState().getLocationFromMovementDirection(currentSite, nextMovementDirection) : null;
            if (nextLocation != null && !Filters.marker_site.accepts(game, nextLocation)) {
                nextLocation = null;
            }

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setPerformingPlayer(playerId);
            action.setText("Move to next marker");
            action.setActionMsg("Move " + GameUtils.getCardLink(self) + " to next marker");
            // Perform result(s)
            if (nextLocation != null) {
                action.appendEffect(
                        new MoveMobileEffectEffect(action, self, nextLocation, nextMovementDirection));
            }
            else {
                action.appendEffect(
                        new LoseCardFromTableEffect(action, self));
            }
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.isAtLocation(game, self, Filters.exterior_site)) {
            Collection<PhysicalCard> presentCharacters = Filters.filterActive(game, self,
                    SpotOverride.INCLUDE_ALL, Filters.and(Filters.character, Filters.present(self), Filters.not(Filters.missing), Filters.canBeTargetedBy(self, TargetingReason.TO_BE_MISSING)));
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
            if (nextMovementDirection != MovementDirection.LEFT && nextMovementDirection != MovementDirection.RIGHT) {
                if (!Filters.innermostMarker.accepts(game, currentSite)) {
                    PhysicalCard innerMostMarker = Filters.findFirstFromTopLocationsOnTable(game, Filters.innermostMarker);
                    PhysicalCard locationLeft = game.getGameState().getLocationFromMovementDirection(currentSite, MovementDirection.LEFT);
                    if (locationLeft != null && Filters.toward(currentSite, innerMostMarker).accepts(game, locationLeft)) {
                        nextMovementDirection = MovementDirection.LEFT;
                    }
                    else {
                        PhysicalCard locationRight = game.getGameState().getLocationFromMovementDirection(currentSite, MovementDirection.RIGHT);
                        if (locationRight != null && Filters.toward(currentSite, innerMostMarker).accepts(game, locationRight)) {
                            nextMovementDirection = MovementDirection.RIGHT;
                        }
                    }
                }
            }
            else if (Filters.innermostMarker.accepts(game, currentSite)) {
                nextMovementDirection = nextMovementDirection.getReversedDirection();
            }
            PhysicalCard nextLocation = (nextMovementDirection != null) ? game.getGameState().getLocationFromMovementDirection(currentSite, nextMovementDirection) : null;
            if (nextLocation != null && !Filters.marker_site.accepts(game, nextLocation)) {
                nextLocation = null;
            }

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Move to next marker");
            action.setActionMsg("Move " + GameUtils.getCardLink(self) + " to next marker");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            if (nextLocation != null) {
                action.appendEffect(
                        new MoveMobileEffectEffect(action, self, nextLocation, nextMovementDirection));
            }
            else {
                action.appendEffect(
                        new LoseCardFromTableEffect(action, self));
            }
            actions.add(action);
        }

        return actions;
    }
}