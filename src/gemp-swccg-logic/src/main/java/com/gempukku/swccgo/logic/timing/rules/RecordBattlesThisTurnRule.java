package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.BattleThisTurnRecord;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.BattleEndedResult;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Records each completed battle this turn (participants, winner/loser, battle damage)
 * so cards such as Apology Accepted can evaluate "survived a battle you lost this turn"
 * after the BattleState is cleared.
 */
public class RecordBattlesThisTurnRule implements Rule {
    private final ActionsEnvironment _actionsEnvironment;

    public RecordBattlesThisTurnRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        if (TriggerConditions.battleEnded(game, effectResult)) {
                            BattleEndedResult battleEndedResult = (BattleEndedResult) effectResult;
                            BattleState battleState = battleEndedResult.getBattleState();
                            if (battleState == null || battleState.isCanceled()) {
                                return null;
                            }

                            PhysicalCard location = battleEndedResult.getLocation();
                            if (location == null) {
                                return null;
                            }

                            Map<String, Float> battleDamageByPlayer = new HashMap<String, Float>();
                            String dark = game.getDarkPlayer();
                            String light = game.getLightPlayer();
                            battleDamageByPlayer.put(dark, battleState.getBaseBattleDamage(dark));
                            battleDamageByPlayer.put(light, battleState.getBaseBattleDamage(light));

                            Set<Integer> participantIds = new HashSet<Integer>();
                            Collection<PhysicalCard> whenDetermined = battleState.getAllCardsParticipatingWhenResultDetermined();
                            Collection<PhysicalCard> participants = (whenDetermined != null && !whenDetermined.isEmpty())
                                    ? whenDetermined
                                    : battleState.getAllCardsParticipating();
                            if (participants != null) {
                                for (PhysicalCard participant : participants) {
                                    if (participant != null) {
                                        participantIds.add(participant.getCardId());
                                    }
                                }
                            }

                            BattleThisTurnRecord record = new BattleThisTurnRecord(
                                    location.getCardId(),
                                    battleState.isWinner(dark) ? dark : (battleState.isWinner(light) ? light : null),
                                    battleState.isLoser(dark) ? dark : (battleState.isLoser(light) ? light : null),
                                    battleDamageByPlayer,
                                    participantIds);
                            game.getModifiersQuerying().recordBattleThisTurn(record);
                        }
                        return null;
                    }
                }
        );
    }
}
