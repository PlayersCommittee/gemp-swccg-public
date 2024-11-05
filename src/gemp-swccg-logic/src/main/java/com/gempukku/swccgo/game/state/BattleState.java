package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ExcludedFromBattleModifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersEnvironment;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.SnapshotData;
import com.gempukku.swccgo.logic.timing.Snapshotable;
import com.gempukku.swccgo.logic.timing.actions.battle.BattleDamageSegmentAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

// This class contains the state information for a
// battle within a game of Gemp-Swccg.
//
public class BattleState implements Snapshotable<BattleState> {
    private SwccgGame _game;
    private String _playerInitiatedBattle;
    private PhysicalCard _location;
    private boolean _isLocalTrouble;
    private boolean _isBesieged;
    private Set<PhysicalCard> _localTroubleParticipants = new HashSet<PhysicalCard>();
    private Set<PhysicalCard> _darkCardsParticipants = new HashSet<PhysicalCard>();
    private Set<PhysicalCard> _lightCardsParticipants = new HashSet<PhysicalCard>();
    private Set<PhysicalCard> _darkCardsParticipantsWhenResultDetermined = new HashSet<PhysicalCard>();
    private Set<PhysicalCard> _lightCardsParticipantsWhenResultDetermined = new HashSet<PhysicalCard>();
    private boolean _battleStarted;
    private boolean _reachedPowerSegment;
    private boolean _baseAttritionCalculated;
    private boolean _reachedDamageSegment;
    private boolean _canceled;
    private boolean _endDuringPowerSegment;
    private String _winner;
    private String _loser;
    private Map<String, Float> _totalDestinyToPowerOnly = new HashMap<String, Float>();
    private boolean _battleDestinyFromDrawsSwitched;
    private boolean _preBattleDestinyTotalPowerSwitched;
    private Map<String, Float> _totalBattleDestinyFromDraws = new HashMap<String, Float>();
    private Map<String, Float> _totalBattleDestinyDiffFromDraws = new HashMap<String, Float>();
    private Map<String, Float> _totalBattleDestiny = new HashMap<String, Float>();
    private Map<String, Boolean> _totalBattleDestinyOverridden = new HashMap<String, Boolean>();
    private Map<String, List<PhysicalCard>> _battleDestinyDraws = new HashMap<String, List<PhysicalCard>>();
    private Map<String, List<Float>> _battleDestinyDrawValues = new HashMap<String, List<Float>>();
    private Map<String, List<Boolean>> _battleDestinyDrawsCancelableByOpponent = new HashMap<String, List<Boolean>>();
    private Map<String, Boolean> _drewDestinyToAttrition = new HashMap<String, Boolean>();
    private Map<String, Integer> _destinyDrawsToPowerMade = new HashMap<String, Integer>();
    private Map<String, Integer> _destinyDrawsToAttritionMade = new HashMap<String, Integer>();
    private Map<String, Boolean> _takesNoBattleDamage = new HashMap<String, Boolean>();
    private Map<String, Float> _baseBattleDamage = new HashMap<String, Float>();
    private Map<String, Float> _battleDamageSatisfied = new HashMap<String, Float>();
    private Map<String, Integer> _forceLostToBattleDamage = new HashMap<String, Integer>();
    private Map<String, Boolean> _allBattleDamageSatisfied = new HashMap<String, Boolean>();
    private Map<String, Float> _baseAttrition = new HashMap<String, Float>();
    private Map<String, Float> _totalAttrition = new HashMap<String, Float>();
    private Map<String, Float> _attritionSatisfied = new HashMap<String, Float>();
    private Map<String, Boolean> _allAttritionSatisfied = new HashMap<String, Boolean>();
    private BattleDamageSegmentAction.ChooseCardToLoseOrForfeitEffect _currentLostOrForfeitEffect;

    /**
     * Needed to generate snapshot.
     */
    public BattleState() {
    }

