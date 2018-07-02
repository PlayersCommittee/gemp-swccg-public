package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InPlayDataSetCondition;
import com.gempukku.swccgo.cards.effects.SetTargetedCardEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Title: I've Lost Artoo!
 */
public class Card1_218 extends AbstractNormalEffect {
    public Card1_218() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, "I've Lost Artoo!");
        setLore("'WHAAAAAAAAAOOOOW!'");
        setGameText("Use 1 Force to target a starship's [Nav Computer] or astromech. Draw destiny. If destiny > 1, [Nav Computer] or astromech is lost. If starship's [Nav Computer] is lost, place Effect on starship (may add 1 astromech); otherwise, Effect lost.");
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 1));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.starship, Filters.or(Icon.NAV_COMPUTER, Filters.hasAboardExceptRelatedSites(self, Filters.and(Filters.astromech_droid, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_LOST)))));
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.starship;
    }

    @Override
    public List<PlayCardAction> getPlayCardActions(final String playerId, SwccgGame game, final PhysicalCard self, PhysicalCard sourceCard, boolean forFree, float changeInCost, DeploymentOption deploymentOption, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption, ReactActionOption reactActionOption, PhysicalCard cardToDeployWith, boolean cardToDeployWithForFree, float cardToDeployWithChangeInCost, Filter deployTargetFilter, Filter specialLocationConditions) {
        if (!forFree) {
            forFree = isCardTypeAlwaysPlayedForFree() || game.getGameState().getCurrentPhase() == Phase.PLAY_STARTING_CARDS;
        }

        List<PlayCardAction> actions = super.getPlayCardActions(playerId, game, self, sourceCard, forFree, changeInCost, deploymentOption, deploymentRestrictionsOption, deployAsCaptiveOption, reactActionOption, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost, deployTargetFilter, specialLocationConditions);
        for (final PlayCardAction action : actions) {
            // Ask player to target starship's nav computer or astromech
            action.appendTargeting(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(final SwccgGame game) {
                            final GameState gameState = game.getGameState();
                            final PhysicalCard starship = action.getAllPrimaryTargetCards().values().iterator().next().keySet().iterator().next();
                            boolean canTargetNavComputer = game.getModifiersQuerying().hasIcon(gameState, starship, Icon.NAV_COMPUTER);
                            final TargetingReason targetingReason = TargetingReason.TO_BE_LOST;
                            final Filter astromechFilter = Filters.and(Filters.astromech_droid, Filters.aboardExceptRelatedSites(starship));
                            boolean canTargetAstromech = GameConditions.canTarget(game, self, targetingReason, astromechFilter);
                            if (canTargetAstromech && canTargetNavComputer) {
                                action.appendTargeting(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new MultipleChoiceAwaitingDecision("Choose target", new String[]{"Nav Computer", "Astromech Droid"}) {
                                                    @Override
                                                    protected void validDecisionMade(int index, String result) {
                                                        if (index==0) {
                                                            gameState.sendMessage(playerId + " chooses to target " + GameUtils.getCardLink(starship) + "'s nav computer");
                                                        }
                                                        else {
                                                            action.appendTargeting(
                                                                    new TargetCardOnTableEffect(action, playerId, "Choose astromech", targetingReason, astromechFilter) {
                                                                        @Override
                                                                        protected void cardTargeted(int targetGroupId, PhysicalCard targetedCard) {
                                                                            gameState.sendMessage(playerId + " chooses to target astromech " + GameUtils.getCardLink(targetedCard));
                                                                            action.appendTargeting(
                                                                                    new SetTargetedCardEffect(action, self, TargetId.EFFECT_TARGET_1, targetGroupId, targetedCard, astromechFilter));
                                                                        }
                                                                    }
                                                            );
                                                        }
                                                    }
                                                }
                                        )
                                );
                            }
                            else if (canTargetAstromech) {
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose astromech", targetingReason, astromechFilter) {
                                            @Override
                                            protected void cardTargeted(int targetGroupId, PhysicalCard targetedCard) {
                                                gameState.sendMessage(playerId + " chooses to target astromech " + GameUtils.getCardLink(targetedCard));
                                                action.appendTargeting(
                                                        new SetTargetedCardEffect(action, self, TargetId.EFFECT_TARGET_1, targetGroupId, targetedCard, astromechFilter));
                                            }
                                        }
                                );
                            }
                            else {
                                gameState.sendMessage(playerId + " chooses to target " + GameUtils.getCardLink(starship) + "'s nav computer");
                            }
                        }
                    }
            );
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            final String playerId = self.getOwner();
            final GameState gameState = game.getGameState();
            final PhysicalCard astromech = self.getTargetedCard(gameState, TargetId.EFFECT_TARGET_1);
            final boolean isNavComputer = (astromech == null);

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Draw destiny");
            // Perform result(s)
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            if (totalDestiny == null) {
                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                action.appendEffect(
                                        new LoseCardFromTableEffect(action, self));
                                return;
                            }

                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                            if (totalDestiny > 1) {
                                gameState.sendMessage("Result: Succeeded");
                                if (isNavComputer) {
                                    // If Nav Computer was targeted, getGameTextWhileActiveModifiers handles removing Nav Computer and (may add 1 astromech)
                                    self.setWhileInPlayData(new WhileInPlayData());
                                    return;
                                }
                                else {
                                    action.appendEffect(
                                            new LoseCardFromTableEffect(action, astromech));
                                }
                            }
                            else {
                                gameState.sendMessage("Result: Failed");
                            }
                            action.appendEffect(
                                    new LoseCardFromTableEffect(action, self));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition inPlayDataSet = new InPlayDataSetCondition(self);
        Filter starship = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelIconModifier(self, starship, inPlayDataSet, Icon.NAV_COMPUTER));
        modifiers.add(new RemovePermanentAstromechsModifier(self, inPlayDataSet, starship));
        modifiers.add(new AstromechCapacityModifier(self, starship, inPlayDataSet, 1));
        return modifiers;
    }
}