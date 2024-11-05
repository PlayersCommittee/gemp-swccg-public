package com.gempukku.swccgo.cards.set302.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WeaponFiringState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.EnhanceForceDrainResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dark Jedi Brotherhood Core
 * Type: Interrupt
 * Subtype: Used
 * Title: Training Failure
 */
public class Card302_017 extends AbstractUsedInterrupt {
    public Card302_017() {
        super(Side.LIGHT, 5, "Training Failure", Uniqueness.UNRESTRICTED, ExpansionSet.DJB_CORE, Rarity.V);
        setLore("The Shadow Academy spends considerable time focusing on Lightsaber combat. However, accidents are known to happen...from time to time.");
        setGameText("Cancel Lightsaber Combat Training. OR Target a character with ability < 4 using a lightsaber in a battle or a Force drain. Draw destiny. Target lost if destiny > ability. Lightsaber lost if destiny = ability.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Lightsaber_Combat_Training)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Lightsaber_Combat_Training, Title.Lightsaber_Combat_Training);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Lightsaber_Combat_Training)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        // Check condition(s)
        if (TriggerConditions.isFiringWeapon(game, effect, Filters.lightsaber, Filters.and(Filters.character, Filters.abilityLessThan(4)))) {
            WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();
            if (weaponFiringState != null) {
                PhysicalCard lightsaber = weaponFiringState.getCardFiring();
                PhysicalCard character = weaponFiringState.getCardFiringWeapon();
                if (GameConditions.canTarget(game, self, TargetingReason.TO_BE_LOST, character)) {

                    PlayInterruptAction action = getTargetCharacterUsingLightsaber(playerId, game, self, character, lightsaber);
                    actions.add(action);
                }
            }
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.forceDrainEnhancedByWeapon(game, effectResult, Filters.and(Filters.lightsaber, Filters.attachedTo(Filters.and(Filters.character, Filters.abilityLessThan(4)))))) {
            PhysicalCard lightsaber = ((EnhanceForceDrainResult) effectResult).getWeapon();
            PhysicalCard character = ((EnhanceForceDrainResult) effectResult).getWeapon().getAttachedTo();
            if (GameConditions.canTarget(game, self, TargetingReason.TO_BE_LOST, character)) {

                PlayInterruptAction action = getTargetCharacterUsingLightsaber(playerId, game, self, character, lightsaber);
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    private PlayInterruptAction getTargetCharacterUsingLightsaber(final String playerId, SwccgGame game, final PhysicalCard self, final PhysicalCard character, final PhysicalCard lightsaber) {
        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Target character using lightsaber");
        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, playerId, "Choose character", TargetingReason.TO_BE_LOST, character) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                        // Allow response(s)
                        action.allowResponses("Target " + GameUtils.getCardLink(targetedCard) + " using a lightsaber",
                                new RespondablePlayCardEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, playerId) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(finalTarget);
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                            return;
                                                        }

                                                        float ability = game.getModifiersQuerying().getAbility(game.getGameState(), finalTarget);
                                                        gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        gameState.sendMessage("Ability: " + GuiUtils.formatAsString(ability));

                                                        if (totalDestiny > ability) {
                                                            gameState.sendMessage("Result: Target lost");
                                                            action.appendEffect(
                                                                    new LoseCardFromTableEffect(action, finalTarget));
                                                        }
                                                        else if (totalDestiny == ability
                                                                && Filters.lightsaber.accepts(game, lightsaber)) {
                                                            gameState.sendMessage("Result: Lightsaber lost");
                                                            action.appendEffect(
                                                                    new LoseCardFromTableEffect(action, lightsaber));
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                });
                                    }
                                }
                        );
                    }
                }
        );
        return action;
    }
}