    @Override
    public void generateSnapshot(BattleState selfSnapshot, SnapshotData snapshotData) {
        BattleState snapshot = selfSnapshot;

        // Set each field
        snapshot._game = _game;
        snapshot._playerInitiatedBattle = _playerInitiatedBattle;
        snapshot._location = snapshotData.getDataForSnapshot(_location);
        snapshot._isLocalTrouble = _isLocalTrouble;
        snapshot._isBesieged = _isBesieged;
        for (PhysicalCard card : _localTroubleParticipants) {
            snapshot._localTroubleParticipants.add(snapshotData.getDataForSnapshot(card));
        }
        for (PhysicalCard card : _darkCardsParticipants) {
            snapshot._darkCardsParticipants.add(snapshotData.getDataForSnapshot(card));
        }
        for (PhysicalCard card : _lightCardsParticipants) {
            snapshot._lightCardsParticipants.add(snapshotData.getDataForSnapshot(card));
        }
        for (PhysicalCard card : _darkCardsParticipantsWhenResultDetermined) {
            snapshot._darkCardsParticipantsWhenResultDetermined.add(snapshotData.getDataForSnapshot(card));
        }
        for (PhysicalCard card : _lightCardsParticipantsWhenResultDetermined) {
            snapshot._lightCardsParticipantsWhenResultDetermined.add(snapshotData.getDataForSnapshot(card));
        }
        snapshot._battleStarted = _battleStarted;
        snapshot._reachedPowerSegment = _reachedPowerSegment;
        if (_reachedPowerSegment) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " after weapons segment");
        }
        snapshot._baseAttritionCalculated = _baseAttritionCalculated;
        snapshot._reachedDamageSegment = _reachedDamageSegment;
        snapshot._canceled = _canceled;
        snapshot._endDuringPowerSegment = _endDuringPowerSegment;
        snapshot._winner = _winner;
        snapshot._loser = _loser;
        snapshot._totalDestinyToPowerOnly.putAll(_totalDestinyToPowerOnly);
        snapshot._battleDestinyFromDrawsSwitched = _battleDestinyFromDrawsSwitched;
        snapshot._preBattleDestinyTotalPowerSwitched = _preBattleDestinyTotalPowerSwitched;
        snapshot._totalBattleDestinyFromDraws.putAll(_totalBattleDestinyFromDraws);
        snapshot._totalBattleDestinyDiffFromDraws.putAll(_totalBattleDestinyDiffFromDraws);
        snapshot._totalBattleDestiny.putAll(_totalBattleDestiny);
        snapshot._totalBattleDestinyOverridden.putAll(_totalBattleDestinyOverridden);
        for (String playerId : _battleDestinyDraws.keySet()) {
            List<PhysicalCard> snapshotList = new ArrayList<PhysicalCard>();
            snapshot._battleDestinyDraws.put(playerId, snapshotList);
            for (PhysicalCard card : _battleDestinyDraws.get(playerId)) {
                snapshotList.add(snapshotData.getDataForSnapshot(card));
            }
        }
        for (String playerId : _battleDestinyDrawValues.keySet()) {
            List<Float> snapshotList = new ArrayList<Float>(_battleDestinyDrawValues.get(playerId));
            snapshot._battleDestinyDrawValues.put(playerId, snapshotList);
        }
        for (String playerId : _battleDestinyDrawsCancelableByOpponent.keySet()) {
            List<Boolean> snapshotList = new ArrayList<Boolean>(_battleDestinyDrawsCancelableByOpponent.get(playerId));
            snapshot._battleDestinyDrawsCancelableByOpponent.put(playerId, snapshotList);
        }
        snapshot._drewDestinyToAttrition.putAll(_drewDestinyToAttrition);
        snapshot._destinyDrawsToPowerMade.putAll(_destinyDrawsToPowerMade);
        snapshot._destinyDrawsToAttritionMade.putAll(_destinyDrawsToAttritionMade);
        snapshot._takesNoBattleDamage.putAll(_takesNoBattleDamage);
        snapshot._baseBattleDamage.putAll(_baseBattleDamage);
        snapshot._battleDamageSatisfied.putAll(_battleDamageSatisfied);
        snapshot._forceLostToBattleDamage.putAll(_forceLostToBattleDamage);
        snapshot._allBattleDamageSatisfied.putAll(_allBattleDamageSatisfied);
        snapshot._baseAttrition.putAll(_baseAttrition);
        snapshot._totalAttrition.putAll(_totalAttrition);
        snapshot._attritionSatisfied.putAll(_attritionSatisfied);
        snapshot._allAttritionSatisfied.putAll(_allAttritionSatisfied);
        if (_currentLostOrForfeitEffect != null) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " with ChooseCardToLoseOrForfeitEffect");
        }
    }

    public BattleState(SwccgGame game, String playerId, PhysicalCard location, boolean isLocalTrouble) {
        _game = game;
        _playerInitiatedBattle = playerId;
        _location = location;
        _isLocalTrouble = isLocalTrouble;
    }

    public String getPlayerInitiatedBattle() {
        return _playerInitiatedBattle;
    }

    public PhysicalCard getBattleLocation() {
        return _location;
    }

    public Collection<PhysicalCard> getDarkCardsParticipating() {
        return _darkCardsParticipants;
    }

    public Collection<PhysicalCard> getLightCardsParticipating() {
        return _lightCardsParticipants;
    }

    public Collection<PhysicalCard> getCardsParticipating(String playerId) {
        if (_game.getDarkPlayer().equals(playerId))
            return _darkCardsParticipants;
        else
            return _lightCardsParticipants;
    }

    public Collection<PhysicalCard> getAllCardsParticipating() {
        Collection<PhysicalCard> allCards = new LinkedList<PhysicalCard>();
        allCards.addAll(_darkCardsParticipants);
        allCards.addAll(_lightCardsParticipants);
        return allCards;
    }

    public Collection<PhysicalCard> getCardsParticipatingWhenResultDetermined(String playerId) {
        if (_game.getDarkPlayer().equals(playerId))
            return _darkCardsParticipantsWhenResultDetermined;
        else
            return _lightCardsParticipantsWhenResultDetermined;
    }

    public Collection<PhysicalCard> getAllCardsParticipatingWhenResultDetermined() {
        Collection<PhysicalCard> allCards = new LinkedList<PhysicalCard>();
        allCards.addAll(_darkCardsParticipantsWhenResultDetermined);
        allCards.addAll(_lightCardsParticipantsWhenResultDetermined);
        return allCards;
    }

    public void setLocalTroubleParticipants(Collection<PhysicalCard> cards) {
        _localTroubleParticipants.addAll(cards);
    }

    public void addParticipant(GameState gameState, PhysicalCard card) {
        if (card.getOwner().equals(gameState.getDarkPlayer()))
            _darkCardsParticipants.add(card);
        else
            _lightCardsParticipants.add(card);
    }

    public void addParticipants(GameState gameState, Collection<PhysicalCard> cards) {
        for (PhysicalCard card : cards) {
            if (card.getOwner().equals(gameState.getDarkPlayer()))
                _darkCardsParticipants.add(card);
            else
                _lightCardsParticipants.add(card);
        }
    }

    public void removeParticipant(GameState gameState, PhysicalCard card) {
        _darkCardsParticipants.remove(card);
        _lightCardsParticipants.remove(card);
    }

    public void addDarkCardParticipant(PhysicalCard card) {
        _darkCardsParticipants.add(card);
    }

    public void addLightCardParticipant(PhysicalCard card) {
        _lightCardsParticipants.add(card);
    }

    public boolean isCardParticipatingInBattle(PhysicalCard card) {
        for (PhysicalCard cardInBattle : getAllCardsParticipating())
            if (cardInBattle.getCardId()==card.getCardId())
                return true;

        return false;
    }

    public void setCardsParticipatingWhenResultDetermined() {
        _darkCardsParticipantsWhenResultDetermined.addAll(_darkCardsParticipants);
        _lightCardsParticipantsWhenResultDetermined.addAll(_lightCardsParticipants);
    }

    public void updateParticipants(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();

        // Figure out cards that will initially participate in battle for each side
        Collection<PhysicalCard> previousParticipants = getAllCardsParticipating();
        _darkCardsParticipants.clear();
        _lightCardsParticipants.clear();

        Collection<PhysicalCard> currentParticipants;
        if (_isLocalTrouble)
            currentParticipants = Filters.filterActive(game, null, Filters.and(Filters.canParticipateInBattleAt(_location, _playerInitiatedBattle), Filters.in(_localTroubleParticipants)));
        else
            currentParticipants = Filters.filterActive(game, null, Filters.canParticipateInBattleAt(_location, _playerInitiatedBattle));

        for (PhysicalCard currentParticipant : currentParticipants) {
            if (!previousParticipants.contains(currentParticipant)) {
                // Only add participants if not reached power segment and card has not already participated in a battle,
                // otherwise set card as excluded from battle
                if (!_reachedPowerSegment &&
                        !modifiersQuerying.hasParticipatedInBattle(currentParticipant)) {

                    modifiersQuerying.participatedInBattle(currentParticipant, _location);
                    gameState.addCardToBattleGroup(currentParticipant);
                    addParticipant(gameState, currentParticipant);
                }
                else {
                    modifiersEnvironment.addUntilEndOfBattleModifier(new ExcludedFromBattleModifier(null, Filters.sameCardId(currentParticipant)));
                }
            }
            else {
                addParticipant(gameState, currentParticipant);
            }
        }

        for (PhysicalCard previousParticipant : previousParticipants) {
            // Remove cards no longer in the battle
            if (!_darkCardsParticipants.contains(previousParticipant) && !_lightCardsParticipants.contains(previousParticipant)) {
                gameState.removeCardFromBattleGroup(previousParticipant);
                removeParticipant(gameState, previousParticipant);
            }
        }
    }

    public boolean isLocalTrouble() {
        return _isLocalTrouble;
    }

    public boolean isBombingRun() {
        for (PhysicalCard card : getAllCardsParticipating()) {
             if (card.isMakingBombingRun()) {
                 return true;
             }
        }
        return false;
    }

    public boolean isBesieged() {
        return _isBesieged;
    }

    public void battleStarted() {
        _battleStarted = true;
    }

    public boolean isBattleStarted() {
        return _battleStarted;
    }

    public void cancel() {
        _canceled = true;
    }

    public boolean isCanceled() {
        return _canceled;
    }

    public boolean canContinue(SwccgGame game) {
        if (_canceled)
            return false;

        if (isReachedDamageSegment())
            return true;

        boolean foundMayInitiateBattle = Filters.canSpot(game, null, Filters.and(Filters.owner(_playerInitiatedBattle), Filters.mayInitiateBattle, Filters.canParticipateInBattleAt(_location, _playerInitiatedBattle)));
        boolean foundMayBeBattled = Filters.canSpot(game, null, Filters.and(Filters.owner(game.getOpponent(_playerInitiatedBattle)), Filters.mayBeBattled, Filters.canParticipateInBattleAt(_location, _playerInitiatedBattle)));

        return (foundMayInitiateBattle || game.getModifiersQuerying().hasPresenceAt(game.getGameState(), _playerInitiatedBattle, _location, true, _playerInitiatedBattle, null))
                && (foundMayBeBattled || game.getModifiersQuerying().hasPresenceAt(game.getGameState(), game.getOpponent(_playerInitiatedBattle), _location, true, _playerInitiatedBattle, null));
    }

    public void endDuringPowerSegment() {
        _endDuringPowerSegment = true;
    }

    public boolean isEndDuringPowerSegment() {
        return _endDuringPowerSegment;
    }

    public void reachedPowerSegment() {
        _reachedPowerSegment = true;
    }

    public boolean isReachedPowerSegment() {
        return _reachedPowerSegment;
    }

    public void baseAttritionCalculated() {
        _baseAttritionCalculated = true;
    }

    public boolean isBaseAttritionCalculated() {
        return _baseAttritionCalculated;
    }

    public void reachedDamageSegment() {
        _reachedDamageSegment = true;
    }

    public boolean isReachedDamageSegment() {
        return _reachedDamageSegment;
    }

    public void setWinner(String winner) {
        _winner = winner;
    }

    public void setLoser(String loser) {
        _loser = loser;
    }

    public boolean isWinner(String playerId) {
        return (_winner != null && _winner.equals(playerId));
    }

    public boolean isLoser(String playerId) {
        return (_loser != null && _loser.equals(playerId));
    }

    public void increaseTotalDestinyToPowerOnly(String player, float destiny, int numDraws) {
        final Float previousValue = _totalDestinyToPowerOnly.get(player);
        if (previousValue==null)
            _totalDestinyToPowerOnly.put(player, destiny);
        else
            _totalDestinyToPowerOnly.put(player, previousValue + destiny);

        final Integer previousNumDraws = _destinyDrawsToPowerMade.get(player);
        if (previousNumDraws==null)
            _destinyDrawsToPowerMade.put(player, numDraws);
        else
            _destinyDrawsToPowerMade.put(player, previousNumDraws + numDraws);
    }

    public float getTotalDestinyToPowerOnly(String player) {
        final Float value = _totalDestinyToPowerOnly.get(player);
        if (value==null)
            return 0;
        else
            return value;
    }

    public int getNumDestinyToTotalPowerDrawn(String player) {
        final Integer value = _destinyDrawsToPowerMade.get(player);
        if (value==null)
            return 0;
        else
            return value;
    }

    public void switchBattleDestinyFromDraws() {
        _battleDestinyFromDrawsSwitched = true;
    }

    public void switchPreBattleDestinyTotalPower() {
        _preBattleDestinyTotalPowerSwitched = !_preBattleDestinyTotalPowerSwitched;
    }

    public boolean isPreBattleDestinyTotalPowerSwitched() {
        return _preBattleDestinyTotalPowerSwitched;
    }

    public void increaseTotalBattleDestinyFromDraws(String player, List<PhysicalCard> destinyCardDraws, List<Boolean> destinyDrawCancelableByOpponent, List<Float> destinyDrawValues, Float totalDestiny) {
        List<PhysicalCard> previousDraws = _battleDestinyDraws.get(player);
        if (previousDraws == null) {
            previousDraws = new ArrayList<PhysicalCard>();
            _battleDestinyDraws.put(player, previousDraws);
        }
        previousDraws.addAll(destinyCardDraws);

        List<Boolean> previousDrawsCancelableByOpponent = _battleDestinyDrawsCancelableByOpponent.get(player);
        if (previousDrawsCancelableByOpponent == null) {
            previousDrawsCancelableByOpponent = new ArrayList<Boolean>();
            _battleDestinyDrawsCancelableByOpponent.put(player, previousDrawsCancelableByOpponent);
        }
        previousDrawsCancelableByOpponent.addAll(destinyDrawCancelableByOpponent);

        List<Float> previousDrawValues = _battleDestinyDrawValues.get(player);
        if (previousDrawValues == null) {
            previousDrawValues = new ArrayList<Float>();
            _battleDestinyDrawValues.put(player, previousDrawValues);
        }
        previousDrawValues.addAll(destinyDrawValues);

        Float totalFromDraws = _totalBattleDestinyFromDraws.get(player);
        if (totalFromDraws == null) {
            totalFromDraws = 0f;
        }
        for (Float drawValue : destinyDrawValues) {
            totalFromDraws += drawValue;
        }
        _totalBattleDestinyFromDraws.put(player, totalFromDraws);

        Float previousTotal = _totalBattleDestiny.get(player);
        if (previousTotal == null) {
            previousTotal = 0f;
        }
        _totalBattleDestiny.put(player, previousTotal + totalDestiny);
        _totalBattleDestinyDiffFromDraws.put(player, _totalBattleDestiny.get(player) - _totalBattleDestinyFromDraws.get(player));
    }

    public float getTotalBattleDestinyFromDrawsOnly(SwccgGame game, String player) {
        String playerToUse = _battleDestinyFromDrawsSwitched ? game.getOpponent(player) : player;
        final Float value = _totalBattleDestinyFromDraws.get(playerToUse);
        if (value == null)
            return 0;
        else
            return value;
    }

    public void setTotalBattleDestiny(SwccgGame game, String player, float destiny) {
        String playerToUse = _battleDestinyFromDrawsSwitched ? game.getOpponent(player) : player;
        _totalBattleDestiny.put(playerToUse, destiny);
        _totalBattleDestinyOverridden.put(playerToUse, true);
    }

    public float getTotalBattleDestiny(SwccgGame game, String player) {
        String playerToUse = _battleDestinyFromDrawsSwitched ? game.getOpponent(player) : player;
        Float value;
        if (_totalBattleDestinyOverridden.get(player) != null && _totalBattleDestinyOverridden.get(player)) {
            value = _totalBattleDestiny.get(player);
            if (value == null) {
                value = 0f;
            }
        }
        else {
            value = _totalBattleDestinyFromDraws.get(playerToUse);
            if (value == null) {
                value = 0f;
            }
            Float valueToAdd = _totalBattleDestinyDiffFromDraws.get(player);
            if (valueToAdd != null) {
                value += valueToAdd;
            }
        }

        return value;
    }

    public int getNumBattleDestinyDrawn(String player) {
        return _battleDestinyDraws.get(player) != null ? _battleDestinyDraws.get(player).size() : 0;
    }

    public int getNumBattleDestinyDrawnCancelableByOpponent(String player) {
        int numCancelable = 0;
        List<Boolean> list = _battleDestinyDrawsCancelableByOpponent.get(player);
        if (list != null) {
            for (Boolean cancelable : list) {
                if (cancelable) {
                    numCancelable++;
                }
            }
        }
        return numCancelable;
    }

    public List<PhysicalCard> getBattleDestinyDraws(String player) {
        return _battleDestinyDraws.get(player);
    }

    public List<Float> getBattleDestinyDrawsValues(String player) {
        return _battleDestinyDrawValues.get(player);
    }

    public List<Boolean> getBattleDestinyDrawsCancelableByOpponent(String player) {
        return _battleDestinyDrawsCancelableByOpponent.get(player);
    }

    public void cancelPreviousBattleDestinyDraws(String player, Set<Integer> drawsToCancel) {
        List<Integer> orderedDrawsToCancel = new LinkedList<Integer>(drawsToCancel);
        Collections.sort(orderedDrawsToCancel);
        Collections.reverse(orderedDrawsToCancel);

        for (Integer orderDrawToCancel : orderedDrawsToCancel) {
            _battleDestinyDrawsCancelableByOpponent.get(player).remove(orderDrawToCancel.intValue());
            _battleDestinyDrawValues.get(player).remove(orderDrawToCancel.intValue());
            _battleDestinyDraws.get(player).remove(orderDrawToCancel.intValue());
        }

        if (_totalBattleDestinyOverridden.get(player) == null || !_totalBattleDestinyOverridden.get(player)) {
            float totalBattleDestiny = 0;
            for (Float value : _battleDestinyDrawValues.get(player)) {
                totalBattleDestiny += value;
            }
            _totalBattleDestinyFromDraws.put(player, totalBattleDestiny);
        }
    }

    public void increaseAttritionFromDestinyToAttritionDrawn(String player, float destiny, int numDraws) {
        increaseBaseAttrition(_game.getOpponent(player), destiny);
        final Integer previousNumDraws = _destinyDrawsToAttritionMade.get(player);
        if (previousNumDraws==null)
            _destinyDrawsToAttritionMade.put(player, numDraws);
        else
            _destinyDrawsToAttritionMade.put(player, previousNumDraws + numDraws);
    }

    public boolean isDrewDestinyToAttrition(String player) {
        final Boolean value = _drewDestinyToAttrition.get(player);
        if (value==null)
            return false;
        else
            return value;
    }

    public void setDrewDestinyToAttrition(String player, boolean drew) {
        _drewDestinyToAttrition.put(player, drew);
    }

    public int getNumDestinyToAttritionDrawn(String player) {
        final Integer value = _destinyDrawsToAttritionMade.get(player);
        if (value==null)
            return 0;
        else
            return value;
    }

    public void setBaseBattleDamage(String player, float damage) {
        _baseBattleDamage.put(player, damage);
    }

    public float getBaseBattleDamage(String player) {
        final Float baseDamage = _baseBattleDamage.get(player);
        if (baseDamage==null)
            return 0;
        return Math.max(0, baseDamage);
    }

    public float getBattleDamageTotal(SwccgGame game, String player) {
        final Float baseDamage = _baseBattleDamage.get(player);
        if (baseDamage==null)
            return 0;

        return game.getModifiersQuerying().getTotalBattleDamage(game.getGameState(), player);
    }

    public float getBattleDamageRemaining(SwccgGame game, String player) {
        if (_allBattleDamageSatisfied.get(player) != null)
            return 0;

        return Math.max(0, getBattleDamageTotal(game, player) - getBattleDamageSatisfied(player));
    }

    public void increaseForceLostToBattleDamage(String player, int increaseBy) {
        final Integer previousValue = _forceLostToBattleDamage.get(player);
        if (previousValue==null)
            _forceLostToBattleDamage.put(player, increaseBy);
        else
            _forceLostToBattleDamage.put(player, previousValue + increaseBy);
    }

    public int getForceLostToBattleDamage(String player) {
        final Integer forceLost = _forceLostToBattleDamage.get(player);
        if (forceLost==null)
            return 0;
        return forceLost;
    }

    public void increaseBattleDamageSatisfied(String player, float increaseBy) {
        final Float previousValue = _battleDamageSatisfied.get(player);
        if (previousValue==null)
            _battleDamageSatisfied.put(player, increaseBy);
        else
            _battleDamageSatisfied.put(player, previousValue + increaseBy);
    }

    public void satisfyAllBattleDamage(String player) {
        _allBattleDamageSatisfied.put(player, true);
    }

    public float getBattleDamageSatisfied(String player) {
        final Float satisfied = _battleDamageSatisfied.get(player);
        if (satisfied == null)
            return 0;
        return satisfied;
    }

    public float getTotalPower(SwccgGame game, String playerId) {
        if (isReachedDamageSegment())
            return 0;

        String preBattleDestinyPlayerIdToUse = isPreBattleDestinyTotalPowerSwitched() ? game.getOpponent(playerId) : playerId;

        float totalPower = game.getModifiersQuerying().getTotalPowerAtLocation(game.getGameState(), _location, preBattleDestinyPlayerIdToUse, true, false);

        // Add total destiny to power only
        totalPower += getTotalDestinyToPowerOnly(preBattleDestinyPlayerIdToUse);

        // Add total battle destiny to power
        totalPower += getTotalBattleDestiny(game, playerId);

        // Apply any modifiers after destinies are drawn
        totalPower += game.getModifiersQuerying().getTotalPowerDuringBattle(game.getGameState(), playerId, getBattleLocation());

        return Math.max(0, totalPower);
    }

    public void increaseBaseAttrition(String player, float increaseBy) {
        _baseAttrition.put(player, getBaseAttrition(player) + increaseBy);
    }

    public float getBaseAttrition(String player) {
        final Float baseAttrition = _baseAttrition.get(player);
        if (baseAttrition==null)
            return 0;
        return Math.max(0, baseAttrition);
    }

    public void setAttritionTotal(String player, float attrition) {
        Float currentAttrition = _totalAttrition.get(player);
        if (currentAttrition == null || currentAttrition > attrition) {
            _totalAttrition.put(player, attrition);
        }
    }

    public boolean hasAttritionTotal(String player) {
        return _totalBattleDestiny.get(player)!=null;
    }

    public float getAttritionTotal(SwccgGame game, String player) {
        if (_reachedDamageSegment)
            return _totalAttrition.get(player);

        if (_baseAttrition.get(player) == null)
            return 0;

        return game.getModifiersQuerying().getTotalAttrition(game.getGameState(), player);
    }

    public float getAttritionRemaining(SwccgGame game, String player) {
        if (_allAttritionSatisfied.get(player) != null)
            return 0;

        return Math.max(0, getAttritionTotal(game, player) - getAttritionSatisfied(player));
    }

    public void increaseAttritionSatisfied(String player, float increaseBy) {
        final Float previousValue = _attritionSatisfied.get(player);
        if (previousValue==null)
            _attritionSatisfied.put(player, increaseBy);
        else
            _attritionSatisfied.put(player, previousValue + increaseBy);
    }

    public void satisfyAllAttrition(String player) {
        _allAttritionSatisfied.put(player, true);
    }

    public float getAttritionSatisfied(String player) {
        final Float satisfied = _attritionSatisfied.get(player);
        if (satisfied==null)
            return 0;
        return satisfied;
    }

    public void setCurrentLoseOrForfeitEffect(BattleDamageSegmentAction.ChooseCardToLoseOrForfeitEffect effect) {
        _currentLostOrForfeitEffect = effect;
    }

    public BattleDamageSegmentAction.ChooseCardToLoseOrForfeitEffect getCurrentLoseOrForfeitEffect() {
        return _currentLostOrForfeitEffect;
    }
}
