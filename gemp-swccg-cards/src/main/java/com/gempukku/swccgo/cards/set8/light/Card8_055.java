package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromHandEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;


/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Used
 * Title: I Have A Really Bad Feeling About This
 */
public class Card8_055 extends AbstractUsedInterrupt {
    public Card8_055() {
        super(Side.LIGHT, 5, "I Have A Really Bad Feeling About This", Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.C);
        setLore("'I'm rather embarrassed, General Solo, but it appears you are to be the main course at a banquet in my honor.'");
        setGameText("If opponent just initiated a battle where opponent has more than double your power, target your highest-ability character in that battle. Draw destiny. If destiny < target's ability, deploy one character there from hand (for free).");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);
        Filter characterToDeployFilter = Filters.and(Filters.character, Filters.deployableToLocation(self, Filters.battleLocation, true, 0));

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult, opponent)
                && GameConditions.hasInHand(game, playerId, characterToDeployFilter)) {
            float playersPower = GameConditions.getBattlePower(game, playerId);
            float opponentsPower = GameConditions.getBattlePower(game, opponent);
            if ((2 * playersPower) < opponentsPower) {
                float highestAbility = 0;
                Collection<PhysicalCard> charactersInBattle = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.character, Filters.participatingInBattle, Filters.hasAbility, Filters.notExcludedFromBeingHighestAbilityCharacter(self)));

                Collection<PhysicalCard> highestAbilityCharacters = new HashSet<>();
                for (PhysicalCard character : charactersInBattle) {
                    float ability = game.getModifiersQuerying().getAbility(game.getGameState(), character);
                    if (Float.compare(ability, highestAbility) == 0) {
                        highestAbilityCharacters.add(character);
                    } else if (Float.compare(ability, highestAbility) > 0) {
                        highestAbility = ability;
                        highestAbilityCharacters.clear();
                        highestAbilityCharacters.add(character);
                    }
                }

                if (!highestAbilityCharacters.isEmpty()) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self);
                    action.setText("Draw destiny to deploy a character");
                    // Choose target(s)
                    action.appendTargeting(
                            new TargetCardOnTableEffect(action, playerId, "Choose a highest-ability character", Filters.in(highestAbilityCharacters)) {
                                @Override
                                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCharacter) {
                                    action.addAnimationGroup(targetedCharacter);
                                    // Allow response(s)
                                    action.allowResponses("Deploy a character by drawing destiny against " + GameUtils.getCardLink(targetedCharacter),
                                            new RespondablePlayCardEffect(action) {
                                                @Override
                                                protected void performActionResults(Action targetingAction) {
                                                    // Get the targeted card(s) from the action using the targetGroupId.
                                                    // This needs to be done in case the target(s) were changed during the responses.
                                                    final PhysicalCard finalCharacter = targetingAction.getPrimaryTargetCard(targetGroupId);

                                                    // Perform result(s)
                                                    action.appendEffect(
                                                            new DrawDestinyEffect(action, playerId) {
                                                                @Override
                                                                protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                                    return finalCharacter != null ? Collections.singletonList(finalCharacter) : Collections.<PhysicalCard>emptyList();
                                                                }

                                                                @Override
                                                                protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                    GameState gameState = game.getGameState();
                                                                    if (totalDestiny == null) {
                                                                        gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                        return;
                                                                    }
                                                                    if (finalCharacter == null) {
                                                                        gameState.sendMessage("Result: Failed due to no highest-ability character");
                                                                        return;
                                                                    }

                                                                    float ability = game.getModifiersQuerying().getAbility(game.getGameState(), finalCharacter);
                                                                    gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                    gameState.sendMessage("Ability: " + GuiUtils.formatAsString(ability));
                                                                    if (totalDestiny < ability) {
                                                                        gameState.sendMessage("Result: Succeeded");
                                                                        action.appendEffect(
                                                                                new DeployCardToLocationFromHandEffect(action, playerId, Filters.character, Filters.battleLocation, true));
                                                                    } else {
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
            }
        }
        return null;
    }
}