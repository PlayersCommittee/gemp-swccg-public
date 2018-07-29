package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.state.*;
import com.gempukku.swccgo.game.state.actions.GameTextActionState;
import com.gempukku.swccgo.game.state.actions.PlayCardState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.GameTextAction;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.DuelEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.*;

import java.util.*;

// This class implements the applying of the modifiers.
//
public class ModifiersLogic implements ModifiersEnvironment, ModifiersQuerying, Snapshotable<ModifiersLogic> {
    private SwccgGame _swccgGame;
    private Map<ModifierType, List<Modifier>> _modifiers = new HashMap<ModifierType, List<Modifier>>();
    private Map<Modifier, Set<Integer>> _excludedFromBeingAffected = new HashMap<Modifier, Set<Integer>>();

    private List<Modifier> _untilEndOfTurnModifiers = new LinkedList<Modifier>();
    private List<Modifier> _untilEndOfForceDrainModifiers = new LinkedList<Modifier>();
    private Map<Integer, List<Modifier>> _untilEndOfForceLossModifiers = new HashMap<Integer, List<Modifier>>();
    private Map<Integer, List<Modifier>> _untilEndOfForceRetrievalModifiers = new HashMap<Integer, List<Modifier>>();
    private Map<Integer, List<Modifier>> _untilEndOfDrawDestinyModifiers = new HashMap<Integer, List<Modifier>>();
    private Map<Integer, List<Modifier>> _untilEndOfEachDrawnDestinyModifiers = new HashMap<Integer, List<Modifier>>();
    private Map<Integer, List<Modifier>> _untilEndOfBlowAwayModifiers = new HashMap<Integer, List<Modifier>>();
    private Map<PhysicalCard, List<Modifier>> _untilEndOfCardPlayedModifiers = new HashMap<PhysicalCard, List<Modifier>>();
    private Map<EffectResult, List<Modifier>> _untilEndOfEffectResultModifiers = new HashMap<EffectResult, List<Modifier>>();
    private Map<Integer, List<Modifier>> _untilEndOfGameTextActionModifiers = new HashMap<Integer, List<Modifier>>();
    private List<Modifier> _untilEndOfWeaponFiringModifiers = new LinkedList<Modifier>();
    private List<Modifier> _untilDamageSegmentOfBattleModifiers = new LinkedList<Modifier>();
    private List<Modifier> _untilEndOfBattleModifiers = new LinkedList<Modifier>();
    private List<Modifier> _untilEndOfAttackModifiers = new LinkedList<Modifier>();
    private List<Modifier> _untilEndOfDuelModifiers = new LinkedList<Modifier>();
    private List<Modifier> _untilEndOfLightsaberCombatModifiers = new LinkedList<Modifier>();
    private List<Modifier> _untilEndOfEpicEventModifiers = new LinkedList<Modifier>();
    private List<Modifier> _untilEndOfSabaccModifiers = new LinkedList<Modifier>();
    private Map<String, List<Modifier>> _untilEndOfPlayersNextTurnModifiers = new HashMap<String, List<Modifier>>();

    private Set<Modifier> _skipSet = new HashSet<Modifier>();

    private Map<Phase, Map<String, LimitCounter>> _endOfPhaseLimitCounters = new HashMap<Phase, Map<String, LimitCounter>>();
    private Map<Phase, Map<String, LimitCounter>> _startOfPhaseLimitCounters = new HashMap<Phase, Map<String, LimitCounter>>();
    private Map<String, LimitCounter> _forceDrainLimitCounters = new HashMap<String, LimitCounter>();
    private Map<String, LimitCounter> _gameLimitCounters = new HashMap<String, LimitCounter>();
    private Map<String, LimitCounter> _turnLimitCounters = new HashMap<String, LimitCounter>();
    private Map<String, LimitCounter> _turnForCardTitleLimitCounters = new HashMap<String, LimitCounter>();
    private Map<String, LimitCounter> _battleLimitCounters = new HashMap<String, LimitCounter>();
    private Map<String, LimitCounter> _duelLimitCounters = new HashMap<String, LimitCounter>();
    private Map<Integer, Map<String, LimitCounter>> _forceLossLimitCounters = new HashMap<Integer, Map<String, LimitCounter>>();
    private Map<String, LimitCounter> _cardTitlePlayedTurnLimitCounters = new HashMap<String, LimitCounter>();
    private Map<Integer, Map<String, LimitCounter>> _captivityLimitCounters = new HashMap<Integer, Map<String, LimitCounter>>();
    private Map<Float, Map<String, LimitCounter>> _raceTotalLimitCounters = new HashMap<Float, Map<String, LimitCounter>>();

    // These collections are used to keep track of game stats for a period of time
    // for modifiers and game rules that have a limit to how many times something
    // is allowed within that period of time.
    private Map<String, Integer> _forceGenerationActivatedThisTurnMap = new HashMap<String, Integer>();
    private Map<String, Integer> _forceActivatedThisTurnMap = new HashMap<String, Integer>();
    private Map<Phase, Map<String, Integer>> _forceActivatedPerPhaseMap = new HashMap<Phase, Map<String, Integer>>();
    private Set<PhysicalCard> _forceDrainStartedSet = new HashSet<PhysicalCard>();
    private Map<PhysicalCard, Float> _forceDrainCompletedMap = new HashMap<PhysicalCard, Float>();
    private Set<Integer> _regularMoveSet = new HashSet<Integer>();
    private Map<String, Integer> _battleInitiatedByPlayerMap = new HashMap<String, Integer>();
    private Map<Integer, Integer> _locationBattleMap = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> _forceDrainParticipationMap = new HashMap<Integer, Integer>();
    private Set<Integer> _locationAttackOnCreatureSet = new HashSet<Integer>();
    private Set<Integer> _attackOnCreatureParticipationSet = new HashSet<Integer>();
    private Set<Integer> _attackOnNonCreatureParticipationSet = new HashSet<Integer>();
    private Map<Integer, Integer> _battleParticipationMap = new HashMap<Integer, Integer>();
    private Map<Integer, List<Integer>> _usedDevicesMap = new HashMap<Integer, List<Integer>>();
    private Map<Integer, List<Integer>> _usedWeaponsMap = new HashMap<Integer, List<Integer>>();
    private Map<String, Integer> _firedInAttackMap = new HashMap<String, Integer>();
    private Map<String, Integer> _firedInAttackCompletedMap = new HashMap<String, Integer>();
    private Map<String, Map<String, PhysicalCard>> _firedInAttackByPlayerMap = new HashMap<String, Map<String, PhysicalCard>>();
    private Map<String, Map<String, PhysicalCard>> _firedInAttackByPlayerCompletedMap = new HashMap<String, Map<String, PhysicalCard>>();
    private Map<String, Map<String, SwccgBuiltInCardBlueprint>> _permWeaponFiredInAttackByPlayerMap = new HashMap<String, Map<String, SwccgBuiltInCardBlueprint>>();
    private Map<String, Map<String, SwccgBuiltInCardBlueprint>> _permWeaponFiredInAttackByPlayerCompletedMap = new HashMap<String, Map<String, SwccgBuiltInCardBlueprint>>();
    private Map<String, Map<String, PhysicalCard>> _firedInAttackByCardMap = new HashMap<String, Map<String, PhysicalCard>>();
    private Map<String, Map<String, PhysicalCard>> _firedInAttackByCardCompletedMap = new HashMap<String, Map<String, PhysicalCard>>();
    private Map<String, Map<String, SwccgBuiltInCardBlueprint>> _permWeaponFiredInAttackByCardMap = new HashMap<String, Map<String, SwccgBuiltInCardBlueprint>>();
    private Map<String, Map<String, SwccgBuiltInCardBlueprint>> _permWeaponFiredInAttackByCardCompletedMap = new HashMap<String, Map<String, SwccgBuiltInCardBlueprint>>();
    private Map<String, Integer> _firedInAttackRunMap = new HashMap<String, Integer>();
    private Map<String, Integer> _firedInAttackRunCompletedMap = new HashMap<String, Integer>();
    private Map<String, Integer> _firedInBattleMap = new HashMap<String, Integer>();
    private Map<String, Integer> _firedInBattleCompletedMap = new HashMap<String, Integer>();
    private Map<String, Map<String, PhysicalCard>> _firedInBattleByPlayerMap = new HashMap<String, Map<String, PhysicalCard>>();
    private Map<String, Map<String, PhysicalCard>> _firedInBattleByPlayerCompletedMap = new HashMap<String, Map<String, PhysicalCard>>();
    private Map<String, Map<String, SwccgBuiltInCardBlueprint>> _permWeaponFiredInBattleByPlayerMap = new HashMap<String, Map<String, SwccgBuiltInCardBlueprint>>();
    private Map<String, Map<String, SwccgBuiltInCardBlueprint>> _permWeaponFiredInBattleByPlayerCompletedMap = new HashMap<String, Map<String, SwccgBuiltInCardBlueprint>>();
    private Map<String, Map<String, PhysicalCard>> _firedInBattleByCardMap = new HashMap<String, Map<String, PhysicalCard>>();
    private Map<String, Map<String, PhysicalCard>> _firedInBattleByCardCompletedMap = new HashMap<String, Map<String, PhysicalCard>>();
    private Map<String, Map<String, SwccgBuiltInCardBlueprint>> _permWeaponFiredInBattleByCardMap = new HashMap<String, Map<String, SwccgBuiltInCardBlueprint>>();
    private Map<String, Map<String, SwccgBuiltInCardBlueprint>> _permWeaponFiredInBattleByCardCompletedMap = new HashMap<String, Map<String, SwccgBuiltInCardBlueprint>>();
    private Map<String, Integer> _specialDownloadBattlegroundMap = new HashMap<String, Integer>();
    private Map<PhysicalCard, Set<PhysicalCard>> _forfeitedFromLocationMap = new HashMap<PhysicalCard, Set<PhysicalCard>>();
    private Map<Integer, Set<Integer>> _asteroidDestinyDrawnAgainstMap = new HashMap<Integer, Set<Integer>>();
    private Map<String, List<PhysicalCard>> _cardPlayedThisGame = new HashMap<String, List<PhysicalCard>>();
    private Map<String, List<PhysicalCard>> _cardPlayedThisTurn = new HashMap<String, List<PhysicalCard>>();
    private Map<String, List<PhysicalCard>> _cardWithAbilityDeployedThisTurn = new HashMap<String, List<PhysicalCard>>();
    private Map<String, Map<Integer, List<PhysicalCard>>> _cardPlayedToLocationThisTurn = new HashMap<String, Map<Integer, List<PhysicalCard>>>();
    private boolean _bluffCardStacked;
    private Set<String> _usedCombatCard = new HashSet<String>();
    private Map<Integer, List<PhysicalCard>> _targetedByWeaponsMap = new HashMap<Integer, List<PhysicalCard>>();
    private Map<Integer, List<SwccgBuiltInCardBlueprint>> _targetedByPermanentWeaponsMap = new HashMap<Integer, List<SwccgBuiltInCardBlueprint>>();
    private Map<Integer, PhysicalCard> _attemptedJediTestThisTurnMap = new HashMap<Integer, PhysicalCard>();

    private Set<PhysicalCard> _blownAwayCards = new HashSet<PhysicalCard>();
    private Set<PhysicalCard> _cardsThatWonSabacc = new HashSet<PhysicalCard>();
    private Set<Persona> _personasCrossedOver = new HashSet<Persona>();
    private Map<String, List<PhysicalCard>> _completedUtinniEffect = new HashMap<String, List<PhysicalCard>>();
    private Map<Integer, PhysicalCard> _completedJediTest = new HashMap<Integer, PhysicalCard>();

    /**
     * Needed to generate snapshot.
     */
    public ModifiersLogic() {
    }

    @Override
    public void generateSnapshot(ModifiersLogic selfSnapshot, SnapshotData snapshotData) {
        ModifiersLogic snapshot = selfSnapshot;

        // Set each field
        snapshot._swccgGame = _swccgGame;
        for (ModifierType modifierType : _modifiers.keySet()) {
            List<Modifier> snapshotList = new LinkedList<Modifier>(_modifiers.get(modifierType));
            snapshot._modifiers.put(modifierType, snapshotList);
        }
        for (Modifier modifier : _excludedFromBeingAffected.keySet()) {
            Set<Integer> snapshotSet = new HashSet<Integer>(_excludedFromBeingAffected.get(modifier));
            snapshot._excludedFromBeingAffected.put(modifier, snapshotSet);
        }
        snapshot._untilEndOfTurnModifiers.addAll(_untilEndOfTurnModifiers);
        snapshot._untilEndOfForceDrainModifiers.addAll(_untilEndOfForceDrainModifiers);
        for (Integer id : _untilEndOfForceLossModifiers.keySet()) {
            List<Modifier> snapshotList = new LinkedList<Modifier>(_untilEndOfForceLossModifiers.get(id));
            snapshot._untilEndOfForceLossModifiers.put(id, snapshotList);
        }
        for (Integer id : _untilEndOfForceRetrievalModifiers.keySet()) {
            List<Modifier> snapshotList = new LinkedList<Modifier>(_untilEndOfForceRetrievalModifiers.get(id));
            snapshot._untilEndOfForceRetrievalModifiers.put(id, snapshotList);
        }
        for (Integer id : _untilEndOfDrawDestinyModifiers.keySet()) {
            List<Modifier> snapshotList = new LinkedList<Modifier>(_untilEndOfDrawDestinyModifiers.get(id));
            snapshot._untilEndOfDrawDestinyModifiers.put(id, snapshotList);
        }
        for (Integer id : _untilEndOfEachDrawnDestinyModifiers.keySet()) {
            List<Modifier> snapshotList = new LinkedList<Modifier>(_untilEndOfEachDrawnDestinyModifiers.get(id));
            snapshot._untilEndOfEachDrawnDestinyModifiers.put(id, snapshotList);
        }
        for (Integer id : _untilEndOfBlowAwayModifiers.keySet()) {
            List<Modifier> snapshotList = new LinkedList<Modifier>(_untilEndOfBlowAwayModifiers.get(id));
            snapshot._untilEndOfBlowAwayModifiers.put(id, snapshotList);
        }
        for (PhysicalCard card : _untilEndOfCardPlayedModifiers.keySet()) {
            PhysicalCard snapshotCard = snapshotData.getDataForSnapshot(card);
            List<Modifier> snapshotList = new LinkedList<Modifier>(_untilEndOfCardPlayedModifiers.get(card));
            snapshot._untilEndOfCardPlayedModifiers.put(snapshotCard, snapshotList);
        }
        if (!_untilEndOfEffectResultModifiers.isEmpty()) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " with UntilEndOfEffectResultModifiers");
        }
        for (Integer id : _untilEndOfGameTextActionModifiers.keySet()) {
            List<Modifier> snapshotList = new LinkedList<Modifier>(_untilEndOfGameTextActionModifiers.get(id));
            snapshot._untilEndOfGameTextActionModifiers.put(id, snapshotList);
        }
        snapshot._untilEndOfWeaponFiringModifiers.addAll(_untilEndOfWeaponFiringModifiers);
        snapshot._untilDamageSegmentOfBattleModifiers.addAll(_untilDamageSegmentOfBattleModifiers);
        snapshot._untilEndOfBattleModifiers.addAll(_untilEndOfBattleModifiers);
        snapshot._untilEndOfAttackModifiers.addAll(_untilEndOfAttackModifiers);
        snapshot._untilEndOfDuelModifiers.addAll(_untilEndOfDuelModifiers);
        snapshot._untilEndOfLightsaberCombatModifiers.addAll(_untilEndOfLightsaberCombatModifiers);
        snapshot._untilEndOfEpicEventModifiers.addAll(_untilEndOfEpicEventModifiers);
        snapshot._untilEndOfSabaccModifiers.addAll(_untilEndOfSabaccModifiers);
        for (String playerId : _untilEndOfPlayersNextTurnModifiers.keySet()) {
            List<Modifier> snapshotList = new LinkedList<Modifier>(_untilEndOfPlayersNextTurnModifiers.get(playerId));
            snapshot._untilEndOfPlayersNextTurnModifiers.put(playerId, snapshotList);
        }
        snapshot._skipSet.addAll(_skipSet);
        for (Phase phase : _endOfPhaseLimitCounters.keySet()) {
            Map<String, LimitCounter> snapshotMap = new HashMap<String, LimitCounter>();
            snapshot._endOfPhaseLimitCounters.put(phase, snapshotMap);
            for (Map.Entry<String, LimitCounter> entry : _endOfPhaseLimitCounters.get(phase).entrySet()) {
                snapshotMap.put(entry.getKey(), snapshotData.getDataForSnapshot(entry.getValue()));
            }
        }
        for (Phase phase : _startOfPhaseLimitCounters.keySet()) {
            Map<String, LimitCounter> snapshotMap = new HashMap<String, LimitCounter>();
            snapshot._startOfPhaseLimitCounters.put(phase, snapshotMap);
            for (Map.Entry<String, LimitCounter> entry : _startOfPhaseLimitCounters.get(phase).entrySet()) {
                snapshotMap.put(entry.getKey(), snapshotData.getDataForSnapshot(entry.getValue()));
            }
        }
        for (Map.Entry<String, LimitCounter> entry : _forceDrainLimitCounters.entrySet()) {
            snapshot._forceDrainLimitCounters.put(entry.getKey(), snapshotData.getDataForSnapshot(entry.getValue()));
        }
        for (Map.Entry<String, LimitCounter> entry : _gameLimitCounters.entrySet()) {
            snapshot._gameLimitCounters.put(entry.getKey(), snapshotData.getDataForSnapshot(entry.getValue()));
        }
        for (Map.Entry<String, LimitCounter> entry : _turnLimitCounters.entrySet()) {
            snapshot._turnLimitCounters.put(entry.getKey(), snapshotData.getDataForSnapshot(entry.getValue()));
        }
        for (Map.Entry<String, LimitCounter> entry : _turnForCardTitleLimitCounters.entrySet()) {
            snapshot._turnForCardTitleLimitCounters.put(entry.getKey(), snapshotData.getDataForSnapshot(entry.getValue()));
        }
        for (Map.Entry<String, LimitCounter> entry : _battleLimitCounters.entrySet()) {
            snapshot._battleLimitCounters.put(entry.getKey(), snapshotData.getDataForSnapshot(entry.getValue()));
        }
        for (Map.Entry<String, LimitCounter> entry : _duelLimitCounters.entrySet()) {
            snapshot._duelLimitCounters.put(entry.getKey(), snapshotData.getDataForSnapshot(entry.getValue()));
        }
        for (Integer forceLossId : _forceLossLimitCounters.keySet()) {
            Map<String, LimitCounter> snapshotMap = new HashMap<String, LimitCounter>();
            snapshot._forceLossLimitCounters.put(forceLossId, snapshotMap);
            for (Map.Entry<String, LimitCounter> entry : _forceLossLimitCounters.get(forceLossId).entrySet()) {
                snapshotMap.put(entry.getKey(), snapshotData.getDataForSnapshot(entry.getValue()));
            }
        }
        for (Map.Entry<String, LimitCounter> entry : _cardTitlePlayedTurnLimitCounters.entrySet()) {
            snapshot._cardTitlePlayedTurnLimitCounters.put(entry.getKey(), snapshotData.getDataForSnapshot(entry.getValue()));
        }
        for (Integer cardId : _captivityLimitCounters.keySet()) {
            Map<String, LimitCounter> snapshotMap = new HashMap<String, LimitCounter>();
            snapshot._captivityLimitCounters.put(cardId, snapshotMap);
            for (Map.Entry<String, LimitCounter> entry : _captivityLimitCounters.get(cardId).entrySet()) {
                snapshotMap.put(entry.getKey(), snapshotData.getDataForSnapshot(entry.getValue()));
            }
        }
        for (Float raceTotal : _raceTotalLimitCounters.keySet()) {
            Map<String, LimitCounter> snapshotMap = new HashMap<String, LimitCounter>();
            snapshot._raceTotalLimitCounters.put(raceTotal, snapshotMap);
            for (Map.Entry<String, LimitCounter> entry : _raceTotalLimitCounters.get(raceTotal).entrySet()) {
                snapshotMap.put(entry.getKey(), snapshotData.getDataForSnapshot(entry.getValue()));
            }
        }
        snapshot._forceGenerationActivatedThisTurnMap.putAll(_forceGenerationActivatedThisTurnMap);
        snapshot._forceActivatedThisTurnMap.putAll(_forceActivatedThisTurnMap);
        for (Phase phase : _forceActivatedPerPhaseMap.keySet()) {
            Map<String, Integer> snapshotMap = new HashMap<String, Integer>(_forceActivatedPerPhaseMap.get(phase));
            snapshot._forceActivatedPerPhaseMap.put(phase, snapshotMap);
        }
        for (PhysicalCard card : _forceDrainStartedSet) {
            snapshot._forceDrainStartedSet.add(snapshotData.getDataForSnapshot(card));
        }
        for (Map.Entry<PhysicalCard, Float> entry : _forceDrainCompletedMap.entrySet()) {
            snapshot._forceDrainCompletedMap.put(snapshotData.getDataForSnapshot(entry.getKey()), entry.getValue());
        }
        snapshot._regularMoveSet.addAll(_regularMoveSet);
        snapshot._battleInitiatedByPlayerMap.putAll(_battleInitiatedByPlayerMap);
        snapshot._locationBattleMap.putAll(_locationBattleMap);
        snapshot._forceDrainParticipationMap.putAll(_forceDrainParticipationMap);
        snapshot._locationAttackOnCreatureSet.addAll(_locationAttackOnCreatureSet);
        snapshot._attackOnCreatureParticipationSet.addAll(_attackOnCreatureParticipationSet);
        snapshot._attackOnNonCreatureParticipationSet.addAll(_attackOnNonCreatureParticipationSet);
        snapshot._battleParticipationMap.putAll(_battleParticipationMap);
        for (Integer cardId : _usedDevicesMap.keySet()) {
            List<Integer> snapshotList = new LinkedList<Integer>(_usedDevicesMap.get(cardId));
            snapshot._usedDevicesMap.put(cardId, snapshotList);
        }
        for (Integer cardId : _usedWeaponsMap.keySet()) {
            List<Integer> snapshotList = new LinkedList<Integer>(_usedWeaponsMap.get(cardId));
            snapshot._usedWeaponsMap.put(cardId, snapshotList);
        }
        snapshot._firedInAttackMap.putAll(_firedInAttackMap);
        snapshot._firedInAttackCompletedMap.putAll(_firedInAttackCompletedMap);
        for (String playerId : _firedInAttackByPlayerMap.keySet()) {
            Map<String, PhysicalCard> snapshotMap = new HashMap<String, PhysicalCard>();
            snapshot._firedInAttackByPlayerMap.put(playerId, snapshotMap);
            for (Map.Entry<String, PhysicalCard> entry : _firedInAttackByPlayerMap.get(playerId).entrySet()) {
                snapshotMap.put(entry.getKey(), snapshotData.getDataForSnapshot(entry.getValue()));
            }
        }
        for (String playerId : _firedInAttackByPlayerCompletedMap.keySet()) {
            Map<String, PhysicalCard> snapshotMap = new HashMap<String, PhysicalCard>();
            snapshot._firedInAttackByPlayerCompletedMap.put(playerId, snapshotMap);
            for (Map.Entry<String, PhysicalCard> entry : _firedInAttackByPlayerCompletedMap.get(playerId).entrySet()) {
                snapshotMap.put(entry.getKey(), snapshotData.getDataForSnapshot(entry.getValue()));
            }
        }
        for (String playerId : _permWeaponFiredInAttackByPlayerMap.keySet()) {
            Map<String, SwccgBuiltInCardBlueprint> snapshotMap = new HashMap<String, SwccgBuiltInCardBlueprint>();
            snapshot._permWeaponFiredInAttackByPlayerMap.put(playerId, snapshotMap);
            for (Map.Entry<String, SwccgBuiltInCardBlueprint> entry : _permWeaponFiredInAttackByPlayerMap.get(playerId).entrySet()) {
                snapshotMap.put(entry.getKey(), entry.getValue());
            }
        }
        for (String playerId : _permWeaponFiredInAttackByPlayerCompletedMap.keySet()) {
            Map<String, SwccgBuiltInCardBlueprint> snapshotMap = new HashMap<String, SwccgBuiltInCardBlueprint>();
            snapshot._permWeaponFiredInAttackByPlayerCompletedMap.put(playerId, snapshotMap);
            for (Map.Entry<String, SwccgBuiltInCardBlueprint> entry : _permWeaponFiredInAttackByPlayerCompletedMap.get(playerId).entrySet()) {
                snapshotMap.put(entry.getKey(), entry.getValue());
            }
        }
        for (String cardId : _firedInAttackByCardMap.keySet()) {
            Map<String, PhysicalCard> snapshotMap = new HashMap<String, PhysicalCard>();
            snapshot._firedInAttackByCardMap.put(cardId, snapshotMap);
            for (Map.Entry<String, PhysicalCard> entry : _firedInAttackByCardMap.get(cardId).entrySet()) {
                snapshotMap.put(entry.getKey(), snapshotData.getDataForSnapshot(entry.getValue()));
            }
        }
        for (String cardId : _firedInAttackByCardCompletedMap.keySet()) {
            Map<String, PhysicalCard> snapshotMap = new HashMap<String, PhysicalCard>();
            snapshot._firedInAttackByCardCompletedMap.put(cardId, snapshotMap);
            for (Map.Entry<String, PhysicalCard> entry : _firedInAttackByCardCompletedMap.get(cardId).entrySet()) {
                snapshotMap.put(entry.getKey(), snapshotData.getDataForSnapshot(entry.getValue()));
            }
        }
        for (String cardId : _permWeaponFiredInAttackByCardMap.keySet()) {
            Map<String, SwccgBuiltInCardBlueprint> snapshotMap = new HashMap<String, SwccgBuiltInCardBlueprint>();
            snapshot._permWeaponFiredInAttackByCardMap.put(cardId, snapshotMap);
            for (Map.Entry<String, SwccgBuiltInCardBlueprint> entry : _permWeaponFiredInAttackByCardMap.get(cardId).entrySet()) {
                snapshotMap.put(entry.getKey(), entry.getValue());
            }
        }
        for (String cardId : _permWeaponFiredInAttackByCardCompletedMap.keySet()) {
            Map<String, SwccgBuiltInCardBlueprint> snapshotMap = new HashMap<String, SwccgBuiltInCardBlueprint>();
            snapshot._permWeaponFiredInAttackByCardCompletedMap.put(cardId, snapshotMap);
            for (Map.Entry<String, SwccgBuiltInCardBlueprint> entry : _permWeaponFiredInAttackByCardCompletedMap.get(cardId).entrySet()) {
                snapshotMap.put(entry.getKey(), entry.getValue());
            }
        }
        snapshot._firedInAttackRunMap.putAll(_firedInAttackRunMap);
        snapshot._firedInAttackRunCompletedMap.putAll(_firedInAttackRunCompletedMap);
        snapshot._firedInBattleMap.putAll(_firedInBattleMap);
        snapshot._firedInBattleCompletedMap.putAll(_firedInBattleCompletedMap);
        for (String playerId : _firedInBattleByPlayerMap.keySet()) {
            Map<String, PhysicalCard> snapshotMap = new HashMap<String, PhysicalCard>();
            snapshot._firedInBattleByPlayerMap.put(playerId, snapshotMap);
            for (Map.Entry<String, PhysicalCard> entry : _firedInBattleByPlayerMap.get(playerId).entrySet()) {
                snapshotMap.put(entry.getKey(), snapshotData.getDataForSnapshot(entry.getValue()));
            }
        }
        for (String playerId : _firedInBattleByPlayerCompletedMap.keySet()) {
            Map<String, PhysicalCard> snapshotMap = new HashMap<String, PhysicalCard>();
            snapshot._firedInBattleByPlayerCompletedMap.put(playerId, snapshotMap);
            for (Map.Entry<String, PhysicalCard> entry : _firedInBattleByPlayerCompletedMap.get(playerId).entrySet()) {
                snapshotMap.put(entry.getKey(), snapshotData.getDataForSnapshot(entry.getValue()));
            }
        }
        for (String playerId : _permWeaponFiredInBattleByPlayerMap.keySet()) {
            Map<String, SwccgBuiltInCardBlueprint> snapshotMap = new HashMap<String, SwccgBuiltInCardBlueprint>();
            snapshot._permWeaponFiredInBattleByPlayerMap.put(playerId, snapshotMap);
            for (Map.Entry<String, SwccgBuiltInCardBlueprint> entry : _permWeaponFiredInBattleByPlayerMap.get(playerId).entrySet()) {
                snapshotMap.put(entry.getKey(), entry.getValue());
            }
        }
        for (String playerId : _permWeaponFiredInBattleByPlayerCompletedMap.keySet()) {
            Map<String, SwccgBuiltInCardBlueprint> snapshotMap = new HashMap<String, SwccgBuiltInCardBlueprint>();
            snapshot._permWeaponFiredInBattleByPlayerCompletedMap.put(playerId, snapshotMap);
            for (Map.Entry<String, SwccgBuiltInCardBlueprint> entry : _permWeaponFiredInBattleByPlayerCompletedMap.get(playerId).entrySet()) {
                snapshotMap.put(entry.getKey(), entry.getValue());
            }
        }
        for (String cardId : _firedInBattleByCardMap.keySet()) {
            Map<String, PhysicalCard> snapshotMap = new HashMap<String, PhysicalCard>();
            snapshot._firedInBattleByCardMap.put(cardId, snapshotMap);
            for (Map.Entry<String, PhysicalCard> entry : _firedInBattleByCardMap.get(cardId).entrySet()) {
                snapshotMap.put(entry.getKey(), snapshotData.getDataForSnapshot(entry.getValue()));
            }
        }
        for (String cardId : _firedInBattleByCardCompletedMap.keySet()) {
            Map<String, PhysicalCard> snapshotMap = new HashMap<String, PhysicalCard>();
            snapshot._firedInBattleByCardCompletedMap.put(cardId, snapshotMap);
            for (Map.Entry<String, PhysicalCard> entry : _firedInBattleByCardCompletedMap.get(cardId).entrySet()) {
                snapshotMap.put(entry.getKey(), snapshotData.getDataForSnapshot(entry.getValue()));
            }
        }
        for (String cardId : _permWeaponFiredInBattleByCardMap.keySet()) {
            Map<String, SwccgBuiltInCardBlueprint> snapshotMap = new HashMap<String, SwccgBuiltInCardBlueprint>();
            snapshot._permWeaponFiredInBattleByCardMap.put(cardId, snapshotMap);
            for (Map.Entry<String, SwccgBuiltInCardBlueprint> entry : _permWeaponFiredInBattleByCardMap.get(cardId).entrySet()) {
                snapshotMap.put(entry.getKey(), entry.getValue());
            }
        }
        for (String cardId : _permWeaponFiredInBattleByCardCompletedMap.keySet()) {
            Map<String, SwccgBuiltInCardBlueprint> snapshotMap = new HashMap<String, SwccgBuiltInCardBlueprint>();
            snapshot._permWeaponFiredInBattleByCardCompletedMap.put(cardId, snapshotMap);
            for (Map.Entry<String, SwccgBuiltInCardBlueprint> entry : _permWeaponFiredInBattleByCardCompletedMap.get(cardId).entrySet()) {
                snapshotMap.put(entry.getKey(), entry.getValue());
            }
        }
        snapshot._specialDownloadBattlegroundMap.putAll(_specialDownloadBattlegroundMap);
        for (PhysicalCard card : _forfeitedFromLocationMap.keySet()) {
            PhysicalCard snapshotCard = snapshotData.getDataForSnapshot(card);
            Set<PhysicalCard> snapshotSet = new HashSet<PhysicalCard>();
            snapshot._forfeitedFromLocationMap.put(snapshotCard, snapshotSet);
            for (PhysicalCard card2 : _forfeitedFromLocationMap.get(card)) {
                snapshotSet.add(snapshotData.getDataForSnapshot(card2));
            }
        }
        for (Integer cardId : _asteroidDestinyDrawnAgainstMap.keySet()) {
            Set<Integer> snapshotSet = new HashSet<Integer>(_asteroidDestinyDrawnAgainstMap.get(cardId));
            snapshot._asteroidDestinyDrawnAgainstMap.put(cardId, snapshotSet);
        }
        for (String playerId : _cardPlayedThisGame.keySet()) {
            List<PhysicalCard> snapshotList = new LinkedList<PhysicalCard>();
            snapshot._cardPlayedThisGame.put(playerId, snapshotList);
            for (PhysicalCard card : _cardPlayedThisGame.get(playerId)) {
                snapshotList.add(snapshotData.getDataForSnapshot(card));
            }
        }
        for (String playerId : _cardPlayedThisTurn.keySet()) {
            List<PhysicalCard> snapshotList = new LinkedList<PhysicalCard>();
            snapshot._cardPlayedThisTurn.put(playerId, snapshotList);
            for (PhysicalCard card : _cardPlayedThisTurn.get(playerId)) {
                snapshotList.add(snapshotData.getDataForSnapshot(card));
            }
        }
        for (String playerId : _cardWithAbilityDeployedThisTurn.keySet()) {
            List<PhysicalCard> snapshotList = new LinkedList<PhysicalCard>();
            snapshot._cardWithAbilityDeployedThisTurn.put(playerId, snapshotList);
            for (PhysicalCard card : _cardWithAbilityDeployedThisTurn.get(playerId)) {
                snapshotList.add(snapshotData.getDataForSnapshot(card));
            }
        }
        for (String playerId : _cardPlayedToLocationThisTurn.keySet()) {
            Map<Integer, List<PhysicalCard>> snapshotMap = new HashMap<Integer, List<PhysicalCard>>();
            snapshot._cardPlayedToLocationThisTurn.put(playerId, snapshotMap);
            Map<Integer, List<PhysicalCard>> map = _cardPlayedToLocationThisTurn.get(playerId);
            for (Integer cardId : map.keySet()) {
                List<PhysicalCard> snapshotList = new LinkedList<PhysicalCard>();
                snapshotMap.put(cardId, snapshotList);
                for (PhysicalCard card : map.get(cardId)) {
                    snapshotList.add(snapshotData.getDataForSnapshot(card));
                }
            }
        }
        snapshot._bluffCardStacked = _bluffCardStacked;
        snapshot._usedCombatCard.addAll(_usedCombatCard);
        for (Integer cardId : _targetedByWeaponsMap.keySet()) {
            List<PhysicalCard> snapshotList = new LinkedList<PhysicalCard>();
            snapshot._targetedByWeaponsMap.put(cardId, snapshotList);
            for (PhysicalCard card : _targetedByWeaponsMap.get(cardId)) {
                snapshotList.add(snapshotData.getDataForSnapshot(card));
            }
        }
        for (Integer cardId : _targetedByPermanentWeaponsMap.keySet()) {
            List<SwccgBuiltInCardBlueprint> snapshotList = new LinkedList<SwccgBuiltInCardBlueprint>();
            snapshot._targetedByPermanentWeaponsMap.put(cardId, snapshotList);
            snapshotList.addAll(_targetedByPermanentWeaponsMap.get(cardId));
        }
        for (Map.Entry<Integer, PhysicalCard> entry : _attemptedJediTestThisTurnMap.entrySet()) {
            snapshot._attemptedJediTestThisTurnMap.put(entry.getKey(), snapshotData.getDataForSnapshot(entry.getValue()));
        }
        for (PhysicalCard card : _blownAwayCards) {
            snapshot._blownAwayCards.add(snapshotData.getDataForSnapshot(card));
        }
        for (PhysicalCard card : _cardsThatWonSabacc) {
            snapshot._cardsThatWonSabacc.add(snapshotData.getDataForSnapshot(card));
        }
        snapshot._personasCrossedOver.addAll(_personasCrossedOver);
        for (String playerId : _completedUtinniEffect.keySet()) {
            List<PhysicalCard> snapshotList = new LinkedList<PhysicalCard>();
            snapshot._completedUtinniEffect.put(playerId, snapshotList);
            for (PhysicalCard card : _completedUtinniEffect.get(playerId)) {
                snapshotList.add(snapshotData.getDataForSnapshot(card));
            }
        }
        for (Map.Entry<Integer, PhysicalCard> entry : _completedJediTest.entrySet()) {
            snapshot._completedJediTest.put(entry.getKey(), snapshotData.getDataForSnapshot(entry.getValue()));
        }
    }

    public ModifiersLogic(SwccgGame swccgGame) {
        _swccgGame = swccgGame;
    }

    @Override
    public LimitCounter getUntilStartOfPhaseLimitCounter(PhysicalCard card, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId, Phase phase) {
        Map<String, LimitCounter> limitCounterMap = _startOfPhaseLimitCounters.get(phase);
        if (limitCounterMap == null) {
            limitCounterMap = new HashMap<String, LimitCounter>();
            _startOfPhaseLimitCounters.put(phase, limitCounterMap);
        }
        String key = card.getCardId()+"|"+playerId+"|"+gameTextSourceCardId+"|"+ gameTextActionId;
        if (card.getBlueprint().getCardCategory() == CardCategory.INTERRUPT) {
            key = "|"+playerId+"|" + "|"+ gameTextActionId;
        }
        LimitCounter limitCounter = limitCounterMap.get(key);
        if (limitCounter == null) {
            limitCounter = new DefaultLimitCounter();
            limitCounterMap.put(key, limitCounter);
        }
        return limitCounter;
    }

    @Override
    public LimitCounter getUntilEndOfPhaseLimitCounter(PhysicalCard card, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId, Phase phase) {
        Map<String, LimitCounter> limitCounterMap = _endOfPhaseLimitCounters.get(phase);
        if (limitCounterMap == null) {
            limitCounterMap = new HashMap<String, LimitCounter>();
            _endOfPhaseLimitCounters.put(phase, limitCounterMap);
        }
        String key = card.getCardId()+"|"+playerId+"|"+gameTextSourceCardId+"|"+ gameTextActionId;
        if (card.getBlueprint().getCardCategory() == CardCategory.INTERRUPT) {
            key = "|"+playerId+"|" + "|"+ gameTextActionId;
        }
        LimitCounter limitCounter = limitCounterMap.get(key);
        if (limitCounter == null) {
            limitCounter = new DefaultLimitCounter();
            limitCounterMap.put(key, limitCounter);
        }
        return limitCounter;
    }

    @Override
    public LimitCounter getUntilEndOfBattleLimitCounter(PhysicalCard card, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        String key = card.getCardId()+"|"+playerId+"|"+gameTextSourceCardId+"|"+ gameTextActionId;
        if (card.getBlueprint().getCardCategory() == CardCategory.INTERRUPT) {
            key = "|"+playerId+"|" + "|"+ gameTextActionId;
        }
        LimitCounter limitCounter = _battleLimitCounters.get(key);
        if (limitCounter == null) {
            limitCounter = new DefaultLimitCounter();
            _battleLimitCounters.put(key, limitCounter);
        }
        return limitCounter;
    }

    @Override
    public LimitCounter getUntilEndOfDuelLimitCounter(PhysicalCard card, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        String key = card.getCardId()+"|"+playerId+"|"+gameTextSourceCardId+"|"+ gameTextActionId;
        if (card.getBlueprint().getCardCategory() == CardCategory.INTERRUPT) {
            key = "|"+playerId+"|" + "|"+ gameTextActionId;
        }
        LimitCounter limitCounter = _duelLimitCounters.get(key);
        if (limitCounter == null) {
            limitCounter = new DefaultLimitCounter();
            _duelLimitCounters.put(key, limitCounter);
        }
        return limitCounter;
    }

    @Override
    public LimitCounter getUntilEndOfTurnLimitCounter(PhysicalCard card, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        String key = card.getCardId()+"|"+playerId+"|"+gameTextSourceCardId+"|"+ gameTextActionId;
        if (card.getBlueprint().getCardCategory() == CardCategory.INTERRUPT) {
            key = "|"+playerId+"|" + "|"+ gameTextActionId;
        }
        LimitCounter limitCounter = _turnLimitCounters.get(key);
        if (limitCounter == null) {
            limitCounter = new DefaultLimitCounter();
            _turnLimitCounters.put(key, limitCounter);
        }
        return limitCounter;
    }

    @Override
    public LimitCounter getUntilEndOfTurnForCardTitleLimitCounter(String title, GameTextActionId cardAction) {
        String key = title+"|"+cardAction;
        LimitCounter limitCounter = _turnForCardTitleLimitCounters.get(key);
        if (limitCounter == null) {
            limitCounter = new DefaultLimitCounter();
            _turnForCardTitleLimitCounters.put(key, limitCounter);
        }
        return limitCounter;
    }

    @Override
    public LimitCounter getUntilEndOfCaptivityLimitCounter(String title, GameTextActionId cardAction, PhysicalCard captive) {
        Map<String, LimitCounter> mapForCaptive = _captivityLimitCounters.get(captive.getCardId());
        if (mapForCaptive == null) {
            mapForCaptive = new HashMap<String, LimitCounter>();
            _captivityLimitCounters.put(captive.getCardId(), mapForCaptive);
        }
        String key = title+"|"+cardAction;
        LimitCounter limitCounter = mapForCaptive.get(key);
        if (limitCounter == null) {
            limitCounter = new DefaultLimitCounter();
            mapForCaptive.put(key, limitCounter);
        }
        return limitCounter;
    }

    @Override
    public LimitCounter getPerRaceTotalLimitCounter(String title, GameTextActionId cardAction, float raceTotal) {
        Map<String, LimitCounter> mapForRaceTotal = _raceTotalLimitCounters.get(raceTotal);
        if (mapForRaceTotal == null) {
            mapForRaceTotal = new HashMap<String, LimitCounter>();
            _raceTotalLimitCounters.put(raceTotal, mapForRaceTotal);
        }
        String key = title+"|"+cardAction;
        LimitCounter limitCounter = mapForRaceTotal.get(key);
        if (limitCounter == null) {
            limitCounter = new DefaultLimitCounter();
            mapForRaceTotal.put(key, limitCounter);
        }
        return limitCounter;
    }

    @Override
    public LimitCounter getUntilEndOfGameLimitCounter(String title, GameTextActionId cardAction) {
        String key = title+"|"+cardAction;
        LimitCounter limitCounter = _gameLimitCounters.get(key);
        if (limitCounter == null) {
            limitCounter = new DefaultLimitCounter();
            _gameLimitCounters.put(key, limitCounter);
        }
        return limitCounter;
    }

    @Override
    public LimitCounter getUntilEndOfForceDrainLimitCounter(String title, GameTextActionId cardAction) {
        String key = title+"|"+cardAction;
        LimitCounter limitCounter = _forceDrainLimitCounters.get(key);
        if (limitCounter == null) {
            limitCounter = new DefaultLimitCounter();
            _forceDrainLimitCounters.put(key, limitCounter);
        }
        return limitCounter;
    }

    @Override
    public LimitCounter getUntilEndOfForceLossLimitCounter(PhysicalCard card, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        Integer forceLossId = _swccgGame.getGameState().getTopForceLossState().getId();
        Map<String, LimitCounter> mapForForceLoss = _forceLossLimitCounters.get(forceLossId);
        if (mapForForceLoss == null) {
            mapForForceLoss = new HashMap<String, LimitCounter>();
            _forceLossLimitCounters.put(forceLossId, mapForForceLoss);
        }
        String key = card.getCardId()+"|"+playerId+"|"+gameTextSourceCardId+"|"+ gameTextActionId;
        if (card.getBlueprint().getCardCategory() == CardCategory.INTERRUPT) {
            key = "|"+playerId+"|" + "|"+ gameTextActionId;
        }
        LimitCounter limitCounter = mapForForceLoss.get(key);
        if (limitCounter == null) {
            limitCounter = new DefaultLimitCounter();
            mapForForceLoss.put(key, limitCounter);
        }
        return limitCounter;
    }

    @Override
    public LimitCounter getCardTitlePlayedTurnLimitCounter(String title) {
        LimitCounter limitCounter = _cardTitlePlayedTurnLimitCounters.get(title);
        if (limitCounter == null) {
            limitCounter = new DefaultLimitCounter();
            _cardTitlePlayedTurnLimitCounters.put(title, limitCounter);
        }
        return limitCounter;
    }

    @Override
    public final boolean hasFlagActive(GameState gameState, ModifierFlag modifierFlag) {
        return hasFlagActive(gameState, modifierFlag, null);
    }

    @Override
    public boolean hasFlagActive(GameState gameState, ModifierFlag modifierFlag, String playerId) {
        for (Modifier modifier : getModifiers(gameState, ModifierType.SPECIAL_FLAG))
            if (modifier.hasFlagActive(gameState, this, modifierFlag, playerId))
                return true;

        return false;
    }

    @Override
    public int getFlagActiveCount(GameState gameState, ModifierFlag modifierFlag, String playerId) {
        int count = 0;
        for (Modifier modifier : getModifiers(gameState, ModifierType.SPECIAL_FLAG)) {
            if (modifier.hasFlagActive(gameState, this, modifierFlag, playerId)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Excludes the specified card from being affected by the modifier.
     * This is typically used when a card is 'restored' to normal.
     * @param modifier the modifier
     * @param card the card
     */
    @Override
    public void excludeFromBeingAffected(Modifier modifier, PhysicalCard card) {
        Set<Integer> excludedCards = _excludedFromBeingAffected.get(modifier);
        if (excludedCards == null) {
            excludedCards = new HashSet<Integer>();
            _excludedFromBeingAffected.put(modifier, excludedCards);
        }
        excludedCards.add(card.getCardId());
    }

    /**
     * Determines if the specified card is excluded from being affected by the modifier.
     * @param modifier the modifier
     * @param card the card
     * @return true or false
     */
    @Override
    public boolean isExcludedFromBeingAffected(Modifier modifier, PhysicalCard card) {
        Set<Integer> excludedCards = _excludedFromBeingAffected.get(modifier);
        return excludedCards != null && excludedCards.contains(card.getCardId());
    }

    /**
     * Determines if the card has the specified gametext modification.
     * @param gameState the game state
     * @param card the card
     * @param type the gametext modification type
     * @return true if card has the modification, otherwise false
     */
    @Override
    public boolean hasGameTextModification(GameState gameState, PhysicalCard card, ModifyGameTextType type) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MODIFY_GAME_TEXT, card)) {
            if (modifier.getModifyGameTextType(gameState, this, card)==type)
                return true;
        }
        return false;
    }

    /**
     * Gets the number of times the card has the specified gametext modification applied cumulatively.
     * @param gameState the game state
     * @param card the card
     * @param type the gametext modification type
     * @return the number of times the card has the specified gametext modification
     */
    @Override
    public int getGameTextModificationCount(GameState gameState, PhysicalCard card, ModifyGameTextType type) {
        int count = 0;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MODIFY_GAME_TEXT, card)) {
            if (modifier.getModifyGameTextType(gameState, this, card)==type) {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean hasKeyword(GameState gameState, PhysicalCard physicalCard, Keyword keyword) {
        return hasKeyword(gameState, physicalCard, keyword, new ModifierCollectorImpl());
    }

    @Override
    public boolean hasKeyword(GameState gameState, PhysicalCard physicalCard, Keyword keyword, ModifierCollector modifierCollector) {
        // 'Blown away' docking bay is no longer a docking bay
        if (keyword == Keyword.DOCKING_BAY && physicalCard.isBlownAway())
            return false;

        boolean retVal = physicalCard.getBlueprint().hasKeyword(keyword);

        for (Modifier modifier : getKeywordModifiersAffectingCard(gameState, ModifierType.GIVE_KEYWORD, keyword, physicalCard)) {
            if (modifier.hasKeyword(gameState, this, physicalCard, keyword)) {
                retVal = true;
                modifierCollector.addModifier(modifier);
            }
        }

        for (Modifier modifier : getKeywordModifiersAffectingCard(gameState, ModifierType.REMOVE_KEYWORD, keyword, physicalCard)) {
            if (modifier.isKeywordRemoved(gameState, this, physicalCard, keyword)) {
                retVal = false;
                modifierCollector.addModifier(modifier);
            }
        }

        return retVal;
    }

    @Override
    public boolean hasLightAndDarkForceIcons(GameState gameState, PhysicalCard physicalCard, PhysicalCard ignoreForceIconsFromCard) {
        if (physicalCard.isBlownAway() || physicalCard.isCollapsed()) {
            return false;
        }

        // This is used as part of checking if a location is a battleground.  We use this since
        // we want to skip checking if location is rotated since it does not affect whether it is a battleground
        // for not. Also, in case there is a card that affects whether a location is rotated, based on
        // checking the battlegrounds in play, we want to avoid a loop between the two.
        if (!getModifiersAffectingCard(gameState, ModifierType.CANCEL_FORCE_ICONS, physicalCard).isEmpty()) {
            return false;
        }

        int numLightIcons = physicalCard.getBlueprint().getIconCount(Icon.LIGHT_FORCE);
        int numDarkIcons = physicalCard.getBlueprint().getIconCount(Icon.DARK_FORCE);

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.CANCEL_FORCE_ICON, physicalCard)) {
            numLightIcons -= modifier.getIconCountModifier(gameState, this, physicalCard, Icon.LIGHT_FORCE);
            numDarkIcons -= modifier.getIconCountModifier(gameState, this, physicalCard, Icon.DARK_FORCE);
        }

        if (numLightIcons > 0 && numDarkIcons > 0) {
            return true;
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.GIVE_ICON, physicalCard)) {
            if (ignoreForceIconsFromCard == null
                    || modifier.getSource(gameState).getCardId() != ignoreForceIconsFromCard.getCardId()) {
                numLightIcons += modifier.getIconCountModifier(gameState, this, physicalCard, Icon.LIGHT_FORCE);
                numDarkIcons += modifier.getIconCountModifier(gameState, this, physicalCard, Icon.DARK_FORCE);
            }
        }

        // Check if Force icons on location are added until equalized
        if (!getModifiersAffectingCard(gameState, ModifierType.EQUALIZE_FORCE_ICONS, physicalCard).isEmpty()) {
            numLightIcons = numDarkIcons = Math.max(numLightIcons, numDarkIcons);
        }

        return (numLightIcons > 0 && numDarkIcons > 0);
    }

    @Override
    public boolean hasIcon(GameState gameState, PhysicalCard physicalCard, Icon icon) {
        return getIconCount(gameState, physicalCard, icon) > 0;
    }

    @Override
    public int getIconCount(GameState gameState, PhysicalCard physicalCard, Icon icon) {
        return getIconCount(gameState, physicalCard, icon, false, new ModifierCollectorImpl());
    }

    @Override
    public int getIconCount(GameState gameState, PhysicalCard physicalCard, Icon icon, ModifierCollector modifierCollector) {
        return getIconCount(gameState, physicalCard, icon, false, modifierCollector);
    }

    private int getIconCount(GameState gameState, PhysicalCard physicalCard, Icon iconInput, boolean skipEqualizeCheck, ModifierCollector modifierCollector) {
        Icon icon = iconInput;
        if (physicalCard.isCrossedOver()) {
            if (icon == Icon.IMPERIAL)
                icon = Icon.REBEL;
            else if (icon == Icon.REBEL)
                icon = Icon.IMPERIAL;
        }

        // Special case for Big One: Asteroid Cave or Space Slug Belly (planet site or creature site)
        if (Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly.accepts(gameState, this, physicalCard)
                && Filters.in_play.accepts(gameState, this, physicalCard)) {
            if (physicalCard.isSpaceSlugBelly() && icon == Icon.PLANET) {
                return 0;
            }
            if (!physicalCard.isSpaceSlugBelly() && icon == Icon.CREATURE_SITE) {
                return 0;
            }
        }

        // If a system is 'blown away' it becomes a space system
        if (physicalCard.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM && physicalCard.isBlownAway()) {
            if (icon == Icon.SPACE) {
                return 1;
            }
            else if (icon == Icon.MOBILE || icon == Icon.PLANET) {
                return 0;
            }
        }

        // If a site is 'blown away' it becomes an exterior site
        if (physicalCard.getBlueprint().getCardSubtype() == CardSubtype.SITE && physicalCard.isBlownAway()) {
            if (icon == Icon.EXTERIOR_SITE) {
                return 1;
            }
            else if (icon == Icon.INTERIOR_SITE) {
                return 0;
            }
        }

        // Certain icons never get added/removed, so avoid circular checking (e.g. Ithorian), skip checking if these icons were added/removed
        if (icon != Icon.CREATURE_SITE && icon != Icon.EXTERIOR_SITE && icon != Icon.INTERIOR_SITE && icon != Icon.MOBILE
                && icon != Icon.PLANET && icon != Icon.PRESENCE && icon != Icon.SPACE && icon != Icon.STARSHIP_SITE
                && icon != Icon.UNDERGROUND && icon != Icon.UNDERWATER && icon != Icon.VEHICLE_SITE) {

            boolean iconsCanceled = false;
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.CANCEL_ICONS, physicalCard)) {
                if (modifier.getIcon() == icon) {
                    modifierCollector.addModifier(modifier);
                    iconsCanceled = true;
                }
            }
            if (iconsCanceled) {
                return 0;
            }
        }

        int result;
        if (icon == Icon.LIGHT_FORCE || icon == Icon.DARK_FORCE) {

            // Light and Dark Force only exist on locations
            if (physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION) {
                return 0;
            }

            if (physicalCard.isBlownAway() || physicalCard.isCollapsed()) {
                return 0;
            }

            boolean iconsCanceled = false;
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.CANCEL_FORCE_ICONS, physicalCard)) {
                if (modifier.isForPlayer(icon == Icon.LIGHT_FORCE ? gameState.getLightPlayer() : gameState.getDarkPlayer())) {
                    modifierCollector.addModifier(modifier);
                    iconsCanceled = true;
                }
            }
            if (iconsCanceled) {
                return 0;
            }

            if (isRotatedLocation(gameState, physicalCard)) {
                if (icon == Icon.LIGHT_FORCE)
                    result = physicalCard.getBlueprint().getIconCount(Icon.DARK_FORCE);
                else
                    result = physicalCard.getBlueprint().getIconCount(Icon.LIGHT_FORCE);
            }
            else {
                result = physicalCard.getBlueprint().getIconCount(icon);
            }
        }
        else {
            result = physicalCard.getBlueprint().getIconCount(icon);
        }

        // Certain icons never get added/removed, so avoid circular checking (e.g. Ithorian), skip checking if these icons were added/removed
        if (icon != Icon.CREATURE_SITE && icon != Icon.EXTERIOR_SITE && icon != Icon.INTERIOR_SITE && icon != Icon.MOBILE
                && icon != Icon.PLANET && icon != Icon.PRESENCE && icon != Icon.SPACE && icon != Icon.STARSHIP_SITE
                && icon != Icon.UNDERGROUND && icon != Icon.UNDERWATER && icon != Icon.VEHICLE_SITE) {

            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.CANCEL_FORCE_ICON, physicalCard)) {
                if (modifier.getIcon() == icon) {
                    modifierCollector.addModifier(modifier);
                    result -= modifier.getIconCountModifier(gameState, this, physicalCard, icon);
                }
            }

            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.GIVE_ICON, physicalCard)) {
                if (modifier.getIcon() == icon) {
                    modifierCollector.addModifier(modifier);
                    result += modifier.getIconCountModifier(gameState, this, physicalCard, icon);
                }
            }
        }

        // Check if Force icons on location are added until equalized
        if (!skipEqualizeCheck
                && physicalCard.getBlueprint().getCardCategory() == CardCategory.LOCATION
                && (icon == Icon.LIGHT_FORCE || icon == Icon.DARK_FORCE)) {
            boolean equalize = false;
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EQUALIZE_FORCE_ICONS, physicalCard)) {
                modifierCollector.addModifier(modifier);
                equalize = true;
            }
            if (equalize) {
                result = Math.max(result, getIconCount(gameState, physicalCard, (icon == Icon.LIGHT_FORCE ? Icon.DARK_FORCE : Icon.LIGHT_FORCE), true, modifierCollector));
            }
        }

        return Math.max(0, result);
    }

    @Override
    public boolean isSpecies(GameState gameState, PhysicalCard physicalCard, Species species) {
        return physicalCard.getBlueprint().hasSpeciesAttribute()
                && physicalCard.getBlueprint().getSpecies() == species;
    }

    /**
     * Gets the personas of the specified card.
     * @param gameState the game state
     * @param card the card
     * @return personas
     */
    @Override
    public Set<Persona> getPersonas(GameState gameState, PhysicalCard card) {
        if (card.getBlueprint().hasCharacterPersonaOnlyWhileOnTable() && !Filters.onTable.accepts(gameState, this, card)) {
            return Collections.emptySet();
        }
        Set<Persona> personas = card.getBlueprint().getPersonas();
        if (card.isCrossedOver()) {
            Set<Persona> crossedOverPersonas = new HashSet<Persona>();
            for (Persona persona : personas) {
                crossedOverPersonas.add(persona.getCrossedOverPersona());
            }
            return crossedOverPersonas;
        }
        return personas;
    }

    /**
     * Determines if the specified card has the specified persona.
     * @param gameState the game state
     * @param card the card
     * @param persona the persona
     * @return true or false
     */
    @Override
    public boolean hasPersona(GameState gameState, PhysicalCard card, Persona persona) {
        return getPersonas(gameState, card).contains(persona);
    }

    /**
     * Gets the number of cards the specified player draws in starting hand.
     * @param gameState the game state
     * @param playerId the player
     * @return the number of cards
     */
    @Override
    public int getNumCardsToDrawInStartingHand(GameState gameState, String playerId) {
        int numCards = 8;

        for (Modifier modifier : getModifiers(gameState, ModifierType.NUM_CARDS_DRAWN_IN_STARTING_HAND)) {
            if (modifier.isForPlayer(playerId)) {
                numCards = (int) modifier.getValue(gameState, this, (PhysicalCard) null);
            }
        }
        return Math.max(0, numCards);
    }

    /**
     * Gets the political agendas of the specified card.
     * @param gameState the game state
     * @param card the card
     * @return the political agendas
     */
    @Override
    public List<Agenda> getAgendas(GameState gameState, PhysicalCard card) {
        List<Agenda> agendas = new LinkedList<Agenda>();
        if (card.getBlueprint().getCardCategory() != CardCategory.CHARACTER)
            return agendas;

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.GIVE_AGENDA, card)) {
            for (Agenda agenda : Agenda.values()) {
                if (!agendas.contains(agenda) && modifier.hasAgenda(agenda)) {
                    agendas.add(agenda);
                }
            }
        }
        return agendas;
    }

    /**
     * Determines if the card has the specified political agenda.
     * @param gameState the game state
     * @param card the card
     * @param agenda the agenda
     * @return true if card has the agenda, otherwise false
     */
    @Override
    public boolean hasAgenda(GameState gameState, PhysicalCard card, Agenda agenda) {
        if (card.getBlueprint().getCardCategory() != CardCategory.CHARACTER)
            return false;

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.GIVE_AGENDA, card)) {
            if (modifier.hasAgenda(agenda)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets a card's current ferocity.
     * @param gameState the game state
     * @param physicalCard a card
     * @param ferocityDestinyTotal the ferocity destiny total, or null
     * @return the card's ferocity
     */
    @Override
    public float getFerocity(GameState gameState, PhysicalCard physicalCard, Float ferocityDestinyTotal) {
        return getFerocity(gameState, physicalCard, ferocityDestinyTotal, new ModifierCollectorImpl());
    }

    /**
     * Gets a card's current ferocity.
     * @param gameState the game state
     * @param physicalCard a card
     * @param ferocityDestinyTotal the ferocity destiny total, or null
     * @param modifierCollector collector of affecting modifiers
     * @return the card's ferocity
     */
    @Override
    public float getFerocity(GameState gameState, PhysicalCard physicalCard, Float ferocityDestinyTotal, ModifierCollector modifierCollector) {
        Float result;

        if (!physicalCard.getBlueprint().hasFerocityAttribute())
            return 0;

        result = physicalCard.getBlueprint().getFerocity();

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_FEROCITY, physicalCard)) {
            result = modifier.getBaseFerocityDefinedByGameText(gameState, this, physicalCard);
            modifierCollector.addModifier(modifier);
        }

        // If value if undefined, then return 0
        if (result == null)
            return 0;

        if (ferocityDestinyTotal != null) {
            result += ferocityDestinyTotal;
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FEROCITY, physicalCard)) {
            result += modifier.getFerocityModifier(gameState, this, physicalCard);
            modifierCollector.addModifier(modifier);
        }

        // Check if value was reset to an "unmodifiable value", and use lowest found
        Float lowestResetValue = null;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_FEROCITY, physicalCard)) {
            float modifierAmount = modifier.getValue(gameState, this, physicalCard);
            lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
            modifierCollector.addModifier(modifier);
        }
        if (lowestResetValue != null) {
            result = lowestResetValue;
        }

        return Math.max(0, result);
    }

    /**
     * Gets the card's number of ferocity destiny to draw.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the card's number of ferocity destiny
     */
    @Override
    public int getNumFerocityDestiny(GameState gameState, PhysicalCard physicalCard) {
        Integer result = null;

        if (!physicalCard.getBlueprint().hasFerocityAttribute())
            return 0;

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_FEROCITY, physicalCard)) {
            result = modifier.getNumFerocityDestinyDefinedByGameText(gameState, this, physicalCard);
        }

        // If value if undefined, then return 0
        if (result == null)
            return 0;

        return Math.max(0, result);
    }

    /**
     * Determines if the player may not draw race destiny.
     * @param gameState the game state
     * @param playerId the player
     * @return true or false
     */
    @Override
    public boolean mayNotDrawRaceDestiny(GameState gameState, String playerId) {
        for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_NOT_DRAW_RACE_DESTINY)) {
            if (modifier.isForPlayer(playerId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the Podracer's number of race destiny to draw.
     * @param gameState the game state
     * @param playerId the player to draw race destiny
     * @param physicalCard a card
     * @return the Podracer's number of race destiny to draw
     */
    @Override
    public int getNumRaceDestinyToDraw(GameState gameState, String playerId, PhysicalCard physicalCard) {
        int result = 1;

        if (mayNotDrawRaceDestiny(gameState, playerId)) {
            return 0;
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.NUM_RACE_DESTINY_DRAWS, physicalCard)) {
            int numDestinies = (int) modifier.getValue(gameState, this, physicalCard);
            result = Math.max(result, numDestinies);
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.NUM_RACE_DESTINY_DRAW_AND_CHOOSE, physicalCard)) {
            int numDestinies = modifier.getNumToDraw(gameState, this, physicalCard);
            result = Math.max(result, numDestinies);
        }

        return Math.max(0, result);
    }

    /**
     * Gets the Podracer's number of race destiny to choose (in case of draw X and choose Y).
     * @param gameState the game state
     * @param playerId the player to draw race destiny
     * @param physicalCard a card
     * @return the Podracer's number of race destiny to choose (in case of draw X and choose Y), otherwise 0
     */
    @Override
    public int getNumRaceDestinyToChoose(GameState gameState, String playerId, PhysicalCard physicalCard) {
        int result = 0;

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.NUM_RACE_DESTINY_DRAW_AND_CHOOSE, physicalCard)) {
            int numDestinies = modifier.getNumToChoose(gameState, this, physicalCard);
            result = Math.max(result, numDestinies);
        }

        return Math.max(0, result);
    }

    /**
     * Gets a card's current power.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the card's power
     */
    @Override
    public float getPower(GameState gameState, PhysicalCard physicalCard) {
        return getPower(gameState, physicalCard, new ModifierCollectorImpl());
    }

    /**
     * Gets a card's current power.
     * @param gameState the game state
     * @param physicalCard a card
     * @param modifierCollector collector of affecting modifiers
     * @return the card's power
     */
    @Override
    public float getPower(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
        Float result;

        // Use destiny number instead if "Dejarik Rules"
        if (physicalCard.isDejarikHologramAtHolosite()) {
            result = getDestiny(gameState, physicalCard);
        }
        else if (isPoliticsUsedForPower(gameState, physicalCard, modifierCollector)) {
            return getPolitics(gameState, physicalCard);
        }
        else {
            if (!physicalCard.getBlueprint().hasPowerAttribute())
                return 0;

            if ((physicalCard.getBlueprint().getCardCategory() == CardCategory.STARSHIP || physicalCard.getBlueprint().getCardCategory() == CardCategory.VEHICLE)
                    && !isPiloted(gameState, physicalCard, false))
                return 0;

            result = physicalCard.getBlueprint().getPower();

            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_POWER, physicalCard)) {
                result = modifier.getPrintedValueDefinedByGameText(gameState, this, physicalCard);
                modifierCollector.addModifier(modifier);
            }
        }
        // If value if undefined, then return 0
        if (result == null)
            return 0;

        // If card is a character and it is "doubled", then double the printed number
        if (physicalCard.getBlueprint().getCardCategory()==CardCategory.CHARACTER
                && isDoubled(gameState, physicalCard, modifierCollector)) {
            result *= 2;
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.POWER, physicalCard)) {
            result *= modifier.getPowerMultiplierModifier(gameState, this, physicalCard);
            PhysicalCard sourceCard = modifier.getSource(gameState) != null ? modifier.getSource(gameState) : null;
            String playerId = sourceCard != null ? sourceCard.getOwner() : null;
            float modifierAmount = modifier.getPowerModifier(gameState, this, physicalCard);
            if (modifierAmount <= 0 || !isProhibitedFromHavingPowerIncreasedByCard(gameState, physicalCard, playerId, sourceCard, modifierCollector)) {
                if (modifierAmount >= 0 || !isProhibitedFromHavingPowerReduced(gameState, physicalCard, playerId, modifierCollector)) {
                    result += modifierAmount;
                    modifierCollector.addModifier(modifier);
                }
            }
        }

        // Check if value was reset to an "unmodifiable value", and use lowest found
        Float lowestResetValue = null;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_POWER, physicalCard)) {
            float modifierAmount = modifier.getValue(gameState, this, physicalCard);
            if (modifierAmount >= result || !isProhibitedFromHavingPowerReduced(gameState, physicalCard, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null, modifierCollector)) {
                lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
                modifierCollector.addModifier(modifier);
            }
        }
        if (lowestResetValue != null) {
            result = lowestResetValue;
        }

        return Math.max(0, result);
    }

    /**
     * Determines if a card's power is less than a specified value.
     *
     * @param gameState the game state
     * @param card a card
     * @param value the power value
     * @return true if card's power is less than the specified value, otherwise false
     */
    @Override
    public boolean hasPowerLessThan(GameState gameState, PhysicalCard card, float value) {
        if (!hasPowerAttribute(card))
            return false;

        return getPower(gameState, card) < value;
    }

    /**
     * Determines if a card's power is equal to a specified value.
     *
     * @param gameState the game state
     * @param card a card
     * @param value the power value
     * @return true if card's power is equal to the specified value, otherwise false
     */
    @Override
    public boolean hasPowerEqualTo(GameState gameState, PhysicalCard card, float value) {
        if (!hasPowerAttribute(card))
            return false;

        return getPower(gameState, card) == value;
    }

    /**
     * Determines if a card's power may not be reduced by the specified player.
     * @param gameState the game state
     * @param card a card
     * @param playerId the player
     * @return true if card's power may not be reduced, otherwise false
     */
    @Override
    public boolean isProhibitedFromHavingPowerReduced(GameState gameState, PhysicalCard card, String playerId) {
        return isProhibitedFromHavingPowerReduced(gameState, card, playerId, new ModifierCollectorImpl());
    }

    /**
     * Determines if a card's power may not be reduced by the specified player.
     * @param gameState the game state
     * @param card a card
     * @param playerId the player
     * @param modifierCollector collector of affecting modifiers
     * @return true if card's power may not be reduced, otherwise false
     */
    @Override
    public boolean isProhibitedFromHavingPowerReduced(GameState gameState, PhysicalCard card, String playerId, ModifierCollector modifierCollector) {
        boolean retVal = false;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_HAVE_POWER_REDUCED, card)) {
            if (modifier.isForPlayer(playerId)) {
                retVal = true;
                modifierCollector.addModifier(modifier);
            }
        }
        return retVal;
    }

    /**
     * Determines if a card's power may not be increased by certain cards.
     * @param gameState the game state
     * @param card a card
     * @param playerId the player
     * @param increasedByCard the card to check if its ability to increase power is being restricted
     * @return true if card's power may not be reduced, otherwise false
     */
    @Override
    public boolean isProhibitedFromHavingPowerIncreasedByCard(GameState gameState, PhysicalCard card, String playerId, PhysicalCard increasedByCard) {
        return isProhibitedFromHavingPowerIncreasedByCard(gameState, card, playerId, increasedByCard, new ModifierCollectorImpl());
    }

    /**
     * Determines if a card's power may not be increased by certain cards.
     * @param gameState the game state
     * @param card a card
     * @param playerId the player
     * @param increasedByCard the card to check if its ability to increase power is being restricted
     * @param modifierCollector collector of affecting modifiers
     * @return true if card's power may not be increased by certain cards, otherwise false
     */
    @Override
    public boolean isProhibitedFromHavingPowerIncreasedByCard(GameState gameState, PhysicalCard card, String playerId, PhysicalCard increasedByCard, ModifierCollector modifierCollector) {
        boolean retVal = false;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_HAVE_POWER_INCREASED_BY_CARD, card)) {
            if (modifier.isForPlayer(playerId)) {
                Filter restrictedCardsFilter = modifier.getCardsRestrictedFromIncreasingPowerFilter();
                if (restrictedCardsFilter.accepts(gameState, this, increasedByCard)) {
                    retVal = true;
                    modifierCollector.addModifier(modifier);
                }
            }
        }
        return retVal;
    }

    /**
     * Determines if a character's politics used for that card's power.
     * @param gameState the game state
     * @param card a card
     * @param modifierCollector collector of affecting modifiers
     * @return true if character's politics is used for power, otherwise false
     */
    @Override
    public boolean isPoliticsUsedForPower(GameState gameState, PhysicalCard card, ModifierCollector modifierCollector) {
        if (card.getBlueprint().getCardCategory() != CardCategory.CHARACTER)
            return false;

        boolean retVal = false;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.USE_POLITICS_FOR_POWER, card)) {
            retVal = true;
            modifierCollector.addModifier(modifier);
        }
        return retVal;
    }

    @Override
    public float getAbility(GameState gameState, PhysicalCard physicalCard) {
        return getAbility(gameState, physicalCard, false);
    }

    @Override
    public float getAbility(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
        return getAbility(gameState, physicalCard, false, modifierCollector);
    }

    @Override
    public float getAbility(GameState gameState, PhysicalCard physicalCard, boolean includePermPilots) {
        return getAbility(gameState, physicalCard, includePermPilots, new ModifierCollectorImpl());
    }

    @Override
    public float getAbility(GameState gameState, PhysicalCard physicalCard, boolean includePermPilots, ModifierCollector modifierCollector) {

        SwccgCardBlueprint blueprint = physicalCard.getBlueprint();
        float result = 0;

        // Use destiny number instead if "Dejarik Rules"
        if (physicalCard.isDejarikHologramAtHolosite()) {
            result = getDestiny(gameState, physicalCard);
        }
        else {
            if (physicalCard.getBlueprint().getCardCategory() != CardCategory.CHARACTER
                    && physicalCard.getBlueprint().getCardCategory() != CardCategory.STARSHIP
                    && physicalCard.getBlueprint().getCardCategory() != CardCategory.VEHICLE)
                return 0;

            if (!hasAbility(gameState, physicalCard, includePermPilots, true))
                return 0;

            // Check if value was reset to an "unmodifiable value", and use lowest found
            Float lowestResetValue = null;
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_ABILITY, physicalCard)) {
                float modifierAmount = modifier.getUnmodifiableAbility(gameState, this, physicalCard);
                lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
                modifierCollector.addModifier(modifier);
            }
            if (lowestResetValue != null) {
                return lowestResetValue;
            }
        }

        if (Filters.or(Filters.character, Filters.creature_vehicle).accepts(gameState, this, physicalCard)) {
            Float ability = blueprint.getAbility();

            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_ABILITY, physicalCard)) {
                ability = modifier.getPrintedValueDefinedByGameText(gameState, this, physicalCard);
                modifierCollector.addModifier(modifier);
            }
            // If value if undefined, then return 0
            if (ability == null)
                return 0;

            result = ability;

            // If card is a character and it is "doubled", then double the printed number
            if (physicalCard.getBlueprint().getCardCategory() == CardCategory.CHARACTER
                    && isDoubled(gameState, physicalCard, modifierCollector)) {
                result *= 2;
            }

            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.ABILITY, physicalCard)) {
                result += modifier.getAbilityModifier(gameState, this, physicalCard);
                modifierCollector.addModifier(modifier);
            }
        }

        // If character has completed any Jedi Tests, ability is increased to highest Jedi Test number completed
        if (blueprint.getCardCategory() == CardCategory.CHARACTER) {
            Collection<PhysicalCard> completedJediTests = Filters.filterAllOnTable(gameState.getGame(), Filters.and(Filters.completed_Jedi_Test, Filters.jediTestTargetingApprentice(Filters.sameCardId(physicalCard))));
            for (PhysicalCard completedJediTest : completedJediTests) {
                int jediTestNumber = getJediTestNumber(gameState, completedJediTest);
                if (jediTestNumber > result && gameState.isCardInPlayActive(completedJediTest)) {
                    result = jediTestNumber;
                }
            }
        }

        if (includePermPilots
                && (blueprint.getCardCategory()==CardCategory.STARSHIP || blueprint.getCardCategory()==CardCategory.VEHICLE)
                && !physicalCard.isCrashed()
                && hasIcon(gameState, physicalCard, Icon.PILOT)) {
            List<SwccgBuiltInCardBlueprint> permPilots = getPermanentPilotsAboard(gameState, physicalCard);
            if (permPilots != null) {
                for (SwccgBuiltInCardBlueprint permPilot : permPilots) {
                    float permPilotAbility = permPilot.getAbility();
                    if (permPilotAbility == 1) {
                        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.REPLACE_ABILITY_1_PERMANENT_PILOTS, physicalCard)) {
                            permPilotAbility = Math.max(permPilotAbility, modifier.getReplacementPermanentPilotAbility(gameState, this, physicalCard));
                            modifierCollector.addModifier(modifier);
                        }
                    }
                    result += permPilotAbility;
                }
            }
        }

        return Math.max(0, result);
    }


    @Override
    public float getAbilityForBattleDestiny(GameState gameState, PhysicalCard physicalCard) {
        if (cannotApplyAbilityForBattleDestiny(gameState, physicalCard))
            return 0;

        if (physicalCard.getBlueprint().getCardCategory()==CardCategory.STARSHIP || physicalCard.getBlueprint().getCardCategory()==CardCategory.VEHICLE) {
            if (!isPiloted(gameState, physicalCard, false)
                    || isPermanentPilotsNotAbleToApplyAbilityForBattleDestiny(gameState, physicalCard)) {
                return 0;
            }
        }

        float result = getAbility(gameState, physicalCard, true);

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.ABILITY_FOR_BATTLE_DESTINY, physicalCard)) {
            result += modifier.getValue(gameState, this, physicalCard);
        }

        return Math.max(0, result);
    }

    /**
     * Determines if the card has ability.
     *
     * @param gameState the game state
     * @param card a card
     * @param includePermPilots true if including permanent pilots, otherwise false
     * @return true if card has ability, otherwise false
     */
    @Override
    public boolean hasAbility(GameState gameState, PhysicalCard card, boolean includePermPilots) {
        return hasAbility(gameState, card, includePermPilots, false);
    }

    private boolean hasAbility(GameState gameState, PhysicalCard card, boolean includePermPilots, boolean skipAbilityValueCheck) {
        if (!includePermPilots && !card.getBlueprint().hasAbilityAttribute() && !card.isDejarikHologramAtHolosite())
            return false;

        if (!skipAbilityValueCheck) {
            return getAbility(gameState, card, includePermPilots) > 0;
        }

        return true;
    }

    /**
     * Determines if a card's ability is less than a specified value.
     *
     * @param gameState the game state
     * @param card a card
     * @param value the ability value
     * @return true if card's ability is less than the specified value, otherwise false
     */
    @Override
    public boolean hasAbilityLessThan(GameState gameState, PhysicalCard card, float value) {
        return getAbility(gameState, card) < value;
    }

    /**
     * Determines if a card's ability is equal to a specified value.
     *
     * @param gameState the game state
     * @param card a card
     * @param value the ability value
     * @return true if card's ability is equal to the specified value, otherwise false
     */
    @Override
    public boolean hasAbilityEqualTo(GameState gameState, PhysicalCard card, float value) {
        return getAbility(gameState, card) == value;
    }

    /**
     * Determines if a card's ability is more than a specified value.
     *
     * @param gameState the game state
     * @param card      a card
     * @param value     the ability value
     * @param includePermPilots true if ability of permanent pilots is included, otherwise false
     * @return true if card's ability is more than the specified value, otherwise false
     */
    @Override
    public boolean hasAbilityMoreThan(GameState gameState, PhysicalCard card, float value, boolean includePermPilots) {
        return getAbility(gameState, card, includePermPilots) > value;
    }

    /**
     * Determines if a card has its ability-1 permanent pilot replaced.
     * @param gameState the game state
     * @param card a card
     * @return true or false
     */
    @Override
    public boolean isAbility1PermanentPilotReplaced(GameState gameState, PhysicalCard card) {
        List<SwccgBuiltInCardBlueprint> permPilots = getPermanentPilotsAboard(gameState, card);
        if (permPilots != null) {
            for (SwccgBuiltInCardBlueprint permPilot : permPilots) {
                float permPilotAbility = permPilot.getAbility();
                if (permPilotAbility == 1) {
                    return !getModifiersAffectingCard(gameState, ModifierType.REPLACE_ABILITY_1_PERMANENT_PILOTS, card).isEmpty();
                }
            }
        }
        return false;
    }

    @Override
    public float getHighestAbilityPiloting(GameState gameState, PhysicalCard physicalCard, boolean onlyPermanentPilots) {
        SwccgCardBlueprint blueprint = physicalCard.getBlueprint();
        if (blueprint.getCardCategory()!=CardCategory.STARSHIP && blueprint.getCardCategory()!=CardCategory.VEHICLE)
            return 0;

        float result = 0;
        List<SwccgBuiltInCardBlueprint> permPilots = getPermanentPilotsAboard(gameState, physicalCard);
        if (permPilots != null) {
            for (SwccgBuiltInCardBlueprint permPilot : permPilots) {
                float permPilotAbility = permPilot.getAbility();
                if (permPilotAbility == 1) {
                    for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.REPLACE_ABILITY_1_PERMANENT_PILOTS, physicalCard)) {
                        permPilotAbility = Math.max(permPilotAbility, modifier.getReplacementPermanentPilotAbility(gameState, this, physicalCard));
                    }
                }
                if (permPilotAbility > result) {
                    result = permPilotAbility;
                }
            }
        }

        if (!onlyPermanentPilots) {
            List<PhysicalCard> pilots = gameState.getPilotCardsAboard(this, physicalCard, true);
            for (PhysicalCard pilot : pilots) {
                float pilotAbility = getAbility(gameState, pilot);
                if (pilotAbility > result) {
                    result = pilotAbility;
                }
            }
        }

        return Math.max(0, result);
    }

    @Override
    public List<Float> getAbilityOfPilotsAboard(GameState gameState, PhysicalCard physicalCard) {
        SwccgCardBlueprint blueprint = physicalCard.getBlueprint();
        if (blueprint.getCardCategory()!=CardCategory.STARSHIP && blueprint.getCardCategory()!=CardCategory.VEHICLE) {
            return Collections.emptyList();
        }

        List<Float> abilities = new LinkedList<Float>();
        List<SwccgBuiltInCardBlueprint> permPilots = getPermanentPilotsAboard(gameState, physicalCard);
        if (permPilots != null) {
            for (SwccgBuiltInCardBlueprint permPilot : permPilots) {
                float permPilotAbility = permPilot.getAbility();
                if (permPilotAbility == 1) {
                    for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.REPLACE_ABILITY_1_PERMANENT_PILOTS, physicalCard)) {
                        permPilotAbility = Math.max(permPilotAbility, modifier.getReplacementPermanentPilotAbility(gameState, this, physicalCard));
                    }
                }
                abilities.add(permPilotAbility);
            }
        }

        List<PhysicalCard> pilots = gameState.getPilotCardsAboard(this, physicalCard, true);
        for (PhysicalCard pilot : pilots) {
            float pilotAbility = getAbility(gameState, pilot);
            abilities.add(pilotAbility);
        }

        return abilities;
    }

    /**
     * Gets a card's current politics.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the card's politics
     */
    @Override
    public float getPolitics(GameState gameState, PhysicalCard physicalCard) {
        return getPolitics(gameState, physicalCard, new ModifierCollectorImpl());
    }

    /**
     * Gets a card's current politics.
     * @param gameState the game state
     * @param physicalCard a card
     * @param modifierCollector collector of affecting modifiers
     * @return the card's politics
     */
    @Override
    public float getPolitics(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
        float result = physicalCard.getBlueprint().getPolitics();

        // If card is a character and it is "doubled", then double the printed number
        if (physicalCard.getBlueprint().getCardCategory()==CardCategory.CHARACTER
                && isDoubled(gameState, physicalCard, modifierCollector)) {
            result *= 2;
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.POLITICS, physicalCard)) {
            result += modifier.getPoliticsModifier(gameState, this, physicalCard);
            modifierCollector.addModifier(modifier);
        }

        // Check if value was reset to an "unmodifiable value", and use lowest found
        Float lowestResetValue = null;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_POLITICS, physicalCard)) {
            float modifierAmount = modifier.getUnmodifiablePolitics(gameState, this, physicalCard);
            lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
            modifierCollector.addModifier(modifier);
        }
        if (lowestResetValue != null) {
            result = lowestResetValue;
        }

        return Math.max(0, result);
    }

    /**
     * Determines if a card's politics is more than a specified value.
     *
     * @param gameState the game state
     * @param card a card
     * @param value the maneuver value
     * @return true if card's politics is more than the specified value, otherwise false
     */
    @Override
    public boolean hasPoliticsMoreThan(GameState gameState, PhysicalCard card, float value) {
        if (!hasPoliticsAttribute(card))
            return false;

        return getPolitics(gameState, card) > value;
    }

    /**
     * Determines if a card's politics is equal to a specified value.
     *
     * @param gameState the game state
     * @param card      a card
     * @param value     the politics value
     * @return true if card's politics is equal to the specified value, otherwise false
     */
    @Override
    public boolean hasPoliticsEqualTo(GameState gameState, PhysicalCard card, float value) {
        if (!hasPoliticsAttribute(card))
            return false;

        return getPolitics(gameState, card) == value;
    }

    /**
     * Determines if a card satisfies all battle damage when forfeited.
     * @param gameState the game state
     * @param card a card
     * @return the forfeit value
     */
    @Override
    public boolean isSatisfyAllBattleDamageWhenForfeited(GameState gameState, PhysicalCard card) {
        return (!getModifiersAffectingCard(gameState, ModifierType.SATISFIES_ALL_BATTLE_DAMAGE_WHEN_FORFEITED, card).isEmpty());
    }

    /**
     * Determines if a card satisfies all attrition when forfeited.
     * @param gameState the game state
     * @param card a card
     * @return the forfeit value
     */
    @Override
    public boolean isSatisfyAllAttritionWhenForfeited(GameState gameState, PhysicalCard card) {
        if (cannotSatisfyAttrition(gameState, card))
            return false;

        return (!getModifiersAffectingCard(gameState, ModifierType.SATISFIES_ALL_ATTRITION_WHEN_FORFEITED, card).isEmpty());
    }

    @Override
    public boolean mayBeForfeitedInBattle(GameState gameState, PhysicalCard physicalCard) {
        return (physicalCard.getBlueprint().hasForfeitAttribute() || physicalCard.isDejarikHologramAtHolosite())
                && !hasKeyword(gameState, physicalCard, Keyword.MAY_NOT_BE_FORFEITED_IN_BATTLE);
    }

    /**
     * Gets a card's current forfeit value to use when forfeiting card.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the forfeit value to use when forfeiting card
     */
    @Override
    public float getForfeitWhenForfeiting(GameState gameState, PhysicalCard physicalCard) {
        // Check if card has another value to be used when forfeited
        Float lowestValue = null;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FORFEIT_VALUE_TO_USE, physicalCard)) {
            float modifierAmount = modifier.getValue(gameState, this, physicalCard);
            if (lowestValue == null || modifierAmount < lowestValue) {
                lowestValue = (lowestValue != null) ? Math.min(lowestValue, modifierAmount) : modifierAmount;
            }
        }
        if (lowestValue != null) {
            return Math.max(0, lowestValue);
        }

        return getForfeit(gameState, physicalCard);
    }

    @Override
    public float getForfeit(GameState gameState, PhysicalCard physicalCard) {
        return getForfeit(gameState, physicalCard, new ModifierCollectorImpl());
    }

    @Override
    public float getForfeit(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
        if (!physicalCard.getBlueprint().hasForfeitAttribute() && !physicalCard.isDejarikHologramAtHolosite())
            return 0;

        Float result;

        // Use destiny number instead if "Dejarik Rules"
        if (physicalCard.isDejarikHologramAtHolosite()) {
            result = getDestiny(gameState, physicalCard);
        }
        else {
            result = physicalCard.getBlueprint().getForfeit();
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_FORFEIT_VALUE, physicalCard)) {
            result = modifier.getPrintedValueDefinedByGameText(gameState, this, physicalCard);
            modifierCollector.addModifier(modifier);
        }
        // If value if undefined, then return 0
        if (result == null)
            return 0;

        // If card is a character and it is "doubled", then double the printed number
        if (physicalCard.getBlueprint().getCardCategory()==CardCategory.CHARACTER
                && isDoubled(gameState, physicalCard, modifierCollector)) {
            result *= 2;
        }

        boolean forfeitMayNotBeReduced = isProhibitedFromHavingForfeitReduced(gameState, physicalCard, modifierCollector);

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FORFEIT_VALUE, physicalCard)) {
            float modifierAmount = modifier.getForfeitModifier(gameState, this, physicalCard);
            if (modifierAmount >= 0 || !forfeitMayNotBeReduced) {
                result += modifierAmount;
                modifierCollector.addModifier(modifier);
            }
        }

        // Check if value was reset to an "unmodifiable value", and use lowest found
        Float lowestResetValue = null;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_FORFEIT_VALUE, physicalCard)) {
            float modifierAmount = modifier.getUnmodifiableForfeit(gameState, this, physicalCard);
            if (modifierAmount >= result || !forfeitMayNotBeReduced) {
                lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
                modifierCollector.addModifier(modifier);
            }
        }
        if (lowestResetValue != null) {
            result = lowestResetValue;
        }

        return Math.max(0, result);
    }

    /**
     * Determines if a card remains in play and reduces it's forfeit when 'forfeited'.
     * @param gameState the game state
     * @param card a card
     * @return true or false
     */
    @Override
    public boolean isRemainsInPlayAndReducesForfeitWhenForfeited(GameState gameState, PhysicalCard card) {
        return !getModifiersAffectingCard(gameState, ModifierType.REMAINS_IN_PLAY_WHEN_FORFEITED, card).isEmpty();
    }

    /**
     * Determines if a card's forfeit may not be reduced.
     * @param gameState the game state
     * @param card a card
     * @return true if card's forfeit may not be reduced, otherwise false
     */
    @Override
    public boolean isProhibitedFromHavingForfeitReduced(GameState gameState, PhysicalCard card) {
        return isProhibitedFromHavingForfeitReduced(gameState, card, new ModifierCollectorImpl());
    }

    /**
     * Determines if a card's forfeit may not be reduced.
     * @param gameState the game state
     * @param card a card
     * @param modifierCollector collector of affecting modifiers
     * @return true if card's forfeit may not be reduced, otherwise false
     */
    @Override
    public boolean isProhibitedFromHavingForfeitReduced(GameState gameState, PhysicalCard card, ModifierCollector modifierCollector) {
        boolean retVal = false;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_HAVE_FORFEIT_VALUE_REDUCED, card)) {
            retVal = true;
            modifierCollector.addModifier(modifier);
        }
        return retVal;
    }

    /**
     * Determines if a card's game text may not be canceled.
     * @param gameState the game state
     * @param card a card
     * @return true if card's game text may not be canceled, otherwise false
     */
    @Override
    public boolean isProhibitedFromHavingGameTextCanceled(GameState gameState, PhysicalCard card) {
        return !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_HAVE_GAME_TEXT_CANCELED, card).isEmpty();
    }

    /**
     * Determines if a card may not be suspended.
     * @param gameState the game state
     * @param card a card
     * @return true if card may not be suspended, otherwise false
     */
    @Override
    public boolean isProhibitedFromBeingSuspended(GameState gameState, PhysicalCard card) {
        return !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_SUSPENDED, card).isEmpty();
    }

    /**
     * Determines if a Revolution card has its effects canceled.
     * @param gameState the game state
     * @param card a card
     * @return true or false
     */
    @Override
    public boolean isEffectsOfRevolutionCanceled(GameState gameState, PhysicalCard card) {
        return !getModifiersAffectingCard(gameState, ModifierType.EFFECTS_OF_REVOLUTION_CANCELED, card).isEmpty();
    }

    /**
     * Determines if a card is lost anytime it is about to be stolen.
     * @param gameState the game state
     * @param card a card
     * @return true if card is lost anytime it is about to be stolen, otherwise false
     */
    @Override
    public boolean isLostIfAboutToBeStolen(GameState gameState, PhysicalCard card) {
        return !getModifiersAffectingCard(gameState, ModifierType.LOST_IF_ABOUT_TO_BE_STOLEN, card).isEmpty();
    }

    /**
     * Determines if the card is granted the ability to use the device.
     * @param gameState the game state
     * @param card a card
     * @param device a device
     * @return true if card is granted ability to use the device, otherwise false
     */
    @Override
    public boolean grantedToUseDevice(GameState gameState, PhysicalCard card, PhysicalCard device) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_USE_DEVICE, card)) {
            if (modifier.isAffectedTarget(gameState, this, device)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the card is granted the ability to use the weapon.
     * @param gameState the game state
     * @param card a card
     * @param weapon a weapon
     * @return true if card is granted ability to use the weapon, otherwise false
     */
    @Override
    public boolean grantedToUseWeapon(GameState gameState, PhysicalCard card, PhysicalCard weapon) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_USE_WEAPON, card)) {
            if (modifier.isAffectedTarget(gameState, this, weapon)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets a card's current destiny value.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the destiny value
     */
    @Override
    public float getDestiny(GameState gameState, PhysicalCard physicalCard) {
        return getDestiny(gameState, physicalCard, new ModifierCollectorImpl());
    }

    /**
     * Gets a card's current destiny value.
     * @param gameState the game state
     * @param physicalCard a card
     * @param modifierCollector collector of affecting modifiers
     * @return the destiny value
     */
    @Override
    public float getDestiny(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
        Float result = physicalCard.getDestinyValueToUse();

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DESTINY, physicalCard)) {
            result = modifier.getPrintedValueDefinedByGameText(gameState, this, physicalCard);
            modifierCollector.addModifier(modifier);
        }
        // If value if undefined, then return 0
        if (result == null)
            return 0;

        // If card is a character and it is "doubled", then double the printed number
        if (physicalCard.getBlueprint().getCardCategory()==CardCategory.CHARACTER
                && isDoubled(gameState, physicalCard, modifierCollector)) {
            result *= 2;
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY, physicalCard)) {
            result += modifier.getDestinyModifier(gameState, this, physicalCard);
            modifierCollector.addModifier(modifier);
        }

        return result;
    }

    /**
     * Determines if a card's destiny value is less than a specified value.
     *
     * @param gameState the game state
     * @param card a card
     * @param value the destiny value
     * @return true if card's destiny value is less than the specified value, otherwise false
     */
    @Override
    public boolean hasDestinyLessThan(GameState gameState, PhysicalCard card, float value) {
        return getDestiny(gameState, card) < value;
    }

    /**
     * Determines if a card's destiny value is less than or equal to a specified value.
     *
     * @param gameState the game state
     * @param card a card
     * @param value the destiny value
     * @return true if card's destiny value is less than or equal to the specified value, otherwise false
     */
    @Override
    public boolean hasDestinyLessThanOrEqualTo(GameState gameState, PhysicalCard card, float value) {
        return getDestiny(gameState, card) <= value;
    }

    /**
     * Determines if a card's destiny value is equal to a specified value.
     *
     * @param gameState the game state
     * @param card a card
     * @param value the destiny value
     * @return true if card's destiny value is equal to the specified value, otherwise false
     */
    @Override
    public boolean hasDestinyEqualTo(GameState gameState, PhysicalCard card, float value) {
        return getDestiny(gameState, card) == value;
    }

    /**
     * Gets the destiny value when a card is drawn for destiny.
     * @param gameState the game state
     * @param card the card drawn for destiny
     * @param destinyDrawActionSource the source card for the draw destiny action
     * @return the destiny value
     */
    @Override
    public float getDestinyForDestinyDraw(GameState gameState, PhysicalCard card, PhysicalCard destinyDrawActionSource) {
        Float result = card.getDestinyValueToUse();

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DESTINY, card)) {
            result = modifier.getPrintedValueDefinedByGameText(gameState, this, card);
        }
        // If value if undefined, then return 0
        if (result == null)
            return 0;

        // If card is a character and it is "doubled", then double the printed number
        if (card.getBlueprint().getCardCategory()==CardCategory.CHARACTER
                && isDoubled(gameState, card)) {
            result *= 2;
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY, card)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getDestinyModifier(gameState, this, card);
            }
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_WHEN_DRAWN_FOR_DESTINY, card)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getDestinyWhenDrawnForDestinyModifier(gameState, this, card);
            }
        }
        for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
            if (modifier.isForTopDrawDestinyEffect(gameState)) {
                if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    result += modifier.getValue(gameState, this, (PhysicalCard) null);
                }
            }
        }
        if (destinyDrawActionSource != null) {
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EACH_DESTINY_DRAW_FOR_ACTION_SOURCE, destinyDrawActionSource)) {
                if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    result += modifier.getDestinyDrawFromSourceCardModifier(card.getOwner(), gameState, this, destinyDrawActionSource);
                }
            }
        }

        return result;
    }

    /**
     * Determines if the card has landspeed.
     *
     * @param gameState the game state
     * @param card a card
     * @return true if card has landspeed, otherwise false
     */
    @Override
    public boolean hasLandspeed(GameState gameState, PhysicalCard card) {
        return hasLandspeed(gameState, card, false);
    }

    private boolean hasLandspeed(GameState gameState, PhysicalCard card, boolean skipLandspeedValueCheck) {
        if (!card.getBlueprint().hasLandspeedAttribute() && !card.getBlueprint().isMovesLikeCharacter())
            return false;

        if (!skipLandspeedValueCheck) {
            return getLandspeed(gameState, card) > 0;
        }

        return true;
    }

    /**
     * Determines if a card's landspeed is more than a specified value.
     *
     * @param gameState the game state
     * @param card a card
     * @param value the landspeed value
     * @return true if card's landspeed is more than the specified value, otherwise false
     */
    @Override
    public boolean hasLandspeedMoreThan(GameState gameState, PhysicalCard card, float value) {
        return getLandspeed(gameState, card) > value;
    }

    @Override
    public float getLandspeed(GameState gameState, PhysicalCard physicalCard) {
        return getLandspeed(gameState, physicalCard, new ModifierCollectorImpl());
    }

    @Override
    public float getLandspeed(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
        if (!hasLandspeed(gameState, physicalCard, true))
            return 0;

        if (physicalCard.getBlueprint().isMovesLikeCharacter()) {
            return 1;
        }

        Float result = physicalCard.getBlueprint().getLandspeed();
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_LANDSPEED, physicalCard)) {
            result = modifier.getPrintedValueDefinedByGameText(gameState, this, physicalCard);
            modifierCollector.addModifier(modifier);
        }
        // If value if undefined, then return 0
        if (result == null)
            return 0;

        boolean canLandspeedBeIncreased = !isProhibitedFromHavingLandspeedIncreased(gameState, physicalCard);

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.LANDSPEED, physicalCard)) {
            float modifyAmount = modifier.getValue(gameState, this, physicalCard);
            if (modifyAmount < 0 || canLandspeedBeIncreased) {
                result += modifyAmount;
                modifierCollector.addModifier(modifier);
            }
        }

        // Check if value was reset to an "unmodifiable value", and use lowest found
        Float lowestResetValue = null;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_LANDSPEED, physicalCard)) {
            float resetValue = modifier.getValue(gameState, this, physicalCard);
            if (resetValue < result || canLandspeedBeIncreased) {
                lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, resetValue) : resetValue;
                modifierCollector.addModifier(modifier);
            }
        }
        if (lowestResetValue != null) {
            result = lowestResetValue;
        }

        return Math.max(0, result);
    }

    /**
     * Determines if a card's landspeed may not be increased.
     * @param gameState the game state
     * @param card a card
     * @return true if card's landspeed may not be increased, otherwise false
     */
    @Override
    public boolean isProhibitedFromHavingLandspeedIncreased(GameState gameState, PhysicalCard card) {
        return !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_HAVE_LANDSPEED_INCREASED, card).isEmpty();
    }

    /**
     * Gets landspeed required to move the card to the specified site using landspeed.
     * @param gameState the game state
     * @param card a card
     * @param toSite the site to move to
     * @return the landspeed required to move, or null if not valid to calculate
     */
    @Override
    public Integer getLandspeedRequired(GameState gameState, PhysicalCard card, PhysicalCard toSite) {
        PhysicalCard fromSite = getLocationThatCardIsAt(gameState, card);
        if (fromSite == null) {
            return null;
        }

        int landspeedRequiredForSite = 1;

        // Moving from initial site
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_FROM_LOCATION_LANDSPEED_REQUIREMENT, fromSite)) {
            if (modifier.isAffectedTarget(gameState, this, card)
                    && !isImmuneToLandspeedRequirementsFromCard(gameState, card, modifier.getSource(gameState))) {
                landspeedRequiredForSite += modifier.getValue(gameState, this, card);
            }
        }
        int totalLandspeedRequired = Math.max(0, landspeedRequiredForSite);

        List<PhysicalCard> sitesBetween = getSitesBetween(gameState, fromSite, toSite);
        if (sitesBetween == null) {
            return null;
        }

        // Moving from sites in between
        for (PhysicalCard siteBetween : sitesBetween) {
            landspeedRequiredForSite = 1;

            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_FROM_LOCATION_LANDSPEED_REQUIREMENT, siteBetween)) {
                if (modifier.isAffectedTarget(gameState, this, card)
                        && !isImmuneToLandspeedRequirementsFromCard(gameState, card, modifier.getSource(gameState))) {
                    landspeedRequiredForSite += modifier.getValue(gameState, this, card);
                }
            }

            totalLandspeedRequired += Math.max(0, landspeedRequiredForSite);
        }

        return totalLandspeedRequired;
    }

    /**
     * Determines if the specified card is immune to landspeed requirements from the specified source card.
     * @param gameState the game state
     * @param card the card
     * @param sourceCard the source of the modifier
     * @return true if card is immune, otherwise false
     */
    @Override
    public boolean isImmuneToLandspeedRequirementsFromCard(GameState gameState, PhysicalCard card, PhysicalCard sourceCard) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IMMUNE_TO_LANDSPEED_REQUIREMENTS, card)) {
            if (modifier.isImmuneToLandspeedRequirementModifierFromCard(gameState, this, sourceCard)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the card does not have a hyperdrive.
     * @param gameState the game state
     * @param card a card
     * @return true if card does not have a hyperdrive, otherwise false
     */
    @Override
    public boolean hasNoHyperdrive(GameState gameState, PhysicalCard card) {
        if (!card.getBlueprint().hasHyperspeedAttribute())
            return true;

        return hasKeyword(gameState, card, Keyword.NO_HYPERDRIVE);
    }

    /**
     * Gets a card's hyperspeed value.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the hyperspeed value
     */
    @Override
    public float getHyperspeed(GameState gameState, PhysicalCard physicalCard) {
        return getHyperspeed(gameState, physicalCard, null, null, new ModifierCollectorImpl());
    }

    /**
     * Gets a card's hyperspeed value.
     * @param gameState the game state
     * @param physicalCard a card
     * @param modifierCollector collector of affecting modifiers
     * @return the hyperspeed value
     */
    @Override
    public float getHyperspeed(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
        return getHyperspeed(gameState, physicalCard, null, null, modifierCollector);
    }

    /**
     * Gets a card's hyperspeed value when moving to the specified system.
     * @param gameState the game state
     * @param card a card
     * @param fromSystem the system to move from
     * @param toSystem the system to move to
     * @return the hyperspeed value
     */
    @Override
    public float getHyperspeed(GameState gameState, PhysicalCard card, PhysicalCard fromSystem, PhysicalCard toSystem) {
        return getHyperspeed(gameState, card, fromSystem, toSystem, new ModifierCollectorImpl());
    }

    /**
     * Gets a card's hyperspeed value when moving to the specified system.
     * @param gameState the game state
     * @param card a card
     * @param fromSystem the system to move from
     * @param toSystem the system to move to
     * @param modifierCollector collector of affecting modifiers
     * @return the hyperspeed value
     */
    @Override
    public float getHyperspeed(GameState gameState, PhysicalCard card, PhysicalCard fromSystem, PhysicalCard toSystem, ModifierCollector modifierCollector) {
        if (hasNoHyperdrive(gameState, card))
            return 0;

        Float result = card.getBlueprint().getHyperspeed();
        // If value if undefined, then return 0
        if (result == null)
            return 0;

        // Check if hyperspeed is modified
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.HYPERSPEED, card)) {
            result += modifier.getHyperspeedModifier(gameState, this, card);
            modifierCollector.addModifier(modifier);
        }

        // Check if hyperspeed is affected when moving from specific systems
        if (fromSystem != null) {
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.HYPERSPEED_WHEN_MOVING_FROM_LOCATION, card)) {
                if (modifier.isAffectedTarget(gameState, this, fromSystem)) {
                    result += modifier.getHyperspeedModifier(gameState, this, card);
                    modifierCollector.addModifier(modifier);
                }
            }
        }

        // Check if hyperspeed is affected when moving to specific systems
        if (toSystem != null) {
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.HYPERSPEED_WHEN_MOVING_TO_LOCATION, card)) {
                if (modifier.isAffectedTarget(gameState, this, toSystem)) {
                    result += modifier.getHyperspeedModifier(gameState, this, card);
                    modifierCollector.addModifier(modifier);
                }
            }
        }

        // Check if value was reset to an "unmodifiable value", and use lowest found
        Float lowestResetValue = null;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_HYPERSPEED, card)) {
            float modifierAmount = modifier.getValue(gameState, this, card);
            lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
            modifierCollector.addModifier(modifier);
        }
        if (lowestResetValue != null) {
            result = lowestResetValue;
        }

        return Math.max(0, result);
    }

    /**
     * Determines if the card has maneuver.
     *
     * @param gameState the game state
     * @param card a card
     * @return true if card has maneuver, otherwise false
     */
    @Override
    public boolean hasManeuver(GameState gameState, PhysicalCard card) {
        return hasManeuver(gameState, card, false);
    }

    private boolean hasManeuver(GameState gameState, PhysicalCard card, boolean skipManeuverValueCheck) {
        if (!card.getBlueprint().hasManeuverAttribute())
            return false;

        if (!skipManeuverValueCheck) {
            return getManeuver(gameState, card) > 0;
        }

        return true;
    }

    /**
     * Determines if a card's maneuver is more than a specified value.
     *
     * @param gameState the game state
     * @param card a card
     * @param value the maneuver value
     * @return true if card's maneuver is more than the specified value, otherwise false
     */
    @Override
    public boolean hasManeuverMoreThan(GameState gameState, PhysicalCard card, float value) {
        return getManeuver(gameState, card) > value;
    }

    @Override
    public float getManeuver(GameState gameState, PhysicalCard physicalCard) {
        return getManeuver(gameState, physicalCard, new ModifierCollectorImpl());
    }

    @Override
    public float getManeuver(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
        if (!hasManeuver(gameState, physicalCard, true))
            return 0;

        if ((physicalCard.getBlueprint().getCardCategory()==CardCategory.STARSHIP || physicalCard.getBlueprint().getCardCategory()==CardCategory.VEHICLE)
                && !isPiloted(gameState, physicalCard, false))
            return 0;

        Float result = physicalCard.getBlueprint().getManeuver();

        if (result != null) {
            // If card is a character and it is "doubled", then double the printed number
            if (physicalCard.getBlueprint().getCardCategory()==CardCategory.CHARACTER
                    && isDoubled(gameState, physicalCard, modifierCollector)) {
                result *= 2;
            }

            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MANEUVER, physicalCard)) {
                result += modifier.getManeuverModifier(gameState, this, physicalCard);
                modifierCollector.addModifier(modifier);
            }
        }

        // Check if value was reset to an "unmodifiable value", and use lowest found
        Float lowestResetValue = null;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_MANEUVER, physicalCard)) {
            float modifierAmount = modifier.getValue(gameState, this, physicalCard);
            lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
            modifierCollector.addModifier(modifier);
        }
        if (lowestResetValue != null) {
            result = lowestResetValue;
        }

        if (result == null)
            return 0;

        return Math.max(0, result);
    }

    /**
     * Determines if the card has armor.
     *
     * @param gameState the game state
     * @param card a card
     * @return true if card has armor, otherwise false
     */
    @Override
    public boolean hasArmor(GameState gameState, PhysicalCard card) {
        return hasArmor(gameState, card, false);
    }

    private boolean hasArmor(GameState gameState, PhysicalCard card, boolean skipArmorValueCheck) {
        if (!card.getBlueprint().hasArmorAttribute())
            return false;

        if (!skipArmorValueCheck) {
            return getArmor(gameState, card) > 0;
        }

        return true;
    }

    @Override
    public float getArmor(GameState gameState, PhysicalCard physicalCard) {
        return getArmor(gameState, physicalCard, new ModifierCollectorImpl());
    }

    @Override
    public float getArmor(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
        if (!hasArmor(gameState, physicalCard, true))
            return 0;

        Float result = physicalCard.getBlueprint().getArmor();

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_ARMOR, physicalCard)) {
            result = modifier.getPrintedValueDefinedByGameText(gameState, this, physicalCard);
            modifierCollector.addModifier(modifier);
        }

        if (result != null) {
            // If card is a starship or vehicle and it is unpiloted, armor = 2
            if ((physicalCard.getBlueprint().getCardCategory() == CardCategory.STARSHIP || physicalCard.getBlueprint().getCardCategory() == CardCategory.VEHICLE)
                    && !isPiloted(gameState, physicalCard, false)) {
                return 2;
            }

            // If card is a character and it is "doubled", then double the printed number
            if (physicalCard.getBlueprint().getCardCategory() == CardCategory.CHARACTER
                    && isDoubled(gameState, physicalCard, modifierCollector)) {
                result *= 2;
            }

            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.ARMOR, physicalCard)) {
                result += modifier.getArmorModifier(gameState, this, physicalCard);
                modifierCollector.addModifier(modifier);
            }
        }

        // Check if value was reset to an "unmodifiable value", and use lowest found
        Float lowestResetValue = null;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_ARMOR, physicalCard)) {
            float modifierAmount = modifier.getValue(gameState, this, physicalCard);
            lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
            modifierCollector.addModifier(modifier);
        }
        if (lowestResetValue != null) {
            result = lowestResetValue;
        }

        if (result == null)
            return 0;

        return Math.max(0, result);
    }

    @Override
    public float getSpecialDefenseValue(GameState gameState, PhysicalCard physicalCard) {
        float result = physicalCard.getBlueprint().getSpecialDefenseValue();
        return Math.max(0, result);
    }

    /**
     * Gets a card's defense value.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the defense value
     */
    @Override
    public float getDefenseValue(GameState gameState, PhysicalCard physicalCard) {
        return getDefenseValue(gameState, physicalCard, new ModifierCollectorImpl());
    }

    /**
     * Gets a card's defense value.
     * @param gameState the game state
     * @param physicalCard a card
     * @param modifierCollector collector of affecting modifiers
     * @return the defense value
     */
    @Override
    public float getDefenseValue(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
        float result = 0;

        // Determine value to use for defense value
        if (physicalCard.isDejarikHologramAtHolosite()
                || (physicalCard.getBlueprint().getCardCategory()==CardCategory.CHARACTER
                        && !physicalCard.getBlueprint().hasIcon(Icon.DROID))) {
            result = Math.max(result, getAbility(gameState, physicalCard));
        }
        else {
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DEFENSE_VALUE, physicalCard)) {
                result = modifier.getPrintedValueDefinedByGameText(gameState, this, physicalCard);
                modifierCollector.addModifier(modifier);
            }
        }
        result = Math.max(result, getManeuver(gameState, physicalCard));
        result = Math.max(result, getArmor(gameState, physicalCard));
        if (physicalCard.getBlueprint().hasSpecialDefenseValueAttribute()) {
            result = Math.max(result, getSpecialDefenseValue(gameState, physicalCard));
        }

        float defenseValueBeforeModified = result;

        // Check if defense value is modified
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DEFENSE_VALUE, physicalCard)) {
            result +=  modifier.getDefenseValueModifier(gameState, this, physicalCard);
            modifierCollector.addModifier(modifier);
        }

        // Check if defense value may not be reduced below a specified value
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MIN_DEFENSE_VALUE_REDUCED_TO, physicalCard)) {
            float modifierAmount = modifier.getValue(gameState, this, physicalCard);
            if (modifierAmount <= defenseValueBeforeModified) {
                result = Math.max(modifierAmount, result);
            }
            modifierCollector.addModifier(modifier);
        }

        // Check if value was reset to an "unmodifiable value", and use lowest found
        Float lowestResetValue = null;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_DEFENSE_VALUE, physicalCard)) {
            float modifierAmount = modifier.getValue(gameState, this, physicalCard);
            lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
            modifierCollector.addModifier(modifier);
        }
        if (lowestResetValue != null) {
            result = lowestResetValue;
        }

        return Math.max(0, result);
    }

    @Override
    public Statistic getDefenseValueStatistic(GameState gameState, PhysicalCard physicalCard) {
        Statistic result;
        float highestValue;
        SwccgCardBlueprint blueprint = physicalCard.getBlueprint();
        if (physicalCard.isDejarikHologramAtHolosite()
                || (blueprint.getCardCategory()==CardCategory.CHARACTER
                        && !blueprint.hasIcon(Icon.DROID))) {
            result = Statistic.ABILITY;
            highestValue = getAbility(gameState, physicalCard, false);
        }
        else {
            result = Statistic.DEFENSE_VALUE;
            highestValue = 0;
        }

        if (blueprint.getCardCategory()==CardCategory.CHARACTER
                || blueprint.getCardCategory()==CardCategory.VEHICLE
                || blueprint.getCardCategory()==CardCategory.STARSHIP) {
            float maneuver = getManeuver(gameState, physicalCard);
            if (maneuver > highestValue) {
                result = Statistic.MANEUVER;
                highestValue = maneuver;
            }
            float armor = getArmor(gameState, physicalCard);
            if (armor > highestValue) {
                result = Statistic.ARMOR;
                highestValue = armor;
            }
        }

        return result;
    }

    /**
     * Gets the permanent weapon built into the card, not including if card is disarmed or game text canceled.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the permanent weapon
     */
    @Override
    public SwccgBuiltInCardBlueprint getPermanentWeapon(GameState gameState, PhysicalCard physicalCard) {
        if (physicalCard.getBlueprint().getCardCategory() != CardCategory.CHARACTER)
            return null;

        SwccgBuiltInCardBlueprint permWeapon = physicalCard.getBlueprint().getPermanentWeapon(physicalCard);
        if (permWeapon == null)
            return null;

        if (physicalCard.isDisarmed())
            return null;

        return permWeapon;
    }

    /**
     * Gets the permanent pilots and astromechs aboard the card, not including any that are removed/suspended.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the permanent pilots and astromechs
     */
    @Override
    public List<SwccgBuiltInCardBlueprint> getPermanentsAboard(GameState gameState, PhysicalCard physicalCard) {
        List<SwccgBuiltInCardBlueprint> permanentsAboard = new ArrayList<SwccgBuiltInCardBlueprint>();
        permanentsAboard.addAll(getPermanentPilotsAboard(gameState, physicalCard));
        permanentsAboard.addAll(getPermanentAstromechsAboard(gameState, physicalCard));
        return permanentsAboard;
    }

    /**
     * Gets the permanent pilots aboard the card, not including any that are removed/suspended.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the permanent pilots
     */
    @Override
    public List<SwccgBuiltInCardBlueprint> getPermanentPilotsAboard(GameState gameState, PhysicalCard physicalCard) {
        List<SwccgBuiltInCardBlueprint> permanentsAboard = physicalCard.getBlueprint().getPermanentsAboard(physicalCard);
        if (permanentsAboard == null || permanentPilotsSuspended(gameState, physicalCard))
            return Collections.emptyList();

        List<SwccgBuiltInCardBlueprint> permanentPilots = new ArrayList<SwccgBuiltInCardBlueprint>();
        for (SwccgBuiltInCardBlueprint permanentAboard : permanentsAboard) {
            if (permanentAboard.isPilot()) {
                permanentPilots.add(permanentAboard);
            }
        }

        return permanentPilots;
    }

    /**
     * Gets the permanent astromechs aboard the card, not including any that are removed/suspended.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the permanent astromechs
     */
    @Override
    public List<SwccgBuiltInCardBlueprint> getPermanentAstromechsAboard(GameState gameState, PhysicalCard physicalCard) {
        List<SwccgBuiltInCardBlueprint> permanentsAboard = physicalCard.getBlueprint().getPermanentsAboard(physicalCard);
        if (permanentsAboard == null || permanentAstromechsSuspended(gameState, physicalCard))
            return Collections.emptyList();

        List<SwccgBuiltInCardBlueprint> permanentAstromechs = new ArrayList<SwccgBuiltInCardBlueprint>();
        for (SwccgBuiltInCardBlueprint permanentAboard : permanentsAboard) {
            if (permanentAboard.isAstromech()) {
                permanentAstromechs.add(permanentAboard);
            }
        }

        return permanentAstromechs;
    }

    /**
     * Gets the modifiers from the specified permanent built-in.
     * @param gameState the game state
     * @param permanentBuiltIn the permanent built-in
     * @return the modifiers
     */
    @Override
    public List<Modifier> getModifiersFromPermanentBuiltIn(GameState gameState, SwccgBuiltInCardBlueprint permanentBuiltIn) {
        PhysicalCard card = permanentBuiltIn.getPhysicalCard(_swccgGame);
        return permanentBuiltIn.getGameTextModifiers(card);
    }

    /**
     * Gets the sabacc total for the player.
     * @param gameState the game state
     * @param playerId the player
     * @return the sabacc total
     */
    @Override
    public float getSabaccTotal(GameState gameState, String playerId) {
        float result = 0;

        for (PhysicalCard sabaccCard : gameState.getSabaccHand(playerId)) {
            result += sabaccCard.getSabaccValue();
        }

        // Check modifiers to "sabacc total"
        for (Modifier modifier : getModifiers(gameState, ModifierType.SABACC_TOTAL))
            result += modifier.getSabaccTotalModifier(playerId, gameState, this);

        result = Math.max(0, result);
        return result;
    }

    /**
     * Determines if the card may have its destiny number cloned in sabacc by specified player when not in sabacc hand.
     * @param gameState the game state
     * @param card the card
     * @param playerId the player
     * @return true or false
     */
    @Override
    public boolean mayHaveDestinyNumberClonedInSabacc(GameState gameState, PhysicalCard card, String playerId) {
        for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_CLONE_DESTINY_IN_SABACC)) {
            if (modifier.isForPlayer(playerId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the amount of Force generation at a location for the player.
     * @param gameState the game state
     * @param physicalCard the location
     * @param playerId the player
     * @return the amount of Force generation
     */
    @Override
    public float getForceGenerationFromLocation(GameState gameState, PhysicalCard physicalCard, String playerId) {
        return getForceGenerationFromLocation(gameState, physicalCard, playerId, new ModifierCollectorImpl());
    }

    /**
     * Gets the amount of Force generation at a location for the player.
     * @param gameState the game state
     * @param physicalCard the location
     * @param playerId the player
     * @param modifierCollector collector of affecting modifiers
     * @return the amount of Force generation
     */
    @Override
    public float getForceGenerationFromLocation(GameState gameState, PhysicalCard physicalCard, String playerId, ModifierCollector modifierCollector) {
        float result = getIconCount(gameState, physicalCard, gameState.getDarkPlayer().equals(playerId) ? Icon.DARK_FORCE : Icon.LIGHT_FORCE);

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FORCE_GENERATION_AT_LOCATION, physicalCard)) {
            if (modifier.isForPlayer(playerId)) {
                result += modifier.getValue(gameState, this, physicalCard);
                modifierCollector.addModifier(modifier);
            }
        }

        // Check if value was reset to an "unmodifiable value", and use lowest found
        Float lowestResetValue = null;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_FORCE_GENERATION_AT_LOCATION, physicalCard)) {
            if (modifier.isForPlayer(playerId)) {
                float modifierAmount = modifier.getValue(gameState, this, physicalCard);
                lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
                modifierCollector.addModifier(modifier);
            }
        }
        if (lowestResetValue != null) {
            result = lowestResetValue;
        }

        // Limit Force Generation
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.LIMIT_FORCE_GENERATION_AT_LOCATION, physicalCard)) {
            if (modifier.isForPlayer(playerId)) {
                result = Math.min(result, modifier.getValue(gameState, this, physicalCard));
                modifierCollector.addModifier(modifier);
            }
        }

        return Math.max(0, result);
    }

    /**
     * Determines if the specified player is explicitly not allowed to activate Force due to existence of a "can't activate Force"
     * modifier affecting the player.
     * @param gameState the game state
     * @param playerId the player
     * @return true if player not allowed to activate Force, otherwise false
     */
    @Override
    public boolean isActivatingForceProhibited(GameState gameState, String playerId) {
        return hasFlagActive(gameState, ModifierFlag.MAY_NOT_ACTIVATE_FORCE, playerId);
    }

    /**
     * Determines if the specified player is explicitly not allowed to activate Force due to existence of a "can't activate Force"
     * modifier affecting the player.
     * @param gameState the game state
     * @param playerId the player
     * @return true if player not allowed to activate Force, otherwise false
     */
    @Override
    public boolean isActivateForceFromForceGenerationLimitReached(GameState gameState, String playerId) {
        return getForceActivatedThisTurn(playerId, true) >= Math.floor(gameState.getPlayersTotalForceGeneration(playerId));
    }

    /**
     * Gets the total Force generation for the specified player.
     * @param gameState the game state
     * @param playerId the player
     * @return the total Force generation for the player
     */
    @Override
    public float getTotalForceGeneration(GameState gameState, String playerId) {
        float total = 1; // 1 Force from player

        // Add Force generation from locations
        List<PhysicalCard> locations = gameState.getTopLocations();
        for (PhysicalCard location : locations) {
            total += getForceGenerationFromLocation(gameState, location, playerId);
        }

        for (Modifier modifier : getModifiers(gameState, ModifierType.TOTAL_FORCE_GENERATION)) {
            total += modifier.getTotalForceGenerationModifier(playerId, gameState, this);
        }

        // Add 1 for each character with matching Force icon
        Icon icon = playerId.equals(gameState.getDarkPlayer()) ? Icon.DARK_JEDI_MASTER : Icon.JEDI_MASTER;
        total += Filters.countActive(gameState.getGame(), null, Filters.and(CardCategory.CHARACTER, icon));

        return Math.max(0, total);
    }

    /**
     * Increments the amount of Force that has been activated by the player.
     * @param playerId the player
     * @param fromForceGeneration true if Force was activated due to Force generation, otherwise false
     */
    @Override
    public void forceActivated(String playerId, boolean fromForceGeneration) {
        final Integer totalActivated = _forceActivatedThisTurnMap.get(playerId);
        if (totalActivated == null)
            _forceActivatedThisTurnMap.put(playerId, 1);
        else
            _forceActivatedThisTurnMap.put(playerId, totalActivated + 1);

        if (fromForceGeneration) {
            final Integer totalActivatedFromFG = _forceGenerationActivatedThisTurnMap.get(playerId);
            if (totalActivatedFromFG == null)
                _forceGenerationActivatedThisTurnMap.put(playerId, 1);
            else
                _forceGenerationActivatedThisTurnMap.put(playerId, totalActivatedFromFG + 1);
        }

        Phase phase = _swccgGame.getGameState().getCurrentPhase();
        Map<String, Integer> activationMap = _forceActivatedPerPhaseMap.get(phase);
        if (activationMap == null) {
            activationMap = new HashMap<String, Integer>();
            _forceActivatedPerPhaseMap.put(phase, activationMap);
        }
        final Integer totalActivatedThisPhase = activationMap.get(playerId);
        if (totalActivatedThisPhase == null)
            activationMap.put(playerId, 1);
        else
            activationMap.put(playerId, totalActivatedThisPhase + 1);
    }

    /**
     * Gets the amount of Force the player has activated this turn.
     * @param playerId the player
     * @param onlyFromForceGeneration true if only Force activate due to Force generation is counted
     * @return the amount of Force
     */
    @Override
    public int getForceActivatedThisTurn(String playerId, boolean onlyFromForceGeneration) {
        if (onlyFromForceGeneration) {
            final Integer totalActivatedFromFG = _forceGenerationActivatedThisTurnMap.get(playerId);
            if (totalActivatedFromFG == null)
                return 0;
            return totalActivatedFromFG;
        }
        else {
            final Integer totalActivated = _forceActivatedThisTurnMap.get(playerId);
            if (totalActivated == null)
                return 0;
            return totalActivated;
        }
    }

    /**
     * Gets the amount of Force the player has activated this phase.
     * @param playerId the player
     * @return the amount of Force
     */
    @Override
    public int getForceActivatedThisPhase(String playerId) {
        Phase phase = _swccgGame.getGameState().getCurrentPhase();
        Map<String, Integer> activationMap = _forceActivatedPerPhaseMap.get(phase);
        if (activationMap == null) {
            activationMap = new HashMap<String, Integer>();
            _forceActivatedPerPhaseMap.put(phase, activationMap);
        }
        final Integer totalActivated = activationMap.get(playerId);
        if (totalActivated == null) {
            return 0;
        }
        return totalActivated;
    }

    /**
     * Gets the amount of Force the player has available to use.
     * @param gameState the game state
     * @param playerId the player
     * @return the amount of Force
     */
    @Override
    public int getForceAvailableToUse(GameState gameState, String playerId) {
        int playersForcePileSize = gameState.getForcePile(playerId).size();
        int opponentsForceAvailable = getOpponentsForceAvailableToUse(gameState, playerId);

        return Math.max(0, playersForcePileSize + opponentsForceAvailable);
    }

    /**
     * Gets the amount of opponent's Force the player can has available to use.
     * @param gameState the game state
     * @param playerId the player
     * @return the amount of Force
     */
    @Override
    public int getOpponentsForceAvailableToUse(GameState gameState, String playerId) {
        int opponentsForcePileSize = gameState.getForcePile(gameState.getOpponent(playerId)).size();

        // Determine the maximum number of opponent's Force that can be used
        int opponentsForceAvailable = 0;
        for (int forceToUse = opponentsForcePileSize; forceToUse > 0; --forceToUse) {
            if (!getCardsToUseOpponentsForceFirst(gameState, playerId, forceToUse, opponentsForcePileSize).isEmpty()) {
                opponentsForceAvailable = forceToUse;
                break;
            }
        }

        return Math.max(0, opponentsForceAvailable);
    }

    /**
     * Gets the maximum number of Force the player may use from opponent's Force Pile via the specified card.
     * @param gameState the game state
     * @param playerId the player
     * @param card the card
     * @param opponentsForceAlreadyToBeUsed the amount of opponent's Force already reserved to be used
     * @param minOpponentForceToUse the minimum amount of total opponent's Force that must be used
     * @return the amount of Force
     */
    @Override
    public int getMaxOpponentsForceToUseViaCard(GameState gameState, String playerId, PhysicalCard card, int opponentsForceAlreadyToBeUsed, int minOpponentForceToUse) {
        String opponent = gameState.getOpponent(playerId);
        int opponentsForcePileSize = Math.max(0, gameState.getForcePile(opponent).size() - opponentsForceAlreadyToBeUsed);
        int minToUse = Math.max(0, minOpponentForceToUse - opponentsForceAlreadyToBeUsed);

        // Determine the maximum number of opponent's Force that can be used by the card
        for (int forceToUse = opponentsForcePileSize; forceToUse > 0 && forceToUse >= minToUse; --forceToUse) {
            Map<PhysicalCard, Integer> cardMap = getCardsToUseOpponentsForceFirst(gameState, playerId, forceToUse, opponentsForcePileSize);
            if (cardMap.containsKey(card)) {
                return cardMap.get(card);
            }
        }
        return 0;
    }

    /**
     * Determine the cards that must be used by the player to use opponent's Force first in order to use the specified
     * amount of opponent's Force and how much those cards can use if used first.
     * @param gameState the game state
     * @param playerId the player
     * @param forceToUse the amount of Force to attempt to use
     * @param opponentsForcePileSize the size of opponents Force Pile
     * @return the map containing a card that must be used first and max Force that may be used
     */
    private Map<PhysicalCard, Integer> getCardsToUseOpponentsForceFirst(GameState gameState, String playerId, int forceToUse, int opponentsForcePileSize) {
        Map<PhysicalCard, Integer> validCardsToUseFirst = new HashMap<PhysicalCard, Integer>();

        // Look at modifiers that allow player to use opponent's Force
        List<PhysicalCard> cardList = new ArrayList<PhysicalCard>();
        Map<PhysicalCard, Integer> maxUsableByCardMap = new HashMap<PhysicalCard, Integer>();
        Map<PhysicalCard, Integer> minForcePileRequiredByCardMap = new HashMap<PhysicalCard, Integer>();
        for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_USE_OPPONENTS_FORCE)) {
            if (modifier.isForPlayer(playerId)) {
                cardList.add(modifier.getSource(gameState));
                maxUsableByCardMap.put(modifier.getSource(gameState), Math.min(forceToUse, (int) modifier.getValue(gameState, this, (PhysicalCard) null)));
                minForcePileRequiredByCardMap.put(modifier.getSource(gameState), modifier.getMinForcePileSizeToUseOpponentsForce(gameState, this));
            }
        }

        if (!cardList.isEmpty()) {
            List<List<PhysicalCard>> cardUsageOrderPermutations = generateCardUsageOrderPermutations(cardList, maxUsableByCardMap);
            for (List<PhysicalCard> cardUsageOrder : cardUsageOrderPermutations) {
                if (!cardUsageOrder.isEmpty() && cardUsageOrder.size() >= forceToUse) {
                    PhysicalCard firstCard = cardUsageOrder.get(0);
                    int numForceIfUsedFirst = getMaxOpponentsForceFirstCardCanUseInUsageOrder(forceToUse, opponentsForcePileSize, cardUsageOrder, minForcePileRequiredByCardMap);
                    if (numForceIfUsedFirst > 0) {
                        int valueToSet = validCardsToUseFirst.containsKey(firstCard) ? Math.max(numForceIfUsedFirst, validCardsToUseFirst.get(firstCard)) : numForceIfUsedFirst;
                        validCardsToUseFirst.put(firstCard, valueToSet);
                    }
                }
            }
        }

        return validCardsToUseFirst;
    }

    /**
     * Generates a list of the card usage order permutations.
     * @param cardList the cards
     * @param maxUsableByCard the max amount of opponent's Force the card allows
     * @return list of the card order permutations
     */
    private List<List<PhysicalCard>> generateCardUsageOrderPermutations(List<PhysicalCard> cardList, Map<PhysicalCard, Integer> maxUsableByCard) {
        List<PhysicalCard> expandedCardList = new ArrayList<PhysicalCard>();
        for (PhysicalCard card : cardList) {
            for (int i=0; i<maxUsableByCard.get(card); ++i) {
                expandedCardList.add(card);
            }
        }
        List<List<PhysicalCard>> allPermutations = new ArrayList<List<PhysicalCard>>();
        generateCardListPermutations(allPermutations, new ArrayList<PhysicalCard>(), new ArrayList<PhysicalCard>(expandedCardList));
        return allPermutations;
    }

    /**
     * Generates all the card permutations (recursively).
     * @param allPermutations all the permutations
     * @param prefixList prefix list
     * @param suffixList suffix list
     */
    private void generateCardListPermutations(List<List<PhysicalCard>> allPermutations, List<PhysicalCard> prefixList, List<PhysicalCard> suffixList) {
        int n = suffixList.size();
        if (n == 0) {
            allPermutations.add(prefixList);
        }
        else {
            for (int i = 0; i < n; ++i) {
                List<PhysicalCard> newPrefixList = new ArrayList<PhysicalCard>();
                newPrefixList.addAll(prefixList);
                newPrefixList.add(suffixList.get(i));
                List<PhysicalCard> newSuffixList = new ArrayList<PhysicalCard>();
                newSuffixList.addAll(suffixList.subList(0, i));
                newSuffixList.addAll(suffixList.subList(i+1, n));
                generateCardListPermutations(allPermutations, newPrefixList, newSuffixList);
            }
        }
    }

    /**
     * Determine the cards that must be used to use opponent's Force first in order to use the specified amount of opponent's Force.
     * @param forceToUse the amount of Force to attempt to use
     * @param opponentsForcePileSize the size of opponents Force Pile
     * @param cardUsageOrder the order of cards to use that allow using opponent's Force
     * @param minForcePileRequiredByCard the minimum amount of Force in opponent's Force Pile required for the card to allow using opponent's Force
     * @return the list containing a card that must be used first
     */
    private int getMaxOpponentsForceFirstCardCanUseInUsageOrder(int forceToUse, int opponentsForcePileSize, List<PhysicalCard> cardUsageOrder, Map<PhysicalCard, Integer> minForcePileRequiredByCard) {
        if (cardUsageOrder.isEmpty() || (cardUsageOrder.size() < forceToUse)) {
            return 0;
        }

        int forceUsedByFirstCard = 0;
        boolean foundOtherCard = false;
        int opponentsForcePileSizeLeft = opponentsForcePileSize;
        for (int i=0; i<forceToUse; ++i) {
            PhysicalCard cardToUse = cardUsageOrder.get(i);
            if (opponentsForcePileSizeLeft < minForcePileRequiredByCard.get(cardToUse)) {
                return 0;
            }
            if (cardToUse.getCardId() != cardUsageOrder.get(0).getCardId()) {
                foundOtherCard = true;
            }
            if (!foundOtherCard) {
                forceUsedByFirstCard++;
            }
            opponentsForcePileSizeLeft--;
        }
        return forceUsedByFirstCard;
    }

    /**
     * Gets the amount of extra Force required to fire the specified weapon (or permanent weapon).
     * @param gameState the game state
     * @param weaponCard the weapon card, or null if permanent weapon
     * @param permanentWeapon the permanent weapon, or null if not a permanent weapon
     * @return the amount of Force
     */
    @Override
    public int getExtraForceRequiredToFireWeapon(GameState gameState, PhysicalCard weaponCard, SwccgBuiltInCardBlueprint permanentWeapon) {
        int result = 0;
        for (Modifier modifier : getModifiers(gameState, ModifierType.EXTRA_FORCE_COST_TO_FIRE_WEAPON)) {
            if (weaponCard != null && modifier.isAffectedTarget(gameState, this, weaponCard)) {
                result += modifier.getValue(gameState, this, weaponCard);
            }
            if (permanentWeapon != null && modifier.isAffectedTarget(gameState, this, permanentWeapon)) {
                result += modifier.getValue(gameState, this, permanentWeapon);
            }
        }
        result = Math.max(0, result);
        return result;
    }

    /**
     * Gets the amount of extra Force required to deploy the specified card to the specified target.
     * @param gameState the game state
     * @param cardToDeploy the card to deploy
     * @param target the deploy target, or null
     * @param targetOfAttachedTo if deploying simultaneously on another card, the target for the card this card will be attached to, otherwise null
     * @param sourceCard the card to initiate the deployment
     * @param forFree true if deploy card explicitly for free, otherwise false
     * @return the amount of Force
     */
    @Override
    public int getExtraForceRequiredToDeployToTarget(GameState gameState, PhysicalCard cardToDeploy, PhysicalCard target, PhysicalCard targetOfAttachedTo, PhysicalCard sourceCard, boolean forFree) {
        int result = 0;
        if (target != null) {
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EXTRA_FORCE_COST_TO_DEPLOY_TO_TARGET, cardToDeploy)) {
                if (modifier.isAffectedTarget(gameState, this, target)) {
                    result += modifier.getValue(gameState, this, cardToDeploy, target);
                }
            }
        }

        // Check if card is deploying for free, but not due to its own game text
        boolean forFreeOwnGameText = forFree && sourceCard != null && sourceCard.getCardId() == cardToDeploy.getCardId();
        if (!forFreeOwnGameText) {
            boolean forFreeNotFromOwnGameText = forFree;

            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DEPLOYS_FREE, cardToDeploy)) {
                if (modifier.getSource(gameState) != null && modifier.getSource(gameState).getCardId() == cardToDeploy.getCardId()) {
                    forFreeOwnGameText = true;
                    break;
                }
                else {
                    forFreeNotFromOwnGameText = true;
                }
            }

            if (!forFreeOwnGameText) {
                for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DEPLOYS_FREE_TO_TARGET, cardToDeploy)) {
                    if (modifier.isDeployFreeToTarget(gameState, this, target)) {
                        if (modifier.getSource(gameState) != null && modifier.getSource(gameState).getCardId() == cardToDeploy.getCardId()) {
                            forFreeOwnGameText = true;
                            break;
                        }
                        else {
                            forFreeNotFromOwnGameText = true;
                        }
                    }
                    // Check if self deployment modifier is applied at any location
                    if (modifier.getSource(gameState) != null && modifier.getSource(gameState).getCardId() == cardToDeploy.getCardId()
                            && appliesOwnDeploymentModifiersAtAnyLocation(gameState, cardToDeploy)) {
                        forFreeOwnGameText = true;
                        break;
                    }
                }

                if (!forFreeNotFromOwnGameText) {
                    if (targetOfAttachedTo != null) {
                        // Check if pilot deploys for free when simultaneously deployed with ship
                        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.SIMULTANEOUS_PILOT_DEPLOYS_FOR_FREE, target)) {
                            if (modifier.isAffectedPilot(gameState, this, cardToDeploy)
                                    && modifier.isAffectedTarget(gameState, this, targetOfAttachedTo)) {
                                forFreeNotFromOwnGameText = true;
                                break;
                            }
                        }
                    }
                }

                if (!forFreeOwnGameText && forFreeNotFromOwnGameText) {
                    for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EXTRA_FORCE_COST_TO_DEPLOY_FOR_FREE_EXCEPT_BY_OWN_GAME_TEXT, cardToDeploy)) {
                        result += modifier.getValue(gameState, this, cardToDeploy);
                    }
                }
            }
        }

        result = Math.max(0, result);
        return result;
    }

    /**
     * Gets the amount of extra Force required to play the specified Interrupt.
     * @param gameState the game state
     * @param card the Interrupt card
     * @return the amount of Force
     */
    @Override
    public int getExtraForceRequiredToPlayInterrupt(GameState gameState, PhysicalCard card) {
        int result = 0;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EXTRA_FORCE_COST_TO_PLAY_INTERRUPT, card)) {
            result += modifier.getValue(gameState, this, card);
        }
        result = Math.max(0, result);
        return result;
    }

    /**
     * Records that a 'bluff card' was stacked.
     */
    @Override
    public void bluffCardStacked() {
        _bluffCardStacked = true;
    }

    /**
     * Determines if a bluff card was stacked this turn.
     * @return true or false
     */
    @Override
    public boolean isBluffCardStackedThisTurn() {
        return _bluffCardStacked;
    }

    /**
     * Records that the specified card being played (or being deployed).
     * @param card the card
     */
    @Override
    public void cardBeingPlayed(PhysicalCard card) {
        String playerId = card.getOwner();

        List<PhysicalCard> playedCardsThisGame = _cardPlayedThisGame.get(playerId);
        if (playedCardsThisGame == null) {
            playedCardsThisGame = new LinkedList<PhysicalCard>();
            _cardPlayedThisGame.put(playerId, playedCardsThisGame);
        }
        playedCardsThisGame.add(card);

        List<PhysicalCard> playedCardsThisTurn = _cardPlayedThisTurn.get(playerId);
        if (playedCardsThisTurn == null) {
            playedCardsThisTurn = new LinkedList<PhysicalCard>();
            _cardPlayedThisTurn.put(playerId, playedCardsThisTurn);
        }
        playedCardsThisTurn.add(card);
    }

    /**
     * Records that the specified card was just deployed on table.
     * @param card the card
     */
    @Override
    public void cardJustDeployed(PhysicalCard card) {
        String playerId = card.getOwner();

        if (_swccgGame.getModifiersQuerying().hasAbility(_swccgGame.getGameState(), card, true)) {
            List<PhysicalCard> deployedCardsWithAbilityThisTurn = _cardWithAbilityDeployedThisTurn.get(playerId);
            if (deployedCardsWithAbilityThisTurn == null) {
                deployedCardsWithAbilityThisTurn = new LinkedList<PhysicalCard>();
                _cardWithAbilityDeployedThisTurn.put(playerId, deployedCardsWithAbilityThisTurn);
            }
            deployedCardsWithAbilityThisTurn.add(card);
        }

        PhysicalCard location = getLocationThatCardIsAt(_swccgGame.getGameState(), card);
        if (location != null) {
            Integer locationId = location.getCardId();
            Map<Integer, List<PhysicalCard>> playedCardsToLocationMap = _cardPlayedToLocationThisTurn.get(playerId);
            if (playedCardsToLocationMap == null) {
                playedCardsToLocationMap = new HashMap<Integer, List<PhysicalCard>>();
                _cardPlayedToLocationThisTurn.put(playerId, playedCardsToLocationMap);
            }
            List<PhysicalCard> playedCardsToLocation = playedCardsToLocationMap.get(locationId);
            if (playedCardsToLocation == null) {
                playedCardsToLocation = new LinkedList<PhysicalCard>();
                playedCardsToLocationMap.put(locationId, playedCardsToLocation);
            }
            playedCardsToLocation.add(card);
        }
    }

    /**
     * Gets the starting location of the specified player.
     * @param playerId the player
     * @return the starting location, or null
     */
    @Override
    public PhysicalCard getStartingLocation(String playerId) {
        List<PhysicalCard> cardsPlayed = getCardsPlayedThisGame(playerId);
        for (PhysicalCard cardPlayed : cardsPlayed) {
            if (cardPlayed.isStartingLocation()) {
                return cardPlayed;
            }
        }
        return null;
    }

    /**
     * Gets the cards that were played (or deployed) this game by the specified player.
     * @param playerId the player
     * @return the cards
     */
    @Override
    public List<PhysicalCard> getCardsPlayedThisGame(String playerId) {
        List<PhysicalCard> cardsPlayed = _cardPlayedThisGame.get(playerId);
        if (cardsPlayed == null) {
            cardsPlayed = new LinkedList<PhysicalCard>();
            _cardPlayedThisGame.put(playerId, cardsPlayed);
        }
        return cardsPlayed;
    }

    /**
     * Gets the cards that were played (or deployed) this turn by the specified player.
     * @param playerId the player
     * @return the cards
     */
    @Override
    public List<PhysicalCard> getCardsPlayedThisTurn(String playerId) {
        List<PhysicalCard> cardsPlayed = _cardPlayedThisTurn.get(playerId);
        if (cardsPlayed == null) {
            cardsPlayed = new LinkedList<PhysicalCard>();
            _cardPlayedThisTurn.put(playerId, cardsPlayed);
        }
        return cardsPlayed;
    }

    /**
     * Gets the cards with ability that were deployed this turn by the specified player.
     * @param playerId the player
     * @return the cards
     */
    @Override
    public List<PhysicalCard> getCardsWithAbilityDeployedThisTurn(String playerId) {
        List<PhysicalCard> cardsWithAbilityDeployed = _cardWithAbilityDeployedThisTurn.get(playerId);
        if (cardsWithAbilityDeployed == null) {
            cardsWithAbilityDeployed = new LinkedList<PhysicalCard>();
            _cardWithAbilityDeployedThisTurn.put(playerId, cardsWithAbilityDeployed);
        }
        return cardsWithAbilityDeployed;
    }

    /**
     * Gets the cards that were played (or deployed) this turn by the specified player to the specified location.
     * @param playerId the player
     * @param location the location
     * @return the cards
     */
    @Override
    public List<PhysicalCard> getCardsPlayedThisTurnToLocation(String playerId, PhysicalCard location) {
        Map<Integer, List<PhysicalCard>> playedCardsToLocationMap = _cardPlayedToLocationThisTurn.get(playerId);
        if (playedCardsToLocationMap == null) {
            playedCardsToLocationMap = new HashMap<Integer, List<PhysicalCard>>();
            _cardPlayedToLocationThisTurn.put(playerId, playedCardsToLocationMap);
        }
        List<PhysicalCard> playedCardsToLocation = playedCardsToLocationMap.get(location.getCardId());
        if (playedCardsToLocation == null) {
            playedCardsToLocation = new LinkedList<PhysicalCard>();
            playedCardsToLocationMap.put(location.getCardId(), playedCardsToLocation);
        }
        return playedCardsToLocation;
    }

    /**
     * Gets the cost for the specified player to initiate a Force drain at the specified location.
     * @param gameState the game state
     * @param location the location
     * @param playerId the player
     * @return the cost
     */
    @Override
    public float getInitiateForceDrainCost(GameState gameState, PhysicalCard location, String playerId) {
        float result = 0;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.INITIATE_FORCE_DRAIN_COST, location)) {
            if (modifier.isForPlayer(playerId)) {
                result += modifier.getValue(gameState, this, location);
            }
        }
        return Math.max(0, result);
    }

    /**
     * Gets the amount of the Force drain.
     * @param gameState the game state
     * @param location the Force drain location
     * @param performingPlayerId the player performing the Force drain
     * @return the amount of the Force drain
     */
    @Override
    public float getForceDrainAmount(GameState gameState, PhysicalCard location, String performingPlayerId) {
        return getForceDrainAmount(gameState, location, performingPlayerId, new ModifierCollectorImpl());
    }

    /**
     * Gets the amount of the Force drain.
     * @param gameState the game state
     * @param location the Force drain location
     * @param performingPlayerId the player performing the Force drain
     * @param modifierCollector collector of affecting modifiers
     * @return the amount of the Force drain
     */
    @Override
    public float getForceDrainAmount(GameState gameState, PhysicalCard location, String performingPlayerId, ModifierCollector modifierCollector) {
        Icon icon = (gameState.getSide(performingPlayerId) == Side.DARK) ? Icon.LIGHT_FORCE : Icon.DARK_FORCE;

        int result = getIconCount(gameState, location, icon);
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FORCE_DRAIN_AMOUNT, location)) {
            if (modifier.isForPlayer(performingPlayerId)) {
                result += modifier.getForceDrainModifier(performingPlayerId, gameState, this, location);
                modifierCollector.addModifier(modifier);
            }
        }
        return Math.max(0, result);
    }

    /**
     * Records that a Force drain was initiated (or that something was done "instead of Force draining") at the specified
     * location.
     * @param location the location
     */
    @Override
    public void forceDrainAttempted(PhysicalCard location) {
        _forceDrainStartedSet.add(location);
    }

    /**
     * Determines if a Force drain has been initiated (or if something was done "instead of Force draining") at the specified
     * location.
     * @param location the location
     * @return true or false
     */
    @Override
    public boolean isForceDrainAttemptedThisTurn(PhysicalCard location) {
        return _forceDrainStartedSet.contains(location);
    }

    /**
     * Records that a Force drain of the specified amount of Force was setFulfilledByOtherAction at the specified location.
     * @param location the location
     * @param amount the amount of Force
     */
    @Override
    public void forceDrainPerformed(PhysicalCard location, Float amount) {
        _forceDrainCompletedMap.put(location, amount);
    }

    /**
     * Gets the number of Force drains initiated this turn.
     * @return the amount of Force
     */
    @Override
    public float getNumForceDrainsInitiatedThisTurn() {
        return _forceDrainStartedSet.size();
    }

    /**
     * Gets the total amount of Force that has been Force drained this turn.
     * @return the amount of Force
     */
    @Override
    public float getTotalForceDrainedThisTurn() {
        float total = 0;
        for (Float amount : _forceDrainCompletedMap.values())
            total += amount;
        return total;
    }

    /**
     * Records that a card was forfeited from the specified location.
     * @param location the location
     * @param forfeitedCard the forfeited card
     */
    @Override
    public void forfeitedFromLocation(PhysicalCard location, PhysicalCard forfeitedCard) {
        Set<PhysicalCard> forfeitedCards = _forfeitedFromLocationMap.get(location);
        if (forfeitedCards == null) {
            forfeitedCards = new HashSet<PhysicalCard>();
            _forfeitedFromLocationMap.put(location, forfeitedCards);
        }
        forfeitedCards.add(forfeitedCard);
    }

    /**
     * Gets the cards that were forfeited from a location this turn.
     * @return the forfeited cards
     */
    @Override
    public Map<PhysicalCard, Set<PhysicalCard>> getForfeitedFromLocationsThisTurn() {
        return Collections.unmodifiableMap(_forfeitedFromLocationMap);
    }

    /**
     * Records that the specified player used a combat card.
     * @param playerId the player
     */
    @Override
    public void combatCardUsed(String playerId) {
        _usedCombatCard.add(playerId);
    }

    /**
     * Determines if the player has used a combat card this turn.
     * @param playerId the player
     * @return true or false
     */
    @Override
    public boolean isCombatCardUsedThisTurn(String playerId) {
        return _usedCombatCard.contains(playerId);
    }

    /**
     * Records that the specified character card won a game of sabacc.
     * @param character the character that won sabacc game
     */
    @Override
    public void wonSabaccGame(PhysicalCard character) {
        try {
            PhysicalCard savedCopy = character.clone();
            _cardsThatWonSabacc.add(savedCopy);
        }
        catch (CloneNotSupportedException e) {
            _cardsThatWonSabacc.add(character);
        }
    }

    /**
     * Determines if a character accepted by the specified filter has won a sabacc game.
     * @param filters the filter
     * @return true or false
     */
    @Override
    public boolean hasWonSabaccGame(GameState gameState, Filterable filters) {
        for (PhysicalCard wonSabacc : _cardsThatWonSabacc) {
            if (Filters.and(filters).accepts(gameState, this, wonSabacc))
                return true;
        }
        return false;
    }

    /**
     * Records that the specified persona has 'crossed over'.
     *
     * @param persona the persona that was 'crossed over'
     */
    @Override
    public void crossedOver(Persona persona) {
        _personasCrossedOver.add(persona);
    }

    /**
     * Determines if the specified persona has 'crossed over'.
     *
     * @param gameState the game state
     * @param persona   the persona
     * @return true or false
     */
    @Override
    public boolean isCrossedOver(GameState gameState, Persona persona) {
        return _personasCrossedOver.contains(persona);
    }

    @Override
    public void blownAway(PhysicalCard card) {
        try {
            PhysicalCard savedCopy = card.clone();
            _blownAwayCards.add(savedCopy);
        }
        catch (CloneNotSupportedException e) {
            _blownAwayCards.add(card);
        }
    }

    @Override
    public boolean isBlownAway(GameState gameState, Filterable filters) {
        for (PhysicalCard blowAwayCard : _blownAwayCards) {
            if (Filters.and(filters).accepts(gameState, this, blowAwayCard))
                return true;
        }
        return false;
    }

    /**
     * Gets the amount of Force loss for the specified player due to the current blown away action.
     * @param gameState the game state
     * @param playerId the player
     * @return the amount of Force
     */
    @Override
    public float getBlownAwayForceLoss(GameState gameState, String playerId) {
        float total = 0;

        for (Modifier modifier : getModifiers(gameState, ModifierType.BLOWN_AWAY_FORCE_LOSS)) {
            if (modifier.isForTopBlowAwayEffect(gameState)) {
                if (modifier.isForPlayer(playerId)) {
                    total += modifier.getValue(gameState, this, (PhysicalCard) null);
                }
            }
        }

        for (Modifier modifier : getModifiers(gameState, ModifierType.BLOWN_AWAY_FORCE_MULTIPLIER)) {
            if (modifier.isForTopBlowAwayEffect(gameState)) {
                if (modifier.isForPlayer(playerId)) {
                    total *= modifier.getMultiplierValue(gameState, this, null);
                }
            }
        }

        return Math.max(0, total);
    }

    /**
     * Gets the Force loss amount for losing lightsaber combat.
     * @param gameState the game state
     * @return the lightsaber combat destiny value
     */
    @Override
    public float getLightsaberCombatForceLoss(GameState gameState, float baseForceLoss) {
        float total = baseForceLoss;

        LightsaberCombatState lightsaberCombatState = gameState.getLightsaberCombatState();
        if (lightsaberCombatState != null) {
            PhysicalCard winningCharacter = lightsaberCombatState.getWinningCharacter();
            if (winningCharacter != null) {
                for (Modifier modifier : getModifiers(gameState, ModifierType.LIGHTSABER_COMBAT_FORCE_LOSS)) {
                    total += modifier.getLightsaberCombatForceLossModifier(gameState, this, winningCharacter);
                }
            }
        }

        return Math.max(0, total);
    }

    /**
     * Determines if the specified card may use the specified combat card.
     * @param gameState the game state
     * @param character the character
     * @param combatCard the combat card
     * @return true or false
     */
    @Override
    public boolean mayUseCombatCard(GameState gameState, PhysicalCard character, PhysicalCard combatCard) {
        PhysicalCard stackedOn = combatCard.getStackedOn();
        if (stackedOn == null)  {
            return false;
        }

        if (Filters.sameCardId(stackedOn).accepts(gameState, this, character)) {
            return true;
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_USE_OTHER_CHARACTERS_COMBAT_CARDS, character)) {
            if (modifier.isAffectedTarget(gameState, this, stackedOn)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the Force retrieval amount for winning a Podrace.
     * @param gameState the game state
     * @param baseForceRetrieval the base Force retrieval amount
     * @return the amount of Force
     */
    @Override
    public float getPodraceForceRetrieval(GameState gameState, float baseForceRetrieval) {
        float total = baseForceRetrieval;

        if (gameState.isDuringPodrace()) {
            String winner = gameState.getPodraceWinner();
            if (winner != null) {
                for (Modifier modifier : getModifiers(gameState, ModifierType.PODRACE_FORCE_RETRIEVAL)) {
                    if (modifier.isForPlayer(winner)) {
                        total += modifier.getValue(gameState, this, (PhysicalCard) null);
                    }
                }
            }
        }

        return Math.max(0, total);
    }

    /**
     * Gets the Force loss amount for losing a Podrace.
     * @param gameState the game state
     * @param baseForceLoss the base Force loss amount
     * @return the amount of Force
     */
    @Override
    public float getPodraceForceLoss(GameState gameState, float baseForceLoss) {
        float total = baseForceLoss;

        if (gameState.isDuringPodrace()) {
            String loser = gameState.getPodraceLoser();
            if (loser != null) {
                for (Modifier modifier : getModifiers(gameState, ModifierType.PODRACE_FORCE_LOSS)) {
                    if (modifier.isForPlayer(loser)) {
                        total += modifier.getValue(gameState, this, (PhysicalCard) null);
                    }
                }
            }
        }

        return Math.max(0, total);
    }

    /**
     * Records that the specified Utinni Effect has been completed.
     * @param playerId the player that completed the Utinni Effect
     * @param utinniEffect the Utinni Effect that was completed
     */
    @Override
    public void completedUtinniEffect(String playerId, PhysicalCard utinniEffect) {
        PhysicalCard cardToSave = utinniEffect;
        try {
            cardToSave = utinniEffect.clone();
        }
        catch (CloneNotSupportedException e) {
        }
        List<PhysicalCard> utinniEffects = _completedUtinniEffect.get(playerId);
        if (utinniEffects == null) {
            utinniEffects = new ArrayList<PhysicalCard>();
            _completedUtinniEffect.put(playerId, utinniEffects);
        }
        utinniEffects.add(cardToSave);
    }

    /**
     * Determines if at least a specified number of Utinni Effects accepted by the filter have been completed by the specified player.
     * @param gameState the game state
     * @param playerId the player
     * @param count the count
     * @param filters the filter
     * @return true or false
     */
    @Override
    public boolean hasCompletedUtinniEffect(GameState gameState, String playerId, int count, Filterable filters) {
        List<PhysicalCard> utinniEffects = _completedUtinniEffect.get(playerId);
        if (utinniEffects == null) {
            return false;
        }
        return Filters.canSpot(utinniEffects, gameState.getGame(), count, filters);
    }

    /**
     * Records that the specified Jedi Test has been attempted by the specified character.
     * @param jediTest the Jedi Test that was attempted
     * @param attemptedBy the character that attempted the Jedi Test
     */
    @Override
    public void attemptedJediTest(PhysicalCard jediTest, PhysicalCard attemptedBy) {
        _attemptedJediTestThisTurnMap.put(jediTest.getCardId(), attemptedBy);
    }

    /**
     * Determines if any Jedi Tests have been attempted this turn.
     * @return true or false
     */
    @Override
    public boolean hasAttemptedJediTests() {
        return !_attemptedJediTestThisTurnMap.isEmpty();
    }

    /**
     * Records that the specified Jedi Test has been completed by the specified character.
     * @param jediTest the Jedi Test that was completed
     * @param completedBy the character that completed the Jedi Test
     */
    @Override
    public void completedJediTest(PhysicalCard jediTest, PhysicalCard completedBy) {
        _completedJediTest.put(jediTest.getCardId(), completedBy);
    }

    /**
     * Records that the specified starship has had asteroid destiny drawn against it at the specified location.
     *
     * @param starship the starship
     * @param location the location with "Asteroid Rules"
     */
    @Override
    public void asteroidDestinyDrawnAgainst(PhysicalCard starship, PhysicalCard location) {
        int starshipCardId = starship.getCardId();
        int locationCardId = location.getCardId();

        Set<Integer> starshipCardIds = _asteroidDestinyDrawnAgainstMap.get(locationCardId);
        if (starshipCardIds == null) {
            starshipCardIds = new HashSet<Integer>();
            _asteroidDestinyDrawnAgainstMap.put(locationCardId, starshipCardIds);
        }
        starshipCardIds.add(starshipCardId);
    }

    /**
     * Determines if the specified starship has had asteroid destiny drawn against it at the specified location this turn.
     *
     * @param starship the starship
     * @param location the location with "Asteroid Rules"
     * @return true if starship has already had asteroid destiny drawn against it at the location, otherwise false
     */
    @Override
    public boolean hadAsteroidDestinyDrawnAgainstThisTurn(PhysicalCard starship, PhysicalCard location) {
        int starshipCardId = starship.getCardId();
        int locationCardId = location.getCardId();

        Set<Integer> starshipCardIds = _asteroidDestinyDrawnAgainstMap.get(locationCardId);
        return (starshipCardIds != null && starshipCardIds.contains(starshipCardId));
    }

    @Override
    public void participatedInForceDrain(PhysicalCard card) {
        final int cardId = card.getCardId();
        final Integer value = _forceDrainParticipationMap.get(cardId);
        if (value == null)
            _forceDrainParticipationMap.put(cardId, 1);
        else
            _forceDrainParticipationMap.put(cardId, value + 1);
    }

    @Override
    public boolean hasParticipatedInForceDrainThisTurn(PhysicalCard card) {
        return (_forceDrainParticipationMap.get(card.getCardId())!=null);
    }

    @Override
    public boolean mayForceDrain(GameState gameState, PhysicalCard card) {
        return !getModifiersAffectingCard(gameState, ModifierType.MAY_FORCE_DRAIN, card).isEmpty();
    }

    /**
     * Determines if the specified player is prohibited from Force draining at the specified location.
     * @param gameState the game state
     * @param location the location
     * @param playerId the player
     * @return true if player is not allowed to Force drain at location, otherwise false
     */
    @Override
    public boolean isProhibitedFromForceDrainingAtLocation(GameState gameState, PhysicalCard location, String playerId) {
        // Neither player may Force drain at a Death Star II sector
        if (Filters.Death_Star_II_sector.accepts(gameState, this, location)) {
            return true;
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_FORCE_DRAIN_AT_LOCATION, location)) {
            if (modifier.isForPlayer(playerId))
                return true;
        }
        return false;
    }

    @Override
    public boolean cantCancelForceDrainAtLocation(GameState gameState, PhysicalCard location, PhysicalCard cardCanceling, String playerCanceling, String playerDraining) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_CANCEL_FORCE_DRAIN_AT_LOCATION, location)) {
            if (modifier.cantCancelForceDrain(gameState, this, playerCanceling, playerDraining))
                return true;
        }

        // Check if source card may not cancel Force drains
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_CANCEL_FORCE_DRAINS_BY_USING_CARD, cardCanceling)) {
            if (modifier.isForPlayer(playerDraining)) {
                if (modifier.isAffectedTarget(gameState, this, location)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Determines if a Force generation is immune to limit.
     * @param gameState the game state
     * @param playerId the player whose Force generation is being checked
     * @param location the location
     * @param source the source of the limit
     * @return true if Force generation at location for player is immune to limit, otherwise false
     */
    @Override
    public boolean isImmuneToForceGenerationLimit(GameState gameState, String playerId, PhysicalCard location, PhysicalCard source) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FORCE_GENERATION_AT_LOCATION_IMMUNE_TO, location)) {
            if (modifier.isForPlayer(playerId)) {
                if (modifier.isAffectedTarget(gameState, this, source)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if a Force drain modifier is canceled.
     * @param gameState the game state
     * @param location the Force drain location
     * @param source the source of the modifier
     * @param playerModifying the owner of the source card
     * @param playerDraining the player Force draining
     * @param amount the amount of the modifier
     * @return true if modifier is canceled, otherwise false
     */
    @Override
    public boolean isForceDrainModifierCanceled(GameState gameState, PhysicalCard location, PhysicalCard source, String playerModifying, String playerDraining, float amount) {
        // Check if Force drain modifier may not be canceled
        if (!getModifiersAffectingCard(gameState, ModifierType.FORCE_DRAIN_MODIFIERS_MAY_NOT_BE_CANCELED, source).isEmpty()) {
            return false;
        }

        // Check if Force drains at location may not be modified
        if (cantModifyForceDrainAtLocation(gameState, location, source, playerModifying, playerDraining)) {
            return true;
        }

        // Check if opponent's Force drain modifiers are canceled
        if (playerModifying.equals(playerDraining)) {
            for (Modifier modifier : getModifiers(gameState, ModifierType.CANCEL_OPPONENTS_FORCE_DRAIN_MODIFIERS)) {
                if (modifier.isForPlayer(gameState.getOpponent(playerDraining))) {
                    if (modifier.isAffectedTarget(gameState, this, location)) {
                        return true;
                    }
                }
            }
        }

        if (amount > 0) {
            // Check if Force drain bonuses from specific cards are canceled
            if (!getModifiersAffectingCard(gameState, ModifierType.CANCEL_FORCE_DRAIN_BONUSES_FROM_CARD, source).isEmpty()) {
                return true;
            }

            // Check if opponent's Force drain bonuses are canceled
            if (playerModifying.equals(playerDraining)) {
                for (Modifier modifier : getModifiers(gameState, ModifierType.CANCEL_OPPONENTS_FORCE_DRAIN_BONUSES)) {
                    if (modifier.isForPlayer(gameState.getOpponent(playerDraining))) {
                        if (modifier.isAffectedTarget(gameState, this, location)) {
                            return true;
                        }
                    }
                }
            }
        }
        else if (amount < 0) {
            // Check if Force drains at location may not be reduced
            if (cantReduceForceDrainAtLocation(gameState, location, source, playerModifying, playerDraining)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if the specified player is prohibited from initiating attacks at the specified location.
     *
     * @param gameState the game state
     * @param location  the location
     * @param playerId  the player
     * @return true if player is not allowed to initiate attacks at location, otherwise false
     */
    @Override
    public boolean mayNotInitiateAttacksAtLocation(GameState gameState, PhysicalCard location, String playerId) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_INITIATE_ATTACKS_AT_LOCATION, location)) {
            if (modifier.isForPlayer(playerId)) {
                return false;
            }
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_INITIATE_ATTACKS_AT_LOCATION, location)) {
            if (modifier.isForPlayer(playerId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the specified player is prohibited from initiating battle at the specified location.
     *
     * @param gameState the game state
     * @param location  the location
     * @param playerId  the player
     * @return true if player is not allowed to initiate battle at location, otherwise false
     */
    @Override
    public boolean mayNotInitiateBattleAtLocation(GameState gameState, PhysicalCard location, String playerId) {
        // Neither player may Force drain at a Death Star II sector
        if (Filters.Death_Star_II_sector.accepts(gameState, this, location)) {
            return true;
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_INITIATE_BATTLE_AT_LOCATION, location)) {
            if (modifier.isForPlayer(playerId)) {
                return false;
            }
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_INITIATE_BATTLE_AT_LOCATION, location)) {
            if (modifier.isForPlayer(playerId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the cost for the specified player to initiate a battle at the specified location.
     *
     * @param gameState the game state
     * @param location  the location
     * @param playerId  the player
     * @return the cost
     */
    @Override
    public float getInitiateBattleCost(GameState gameState, PhysicalCard location, String playerId) {

        // Check if initiates battle for free
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.INITIATE_BATTLE_FOR_FREE, location)) {
            if (modifier.isForPlayer(playerId)) {
                return 0;
            }
        }

        float result = 1;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.INITIATE_BATTLE_COST, location)) {
            if (modifier.isForPlayer(playerId)) {
                result += modifier.getValue(gameState, this, location);
            }
        }

        // Check if value was reset to an "unmodifiable value", and use lowest found
        Float lowestResetValue = null;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_INITIATE_BATTLE_COST, location)) {
            if (modifier.isForPlayer(playerId)) {
                float modifierAmount = modifier.getValue(gameState, this, location);
                lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
            }
        }
        if (lowestResetValue != null) {
            result = lowestResetValue;
        }

        result = Math.max(0, result);
        return result;
    }

    @Override
    public float getInitiateBattleCostAsLoseForce(GameState gameState, PhysicalCard location, String playerId) {
        float result = 0;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.INITIATE_BATTLE_COST_AS_LOSE_FORCE, location)) {
            if (modifier.isForPlayer(playerId)) {
                result += modifier.getValue(gameState, this, location);
            }
        }
        return Math.max(0, result);
    }

    /**
     * Determines the player to take the first weapons segment action in the current battle.
     * @param gameState the game state
     * @return the player to take the first weapons segment action
     */
    @Override
    public String getPlayerToTakeFirstBattleWeaponsSegmentAction(GameState gameState) {
        if (!gameState.isDuringBattle())
            return null;

        String playerInitiatedBattle = gameState.getBattleState().getPlayerInitiatedBattle();
        String opponent = gameState.getOpponent(playerInitiatedBattle);

        if (hasFlagActive(gameState, ModifierFlag.TAKES_FIRST_BATTLE_WEAPONS_SEGMENT_ACTION, opponent)) {
            return opponent;
        }

        return playerInitiatedBattle;
    }

    /**
     * Determines if the card is placed out of play when eaten by the specified card.
     * @param gameState the game state
     * @param cardEaten the card eaten
     * @param cardEatenBy the card that ate the card
     * @return true or false
     */
    @Override
    public boolean isEatenByPlacedOutOfPlay(GameState gameState, PhysicalCard cardEaten, PhysicalCard cardEatenBy) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EATEN_BY_IS_PLACED_OUT_OF_PLAY, cardEatenBy)) {
            if (modifier.isAffectedTarget(gameState, this, cardEaten)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Records that an attack on a creature was initiated at the specified location.
     *
     * @param location the location
     */
    @Override
    public void attackOnCreatureInitiatedAtLocation(PhysicalCard location) {
        _locationAttackOnCreatureSet.add(location.getCardId());
    }

    /**
     * Determines if an attack on a creature has been initiated at the specified location this turn.
     *
     * @param location the location
     * @return true or false
     */
    @Override
    public boolean isAttackOnCreatureOccurredAtLocationThisTurn(PhysicalCard location) {
        return _locationAttackOnCreatureSet.contains(location.getCardId());
    }

    /**
     * Records that the specified card has participated in an attack on a creature.
     *
     * @param card the card
     */
    @Override
    public void participatedInAttackOnCreature(PhysicalCard card) {
        _attackOnCreatureParticipationSet.add(card.getCardId());
    }

    /**
     * Determines if the specified card has participated in an attack on a creature this turn.
     *
     * @param card the card
     * @return true if card has participated, otherwise false
     */
    @Override
    public boolean hasParticipatedInAttackOnCreatureThisTurn(PhysicalCard card) {
        return _attackOnCreatureParticipationSet.contains(card.getCardId());
    }

    /**
     * Records that the specified card has participated in an attack on a non-creature.
     *
     * @param card the card
     */
    @Override
    public void participatedInAttackOnNonCreature(PhysicalCard card) {
        _attackOnNonCreatureParticipationSet.add(card.getCardId());
    }

    /**
     * Determines if the specified card has participated in an attack on a non-creature this turn.
     *
     * @param card the card
     * @return true if card has participated, otherwise false
     */
    @Override
    public boolean hasParticipatedInAttackOnNonCreatureThisTurn(PhysicalCard card) {
        return _attackOnNonCreatureParticipationSet.contains(card.getCardId());
    }

    /**
     * Determines if the specified card is prohibited from attacking the specified target.
     * @param gameState the game state
     * @param card the card
     * @param target the target
     * @return true is prohibited from attacking the specified target, otherwise false
     */
    @Override
    public boolean isProhibitedFromAttackingTarget(GameState gameState, PhysicalCard card, PhysicalCard target) {
        if (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_ATTACK, card).isEmpty()) {
            return true;
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_ATTACK_TARGET, card)) {
            if (modifier.isAffectedTarget(gameState, this, target)) {
                return true;
            }
        }
        if (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_ATTACKED, target).isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Determines if the specified card is granted to attack the specified target.
     * @param gameState the game state
     * @param card the card
     * @param target the target
     * @return true is granted to attack the specified target, otherwise false
     */
    @Override
    public boolean grantedToAttackTarget(GameState gameState, PhysicalCard card, PhysicalCard target) {
        if (Filters.parasite.accepts(gameState, this, card)) {
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PARASITE_TARGET, card)) {
                if (modifier.isAffectedTarget(gameState, this, target)) {
                    return true;
                }
            }
            return false;
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_ATTACK_TARGET, card)) {
            if (modifier.isAffectedTarget(gameState, this, target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Records that a battle was initiated at the specified location by the specified player.
     *
     * @param playerId the player
     * @param location the location
     */
    @Override
    public void battleInitiatedAtLocation(String playerId, PhysicalCard location) {
        Integer numBattles = _battleInitiatedByPlayerMap.get(playerId);
        if (numBattles != null)
            _battleInitiatedByPlayerMap.put(playerId, numBattles + 1);
        else
            _battleInitiatedByPlayerMap.put(playerId, 1);

        final int cardId = location.getCardId();
        final Integer value = _locationBattleMap.get(cardId);
        if (value == null)
            _locationBattleMap.put(cardId, 1);
        else
            _locationBattleMap.put(cardId, value + 1);
    }

    /**
     * Gets the number of battles that have been initiated this turn by the specified player.
     * @param playerId the player
     * @return the number of battles
     */
    @Override
    public int getNumBattlesInitiatedThisTurn(String playerId) {
        Integer numBattles = _battleInitiatedByPlayerMap.get(playerId);
        if (numBattles != null) {
            return numBattles;
        }
        return 0;
    }

    @Override
    public boolean isBattleOccurredAtLocationThisTurn(PhysicalCard location) {
        return (_locationBattleMap.get(location.getCardId())!=null);
    }

    /**
     * Records that the specified card has participated in a battle.
     *
     * @param card the card
     */
    @Override
    public void participatedInBattle(PhysicalCard card, PhysicalCard location) {
        final int cardId = card.getCardId();
        if (_battleParticipationMap.get(cardId)==null)
            _battleParticipationMap.put(cardId, location.getCardId());
    }

    /**
     * Determines if the specified card has participated in a battle this turn.
     *
     * @param card the card
     * @return true if card has participated, otherwise false
     */
    @Override
    public boolean hasParticipatedInBattle(PhysicalCard card) {
        return (_battleParticipationMap.get(card.getCardId())!=null);
    }

    /**
     * Determines if the specified card has participated in a battle at another location this turn.
     *
     * @param card the card
     * @param location the location
     * @return true if card has participated, otherwise false
     */
    @Override
    public boolean hasParticipatedInBattleAtOtherLocation(PhysicalCard card, PhysicalCard location) {
        final int cardId = card.getCardId();
        final Integer locationCardId = _battleParticipationMap.get(cardId);
        return (locationCardId != null && locationCardId != location.getCardId());
    }

    /**
     * Determines if the specified card is prohibited from participating in battle.
     * @param gameState the game state
     * @param card the card
     * @param playerInitiatingBattle the player initiating battle
     * @return true is prohibited from participating in battle, otherwise false
     */
    @Override
    public boolean isProhibitedFromParticipatingInBattle(GameState gameState, PhysicalCard card, String playerInitiatingBattle) {
        if (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_PARTICIPATE_IN_BATTLE, card).isEmpty()) {
            return true;
        }
        if (card.getOwner().equals(playerInitiatingBattle)) {
            if (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_PARTICIPATE_IN_BATTLE_INITIATED_BY_OWNER, card).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mayInitiateBattle(GameState gameState, PhysicalCard card) {
        if (card.getBlueprint().getCardCategory() != CardCategory.CHARACTER
                && card.getBlueprint().getCardCategory() != CardCategory.STARSHIP
                && card.getBlueprint().getCardCategory() != CardCategory.VEHICLE)
            return false;

        return !getModifiersAffectingCard(gameState, ModifierType.MAY_INITIATE_BATTLE, card).isEmpty();
    }

    @Override
    public boolean mayBeBattled(GameState gameState, PhysicalCard card) {
        if (card.getBlueprint().getCardCategory() != CardCategory.CHARACTER
                && card.getBlueprint().getCardCategory() != CardCategory.STARSHIP
                && card.getBlueprint().getCardCategory() != CardCategory.VEHICLE)
            return false;

        return !getModifiersAffectingCard(gameState, ModifierType.MAY_BE_BATTLED, card).isEmpty();
    }

    @Override
    public boolean mayNotBeBattled(GameState gameState, PhysicalCard card) {
        if (card.getBlueprint().getCardCategory() != CardCategory.CHARACTER
                && card.getBlueprint().getCardCategory() != CardCategory.STARSHIP
                && card.getBlueprint().getCardCategory() != CardCategory.VEHICLE)
            return false;

        return !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_BATTLED, card).isEmpty();
    }

    /**
     * Determines if the specified card may not be excluded from battle.
     * @param gameState the game state
     * @param card the card
     * @return true if card may not be excluded from battle, otherwise false
     */
    @Override
    public boolean mayNotBeExcludedFromBattle(GameState gameState, PhysicalCard card) {
        return !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_EXCLUDED_FROM_BATTLE, card).isEmpty();
    }

    /**
     * Determines if the specified card is currently excluded from battle.
     * @param gameState the game state
     * @param card the card
     * @return true if excluded from battle, otherwise false
     */
    @Override
    public boolean isExcludedFromBattle(GameState gameState, PhysicalCard card) {
        CardCategory cardCategory = card.getBlueprint().getCardCategory();
        if (cardCategory == CardCategory.CHARACTER || cardCategory==CardCategory.DEVICE || cardCategory == CardCategory.VEHICLE
                || cardCategory == CardCategory.STARSHIP || cardCategory == CardCategory.WEAPON) {
            return (!getModifiersAffectingCard(gameState, ModifierType.EXCLUDED_FROM_BATTLE, card).isEmpty());
        }
        return false;
    }

    /**
     * Gets the card causes the specified card to be excluded from battle, or null if the card is either not excluded from
     * battle or is excluded from battle by rule.
     * @param gameState the game state
     * @param card the card
     * @return the card causing the exclusion from battle, or null
     */
    @Override
    public PhysicalCard getCardCausingExclusionFromBattle(GameState gameState, PhysicalCard card) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_PARTICIPATE_IN_BATTLE, card)) {
            if (modifier.getSource(gameState) != null) {
                return modifier.getSource(gameState);
            }
        }
        if (gameState.getBattleState() != null && card.getOwner().equals(gameState.getBattleState().getPlayerInitiatedBattle())) {
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_PARTICIPATE_IN_BATTLE_INITIATED_BY_OWNER, card)) {
                if (modifier.getSource(gameState) != null) {
                    return modifier.getSource(gameState);
                }
            }
        }
        return null;
    }

    @Override
    public boolean isImmuneToCardTitle(GameState gameState, PhysicalCard card, String cardTitle) {
        if (card.getBlueprint().isImmuneToCardTitle(cardTitle))
            return true;

        if (card.getBlueprint().isImmuneToOwnersCardTitle(cardTitle))
            return true;

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IMMUNE_TO_TITLE, card)) {
            if (modifier.isImmuneToCardTitleModifier(gameState, this, cardTitle)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the modifiers from the source card are suspended from affecting the specified card.
     * @param gameState the game state
     * @param source the source card
     * @param affectedCard the affected card
     * @return true if effects from modifier are suspended, otherwise false
     */
    @Override
    public boolean isEffectsFromModifierToCardSuspended(GameState gameState, PhysicalCard source, PhysicalCard affectedCard) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.SUSPEND_EFFECTS_FROM_CARD, affectedCard)) {
            if (modifier.isAffectedTarget(gameState, this, source)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isPermanentPilotsNotAbleToApplyAbilityForBattleDestiny(GameState gameState, PhysicalCard card) {
        if (card.getBlueprint().getCardCategory() != CardCategory.STARSHIP &&
                card.getBlueprint().getCardCategory() != CardCategory.VEHICLE)
            return false;

        if (!getModifiersAffectingCard(gameState, ModifierType.PERMANENT_PILOTS_MAY_NOT_APPLY_ABILITY_FOR_BATTLE_DESTINY, card).isEmpty()) {
            return true;
        }

        // Check if attached to "crashed vehicle"
        if (card.isCrashed()) {
            return true;
        }

        return false;
    }

    @Override
    public boolean cannotApplyAbilityForBattleDestiny(GameState gameState, PhysicalCard card) {
        if (card.getBlueprint().getCardCategory() != CardCategory.CHARACTER)
            return false;

        if (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_APPLY_ABILITY_FOR_BATTLE_DESTINY, card).isEmpty()) {
            return true;
        }

        // Check if attached to "crashed vehicle"
        if (card.getBlueprint().getCardCategory()==CardCategory.CHARACTER
                && card.getAttachedTo() != null
                && card.getAttachedTo().isCrashed()) {
            return true;
        }

        return false;
    }

    @Override
    public boolean passengerAppliesAbilityForBattleDestiny(GameState gameState, PhysicalCard card) {
        return !getModifiersAffectingCard(gameState, ModifierType.PASSENGER_APPLIES_ABILITY_FOR_BATTLE_DESTINY, card).isEmpty();
    }

    @Override
    public boolean mayNotApplyAbilityForSenseOrAlterDestiny(GameState gameState, PhysicalCard card) {
        return !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_APPLY_ABILITY_FOR_SENSE_ALTER_DESTINY, card).isEmpty();
    }

    /**
     * Determines if the specified player may not add destiny draws to power.
     * @param gameState the game state
     * @param playerId the player
     * @return true if destinies draws to power may not be added, otherwise false
     */
    @Override
    public boolean mayNotAddDestinyDrawsToPower(GameState gameState, String playerId) {
        for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_NOT_ADD_DESTINY_DRAWS_TO_POWER)) {
            if (modifier.isForPlayer(playerId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the specified player may not add destiny draws to attrition.
     * @param gameState the game state
     * @param playerId the player
     * @return true if destinies draws to attrition may not be added, otherwise false
     */
    @Override
    public boolean mayNotAddDestinyDrawsToAttrition(GameState gameState, String playerId) {
        for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_NOT_ADD_DESTINY_DRAWS_TO_ATTRITION)) {
            if (modifier.isForPlayer(playerId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the specified card may not add battle destiny draws.
     * @param gameState the game state
     * @param card the card
     * @return true if battle destinies draws may not be added, otherwise false
     */
    @Override
    public boolean mayNotAddBattleDestinyDraws(GameState gameState, PhysicalCard card) {
        return !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_ADD_BATTLE_DESTINY_DRAWS, card).isEmpty();
    }

    /**
     * Determines if battle destiny draws by a specified player may not be canceled by the other specified player.
     * @param gameState the game state
     * @param playerDrawingDestiny the player drawing battle destiny
     * @param playerToCancel the player to cancel battle destiny
     * @param isCancelAndRedraw true if cancel and redraw, otherwise cancel only
     * @return true if battle destinies may not be canceled, otherwise false
     */
    @Override
    public boolean mayNotCancelBattleDestinyDraws(GameState gameState, String playerDrawingDestiny, String playerToCancel, boolean isCancelAndRedraw) {
        PhysicalCard battleLocation = gameState.getBattleLocation();
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_CANCEL_BATTLE_DESTINY, battleLocation)) {
            if (modifier.mayNotCancelBattleDestiny(playerDrawingDestiny, playerToCancel)) {
                return true;
            }
        }
        if (!isCancelAndRedraw) {
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_CANCEL_BATTLE_DESTINY_UNLESS_BEING_REDRAWN, battleLocation)) {
                if (modifier.mayNotCancelBattleDestiny(playerDrawingDestiny, playerToCancel)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the current destiny draw may not be canceled by the specified player.
     * @param gameState the game state
     * @param playerId the player
     * @param isCancelAndRedraw true if cancel and redraw, otherwise cancel only
     * @return true if destiny may not be canceled, otherwise false
     */
    @Override
    public boolean mayNotCancelDestinyDraw(GameState gameState, String playerId, boolean isCancelAndRedraw) {
        DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
        if (drawDestinyState == null)
            return true;

        DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();

        if (drawDestinyEffect.isDestinyCanceled()
                || drawDestinyEffect.getSubstituteDestiny() != null
                || drawDestinyEffect.mayNotBeCanceledByPlayer(playerId))
            return true;

        if (drawDestinyEffect.getDestinyType() == DestinyType.BATTLE_DESTINY) {
            if (mayNotCancelBattleDestinyDraws(gameState, drawDestinyEffect.getPlayerDrawingDestiny(), playerId, isCancelAndRedraw)) {
                return true;
            }
        }
        else if (drawDestinyEffect.getDestinyType() == DestinyType.WEAPON_DESTINY) {
            // Get from WeaponFiringState
            WeaponFiringState weaponFiringState = gameState.getWeaponFiringState();
            if (weaponFiringState != null) {
                PhysicalCard weapon = weaponFiringState.getCardFiring();
                SwccgBuiltInCardBlueprint permanentWeapon = weaponFiringState.getPermanentWeaponFiring();
                PhysicalCard cardFiringWeapon = weaponFiringState.getCardFiringWeapon();

                for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_NOT_CANCEL_WEAPON_DESTINY)) {
                    if (modifier.mayNotCancelWeaponDestiny(gameState, this, drawDestinyEffect.getPlayerDrawingDestiny(), playerId, weapon, permanentWeapon, cardFiringWeapon)) {
                        return true;
                    }
                }
            }
        }


        return false;
    }

    /**
     * Determines if total battle destiny for a specified player may not be modified by the other specified player.
     * @param gameState the game state
     * @param playerDrawingDestiny the player with total battle destiny
     * @param playerToModify the player to modify total battle destiny
     * @return true if total battle destiny may not be modified, otherwise false
     */
    @Override
    public boolean mayNotModifyTotalBattleDestiny(GameState gameState, String playerDrawingDestiny, String playerToModify) {
        PhysicalCard battleLocation = gameState.getBattleLocation();
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MODIFY_TOTAL_BATTLE_DESTINY, battleLocation)) {
            if (modifier.mayNotModifyBattleDestiny(playerDrawingDestiny, playerToModify)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if battle destiny draws by a specified player may not be modified by the other specified player.
     * @param gameState the game state
     * @param playerDrawingDestiny the player drawing battle destiny
     * @param playerToModify the player to modify battle destiny
     * @return true if battle destinies may not be modified, otherwise false
     */
    @Override
    public boolean mayNotModifyBattleDestinyDraws(GameState gameState, String playerDrawingDestiny, String playerToModify) {
        PhysicalCard battleLocation = gameState.getBattleLocation();
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MODIFY_BATTLE_DESTINY, battleLocation)) {
            if (modifier.mayNotModifyBattleDestiny(playerDrawingDestiny, playerToModify)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the current destiny draw may not be modified by the specified player.
     * @param gameState the game state
     * @param playerId the player
     * @return true if destiny may not be modified, otherwise false
     */
    @Override
    public boolean mayNotModifyDestinyDraw(GameState gameState, String playerId) {
        DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
        if (drawDestinyState == null)
            return true;

        DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();

        if (drawDestinyEffect.isDestinyCanceled()
                || drawDestinyEffect.getSubstituteDestiny() != null)
            return true;

        if (drawDestinyEffect.getDestinyType() == DestinyType.BATTLE_DESTINY) {
            if (mayNotModifyBattleDestinyDraws(gameState, drawDestinyEffect.getPlayerDrawingDestiny(), playerId)) {
                return true;
            }
        }
        else if (drawDestinyEffect.getDestinyType() == DestinyType.WEAPON_DESTINY) {
            // Get from WeaponFiringState
            WeaponFiringState weaponFiringState = gameState.getWeaponFiringState();
            if (weaponFiringState != null) {
                PhysicalCard weapon = weaponFiringState.getCardFiring();
                SwccgBuiltInCardBlueprint permanentWeapon = weaponFiringState.getPermanentWeaponFiring();
                PhysicalCard cardFiringWeapon = weaponFiringState.getCardFiringWeapon();

                for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_NOT_MODIFY_WEAPON_DESTINY)) {
                    if (modifier.mayNotModifyWeaponDestiny(gameState, this, drawDestinyEffect.getPlayerDrawingDestiny(), playerId, weapon, permanentWeapon, cardFiringWeapon)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if total battle destiny for a specified player may not be reset by the other specified player.
     * @param gameState the game state
     * @param playerDrawingDestiny the player with total battle destiny
     * @param playerToReset the player to reset total battle destiny
     * @return true if total battle destiny may not be reset, otherwise false
     */
    @Override
    public boolean mayNotResetTotalBattleDestiny(GameState gameState, String playerDrawingDestiny, String playerToReset) {
        PhysicalCard battleLocation = gameState.getBattleLocation();
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_RESET_TOTAL_BATTLE_DESTINY, battleLocation)) {
            if (modifier.mayNotResetBattleDestiny(playerDrawingDestiny, playerToReset)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if battle destiny draws by a specified player may not be reset by the other specified player.
     * @param gameState the game state
     * @param playerDrawingDestiny the player drawing battle destiny
     * @param playerToReset the player to reset battle destiny
     * @return true if battle destinies may not be reset, otherwise false
     */
    @Override
    public boolean mayNotResetBattleDestinyDraws(GameState gameState, String playerDrawingDestiny, String playerToReset) {
        PhysicalCard battleLocation = gameState.getBattleLocation();
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_RESET_BATTLE_DESTINY, battleLocation)) {
            if (modifier.mayNotResetBattleDestiny(playerDrawingDestiny, playerToReset)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the current destiny draw may not be reset by the specified player.
     * @param gameState the game state
     * @param playerId the player
     * @return true if destiny may not be reset, otherwise false
     */
    @Override
    public boolean mayNotResetDestinyDraw(GameState gameState, String playerId) {
        DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
        if (drawDestinyState == null)
            return true;

        DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();

        if (drawDestinyEffect.isDestinyCanceled()
                || drawDestinyEffect.getSubstituteDestiny() != null)
            return true;

        if (drawDestinyEffect.getDestinyType() == DestinyType.BATTLE_DESTINY) {
            if (mayNotResetBattleDestinyDraws(gameState, drawDestinyEffect.getPlayerDrawingDestiny(), playerId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if battle destiny draws by a specified player may not be substituted.
     * @param gameState the game state
     * @param playerId the player drawing battle destiny
     * @return true if battle destinies may not be substituted, otherwise false
     */
    @Override
    public boolean mayNotSubstituteBattleDestinyDraws(GameState gameState, String playerId) {
        PhysicalCard battleLocation = gameState.getBattleLocation();
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_SUBSTITUTE_BATTLE_DESTINY, battleLocation)) {
            if (modifier.isForPlayer(playerId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the current destiny draw may not be substituted.
     * @param gameState the game state
     * @return true if destiny may not be substituted, otherwise false
     */
    @Override
    public boolean mayNotSubstituteDestinyDraw(GameState gameState) {
        DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
        if (drawDestinyState == null)
            return true;

        DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();

        if (drawDestinyEffect.isDestinyCanceled()
                || drawDestinyEffect.getSubstituteDestiny() != null)
            return true;

        if (drawDestinyEffect.getDestinyType() == DestinyType.BATTLE_DESTINY) {
            if (mayNotSubstituteBattleDestinyDraws(gameState, drawDestinyEffect.getPlayerDrawingDestiny())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if the number of battle destinies the specified player may draw if unable to otherwise.
     * @param gameState the game state
     * @param player the player
     * @return the number of battle destinies
     */
    @Override
    public int getNumBattleDestinyDrawsIfUnableToOtherwise(GameState gameState, String player) {
        BattleState battleState = gameState.getBattleState();
        if (battleState == null)
            return 0;

        int result = 0;
        for (PhysicalCard battleParticipant : battleState.getCardsParticipating(player)) {
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MIN_BATTLE_DESTINY_DRAWS, battleParticipant)) {
                result = Math.max(result, modifier.getMinimumBattleDestinyDrawsModifier(gameState, this));
            }
        }

        return result;
    }

    /**
     * Gets the number of destinies that the specified player can draw for battle destiny.
     * @param gameState the game state
     * @param player the player
     * @param isGetLimit if true, gets the limit to the number of draws, otherwise gets then the number that can be attempted
     *                   when ignoring limit.
     * @param isForGui if true, gets the number of destiny draws to show on user interface that can be attempted
     * @return the number of battle destinies
     */
    @Override
    public int getNumBattleDestinyDraws(GameState gameState, String player, boolean isGetLimit, boolean isForGui) {
        BattleState battleState = gameState.getBattleState();
        if (battleState == null)
            return 0;

        int result = 0;
        float abilityRequired = 4;
        boolean abilityRequiredWasChanged = false;
        boolean moreThanAbilityRequired = false;

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.ABILITY_REQUIRED_FOR_BATTLE_DESTINY_MODIFIER, battleState.getBattleLocation())) {
            if (modifier.isForPlayer(player)) {
                float value = modifier.getAbilityRequiredToDrawBattleDestinyModifier(player, gameState, this);
                abilityRequiredWasChanged = true;
                abilityRequired += value;
            }
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_ABILITY_REQUIRED_FOR_BATTLE_DESTINY, battleState.getBattleLocation())) {
            if (modifier.isForPlayer(player)) {
                float value = modifier.getUnmodifiableAbilityRequiredToDrawBattleDestiny(player, gameState, this);
                abilityRequiredWasChanged = true;
                abilityRequired = Math.max(abilityRequired, value);
            }
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_ABILITY_MORE_THAN_REQUIRED_FOR_BATTLE_DESTINY, battleState.getBattleLocation())) {
            if (modifier.isForPlayer(player)) {
                float value = modifier.getUnmodifiableAbilityRequiredToDrawBattleDestiny(player, gameState, this);
                if (!abilityRequiredWasChanged || value >= abilityRequired) {
                    abilityRequiredWasChanged = true;
                    moreThanAbilityRequired = true;
                    abilityRequired = Math.max(abilityRequired, value);
                }
            }
        }

        float abilityForBattle = getTotalAbilityAtLocation(gameState, player, battleState.getBattleLocation(), false, false, true, battleState.getPlayerInitiatedBattle(), true, false, null);
        if ((!moreThanAbilityRequired && (abilityForBattle >= abilityRequired)) || (abilityForBattle > abilityRequired))
            result = 1;

        // Skip destiny draw adders if ability required was changed and requirement is not met
        if (!abilityRequiredWasChanged
                || (!moreThanAbilityRequired && (abilityForBattle >= abilityRequired))
                || (abilityForBattle > abilityRequired)) {
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.NUM_BATTLE_DESTINY_DRAWS, battleState.getBattleLocation())) {
                if (modifier.isForPlayer(player)) {
                    result += modifier.getValue(gameState, this, battleState.getBattleLocation());
                }
            }
        }

        result = Math.max(0, result);

        Collection<PhysicalCard> battleParticipants = battleState.getCardsParticipating(player);

        // If getting limit, then limit is MAX_BATTLE_DESTINY_DRAWS (or MIN_BATTLE_DESTINY_DRAWS if MIN_BATTLE_DESTINY_DRAWS is larger than the number
        // of destinies determined at this point and larger than MAX_BATTLE_DESTINY_DRAWS).
        if (isGetLimit) {
            Integer curMinLimit = null;
            Integer curMaxLimit = null;

            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAX_BATTLE_DESTINY_DRAWS, battleState.getBattleLocation())) {
                if (modifier.isForPlayer(player)) {
                    int limit = modifier.getMaximumBattleDestinyDrawsModifier(player, gameState, this);
                    if (curMaxLimit == null || limit < curMaxLimit) {
                        curMaxLimit = limit;
                    }
                }
            }

            for (PhysicalCard battleParticipant : battleParticipants) {
                for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MIN_BATTLE_DESTINY_DRAWS, battleParticipant)) {
                    int limit = modifier.getMinimumBattleDestinyDrawsModifier(gameState, this);
                    if (curMinLimit == null || limit > curMinLimit) {
                        curMinLimit = limit;
                    }
                }
            }

            if (curMinLimit != null && curMinLimit > result)
                return curMinLimit;

            if (curMinLimit != null && curMaxLimit != null)
                return Math.max(curMaxLimit, curMinLimit);

            if (curMinLimit == null && curMaxLimit != null)
                return curMaxLimit;

            return Integer.MAX_VALUE;
        }
        else {
            // Do not check MAX_BATTLE_DESTINY_DRAWS if not checking drawing limit or not for showing on user interface

            if (isForGui) {
                for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAX_BATTLE_DESTINY_DRAWS, battleState.getBattleLocation())) {
                    if (modifier.isForPlayer(player)) {
                        result = Math.min(result, modifier.getMaximumBattleDestinyDrawsModifier(player, gameState, this));
                    }
                }
            }

            for (PhysicalCard battleParticipant : battleParticipants) {
                for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MIN_BATTLE_DESTINY_DRAWS, battleParticipant)) {
                    result = Math.max(result, modifier.getMinimumBattleDestinyDrawsModifier(gameState, this));
                }
            }
        }

        return Math.max(0, result);
    }

    @Override
    public int getNumDestinyDrawsToTotalPowerOnly(GameState gameState, String player, boolean isGetLimit, boolean isForGui) {
        BattleState battleState = gameState.getBattleState();
        if (battleState ==null)
            return 0;

        int result = 0;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.NUM_DESTINY_DRAWS_TO_POWER_ONLY, battleState.getBattleLocation()))
            result += modifier.getNumDestinyDrawsToPowerOnlyModifier(player, gameState, this);

        if (isGetLimit) {
            // TODO: See getNumBattleDestinyDraws() for what to do here.
            return Integer.MAX_VALUE;
        }

        return Math.max(0, result);
    }

    @Override
    public int getNumDestinyDrawsToAttritionOnly(GameState gameState, String player, boolean isGetLimit, boolean isForGui) {
        BattleState battleState = gameState.getBattleState();
        if (battleState ==null)
            return 0;

        int result = 0;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.NUM_DESTINY_DRAWS_TO_ATTRITION_ONLY, battleState.getBattleLocation()))
            result += modifier.getNumDestinyDrawsToAttritionOnlyModifier(player, gameState, this);

        if (isGetLimit) {
            // TODO: See getNumBattleDestinyDraws() for what to do here.
            return Integer.MAX_VALUE;
        }

        return Math.max(0, result);
    }

    /**
     * Records that the specified card has performed a regular move.
     * @param card the card
     */
    @Override
    public void regularMovePerformed(PhysicalCard card) {
        _regularMoveSet.add(card.getCardId());
    }

    /**
     * Determines if the specified card has performed a regular move this turn.
     * @param card the card
     * @return true or false
     */
    @Override
    public boolean hasPerformedRegularMoveThisTurn(PhysicalCard card) {
        return _regularMoveSet.contains(card.getCardId());
    }

    @Override
    public void deviceUsedBy(PhysicalCard user, PhysicalCard device) {
        if (user==null)
            return;

        List<Integer> usedDevices = _usedDevicesMap.get(user.getCardId());
        if (usedDevices == null) {
            usedDevices = new LinkedList<Integer>();
            _usedDevicesMap.put(user.getCardId(), usedDevices);
        }
        usedDevices.add(device.getCardId());
    }

    @Override
    public List<Integer> otherDevicesUsed(PhysicalCard user, PhysicalCard device) {
        List<Integer> usedDevices = _usedDevicesMap.get(user.getCardId());
        List<Integer> otherDevices = new LinkedList<Integer>();
        if (usedDevices != null)
            for (Integer cardId : usedDevices)
                if (cardId!=device.getCardId())
                    otherDevices.add(cardId);

        return otherDevices;
    }

    @Override
    public int numDevicesAllowedToUse(GameState gameState, PhysicalCard card, boolean allowLanded) {
        SwccgCardBlueprint blueprint = card.getBlueprint();
        CardCategory cardCategory = blueprint.getCardCategory();
        if (cardCategory!=CardCategory.CHARACTER && cardCategory!=CardCategory.STARSHIP && cardCategory!=CardCategory.VEHICLE)
            return 0;

        if (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_USE_DEVICES, card).isEmpty()) {
            return 0;
        }

        if (allowLanded) {
            if (Filters.and(Filters.or(Filters.starship, Filters.vehicle), Filters.not(Filters.pilotedForTakeOff)).accepts(gameState, this, card)) {
                return 0;
            }
        }
        else {
            if (Filters.and(Filters.or(Filters.starship, Filters.vehicle), Filters.unpiloted).accepts(gameState, this, card)) {
                return 0;
            }
        }

        if (Filters.capital_starship.accepts(gameState, this, card)) {
            return Integer.MAX_VALUE;
        }

        if (!getModifiersAffectingCard(gameState, ModifierType.MAY_USE_ANY_NUMBER_OF_DEVICES, card).isEmpty()) {
            return Integer.MAX_VALUE;
        }

        int numDeviceUsedLimit = 1;

        if (Filters.squadron.accepts(gameState, this, card)) {
            numDeviceUsedLimit = 3;
        }

        return Math.max(0, numDeviceUsedLimit);
    }

    @Override
    public void weaponUsedBy(PhysicalCard user, PhysicalCard weapon) {
        if (user==null)
            return;

        List<Integer> usedWeapons = _usedWeaponsMap.get(user.getCardId());
        if (usedWeapons == null) {
            usedWeapons = new LinkedList<Integer>();
            _usedWeaponsMap.put(user.getCardId(), usedWeapons);
        }
        usedWeapons.add(weapon.getCardId());
    }

    @Override
    public List<Integer> otherWeaponsUsed(PhysicalCard user, PhysicalCard weapon) {
        List<Integer> usedWeapons = _usedWeaponsMap.get(user.getCardId());
        List<Integer> otherWeapons = new LinkedList<Integer>();
        if (usedWeapons != null)
            for (Integer cardId : usedWeapons)
                if (cardId!=weapon.getCardId())
                    otherWeapons.add(cardId);

        return otherWeapons;
    }

    @Override
    public List<Integer> otherWeaponsUsed(PhysicalCard user, SwccgBuiltInCardBlueprint permanentWeapon) {
        return otherWeaponsUsed(user, permanentWeapon.getPhysicalCard(_swccgGame));
    }

    @Override
    public int numWeaponsAllowedToUse(GameState gameState, PhysicalCard card, boolean allowLanded) {
        SwccgCardBlueprint blueprint = card.getBlueprint();
        CardCategory cardCategory = blueprint.getCardCategory();
        if (cardCategory!=CardCategory.CHARACTER && cardCategory!=CardCategory.STARSHIP && cardCategory!=CardCategory.VEHICLE && cardCategory!=CardCategory.LOCATION)
            return 0;

        if (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_USE_WEAPONS, card).isEmpty()) {
            return 0;
        }

        if (allowLanded) {
            if (Filters.and(Filters.or(Filters.starship, Filters.vehicle), Filters.not(Filters.pilotedForTakeOff)).accepts(gameState, this, card)) {
                return 0;
            }
        }
        else {
            if (Filters.and(Filters.or(Filters.starship, Filters.vehicle), Filters.unpiloted).accepts(gameState, this, card)) {
                return 0;
            }
        }

        if (Filters.capital_starship.accepts(gameState, this, card)) {
            return Integer.MAX_VALUE;
        }

        if (!getModifiersAffectingCard(gameState, ModifierType.MAY_USE_ANY_NUMBER_OF_WEAPONS, card).isEmpty()) {
            return Integer.MAX_VALUE;
        }

        int numWeaponUsedLimit = 1;

        if (Filters.squadron.accepts(gameState, this, card)) {
            numWeaponUsedLimit = 3;
        }
        else if (cardCategory == CardCategory.CHARACTER) {
            numWeaponUsedLimit = Math.max(numWeaponUsedLimit, getIconCount(gameState, card, Icon.WARRIOR));
        }

        return Math.max(0, numWeaponUsedLimit);
    }

    /**
     * Determines if the specified card is not prohibited from firing weapons.
     * @param gameState the game state
     * @param card the card
     * @return true or false
     */
    @Override
    public boolean notProhibitedFromFiringWeapons(GameState gameState, PhysicalCard card) {
        return (getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_FIRE_WEAPONS, card).isEmpty());
    }

    /**
     * Determines if the specified card is allowed to fire any number of weapons.
     * @param gameState the game state
     * @param card the card
     * @return true or false
     */
    @Override
    public boolean mayFireAnyNumberOfWeapons(GameState gameState, PhysicalCard card) {
        return (!getModifiersAffectingCard(gameState, ModifierType.MAY_FIRE_ANY_NUMBER_OF_WEAPONS, card).isEmpty());
    }

    /**
     * Records that the specified weapon has been fired in attack.
     * @param card the card
     * @param cardFiringWeapon the card firing the weapon
     * @param complete true if firing completed, false if targeted but not actually fired yet
     */
    @Override
    public void firedInAttack(PhysicalCard card, PhysicalCard cardFiringWeapon, boolean complete) {
        Map<String, Integer> firedInAttackMap = complete ? _firedInAttackCompletedMap : _firedInAttackMap;

        String cardId = String.valueOf(card.getCardId());
        final Integer value = firedInAttackMap.get(cardId);
        if (value == null)
            firedInAttackMap.put(cardId, 1);
        else
            firedInAttackMap.put(cardId, value + 1);

        Map<String, Map<String, PhysicalCard>> firedInAttackByPlayerMap = complete ? _firedInAttackByPlayerCompletedMap : _firedInAttackByPlayerMap;

        String playerId = card.getOwner();
        Map<String, PhysicalCard> firedWeaponsByPlayer = firedInAttackByPlayerMap.get(playerId);
        if (firedWeaponsByPlayer == null) {
            firedWeaponsByPlayer = new HashMap<String, PhysicalCard>();
            firedInAttackByPlayerMap.put(playerId, firedWeaponsByPlayer);
        }
        firedWeaponsByPlayer.put(cardId, card);

        if (cardFiringWeapon != null) {
            Map<String, Map<String, PhysicalCard>> firedInAttackByCardMap = complete ? _firedInAttackByCardCompletedMap : _firedInAttackByCardMap;

            String cardFiringWeaponCardId = String.valueOf(cardFiringWeapon.getCardId());
            Map<String, PhysicalCard> firedWeaponsByCard = firedInAttackByCardMap.get(cardFiringWeaponCardId);
            if (firedWeaponsByCard == null) {
                firedWeaponsByCard = new HashMap<String, PhysicalCard>();
                firedInAttackByCardMap.put(cardFiringWeaponCardId, firedWeaponsByCard);
            }
            firedWeaponsByCard.put(cardId, card);
        }
    }

    /**
     * Records that the specified permanent weapon has been fired in attack.
     * @param permanentWeapon the permanent weapon
     * @param cardFiringWeapon the card firing the weapon
     * @param complete true if firing completed, false if targeted but not actually fired yet
     */
    @Override
    public void firedInAttack(SwccgBuiltInCardBlueprint permanentWeapon, PhysicalCard cardFiringWeapon, boolean complete) {
        Map<String, Integer> firedInAttackMap = complete ? _firedInAttackCompletedMap : _firedInAttackMap;

        PhysicalCard card = permanentWeapon.getPhysicalCard(_swccgGame);
        String permWeaponId = card.getCardId() + "_" + permanentWeapon.getBuiltInId();
        final Integer value = firedInAttackMap.get(permWeaponId);
        if (value == null)
            firedInAttackMap.put(permWeaponId, 1);
        else
            firedInAttackMap.put(permWeaponId, value + 1);

        Map<String, Map<String, SwccgBuiltInCardBlueprint>> permWeaponFiredInAttackByPlayerMap = complete ? _permWeaponFiredInAttackByPlayerCompletedMap : _permWeaponFiredInAttackByPlayerMap;

        String playerId = card.getOwner();
        Map<String, SwccgBuiltInCardBlueprint> firedPermWeaponsByPlayer = permWeaponFiredInAttackByPlayerMap.get(playerId);
        if (firedPermWeaponsByPlayer == null) {
            firedPermWeaponsByPlayer = new HashMap<String, SwccgBuiltInCardBlueprint>();
            permWeaponFiredInAttackByPlayerMap.put(playerId, firedPermWeaponsByPlayer);
        }
        firedPermWeaponsByPlayer.put(permWeaponId, permanentWeapon);

        if (cardFiringWeapon != null) {
            Map<String, Map<String, SwccgBuiltInCardBlueprint>> firedInAttackByCardMap = complete ? _permWeaponFiredInAttackByCardCompletedMap : _permWeaponFiredInAttackByCardMap;

            String cardFiringWeaponCardId = String.valueOf(cardFiringWeapon.getCardId());
            Map<String, SwccgBuiltInCardBlueprint> firedWeaponsByCard = firedInAttackByCardMap.get(cardFiringWeaponCardId);
            if (firedWeaponsByCard == null) {
                firedWeaponsByCard = new HashMap<String, SwccgBuiltInCardBlueprint>();
                firedInAttackByCardMap.put(cardFiringWeaponCardId, firedWeaponsByCard);
            }
            firedWeaponsByCard.put(permWeaponId, permanentWeapon);
        }
    }

    /**
     * Gets the number of times the weapon has been fired in the current attack.
     * @param card the card
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     * @return the number of times the weapon has been fired
     */
    @Override
    public int numTimesFiredInAttack(PhysicalCard card, boolean completeOnly) {
        Map<String, Integer> firedInAttackMap = completeOnly ? _firedInAttackCompletedMap : _firedInAttackMap;

        final String cardId = String.valueOf(card.getCardId());
        final Integer value = firedInAttackMap.get(cardId);
        if (value == null)
            return 0;
        else
            return value;
    }

    /**
     * Gets the number of times the permanent weapon has been fired in the current attack.
     * @param permanentWeapon the permanent weapon
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     * @return the number of times the weapon has been fired
     */
    @Override
    public int numTimesFiredInAttack(SwccgBuiltInCardBlueprint permanentWeapon, boolean completeOnly) {
        Map<String, Integer> firedInAttackMap = completeOnly ? _firedInAttackCompletedMap : _firedInAttackMap;

        PhysicalCard card = permanentWeapon.getPhysicalCard(_swccgGame);
        String permWeaponId = card.getCardId() + "_" + permanentWeapon.getBuiltInId();
        final Integer value = firedInAttackMap.get(permWeaponId);
        if (value == null)
            return 0;
        else
            return value;
    }

    /**
     * Gets the weapons that have been fired in the current attack by the specified player.
     * @param playerId the player
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     */
    @Override
    public List<PhysicalCard> getWeaponsFiredInAttackByPlayer(String playerId, boolean completeOnly) {
        Map<String, Map<String, PhysicalCard>> firedInAttackByPlayerMap = completeOnly ? _firedInAttackByPlayerCompletedMap : _firedInAttackByPlayerMap;

        List<PhysicalCard> firedInAttackByPlayer = new LinkedList<PhysicalCard>();
        Map<String, PhysicalCard> firedWeaponsByPlayer = firedInAttackByPlayerMap.get(playerId);
        if (firedWeaponsByPlayer != null) {
            firedInAttackByPlayer.addAll(firedWeaponsByPlayer.values());
        }
        return firedInAttackByPlayer;
    }

    /**
     * Gets the permanent weapons that have been fired in the current attack by the specified player.
     * @param playerId the player
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     */
    @Override
    public List<SwccgBuiltInCardBlueprint> getPermanentWeaponsFiredInAttackByPlayer(String playerId, boolean completeOnly) {
        Map<String, Map<String, SwccgBuiltInCardBlueprint>> permWeaponFiredInAttackByPlayerMap = completeOnly ? _permWeaponFiredInAttackByPlayerCompletedMap : _permWeaponFiredInAttackByPlayerMap;

        List<SwccgBuiltInCardBlueprint> firedInAttackByPlayer = new LinkedList<SwccgBuiltInCardBlueprint>();
        Map<String, SwccgBuiltInCardBlueprint> firedWeaponsByPlayer = permWeaponFiredInAttackByPlayerMap.get(playerId);
        if (firedWeaponsByPlayer != null) {
            firedInAttackByPlayer.addAll(firedWeaponsByPlayer.values());
        }
        return firedInAttackByPlayer;
    }

    /**
     * Gets the weapons that have been fired in the current attack by the specified weapon user.
     * @param weaponUser the weapon user
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     */
    @Override
    public List<PhysicalCard> getWeaponsFiredInAttackByWeaponUser(PhysicalCard weaponUser, boolean completeOnly) {
        Map<String, Map<String, PhysicalCard>> firedInAttackByCardMap = completeOnly ? _firedInAttackByCardCompletedMap : _firedInAttackByCardMap;

        List<PhysicalCard> firedInAttackByCard = new LinkedList<PhysicalCard>();
        Map<String, PhysicalCard> firedWeaponsByCard = firedInAttackByCardMap.get(String.valueOf(weaponUser.getCardId()));
        if (firedWeaponsByCard != null) {
            firedInAttackByCard.addAll(firedWeaponsByCard.values());
        }
        return firedInAttackByCard;
    }

    /**
     * Gets the permanent weapons that have been fired in the current attack by the specified weapon user.
     * @param weaponUser the weapon user
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     */
    @Override
    public List<SwccgBuiltInCardBlueprint> getPermanentWeaponsFiredInAttackByWeaponUser(PhysicalCard weaponUser, boolean completeOnly) {
        Map<String, Map<String, SwccgBuiltInCardBlueprint>> permWeaponFiredInAttackByCardMap = completeOnly ? _permWeaponFiredInAttackByCardCompletedMap : _permWeaponFiredInAttackByCardMap;

        List<SwccgBuiltInCardBlueprint> firedInAttackByCard = new LinkedList<SwccgBuiltInCardBlueprint>();
        Map<String, SwccgBuiltInCardBlueprint> firedWeaponsByCard = permWeaponFiredInAttackByCardMap.get(String.valueOf(weaponUser.getCardId()));
        if (firedWeaponsByCard != null) {
            firedInAttackByCard.addAll(firedWeaponsByCard.values());
        }
        return firedInAttackByCard;
    }

    /**
     * Records that the specified weapon has been fired in battle.
     * @param card the card
     * @param cardFiringWeapon the card firing the weapon
     * @param complete true if firing completed, false if targeted but not actually fired yet
     */
    @Override
    public void firedInBattle(PhysicalCard card, PhysicalCard cardFiringWeapon, boolean complete) {
        Map<String, Integer> firedInBattleMap = complete ? _firedInBattleCompletedMap : _firedInBattleMap;

        String cardId = String.valueOf(card.getCardId());
        final Integer value = firedInBattleMap.get(cardId);
        if (value == null)
            firedInBattleMap.put(cardId, 1);
        else
            firedInBattleMap.put(cardId, value + 1);

        Map<String, Map<String, PhysicalCard>> firedInBattleByPlayerMap = complete ? _firedInBattleByPlayerCompletedMap : _firedInBattleByPlayerMap;

        String playerId = card.getOwner();
        Map<String, PhysicalCard> firedWeaponsByPlayer = firedInBattleByPlayerMap.get(playerId);
        if (firedWeaponsByPlayer == null) {
            firedWeaponsByPlayer = new HashMap<String, PhysicalCard>();
            firedInBattleByPlayerMap.put(playerId, firedWeaponsByPlayer);
        }
        firedWeaponsByPlayer.put(cardId, card);

        if (cardFiringWeapon != null) {
            Map<String, Map<String, PhysicalCard>> firedInBattleByCardMap = complete ? _firedInBattleByCardCompletedMap : _firedInBattleByCardMap;

            String cardFiringWeaponCardId = String.valueOf(cardFiringWeapon.getCardId());
            Map<String, PhysicalCard> firedWeaponsByCard = firedInBattleByCardMap.get(cardFiringWeaponCardId);
            if (firedWeaponsByCard == null) {
                firedWeaponsByCard = new HashMap<String, PhysicalCard>();
                firedInBattleByCardMap.put(cardFiringWeaponCardId, firedWeaponsByCard);
            }
            firedWeaponsByCard.put(cardId, card);
        }
    }

    /**
     * Records that the specified permanent weapon has been fired in battle.
     * @param permanentWeapon the permanent weapon
     * @param cardFiringWeapon the card firing the weapon
     * @param complete true if firing completed, false if targeted but not actually fired yet
     */
    @Override
    public void firedInBattle(SwccgBuiltInCardBlueprint permanentWeapon, PhysicalCard cardFiringWeapon, boolean complete) {
        Map<String, Integer> firedInBattleMap = complete ? _firedInBattleCompletedMap : _firedInBattleMap;

        PhysicalCard card = permanentWeapon.getPhysicalCard(_swccgGame);
        String permWeaponId = card.getCardId() + "_" + permanentWeapon.getBuiltInId();
        final Integer value = firedInBattleMap.get(permWeaponId);
        if (value == null)
            firedInBattleMap.put(permWeaponId, 1);
        else
            firedInBattleMap.put(permWeaponId, value + 1);

        Map<String, Map<String, SwccgBuiltInCardBlueprint>> permWeaponFiredInBattleByPlayerMap = complete ? _permWeaponFiredInBattleByPlayerCompletedMap : _permWeaponFiredInBattleByPlayerMap;

        String playerId = card.getOwner();
        Map<String, SwccgBuiltInCardBlueprint> firedPermWeaponsByPlayer = permWeaponFiredInBattleByPlayerMap.get(playerId);
        if (firedPermWeaponsByPlayer == null) {
            firedPermWeaponsByPlayer = new HashMap<String, SwccgBuiltInCardBlueprint>();
            permWeaponFiredInBattleByPlayerMap.put(playerId, firedPermWeaponsByPlayer);
        }
        firedPermWeaponsByPlayer.put(permWeaponId, permanentWeapon);

        if (cardFiringWeapon != null) {
            Map<String, Map<String, SwccgBuiltInCardBlueprint>> firedInBattleByCardMap = complete ? _permWeaponFiredInBattleByCardCompletedMap : _permWeaponFiredInBattleByCardMap;

            String cardFiringWeaponCardId = String.valueOf(cardFiringWeapon.getCardId());
            Map<String, SwccgBuiltInCardBlueprint> firedWeaponsByCard = firedInBattleByCardMap.get(cardFiringWeaponCardId);
            if (firedWeaponsByCard == null) {
                firedWeaponsByCard = new HashMap<String, SwccgBuiltInCardBlueprint>();
                firedInBattleByCardMap.put(cardFiringWeaponCardId, firedWeaponsByCard);
            }
            firedWeaponsByCard.put(permWeaponId, permanentWeapon);
        }
    }

    /**
     * Gets the number of times the weapon has been fired in the current battle.
     * @param card the card
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     * @return the number of times the weapon has been fired
     */
    @Override
    public int numTimesFiredInBattle(PhysicalCard card, boolean completeOnly) {
        Map<String, Integer> firedInBattleMap = completeOnly ? _firedInBattleCompletedMap : _firedInBattleMap;

        final String cardId = String.valueOf(card.getCardId());
        final Integer value = firedInBattleMap.get(cardId);
        if (value == null)
            return 0;
        else
            return value;
    }

    /**
     * Gets the number of times the permanent weapon has been fired in the current battle.
     * @param permanentWeapon the permanent weapon
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     * @return the number of times the weapon has been fired
     */
    @Override
    public int numTimesFiredInBattle(SwccgBuiltInCardBlueprint permanentWeapon, boolean completeOnly) {
        Map<String, Integer> firedInBattleMap = completeOnly ? _firedInBattleCompletedMap : _firedInBattleMap;

        PhysicalCard card = permanentWeapon.getPhysicalCard(_swccgGame);
        String permWeaponId = card.getCardId() + "_" + permanentWeapon.getBuiltInId();
        final Integer value = firedInBattleMap.get(permWeaponId);
        if (value == null)
            return 0;
        else
            return value;
    }

    /**
     * Gets the weapons that have been fired in the current battle by the specified player.
     * @param playerId the player
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     */
    @Override
    public List<PhysicalCard> getWeaponsFiredInBattleByPlayer(String playerId, boolean completeOnly) {
        Map<String, Map<String, PhysicalCard>> firedInBattleByPlayerMap = completeOnly ? _firedInBattleByPlayerCompletedMap : _firedInBattleByPlayerMap;

        List<PhysicalCard> firedInBattleByPlayer = new LinkedList<PhysicalCard>();
        Map<String, PhysicalCard> firedWeaponsByPlayer = firedInBattleByPlayerMap.get(playerId);
        if (firedWeaponsByPlayer != null) {
            firedInBattleByPlayer.addAll(firedWeaponsByPlayer.values());
        }
        return firedInBattleByPlayer;
    }

    /**
     * Gets the permanent weapons that have been fired in the current battle by the specified player.
     * @param playerId the player
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     */
    @Override
    public List<SwccgBuiltInCardBlueprint> getPermanentWeaponsFiredInBattleByPlayer(String playerId, boolean completeOnly) {
        Map<String, Map<String, SwccgBuiltInCardBlueprint>> permWeaponFiredInBattleByPlayerMap = completeOnly ? _permWeaponFiredInBattleByPlayerCompletedMap : _permWeaponFiredInBattleByPlayerMap;

        List<SwccgBuiltInCardBlueprint> firedInBattleByPlayer = new LinkedList<SwccgBuiltInCardBlueprint>();
        Map<String, SwccgBuiltInCardBlueprint> firedWeaponsByPlayer = permWeaponFiredInBattleByPlayerMap.get(playerId);
        if (firedWeaponsByPlayer != null) {
            firedInBattleByPlayer.addAll(firedWeaponsByPlayer.values());
        }
        return firedInBattleByPlayer;
    }

    /**
     * Gets the weapons that have been fired in the current battle by the specified weapon user.
     * @param weaponUser the weapon user
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     */
    @Override
    public List<PhysicalCard> getWeaponsFiredInBattleByWeaponUser(PhysicalCard weaponUser, boolean completeOnly) {
        Map<String, Map<String, PhysicalCard>> firedInBattleByCardMap = completeOnly ? _firedInBattleByCardCompletedMap : _firedInBattleByCardMap;

        List<PhysicalCard> firedInBattleByCard = new LinkedList<PhysicalCard>();
        Map<String, PhysicalCard> firedWeaponsByCard = firedInBattleByCardMap.get(String.valueOf(weaponUser.getCardId()));
        if (firedWeaponsByCard != null) {
            firedInBattleByCard.addAll(firedWeaponsByCard.values());
        }
        return firedInBattleByCard;
    }

    /**
     * Gets the permanent weapons that have been fired in the current battle by the specified weapon user.
     * @param weaponUser the weapon user
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     */
    @Override
    public List<SwccgBuiltInCardBlueprint> getPermanentWeaponsFiredInBattleByWeaponUser(PhysicalCard weaponUser, boolean completeOnly) {
        Map<String, Map<String, SwccgBuiltInCardBlueprint>> permWeaponFiredInBattleByCardMap = completeOnly ? _permWeaponFiredInBattleByCardCompletedMap : _permWeaponFiredInBattleByCardMap;

        List<SwccgBuiltInCardBlueprint> firedInBattleByCard = new LinkedList<SwccgBuiltInCardBlueprint>();
        Map<String, SwccgBuiltInCardBlueprint> firedWeaponsByCard = permWeaponFiredInBattleByCardMap.get(String.valueOf(weaponUser.getCardId()));
        if (firedWeaponsByCard != null) {
            firedInBattleByCard.addAll(firedWeaponsByCard.values());
        }
        return firedInBattleByCard;
    }

    /**
     * Records that the specified weapon has been fired during Attack Run.
     * @param card the card
     * @param cardFiringWeapon the card firing the weapon
     * @param complete true if firing completed, false if targeted but not actually fired yet
     */
    @Override
    public void firedInAttackRun(PhysicalCard card, PhysicalCard cardFiringWeapon, boolean complete) {
        Map<String, Integer> firedInAttackRunMap = complete ? _firedInAttackRunCompletedMap : _firedInAttackRunMap;

        String cardId = String.valueOf(card.getCardId());
        final Integer value = firedInAttackRunMap.get(cardId);
        if (value == null)
            firedInAttackRunMap.put(cardId, 1);
        else
            firedInAttackRunMap.put(cardId, value + 1);
    }

    /**
     * Records that the specified permanent weapon has been fired during Attack Run.
     * @param permanentWeapon the permanent weapon
     * @param cardFiringWeapon the card firing the weapon
     * @param complete true if firing completed, false if targeted but not actually fired yet
     */
    @Override
    public void firedInAttackRun(SwccgBuiltInCardBlueprint permanentWeapon, PhysicalCard cardFiringWeapon, boolean complete) {
        Map<String, Integer> firedInAttackRunMap = complete ? _firedInAttackRunCompletedMap : _firedInAttackRunMap;

        PhysicalCard card = permanentWeapon.getPhysicalCard(_swccgGame);
        String permWeaponId = card.getCardId() + "_" + permanentWeapon.getBuiltInId();
        final Integer value = firedInAttackRunMap.get(permWeaponId);
        if (value == null)
            firedInAttackRunMap.put(permWeaponId, 1);
        else
            firedInAttackRunMap.put(permWeaponId, value + 1);
    }

    /**
     * Gets the number of times the weapon has been fired in the during current Attack Run.
     * @param card the card
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     * @return the number of times the weapon has been fired
     */
    @Override
    public int numTimesFiredInAttackRun(PhysicalCard card, boolean completeOnly) {
        Map<String, Integer> firedInAttackRunMap = completeOnly ? _firedInAttackRunCompletedMap : _firedInAttackRunMap;

        final String cardId = String.valueOf(card.getCardId());
        final Integer value = firedInAttackRunMap.get(cardId);
        if (value == null)
            return 0;
        else
            return value;
    }

    /**
     * Gets the number of times the permanent weapon has been fired during current Attack Run.
     * @param permanentWeapon the permanent weapon
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     * @return the number of times the weapon has been fired
     */
    @Override
    public int numTimesFiredInAttackRun(SwccgBuiltInCardBlueprint permanentWeapon, boolean completeOnly) {
        Map<String, Integer> firedInAttackRunMap = completeOnly ? _firedInAttackRunCompletedMap : _firedInAttackRunMap;

        PhysicalCard card = permanentWeapon.getPhysicalCard(_swccgGame);
        String permWeaponId = card.getCardId() + "_" + permanentWeapon.getBuiltInId();
        final Integer value = firedInAttackRunMap.get(permWeaponId);
        if (value == null)
            return 0;
        else
            return value;
    }

    /**
     * Determines if the specified weapon is granted ability to fire twice per battle.
     * @param gameState the game state
     * @param weapon the weapon
     * @return true or false
     */
    @Override
    public boolean mayBeFiredTwicePerBattle(GameState gameState, PhysicalCard weapon) {
        return !getModifiersAffectingCard(gameState, ModifierType.MAY_FIRE_TWICE_PER_BATTLE, weapon).isEmpty();
    }

    /**
     * Determines if the specified card is granted ability to fire the specified weapon twice per battle.
     * @param gameState the game state
     * @param weaponUser the weapon user
     * @param weapon the weapon
     * @return true or false
     */
    @Override
    public boolean mayFireWeaponTwicePerBattle(GameState gameState, PhysicalCard weaponUser, PhysicalCard weapon) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_FIRE_A_WEAPON_TWICE_PER_BATTLE, weaponUser)) {
            if (modifier.isAffectedTarget(gameState, this, weapon)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Records that the specified target card was targeted by the specified weapon.
     * @param target the target
     * @param weapon the weapon
     */
    @Override
    public void targetedByWeapon(PhysicalCard target, PhysicalCard weapon) {
        List<PhysicalCard> targetedByWeapons = _targetedByWeaponsMap.get(target.getCardId());
        if (targetedByWeapons == null) {
            targetedByWeapons = new LinkedList<PhysicalCard>();
            _targetedByWeaponsMap.put(target.getCardId(), targetedByWeapons);
        }
        targetedByWeapons.add(weapon);
    }

    /**
     * Records that the specified target card was targeted by the specified permanent weapon.
     * @param target the target
     * @param permanentWeapon the permanent weapon
     */
    @Override
    public void targetedByPermanentWeapon(PhysicalCard target, SwccgBuiltInCardBlueprint permanentWeapon) {
        List<SwccgBuiltInCardBlueprint> targetedByPermanentWeapons = _targetedByPermanentWeaponsMap.get(target.getCardId());
        if (targetedByPermanentWeapons == null) {
            targetedByPermanentWeapons = new LinkedList<SwccgBuiltInCardBlueprint>();
            _targetedByPermanentWeaponsMap.put(target.getCardId(), targetedByPermanentWeapons);
        }
        targetedByPermanentWeapons.add(permanentWeapon);
    }

    /**
     * Gets the weapons that have targeted the specified target this turn.
     * @param target the target
     * @return the weapons that have targeted the specified target this turn
     */
    @Override
    public List<PhysicalCard> weaponsTargetedByThisTurn(PhysicalCard target) {
        List<PhysicalCard> weapons = _targetedByWeaponsMap.get(target.getCardId());
        if (weapons == null) {
            weapons = Collections.emptyList();
        }
        return weapons;
    }

    /**
     * Gets the permanent weapons that have targeted the specified target this turn.
     * @param target the target
     * @return the permanent weapons that have targeted the specified target this turn
     */
    @Override
    public List<SwccgBuiltInCardBlueprint> permanentWeaponsTargetedByThisTurn(PhysicalCard target) {
        List<SwccgBuiltInCardBlueprint> permanentWeapons = _targetedByPermanentWeaponsMap.get(target.getCardId());
        if (permanentWeapons == null) {
            permanentWeapons = Collections.emptyList();
        }
        return permanentWeapons;
    }

    /**
     * Determines if the specified artillery weapon is powered.
     * @param gameState the game state
     * @param artilleryWeapon the artillery weapon
     * @return true or false
     */
    @Override
    public boolean isPowered(GameState gameState, PhysicalCard artilleryWeapon) {
        if (!getModifiersAffectingCard(gameState, ModifierType.IS_POWERED, artilleryWeapon).isEmpty())
            return true;

        if (Filters.presentWith(null, Filters.and(Filters.owner(artilleryWeapon.getOwner()),
                Filters.or(Filters.power_droid, Filters.fusion_generator))).accepts(gameState, this, artilleryWeapon))
            return true;

        return false;
    }

    /**
     * Determines if the specified artillery weapon is does not require a power source.
     * @param gameState the game state
     * @param artilleryWeapon the artillery weapon
     * @return true or false
     */
    @Override
    public boolean doesNotRequirePowerSource(GameState gameState, PhysicalCard artilleryWeapon) {
        return !getModifiersAffectingCard(gameState, ModifierType.DOES_NOT_REQUIRE_POWER_SOURCE, artilleryWeapon).isEmpty();
    }

    @Override
    public boolean ignoreDuringEpicEventCalculation(GameState gameState, PhysicalCard card) {
        if (Filters.and(Filters.generic, Filters.location).accepts(gameState, this, card)) {
            return true;
        }
        return (!getModifiersAffectingCard(gameState, ModifierType.IGNORE_DURING_EPIC_EVENT_CALCULATION, card).isEmpty());
    }

    @Override
    public float getEpicEventCalculationTotal(GameState gameState, PhysicalCard physicalCard, float baseTotal) {
        float result = baseTotal;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EPIC_EVENT_CALCULATION_TOTAL, physicalCard)) {
            result += modifier.getEpicEventCalculationTotalModifier(gameState, this, physicalCard);
        }
        return Math.max(0, result);
    }

    /**
     * Gets the calculation total.
     * @param gameState the game state
     * @param calculationSource the source card during the calculation
     * @param baseTotal the base total
     * @return the calculation total
     */
    @Override
    public float getCalculationTotal(GameState gameState, PhysicalCard calculationSource, float baseTotal) {
        float result = baseTotal;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.CALCULATION_TOTAL, calculationSource)) {
            result += modifier.getValue(gameState, this, calculationSource);
        }
        return Math.max(0, result);
    }

    /**
     * Gets the calculation total when targeting a specified card.
     * @param gameState the game state
     * @param calculationSource the source card during the calculation
     * @param target the target
     * @param baseTotal the base total
     * @return the calculation total
     */
    @Override
    public float getCalculationTotalTargetingCard(GameState gameState, PhysicalCard calculationSource, PhysicalCard target, float baseTotal) {
        float result = baseTotal;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.CALCULATION_TOTAL, calculationSource)) {
            result += modifier.getValue(gameState, this, calculationSource);
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.CALCULATION_TOTAL_WHEN_TARGETED, target)) {
            if (modifier.isActionSource(gameState, this, calculationSource)) {
                result += modifier.getValue(gameState, this, target);
            }
        }
        return Math.max(0, result);
    }

    /**
     * Gets the 'blow away' Blockade Flagship attempt total.
     * @param gameState the game state
     * @param baseTotal the base total
     * @return the total
     */
    @Override
    public float getBlowAwayBlockadeFlagshipAttemptTotal(GameState gameState, float baseTotal) {
        float result = baseTotal;
        for (Modifier modifier : getModifiers(gameState, ModifierType.BLOW_AWAY_BLOCKADE_FLAGSHIP_ATTEMPT_TOTAL)) {
            result += modifier.getValue(gameState, this, (PhysicalCard) null);
        }
        return Math.max(0, result);
    }

    @Override
    public boolean mayNotBeUsed(GameState gameState, PhysicalCard deviceOrWeapon) {
        for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_NOT_BE_USED)) {
            if (modifier.isAffectedTarget(gameState, this, deviceOrWeapon)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mayNotBeUsed(GameState gameState, SwccgBuiltInCardBlueprint permanentWeapon) {
        for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_NOT_BE_USED)) {
            if (modifier.isAffectedTarget(gameState, this, permanentWeapon)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mayNotBeFired(GameState gameState, PhysicalCard weapon) {
        for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_NOT_BE_FIRED)) {
            if (modifier.isAffectedTarget(gameState, this, weapon)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mayNotBeFired(GameState gameState, SwccgBuiltInCardBlueprint permanentWeapon) {
        for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_NOT_BE_FIRED)) {
            if (modifier.isAffectedTarget(gameState, this, permanentWeapon)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the specified device or weapon is allowed to be used by a landed starship.
     * @param gameState the game state
     * @param deviceOrWeapon the device or weapon
     * @return true or false
     */
    @Override
    public boolean mayBeUsedByLandedStarship(GameState gameState, PhysicalCard deviceOrWeapon) {
        return !getModifiersAffectingCard(gameState, ModifierType.MAY_BE_USED_BY_LANDED_STARSHIP, deviceOrWeapon).isEmpty();
    }

    @Override
    public boolean canWeaponTargetAdjacentSite(GameState gameState, PhysicalCard weapon) {
        for (Modifier modifier : getModifiers(gameState, ModifierType.TARGET_ADJACENT_SITE)) {
            if (modifier.isAffectedTarget(gameState, this, weapon)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canWeaponTargetAdjacentSite(GameState gameState, SwccgBuiltInCardBlueprint permanentWeapon) {
        for (Modifier modifier : getModifiers(gameState, ModifierType.TARGET_ADJACENT_SITE)) {
            if (modifier.isAffectedTarget(gameState, this, permanentWeapon)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canWeaponTargetTwoSitesAway(GameState gameState, PhysicalCard weapon) {
        for (Modifier modifier : getModifiers(gameState, ModifierType.TARGET_TWO_SITE_AWAY)) {
            if (modifier.isAffectedTarget(gameState, this, weapon)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canWeaponTargetTwoSitesAway(GameState gameState, SwccgBuiltInCardBlueprint permanentWeapon) {
        for (Modifier modifier : getModifiers(gameState, ModifierType.TARGET_TWO_SITE_AWAY)) {
            if (modifier.isAffectedTarget(gameState, this, permanentWeapon)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canWeaponTargetNearestRelatedExteriorSite(GameState gameState, PhysicalCard weapon) {
        for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_TARGET_AT_NEAREST_RELATED_EXTERIOR_SITE)) {
            if (modifier.isAffectedTarget(gameState, this, weapon)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canWeaponTargetNearestRelatedExteriorSite(GameState gameState, SwccgBuiltInCardBlueprint permanentWeapon) {
        for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_TARGET_AT_NEAREST_RELATED_EXTERIOR_SITE)) {
            if (modifier.isAffectedTarget(gameState, this, permanentWeapon)) {
                return true;
            }
        }
        return false;
    }

    private List<Modifier> getEffectModifiers(ModifierType modifierType) {
        List<Modifier> modifiers = _modifiers.get(modifierType);
        if (modifiers == null) {
            modifiers = new LinkedList<Modifier>();
            _modifiers.put(modifierType, modifiers);
        }
        return modifiers;
    }

    @Override
    public boolean canPerformSpecialBattlegroundDownload(GameState gameState, String playerId) {
       if (!gameState.getGame().getFormat().hasDownloadBattlegroundRule())
           return false;
       return _specialDownloadBattlegroundMap.get(playerId)==null;
    }

    @Override
    public void performedSpecialBattlegroundDownload(String playerId) {
        _specialDownloadBattlegroundMap.put(playerId, 1);
    }

    /**
     * Determines if the specified card is prohibited from allowing the specified player to download cards.
     * @param gameState the game state
     * @param card the card
     * @param playerId the playerId
     * @return true or false
     */
    @Override
    public boolean isProhibitedFromAllowingPlayerToDownloadCards(GameState gameState, PhysicalCard card, String playerId) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_ALLOW_PLAYER_TO_DOWNLOAD_CARDS, card)) {
            if (modifier.isForPlayer(playerId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the specified player is prohibited from deploying cards as a 'react' to the current battle or Force
     * Drain location.
     * @param gameState the game state
     * @param playerId the player
     * @return true if player may not deploy cards as a 'react', otherwise false
     */
    @Override
    public boolean isProhibitedFromDeployingAsReact(GameState gameState, String playerId) {
        PhysicalCard location = gameState.getBattleOrForceDrainLocation();
        if (location == null)
            return true;

        // Check if player may not 'react' to the location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REACT_TO_LOCATION, location)) {
            if (modifier.isForPlayer(playerId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the 'react' action option if the specified card is allowed to deploy as a 'react'.
     * @param gameState the game state
     * @param card the card
     * @param reactActionFromOtherCard a 'react' action if 'react' originates from another card, or null
     * @param deployTargetFilter the filter for where the card can be played
     * @return 'react' action option, or null
     */
    @Override
    public ReactActionOption getDeployAsReactOption(GameState gameState, PhysicalCard card, ReactActionOption reactActionFromOtherCard, Filter deployTargetFilter) {
        // Check if card is prohibited from any 'react'
        if (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REACT, card).isEmpty()) {
            return null;
        }

        PhysicalCard location = gameState.getBattleOrForceDrainLocation();

        // Check if player may not 'react' to the location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REACT_TO_LOCATION, location)) {
            if (modifier.isForPlayer(card.getOwner())) {
                return null;
            }
        }

        // If can deploy to as 'react' due to another card, then return that option
        if (reactActionFromOtherCard != null) {
            return reactActionFromOtherCard;
        }

        // Gets possible 'react' to targets
        Collection<PhysicalCard> targets = Filters.filterActive(gameState.getGame(), null, Filters.and(deployTargetFilter, Filters.locationAndCardsAtLocation(Filters.sameCardId(location))));

        // Check if card may deploy as a 'react' to the target
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_AS_REACT_TO_TARGET, card)) {
            for (PhysicalCard target : targets) {
                if (modifier.isAffectedTarget(gameState, this, target)) {

                    return new ReactActionOption(modifier.getSource(gameState), modifier.isReactForFree(),
                            modifier.getChangeInCost(), false, modifier.getText(gameState, this, card), card, deployTargetFilter, null, modifier.isGrantedToDeployToTarget());
                }
            }
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_WITH_PILOT_OR_DRIVER_AS_REACT_TO_TARGET, card)) {
            for (PhysicalCard target : targets) {
                if (modifier.isAffectedTarget(gameState, this, target)) {

                    return new ReactActionOption(modifier.getSource(gameState), modifier.isReactForFree(),
                            modifier.getChangeInCost(), false, modifier.getText(gameState, this, card), card, deployTargetFilter, modifier.getPilotOrDriverFilter(), modifier.isGrantedToDeployToTarget());
                }
            }
        }

        return null;
    }

    /**
     * Gets the 'react' action option if the player can use the specified card to deploy other cards as a 'react'.
     * @param playerId the player
     * @param gameState the game state
     * @param card the card
     * @return 'react' action option, or null
     */
    @Override
    public ReactActionOption getDeployOtherCardsAsReactOption(String playerId, GameState gameState, PhysicalCard card) {

        // Check the cards in player's hand and the stacked cards that can deploy as if from hand.
        List<PhysicalCard> cardsToCheck = new ArrayList<PhysicalCard>();
        cardsToCheck.addAll(gameState.getHand(playerId));
        cardsToCheck.addAll(Filters.filter(gameState.getAllStackedCards(), gameState.getGame(), Filters.and(Filters.owner(playerId), Filters.canDeployAsIfFromHand)));
        if (cardsToCheck.isEmpty()) {
            return null;
        }

        // Check if card may deploy other cards as a 'react'
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_OTHER_CARD_AS_REACT_TO_TARGET, card)) {
            if (modifier.isForPlayer(playerId)) {
                Filter cardToReactFilter = modifier.getCardToReactFilter();
                Filter targetFilter = modifier.getTargetFilter();
                ReactActionOption reactActionOption = new ReactActionOption(card, modifier.isReactForFree(), modifier.getChangeInCost(),
                        false, modifier.getActionText(), cardToReactFilter, targetFilter, null, modifier.isGrantedToDeployToTarget());

                List<PhysicalCard> validToDeployAsReact = new ArrayList<PhysicalCard>();

                // Check if card can deploy as a 'react' (performed by this card)
                for (PhysicalCard cardToCheck : cardsToCheck) {
                    if (cardToReactFilter.accepts(gameState, this, cardToCheck)) {
                        reactActionOption.setCardToReactFilter(cardToCheck);

                        Action deployAsReactAction = cardToCheck.getBlueprint().getDeployAsReactAction(playerId, gameState.getGame(),
                                cardToCheck, reactActionOption, targetFilter);
                        if (deployAsReactAction != null) {
                            validToDeployAsReact.add(cardToCheck);
                        }
                    }
                }

                if (!validToDeployAsReact.isEmpty()) {
                    // Update the filter with the cards that can actually deploy as a 'react' and return the action option
                    reactActionOption.setCardToReactFilter(Filters.in(validToDeployAsReact));
                    return reactActionOption;
                }
            }
        }

        return null;
    }

    /**
     * Gets the 'react' action option if the specified card is allowed to move as a 'react'.
     * @param gameState the game state
     * @param card the card
     * @param reactActionFromOtherCard a 'react' action if 'react' originates from another card, or null
     * @param asReactAway true if 'react' away, otherwise 'react'
     * @param moveTargetFilter the filter for where the card can be moved
     * @return 'react' action options
     */
    @Override
    public ReactActionOption getMoveAsReactOption(GameState gameState, PhysicalCard card, ReactActionOption reactActionFromOtherCard, boolean asReactAway, Filter moveTargetFilter) {
        // Check if card is prohibited from any 'react'
        if (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REACT, card).isEmpty()) {
            return null;
        }

        // Check if location is accepted by the move target filter
        PhysicalCard location = gameState.getBattleOrForceDrainLocation();
        if (asReactAway) {
            if (moveTargetFilter.accepts(gameState, this, location)) {
                return null;
            }

            // Check if player may not 'react' from the location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REACT_FROM_LOCATION, location)) {
                if (modifier.isForPlayer(card.getOwner())) {
                    return null;
                }
            }

            // If can move to as 'react' due to another card, then only return that option
            if (reactActionFromOtherCard != null && reactActionFromOtherCard.isReactAway()) {
                return reactActionFromOtherCard;
            }

            // Check if card may move away as a 'react'
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_MOVE_AWAY_AS_REACT_TO_LOCATION, card)) {
                if (modifier.isAffectedTarget(gameState, this, location)) {

                    return new ReactActionOption(modifier.getSource(gameState), modifier.isReactForFree(),
                            modifier.getChangeInCost(), true, modifier.getText(gameState, this, card), card, moveTargetFilter, null, false);
                }
            }
        }
        else {
            if (!moveTargetFilter.accepts(gameState, this, location)) {
                return null;
            }

            // Check if player may not 'react' to the location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REACT_TO_LOCATION, location)) {
                if (modifier.isForPlayer(card.getOwner())) {
                    return null;
                }
            }

            // If can move to as 'react' due to another card, then only return that option
            if (reactActionFromOtherCard != null && !reactActionFromOtherCard.isReactAway()) {
                return reactActionFromOtherCard;
            }

            // Check if card may move as a 'react' to the location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_MOVE_AS_REACT_TO_LOCATION, card)) {
                if (modifier.isAffectedTarget(gameState, this, location)) {

                    return new ReactActionOption(modifier.getSource(gameState), modifier.isReactForFree(),
                            modifier.getChangeInCost(), false, modifier.getText(gameState, this, card), card, moveTargetFilter, null, false);
                }
            }
        }

        return null;
    }

    /**
     * Gets the 'react' action option if the player can use the specified card to move other cards as a 'react'.
     * @param playerId the player
     * @param gameState the game state
     * @param card the card
     * @return 'react' action option, or null
     */
    @Override
    public ReactActionOption getMoveOtherCardsAsReactOption(String playerId, GameState gameState, PhysicalCard card) {

        // Check the cards in the player has in play.
        List<PhysicalCard> cardsToCheck = new ArrayList<PhysicalCard>(Filters.filterActive(gameState.getGame(), null,
                Filters.and(Filters.owner(playerId), Filters.or(Filters.character, Filters.starship, Filters.vehicle))));
        if (cardsToCheck.isEmpty()) {
            return null;
        }

        // Check if card may allow other cards to move as a 'react'
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_MOVE_OTHER_CARD_AS_REACT_TO_LOCATION, card)) {
            if (modifier.isForPlayer(playerId)) {
                Filter cardToReactFilter = modifier.getCardToReactFilter();
                Filter targetFilter = modifier.getTargetFilter();
                ReactActionOption reactActionOption = new ReactActionOption(card, modifier.isReactForFree(), modifier.getChangeInCost(),
                        false, modifier.getActionText(), cardToReactFilter, targetFilter, null, false);

                List<PhysicalCard> validToMoveAsReact = new ArrayList<PhysicalCard>();

                // Check if card can move as a 'react' (performed by this card)
                for (PhysicalCard cardToCheck : cardsToCheck) {
                    if (cardToReactFilter.accepts(gameState, this, cardToCheck)) {

                        Action moveAsReactAction = cardToCheck.getBlueprint().getMoveAsReactAction(playerId, gameState.getGame(),
                                cardToCheck, reactActionOption, targetFilter);
                        if (moveAsReactAction != null) {
                            validToMoveAsReact.add(cardToCheck);
                        }
                    }
                }

                if (!validToMoveAsReact.isEmpty()) {
                    // Update the filter with the cards that can actually move as a 'react' and return the action option
                    reactActionOption.setCardToReactFilter(Filters.in(validToMoveAsReact));
                    return reactActionOption;
                }
            }
        }

        return null;
    }

    /**
     * Determines if the specified card is able to join the move as 'react'.
     * @param playerId the player
     * @param gameState the game state
     * @param sourceCard the source card of the 'react'
     * @param card the card
     * @return true or false
     */
    @Override
    public boolean isCardEligibleToJoinMoveAsReact(String playerId, GameState gameState, PhysicalCard sourceCard, PhysicalCard card) {

        if (!Filters.and(Filters.owner(playerId), Filters.or(Filters.character, Filters.starship, Filters.vehicle)).accepts(gameState, this, card)) {
            return false;
        }

        // Check if card may allow other cards to move as a 'react'
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_MOVE_OTHER_CARD_AS_REACT_TO_LOCATION, sourceCard)) {
            if (modifier.isForPlayer(playerId)) {
                Filter cardToReactFilter = modifier.getCardToReactFilter();
                Filter targetFilter = modifier.getTargetFilter();
                ReactActionOption curReactActionOption = new ReactActionOption(sourceCard, true, 0, false,
                        modifier.getActionText(), cardToReactFilter, targetFilter, null, false);

                if (cardToReactFilter.accepts(gameState, this, card)) {

                    Action moveAsReactAction = card.getBlueprint().getMoveAsReactAction(playerId, gameState.getGame(),
                            card, curReactActionOption, targetFilter);
                    return moveAsReactAction != null;
                }
            }
        }

        return false;
    }

    /**
     * Gets the 'react' action option if the player can use the specified card to move other cards away as a 'react'.
     * @param playerId the player
     * @param gameState the game state
     * @param card the card
     * @return 'react' action option, or null
     */
    @Override
    public ReactActionOption getMoveOtherCardsAwayAsReactOption(String playerId, GameState gameState, PhysicalCard card) {

        // Check the cards in the player has in play.
        List<PhysicalCard> cardsToCheck = new ArrayList<PhysicalCard>(Filters.filterActive(gameState.getGame(), null, Filters.and(Filters.owner(playerId), Filters.or(Filters.character, Filters.starship, Filters.vehicle))));
        if (cardsToCheck.isEmpty()) {
            return null;
        }

        // Check if card may allow other cards to move away as a 'react'
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_MOVE_OTHER_CARD_AWAY_AS_REACT_TO_LOCATION, card)) {
            if (modifier.isForPlayer(playerId)) {
                Filter cardToReactFilter = modifier.getCardToReactFilter();
                Filter targetFilter = modifier.getTargetFilter();
                ReactActionOption reactActionOption = new ReactActionOption(card, modifier.isReactForFree(), modifier.getChangeInCost(),
                        true, modifier.getActionText(), cardToReactFilter, targetFilter, null, false);

                List<PhysicalCard> validToMoveAwayAsReact = new ArrayList<PhysicalCard>();

                // Check if card can move away as a 'react' (performed by this card)
                for (PhysicalCard cardToCheck : cardsToCheck) {
                    if (cardToReactFilter.accepts(gameState, this, cardToCheck)) {

                        Action moveAsReactAction = cardToCheck.getBlueprint().getMoveAsReactAction(playerId, gameState.getGame(),
                                cardToCheck, reactActionOption, targetFilter);
                        if (moveAsReactAction != null) {
                            validToMoveAwayAsReact.add(cardToCheck);
                        }
                    }
                }

                if (!validToMoveAwayAsReact.isEmpty()) {
                    // Update the filter with the cards that can actually move away as a 'react' and return the action option
                    reactActionOption.setCardToReactFilter(Filters.in(validToMoveAwayAsReact));
                    return reactActionOption;
                }
            }
        }

        return null;
    }

    /**
     * Determines if the specified card is prohibited from participating in a 'react'.
     * @param gameState the game state
     * @param card the card
     * @return true if card is prohibited from participating in a 'react', otherwise false
     */
    @Override
    public boolean isProhibitedFromParticipatingInReact(GameState gameState, PhysicalCard card) {
        return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REACT, card).isEmpty());
    }

    /**
     * Determines if the specified captive is prohibited from being transferred.
     * @param gameState the game state
     * @param card the card
     * @return true if card is prohibited from being transferred, otherwise false
     */
    @Override
    public boolean mayNotBeTransferred(GameState gameState, PhysicalCard card) {
        if (!card.isCaptive() || mayNotMove(gameState, card))
            return true;

        PhysicalCard escort = Filters.escortedCaptive.accepts(gameState, this, card) ? card.getAttachedTo() : null;
        if (escort != null && mayNotMove(gameState, escort)) {
            return true;
        }

        return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_TRANSFERRED, card).isEmpty());
    }

    /**
     * Determines if the specified card is prohibited from moving.
     * @param gameState the game state
     * @param card the card
     * @return true if card is prohibited from moving, otherwise false
     */
    @Override
    public boolean mayNotMove(GameState gameState, PhysicalCard card) {
        if (card.isMissing() || card.isConcealed() || card.isCrashed())
            return true;

        if (card.getAttachedTo() != null
                && Filters.aboard(Filters.makingBombingRun).accepts(gameState, this, card)) {
            return true;
        }

        return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE, card).isEmpty());
    }

    /**
     * Determines if the specified card is prohibited from moving except using landspeed.
     * @param gameState the game state
     * @param card the card
     * @return true if card is prohibited from moving except using landspeed, otherwise false
     */
    @Override
    public boolean mayOnlyMoveUsingLandspeed(GameState gameState, PhysicalCard card) {
        return (!getModifiersAffectingCard(gameState, ModifierType.MAY_ONLY_MOVE_USING_LANDSPEED, card).isEmpty());
    }

    /**
     * Determines if the specified card is prohibited from moving from site to site using hyperspeed.
     * @param gameState the game state
     * @param card the card
     * @param fromSite the site to move from
     * @param toSite the site to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @return true if card is prohibited from moving, otherwise false
     */
    @Override
    public boolean mayNotMoveFromLocationToLocationUsingLandspeed(GameState gameState, PhysicalCard card, PhysicalCard fromSite, PhysicalCard toSite, boolean asReact) {
        List<PhysicalCard> sitesBetween = getSitesBetween(gameState, fromSite, toSite);
        if (sitesBetween == null) {
            return true;
        }

        List<PhysicalCard> sitesAlongPath = new ArrayList<PhysicalCard>();
        sitesAlongPath.add(fromSite);
        sitesAlongPath.addAll(sitesBetween);
        sitesAlongPath.add(toSite);

        for (int i=0; i<sitesAlongPath.size()-1; ++i) {
            PhysicalCard curFromSite = sitesAlongPath.get(i);
            PhysicalCard curToSite = sitesAlongPath.get(i+1);

            // Check if not a valid location for card to move to
            if (!card.getBlueprint().getValidMoveTargetFilter(card.getOwner(), gameState.getGame(), card, false).accepts(gameState, this, curToSite)) {
                return true;
            }

            // Check if may not move from location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_FROM_LOCATION, curFromSite)) {
                if (modifier.prohibitedFromMovingFromLocation(gameState, this, card)) {
                    return true;
                }
            }

            // Check if may not move from location using landspeed
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_FROM_LOCATION_USING_LANDSPEED, curFromSite)) {
                if (modifier.prohibitedFromMovingFromLocation(gameState, this, card)) {
                    return true;
                }
            }

            if (asReact) {
                // Check if player may not 'react' from the location
                for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REACT_FROM_LOCATION, curFromSite)) {
                    if (modifier.isForPlayer(card.getOwner())) {
                        return true;
                    }
                }
            }

            // Check if may not move from location to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_FROM_LOCATION_TO_LOCATION, curFromSite)) {
                if (modifier.prohibitedFromMovingFromLocationToLocation(gameState, this, card, curToSite)) {
                    return true;
                }
            }

            // Check if may not move from location to location using landspeed
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_FROM_LOCATION_TO_LOCATION_USING_LANDSPEED, curFromSite)) {
                if (modifier.prohibitedFromMovingFromLocationToLocation(gameState, this, card, curToSite)) {
                    return true;
                }
            }

            // Check if may not move away from location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_AWAY_FROM_LOCATION, card)) {
                if (modifier.prohibitedFromMovingAwayFromLocation(gameState, this, curFromSite, curToSite)) {
                    return true;
                }
            }

            // Check if may not move to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_TO_LOCATION, curToSite)) {
                if (modifier.prohibitedFromMovingToLocation(gameState, this, card)) {
                    return true;
                }
            }

            // Check if may not move to location using landspeed
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_TO_LOCATION_USING_LANDSPEED, curToSite)) {
                if (modifier.prohibitedFromMovingToLocation(gameState, this, card)) {
                    return true;
                }
            }

            // Check if card has (limit 1 per location)
            if (isOperativePreventedFromDeployingToOrMovingToLocation(gameState, card, curToSite)) {
                return true;
            }

            if (asReact) {
                // Check if player may not 'react' to the location
                for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REACT_TO_LOCATION, curToSite)) {
                    if (modifier.isForPlayer(card.getOwner())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the specified card is prohibited from moving using hyperspeed.
     * @param gameState the game state
     * @param card the card
     * @return true if card is prohibited from moving using hyperspeed, otherwise false
     */
    @Override
    public boolean mayNotMoveUsingHyperspeed(GameState gameState, PhysicalCard card) {
        if (mayNotMove(gameState, card))
            return true;

        return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_USING_HYPERSPEED, card).isEmpty());
    }

    /**
     * Determine if the specified card is prohibited from moving to or from specified locations.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @return true if card is prohibited from moving, otherwise false
     */
    private boolean mayNotMoveFromLocationToLocation(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean asReact) {

        if (fromLocation != null) {
            // Check if may not move from location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_FROM_LOCATION, fromLocation)) {
                if (modifier.prohibitedFromMovingFromLocation(gameState, this, card)) {
                    return true;
                }
            }

            if (asReact) {
                // Check if player may not 'react' from the location
                for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REACT_FROM_LOCATION, fromLocation)) {
                    if (modifier.isForPlayer(card.getOwner())) {
                        return true;
                    }
                }
            }
        }

        if (fromLocation != null && toLocation != null) {
            // Check if may not move from location to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_FROM_LOCATION_TO_LOCATION, fromLocation)) {
                if (modifier.prohibitedFromMovingFromLocationToLocation(gameState, this, card, toLocation)) {
                    return true;
                }
            }

            // Check if may not move away from location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_AWAY_FROM_LOCATION, card)) {
                if (modifier.prohibitedFromMovingAwayFromLocation(gameState, this, fromLocation, toLocation)) {
                    return true;
                }
            }
        }

        if (toLocation != null) {
            // Check if may not move to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_TO_LOCATION, toLocation)) {
                if (modifier.prohibitedFromMovingToLocation(gameState, this, card)) {
                    return true;
                }
            }

            // Check if card has (limit 1 per location)
            if (isOperativePreventedFromDeployingToOrMovingToLocation(gameState, card, toLocation)) {
                return true;
            }

            if (asReact) {
                // Check if player may not 'react' to the location
                for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REACT_TO_LOCATION, toLocation)) {
                    if (modifier.isForPlayer(card.getOwner())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the specified card is prohibited from moving from location to location using hyperspeed.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @return true if card is prohibited from moving, otherwise false
     */
    @Override
    public boolean mayNotMoveFromLocationToLocationUsingHyperspeed(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean asReact) {
        if (mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, asReact))
            return true;

        if (fromLocation != null) {
            // Check if may not move from location using hyperspeed
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_FROM_LOCATION_USING_HYPERSPEED, fromLocation)) {
                if (modifier.prohibitedFromMovingFromLocation(gameState, this, card)) {
                    return true;
                }
            }
        }

        if (fromLocation != null && toLocation != null) {
            // Check if may not move from location to location using hyperspeed
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_FROM_LOCATION_TO_LOCATION_USING_HYPERSPEED, fromLocation)) {
                if (modifier.prohibitedFromMovingFromLocationToLocation(gameState, this, card, toLocation)) {
                    return true;
                }
            }
        }

        if (toLocation != null) {
            // Check if may not move to location using hyperspeed
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_TO_LOCATION_USING_HYPERSPEED, toLocation)) {
                if (modifier.prohibitedFromMovingToLocation(gameState, this, card)) {
                    return true;
                }
            }

            // Check if card has (limit 1 per location)
            if (isOperativePreventedFromDeployingToOrMovingToLocation(gameState, card, toLocation)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if the specified card is prohibited from moving from location to location without using hyperspeed.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @return true if card is prohibited from moving, otherwise false
     */
    @Override
    public boolean mayNotMoveFromLocationToLocationWithoutUsingHyperspeed(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean asReact) {
        return mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, asReact);
    }

    /**
     * Determines if the specified card is prohibited from moving from location to location using sector movement.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @return true if card is prohibited from moving, otherwise false
     */
    @Override
    public boolean mayNotMoveFromLocationToLocationUsingSectorMovement(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean asReact) {
        List<PhysicalCard> sectorsBetween = getSectorsBetween(gameState, fromLocation, toLocation);

        List<PhysicalCard> locationsAlongPath = new ArrayList<PhysicalCard>();
        locationsAlongPath.add(fromLocation);
        locationsAlongPath.addAll(sectorsBetween);
        locationsAlongPath.add(toLocation);

        for (int i=0; i<locationsAlongPath.size()-1; ++i) {
            PhysicalCard curFromLocation = locationsAlongPath.get(i);
            PhysicalCard curToLocation = locationsAlongPath.get(i+1);

            // Check if not a valid location for card to move to
            if (!card.getBlueprint().getValidMoveTargetFilter(card.getOwner(), gameState.getGame(), card, false).accepts(gameState, this, curToLocation)) {
                return true;
            }

            // Check if may not move from location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_FROM_LOCATION, curFromLocation)) {
                if (modifier.prohibitedFromMovingFromLocation(gameState, this, card)) {
                    return true;
                }
            }

            if (asReact) {
                // Check if player may not 'react' from the location
                for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REACT_FROM_LOCATION, curFromLocation)) {
                    if (modifier.isForPlayer(card.getOwner())) {
                        return true;
                    }
                }
            }

            // Check if may not move from location to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_FROM_LOCATION_TO_LOCATION, curFromLocation)) {
                if (modifier.prohibitedFromMovingFromLocationToLocation(gameState, this, card, curToLocation)) {
                    return true;
                }
            }

            // Check if may not move away from location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_AWAY_FROM_LOCATION, card)) {
                if (modifier.prohibitedFromMovingAwayFromLocation(gameState, this, curFromLocation, curToLocation)) {
                    return true;
                }
            }

            // Check if may not move to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_TO_LOCATION, curToLocation)) {
                if (modifier.prohibitedFromMovingToLocation(gameState, this, card)) {
                    return true;
                }
            }

            // Check if card has (limit 1 per location)
            if (isOperativePreventedFromDeployingToOrMovingToLocation(gameState, card, curToLocation)) {
                return true;
            }

            if (asReact) {
                // Check if player may not 'react' to the location
                for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REACT_TO_LOCATION, curToLocation)) {
                    if (modifier.isForPlayer(card.getOwner())) {
                        return true;
                    }
                }
            }
        }

        // If during attempt to 'blow away' Death Star II, then starfighters may only move from a Death Star II sector toward the Death Star II system
        if (gameState.getEpicEventState() != null && gameState.getEpicEventState().getEpicEventType() == EpicEventState.Type.ATTEMPT_TO_BLOW_AWAY_DEATH_STAR_II) {
            PhysicalCard deathStarII = Filters.findFirstFromTopLocationsOnTable(gameState.getGame(), Filters.Death_Star_II_system);
            if (deathStarII == null) {
                return true;
            }
            if (!Filters.starfighter.accepts(gameState, this, card)
                    || !Filters.Death_Star_II_sector.accepts(gameState, this, fromLocation)
                    || !Filters.toward(card, deathStarII).accepts(gameState, this, toLocation)) {
                return true;
            }
        }
        // Dark side starfighters may not move to a Death Star II sector that is not toward the Death Star II system if
        // there are no Light side starfighters at Death Star II sectors.
        else if (card.getOwner().equals(gameState.getDarkPlayer())
                && Filters.starfighter.accepts(gameState, this, card)
                && Filters.Death_Star_II_sector.accepts(gameState, this, toLocation)) {

            // Check if no Light side starfighters at Death Star II sectors.
            if (!Filters.canSpot(gameState.getGame(), null, Filters.and(Filters.owner(gameState.getLightPlayer()), Filters.starfighter, Filters.at(Filters.Death_Star_II_sector)))) {
                PhysicalCard deathStarII = Filters.findFirstFromTopLocationsOnTable(gameState.getGame(), Filters.Death_Star_II_system);
                if (deathStarII == null) {
                    return true;
                }

                // Check if not moving toward Death Star II system (or not during player's move phase)
                if (!gameState.getCurrentPlayerId().equals(card.getOwner())
                        || gameState.getCurrentPhase() != Phase.MOVE
                        || !Filters.toward(card, deathStarII).accepts(gameState, this, toLocation)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Determines if the specified card is prohibited from moving from location to location using location text.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @return true if card is prohibited from moving, otherwise false
     */
    @Override
    public boolean mayNotMoveFromLocationToLocationUsingLocationText(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation) {
        return mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, false);
    }

    /**
     * Determines if the specified card is prohibited from moving from location to location using docking bay transit.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the docking bay to move from
     * @param toLocation the docking bay to move to
     * @return true if card is prohibited from moving, otherwise false
     */
    @Override
    public boolean mayNotMoveFromLocationToLocationUsingDockingBayTransit(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation) {
        if (mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, false)) {
            return true;
        }

        // Check for "Hoth Energy Shield", which Dark side cards may not transit through.
        if (card.getOwner().equals(gameState.getDarkPlayer())
                && (isLocationUnderHothEnergyShield(gameState, fromLocation)
                || isLocationUnderHothEnergyShield(gameState, toLocation))) {
            return true;
        }

        return false;
    }

    /**
     * Determines if the specified card is prohibited from landing from location to location.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @return true if card is prohibited from moving, otherwise false
     */
    @Override
    public boolean mayNotLandFromLocationToLocation(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean asReact) {
        if (mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, asReact)) {
            return true;
        }

        // Check if TIE, which can only land at docking bay (or starship site that may be landed at instead of embarking on related starship).
        if (Filters.TIE.accepts(gameState, this, card)
                && !Filters.docking_bay.accepts(gameState, this, toLocation)
                && !Filters.starshipSiteToShuttleTransferLandAndTakeOffAtForFreeInsteadOfRelatedStarship(card.getOwner()).accepts(gameState, this, card)) {
            return true;
        }

        // Check for "Hoth Energy Shield", which Dark side cards may not land under.
        if (card.getOwner().equals(gameState.getDarkPlayer())
                && isLocationUnderHothEnergyShield(gameState, toLocation)) {
            return true;
        }

        // Check for "Cave Rules". Cards may not land to Space Slug Belly if Space Slug mouth is closed.
        if (Filters.Space_Slug_Belly.accepts(gameState, this, toLocation)) {
            PhysicalCard spaceSlug = Filters.findFirstFromAllOnTable(gameState.getGame(), Filters.and(Filters.Space_Slug, Filters.at(Filters.relatedBigOne(toLocation))));
            if (spaceSlug != null && spaceSlug.isMouthClosed()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if the specified card is prohibited from taking off from location to location.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @return true if card is prohibited from moving, otherwise false
     */
    @Override
    public boolean mayNotTakeOffFromLocationToLocation(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean asReact) {
        if (mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, asReact)) {
            return true;
        }

        // Check for "Hoth Energy Shield", which Dark side cards may not take off under
        if (card.getOwner().equals(gameState.getDarkPlayer())
                && isLocationUnderHothEnergyShield(gameState, fromLocation)) {
            return true;
        }

        // Check for "Cave Rules". Cards may not take off from Space Slug Belly if Space Slug mouth is closed.
        if (Filters.Space_Slug_Belly.accepts(gameState, this, fromLocation)) {
            PhysicalCard spaceSlug = Filters.findFirstFromAllOnTable(gameState.getGame(), Filters.and(Filters.Space_Slug, Filters.at(Filters.relatedBigOne(fromLocation))));
            if (spaceSlug != null && spaceSlug.isMouthClosed()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if the specified card is prohibited from moving from location to location to start a Bombing Run.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @return true if card is prohibited from moving, otherwise false
     */
    @Override
    public boolean mayNotMoveFromLocationToLocationToStartBombingRun(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation) {
        if (mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, false)) {
            return true;
        }

        // Check for "Hoth Energy Shield", which Dark side cards may not move to make a Bombing Run
        if (card.getOwner().equals(gameState.getDarkPlayer())
                && isLocationUnderHothEnergyShield(gameState, toLocation)) {
            return true;
        }

        return false;
    }

    /**
     * Determines if the specified card is prohibited from moving from location to location to end a Bombing Run.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @return true if card is prohibited from moving, otherwise false
     */
    @Override
    public boolean mayNotMoveFromLocationToLocationToEndBombingRun(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation) {
        if (mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, false)) {
            return true;
        }

        // Check for "Hoth Energy Shield", which Dark side cards may not move from to end a Bombing Run
        if (card.getOwner().equals(gameState.getDarkPlayer())
                && isLocationUnderHothEnergyShield(gameState, fromLocation)) {
            return true;
        }

        return false;
    }

    /**
     * Determines if the specified card is prohibited from moving from location to location at start of an Attack Run.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @return true if card is prohibited from moving, otherwise false
     */
    @Override
    public boolean mayNotMoveFromLocationToLocationAtStartOfAttackRun(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation) {
        return mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, false);
    }

    /**
     * Determines if the specified card is prohibited from moving from location to location at end of an Attack Run.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @return true if card is prohibited from moving, otherwise false
     */
    @Override
    public boolean mayNotMoveFromLocationToLocationAtEndOfAttackRun(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation) {
        return mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, false);
    }

    /**
     * Determines if the specified card is prohibited from moving from starship/vehicle site to related starship/vehicle.
     * @param gameState the game state
     * @param card the card
     * @param fromSite the starship/vehicle site to move from
     * @param toStarshipOrVehicle the starship/vehicle to move to
     * @return true if card is prohibited from moving, otherwise false
     */
    @Override
    public boolean mayNotMoveFromSiteToRelatedStarshipOrVehicle(GameState gameState, PhysicalCard card, PhysicalCard fromSite, PhysicalCard toStarshipOrVehicle) {
        PhysicalCard locationOfStarshipOrVehicle = getLocationThatCardIsAt(gameState, toStarshipOrVehicle);
        return mayNotMoveFromLocationToLocation(gameState, card, fromSite, locationOfStarshipOrVehicle, false);
    }

    /**
     * Determines if the specified card is prohibited from moving from starship/vehicle site to related starship/vehicle.
     * @param gameState the game state
     * @param card the card
     * @param fromStarshipOrVehicle the starship/vehicle to move from
     * @param toSite the starship/vehicle site to move to
     * @return true if card is prohibited from moving, otherwise false
     */
    @Override
    public boolean mayNotMoveFromStarshipOrVehicleToRelatedStarshipOrVehicleSite(GameState gameState, PhysicalCard card, PhysicalCard fromStarshipOrVehicle, PhysicalCard toSite) {
        PhysicalCard locationOfStarshipOrVehicle = getLocationThatCardIsAt(gameState, fromStarshipOrVehicle);
        return mayNotMoveFromLocationToLocation(gameState, card, locationOfStarshipOrVehicle, toSite, false);
    }

    /**
     * Determines if the specified card is prohibited from entering the starship/vehicle site from a site.
     * @param gameState the game state
     * @param card the card
     * @param fromSite the site to move from
     * @param toSite the starship/vehicle site to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @return true if card is prohibited from moving, otherwise false
     */
    @Override
    public boolean mayNotEnterStarshipOrVehicleSite(GameState gameState, PhysicalCard card, PhysicalCard fromSite, PhysicalCard toSite, boolean asReact) {
        return mayNotMoveFromLocationToLocation(gameState, card, fromSite, toSite, asReact);
    }

    /**
     * Determines if the specified card is prohibited from exit the starship/vehicle site to a site.
     * @param gameState the game state
     * @param card the card
     * @param fromSite the starship/vehicle site to move from
     * @param toSite the site to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @return true if card is prohibited from moving, otherwise false
     */
    @Override
    public boolean mayNotExitStarshipOrVehicleSite(GameState gameState, PhysicalCard card, PhysicalCard fromSite, PhysicalCard toSite, boolean asReact) {
        return mayNotMoveFromLocationToLocation(gameState, card, fromSite, toSite, asReact);
    }

    /**
     * Determines if the specified card is prohibited from shuttling from location to location.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to shuttle from (or location the starship is at if shuttling from a starship)
     * @param toLocation the location to shuttle to (or location the starship is at if shuttling to a starship)
     * @return true if card is prohibited from moving, otherwise false
     */
    @Override
    public boolean mayNotShuttleFromLocationToLocation(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation) {
        if (mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, false)) {
            return true;
        }

        // Check for "Dagobah", which neither player may shuttle at
        if (Filters.Dagobah_location.accepts(gameState, this, fromLocation) || Filters.Dagobah_location.accepts(gameState, this, toLocation))
            return true;

        // Check for "Hoth Energy Shield", which Dark side cards may not shuttle under
        if (card.getOwner().equals(gameState.getDarkPlayer())
                && (isLocationUnderHothEnergyShield(gameState, fromLocation)
                || isLocationUnderHothEnergyShield(gameState, toLocation))) {
            return true;
        }

        return false;
    }

    /**
     * Determines if the specified card is prohibited from embarking from location to location.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @return true if card is prohibited from moving, otherwise false
     */
    @Override
    public boolean mayNotEmbarkFromLocationToLocation(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation) {
        return mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, false);
    }

    /**
     * Determines if the specified card is prohibited from disembarking from location to location.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @return true if card is prohibited from moving, otherwise false
     */
    @Override
    public boolean mayNotDisembarkFromLocationToLocation(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation) {
        return mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, false);
    }

    /**
     * Determines if the specified card is prohibited from relocating from location to location.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to relocate from
     * @param toLocation the location to relocate to
     * @param allowDagobah true if relocating from/to Dagobah locations is allowed, otherwise false
     * @return true if card is prohibited from moving, otherwise false
     */
    @Override
    public boolean mayNotRelocateFromLocationToLocation(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean allowDagobah) {
        if (mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, false)) {
            return true;
        }

        // Check for "Dagobah", which neither player may relocate from/to unless explicitly allowed
        if (!allowDagobah) {
            if (Filters.Dagobah_location.accepts(gameState, this, fromLocation) || Filters.Dagobah_location.accepts(gameState, this, toLocation))
                return true;
        }

        // Check for "Hoth Energy Shield", which Dark side cards may not relocate under
        if (card.getOwner().equals(gameState.getDarkPlayer())
                && (isLocationUnderHothEnergyShield(gameState, fromLocation)
                || isLocationUnderHothEnergyShield(gameState, toLocation))) {
            return true;
        }

        // Check if may only move using landspeed
        if (mayOnlyMoveUsingLandspeed(gameState, card)) {
            return true;
        }

        return false;
    }

    /**
     * Determines if the specified card is moved by opponent instead of owner.
     * @param gameState the game state
     * @param card the card
     * @return true if card is moved by opponent instead of owner
     */
    @Override
    public boolean isMovedOnlyByOpponent(GameState gameState, PhysicalCard card) {
        return !getModifiersAffectingCard(gameState, ModifierType.MOVED_ONLY_BY_OPPONENT, card).isEmpty();
    }

    /**
     * Determines if the specified card moves using landspeed only during deploy phase.
     * @param gameState the game state
     * @param card the card
     * @return true if card is moved by opponent instead of owner
     */
    @Override
    public boolean isMovesUsingLandspeedOnlyDuringDeployPhase(GameState gameState, PhysicalCard card) {
        return !getModifiersAffectingCard(gameState, ModifierType.MOVES_USING_LANDSPEED_ONLY_DURING_DEPLOY_PHASE, card).isEmpty();
    }

    /**
     * Determines if the specified card is a location the player may shuttle, transfer, land, and take off at for
     * free instead of the related starship. (Example: Star Destroyer: Launch Bay)
     * @param gameState the game state
     * @param playerId the player
     * @param location the card
     * @return true if card is prohibited from moving, otherwise false
     */
    @Override
    public boolean mayShuttleTransferLandAndTakeOffForFreeAtInsteadOfRelatedStarship(GameState gameState, String playerId, PhysicalCard location) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_USE_LOCATION_TO_SHUTTLE_TRANSFER_LAND_OR_TAKE_OFF_FOR_FREE_INSTEAD_OF_RELATED_STARSHIP, location)) {
            if (modifier.isForPlayer(playerId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the specified card can shuttle directly from the specified location to the other specified location.
     * @param gameState the game state
     * @param cardToMove the card to move
     * @param fromLocation the location to shuttle from
     * @param toLocation the location to shuttle to
     * @return true if card is prohibited from moving, otherwise false
     */
    @Override
    public boolean mayShuttleDirectlyFromLocationToLocation(GameState gameState, PhysicalCard cardToMove, PhysicalCard fromLocation, PhysicalCard toLocation) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_SHUTTLE_DIRECTLY_FROM_LOCATION_TO_LOCATION, cardToMove)) {
            if (modifier.isGrantedToShuttleFromLocationToLocation(gameState, this, fromLocation, toLocation)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean cannotDriveOrPilot(GameState gameState, PhysicalCard card) {
        if (card.getBlueprint().getCardCategory() != CardCategory.CHARACTER)
            return false;

        return !getModifiersAffectingCard(gameState, ModifierType.CANT_DRIVE_OR_PILOT, card).isEmpty();
    }

    @Override
    public boolean cannotSatisfyAttrition(GameState gameState, PhysicalCard card) {
        return !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_SATISFY_ATTRITION, card).isEmpty();
    }

    /**
     * Determines if the specified card is not allowed to steal other cards.
     * @param gameState the game state
     * @param card the card
     * @return true if card is not allowed to steal other cards, otherwise false
     */
    @Override
    public boolean cannotSteal(GameState gameState, PhysicalCard card) {
        return !getModifiersAffectingCard(gameState, ModifierType.CANT_STEAL, card).isEmpty();
    }

    @Override
    public boolean isForfeitedToUsedPile(GameState gameState, PhysicalCard card) {
        return !getModifiersAffectingCard(gameState, ModifierType.FORFEITED_TO_USED_PILE, card).isEmpty();
    }

    /**
     * Gets the cards (if any) that indicate that the specified card's game text is supposed to be canceled.
     * @param gameState the game state
     * @param card the card
     * @return true if supposed to be canceled, otherwise false
     */
    @Override
    public Collection<PhysicalCard> getCardsMarkingGameTextCanceled(GameState gameState, PhysicalCard card) {
        return getCardsMarkingGameTextCanceled(gameState, card, new ModifierCollectorImpl());
    }

    /**
     * Gets the cards (if any) that indicate that the specified card's game text is supposed to be canceled.
     * @param gameState the game state
     * @param card the card
     * @param modifierCollector collector of affecting modifiers
     * @return true if supposed to be canceled, otherwise false
     */
    @Override
    public Collection<PhysicalCard> getCardsMarkingGameTextCanceled(GameState gameState, PhysicalCard card, ModifierCollector modifierCollector) {
        Set<PhysicalCard> cards = new HashSet<PhysicalCard>();
        if (!isProhibitedFromHavingGameTextCanceled(gameState, card)) {
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.CANCEL_GAME_TEXT, card)) {
                cards.add(modifier.getSource(gameState));
                modifierCollector.addModifier(modifier);
            }
        }
        return cards;
    }

    /**
     * Determines if the specified card's game text is canceled.
     * @param gameState the game state
     * @param card the card
     * @return true if canceled, otherwise false
     */
    @Override
    public boolean isGameTextCanceled(GameState gameState, PhysicalCard card) {
        return isGameTextCanceled(gameState, card, false, false);
    }

    /**
     * Determines if the specified card's game text is canceled.
     * @param gameState the game state
     * @param card the card
     * @param allowIfOnlyLanded true if game text is still allowed to be enabled if the card is landed (but still piloted for takeoff)
     * @param skipUnpilotedCheck true if game text is still allowed to be enabled if the card is unpiloted
     * @return true if canceled, otherwise false
     */
    @Override
    public boolean isGameTextCanceled(GameState gameState, PhysicalCard card, boolean allowIfOnlyLanded, boolean skipUnpilotedCheck) {
        if (card.isDejarikHologramAtHolosite())
            return true;

        if (card.isGameTextCanceled() || card.isBlownAway() || card.isCollapsed())
            return true;

        CardCategory cardCategory = card.getBlueprint().getCardCategory();

        if (!skipUnpilotedCheck) {
            // Check if starship or vehicle is in play but is not "piloted" (unless landed is allowed via parameter)
            if (cardCategory == CardCategory.STARSHIP || cardCategory == CardCategory.VEHICLE) {
                if (Filters.onTable.accepts(gameState, this, card) && !isPiloted(gameState, card, allowIfOnlyLanded)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Gets the cards (if any) that indicate that the specified location's game text is supposed to be canceled on the side
     * facing the specified player.
     * @param gameState the game state
     * @param card the card
     * @param playerId the player
     * @return true if canceled, otherwise false
     */
    @Override
    public Collection<PhysicalCard> getCardsMarkingGameTextCanceledForPlayer(GameState gameState, PhysicalCard card, String playerId) {
        return getCardsMarkingGameTextCanceledForPlayer(gameState, card, playerId, new ModifierCollectorImpl());
    }

    /**
     * Gets the cards (if any) that indicate that the specified location's game text is supposed to be canceled on the side
     * facing the specified player.
     * @param gameState the game state
     * @param card the card
     * @param playerId the player
     * @param modifierCollector collector of affecting modifiers
     * @return true if canceled, otherwise false
     */
    @Override
    public Collection<PhysicalCard> getCardsMarkingGameTextCanceledForPlayer(GameState gameState, PhysicalCard card, String playerId, ModifierCollector modifierCollector) {
        Set<PhysicalCard> cards = new HashSet<PhysicalCard>();

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.CANCEL_LOCATION_GAME_TEXT_FOR_PLAYER, card)) {
            if (modifier.isCanceledTextForPlayer(playerId)) {
                cards.add(modifier.getSource(gameState));
                modifierCollector.addModifier(modifier);
            }
        }
        return cards;
    }

    /**
     * Determines if the specified location's game text is canceled on the side facing the specified player.
     * @param gameState the game state
     * @param card the card
     * @param playerId the player
     * @return true if canceled, otherwise false
     */
    @Override
    public boolean isLocationGameTextCanceledForPlayer(GameState gameState, PhysicalCard card, String playerId) {
        if (card.getBlueprint().getCardCategory() != CardCategory.LOCATION)
            return false;

        if (card.isGameTextCanceled() || card.isLocationGameTextCanceledForPlayer(playerId) || card.isBlownAway() || card.isCollapsed())
            return true;

        return false;
    }

    /**
     * Gets the cards (if any) that indicate that the specified card is supposed to be suspended.
     * @param gameState the game state
     * @param card the card
     * @return true if supposed to be suspended, otherwise false
     */
    @Override
    public Collection<PhysicalCard> getCardsMarkingCardSuspended(GameState gameState, PhysicalCard card) {
        return getCardsMarkingCardSuspended(gameState, card, new ModifierCollectorImpl());
    }

    /**
     * Gets the cards (if any) that indicate that the specified card is supposed to be suspended.
     * @param gameState the game state
     * @param card the card
     * @param modifierCollector collector of affecting modifiers
     * @return true if supposed to be suspended, otherwise false
     */
    @Override
    public Collection<PhysicalCard> getCardsMarkingCardSuspended(GameState gameState, PhysicalCard card, ModifierCollector modifierCollector) {
        Set<PhysicalCard> cards = new HashSet<PhysicalCard>();

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.SUSPEND_CARD, card)) {
            cards.add(modifier.getSource(gameState));
            modifierCollector.addModifier(modifier);
        }
        return cards;
    }

    /**
     * Determines if a card is a player's highest ability character on the the table. If multiple characters have the
     * highest ability, then all of them are considered to be a highest ability character.
     *
     * @param gameState the game state
     * @param source the card that is performing this query
     * @param card a card
     * @return true if the card is the owning player's highest ability character on the table, otherwise false
     */
    @Override
    public boolean isPlayersHighestAbilityCharacter(GameState gameState, PhysicalCard source, PhysicalCard card, String playerId) {
        if (card.getBlueprint().getCardCategory() != CardCategory.CHARACTER
                || !card.getOwner().equals(playerId))
            return false;

        if (excludedFromBeingHighestAbilityCharacter(gameState, source, card))
            return false;

        float ability = getAbility(gameState, card);
        if (ability == 0)
            return false;

        return !Filters.canSpot(gameState.getGame(), source,
                Filters.and(Filters.owner(playerId), CardCategory.CHARACTER, Filters.abilityMoreThan(ability), Filters.notExcludedFromBeingHighestAbilityCharacter(source)));
    }

    /**
     * Determines if the specified card is excluded from being the highest-ability character from the perspective of the
     * card performing the query.
     * @param gameState the game state
     * @param cardPerformingQuery the card performing the query
     * @param card the card
     * @return true if not allowed, otherwise false
     */
    @Override
    public boolean excludedFromBeingHighestAbilityCharacter(GameState gameState, PhysicalCard cardPerformingQuery, PhysicalCard card) {
        if (card.getBlueprint().getCardCategory() != CardCategory.CHARACTER)
            return false;

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_HIGHEST_ABILITY_CHARACTER, card)) {
            if (modifier.isAffectedTarget(gameState, this, cardPerformingQuery)) {
                return true;
            };
        }

        return false;
    }

    @Override
    public Uniqueness getUniqueness(GameState gameState, PhysicalCard card) {
        if (!getModifiersAffectingCard(gameState, ModifierType.NOT_UNIQUE, card).isEmpty())
            return null;

        if (!getModifiersAffectingCard(gameState, ModifierType.UNIQUE, card).isEmpty())
            return Uniqueness.UNIQUE;

        return card.getBlueprint().getUniqueness();
    }

    @Override
    public CardSubtype getInterruptType(GameState gameState, PhysicalCard card) {
        if (!getModifiersAffectingCard(gameState, ModifierType.LOST_INTERRUPT, card).isEmpty())
            return CardSubtype.LOST;

        if (!getModifiersAffectingCard(gameState, ModifierType.USED_INTERRUPT, card).isEmpty())
            return CardSubtype.USED;

        return card.getBlueprint().getCardSubtype();
    }

    @Override
    public String getPlayerToChooseCardTargetAtLocation(GameState gameState, PhysicalCard card, PhysicalCard location, String defaultPlayerId) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PLAYER_TO_SELECT_CARD_TARGET_AT_LOCATION, card)) {
            String playerId = modifier.getPlayerToSelectCardTargetAtLocation(gameState, this, location);
            if (playerId!=null)
                return playerId;
        }

        return defaultPlayerId;
    }

    /**
     * Gets the value of a race destiny.
     * @param gameState the game state
     * @param physicalCard the race destiny card
     * @return the race destiny value
     */
    @Override
    public float getRaceDestiny(GameState gameState, PhysicalCard physicalCard) {
        return getRaceDestiny(gameState, physicalCard, new ModifierCollectorImpl());
    }

    /**
     * Gets the value of a race destiny.
     * @param gameState the game state
     * @param physicalCard the race destiny card
     * @param modifierCollector collector of affecting modifiers
     * @return the race destiny value
     */
    @Override
    public float getRaceDestiny(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
        Float result = physicalCard.getDestinyValueToUse();

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DESTINY, physicalCard)) {
            result = modifier.getPrintedValueDefinedByGameText(gameState, this, physicalCard);
        }
        // If value if undefined, then return 0
        if (result == null)
            return 0;

        // If card is a character and it is "doubled", then double the printed number
        if (physicalCard.getBlueprint().getCardCategory()==CardCategory.CHARACTER
                && isDoubled(gameState, physicalCard, modifierCollector)) {
            result *= 2;
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY, physicalCard)) {
            result += modifier.getDestinyModifier(gameState, this, physicalCard);
            modifierCollector.addModifier(modifier);
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.RACE_DESTINY, physicalCard)) {
            result += modifier.getValue(gameState, this, physicalCard);
            modifierCollector.addModifier(modifier);
        }

        return result;
    }

    /**
     * Gets the race total for the specified player.
     * @param gameState the game state
     * @param playerId the player
     * @return the race total
     */
    @Override
    public float getHighestRaceTotal(GameState gameState, String playerId) {
        SwccgGame game = gameState.getGame();
        float result = 0;

        // Check for race destinies on Podracer Arena
        PhysicalCard podracerArena = Filters.findFirstFromTopLocationsOnTable(game, Filters.Podrace_Arena);
        if (podracerArena != null) {
            Collection<PhysicalCard> raceDestinies = Filters.filterStacked(gameState.getGame(), Filters.and(Filters.raceDestinyForPlayer(playerId), Filters.stackedOn(podracerArena)));
            for (PhysicalCard raceDestiny : raceDestinies) {
                result += getRaceDestiny(gameState, raceDestiny);
            }
        }

        // Check for race destinies on Podracers
        Collection<PhysicalCard> podracers = Filters.filterActive(game, null, Filters.and(Filters.owner(playerId), Filters.Podracer));
        for (PhysicalCard podracer : podracers) {
            float podracerRaceTotal = getPodracerRaceTotal(gameState, podracer);
            result = Math.max(result, podracerRaceTotal);
        }

        return Math.max(0, result);
    }

    /**
     * Gets the race total for the specified Podracer.
     * @param gameState the game state
     * @param podracer the Podracer
     * @return the race total
     */
    @Override
    public float getPodracerRaceTotal(GameState gameState, PhysicalCard podracer) {
        SwccgGame game = gameState.getGame();
        String playerId = podracer.getOwner();
        float result = 0;

        Collection<PhysicalCard> raceDestinies = Filters.filterStacked(game, Filters.and(Filters.raceDestinyForPlayer(playerId), Filters.stackedOn(podracer)));
        for (PhysicalCard raceDestiny : raceDestinies) {
            result += getRaceDestiny(gameState, raceDestiny);
        }

        return Math.max(0, result);
    }

    /**
     * Gets the Podracer cards that are leading the Podrace.
     * @param gameState the game state
     * @return the Podracer cards leading the Podrace
     */
    @Override
    public Collection<PhysicalCard> getPodracersLeadingPodrace(GameState gameState) {
        SwccgGame game = gameState.getGame();
        List<PhysicalCard> leadingPodracers = new ArrayList<>();

        float leadingRaceTotal = Math.max(getHighestRaceTotal(gameState, gameState.getDarkPlayer()), getHighestRaceTotal(gameState, gameState.getLightPlayer()));

        Collection<PhysicalCard> podracers = Filters.filterActive(game, null, Filters.Podracer);
        for (PhysicalCard podracer : podracers) {
            if (getPodracerRaceTotal(gameState, podracer) >= leadingRaceTotal) {
                leadingPodracers.add(podracer);
            }
        }

        return leadingPodracers;
    }

    /**
     * Gets the Podracer cards that are behind in the Podrace.
     * @param gameState the game state
     * @return the Podracer cards behind in the Podrace
     */
    @Override
    public Collection<PhysicalCard> getPodracersBehindInPodrace(GameState gameState) {
        SwccgGame game = gameState.getGame();
        List<PhysicalCard> podracersBehind = new ArrayList<>();

        float leadingRaceTotal = Math.max(getHighestRaceTotal(gameState, gameState.getDarkPlayer()), getHighestRaceTotal(gameState, gameState.getLightPlayer()));

        Collection<PhysicalCard> podracers = Filters.filterActive(game, null, Filters.Podracer);
        for (PhysicalCard podracer : podracers) {
            if (getPodracerRaceTotal(gameState, podracer) < leadingRaceTotal) {
                podracersBehind.add(podracer);
            }
        }

        return podracersBehind;
    }

    @Override
    public CardState getCardState(GameState gameState, PhysicalCard physicalCard, boolean includeExcludedFromBattle, boolean includeUndercover, boolean includeCaptives,
                                  boolean includeConcealed, boolean includeWeaponsForStealing, boolean includeMissing, boolean includeBinaryOff, boolean includeSuspended) {
        Zone zone = GameUtils.getZoneFromZoneTop(physicalCard.getZone());
        CardCategory cardCategory = physicalCard.getBlueprint().getCardCategory();
        CardSubtype cardSubtype = physicalCard.getBlueprint().getCardSubtype();

        if (zone==Zone.OUT_OF_PLAY)
            return CardState.OUT_OF_PLAY;

        if (zone.isLifeForce() || zone==Zone.HAND || zone==Zone.LOST_PILE)
            return CardState.UNIT_OF_FORCE;

        if (zone==Zone.STACKED_FACE_DOWN)
            return CardState.SUPPORTING;

        if (zone==Zone.STACKED) {
            if (!physicalCard.isStackedAsInactive()
                    || (physicalCard.getStackedOn() != null && Filters.grabber.accepts(gameState, this, physicalCard.getStackedOn()))) {
                return CardState.SUPPORTING;
            }
            return CardState.INACTIVE;
        }

        if (zone==Zone.CONVERTED_LOCATIONS)
            return CardState.INACTIVE;

        if (!includeSuspended && physicalCard.isSuspended())
            return CardState.INACTIVE;

        if (!includeBinaryOff && physicalCard.isBinaryOff())
            return CardState.INACTIVE;

        if (!includeMissing && physicalCard.isMissing())
            return CardState.INACTIVE;

        if (!includeCaptives && (physicalCard.isCaptive() || physicalCard.isCapturedStarship()))
            return CardState.INACTIVE;

        if (!includeExcludedFromBattle && zone.isInPlay() && gameState.isDuringBattle() && isExcludedFromBattle(gameState, physicalCard))
            return CardState.INACTIVE;

        if (!includeConcealed && physicalCard.isConcealed())
            return CardState.INACTIVE;

        if (!includeUndercover && physicalCard.isUndercover())
            return CardState.INACTIVE;

        if (!includeWeaponsForStealing) {
            if (cardCategory == CardCategory.WEAPON && cardSubtype == CardSubtype.CHARACTER && physicalCard.getAttachedTo() != null
                    && !physicalCard.getBlueprint().getValidToUseWeaponFilter(physicalCard.getOwner(), gameState.getGame(), physicalCard).accepts(gameState, this, physicalCard.getAttachedTo()))
                return CardState.INACTIVE;

            if (cardCategory == CardCategory.DEVICE && cardSubtype == CardSubtype.CHARACTER && physicalCard.getAttachedTo() != null
                    && !physicalCard.getBlueprint().getValidToUseDeviceFilter(physicalCard.getOwner(), gameState.getGame(), physicalCard).accepts(gameState, this, physicalCard.getAttachedTo()))
                return CardState.INACTIVE;
        }

        if (physicalCard.getAttachedTo() != null) {
            return getCardState(gameState, physicalCard.getAttachedTo(), includeExcludedFromBattle, true, includeCaptives, includeConcealed, includeWeaponsForStealing, includeMissing, includeBinaryOff, includeSuspended);
        }

        if (zone.isInPlay() && !physicalCard.getBlueprint().isInactiveInsteadOfActive(gameState.getGame(), physicalCard))
            return CardState.ACTIVE;

        return CardState.INACTIVE;
    }

    /**
     * Gets actions that the specified Interrupt is currently able to perform to initiate an epic duel.
     *
     * @param gameState the game state
     * @param card a card
     * @return true or false
     */
    @Override
    public List<PlayInterruptAction> getInitiateEpicDuelActions(final GameState gameState, final PhysicalCard card) {
        List<PlayInterruptAction> actionList = new ArrayList<PlayInterruptAction>();

        // Check if the card is granted the ability to initiate an epic duel
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_PLAY_TO_INITIATE_EPIC_DUEL, card)) {

            // Get valid participants
            Filter validDarkSideParticipantFilter = getValidDuelParticipant(gameState, card, Side.DARK);
            Filter validLightSideParticipantFilter = getValidDuelParticipant(gameState, card, Side.LIGHT);
            final PhysicalCard source = modifier.getSource(gameState);

            // Determine if the epic duel can be initiated with the allowed participants
            final Map<PhysicalCard, Collection<PhysicalCard>> duelMatchups = source.getBlueprint().getInitiateEpicDuelMatchup(gameState.getGame(), source, validDarkSideParticipantFilter, validLightSideParticipantFilter);
            if (!duelMatchups.isEmpty()) {

                final PlayInterruptAction action = new PlayInterruptAction(gameState.getGame(), card);
                action.setText("Initiate epic duel with " + GameUtils.getFullName(source));
                action.appendUsage(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                game.getGameState().activatedCard(card.getOwner(), source);
                            }
                        }
                );

                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, action.getPerformingPlayer(), "Choose Dark Side character", TargetingReason.TO_BE_DUELED, Filters.in(duelMatchups.keySet())) {
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard darkSideCharacter) {
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, action.getPerformingPlayer(), "Choose Light Side character", TargetingReason.TO_BE_DUELED, Filters.in(duelMatchups.get(darkSideCharacter))) {
                                            @Override
                                            protected void cardTargeted(final int targetGroupId2, PhysicalCard lightSideCharacter) {
                                                // Allow response(s)
                                                action.allowResponses("Initiate epic duel between " + GameUtils.getCardLink(darkSideCharacter) + " and " + GameUtils.getCardLink(lightSideCharacter),
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                                // This needs to be done in case the target(s) were changed during the responses.
                                                                PhysicalCard darkCharacter = targetingAction.getPrimaryTargetCard(targetGroupId1);
                                                                PhysicalCard lightCharacter = targetingAction.getPrimaryTargetCard(targetGroupId2);

                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new DuelEffect(action, darkCharacter, lightCharacter, source.getBlueprint().getDuelDirections(gameState.getGame())));
                                                            }
                                                        });
                                            }
                                        }
                                );
                            }
                        }
                );
                actionList.add(action);
            }
        }

        return actionList;
    }

    /**
     * Gets a filter that accepts cards that can be participants for the specified side of the Force in a duel initiated
     * by the specified card.
     *
     * @param gameState the game state
     * @param card the card initiating the duel
     * @param side the side of the Force of the participant
     * @return the filter
     */
    @Override
    public Filter getValidDuelParticipant(GameState gameState, PhysicalCard card, Side side) {
        // Gets filter for cards that are valid dark side participants for a duel initiated by the specified card
        return Filters.and(Filters.character, card.getBlueprint().getValidDuelParticipant(side, gameState.getGame(), card));
    }

    /**
     * Gets the value of a drawn epic event destiny.
     * @param gameState the game state
     * @param physicalCard the card drawn for epic event destiny
     * @param playerId the player drawing epic event destiny
     * @return the epic event destiny value
     */
    @Override
    public float getEpicEventDestiny(GameState gameState, PhysicalCard physicalCard, String playerId) {
        Float result = physicalCard.getDestinyValueToUse();

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DESTINY, physicalCard)) {
            result = modifier.getPrintedValueDefinedByGameText(gameState, this, physicalCard);
        }
        // If value if undefined, then return 0
        if (result == null)
            return 0;

        // If card is a character and it is "doubled", then double the printed number
        if (physicalCard.getBlueprint().getCardCategory()==CardCategory.CHARACTER
                && isDoubled(gameState, physicalCard)) {
            result *= 2;
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY, physicalCard)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getDestinyModifier(gameState, this, physicalCard);
            }
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_WHEN_DRAWN_FOR_DESTINY, physicalCard)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getDestinyWhenDrawnForDestinyModifier(gameState, this, physicalCard);
            }
        }
        for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
            if (modifier.isForTopDrawDestinyEffect(gameState)) {
                if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    result += modifier.getValue(gameState, this, (PhysicalCard) null);
                }
            }
        }
        // Get from EpicEventState
        EpicEventState epicEventState = gameState.getEpicEventState();
        if (epicEventState != null) {
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EACH_EPIC_EVENT_DESTINY_DRAW, epicEventState.getEpicEvent())) {
                if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    result += modifier.getEpicEventDestinyModifier(playerId, gameState, this, epicEventState.getEpicEvent());
                }
            }
        }

        return result;
    }

    /**
     * Gets the value of a drawn epic event and weapon destiny.
     * @param gameState the game state
     * @param physicalCard the card drawn for epic event and weapon destiny
     * @param playerId the player drawing epic event and weapon destiny
     * @return the epic event and weapon destiny value
     */
    @Override
    public float getEpicEventAndWeaponDestiny(GameState gameState, PhysicalCard physicalCard, String playerId) {
        Float result = physicalCard.getDestinyValueToUse();

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DESTINY, physicalCard)) {
            result = modifier.getPrintedValueDefinedByGameText(gameState, this, physicalCard);
        }
        // If value if undefined, then return 0
        if (result == null)
            return 0;

        // If card is a character and it is "doubled", then double the printed number
        if (physicalCard.getBlueprint().getCardCategory()==CardCategory.CHARACTER
                && isDoubled(gameState, physicalCard)) {
            result *= 2;
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY, physicalCard)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getDestinyModifier(gameState, this, physicalCard);
            }
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_WHEN_DRAWN_FOR_DESTINY, physicalCard)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getDestinyWhenDrawnForDestinyModifier(gameState, this, physicalCard);
            }
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_WHEN_DRAWN_FOR_WEAPON_DESTINY, physicalCard)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getDestinyWhenDrawnForDestinyModifier(gameState, this, physicalCard);
            }
        }
        for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
            if (modifier.isForTopDrawDestinyEffect(gameState)) {
                if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    result += modifier.getValue(gameState, this, (PhysicalCard) null);
                }
            }
        }
        // Get from EpicEventState
        EpicEventState epicEventState = gameState.getEpicEventState();
        if (epicEventState != null) {
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EACH_EPIC_EVENT_DESTINY_DRAW, epicEventState.getEpicEvent())) {
                if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    result += modifier.getEpicEventDestinyModifier(playerId, gameState, this, epicEventState.getEpicEvent());
                }
            }
        }

        // Get from WeaponFiringState
        WeaponFiringState weaponFiringState = gameState.getWeaponFiringState();
        if (weaponFiringState != null) {
            PhysicalCard weapon = weaponFiringState.getCardFiring();
            SwccgBuiltInCardBlueprint permanentWeapon = weaponFiringState.getPermanentWeaponFiring();
            Collection<PhysicalCard> weaponTargets = weaponFiringState.getTargets();
            PhysicalCard cardFiringWeapon = weaponFiringState.getCardFiringWeapon();

            for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_WEAPON_DESTINY)) {
                if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    result += modifier.getWeaponDestinyModifier(gameState, this, cardFiringWeapon, weapon, permanentWeapon, weaponTargets);
                }
            }
        }

        return result;
    }

    /**
     * Gets the value of a drawn weapon destiny.
     * @param gameState the game state
     * @param physicalCard the card drawn for weapon destiny
     * @param playerId the player drawing weapon destiny
     * @return the weapon destiny value
     */
    @Override
    public float getWeaponDestiny(GameState gameState, PhysicalCard physicalCard, String playerId) {
        Float result = physicalCard.getDestinyValueToUse();

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DESTINY, physicalCard)) {
            result = modifier.getPrintedValueDefinedByGameText(gameState, this, physicalCard);
        }
        // If value if undefined, then return 0
        if (result == null)
            return 0;

        // If card is a character and it is "doubled", then double the printed number
        if (physicalCard.getBlueprint().getCardCategory()==CardCategory.CHARACTER
                && isDoubled(gameState, physicalCard)) {
            result *= 2;
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY, physicalCard)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getDestinyModifier(gameState, this, physicalCard);
            }
        }
        for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
            if (modifier.isForTopDrawDestinyEffect(gameState)) {
                if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    result += modifier.getValue(gameState, this, (PhysicalCard) null);
                }
            }
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_WHEN_DRAWN_FOR_DESTINY, physicalCard)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getDestinyWhenDrawnForDestinyModifier(gameState, this, physicalCard);
            }
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_WHEN_DRAWN_FOR_WEAPON_DESTINY, physicalCard)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getDestinyWhenDrawnForDestinyModifier(gameState, this, physicalCard);
            }
        }
        for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
            if (modifier.isForTopDrawDestinyEffect(gameState)) {
                if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    result += modifier.getValue(gameState, this, (PhysicalCard) null);
                }
            }
        }

        // Get from WeaponFiringState
        WeaponFiringState weaponFiringState = gameState.getWeaponFiringState();
        if (weaponFiringState != null) {
            PhysicalCard weapon = weaponFiringState.getCardFiring();
            SwccgBuiltInCardBlueprint permanentWeapon = weaponFiringState.getPermanentWeaponFiring();
            Collection<PhysicalCard> weaponTargets = weaponFiringState.getTargets();
            PhysicalCard cardFiringWeapon = weaponFiringState.getCardFiringWeapon();

            for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_WEAPON_DESTINY)) {
                if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    result += modifier.getWeaponDestinyModifier(gameState, this, cardFiringWeapon, weapon, permanentWeapon, weaponTargets);
                }
            }
        }

        return result;
    }

    /**
     * Gets the value of a drawn destiny to power.
     * @param gameState the game state
     * @param physicalCard the card drawn for destiny to power
     * @param playerId the player drawing destiny to power
     * @return the destiny to power value
     */
    @Override
    public float getDestinyToPower(GameState gameState, PhysicalCard physicalCard, String playerId) {
        Float result = physicalCard.getDestinyValueToUse();

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DESTINY, physicalCard)) {
            result = modifier.getPrintedValueDefinedByGameText(gameState, this, physicalCard);
        }
        // If value if undefined, then return 0
        if (result == null)
            return 0;

        // If card is a character and it is "doubled", then double the printed number
        if (physicalCard.getBlueprint().getCardCategory()==CardCategory.CHARACTER
                && isDoubled(gameState, physicalCard)) {
            result *= 2;
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY, physicalCard)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getDestinyModifier(gameState, this, physicalCard);
            }
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_WHEN_DRAWN_FOR_DESTINY, physicalCard)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getDestinyWhenDrawnForDestinyModifier(gameState, this, physicalCard);
            }
        }
        for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
            if (modifier.isForTopDrawDestinyEffect(gameState)) {
                if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    result += modifier.getValue(gameState, this, (PhysicalCard) null);
                }
            }
        }

        return result;
    }

    /**
     * Gets the value of a drawn battle destiny.
     * @param gameState the game state
     * @param physicalCard the card drawn for battle destiny
     * @param playerId the player drawing battle destiny
     * @return the battle destiny value
     */
    @Override
    public float getBattleDestiny(GameState gameState, PhysicalCard physicalCard, String playerId) {
        PhysicalCard battleLocation = gameState.getBattleState().getBattleLocation();
        Float result = physicalCard.getDestinyValueToUse();

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DESTINY, physicalCard)) {
            result = modifier.getPrintedValueDefinedByGameText(gameState, this, physicalCard);
        }
        // If value if undefined, then return 0
        if (result == null)
            return 0;

        // If card is a character and it is "doubled", then double the printed number
        if (physicalCard.getBlueprint().getCardCategory()==CardCategory.CHARACTER
                && isDoubled(gameState, physicalCard)) {
            result *= 2;
        }

        // Check if player's battle destiny modifiers affect total battle destiny instead
        boolean affectTotalBdInstead = hasFlagActive(gameState, ModifierFlag.BATTLE_DESTINY_MODIFIERS_AFFECT_TOTAL_BATTLE_DESTINY_INSTEAD, playerId);

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY, physicalCard)) {
            if (!affectTotalBdInstead || modifier.getSource(gameState) == null || !playerId.equals(modifier.getSource(gameState).getOwner())) {
                if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    result += modifier.getDestinyModifier(gameState, this, physicalCard);
                }
            }
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_WHEN_DRAWN_FOR_DESTINY, physicalCard)) {
            if (!affectTotalBdInstead || modifier.getSource(gameState) == null || !playerId.equals(modifier.getSource(gameState).getOwner())) {
                if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    result += modifier.getDestinyWhenDrawnForDestinyModifier(gameState, this, physicalCard);
                }
            }
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_WHEN_DRAWN_FOR_BATTLE_DESTINY, physicalCard)) {
            if (!affectTotalBdInstead || modifier.getSource(gameState) == null || !playerId.equals(modifier.getSource(gameState).getOwner())) {
                if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    result += modifier.getDestinyWhenDrawnForDestinyModifier(gameState, this, physicalCard);
                }
            }
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EACH_BATTLE_DESTINY_AT_LOCATION, battleLocation)) {
            if (!affectTotalBdInstead || modifier.getSource(gameState) == null || !playerId.equals(modifier.getSource(gameState).getOwner())) {
                if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    result += modifier.getBattleDestinyAtLocationModifier(playerId, gameState, this, battleLocation);
                }
            }
        }
        for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
            if (modifier.isForTopDrawDestinyEffect(gameState)) {
                if (!affectTotalBdInstead || modifier.getSource(gameState) == null || !playerId.equals(modifier.getSource(gameState).getOwner())) {
                    if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                        result += modifier.getValue(gameState, this, (PhysicalCard) null);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Gets the player's battle destiny modifiers and the modifier amount for the specified card drawn for battle destiny,
     * even if the battle destiny cannot be modified. This is used when having the battle destiny modifiers affect total
     * battle destiny instead.
     * @param gameState the game state
     * @param physicalCard the card drawn for battle destiny
     * @param playerId the player drawing battle destiny
     * @return the list of source card to modifier amount
     */
    public List<Map<PhysicalCard, Float>> getPlayersBattleDestinyModifiersToApplyToTotalBattleDestiny(GameState gameState, PhysicalCard physicalCard, String playerId) {
        List<Map<PhysicalCard, Float>> list = new LinkedList<Map<PhysicalCard, Float>>();

        // Check if player's battle destiny modifiers affect total battle destiny instead
        if (hasFlagActive(gameState, ModifierFlag.BATTLE_DESTINY_MODIFIERS_AFFECT_TOTAL_BATTLE_DESTINY_INSTEAD, playerId)) {

            PhysicalCard battleLocation = gameState.getBattleState().getBattleLocation();

            if (physicalCard != null) {
                for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY, physicalCard)) {
                    if (modifier.getSource(gameState) != null && playerId.equals(modifier.getSource(gameState).getOwner())) {
                        list.add(Collections.singletonMap(modifier.getSource(gameState), modifier.getDestinyModifier(gameState, this, physicalCard)));
                    }
                }
                for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_WHEN_DRAWN_FOR_DESTINY, physicalCard)) {
                    if (modifier.getSource(gameState) != null && playerId.equals(modifier.getSource(gameState).getOwner())) {
                        list.add(Collections.singletonMap(modifier.getSource(gameState), modifier.getDestinyWhenDrawnForDestinyModifier(gameState, this, physicalCard)));
                    }
                }
                for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_WHEN_DRAWN_FOR_BATTLE_DESTINY, physicalCard)) {
                    if (modifier.getSource(gameState) != null && playerId.equals(modifier.getSource(gameState).getOwner())) {
                        list.add(Collections.singletonMap(modifier.getSource(gameState), modifier.getDestinyWhenDrawnForDestinyModifier(gameState, this, physicalCard)));
                    }
                }
            }
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EACH_BATTLE_DESTINY_AT_LOCATION, battleLocation)) {
                if (modifier.getSource(gameState) != null && playerId.equals(modifier.getSource(gameState).getOwner())) {
                    list.add(Collections.singletonMap(modifier.getSource(gameState), modifier.getBattleDestinyAtLocationModifier(playerId, gameState, this, battleLocation)));
                }
            }
            for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
                if (modifier.isForTopDrawDestinyEffect(gameState)) {
                    if (modifier.getSource(gameState) != null && playerId.equals(modifier.getSource(gameState).getOwner())) {
                        list.add(Collections.singletonMap(modifier.getSource(gameState), modifier.getValue(gameState, this, (PhysicalCard) null)));
                    }
                }
            }
        }
        return list;
    }

    /**
     * Gets the value of a drawn destiny to attrition.
     * @param gameState the game state
     * @param physicalCard the card drawn for destiny to attrition
     * @param playerId the player drawing destiny to attrition
     * @return the destiny to attrition value
     */
    @Override
    public float getDestinyToAttrition(GameState gameState, PhysicalCard physicalCard, String playerId) {
        Float result = physicalCard.getDestinyValueToUse();

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DESTINY, physicalCard)) {
            result = modifier.getPrintedValueDefinedByGameText(gameState, this, physicalCard);
        }
        // If value if undefined, then return 0
        if (result == null)
            return 0;

        // If card is a character and it is "doubled", then double the printed number
        if (physicalCard.getBlueprint().getCardCategory()==CardCategory.CHARACTER
                && isDoubled(gameState, physicalCard)) {
            result *= 2;
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY, physicalCard)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getDestinyModifier(gameState, this, physicalCard);
            }
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_WHEN_DRAWN_FOR_DESTINY, physicalCard)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getDestinyWhenDrawnForDestinyModifier(gameState, this, physicalCard);
            }
        }
        for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
            if (modifier.isForTopDrawDestinyEffect(gameState)) {
                if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    result += modifier.getValue(gameState, this, (PhysicalCard) null);
                }
            }
        }

        return result;
    }

    /**
     * Gets the value of a drawn carbon-freezing destiny.
     * @param gameState the game state
     * @param physicalCard the card drawn for carbon-freezing destiny
     * @return the total battle destiny
     */
    @Override
    public float getCarbonFreezingDestiny(GameState gameState, PhysicalCard physicalCard) {
        Float result = physicalCard.getDestinyValueToUse();

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DESTINY, physicalCard)) {
            result = modifier.getPrintedValueDefinedByGameText(gameState, this, physicalCard);
        }
        // If value if undefined, then return 0
        if (result == null)
            return 0;

        // If card is a character and it is "doubled", then double the printed number
        if (physicalCard.getBlueprint().getCardCategory()==CardCategory.CHARACTER
                && isDoubled(gameState, physicalCard)) {
            result *= 2;
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY, physicalCard)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getDestinyModifier(gameState, this, physicalCard);
            }
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_WHEN_DRAWN_FOR_DESTINY, physicalCard)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getDestinyWhenDrawnForDestinyModifier(gameState, this, physicalCard);
            }
        }
        for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_CARBON_FREEZING_DESTINY)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getValue(gameState, this, physicalCard);
            }
        }
        for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
            if (modifier.isForTopDrawDestinyEffect(gameState)) {
                if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    result += modifier.getValue(gameState, this, (PhysicalCard) null);
                }
            }
        }

        return result;
    }

    @Override
    public float getTotalCarbonFreezingDestiny(GameState gameState, String playerId, float baseTotalDestiny) {
        float result = baseTotalDestiny;
        for (Modifier modifier : getModifiers(gameState, ModifierType.TOTAL_CARBON_FREEZING_DESTINY)) {
            if (modifier.isForPlayer(playerId)) {
                result += modifier.getValue(gameState, this, (PhysicalCard) null);
            }
        }
        return Math.max(0, result);
    }

    /**
     * Gets the value of a drawn asteroid destiny.
     * @param gameState the game state
     * @param physicalCard the card drawn for asteroid destiny
     * @param playerId the player drawing asteroid destiny
     * @return the asteroid destiny value
     */
    @Override
    public float getAsteroidDestiny(GameState gameState, PhysicalCard physicalCard, String playerId) {
        Float result = physicalCard.getDestinyValueToUse();

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DESTINY, physicalCard)) {
            result = modifier.getPrintedValueDefinedByGameText(gameState, this, physicalCard);
        }
        // If value if undefined, then return 0
        if (result == null)
            return 0;

        // If card is a character and it is "doubled", then double the printed number
        if (physicalCard.getBlueprint().getCardCategory()==CardCategory.CHARACTER
                && isDoubled(gameState, physicalCard)) {
            result *= 2;
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY, physicalCard)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getDestinyModifier(gameState, this, physicalCard);
            }
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_WHEN_DRAWN_FOR_DESTINY, physicalCard)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getDestinyWhenDrawnForDestinyModifier(gameState, this, physicalCard);
            }
        }
        for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
            if (modifier.isForTopDrawDestinyEffect(gameState)) {
                if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    result += modifier.getValue(gameState, this, (PhysicalCard) null);
                }
            }
        }

        PhysicalCard targetedStarship = gameState.getStarshipDrawingAsteroidDestinyAgainst();
        if (targetedStarship != null) {
            for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_ASTEROID_DESTINY)) {
                if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    if (modifier.isAffectedTarget(gameState, this, targetedStarship)) {
                        result += modifier.getValue(gameState, this, physicalCard);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Gets the total asteroid destiny value after applying modifiers to the base total asteroid destiny.
     * @param gameState the game state
     * @param playerId the player with the asteroid destiny
     * @param baseTotalDestiny the base total asteroid destiny
     * @return the total asteroid destiny
     */
    @Override
    public float getTotalAsteroidDestiny(GameState gameState, String playerId, float baseTotalDestiny) {
        float result = baseTotalDestiny;

        PhysicalCard location = gameState.getLocationDrawingAsteroidDestinyAt();
        if (location == null)
            return result;

        // Add 1 for each additional sector at that system that has 'Asteroid Rules' in effect.
        int additionalSectors = Filters.countTopLocationsOnTable(gameState.getGame(), Filters.and(Filters.asteroidRulesInEffect, Filters.relatedAsteroidSector(location)));
        result += additionalSectors;

        PhysicalCard targetedStarship = gameState.getStarshipDrawingAsteroidDestinyAgainst();
        if (targetedStarship != null) {
            for (Modifier modifier : getModifiers(gameState, ModifierType.TOTAL_ASTEROID_DESTINY)) {
                if (modifier.isAffectedTarget(gameState, this, targetedStarship)) {
                    result += modifier.getValue(gameState, this, targetedStarship);
                }
            }
        }

        return Math.max(0, result);
    }

    /**
     * Gets the value of a drawn search party destiny.
     * @param gameState the game state
     * @param physicalCard the card drawn for search party destiny
     * @param playerId the player with the search party destiny
     * @return the total battle destiny
     */
    @Override
    public float getSearchPartyDestiny(GameState gameState, PhysicalCard physicalCard, String playerId) {
        Float result = physicalCard.getDestinyValueToUse();

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DESTINY, physicalCard)) {
            result = modifier.getPrintedValueDefinedByGameText(gameState, this, physicalCard);
        }
        // If value if undefined, then return 0
        if (result == null)
            return 0;

        // If card is a character and it is "doubled", then double the printed number
        if (physicalCard.getBlueprint().getCardCategory()==CardCategory.CHARACTER
                && isDoubled(gameState, physicalCard)) {
            result *= 2;
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY, physicalCard)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getDestinyModifier(gameState, this, physicalCard);
            }
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_WHEN_DRAWN_FOR_DESTINY, physicalCard)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getDestinyWhenDrawnForDestinyModifier(gameState, this, physicalCard);
            }
        }
        for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
            if (modifier.isForTopDrawDestinyEffect(gameState)) {
                if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    result += modifier.getValue(gameState, this, (PhysicalCard) null);
                }
            }
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EACH_SEARCH_PARTY_DESTINY_AT_LOCATION, gameState.getSearchPartyLocation())) {
            if (modifier.isForPlayer(playerId)) {
                if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    result += modifier.getValue(gameState, this, physicalCard);
                }
            }
        }

        // Add 1 for each character (or 2 if a scout) in search party.
        for (PhysicalCard searchPartyMember : gameState.getSearchParty()) {
            result += (Filters.scout.accepts(gameState, this, searchPartyMember) ? 2 : 1);
        }

        return result;
    }

    /**
     * Gets the total search party destiny value after applying modifiers to the base search party destiny.
     * @param gameState the game state
     * @param playerId the player with the search party destiny
     * @param baseTotalDestiny the base total search party destiny
     * @return the total battle destiny
     */
    @Override
    public float getTotalSearchPartyDestiny(GameState gameState, String playerId, float baseTotalDestiny) {
        return Math.max(0, baseTotalDestiny);
    }

    /**
     * Gets the value of a drawn training destiny.
     * @param gameState the game state
     * @param jediTest the Jedi Test
     * @param physicalCard the card drawn for training destiny
     * @param playerId the player with the training destiny
     * @return the training destiny draw value
     */
    @Override
    public float getTrainingDestiny(GameState gameState, PhysicalCard jediTest, PhysicalCard physicalCard, String playerId) {
        Float result = physicalCard.getDestinyValueToUse();

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DESTINY, physicalCard)) {
            result = modifier.getPrintedValueDefinedByGameText(gameState, this, physicalCard);
        }
        // If value if undefined, then return 0
        if (result == null)
            return 0;

        // If card is a character and it is "doubled", then double the printed number
        if (physicalCard.getBlueprint().getCardCategory()==CardCategory.CHARACTER
                && isDoubled(gameState, physicalCard)) {
            result *= 2;
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY, physicalCard)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getDestinyModifier(gameState, this, physicalCard);
            }
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_WHEN_DRAWN_FOR_DESTINY, physicalCard)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getDestinyWhenDrawnForDestinyModifier(gameState, this, physicalCard);
            }
        }
        for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
            if (modifier.isForTopDrawDestinyEffect(gameState)) {
                if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    result += modifier.getValue(gameState, this, (PhysicalCard) null);
                }
            }
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EACH_TRAINING_DESTINY, jediTest)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getValue(gameState, this, physicalCard);
            }
        }

        return result;
    }

    /**
     * Gets the total training destiny value after applying modifiers to the base training destiny.
     * @param gameState the game state
     * @param jediTest the Jedi Test
     * @param baseTotalDestiny the base total training destiny
     * @return the total battle destiny
     */
    @Override
    public float getTotalTrainingDestiny(GameState gameState, PhysicalCard jediTest, float baseTotalDestiny) {
        float result = baseTotalDestiny;

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TOTAL_TRAINING_DESTINY, jediTest)) {
            result += modifier.getValue(gameState, this, jediTest);
        }

        return Math.max(0, result);
    }

    /**
     * Gets the total movement destiny value after applying modifiers to the base total movement destiny.
     * @param gameState the game state
     * @param playerId the player with the movement destiny
     * @param baseTotalDestiny the base total movement destiny
     * @return the total movement destiny
     */
    @Override
    public float getTotalMovementDestiny(GameState gameState, String playerId, float baseTotalDestiny) {
        float result = baseTotalDestiny;

        PhysicalCard starship = gameState.getStarshipDrawingMovementDestinyAgainst();
        if (starship == null)
            return result;

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TOTAL_MOVEMENT_DESTINY, starship)) {
            result += modifier.getValue(gameState, this, starship);
        }

        return Math.max(0, result);
    }

    /**
     * Gets the value of a drawn duel destiny.
     * @param gameState the game state
     * @param physicalCard the card drawn for duel destiny
     * @param playerId the player drawing duel destiny
     * @return the duel destiny value
     */
    @Override
    public float getDuelDestiny(GameState gameState, PhysicalCard physicalCard, String playerId) {
        Float result = physicalCard.getDestinyValueToUse();

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DESTINY, physicalCard)) {
            result = modifier.getPrintedValueDefinedByGameText(gameState, this, physicalCard);
        }
        // If value if undefined, then return 0
        if (result == null)
            return 0;

        // If card is a character and it is "doubled", then double the printed number
        if (physicalCard.getBlueprint().getCardCategory()==CardCategory.CHARACTER
                && isDoubled(gameState, physicalCard)) {
            result *= 2;
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY, physicalCard)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getDestinyModifier(gameState, this, physicalCard);
            }
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_WHEN_DRAWN_FOR_DESTINY, physicalCard)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getDestinyWhenDrawnForDestinyModifier(gameState, this, physicalCard);
            }
        }
        for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DUEL_DESTINY)) {
            if (modifier.isForPlayer(playerId)) {
                if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    result += modifier.getValue(gameState, this, physicalCard);
                }
            }
        }
        for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
            if (modifier.isForTopDrawDestinyEffect(gameState)) {
                if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    result += modifier.getValue(gameState, this, (PhysicalCard) null);
                }
            }
        }

        return result;
    }

    /**
     * Gets the value of a drawn lightsaber combat destiny.
     * @param gameState the game state
     * @param physicalCard the card drawn for lightsaber combat destiny
     * @param playerId the player drawing lightsaber combat destiny
     * @return the lightsaber combat destiny value
     */
    @Override
    public float getLightsaberCombatDestiny(GameState gameState, PhysicalCard physicalCard, String playerId) {
        Float result = physicalCard.getDestinyValueToUse();

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DESTINY, physicalCard)) {
            result = modifier.getPrintedValueDefinedByGameText(gameState, this, physicalCard);
        }
        // If value if undefined, then return 0
        if (result == null)
            return 0;

        // If card is a character and it is "doubled", then double the printed number
        if (physicalCard.getBlueprint().getCardCategory()==CardCategory.CHARACTER
                && isDoubled(gameState, physicalCard)) {
            result *= 2;
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY, physicalCard)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getDestinyModifier(gameState, this, physicalCard);
            }
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_WHEN_DRAWN_FOR_DESTINY, physicalCard)) {
            if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                result += modifier.getDestinyWhenDrawnForDestinyModifier(gameState, this, physicalCard);
            }
        }
        for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_LIGHTSABER_COMBAT_DESTINY)) {
            if (modifier.isForPlayer(playerId)) {
                if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    result += modifier.getValue(gameState, this, physicalCard);
                }
            }
        }
        for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
            if (modifier.isForTopDrawDestinyEffect(gameState)) {
                if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    result += modifier.getValue(gameState, this, (PhysicalCard) null);
                }
            }
        }

        return result;
    }

    /**
     * Gets the total destiny value after applying modifiers to the base total destiny.
     * @param gameState the game state
     * @param playerId the player with the destiny
     * @param baseTotalDestiny the base total destiny
     * @return the total destiny
     */
    @Override
    public float getTotalDestiny(GameState gameState, String playerId, float baseTotalDestiny) {
        float result = baseTotalDestiny;
        for (Modifier modifier : getModifiers(gameState, ModifierType.TOTAL_DESTINY)) {
            if (modifier.isForTopDrawDestinyEffect(gameState)) {
                result += modifier.getValue(gameState, this, (PhysicalCard) null);
            }
        }
        return Math.max(0, result);
    }

    /**
     * Gets the total weapon destiny value after applying modifiers to the base total weapon destiny.
     * @param gameState the game state
     * @param playerId the player with the weapon destiny
     * @param baseTotalDestiny the base total weapon destiny
     * @return the total weapon destiny
     */
    @Override
    public float getTotalWeaponDestiny(GameState gameState, String playerId, float baseTotalDestiny) {
        float result = baseTotalDestiny;

        // Get from WeaponFiringState
        WeaponFiringState weaponFiringState = gameState.getWeaponFiringState();
        if (weaponFiringState != null) {
            PhysicalCard weapon = weaponFiringState.getCardFiring();
            SwccgBuiltInCardBlueprint permanentWeapon = weaponFiringState.getPermanentWeaponFiring();
            Collection<PhysicalCard> weaponTargets = weaponFiringState.getTargets();
            PhysicalCard cardFiringWeapon = weaponFiringState.getCardFiringWeapon();

            for (Modifier modifier : getModifiers(gameState, ModifierType.TOTAL_WEAPON_DESTINY)) {
                result += modifier.getTotalWeaponDestinyModifier(gameState, this, cardFiringWeapon, weapon, permanentWeapon, weaponTargets);
            }
        }

        return Math.max(0, result);
    }

    @Override
    public float getTotalWeaponDestinyForCombinedFiring(GameState gameState, String playerId, PhysicalCard weaponTarget, float baseTotalDestiny) {
        float result = baseTotalDestiny;
        //if (!gameState.getWeaponFiringState().isCombinedFiring())
        //    return result;
        /* TODO: Fix this

        Collection<PhysicalCard> differentCardTitlesFiring = gameState.getWeaponFiringState().getCardsWithDifferentTitlesFiring();
        for (PhysicalCard weapon : differentCardTitlesFiring) {
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TOTAL_WEAPON_DESTINY_FOR_WEAPON, weapon)) {
                result += modifier.getTotalWeaponDestinyModifier(gameState, this, weapon.getAttachedTo(), weapon, weaponTarget);
            }

            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TOTAL_WEAPON_DESTINY_FOR_WEAPON_FIRED_BY, weapon.getAttachedTo())) {
                result += modifier.getTotalWeaponDestinyModifier(gameState, this, weapon.getAttachedTo(), weapon, weaponTarget);
            }
        } */
        return Math.max(0, result);
    }

    /**
     * Gets the total destiny to power value after applying modifiers to the base total destiny to power.
     * @param gameState the game state
     * @param playerId the player with the destiny to power
     * @param baseTotalDestiny the base total destiny to power
     * @return the total destiny to power
     */
    @Override
    public float getTotalDestinyToPower(GameState gameState, String playerId, float baseTotalDestiny) {
        return Math.max(0, baseTotalDestiny);
    }

    /**
     * Gets the total battle destiny value after applying modifiers to the base total battle destiny.
     * @param gameState the game state
     * @param playerId the player with the battle destiny
     * @param baseTotalDestiny the base total battle destiny
     * @return the total battle destiny
     */
    @Override
    public float getTotalBattleDestiny(GameState gameState, String playerId, float baseTotalDestiny) {
        PhysicalCard battleLocation = gameState.getBattleState().getBattleLocation();
        if (battleLocation == null)
            return 0;

        // Check if value was reset to an "unmodifiable value", and use lowest found
        Float lowestResetValue = null;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_TOTAL_BATTLE_DESTINY_AT_LOCATION, battleLocation)) {
            if (modifier.isForPlayer(playerId)) {
                if (!mayNotResetTotalBattleDestiny(gameState, playerId, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    float modifierAmount = modifier.getValue(gameState, this, battleLocation);
                    lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
                }
            }
        }
        if (lowestResetValue != null) {
            return Math.max(0, lowestResetValue);
        }

        float result = baseTotalDestiny;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TOTAL_BATTLE_DESTINY_AT_LOCATION, battleLocation)) {
            if (modifier.isForPlayer(playerId)) {
                if (!mayNotModifyTotalBattleDestiny(gameState, playerId, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
                    result *= modifier.getMultiplierValue(gameState, this, battleLocation);
                    result += modifier.getValue(gameState, this, battleLocation);
                }
            }
        }
        return Math.max(0, result);
    }

    /**
     * Gets the total destiny to attrition value after applying modifiers to the base total destiny to attrition.
     * @param gameState the game state
     * @param playerId the player with the destiny to attrition
     * @param baseTotalDestiny the base total destiny to attrition
     * @return the total destiny to attrition
     */
    @Override
    public float getTotalDestinyToAttrition(GameState gameState, String playerId, float baseTotalDestiny) {
        return Math.max(0, baseTotalDestiny);
    }

    /**
     * Determines if the specified player takes no battle damage.
     * @param gameState the game state
     * @param playerId the player
     * @return true or false
     */
    @Override
    public boolean isTakesNoBattleDamage(GameState gameState, String playerId) {
        BattleState battleState = gameState.getBattleState();
        if (battleState == null)
            return false;

        PhysicalCard location = battleState.getBattleLocation();
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.NO_BATTLE_DAMAGE, location)) {
            if (modifier.isForPlayer(playerId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public float getTotalBattleDamage(GameState gameState, String playerId) {
        BattleState battleState = gameState.getBattleState();
        if (battleState == null)
            return 0;

        float result = battleState.getBaseBattleDamage(playerId);
        if (result == 0)
            return 0;

        // Check if all Force loss is divided in half (rounding up or down) first
        if (hasFlagActive(gameState, ModifierFlag.HALVE_AND_ROUND_UP_FORCE_LOSS, playerId))
            result = (float) Math.ceil((double) result / 2);
        else if (hasFlagActive(gameState, ModifierFlag.HALVE_AND_ROUND_DOWN_FORCE_LOSS, playerId))
            result = (float) Math.floor((double) result / 2);

        PhysicalCard location = battleState.getBattleLocation();

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.BATTLE_DAMAGE, location)) {
            if (modifier.isForPlayer(playerId)) {
                result *= modifier.getMultiplierValue(gameState, this, location);
                result += modifier.getValue(gameState, this, location);
            }
        }

        // Last, check if battle damage has a limit
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.BATTLE_DAMAGE_LIMIT, location)) {
            if (modifier.isForPlayer(playerId)) {
                result = Math.min(result, modifier.getValue(gameState, this, location));
            }
        }

        return Math.max(0, result);
    }

    @Override
    public float getTotalAttrition(GameState gameState, String playerId) {
        BattleState battleState = gameState.getBattleState();
        if (battleState == null)
            return 0;

        // If during damage segment of battle, then attrition is already set
        if (battleState.isReachedDamageSegment()) {
            return battleState.getAttritionTotal(gameState.getGame(), playerId);
        }

        // If opponent did not draw any battle destiny, attrition is 0
        if (battleState.getNumBattleDestinyDrawn(gameState.getOpponent(playerId)) == 0)
            return 0;

        PhysicalCard location = battleState.getBattleLocation();

        Float lowestResetValue = null;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_ATTRITION, location)) {
            if (modifier.isForPlayer(playerId)) {
                float modifierAmount = modifier.getValue(gameState, this, location);
                lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
            }
        }
        if (lowestResetValue != null) {
            return Math.max(0, lowestResetValue);
        }

        float result = battleState.getBaseAttrition(playerId);
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.ATTRITION, location)) {
            result += modifier.getAttritionModifier(playerId, gameState, this, location);
        }

        result = Math.max(0, result);
        return result;
    }

    /**
     * Gets the number of duel destiny draws for the specified player.
     * @param gameState the game state
     * @param player the player
     * @return the number duel destiny draws
     */
    @Override
    public int getNumDuelDestinyDraws(GameState gameState, String player) {
        DuelState duelState = gameState.getDuelState();
        if (duelState ==null)
            return 0;

        int result = duelState.getBaseNumDuelDestinyDraws(player);
        // Check modifiers to "number of duel destiny draws"
        for (Modifier modifier : getModifiers(gameState, ModifierType.NUM_DUEL_DESTINY_DRAWS))
            result += modifier.getNumDuelDestinyDrawsModifier(player, gameState, this);

        return Math.max(0, result);
    }

    /**
     * Gets the number of lightsaber combat destiny draws for the specified player.
     * @param gameState the game state
     * @param player the player
     * @return the number lightsaber combat destiny draws
     */
    @Override
    public int getNumLightsaberCombatDestinyDraws(GameState gameState, String player) {
        LightsaberCombatState lightsaberCombatState = gameState.getLightsaberCombatState();
        if (lightsaberCombatState == null)
            return 0;

        int result = lightsaberCombatState.getBaseNumDuelDestinyDraws(player);

        return Math.max(0, result);
    }

    /**
     * Gets the attack total.
     * @param gameState the game state
     * @param defender true if total for defender, otherwise total for attacker
     * @return the attack total
     */
    @Override
    public float getAttackTotal(GameState gameState, boolean defender) {
        AttackState attackState = gameState.getAttackState();
        if (attackState == null) {
            return 0;
        }

        // Check if attack total is final
        if (attackState.isFinalTotalsSet()) {
            return defender ? attackState.getFinalDefenderTotal() : attackState.getFinalAttackerTotal();
        }

        float result = getAttackTotalPowerOrFerocity(gameState, defender);
        if ((defender && attackState.isCreatureAttackingNonCreature()) || (!defender && attackState.isNonCreatureAttackingCreature())) {
            Float attackDestinyTotal = attackState.getAttackDestinyTotal(defender ? attackState.getDefenderOwner() : attackState.getAttackerOwner());
            if (attackDestinyTotal != null) {
                result += attackDestinyTotal;
            }
        }
        if (defender && attackState.isNonCreatureAttackingCreature()) {
            for (PhysicalCard cardDefending : attackState.getCardsDefending()) {
                result += getDefenseValue(gameState, cardDefending);
            }
        }

        return Math.max(0, result);
    }

    /**
     * Gets the duel total for the specified player.
     * @param gameState the game state
     * @param playerId the player
     * @return the duel total
     */
    @Override
    public float getDuelTotal(GameState gameState, String playerId) {
        DuelState duelState = gameState.getDuelState();
        if (duelState == null) {
            return 0;
        }

        // Check if duel total is final
        if (duelState.isReachedResults())
            return duelState.getFinalDuelTotal(playerId);

        float result = duelState.getBaseDuelTotal(playerId);
        PhysicalCard character = duelState.getCharacter(playerId);

        // Check modifiers to "duel total"
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DUEL_TOTAL, character)) {
            result += modifier.getValue(gameState, this, character);
        }

        // Check if this is an attempt to cross over to dark side
        if (duelState.isCrossOverToDarkSideAttempt() && playerId.equals(gameState.getDarkPlayer())) {
            result = getCrossoverAttemptTotal(gameState, duelState.getCharacter(gameState.getLightPlayer()), result);
        }

        return Math.max(0, result);
    }

    /**
     * Gets the lightsaber combat total for the specified player.
     * @param gameState the game state
     * @param playerId the player
     * @return the lightsaber combat total
     */
    @Override
    public float getLightsaberCombatTotal(GameState gameState, String playerId) {
        LightsaberCombatState lightsaberCombatState = gameState.getLightsaberCombatState();
        if (lightsaberCombatState == null)
            return 0;

        // Check if lightsaber combat total is final
        if (lightsaberCombatState.isReachedResults())
            return lightsaberCombatState.getFinalLightsaberCombatTotal(playerId);

        // If player did not draw any lightsaber combat destiny, then total is 0
        if (lightsaberCombatState.getNumLightsaberCombatDestinyDrawn(playerId) == 0)
            return 0;

        float result = lightsaberCombatState.getBaseLightsaberCombatTotal(playerId);
        PhysicalCard character = lightsaberCombatState.getCharacter(playerId);

        // Check modifiers to "lightsaber combat total"
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.LIGHTSABER_COMBAT_TOTAL, character)) {
            result += modifier.getValue(gameState, this, character);
        }

        return Math.max(0, result);
    }

    /**
     * Gets the crossover attempt total when attempting to cross over the specified character.
     * @param gameState the game state
     * @param character the character to attempt to cross over
     * @param baseValue the initial value of the cross over attempt total
     * @return the duel total
     */
    @Override
    public float getCrossoverAttemptTotal(GameState gameState, PhysicalCard character, float baseValue) {
        float result = baseValue;

        // Check modifiers to "cross over attempt total"
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.CROSS_OVER_ATTEMPT_TOTAL, character)) {
            result += modifier.getValue(gameState, this, character);
        }

        return Math.max(0, result);
    }

    /**
     * Gets the amount of Force for the specified player to lose.
     * @param gameState the game state
     * @param playerId the player to lose Force
     * @param isCost true if Force loss is a cost, otherwise false
     * @param baseValue the initial value of the Force loss
     * @return the amount of Force to lose
     */
    @Override
    public float getForceToLose(GameState gameState, String playerId, boolean isCost, float baseValue) {
        float result = baseValue;

        // If not a cost, check if all Force loss is divided in half (rounding up or down) first, then other modifiers
        if (!isCost) {
            if (hasFlagActive(gameState, ModifierFlag.HALVE_AND_ROUND_UP_FORCE_LOSS, playerId))
                result = (float) Math.ceil((double) result / 2);
            else if (hasFlagActive(gameState, ModifierFlag.HALVE_AND_ROUND_DOWN_FORCE_LOSS, playerId))
                result = (float) Math.floor((double) result / 2);

            for (Modifier modifier : getModifiers(gameState, ModifierType.FORCE_LOSS)) {
                if (modifier.isForPlayer(playerId)) {
                    result += modifier.getForceLossModifier(gameState, this);
                }
            }

            for (Modifier modifier : getModifiers(gameState, ModifierType.FORCE_LOSS_MINIMUM)) {
                if (modifier.isForPlayer(playerId)) {
                    result = Math.max(result, modifier.getForceLossMinimum(gameState, this));
                }
            }
        }

        return Math.max(0, result);
    }

    /**
     * Gets the maximum amount of Force that the specified player can lose from the specified source card.
     * @param gameState the game state
     * @param playerId the player to lose Force
     * @param source the source card of the Force loss
     * @return the maximum amount of Force to lose
     */
    @Override
    public float getForceToLoseFromCardLimit(GameState gameState, String playerId, PhysicalCard source) {
        float result = Integer.MAX_VALUE;

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.LIMIT_FORCE_LOSS_FROM_CARD, source)) {
            if (modifier.isForPlayer(playerId)) {
                result = Math.min(result, modifier.getForceLossLimit(gameState, this));
            }
        }

        return Math.max(0, result);
    }

    /**
     * Gets the maximum amount of Force that the specified player can lose from a Force drain at the specified location.
     * @param gameState the game state
     * @param playerId the player to lose Force
     * @param location the Force drain location
     * @return the maximum amount of Force to lose
     */
    @Override
    public float getForceToLoseFromForceDrainLimit(GameState gameState, String playerId, PhysicalCard location) {
        float result = Float.MAX_VALUE;

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.LIMIT_FORCE_LOSS_FROM_FORCE_DRAIN, location)) {
            if (modifier.isForPlayer(playerId)) {
                result = Math.min(result, modifier.getForceLossLimit(gameState, this));
            }
        }

        return Math.max(0, result);
    }

    /**
     * Gets the maximum amount of Force that the specified player can lose from an 'insert' card.
     * @param gameState the game state
     * @param playerId the player to lose Force
     * @return the maximum amount of Force to lose
     */
    @Override
    public float getForceToLoseFromInsertCardLimit(GameState gameState, String playerId) {
        float result = Integer.MAX_VALUE;

        for (Modifier modifier : getModifiers(gameState, ModifierType.LIMIT_FORCE_LOSS_FROM_INSERT_CARD)) {
            if (modifier.isForPlayer(playerId)) {
                result = Math.min(result, modifier.getForceLossLimit(gameState, this));
            }
        }

        return Math.max(0, result);
    }

    /**
     * Determines if Force retrieval from the specified card is immune to Secret Plans.
     * @param gameState the game state
     * @param card the card
     * @return true or false
     */
    @Override
    public boolean isForceRetrievalImmuneToSecretPlans(GameState gameState, PhysicalCard card) {
        return !getModifiersAffectingCard(gameState, ModifierType.FORCE_RETRIEVAL_IMMUNE_TO_SECRET_PLANS, card).isEmpty();
    }

    /**
     * Gets the initial calculated amount of Force for the specified player to retrieve when collecting a bounty.
     * @param gameState the game state
     * @param playerId the player to retrieve Force
     * @param bountyHunterToCollect the bounty hunter to collect the bounty
     * @param baseValue the initial value of the Force retrieval
     * @return the amount of Force to retrieve
     */
    @Override
    public float getForceToRetrieveForBounty(GameState gameState, String playerId, PhysicalCard bountyHunterToCollect, float baseValue) {
        float result = baseValue;

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FORCE_RETRIEVAL_FOR_BOUNTY, bountyHunterToCollect)) {
            result += modifier.getValue(gameState, this, bountyHunterToCollect);
        }

        return Math.max(0, result);
    }

    /**
     * Gets the amount of Force for the specified player to retrieve.
     * @param gameState the game state
     * @param playerId  the player to retrieve Force
     * @param source the source card of the Force retrieval
     * @param baseValue the initial value of the Force retrieval
     * @return the amount of Force to retrieve
     */
    @Override
    public float getForceToRetrieve(GameState gameState, String playerId, PhysicalCard source, float baseValue) {
        float result = baseValue;

        for (Modifier modifier : getModifiers(gameState, ModifierType.FORCE_RETRIEVAL)) {
            if (modifier.isForPlayer(playerId)) {
                result += modifier.getValue(gameState, this, source);
            }
        }

        // Check if value was reset to an "unmodifiable value", and use lowest found
        Float lowestResetValue = null;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_FORCE_RETRIEVAL, source)) {
            float modifierAmount = modifier.getValue(gameState, this, source);
            lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
        }
        if (lowestResetValue != null) {
            result = lowestResetValue;
        }

        return Math.max(0, result);
    }

    /**
     * Determines if the specified player is explicitly not allowed to retrieve Force for initiating a battle.
     * @param gameState the game state
     * @param playerId the player
     * @return true or false
     */
    @Override
    public boolean mayNotRetrieveForceForInitiatingBattle(GameState gameState, String playerId) {
        return hasFlagActive(gameState, ModifierFlag.MAY_NOT_RETRIEVE_FORCE_FOR_INITIATING_BATTLE, playerId);
    }

    /**
     * Determines if the specified card is explicitly not allowed to contribute to Force retrieval.
     * @param gameState the game state
     * @param card the card
     * @return true or false
     */
    @Override
    public boolean mayNotContributeToForceRetrieval(GameState gameState, PhysicalCard card) {
        return !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_CONTRIBUTE_TO_FORCE_RETRIEVAL, card).isEmpty();
    }

    /**
     * Determines if the specified card is explicitly not allowed to contribute to Force retrieval.
     * @param gameState the game state
     * @param card the card
     * @return true or false
     */
    @Override
    public boolean playersCardsAtLocationMayNotContributeToForceRetrieval(GameState gameState, PhysicalCard card, String playerId) {
        return !getModifiersAffectingCard(gameState, ModifierType.PLAYERS_CARDS_AT_LOCATION_MAY_NOT_CONTRIBUTE_TO_FORCE_RETRIEVAL, card).isEmpty();
    }

    /**
     * Determines if the card has any immunity to attrition.
     *
     * @param gameState the game state
     * @param card a card
     * @return true if card has any immunity to attrition, otherwise false
     */
    @Override
    public boolean hasAnyImmunityToAttrition(GameState gameState, PhysicalCard card) {
        return hasAnyImmunityToAttrition(gameState, card, false, null, new ModifierCollectorImpl());
    }

    /**
     * Determines if the card already has immunity to attrition (when ignoring modifiers from specified card).
     * @param gameState the game state
     * @param card      a card
     * @param sourceToIgnore source card to ignore modifiers from
     * @return true if card already has any immunity to attrition, otherwise false
     */
    @Override
    public boolean alreadyHasImmunityToAttrition(GameState gameState, PhysicalCard card, Filterable sourceToIgnore) {
        return hasAnyImmunityToAttrition(gameState, card, false, sourceToIgnore, new ModifierCollectorImpl());
    }

    private boolean hasAnyImmunityToAttrition(GameState gameState, PhysicalCard card, boolean skipImmunityValueCheck, Filterable sourceToIgnore, ModifierCollector modifierCollector) {
        if (!card.getBlueprint().hasImmunityToAttritionAttribute())
            return false;

        boolean mayNotBeCanceled = false;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IMMUNITY_TO_ATTRITION_MAY_NOT_BE_CANCELED, card)) {
            mayNotBeCanceled = true;
            modifierCollector.addModifier(modifier);
        }

        if (!mayNotBeCanceled) {
            boolean isCanceled = false;
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.LOSE_IMMUNITY_TO_ATTRITION, card)) {
                isCanceled = true;
                modifierCollector.addModifier(modifier);
            }
            if (isCanceled) {
                return false;
            }
        }

        if (!skipImmunityValueCheck) {
            return (getImmunityToAttritionLessThan(gameState, card, sourceToIgnore, modifierCollector) > 0 || getImmunityToAttritionOfExactly(gameState, card, sourceToIgnore, modifierCollector) > 0);
        }

        return true;
    }

    /**
     * Gets the amount of attrition the specified card is immune to less than.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the immunity to attrition less than value
     */
    @Override
    public float getImmunityToAttritionLessThan(GameState gameState, PhysicalCard physicalCard) {
        return getImmunityToAttritionLessThan(gameState, physicalCard, null, new ModifierCollectorImpl());
    }

    /**
     * Gets the amount of attrition the specified card is immune to less than.
     * @param gameState the game state
     * @param physicalCard a card
     * @param modifierCollector collector of affecting modifiers
     * @return the immunity to attrition less than value
     */
    @Override
    public float getImmunityToAttritionLessThan(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
        return getImmunityToAttritionLessThan(gameState, physicalCard, null, modifierCollector);
    }

    private float getImmunityToAttritionLessThan(GameState gameState, PhysicalCard physicalCard, Filterable sourceToIgnore, ModifierCollector modifierCollector) {
        Float lockedValue = null;
        // During the damage segment of a battle, immunity to attrition cannot change, so look at the saved value
        if (gameState.isDuringDamageSegmentOfBattle() && Filters.participatingInBattle.accepts(gameState, this, physicalCard)) {
            float value = physicalCard.getImmunityToAttritionLessThan();
            if (physicalCard.getImmunityToAttritionOfExactly() >= value)
                lockedValue = 0f;
            else
                lockedValue = value;
        }

        if (!hasAnyImmunityToAttrition(gameState, physicalCard, true, sourceToIgnore, modifierCollector)) {
            if (lockedValue != null) {
                return lockedValue;
            }
            return 0;
        }

        float result = 0;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IMMUNITY_TO_ATTRITION_LESS_THAN, physicalCard)) {
            if (sourceToIgnore == null || modifier.getSource(gameState) == null || !Filters.and(sourceToIgnore).accepts(gameState, this, modifier.getSource(gameState))) {
                result = Math.max(result, modifier.getImmunityToAttritionLessThanModifier(gameState, this, physicalCard));
                modifierCollector.addModifier(modifier);
            }
        }

        if (result > 0) {
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IMMUNITY_TO_ATTRITION_CHANGE, physicalCard)) {
                if (sourceToIgnore == null || modifier.getSource(gameState) == null || !Filters.and(sourceToIgnore).accepts(gameState, this, modifier.getSource(gameState))) {
                    result += modifier.getImmunityToAttritionChangedModifier(gameState, this, physicalCard);
                    modifierCollector.addModifier(modifier);
                }
            }
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IMMUNITY_TO_ATTRITION_OF_EXACTLY, physicalCard)) {
            if (sourceToIgnore == null || modifier.getSource(gameState) == null || !Filters.and(sourceToIgnore).accepts(gameState, this, modifier.getSource(gameState))) {
                if (result <= modifier.getImmunityToAttritionOfExactlyModifier(gameState, this, physicalCard)) {
                    if (lockedValue != null) {
                        return lockedValue;
                    }
                    return 0;
                }
            }
        }

        if (lockedValue != null) {
            return lockedValue;
        }
        return Math.max(0, result);
    }

    /**
     * Gets the amount of attrition the specified card is immune to exactly only.
     * @param gameState the game state
     * @param physicalCard  a card
     * @return the immunity to attrition of exactly value
     */
    @Override
    public float getImmunityToAttritionOfExactly(GameState gameState, PhysicalCard physicalCard) {
        return getImmunityToAttritionOfExactly(gameState, physicalCard, Filters.none, new ModifierCollectorImpl());
    }

    /**
     * Gets the amount of attrition the specified card is immune to exactly only.
     * @param gameState the game state
     * @param physicalCard  a card
     * @param modifierCollector collector of affecting modifiers
     * @return the immunity to attrition of exactly value
     */
    @Override
    public float getImmunityToAttritionOfExactly(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
        return getImmunityToAttritionOfExactly(gameState, physicalCard, Filters.none, modifierCollector);
    }

    private float getImmunityToAttritionOfExactly(GameState gameState, PhysicalCard physicalCard, Filterable sourceToIgnore, ModifierCollector modifierCollector) {
        Float lockedValue = null;
        // During the damage segment of a battle, immunity to attrition cannot change, so look at the saved value
        if (gameState.isDuringDamageSegmentOfBattle()) {
            float value = physicalCard.getImmunityToAttritionOfExactly();
            if (physicalCard.getImmunityToAttritionLessThan() > value)
                lockedValue = 0f;
            else
                lockedValue = value;
        }

        if (!hasAnyImmunityToAttrition(gameState, physicalCard, true, sourceToIgnore, modifierCollector)) {
            if (lockedValue != null) {
                return lockedValue;
            }
            return 0;
        }

        float result = 0;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IMMUNITY_TO_ATTRITION_OF_EXACTLY, physicalCard)) {
            if (sourceToIgnore == null || modifier.getSource(gameState) == null || !Filters.and(sourceToIgnore).accepts(gameState, this, modifier.getSource(gameState))) {
                result = Math.max(result, modifier.getImmunityToAttritionOfExactlyModifier(gameState, this, physicalCard));
                modifierCollector.addModifier(modifier);
            }
        }

        if (result > 0) {
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IMMUNITY_TO_ATTRITION_CHANGE, physicalCard)) {
                if (sourceToIgnore == null || modifier.getSource(gameState) == null || !Filters.and(sourceToIgnore).accepts(gameState, this, modifier.getSource(gameState))) {
                    result += modifier.getImmunityToAttritionChangedModifier(gameState, this, physicalCard);
                    modifierCollector.addModifier(modifier);
                }
            }
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IMMUNITY_TO_ATTRITION_LESS_THAN, physicalCard)) {
            if (sourceToIgnore == null || modifier.getSource(gameState) == null || !Filters.and(sourceToIgnore).accepts(gameState, this, modifier.getSource(gameState))) {
                if (result < modifier.getImmunityToAttritionLessThanModifier(gameState, this, physicalCard)) {
                    if (lockedValue != null) {
                        return lockedValue;
                    }
                    return 0;
                }
            }
        }

        if (lockedValue != null) {
            return lockedValue;
        }
        return Math.max(0, result);
    }

    @Override
    public boolean isImmuneToDeployCostToTargetModifierFromCard(GameState gameState, PhysicalCard cardToDeploy, PhysicalCard deployToTarget, PhysicalCard sourceOfModifier) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IMMUNE_TO_DEPLOY_COST_MODIFIERS_TO_TARGET, cardToDeploy)) {
            if (modifier.isImmuneToDeployCostToTargetModifierFromCard(gameState, this, deployToTarget, sourceOfModifier)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean cantModifyForceDrainAtLocation(GameState gameState, PhysicalCard location, PhysicalCard modifiedByCard, String playerModifying, String playerDraining) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MODIFY_FORCE_DRAIN_AT_LOCATION, location)) {
            if (modifier.cantModifyForceDrain(gameState, this, playerModifying, playerDraining))
                return true;
        }
        // Check if source card may not modify Force drains
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MODIFY_FORCE_DRAINS_BY_USING_CARD, modifiedByCard)) {
            if (modifier.isForPlayer(playerDraining)) {
                if (modifier.isAffectedTarget(gameState, this, location)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean cantReduceForceDrainAtLocation(GameState gameState, PhysicalCard location, PhysicalCard reducedByCard, String playerReducing, String playerDraining) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REDUCE_FORCE_DRAIN_AT_LOCATION, location)) {
            if (modifier.cantModifyForceDrain(gameState, this, playerReducing, playerDraining))
                return true;
        }
        return false;
    }

    /**
     * Determines if the specified card's deploy cost is not allowed to be modified.
     * @param gameState the game state
     * @param card the card
     * @param playerModifyingCost the player to modify the deploy cost
     * @return true if deploy cost may not be modified, otherwise false
     */
    @Override
    public boolean isDeployCostNotAllowedToBeModified(GameState gameState, PhysicalCard card, String playerModifyingCost) {
        return isDeployCostNotAllowedToBeModified(gameState, card, playerModifyingCost, new ModifierCollectorImpl());
    }

    /**
     * Determines if the specified card's deploy cost is not allowed to be modified.
     * @param gameState the game state
     * @param card the card
     * @param playerModifyingCost the player to modify the deploy cost
     * @param modifierCollector collector of affecting modifiers
     * @return true if deploy cost may not be modified, otherwise false
     */
    @Override
    public boolean isDeployCostNotAllowedToBeModified(GameState gameState, PhysicalCard card, String playerModifyingCost, ModifierCollector modifierCollector) {
        boolean retVal = false;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_HAVE_DEPLOY_COST_MODIFIED, card)) {
            if (modifier.isForPlayer(playerModifyingCost)) {
                retVal = true;
                modifierCollector.addModifier(modifier);
            }
        }
        return retVal;
    }

    /**
     * Determines if the specified card's deploy cost is not allowed to be increased.
     * @param gameState the game state
     * @param card the card
     * @param playerIncreasingCost the player to increase the deploy cost
     * @return true if deploy cost may not be increased, otherwise false
     */
    @Override
    public boolean isDeployCostNotAllowedToBeIncreased(GameState gameState, PhysicalCard card, String playerIncreasingCost) {
        return isDeployCostNotAllowedToBeIncreased(gameState, card, playerIncreasingCost, new ModifierCollectorImpl());
    }

    /**
     * Determines if the specified card's deploy cost is not allowed to be increased.
     * @param gameState the game state
     * @param card the card
     * @param playerIncreasingCost the player to increase the deploy cost
     * @param modifierCollector collector of affecting modifiers
     * @return true if deploy cost may not be increased, otherwise false
     */
    @Override
    public boolean isDeployCostNotAllowedToBeIncreased(GameState gameState, PhysicalCard card, String playerIncreasingCost, ModifierCollector modifierCollector) {
        boolean retVal = false;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_HAVE_DEPLOY_COST_INCREASED, card)) {
            if (modifier.isForPlayer(playerIncreasingCost)) {
                retVal = true;
                modifierCollector.addModifier(modifier);
            }
        }
        return retVal;
    }

    /**
     * Determines if the specified card's own deployment modifiers are applied at any location.
     * @param gameState the game state
     * @param card the card
     * @return true or false
     */
    @Override
    public boolean appliesOwnDeploymentModifiersAtAnyLocation(GameState gameState, PhysicalCard card) {
        return (!getModifiersAffectingCard(gameState, ModifierType.APPLIES_OWN_DEPLOYMENT_MODIFIERS_AT_ANY_LOCATION, card).isEmpty());
    }

    @Override
    // The deploy cost not specific to deploying to a specific target
    public float getDeployCost(GameState gameState, PhysicalCard cardToDeploy) {
        return getDeployCost(gameState, cardToDeploy, cardToDeploy, false, false);
    }

    @Override
    // The deploy cost not specific to deploying to a specific target
    public float getDeployCost(GameState gameState, PhysicalCard sourceCard, PhysicalCard cardToDeploy, boolean isDejarikRules, boolean includeExtraCost) {
        return getDeployCost(gameState, sourceCard, cardToDeploy, null, false, isDejarikRules, null, false, 0, null, null, includeExtraCost, new ModifierCollectorImpl());
    }

    @Override
    // If targetCard is null, then it is the deploy cost not specific to deploying to a specific target
    public float getDeployCost(GameState gameState, PhysicalCard sourceCard, PhysicalCard cardToDeploy, PhysicalCard targetCard, boolean isDejarikRules, PlayCardOption playCardOption, boolean forFree, float changeInCost, ReactActionOption reactActionOption, boolean includeExtraCost) {
        return getDeployCost(gameState, sourceCard, cardToDeploy, targetCard, false, isDejarikRules, playCardOption, forFree, changeInCost, reactActionOption, null, includeExtraCost, new ModifierCollectorImpl());
    }

    @Override
    // If targetCard is null, then it is the deploy cost not specific to deploying to a specific target
    public float getDeployCost(GameState gameState, PhysicalCard sourceCard, PhysicalCard cardToDeploy, PhysicalCard targetCard, boolean isDejarikRules, PlayCardOption playCardOption, boolean forFree, float changeInCost, ReactActionOption reactActionOption, boolean includeExtraCost, ModifierCollector modifierCollector) {
        return getDeployCost(gameState, sourceCard, cardToDeploy, targetCard, false, isDejarikRules, playCardOption, forFree, changeInCost, reactActionOption, null, includeExtraCost, modifierCollector);
    }

    // If targetCard is null, then it is the deploy cost not specific to deploying to a specific target
    // Optionally can skip free check since other local methods may have already checked that
    private float getDeployCost(GameState gameState, PhysicalCard sourceCard, PhysicalCard cardToDeploy, PhysicalCard targetCard, boolean skipFreeCheck, boolean isDejarikRules, PlayCardOption playCardOption, boolean forFree, float changeInCost, ReactActionOption reactActionOption, PhysicalCard withPilot, boolean includeExtraCost, ModifierCollector modifierCollector) {
        if (!forFree && reactActionOption != null) {
            forFree = reactActionOption.isForFree();
            if (!forFree) {
                forFree = reactActionOption.getForFreeCardFilter() != null && reactActionOption.getForFreeCardFilter().accepts(gameState, this, cardToDeploy);
            }
        }

        float extraCost = includeExtraCost ? getExtraForceRequiredToDeployToTarget(gameState, cardToDeploy, targetCard, null, sourceCard, forFree) : 0;

        if (forFree) {
            return extraCost;
        }

        String owner = cardToDeploy.getOwner();
        String opponent = gameState.getOpponent(owner);
        boolean deployCostMayNotBeModified = isDeployCostNotAllowedToBeModified(gameState, cardToDeploy, null, modifierCollector);
        boolean deployCostMayNotBeModifiedByOwner = deployCostMayNotBeModified || isDeployCostNotAllowedToBeModified(gameState, cardToDeploy, owner, modifierCollector);
        boolean deployCostMayNotBeModifiedByOpponent = deployCostMayNotBeModified || isDeployCostNotAllowedToBeModified(gameState, cardToDeploy, opponent, modifierCollector);
        boolean deployCostMayNotBeIncreased = deployCostMayNotBeModified || isDeployCostNotAllowedToBeIncreased(gameState, cardToDeploy, null, modifierCollector);
        boolean deployCostMayNotBeIncreasedByOwner = deployCostMayNotBeIncreased || isDeployCostNotAllowedToBeIncreased(gameState, cardToDeploy, owner, modifierCollector);
        boolean deployCostMayNotBeIncreasedByOpponent = deployCostMayNotBeIncreased || isDeployCostNotAllowedToBeIncreased(gameState, cardToDeploy, opponent, modifierCollector);

        // Use destiny number instead if "Dejarik Rules"
        if (isDejarikRules || cardToDeploy.isDejarikHologramAtHolosite()) {
            Float result = getDestiny(gameState, cardToDeploy) + extraCost;

            // Check if deploy cost using dejarik rules is modified by something else already in play
            if (!deployCostMayNotBeModified) {
                for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DEPLOY_COST_USING_DEJARIK_RULES, cardToDeploy)) {
                    String modifierOwner = modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null;
                    if (modifierOwner == null || !(opponent.equals(modifierOwner) ? deployCostMayNotBeModifiedByOpponent : deployCostMayNotBeModifiedByOwner)) {
                        float modifierValue = modifier.getDeployCostModifier(gameState, this, cardToDeploy);
                        if (modifierValue < 0 || (!deployCostMayNotBeIncreased && (modifierOwner == null || !(opponent.equals(modifierOwner) ? deployCostMayNotBeIncreasedByOpponent : deployCostMayNotBeIncreasedByOwner)))) {
                            result += modifierValue;
                        }
                    }
                }
            }

            result = Math.max(0, result);
            return result;
        }

        if (!skipFreeCheck) {
            // Check if the card deploys for free
            if (grantedDeployForFree(gameState, cardToDeploy, targetCard, modifierCollector)) {
                return extraCost;
            }
        }

        // Check if deploy cost is determined by a calculation, instead of normally
        Float deployCostViaCalculation = getDeployCostFromCalculation(gameState, cardToDeploy, modifierCollector);
        if (deployCostViaCalculation != null) {
            return deployCostViaCalculation + extraCost;
        }

        Float result = cardToDeploy.getBlueprint().getDeployCost();

        // Check if deploy cost is specified by game text
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DEPLOY_COST, cardToDeploy)) {
            if (modifier.isPlayCardOption(playCardOption)) {
                result = Math.min(result != null ? result : Float.MAX_VALUE, modifier.getPrintedValueDefinedByGameText(gameState, this, cardToDeploy));
                modifierCollector.addModifier(modifier);
            }
        }

        // Check if deploy cost to specific targets is specified by game text
        if (targetCard != null) {
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DEPLOY_COST_TO_TARGET, cardToDeploy)) {
                if (modifier.isDefinedDeployCostToTarget(gameState, this, targetCard)) {
                    result = Math.min(result != null ? result : Float.MAX_VALUE, modifier.getDefinedDeployCostToTarget(gameState, this, targetCard));
                    modifierCollector.addModifier(modifier);
                }
            }
        }

        // If value if undefined, then return 0
        if (result == null)
            return extraCost;

        // If card is a character and it is "doubled", then double the printed number
        if (cardToDeploy.getBlueprint().getCardCategory()==CardCategory.CHARACTER
                && isDoubled(gameState, cardToDeploy, modifierCollector)) {
            result *= 2;
        }

        float totalReduceCostModifiers = 0;

        // Check for change in cost
        if (!deployCostMayNotBeModifiedByOwner) {
            if (reactActionOption != null) {
                if (reactActionOption.getChangeInCost() < 0 || !deployCostMayNotBeIncreasedByOwner) {
                    result += reactActionOption.getChangeInCost();
                    if (reactActionOption.getChangeInCost() < 0) {
                        totalReduceCostModifiers -= reactActionOption.getChangeInCost();
                    }
                }
            }
            else {
                if (changeInCost < 0 || !deployCostMayNotBeIncreasedByOwner) {
                    result += changeInCost;
                    if (changeInCost < 0) {
                        totalReduceCostModifiers -= changeInCost;
                    }
                }
            }
        }

        // Check if deploy cost is modified by something else already in play
        if (!deployCostMayNotBeModified) {
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DEPLOY_COST, cardToDeploy)) {
                String modifierOwner = modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null;
                if (modifierOwner == null || !(opponent.equals(modifierOwner) ? deployCostMayNotBeModifiedByOpponent : deployCostMayNotBeModifiedByOwner)) {
                    if (targetCard == null || !isImmuneToDeployCostToTargetModifierFromCard(gameState, cardToDeploy, targetCard, modifier.getSource(gameState))) {
                        float modifierValue = modifier.getDeployCostModifier(gameState, this, cardToDeploy);
                        if (modifierValue < 0 || (!deployCostMayNotBeIncreased && (modifierOwner == null || !(opponent.equals(modifierOwner) ? deployCostMayNotBeIncreasedByOpponent : deployCostMayNotBeIncreasedByOwner)))) {
                            result += modifierValue;
                            if (modifierValue < 0) {
                                totalReduceCostModifiers -= modifierValue;
                                modifierCollector.addModifier(modifier);
                            }
                        }
                    }
                }
            }

            // Check if deploy cost of starship is modified when simultaneously deployed with pilot
            if (withPilot != null) {
                for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DEPLOY_COST_WITH_PILOT, cardToDeploy)) {
                    String modifierOwner = modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null;
                    if (modifierOwner == null || !(opponent.equals(modifierOwner) ? deployCostMayNotBeModifiedByOpponent : deployCostMayNotBeModifiedByOwner)) {
                        if (!isImmuneToDeployCostToTargetModifierFromCard(gameState, cardToDeploy, targetCard, modifier.getSource(gameState))) {
                            float modifierValue = modifier.getDeployCostWithPilotModifier(gameState, this, withPilot);
                            if (modifierValue < 0 || (!deployCostMayNotBeIncreased && (modifierOwner == null || !(opponent.equals(modifierOwner) ? deployCostMayNotBeIncreasedByOpponent : deployCostMayNotBeIncreasedByOwner)))) {
                                result += modifierValue;
                                if (modifierValue < 0) {
                                    totalReduceCostModifiers -= modifierValue;
                                    modifierCollector.addModifier(modifier);
                                }
                            }
                        }
                    }
                }
            }
        }

        // Check if deploy cost is affected when deployed to specific targets
        if (targetCard != null) {

            if (!deployCostMayNotBeModified) {

                // Deploying to collapsed site requires 1 additional Force
                if (!deployCostMayNotBeIncreased) {
                    PhysicalCard location = getLocationHere(gameState, targetCard);
                    if (location != null && location.isCollapsed()) {
                        result += 1;
                    }
                }

                // From something else already in play
                for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DEPLOY_COST_TO_TARGET, targetCard)) {
                    String modifierOwner = modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null;
                    if (modifierOwner == null || !(opponent.equals(modifierOwner) ? deployCostMayNotBeModifiedByOpponent : deployCostMayNotBeModifiedByOwner)) {
                        if (!isImmuneToDeployCostToTargetModifierFromCard(gameState, cardToDeploy, targetCard, modifier.getSource(gameState))) {
                            float modifierValue = modifier.getDeployCostToTargetModifier(gameState, this, cardToDeploy, targetCard);
                            if (modifierValue < 0 || (!deployCostMayNotBeIncreased && (modifierOwner == null || !(opponent.equals(modifierOwner) ? deployCostMayNotBeIncreasedByOpponent : deployCostMayNotBeIncreasedByOwner)))) {
                                result += modifierValue;
                                if (modifierValue < 0) {
                                    totalReduceCostModifiers -= modifierValue;
                                    modifierCollector.addModifier(modifier);
                                }
                            }
                        }
                    }
                }

                // From self
                for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.SELF_DEPLOY_COST_TO_TARGET, cardToDeploy)) {
                    float modifierValue = modifier.getDeployCostToTargetModifier(gameState, this, cardToDeploy, targetCard);
                    if (modifierValue < 0 || (!deployCostMayNotBeIncreased && !deployCostMayNotBeIncreasedByOwner)) {
                        result += modifierValue;
                        if (modifierValue < 0) {
                            totalReduceCostModifiers -= modifierValue;
                            modifierCollector.addModifier(modifier);
                        }
                    }
                }
            }
        }

        // Check the most that the deploy cost can be modified (reduced) by
        float maxToReduceCostBy = Float.MAX_VALUE;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAX_AMOUNT_TO_REDUCE_DEPLOY_COST_BY, cardToDeploy)) {
            maxToReduceCostBy = Math.max(0, Math.min(maxToReduceCostBy, modifier.getMaximumToReduceDeployCostBy(gameState, this, cardToDeploy)));
            modifierCollector.addModifier(modifier);
        }
        if (maxToReduceCostBy != Float.MAX_VALUE) {
            result += Math.max(0, totalReduceCostModifiers - maxToReduceCostBy);
        }

        // Check if value was reset to an "unmodifiable value", and use lowest found
        if (targetCard != null) {
            Float lowestResetValue = null;
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_DEPLOY_COST_TO_TARGET, cardToDeploy)) {
                if (modifier.isAffectedTarget(gameState, this, targetCard)) {
                    float modifierAmount = modifier.getValue(gameState, this, cardToDeploy);
                    lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
                    modifierCollector.addModifier(modifier);
                }
            }
            if (lowestResetValue != null) {
                result = lowestResetValue;
            }
        }
        Float lowestResetValue = null;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_DEPLOY_COST, cardToDeploy)) {
            float modifierAmount = modifier.getValue(gameState, this, cardToDeploy);
            lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
            modifierCollector.addModifier(modifier);
        }
        if (lowestResetValue != null) {
            result = lowestResetValue;
        }

        return Math.max(0, result) + extraCost;
    }

    @Override
    public float getSimultaneousDeployCost(GameState gameState, PhysicalCard sourceCard, PhysicalCard starship, boolean starshipForFree, float starshipChangeInCost, PhysicalCard pilot, boolean pilotForFree, float pilotChangeInCost, PhysicalCard targetCard, ReactActionOption reactActionOption, boolean includeExtraCost) {
        float extraCostForStarship = includeExtraCost ? getExtraForceRequiredToDeployToTarget(gameState, starship, targetCard, null, sourceCard, starshipForFree) : 0;
        float extraCostForPilot = includeExtraCost ? getExtraForceRequiredToDeployToTarget(gameState, pilot, starship, targetCard, sourceCard, pilotForFree) : 0;

        // Check if 'react' and 'react' is free
        if (reactActionOption != null && (reactActionOption.isForFree() || (reactActionOption.getForFreeCardFilter() != null
                && reactActionOption.getForFreeCardFilter().accepts(gameState, this, starship) && reactActionOption.getForFreeCardFilter().accepts(gameState, this, pilot)))) {
            return extraCostForStarship + extraCostForPilot;
        }

        float starshipDeployCost = 0;
        if (!starshipForFree) {
            starshipDeployCost = getDeployCost(gameState, sourceCard, starship, targetCard, false, false, null, false, starshipChangeInCost, reactActionOption, pilot, false, new ModifierCollectorImpl());
        }
        starshipDeployCost = (starshipDeployCost + extraCostForStarship);

        // Check if pilot deploys for free
        if (pilotForFree || grantedDeployForFree(gameState, pilot, starship) || grantedDeployForFree(gameState, pilot, targetCard)) {
            return starshipDeployCost + extraCostForPilot;
        }

        // Check if pilot deploys for free when simultaneously deployed with ship
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.SIMULTANEOUS_PILOT_DEPLOYS_FOR_FREE, starship)) {
            if (modifier.isAffectedPilot(gameState, this, pilot)
                    && modifier.isAffectedTarget(gameState, this, targetCard)) {
                return starshipDeployCost + extraCostForPilot;
            }
        }

        float pilotCost = 0;

        Float result = pilot.getBlueprint().getDeployCost();

        // Check if deploy cost is specified by game text
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DEPLOY_COST, pilot)) {
            result = Math.min(result != null ? result : Float.MAX_VALUE, modifier.getPrintedValueDefinedByGameText(gameState, this, pilot));
        }

        // Check if deploy cost to specific targets is specified by game text
        if (targetCard != null) {
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DEPLOY_COST_TO_TARGET, pilot)) {
                if (modifier.isDefinedDeployCostToTarget(gameState, this, targetCard)) {
                    result = Math.min(result != null ? result : Float.MAX_VALUE, modifier.getDefinedDeployCostToTarget(gameState, this, targetCard));
                }
            }
        }

        // If value if undefined, then return 0
        if (result != null) {

            // If card is a character and it is "doubled", then double the printed number
            if (pilot.getBlueprint().getCardCategory() == CardCategory.CHARACTER
                    && isDoubled(gameState, pilot)) {
                result *= 2;
            }

            String owner = starship.getOwner();
            String opponent = gameState.getOpponent(owner);
            boolean pilotDeployCostMayNotBeModified = isDeployCostNotAllowedToBeModified(gameState, starship, null);
            boolean pilotDeployCostMayNotBeModifiedByOwner = pilotDeployCostMayNotBeModified || isDeployCostNotAllowedToBeModified(gameState, pilot, owner);
            boolean pilotDeployCostMayNotBeModifiedByOpponent = pilotDeployCostMayNotBeModified || isDeployCostNotAllowedToBeModified(gameState, pilot, opponent);
            boolean pilotDeployCostMayNotBeIncreased = pilotDeployCostMayNotBeModified || isDeployCostNotAllowedToBeIncreased(gameState, pilot, null);
            boolean pilotDeployCostMayNotBeIncreasedByOwner = pilotDeployCostMayNotBeIncreased || isDeployCostNotAllowedToBeIncreased(gameState, pilot, owner);
            boolean pilotDeployCostMayNotBeIncreasedByOpponent = pilotDeployCostMayNotBeIncreased || isDeployCostNotAllowedToBeIncreased(gameState, pilot, opponent);

            float pilotTotalReduceCostModifiers = 0;

            if (!pilotDeployCostMayNotBeModified) {

                // Check for change in cost
                if (pilotChangeInCost < 0 || !pilotDeployCostMayNotBeIncreasedByOwner) {
                    result += pilotChangeInCost;
                    if (pilotChangeInCost < 0) {
                        pilotTotalReduceCostModifiers -= pilotChangeInCost;
                    }
                }

                // Deploying to collapsed site requires 1 additional Force
                if (!pilotDeployCostMayNotBeIncreased) {
                    PhysicalCard location = getLocationHere(gameState, targetCard);
                    if (location != null && location.isCollapsed()) {
                        result += 1;
                    }
                }

                // Check if deploy cost is modified by something else already in play
                for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DEPLOY_COST, pilot)) {
                    String modifierOwner = modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null;
                    if (modifierOwner == null || !(opponent.equals(modifierOwner) ? pilotDeployCostMayNotBeModifiedByOpponent : pilotDeployCostMayNotBeModifiedByOwner)) {
                        if (!isImmuneToDeployCostToTargetModifierFromCard(gameState, pilot, starship, modifier.getSource(gameState))
                                && !isImmuneToDeployCostToTargetModifierFromCard(gameState, pilot, targetCard, modifier.getSource(gameState))) {
                            float modifierValue = modifier.getDeployCostModifier(gameState, this, pilot);
                            if (modifierValue < 0 || (!pilotDeployCostMayNotBeIncreased && (modifierOwner == null || !(opponent.equals(modifierOwner) ? pilotDeployCostMayNotBeIncreasedByOpponent : pilotDeployCostMayNotBeIncreasedByOwner)))) {
                                result += modifierValue;
                                if (modifierValue < 0) {
                                    pilotTotalReduceCostModifiers -= modifierValue;
                                }
                            }
                        }
                    }
                }
                // Check if deploy cost is affected when deployed to specific targets
                for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DEPLOY_COST_TO_TARGET, starship)) {
                    String modifierOwner = modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null;
                    if (modifierOwner == null || !(opponent.equals(modifierOwner) ? pilotDeployCostMayNotBeModifiedByOpponent : pilotDeployCostMayNotBeModifiedByOwner)) {
                        if (!isImmuneToDeployCostToTargetModifierFromCard(gameState, pilot, starship, modifier.getSource(gameState))) {
                            float modifierValue = modifier.getDeployCostToTargetModifier(gameState, this, pilot, starship);
                            if (modifierValue < 0 || (!pilotDeployCostMayNotBeIncreased && (modifierOwner == null || !(opponent.equals(modifierOwner) ? pilotDeployCostMayNotBeIncreasedByOpponent : pilotDeployCostMayNotBeIncreasedByOwner)))) {
                                result += modifierValue;
                                if (modifierValue < 0) {
                                    pilotTotalReduceCostModifiers -= modifierValue;
                                }
                            }
                        }
                    }
                }
                for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DEPLOY_COST_TO_TARGET, targetCard)) {
                    String modifierOwner = modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null;
                    if (modifierOwner == null || !(opponent.equals(modifierOwner) ? pilotDeployCostMayNotBeModifiedByOpponent : pilotDeployCostMayNotBeModifiedByOwner)) {
                        if (!isImmuneToDeployCostToTargetModifierFromCard(gameState, pilot, targetCard, modifier.getSource(gameState))) {
                            float modifierValue = modifier.getDeployCostToTargetModifier(gameState, this, pilot, targetCard);
                            if (modifierValue < 0 || (!pilotDeployCostMayNotBeIncreased && (modifierOwner == null || !(opponent.equals(modifierOwner) ? pilotDeployCostMayNotBeIncreasedByOpponent : pilotDeployCostMayNotBeIncreasedByOwner)))) {
                                result += modifierValue;
                                if (modifierValue < 0) {
                                    pilotTotalReduceCostModifiers -= modifierValue;
                                }
                            }
                        }
                    }
                }
                // Check if deploy cost is affected when simultaneously deployed with ship
                for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.SIMULTANEOUS_PILOT_DEPLOY_COST, starship)) {
                    String modifierOwner = modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null;
                    if (modifierOwner == null || !(opponent.equals(modifierOwner) ? pilotDeployCostMayNotBeModifiedByOpponent : pilotDeployCostMayNotBeModifiedByOwner)) {
                        if (modifier.isAffectedPilot(gameState, this, pilot)
                                && modifier.isAffectedTarget(gameState, this, targetCard)) {
                            float modifierValue = modifier.getDeployCostToTargetModifier(gameState, this, pilot, targetCard);
                            if (modifierValue < 0 || (!pilotDeployCostMayNotBeIncreased && (modifierOwner == null || !(opponent.equals(modifierOwner) ? pilotDeployCostMayNotBeIncreasedByOpponent : pilotDeployCostMayNotBeIncreasedByOwner)))) {
                                result += modifierValue;
                                if (modifierValue < 0) {
                                    pilotTotalReduceCostModifiers -= modifierValue;
                                }
                            }
                        }
                    }
                }
                // From self
                for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.SELF_DEPLOY_COST_TO_TARGET, pilot)) {

                    float modifierValue = modifier.getDeployCostToTargetModifier(gameState, this, pilot, starship);
                    if (modifierValue < 0 || !pilotDeployCostMayNotBeIncreasedByOwner) {
                        result += modifierValue;
                        if (modifierValue < 0) {
                            pilotTotalReduceCostModifiers -= modifierValue;
                        }
                    }
                    modifierValue = modifier.getDeployCostToTargetModifier(gameState, this, pilot, targetCard);
                    if (modifierValue < 0 || !pilotDeployCostMayNotBeIncreasedByOwner) {
                        result += modifierValue;
                        if (modifierValue < 0) {
                            pilotTotalReduceCostModifiers -= modifierValue;
                        }
                    }
                }
            }

            // Check the most that the deploy cost can be modified (reduced) by
            float pilotMaxToReduceCostBy = Float.MAX_VALUE;
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAX_AMOUNT_TO_REDUCE_DEPLOY_COST_BY, pilot)) {
                pilotMaxToReduceCostBy = Math.max(0, Math.min(pilotMaxToReduceCostBy, modifier.getMaximumToReduceDeployCostBy(gameState, this, pilot)));
            }
            if (pilotMaxToReduceCostBy != Float.MAX_VALUE) {
                result += Math.max(0, pilotTotalReduceCostModifiers - pilotMaxToReduceCostBy);
            }

            // Check if value was reset to an "unmodifiable value", and use lowest found
            Float lowestResetValue = null;
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_DEPLOY_COST_TO_TARGET, pilot)) {
                if (modifier.isAffectedTarget(gameState, this, starship)
                        || modifier.isAffectedTarget(gameState, this, targetCard)) {
                    float modifierAmount = modifier.getValue(gameState, this, pilot);
                    lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
                }
            }
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_DEPLOY_COST, pilot)) {
                float modifierAmount = modifier.getValue(gameState, this, pilot);
                lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
            }
            if (lowestResetValue != null) {
                result = lowestResetValue;
            }

            pilotCost = result;
        }

        return starshipDeployCost + Math.max(0, pilotCost) + extraCostForPilot;
    }

    /**
     * Gets the amount of Force needed for the card to be transferred to the target.
     * @param gameState the game state
     * @param cardToTransfer the card to be transferred
     * @param target the target
     * @param playCardOption the play card option chosen
     * @return the transfer cost
     */
    @Override
    public float getTransferCost(GameState gameState, PhysicalCard cardToTransfer, PhysicalCard target, PlayCardOption playCardOption) {
        // Check if transfers for free to target
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TRANSFERS_FREE_TO_TARGET, cardToTransfer)) {
            if (modifier.isAffectedTarget(gameState, this, target)) {
                return 0;
            }
        }
        return getDeployCost(gameState, cardToTransfer, cardToTransfer, target, false, playCardOption, false, 0, null, false);
    }

    @Override
    public boolean isDeployUsingBothForcePiles(GameState gameState, PhysicalCard physicalCard) {
        return physicalCard.getBlueprint().isDeployUsingBothForcePiles();
    }

    @Override
    public boolean isDeployUsingBothForcePiles(GameState gameState, PhysicalCard physicalCard, PhysicalCard targetCard) {
        boolean result = physicalCard.getBlueprint().isDeployUsingBothForcePiles();
        if (result) {
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DEPLOY_ONLY_USING_OWN_FORCE_TO_TARGET, physicalCard)) {
                if (modifier.isAffectedTarget(gameState, this, targetCard))
                    return false;
            }
        }
        return result;
    }

    /**
     * Gets the deploy cost for a card if the card's deploy cost is determined by a calculation, instead of normally.
     * @param gameState the game state
     * @param card a card
     * @return the deploy cost as determined by a calculation, otherwise null
     */
    @Override
    public Float getDeployCostFromCalculation(GameState gameState, PhysicalCard card) {
        return getDeployCostFromCalculation(gameState, card, new ModifierCollectorImpl());
    }

    /**
     * Gets the deploy cost for a card if the card's deploy cost is determined by a calculation, instead of normally.
     * @param gameState the game state
     * @param card a card
     * @return the deploy cost as determined by a calculation, otherwise null
     */
    @Override
    public Float getDeployCostFromCalculation(GameState gameState, PhysicalCard card, ModifierCollector modifierCollector) {
        Float value = null;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.USE_CALCULATION_FOR_DEPLOY_COST, card)) {
            float curValue = modifier.getValue(gameState, this, card);
            if (value == null || curValue < value) {
                value = curValue;
                modifierCollector.addModifier(modifier);
            }
        }
        if (value != null) {
            return Math.max(0, value);
        }
        return null;
    }

    /**
     * Gets the amount of Force needed to move the card using landspeed.
     * @param gameState the game state
     * @param card the card to move using landspeed
     * @param fromSite the site to move from
     * @param toSite the site to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    @Override
    public float getMoveUsingLandspeedCost(GameState gameState, PhysicalCard card, PhysicalCard fromSite, PhysicalCard toSite, boolean asReact, float changeInCost) {

        // Check if moves for free
        if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
            return 0;

        // Check if moves for free using landspeed
        if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_USING_LANDSPEED, card).isEmpty())
            return 0;

        // Check if moves for free from location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION, card)) {
            if (modifier.isMoveFreeFromLocation(gameState, this, fromSite)) {
                return 0;
            }
        }

        // Check if moves for free from location using landspeed
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_USING_LANDSPEED, card)) {
            if (modifier.isMoveFreeFromLocation(gameState, this, fromSite)) {
                return 0;
            }
        }

        // Check if moves for free from location to location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION, card)) {
            if (modifier.isMoveFreeFromLocationToLocation(gameState, this, fromSite, toSite)) {
                return 0;
            }
        }

        // Check if moves for free from location to location using landspeed
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION_USING_LANDSPEED, card)) {
            if (modifier.isMoveFreeFromLocationToLocation(gameState, this, fromSite, toSite)) {
                return 0;
            }
        }

        // Check if moves for free to location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION, card)) {
            if (modifier.isMoveFreeToLocation(gameState, this, toSite)) {
                return 0;
            }
        }

        // Check if moves for free to location using landspeed
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION_USING_LANDSPEED, card)) {
            if (modifier.isMoveFreeToLocation(gameState, this, toSite)) {
                return 0;
            }
        }

        // Check if moves for free when moving toward a location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TOWARD_TARGET, card)) {
            if (modifier.isMovingTowardTarget(gameState, this, card, toSite)) {
                return 0;
            }
        }

        // Default move using landspeed cost
        float result = 1 + changeInCost;

        // Check for modifiers to move cost
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST, card)) {
            result += modifier.getMoveCostModifier(gameState, this, card);
        }

        // Check for modifiers to move cost using hyperspeed
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_USING_LANDSPEED, card)) {
            result += modifier.getMoveCostModifier(gameState, this, card);
        }

        // Moving from collapsed site requires 1 additional Force
        if (fromSite.isCollapsed()) {
            result += 1;
        }

        // Check for modifiers to move cost when moving from location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION, fromSite)) {
            result += modifier.getMoveCostFromLocationModifier(gameState, this, card, fromSite);
        }

        // Check for modifiers to move cost when moving from location using landspeed
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_USING_LANDSPEED, fromSite)) {
            result += modifier.getMoveCostFromLocationModifier(gameState, this, card, fromSite);
        }

        // Check for modifiers to move cost when moving from location to location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION, card)) {
            result += modifier.getMoveCostFromLocationToLocationModifier(gameState, this, card, fromSite, toSite);
        }

        // Check for modifiers to move cost when moving from location to location using landspeed
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION_USING_LANDSPEED, card)) {
            result += modifier.getMoveCostFromLocationToLocationModifier(gameState, this, card, fromSite, toSite);
        }

        // Moving to collapsed site requires 1 additional Force
        if (toSite.isCollapsed()) {
            result += 1;
        }

        // Check for modifiers to move cost when moving to location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION, toSite)) {
            result += modifier.getMoveCostToLocationModifier(gameState, this, card, toSite);
        }

        // Check for modifiers to move cost when moving to location using landspeed
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION_USING_LANDSPEED, toSite)) {
            result += modifier.getMoveCostToLocationModifier(gameState, this, card, toSite);
        }

        return Math.max(0, result);
    }

    /**
     * Gets the amount of Force needed to move the card using hyperspeed.
     * @param gameState the game state
     * @param card the card to move using hyperspeed
     * @param fromSystem the system to move from
     * @param toSystem the system to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    @Override
    public float getMoveUsingHyperspeedCost(GameState gameState, PhysicalCard card, PhysicalCard fromSystem, PhysicalCard toSystem, boolean asReact, float changeInCost) {

        // Check if moves for free
        if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
            return 0;

        // Check if moves for free using hyperspeed
        if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_USING_LANDSPEED, card).isEmpty())
            return 0;

        if (fromSystem != null) {
            // Check if moves for free from location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION, card)) {
                if (modifier.isMoveFreeFromLocation(gameState, this, fromSystem)) {
                    return 0;
                }
            }

            // Check if moves for free from location using hyperspeed
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_USING_HYPERSPEED, card)) {
                if (modifier.isMoveFreeFromLocation(gameState, this, fromSystem)) {
                    return 0;
                }
            }
        }

        if (fromSystem != null && toSystem != null) {
            // Check if moves for free from location to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION, card)) {
                if (modifier.isMoveFreeFromLocationToLocation(gameState, this, fromSystem, toSystem)) {
                    return 0;
                }
            }

            // Check if moves for free from location to location using hyperspeed
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION_USING_HYPERSPEED, card)) {
                if (modifier.isMoveFreeFromLocationToLocation(gameState, this, fromSystem, toSystem)) {
                    return 0;
                }
            }
        }

        if (toSystem != null) {
            // Check if moves for free to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION, card)) {
                if (modifier.isMoveFreeToLocation(gameState, this, toSystem)) {
                    return 0;
                }
            }

            // Check if moves for free to location using hyperspeed
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION_USING_HYPERSPEED, card)) {
                if (modifier.isMoveFreeToLocation(gameState, this, toSystem)) {
                    return 0;
                }
            }
        }

        // Default move using hyperspeed cost
        float result = 1 + changeInCost;

        // Check for modifiers to move cost
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST, card)) {
            result += modifier.getMoveCostModifier(gameState, this, card);
        }

        // Check for modifiers to move cost using hyperspeed
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_USING_HYPERSPEED, card)) {
            result += modifier.getMoveCostModifier(gameState, this, card);
        }

        if (fromSystem != null) {
            // Check for modifiers to move cost when moving from location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION, fromSystem)) {
                result += modifier.getMoveCostFromLocationModifier(gameState, this, card, fromSystem);
            }

            // Check for modifiers to move cost when moving from location using hyperspeed
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_USING_HYPERSPEED, fromSystem)) {
                result += modifier.getMoveCostFromLocationModifier(gameState, this, card, fromSystem);
            }
        }

        if (fromSystem != null && toSystem != null) {
            // Check for modifiers to move cost when moving from location to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION, card)) {
                result += modifier.getMoveCostFromLocationToLocationModifier(gameState, this, card, fromSystem, toSystem);
            }

            // Check for modifiers to move cost when moving from location to location using hyperspeed
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION_USING_HYPERSPEED, card)) {
                result += modifier.getMoveCostFromLocationToLocationModifier(gameState, this, card, fromSystem, toSystem);
            }
        }

        if (toSystem != null) {
            // Check for modifiers to move cost when moving to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION, toSystem)) {
                result += modifier.getMoveCostToLocationModifier(gameState, this, card, toSystem);
            }

            // Check for modifiers to move cost when moving to location using hyperspeed
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION_USING_HYPERSPEED, toSystem)) {
                result += modifier.getMoveCostToLocationModifier(gameState, this, card, toSystem);
            }
        }

        return Math.max(0, result);
    }

    /**
     * Gets the amount of Force needed to move the card without using hyperspeed.
     * @param gameState the game state
     * @param card the card to move without using hyperspeed
     * @param fromSystem the system to move from
     * @param toSystem the system to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    @Override
    public float getMoveWithoutUsingHyperspeedCost(GameState gameState, PhysicalCard card, PhysicalCard fromSystem, PhysicalCard toSystem, boolean asReact, float changeInCost) {

        // Check if moves for free
        if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
            return 0;

        if (fromSystem != null) {
            // Check if moves for free from location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION, card)) {
                if (modifier.isMoveFreeFromLocation(gameState, this, fromSystem)) {
                    return 0;
                }
            }
        }

        if (fromSystem != null && toSystem != null) {
            // Check if moves for free from location to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION, card)) {
                if (modifier.isMoveFreeFromLocationToLocation(gameState, this, fromSystem, toSystem)) {
                    return 0;
                }
            }
        }

        if (toSystem != null) {
            // Check if moves for free to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION, card)) {
                if (modifier.isMoveFreeToLocation(gameState, this, toSystem)) {
                    return 0;
                }
            }
        }

        // Default move without using hyperspeed cost
        float result = 1 + changeInCost;

        // Check for modifiers to move cost
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST, card)) {
            result += modifier.getMoveCostModifier(gameState, this, card);
        }

        if (fromSystem != null) {
            // Check for modifiers to move cost when moving from location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION, fromSystem)) {
                result += modifier.getMoveCostFromLocationModifier(gameState, this, card, fromSystem);
            }
        }

        if (fromSystem != null && toSystem != null) {
            // Check for modifiers to move cost when moving from location to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION, card)) {
                result += modifier.getMoveCostFromLocationToLocationModifier(gameState, this, card, fromSystem, toSystem);
            }
        }

        if (toSystem != null) {
            // Check for modifiers to move cost when moving to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION, toSystem)) {
                result += modifier.getMoveCostToLocationModifier(gameState, this, card, toSystem);
            }
        }

        return Math.max(0, result);
    }

    /**
     * Gets the amount of Force needed to move the card using sector movement.
     * @param gameState the game state
     * @param card the card to move using sector movement
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    @Override
    public float getMoveUsingSectorMovementCost(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean asReact, float changeInCost) {

        // Check if moves for free
        if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
            return 0;

        // Check if moves for free from location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION, card)) {
            if (modifier.isMoveFreeFromLocation(gameState, this, fromLocation)) {
                return 0;
            }
        }

        // Check if moves for free from location to location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION, card)) {
            if (modifier.isMoveFreeFromLocationToLocation(gameState, this, fromLocation, toLocation)) {
                return 0;
            }
        }

        // Check if moves for free to location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION, card)) {
            if (modifier.isMoveFreeToLocation(gameState, this, toLocation)) {
                return 0;
            }
        }

        // Dark side starfighters moving (as first regular move) from Death Star II sector during move phase is free
        // when there are no Light side starfighters at Death Star II sectors.
        if (card.getOwner().equals(gameState.getDarkPlayer())
                && Filters.starfighter.accepts(gameState, this, card)
                && Filters.Death_Star_II_sector.accepts(gameState, this, fromLocation)) {

            // Check if no Light side starfighters at Death Star II sectors.
            if (gameState.getCurrentPlayerId().equals(card.getOwner())
                    && gameState.getCurrentPhase() == Phase.MOVE
                    && !hasPerformedRegularMoveThisTurn(card)
                    && !Filters.canSpot(gameState.getGame(), null, Filters.and(Filters.owner(gameState.getLightPlayer()), Filters.starfighter, Filters.at(Filters.Death_Star_II_sector)))) {
                return 0;
            }
        }

        // Default move using sector movement cost
        float result = 1 + changeInCost;

        // Check for modifiers to move cost
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST, card)) {
            result += modifier.getMoveCostModifier(gameState, this, card);
        }

        // Check for modifiers to move cost when moving from location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION, fromLocation)) {
            result += modifier.getMoveCostFromLocationModifier(gameState, this, card, fromLocation);
        }

        // Check for modifiers to move cost when moving from location to location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION, card)) {
            result += modifier.getMoveCostFromLocationToLocationModifier(gameState, this, card, fromLocation, toLocation);
        }

        // Check for modifiers to move cost when moving to location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION, toLocation)) {
            result += modifier.getMoveCostToLocationModifier(gameState, this, card, toLocation);
        }

        return Math.max(0, result);
    }

    /**
     * Gets the amount of Force needed to land the card.
     * @param gameState the game state
     * @param card the card to land
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    @Override
    public float getLandingCost(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean asReact, float changeInCost) {

        // Check if landing to docking bay, which is free
        if (Filters.docking_bay.accepts(gameState, this, toLocation))
            return 0;

        // Check if moves for free
        if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
            return 0;

        // Check if lands for free
        if (!getModifiersAffectingCard(gameState, ModifierType.LANDS_FOR_FREE, card).isEmpty())
            return 0;

        if (fromLocation != null) {
            // Check if moves for free from location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION, card)) {
                if (modifier.isMoveFreeFromLocation(gameState, this, fromLocation)) {
                    return 0;
                }
            }

            // Check if lands for free from location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.LANDS_FOR_FREE_FROM_LOCATION, card)) {
                if (modifier.isMoveFreeFromLocation(gameState, this, fromLocation)) {
                    return 0;
                }
            }

            // Check if lands for free from location (instead of related starship)
            if (mayShuttleTransferLandAndTakeOffForFreeAtInsteadOfRelatedStarship(gameState, card.getOwner(), fromLocation)) {
                return 0;
            }
        }

        if (fromLocation != null && toLocation != null) {
            // Check if moves for free from location to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION, card)) {
                if (modifier.isMoveFreeFromLocationToLocation(gameState, this, fromLocation, toLocation)) {
                    return 0;
                }
            }

            // Check if lands for free from location to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.LANDS_FOR_FREE_FROM_LOCATION_TO_LOCATION, card)) {
                if (modifier.isMoveFreeFromLocationToLocation(gameState, this, fromLocation, toLocation)) {
                    return 0;
                }
            }
        }

        if (toLocation != null) {
            // Check if moves for free to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION, card)) {
                if (modifier.isMoveFreeToLocation(gameState, this, toLocation)) {
                    return 0;
                }
            }

            // Check if lands for free to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.LANDS_FOR_FREE_TO_LOCATION, card)) {
                if (modifier.isMoveFreeToLocation(gameState, this, toLocation)) {
                    return 0;
                }
            }

            // Check if lands for free to location (instead of related starship)
            if (mayShuttleTransferLandAndTakeOffForFreeAtInsteadOfRelatedStarship(gameState, card.getOwner(), toLocation)) {
                return 0;
            }
        }

        // Default landing cost
        float result = 1 + changeInCost;

        // Check for modifiers to move cost
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST, card)) {
            result += modifier.getMoveCostModifier(gameState, this, card);
        }

        if (fromLocation != null) {
            // Moving from collapsed site requires 1 additional Force
            if (fromLocation.isCollapsed()) {
                result += 1;
            }

            // Check for modifiers to move cost when moving from location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION, fromLocation)) {
                result += modifier.getMoveCostFromLocationModifier(gameState, this, card, fromLocation);
            }
        }

        if (fromLocation != null && toLocation != null) {
            // Check for modifiers to move cost when moving from location to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION, card)) {
                result += modifier.getMoveCostFromLocationToLocationModifier(gameState, this, card, fromLocation, toLocation);
            }
        }

        if (toLocation != null) {
            // Moving to collapsed site requires 1 additional Force
            if (toLocation.isCollapsed()) {
                result += 1;
            }

            // Check for modifiers to move cost when moving to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION, toLocation)) {
                result += modifier.getMoveCostToLocationModifier(gameState, this, card, toLocation);
            }
        }

        return Math.max(0, result);
    }

    /**
     * Gets the amount of Force needed for the card to take off.
     * @param gameState the game state
     * @param card the card to take off
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    @Override
    public float getTakeOffCost(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean asReact, float changeInCost) {

        // Check if taking off from docking bay, which is free
        if (Filters.docking_bay.accepts(gameState, this, fromLocation))
            return 0;

        // Check if moves for free
        if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
            return 0;

        // Check if takes off for free
        if (!getModifiersAffectingCard(gameState, ModifierType.TAKES_OFF_FOR_FREE, card).isEmpty())
            return 0;

        if (fromLocation != null) {
            // Check if moves for free from location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION, card)) {
                if (modifier.isMoveFreeFromLocation(gameState, this, fromLocation)) {
                    return 0;
                }
            }

            // Check if takes off for free from location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TAKES_OFF_FOR_FREE_FROM_LOCATION, card)) {
                if (modifier.isMoveFreeFromLocation(gameState, this, fromLocation)) {
                    return 0;
                }
            }

            // Check if take off for free from location (instead of related starship)
            if (mayShuttleTransferLandAndTakeOffForFreeAtInsteadOfRelatedStarship(gameState, card.getOwner(), fromLocation)) {
                return 0;
            }
        }

        if (fromLocation != null && toLocation != null) {
            // Check if moves for free from location to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION, card)) {
                if (modifier.isMoveFreeFromLocationToLocation(gameState, this, fromLocation, toLocation)) {
                    return 0;
                }
            }

            // Check if takes off for free from location to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TAKES_OFF_FOR_FREE_FROM_LOCATION_TO_LOCATION, card)) {
                if (modifier.isMoveFreeFromLocationToLocation(gameState, this, fromLocation, toLocation)) {
                    return 0;
                }
            }
        }

        if (toLocation != null) {
            // Check if moves for free to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION, card)) {
                if (modifier.isMoveFreeToLocation(gameState, this, toLocation)) {
                    return 0;
                }
            }

            // Check if takes off for free to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TAKES_OFF_FOR_FREE_TO_LOCATION, card)) {
                if (modifier.isMoveFreeToLocation(gameState, this, toLocation)) {
                    return 0;
                }
            }

            // Check if take off for free to location (instead of related starship)
            if (mayShuttleTransferLandAndTakeOffForFreeAtInsteadOfRelatedStarship(gameState, card.getOwner(), toLocation)) {
                return 0;
            }
        }

        // Default taking off cost
        float result = 1 + changeInCost;

        // Check for modifiers to move cost
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST, card)) {
            result += modifier.getMoveCostModifier(gameState, this, card);
        }

        if (fromLocation != null) {
            // Moving from collapsed site requires 1 additional Force
            if (fromLocation.isCollapsed()) {
                result += 1;
            }

            // Check for modifiers to move cost when moving from location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION, fromLocation)) {
                result += modifier.getMoveCostFromLocationModifier(gameState, this, card, fromLocation);
            }
        }

        if (fromLocation != null && toLocation != null) {
            // Check for modifiers to move cost when moving from location to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION, card)) {
                result += modifier.getMoveCostFromLocationToLocationModifier(gameState, this, card, fromLocation, toLocation);
            }
        }

        if (toLocation != null) {
            // Moving to collapsed site requires 1 additional Force
            if (toLocation.isCollapsed()) {
                result += 1;
            }

            // Check for modifiers to move cost when moving to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION, toLocation)) {
                result += modifier.getMoveCostToLocationModifier(gameState, this, card, toLocation);
            }
        }

        return Math.max(0, result);
    }

    /**
     * Gets the amount of Force needed for the card to enter the starship/vehicle site.
     * @param gameState the game state
     * @param card the card to move
     * @param fromSite the site to move from
     * @param toSite the starship/vehicle site to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    @Override
    public float getEnterStarshipOrVehicleSiteCost(GameState gameState, PhysicalCard card, PhysicalCard fromSite, PhysicalCard toSite, boolean asReact, float changeInCost) {

        // Check if moves for free
        if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
            return 0;

        if (fromSite != null) {
            // Check if moves for free from location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION, card)) {
                if (modifier.isMoveFreeFromLocation(gameState, this, fromSite)) {
                    return 0;
                }
            }
        }

        if (fromSite != null && toSite != null) {
            // Check if moves for free from location to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION, card)) {
                if (modifier.isMoveFreeFromLocationToLocation(gameState, this, fromSite, toSite)) {
                    return 0;
                }
            }
        }

        if (toSite != null) {
            // Check if moves for free to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION, card)) {
                if (modifier.isMoveFreeToLocation(gameState, this, toSite)) {
                    return 0;
                }
            }
        }

        // Default enter starship/vehicle site cost
        float result = 0 + changeInCost;

        // Check for defined enter starship/vehicle site cost
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.ENTER_EXIT_COST, toSite)) {
            if (modifier.isForPlayer(card.getOwner())) {
                result = modifier.getPrintedValueDefinedByGameText(gameState, this, toSite);
            }
        }

        // Check for modifiers to move cost
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST, card)) {
            result += modifier.getMoveCostModifier(gameState, this, card);
        }

        if (fromSite != null) {
            // Moving from collapsed site requires 1 additional Force
            if (fromSite.isCollapsed()) {
                result += 1;
            }

            // Check for modifiers to move cost when moving from location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION, fromSite)) {
                result += modifier.getMoveCostFromLocationModifier(gameState, this, card, fromSite);
            }
        }

        if (fromSite != null && toSite != null) {
            // Check for modifiers to move cost when moving from location to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION, card)) {
                result += modifier.getMoveCostFromLocationToLocationModifier(gameState, this, card, fromSite, toSite);
            }
        }

        if (toSite != null) {
            // Moving to collapsed site requires 1 additional Force
            if (toSite.isCollapsed()) {
                result += 1;
            }

            // Check for modifiers to move cost when moving to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION, toSite)) {
                result += modifier.getMoveCostToLocationModifier(gameState, this, card, toSite);
            }
        }

        return Math.max(0, result);
    }

    /**
     * Gets the amount of Force needed for the card to exit the starship/vehicle site.
     * @param gameState the game state
     * @param card the card to move
     * @param fromSite the starship/vehicle site to move from
     * @param toSite the site to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    @Override
    public float getExitStarshipOrVehicleSiteCost(GameState gameState, PhysicalCard card, PhysicalCard fromSite, PhysicalCard toSite, boolean asReact, float changeInCost) {

        // Check if moves for free
        if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
            return 0;

        if (fromSite != null) {
            // Check if moves for free from location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION, card)) {
                if (modifier.isMoveFreeFromLocation(gameState, this, fromSite)) {
                    return 0;
                }
            }
        }

        if (fromSite != null && toSite != null) {
            // Check if moves for free from location to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION, card)) {
                if (modifier.isMoveFreeFromLocationToLocation(gameState, this, fromSite, toSite)) {
                    return 0;
                }
            }
        }

        if (toSite != null) {
            // Check if moves for free to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION, card)) {
                if (modifier.isMoveFreeToLocation(gameState, this, toSite)) {
                    return 0;
                }
            }
        }

        // Default exit starship/vehicle site cost
        float result = 0 + changeInCost;

        // Check for defined exit starship/vehicle site cost
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.ENTER_EXIT_COST, fromSite)) {
            if (modifier.isForPlayer(card.getOwner())) {
                result = modifier.getPrintedValueDefinedByGameText(gameState, this, fromSite);
            }
        }

        // Check for modifiers to move cost
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST, card)) {
            result += modifier.getMoveCostModifier(gameState, this, card);
        }

        if (fromSite != null) {
            // Moving from collapsed site requires 1 additional Force
            if (fromSite.isCollapsed()) {
                result += 1;
            }

            // Check for modifiers to move cost when moving from location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION, fromSite)) {
                result += modifier.getMoveCostFromLocationModifier(gameState, this, card, fromSite);
            }
        }

        if (fromSite != null && toSite != null) {
            // Check for modifiers to move cost when moving from location to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION, card)) {
                result += modifier.getMoveCostFromLocationToLocationModifier(gameState, this, card, fromSite, toSite);
            }
        }

        if (toSite != null) {
            // Moving to collapsed site requires 1 additional Force
            if (toSite.isCollapsed()) {
                result += 1;
            }

            // Check for modifiers to move cost when moving to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION, toSite)) {
                result += modifier.getMoveCostToLocationModifier(gameState, this, card, toSite);
            }
        }

        return Math.max(0, result);
    }

    /**
     * Gets the amount of Force needed for the card to move to start a Bombing Run.
     * @param gameState the game state
     * @param card the card to move
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    @Override
    public float getMoveToStartBombingRunCost(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, float changeInCost) {

        // Check if moves for free
        if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
            return 0;

        if (fromLocation != null) {
            // Check if moves for free from location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION, card)) {
                if (modifier.isMoveFreeFromLocation(gameState, this, fromLocation)) {
                    return 0;
                }
            }
        }

        if (fromLocation != null && toLocation != null) {
            // Check if moves for free from location to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION, card)) {
                if (modifier.isMoveFreeFromLocationToLocation(gameState, this, fromLocation, toLocation)) {
                    return 0;
                }
            }
        }

        if (toLocation != null) {
            // Check if moves for free to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION, card)) {
                if (modifier.isMoveFreeToLocation(gameState, this, toLocation)) {
                    return 0;
                }
            }
        }

        // Default move to start Bombing Run cost
        float result = 1 + changeInCost;

        // Add 1 to move to start Bombing Run cost for each cloud sector between locations
        result += Filters.filter(getSectorsBetween(gameState, fromLocation, toLocation), gameState.getGame(), Filters.cloud_sector).size();

        // Check for modifiers to move cost
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST, card)) {
            result += modifier.getMoveCostModifier(gameState, this, card);
        }

        if (fromLocation != null) {
            // Moving from collapsed site requires 1 additional Force
            if (fromLocation.isCollapsed()) {
                result += 1;
            }

            // Check for modifiers to move cost when moving from location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION, fromLocation)) {
                result += modifier.getMoveCostFromLocationModifier(gameState, this, card, fromLocation);
            }
        }

        if (fromLocation != null && toLocation != null) {
            // Check for modifiers to move cost when moving from location to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION, card)) {
                result += modifier.getMoveCostFromLocationToLocationModifier(gameState, this, card, fromLocation, toLocation);
            }
        }

        if (toLocation != null) {
            // Moving to collapsed site requires 1 additional Force
            if (toLocation.isCollapsed()) {
                result += 1;
            }

            // Check for modifiers to move cost when moving to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION, toLocation)) {
                result += modifier.getMoveCostToLocationModifier(gameState, this, card, toLocation);
            }
        }

        return Math.max(0, result);
    }

    /**
     * Gets the amount of Force needed for the card to be shuttled.
     * @param gameState the game state
     * @param card the card to be shuttled
     * @param fromLocation the location to shuttle from (or location the starship is at if shuttling from a starship)
     * @param toLocation the location to shuttle to (or location the starship is at if shuttling to a starship)
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    @Override
    public float getShuttleCost(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, float changeInCost) {

        // Check if moves for free
        if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
            return 0;

        // Check if shuttles for free
        if (!getModifiersAffectingCard(gameState, ModifierType.SHUTTLES_FOR_FREE, card).isEmpty())
            return 0;

        if (fromLocation != null) {
            // Check if moves for free from location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION, card)) {
                if (modifier.isMoveFreeFromLocation(gameState, this, fromLocation)) {
                    return 0;
                }
            }

            // Check if shuttles for free from location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.SHUTTLES_FOR_FREE_FROM_LOCATION, card)) {
                if (modifier.isMoveFreeFromLocation(gameState, this, fromLocation)) {
                    return 0;
                }
            }

            // Check if shuttle for free from location (instead of related starship)
            if (mayShuttleTransferLandAndTakeOffForFreeAtInsteadOfRelatedStarship(gameState, card.getOwner(), fromLocation)) {
                return 0;
            }
        }

        if (fromLocation != null && toLocation != null) {
            // Check if moves for free from location to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION, card)) {
                if (modifier.isMoveFreeFromLocationToLocation(gameState, this, fromLocation, toLocation)) {
                    return 0;
                }
            }

            // Check if shuttles for free from location to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.SHUTTLES_FOR_FREE_FROM_LOCATION_TO_LOCATION, card)) {
                if (modifier.isMoveFreeFromLocationToLocation(gameState, this, fromLocation, toLocation)) {
                    return 0;
                }
            }
        }

        if (toLocation != null) {
            // Check if moves for free to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION, card)) {
                if (modifier.isMoveFreeToLocation(gameState, this, toLocation)) {
                    return 0;
                }
            }

            // Check if shuttles for free to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.SHUTTLES_FOR_FREE_TO_LOCATION, card)) {
                if (modifier.isMoveFreeToLocation(gameState, this, toLocation)) {
                    return 0;
                }
            }

            // Check if shuttle for free to location (instead of related starship)
            if (mayShuttleTransferLandAndTakeOffForFreeAtInsteadOfRelatedStarship(gameState, card.getOwner(), toLocation)) {
                return 0;
            }
        }

        // Default shuttling cost
        float result = 1 + changeInCost;

        // Add 1 to shuttling cost for each cloud sector between locations
        result += Filters.filter(getSectorsBetween(gameState, fromLocation, toLocation), gameState.getGame(), Filters.cloud_sector).size();

        // Check for modifiers to move cost
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST, card)) {
            result += modifier.getMoveCostModifier(gameState, this, card);
        }

        if (fromLocation != null) {
            // Moving from collapsed site requires 1 additional Force
            if (fromLocation.isCollapsed()) {
                result += 1;
            }

            // Check for modifiers to move cost when moving from location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION, fromLocation)) {
                result += modifier.getMoveCostFromLocationModifier(gameState, this, card, fromLocation);
            }
        }

        if (fromLocation != null && toLocation != null) {
            // Check for modifiers to move cost when moving from location to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION, card)) {
                result += modifier.getMoveCostFromLocationToLocationModifier(gameState, this, card, fromLocation, toLocation);
            }
        }

        if (toLocation != null) {
            // Moving to collapsed site requires 1 additional Force
            if (toLocation.isCollapsed()) {
                result += 1;
            }

            // Check for modifiers to move cost when moving to location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION, toLocation)) {
                result += modifier.getMoveCostToLocationModifier(gameState, this, card, toLocation);
            }
        }

        return Math.max(0, result);
    }

    /**
     * Gets the amount of Force needed for the card to embark.
     * @param gameState the game state
     * @param card the card to move
     * @param moveTo the card to embark on
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    @Override
    public float getEmbarkingCost(GameState gameState, PhysicalCard card, PhysicalCard moveTo, float changeInCost) {

        // Check if this is a embark on 'crashed' enclosed vehicle
        if (moveTo.isCrashed() && moveTo.getBlueprint().hasKeyword(Keyword.ENCLOSED)) {

            // Check if moves for free
            if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
                return 0;

            return Math.max(1, changeInCost);
        }

        return Math.max(0, changeInCost);
    }

    /**
     * Gets the amount of Force needed for the card to disembark.
     * @param gameState the game state
     * @param card the card to move
     * @param moveTo the card to disembark to
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    @Override
    public float getDisembarkingCost(GameState gameState, PhysicalCard card, PhysicalCard moveTo, float changeInCost) {

        // Check if this is a disembark from a 'crashed' enclosed vehicle
        if (card.getAttachedTo() != null
                && card.getAttachedTo().isCrashed() && card.getAttachedTo().getBlueprint().hasKeyword(Keyword.ENCLOSED)) {

            // Check if moves for free
            if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
                return 0;

            return Math.max(1, changeInCost);
        }

        return Math.max(0, changeInCost);
    }

    /**
     * Determines if characters aboard vehicle may "jump off" when vehicle is about to be lost.
     * @param gameState the game state
     * @param card the vehicle
     * @return true if characters may "jump off", otherwise false
     */
    @Override
    public boolean allowsCharactersAboardToJumpOff(GameState gameState, PhysicalCard card) {
        return (!getModifiersAffectingCard(gameState, ModifierType.CHARACTERS_ABOARD_MAY_JUMP_OFF, card).isEmpty());
    }

    /**
     * Gets the amount of Force needed for the cards to ship-dock.
     * @param gameState the game state
     * @param starship1 a starship to ship-dock
     * @param starship2 a starship to ship-dock
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the ship-docking cost
     */
    @Override
    public float getShipdockingCost(GameState gameState, PhysicalCard starship1, PhysicalCard starship2, float changeInCost) {
        // Check if either starship moves for free
        if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, starship1).isEmpty())
            return 0;

        if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, starship2).isEmpty())
            return 0;

        // Check if either starship ship-docks for free
        if (!getModifiersAffectingCard(gameState, ModifierType.SHIPDOCKS_FOR_FREE, starship1).isEmpty())
            return 0;

        if (!getModifiersAffectingCard(gameState, ModifierType.SHIPDOCKS_FOR_FREE, starship2).isEmpty())
            return 0;

        return Math.max(1, changeInCost);
    }

    /**
     * Gets the amount of Force needed for the card to move using location text.
     * @param gameState the game state
     * @param card the card to move
     * @param fromLocation the location to move from (or location the starship/vehicle is at if move from a starship/vehicle)
     * @param toLocation the location to move to (or location the starship/vehicle is at if move to a starship/vehicle)
     * @param baseCost base cost in amount of Force required to perform the movement
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    @Override
    public float getMoveUsingLocationTextCost(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, float baseCost, float changeInCost) {
        // Check if moves for free
        if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
            return 0;

        // Check if moves for free from location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION, card)) {
            if (modifier.isMoveFreeFromLocation(gameState, this, fromLocation)) {
                return 0;
            }
        }

        // Check if moves for free from location to location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION, card)) {
            if (modifier.isMoveFreeFromLocationToLocation(gameState, this, fromLocation, toLocation)) {
                return 0;
            }
        }

        // Check if moves for free to location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION, card)) {
            if (modifier.isMoveFreeToLocation(gameState, this, toLocation)) {
                return 0;
            }
        }

        // Default move using location text movement cost
        float result = baseCost + changeInCost;

        // Check for modifiers to move cost
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST, card)) {
            result += modifier.getMoveCostModifier(gameState, this, card);
        }

        // Moving from collapsed site requires 1 additional Force
        if (fromLocation.isCollapsed()) {
            result += 1;
        }

        // Check for modifiers to move cost when moving from location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION, fromLocation)) {
            result += modifier.getMoveCostFromLocationModifier(gameState, this, card, fromLocation);
        }

        // Check for modifiers to move cost when moving from location to location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION, card)) {
            result += modifier.getMoveCostFromLocationToLocationModifier(gameState, this, card, fromLocation, toLocation);
        }

        // Moving to collapsed site requires 1 additional Force
        if (toLocation.isCollapsed()) {
            result += 1;
        }

        // Check for modifiers to move cost when moving to location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION, toLocation)) {
            result += modifier.getMoveCostToLocationModifier(gameState, this, card, toLocation);
        }

        return Math.max(0, result);
    }

    /**
     * Gets the amount of Force needed for the card to move using docking bay transit.
     * @param gameState the game state
     * @param card the card to move
     * @param fromDockingBay the docking bay to move from
     * @param toDockingBay the docking bay to move to
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    @Override
    public float getDockingBayTransitCost(GameState gameState, PhysicalCard card, PhysicalCard fromDockingBay, PhysicalCard toDockingBay, float changeInCost) {
        String playerId = card.getOwner();

        // Check if moves for free
        if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty()) {
            return 0;
        }

        // Check if moves for free from location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION, card)) {
            if (modifier.isMoveFreeFromLocation(gameState, this, fromDockingBay)) {
                return 0;
            }
        }

        // Check if moves for free from location to location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION, card)) {
            if (modifier.isMoveFreeFromLocationToLocation(gameState, this, fromDockingBay, toDockingBay)) {
                return 0;
            }
        }

        // Check if moves for free to location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION, card)) {
            if (modifier.isMoveFreeToLocation(gameState, this, toDockingBay)) {
                return 0;
            }
        }

        // Check if docking bay transiting to ignores cost of other docking bay
        boolean ignoreFromDockingBayCost = false;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IGNORE_OTHER_DOCKING_BAY_TRANSIT_COST, toDockingBay)) {
            if (modifier.isForPlayer(playerId) && modifier.getSource(gameState).getCardId() == toDockingBay.getCardId()) {
                ignoreFromDockingBayCost = true;
                break;
            }
        }

        // Default move using docking bay transit cost
        float result = 0 + changeInCost;

        // Check for docking bay transits free from docking bay
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DOCKING_BAY_TRANSIT_FROM_FOR_FREE, fromDockingBay)) {
            if (modifier.isForPlayer(playerId)
                    && (!ignoreFromDockingBayCost || modifier.getSource(gameState).getCardId() != fromDockingBay.getCardId())) {
                return 0;
            }
        }

        // Check docking bay transits cost from docking bay
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DOCKING_BAY_TRANSIT_FROM_COST, fromDockingBay)) {
            if (modifier.isForPlayer(playerId)
                    && (!ignoreFromDockingBayCost || modifier.getSource(gameState).getCardId() != fromDockingBay.getCardId())) {
                result += modifier.getValue(gameState, this, fromDockingBay);
                break;
            }
        }

        // Check if docking bay transiting to ignores cost of other docking bay
        boolean ignoreToDockingBayCost = false;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IGNORE_OTHER_DOCKING_BAY_TRANSIT_COST, fromDockingBay)) {
            if (modifier.isForPlayer(playerId) && modifier.getSource(gameState).getCardId() == fromDockingBay.getCardId()) {
                ignoreToDockingBayCost = true;
                break;
            }
        }

        // Check for docking bay transits free to docking bay
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DOCKING_BAY_TRANSIT_TO_FOR_FREE, toDockingBay)) {
            if (modifier.isForPlayer(playerId)
                    && (!ignoreToDockingBayCost || modifier.getSource(gameState).getCardId() != toDockingBay.getCardId())) {
                return 0;
            }
        }

        // Check docking bay transits cost to docking bay
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DOCKING_BAY_TRANSIT_TO_COST, toDockingBay)) {
            if (modifier.isForPlayer(playerId)
                    && (!ignoreToDockingBayCost || modifier.getSource(gameState).getCardId() != toDockingBay.getCardId())) {
                result += modifier.getValue(gameState, this, toDockingBay);
                break;
            }
        }

        // Check for modifiers to move cost
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST, card)) {
            result += modifier.getMoveCostModifier(gameState, this, card);
        }

        // Moving from collapsed site requires 1 additional Force
        if (fromDockingBay.isCollapsed()) {
            result += 1;
        }

        // Check for modifiers to move cost when moving from location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION, fromDockingBay)) {
            result += modifier.getMoveCostFromLocationModifier(gameState, this, card, fromDockingBay);
        }

        // Check for modifiers to move cost when moving from location to location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION, card)) {
            result += modifier.getMoveCostFromLocationToLocationModifier(gameState, this, card, fromDockingBay, toDockingBay);
        }

        // Moving to collapsed site requires 1 additional Force
        if (toDockingBay.isCollapsed()) {
            result += 1;
        }

        // Check for modifiers to move cost when moving to location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION, toDockingBay)) {
            result += modifier.getMoveCostToLocationModifier(gameState, this, card, toDockingBay);
        }

        return Math.max(0, result);
    }

    /**
     * Gets the amount of Force needed for the card to relocate between locations.
     * @param gameState the game state
     * @param card the card to move
     * @param fromLocation the location to relocate from
     * @param toLocation the location to relocate to
     * @param baseCost the base cost (as defined by the card performing the relocation)
     * @return the move cost
     */
    @Override
    public float getRelocateBetweenLocationsCost(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, float baseCost) {
        // Check if moves for free
        if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
            return 0;

        // Check if moves for free from location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION, card)) {
            if (modifier.isMoveFreeFromLocation(gameState, this, fromLocation)) {
                return 0;
            }
        }

        // Check if moves for free from location to location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION, card)) {
            if (modifier.isMoveFreeFromLocationToLocation(gameState, this, fromLocation, toLocation)) {
                return 0;
            }
        }

        // Check if moves for free to location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION, card)) {
            if (modifier.isMoveFreeToLocation(gameState, this, toLocation)) {
                return 0;
            }
        }

        // Default relocation cost
        float result = baseCost;

        // Check for modifiers to move cost
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST, card)) {
            result += modifier.getMoveCostModifier(gameState, this, card);
        }

        // Moving from collapsed site requires 1 additional Force
        if (fromLocation.isCollapsed()) {
            result += 1;
        }

        // Check for modifiers to move cost when moving from location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION, fromLocation)) {
            result += modifier.getMoveCostFromLocationModifier(gameState, this, card, fromLocation);
        }

        // Check for modifiers to move cost when moving from location to location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION, card)) {
            result += modifier.getMoveCostFromLocationToLocationModifier(gameState, this, card, fromLocation, toLocation);
        }

        // Moving to collapsed site requires 1 additional Force
        if (toLocation.isCollapsed()) {
            result += 1;
        }

        // Check for modifiers to move cost when moving to location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION, toLocation)) {
            result += modifier.getMoveCostToLocationModifier(gameState, this, card, toLocation);
        }

        return Math.max(0, result);
    }

    /**
     * Gets the amount of Force needed for the cards to relocate between locations.
     * @param gameState the game state
     * @param cards the cards to move
     * @param fromLocation the location to relocate from
     * @param toLocation the location to relocate to
     * @param baseCost the base cost (as defined by the card performing the relocation)
     */
    @Override
    public float getRelocateBetweenLocationsCost(GameState gameState, Collection<PhysicalCard> cards, PhysicalCard fromLocation, PhysicalCard toLocation, float baseCost) {
        float maxCost = 0;

        for (PhysicalCard cardToMove : cards) {
            maxCost = Math.max(maxCost, getRelocateBetweenLocationsCost(gameState, cardToMove, fromLocation, toLocation, baseCost));
        }

        return maxCost;
    }

    /**
     * Gets the cost to fire the weapon.
     * @param gameState the game state
     * @param weapon the weapon
     * @param cardFiringWeapon the card firing the weapon, or null if weapon is not fired by another card
     * @param target the card targeted by the weapon, or null if no target specified
     * @param baseCost the base cost (as defined by the weapon game text)
     * @return the cost to fire the weapon
     */
    @Override
    public float getFireWeaponCost(GameState gameState, PhysicalCard weapon, PhysicalCard cardFiringWeapon, PhysicalCard target, int baseCost) {
        float result = baseCost;

        // Check if fires for free
        if (!getModifiersAffectingCard(gameState, ModifierType.FIRES_FOR_FREE, weapon).isEmpty()) {
            return 0;
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FIRE_WEAPON_FIRED_BY_FOR_FREE, cardFiringWeapon)) {
            if (modifier.isAffectedTarget(gameState, this, weapon)) {
                return 0;
            }
        }

        // Check if printed firing cost
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_FIRE_WEAPON_COST, weapon)) {
            if (modifier.isDefinedFireWeaponCost(gameState, this, cardFiringWeapon)) {
                result = modifier.getDefinedFireWeaponCost(gameState, this, cardFiringWeapon);
            }
        }

        // Check if fire weapon cost is changed
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FIRE_WEAPON_COST, weapon)) {
            result += modifier.getValue(gameState, this, weapon);
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FIRE_WEAPON_FIRED_BY_COST, cardFiringWeapon)) {
            if (modifier.isAffectedTarget(gameState, this, weapon)) {
                result += modifier.getValue(gameState, this, cardFiringWeapon);
            }
        }

        result = Math.max(0, result);
        return result;
    }

    /**
     * Gets the cost to fire the permanent weapon.
     * @param gameState the game state
     * @param permanentWeapon the permanent weapon
     * @param cardFiringWeapon the card firing the permanent weapon
     * @param target the card targeted by the permanent weapon, or null if no target specified
     * @param baseCost the base cost (as defined by the permanent weapon game text)
     * @return the cost to fire the weapon
     */
    @Override
    public float getFireWeaponCost(GameState gameState, SwccgBuiltInCardBlueprint permanentWeapon, PhysicalCard cardFiringWeapon, PhysicalCard target, int baseCost) {
        float result = baseCost;

        // Check if fires for free
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FIRE_WEAPON_FIRED_BY_FOR_FREE, cardFiringWeapon)) {
            if (modifier.isAffectedTarget(gameState, this, permanentWeapon)) {
                return 0;
            }
        }

        // Check if fire weapon cost is changed
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FIRE_WEAPON_FIRED_BY_COST, cardFiringWeapon)) {
            if (modifier.isAffectedTarget(gameState, this, permanentWeapon)) {
                result += modifier.getValue(gameState, this, permanentWeapon);
            }
        }

        result = Math.max(0, result);
        return result;
    }

    /**
     * Determines if the specified weapon may be fired repeatedly.
     * @param gameState the game state
     * @param weapon the weapon
     * @return true or false
     */
    @Override
    public boolean mayFireWeaponRepeatedly(GameState gameState, PhysicalCard weapon) {
        return (!getModifiersAffectingCard(gameState, ModifierType.MAY_FIRE_REPEATEDLY_FOR_COST, weapon).isEmpty());
    }

    /**
     * Gets the cost to fire the weapon repeatedly.
     * @param gameState the game state
     * @param weapon the weapon
     * @return the cost for fire the weapon repeatedly
     */
    @Override
    public float getFireWeaponRepeatedlyCost(GameState gameState, PhysicalCard weapon) {
        float result = Float.MAX_VALUE;

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_FIRE_REPEATEDLY_FOR_COST, weapon)) {
            result = Math.min(result, modifier.getValue(gameState, this, weapon));
        }

        result = Math.max(0, result);
        return result;
    }

    /**
     * Determines if the specified artillery weapon may be fired without a warrior present.
     * @param gameState the game state
     * @param artilleryWeapon the artillery weapon
     * @return true or false
     */
    @Override
    public boolean mayFireArtilleryWeaponWithoutWarriorPresent(GameState gameState, PhysicalCard artilleryWeapon) {
        return (!getModifiersAffectingCard(gameState, ModifierType.MAY_FIRE_ARTILLERY_WEAPON_WITHOUT_WARRIOR_PRESENT, artilleryWeapon).isEmpty());
    }

    @Override
    public float getTotalPowerAtLocation(GameState gameState, PhysicalCard location, String playerId, boolean inBattle, boolean onlyPresent) {
        float result = 0;
        Filter participantFilter = Filters.any;
        if (inBattle)
            participantFilter = Filters.participatingInBattle;

        // Figure out cards present at the location
        Collection<PhysicalCard> presentCards = Filters.filterActive(gameState.getGame(), null, Filters.and(Filters.owner(playerId), Filters.present(location), participantFilter));
        for (PhysicalCard presentCard : presentCards) {
            // If card is a character, vehicle, or starship, add power of these cards
            CardCategory cardCategory = presentCard.getBlueprint().getCardCategory();
            if (presentCard.isDejarikHologramAtHolosite() || cardCategory == CardCategory.CHARACTER || cardCategory == CardCategory.VEHICLE || cardCategory == CardCategory.STARSHIP)
                result += getPower(gameState, presentCard);
        }

        if (!onlyPresent) {
            // Apply modifiers to total power at location
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TOTAL_POWER_AT_LOCATION, location))
                result += modifier.getTotalPowerModifier(playerId, gameState, this, location);
        }

        result = Math.max(0, result);
        return result;
    }

    /**
     * Gets the total power (or ferocity) in the attack.
     * @param gameState the game state
     * @param defender true if total for defender, otherwise total for attacker
     * @return the total power (or ferocity)
     */
    @Override
    public float getAttackTotalPowerOrFerocity(GameState gameState, boolean defender) {
        AttackState attackState = gameState.getAttackState();
        if (attackState == null) {
            return 0;
        }
        float result = 0;

        Collection<PhysicalCard> cardsInAttack = defender ? attackState.getCardsDefending() : attackState.getCardsAttacking();
        for (PhysicalCard cardInAttack : Filters.filter(cardsInAttack, _swccgGame, Filters.present(attackState.getAttackLocation()))) {
            // If card is a creature, character, vehicle, or starship, add power (or ferocity) of these cards
            CardCategory cardCategory = cardInAttack.getBlueprint().getCardCategory();
            if (cardCategory == CardCategory.CHARACTER || cardCategory == CardCategory.VEHICLE || cardCategory == CardCategory.STARSHIP) {
                result += getPower(gameState, cardInAttack);
            }
            else if (cardCategory == CardCategory.CREATURE) {
                Float ferocityDestinyTotal = attackState.getFerocityDestinyTotal(cardInAttack);
                result += getFerocity(gameState, cardInAttack, ferocityDestinyTotal);
            }
        }

        // Apply modifiers to total power at location if non-creature
        if ((attackState.isNonCreatureAttackingCreature() && !defender)
                || (attackState.isCreatureAttackingNonCreature() && defender)) {
            String playerWithTotalPower = defender ? attackState.getDefenderOwner() : attackState.getAttackerOwner();
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TOTAL_POWER_AT_LOCATION, attackState.getAttackLocation())) {
                result += modifier.getTotalPowerModifier(playerWithTotalPower, gameState, this, attackState.getAttackLocation());
            }
        }

        result = Math.max(0, result);
        return result;
    }

    /**
     * Gets the total ability in the attack.
     * @param gameState the game state
     * @param defender true if total for defender, otherwise total for attacker
     * @return the total ability
     */
    @Override
    public float getAttackTotalAbility(GameState gameState, boolean defender) {
        AttackState attackState = gameState.getAttackState();
        if (attackState == null) {
            return 0;
        }

        float result = 0;

        PhysicalCard attackLocation = attackState.getAttackLocation();
        String owningPlayer = defender ? attackState.getDefenderOwner() : attackState.getAttackerOwner();
        Collection<PhysicalCard> cardsInAttack = defender ? attackState.getCardsDefending() : attackState.getCardsAttacking();

        for (PhysicalCard presentCard : Filters.filter(cardsInAttack, _swccgGame, Filters.present(attackState.getAttackLocation()))) {

            // If card is a character or vehicle, add ability of the character or vehicle,
            // and include permanent pilot ability for vehicles and starships
            CardCategory cardCategory = presentCard.getBlueprint().getCardCategory();
            if (cardCategory == CardCategory.CHARACTER || cardCategory == CardCategory.VEHICLE || cardCategory == CardCategory.STARSHIP) {
                result += getAbility(gameState, presentCard, true);
            }

            // If card is a starship or enclosed vehicle, add ability of cards present aboard that starship or vehicle
            if (cardCategory == CardCategory.STARSHIP || Filters.enclosed_vehicle.accepts(gameState, this, presentCard)) {

                // Check if unpiloted starship or vehicle
                boolean isUnpiloted = !isPiloted(gameState, presentCard, false);
                Collection<PhysicalCard> cardsPresentAboard = Filters.filter(cardsInAttack, _swccgGame, Filters.aboardExceptRelatedSites(presentCard));
                for (PhysicalCard presentCardAboard : cardsPresentAboard) {
                    boolean isPiloting = !isUnpiloted && Filters.or(Filters.piloting(presentCard), Filters.driving(presentCard)).accepts(gameState, this, presentCardAboard);

                    CardCategory presentAboardCardCategory = presentCardAboard.getBlueprint().getCardCategory();
                    if (presentAboardCardCategory == CardCategory.STARSHIP || presentAboardCardCategory == CardCategory.VEHICLE || presentAboardCardCategory == CardCategory.CHARACTER) {
                        // Only count pilots
                        if (isPiloting) {
                            result += getAbility(gameState, presentCardAboard, true);
                        }
                    }
                }
            }
        }

        // Apply modifiers to total ability at location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TOTAL_ABILITY_AT_LOCATION, attackLocation)) {
            if (modifier.isForPlayer(owningPlayer)) {
                float modifierAmount = modifier.getValue(gameState, this, attackLocation);
                if (modifierAmount >= 0 || !isProhibitedFromHavingTotalAbilityReduced(gameState, attackLocation, owningPlayer)) {
                    result += modifierAmount;
                }
            }
        }

        // Check if value was reset to an "unmodifiable value", and use lowest found
        Float lowestResetValue = null;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_TOTAL_ABILITY_AT_LOCATION, attackLocation)) {
            if (modifier.isForPlayer(owningPlayer)) {
                float modifierAmount = modifier.getValue(gameState, this, attackLocation);
                if (modifierAmount >= result || !isProhibitedFromHavingTotalAbilityReduced(gameState, attackLocation, owningPlayer)) {
                    lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
                }
            }
        }
        if (lowestResetValue != null) {
            return Math.max(0, lowestResetValue);
        }

        return Math.max(0, result);
    }

    /**
     * Gets the player that has a "senate majority", or null if neither player does.
     * @param gameState the game state
     * @return the player with a "senate majority"
     */
    @Override
    public String getPlayerWithSenateMajority(GameState gameState) {
        PhysicalCard galacticSenate = Filters.findFirstFromTopLocationsOnTable(gameState.getGame(), Filters.Galactic_Senate);
        if (galacticSenate == null)
            return null;

        float dsTotal = 0;
        float lsTotal = 0;

        Collection<PhysicalCard> cardsAtSenate = Filters.filterActive(gameState.getGame(), null, Filters.and(Filters.character, Filters.at(galacticSenate)));
        for (PhysicalCard cardAtSenate : cardsAtSenate) {
            if (cardAtSenate.getOwner().equals(gameState.getDarkPlayer()))
                dsTotal += getPolitics(gameState, cardAtSenate);
            else
                lsTotal += getPolitics(gameState, cardAtSenate);
        }

        if (dsTotal > lsTotal)
            return gameState.getDarkPlayer();
        else if (lsTotal > dsTotal)
            return gameState.getLightPlayer();
        else
            return null;
    }

    /**
     * Gets the player's total politics at Galactic Senate.
     * @param gameState the game state
     * @param playerId the player
     * @return the politics total
     */
    @Override
    public float getTotalPoliticsAtGalacticSenate(GameState gameState, String playerId) {
        PhysicalCard galacticSenate = Filters.findFirstFromTopLocationsOnTable(gameState.getGame(), Filters.Galactic_Senate);
        float total = 0;

        Collection<PhysicalCard> cardsAtSenate = Filters.filterActive(gameState.getGame(), null, Filters.and(Filters.owner(playerId), Filters.character, Filters.at(galacticSenate)));
        for (PhysicalCard cardAtSenate : cardsAtSenate) {
            total += getPolitics(gameState, cardAtSenate);
        }

        return Math.max(0, total);
    }

    @Override
    public int getPilotCapacity(GameState gameState, PhysicalCard physicalCard) {
        if (physicalCard.getBlueprint().getCardCategory()!=CardCategory.STARSHIP && physicalCard.getBlueprint().getCardCategory()!=CardCategory.VEHICLE)
            return 0;

        int result = physicalCard.getBlueprint().getPilotCapacity();
        if (result==Integer.MAX_VALUE)
            return result;

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PILOT_CAPACITY, physicalCard)) {
            result += modifier.getPilotCapacityModifier(gameState, this, physicalCard);
        }
        return Math.max(0, result);
    }

    @Override
    public int getAstromechCapacity(GameState gameState, PhysicalCard physicalCard) {
        if (physicalCard.getBlueprint().getCardCategory()!=CardCategory.STARSHIP && physicalCard.getBlueprint().getCardCategory()!=CardCategory.VEHICLE)
            return 0;

        int result = physicalCard.getBlueprint().getAstromechCapacity();
        if (result==Integer.MAX_VALUE)
            return result;

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.ASTROMECH_CAPACITY, physicalCard)) {
            result += modifier.getAstromechCapacityModifier(gameState, this, physicalCard);
        }
        return Math.max(0, result);
    }

    @Override
    public boolean canCarryPassengerAsIfCreatureVehicle(GameState gameState, PhysicalCard physicalCard, PhysicalCard passenger) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_CARRY_PASSENGER_AS_IF_CREATURE_VEHICLE, physicalCard)) {
            if (passenger == null || modifier.isAffectedTarget(gameState, this, passenger)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasCapacityForCardsToRelocate(GameState gameState, PhysicalCard physicalCard, Collection<PhysicalCard> cardsToRelocate) {
        // Only allow this for capital starships
        if (physicalCard.getBlueprint().getCardCategory()!=CardCategory.STARSHIP && physicalCard.getBlueprint().getCardSubtype()==CardSubtype.CAPITAL)
            return false;

        // Get capacities available
        int availablePilotSlots = gameState.getAvailablePilotCapacity(this, physicalCard, null);
        int availablePassengerSlots = gameState.getAvailablePassengerCapacity(this, physicalCard, null);
        int availableAstromechOnlySlots = gameState.getAvailablePassengerCapacityForAstromech(this, physicalCard, null) - availablePassengerSlots;

        // Check if characters could fit in the available slots (fill passenger slots last since any character)
        for (PhysicalCard cardToRelocate : cardsToRelocate) {
            if (Filters.astromech_droid.accepts(gameState, this, cardToRelocate) && availableAstromechOnlySlots > 0)
                availableAstromechOnlySlots--;
            else if (hasIcon(gameState, cardToRelocate, Icon.PILOT) && availablePilotSlots>0)
                availablePilotSlots--;
            else if (availablePassengerSlots>0)
                availablePassengerSlots--;
            else
                return false;
        }

        return true;
    }

    /**
     * Determines if the card is piloted.
     * @param gameState the game state
     * @param card the card
     * @param forStarshipTakeoff true if checking if starship is piloted for takeoff, otherwise false
     * @return true if piloted, otherwise false
     */
    @Override
    public boolean isPiloted(GameState gameState, PhysicalCard card, boolean forStarshipTakeoff) {
        SwccgCardBlueprint blueprint = card.getBlueprint();

        // Crashed vehicle is not piloted
        if (card.isCrashed())
            return false;

        // Creature vehicles and Lift Tubes are piloted
        if (blueprint.getCardSubtype()==CardSubtype.CREATURE || card.getTitle().equals(Title.Lift_Tube))
            return true;

        // Landed starships are not piloted (except for takeoff)
        if (blueprint.getCardCategory() == CardCategory.STARSHIP
                && !card.isMakingBombingRun()
                && ((card.getAttachedTo() != null && (!forStarshipTakeoff || !card.isConcealed()))
                || (card.getAtLocation() != null && !forStarshipTakeoff && Filters.and(Filters.site, Filters.not(Filters.Death_Star_Trench)).accepts(gameState, this, card.getAtLocation()))))
            return false;

        // Check for permanent pilots
        int permanentPilotCount = getIconCount(gameState, card, Icon.PILOT);
        if (permanentPilotCount > 0) {
            if (Filters.squadron.accepts(gameState, this, card)) {
                return permanentPilotCount >= blueprint.getModelTypes().size();
            }
            else {
                return true;
            }
        }

        // Make sure at least one active pilot fits the valid pilot filter (see "Mist Hunter" from Dagobah for example)
        for (PhysicalCard pilotCard : gameState.getPilotCardsAboard(this, card, true)) {
            if (gameState.isCardInPlayActive(pilotCard, false, false, false, forStarshipTakeoff, false, false, false, false)
                    && card.getBlueprint().getValidPilotFilter(card.getOwner(), gameState.getGame(), card, false).accepts(gameState, gameState.getGame().getModifiersQuerying(), pilotCard)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isLanded(GameState gameState, PhysicalCard physicalCard) {
        if (physicalCard.getBlueprint().getCardCategory() != CardCategory.STARSHIP
                && physicalCard.getBlueprint().getCardCategory() != CardCategory.VEHICLE) {
            return false;
        }

        if (physicalCard.isMakingBombingRun())
            return false;

        if (physicalCard.isInCargoHoldAsVehicle() || physicalCard.isInCargoHoldAsStarfighterOrTIE())
            return true;

        if (physicalCard.getBlueprint().getCardCategory() == CardCategory.STARSHIP) {
            if (physicalCard.getAtLocation() != null) {
                return Filters.and(Filters.site, Filters.not(Filters.Death_Star_Trench)).accepts(gameState, this, physicalCard.getAtLocation());
            }
        }

        return false;
    }

    @Override
    public boolean cannotAddToPowerOfPilotedBy(GameState gameState, PhysicalCard physicalCard) {
        if (physicalCard.getBlueprint().getCardCategory() != CardCategory.CHARACTER)
            return false;

        return !getModifiersAffectingCard(gameState, ModifierType.DOES_NOT_ADD_TO_POWER_WHEN_PILOTING, physicalCard).isEmpty();
    }

    @Override
    public boolean hasAstromech(GameState gameState, PhysicalCard physicalCard) {
        if (physicalCard.getBlueprint().getCardCategory() != CardCategory.STARSHIP)
            return false;

        List<PhysicalCard> aboardCards = gameState.getPassengerCardsAboard(physicalCard);
        for (PhysicalCard aboardCard : aboardCards) {
            if (Filters.astromech_droid.acceptsCount(gameState, this, aboardCard) >= 1) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if the starship has an astromech or nav computer.
     * @param gameState the game state
     * @param card the starship
     * @return true or false
     */
    @Override
    public boolean hasAstromechOrNavComputer(GameState gameState, PhysicalCard card) {
        if (card.getBlueprint().getCardCategory() != CardCategory.STARSHIP)
            return false;

        if (hasIcon(gameState, card, Icon.NAV_COMPUTER))
            return true;

        return hasAstromech(gameState, card);
    }

    /**
     * Determines if the specified card is explicitly not allowed to be played due to existence of a "can't play" modifier
     * affecting the card.
     * @param gameState the game state
     * @param card the card
     * @param isDejarikRules true if playing using Dejarik Rules, otherwise false
     * @return true if card cannot be played, otherwise false
     */
    @Override
    public boolean isPlayingCardProhibited(GameState gameState, PhysicalCard card, boolean isDejarikRules) {
        if (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_PLAY, card).isEmpty()) {
            return true;
        }
        if (isDejarikRules && !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_PLAY_USING_DEJARIK_RULES, card).isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Determines if the limit of how may times a card with any titles of the specified card can be played per turn has
     * been reached.
     * @param gameState the game state
     * @param card the card
     * @return true if the limit has been reached, otherwise false
     */
    @Override
    public boolean isPlayingCardTitleTurnLimitReached(GameState gameState, PhysicalCard card) {
        if (gameState.getCurrentPhase()==Phase.PLAY_STARTING_CARDS)
            return false;

        Uniqueness uniqueness = getUniqueness(gameState, card);
        if (uniqueness==null || uniqueness.isPerSystem())
            return false;

        for (String title : card.getTitles()) {
            if (uniqueness.getValue() <= getCardTitlePlayedTurnLimitCounter(title).getUsedLimit()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if the limit of the number of cards on table (or out of play) with same title or persona as the specified
     * card has been reached.
     * @param gameState the game state
     * @param card the card
     * @return true if the limit has been reached, otherwise false
     */
    @Override
    public boolean isUniquenessOnTableLimitReached(GameState gameState, PhysicalCard card) {
        SwccgCardBlueprint blueprint = card.getBlueprint();
        SwccgBuiltInCardBlueprint permanentWeapon = null;

        // Get any personas (including any permanents aboard).
        Set<Persona> personas = new HashSet<Persona>(getPersonas(gameState, card));
        List<SwccgBuiltInCardBlueprint> permanentsAboard = getPermanentsAboard(gameState, card);
        for (SwccgBuiltInCardBlueprint permanentAboard : permanentsAboard) {
            personas.addAll(permanentAboard.getPersonas(_swccgGame));
        }

        // Only check "out of play" for characters, starships, and vehicles
        if (blueprint.getCardCategory() == CardCategory.CHARACTER || blueprint.getCardCategory() == CardCategory.STARSHIP || blueprint.getCardCategory() == CardCategory.VEHICLE) {
            for (Persona persona : personas) {
                if (!Filters.filterCount(gameState.getAllOutOfPlayCards(), gameState.getGame(), 1, Filters.persona(persona)).isEmpty()) {
                    return true;
                }
            }

            // Add any permanent weapon personas
            permanentWeapon = getPermanentWeapon(gameState, card);
            if (permanentWeapon != null) {
                personas.addAll(permanentWeapon.getPersonas(_swccgGame));
            }
        }

        // Check uniqueness of any personas (within the card) on table
        for (Persona persona : personas) {
            if (Filters.canSpotForUniquenessChecking(gameState.getGame(), Filters.and(Filters.not(card), Filters.or(Filters.persona(persona), Filters.hasPermanentAboard(Filters.persona(persona)), Filters.hasPermanentWeapon(Filters.persona(persona)))))) {
                return true;
            }
        }

        // Otherwise, check based on uniqueness of card title.
        Uniqueness uniqueness = getUniqueness(gameState, card);
        if (uniqueness != null && !uniqueness.isPerSystem()) {

            Filter filterForUniqueness = Filters.sameTitleAs(card, false);
            if (blueprint.getCardCategory() == CardCategory.LOCATION) {
                filterForUniqueness = Filters.and(filterForUniqueness, Filters.owner(card.getOwner()), Filters.not(Filters.collapsed), Filters.not(Filters.perSystemUniqueness));
            }
            else {
                filterForUniqueness = Filters.or(filterForUniqueness, Filters.hasPermanentAboard(filterForUniqueness), Filters.hasPermanentWeapon(filterForUniqueness));
            }
            int count = Filters.countForUniquenessChecking(gameState.getGame(), filterForUniqueness);

            if (uniqueness == Uniqueness.UNIQUE) {
                if (count > 0) {
                    return true;
                }

                // Only check out of play for characters, starships, and vehicles (except Jabba's Prize)
                if ((blueprint.getCardCategory() == CardCategory.CHARACTER || blueprint.getCardCategory() == CardCategory.STARSHIP || blueprint.getCardCategory() == CardCategory.VEHICLE)
                        && !Filters.Jabbas_Prize.accepts(gameState, this, card)) {
                    if (!Filters.filterCount(gameState.getAllOutOfPlayCards(), gameState.getGame(), 1, Filters.sameTitleAs(card, false)).isEmpty()) {
                        return true;
                    }
                }
            }

            if (uniqueness.getValue() <= count) {
                return true;
            }
        }

        // Check its permanent weapon by title (if any)
        if (permanentWeapon != null) {
            String weaponTitle = permanentWeapon.getTitle(_swccgGame);
            Uniqueness weaponUniqueness = permanentWeapon.getUniqueness();

            if (weaponUniqueness != null) {

                int count = Filters.countForUniquenessChecking(gameState.getGame(), Filters.and(Filters.not(card), Filters.or(Filters.title(weaponTitle), Filters.hasPermanentAboard(Filters.title(weaponTitle)), Filters.hasPermanentWeapon(Filters.title(weaponTitle)))));

                if (weaponUniqueness == Uniqueness.UNIQUE
                        && count > 0) {
                    return true;
                }

                if (weaponUniqueness.getValue() <= count) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Determines if the player is explicitly not allowed to search the card pile using the game text action on the specified
     * card.
     * @param gameState the game state
     * @param card the card
     * @param playerId the player
     * @param cardPile the card pile
     * @param cardPileOwner the card pile owner
     * @param gameTextActionId the game text action id
     * @return true if card pile is not allowed to be searched, otherwise false
     */
    @Override
    public boolean isSearchingCardPileProhibited(GameState gameState, PhysicalCard card, String playerId, Zone cardPile,
                                                 String cardPileOwner, GameTextActionId gameTextActionId) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.CANT_SEARCH_CARD_PILE, card))
            if (modifier.isProhibitedFromSearchingCardPile(gameState, this, playerId, cardPile, cardPileOwner, gameTextActionId))
                return true;
        return false;
    }

    @Override
    public boolean prohibitedFromCarrying(GameState gameState, PhysicalCard character, PhysicalCard cardToBeCarried) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_CARRY, character))
            if (modifier.prohibitedFromCarrying(gameState, this, character, cardToBeCarried))
                return true;
        return false;
    }

    @Override
    public boolean prohibitedFromPiloting(GameState gameState, PhysicalCard pilot, PhysicalCard starshipOrVehicle) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_PILOT_TARGET, pilot))
            if (modifier.prohibitedFromPiloting(gameState, this, starshipOrVehicle))
                return true;
        return false;
    }

    /**
     * Determines if the specified Interrupt is explicitly allowed to be played to cancel a Force drain at the location.
     * @param gameState the game state
     * @param card the Interrupt card
     * @param location the Force drain location
     * @return true if allowed, otherwise false
     */
    @Override
    public boolean mayPlayInterruptToCancelForceDrain(GameState gameState, PhysicalCard card, PhysicalCard location) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_PLAY_TO_CANCEL_FORCE_DRAIN, card))
            if (modifier.isAffectedTarget(gameState, this, location))
                return true;
        return false;
    }

    /**
     * Determines if the specified Interrupt is explicitly allowed to be played to cancel the specified card (being played or
     * on table).
     * @param gameState the game state
     * @param card the Interrupt card
     * @param targetCard the card being played or on table
     * @return true if allowed, otherwise false
     */
    @Override
    public boolean mayPlayInterruptToCancelCard(GameState gameState, PhysicalCard card, PhysicalCard targetCard) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_PLAY_TO_CANCEL_CARD, card))
            if (modifier.isAffectedTarget(gameState, this, targetCard))
                return true;
        return false;
    }

    /**
     * Determines if the affected cards is prohibited from existing at (deploying or moving to) the specified targeted.
     * @param gameState the game state
     * @param card the card
     * @param target the target card
     * @return true if card may not exist at target, otherwise false
     */
    @Override
    public boolean isProhibitedFromTarget(GameState gameState, PhysicalCard card, PhysicalCard target) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_EXIST_AT_TARGET, card))
            if (modifier.isProhibitedFromExistingAt(gameState, this, target))
                return true;

        // Undercover spies may only exist physically at sites
        if (card.isUndercover() && target.getBlueprint().getCardSubtype() != CardSubtype.SITE)
            return true;

        // If card has "per system" uniqueness, then it cannot exist at same system as another card of same title (per uniqueness limit)
        Uniqueness uniqueness = getUniqueness(gameState, card);
        if (uniqueness != null && uniqueness.isPerSystem()) {
            PhysicalCard location = getLocationHere(gameState, target);
            if (location != null) {
                String systemName = location.getPartOfSystem() != null ? location.getPartOfSystem() : location.getSystemOrbited();
                if (systemName != null) {
                    if (Filters.canSpotFromAllOnTable(gameState.getGame(), uniqueness.getValue(), Filters.and(Filters.sameTitleAs(card),
                            Filters.locationAndCardsAtLocation(Filters.or(Filters.partOfSystem(systemName),
                                    Filters.and(Filters.not(Filters.system), Filters.isOrbiting(systemName))))))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the affected cards is prohibited from deploying to the specified targeted.
     * @param gameState the game state
     * @param playedCard the card
     * @param target the target card
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @return true if card may not be deployed to target, otherwise false
     */
    @Override
    public boolean isProhibitedFromDeployingTo(GameState gameState, PhysicalCard playedCard, PhysicalCard target, DeploymentRestrictionsOption deploymentRestrictionsOption) {
        CardCategory cardCategory = playedCard.getBlueprint().getCardCategory();
        PhysicalCard location = getLocationHere(gameState, target);

        // Only Epic Events may deploy at a Death Star II sector
        if (cardCategory != CardCategory.EPIC_EVENT) {
            if (location != null && Filters.Death_Star_II_sector.accepts(gameState, this, location)) {
                return true;
            }
        }

        // Only may deploy to Death Star: Trench if explicitly allowed
        if (deploymentRestrictionsOption == null || !deploymentRestrictionsOption.isAllowTrench()) {
            if (location != null && Filters.Death_Star_Trench.accepts(gameState, this, location)) {
                if (!isGrantedToDeployTo(gameState, playedCard, target, null)) {
                    return true;
                }
            }
        }

        // Check if card has (limit 1 per location)
        if (location != null
                && isOperativePreventedFromDeployingToOrMovingToLocation(gameState, playedCard, location)) {
            return true;
        }

        // Check if location deployment restrictions are ignored when deploying to specified target
        boolean ignoresLocationDeploymentRestrictions = ignoresLocationDeploymentRestrictions(gameState, playedCard, target, deploymentRestrictionsOption, false);
        boolean ignoresLocationDeploymentRestrictionsInGameText = ignoresLocationDeploymentRestrictions || ignoresGameTextLocationDeploymentRestrictions(gameState, playedCard);

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_DEPLOY_TO_TARGET, playedCard)) {
            PhysicalCard sourceCard = modifier.getSource(gameState);
            if ((modifier.isAlwaysInEffect() || sourceCard == null
                    || ((!ignoresLocationDeploymentRestrictionsInGameText || !playedCard.equals(sourceCard))
                    && !ignoresLocationDeploymentRestrictionsFromSource(gameState, playedCard, sourceCard)))
                    && modifier.isAffectedTarget(gameState, this, target)) {
                return true;
            }
        }

        if (!ignoresLocationDeploymentRestrictions) {

            // Check for "Hoth Energy Shield"
            if (playedCard.getOwner().equals(gameState.getDarkPlayer())
                    && (cardCategory == CardCategory.CHARACTER || cardCategory == CardCategory.STARSHIP || cardCategory == CardCategory.VEHICLE)) {
                if (location != null && isLocationUnderHothEnergyShield(gameState, location)) {
                    return true;
                }
            }

            // Check for "Dagobah"
            if (cardCategory == CardCategory.CHARACTER || cardCategory == CardCategory.STARSHIP || cardCategory == CardCategory.VEHICLE) {
                if (location != null && Filters.Dagobah_location.accepts(gameState, this, location)) {
                    // Check if allowed to deploy to Dagobah location (or target at Dagobah location)
                    if (!grantedToDeployToDagobahTarget(gameState, playedCard, target)) {
                        return true;
                    }
                }
            }
            else if (cardCategory == CardCategory.DEVICE || cardCategory == CardCategory.WEAPON) {
                if (Filters.Dagobah_location.accepts(gameState, this, target)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Determines if sites are prevented from deploying between the specified sites.
     * @param gameState the game state
     * @param site1 a site on one side
     * @param site2 a site on the other side
     * @return true if allowed, otherwise false
     */
    @Override
    public boolean isSitePreventedFromDeployingBetweenSites(GameState gameState, PhysicalCard site1, PhysicalCard site2) {
        for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_NOT_DEPLOY_SITES_BETWEEN_SITES)) {
            if (modifier.mayNotDeploySiteBetweenSites(gameState, this, site1, site2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if specified card is not allowed to be used to transport to or from specified location.
     * @param gameState the game state
     * @param card the card
     * @param location the location
     * @return true if not allowed, otherwise false
     */
    @Override
    public boolean prohibitedFromUsingCardToTransportToOrFromLocation(GameState gameState, PhysicalCard card, PhysicalCard location) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.CANT_USE_TO_TRANSPORT_TO_OR_FROM_LOCATION, card)) {
            if (modifier.isAffectedTarget(gameState, this, location)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if the specified Operative is prevented from deploying to or moving to location.
     * @param gameState the game state
     * @param card the Operative
     * @param location the location
     * @return true if Operative cannot deploy or move to location, otherwise false
     */
    @Override
    public boolean isOperativePreventedFromDeployingToOrMovingToLocation(GameState gameState, PhysicalCard card, PhysicalCard location) {
        // Special rule: A player may not move own Operative to same location on matching planet as another of that
        // player's Operatives with same title.
        if (Filters.operative.accepts(gameState, this, card)) {
            String matchingSystem = card.getBlueprint().getMatchingSystem();
            if (Filters.and(Filters.on(matchingSystem), Filters.sameLocationAs(null, SpotOverride.INCLUDE_ALL,
                    Filters.and(Filters.your(card), Filters.operative, Filters.sameTitleAs(card)))).accepts(gameState, this, location)) {
                return !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_DEPLOY_MOVE_OPERATIVE_RULE, card).isEmpty();
            }
        }
        return false;
    }

    /**
     * Determines if the specified card is explicitly not allowed to 'cloak'.
     * @param gameState the game state
     * @param card the card
     * @return true if card cannot 'cloak', otherwise false
     */
    @Override
    public boolean isCloakingCardProhibited(GameState gameState, PhysicalCard card) {
        return !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_CLOAK, card).isEmpty();
    }

    /**
     * Determines if the specified card is explicitly not allowed to 'attach'.
     * @param gameState the game state
     * @param card the card
     * @return true if card cannot 'attach', otherwise false
     */
    @Override
    public boolean isAttachingCardProhibited(GameState gameState, PhysicalCard card) {
        return !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_ATTACH, card).isEmpty();
    }

    /**
     * Determines if a card is allowed to make a Kessel Run if not a smuggler.
     * @param gameState the game state
     * @param card a card
     * @return true if not allowed, otherwise false
     */
    @Override
    public boolean isAllowedToMakeKesselRunWhenNotSmuggler(GameState gameState, PhysicalCard card) {
        return !getModifiersAffectingCard(gameState, ModifierType.MAY_MAKE_KESSEL_RUN_WHEN_NOT_SMUGGLER, card).isEmpty();
    }

    /**
     * Determines if a location is under the "Hoth Energy Shield"
     * @param gameState the game state
     * @param location a location
     * @return true if under "Hoth Energy Shield", otherwise false
     */
    @Override
    public boolean isLocationUnderHothEnergyShield(GameState gameState, PhysicalCard location) {
        return !getModifiersAffectingCard(gameState, ModifierType.UNDER_HOTH_ENERGY_SHIELD, location).isEmpty();
    }

    /**
     * Determines if the specified card ignores location deployment restrictions when deploying to the specified target.
     * @param gameState the game state
     * @param card the card
     * @param target the target card
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param skipForceIconsOrPresenceCheck the skip checking sufficent Force icons or presence
     * @return true if card ignores location deployment restrictions when deploying to target
     */
    @Override
    public boolean ignoresLocationDeploymentRestrictions(GameState gameState, PhysicalCard card, PhysicalCard target, DeploymentRestrictionsOption deploymentRestrictionsOption, boolean skipForceIconsOrPresenceCheck) {
        if (deploymentRestrictionsOption != null && deploymentRestrictionsOption.isIgnoreLocationDeploymentRestrictions()) {
            return true;
        }

        PhysicalCard location = getLocationHere(gameState, target);
        if (location != null) {
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IGNORES_LOCATION_DEPLOYMENT_RESTRICTIONS_WHEN_DEPLOYING_TO_LOCATION, card)) {
                if (!skipForceIconsOrPresenceCheck || !modifier.isExceptForceIconOrPresenceRequirement()) {
                    if (modifier.isAffectedTarget(gameState, this, location)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the specified card ignores location deployment restrictions from the source card.
     * @param gameState the game state
     * @param cardToDeploy the card to deploy
     * @param sourceCard the source card of the location deployment restriction
     * @return true if card ignores location deployment restrictions in its game text
     */
    @Override
    public boolean ignoresLocationDeploymentRestrictionsFromSource(GameState gameState, PhysicalCard cardToDeploy, PhysicalCard sourceCard) {
        String playerId = cardToDeploy.getOwner();
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IGNORES_LOCATION_DEPLOYMENT_RESTRICTIONS_FROM_CARD, sourceCard)) {
            if (modifier.isForPlayer(playerId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the specified card ignores location deployment restrictions in its game text.
     * @param gameState the game state
     * @param card the card
     * @return true if card ignores location deployment restrictions in its game text
     */
    @Override
    public boolean ignoresGameTextLocationDeploymentRestrictions(GameState gameState, PhysicalCard card) {
        return (!getModifiersAffectingCard(gameState, ModifierType.IGNORES_LOCATION_DEPLOYMENT_RESTRICTIONS_IN_GAME_TEXT, card).isEmpty());
    }

    /**
     * Determines if the specified card is granted the ability to deploy to the specified target.
     * @param gameState the game state
     * @param card the card
     * @param target the target
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @return true if card is granted the ability to deploy to the target, otherwise false
     */
    @Override
    public boolean isGrantedToDeployTo(GameState gameState, PhysicalCard card, PhysicalCard target, ReactActionOption reactActionOption) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_TO_TARGET, card)) {
            if (modifier.isAffectedTarget(gameState, this, target)) {
                return true;
            }
        }

        if (reactActionOption != null) {
            if (reactActionOption.isGrantedDeployToTarget()
                    && Filters.and(reactActionOption.getCardToReactFilter()).accepts(gameState, this, card)
                    && reactActionOption.getTargetFilter().accepts(gameState, this, target)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if the specified card is a squadron that is granted the ability to deploy.
     * @param gameState the game state
     * @param card the card
     * @return true if card is granted the ability to deploy, otherwise false
     */
    @Override
    public boolean isSquadronAllowedToDeploy(GameState gameState, PhysicalCard card) {
        return Filters.squadron.accepts(gameState, this, card)
                && !getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_TO_TARGET, card).isEmpty();
    }

    @Override
    public boolean grantedToDeployToDagobahTarget(GameState gameState, PhysicalCard playedCard, PhysicalCard target) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_TO_DAGOBAH_TARGET, playedCard))
            if (modifier.grantedToDeployToDagobahTarget(gameState, this, target))
                return true;

        return false;
    }

    @Override
    public boolean grantedToDeployToAsLanded(GameState gameState, PhysicalCard playedCard, PhysicalCard target) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_AS_LANDED_TO_TARGET, playedCard))
            if (modifier.isAffectedTarget(gameState, this, target))
                return true;

        return false;
    }

    @Override
    public PhysicalCard getCardIsPresentAt(GameState gameState, PhysicalCard physicalCard) {
        return getCardIsPresentAt(gameState, physicalCard, false, false);
    }

    @Override
    public PhysicalCard getCardIsPresentAt(GameState gameState, PhysicalCard physicalCard, boolean includeMovesLikeCharacter, boolean includeEnclosedInPrison) {
        // Only a character, creature, starship, vehicle, weapon or device can be "present" somewhere (include effects)
        // If includeMovesLikeCharacter is set to true, then also allow cards that "move like characters"

        // Card is "present" at a location if the card is physically at that location or is
        // attached to another card (except a starship or "enclosed" vehicle) that is "present at" that location.

        // Card is "present" on/in a starship, "enclosed" vehicle, or "prison" location if the card
        // is attached to that starship, vehicle, or location, or is attached to another card that
        // is "present" on/in that starship, vehicle, or location.
        CardCategory cardCategory = physicalCard.getBlueprint().getCardCategory();
        if (cardCategory != CardCategory.CHARACTER && cardCategory != CardCategory.STARSHIP && cardCategory != CardCategory.VEHICLE && cardCategory != CardCategory.EFFECT
                && cardCategory != CardCategory.WEAPON && cardCategory != CardCategory.DEVICE && cardCategory != CardCategory.JEDI_TEST && cardCategory != CardCategory.CREATURE
                && !physicalCard.isDejarikHologramAtHolosite()
                && (!includeMovesLikeCharacter || !physicalCard.getBlueprint().isMovesLikeCharacter()))
            return null;

        PhysicalCard atLocation = physicalCard.getAtLocation();
        if (atLocation != null)
            return atLocation;

        PhysicalCard attachedTo = physicalCard.getAttachedTo();
        if (attachedTo == null)
            return null;

        if ((attachedTo.getBlueprint().getCardCategory() == CardCategory.LOCATION && (!physicalCard.isImprisoned() || includeEnclosedInPrison))
                || (attachedTo.getBlueprint().getCardCategory() == CardCategory.STARSHIP && physicalCard.getBlueprint().getCardSubtype() != CardSubtype.STARSHIP)
                || (attachedTo.getBlueprint().getCardCategory() == CardCategory.VEHICLE && hasKeyword(gameState, attachedTo, Keyword.ENCLOSED) && physicalCard.getBlueprint().getCardSubtype() != CardSubtype.VEHICLE))
            return attachedTo;

        return getCardIsPresentAt(gameState, attachedTo);
    }

    @Override
    public boolean isPresentAt(GameState gameState, PhysicalCard physicalCard, PhysicalCard atTarget) {
        PhysicalCard presentAt = getCardIsPresentAt(gameState, physicalCard);
        return (presentAt!=null) && (presentAt.getCardId() == atTarget.getCardId());
    }

    /**
     * Determines if the two cards are "with" each other.
     *
     * @param gameState the game state
     * @param physicalCard1 a card
     * @param physicalCard2 a card
     * @return true if cards are "with" each other, otherwise false
     */
    @Override
    public boolean isWith(GameState gameState, PhysicalCard physicalCard1, PhysicalCard physicalCard2) {
        // A card is not "with" itself
        if (physicalCard1.getCardId() == physicalCard2.getCardId())
            return false;

        // Two cards are "with" each other if they are both "at" the same location
        PhysicalCard at1 = getLocationThatCardIsAt(gameState, physicalCard1);
        PhysicalCard at2 = getLocationThatCardIsAt(gameState, physicalCard2);
        return (at1 != null) && (at2 != null) && (at1.getCardId() == at2.getCardId());
    }

    /**
     * Determines if the two cards are "present with" each other.
     *
     * @param gameState the game state
     * @param card1 a card
     * @param card2 a card
     * @return true if cards are "present with" each other, otherwise false
     */
    @Override
    public boolean isPresentWith(GameState gameState, PhysicalCard card1, PhysicalCard card2) {
        return isPresentWith(gameState, card1, card2, false);
    }

    /**
     * Determines if the two cards are "present with" each other. Optionally includes cards that "move like a character".
     *
     * @param gameState the game state
     * @param card1 a card
     * @param card2 a card
     * @param includeMovesLikeCharacter true if including cards that "move like a character", otherwise false
     * @return true if cards are "present with" each other, otherwise false
     */
    @Override
    public boolean isPresentWith(GameState gameState, PhysicalCard card1, PhysicalCard card2, boolean includeMovesLikeCharacter) {
        // A card is not "present with" itself
        if (card1.getCardId() == card2.getCardId())
            return false;

        // Imprisoned characters are not "present with" anything else
        if (Filters.imprisoned.accepts(gameState, this, card1)
                || Filters.imprisoned.accepts(gameState, this, card2)) {
            return false;
        }

        // Two cards are "present with" each other if they are both "present" at/on/in the same place
        PhysicalCard presentAt1 = getCardIsPresentAt(gameState, card1, includeMovesLikeCharacter, false);
        PhysicalCard presentAt2 = getCardIsPresentAt(gameState, card2, includeMovesLikeCharacter, false);
        return (presentAt1 != null) && (presentAt2 != null) && (presentAt1.getCardId() == presentAt2.getCardId());
    }

    /**
     * Gets the system that the specified card is "at".
     *
     * @param gameState the game state
     * @param card the card
     * @return name of the planet the specified card is "at", otherwise null
     */
    @Override
    public String getSystemThatCardIsAt(GameState gameState, PhysicalCard card) {
        // A character, starship, vehicle, weapon or device is "at" a planet if
        // (1) it is on that planet
        // (2) At the bridge, cockpit or cargo bay of a starship that is present at (orbiting) that system.
        String onPlanet = getSystemThatCardIsOn(gameState, card);
        if (onPlanet != null)
            return onPlanet;

        PhysicalCard location = getLocationThatCardIsAt(gameState, card);
        if (location == null)
            return null;

        return location.getPartOfSystem();
    }

    /**
     * Gets the system that the specified card is "on".
     *
     * @param gameState the game state
     * @param card the card
     * @return name of the planet the specified card is "on", otherwise null
     */
    @Override
    public String getSystemThatCardIsOn(GameState gameState, PhysicalCard card) {
        // A character, starship, vehicle, weapon or device is "on" a planet if it is:
        // (1) Present at any site, cloud or Death Star II sector related to that planet name.
        // (2) At the bridge, cockpit or cargo bay of a starship or vehicle that is present at
        // any site, cloud or Death Star II sector related to that planet name.
        if (card.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
            if (card.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM)
                return null;
            else
                return card.getPartOfSystem();
        }

        PhysicalCard presentAt = getCardIsPresentAt(gameState, card, true, true);
        if (presentAt == null)
            return null;

        return getSystemThatCardIsOn(gameState, presentAt);
    }

    @Override
    public boolean isPlaceToBePresentOnPlanet(GameState gameState, PhysicalCard physicalCard, String planet) {

        CardCategory cardCategory = physicalCard.getBlueprint().getCardCategory();
        if (cardCategory == CardCategory.LOCATION)
            return (physicalCard.getBlueprint().getCardSubtype()!=CardSubtype.SYSTEM
                    && physicalCard.getPartOfSystem()!=null && physicalCard.getPartOfSystem().equals(planet));

        PhysicalCard presentAt = getCardIsPresentAt(gameState, physicalCard);
        if (presentAt==null)
            return false;

        return isPlaceToBePresentOnPlanet(gameState, presentAt, planet);
    }

    /**
     * Determines if the two specified cards are adjacent sectors.
     *
     * @param gameState the game state
     * @param sector1     a card
     * @param sector2     a card
     * @return true if specified cards are adjacent sectors, otherwise false
     */
    @Override
    public boolean isAdjacentSectors(GameState gameState, PhysicalCard sector1, PhysicalCard sector2) {
        Integer distance = getDistanceBetweenSectors(gameState, sector1, sector2);
        return distance != null && distance == 1;
    }

    /**
     * Gets the distance between the sector (or the sectors the cards are "at"), or null if determining a distance is not valid.
     *
     * @param gameState the game state
     * @param card1     a card
     * @param card2     a card
     * @return the distance between the sectors, or null
     */
    @Override
    public Integer getDistanceBetweenSectors(GameState gameState, PhysicalCard card1, PhysicalCard card2) {
        PhysicalCard sector1 = getLocationHere(gameState, card1);
        PhysicalCard sector2 = getLocationHere(gameState, card2);

        if (sector1 == null || sector1.getBlueprint().getCardSubtype() != CardSubtype.SECTOR
                || sector2 == null || sector2.getBlueprint().getCardSubtype() != CardSubtype.SECTOR)
            return null;

        int site1Index = sector1.getLocationZoneIndex();
        int site2Index = sector2.getLocationZoneIndex();
        int indexDistance = Math.abs(site1Index - site2Index);

        // Check if sectors are part of same system (or orbiting the same system)
        if ((sector1.getPartOfSystem() != null && sector1.getPartOfSystem().equals(sector2.getPartOfSystem()))
                || (sector1.getSystemOrbited() != null && sector1.getSystemOrbited().equals(sector2.getSystemOrbited()))) {

            return indexDistance;
        }

        return null;
    }

    /**
     * Gets the sectors in order between the cards (or between the locations the cards are "at"), or null if determining
     * sectors between is not valid.
     *
     * @param gameState the game state
     * @param card1     a card
     * @param card2     a card
     * @return the sectors in order between card1 and card2, or null
     */
    @Override
    public List<PhysicalCard> getSectorsBetween(GameState gameState, PhysicalCard card1, PhysicalCard card2) {
        PhysicalCard location1 = getLocationHere(gameState, card1);
        PhysicalCard location2 = getLocationHere(gameState, card2);

        if (location1 == null || location2 == null) {
            return null;
        }

        boolean related = false;

        if (location1.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM) {
            if (location1.getTitle().equals(location2.getPartOfSystem())
                    || (location2.getBlueprint().getCardSubtype() != CardSubtype.SYSTEM
                    && location1.getTitle().equals(location2.getSystemOrbited()))) {
                related = true;
            }
        }
        else if (location2.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM) {
            if (location2.getTitle().equals(location1.getPartOfSystem())
                    || (location1.getBlueprint().getCardSubtype() != CardSubtype.SYSTEM
                    && location2.getTitle().equals(location1.getSystemOrbited()))) {
                related = true;
            }
        }
        else if (location1.getPartOfSystem() != null && location1.getPartOfSystem().equals(location2.getPartOfSystem())) {
            related = true;
        }
        else if (location1.getSystemOrbited() != null && location1.getSystemOrbited().equals(location2.getSystemOrbited())) {
            related = true;
        }

        if (!related) {
            return null;
        }

        List<PhysicalCard> sectors = new ArrayList<PhysicalCard>();

        int location1Index = location1.getLocationZoneIndex();
        int location2Index = location2.getLocationZoneIndex();
        boolean leftToRight = location1Index < location2Index;

        // Add sectors in between (in correct order)
        List<PhysicalCard> locationsInOrder = gameState.getLocationsInOrder();
        for (int i=location1Index; i!=location2Index;) {
            if (i != location1Index
                    && locationsInOrder.get(i).getBlueprint().getCardSubtype() == CardSubtype.SECTOR) {
                sectors.add(locationsInOrder.get(i));
            }

            if (leftToRight)
                i++;
            else
                i--;
        }

        return sectors;
    }

    /**
     * Determines if the two specified cards are adjacent sites.
     *
     * @param gameState the game state
     * @param site1     a card
     * @param site2     a card
     * @return true if specified cards are adjacent sites, otherwise false
     */
    @Override
    public boolean isAdjacentSites(GameState gameState, PhysicalCard site1, PhysicalCard site2) {
        Integer distance = getDistanceBetweenSites(gameState, site1, site2);
        return distance != null && distance == 1;
    }

    /**
     * Determines if the sites are part of the same system, starship, or vehicle.
     *
     * @param gameState the game state
     * @param site1     a site
     * @param site2     a site
     * @return the sites in order between card1 and card2
     */
    @Override
    public boolean isSitesWithSameParent(GameState gameState, PhysicalCard site1, PhysicalCard site2) {
        if (site1 == null || site1.getBlueprint().getCardSubtype() != CardSubtype.SITE
                || site2 == null || site2.getBlueprint().getCardSubtype() != CardSubtype.SITE) {
            return false;
        }

        // Check if sites are part of the same system
        if (site1.getPartOfSystem() != null
                && site1.getPartOfSystem().equals(site2.getPartOfSystem())) {

            return true;
        }
        // Check if sites are part of the same starship/vehicle
        else if ((site1.getBlueprint().hasIcon(Icon.STARSHIP_SITE) && site2.getBlueprint().hasIcon(Icon.STARSHIP_SITE))
                || (site1.getBlueprint().hasIcon(Icon.VEHICLE_SITE) && site2.getBlueprint().hasIcon(Icon.VEHICLE_SITE))) {

            if (site1.getBlueprint().getRelatedStarshipOrVehiclePersona() != null) {
                return Filters.siteOfStarshipOrVehicle(site1.getBlueprint().getRelatedStarshipOrVehiclePersona(), false).accepts(gameState, this, site2);
            }
            else if (site2.getBlueprint().getRelatedStarshipOrVehiclePersona() != null) {
                return Filters.siteOfStarshipOrVehicle(site2.getBlueprint().getRelatedStarshipOrVehiclePersona(), false).accepts(gameState, this, site1);
            }

            return site1.getRelatedStarshipOrVehicle() != null
                    && site2.getRelatedStarshipOrVehicle() != null
                    && site1.getRelatedStarshipOrVehicle().getCardId() == site2.getRelatedStarshipOrVehicle().getCardId();
        }

        return false;
    }

    /**
     * Gets the distance between the sites (or the sites the cards are "at"), or null if determining a distance is not
     * valid.
     *
     * @param gameState the game state
     * @param card1     a card
     * @param card2     a card
     * @return the distance between the sites, or null
     */
    @Override
    public Integer getDistanceBetweenSites(GameState gameState, PhysicalCard card1, PhysicalCard card2) {
        PhysicalCard site1 = getLocationHere(gameState, card1);
        PhysicalCard site2 = getLocationHere(gameState, card2);

        if (!isSitesWithSameParent(gameState, site1, site2)
                || Filters.Death_Star_Trench.accepts(gameState, this, site1)
                || Filters.Death_Star_Trench.accepts(gameState, this, site2)) {
            return null;
        }

        int site1Index = site1.getLocationZoneIndex();
        int site2Index = site2.getLocationZoneIndex();

        return Math.abs(site1Index - site2Index);
    }

    /**
     * Gets the sites in order between the cards (or between the locations the cards are "at"), or null if determining
     * sites between is not valid.
     *
     * @param gameState the game state
     * @param card1     a card
     * @param card2     a card
     * @return the sites in order between card1 and card2, or null
     */
    @Override
    public List<PhysicalCard> getSitesBetween(GameState gameState, PhysicalCard card1, PhysicalCard card2) {
        PhysicalCard site1 = getLocationHere(gameState, card1);
        PhysicalCard site2 = getLocationHere(gameState, card2);

        if (!isSitesWithSameParent(gameState, site1, site2)
                || Filters.Death_Star_Trench.accepts(gameState, this, site1)
                || Filters.Death_Star_Trench.accepts(gameState, this, site2)) {
            return null;
        }

        int location1Index = site1.getLocationZoneIndex();
        int location2Index = site2.getLocationZoneIndex();
        boolean leftToRight = location1Index < location2Index;

        List<PhysicalCard> sites = new ArrayList<PhysicalCard>();

        // Add sites in between (in correct order)
        List<PhysicalCard> locationsInOrder = gameState.getLocationsInOrder();
        for (int i=location1Index; i!=location2Index;) {
            if (i != location1Index
                    && locationsInOrder.get(i).getBlueprint().getCardSubtype() == CardSubtype.SITE) {
                sites.add(locationsInOrder.get(i));
            }

            if (leftToRight)
                i++;
            else
                i--;
        }

        return sites;
    }

    /**
     * Gets the location that the specified card is "at".
     *
     * @param gameState the game state
     * @param card the card
     * @return location that the specified card is "at", otherwise null
     */
    @Override
    public PhysicalCard getLocationThatCardIsAt(GameState gameState, PhysicalCard card) {
        // An Effect or Epic Event is "at" a location if:
        // (1) Its "atLocation" or "attachedTo" is that location
        // (2) It is attached to a card that is at that location
        if (card.getBlueprint().getCardCategory()==CardCategory.DEFENSIVE_SHIELD
                || card.getBlueprint().getCardCategory()==CardCategory.EFFECT
                || card.getBlueprint().getCardCategory()==CardCategory.EPIC_EVENT
                || card.getBlueprint().getCardCategory()==CardCategory.JEDI_TEST) {
            PhysicalCard atLocation = card.getAtLocation();
            if (atLocation!=null) {
                if (atLocation.getBlueprint().getCardCategory()==CardCategory.LOCATION)
                    return atLocation;
                else
                    return getLocationThatCardIsAt(gameState, atLocation);
            }

            PhysicalCard attachedTo = card.getAttachedTo();
            if (attachedTo!=null) {
                if (attachedTo.getBlueprint().getCardCategory()==CardCategory.LOCATION)
                    return attachedTo;
                else
                    return getLocationThatCardIsAt(gameState, attachedTo);
            }

            return null;
        }

        // A character, starship, vehicle, weapon or device is "at" a location if it is:
        // (1) Present at that location
        // (2) Abort a starship or vehicle at that location.
        PhysicalCard presentAt = getCardIsPresentAt(gameState, card, true, true);
        if (presentAt==null)
            return null;

        CardCategory cardCategory = presentAt.getBlueprint().getCardCategory();
        if (cardCategory == CardCategory.STARSHIP || cardCategory == CardCategory.VEHICLE)
            return getLocationThatCardIsAt(gameState, presentAt);

        if (cardCategory == CardCategory.LOCATION)
            return presentAt;

        return null;
    }

    /**
     * Gets the locations that the specified cards are "at".
     *
     * @param gameState the game state
     * @param cards     the cards
     * @return locations that the specified cards are "at"
     */
    @Override
    public Collection<PhysicalCard> getLocationsThatCardsAreAt(GameState gameState, Collection<PhysicalCard> cards) {
        Set<PhysicalCard> locations = new HashSet<PhysicalCard>();
        for (PhysicalCard card : cards) {
            PhysicalCard location = getLocationThatCardIsAt(gameState, card);
            if (location != null) {
                locations.add(location);
            }
        }

        return locations;
    }

    /**
     * Gets the location that the specified card is "present" at.
     *
     * @param gameState the game state
     * @param card      the card
     * @return location that the specified card is "present" at, otherwise null
     */
    @Override
    public PhysicalCard getLocationThatCardIsPresentAt(GameState gameState, PhysicalCard card) {
        PhysicalCard presentAt = getCardIsPresentAt(gameState, card, true, false);
        if (presentAt == null || presentAt.getBlueprint().getCardCategory() != CardCategory.LOCATION)
            return null;

        return presentAt;
    }

    /**
     * Gets the locations that the specified cards are "present" at.
     *
     * @param gameState the game state
     * @param cards     the cards
     * @return locations that the specified cards are "present" at
     */
    @Override
    public Collection<PhysicalCard> getLocationsThatCardsArePresentAt(GameState gameState, Collection<PhysicalCard> cards) {
        Set<PhysicalCard> locations = new HashSet<PhysicalCard>();
        for (PhysicalCard card : cards) {
            PhysicalCard location = getLocationThatCardIsPresentAt(gameState, card);
            if (location != null) {
                locations.add(location);
            }
        }

        return locations;
    }

    @Override
    public int getParsecNumber(GameState gameState, PhysicalCard physicalCard) {
        SwccgCardBlueprint blueprint = physicalCard.getBlueprint();
        if (blueprint.getCardCategory()!=CardCategory.LOCATION
                || blueprint.getCardSubtype()!=CardSubtype.SYSTEM)
            return 0;

        return physicalCard.getParsec();
    }

    @Override
    public boolean isAtSite(GameState gameState, PhysicalCard physicalCard, PhysicalCard site) {
        // A character, starship, vehicle, weapon or device is "at" a site if it is:
        // (1) Present at that site
        // (2) Abort a starship or vehicle at that site.

        PhysicalCard location = getLocationThatCardIsAt(gameState, physicalCard);
        if (location==null || Filters.site.accepts(gameState, this, location))
            return false;

        return (location.getCardId()==site.getCardId());
    }

    @Override
    public boolean isAboard(GameState gameState, PhysicalCard physicalCard, PhysicalCard starshipOrVehicle, boolean includeAboardCargoOf, boolean includeRelatedSites) {
        if (starshipOrVehicle.getBlueprint().getCardCategory() != CardCategory.STARSHIP && starshipOrVehicle.getBlueprint().getCardCategory() != CardCategory.VEHICLE)
            return false;

        // A character, starship, vehicle, weapon or device is "aboard" (or "on") a starship or vehicle if it is:
        // (1) Present at any site related to that starship or vehicle.
        // (2) At the bridge, cockpit or cargo bay of that starship or vehicle.
        CardCategory cardCategory = physicalCard.getBlueprint().getCardCategory();
        if (cardCategory!=CardCategory.CHARACTER && cardCategory!=CardCategory.STARSHIP && cardCategory!=CardCategory.VEHICLE && cardCategory!=CardCategory.WEAPON && cardCategory!=CardCategory.DEVICE)
            return false;


        List<PhysicalCard> physicalCardList = gameState.getAboardCards(starshipOrVehicle, includeAboardCargoOf);
        if (!Filters.filter(physicalCardList, gameState.getGame(), physicalCard).isEmpty())
            return true;

        if (!includeRelatedSites)
            return false;

        PhysicalCard location = getLocationThatCardIsAt(gameState, physicalCard);
        if (location==null)
            return false;

        if (location.getBlueprint().hasIcon(Icon.STARSHIP_SITE) || location.getBlueprint().hasIcon(Icon.VEHICLE_SITE)) {
            if (location.getBlueprint().getRelatedStarshipOrVehiclePersona() != null
                    && starshipOrVehicle.getBlueprint().hasPersona(location.getBlueprint().getRelatedStarshipOrVehiclePersona())) {
                return true;
            }

            if (location.getRelatedStarshipOrVehicle() != null
                    && starshipOrVehicle.getCardId() == location.getRelatedStarshipOrVehicle().getCardId()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isAtStarshipSite(GameState gameState, PhysicalCard physicalCard) {

        PhysicalCard site = getLocationThatCardIsAt(gameState, physicalCard);
        return (site!=null && site.getBlueprint().hasIcon(Icon.STARSHIP_SITE));
    }

    @Override
    public boolean isAtVehicleSite(GameState gameState, PhysicalCard physicalCard) {

        PhysicalCard site = getLocationThatCardIsAt(gameState, physicalCard);
        return (site!=null && site.getBlueprint().hasIcon(Icon.VEHICLE_SITE));
    }

    @Override
    public boolean isAtStarshipSiteOrVehicleSiteOfPersona(GameState gameState, PhysicalCard physicalCard, Persona starshipOrVehicle) {

        PhysicalCard site = getLocationThatCardIsAt(gameState, physicalCard);
        if (site==null)
            return false;

        // TODO: Since non-unique starship/vehicle sites will not have the starship/vehicle persona in the blueprint, need to check this a different way
        // TODO: when those cards are added

        if (site.getBlueprint().hasIcon(Icon.STARSHIP_SITE) || site.getBlueprint().hasIcon(Icon.VEHICLE_SITE)) {
            return site.getBlueprint().getRelatedStarshipOrVehiclePersona() != null
                    && site.getBlueprint().getRelatedStarshipOrVehiclePersona()==starshipOrVehicle;
        }

        return false;
    }

    @Override
    public boolean isAlone(GameState gameState, PhysicalCard physicalCard) {
         return isCharacterAlone(gameState, physicalCard) || isStarshipOrVehicleAlone(gameState, physicalCard);
    }

    @Override
    public boolean isCharacterAlone(GameState gameState, PhysicalCard physicalCard) {
        // Your character or permanent pilot is alone at a location if it is active
        // and you have no other cards at that location that are active characters
        // or active cards with ability. Combo Cards (such as Artoo & Threepio or Tonnika Sisters),
        // TODO: Handle the rest...
        // and a permanent pilot of a starship or vehicle that has multiple permanent pilots
        // (such as Executor or a TIE Squadron), are not considered to be alone.
        // Your starship or vehicle is alone at a location if the only active characters,
        // vehicles and starships you have at that location are aboard that starship or vehicle.
        if (physicalCard.getBlueprint().getCardCategory() != CardCategory.CHARACTER
                || physicalCard.getBlueprint().isComboCard())
            return false;

        PhysicalCard location = getLocationThatCardIsAt(gameState, physicalCard);
        if (location==null)
            return false;

        if (Filters.canSpot(gameState.getGame(), null, Filters.and(Filters.not(physicalCard), Filters.at(location),
                Filters.owner(physicalCard.getOwner()), Filters.or(CardCategory.CHARACTER, Filters.abilityMoreThan(0, true)))))
            return false;

        return true;
    }

    @Override
    public boolean isStarshipOrVehicleAlone(GameState gameState, PhysicalCard physicalCard) {
        // Your character or permanent pilot is alone at a location if it is active
        // and you have no other cards at that location that are active characters
        // or active cards with ability. Combo Cards (such as Artoo & Threepio or Tonnika Sisters),
        // TODO: Handle the rest...
        // and a permanent pilot of a starship or vehicle that has multiple permanent pilots
        // (such as Executor or a TIE Squadron), are not considered to be alone.
        // Your starship or vehicle is alone at a location if the only active characters,
        // vehicles and starships you have at that location are aboard that starship or vehicle.
        if ((physicalCard.getBlueprint().getCardCategory() != CardCategory.VEHICLE
                && physicalCard.getBlueprint().getCardCategory() != CardCategory.STARSHIP)
                || physicalCard.getBlueprint().isComboCard())
            return false;

        PhysicalCard location = getLocationThatCardIsAt(gameState, physicalCard);
        if (location==null)
            return false;

        if (Filters.canSpot(gameState.getGame(), null, Filters.and(Filters.at(location), Filters.owner(physicalCard.getOwner()),
                Filters.or(CardCategory.CHARACTER, CardCategory.STARSHIP, CardCategory.VEHICLE),
                Filters.not(Filters.or(physicalCard, Filters.aboardOrAboardCargoOf(physicalCard))))))
            return false;

        return true;
    }

    @Override
    public boolean hasPermanentPilotAlone(GameState gameState, PhysicalCard physicalCard) {
        // Your character or permanent pilot is alone at a location if it is active
        // and you have no other cards at that location that are active characters
        // or active cards with ability. Combo Cards (such as Artoo & Threepio or Tonnika Sisters),
        // TODO: Handle the rest...
        // and a permanent pilot of a starship or vehicle that has multiple permanent pilots
        // (such as Executor or a TIE Squadron), are not considered to be alone.
        // Your starship or vehicle is alone at a location if the only active characters,
        // vehicles and starships you have at that location are aboard that starship or vehicle.
        if ((physicalCard.getBlueprint().getCardCategory() != CardCategory.VEHICLE
                && physicalCard.getBlueprint().getCardCategory() != CardCategory.STARSHIP)
                || physicalCard.getBlueprint().isComboCard())
            return false;

        if (getPermanentsAboard(gameState, physicalCard).size() > 1)
            return false;

        PhysicalCard location = getLocationThatCardIsAt(gameState, physicalCard);
        if (location==null)
            return false;

        if (Filters.canSpot(gameState.getGame(), null, Filters.and(Filters.not(physicalCard), Filters.at(location),
                Filters.owner(physicalCard.getOwner()), Filters.or(Filters.character, Filters.abilityMoreThan(0, true)))))
            return false;

        return true;
    }

    @Override
    public boolean hasPermanentPilot(GameState gameState, PhysicalCard physicalCard) {
        if (physicalCard.getBlueprint().getCardCategory()!=CardCategory.STARSHIP
                && physicalCard.getBlueprint().getCardCategory()!=CardCategory.VEHICLE)
            return false;

        return !getPermanentPilotsAboard(gameState, physicalCard).isEmpty();
    }

    @Override
    public boolean hasPermanentAstromech(GameState gameState, PhysicalCard physicalCard) {
        if (physicalCard.getBlueprint().getCardCategory()!=CardCategory.STARSHIP
                && physicalCard.getBlueprint().getCardCategory()!=CardCategory.VEHICLE)
            return false;

        return !getPermanentAstromechsAboard(gameState, physicalCard).isEmpty();
    }

    /**
     * Determines if a specified player's total ability at the specified location may not be reduced.
     * @param gameState the game state
     * @param location the location
     * @param playerId the player
     * @return true or false
     */
    @Override
    public boolean isProhibitedFromHavingTotalAbilityReduced(GameState gameState, PhysicalCard location, String playerId) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_HAVE_TOTAL_ABILITY_AT_LOCATION_REDUCED, location)) {
            if (modifier.isForPlayer(playerId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public float getTotalAbilityAtLocation(GameState gameState, String player, PhysicalCard physicalCard) {
        return getTotalAbilityAtLocation(gameState, player, physicalCard, false, false, false, null, false, false, null);
    }

    @Override
    public float getTotalAbilityAtLocation(GameState gameState, String player, PhysicalCard physicalCard, boolean forPresence, boolean forControl, boolean forBattle,
                                           String playerInitiatingBattle, boolean forBattleDestiny, boolean onlyPiloting, Map<InactiveReason, Boolean> spotOverrides) {
        float result = 0;

        Filter cardsPresentFilter = Filters.and(Filters.owner(player), Filters.present(physicalCard));
        // If checking if control location, skip matching operatives
        if (forControl) {
            cardsPresentFilter = Filters.and(cardsPresentFilter, Filters.not(Filters.operativeOnMatchingPlanet));
        }
        Collection<PhysicalCard> cardsPresentAt = Filters.filterActive(gameState.getGame(), null, spotOverrides, cardsPresentFilter);
        for (PhysicalCard presentCard : cardsPresentAt) {
            // If this is for battle, skip cards that cannot participate in battle (or already have)
            if (forBattle && (isProhibitedFromParticipatingInBattle(gameState, presentCard, playerInitiatingBattle) || hasParticipatedInBattleAtOtherLocation(presentCard, physicalCard)))
                continue;

            // If card is a character or vehicle, add ability of the character or vehicle,
            // and include permanent pilot ability for vehicles and starships
            CardCategory cardCategory = presentCard.getBlueprint().getCardCategory();
            if (presentCard.isDejarikHologramAtHolosite()
                    || (cardCategory == CardCategory.CHARACTER && !onlyPiloting) || cardCategory == CardCategory.VEHICLE || cardCategory == CardCategory.STARSHIP) {
                // If this is for battle destiny, skip cards not participating in battle
                if (!forBattleDestiny || gameState.isParticipatingInBattle(presentCard)) {
                    if (forBattleDestiny) {
                        result += getAbilityForBattleDestiny(gameState, presentCard);
                    }
                    else {
                        if (!forBattle || player.equals(playerInitiatingBattle) || !Filters.mayNotBeBattled.accepts(gameState, this, presentCard)) {
                            result += getAbility(gameState, presentCard, true);
                        }
                    }
                }
            }

            // If card is a starship or enclosed vehicle, add ability of cards present aboard that starship or vehicle
            if (cardCategory == CardCategory.STARSHIP || Filters.enclosed_vehicle.accepts(gameState, this, presentCard)) {

                // Check if unpiloted starship or vehicle
                boolean isUnpiloted = (forBattleDestiny || onlyPiloting) && !isPiloted(gameState, presentCard, false);

                Filter cardsPresentAboardFilter = Filters.and(Filters.owner(player), Filters.aboardExceptRelatedSites(presentCard));
                // If checking if control location, skip matching operatives
                if (forControl) {
                    cardsPresentAboardFilter = Filters.and(cardsPresentAboardFilter, Filters.not(Filters.operativeOnMatchingPlanet));
                }
                Collection<PhysicalCard> cardsPresentAboard = Filters.filterActive(gameState.getGame(), null, spotOverrides, cardsPresentAboardFilter);
                for (PhysicalCard presentCardAboard : cardsPresentAboard) {
                    // If this is for battle, skip cards that cannot participate in battle
                    if (forBattle && (isProhibitedFromParticipatingInBattle(gameState, presentCardAboard, playerInitiatingBattle) || hasParticipatedInBattleAtOtherLocation(presentCardAboard, physicalCard)))
                        continue;

                    // If only counting ability piloting, only include pilots (if piloted)
                    boolean isPiloting = !isUnpiloted && Filters.or(Filters.piloting(presentCard), Filters.driving(presentCard)).accepts(gameState, this, presentCardAboard);
                    if (onlyPiloting && !isPiloting)
                        continue;

                    CardCategory presentAboardCardCategory = presentCardAboard.getBlueprint().getCardCategory();
                    if (presentAboardCardCategory == CardCategory.STARSHIP || presentAboardCardCategory == CardCategory.VEHICLE || presentAboardCardCategory == CardCategory.CHARACTER) {
                        // If this is for battle destiny, skip cards not participating in battle, and only count pilots (or passengers that add ability toward battle destiny)
                        if (!forBattleDestiny || gameState.isParticipatingInBattle(presentCardAboard)) {
                            if (forBattleDestiny) {
                                if (isPiloting || passengerAppliesAbilityForBattleDestiny(gameState, presentCardAboard)) {
                                    result += getAbilityForBattleDestiny(gameState, presentCardAboard);
                                }
                            }
                            else {
                                if (!forBattle || player.equals(playerInitiatingBattle) || !Filters.mayNotBeBattled.accepts(gameState, this, presentCardAboard)) {
                                    result += getAbility(gameState, presentCardAboard, true);
                                }
                            }
                        }
                    }
                }
            }
        }

        // Apply modifiers to total ability at location
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TOTAL_ABILITY_AT_LOCATION, physicalCard)) {
            if (modifier.isForPlayer(player)) {
                float modifierAmount = modifier.getValue(gameState, this, physicalCard);
                if (modifierAmount >= 0 || !isProhibitedFromHavingTotalAbilityReduced(gameState, physicalCard, player)) {
                    result += modifierAmount;
                }
            }
        }

        // Apply modifiers to total ability that can be used for drawing battle destiny
        if (forBattleDestiny) {
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TOTAL_ABILITY_FOR_BATTLE_DESTINY, physicalCard)) {
                if (modifier.isForPlayer(player)) {
                    result += modifier.getValue(gameState, this, physicalCard);
                }
            }
        }

        // Check if value was reset to an "unmodifiable value", and use lowest found
        Float lowestResetValue = null;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_TOTAL_ABILITY_AT_LOCATION, physicalCard)) {
            if (modifier.isForPlayer(player)) {
                float modifierAmount = modifier.getValue(gameState, this, physicalCard);
                if (modifierAmount >= result || !isProhibitedFromHavingTotalAbilityReduced(gameState, physicalCard, player)) {
                    lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
                }
            }
        }
        if (lowestResetValue != null) {
            return Math.max(0, lowestResetValue);
        }

        return Math.max(0, result);
    }

    /**
     * Gets the total ability the specified player has present at the specified location.
     * @param gameState the game state
     * @param player the player
     * @param location the location
     * @return the total ability present
     */
    @Override
    public float getTotalAbilityPresentAtLocation(GameState gameState, String player, PhysicalCard location) {
        float result = 0;
        Collection<PhysicalCard> cardsPresentAt = Filters.filterActive(gameState.getGame(), null, Filters.and(Filters.owner(player), Filters.present(location)));
        for (PhysicalCard presentCard : cardsPresentAt) {
            // If card is a character or vehicle, add ability of the character or vehicle
            CardCategory cardCategory = presentCard.getBlueprint().getCardCategory();
            if (cardCategory == CardCategory.CHARACTER || cardCategory == CardCategory.VEHICLE) {
                result += getAbility(gameState, presentCard, false);
            }
        }

        return Math.max(0, result);
    }

    @Override
    public boolean hasPresenceAt(GameState gameState, String player, PhysicalCard physicalCard, boolean forBattle, String playerInitiatingBattle, Map<InactiveReason, Boolean> spotOverrides) {
        Filter presenceIconFilter = Filters.and(Filters.owner(player), Icon.PRESENCE);
        if (forBattle) {
            presenceIconFilter = Filters.and(presenceIconFilter, Filters.canParticipateInBattleAt(physicalCard, playerInitiatingBattle));
            if (!player.equals(playerInitiatingBattle)) {
                presenceIconFilter = Filters.and(presenceIconFilter, Filters.not(Filters.mayNotBeBattled));
            }
        }
        else {
            presenceIconFilter = Filters.and(presenceIconFilter, Filters.at(physicalCard));
        }

        // Check for [Presence icon] character.
        if (Filters.canSpot(gameState.getGame(), null, spotOverrides, presenceIconFilter))
            return true;

        // Having presence at a location is defined as
        // (1) having total ability of 1 or higher present at that location or
        // (2) having a vehicle or starship present at that location that has total ability of 1 or higher at its bridge, cockpit or cargo bay.

        return getTotalAbilityAtLocation(gameState, player, physicalCard, true, false, forBattle, playerInitiatingBattle, false, false, spotOverrides) >= 1;
    }

    /**
     * Determines if the specified player occupies the specified location.
     *
     * @param gameState the game state
     * @param location the location
     * @param playerId the player
     * @return true if the player occupies the location, otherwise false
     */
    @Override
    public boolean occupiesLocation(GameState gameState, PhysicalCard location, String playerId) {
        return occupiesLocation(gameState, location, playerId, null);
    }

    /**
     * Determines if the specified player occupies the specified location.
     *
     * @param gameState the game state
     * @param location the location
     * @param playerId the player
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @return true if the player occupies the location, otherwise false
     */
    @Override
    public boolean occupiesLocation(GameState gameState, PhysicalCard location, String playerId, Map<InactiveReason, Boolean> spotOverrides) {
        if (location.getBlueprint().getCardCategory() != CardCategory.LOCATION)
            return false;

        return hasPresenceAt(gameState, playerId, location, false, null, spotOverrides);
    }

    /**
     * Determines if the specified player controls the specified location.
     *
     * @param gameState the game state
     * @param location the location
     * @param playerId the player
     * @return true if the player controls the location, otherwise false
     */
    @Override
    public boolean controlsLocation(GameState gameState, PhysicalCard location, String playerId) {
        return controlsLocation(gameState, location, playerId, null);
    }

    /**
     * Determines if the specified player controls the specified location.
     *
     * @param gameState the game state
     * @param location the location
     * @param playerId the player
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @return true if the player controls the location, otherwise false
     */
    @Override
    public boolean controlsLocation(GameState gameState, PhysicalCard location, String playerId, Map<InactiveReason, Boolean> spotOverrides) {
        if (!occupiesLocation(gameState, location, playerId, spotOverrides))
            return false;

        if (occupiesLocation(gameState, location, gameState.getOpponent(playerId), spotOverrides))
            return false;

        if (!meetsExtraRequirementsToControlLocation(gameState, location, playerId, spotOverrides))
            return false;

        return true;
    }

    private boolean meetsExtraRequirementsToControlLocation(GameState gameState, PhysicalCard location, String player, Map<InactiveReason, Boolean> spotOverrides) {
        Float minAbility = null;

        // Check if a minimum amount of ability is required to control location.
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.ABILITY_REQUIRED_TO_CONTROL_LOCATION, location)) {
            if (modifier.isForPlayer(player)) {
                float value = modifier.getValue(gameState, this, location);
                if (minAbility == null || minAbility < value) {
                    minAbility = value;
                }
            }
        }

        if (minAbility != null) {
            if (getTotalAbilityAtLocation(gameState, player, location, false, true, false, null, false, false, spotOverrides) < minAbility) {
                return false;
            }
        }

        // Check for operative on matching planet (there needs to be a [Presence] Icon or ability>=1 from other cards)
        if (Filters.canSpot(gameState.getGame(), null, spotOverrides, Filters.and(Filters.owner(player), Filters.operativeOnMatchingPlanet, Filters.at(location)))) {
            // Check for [Presence icon] character that is not a operative on matching planet
            if (Filters.canSpot(gameState.getGame(), null, spotOverrides, Filters.and(Filters.owner(player), Icon.PRESENCE, Filters.not(Filters.operativeOnMatchingPlanet), Filters.at(location)))) {
                return true;
            }

            if (getTotalAbilityAtLocation(gameState, player, location, false, true, false, null, false, false, spotOverrides) < 1) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines if the specified location is a battleground.
     *
     * @param gameState the game state
     * @param location the location
     * @param ignoreForceIconsFromCard the card from which added Force icons are ignored when checking if battleground
     * @return true if location is a battleground, otherwise false
     */
    @Override
    public boolean isBattleground(GameState gameState, PhysicalCard location, PhysicalCard ignoreForceIconsFromCard) {
        return isBattleground(gameState, location, ignoreForceIconsFromCard, new ModifierCollectorImpl());
    }

    /**
     * Determines if the specified location is a battleground.
     *
     * @param gameState the game state
     * @param location the location
     * @param ignoreForceIconsFromCard the card from which added Force icons are ignored when checking if battleground
     * @param modifierCollector collector of affecting modifiers
     * @return true if location is a battleground, otherwise false
     */
    @Override
    public boolean isBattleground(GameState gameState, PhysicalCard location, PhysicalCard ignoreForceIconsFromCard, ModifierCollector modifierCollector) {
        if (location.getBlueprint().getCardCategory() != CardCategory.LOCATION)
            return false;

        // Holosites are never battlegrounds
        if (Filters.holosite.accepts(gameState, this, location))
            return false;

        // Coruscant: Galactic Senate is never a battleground
        if (Filters.Galactic_Senate.accepts(gameState, this, location))
            return false;

        // Dagobah locations are never battlegrounds
        if (Filters.Dagobah_location.accepts(gameState, this, location))
            return false;

        // Audience Chamber when Bo Shuda is deployed there is never a battleground
        if (Filters.and(Filters.Audience_Chamber, Filters.hasAttached(Filters.Bo_Shuda)).accepts(gameState, this, location))
            return false;

        // ◇ Desert where a Sandwhirl is present is never a battleground.
        if (Filters.and(Filters.generic, Filters.desert, Filters.hasAttached(Filters.Sandwhirl)).accepts(gameState, this, location))
            return false;

        // Tatooine: Podrace Arena while either player has a race total > 0. While Expand The Empire is deployed
        // on the Tatooine: Podrace Arena, the adjacent sites are also prohibited from being battlegrounds while
        // either player has a race total > 0
        if (gameState.isDuringPodrace()) {
            if (getHighestRaceTotal(gameState, gameState.getDarkPlayer()) > 0 || getHighestRaceTotal(gameState, gameState.getLightPlayer()) > 0) {
                if (Filters.or(Filters.Podrace_Arena, Filters.adjacentSiteTo(null, Filters.and(Filters.Podrace_Arena,
                        Filters.hasAttached(Filters.Expand_The_Empire)))).accepts(gameState, this, location)) {
                    return false;
                }
            }
        }

        // Shielded Hoth locations are never battlegrounds
        if (isLocationUnderHothEnergyShield(gameState, location))
            return false;

        // Must have both Light and Dark icons
        if (hasLightAndDarkForceIcons(gameState, location, ignoreForceIconsFromCard))
            return true;

        // Check if considered a battleground regardless of Force icons
        boolean retVal = false;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.BATTLEGROUND, location)) {
            retVal = true;
            modifierCollector.addModifier(modifier);
        }

        return retVal;
    }

    /**
     * Determines if the specified starship can deploy as landed to the specified location.
     *
     * @param gameState the game state
     * @param location the location
     * @param starship the starship
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @return true if starship may deploy as landed to the location, otherwise false
     */
    @Override
    public boolean isLocationStarshipMayDeployToAsLanded(GameState gameState, PhysicalCard location, PhysicalCard starship, DeploymentRestrictionsOption deploymentRestrictionsOption) {
        if (starship.getBlueprint().getCardCategory() != CardCategory.STARSHIP)
            return false;

        if (location.getBlueprint().getCardCategory() != CardCategory.LOCATION)
            return false;

        if (isDockingBay(gameState, location))
            return true;

        if (Filters.launch_bay.accepts(gameState, this, location))
            return true;

        if (deploymentRestrictionsOption != null && deploymentRestrictionsOption.isAllowDeployLandedToExteriorSites() && Filters.exterior_site.accepts(gameState, this, location))
            return true;

        return grantedToDeployToAsLanded(gameState, starship, location);
    }

    /**
     * Determines if the specified location is a docking bay.
     *
     * @param gameState the game state
     * @param location the location
     * @return true if location is a docking bay, otherwise false
     */
    @Override
    public boolean isDockingBay(GameState gameState, PhysicalCard location) {
        if (location.getBlueprint().getCardCategory() != CardCategory.LOCATION)
            return false;

        return location.getBlueprint().hasKeyword(Keyword.DOCKING_BAY) && !location.isBlownAway();
    }

    /**
     * Determines if the specified location is the battle location.
     *
     * @param gameState the game state
     * @param location the location
     * @return true if location is the battle location, otherwise false
     */
    @Override
    public boolean isBattleLocation(GameState gameState, PhysicalCard location) {
        if (location.getBlueprint().getCardCategory() != CardCategory.LOCATION)
            return false;

        PhysicalCard battleLocation = gameState.getBattleLocation();

        return battleLocation != null
                && battleLocation.getCardId() == location.getCardId();
    }

    /**
     * Determines if the specified location is the Force drain location.
     *
     * @param gameState the game state
     * @param location the location
     * @return true if location is the Force drain location, otherwise false
     */
    @Override
    public boolean isForceDrainLocation(GameState gameState, PhysicalCard location) {
        if (location.getBlueprint().getCardCategory() != CardCategory.LOCATION)
            return false;

        PhysicalCard forceDrainLocation = gameState.getForceDrainLocation();

        return forceDrainLocation != null
                && forceDrainLocation.getCardId() == location.getCardId();
    }

    /**
     * Determines if the specified locations are related locations.
     *
     * @param gameState the game state
     * @param location1 a location
     * @param location2 a location
     * @return true if the locations are related locations, otherwise false
     */
    @Override
    public boolean isRelatedLocations(GameState gameState, PhysicalCard location1, PhysicalCard location2) {
        if (location1.getBlueprint().getCardCategory() != CardCategory.LOCATION
                || location2.getBlueprint().getCardCategory() != CardCategory.LOCATION
                || location1.getZone() != Zone.LOCATIONS || location2.getZone() != Zone.LOCATIONS
                || location1.getCardId() == location2.getCardId()) {
            return false;
        }

        // Two asteroid sectors are related if they are orbiting the same system
        if (location1.getBlueprint().hasKeyword(Keyword.ASTEROID) && location2.getBlueprint().hasKeyword(Keyword.ASTEROID)) {
            String relatedSystemName = location1.getSystemOrbited();
            return (relatedSystemName != null && relatedSystemName.equals(location2.getSystemOrbited()));
        }

        // System and asteroid sector are related if the sector is orbiting the system
        if (location1.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM && location2.getBlueprint().hasKeyword(Keyword.ASTEROID)) {
            return location1.getTitle().equals(location2.getSystemOrbited());
        }
        if (location2.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM && location1.getBlueprint().hasKeyword(Keyword.ASTEROID)) {
            return location2.getTitle().equals(location1.getSystemOrbited());
        }

        // Two sites or sectors are related if they are part of the same system
        if ((location1.getBlueprint().getCardSubtype() == CardSubtype.SITE || location1.getBlueprint().getCardSubtype() == CardSubtype.SECTOR)
                && (location2.getBlueprint().getCardSubtype() == CardSubtype.SITE || location2.getBlueprint().getCardSubtype() == CardSubtype.SECTOR)) {
            String relatedSystemName = location1.getPartOfSystem();
            if (relatedSystemName != null && relatedSystemName.equals(location2.getPartOfSystem())) {
                return true;
            }
        }

        // System and site or sector are related if sector is part of the system
        if (location1.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM &&
                (location2.getBlueprint().getCardSubtype() == CardSubtype.SITE || location2.getBlueprint().getCardSubtype() == CardSubtype.SECTOR)) {
            // Check if site or sector belongs to same system
            return (location2.getPartOfSystem() != null && location2.getPartOfSystem().equals(location1.getTitle()));
        }
        if (location2.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM &&
                (location1.getBlueprint().getCardSubtype() == CardSubtype.SITE || location1.getBlueprint().getCardSubtype() == CardSubtype.SECTOR)) {
            // Check if site or sector belongs to same system
            return (location1.getPartOfSystem() != null && location1.getPartOfSystem().equals(location2.getTitle()));
        }

        // Check if starship and vehicle sites are related
        if ((location1.getBlueprint().hasIcon(Icon.STARSHIP_SITE) || location1.getBlueprint().hasIcon(Icon.VEHICLE_SITE))
                && location2.getBlueprint().hasIcon(Icon.STARSHIP_SITE) || location2.getBlueprint().hasIcon(Icon.VEHICLE_SITE)) {

            // Check if persona matches
            if (location1.getBlueprint().getRelatedStarshipOrVehiclePersona() != null) {
                return Filters.siteOfStarshipOrVehicle(location1.getBlueprint().getRelatedStarshipOrVehiclePersona(), false).accepts(gameState, this, location2);
            }

            // Check if starship/vehicle card matches
            if (location1.getRelatedStarshipOrVehicle() != null) {
                return Filters.siteOfStarshipOrVehicle(location1.getRelatedStarshipOrVehicle()).accepts(gameState, this, location2);
            }

            return false;
        }

        // If site is a vehicle site, then it is related to sites on the system it is at a site of
        if (location1.getBlueprint().hasIcon(Icon.VEHICLE_SITE) && location2.getBlueprint().getCardSubtype() == CardSubtype.SITE) {
            PhysicalCard vehicle = location1.getRelatedStarshipOrVehicle();
            if (vehicle == null && location1.getBlueprint().getRelatedStarshipOrVehiclePersona() != null) {
                vehicle = Filters.findFirstFromAllOnTable(gameState.getGame(), location1.getBlueprint().getRelatedStarshipOrVehiclePersona());
            }
            PhysicalCard locationVehicleIsAt = getLocationThatCardIsAt(gameState, vehicle);
            if (locationVehicleIsAt != null && locationVehicleIsAt.getBlueprint().getCardSubtype() == CardSubtype.SITE) {
                String partOfSystem = locationVehicleIsAt.getPartOfSystem();

                if (partOfSystem != null && partOfSystem.equals(location2.getPartOfSystem())) {
                    return true;
                }
            }

            return false;
        }
        if (location2.getBlueprint().hasIcon(Icon.VEHICLE_SITE) && location1.getBlueprint().getCardSubtype() == CardSubtype.SITE) {
            PhysicalCard vehicle = location2.getRelatedStarshipOrVehicle();
            if (vehicle == null && location2.getBlueprint().getRelatedStarshipOrVehiclePersona() != null) {
                vehicle = Filters.findFirstFromAllOnTable(gameState.getGame(), location2.getBlueprint().getRelatedStarshipOrVehiclePersona());
            }
            PhysicalCard locationVehicleIsAt = getLocationThatCardIsAt(gameState, vehicle);
            if (locationVehicleIsAt != null && locationVehicleIsAt.getBlueprint().getCardSubtype() == CardSubtype.SITE) {
                String partOfSystem = locationVehicleIsAt.getPartOfSystem();

                if (partOfSystem != null && partOfSystem.equals(location1.getPartOfSystem())) {
                    return true;
                }
            }

            return false;
        }

        // Big One and its Asteroid Cave (Space Slug Belly) are related
        if (Filters.Big_One.accepts(gameState, this, location1) && Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly.accepts(gameState, this, location2)) {
            String relatedSystemName = location1.getSystemOrbited();
            return (relatedSystemName != null && relatedSystemName.equals(location2.getSystemOrbited()));
        }
        if (Filters.Big_One.accepts(gameState, this, location2) && Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly.accepts(gameState, this, location1)) {
            String relatedSystemName = location2.getSystemOrbited();
            return (relatedSystemName != null && relatedSystemName.equals(location1.getSystemOrbited()));
        }

        return false;
    }

    /**
     * Determines if the specified location is a starship or vehicle site of the specified starship or vehicle.
     *
     * @param gameState the game state
     * @param starshipOrVehicle a starship or vehicle
     * @param location a location
     * @return true if the locations are related locations, otherwise false
     */
    @Override
    public boolean isRelatedStarshipOrVehicleSite(GameState gameState, PhysicalCard starshipOrVehicle, PhysicalCard location) {
        if (!Filters.or(Filters.starship_site, Filters.vehicle_site).accepts(gameState, this, location))
            return false;

        if (location.getBlueprint().getRelatedStarshipOrVehiclePersona() != null
                && starshipOrVehicle.getBlueprint().hasPersona(location.getBlueprint().getRelatedStarshipOrVehiclePersona()))
            return true;

        if (location.getRelatedStarshipOrVehicle() != null
                && location.getRelatedStarshipOrVehicle().getCardId() == starshipOrVehicle.getCardId())
            return true;

        return false;
    }


    /**
     * Determines if the card may deploy to the target without presence or Force icons.
     * @param gameState the game state
     * @param target the target
     * @param cardToDeploy the card to deploy
     * @return true if card can be deployed to the target without presence or Force icons, otherwise false
     */
    @Override
    public boolean mayDeployToTargetWithoutPresenceOrForceIcons(GameState gameState, PhysicalCard target, PhysicalCard cardToDeploy) {
        // Do not need presence to deploy a Spy (or card that deploys without presence or Force icons) or "moves like character"
        if (hasKeyword(gameState, cardToDeploy, Keyword.SPY) || cardToDeploy.getBlueprint().isMovesLikeCharacter())
            return true;

        // Check for deploy without presence or Force icons modifier
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_WITHOUT_PRESENCE_OR_FORCE_ICONS, cardToDeploy))
            if (modifier.isAffectedTarget(gameState, this, target))
                return true;

        return false;
    }

    @Override
    public boolean mayDeployAsIfFromHand(GameState gameState, PhysicalCard card) {
        if (card.getZone() != Zone.STACKED)
            return false;

        return (!getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_AS_IF_FROM_HAND, card).isEmpty());
    }

    /**
     * Determines if a card is allowed to be deployed instead of a starfighter using Combat Response.
     * @param gameState the game state
     * @param card the card
     * @return true if allowed, otherwise false
     */
    @Override
    public boolean mayDeployInsteadOfStarfighterUsingCombatResponse(GameState gameState, PhysicalCard card) {
        return (!getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_INSTEAD_OF_STARFIGHTER_USING_COMBAT_RESPONSE, card).isEmpty());
    }

    /**
     * Determines if a card is allowed to be deployed with the specified pilot instead of a matching starfighter using Combat Response.
     * @param gameState the game state
     * @param pilot the pilot
     * @param card the card
     * @return true if allowed, otherwise false
     */
    @Override
    public boolean mayDeployWithInsteadOfMatchingStarfighterUsingCombatResponse(GameState gameState, PhysicalCard pilot, PhysicalCard card) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_WITH_INSTEAD_OF_MATCHING_STARFIGHTER_USING_COMBAT_RESPONSE, pilot)) {
            if (modifier.isAffectedTarget(gameState, this, card)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if a card may be replaced (character converted) by opponent.
     * @param gameState the game state
     * @param card the card
     * @return true if allowed, otherwise false
     */
    @Override
    public boolean mayBeReplacedByOpponent(GameState gameState, PhysicalCard card) {
        return !getModifiersAffectingCard(gameState, ModifierType.MAY_BE_REPLACED_BY_OPPONENT, card).isEmpty()
                && getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_REPLACED_BY_OPPONENT, card).isEmpty();
    }

    @Override
    public PhysicalCard hasExpandedGameTextFromLocation(GameState gameState, Side sideExpandedFrom, PhysicalCard expandedToLocation, Side sideExpandedTo) {
        if (expandedToLocation.getBlueprint().getCardCategory() != CardCategory.LOCATION)
            return null;

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EXPAND_LOCATION_GAME_TEXT, expandedToLocation)) {
            PhysicalCard expandedLocation = modifier.includesGameTextFrom(gameState, this, sideExpandedFrom);
            if (expandedLocation != null) {
                boolean isExpandedLocationRotated = expandedLocation.isInverted();
                boolean isAffectedLocationRotated = expandedToLocation.isInverted();
                if ((isExpandedLocationRotated == isAffectedLocationRotated) == (sideExpandedFrom == sideExpandedTo)) {
                    return expandedLocation;
                }
            }
        }

        return null;
    }

    @Override
    public boolean isRotatedLocation(GameState gameState, PhysicalCard physicalCard) {
        if (physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION)
            return false;

        return (!getModifiersAffectingCard(gameState, ModifierType.ROTATE_LOCATION, physicalCard).isEmpty());
    }

    @Override
    public boolean cannotBeConverted(GameState gameState, PhysicalCard location) {
        return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_CONVERTED, location).isEmpty());
    }

    @Override
    public boolean canBeConvertedByDeployment(GameState gameState, PhysicalCard card, String playerId) {
        return false;
    }

    /**
     * Determines if the specified player can remove cards from opponent hand using the specified card.
     * @param gameState the game state
     * @param actionSource the source card of the action
     * @param playerId the player
     * @return true or false
     */
    @Override
    public boolean mayNotRemoveCardsFromOpponentsHand(GameState gameState, PhysicalCard actionSource, String playerId) {
        for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_NOT_REMOVE_CARDS_FROM_OPPONENTS_HAND)) {
            if (modifier.isForPlayer(playerId)) {
                if (modifier.isActionSource(gameState, this, actionSource)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the specified spy does not 'break cover' during deploy using normal Undercover rules.
     * @param gameState the game state
     * @param card the card
     * @return true or false
     */
    @Override
    public boolean mayNotBreakOwnCoverDuringDeployPhase(GameState gameState, PhysicalCard card) {
        return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BREAK_OWN_COVER_DURING_DEPLOY_PHASE, card).isEmpty());
    }

    /**
     * Determines if the specified card may not attempt Jedi Tests.
     * @param gameState the game state
     * @param card the card
     * @return true or false
     */
    @Override
    public boolean mayNotAttemptJediTests(GameState gameState, PhysicalCard card) {
        return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_ATTEMPT_JEDI_TESTS, card).isEmpty());
    }

    /**
     * Determines if the specified Jedi Test is placed on table when completed.
     * @param gameState the game state
     * @param card the Jedi Test
     * @return true or false
     */
    @Override
    public boolean isJediTestPlacedOnTableWhenCompleted(GameState gameState, PhysicalCard card) {
        return !getModifiersAffectingCard(gameState, ModifierType.PLACE_JEDI_TEST_ON_TABLE_WHEN_COMPLETED, card).isEmpty();
    }

    /**
     * Determines if the specified Jedi Test is suspended instead of lost when target not on table.
     * @param gameState the game state
     * @param card the Jedi Test
     * @return true or false
     */
    @Override
    public boolean isJediTestSuspendedInsteadOfLost(GameState gameState, PhysicalCard card) {
        return !getModifiersAffectingCard(gameState, ModifierType.JEDI_TEST_SUSPENDED_INSTEAD_OF_LOST, card).isEmpty();
    }

    /**
     * Gets the Jedi Test number of the specified Jedi Test.
     * @param gameState the game state
     * @param jediTest the card
     * @return true or false
     */
    @Override
    public int getJediTestNumber(GameState gameState, PhysicalCard jediTest) {
        return jediTest.getBlueprint().getDestiny().intValue();
    }

    @Override
    public boolean cannotJoinSearchParty(GameState gameState, PhysicalCard card) {
        return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_JOIN_SEARCH_PARTY, card).isEmpty());
    }

    @Override
    public boolean isDoubled(GameState gameState, PhysicalCard physicalCard) {
        return isDoubled(gameState, physicalCard, new ModifierCollectorImpl());
    }

    @Override
    public boolean isDoubled(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
        boolean retVal = false;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IS_DOUBLED, physicalCard)) {
            retVal = true;
            modifierCollector.addModifier(modifier);
        }
        return retVal;
    }

    /**
     * Determines if the card deploys and moves like an undercover spy (including if card is an undercover spy)).
     * @param gameState the game state
     * @param card a card
     * @return true if card is an undercover spy or deploys and moves like an undercover spy, otherwise false
     */
    @Override
    public boolean isDeploysAndMovesLikeUndercoverSpy(GameState gameState, PhysicalCard card) {
        if (card.isUndercover())
            return true;

        return (!getModifiersAffectingCard(gameState, ModifierType.DEPLOYS_AND_MOVES_LIKE_UNDERCOVER_SPY, card).isEmpty());
    }

    /**
     * Determines if a card was granted to targeted by the specified card.
     * @param gameState the game state
     * @param cardTargeted the card targeted
     * @param cardTargeting the card doing the targeting
     * @return true if card may be targeted, otherwise false
     */
    @Override
    public boolean grantedMayBeTargetedBy(GameState gameState, PhysicalCard cardTargeted, PhysicalCard cardTargeting) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_BE_TARGETED_BY, cardTargeted)) {
            if (modifier.grantedToBeTargetedByCard(gameState, this, cardTargeting))
                return true;
        }
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_BE_TARGETED_BY_WEAPONS, cardTargeted)) {
            if (modifier.grantedToBeTargetedByCard(gameState, this, cardTargeting))
                return true;
        }
        return false;
    }

    /**
     * Determines if a card is explicitly allowed to be placed on owner's Political Effect.
     * @param gameState the game state
     * @param card the card
     * @return true if card may be placed on Political Effects, otherwise false
     */
    @Override
    public boolean grantedMayBePlaceOnOwnersPoliticalEffect(GameState gameState, PhysicalCard card) {
        return (!getModifiersAffectingCard(gameState, ModifierType.MAY_BE_PLACED_ON_OWNERS_POLITICAL_EFFECT, card).isEmpty());
    }

    /**
     * Gets the number of captives a character may escort.
     * @param gameState the game state
     * @param escort the escort
     * @param skipWarriorCheck true if checking that escort is a warrior, etc. is skipped, otherwise false
     * @return true or false
     */
    @Override
    public int getNumCaptivesAllowedToEscort(GameState gameState, PhysicalCard escort, boolean skipWarriorCheck) {
        if (!escort.getOwner().equals(gameState.getDarkPlayer())
                || escort.getBlueprint().getCardCategory() != CardCategory.CHARACTER) {
            return 0;
        }

        // Check if this type of character is allowed to escort
        if (skipWarriorCheck
                || Filters.bounty_hunter.accepts(gameState, this, escort)
                || Filters.warrior.accepts(gameState, this, escort)
                || Filters.battle_droid.accepts(gameState, this, escort)
                || !getModifiersAffectingCard(gameState, ModifierType.MAY_ESCORT_A_CAPTIVE, escort).isEmpty()) {

            int maxCaptives = 1;
            if (!getModifiersAffectingCard(gameState, ModifierType.MAY_ESCORT_ANY_NUMBER_OF_CAPTIVES, escort).isEmpty()) {
                maxCaptives = Integer.MAX_VALUE;
            }
            return maxCaptives;
        }

        return 0;
    }

    /**
     * Determines if a specified card can escort another specified card as a captive.
     * @param gameState the game state
     * @param escort the escort
     * @param captive the captive
     * @param skipWarriorCheck true if checking that escort is a warrior, etc. is skipped, otherwise false
     * @return true or false
     */
    @Override
    public boolean canEscortCaptive(GameState gameState, PhysicalCard escort, PhysicalCard captive, boolean skipWarriorCheck) {
        // Need to allow if character is already a captive since Jabba's Prize is a Dark Side card
        if ((!captive.isCaptive() && !captive.getOwner().equals(gameState.getLightPlayer()))
                || captive.getBlueprint().getCardCategory() != CardCategory.CHARACTER) {
            return false;
        }

        int maxCaptives = getNumCaptivesAllowedToEscort(gameState, escort, skipWarriorCheck);
        if (maxCaptives == 0) {
            return false;
        }

        Collection<PhysicalCard> captives = gameState.getCaptivesOfEscort(escort);
        if (!captives.contains(captive)) {

            if (captives.size() >= maxCaptives) {
                return false;
            }

            // Check that new escort is allowed to move (if card to be captive is already a captive)
            if (captive.isCaptive() && mayNotMove(gameState, escort)) {
                return false;
            }

            // If character is aboard a vehicle or starship (except vehicle/starship sites), unless captive already aboard that vehicle or starship,
            // check if there is at least one available passenger slot, since the captive takes up a passenger slot
            PhysicalCard attachedTo = escort.getAttachedTo();
            if (attachedTo != null && (escort.isPilotOf() || escort.isPassengerOf())
                    && (attachedTo.getBlueprint().getCardCategory() == CardCategory.STARSHIP || attachedTo.getBlueprint().getCardCategory() == CardCategory.VEHICLE)) {
                if ((!captive.isCaptive() || captive.getAttachedTo() == null || !Filters.or(Filters.piloting(attachedTo), Filters.aboardAsPassenger(attachedTo)).accepts(gameState, this, captive.getAttachedTo()))
                        && gameState.getAvailablePassengerCapacity(this, attachedTo, captive) < 1) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Determines if the card is placed in Used Pile (instead of Lost Pile) when canceled by the specified player and card.
     * @param gameState the game state
     * @param card the card being canceled
     * @param canceledByPlayerId the player canceling the card
     * @param canceledByCard the card canceling the card
     * @return true if card is to be placed in Used Pile, otherwise false
     */
    @Override
    public boolean isPlacedInUsedPileWhenCanceled(GameState gameState, PhysicalCard card, String canceledByPlayerId, PhysicalCard canceledByCard) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PLACE_IN_USED_PILE_WHEN_CANCELED, card)) {
            if (modifier.isPlacedInUsedPileWhenCanceled(gameState, this, canceledByPlayerId, canceledByCard)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isMatchingPair(GameState gameState, PhysicalCard character, PhysicalCard starshipVehicleOrWeapon) {
        if (character.getBlueprint().getCardCategory()==CardCategory.CHARACTER) {
            SwccgCardBlueprint characterBlueprint = character.getBlueprint();

            // Do checking based on if other card is starship, vehicle, or weapon
            if (starshipVehicleOrWeapon.getBlueprint().getCardCategory()==CardCategory.STARSHIP) {
                SwccgCardBlueprint starshipBlueprint = starshipVehicleOrWeapon.getBlueprint();

                // Check if the character has the starship as matching (or vice versa)
                if (characterBlueprint.getMatchingStarshipFilter().accepts(gameState, this, starshipVehicleOrWeapon)
                        || starshipBlueprint.getMatchingPilotFilter().accepts(gameState, this, character)) {

                    // Further check that the character is a valid pilot for the starship
                    if (Filters.pilot.accepts(gameState, this, character)
                            && starshipBlueprint.getValidPilotFilter(starshipVehicleOrWeapon.getOwner(), gameState.getGame(), starshipVehicleOrWeapon, false).accepts(gameState, this, character)) {
                        return true;
                    }
                }
            }
            else if (starshipVehicleOrWeapon.getBlueprint().getCardCategory()==CardCategory.VEHICLE) {
                SwccgCardBlueprint vehicleBlueprint = starshipVehicleOrWeapon.getBlueprint();
                // Creature and transport vehicles are not 'piloted'
                if (vehicleBlueprint.getCardSubtype()==CardSubtype.CREATURE
                        || vehicleBlueprint.getCardSubtype()==CardSubtype.TRANSPORT) {
                    return false;
                }

                // Check if the character has the vehicle as matching (or vice versa)
                if (characterBlueprint.getMatchingVehicleFilter().accepts(gameState, this, starshipVehicleOrWeapon)
                        || vehicleBlueprint.getMatchingPilotFilter().accepts(gameState, this, character)) {

                    // Further check that the character is a valid pilot for the vehicle
                    if (Filters.pilot.accepts(gameState, this, character)
                            && vehicleBlueprint.getValidPilotFilter(starshipVehicleOrWeapon.getOwner(), gameState.getGame(), starshipVehicleOrWeapon, false).accepts(gameState, this, character)) {
                        return true;
                    }
                }
            }
            else if (starshipVehicleOrWeapon.getBlueprint().getCardCategory()==CardCategory.WEAPON) {
                SwccgCardBlueprint weaponBlueprint = starshipVehicleOrWeapon.getBlueprint();
                // Only character weapons can be 'matching'
                if (weaponBlueprint.getCardSubtype()!=CardSubtype.CHARACTER) {
                    return false;
                }

                // Check if the character has the weapon as matching (or vice versa)
                if (characterBlueprint.getMatchingWeaponFilter().accepts(gameState, this, starshipVehicleOrWeapon)
                        || weaponBlueprint.getMatchingCharacterFilter().accepts(gameState, this, character)) {

                    // Further check that the character is a valid user for the weapon
                    if (weaponBlueprint.getValidToUseWeaponFilter(starshipVehicleOrWeapon.getOwner(), gameState.getGame(), starshipVehicleOrWeapon).accepts(gameState, this, character)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean permanentPilotsSuspended(GameState gameState, PhysicalCard physicalCard) {
        return (!getModifiersAffectingCard(gameState, ModifierType.SUSPEND_PERMANENT_PILOT, physicalCard).isEmpty());
    }

    @Override
    public boolean permanentAstromechsSuspended(GameState gameState, PhysicalCard physicalCard) {
        return (!getModifiersAffectingCard(gameState, ModifierType.SUSPEND_PERMANENT_ASTROMECH, physicalCard).isEmpty());
    }

    @Override
    public boolean cannotBeFlipped(GameState gameState, PhysicalCard card) {
        return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_FLIPPED, card).isEmpty());
    }

    @Override
    public boolean notImmediatelyLostIfAsteroidSectorDrawnForAsteroidDestiny(GameState gameState, PhysicalCard card) {
        return (!getModifiersAffectingCard(gameState, ModifierType.NOT_LOST_IF_ASTEROID_SECTOR_DRAWN_FOR_ASTEROID_DESTINY, card).isEmpty());
    }

    @Override
    public boolean cannotTurnOnBinaryDroid(GameState gameState, PhysicalCard card) {
        return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_TURNED_ON, card).isEmpty());
    }

    @Override
    public boolean mayNotBeGrabbed(GameState gameState, PhysicalCard card) {
        return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_GRABBED, card).isEmpty());
    }

    /**
     * Determines if the specified card may not be canceled.
     * @param gameState the game state
     * @param card the card
     * @return true if card may not be canceled, otherwise false
     */
    @Override
    public boolean mayNotBeCanceled(GameState gameState, PhysicalCard card) {
        if (card.getBlueprint().isCardTypeMayNotBeCanceled())
            return false;

        return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_CANCELED, card).isEmpty());
    }

    /**
     * Determines if the specified card may not be placed out of play.
     * @param gameState the game state
     * @param card the card
     * @return true if card may not be placed out of play, otherwise false
     */
    @Override
    public boolean mayNotBePlacedOutOfPlay(GameState gameState, PhysicalCard card) {
        return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_PLACED_OUT_OF_PLAY, card).isEmpty());
    }

    /**
     * Determines if a card may not be targeted by weapons used by the specified card.
     * @param gameState the game state
     * @param cardTargeted the card targeted
     * @param weaponUser the card use
     * @return true if card may be targeted, otherwise false
     */
    @Override
    public boolean mayNotBeTargetedByWeaponUser(GameState gameState, PhysicalCard cardTargeted, PhysicalCard weaponUser) {
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_TARGETED_BY_WEAPON_USER, cardTargeted)) {
            if (modifier.mayNotBeTargetedBy(gameState, this, cardTargeted, weaponUser, null))
                return true;
        }
        return false;
    }

    /**
     * Determines if the specified card may not be removed (unless attached to card is Disarmed).
     * @param gameState the game state
     * @param card the card
     * @return true if card may not be removed, otherwise false
     */
    @Override
    public boolean mayNotRemoveDeviceUnlessDisarmed(GameState gameState, PhysicalCard card) {
        return (!getModifiersAffectingCard(gameState, ModifierType.DEVICE_MAY_NOT_BE_REMOVED_UNLESS_DISARMED, card).isEmpty());
    }

    @Override
    public boolean canBeTargetedBy(GameState gameState, PhysicalCard cardToTarget, PhysicalCard cardDoingTargeting) {
        return canBeTargetedBy(gameState, cardToTarget, cardDoingTargeting, null, Collections.singleton(TargetingReason.OTHER));
    }

    @Override
    public boolean canBeTargetedBy(GameState gameState, PhysicalCard cardToTarget, PhysicalCard cardDoingTargeting, Set<TargetingReason> targetingReasons) {
        return canBeTargetedBy(gameState, cardToTarget, cardDoingTargeting, null, targetingReasons);
    }

    @Override
    public boolean canBeTargetedBy(GameState gameState, PhysicalCard cardToTarget, SwccgBuiltInCardBlueprint permanentWeaponDoingTargeting) {
        return canBeTargetedBy(gameState, cardToTarget, permanentWeaponDoingTargeting.getPhysicalCard(_swccgGame), permanentWeaponDoingTargeting, Collections.singleton(TargetingReason.OTHER));
    }

    @Override
    public boolean canBeTargetedBy(GameState gameState, PhysicalCard cardToTarget, SwccgBuiltInCardBlueprint permanentWeaponDoingTargeting, Set<TargetingReason> targetingReasons) {
        return canBeTargetedBy(gameState, cardToTarget, permanentWeaponDoingTargeting.getPhysicalCard(_swccgGame), permanentWeaponDoingTargeting, targetingReasons);
    }



    private boolean canBeTargetedBy(GameState gameState, PhysicalCard cardToTarget, PhysicalCard cardDoingTargeting, SwccgBuiltInCardBlueprint permanentWeaponDoingTargeting) {
        return canBeTargetedBy(gameState, cardToTarget, cardDoingTargeting, permanentWeaponDoingTargeting, Collections.singleton(TargetingReason.OTHER));
    }


    private boolean canBeTargetedBy(GameState gameState, PhysicalCard cardToTarget, PhysicalCard cardDoingTargeting, SwccgBuiltInCardBlueprint permanentWeaponDoingTargeting, Set<TargetingReason> targetingReasons) {
        if (cardDoingTargeting != null) {
            for (String title : cardDoingTargeting.getTitles()) {
                if (cardToTarget.getBlueprint().isImmuneToCardTitle(title))
                    return false;

                if (cardDoingTargeting.getOwner().equals(cardToTarget.getOwner())
                        && cardToTarget.getBlueprint().isImmuneToOwnersCardTitle(title))
                    return false;

                if (cardToTarget.getBlueprint().isImmuneToOpponentsObjective()
                        && Filters.and(Filters.opponents(cardToTarget), Filters.Objective).accepts(gameState, this, cardDoingTargeting))
                    return false;
            }

            if (permanentWeaponDoingTargeting != null) {
                if (cardToTarget.getBlueprint().isImmuneToCardTitle(permanentWeaponDoingTargeting.getTitle(_swccgGame)))
                    return false;

                if (cardDoingTargeting.getOwner().equals(cardToTarget.getOwner())
                        && cardToTarget.getBlueprint().isImmuneToOwnersCardTitle(permanentWeaponDoingTargeting.getTitle(_swccgGame)))
                    return false;
            }

            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IMMUNE_TO_TITLE, cardToTarget))
                if (modifier.isImmuneToCardModifier(gameState, this, cardDoingTargeting, permanentWeaponDoingTargeting))
                    return false;

            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_TARGETED_BY, cardToTarget))
                if (modifier.mayNotBeTargetedBy(gameState, this, cardToTarget, cardDoingTargeting, permanentWeaponDoingTargeting))
                    return false;
        }

        if (targetingReasons.contains(TargetingReason.TO_BE_CANCELED)
                && mayNotBeCanceled(gameState, cardToTarget))
            return false;

        if (targetingReasons.contains(TargetingReason.TO_BE_DISARMED)
                && !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_DISARMED, cardToTarget).isEmpty())
            return false;

        if (targetingReasons.contains(TargetingReason.TO_BE_CAPTURED)
                && !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_TARGET_TO_BE_CAPTURED, cardToTarget).isEmpty())
            return false;

        if (targetingReasons.contains(TargetingReason.TO_BE_FROZEN)
                && !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_TARGET_TO_BE_FROZEN, cardToTarget).isEmpty())
            return false;

        if (targetingReasons.contains(TargetingReason.TO_BE_PLACED_OUT_OF_PLAY)) {
            if (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_PLACED_OUT_OF_PLAY, cardToTarget).isEmpty()) {
                return false;
            }

            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_TARGET_TO_BE_PLACED_OUT_OF_PLAY_BY, cardToTarget)) {
                if (modifier.isAffectedTarget(gameState, this, cardDoingTargeting)) {
                    return false;
                }
            }
        }

        if (targetingReasons.contains(TargetingReason.TO_BE_TORTURED)
                && !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_TARGET_TO_BE_TORTURED, cardToTarget).isEmpty())
            return false;

        if (targetingReasons.contains(TargetingReason.TO_BE_HIT)) {
            if (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_TARGET_TO_BE_HIT, cardToTarget).isEmpty())
                return false;

            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_HIT_BY, cardToTarget)) {
                if (modifier.isAffectedTarget(gameState, this, cardDoingTargeting))
                    return false;
                if (modifier.isAffectedTarget(gameState, this, permanentWeaponDoingTargeting))
                    return false;
            }
        }

        if (targetingReasons.contains(TargetingReason.TO_BE_STOLEN)
                || targetingReasons.contains(TargetingReason.TO_BE_STOLEN_EVEN_IF_CARDS_ABOARD)
                || targetingReasons.contains(TargetingReason.TO_BE_PURCHASED)) {

            if ((targetingReasons.contains(TargetingReason.TO_BE_STOLEN)
                    || targetingReasons.contains(TargetingReason.TO_BE_STOLEN_EVEN_IF_CARDS_ABOARD))
                    && !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_STOLEN, cardToTarget).isEmpty()) {
                return false;
            }

            if (targetingReasons.contains(TargetingReason.TO_BE_PURCHASED)
                    && !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_PURCHASED, cardToTarget).isEmpty()) {
                return false;
            }

            if (cardToTarget.getBlueprint().getCardCategory() == CardCategory.DEVICE
                    && mayNotRemoveDeviceUnlessDisarmed(gameState, cardToTarget)) {
                return false;
            }

            if (cardDoingTargeting != null) {
                if (cardToTarget.getOwner().equals(cardDoingTargeting.getOwner()))
                    return false;

                if ((targetingReasons.contains(TargetingReason.TO_BE_STOLEN)
                        || targetingReasons.contains(TargetingReason.TO_BE_STOLEN_EVEN_IF_CARDS_ABOARD))
                        && cannotSteal(gameState, cardDoingTargeting))
                    return false;
            }

            if (Filters.in_play.accepts(gameState, this, cardToTarget)) {
                CardCategory cardCategory = cardToTarget.getBlueprint().getCardCategory();
                // An opponent's device, starship, or vehicle may not be stolen if the opponent has characters aboard
                if (cardCategory == CardCategory.STARSHIP || cardCategory == CardCategory.VEHICLE) {
                    if (!targetingReasons.contains(TargetingReason.TO_BE_STOLEN_EVEN_IF_CARDS_ABOARD)) {
                        boolean foundCharacterAboard = Filters.canSpot(gameState.getGame(), null, Filters.and(Filters.attachedToWithRecursiveChecking(cardToTarget), CardCategory.CHARACTER));
                        return !foundCharacterAboard;
                    }
                    return true;
                }
                if (cardCategory == CardCategory.DEVICE) {
                    if (!targetingReasons.contains(TargetingReason.TO_BE_STOLEN_EVEN_IF_CARDS_ABOARD)) {
                        if (Filters.canSpot(gameState.getGame(), null, Filters.and(Filters.attachedToWithRecursiveChecking(cardToTarget), CardCategory.CHARACTER))) {
                            return false;
                        }
                    }
                }

                if (cardDoingTargeting != null) {
                    // Characters may only steal weapons or devices that say they can be
                    // deployed on (or moved by) characters.
                    if (cardCategory == CardCategory.DEVICE || cardCategory == CardCategory.WEAPON) {
                        if (cardDoingTargeting.getBlueprint().getCardCategory() == CardCategory.CHARACTER)
                            return cardToTarget.getBlueprint().canBeDeployedOnCharacter();
                    }
                }
            }
        }

        if (targetingReasons.contains(TargetingReason.TO_BE_CHOKED)
                && !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_CHOKED, cardToTarget).isEmpty())
            return false;

        if (targetingReasons.contains(TargetingReason.TO_BE_LOST) || targetingReasons.contains(TargetingReason.TO_BE_CHOKED)) {
            if (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_TARGET_TO_BE_LOST, cardToTarget).isEmpty()) {
                return false;
            }

            if (cardToTarget.getBlueprint().getCardCategory() == CardCategory.DEVICE
                    && mayNotRemoveDeviceUnlessDisarmed(gameState, cardToTarget)) {
                return false;
            }
        }

        if (targetingReasons.contains(TargetingReason.TO_BE_PLACED_OUT_OF_PLAY)) {
            if (cardToTarget.getZone() == Zone.OUT_OF_PLAY
                    || (cardToTarget.getStackedOn() != null && Filters.grabber.accepts(gameState, this, cardToTarget.getStackedOn()))) {
                return false;
            }

            if (cardToTarget.getBlueprint().getCardCategory() == CardCategory.DEVICE
                    && mayNotRemoveDeviceUnlessDisarmed(gameState, cardToTarget)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public float getVariableValue(GameState gameState, PhysicalCard physicalCard, Variable variable, float baseValue) {
        Float result = baseValue;

        // Apply modifier that sets initial value
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.INITIAL_CALCULATION, physicalCard)) {
            if (modifier.isAffectedVariable(variable)) {
                result = modifier.getValue(gameState, this, physicalCard);
            }
        }

        // Apply multiplication modifiers
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MULTIPLICATION_CALCULATION, physicalCard))
            result *= modifier.getMultiplicationCalculationModifier(gameState, this, physicalCard, variable);

        // Apply addition modifiers
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.ADDITION_CALCULATION, physicalCard))
            result += modifier.getAdditionCalculationModifier(gameState, this, physicalCard, variable);

        // Check if value was reset to an "unmodifiable value", and use lowest found
        Float lowestResetValue = null;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.RESET_CALCULATION, physicalCard)) {
            if (modifier.isAffectedVariable(variable)) {
                float resetValue = modifier.getValue(gameState, this, physicalCard);
                lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, resetValue) : resetValue;
            }
        }
        if (lowestResetValue != null) {
            result = lowestResetValue;
        }

        return Math.max(0, result);
    }

    private void removeModifiers(List<Modifier> modifiers) {
        for (List<Modifier> list : _modifiers.values())
            list.removeAll(modifiers);
    }

    void removeModifier(Modifier modifier) {
        for (List<Modifier> list : _modifiers.values())
            list.remove(modifier);
    }

    @Override
    public ModifierHook addAlwaysOnModifier(Modifier modifier) {
        addModifier(modifier);
        return new ModifierHookImpl(this, modifier);
    }

    private void addModifier(Modifier modifier) {
        ModifierType modifierType = modifier.getModifierType();
        getEffectModifiers(modifierType).add(modifier);
    }

    /**
     * Gets all the cards that are targeting the specified card. This is used for the card info screen on the user interface.
     * @param gameState the game state
     * @param card the card
     * @return the cards targeting the specified card
     */
    @Override
    public List<PhysicalCard> getAllCardsTargetingCard(GameState gameState, PhysicalCard card) {
        List<PhysicalCard> cardsTargetingCard = new LinkedList<PhysicalCard>();

        // Check card being played (if any)
        for (PlayCardState playCardState : gameState.getPlayCardStates()) {
            PlayCardAction action = playCardState.getPlayCardAction();
            if (!cardsTargetingCard.contains(action.getPlayedCard())) {
                if (card.getPermanentCardId() != action.getPlayedCard().getPermanentCardId()
                        && TargetingActionUtils.isTargeting(_swccgGame, action, Filters.samePermanentCardId(card))) {
                    cardsTargetingCard.add(action.getPlayedCard());
                }
            }
            if (action.getOtherPlayedCard() != null && !cardsTargetingCard.contains(action.getOtherPlayedCard())) {
                if (card.getPermanentCardId() != action.getOtherPlayedCard().getPermanentCardId()
                        && TargetingActionUtils.isTargeting(_swccgGame, action, Filters.samePermanentCardId(card))) {
                    cardsTargetingCard.add(action.getOtherPlayedCard());
                }
            }
        }

        cardsTargetingCard.addAll(getCardsOnTableTargetingCard(gameState, card));

        return cardsTargetingCard;
    }

    /**
     * Gets all the cards on table that are targeting the specified card.
     * @param gameState the game state
     * @param card the card
     * @return the cards on table targeting the specified card
     */
    @Override
    public List<PhysicalCard> getCardsOnTableTargetingCard(GameState gameState, PhysicalCard card) {
        List<PhysicalCard> cardsTargetingCard = new LinkedList<PhysicalCard>();

        // Any cards attached to this card are targeting it (except captives)
        for (PhysicalCard attachedCard : card.getCardsAttached()) {
            if (!attachedCard.isCaptive() && !cardsTargetingCard.contains(attachedCard)) {
                cardsTargetingCard.add(attachedCard);
            }
        }

        // Check all cards in play to see if they are explicitly targeting this card
        for (PhysicalCard cardInPlay : Filters.filterAllOnTable(_swccgGame, Filters.any)) {
            if (card.getPermanentCardId() != cardInPlay.getPermanentCardId() && !cardsTargetingCard.contains(cardInPlay)) {
                if (cardInPlay.getTargetedCards(gameState).values().contains(card)) {
                    cardsTargetingCard.add(cardInPlay);
                }
            }
        }

        // Check any modifiers from other cards targeting this card
        for (ModifierType modifierType : ModifierType.values()) {
            List<Modifier> modifiers = _modifiers.get(modifierType);
            if (modifiers != null) {
                for (Modifier modifier : modifiers) {
                    PhysicalCard source = modifier.getSource(gameState);
                    if (source != null && card.getPermanentCardId() != source.getPermanentCardId() && !cardsTargetingCard.contains(source)) {

                        if (modifier.isPersistent() || !(isGameTextCanceled(gameState, source, false, modifier.isEvenIfUnpilotedInPlay()) || source.isSuspended())) {
                            if (modifier.isPersistent() || modifier.getLocationSidePlayer() == null || !isLocationGameTextCanceledForPlayer(gameState, source, modifier.getLocationSidePlayer())) {
                                // For some modifier types, the affects card checking is faster than the condition checking, so for those check targets card first
                                boolean checkTargetsCardFirst = (modifierType == ModifierType.GIVE_ICON || modifierType == ModifierType.CANCEL_FORCE_ICON || modifierType == ModifierType.CANCEL_FORCE_ICONS || modifierType == ModifierType.CANCEL_ICONS || modifierType == ModifierType.EQUALIZE_FORCE_ICONS);
                                if (!checkTargetsCardFirst || modifier.isTargetingCard(gameState, this, card)) {
                                    Condition condition = modifier.getCondition();
                                    Condition additionalCondition = modifier.getAdditionalCondition(gameState, this, card);
                                    if ((condition == null || condition.isFulfilled(gameState, this)) && (additionalCondition == null || additionalCondition.isFulfilled(gameState, this))) {
                                        if (modifier.isPersistent() || source.getZone() == Zone.STACKED || modifier.isWhileInactiveInPlay() == !gameState.isCardInPlayActive(source, false, true, false, false, false, false, false, false)) {
                                            if (modifier.isPersistent() || !modifier.isFromPermanentPilot() || hasPermanentPilot(gameState, source)) {
                                                if (modifier.isPersistent() || !modifier.isFromPermanentAstromech() || hasPermanentAstromech(gameState, source)) {
                                                    if (checkTargetsCardFirst || modifier.isTargetingCard(gameState, this, card)) {
                                                        cardsTargetingCard.add(source);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return cardsTargetingCard;
    }

    /**
     * Gets all the existing persistent modifiers that affect the specified card.
     * This is used to determine which modifiers to exclude the card from when "restoring to normal".
     * @param gameState the game state
     * @param card the card
     * @return the modifiers
     */
    @Override
    public List<Modifier> getPersistentModifiersAffectingCard(GameState gameState, PhysicalCard card) {
        List<Modifier> persistentModifiers = new LinkedList<Modifier>();
        for (List<Modifier> modifiers : _modifiers.values()) {
            for (Modifier modifier : modifiers) {
                if (modifier.isPersistent()
                        && modifier.affectsCard(gameState, this, card)) {
                    persistentModifiers.add(modifier);
                }
            }
        }
        return persistentModifiers;
    }


    private List<Modifier> getModifiers(GameState gameState, ModifierType modifierType) {
        return getKeywordModifiersAffectingCard(gameState, modifierType, null, null);
    }

    private List<Modifier> getModifiersAffectingCard(GameState gameState, ModifierType modifierType, PhysicalCard card) {
        return getKeywordModifiersAffectingCard(gameState, modifierType, null, card);
    }

    private List<Modifier> getKeywordModifiersAffectingCard(GameState gameState, ModifierType modifierType, Keyword keyword, PhysicalCard card) {
        // Get always on modifiers
        List<? extends Modifier> alwaysOnModifiers = null;
        if (card != null) {
            alwaysOnModifiers = card.getBlueprint().getAlwaysOnModifiers(gameState.getGame(), card);
        }
        List<Modifier> modifiers = _modifiers.get(modifierType);
        if (alwaysOnModifiers == null && modifiers == null)
            return Collections.emptyList();
        else {
            LinkedList<Modifier> liveModifiers = new LinkedList<Modifier>();
            if (alwaysOnModifiers!=null) {
                for (Modifier modifier : alwaysOnModifiers) {
                    if (modifierType==modifier.getModifierType()) {
                        if (keyword == null || ((KeywordAffectingModifier) modifier).getKeyword() == keyword) {
                            if (!_skipSet.contains(modifier)) {
                                _skipSet.add(modifier);
                                if (modifier.getSource(gameState) == null || modifier.isPersistent() || !(isGameTextCanceled(gameState, modifier.getSource(gameState)) || modifier.getSource(gameState).isSuspended())) {
                                    Condition condition = modifier.getCondition();
                                    Condition additionalCondition = modifier.getAdditionalCondition(gameState, this, card);
                                    if ((condition == null || condition.isFulfilled(gameState, this)) && (additionalCondition == null || additionalCondition.isFulfilled(gameState, this)))
                                        if (!foundCumulativeConflict(gameState, liveModifiers, modifier))
                                            liveModifiers.add(modifier);
                                }
                                _skipSet.remove(modifier);
                            }
                        }
                    }
                }
            }
            if (modifiers!=null) {
                for (Modifier modifier : modifiers) {
                    if (keyword == null || ((KeywordAffectingModifier) modifier).getKeyword() == keyword) {
                        if (!_skipSet.contains(modifier)) {
                            _skipSet.add(modifier);
                            if (modifier.getSource(gameState) == null || modifier.isPersistent() || !(isGameTextCanceled(gameState, modifier.getSource(gameState), false, modifier.isEvenIfUnpilotedInPlay()) || modifier.getSource(gameState).isSuspended())) {
                                if (modifier.getSource(gameState) == null || modifier.isPersistent() || modifier.getLocationSidePlayer() == null || !isLocationGameTextCanceledForPlayer(gameState, modifier.getSource(gameState), modifier.getLocationSidePlayer())) {
                                    // For some modifier types, the affects card checking is faster than the condition checking, so for those check the affects card first
                                    boolean checkAffectsCardFirst = (modifierType == ModifierType.GIVE_ICON || modifierType == ModifierType.CANCEL_FORCE_ICON || modifierType == ModifierType.CANCEL_FORCE_ICONS || modifierType == ModifierType.CANCEL_ICONS || modifierType == ModifierType.EQUALIZE_FORCE_ICONS);
                                    if (!checkAffectsCardFirst || card == null || modifier.affectsCard(gameState, this, card)) {
                                        Condition condition = modifier.getCondition();
                                        Condition additionalCondition = modifier.getAdditionalCondition(gameState, this, card);
                                        if ((condition == null || condition.isFulfilled(gameState, this)) && (additionalCondition == null || additionalCondition.isFulfilled(gameState, this)))
                                            if (modifier.getSource(gameState) == null || modifier.isPersistent() || modifier.getSource(gameState).getZone() == Zone.STACKED || modifier.isWhileInactiveInPlay() == !gameState.isCardInPlayActive(modifier.getSource(gameState), false, true, false, false, false, false, false, false))
                                                if (modifier.getSource(gameState) == null || modifier.isPersistent() || !modifier.isFromPermanentPilot() || hasPermanentPilot(gameState, modifier.getSource(gameState)))
                                                    if (modifier.getSource(gameState) == null || modifier.isPersistent() || !modifier.isFromPermanentAstromech() || hasPermanentAstromech(gameState, modifier.getSource(gameState)))
                                                        if (checkAffectsCardFirst || card == null || modifier.affectsCard(gameState, this, card))
                                                            if (!foundCumulativeConflict(gameState, liveModifiers, modifier))
                                                                liveModifiers.add(modifier);
                                    }
                                }
                            }
                            _skipSet.remove(modifier);
                        }
                    }
                }
            }
            return liveModifiers;
        }
    }

    private boolean foundCumulativeConflict(GameState gameState, Collection<Modifier> modifierList, Modifier modifier) {
        // If modifier is not cumulative, then check if modifiers from another copy
        // card of same title is already in the list
        if (!modifier.isCumulative() && modifier.getSource(gameState) != null) {

            ModifierType modifierType = modifier.getModifierType();
            String cardTitle = modifier.getSource(gameState).getTitle();
            String forPlayer = modifier.getForPlayer();
            Icon icon = modifier.getIcon();

            for (Modifier liveModifier : modifierList) {
                if (liveModifier.getModifierType() == modifierType
                        && liveModifier.getSource(gameState) != null
                        && (liveModifier.isFromPermanentPilot() == modifier.isFromPermanentPilot()
                                && liveModifier.isFromPermanentAstromech() == modifier.isFromPermanentAstromech())
                        && liveModifier.getSource(gameState).getTitle().equals(cardTitle)
                        && (modifier.getSource(gameState).getBlueprint().getUniqueness() != Uniqueness.UNIQUE
                        || liveModifier.getSource(gameState).getBlueprint().getUniqueness() != Uniqueness.UNIQUE
                        || modifier.getSource(gameState).getCardId() != liveModifier.getSource(gameState).getCardId())
                        && liveModifier.isForPlayer(forPlayer)
                        && liveModifier.getIcon() == icon) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Removes modifiers whose expire condition is met.
     */
    @Override
    public void removeExpiredModifiers() {
        GameState gameState = _swccgGame.getGameState();
        ModifiersQuerying modifiersQuerying = _swccgGame.getModifiersQuerying();

        for (List<Modifier> list : _modifiers.values()) {
            for (Iterator<Modifier> iterator = list.iterator(); iterator.hasNext(); ) {
                Modifier modifier = iterator.next();
                Condition expireCondition = modifier.getExpireCondition();
                if (expireCondition != null
                        && expireCondition.isFulfilled(gameState, modifiersQuerying)) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Removes modifiers that expire when the current turn is finished.
     */
    @Override
    public void removeEndOfTurnModifiers() {
        removeModifiers(_untilEndOfTurnModifiers);
        _untilEndOfTurnModifiers.clear();

        // Move modifiers in "until end of player's next turn" list for the next player
        // to the "until end of turn" list since that player's turn is about to start
        String nextPlayer = _swccgGame.getOpponent(_swccgGame.getGameState().getCurrentPlayerId());
        List<Modifier> nextTurnModifiers = _untilEndOfPlayersNextTurnModifiers.get(nextPlayer);
        if (nextTurnModifiers != null) {
            _untilEndOfTurnModifiers.addAll(nextTurnModifiers);
            _untilEndOfPlayersNextTurnModifiers.remove(nextPlayer);
        }
    }

    /**
     * Removes counters that expire when the current turn is finished.
     */
    @Override
    public void removeEndOfTurnCounters() {
        _cardTitlePlayedTurnLimitCounters.clear();
        _turnLimitCounters.clear();
        _turnForCardTitleLimitCounters.clear();
        _forceActivatedPerPhaseMap.clear();
        _forceActivatedThisTurnMap.clear();
        _forceGenerationActivatedThisTurnMap.clear();
        _forceDrainStartedSet.clear();
        _forceDrainCompletedMap.clear();
        _forceDrainParticipationMap.clear();
        _attemptedJediTestThisTurnMap.clear();
        _asteroidDestinyDrawnAgainstMap.clear();
        _forfeitedFromLocationMap.clear();
        _targetedByWeaponsMap.clear();
        _regularMoveSet.clear();
        _locationAttackOnCreatureSet.clear();
        _attackOnCreatureParticipationSet.clear();
        _attackOnNonCreatureParticipationSet.clear();
        _locationBattleMap.clear();
        _battleParticipationMap.clear();
        _battleInitiatedByPlayerMap.clear();
        _usedDevicesMap.clear();
        _usedWeaponsMap.clear();
        _startOfPhaseLimitCounters.clear();
        _endOfPhaseLimitCounters.clear();
        _bluffCardStacked = false;
        _usedCombatCard.clear();
        _cardPlayedThisTurn.clear();
        _cardWithAbilityDeployedThisTurn.clear();
        _cardPlayedToLocationThisTurn.clear();
    }

    /**
     * Removes modifiers that expire when the captives captivity ends.
     */
    @Override
    public void removeEndOfCaptivity(PhysicalCard captive) {
        _captivityLimitCounters.remove(captive.getCardId());
    }

    /**
     * Removes modifiers that expire when the Podrace ends.
     */
    @Override
    public void removeEndOfPodrace() {
        _raceTotalLimitCounters.clear();
    }

    /**
     * Removes modifiers that expire when the current Force drain is complete.
     */
    @Override
    public void removeEndOfForceDrain() {
        removeModifiers(_untilEndOfForceDrainModifiers);
        _untilEndOfForceDrainModifiers.clear();
        _forceDrainLimitCounters.clear();
    }

    /**
     * Removes modifiers and counters that expire when the current Force loss is complete.
     */
    @Override
    public void removeEndOfForceLoss() {
        Integer forceLossId = _swccgGame.getGameState().getTopForceLossState().getId();
        List<Modifier> list = _untilEndOfForceLossModifiers.get(forceLossId);
        if (list != null) {
            removeModifiers(list);
            list.clear();
        }
        Map<String, LimitCounter> counterMap = _forceLossLimitCounters.get(forceLossId);
        if (counterMap != null) {
            counterMap.clear();
        }
    }

    /**
     * Removes modifiers that expire when the current Force retrieval is complete.
     */
    @Override
    public void removeEndOfForceRetrieval() {
        Integer forceRetrievalId = _swccgGame.getGameState().getTopForceRetrievalState().getId();
        List<Modifier> list = _untilEndOfForceRetrievalModifiers.get(forceRetrievalId);
        if (list != null) {
            removeModifiers(list);
            list.clear();
        }
    }

    /**
     * Removes modifiers that expire when the current draw destiny is complete.
     */
    @Override
    public void removeEndOfDrawDestiny() {
        Integer drawDestinyId = _swccgGame.getGameState().getTopDrawDestinyState().getId();
        List<Modifier> list = _untilEndOfDrawDestinyModifiers.get(drawDestinyId);
        if (list != null) {
            removeModifiers(list);
            list.clear();
        }
    }

    /**
     * Removes modifiers that expire when the current drawn destiny within a draw destiny action is finished.
     */
    @Override
    public void removeEndOfEachDrawnDestiny() {
        Integer drawDestinyId = _swccgGame.getGameState().getTopEachDrawnDestinyState().getId();
        List<Modifier> list = _untilEndOfEachDrawnDestinyModifiers.get(drawDestinyId);
        if (list != null) {
            removeModifiers(list);
            list.clear();
        }
    }

    /**
     * Removes modifiers that expire when the current blow away is complete.
     */
    @Override
    public void removeEndOfBlowAway() {
        Integer blowAwayId = _swccgGame.getGameState().getTopBlowAwayState().getId();
        List<Modifier> list = _untilEndOfBlowAwayModifiers.get(blowAwayId);
        if (list != null) {
            removeModifiers(list);
            list.clear();
        }
    }

    /**
     * Removes modifiers that expire when the specified effect result is complete.
     * @param effectResult the effect result
     */
    @Override
    public void removeEndOfEffectResult(EffectResult effectResult) {
        List<Modifier> list = _untilEndOfEffectResultModifiers.get(effectResult);
        if (list != null) {
            removeModifiers(list);
            list.clear();
        }
    }

    /**
     * Removes modifiers that expire when playing the specified card is complete.
     * @param card the card
     */
    @Override
    public void removeEndOfCardPlayed(PhysicalCard card) {
        List<Modifier> list = _untilEndOfCardPlayedModifiers.get(card);
        if (list != null) {
            removeModifiers(list);
            list.clear();
        }
    }

    /**
     * Removes modifiers that expire when the current game text action is finished.
     */
    @Override
    public void removeEndOfGameTextAction() {
        Integer gameTextActionId = _swccgGame.getGameState().getTopGameTextActionState().getId();
        List<Modifier> list = _untilEndOfGameTextActionModifiers.get(gameTextActionId);
        if (list != null) {
            removeModifiers(list);
            list.clear();
        }
    }

    /**
     * Removes modifiers that expire when the current weapon firing is finished.
     */
    @Override
    public void removeEndOfWeaponFiring() {
        removeModifiers(_untilEndOfWeaponFiringModifiers);
        _untilEndOfWeaponFiringModifiers.clear();
    }

    /**
     * Removes modifiers that expire when the current attack is finished.
     */
    @Override
    public void removeEndOfAttack() {
        removeModifiers(_untilEndOfAttackModifiers);
        _untilEndOfAttackModifiers.clear();
        _firedInAttackMap.clear();
        _firedInAttackCompletedMap.clear();
        _firedInAttackByPlayerMap.clear();
        _firedInAttackByPlayerCompletedMap.clear();
        _permWeaponFiredInAttackByPlayerMap.clear();
        _permWeaponFiredInAttackByPlayerCompletedMap.clear();
    }

    /**
     * Removes modifiers that expire when the damage segment of battle is reached.
     */
    @Override
    public void removeReachedDamageSegmentOfBattle() {
        removeModifiers(_untilDamageSegmentOfBattleModifiers);
        _untilDamageSegmentOfBattleModifiers.clear();
    }

    /**
     * Removes modifiers that expire when the current battle is finished.
     */
    @Override
    public void removeEndOfBattle() {
        removeReachedDamageSegmentOfBattle();
        removeModifiers(_untilEndOfBattleModifiers);
        _untilEndOfBattleModifiers.clear();
        _battleLimitCounters.clear();
        _firedInBattleMap.clear();
        _firedInBattleCompletedMap.clear();
        _firedInBattleByPlayerMap.clear();
        _firedInBattleByPlayerCompletedMap.clear();
        _permWeaponFiredInBattleByPlayerMap.clear();
        _permWeaponFiredInBattleByPlayerCompletedMap.clear();
    }

    /**
     * Removes modifiers that expire when the current Sabacc game is finished.
     */
    @Override
    public void removeEndOfSabacc() {
        removeModifiers(_untilEndOfSabaccModifiers);
        _untilEndOfSabaccModifiers.clear();
    }

    /**
     * Removes modifiers that expire when the current duel is finished.
     */
    @Override
    public void removeEndOfDuel() {
        removeModifiers(_untilEndOfDuelModifiers);
        _untilEndOfDuelModifiers.clear();
        _duelLimitCounters.clear();
    }

    /**
     * Removes modifiers that expire when the current lightsaber combat is finished.
     */
    @Override
    public void removeEndOfLightsaberCombat() {
        removeModifiers(_untilEndOfLightsaberCombatModifiers);
        _untilEndOfLightsaberCombatModifiers.clear();
    }

    /**
     * Removes modifiers that expire when the current epic event action is finished.
     */
    @Override
    public void removeEndOfEpicEvent() {
        removeModifiers(_untilEndOfEpicEventModifiers);
        _untilEndOfEpicEventModifiers.clear();
    }

    /**
     * Adds a modifier that expires when the current turn is finished.
     * @param modifier the modifier
     */
    @Override
    public void addUntilEndOfTurnModifier(Modifier modifier) {
        modifier.setPersistent(true);
        addModifier(modifier);
        _untilEndOfTurnModifiers.add(modifier);
    }

    /**
     * Adds a modifier that expires when the next turn is finished.
     * @param modifier the modifier
     */
    @Override
    public void addUntilEndOfNextTurnModifier(Modifier modifier) {
        addUntilEndOfPlayersNextTurnModifier(modifier, _swccgGame.getOpponent(_swccgGame.getGameState().getCurrentPlayerId()));
    }

    /**
     * Adds a modifier that expires when the specified players next turn is finished.
     * @param modifier the modifier
     */
    @Override
    public void addUntilEndOfPlayersNextTurnModifier(Modifier modifier, String playerId) {
        modifier.setPersistent(true);
        addModifier(modifier);
        List<Modifier> list = _untilEndOfPlayersNextTurnModifiers.get(playerId);
        if (list == null) {
            list = new LinkedList<Modifier>();
            _untilEndOfPlayersNextTurnModifiers.put(playerId, list);
        }
        list.add(modifier);
    }

    /**
     * Adds a modifier that expires when the current Force drain is finished.
     * @param modifier the modifier
     */
    @Override
    public void addUntilEndOfForceDrainModifier(Modifier modifier) {
        if (!_swccgGame.getGameState().isDuringForceDrain()) {
            throw new UnsupportedOperationException("Adding until end of Force drain modifier outside of Force drain. Source: " + (modifier.getSource(_swccgGame.getGameState()) != null ? GameUtils.getFullName(modifier.getSource(_swccgGame.getGameState())) : "null") + " Class: " + modifier.getClass().getSimpleName());
        }
        modifier.setPersistent(true);
        addModifier(modifier);
        _untilEndOfForceDrainModifiers.add(modifier);
    }

    /**
     * Adds a modifier that expires when the current Force loss (not including battle damage) is finished.
     * @param modifier the modifier
     */
    @Override
    public void addUntilEndOfForceLossModifier(Modifier modifier) {
        ForceLossState currentForceLossState = _swccgGame.getGameState().getTopForceLossState();
        if (currentForceLossState == null) {
            throw new UnsupportedOperationException("Adding until end of Force loss modifier outside of Force loss. Source: " + (modifier.getSource(_swccgGame.getGameState()) != null ? GameUtils.getFullName(modifier.getSource(_swccgGame.getGameState())) : "null") + " Class: " + modifier.getClass().getSimpleName());
        }
        modifier.setPersistent(true);
        addModifier(modifier);
        Integer forceLossId = currentForceLossState.getId();
        List<Modifier> list = _untilEndOfForceLossModifiers.get(forceLossId);
        if (list == null) {
            list = new LinkedList<Modifier>();
            _untilEndOfForceLossModifiers.put(forceLossId, list);
        }
        list.add(modifier);
    }

    /**
     * Adds a modifier that expires when the current Force retrieval is finished.
     * @param modifier the modifier
     */
    @Override
    public void addUntilEndOfForceRetrievalModifier(Modifier modifier) {
        ForceRetrievalState currentForceRetrievalState = _swccgGame.getGameState().getTopForceRetrievalState();
        if (currentForceRetrievalState == null) {
            throw new UnsupportedOperationException("Adding until end of Force retrieval modifier outside of Force retrieval. Source: " + (modifier.getSource(_swccgGame.getGameState()) != null ? GameUtils.getFullName(modifier.getSource(_swccgGame.getGameState())) : "null") + " Class: " + modifier.getClass().getSimpleName());
        }
        modifier.setPersistent(true);
        addModifier(modifier);
        Integer forceRetrievalId = currentForceRetrievalState.getId();
        List<Modifier> list = _untilEndOfForceRetrievalModifiers.get(forceRetrievalId);
        if (list == null) {
            list = new LinkedList<Modifier>();
            _untilEndOfForceRetrievalModifiers.put(forceRetrievalId, list);
        }
        list.add(modifier);
    }

    /**
     * Adds a modifier that expires when the current draw destiny is finished.
     * @param modifier the modifier
     */
    @Override
    public void addUntilEndOfDrawDestinyModifier(Modifier modifier) {
        DrawDestinyState currentDrawDestinyState = _swccgGame.getGameState().getTopDrawDestinyState();
        if (currentDrawDestinyState == null) {
            throw new UnsupportedOperationException("Adding until end of draw destiny modifier outside of draw destiny. Source: " + (modifier.getSource(_swccgGame.getGameState()) != null ? GameUtils.getFullName(modifier.getSource(_swccgGame.getGameState())) : "null") + " Class: " + modifier.getClass().getSimpleName());
        }
        modifier.setPersistent(true);
        addModifier(modifier);
        Integer drawDestinyId = currentDrawDestinyState.getId();
        List<Modifier> list = _untilEndOfDrawDestinyModifiers.get(drawDestinyId);
        if (list == null) {
            list = new LinkedList<Modifier>();
            _untilEndOfDrawDestinyModifiers.put(drawDestinyId, list);
        }
        list.add(modifier);
    }

    /**
     * Adds a modifier that expires when the current drawn destiny within a draw destiny action is finished.
     * @param modifier the modifier
     */
    @Override
    public void addUntilEndOfEachDrawnDestinyModifier(Modifier modifier) {
        EachDrawnDestinyState eachDrawnDestinyState = _swccgGame.getGameState().getTopEachDrawnDestinyState();
        if (eachDrawnDestinyState == null) {
            throw new UnsupportedOperationException("Adding until end of each drawn destiny modifier outside of each drawn destiny. Source: " + (modifier.getSource(_swccgGame.getGameState()) != null ? GameUtils.getFullName(modifier.getSource(_swccgGame.getGameState())) : "null") + " Class: " + modifier.getClass().getSimpleName());
        }
        modifier.setPersistent(true);
        addModifier(modifier);
        Integer drawDestinyId = eachDrawnDestinyState.getId();
        List<Modifier> list = _untilEndOfEachDrawnDestinyModifiers.get(drawDestinyId);
        if (list == null) {
            list = new LinkedList<Modifier>();
            _untilEndOfEachDrawnDestinyModifiers.put(drawDestinyId, list);
        }
        list.add(modifier);
    }

    /**
     * Adds a modifier that expires when the current blow away is finished.
     * @param modifier the modifier
     */
    @Override
    public void addUntilEndOfBlowAwayModifier(Modifier modifier) {
        BlowAwayState currentBlowAwayState = _swccgGame.getGameState().getTopBlowAwayState();
        if (currentBlowAwayState == null) {
            throw new UnsupportedOperationException("Adding until end of blow away modifier outside of blow away. Source: " + (modifier.getSource(_swccgGame.getGameState()) != null ? GameUtils.getFullName(modifier.getSource(_swccgGame.getGameState())) : "null") + " Class: " + modifier.getClass().getSimpleName());
        }
        modifier.setPersistent(true);
        addModifier(modifier);
        Integer blowAwayId = currentBlowAwayState.getId();
        List<Modifier> list = _untilEndOfBlowAwayModifiers.get(blowAwayId);
        if (list == null) {
            list = new LinkedList<Modifier>();
            _untilEndOfBlowAwayModifiers.put(blowAwayId, list);
        }
        list.add(modifier);
    }

    /**
     * Adds a modifier that expires when the specified effect result is complete.
     * @param modifier the modifier
     * @param effectResult the effect result
     */
    @Override
    public void addUntilEndOfEffectResultModifier(Modifier modifier, EffectResult effectResult) {
        modifier.setPersistent(true);
        addModifier(modifier);
        List<Modifier> list = _untilEndOfEffectResultModifiers.get(effectResult);
        if (list == null) {
            list = new LinkedList<Modifier>();
            _untilEndOfEffectResultModifiers.put(effectResult, list);
        }
        list.add(modifier);
    }

    /**
     * Adds a modifier that expires when the card is finished being played.
     * @param modifier the modifier
     * @param card the card
     */
    @Override
    public void addUntilEndOfCardPlayedModifier(Modifier modifier, PhysicalCard card) {
        modifier.setPersistent(true);
        addModifier(modifier);
        List<Modifier> list = _untilEndOfCardPlayedModifiers.get(card);
        if (list == null) {
            list = new LinkedList<Modifier>();
            _untilEndOfCardPlayedModifiers.put(card, list);
        }
        list.add(modifier);
    }

    /**
     * Adds a modifier that expires when the specified game text action is complete.
     * @param modifier the modifier
     * @param gameTextAction the game text action
     */
    @Override
    public void addUntilEndOfGameTextActionModifier(Modifier modifier, GameTextAction gameTextAction) {
        GameTextActionState matchingGameTextActionState = _swccgGame.getGameState().getGameTextActionState(gameTextAction);
        if (matchingGameTextActionState == null) {
            throw new UnsupportedOperationException("Adding until end of game text action outside of the specified game text action. Source: " + (modifier.getSource(_swccgGame.getGameState()) != null ? GameUtils.getFullName(modifier.getSource(_swccgGame.getGameState())) : "null") + " Class: " + modifier.getClass().getSimpleName());
        }
        modifier.setPersistent(true);
        addModifier(modifier);
        Integer gameTextActionId = matchingGameTextActionState.getId();
        List<Modifier> list = _untilEndOfGameTextActionModifiers.get(gameTextActionId);
        if (list == null) {
            list = new LinkedList<Modifier>();
            _untilEndOfGameTextActionModifiers.put(gameTextActionId, list);
        }
        list.add(modifier);
    }

    @Override
    public void addUntilEndOfWeaponFiringModifier(Modifier modifier) {
        modifier.setPersistent(true);
        addModifier(modifier);
        _untilEndOfWeaponFiringModifiers.add(modifier);
    }

    /**
     * Adds a modifier that expires when the damage segment of the current battle is reached.
     * @param modifier the modifier
     */
    @Override
    public void addUntilDamageSegmentOfBattleModifier(Modifier modifier) {
        if (!_swccgGame.getGameState().isDuringBattle()) {
            throw new UnsupportedOperationException("Adding until damage segment of battle modifier outside of battle. Source: " + (modifier.getSource(_swccgGame.getGameState()) != null ? GameUtils.getFullName(modifier.getSource(_swccgGame.getGameState())) : "null") + " Class: " + modifier.getClass().getSimpleName());
        }
        modifier.setPersistent(true);
        addModifier(modifier);
        _untilDamageSegmentOfBattleModifiers.add(modifier);
    }


    /**
     * Adds a modifier that expires when the current battle is finished.
     * @param modifier the modifier
     */
    @Override
    public void addUntilEndOfBattleModifier(Modifier modifier) {
        if (!_swccgGame.getGameState().isDuringBattle()) {
            throw new UnsupportedOperationException("Adding until end of battle modifier outside of battle. Source: " + (modifier.getSource(_swccgGame.getGameState()) != null ? GameUtils.getFullName(modifier.getSource(_swccgGame.getGameState())) : "null") + " Class: " + modifier.getClass().getSimpleName());
        }
        modifier.setPersistent(true);
        addModifier(modifier);
        _untilEndOfBattleModifiers.add(modifier);
    }

    /**
     * Adds a modifier that expires when the current attack is finished.
     * @param modifier the modifier
     */
    @Override
    public void addUntilEndOfAttackModifier(Modifier modifier) {
        if (!_swccgGame.getGameState().isDuringAttack()) {
            throw new UnsupportedOperationException("Adding until end of attack modifier outside of attack. Source: " + (modifier.getSource(_swccgGame.getGameState()) != null ? GameUtils.getFullName(modifier.getSource(_swccgGame.getGameState())) : "null") + " Class: " + modifier.getClass().getSimpleName());
        }
        modifier.setPersistent(true);
        addModifier(modifier);
        _untilEndOfAttackModifiers.add(modifier);
    }

    /**
     * Adds a modifier that expires when the current duel is finished.
     * @param modifier the modifier
     */
    @Override
    public void addUntilEndOfDuelModifier(Modifier modifier) {
        if (!_swccgGame.getGameState().isDuringDuel()) {
            throw new UnsupportedOperationException("Adding until end of duel modifier outside of duel. Source: " + (modifier.getSource(_swccgGame.getGameState()) != null ? GameUtils.getFullName(modifier.getSource(_swccgGame.getGameState())) : "null") + " Class: " + modifier.getClass().getSimpleName());
        }
        modifier.setPersistent(true);
        addModifier(modifier);
        _untilEndOfDuelModifiers.add(modifier);
    }

    /**
     * Adds a modifier that expires when the current lightsaber combat is finished.
     * @param modifier the modifier
     */
    @Override
    public void addUntilEndOfLightsaberCombatModifier(Modifier modifier) {
        if (!_swccgGame.getGameState().isDuringLightsaberCombat()) {
            throw new UnsupportedOperationException("Adding until end of lightsaber combat modifier outside of lightsaber combat. Source: " + (modifier.getSource(_swccgGame.getGameState()) != null ? GameUtils.getFullName(modifier.getSource(_swccgGame.getGameState())) : "null") + " Class: " + modifier.getClass().getSimpleName());
        }
        modifier.setPersistent(true);
        addModifier(modifier);
        _untilEndOfLightsaberCombatModifiers.add(modifier);
    }

    /**
     * Adds a modifier that expires when the current epic event action is finished.
     * @param modifier the modifier
     */
    @Override
    public void addUntilEndOfEpicEventModifier(Modifier modifier) {
        if (!_swccgGame.getGameState().isDuringEpicEvent()) {
            throw new UnsupportedOperationException("Adding until end of epic event modifier outside of epic event action. Source: " + (modifier.getSource(_swccgGame.getGameState()) != null ? GameUtils.getFullName(modifier.getSource(_swccgGame.getGameState())) : "null") + " Class: " + modifier.getClass().getSimpleName());
        }
        modifier.setPersistent(true);
        addModifier(modifier);
        _untilEndOfEpicEventModifiers.add(modifier);
    }

    /**
     * Adds a modifier that expires when the current Sabacc game is finished.
     * @param modifier the modifier
     */
    @Override
    public void addUntilEndOfSabaccModifier(Modifier modifier) {
        if (!_swccgGame.getGameState().isDuringSabacc()) {
            throw new UnsupportedOperationException("Adding until end of sabacc modifier outside of sabacc game. Source: " + (modifier.getSource(_swccgGame.getGameState()) != null ? GameUtils.getFullName(modifier.getSource(_swccgGame.getGameState())) : "null") + " Class: " + modifier.getClass().getSimpleName());
        }
        modifier.setPersistent(true);
        addModifier(modifier);
        _untilEndOfSabaccModifiers.add(modifier);
    }

    /**
     * Adds a modifier that does not expire during the game.
     * @param modifier the modifier
     */
    @Override
    public void addUntilEndOfGameModifier(Modifier modifier) {
        modifier.setPersistent(true);
        modifier.setNotRemovedOnRestoreToNormal(true);
        addModifier(modifier);
    }

    @Override
    public Collection<Modifier> getModifiersAffecting(GameState gameState, PhysicalCard card) {

        Set<Modifier> result = new HashSet<Modifier>();
        for (List<Modifier> modifiers : _modifiers.values()) {
            for (Modifier modifier : modifiers) {
                Condition condition = modifier.getCondition();
                Condition additionalCondition = modifier.getAdditionalCondition(gameState, this, card);
                if ((condition == null || condition.isFulfilled(gameState, this)) && (additionalCondition == null || additionalCondition.isFulfilled(gameState, this)))
                    if (modifier.getSource(gameState) == null || modifier.isPersistent() || !isGameTextCanceled(gameState, modifier.getSource(gameState), false, modifier.isEvenIfUnpilotedInPlay()))
                        if (modifier.getSource(gameState) == null || modifier.isPersistent() || modifier.getLocationSidePlayer() == null || !isLocationGameTextCanceledForPlayer(gameState, modifier.getSource(gameState), modifier.getLocationSidePlayer()))
                            if (modifier.getSource(gameState) == null || modifier.isPersistent() || !modifier.isFromPermanentPilot() || hasPermanentPilot(gameState, modifier.getSource(gameState)))
                                if (modifier.getSource(gameState) == null || modifier.isPersistent() || !modifier.isFromPermanentAstromech() || hasPermanentAstromech(gameState, modifier.getSource(gameState)))
                                    if (modifier.getSource(gameState) == null || modifier.isPersistent() || modifier.isWhileInactiveInPlay() == !gameState.isCardInPlayActive(modifier.getSource(gameState), false, true, false, false, false, false, false, false))
                                        if (affectsCardWithSkipSet(gameState, card, modifier))
                                            if (!foundCumulativeConflict(gameState, result, modifier))
                                                result.add(modifier);
            }
        }

        List<Modifier> alwaysOnModifiers = card.getBlueprint().getAlwaysOnModifiers(gameState.getGame(), card);
        if (alwaysOnModifiers != null) {
            for (Modifier modifier : alwaysOnModifiers) {
                Condition condition = modifier.getCondition();
                Condition additionalCondition = modifier.getAdditionalCondition(gameState, this, card);
                if ((condition == null || condition.isFulfilled(gameState, this)) && (additionalCondition == null || additionalCondition.isFulfilled(gameState, this)))
                    if (affectsCardWithSkipSet(gameState, card, modifier))
                        if (!foundCumulativeConflict(gameState, result, modifier))
                            result.add(modifier);

            }
        }

        return result;
    }

    private boolean affectsCardWithSkipSet(GameState gameState, PhysicalCard physicalCard, Modifier modifier) {
        if (!_skipSet.contains(modifier) && physicalCard != null) {
            _skipSet.add(modifier);
            boolean result = modifier.affectsCard(gameState, this, physicalCard);
            _skipSet.remove(modifier);
            return result;
        } else {
            return false;
        }
    }

    /**
     * Determines if the two cards have the same card title. For combo cards, each title is checked.
     *
     * @param gameState the game state
     * @param card1 a card
     * @param card2 a card
     * @return true if cards have same card title, otherwise false
     */
    @Override
    public boolean cardTitlesMatch(GameState gameState, PhysicalCard card1, PhysicalCard card2) {
        // Check each title
        for (String cardTitle : card1.getTitles()) {
            if (Filters.title(cardTitle).accepts(gameState, this, card2)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if the specified interrupt plays for free.
     * @param gameState the game state
     * @param card the card
     * @return true if interrupt plays for free, otherwise false
     */
    @Override
    public boolean isInterruptPlayForFree(GameState gameState, PhysicalCard card) {
        return !getModifiersAffectingCard(gameState, ModifierType.INTERRUPT_PLAYS_FOR_FREE, card).isEmpty();
    }

    /**
     * Determines if a card is explicitly granted the ability to deploy for free when deployed. The target the card is
     * being deployed to may be specified.
     * @param gameState the game state
     * @param card a card
     * @param targetCard the target, or null if not deploying to specific target
     * @return true if card is granted the ability to deploy for free to target, otherwise false
     */
    @Override
    public boolean grantedDeployForFree(GameState gameState, PhysicalCard card, PhysicalCard targetCard) {
        return grantedDeployForFree(gameState, card, targetCard, new ModifierCollectorImpl());
    }

    /**
     * Determines if a card is explicitly granted the ability to deploy for free when deployed. The target the card is
     * being deployed to may be specified.
     * @param gameState the game state
     * @param card a card
     * @param targetCard the target, or null if not deploying to specific target
     * @param modifierCollector collector of affecting modifiers
     * @return true if card is granted the ability to deploy for free to target, otherwise false
     */
    @Override
    public boolean grantedDeployForFree(GameState gameState, PhysicalCard card, PhysicalCard targetCard, ModifierCollector modifierCollector) {
        boolean isAlwaysFree = false;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DEPLOYS_FREE, card)) {
            isAlwaysFree = true;
            modifierCollector.addModifier(modifier);
        }
        if (isAlwaysFree) {
            return true;
        }

        // Check if card deploys for free to specified target
        if (targetCard != null) {
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DEPLOYS_FREE_TO_TARGET, card)) {
                if (modifier.isDeployFreeToTarget(gameState, this, targetCard)) {
                    return true;
                }
                // Check if self deployment modifier is applied at any location
                if (modifier.getSource(gameState) != null && modifier.getSource(gameState).getCardId() == card.getCardId()
                        && appliesOwnDeploymentModifiersAtAnyLocation(gameState, card)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Determines if a card deploys for free.
     *
     * @param gameState the game state
     * @param card a card
     * @return true if card deploys for free, otherwise false
     */
    @Override
    public boolean deploysForFree(GameState gameState, PhysicalCard card) {
        if (!hasDeployCostAttribute(card))
            return false;

        if (grantedDeployForFree(gameState, card, null))
            return true;

        if (card.getBlueprint().getSpecialDeployCostEffect(null, card.getOwner(), gameState.getGame(), card, null, null) != null)
            return false;

        return getDeployCost(gameState, card, card, null, true, false, null, false, 0, null, null, false, new ModifierCollectorImpl()) == 0;
    }

    /**
     * Determines if a card's deploy cost is less than or equal to a specified cost.
     *
     * @param gameState the game state
     * @param card a card
     * @param value the cost
     * @return true if card's deploy cost is less than or equal to the specified cost, otherwise false
     */
    @Override
    public boolean hasDeployCostLessThanOrEqualTo(GameState gameState, PhysicalCard card, float value) {
        if (!hasDeployCostAttribute(card))
            return false;

        return getDeployCost(gameState, card) <= value;
    }

    /**
     * Determines if the specified card is explicitly granted the ability to be deployed during the current phase.
     * @param gameState the game state
     * @param card a card
     * @return true if card is granted the ability to deploy during the current phase, otherwise false
     */
    @Override
    public boolean grantedDeployDuringCurrentPhase(GameState gameState, PhysicalCard card) {
        return (!getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_DURING_CURRENT_PHASE, card).isEmpty());
    }

    /**
     * Determines if a card's forfeit value is equal to a specified value.
     *
     * @param gameState the game state
     * @param card      a card
     * @param value     the forfeit value
     * @return true if card's forfeit value is equal to the specified value, otherwise false
     */
    @Override
    public boolean hasForfeitValueEqualTo(GameState gameState, PhysicalCard card, float value) {
        if (!hasForfeitValueAttribute(card))
            return false;

        return getForfeit(gameState, card) == value;
    }

    /**
     * Determines if a card's forfeit value is more than a specified value.
     *
     * @param gameState the game state
     * @param card a card
     * @param value the forfeit value
     * @return true if card's forfeit value is more than the specified value, otherwise false
     */
    @Override
    public boolean hasForfeitValueMoreThan(GameState gameState, PhysicalCard card, float value) {
        if (!hasForfeitValueAttribute(card))
            return false;

        return getForfeit(gameState, card) > value;
    }

    /**
     * Determines if the card is deployable.
     * @param gameState the game state
     * @param sourceCard the card to initiate the deployment
     * @param cardToDeploy the card
     * @param includePlayable true if includes playable cards, false if only deployable cards
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forFree true if playing the card for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param changeInCostCardFilter the card filter for cards that are affected by the change in cost, or null for all
     * @param deploymentOption specifies special deployment options, or null
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @param cardToDeployWith the card to deploy with simultaneously
     * @param cardToDeployWithForFree true if the card to deploy with deploys for free, otherwise false
     * @param cardToDeployWithChangeInCost change in amount of Force (can be positive or negative) required for the card to deploy with
     * @return true if card can be played or deployed, otherwise false
     */
    @Override
    public boolean isDeployable(GameState gameState, PhysicalCard sourceCard, PhysicalCard cardToDeploy, boolean includePlayable, Filter specialLocationConditions, boolean forFree, float changeInCost, Filter changeInCostCardFilter, DeploymentOption deploymentOption, DeploymentRestrictionsOption deploymentRestrictionsOption, ReactActionOption reactActionOption, PhysicalCard cardToDeployWith, boolean cardToDeployWithForFree, float cardToDeployWithChangeInCost) {
        if (!includePlayable && !cardToDeploy.getBlueprint().isCardTypeDeployed()) {
            return false;
        }

        float changeInCostToUse = (changeInCost != 0 && (changeInCostCardFilter == null || changeInCostCardFilter.accepts(gameState, this, cardToDeploy))) ? changeInCost : 0;

        if (cardToDeploy.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
            return cardToDeploy.getBlueprint().getPlayCardAction(cardToDeploy.getOwner(), gameState.getGame(), cardToDeploy, sourceCard, forFree, changeInCostToUse, deploymentOption, deploymentRestrictionsOption, null, reactActionOption, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost, Filters.any, specialLocationConditions) != null;
        }
        else {
            return !cardToDeploy.getBlueprint().getPlayCardActions(cardToDeploy.getOwner(), gameState.getGame(), cardToDeploy, sourceCard, forFree, changeInCostToUse, deploymentOption, deploymentRestrictionsOption, null, reactActionOption, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost, Filters.any, null).isEmpty();
        }
    }

    /**
     * Determines if the card is deployable to target.
     * @param gameState the game state
     * @param sourceCard the card to initiate the deployment
     * @param cardToDeploy the card to deploy
     * @param includePlayable true if includes playable cards, false if only deployable cards
     * @param targetFilter the target filter
     * @param forFree true if playing the card for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param changeInCostCardFilter the card filter for cards that are affected by the change in cost, or null for all
     * @param deploymentOption specifies special deployment options, or null
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param deployAsCaptiveOption specifies the way to deploy the card as a captive, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @param cardToDeployWith the card to deploy with simultaneously
     * @param cardToDeployWithForFree true if the card to deploy with deploys for free, otherwise false
     * @param cardToDeployWithChangeInCost change in amount of Force (can be positive or negative) required for the card to deploy with
     * @return true if card can be played or deployed, otherwise false
     */
    @Override
    public boolean isDeployableToTarget(GameState gameState, PhysicalCard sourceCard, PhysicalCard cardToDeploy, boolean includePlayable, Filter targetFilter, boolean forFree, float changeInCost, Filter changeInCostCardFilter, DeploymentOption deploymentOption, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption, ReactActionOption reactActionOption, PhysicalCard cardToDeployWith, boolean cardToDeployWithForFree, float cardToDeployWithChangeInCost) {
        if (!includePlayable && !cardToDeploy.getBlueprint().isCardTypeDeployed()) {
            return false;
        }

        float changeInCostToUse = (changeInCost != 0 && (changeInCostCardFilter == null || changeInCostCardFilter.accepts(gameState, this, cardToDeploy))) ? changeInCost : 0;

        return !cardToDeploy.getBlueprint().getPlayCardActions(cardToDeploy.getOwner(), gameState.getGame(), cardToDeploy, sourceCard, forFree, changeInCostToUse, deploymentOption, deploymentRestrictionsOption, deployAsCaptiveOption, reactActionOption, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost, targetFilter, null).isEmpty();
    }

    /**
     * Determines if the card is deployable to the system.
     * @param gameState the game state
     * @param sourceCard the card to initiate the deployment
     * @param cardToDeploy the card to deploy
     * @param includePlayable true if includes playable cards, false if only deployable cards
     * @param systemName the system name
     * @param targetFilter the target filter
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forFree true if playing the card for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param changeInCostCardFilter the card filter for cards that are affected by the change in cost, or null for all
     * @param deploymentOption specifies special deployment options, or null
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @param cardToDeployWith the card to deploy with simultaneously
     * @param cardToDeployWithForFree true if the card to deploy with deploys for free, otherwise false
     * @param cardToDeployWithChangeInCost change in amount of Force (can be positive or negative) required for the card to deploy with
     * @return true if card can be played or deployed, otherwise false
     */
    @Override
    public boolean isDeployableToSystem(GameState gameState, PhysicalCard sourceCard, PhysicalCard cardToDeploy, boolean includePlayable, String systemName, Filter targetFilter, Filter specialLocationConditions, boolean forFree, float changeInCost, Filter changeInCostCardFilter, DeploymentOption deploymentOption, DeploymentRestrictionsOption deploymentRestrictionsOption, ReactActionOption reactActionOption, PhysicalCard cardToDeployWith, boolean cardToDeployWithForFree, float cardToDeployWithChangeInCost) {
        if (!includePlayable && !cardToDeploy.getBlueprint().isCardTypeDeployed()) {
            return false;
        }

        if (cardToDeploy.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
            return cardToDeploy.getBlueprint().getPlayLocationToSystemAction(cardToDeploy.getOwner(), gameState.getGame(), cardToDeploy, sourceCard, systemName, specialLocationConditions) != null;
        }
        else {
            return isDeployableToTarget(gameState, sourceCard, cardToDeploy, includePlayable, Filters.and(Filters.locationAndCardsAtLocation(Filters.partOfSystem(systemName)), targetFilter), forFree, changeInCost, changeInCostCardFilter, deploymentOption, deploymentRestrictionsOption, null, reactActionOption, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost);
        }
    }

    @Override
    public boolean canBeTargetedByWeaponsAsIfPresent(GameState gameState, PhysicalCard card) {
        return (!getModifiersAffectingCard(gameState, ModifierType.MAY_BE_TARGETED_BY_WEAPONS_AS_IF_PRESENT, card).isEmpty());
    }

    @Override
    public boolean canBeTargetedByWeaponsAsStarfighter(GameState gameState, PhysicalCard card) {
        return (!getModifiersAffectingCard(gameState, ModifierType.TARGETED_BY_WEAPONS_LIKE_A_STARFIGHTER, card).isEmpty());
    }

    /**
     * Determines if the card has a deploy cost attribute.
     *
     * @param card a card
     * @return true if card has a deploy cost attribute, otherwise false
     */
    private boolean hasDeployCostAttribute(PhysicalCard card) {

        // TODO: Add a way to check if this card has a deploy cost attribute

        if (card.getBlueprint().getCardCategory() == CardCategory.INTERRUPT)
            return false;

        return true;
    }

    /**
     * Determines if the card has a forfeit value attribute.
     *
     * @param card a card
     * @return true if card has a forfeit value attribute, otherwise false
     */
    private boolean hasForfeitValueAttribute(PhysicalCard card) {

        // TODO: Add a way to check if this card has a forfeit value attribute

        if (card.getBlueprint().getCardCategory() == CardCategory.INTERRUPT)
            return false;

        return true;
    }

    /**
     * Determines if the card has a power attribute.
     *
     * @param card a card
     * @return true if card has a power attribute, otherwise false
     */
    private boolean hasPowerAttribute(PhysicalCard card) {

        // TODO: Add a way to check if this card has a power attribute

        if (card.getBlueprint().getCardCategory() == CardCategory.INTERRUPT)
            return false;

        return true;
    }

    /**
     * Determines if the card has a politics attribute.
     *
     * @param card a card
     * @return true if card has a politics attribute, otherwise false
     */
    private boolean hasPoliticsAttribute(PhysicalCard card) {
        return (card.getBlueprint().getCardCategory() == CardCategory.CHARACTER);
    }

    /**
     * Determines if this deploys and moves like a starfighter.
     * @param gameState the game state
     * @param card the card
     * @return true if deploys and moves like a starfighter, otherwise false
     */
    @Override
    public boolean isDeploysAndMovesLikeStarfighter(GameState gameState, PhysicalCard card) {
        return card.getBlueprint().isDeploysAndMovesLikeStarfighter() || Filters.squadron.accepts(gameState, this, card);
    }

    /**
     * Determines if this deploys and moves like a starfighter at cloud sectors.
     * @param gameState the game state
     * @param card the card
     * @return true if deploys and moves like a starfighter at cloud sectors, otherwise false
     */
    @Override
    public boolean isDeploysAndMovesLikeStarfighterAtCloudSectors(GameState gameState, PhysicalCard card) {
        if (isDeploysAndMovesLikeStarfighter(gameState, card))
            return true;

        if (Filters.or(Filters.shuttle_vehicle, Filters.cloud_car, Filters.Patrol_Craft).accepts(gameState, this, card))
            return true;

        return card.getBlueprint().isDeploysAndMovesLikeStarfighterAtCloudSectors();
    }

    /**
     * Gets the sites marker number.
     * @param gameState the game state
     * @param physicalCard a marker site
     * @return the marker number, or null if not a marker site
     */
    @Override
    public Integer getMarkerNumber(GameState gameState, PhysicalCard physicalCard) {
        if (physicalCard.getBlueprint().hasKeyword(Keyword.MARKER_1)) {
            return 1;
        }
        if (physicalCard.getBlueprint().hasKeyword(Keyword.MARKER_2)) {
            return 2;
        }
        if (physicalCard.getBlueprint().hasKeyword(Keyword.MARKER_3)) {
            return 3;
        }
        if (physicalCard.getBlueprint().hasKeyword(Keyword.MARKER_4)) {
            return 4;
        }
        if (physicalCard.getBlueprint().hasKeyword(Keyword.MARKER_5)) {
            return 5;
        }
        if (physicalCard.getBlueprint().hasKeyword(Keyword.MARKER_6)) {
            return 6;
        }
        if (physicalCard.getBlueprint().hasKeyword(Keyword.MARKER_7)) {
            return 7;
        }
        return null;
    }

    @Override
    public boolean isVehicleSlotOfStarshipCompatible(GameState gameState, PhysicalCard card) {
        return card.getBlueprint().isVehicleSlotOfStarshipCompatible();
    }

    @Override
    public PhysicalCard getIsPilotOf(GameState gameState, PhysicalCard card) {
        PhysicalCard attachedTo = card.getAttachedTo();
        if (attachedTo != null
                && card.isPilotOf()
                && Filters.not(Filters.transport_vehicle).accepts(gameState, this, attachedTo)
                && !cannotDriveOrPilot(gameState, card)) {
            return attachedTo;
        }
        return null;
    }

    @Override
    public PhysicalCard getIsDriverOf(GameState gameState, PhysicalCard card) {
        PhysicalCard attachedTo = card.getAttachedTo();
        if (attachedTo != null
                && card.isPilotOf()
                && Filters.transport_vehicle.accepts(gameState, this, attachedTo)
                && !cannotDriveOrPilot(gameState, card)) {
            return attachedTo;
        }
        return null;
    }

    @Override
    public PhysicalCard getLocationHere(GameState gameState, PhysicalCard card) {
        if (card == null)
            return null;

        if (card.getBlueprint().getCardCategory() == CardCategory.LOCATION)
            return card;

        return getLocationThatCardIsAt(gameState, card);
    }

    @Override
    public Collection<PhysicalCard> getLocationsHere(GameState gameState, Collection<PhysicalCard> cards) {
        Collection<PhysicalCard> locations = new HashSet<PhysicalCard>();

        for (PhysicalCard card : cards) {
            if (card.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
                locations.add(card);
            }
            else {
                PhysicalCard atLocation = getLocationThatCardIsAt(gameState, card);
                if (atLocation != null)
                    locations.add(atLocation);
            }
        }

        return locations;
    }

}
