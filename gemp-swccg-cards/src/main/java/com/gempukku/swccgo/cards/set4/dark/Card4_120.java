package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractUtinniEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Subtype: Utinni
 * Title: Failure At The Cave
 */
public class Card4_120 extends AbstractUtinniEffect {
    public Card4_120() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, "Failure At The Cave", Uniqueness.UNIQUE);
        setLore("'That place is strong with the dark side of the Force. A domain of evil it is. In you must go.'");
        setGameText("Deploy on Dagobah: Cave. Target an apprentice on Dagobah. All Jedi Test game text is suspended. If target present during any battle phase, opponent draws destiny. If destiny < 4, you retrieve 2 Force (also, if destiny = 0, target is lost). Otherwise, Utinni Effect canceled.");
        addIcons(Icon.DAGOBAH);
        addKeywords(Keyword.UTINNI_EFFECT_THAT_RETRIEVES_FORCE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Dagobah_Cave;
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, TargetId targetId) {
        return Filters.and(Filters.apprentice, Filters.on(Title.Dagobah));
    }

    @Override
    public boolean isDagobahAllowed() {
        return true;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextModifier(self, Filters.jediTestTargetingApprentice(Filters.targetedByCardOnTableAsTargetId(self, TargetId.UTINNI_EFFECT_TARGET_1))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final GameState gameState = game.getGameState();
        final PhysicalCard target = self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1);

        // Check condition(s)
        if (GameConditions.isOnceDuringEitherPlayersPhase(game, self, playerId, gameTextSourceCardId, Phase.BATTLE)
                && GameConditions.isAtLocation(game, self, Filters.wherePresent(target))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Draw destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId, 1) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            final String owner = self.getOwner();
                            GameState gameState = game.getGameState();
                            if (totalDestiny == null) {
                                gameState.sendMessage("Result: Failed destiny draw");
                                action.appendEffect(
                                        new RetrieveForceEffect(action, owner, 2) {
                                            @Override
                                            public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                                                return Collections.singletonList(target);
                                            }
                                        });
                                action.appendEffect(
                                        new LoseCardFromTableEffect(action, target));
                                return;
                            }

                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                            if (totalDestiny < 4) {
                                if (totalDestiny == 0) {
                                    gameState.sendMessage("Result: Destiny = 0");
                                    action.appendEffect(
                                            new RetrieveForceEffect(action, owner, 2) {
                                                @Override
                                                public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                                                    return Collections.singletonList(target);
                                                }
                                            });
                                    action.appendEffect(
                                            new LoseCardFromTableEffect(action, target));
                                }
                                else {
                                    gameState.sendMessage("Result: Destiny < 4");
                                    action.appendEffect(
                                            new RetrieveForceEffect(action, owner, 2) {
                                                @Override
                                                public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                                                    return Collections.singletonList(target);
                                                }
                                            });
                                }
                            }
                            else {
                                gameState.sendMessage("Result: Destiny >= 4");
                                if (GameConditions.canBeCanceled(game, self)) {
                                    action.appendEffect(
                                            new CancelCardOnTableEffect(action, self));
                                }
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        final GameState gameState = game.getGameState();
        final PhysicalCard target = self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1);

        // Check condition(s)
        if (TriggerConditions.isEndOfEachPhase(game, effectResult, Phase.BATTLE)
                && GameConditions.isOnceDuringEitherPlayersPhase(game, self, opponent, gameTextSourceCardId, Phase.BATTLE)
                && GameConditions.isAtLocation(game, self, Filters.wherePresent(target))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Draw destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DrawDestinyEffect(action, opponent, 1) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            GameState gameState = game.getGameState();
                            if (totalDestiny == null) {
                                gameState.sendMessage("Result: Failed destiny draw");
                                action.appendEffect(
                                        new RetrieveForceEffect(action, playerId, 2) {
                                            @Override
                                            public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                                                return Collections.singletonList(target);
                                            }
                                        });
                                action.appendEffect(
                                        new LoseCardFromTableEffect(action, target));
                                return;
                            }

                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                            if (totalDestiny < 4) {
                                if (totalDestiny == 0) {
                                    gameState.sendMessage("Result: Destiny = 0");
                                    action.appendEffect(
                                            new RetrieveForceEffect(action, playerId, 2) {
                                                @Override
                                                public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                                                    return Collections.singletonList(target);
                                                }
                                            });
                                    action.appendEffect(
                                            new LoseCardFromTableEffect(action, target));
                                }
                                else {
                                    gameState.sendMessage("Result: Destiny < 4");
                                    action.appendEffect(
                                            new RetrieveForceEffect(action, playerId, 2) {
                                                @Override
                                                public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                                                    return Collections.singletonList(target);
                                                }
                                            });
                                }
                            }
                            else {
                                gameState.sendMessage("Result: Destiny >= 4");
                                if (GameConditions.canBeCanceled(game, self)) {
                                    action.appendEffect(
                                            new CancelCardOnTableEffect(action, self));
                                }
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}