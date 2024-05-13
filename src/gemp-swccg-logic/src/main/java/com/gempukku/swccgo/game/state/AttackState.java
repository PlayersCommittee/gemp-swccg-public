package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.SnapshotData;
import com.gempukku.swccgo.logic.timing.Snapshotable;

import java.util.*;

// This class contains the state information for an
// attack within a game of Gemp-Swccg.
//
public class AttackState implements Snapshotable<AttackState> {
    private SwccgGame _game;
    private String _playerInitiatedAttack;
    private PhysicalCard _location;
    private boolean _isNonCreatureAttackOnCreature;
    private boolean _isCreatureAttackOnNonCreature;
    private boolean _isCreaturesAttackingEachOther;
    private boolean _isParasiteAttackOnNonCreature;
    private Set<PhysicalCard> _attackingCards = new HashSet<>();
    private Set<PhysicalCard> _defendingCards = new HashSet<>();
    private String _attackingCardsOwner;
    private String _defendingCardsOwner;
    private boolean _attackStarted;
    private boolean _canceled;
    private boolean _reachedPowerSegment;
    private boolean _reachedDamageSegment;
    private Float _finalAttackerTotal;
    private Float _finalDefenderTotal;
    private boolean _attackerDefeated;
    private boolean _defenderDefeated;
    private Map<PhysicalCard, Float> _totalCreatureFerocityDestiny = new HashMap<>();
    private Map<String, Float> _totalNonCreaturePlayerAttackDestiny = new HashMap<>();

    /**
     * Needed to generate snapshot.
     */
    public AttackState() {
    }

