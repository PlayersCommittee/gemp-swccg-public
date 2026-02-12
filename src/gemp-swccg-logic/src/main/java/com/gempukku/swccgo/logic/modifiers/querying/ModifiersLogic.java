package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BlowAwayState;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.EachDrawnDestinyState;
import com.gempukku.swccgo.game.state.ForceLossState;
import com.gempukku.swccgo.game.state.ForceRetrievalState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.actions.GameTextActionState;
import com.gempukku.swccgo.game.state.actions.PlayCardState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.GameTextAction;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.SnapshotData;
import com.gempukku.swccgo.logic.timing.Snapshotable;
import com.gempukku.swccgo.logic.timing.TargetingActionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class manages all modifiers in the game by implementing the adding/removing functions of ModifiersEnvironment
 * and the modifier inspection functions of ModifiersQuerying.
 * To prevent this being a 17,170 line monstrosity, all the actual bodies of those implementations are, wherever
 * possible, shunted to "default" functions inside those interfaces themselves.  The only functions that remain here
 * are those which require access to the private lists and maps, in a way that would be troublesome to expose.
 */
public class ModifiersLogic implements ModifiersEnvironment, ModifiersState, ModifiersQuerying, Snapshotable<ModifiersLogic> {

    /**
     * @return Returns the concrete implementation of the ModifiersQuerying.  This is used within the myriad interfaces
     * that make up ModifiersQuerying since "this" is inaccessible.
     */
    public ModifiersQuerying query() { return this; }

    /**
     * @return Returns the top-level game table instance associated with these modifiers.  Used within the myriad
     * interfaces that make up ModifiersQuerying.
     */
    public SwccgGame game() { return _swccgGame; }
    private SwccgGame _swccgGame;
    private Map<ModifierType, List<Modifier>> _modifiers = new HashMap<ModifierType, List<Modifier>>();
    private Map<Integer, List<Modifier>> _alwaysOnModifiersMap = new HashMap<>();
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
    private List<Modifier> _untilEndOfTractorBeamModifiers = new LinkedList<Modifier>();
    private List<Modifier> _untilDamageSegmentOfBattleModifiers = new LinkedList<Modifier>();
    private List<Modifier> _untilEndOfBattleModifiers = new LinkedList<Modifier>();
    private List<Modifier> _untilEndOfAttackModifiers = new LinkedList<Modifier>();
    private List<Modifier> _untilEndOfDuelModifiers = new LinkedList<Modifier>();
    private List<Modifier> _untilEndOfLightsaberCombatModifiers = new LinkedList<Modifier>();
    private List<Modifier> _untilEndOfEpicEventModifiers = new LinkedList<Modifier>();
    private List<Modifier> _untilEndOfSabaccModifiers = new LinkedList<Modifier>();
    private Map<String, List<Modifier>> _untilEndOfPlayersNextTurnModifiers = new HashMap<String, List<Modifier>>();
    private List<Modifier> _untilStartOfTurnModifiers = new LinkedList<Modifier>();
    private Map<String, List<Modifier>> _untilStartOfPlayersNextTurnModifiers = new HashMap<String, List<Modifier>>();

    private Set<Modifier> _skipSet = new HashSet<Modifier>();

    private Map<Phase, Map<String, LimitCounter>> _endOfPhaseLimitCounters = new HashMap<Phase, Map<String, LimitCounter>>();
    private Map<Phase, Map<String, LimitCounter>> _startOfPhaseLimitCounters = new HashMap<Phase, Map<String, LimitCounter>>();
    private Map<String, LimitCounter> _forceDrainLimitCounters = new HashMap<String, LimitCounter>();
    private Map<String, LimitCounter> _gameLimitCounters = new HashMap<String, LimitCounter>();
    private Map<String, LimitCounter> _turnLimitCounters = new HashMap<String, LimitCounter>();
    private Map<String, LimitCounter> _turnForCardTitleLimitCounters = new HashMap<String, LimitCounter>();
    private Map<String, LimitCounter> _battleLimitCounters = new HashMap<String, LimitCounter>();
    private Map<String, LimitCounter> _attackLimitCounters = new HashMap<String, LimitCounter>();
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
    private boolean _deathStarPowerShutDown;
    private boolean _senateIsInSession;
    private Set<String> _usedCombatCard = new HashSet<String>();
    private Map<Integer, List<PhysicalCard>> _targetedByWeaponsMap = new HashMap<Integer, List<PhysicalCard>>();
    private Map<Integer, List<SwccgBuiltInCardBlueprint>> _targetedByPermanentWeaponsMap = new HashMap<Integer, List<SwccgBuiltInCardBlueprint>>();
    private Map<Integer, List<PhysicalCard>> _hitOrMadeLostByWeaponMap = new HashMap<>();
    private Map<Integer, PhysicalCard> _attemptedJediTestThisTurnMap = new HashMap<Integer, PhysicalCard>();

    private Set<PhysicalCard> _blownAwayCards = new HashSet<PhysicalCard>();
    private Set<PhysicalCard> _cardsThatWonSabacc = new HashSet<PhysicalCard>();
    private Set<Persona> _personasCrossedOver = new HashSet<Persona>();
    private Map<String, List<PhysicalCard>> _completedUtinniEffect = new HashMap<String, List<PhysicalCard>>();
    private Map<Integer, PhysicalCard> _completedJediTest = new HashMap<Integer, PhysicalCard>();
    private Map<String, String> _extraInformationForArchetypeLabel = new HashMap<>();

