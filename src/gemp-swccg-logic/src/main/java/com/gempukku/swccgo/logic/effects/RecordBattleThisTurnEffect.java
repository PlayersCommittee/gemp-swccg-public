package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.BattleThisTurnRecord;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Records a completed battle this turn (participants, winner/loser, battle damage)
 * so cards such as Apology Accepted can evaluate "survived a battle you lost this turn"
 * after BattleState is cleared. Same kind of memory write as RecordCardsBeingPlayedEffect.
 */
public class RecordBattleThisTurnEffect extends AbstractSuccessfulEffect {

    /**
     * Creates an effect that records the current battle, if any, into the turn-scoped battle log.
     * @param action the action performing this effect
     */
    public RecordBattleThisTurnEffect(Action action) {
        super(action);
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        BattleState battleState = game.getGameState().getBattleState();
        if (battleState == null || battleState.isCanceled()) {
            return;
        }

        PhysicalCard location = battleState.getBattleLocation();
        if (location == null) {
            return;
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
}
