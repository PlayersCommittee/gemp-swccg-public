package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCharacterOnTableToResetLandspeedUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCharacterToGoMissingEffect;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Lost
 * Title: Lost In The Wilderness
 */
public class Card8_057 extends AbstractLostInterrupt {
    public Card8_057() {
        super(Side.LIGHT, 4, Title.Lost_In_The_Wilderness, Uniqueness.UNIQUE);
        setLore("Survival training becomes essential when an Imperial Scout trooper is separated from his speeder bike.");
        setGameText("Cancel a Force drain at an exterior planet site where opponent has no vehicles or starships. Draw destiny. If destiny > number of opponent's characters at that site, choose one of those characters to be missing.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedAt(game, effectResult, Filters.and(Filters.exterior_planet_site,
                Filters.sameLocationAs(self, Filters.and(Filters.opponents(self), Filters.character)),
                Filters.not(Filters.sameLocationAs(self, Filters.and(Filters.opponents(self), Filters.or(Filters.vehicle, Filters.starship))))))
                && GameConditions.canCancelForceDrain(game, self)) {
            final PhysicalCard forceDrainLocation = game.getGameState().getForceDrainLocation();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel Force drain");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelForceDrainEffect(action));
                            action.appendEffect(
                                    new PassthruEffect(action) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }

                                                            Filter characterFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.at(forceDrainLocation));
                                                            int numCharacters = Filters.countActive(game, self, characterFilter);
                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                            gameState.sendMessage("Number of characters: " + numCharacters);
                                                            if (totalDestiny > numCharacters) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.LOST_IN_THE_WILDERNESS__MISSING_TREATED_AS_LANDSPEED_0)) {
                                                                    action.appendEffect(
                                                                            new ChooseCharacterOnTableToResetLandspeedUntilEndOfTurnEffect(action, playerId, characterFilter, 0));
                                                                }
                                                                else {
                                                                    action.appendEffect(
                                                                            new ChooseCharacterToGoMissingEffect(action, playerId, characterFilter));
                                                                }
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                    }
                                            );
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
}