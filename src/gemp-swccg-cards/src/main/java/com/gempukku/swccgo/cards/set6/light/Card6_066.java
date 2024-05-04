package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.AttackState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Interrupt
 * Subtype: Used
 * Title: Fallen Portal
 */
public class Card6_066 extends AbstractUsedInterrupt {
    public Card6_066() {
        super(Side.LIGHT, 4, Title.Fallen_Portal, Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.U);
        setLore("Jabba ordered the two-meter thick door to keep the rancor in. He never thought it would be the instrument of the rancor's demise.");
        setGameText("Target one creature or up to two characters present that just initiated an attack or battle against you at Back Door, Rancor Pit, Tatooine: Jabba's Palace or any docking bay. Draw destiny. Target(s) immediately lost if destiny +2 > total defense value.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        String opponent = game.getOpponent(playerId);
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;
        Filter locationFilter = Filters.or(Filters.Back_Door, Filters.Rancor_Pit, Filters.Jabbas_Palace, Filters.docking_bay);

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, opponent, locationFilter)) {
            Filter targetFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.presentInBattle);
            if (GameConditions.canTarget(game, self, targetingReason, targetFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Target characters");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardsOnTableEffect(action, playerId, "Choose characters", 1, 2, targetingReason, targetFilter) {
                            @Override
                            protected void cardsTargeted(final int targetGroupId, Collection<PhysicalCard> targetedCards) {
                                action.addAnimationGroup(targetedCards);
                                action.addSecondaryTargetFilter(Filters.battleLocation);
                                // Allow response(s)
                                action.allowResponses("Make " + GameUtils.getAppendedNames(targetedCards) + " lost",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the final targeted card(s)
                                                final Collection<PhysicalCard> finalCharacters = action.getPrimaryTargetCards(targetGroupId);
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new DrawDestinyEffect(action, playerId) {
                                                            @Override
                                                            protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                                return finalCharacters;
                                                            }
                                                            @Override
                                                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                GameState gameState = game.getGameState();
                                                                float totalDefenseValue = 0;
                                                                for (PhysicalCard target : finalCharacters) {
                                                                    totalDefenseValue += game.getModifiersQuerying().getDefenseValue(gameState, target);
                                                                }
                                                                gameState.sendMessage("Destiny: " + (totalDestiny != null ? GuiUtils.formatAsString(totalDestiny) : "Failed destiny draw"));
                                                                gameState.sendMessage("Total defense value: " + GuiUtils.formatAsString(totalDefenseValue));
                                                                if (((totalDestiny != null ? totalDestiny : 0) + 2) > totalDefenseValue) {
                                                                    gameState.sendMessage("Result: Succeeded");
                                                                    action.appendEffect(
                                                                            new LoseCardsFromTableEffect(action, finalCharacters));
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
                actions.add(action);
            }
        }

        // Check condition(s)
        if (TriggerConditions.attackInitiatedAt(game, effectResult, opponent, locationFilter)) {
            AttackState attackState = game.getGameState().getAttackState();
            if (attackState.getDefenderOwner().equals(playerId)) {
                if (attackState.isNonCreatureAttackingCreature()) {
                    Filter targetFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.presentInAttack);
                    if (GameConditions.canTarget(game, self, targetingReason, targetFilter)) {

                        final PlayInterruptAction action = new PlayInterruptAction(game, self);
                        action.setText("Target characters");
                        // Choose target(s)
                        action.appendTargeting(
                                new TargetCardsOnTableEffect(action, playerId, "Choose characters", 1, 2, targetingReason, targetFilter) {
                                    @Override
                                    protected void cardsTargeted(final int targetGroupId, Collection<PhysicalCard> targetedCards) {
                                        action.addAnimationGroup(targetedCards);
                                        action.addSecondaryTargetFilter(Filters.attackLocation);
                                        // Allow response(s)
                                        action.allowResponses("Make " + GameUtils.getAppendedNames(targetedCards) + " lost",
                                                new RespondablePlayCardEffect(action) {
                                                    @Override
                                                    protected void performActionResults(Action targetingAction) {
                                                        // Get the final targeted card(s)
                                                        final Collection<PhysicalCard> finalCharacters = action.getPrimaryTargetCards(targetGroupId);
                                                        // Perform result(s)
                                                        action.appendEffect(
                                                                new DrawDestinyEffect(action, playerId) {
                                                                    @Override
                                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                                        return finalCharacters;
                                                                    }
                                                                    @Override
                                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                        GameState gameState = game.getGameState();
                                                                        float totalDefenseValue = 0;
                                                                        for (PhysicalCard target : finalCharacters) {
                                                                            totalDefenseValue += game.getModifiersQuerying().getDefenseValue(gameState, target);
                                                                        }
                                                                        gameState.sendMessage("Destiny: " + (totalDestiny != null ? GuiUtils.formatAsString(totalDestiny) : "Failed destiny draw"));
                                                                        gameState.sendMessage("Total defense value: " + GuiUtils.formatAsString(totalDefenseValue));
                                                                        if (((totalDestiny != null ? totalDestiny : 0) + 2) > totalDefenseValue) {
                                                                            gameState.sendMessage("Result: Succeeded");
                                                                            action.appendEffect(
                                                                                    new LoseCardsFromTableEffect(action, finalCharacters));
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
                        actions.add(action);
                    }
                } else {
                    Filter targetFilter = Filters.and(Filters.opponents(self), Filters.creature, Filters.presentInAttack);
                    if (GameConditions.canTarget(game, self, targetingReason, targetFilter)) {

                        final PlayInterruptAction action = new PlayInterruptAction(game, self);
                        action.setText("Target creature");
                        // Choose target(s)
                        action.appendTargeting(
                                new TargetCardOnTableEffect(action, playerId, "Choose creature", targetingReason, targetFilter) {
                                    @Override
                                    protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                        action.addAnimationGroup(targetedCard);
                                        action.addSecondaryTargetFilter(Filters.attackLocation);
                                        // Allow response(s)
                                        action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " lost",
                                                new RespondablePlayCardEffect(action) {
                                                    @Override
                                                    protected void performActionResults(Action targetingAction) {
                                                        // Get the final targeted card(s)
                                                        final PhysicalCard finalCreature = action.getPrimaryTargetCard(targetGroupId);
                                                        // Perform result(s)
                                                        action.appendEffect(
                                                                new DrawDestinyEffect(action, playerId) {
                                                                    @Override
                                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                                        return Collections.singletonList(finalCreature);
                                                                    }
                                                                    @Override
                                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                        GameState gameState = game.getGameState();
                                                                        float totalDefenseValue = game.getModifiersQuerying().getDefenseValue(gameState, finalCreature);
                                                                        gameState.sendMessage("Destiny: " + (totalDestiny != null ? GuiUtils.formatAsString(totalDestiny) : "Failed destiny draw"));
                                                                        gameState.sendMessage("Total defense value: " + GuiUtils.formatAsString(totalDefenseValue));
                                                                        if (((totalDestiny != null ? totalDestiny : 0) + 2) > totalDefenseValue) {
                                                                            gameState.sendMessage("Result: Succeeded");
                                                                            action.appendEffect(
                                                                                    new LoseCardFromTableEffect(action, finalCreature));
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
                        actions.add(action);
                    }
                }
            }
        }

        return actions;
    }
}