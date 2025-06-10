package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.google.common.base.Objects;

import java.util.List;

/**
 * Enforces the expanding of a location's game text to other locations.
 */
public class ExpandLocationGameTextRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;

    /**
     * Creates a rule that enforces the expanding of a locations's game text to other locations.
     * @param actionsEnvironment the actions environment
     */
    public ExpandLocationGameTextRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        // Check condition(s)
                        if (TriggerConditions.isTableChanged(game, effectResult)) {

                            GameState gameState = game.getGameState();
                            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                            // Check each location and compare its "expandedGameText" state with whether it is expanded upon.
                            // Use that information to determine if the location game text needs to be re-applied.
                            for (PhysicalCard location : Filters.filterTopLocationsOnTable(game, Filters.any)) {

                                // Check what the values currently are
                                Integer oldExpandedToDarkSideFromCardId = location.getLocationGameTextExpandedToSideFromCardId(Side.DARK);
                                Side oldExpandedToDarkSideFromSide = location.getLocationGameTextExpandedToSideFromSide(Side.DARK);
                                Integer oldExpandedToLightSideFromCardId = location.getLocationGameTextExpandedToSideFromCardId(Side.LIGHT);
                                Side oldExpandedToLightSideFromSide = location.getLocationGameTextExpandedToSideFromSide(Side.LIGHT);

                                // Check the values that need to be set
                                PhysicalCard expandedToDarkSideFromDarkSideOfLocation = modifiersQuerying.hasExpandedGameTextFromLocation(gameState, Side.DARK, location, Side.DARK);
                                PhysicalCard expandedToDarkSideFromLightSideOfLocation = modifiersQuerying.hasExpandedGameTextFromLocation(gameState, Side.LIGHT, location, Side.DARK);
                                Integer newExpandedToDarkSideFromCardId = null;
                                Side newExpandedToDarkSideFromSide = null;
                                if (expandedToDarkSideFromDarkSideOfLocation != null) {
                                    newExpandedToDarkSideFromCardId = expandedToDarkSideFromDarkSideOfLocation.getCardId();
                                    newExpandedToDarkSideFromSide = Side.DARK;
                                } else if (expandedToDarkSideFromLightSideOfLocation != null) {
                                    newExpandedToDarkSideFromCardId = expandedToDarkSideFromLightSideOfLocation.getCardId();
                                    newExpandedToDarkSideFromSide = Side.LIGHT;
                                }
                                PhysicalCard expandedToLightSideFromDarkSideOfLocation = modifiersQuerying.hasExpandedGameTextFromLocation(gameState, Side.DARK, location, Side.LIGHT);
                                PhysicalCard expandedToLightSideFromLightSideOfLocation = modifiersQuerying.hasExpandedGameTextFromLocation(gameState, Side.LIGHT, location, Side.LIGHT);
                                Integer newExpandedToLightSideFromCardId = null;
                                Side newExpandedToLightSideFromSide = null;
                                if (expandedToLightSideFromDarkSideOfLocation != null) {
                                    newExpandedToLightSideFromCardId = expandedToLightSideFromDarkSideOfLocation.getCardId();
                                    newExpandedToLightSideFromSide = Side.DARK;
                                } else if (expandedToLightSideFromLightSideOfLocation != null) {
                                    newExpandedToLightSideFromCardId = expandedToLightSideFromLightSideOfLocation.getCardId();
                                    newExpandedToLightSideFromSide = Side.LIGHT;
                                }

                                boolean foundDifference = false;
                                if (!Objects.equal(oldExpandedToDarkSideFromCardId, newExpandedToDarkSideFromCardId)) {
                                    location.setLocationGameTextExpandedToSideFromCardId(Side.DARK, newExpandedToDarkSideFromCardId);
                                    foundDifference = true;
                                }
                                if (oldExpandedToDarkSideFromSide != newExpandedToDarkSideFromSide) {
                                    location.setLocationGameTextExpandedToSideFromSide(Side.DARK, newExpandedToDarkSideFromSide);
                                    foundDifference = true;
                                }
                                if (!Objects.equal(oldExpandedToLightSideFromCardId, newExpandedToLightSideFromCardId)) {
                                    location.setLocationGameTextExpandedToSideFromCardId(Side.LIGHT, newExpandedToLightSideFromCardId);
                                    foundDifference = true;
                                }
                                if (oldExpandedToLightSideFromSide != newExpandedToLightSideFromSide) {
                                    location.setLocationGameTextExpandedToSideFromSide(Side.LIGHT, newExpandedToLightSideFromSide);
                                    foundDifference = true;
                                }

                                // If any difference is found, then all reapplyAffectingForCard() since the game text needs to be reapplied.
                                if (foundDifference) {
                                    gameState.reapplyAffectingForCard(game, location);
                                }
                            }
                        }
                        return null;
                    }
                }
        );
    }
}
