package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.*;

/**
 * An effect that causes the performing player to choose and cancel previous battle destiny draws made by the specified player.
 */
public class CancelPreviousBattleDestinyDrawsEffect extends AbstractSubActionEffect {
    private String _battleDestinyOwner;
    private int _numberToRemain;

    /**
     * Creates an effect that causes the specified player to choose and cancel previous battle destiny draws.
     * @param action the action performing this effect
     * @param battleDestinyOwner the player whose battle destiny draws are canceled
     * @param numberToRemain the number of battle destinies that must remaining after others are canceled
     */
    public CancelPreviousBattleDestinyDrawsEffect(Action action, String battleDestinyOwner, int numberToRemain) {
        super(action);
        _battleDestinyOwner = battleDestinyOwner;
        _numberToRemain = numberToRemain;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();
        final BattleState battleState = gameState.getBattleState();
        final String performingPlayerId = _action.getPerformingPlayer();

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(final SwccgGame game) {
                        final List<PhysicalCard> cancelableDestinyDraws = new LinkedList<PhysicalCard>();
                        final List<Float> cancelableDestinyDrawValues = new LinkedList<Float>();
                        final List<Integer> originalIndexValue = new LinkedList<Integer>();

                        List<PhysicalCard> battleDestinyDraws = battleState.getBattleDestinyDraws(_battleDestinyOwner);
                        List<Float> battleDestinyDrawValues = battleState.getBattleDestinyDrawsValues(_battleDestinyOwner);
                        List<Boolean> battleDestinyDrawnCancelableByOpponent = battleState.getBattleDestinyDrawsCancelableByOpponent(_battleDestinyOwner);

                        for (int i=0; i<battleDestinyDrawnCancelableByOpponent.size(); ++i) {
                            if (battleDestinyDrawnCancelableByOpponent.get(i)) {
                                cancelableDestinyDraws.add(battleDestinyDraws.get(i));
                                cancelableDestinyDrawValues.add(battleDestinyDrawValues.get(i));
                                originalIndexValue.add(i);
                            }
                        }
                        int numToChoose = Math.min(originalIndexValue.size(), battleDestinyDraws.size() - _numberToRemain);
                        if (numToChoose > 0) {

                            // Create map of destiny text
                            Map<PhysicalCard, String> cardTextMap = new HashMap<PhysicalCard, String>();
                            for (int i = 0; i < originalIndexValue.size(); ++i) {
                                cardTextMap.put(cancelableDestinyDraws.get(i), "destiny = " + GuiUtils.formatAsString(cancelableDestinyDrawValues.get(i)));
                            }
                            game.getGameState().sendMessage(performingPlayerId + " is choosing battle destiny draw" + GameUtils.s(numToChoose) + " to cancel");

                            subAction.appendEffect(
                                    new PlayoutDecisionEffect(subAction, performingPlayerId,
                                            new ArbitraryCardsSelectionDecision("Choose " + numToChoose + " battle destiny draw" + GameUtils.s(numToChoose) + " to cancel", cancelableDestinyDraws, cancelableDestinyDraws, numToChoose, numToChoose, cardTextMap) {
                                                @Override
                                                public void decisionMade(String result) throws DecisionResultInvalidException {
                                                    List<Integer> selectedIndexes = getIndexesByResponse(result);
                                                    Set<Integer> originalIndexesToCancel = new HashSet<Integer>();

                                                    StringBuilder msgText = new StringBuilder(performingPlayerId);
                                                    msgText.append(" chooses for battle destiny draws of ");
                                                    for (Integer selectedIndex : selectedIndexes) {
                                                        msgText.append(GameUtils.getCardLink(cancelableDestinyDraws.get(selectedIndex))).append(" (destiny = ").append(GuiUtils.formatAsString(cancelableDestinyDrawValues.get(selectedIndex))).append("), ");
                                                        originalIndexesToCancel.add(originalIndexValue.get(selectedIndex));
                                                    }
                                                    msgText.setLength(msgText.length() - 2);
                                                    msgText.append(" to be canceled");
                                                    gameState.sendMessage(msgText.toString());
                                                    battleState.cancelPreviousBattleDestinyDraws(_battleDestinyOwner, originalIndexesToCancel);

                                                    game.getGameState().sendMessage(_battleDestinyOwner + "'s total " + DestinyType.BATTLE_DESTINY.getHumanReadable() + " is " + GuiUtils.formatAsString(battleState.getTotalBattleDestiny(game, _battleDestinyOwner)));
                                                }
                                            }
                                    )
                            );
                        }
                    }
                }
        );
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
