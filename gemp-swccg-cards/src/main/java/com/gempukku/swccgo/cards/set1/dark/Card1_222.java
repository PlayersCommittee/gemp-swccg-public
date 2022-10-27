package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractUtinniEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.UtinniEffectStatus;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ResetPowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Subtype: Utinni
 * Title: Lateral Damage
 */
public class Card1_222 extends AbstractUtinniEffect {
    public Card1_222() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.Lateral_Damage, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R2);
        setLore("Starships can become damaged in combat and rendered ineffective until they can be repaired and re-outfitted.");
        setGameText("Deploy on any system. Target an opponent's starship. Target's power and forfeit = 0. When target reaches Utinni Effect, target draws one destiny. Utinni Effect canceled if destiny > 2. Otherwise, draw again next move phase, etc.");
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.system;
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, TargetId targetId) {
        return Filters.and(Filters.opponents(self), Filters.starship);
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilterToRemainTargeting(SwccgGame game, PhysicalCard self, TargetId targetId) {
        return Filters.starship;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter target = Filters.targetedByCardOnTableAsTargetId(self, TargetId.UTINNI_EFFECT_TARGET_1);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetPowerModifier(self, target, 0));
        modifiers.add(new ResetForfeitModifier(self, target, 0));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        final GameState gameState = game.getGameState();
        PhysicalCard target = self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1);

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult) || TriggerConditions.isEndOfEachPhase(game, effectResult, Phase.MOVE)) {
            if (GameConditions.isAtLocation(game, self, Filters.sameLocation(target))) {
                if (!GameConditions.isUtinniEffectReached(game, self) || TriggerConditions.isEndOfEachPhase(game, effectResult, Phase.MOVE)) {
                    if (!GameConditions.isDuringEitherPlayersPhase(game, Phase.MOVE)
                            || GameConditions.isOnceDuringEitherPlayersPhase(game, self, opponent, gameTextSourceCardId, Phase.MOVE)) {

                        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                        action.setSingletonTrigger(true);
                        action.setPerformingPlayer(opponent);
                        action.setText("Draw destiny to cancel");
                        action.setActionMsg("Draw destiny to cancel " + GameUtils.getCardLink(self));
                        // Update usage limit(s)
                        action.appendUsage(
                                new PassthruEffect(action) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        self.setUtinniEffectStatus(UtinniEffectStatus.REACHED);
                                    }
                                }
                        );
                        if (GameConditions.isDuringEitherPlayersPhase(game, Phase.MOVE)) {
                            action.appendUsage(
                                    new OncePerPhaseEffect(action));
                        }
                        // Perform result(s)
                        action.appendEffect(
                                new DrawDestinyEffect(action, opponent, 1) {
                                    @Override
                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                        GameState gameState = game.getGameState();
                                        if (totalDestiny == null) {
                                            gameState.sendMessage("Result: Failed due to failed destiny draw");
                                            return;
                                        }

                                        gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                        if (totalDestiny > 2) {
                                            gameState.sendMessage("Result: Succeeded");
                                            action.appendEffect(
                                                    new CancelCardOnTableEffect(action, self));
                                        }
                                        else {
                                            gameState.sendMessage("Result: Failed");
                                        }
                                    }
                                });
                        return Collections.singletonList(action);
                    }
                }
            }
            else {
                self.setUtinniEffectStatus(UtinniEffectStatus.NOT_REACHED);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final GameState gameState = game.getGameState();
        PhysicalCard target = self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1);

        // Check condition(s)
        if (GameConditions.isOnceDuringEitherPlayersPhase(game, self, playerId, gameTextSourceCardId, Phase.MOVE)
                && GameConditions.isAtLocation(game, self, Filters.sameLocation(target))
                && GameConditions.canBeCanceled(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Draw destiny to cancel");
            action.setActionMsg("Draw destiny to cancel " + GameUtils.getCardLink(self));
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId, 1) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            GameState gameState = game.getGameState();
                            if (totalDestiny == null) {
                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                return;
                            }

                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                            if (totalDestiny > 2) {
                                gameState.sendMessage("Result: Succeeded");
                                action.appendEffect(
                                        new CancelCardOnTableEffect(action, self));
                            }
                            else {
                                gameState.sendMessage("Result: Failed");
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}