    /**
     * Needed to generate snapshot, otherwise unused.
     */
    public ModifiersLogic() {
    }

    /**
     * Creates a modifier manager for a game.
     * @param swccgGame The game table that will use this manager.
     */
    public ModifiersLogic(SwccgGame swccgGame) {
        _swccgGame = swccgGame;
    }

    public void generateSnapshot(ModifiersLogic selfSnapshot, SnapshotData snapshotData) {
        ModifiersLogic snapshot = selfSnapshot;

        // Set each field
        snapshot._swccgGame = _swccgGame;
        for (ModifierType modifierType : _modifiers.keySet()) {
            List<Modifier> snapshotList = new LinkedList<Modifier>(_modifiers.get(modifierType));
            snapshot._modifiers.put(modifierType, snapshotList);
        }
        for (Integer permanentCardId : _alwaysOnModifiersMap.keySet()) {
            List<Modifier> snapshotList = new LinkedList<>(_alwaysOnModifiersMap.get(permanentCardId));
            snapshot._alwaysOnModifiersMap.put(permanentCardId, snapshotList);
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
        snapshot._untilEndOfTractorBeamModifiers.addAll(_untilEndOfTractorBeamModifiers);
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
        for (String playerId : _untilStartOfPlayersNextTurnModifiers.keySet()) {
            List<Modifier> snapshotList = new LinkedList<Modifier>(_untilStartOfPlayersNextTurnModifiers.get(playerId));
            snapshot._untilStartOfPlayersNextTurnModifiers.put(playerId, snapshotList);
        }
        snapshot._untilStartOfTurnModifiers.addAll(_untilStartOfTurnModifiers);
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
        for (Map.Entry<String, LimitCounter> entry : _attackLimitCounters.entrySet()) {
            snapshot._attackLimitCounters.put(entry.getKey(), snapshotData.getDataForSnapshot(entry.getValue()));
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
        snapshot._deathStarPowerShutDown = _deathStarPowerShutDown;
        snapshot._senateIsInSession = _senateIsInSession;

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
        for (Integer cardId : _hitOrMadeLostByWeaponMap.keySet()) {
            List<PhysicalCard> snapshotList = new LinkedList<PhysicalCard>();
            snapshot._hitOrMadeLostByWeaponMap.put(cardId, snapshotList);
            for (PhysicalCard card : _hitOrMadeLostByWeaponMap.get(cardId)) {
                snapshotList.add(snapshotData.getDataForSnapshot(card));
            }
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
        for (String playerId : _extraInformationForArchetypeLabel.keySet()) {
            snapshot._extraInformationForArchetypeLabel.put(playerId, _extraInformationForArchetypeLabel.get(playerId));
        }
    }


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

    public LimitCounter getUntilEndOfAttackLimitCounter(PhysicalCard card, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        String key = card.getCardId()+"|"+playerId+"|"+gameTextSourceCardId+"|"+ gameTextActionId;
        if (card.getBlueprint().getCardCategory() == CardCategory.INTERRUPT) {
            key = "|"+playerId+"|" + "|"+ gameTextActionId;
        }
        LimitCounter limitCounter = _attackLimitCounters.get(key);
        if (limitCounter == null) {
            limitCounter = new DefaultLimitCounter();
            _attackLimitCounters.put(key, limitCounter);
        }
        return limitCounter;
    }

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

    public LimitCounter getUntilEndOfTurnForCardTitleLimitCounter(String title, GameTextActionId cardAction) {
        String key = title+"|"+cardAction;
        LimitCounter limitCounter = _turnForCardTitleLimitCounters.get(key);
        if (limitCounter == null) {
            limitCounter = new DefaultLimitCounter();
            _turnForCardTitleLimitCounters.put(key, limitCounter);
        }
        return limitCounter;
    }

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

    public LimitCounter getUntilEndOfGameLimitCounter(String title, GameTextActionId cardAction) {
        String key = title+"|"+cardAction;
        LimitCounter limitCounter = _gameLimitCounters.get(key);
        if (limitCounter == null) {
            limitCounter = new DefaultLimitCounter();
            _gameLimitCounters.put(key, limitCounter);
        }
        return limitCounter;
    }

    public LimitCounter getUntilEndOfForceDrainLimitCounter(String title, GameTextActionId cardAction) {
        String key = title+"|"+cardAction;
        LimitCounter limitCounter = _forceDrainLimitCounters.get(key);
        if (limitCounter == null) {
            limitCounter = new DefaultLimitCounter();
            _forceDrainLimitCounters.put(key, limitCounter);
        }
        return limitCounter;
    }

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

    public LimitCounter getCardTitlePlayedTurnLimitCounter(String title) {
        LimitCounter limitCounter = _cardTitlePlayedTurnLimitCounters.get(title);
        if (limitCounter == null) {
            limitCounter = new DefaultLimitCounter();
            _cardTitlePlayedTurnLimitCounters.put(title, limitCounter);
        }
        return limitCounter;
    }

    // Modifiers Environment

    private List<Modifier> getEffectModifiers(ModifierType modifierType) {
        List<Modifier> modifiers = _modifiers.get(modifierType);
        if (modifiers == null) {
            modifiers = new LinkedList<Modifier>();
            _modifiers.put(modifierType, modifiers);
        }
        return modifiers;
    }

    private void addModifier(Modifier modifier) {
        ModifierType modifierType = modifier.getModifierType();
        getEffectModifiers(modifierType).add(modifier);
    }

    public ModifierHook addAlwaysOnModifier(Modifier modifier) {
        addModifier(modifier);
        return new ModifierHookImpl(this, modifier);
    }

    public void addCardSpecificAlwaysOnModifiers(SwccgGame game, PhysicalCard card) {
        if (card.getBlueprint().getAlwaysOnModifiers(game, card) != null)
            _alwaysOnModifiersMap.put(card.getPermanentCardId(), card.getBlueprint().getAlwaysOnModifiers(game, card));
    }

    /**
     * Adds a modifier that expires when the current turn is finished.
     * @param modifier the modifier
     */
    public void addUntilEndOfTurnModifier(Modifier modifier) {
        modifier.setPersistent(true);
        addModifier(modifier);
        _untilEndOfTurnModifiers.add(modifier);
    }

    /**
     * Adds a modifier that expires when the next turn is finished.
     * @param modifier the modifier
     */
    public void addUntilEndOfNextTurnModifier(Modifier modifier) {
        addUntilEndOfPlayersNextTurnModifier(modifier, _swccgGame.getOpponent(_swccgGame.getGameState().getCurrentPlayerId()));
    }

    /**
     * Adds a modifier that expires when the specified players next turn is finished.
     * @param modifier the modifier
     */
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
     * Adds a modifier that expires when the specified players next turn begins.
     * @param modifier the modifier
     */
    public void addUntilStartOfPlayersNextTurnModifier(Modifier modifier, String playerId) {
        // if it is the opponent's turn add the modifier to _untilStartOfTurnModifiers
        if (_swccgGame.getOpponent(playerId).equals(_swccgGame.getGameState().getCurrentPlayerId())) {
            addUntilStartOfNextTurnModifier(modifier);
        } else {
            modifier.setPersistent(true);
            addModifier(modifier);
            List<Modifier> list = _untilStartOfPlayersNextTurnModifiers.get(playerId);
            if (list == null) {
                list = new LinkedList<>();
                _untilStartOfPlayersNextTurnModifiers.put(playerId, list);
            }
            list.add(modifier);
        }
    }

    /**
     * Adds a modifier that expires when the next turn begins.
     * @param modifier the modifier
     */
    public void addUntilStartOfNextTurnModifier(Modifier modifier) {
        modifier.setPersistent(true);
        addModifier(modifier);
        _untilStartOfTurnModifiers.add(modifier);
    }

    /**
     * Adds a modifier that expires when the current Force drain is finished.
     * @param modifier the modifier
     */
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

    public void addUntilEndOfWeaponFiringModifier(Modifier modifier) {
        modifier.setPersistent(true);
        addModifier(modifier);
        _untilEndOfWeaponFiringModifiers.add(modifier);
    }

    public void addUntilEndOfTractorBeamModifier(Modifier modifier) {
        modifier.setPersistent(true);
        addModifier(modifier);
        _untilEndOfTractorBeamModifiers.add(modifier);
    }

    /**
     * Adds a modifier that expires when the damage segment of the current battle is reached.
     * @param modifier the modifier
     */
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
    public void addUntilEndOfGameModifier(Modifier modifier) {
        modifier.setPersistent(true);
        modifier.setNotRemovedOnRestoreToNormal(true);
        addModifier(modifier);
    }

    private void removeModifiers(List<Modifier> modifiers) {
        for (List<Modifier> list : _modifiers.values())
            list.removeAll(modifiers);
    }

    protected void removeModifier(Modifier modifier) {
        for (List<Modifier> list : _modifiers.values())
            list.remove(modifier);
    }

    /**
     * Removes modifiers whose expire condition is met.
     */
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
     * Removes modifiers that expire when the current turn begins.
     */
    public void removeStartOfTurnModifiers() {
        removeModifiers(_untilStartOfTurnModifiers);
        _untilStartOfTurnModifiers.clear();

        // Move modifiers in "until start of player's next turn" list for the next player
        // to the "until start of turn" list since that player's turn is next
        String nextPlayer = _swccgGame.getOpponent(_swccgGame.getGameState().getCurrentPlayerId());
        List<Modifier> nextTurnModifiers = _untilStartOfPlayersNextTurnModifiers.get(nextPlayer);
        if (nextTurnModifiers != null) {
            _untilStartOfTurnModifiers.addAll(nextTurnModifiers);
            _untilStartOfPlayersNextTurnModifiers.remove(nextPlayer);
        }
    }

    /**
     * Removes counters that expire when the current turn is finished.
     */
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
        _hitOrMadeLostByWeaponMap.clear();
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
    public void removeEndOfCaptivity(PhysicalCard captive) {
        _captivityLimitCounters.remove(captive.getCardId());
    }

    /**
     * Removes modifiers that expire when the Podrace ends.
     */
    public void removeEndOfPodrace() {
        _raceTotalLimitCounters.clear();
    }

    /**
     * Removes modifiers that expire when the current Force drain is complete.
     */
    public void removeEndOfForceDrain() {
        removeModifiers(_untilEndOfForceDrainModifiers);
        _untilEndOfForceDrainModifiers.clear();
        _forceDrainLimitCounters.clear();
    }

    /**
     * Removes modifiers and counters that expire when the current Force loss is complete.
     */
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
    public void removeEndOfEffectResult(EffectResult effectResult) {
        List<Modifier> list = _untilEndOfEffectResultModifiers.get(effectResult);
        if (list != null) {
            removeModifiers(list);
            list.clear();
        }
        _untilEndOfEffectResultModifiers.remove(effectResult);
    }

    /**
     * Removes modifiers that expire when playing the specified card is complete.
     * @param card the card
     */
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
    public void removeEndOfWeaponFiring() {
        removeModifiers(_untilEndOfWeaponFiringModifiers);
        _untilEndOfWeaponFiringModifiers.clear();
    }

    public void removeEndOfTractorBeam() {
        removeModifiers(_untilEndOfTractorBeamModifiers);
        _untilEndOfTractorBeamModifiers.clear();
    }

    /**
     * Removes modifiers that expire when the current attack is finished.
     */
    public void removeEndOfAttack() {
        removeModifiers(_untilEndOfAttackModifiers);
        _untilEndOfAttackModifiers.clear();
        _attackLimitCounters.clear();
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
    public void removeReachedDamageSegmentOfBattle() {
        removeModifiers(_untilDamageSegmentOfBattleModifiers);
        _untilDamageSegmentOfBattleModifiers.clear();
    }

    /**
     * Removes modifiers that expire when the current battle is finished.
     */
    public void removeEndOfBattle() {
        removeReachedDamageSegmentOfBattle();
        removeModifiers(_untilEndOfBattleModifiers);
        _untilEndOfBattleModifiers.clear();
        _battleLimitCounters.clear();
        _firedInBattleMap.clear();
        _firedInBattleCompletedMap.clear();
        _firedInBattleByPlayerMap.clear();
        _firedInBattleByPlayerCompletedMap.clear();
        _firedInBattleByCardMap.clear();
        _firedInBattleByCardCompletedMap.clear();
        _permWeaponFiredInBattleByPlayerMap.clear();
        _permWeaponFiredInBattleByPlayerCompletedMap.clear();
        _permWeaponFiredInBattleByCardMap.clear();
        _permWeaponFiredInBattleByCardCompletedMap.clear();
    }

    /**
     * Removes modifiers that expire when the current Sabacc game is finished.
     */
    public void removeEndOfSabacc() {
        removeModifiers(_untilEndOfSabaccModifiers);
        _untilEndOfSabaccModifiers.clear();
    }

    /**
     * Removes modifiers that expire when the current duel is finished.
     */
    public void removeEndOfDuel() {
        removeModifiers(_untilEndOfDuelModifiers);
        _untilEndOfDuelModifiers.clear();
        _duelLimitCounters.clear();
    }

    /**
     * Removes modifiers that expire when the current lightsaber combat is finished.
     */
    public void removeEndOfLightsaberCombat() {
        removeModifiers(_untilEndOfLightsaberCombatModifiers);
        _untilEndOfLightsaberCombatModifiers.clear();
    }

    /**
     * Removes modifiers that expire when the current epic event action is finished.
     */
    public void removeEndOfEpicEvent() {
        removeModifiers(_untilEndOfEpicEventModifiers);
        _untilEndOfEpicEventModifiers.clear();

        _firedInAttackRunMap.clear();
        _firedInAttackRunCompletedMap.clear();
    }


    /**
     * Excludes the specified card from being affected by the modifier.
     * This is typically used when a card is 'restored' to normal.
     * @param modifier the modifier
     * @param card the card
     */
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
    public boolean isExcludedFromBeingAffected(Modifier modifier, PhysicalCard card) {
        Set<Integer> excludedCards = _excludedFromBeingAffected.get(modifier);
        return excludedCards != null && excludedCards.contains(card.getCardId());
    }

    public void setExtraInformationForArchetypeLabel(String playerId, String text) {
        _extraInformationForArchetypeLabel.put(playerId, text);
    }

    public String getExtraInformationForArchetypeLabel(String playerId) {
        if (_extraInformationForArchetypeLabel.containsKey(playerId))
            return _extraInformationForArchetypeLabel.get(playerId);

        return null;
    }

    /**
     * Increments the amount of Force that has been activated by the player.
     * @param playerId the player
     * @param fromForceGeneration true if Force was activated due to Force generation, otherwise false
     */
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
     * Records that the specified card has performed a regular move.
     * @param card the card
     */
    public void regularMovePerformed(PhysicalCard card) {
        _regularMoveSet.add(card.getCardId());
    }

    /**
     * Determines if the specified card has performed a regular move this turn.
     * @param card the card
     * @return true or false
     */
    public boolean hasPerformedRegularMoveThisTurn(PhysicalCard card) {
        return _regularMoveSet.contains(card.getCardId());
    }

    //region ModifiersState
    // These functions should probably have been put in GameState and not here, but whatever.

    /**
     * Records that a 'bluff card' was stacked.
     */
    public void bluffCardStacked() {
        _bluffCardStacked = true;
    }

    /**
     * Determines if a bluff card was stacked this turn.
     * @return true or false
     */
    public boolean isBluffCardStackedThisTurn() {
        return _bluffCardStacked;
    }

    /**
     * Records that the Death Star's power is 'shut down'.
     */
    public void deathStarPowerIsShutDown() {
        _deathStarPowerShutDown = true;
    }

    /**
     * Determines if the Death Star's power is 'shut down'.
     *
     * @return true or false
     */
    public boolean isDeathStarPowerShutDown() {
        return _deathStarPowerShutDown;
    }

    /**
     * Records that the Senate is in session.
     */
    public void declareSenateIsInSession() {
        _senateIsInSession = true;
    }

    /**
     * Determines if the Senate is in session.
     *
     * @return true or false
     */
    public boolean isSenateInSession() {
        return _senateIsInSession;
    }


    /**
     * Records that the specified card being played (or being deployed).
     * @param card the card
     */
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
     * Records that a Force drain was initiated (or that something was done "instead of Force draining") at the specified
     * location.
     * @param location the location
     */
    public void forceDrainAttempted(PhysicalCard location) {
        _forceDrainStartedSet.add(location);
    }

    /**
     * Determines if a Force drain has been initiated (or if something was done "instead of Force draining") at the specified
     * location.
     * @param location the location
     * @return true or false
     */
    public boolean isForceDrainAttemptedThisTurn(PhysicalCard location) {
        return _forceDrainStartedSet.contains(location);
    }

    /**
     * Records that a Force drain of the specified amount of Force was setFulfilledByOtherAction at the specified location.
     * @param location the location
     * @param amount the amount of Force
     */
    public void forceDrainPerformed(PhysicalCard location, Float amount) {
        _forceDrainCompletedMap.put(location, amount);
    }

    /**
     * Gets the number of Force drains initiated this turn.
     * @return the amount of Force
     */
    public float getNumForceDrainsInitiatedThisTurn() {
        return _forceDrainStartedSet.size();
    }

    /**
     * Gets the total amount of Force that has been Force drained this turn.
     * @return the amount of Force
     */
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
    public Map<PhysicalCard, Set<PhysicalCard>> getForfeitedFromLocationsThisTurn() {
        return Collections.unmodifiableMap(_forfeitedFromLocationMap);
    }

    /**
     * Records that the specified player used a combat card.
     * @param playerId the player
     */
    public void combatCardUsed(String playerId) {
        _usedCombatCard.add(playerId);
    }

    /**
     * Determines if the player has used a combat card this turn.
     * @param playerId the player
     * @return true or false
     */
    public boolean isCombatCardUsedThisTurn(String playerId) {
        return _usedCombatCard.contains(playerId);
    }

    /**
     * Records that the specified character card won a game of sabacc.
     * @param character the character that won sabacc game
     */
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
    public boolean hasWonSabaccGame(GameState gameState, Filterable filters) {
        for (PhysicalCard wonSabacc : _cardsThatWonSabacc) {
            if (Filters.and(filters).accepts(gameState, query(), wonSabacc))
                return true;
        }
        return false;
    }

    /**
     * Records that the specified persona has 'crossed over'.
     *
     * @param persona the persona that was 'crossed over'
     */
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
    public boolean isCrossedOver(GameState gameState, Persona persona) {
        return _personasCrossedOver.contains(persona);
    }

    public void blownAway(PhysicalCard card) {
        try {
            PhysicalCard savedCopy = card.clone();
            _blownAwayCards.add(savedCopy);
        }
        catch (CloneNotSupportedException e) {
            _blownAwayCards.add(card);
        }
    }

    public boolean isBlownAway(GameState gameState, Filterable filters) {
        for (PhysicalCard blowAwayCard : _blownAwayCards) {
            if (Filters.and(filters).accepts(gameState, query(), blowAwayCard))
                return true;
        }
        return false;
    }

    /**
     * Records that the specified Utinni Effect has been completed.
     * @param playerId the player that completed the Utinni Effect
     * @param utinniEffect the Utinni Effect that was completed
     */
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
    public void attemptedJediTest(PhysicalCard jediTest, PhysicalCard attemptedBy) {
        _attemptedJediTestThisTurnMap.put(jediTest.getCardId(), attemptedBy);
    }

    /**
     * Determines if any Jedi Tests have been attempted this turn.
     * @return true or false
     */
    public boolean hasAttemptedJediTests() {
        return !_attemptedJediTestThisTurnMap.isEmpty();
    }

    /**
     * Records that the specified Jedi Test has been completed by the specified character.
     * @param jediTest the Jedi Test that was completed
     * @param completedBy the character that completed the Jedi Test
     */
    public void completedJediTest(PhysicalCard jediTest, PhysicalCard completedBy) {
        _completedJediTest.put(jediTest.getCardId(), completedBy);
    }

    /**
     * Records that the specified starship has had asteroid destiny drawn against it at the specified location.
     *
     * @param starship the starship
     * @param location the location with "Asteroid Rules"
     */
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
    public boolean hadAsteroidDestinyDrawnAgainstThisTurn(PhysicalCard starship, PhysicalCard location) {
        int starshipCardId = starship.getCardId();
        int locationCardId = location.getCardId();

        Set<Integer> starshipCardIds = _asteroidDestinyDrawnAgainstMap.get(locationCardId);
        return (starshipCardIds != null && starshipCardIds.contains(starshipCardId));
    }

    public void participatedInForceDrain(PhysicalCard card) {
        final int cardId = card.getCardId();
        final Integer value = _forceDrainParticipationMap.get(cardId);
        if (value == null)
            _forceDrainParticipationMap.put(cardId, 1);
        else
            _forceDrainParticipationMap.put(cardId, value + 1);
    }

    public boolean hasParticipatedInForceDrainThisTurn(PhysicalCard card) {
        return (_forceDrainParticipationMap.get(card.getCardId())!=null);
    }

    /**
     * Records that an attack on a creature was initiated at the specified location.
     *
     * @param location the location
     */
    public void attackOnCreatureInitiatedAtLocation(PhysicalCard location) {
        _locationAttackOnCreatureSet.add(location.getCardId());
    }

    /**
     * Determines if an attack on a creature has been initiated at the specified location this turn.
     *
     * @param location the location
     * @return true or false
     */
    public boolean isAttackOnCreatureOccurredAtLocationThisTurn(PhysicalCard location) {
        return _locationAttackOnCreatureSet.contains(location.getCardId());
    }

    /**
     * Records that the specified card has participated in an attack on a creature.
     *
     * @param card the card
     */
    public void participatedInAttackOnCreature(PhysicalCard card) {
        _attackOnCreatureParticipationSet.add(card.getCardId());
    }

    /**
     * Determines if the specified card has participated in an attack on a creature this turn.
     *
     * @param card the card
     * @return true if card has participated, otherwise false
     */
    public boolean hasParticipatedInAttackOnCreatureThisTurn(PhysicalCard card) {
        return _attackOnCreatureParticipationSet.contains(card.getCardId());
    }

    /**
     * Records that the specified card has participated in an attack on a non-creature.
     *
     * @param card the card
     */
    public void participatedInAttackOnNonCreature(PhysicalCard card) {
        _attackOnNonCreatureParticipationSet.add(card.getCardId());
    }

    /**
     * Determines if the specified card has participated in an attack on a non-creature this turn.
     *
     * @param card the card
     * @return true if card has participated, otherwise false
     */
    public boolean hasParticipatedInAttackOnNonCreatureThisTurn(PhysicalCard card) {
        return _attackOnNonCreatureParticipationSet.contains(card.getCardId());
    }

    /**
     * Records that a battle was initiated at the specified location by the specified player.
     *
     * @param playerId the player
     * @param location the location
     */
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
    public int getNumBattlesInitiatedThisTurn(String playerId) {
        Integer numBattles = _battleInitiatedByPlayerMap.get(playerId);
        if (numBattles != null) {
            return numBattles;
        }
        return 0;
    }

    public boolean isBattleOccurredAtLocationThisTurn(PhysicalCard location) {
        return (_locationBattleMap.get(location.getCardId())!=null);
    }

    /**
     * Records that the specified card has participated in a battle.
     *
     * @param card the card
     */
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
    public boolean hasParticipatedInBattleAtOtherLocation(PhysicalCard card, PhysicalCard location) {
        final int cardId = card.getCardId();
        final Integer locationCardId = _battleParticipationMap.get(cardId);
        return (locationCardId != null && locationCardId != location.getCardId());
    }

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

    public List<Integer> otherDevicesUsed(PhysicalCard user, PhysicalCard device) {
        List<Integer> usedDevices = _usedDevicesMap.get(user.getCardId());
        List<Integer> otherDevices = new LinkedList<Integer>();
        if (usedDevices != null)
            for (Integer cardId : usedDevices)
                if (cardId!=device.getCardId())
                    otherDevices.add(cardId);

        return otherDevices;
    }

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

    public List<Integer> otherWeaponsUsed(PhysicalCard user, PhysicalCard weapon) {
        List<Integer> usedWeapons = _usedWeaponsMap.get(user.getCardId());
        List<Integer> otherWeapons = new LinkedList<Integer>();
        if (usedWeapons != null)
            for (Integer cardId : usedWeapons)
                if (cardId!=weapon.getCardId())
                    otherWeapons.add(cardId);

        return otherWeapons;
    }

    public List<Integer> otherWeaponsUsed(PhysicalCard user, SwccgBuiltInCardBlueprint permanentWeapon) {
        return otherWeaponsUsed(user, permanentWeapon.getPhysicalCard(_swccgGame));
    }

    /**
     * Records that the specified weapon has been fired in attack.
     * @param card the card
     * @param cardFiringWeapon the card firing the weapon
     * @param complete true if firing completed, false if targeted but not actually fired yet
     */
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
     * Records that the specified target card was targeted by the specified weapon.
     * @param target the target
     * @param weapon the weapon
     */
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
    public List<SwccgBuiltInCardBlueprint> permanentWeaponsTargetedByThisTurn(PhysicalCard target) {
        List<SwccgBuiltInCardBlueprint> permanentWeapons = _targetedByPermanentWeaponsMap.get(target.getCardId());
        if (permanentWeapons == null) {
            permanentWeapons = Collections.emptyList();
        }
        return permanentWeapons;
    }

    /**
     * Records that the specified target card was hit or made lost by the specified weapon.
     * @param target the target
     * @param weapon the weapon
     */
    public void hitOrMadeLostByWeapon(PhysicalCard target, PhysicalCard weapon) {
        List<PhysicalCard> hitOrMadeLostByWeapon = _hitOrMadeLostByWeaponMap.get(target.getPermanentCardId());
        if (hitOrMadeLostByWeapon == null) {
            hitOrMadeLostByWeapon = new LinkedList<>();
            _hitOrMadeLostByWeaponMap.put(target.getPermanentCardId(), hitOrMadeLostByWeapon);
        }
        hitOrMadeLostByWeapon.add(weapon);
        if (weapon.getAttachedTo() != null) {
            hitOrMadeLostByWeapon.add(weapon.getAttachedTo());
        }
    }

    /**
     * Removes the card from the list of cards that have been hit or made lost by a weapon this turn (to be used when restored to normal)
     * @param card the card
     */
    public void clearHitOrMadeLostByWeapon(PhysicalCard card) {
        _hitOrMadeLostByWeaponMap.remove(card.getPermanentCardId());
    }

    /**
     * Checks if a card was hit or made lost by a card accepted by the specified filter (or a weapon fired by the specified card)
     * @param target the card that was hit
     * @param hitBy the card that hit (or used a weapon to hit) the target
     * @return
     */
    public boolean wasHitOrMadeLostByWeapon(PhysicalCard target, Filter hitBy) {
        if (!_hitOrMadeLostByWeaponMap.containsKey(target.getPermanentCardId()))
            return false;
        List<PhysicalCard> hitOrMadeLostByWeapon = _hitOrMadeLostByWeaponMap.get(target.getPermanentCardId());
        return Filters.filter(hitOrMadeLostByWeapon, _swccgGame, hitBy).isEmpty();
    }

    public boolean canPerformSpecialBattlegroundDownload(GameState gameState, String playerId) {
        if (!gameState.getGame().getFormat().hasDownloadBattlegroundRule())
            return false;
        return _specialDownloadBattlegroundMap.get(playerId)==null;
    }

    public void performedSpecialBattlegroundDownload(String playerId) {
        _specialDownloadBattlegroundMap.put(playerId, 1);
    }
    
    //endregion

    public Collection<Modifier> getModifiersAffecting(GameState gameState, PhysicalCard card) {

        Set<Modifier> result = new HashSet<Modifier>();
        for (List<Modifier> modifiers : _modifiers.values()) {
            for (Modifier modifier : modifiers) {
                Condition condition = modifier.getCondition();
                Condition additionalCondition = modifier.getAdditionalCondition(gameState, query(), card);
                if ((condition == null || condition.isFulfilled(gameState, query())) && (additionalCondition == null || additionalCondition.isFulfilled(gameState, query())))
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

        if (_alwaysOnModifiersMap.containsKey(card.getPermanentCardId())) {
            List<Modifier> alwaysOnModifiers = _alwaysOnModifiersMap.get(card.getPermanentCardId());
            if (alwaysOnModifiers != null) {
                for (Modifier modifier : alwaysOnModifiers) {
                    Condition condition = modifier.getCondition();
                    Condition additionalCondition = modifier.getAdditionalCondition(gameState, query(), card);
                    if ((condition == null || condition.isFulfilled(gameState, query())) && (additionalCondition == null || additionalCondition.isFulfilled(gameState, query())))
                        if (affectsCardWithSkipSet(gameState, card, modifier))
                            if (!foundCumulativeConflict(gameState, result, modifier))
                                result.add(modifier);

                }
            }
        }

        return result;
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
                if (
                        // Modifiers that affect different things cannot be said to be cumulative in any sense
                        liveModifier.getModifierType() == modifierType
                        // Non-card modifiers must be from a game rule, and those cannot violate the cumulative rule.
                        && liveModifier.getSource(gameState) != null
                        && (
                                liveModifier.isFromPermanentPilot() == modifier.isFromPermanentPilot()
                                && liveModifier.isFromPermanentAstromech() == modifier.isFromPermanentAstromech())
                                // The cumulative rule is all about 'copies" of the same card, which is to say cards
                                // with the same title.
                                && liveModifier.getSource(gameState).getTitle().equals(cardTitle)
                                && (
                                        //Non-unique cards with the same title and same modifier type are the main
                                        // reason the cumulative rule exists at all.
                                        modifier.getSource(gameState).getBlueprint().getUniqueness() != Uniqueness.UNIQUE
                                        || liveModifier.getSource(gameState).getBlueprint().getUniqueness() != Uniqueness.UNIQUE
                                        //This is for checking for persistent modifiers that were emitted by a previous
                                        // incarnation of a unique card.  For example, if a unique card modifies a force
                                        // drain amount and then leaves play and re-enters, they are considered a new
                                        // card, but since another "copy" of that card already modified the force drain
                                        // amount, the new one cannot also alter it without violating the cumulative rule.
                                        || modifier.getSource(gameState).getCardId() != liveModifier.getSource(gameState).getCardId()

                                        /*
                                        The above section is the reason for the Rebel Flight Suit failure when you attach
                                        2 copies of it to 2 different Ralltiir Freighter Captains on the same ship.
                                        Either RFS marks its maneuver modifier as cumulative and you end up with two +2
                                        bonuses from nonuniques, or you mark it as non-cumulative and the pilot's own +1
                                        crushes the RFS +2 for cumulative reasons.

                                        This entire for loop should be broken up to use fewer && checks and more standalone
                                        if blocks in the future. (Use "continue" upon finding a relationship that can't
                                        possibly be cumulative rather than endlessly chaining.)

                                        This subsection should then be broken up into two categories:
                                        - a check for both sources being unique
                                            - same current id is the same card with two different unrelated modifiers that should combine
                                            - different current id is a past life invocation that violates cumulative
                                        - a check for both sources being nonunique:
                                            - same permanent id is the same card with two different unrelated modifiers that should combine
                                            - different permanent ids are two different cards violating cumulative

                                         */
                                )
                                // Presumably cards with the same title on different sides do not interfere cumulatively
                                && liveModifier.isForPlayer(forPlayer)
                                && liveModifier.getIcon() == icon
                        ) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean affectsCardWithSkipSet(GameState gameState, PhysicalCard physicalCard, Modifier modifier) {
        if (!_skipSet.contains(modifier) && physicalCard != null) {
            _skipSet.add(modifier);
            boolean result = modifier.affectsCard(gameState, query(), physicalCard);
            _skipSet.remove(modifier);
            return result;
        } else {
            return false;
        }
    }

    /**
     * Gets all the existing persistent modifiers that affect the specified card.
     * This is used to determine which modifiers to exclude the card from when "restoring to normal".
     * @param gameState the game state
     * @param card the card
     * @return the modifiers
     */
    public List<Modifier> getPersistentModifiersAffectingCard(GameState gameState, PhysicalCard card) {
        List<Modifier> persistentModifiers = new LinkedList<Modifier>();
        for (List<Modifier> modifiers : _modifiers.values()) {
            for (Modifier modifier : modifiers) {
                if (modifier.isPersistent()
                        && modifier.affectsCard(gameState, query(), card)) {
                    persistentModifiers.add(modifier);
                }
            }
        }
        return persistentModifiers;
    }


    public List<Modifier> getModifiers(GameState gameState, ModifierType modifierType) {
        return getKeywordModifiersAffectingCard(gameState, modifierType, null, null);
    }

    public List<Modifier> getModifiersAffectingCard(GameState gameState, ModifierType modifierType, PhysicalCard card) {
        return getKeywordModifiersAffectingCard(gameState, modifierType, null, card);
    }

    public List<Modifier> getKeywordModifiersAffectingCard(GameState gameState, ModifierType modifierType, Keyword keyword, PhysicalCard card) {
        // Get always on modifiers
        List<? extends Modifier> alwaysOnModifiers = null;
        if (card != null && _alwaysOnModifiersMap.containsKey(card.getPermanentCardId())) {
            alwaysOnModifiers = _alwaysOnModifiersMap.get(card.getPermanentCardId());
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
                                    Condition additionalCondition = modifier.getAdditionalCondition(gameState, query(), card);
                                    if ((condition == null || condition.isFulfilled(gameState, query())) && (additionalCondition == null || additionalCondition.isFulfilled(gameState, query())))
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
                                    boolean checkAffectsCardFirst = (modifierType == ModifierType.GIVE_ICON || modifierType == ModifierType.CANCEL_FORCE_ICON || modifierType == ModifierType.CANCEL_FORCE_ICONS || modifierType == ModifierType.CANCEL_ICONS || modifierType == ModifierType.EQUALIZE_FORCE_ICONS || modifierType == ModifierType.MAY_NOT_ADD_ICON);
                                    if (!checkAffectsCardFirst || card == null || modifier.affectsCard(gameState, query(), card)) {
                                        Condition condition = modifier.getCondition();
                                        Condition additionalCondition = modifier.getAdditionalCondition(gameState, query(), card);
                                        if ((condition == null || condition.isFulfilled(gameState, query())) && (additionalCondition == null || additionalCondition.isFulfilled(gameState, query())))
                                            if (modifier.getSource(gameState) == null || modifier.isPersistent() || modifier.getSource(gameState).getZone() == Zone.STACKED || modifier.getSource(gameState).getZone() == Zone.OUT_OF_PLAY || modifier.isWhileInactiveInPlay() == !gameState.isCardInPlayActive(modifier.getSource(gameState), false, true, false, false, false, false, false, false))
                                                if (modifier.getSource(gameState) == null || modifier.isPersistent() || !modifier.isFromPermanentPilot() || hasPermanentPilot(gameState, modifier.getSource(gameState)))
                                                    if (modifier.getSource(gameState) == null || modifier.isPersistent() || !modifier.isFromPermanentAstromech() || hasPermanentAstromech(gameState, modifier.getSource(gameState)))
                                                        if (checkAffectsCardFirst || card == null || modifier.affectsCard(gameState, query(), card))
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

    /**
     * Gets all the cards on table that are targeting the specified card.
     * @param gameState the game state
     * @param card the card
     * @return the cards on table targeting the specified card
     */
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
                                boolean checkTargetsCardFirst = (modifierType == ModifierType.GIVE_ICON || modifierType == ModifierType.CANCEL_FORCE_ICON || modifierType == ModifierType.CANCEL_FORCE_ICONS || modifierType == ModifierType.CANCEL_ICONS || modifierType == ModifierType.EQUALIZE_FORCE_ICONS || modifierType == ModifierType.MAY_NOT_ADD_ICON);
                                if (!checkTargetsCardFirst || modifier.isTargetingCard(gameState, query(), card)) {
                                    Condition condition = modifier.getCondition();
                                    Condition additionalCondition = modifier.getAdditionalCondition(gameState, query(), card);
                                    if ((condition == null || condition.isFulfilled(gameState, query())) && (additionalCondition == null || additionalCondition.isFulfilled(gameState, query()))) {
                                        if (modifier.isPersistent() || source.getZone() == Zone.STACKED || modifier.isWhileInactiveInPlay() == !gameState.isCardInPlayActive(source, false, true, false, false, false, false, false, false)) {
                                            if (modifier.isPersistent() || !modifier.isFromPermanentPilot() || hasPermanentPilot(gameState, source)) {
                                                if (modifier.isPersistent() || !modifier.isFromPermanentAstromech() || hasPermanentAstromech(gameState, source)) {
                                                    if (checkTargetsCardFirst || modifier.isTargetingCard(gameState, query(), card)) {
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
     * Gets all the cards that are targeting the specified card. This is used for the card info screen on the user interface.
     * @param gameState the game state
     * @param card the card
     * @return the cards targeting the specified card
     */
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


}