    @Override
    public void generateSnapshot(AttackState selfSnapshot, SnapshotData snapshotData) {
        AttackState snapshot = selfSnapshot;

        // Set each field
        snapshot._game = _game;
        snapshot._playerInitiatedAttack = _playerInitiatedAttack;
        snapshot._location = snapshotData.getDataForSnapshot(_location);
        snapshot._isNonCreatureAttackOnCreature = _isNonCreatureAttackOnCreature;
        snapshot._isCreatureAttackOnNonCreature = _isCreatureAttackOnNonCreature;
        snapshot._isCreaturesAttackingEachOther = _isCreaturesAttackingEachOther;
        snapshot._isParasiteAttackOnNonCreature = _isParasiteAttackOnNonCreature;
        for (PhysicalCard card : _attackingCards) {
            snapshot._attackingCards.add(snapshotData.getDataForSnapshot(card));
        }
        for (PhysicalCard card : _defendingCards) {
            snapshot._defendingCards.add(snapshotData.getDataForSnapshot(card));
        }
        snapshot._attackingCardsOwner = _attackingCardsOwner;
        snapshot._defendingCardsOwner = _defendingCardsOwner;
        snapshot._attackStarted = _attackStarted;
        snapshot._reachedPowerSegment = _reachedPowerSegment;
        if (_reachedPowerSegment) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " after weapons segment");
        }
        snapshot._reachedDamageSegment = _reachedDamageSegment;
        snapshot._canceled = _canceled;
        for (PhysicalCard creature : _totalCreatureFerocityDestiny.keySet()) {
            snapshot._totalCreatureFerocityDestiny.put(snapshotData.getDataForSnapshot(creature), _totalCreatureFerocityDestiny.get(creature));
        }
        snapshot._totalNonCreaturePlayerAttackDestiny.putAll(_totalNonCreaturePlayerAttackDestiny);
    }

    public AttackState(SwccgGame game, String playerId, PhysicalCard location) {
        _game = game;
        _playerInitiatedAttack = playerId;
        _location = location;
    }

    public String getPlayerInitiatedAttack() {
        return _playerInitiatedAttack;
    }

    public PhysicalCard getAttackLocation() {
        return _location;
    }

    public Collection<PhysicalCard> getAllCardsParticipating() {
        Collection<PhysicalCard> allCards = new LinkedList<>();
        allCards.addAll(_attackingCards);
        allCards.addAll(_defendingCards);
        return allCards;
    }

    public Collection<PhysicalCard> getCardsAttacking() {
        return new ArrayList<>(_attackingCards);
    }

    public Collection<PhysicalCard> getCardsDefending() {
        return new ArrayList<>(_defendingCards);
    }

    public void addParticipantsForNonCreatureAttackOnCreature(Collection<PhysicalCard> nonCreatureCards, PhysicalCard creature) {
        _isNonCreatureAttackOnCreature = true;
        _attackingCards.addAll(nonCreatureCards);
        _attackingCardsOwner = nonCreatureCards.iterator().next().getOwner();
        _defendingCards.add(creature);
        _defendingCardsOwner = creature.getOwner();
    }

    public void addParticipantsForCreatureAttackOnNonCreature(PhysicalCard creature, PhysicalCard nonCreature) {
        _isCreatureAttackOnNonCreature = true;
        _isParasiteAttackOnNonCreature = Filters.parasite.accepts(_game, creature);
        _attackingCards.add(creature);
        _attackingCardsOwner = creature.getOwner();
        _defendingCards.add(nonCreature);
        _defendingCardsOwner = nonCreature.getOwner();
    }

    public void addParticipantsForCreaturesAttackingEachOther(PhysicalCard creature, PhysicalCard creature2) {
        _isCreaturesAttackingEachOther = true;
        _attackingCards.add(creature);
        _attackingCardsOwner = creature.getOwner();
        _defendingCards.add(creature2);
        _defendingCardsOwner = creature2.getOwner();
    }

    public boolean isCardParticipatingInAttack(PhysicalCard card) {
        for (PhysicalCard cardInAttack : getAllCardsParticipating())
            if (cardInAttack.getCardId() == card.getCardId())
                return true;

        return false;
    }

    public boolean isCreaturesAttackingEachOther() {
        return _isCreaturesAttackingEachOther;
    }

    public boolean isCreatureAttackingNonCreature() {
        return _isCreatureAttackOnNonCreature;
    }

    public boolean isNonCreatureAttackingCreature() {
        return _isNonCreatureAttackOnCreature;
    }

    public boolean isParasiteAttackingNonCreature() {
        return _isParasiteAttackOnNonCreature;
    }

    public String getAttackerOwner() {
        return _attackingCardsOwner;
    }

    public String getDefenderOwner() {
        return _defendingCardsOwner;
    }

    public void setFerocityDestinyTotal(PhysicalCard creature, float destiny) {
        _totalCreatureFerocityDestiny.put(creature, destiny);
    }

    public Float getFerocityDestinyTotal(PhysicalCard creature) {
        return _totalCreatureFerocityDestiny.get(creature);
    }

    public void setAttackDestinyTotal(String playerId, float destiny) {
        _totalNonCreaturePlayerAttackDestiny.put(playerId, destiny);
    }

    public Float getAttackDestinyTotal(String playerId) {
        return _totalNonCreaturePlayerAttackDestiny.get(playerId);
    }

    public void attackingCreatureDefeated() {
        _attackerDefeated = true;
    }

    public boolean isAttackingCreatureDefeated() {
        return _attackerDefeated;
    }

    public void defenderDefeated() {
        _defenderDefeated = true;
    }

    public boolean isDefenderDefeated() {
        return _defenderDefeated;
    }

    public float getFinalAttackerTotal() {
        return _finalAttackerTotal;
    }

    public void setFinalAttackerTotal(float total) {
        _finalAttackerTotal = total;
    }

    public float getFinalDefenderTotal() {
        return _finalDefenderTotal;
    }

    public void setFinalDefenderTotal(float total) {
        _finalDefenderTotal = total;
    }

    public boolean isFinalTotalsSet() {
        return _finalAttackerTotal != null && _finalDefenderTotal != null;
    }

    public void attackStarted() {
        _attackStarted = true;
    }

    public boolean isAttackStarted() {
        return _attackStarted;
    }

    public void cancel() {
        _canceled = true;
    }

    public boolean isCanceled() {
        return _canceled;
    }

    public void reachedPowerSegment() {
        _reachedPowerSegment = true;
    }

    public boolean isReachedPowerSegment() {
        return _reachedPowerSegment;
    }

    public void reachedDamageSegment() {
        _reachedDamageSegment = true;
    }

    public boolean isReachedDamageSegment() {
        return _reachedDamageSegment;
    }

    public boolean canContinue() {
        if (_canceled)
            return false;

        if (isReachedDamageSegment())
            return true;

        Collection<PhysicalCard> currentAttackingCards = Filters.filter(_attackingCards, _game, Filters.onTable);
        _attackingCards.clear();
        _attackingCards.addAll(currentAttackingCards);
        Collection<PhysicalCard> currentDefendingCards = Filters.filter(_defendingCards, _game, Filters.onTable);
        _defendingCards.clear();
        _defendingCards.addAll(currentDefendingCards);
        
        return !Filters.filter(_attackingCards, _game, Filters.or(Filters.character, Filters.creature, Filters.starship, Filters.vehicle)).isEmpty()
                && !Filters.filter(_defendingCards, _game, Filters.or(Filters.character, Filters.creature, Filters.starship, Filters.vehicle)).isEmpty();
    }
}
