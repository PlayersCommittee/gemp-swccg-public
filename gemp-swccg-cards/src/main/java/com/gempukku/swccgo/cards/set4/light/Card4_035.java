package com.gempukku.swccgo.cards.set4.light;

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
import com.gempukku.swccgo.logic.effects.choose.ChooseStartingDirectionEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextArmorModifier;
import com.gempukku.swccgo.logic.modifiers.MayBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalAsteroidDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: Rogue Asteroid
 */
public class Card4_035 extends AbstractMobileEffect {
    public Card4_035() {
        super(Side.LIGHT, 4, Title.Rogue_Asteroid, Uniqueness.DIAMOND_1);
        setLore("'Considering the amount of damage we've sustained, they must have been destroyed.'");
        setGameText("Deploy on an asteroid sector and specify starting direction. Every move phase, moves to next adjacent asteroid sector (reversing direction as necessary). Where present, adds 2 to asteroid destiny. May be targeted by starship weapons (armor = 3).");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    public boolean hasArmorAttribute() {
        return true;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.asteroid_sector;
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
        if (TriggerConditions.isEndOfEachPhase(game, effectResult, Phase.MOVE)
                && GameConditions.isOnceDuringEitherPlayersPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.MOVE)
                && !GameConditions.mayNotMove(game, self)) {

            // Determine next site and movement direction
            final PhysicalCard currentSector = self.getAttachedTo();
            MovementDirection nextMovementDirection = self.getMovementDirection();
            PhysicalCard nextLocation = game.getGameState().getLocationFromMovementDirection(currentSector, nextMovementDirection);
            if (nextLocation == null || !Filters.and(Filters.adjacentSector(currentSector), Filters.asteroid_sector).accepts(game, nextLocation)) {
                nextMovementDirection = nextMovementDirection.getReversedDirection();
                nextLocation = game.getGameState().getLocationFromMovementDirection(currentSector, nextMovementDirection);
                if (nextLocation != null && !Filters.and(Filters.adjacentSector(currentSector), Filters.asteroid_sector).accepts(game, nextLocation)) {
                    nextLocation = null;
                }
            }
            if (nextLocation != null) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Move to next sector");
                action.setActionMsg("Move " + GameUtils.getCardLink(self) + " to next sector");
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
        if (GameConditions.isOnceDuringEitherPlayersPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.MOVE)
                && !GameConditions.mayNotMove(game, self)) {

            // Determine next site and movement direction
            final PhysicalCard currentSector = self.getAttachedTo();
            MovementDirection nextMovementDirection = self.getMovementDirection();
            PhysicalCard nextLocation = game.getGameState().getLocationFromMovementDirection(currentSector, nextMovementDirection);
            if (nextLocation == null || !Filters.and(Filters.adjacentSector(currentSector), Filters.asteroid_sector).accepts(game, nextLocation)) {
                nextMovementDirection = nextMovementDirection.getReversedDirection();
                nextLocation = game.getGameState().getLocationFromMovementDirection(currentSector, nextMovementDirection);
                if (nextLocation != null && !Filters.and(Filters.adjacentSector(currentSector), Filters.asteroid_sector).accepts(game, nextLocation)) {
                    nextLocation = null;
                }
            }
            if (nextLocation != null) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Move to next sector");
                action.setActionMsg("Move " + GameUtils.getCardLink(self) + " to next sector");
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

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalAsteroidDestinyModifier(self, 2, Filters.presentWith(self)));
        modifiers.add(new MayBeTargetedByWeaponsModifier(self, Filters.starship_weapon));
        modifiers.add(new DefinedByGameTextArmorModifier(self, 3));
        return modifiers;
    }
}