package com.gempukku.swccgo.logic.timing.actions.battle;

import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.actions.SystemQueueAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.BattleDestinyDrawsCompleteForBothPlayersResult;
import com.gempukku.swccgo.logic.timing.results.BattleDestinyDrawsCompleteForPlayerResult;
import com.gempukku.swccgo.logic.timing.results.BeforeBattleDestinyDrawsResult;
import com.gempukku.swccgo.logic.timing.results.InitialAttritionCalculatedResult;

import java.util.ArrayList;
import java.util.List;

/**
 * An action that carries out the power segment of a battle.
 */
public class BattlePowerSegmentAction extends SystemQueueAction {

    /**
     * Creates an action that carries out the power segment of a battle.
     */
    public BattlePowerSegmentAction() {
        appendEffect(
                new PassthruEffect(this) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        BattleState battleState = game.getGameState().getBattleState();

                        // Perform power segment
                        battleState.reachedPowerSegment();

                        // Player that initiated battle draws destiny first
                        String playerId = battleState.getPlayerInitiatedBattle();
                        String opponent = game.getOpponent(playerId);

                        // Draw destinies to power only
                        appendEffect(
                                new DrawDestinyToPowerOnlyEffect(_action, playerId));
                        appendEffect(
                                new DrawDestinyToPowerOnlyEffect(_action, opponent));

                        // Draw battle destinies
                        appendEffect(
                                new TriggeringResultEffect(_action, new BeforeBattleDestinyDrawsResult(playerId)));
                        appendEffect(
                                new CheckToDrawBattleDestinyEffect(_action, playerId));
                        appendEffect(
                                new TriggeringResultEffect(_action, new BattleDestinyDrawsCompleteForPlayerResult(playerId)));
                        appendEffect(
                                new CheckToDrawBattleDestinyEffect(_action, opponent));
                        appendEffect(
                                new TriggeringResultEffect(_action, new BattleDestinyDrawsCompleteForPlayerResult(opponent)));
                        appendEffect(
                                new TriggeringResultEffect(_action, new BattleDestinyDrawsCompleteForBothPlayersResult(playerId)));

                        // Draw destinies to attrition only
                        appendEffect(
                                new DrawDestinyToAttritionOnlyEffect(_action, playerId));
                        appendEffect(
                                new DrawDestinyToAttritionOnlyEffect(_action, opponent));

                        // Calculate initial attrition
                        appendEffect(
                                new CalculateInitialAttritionEffect(_action));
                    }
                }
        );
    }

    /**
     * A private effect that causes the specified player to draw destiny to power only.
     */
    private class DrawDestinyToPowerOnlyEffect extends AbstractSubActionEffect {
        private String _playerId;

        /**
         * Creates a private effect that causes the specified player to draw destiny to power only.
         * @param action the action performing this effect
         * @param playerId the player
         */
        private DrawDestinyToPowerOnlyEffect(Action action, String playerId) {
            super(action);
            _playerId = playerId;
        }

        @Override
        public boolean isPlayableInFull(SwccgGame game) {
            return true;
        }

        @Override
        protected SubAction getSubAction(SwccgGame game) {
            final GameState gameState = game.getGameState();
            final BattleState battleState = gameState.getBattleState();
            final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

            final SubAction subAction = new SubAction(_action, _playerId);

            // Determine how many destinies to power only player must draw
            final int numDraws = modifiersQuerying.getNumDestinyDrawsToTotalPowerOnly(gameState, _playerId, false, false);
            if (numDraws > 0) {
                if (gameState.getReserveDeckSize(_playerId) > 0) {
                    subAction.appendEffect(
                            new DrawDestinyEffect(_action, _playerId, numDraws, DestinyType.DESTINY_TO_TOTAL_POWER) {
                                @Override
                                protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                    if (totalDestiny != null) {
                                        battleState.increaseTotalDestinyToPowerOnly(_playerId, totalDestiny, destinyCardDraws.size());
                                    }
                                }
                            });
                }
                else {
                    gameState.sendMessage(_playerId + " can't draw destiny to total power. No more cards in Reserve Deck");
                }
            }

            return subAction;
        }

        @Override
        protected boolean wasActionCarriedOut() {
            return true;
        }
    }

    /**
     * A private effect that allows the specified player to draw battle destiny.
     */
    private class CheckToDrawBattleDestinyEffect extends AbstractSubActionEffect {
        private String _playerId;

        /**
         * Creates a private effect that allows the specified player to draw battle destiny.
         * @param action the action performing this effect
         * @param playerId the player
         */
        private CheckToDrawBattleDestinyEffect(Action action, String playerId) {
            super(action);
            _playerId = playerId;
        }

        @Override
        public boolean isPlayableInFull(SwccgGame game) {
            return true;
        }

        @Override
        protected SubAction getSubAction(SwccgGame game) {
            final GameState gameState = game.getGameState();
            final BattleState battleState = gameState.getBattleState();
            final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

            final SubAction subAction = new SubAction(_action, _playerId);

            // Determine how many battle destinies player can draw
            final int numDraws = modifiersQuerying.getNumBattleDestinyDraws(gameState, _playerId, false, false);
            final int numDrawsWithLimit = modifiersQuerying.getNumBattleDestinyDraws(gameState, _playerId, true, false);
            if (numDraws > 0 && numDrawsWithLimit > 0) {
                if (gameState.getReserveDeckSize(_playerId) > 0) {

                    String text = "Do you want to draw " + numDraws + " battle destiny";
                    if (numDrawsWithLimit < numDraws) {
                        text += " (limited to " + numDrawsWithLimit + " draw" + GameUtils.s(numDrawsWithLimit) + ")";
                    }
                    text += "?";

                    // Ask player if they want to draw battle destiny
                    game.getUserFeedback().sendAwaitingDecision(_playerId,
                            new MultipleChoiceAwaitingDecision(text, new String[]{"Yes", "No"}) {
                                @Override
                                protected void validDecisionMade(int index, String result) {
                                    if ("Yes".equals(result)) {
                                        subAction.appendEffect(
                                                new DrawDestinyEffect(_action, _playerId, numDraws, DestinyType.BATTLE_DESTINY) {
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        if (totalDestiny != null) {
                                                            boolean mayCancelBattleDestinyDraws = !game.getModifiersQuerying().mayNotCancelBattleDestinyDraws(game.getGameState(), _playerId, game.getOpponent(_playerId), false);
                                                            List<Boolean> cancelableByOpponent = new ArrayList<Boolean>();
                                                            for (PhysicalCard destinyCardDrawn : destinyCardDraws) {
                                                                cancelableByOpponent.add(mayCancelBattleDestinyDraws && destinyCardDrawn != null);
                                                            }
                                                            battleState.increaseTotalBattleDestinyFromDraws(_playerId, destinyCardDraws, cancelableByOpponent, destinyDrawValues, totalDestiny);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                }
                else {
                    gameState.sendMessage(_playerId + " can't draw battle destiny. No more cards in Reserve Deck");
                }
            }

            return subAction;
        }

        @Override
        protected boolean wasActionCarriedOut() {
            return true;
        }
    }

    /**
     * A private effect that causes the specified player to draw destiny to attrition only.
     */
    private class DrawDestinyToAttritionOnlyEffect extends AbstractSubActionEffect {
        private String _playerId;

        /**
         * Creates a private effect that causes the specified player to draw destiny to attrition only.
         * @param action the action performing this effect
         * @param playerId the player
         */
        private DrawDestinyToAttritionOnlyEffect(Action action, String playerId) {
            super(action);
            _playerId = playerId;
        }

        @Override
        public boolean isPlayableInFull(SwccgGame game) {
            return true;
        }

        @Override
        protected SubAction getSubAction(SwccgGame game) {
            final GameState gameState = game.getGameState();
            final BattleState battleState = gameState.getBattleState();
            final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

            final SubAction subAction = new SubAction(_action, _playerId);

            // Determine how many destinies to attrition only player must draw
            final int numDraws = modifiersQuerying.getNumDestinyDrawsToAttritionOnly(gameState, _playerId, false, false);
            if (numDraws > 0) {
                if (gameState.getReserveDeckSize(_playerId) > 0) {
                    battleState.setDrewDestinyToAttrition(_playerId, true);
                    subAction.appendEffect(
                            new DrawDestinyEffect(_action, _playerId, numDraws, DestinyType.DESTINY_TO_ATTRITION) {
                                @Override
                                protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                    if (totalDestiny != null) {
                                        battleState.increaseAttritionFromDestinyToAttritionDrawn(_playerId, totalDestiny, destinyCardDraws.size());
                                    }
                                }
                            });
                }
                else {
                    gameState.sendMessage(_playerId + " can't draw destiny to attrition. No more cards in Reserve Deck");
                }
            }

            return subAction;
        }

        @Override
        protected boolean wasActionCarriedOut() {
            return true;
        }
    }

    /**
     * A private effect that causes the initial attrition values to be calculated.
     */
    private class CalculateInitialAttritionEffect extends AbstractSuccessfulEffect {

        /**
         * Creates a private effect that causes the initial attrition values to be calculated.
         * @param action the action
         */
        private CalculateInitialAttritionEffect(Action action) {
            super(action);
        }

        @Override
        protected void doPlayEffect(final SwccgGame game) {
            // Determine the initial value of attrition for each player
            BattleState battleState = game.getGameState().getBattleState();
            battleState.increaseBaseAttrition(game.getDarkPlayer(), battleState.getTotalBattleDestiny(game, game.getLightPlayer()));
            battleState.increaseBaseAttrition(game.getLightPlayer(), battleState.getTotalBattleDestiny(game, game.getDarkPlayer()));
            battleState.baseAttritionCalculated();

            // Emit effect result that can trigger cards that can respond to the attrition being calculated
            game.getActionsEnvironment().emitEffectResult(
                    new InitialAttritionCalculatedResult(battleState.getPlayerInitiatedBattle()));
        }
    }
}
