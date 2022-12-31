package com.gempukku.swccgo.cards.set9.light;

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
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CrossOverCharacterEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.TotalDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Interrupt
 * Subtype: Used
 * Title: Anakin Skywalker
 */
public class Card9_048 extends AbstractUsedInterrupt {
    public Card9_048() {
        super(Side.LIGHT, 4, Title.Anakin_Skywalker, Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.R);
        setLore("'You were right about me. Tell your sister ... you were right.'");
        setGameText("If Luke is about to be lost from table and Vader is present, shuffle Reserve Deck and draw destiny. Add 6 if Emperor present. If total destiny > 12, Vader instead crosses to Light Side, Luke is not lost, opponent loses 6 Force, and Emperor (if present) is lost. (Immune to Sense.)");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        Filter lukeFilter = Filters.and(Filters.Luke, Filters.canBeTargetedBy(self));

        // Check condition(s)
        if ((TriggerConditions.isAboutToBeLost(game, effectResult, lukeFilter)
                || TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, lukeFilter))
                && GameConditions.canDrawDestiny(game, playerId)) {
            final AboutToLeaveTableResult aboutToLeaveTableResult = (AboutToLeaveTableResult) effectResult;
            PhysicalCard cardToBeLost = aboutToLeaveTableResult.getCardAboutToLeaveTable();
            final Filter vaderFilter = Filters.and(Filters.Vader, Filters.present(cardToBeLost));
            if (GameConditions.canTarget(game, self, vaderFilter)) {
                final Filter emperorFilter = Filters.and(Filters.Emperor, Filters.present(cardToBeLost));

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setImmuneTo(Title.Sense);
                action.setText("Attempt to save Luke and convert Vader");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Luke", cardToBeLost) {
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard targetedLuke) {
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose Vader", vaderFilter) {
                                            @Override
                                            protected boolean getUseShortcut() {
                                                return true;
                                            }
                                            @Override
                                            protected void cardTargeted(final int targetGroupId2, final PhysicalCard targetedVader) {
                                                TargetingReason targetingReason = TargetingReason.TO_BE_LOST;
                                                if (GameConditions.canTarget(game, self, targetingReason, emperorFilter)) {
                                                    action.appendTargeting(
                                                            new TargetCardOnTableEffect(action, playerId, "Choose Emperor", targetingReason, emperorFilter) {
                                                                @Override
                                                                protected boolean getUseShortcut() {
                                                                    return true;
                                                                }
                                                                @Override
                                                                protected void cardTargeted(final int targetGroupId3, PhysicalCard targetedEmperor) {
                                                                    action.addAnimationGroup(targetedLuke, targetedVader, targetedEmperor);
                                                                    // Allow response(s)
                                                                    action.allowResponses("Attempt to save " + GameUtils.getCardLink(targetedLuke) + " and convert " + GameUtils.getCardLink(targetedVader) + " targeting " + GameUtils.getCardLink(targetedEmperor),
                                                                            new RespondablePlayCardEffect(action) {
                                                                                @Override
                                                                                protected void performActionResults(Action targetingAction) {
                                                                                    // Get the final targeted card(s)
                                                                                    PhysicalCard finalLuke = targetingAction.getPrimaryTargetCard(targetGroupId1);
                                                                                    PhysicalCard finalVader = targetingAction.getPrimaryTargetCard(targetGroupId2);
                                                                                    PhysicalCard finalEmperor = targetingAction.getPrimaryTargetCard(targetGroupId3);
                                                                                    drawDestinyAndProcessResult(playerId, self, aboutToLeaveTableResult, action, finalLuke, finalVader, finalEmperor);
                                                                                }
                                                                            }
                                                                    );
                                                                }
                                                            });
                                                } else {
                                                    action.addAnimationGroup(targetedLuke, targetedVader);
                                                    // Allow response(s)
                                                    action.allowResponses("Attempt to save " + GameUtils.getCardLink(targetedLuke) + " and convert " + GameUtils.getCardLink(targetedVader),
                                                            new RespondablePlayCardEffect(action) {
                                                                @Override
                                                                protected void performActionResults(Action targetingAction) {
                                                                    // Get the final targeted card(s)
                                                                    PhysicalCard finalLuke = targetingAction.getPrimaryTargetCard(targetGroupId1);
                                                                    PhysicalCard finalVader = targetingAction.getPrimaryTargetCard(targetGroupId2);
                                                                    drawDestinyAndProcessResult(playerId, self, aboutToLeaveTableResult, action, finalLuke, finalVader, null);
                                                                }
                                                            }
                                                    );
                                                }
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    private void drawDestinyAndProcessResult(final String playerId, final PhysicalCard self, final AboutToLeaveTableResult effectResult, final PlayInterruptAction action, final PhysicalCard luke, final PhysicalCard vader, final PhysicalCard emperor) {
        // Perform result(s)
        action.appendEffect(
                new ShuffleReserveDeckEffect(action));
        action.appendEffect(
                new DrawDestinyEffect(action, playerId) {
                    @Override
                    protected List<Modifier> getDrawDestinyModifiers(SwccgGame game, DrawDestinyState drawDestinyState) {
                        if (emperor != null) {
                            Modifier modifier = new TotalDestinyModifier(self, drawDestinyState.getId(), 6);
                            return Collections.singletonList(modifier);
                        }
                        return null;
                    }
                    @Override
                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                        GameState gameState = game.getGameState();
                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                        if (totalDestiny == null) {
                            gameState.sendMessage("Result: Failed due to failed destiny draw");
                            return;
                        }
                        float crossoverAttemptTotal = modifiersQuerying.getCrossoverAttemptTotal(gameState, vader, totalDestiny);
                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(crossoverAttemptTotal));
                        if (crossoverAttemptTotal > 12) {
                            gameState.sendMessage("Result: Succeeded");
                            effectResult.getPreventableCardEffect().preventEffectOnCard(luke);
                            action.appendEffect(
                                    new CrossOverCharacterEffect(action, vader));
                            action.appendEffect(
                                    new LoseForceEffect(action, game.getOpponent(playerId), 6));
                            if (emperor != null) {
                                action.appendEffect(
                                        new LoseCardFromTableEffect(action, emperor));
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