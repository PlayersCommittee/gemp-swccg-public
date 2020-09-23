package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.FalseCondition;
import com.gempukku.swccgo.logic.conditions.TrueCondition;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.*;
import com.gempukku.swccgo.logic.modifiers.AddsDestinyToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFiredModifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.*;

import java.util.*;

/**
 * A utility class for building common "fire weapon" actions.
 */
public class FireWeaponActionBuilder {

    private String _playerId;
    private String _opponent;
    private SwccgGame _game;
    private PhysicalCard _sourceCard;
    private PhysicalCard _weaponOrCardWithPermanentWeapon;
    private SwccgBuiltInCardBlueprint _permanentWeapon;
    private boolean _forFree;
    private int _extraForceRequired;
    private boolean _repeatedFiring;
    private Filter _fireAtTargetFilter;
    private boolean _ignorePerAttackOrBattleLimit;
    private int _numTimesAllowedToFirePerAttack = 1;
    private int _numTimesAllowedToFirePerBattle = 1;
    private int _numTimesAllowedToFirePerAttackRun = 1;
    private boolean _noWeaponDestinyNeeded;

    private int _numTargets = 1;
    private List<Filter> _proximityFilterList = new ArrayList<Filter>();
    private List<Filter> _targetFilterList = new ArrayList<Filter>();
    private List<Set<TargetingReason>> _targetingReasonList = new ArrayList<Set<TargetingReason>>();
    private List<Boolean> _targetsForFree = new ArrayList<Boolean>();
    private List<Integer> _targetingUseForceCostMin = new ArrayList<Integer>();
    private List<Integer> _targetingUseForceCostMax = new ArrayList<Integer>();
    private List<Boolean> _targetFilterValid = new ArrayList<Boolean>();
    private Filter _targetedAsCharacter;
    private Float _defenseValueAsCharacter;

    private Map<TargetingReason, Filterable> _targetFiltersMap = new HashMap<TargetingReason, Filterable>();

    private List<PhysicalCard> _possibleWeaponUsers = new ArrayList<PhysicalCard>();
    private Set<PhysicalCard> _validWeaponUsers = new HashSet<PhysicalCard>();
    private boolean _artilleryWeaponMayFireWithoutWarriorPresent;

    private boolean _firesWithoutTargeting;
    private boolean _firesWithoutTargetingAtSameSite;
    private boolean _firesWithoutTargetingAtSameOrAdjacentSite;
    private boolean _firesWithoutTargetingAtRelatedLocation;
    private boolean _firesWithoutTargetingForFree;
    private Integer _firesWithoutTargetingUseForceCost;

    /**
     * Creates a fire weapon action builder that is used to build a fire weapon action.
     * @param playerId the player
     * @param game the game
     * @param sourceCard the card to initiate the firing
     * @param weapon the weapon card
     * @param forFree true if playing card for free, otherwise false
     * @param extraForceRequired the extra amount of Force required to perform the fire weapon
     * @param repeatedFiring true if a repeated firing, otherwise false
     * @param targetedAsCharacter filter for cards may be targeted as characters
     * @param defenseValueAsCharacter defense value to use for cards may be targeted as characters, or null
     * @param fireAtTargetFilter the filter for what the weapon can fire at
     * @param ignorePerAttackOrBattleLimit true if per attack/battle firing limit is ignored, otherwise false
     * @return the builder
     */
    public static FireWeaponActionBuilder startBuildPrep(String playerId, SwccgGame game, PhysicalCard sourceCard, PhysicalCard weapon, boolean forFree, int extraForceRequired, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        return new FireWeaponActionBuilder(playerId, game, sourceCard, weapon, null, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit);
    }

    /**
     * Creates a fire weapon action builder that is used to build a fire weapon action for a permanent weapon.
     * @param playerId the player
     * @param game the game
     * @param sourceCard the card to initiate the firing
     * @param cardWithPermanentWeapon the card with the permanent weapon
     * @param permanentWeapon the permanent weapon
     * @param forFree true if playing card for free, otherwise false
     * @param extraForceRequired the extra amount of Force required to perform the fire weapon
     * @param repeatedFiring true if a repeated firing, otherwise false
     * @param targetedAsCharacter filter for cards may be targeted as characters
     * @param defenseValueAsCharacter defense value to use for cards may be targeted as characters, or null
     * @param fireAtTargetFilter the filter for what the card can fire at
     * @param ignorePerAttackOrBattleLimit true if per attack/battle firing limit is ignored, otherwise false
     * @return the builder
     */
    public static FireWeaponActionBuilder startBuildPrep(String playerId, SwccgGame game, PhysicalCard sourceCard, PhysicalCard cardWithPermanentWeapon, SwccgBuiltInCardBlueprint permanentWeapon, boolean forFree, int extraForceRequired, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        return new FireWeaponActionBuilder(playerId, game, sourceCard, cardWithPermanentWeapon, permanentWeapon, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit);
    }

    /**
     * Builds the fire weapon action (if action can be performed) and returns the singleton action list.
     */
    public FireWeaponActionBuilder finishBuildPrep() {
        GameState gameState = _game.getGameState();
        ModifiersQuerying modifiersQuerying = _game.getModifiersQuerying();
        String weaponOwner = _permanentWeapon != null ? _permanentWeapon.getPhysicalCard(_game).getOwner() : _weaponOrCardWithPermanentWeapon.getOwner();

        // Update target filters to include cards granted to target
        for (int i = 0; i < _targetFilterList.size(); ++i) {
            if (_weaponOrCardWithPermanentWeapon != null) {
                _targetFilterList.set(i, Filters.or(_targetFilterList.get(i), Filters.grantedMayBeTargetedBy(_weaponOrCardWithPermanentWeapon)));
            }
        }

        // Check if there is a card in Reserve deck (if weapon destiny needs to be drawn)
        if (!_noWeaponDestinyNeeded && gameState.getReserveDeckSize(_playerId) == 0)
            return null;

        // Check number of times allowed to fire during battle
        if (!_repeatedFiring) {
            if (gameState.isDuringBattle()) {

                if (_numTimesAllowedToFirePerBattle < 2 && modifiersQuerying.mayBeFiredTwicePerBattle(gameState, _weaponOrCardWithPermanentWeapon)) {
                    _numTimesAllowedToFirePerBattle = 2;
                }

                if (_numTimesAllowedToFirePerBattle < 2) {
                    if (_permanentWeapon == null && !_weaponOrCardWithPermanentWeapon.getBlueprint().isFiredByCharacterPresentOrHere()) {
                        // Check if carrier can use weapon
                        PhysicalCard weaponCarrier = _weaponOrCardWithPermanentWeapon.getAttachedTo();
                        // Check that another weapon has not been fired by carrier
                        if (!Filters.canSpot(modifiersQuerying.getWeaponsFiredInBattleByWeaponUser(weaponCarrier, false), _game, Filters.not(_weaponOrCardWithPermanentWeapon))
                                && modifiersQuerying.getPermanentWeaponsFiredInBattleByWeaponUser(weaponCarrier, false).isEmpty()
                                && modifiersQuerying.mayFireWeaponTwicePerBattle(gameState, weaponCarrier, _weaponOrCardWithPermanentWeapon)) {
                            _numTimesAllowedToFirePerBattle = 2;
                        }
                    }
                }

                // Check if weapon has reach weapon firing limit during battle
                if (!_ignorePerAttackOrBattleLimit) {
                    if (_permanentWeapon != null) {
                        if (_numTimesAllowedToFirePerBattle <= modifiersQuerying.numTimesFiredInBattle(_permanentWeapon, false))
                            return null;
                    } else {
                        if (_numTimesAllowedToFirePerBattle <= modifiersQuerying.numTimesFiredInBattle(_weaponOrCardWithPermanentWeapon, false))
                            return null;
                    }
                }
            }
            if (gameState.isDuringAttack()) {

                // Check if weapon has reach weapon firing limit during attack
                if (!_ignorePerAttackOrBattleLimit) {
                    if (_permanentWeapon != null) {
                        if (_numTimesAllowedToFirePerAttack <= modifiersQuerying.numTimesFiredInAttack(_permanentWeapon, false))
                            return null;
                    } else {
                        if (_numTimesAllowedToFirePerAttack <= modifiersQuerying.numTimesFiredInAttack(_weaponOrCardWithPermanentWeapon, false))
                            return null;
                    }
                }
            }
            if (gameState.isDuringAttackRun()) {

                // Check if weapon has reach weapon firing limit during Attack Run
                if (_permanentWeapon != null) {
                    if (_numTimesAllowedToFirePerAttackRun <= modifiersQuerying.numTimesFiredInAttackRun(_permanentWeapon, false))
                        return null;
                } else {
                    if (_numTimesAllowedToFirePerAttackRun <= modifiersQuerying.numTimesFiredInAttackRun(_weaponOrCardWithPermanentWeapon, false))
                        return null;
                }
            }
        }

        // Check if card carrying weapon can use it
        if (_permanentWeapon != null) {
            if (Filters.canFireWeapon(_permanentWeapon).accepts(_game, _weaponOrCardWithPermanentWeapon)) {
                _possibleWeaponUsers.add(_weaponOrCardWithPermanentWeapon);
            }
        }
        // If weapon that is fired by a character present
        else if (_weaponOrCardWithPermanentWeapon.getBlueprint().isFiredByCharacterPresentOrHere()) {
            // Check if a card that can use weapon is found
            _possibleWeaponUsers.addAll(Filters.filterActive(_game, null, Filters.canFireWeapon(_weaponOrCardWithPermanentWeapon)));
        }
        else {
            // Check if carrier can use weapon
            PhysicalCard weaponCarrier = _weaponOrCardWithPermanentWeapon.getAttachedTo();
            if (weaponCarrier == null || Filters.canFireWeapon(_weaponOrCardWithPermanentWeapon).accepts(_game, weaponCarrier)) {
                _possibleWeaponUsers.add(weaponCarrier);
            }
        }

        _artilleryWeaponMayFireWithoutWarriorPresent = Filters.artillery_weapon.accepts(_game, _weaponOrCardWithPermanentWeapon)
                && modifiersQuerying.mayFireArtilleryWeaponWithoutWarriorPresent(gameState, _weaponOrCardWithPermanentWeapon);

        if (_possibleWeaponUsers.isEmpty() && !_artilleryWeaponMayFireWithoutWarriorPresent) {
            return null;
        }

        int forceAvailableToUse = modifiersQuerying.getForceAvailableToUse(gameState, _playerId);

        if (!_firesWithoutTargeting) {

            // If during attack, only creatures participating in attack can be targeted
            Filter inAttackFilter = gameState.isDuringAttack() ? Filters.and(Filters.creature, Filters.participatingInAttack) : Filters.any;

            // If during battle, only cards participating in battle can be targeted
            Filter inBattleFilter = gameState.isDuringBattle() ? Filters.participatingInBattle : Filters.any;

            for (int i = 0; i < _targetFilterList.size(); ++i) {
                boolean isValid = false;
                Filter proximityFilter = _proximityFilterList.get(i);

                // Can only fire into an attack if it is that player's non-creature attack on a creature
                if (!gameState.isDuringAttack()
                        || (gameState.isDuringNonCreatureAttackOnCreature()
                        && gameState.getAttackState().getAttackerOwner().equals(weaponOwner))) {

                    // Check if weapon may target at other sites as well
                    if (_permanentWeapon != null) {
                        if (modifiersQuerying.canWeaponTargetTwoSitesAway(gameState, _permanentWeapon)) {
                            proximityFilter = Filters.or(proximityFilter, Filters.presentAt(Filters.siteWithinDistance(_weaponOrCardWithPermanentWeapon, 2)));
                            _proximityFilterList.set(i, proximityFilter);
                        }
                        if (modifiersQuerying.canWeaponTargetAdjacentSite(gameState, _permanentWeapon)) {
                            proximityFilter = Filters.or(proximityFilter, Filters.presentAt(Filters.adjacentSite(_weaponOrCardWithPermanentWeapon)));
                            _proximityFilterList.set(i, proximityFilter);
                        }
                        if (modifiersQuerying.canWeaponTargetNearestRelatedExteriorSite(gameState, _permanentWeapon)) {
                            proximityFilter = Filters.or(proximityFilter, Filters.presentAt(Filters.nearestRelatedExteriorSite(_weaponOrCardWithPermanentWeapon)));
                            _proximityFilterList.set(i, proximityFilter);
                        }
                    } else {
                        if (modifiersQuerying.canWeaponTargetTwoSitesAway(gameState, _weaponOrCardWithPermanentWeapon)) {
                            proximityFilter = Filters.or(proximityFilter, Filters.presentAt(Filters.siteWithinDistance(_weaponOrCardWithPermanentWeapon, 2)));
                            _proximityFilterList.set(i, proximityFilter);
                        }
                        if (modifiersQuerying.canWeaponTargetAdjacentSite(gameState, _weaponOrCardWithPermanentWeapon)) {
                            proximityFilter = Filters.or(proximityFilter, Filters.presentAt(Filters.adjacentSite(_weaponOrCardWithPermanentWeapon)));
                            _proximityFilterList.set(i, proximityFilter);
                        }
                        if (modifiersQuerying.canWeaponTargetNearestRelatedExteriorSite(gameState, _weaponOrCardWithPermanentWeapon)) {
                            proximityFilter = Filters.or(proximityFilter, Filters.presentAt(Filters.nearestRelatedExteriorSite(_weaponOrCardWithPermanentWeapon)));
                            _proximityFilterList.set(i, proximityFilter);
                        }
                    }
                }

                // Set filter to use for remaining checking
                Filterable newTargetFilterable = Filters.and(inAttackFilter, inBattleFilter, _targetFilterList.get(i), _proximityFilterList.get(i), _fireAtTargetFilter);

                Set<TargetingReason> targetingReasons = _targetingReasonList.get(i);
                for (TargetingReason targetingReason : targetingReasons) {
                    newTargetFilterable = Filters.and(newTargetFilterable, Filters.canBeTargetedBy(_weaponOrCardWithPermanentWeapon, _permanentWeapon, targetingReason));
                }
                
                // Check if Force can be used to target
                if (_repeatedFiring) {

                    if (getUseForceCost(null) <= forceAvailableToUse) {
                        for (PhysicalCard possibleWeaponUser : _possibleWeaponUsers) {
                            // Check if valid target can be found
                            if (Filters.canSpot(_game, _sourceCard, _numTargets, null, targetingReasons, Filters.and(newTargetFilterable, Filters.canBeTargetedByWeaponUser(possibleWeaponUser)))
                                    || Filters.canSpotFromStacked(_game, _numTargets, Filters.and(newTargetFilterable, Filters.canBeTargetedByWeaponUser(possibleWeaponUser)))) {
                                isValid = true;
                                _validWeaponUsers.add(possibleWeaponUser);
                            }
                        }
                    }
                }
                else if (_forFree || _targetsForFree.get(i)) {

                    if (_extraForceRequired <= forceAvailableToUse) {
                        for (PhysicalCard possibleWeaponUser : _possibleWeaponUsers) {
                            // Check if valid target can be found
                            if (Filters.canSpot(_game, _sourceCard, _numTargets, null, targetingReasons, Filters.and(newTargetFilterable, Filters.canBeTargetedByWeaponUser(possibleWeaponUser)))
                                    || Filters.canSpotFromStacked(_game, _numTargets, Filters.and(newTargetFilterable, Filters.canBeTargetedByWeaponUser(possibleWeaponUser)))) {
                                isValid = true;
                                _validWeaponUsers.add(possibleWeaponUser);
                            }
                        }
                    }
                }
                else {

                    Set<PhysicalCard> validTargets = new HashSet<PhysicalCard>();
                    Collection<PhysicalCard> possibleTargets = Filters.filterActive(_game, _sourceCard, null, targetingReasons, newTargetFilterable);
                    possibleTargets = new LinkedList<PhysicalCard>(possibleTargets);
                    // Also include stacked cards that can be targeted by weapons as if present
                    possibleTargets.addAll(Filters.filter(Filters.filterStacked(_game, newTargetFilterable), _game, Filters.canBeTargetedByWeaponAsIfPresent));

                    for (PhysicalCard possibleTarget : possibleTargets) {
                        for (PhysicalCard possibleWeaponUser : _possibleWeaponUsers) {
                            if (Filters.canBeTargetedByWeaponUser(possibleWeaponUser).accepts(gameState, modifiersQuerying, possibleTarget)) {
                                float firingCost;
                                if (_permanentWeapon != null)
                                    firingCost = modifiersQuerying.getFireWeaponCost(gameState, _permanentWeapon, possibleWeaponUser, possibleTarget, _targetingUseForceCostMin.get(i));
                                else
                                    firingCost = modifiersQuerying.getFireWeaponCost(gameState, _weaponOrCardWithPermanentWeapon, possibleWeaponUser, possibleTarget, _targetingUseForceCostMin.get(i));

                                if ((firingCost + _extraForceRequired) <= forceAvailableToUse) {
                                    validTargets.add(possibleTarget);
                                    _validWeaponUsers.add(possibleWeaponUser);
                                }
                            }
                        }
                    }
                    _targetFilterList.set(i, Filters.in(validTargets));
                    isValid = (validTargets.size() >= _numTargets);
                }
                _targetFilterValid.add(isValid);
            }

            if (_validWeaponUsers.isEmpty() && !_artilleryWeaponMayFireWithoutWarriorPresent) {
                return null;
            }

            for (int i = 0; i < _targetFilterList.size(); ++i) {
                if (_targetFilterValid.get(i)) {
                    Filterable newTargetFilterable = Filters.and(inBattleFilter, _targetFilterList.get(i), _proximityFilterList.get(i), _fireAtTargetFilter);

                    Filterable validForUserToTargetFilter = Filters.none;
                    for (PhysicalCard validWeaponUser : _validWeaponUsers) {
                        validForUserToTargetFilter = Filters.or(validForUserToTargetFilter, Filters.canBeTargetedByWeaponUser(validWeaponUser));
                    }
                    Set<TargetingReason> targetingReasons = _targetingReasonList.get(i);
                    for (TargetingReason targetingReason : targetingReasons) {

                        Filter filterToAddToMap = Filters.and(newTargetFilterable, validForUserToTargetFilter, Filters.canBeTargetedBy(_weaponOrCardWithPermanentWeapon, _permanentWeapon, targetingReason));

                        Filterable targetFilterable = _targetFiltersMap.get(targetingReason);
                        if (targetFilterable == null) {
                            _targetFiltersMap.put(targetingReason, filterToAddToMap);
                        } else {
                            _targetFiltersMap.put(targetingReason, Filters.or(targetFilterable, filterToAddToMap));
                        }
                    }
                }
            }

            if (_targetFiltersMap.isEmpty()) {
                return null;
            }
        }
        else {
            if (_repeatedFiring) {
                if (forceAvailableToUse < getUseForceCost(null)) {
                    return null;
                }
                _validWeaponUsers.addAll(_possibleWeaponUsers);
            }
            else if (!_forFree && !_firesWithoutTargetingForFree) {
                for (PhysicalCard possibleWeaponUser : _possibleWeaponUsers) {
                    if (forceAvailableToUse >= (getUseForceCost(possibleWeaponUser) + _extraForceRequired)) {
                        _validWeaponUsers.add(possibleWeaponUser);
                    }
                }
            }
            else if (forceAvailableToUse >= _extraForceRequired) {
                _validWeaponUsers.addAll(_possibleWeaponUsers);
            }

            if (_validWeaponUsers.isEmpty() && !_artilleryWeaponMayFireWithoutWarriorPresent) {
                return null;
            }

            if (gameState.isDuringBattle()) {

                if (_firesWithoutTargetingAtSameOrAdjacentSite) {
                    if (!Filters.atSameOrAdjacentSite(gameState.getBattleLocation()).accepts(_game, _weaponOrCardWithPermanentWeapon)) {
                        return null;
                    }
                }
                else if (_firesWithoutTargetingAtSameSite) {
                    if (!Filters.atSameSite(gameState.getBattleLocation()).accepts(_game, _weaponOrCardWithPermanentWeapon)) {
                        return null;
                    }
                }
                else if (_firesWithoutTargetingAtRelatedLocation) {
                    if (!Filters.relatedLocation(gameState.getBattleLocation()).accepts(_game, _weaponOrCardWithPermanentWeapon)) {
                        return null;
                    }
                }
                else {
                    if (!gameState.isParticipatingInBattle(_weaponOrCardWithPermanentWeapon)) {
                        return null;
                    }
                }
            }

            if (gameState.isDuringAttack()) {

                // Can only fire into an attack if it is that player's non-creature attack on a creature
                if (gameState.getAttackState().isNonCreatureAttackingCreature()
                        && gameState.getAttackState().getAttackerOwner().equals(weaponOwner)) {

                    if (_firesWithoutTargetingAtSameOrAdjacentSite) {
                        if (!Filters.atSameOrAdjacentSite(gameState.getAttackLocation()).accepts(_game, _weaponOrCardWithPermanentWeapon)) {
                            return null;
                        }
                    } else if (_firesWithoutTargetingAtSameSite) {
                        if (!Filters.atSameSite(gameState.getAttackLocation()).accepts(_game, _weaponOrCardWithPermanentWeapon)) {
                            return null;
                        }
                    } else if (_firesWithoutTargetingAtRelatedLocation) {
                        if (!Filters.relatedLocation(gameState.getAttackLocation()).accepts(_game, _weaponOrCardWithPermanentWeapon)) {
                            return null;
                        }
                    }
                }
                else {
                    if (!gameState.isParticipatingInAttack(_weaponOrCardWithPermanentWeapon)) {
                        return null;
                    }
                }
            }
        }

        return this;
    }

    /**
     * Creates a fire weapon action builder that is used to build a fire weapon action for a weapon (or permanent weapon).
     * @param playerId the player
     * @param game the game
     * @param sourceCard the card to initiate the firing
     * @param weaponOrCardWithPermanentWeapon the card
     * @param permanentWeapon the permanent weapon
     * @param forFree true if playing card for free, otherwise false
     * @param extraForceRequired the extra amount of Force required to perform the fire weapon
     * @param repeatedFiring true if a repeated firing, otherwise false
     * @param targetedAsCharacter filter for cards may be targeted as characters
     * @param defenseValueAsCharacter defense value to use for cards may be targeted as characters, or null
     * @param fireAtTargetFilter the filter for where the card can be played
     * @param ignorePerAttackOrBattleLimit true if per attack/battle firing limit is ignored, otherwise false
     */
    private FireWeaponActionBuilder(String playerId, SwccgGame game, PhysicalCard sourceCard, PhysicalCard weaponOrCardWithPermanentWeapon, SwccgBuiltInCardBlueprint permanentWeapon, boolean forFree, int extraForceRequired, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        _playerId = playerId;
        _opponent = game.getOpponent(playerId);
        _game = game;
        _sourceCard = sourceCard;
        _weaponOrCardWithPermanentWeapon = weaponOrCardWithPermanentWeapon;
        _permanentWeapon = permanentWeapon;
        _forFree = forFree;
        _extraForceRequired = extraForceRequired;
        _repeatedFiring = repeatedFiring;
        _targetedAsCharacter = targetedAsCharacter;
        _defenseValueAsCharacter = defenseValueAsCharacter;
        _fireAtTargetFilter = fireAtTargetFilter;
        _ignorePerAttackOrBattleLimit = ignorePerAttackOrBattleLimit;
    }


    //================================================================================
    // Usage
    //================================================================================

    /**
     * Sets that the weapon can be fired twice per battle.
     * @return the builder
     */
    public FireWeaponActionBuilder twicePerBattle() {
        _numTimesAllowedToFirePerBattle = 2;
        return this;
    }

    //================================================================================
    // Targeting and Cost
    //================================================================================

    /**
     * Sets that the weapon does not draw weapon destiny when fired.
     * @return the builder
     */
    public FireWeaponActionBuilder noWeaponDestinyNeeded() {
        _noWeaponDestinyNeeded = true;
        return this;
    }

    /**
     * Sets that the weapon does not target specific cards and does not specify a cost to fire.
     * @return the builder
     */
    public FireWeaponActionBuilder firesWithoutTargeting() {
        _firesWithoutTargeting = true;
        _firesWithoutTargetingUseForceCost = 0;
        return this;
    }

    /**
     * Sets that the weapon does not target specific cards and does not specify a cost to fire at same site.
     * @return the builder
     */
    public FireWeaponActionBuilder firesWithoutTargetingAtSameSite() {
        _firesWithoutTargeting = true;
        _firesWithoutTargetingAtSameSite = true;
        _firesWithoutTargetingUseForceCost = 0;
        return this;
    }

    /**
     * Sets that the weapon does not target specific cards and does not specify a cost to fire at same or adjacent site.
     * @return the builder
     */
    public FireWeaponActionBuilder firesWithoutTargetingAtSameOrAdjacentSite() {
        _firesWithoutTargeting = true;
        _firesWithoutTargetingAtSameOrAdjacentSite = true;
        _firesWithoutTargetingUseForceCost = 0;
        return this;
    }

    /**
     * Sets that the weapon does not target specific cards when fired and fires for free.
     * @return the builder
     */
    public FireWeaponActionBuilder firesForFreeWithoutTargeting() {
        _firesWithoutTargeting = true;
        _firesWithoutTargetingForFree = true;
        _firesWithoutTargetingUseForceCost = 0;
        return this;
    }

    /**
     * Sets that the weapon does not target specific cards when fired and has a cost to fire.
     * @return the builder
     */
    public FireWeaponActionBuilder firesUsingForceWithoutTargeting(int useForceCost) {
        _firesWithoutTargeting = true;
        _firesWithoutTargetingUseForceCost = useForceCost;
        return this;
    }

    /**
     * Sets the targeting info when the weapon does not specify a cost.
     * @param targetFilter the target filter
     * @param targetingReason the targeting reason
     * @return the builder
     */
    public FireWeaponActionBuilder target(Filter targetFilter, TargetingReason targetingReason) {
        return targetUsingForce(targetFilter, 0, targetingReason);
    }

    /**
     * Sets the targeting info when the weapon fires for free.
     * @param targetFilter the target filter
     * @param targetingReason the targeting reason
     * @return the builder
     */
    public FireWeaponActionBuilder targetForFree(Filter targetFilter, TargetingReason targetingReason) {
        Filter presentAt = Filters.presentAt(Filters.wherePresent(_weaponOrCardWithPermanentWeapon));
        _proximityFilterList.add(Filters.or(presentAt,
                Filters.and(Filters.stackedOn(_weaponOrCardWithPermanentWeapon, presentAt), Filters.canBeTargetedByWeaponAsIfPresent)));
        _targetFilterList.add(Filters.and(Filters.or(Filters.opponents(_weaponOrCardWithPermanentWeapon), Filters.creature), targetFilter));
        _targetsForFree.add(true);
        _targetingUseForceCostMin.add(0);
        _targetingUseForceCostMax.add(0);
        _targetingReasonList.add(Collections.singleton(targetingReason));
        return this;
    }

    /**
     * Sets the targeting info when the weapon has a cost to fire.
     * @param targetFilter the target filter
     * @param useForceCost the Force to use
     * @param targetingReason the targeting reason
     * @return the builder
     */
    public FireWeaponActionBuilder targetUsingForce(Filter targetFilter, int useForceCost, TargetingReason targetingReason) {
        return targetUsingForce(1, targetFilter, useForceCost, targetingReason);
    }

    /**
     * Sets the targeting info when the weapon has a cost to fire.
     * @param targetFilter the target filter
     * @param numTargets the number of targets
     * @param useForceCost the Force to use
     * @param targetingReason the targeting reason
     * @return the builder
     */
    public FireWeaponActionBuilder targetUsingForce(int numTargets, Filter targetFilter, int useForceCost, TargetingReason targetingReason) {
        _numTargets = numTargets;
        Filter presentAt = Filters.presentAt(Filters.wherePresent(_weaponOrCardWithPermanentWeapon));
        _proximityFilterList.add(Filters.or(presentAt,
                Filters.and(Filters.stackedOn(_weaponOrCardWithPermanentWeapon, presentAt), Filters.canBeTargetedByWeaponAsIfPresent)));
        _targetFilterList.add(Filters.and(Filters.opponents(_weaponOrCardWithPermanentWeapon), targetFilter));
        _targetsForFree.add(false);
        _targetingUseForceCostMin.add(useForceCost);
        _targetingUseForceCostMax.add(useForceCost);
        _targetingReasonList.add(Collections.singleton(targetingReason));
        return this;
    }

    /**
     * Sets the targeting info when the weapon has a range of costs to fire.
     * @param targetFilter the target filter
     * @param useForceCostMin the lower limit of range of Force to use
     * @param useForceCostMax the upper limit of range of Force to use
     * @param targetingReason the targeting reason
     * @return the builder
     */
    public FireWeaponActionBuilder targetUsingForceRange(Filter targetFilter, int useForceCostMin, int useForceCostMax, TargetingReason targetingReason) {
        Filter presentAt = Filters.presentAt(Filters.wherePresent(_weaponOrCardWithPermanentWeapon));
        _proximityFilterList.add(Filters.or(presentAt,
                Filters.and(Filters.stackedOn(_weaponOrCardWithPermanentWeapon, presentAt), Filters.canBeTargetedByWeaponAsIfPresent)));
        _targetFilterList.add(Filters.and(Filters.opponents(_weaponOrCardWithPermanentWeapon), targetFilter));
        _targetsForFree.add(false);
        _targetingUseForceCostMin.add(useForceCostMin);
        _targetingUseForceCostMax.add(useForceCostMax);
        _targetingReasonList.add(Collections.singleton(targetingReason));
        return this;
    }

    /**
     * Sets the targeting info when the weapon fires for free and can target at same or adjacent site.
     * @param targetFilter the target filter
     * @param targetingReason the targeting reason
     * @return the builder
     */
    public FireWeaponActionBuilder targetAtSameOrAdjacentSiteForFree(Filter targetFilter, TargetingReason targetingReason) {
        Filter presentAt = Filters.presentAt(Filters.sameOrAdjacentSite(_weaponOrCardWithPermanentWeapon));
        _proximityFilterList.add(Filters.or(presentAt,
                Filters.and(Filters.stackedOn(_weaponOrCardWithPermanentWeapon, presentAt), Filters.canBeTargetedByWeaponAsIfPresent)));
        _targetFilterList.add(Filters.and(Filters.opponents(_weaponOrCardWithPermanentWeapon), targetFilter));
        _targetsForFree.add(true);
        _targetingUseForceCostMin.add(0);
        _targetingUseForceCostMax.add(0);
        _targetingReasonList.add(Collections.singleton(targetingReason));
        return this;
    }

    /**
     * Sets the targeting info when the weapon fires for free and can target at same site or exterior sites up to 2 sites away.
     * @param targetFilter the target filter
     * @param targetingReason the targeting reason
     * @return the builder
     */
    public FireWeaponActionBuilder targetAtSameSiteOrUpToTwoExteriorSitesAwayForFree(Filter targetFilter, TargetingReason targetingReason) {
        Filter presentAt = Filters.presentAt(Filters.or(Filters.sameSite(_weaponOrCardWithPermanentWeapon),
                Filters.and(Filters.exterior_site, Filters.siteWithinDistance(_weaponOrCardWithPermanentWeapon, 2))));
        _proximityFilterList.add(Filters.or(presentAt,
                Filters.and(Filters.stackedOn(_weaponOrCardWithPermanentWeapon, presentAt), Filters.canBeTargetedByWeaponAsIfPresent)));
        _targetFilterList.add(Filters.and(Filters.opponents(_weaponOrCardWithPermanentWeapon), targetFilter));
        _targetsForFree.add(true);
        _targetingUseForceCostMin.add(0);
        _targetingUseForceCostMax.add(0);
        _targetingReasonList.add(Collections.singleton(targetingReason));
        return this;
    }


    /**
     * Sets the targeting info when the weapon has a cost to fire and can target at same or adjacent site.
     * @param targetFilter the target filter
     * @param useForceCost the Force to use
     * @param targetingReason the targeting reason
     * @return the builder
     */
    public FireWeaponActionBuilder targetAtSameOrAdjacentSiteUsingForce(Filter targetFilter, int useForceCost, TargetingReason targetingReason) {
        Filter presentAt = Filters.presentAt(Filters.sameOrAdjacentSite(_weaponOrCardWithPermanentWeapon));
        _proximityFilterList.add(Filters.or(presentAt,
                Filters.and(Filters.stackedOn(_weaponOrCardWithPermanentWeapon, presentAt), Filters.canBeTargetedByWeaponAsIfPresent)));
        _targetFilterList.add(Filters.and(Filters.opponents(_weaponOrCardWithPermanentWeapon), targetFilter));
        _targetsForFree.add(false);
        _targetingUseForceCostMin.add(useForceCost);
        _targetingUseForceCostMax.add(useForceCost);
        _targetingReasonList.add(Collections.singleton(targetingReason));
        return this;
    }

    /**
     * Sets the targeting info when the weapon fires for free and can target at same system or orbited system.
     * @param targetFilter the target filter
     * @param targetingReason the targeting reason
     * @return the builder
     */
    public FireWeaponActionBuilder targetAtSameSystemOrSystemOrbitedForFree(Filter targetFilter, TargetingReason targetingReason) {
        Filter sameSystem = Filters.sameSystem(_weaponOrCardWithPermanentWeapon);
        Filter presentAt = Filters.presentAt(Filters.or(sameSystem, Filters.isOrbitedBy(sameSystem)));
        _proximityFilterList.add(Filters.or(presentAt,
                Filters.and(Filters.stackedOn(_weaponOrCardWithPermanentWeapon, presentAt), Filters.canBeTargetedByWeaponAsIfPresent)));
        _targetFilterList.add(Filters.and(Filters.opponents(_weaponOrCardWithPermanentWeapon), targetFilter));
        _targetsForFree.add(true);
        _targetingUseForceCostMin.add(0);
        _targetingUseForceCostMax.add(0);
        _targetingReasonList.add(Collections.singleton(targetingReason));
        return this;
    }

    /**
     * Sets the targeting info when the weapon has a cost to fire and can target at same system or orbited system.
     * @param targetFilter the target filter
     * @param useForceCost the Force to use
     * @param targetingReason the targeting reason
     * @return the builder
     */
    public FireWeaponActionBuilder targetAtSameSystemOrSystemOrbitedUsingForce(Filter targetFilter, int useForceCost, TargetingReason targetingReason) {
        Filter sameSystem = Filters.sameSystem(_weaponOrCardWithPermanentWeapon);
        Filter presentAt = Filters.presentAt(Filters.or(sameSystem, Filters.isOrbitedBy(sameSystem)));
        _proximityFilterList.add(Filters.or(presentAt,
                Filters.and(Filters.stackedOn(_weaponOrCardWithPermanentWeapon, presentAt), Filters.canBeTargetedByWeaponAsIfPresent)));
        _targetFilterList.add(Filters.and(Filters.opponents(_weaponOrCardWithPermanentWeapon), targetFilter));
        _targetsForFree.add(false);
        _targetingUseForceCostMin.add(useForceCost);
        _targetingUseForceCostMax.add(useForceCost);
        _targetingReasonList.add(Collections.singleton(targetingReason));
        return this;
    }

    /**
     * Sets the targeting info when the weapon has a cost to fire and can target at related system.
     * @param targetFilter the target filter
     * @param useForceCost the Force to use
     * @param targetingReason the targeting reason
     * @return the builder
     */
    public FireWeaponActionBuilder targetAtRelatedSystemUsingForce(Filter targetFilter, int useForceCost, TargetingReason targetingReason) {
        Filter presentAt = Filters.presentAt(Filters.relatedSystem(_weaponOrCardWithPermanentWeapon));
        _proximityFilterList.add(Filters.or(presentAt,
                Filters.and(Filters.stackedOn(_weaponOrCardWithPermanentWeapon, presentAt), Filters.canBeTargetedByWeaponAsIfPresent)));
        _targetFilterList.add(Filters.and(Filters.opponents(_weaponOrCardWithPermanentWeapon), targetFilter));
        _targetsForFree.add(false);
        _targetingUseForceCostMin.add(useForceCost);
        _targetingUseForceCostMax.add(useForceCost);
        _targetingReasonList.add(Collections.singleton(targetingReason));
        return this;
    }

    /**
     * Sets the targeting info when the weapon at related interior sites.
     * @param targetingReason the targeting reason
     * @return the builder
     */
    public FireWeaponActionBuilder targetRelatedInteriorSite(TargetingReason targetingReason) {
        _proximityFilterList.add(Filters.and(Filters.interior_site, Filters.relatedSite(_weaponOrCardWithPermanentWeapon)));
        _targetFilterList.add(Filters.interior_site);
        _targetsForFree.add(false);
        _targetingUseForceCostMin.add(0);
        _targetingUseForceCostMax.add(0);
        _targetingReasonList.add(Collections.singleton(targetingReason));
        return this;
    }


    /*    public FireWeaponActionBuilder targetForFreeUsingDefenseValue(Filter targetFilter, int defenseValue) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public FireWeaponActionBuilder targetUsingForce(Filter targetFilter, Evaluator useForceCostEvaluator) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public FireWeaponActionBuilder targetUsingForce(Filter targetFilter, int useForceCost, Filter forFreeWhenFiredByFilter) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public FireWeaponActionBuilder targetUsingVariableForce(Filter targetFilter, int useForceRangeStart, int useForceRangeEnd, Variable variable) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public FireWeaponActionBuilder targetAtSameOrAdjacentSite(Filter targetFilter, int useForceCost) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public FireWeaponActionBuilder targetAtSameOrAdjacentSiteForFree(Filter targetFilter) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public FireWeaponActionBuilder targetAtSameOrAdjacentSiteUsingDefenseValue(Filter targetFilter, int useForceCost, int defenseValue) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public FireWeaponActionBuilder targetAtSameSiteOrExteriorSitesUpToTwoAwayForFree(Filter targetFilter) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public FireWeaponActionBuilder targetAtSameOrAdjacentSite(Filter targetFilter, int useForceCost, Filter alternateCostWhenFiredByFilter, int alternateCost) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    */

    /**
     * Gets the target filters map.
     * @param cardFiringWeapon the card firing the weapon
     */
    public Map<TargetingReason, Filterable> getTargetFiltersMap(PhysicalCard cardFiringWeapon) {
        if (cardFiringWeapon == null) {
            return _targetFiltersMap;
        }

        Map<TargetingReason, Filterable> mapToReturn = new HashMap<TargetingReason, Filterable>();
        for (TargetingReason targetingReason : _targetFiltersMap.keySet()) {
            mapToReturn.put(targetingReason, Filters.and(_targetFiltersMap.get(targetingReason), Filters.canBeTargetedByWeaponUser(cardFiringWeapon)));
        }
        return mapToReturn;
    }

    /**
     * Gets the valid weapon users (for when weapon is fired by a character present).
     */
    public Collection<PhysicalCard> getValidWeaponUsers() {
        return _validWeaponUsers;
    }

    /**
     * Gets the fire weapon (use Force) cost when targeting specified cards.
     * @param weaponUser the card firing the weapon
     * @param cardTargeted the card targeted
     * @return the use Force cost
     */
    public float getUseForceCost(PhysicalCard weaponUser, PhysicalCard cardTargeted) {
        return getUseForceCost(weaponUser, Collections.singletonList(cardTargeted));
    }

    /**
     * Gets the fire weapon (use Force) cost when targeting specified cards.
     * @param weaponUser the card firing the weapon
     * @param cardsTargeted the cards targeted
     * @return the use Force cost
     */
    public float getUseForceCost(PhysicalCard weaponUser, Collection<PhysicalCard> cardsTargeted) {
        if (_repeatedFiring)
            return _game.getModifiersQuerying().getFireWeaponRepeatedlyCost(_game.getGameState(), _weaponOrCardWithPermanentWeapon);

        if (_forFree)
            return 0;

        float firingCost = 0;

        for (PhysicalCard cardTargeted : cardsTargeted) {
            for (int i = 0; i < _targetFilterList.size(); ++i) {

                if (_targetFilterList.get(i).accepts(_game, cardTargeted)) {
                    if (!_targetsForFree.get(i)) {
                        if (_permanentWeapon != null)
                            firingCost = Math.max(firingCost, _game.getModifiersQuerying().getFireWeaponCost(_game.getGameState(), _permanentWeapon, weaponUser, cardTargeted, _targetingUseForceCostMin.get(i)));
                        else
                            firingCost = Math.max(firingCost, _game.getModifiersQuerying().getFireWeaponCost(_game.getGameState(), _weaponOrCardWithPermanentWeapon, weaponUser, cardTargeted, _targetingUseForceCostMin.get(i)));
                    }
                }
            }
        }
        return firingCost;
    }

    /**
     * Gets the minimum fire weapon (use Force) cost in range when targeting a specified card.
     * @param cardTargeted the card targeted
     * @return the use Force cost
     */
    public int getUseForceCostRangeMin(PhysicalCard cardTargeted) {
        if (_forFree)
            return 0;

        for (int i=0; i<_targetFilterList.size(); ++i) {

            if (_targetFilterList.get(i).accepts(_game, cardTargeted)) {
                return _targetingUseForceCostMin.get(i);
            }
        }
        throw new UnsupportedOperationException("Should not get here");
    }

    /**
     * Gets the maximum fire weapon (use Force) cost in range when targeting a specified card.
     * @param cardTargeted the card targeted
     * @return the use Force cost
     */
    public int getUseForceCostRangeMax(PhysicalCard cardTargeted) {
        if (_forFree)
            return 0;

        for (int i=0; i<_targetFilterList.size(); ++i) {

            if (_targetFilterList.get(i).accepts(_game, cardTargeted)) {
                return _targetingUseForceCostMax.get(i);
            }
        }
        throw new UnsupportedOperationException("Should not get here");
    }

    /**
     * Gets the fire weapon (use Force) cost when no card is targeted.
     * @param weaponUser the card firing the weapon
     * @return the use Force cost
     */
    public float getUseForceCost(PhysicalCard weaponUser) {
        GameState gameState = _game.getGameState();
        ModifiersQuerying modifiersQuerying = _game.getModifiersQuerying();

        if (_repeatedFiring)
            return modifiersQuerying.getFireWeaponRepeatedlyCost(gameState, _weaponOrCardWithPermanentWeapon);

        if (_forFree || _firesWithoutTargetingForFree)
            return 0;

        float firingCost;
        if (_permanentWeapon != null)
            firingCost = modifiersQuerying.getFireWeaponCost(gameState, _permanentWeapon, weaponUser, null, _firesWithoutTargetingUseForceCost);
        else
            firingCost = modifiersQuerying.getFireWeaponCost(gameState, _weaponOrCardWithPermanentWeapon, weaponUser, null, _firesWithoutTargetingUseForceCost);

        return firingCost;
    }

    /**
     * Builds a fire weapon action that targets a card present with the weapon, draws destiny, and if successful affects the card's attributes.
     * @param numDestiny the number of weapon destiny to draw
     * @param statistic the statistic to compare total weapon destiny against
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponWithHitAction(final int numDestiny, final Statistic statistic) {
        return buildFireWeaponWithHitAction(numDestiny, 0, statistic, new FalseCondition(), false, 0, 0, Filters.none, 0, Filters.none, 0, Filters.none, 0);
    }

    /**
     * Builds a fire weapon action that targets a card present with the weapon, draws destiny, and if successful affects the card's attributes.
     * @param numDestiny the number of weapon destiny to draw
     * @param plusOrMinus the amount to add to or subtract from total destiny during the calculation
     * @param statistic the statistic to compare total weapon destiny against
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponWithHitAction(final int numDestiny, final int plusOrMinus, final Statistic statistic) {
        return buildFireWeaponWithHitAction(numDestiny, plusOrMinus, statistic, new FalseCondition(), false, 0, 0, Filters.none, 0, Filters.none, 0, Filters.none, 0);
    }

    /**
     * Builds a fire weapon action that targets a card present with the weapon, draws destiny, and if successful affects the card's attributes.
     *
     * @param numDestiny                  the number of weapon destiny to draw
     * @param statistic                   the statistic to compare total weapon destiny against
     * @param resetForfeit                true if the target's forfeit is reset if
     * @param forfeitModifierOrResetValue the amount target's forfeit is modified by (or reset to if resetForfeit is true)
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponWithHitAction(final int numDestiny, final Statistic statistic, final boolean resetForfeit, final int forfeitModifierOrResetValue) {
        return buildFireWeaponWithHitAction(numDestiny, 0, statistic, new TrueCondition(), resetForfeit, forfeitModifierOrResetValue, 0, Filters.none, 0, Filters.none, 0, Filters.none, 0);
    }

    /**
     * Builds a fire weapon action that targets a card present with the weapon, draws destiny, and if successful affects the card's attributes.
     *
     * @param numDestiny                  the number of weapon destiny to draw
     * @param statistic                   the statistic to compare total weapon destiny against
     * @param resetForfeit                true if the target's forfeit is reset if
     * @param forfeitModifierOrResetValue the amount target's forfeit is modified by (or reset to if resetForfeit is true)
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponWithHitAction(final int numDestiny, final Statistic statistic, final boolean resetForfeit, final int forfeitModifierOrResetValue, final int powerModifierValue) {
        return buildFireWeaponWithHitAction(numDestiny, 0, statistic, new TrueCondition(), resetForfeit, forfeitModifierOrResetValue, powerModifierValue, Filters.none, 0, Filters.none, 0, Filters.none, 0);
    }

    /**
     * Builds a fire weapon action that targets a card present with the weapon, draws destiny, and if successful affects the card's attributes.
     * @param numDestiny the number of weapon destiny to draw
     * @param plusOrMinus the amount to add to or subtract from total destiny during the calculation
     * @param statistic the statistic to compare total weapon destiny against
     * @param resetForfeit true if the target's forfeit is reset if
     * @param forfeitModifierOrResetValue the amount target's forfeit is modified by (or reset to if resetForfeit is true)
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponWithHitAction(final int numDestiny, final int plusOrMinus, final Statistic statistic, final boolean resetForfeit, final int forfeitModifierOrResetValue) {
        return buildFireWeaponWithHitAction(numDestiny, plusOrMinus, statistic, new TrueCondition(), resetForfeit, forfeitModifierOrResetValue, 0, Filters.none, 0, Filters.none, 0, Filters.none, 0);
    }

    /**
     * Builds a fire weapon action that targets a card present with the weapon, draws destiny, and if successful affects the card's attributes.
     * @param numDestiny the number of weapon destiny to draw
     * @param statistic the statistic to compare total weapon destiny against
     * @param affectForfeitCondition a condition that if fulfilled, causes the target's forfeit to be affected (as determined by resetForfeit and forfeitModifierOrResetValue)
     * @param resetForfeit true if the target's forfeit is reset if
     * @param forfeitModifierOrResetValue the amount target's forfeit is modified by (or reset to if resetForfeit is true)
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponWithHitAction(final int numDestiny, final Statistic statistic, Condition affectForfeitCondition, final boolean resetForfeit, final int forfeitModifierOrResetValue) {
        return buildFireWeaponWithHitAction(numDestiny, 0, statistic, affectForfeitCondition, resetForfeit, forfeitModifierOrResetValue, 0, Filters.none, 0, Filters.none, 0, Filters.none, 0);
    }

    /**
     * Builds a fire weapon action that targets a card present with the weapon, draws destiny, and if successful affects the card's attributes.
     * @param numDestiny the number of weapon destiny to draw
     * @param plusOrMinus the amount to add to or subtract from total destiny during the calculation
     * @param statistic the statistic to compare total weapon destiny against
     * @param affectForfeitCondition a condition that if fulfilled, causes the target's forfeit to be affected (as determined by resetForfeit and forfeitModifierOrResetValue)
     * @param resetForfeit true if the target's forfeit is reset if
     * @param forfeitModifierOrResetValue the amount target's forfeit is modified by (or reset to if resetForfeit is true)
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponWithHitAction(final int numDestiny, final int plusOrMinus, final Statistic statistic, Condition affectForfeitCondition, final boolean resetForfeit, final int forfeitModifierOrResetValue) {
        return buildFireWeaponWithHitAction(numDestiny, plusOrMinus, statistic, affectForfeitCondition, resetForfeit, forfeitModifierOrResetValue, 0, Filters.none, 0, Filters.none, 0, Filters.none, 0);
    }

    /**
     * Builds a fire weapon action that targets a card present with the weapon, draws destiny, and if successful affects the card's attributes.
     * @param numDestiny the number of weapon destiny to draw
     * @param statistic the statistic to compare total weapon destiny against
     * @param opponentsForceLoss the amount of Force opponent loses if successful
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponWithHitAction(final int numDestiny, final Statistic statistic, int opponentsForceLoss) {
        return buildFireWeaponWithHitAction(numDestiny, 0, statistic, new FalseCondition(), false, 0, 0, Filters.any, opponentsForceLoss, Filters.none, 0, Filters.none, 0);
    }





/*
    /**
     * Builds a fire weapon action that targets a card present with the weapon, draws destiny, and if successful affects the card's attributes,
     * and can cause Force loss.
     * @param numDestiny the number of weapon destiny to draw
     * @param plusOrMinus the amount to add to or subtract from total destiny during the calculation
     * @param statistic the statistic to compare total weapon destiny against
     * @param affectForfeitCondition a condition that if fulfilled, causes the target's forfeit to be affected (as determined by resetForfeit and forfeitModifierOrResetValue)
     * @param resetForfeit true if the target's forfeit is reset if
     * @param forfeitModifierOrResetValue the amount target's forfeit is modified by (or reset to if resetForfeit is true)
     * @param forceLossTargetFilter opponent loses Force (as determined by opponentsForceLoss) if weapon firing successful against card accepted by the filter
     * @param opponentsForceLoss the amount of Force opponent loses if successful
     * @param selfForceLossIfMiss the amount of Force self loses if unsuccessful
     * @param activateForceTargetFilter player may activate Force (as determined by activateForceAmount) if weapon firing successful against card accepted by the filter
     * @param activateForceAmount the amount of Force player may activate if successful
     * @param alternateDefenseValueFilter use alternate defense value (as determined by alternateDefenseValue) if weapon firing at card accepted by the filter
     * @param alternateDefenseValue the defense value to use
     * @return the action

     reference: private FireSingleWeaponAction buildFireWeaponWithHitOrMissAction(final int numDestiny, final int plusOrMinus, final Statistic statistic,
                                                                      final Condition affectForfeitCondition, final boolean resetForfeit, final int forfeitModifierOrResetValue,
                                                                      final Filter forceLossTargetFilter, final int opponentsForceLoss, final int selfForceLossIfMiss,
                                                                      final Filter activateForceTargetFilter, final int activateForceAmount,
                                                                      final Filter alternateDefenseValueFilter, final float alternateDefenseValue)
     */



    public FireSingleWeaponAction buildFireWeaponWithHitOrMissAction(final int numdestiny, final Statistic statistic, int opponentsForceLoss, int selfForceLoss) {
        return buildFireWeaponWithHitOrMissAction(numdestiny, 0, statistic, new TrueCondition(), true, 0, Filters.any, opponentsForceLoss, selfForceLoss,
                Filters.none, 0, Filters.none, 0);
    }

    public FireSingleWeaponAction buildFireWeaponWithHitAndRetrieveAction(final int numdestiny, final Statistic statistic, int selfRetrieve) {
        return buildReysAnakinsLightsaberWeapon(numdestiny, 0, statistic, new TrueCondition(), true, 0, true, 1, Filters.none, 0, Filters.none, 0);
    }


    /**
     * Builds a fire weapon action that targets a card present with the weapon, draws destiny, and if successful affects the card's attributes.
     * @param numDestiny the number of weapon destiny to draw
     * @param statistic the statistic to compare total weapon destiny against
     * @param activateForceTargetFilter player may activate Force (as determined by activateForceAmount) if weapon firing successful against card accepted by the filter
     * @param activateForceAmount the amount of Force player may activate if successful
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponWithHitAction(final int numDestiny, final Statistic statistic, final Filter activateForceTargetFilter, final int activateForceAmount) {
        return buildFireWeaponWithHitAction(numDestiny, 0, statistic, new FalseCondition(), false, 0, 0, Filters.none, 0, activateForceTargetFilter, activateForceAmount, Filters.none, 0);
    }

    /**
     * Builds a fire weapon action that targets a card present with the weapon, draws destiny, and if successful affects the card's attributes.
     * @param numDestiny the number of weapon destiny to draw
     * @param plusOrMinus the amount to add to or subtract from total destiny during the calculation
     * @param statistic the statistic to compare total weapon destiny against
     * @param resetForfeit true if the target's forfeit is reset if
     * @param forfeitModifierOrResetValue the amount target's forfeit is modified by (or reset to if resetForfeit is true)
     * @param opponentsForceLoss the amount of Force opponent loses if successful
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponWithHitAction(final int numDestiny, final int plusOrMinus, final Statistic statistic, final boolean resetForfeit, final int forfeitModifierOrResetValue, int opponentsForceLoss) {
        return buildFireWeaponWithHitAction(numDestiny, plusOrMinus, statistic, new TrueCondition(), resetForfeit, forfeitModifierOrResetValue, 0, Filters.any, opponentsForceLoss, Filters.none, 0, Filters.none, 0);
    }

    /**
     * Builds a fire weapon action that targets a card present with the weapon, draws destiny, and if successful affects the card's attributes.
     * @param numDestiny the number of weapon destiny to draw
     * @param statistic the statistic to compare total weapon destiny against
     * @param resetForfeit true if the target's forfeit is reset if
     * @param forfeitModifierOrResetValue the amount target's forfeit is modified by (or reset to if resetForfeit is true)
     * @param forceLossTargetFilter opponent loses Force (as determined by opponentsForceLoss) if weapon firing successful against card accepted by the filter
     * @param opponentsForceLoss the amount of Force opponent loses if successful
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponWithHitAction(final int numDestiny, final Statistic statistic, final boolean resetForfeit, final int forfeitModifierOrResetValue, Filter forceLossTargetFilter, int opponentsForceLoss) {
        return buildFireWeaponWithHitAction(numDestiny, 0, statistic, new TrueCondition(), resetForfeit, forfeitModifierOrResetValue, 0, forceLossTargetFilter, opponentsForceLoss, Filters.none, 0, Filters.none, 0);
    }

    /**
     * Builds a fire weapon action that targets a card present with the weapon, draws destiny, and if successful affects the card's attributes.
     * @param numDestiny the number of weapon destiny to draw
     * @param plusOrMinus the amount to add to or subtract from total destiny during the calculation
     * @param statistic the statistic to compare total weapon destiny against
     * @param opponentsForceLoss the amount of Force opponent loses if successful
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponWithHitAction(final int numDestiny, final int plusOrMinus, final Statistic statistic, int opponentsForceLoss) {
        return buildFireWeaponWithHitAction(numDestiny, plusOrMinus, statistic, new FalseCondition(), false, 0, 0, Filters.any, opponentsForceLoss, Filters.none, 0, Filters.none, 0);
    }

    /**
     * Builds a fire weapon action that targets a card present with the weapon, draws destiny, and if successful affects the card's attributes.
     * @param numDestiny the number of weapon destiny to draw
     * @param statistic the statistic to compare total weapon destiny against
     * @param alternateDefenseValueFilter use alternate defense value (as determined by alternateDefenseValue) if weapon firing at card accepted by the filter
     * @param alternateDefenseValue the defense value to use
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponWithHitAction(final int numDestiny, final Statistic statistic, final Filter alternateDefenseValueFilter, final float alternateDefenseValue) {
        return buildFireWeaponWithHitAction(numDestiny, 0, statistic, new FalseCondition(), false, 0, 0, Filters.none, 0, Filters.none, 0, alternateDefenseValueFilter, alternateDefenseValue);
    }

    /**
     * Builds a fire weapon action that targets a card present with the weapon, draws destiny, and if successful affects the card's attributes,
     * and can cause Force loss.
     * @param numDestiny the number of weapon destiny to draw
     * @param plusOrMinus the amount to add to or subtract from total destiny during the calculation
     * @param statistic the statistic to compare total weapon destiny against
     * @param affectForfeitCondition a condition that if fulfilled, causes the target's forfeit to be affected (as determined by resetForfeit and forfeitModifierOrResetValue)
     * @param resetForfeit true if the target's forfeit is reset if
     * @param forfeitModifierOrResetValue the amount target's forfeit is modified by (or reset to if resetForfeit is true)
     * @param forceLossTargetFilter opponent loses Force (as determined by opponentsForceLoss) if weapon firing successful against card accepted by the filter
     * @param opponentsForceLoss the amount of Force opponent loses if successful
     * @param activateForceTargetFilter player may activate Force (as determined by activateForceAmount) if weapon firing successful against card accepted by the filter
     * @param activateForceAmount the amount of Force player may activate if successful
     * @param alternateDefenseValueFilter use alternate defense value (as determined by alternateDefenseValue) if weapon firing at card accepted by the filter
     * @param alternateDefenseValue the defense value to use
     * @return the action
     */
    private FireSingleWeaponAction buildFireWeaponWithHitAction(final int numDestiny, final int plusOrMinus, final Statistic statistic,
                                                                final Condition affectForfeitCondition, final boolean resetForfeit,
                                                                final int forfeitModifierOrResetValue, final int powerModifierValue,
                                                                final Filter forceLossTargetFilter, final int opponentsForceLoss,
                                                                final Filter activateForceTargetFilter, final int activateForceAmount,
                                                                final Filter alternateDefenseValueFilter, final float alternateDefenseValue) {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game) + (_numTargets > 1 ? (" at " + _numTargets + " targets") : ""));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardsOnTableEffect(action, action.getPerformingPlayer(), "Choose target" + GameUtils.s(_numTargets), _numTargets, _numTargets, getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardsTargeted(final int targetGroupId, Collection<PhysicalCard> cardsTargeted) {
                        action.addAnimationGroup(cardsTargeted);
                        _game.getGameState().getWeaponFiringState().setTargets(cardsTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardsTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getAppendedNames(cardsTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final Collection<PhysicalCard> cardsFiredAt = targetingAction.getPrimaryTargetCards(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTargets(cardsFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, numDestiny, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        if (statistic == Statistic.DEFENSE_VALUE || statistic == Statistic.MANEUVER || statistic == Statistic.ABILITY) {
                                                            return cardsFiredAt;
                                                        }
                                                        return Collections.emptyList();
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        totalDestiny = totalDestiny + plusOrMinus;
                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        List<StandardEffect> effectList = new ArrayList<StandardEffect>();

                                                        float valueToCompare = 0;
                                                        for (PhysicalCard cardFiredAt : cardsFiredAt) {
                                                            if (statistic == Statistic.DEFENSE_VALUE) {
                                                                if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                                    valueToCompare += _defenseValueAsCharacter;
                                                                } else if (alternateDefenseValueFilter.accepts(game, cardFiredAt)) {
                                                                    valueToCompare += alternateDefenseValue;
                                                                } else {
                                                                    valueToCompare += game.getModifiersQuerying().getDefenseValue(game.getGameState(), cardFiredAt);
                                                                }
                                                            } else {
                                                                throw new UnsupportedOperationException("Invalid statistic " + (statistic != null ? statistic.getHumanReadable() : null));
                                                            }
                                                        }
                                                        gameState.sendMessage((_numTargets > 1 ? "Total defense value: " : "Defense value: ") + GuiUtils.formatAsString(valueToCompare));

                                                        if (totalDestiny > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");

                                                            for (PhysicalCard cardFiredAt : cardsFiredAt) {
                                                                if (affectForfeitCondition.isFulfilled(gameState, game.getModifiersQuerying())) {
                                                                    if (resetForfeit) {
                                                                        if (opponentsForceLoss > 0 && forceLossTargetFilter.accepts(game, cardFiredAt)) {
                                                                            effectList.add(new HitCardResetForfeitAndOpponentLosesForceEffect(action, cardFiredAt, forfeitModifierOrResetValue, opponentsForceLoss, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                                        } else {
                                                                            effectList.add(new HitCardAndResetForfeitEffect(action, cardFiredAt, forfeitModifierOrResetValue, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                                        }
                                                                    } else {
                                                                        if (opponentsForceLoss > 0 && forceLossTargetFilter.accepts(game, cardFiredAt)) {
                                                                            effectList.add(new HitCardModifyForfeitAndOpponentLosesForceEffect(action, cardFiredAt, forfeitModifierOrResetValue, opponentsForceLoss, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                                        } else if (forfeitModifierOrResetValue != 0 && powerModifierValue != 0) {
                                                                            effectList.add(new HitCardAndModifyPowerAndForfeitEffect(action, cardFiredAt, powerModifierValue, forfeitModifierOrResetValue, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                                        } else {
                                                                            effectList.add(new HitCardAndModifyForfeitEffect(action, cardFiredAt, forfeitModifierOrResetValue, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                                        }
                                                                    }
                                                                } else if (opponentsForceLoss > 0 && forceLossTargetFilter.accepts(game, cardFiredAt)) {
                                                                    effectList.add(new HitCardAndOpponentLosesForceEffect(action, cardFiredAt, opponentsForceLoss, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                                } else if (activateForceAmount > 0 && activateForceTargetFilter.accepts(game, cardFiredAt)) {
                                                                    effectList.add(new HitCardAndMayActivateForceEffect(action, cardFiredAt, activateForceAmount, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                                } else {
                                                                    effectList.add(new HitCardEffect(action, cardFiredAt, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                                }
                                                            }
                                                        } else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }

                                                        // Current player chooses order of effects
                                                        if (!effectList.isEmpty()) {
                                                            action.appendEffect(new ChooseEffectOrderEffect(action, effectList, false));
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }






    /**
     * Builds a fire weapon action that targets a card present with the weapon, draws destiny, and if successful affects the card's attributes,
     * and can cause Force loss.
     * @param numDestiny the number of weapon destiny to draw
     * @param plusOrMinus the amount to add to or subtract from total destiny during the calculation
     * @param statistic the statistic to compare total weapon destiny against
     * @param affectForfeitCondition a condition that if fulfilled, causes the target's forfeit to be affected (as determined by resetForfeit and forfeitModifierOrResetValue)
     * @param resetForfeit true if the target's forfeit is reset if
     * @param forfeitModifierOrResetValue the amount target's forfeit is modified by (or reset to if resetForfeit is true)
     * @param forceLossTargetFilter opponent loses Force (as determined by opponentsForceLoss) if weapon firing successful against card accepted by the filter
     * @param opponentsForceLoss the amount of Force opponent loses if successful
     * @param selfForceLossIfMiss the amount of Force self loses if unsuccessful
     * @param activateForceTargetFilter player may activate Force (as determined by activateForceAmount) if weapon firing successful against card accepted by the filter
     * @param activateForceAmount the amount of Force player may activate if successful
     * @param alternateDefenseValueFilter use alternate defense value (as determined by alternateDefenseValue) if weapon firing at card accepted by the filter
     * @param alternateDefenseValue the defense value to use
     * @return the action
     */
    private FireSingleWeaponAction buildFireWeaponWithHitOrMissAction(final int numDestiny, final int plusOrMinus, final Statistic statistic,
                                                                final Condition affectForfeitCondition, final boolean resetForfeit, final int forfeitModifierOrResetValue,
                                                                final Filter forceLossTargetFilter, final int opponentsForceLoss, final int selfForceLossIfMiss,
                                                                final Filter activateForceTargetFilter, final int activateForceAmount,
                                                                final Filter alternateDefenseValueFilter, final float alternateDefenseValue) {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game) + (_numTargets > 1 ? (" at " + _numTargets + " targets") : ""));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardsOnTableEffect(action, action.getPerformingPlayer(), "Choose target" + GameUtils.s(_numTargets), _numTargets, _numTargets, getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardsTargeted(final int targetGroupId, Collection<PhysicalCard> cardsTargeted) {
                        action.addAnimationGroup(cardsTargeted);
                        _game.getGameState().getWeaponFiringState().setTargets(cardsTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardsTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getAppendedNames(cardsTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final Collection<PhysicalCard> cardsFiredAt = targetingAction.getPrimaryTargetCards(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTargets(cardsFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, numDestiny, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        if (statistic == Statistic.DEFENSE_VALUE || statistic == Statistic.MANEUVER || statistic == Statistic.ABILITY) {
                                                            return cardsFiredAt;
                                                        }
                                                        return Collections.emptyList();
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        List<StandardEffect> effectList = new ArrayList<StandardEffect>();

                                                        float valueToCompare = 0;
                                                        for (PhysicalCard cardFiredAt : cardsFiredAt) {
                                                            if (statistic == Statistic.DEFENSE_VALUE) {
                                                                if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                                    valueToCompare += _defenseValueAsCharacter;
                                                                } else if (alternateDefenseValueFilter.accepts(game, cardFiredAt)) {
                                                                    valueToCompare += alternateDefenseValue;
                                                                } else {
                                                                    valueToCompare += game.getModifiersQuerying().getDefenseValue(game.getGameState(), cardFiredAt);
                                                                }
                                                            } else {
                                                                throw new UnsupportedOperationException("Invalid statistic " + (statistic != null ? statistic.getHumanReadable() : null));
                                                            }
                                                        }
                                                        gameState.sendMessage((_numTargets > 1 ? "Total defense value: " : "Defense value: ") + GuiUtils.formatAsString(valueToCompare));

                                                        if ((totalDestiny + plusOrMinus) > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");

                                                            for (PhysicalCard cardFiredAt : cardsFiredAt) {
                                                                if (affectForfeitCondition.isFulfilled(gameState, game.getModifiersQuerying())) {
                                                                    if (resetForfeit) {
                                                                        if (opponentsForceLoss > 0 && forceLossTargetFilter.accepts(game, cardFiredAt)) {
                                                                            effectList.add(new HitCardResetForfeitAndOpponentLosesForceEffect(action, cardFiredAt, forfeitModifierOrResetValue, opponentsForceLoss, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                                        } else {
                                                                            effectList.add(new HitCardAndResetForfeitEffect(action, cardFiredAt, forfeitModifierOrResetValue, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                                        }
                                                                    } else {
                                                                        if (opponentsForceLoss > 0 && forceLossTargetFilter.accepts(game, cardFiredAt)) {
                                                                            effectList.add(new HitCardModifyForfeitAndOpponentLosesForceEffect(action, cardFiredAt, forfeitModifierOrResetValue, opponentsForceLoss, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                                        } else {
                                                                            effectList.add(new HitCardAndModifyForfeitEffect(action, cardFiredAt, forfeitModifierOrResetValue, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                                        }
                                                                    }
                                                                } else if (opponentsForceLoss > 0 && forceLossTargetFilter.accepts(game, cardFiredAt)) {
                                                                    effectList.add(new HitCardAndOpponentLosesForceEffect(action, cardFiredAt, opponentsForceLoss, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                                } else if (activateForceAmount > 0 && activateForceTargetFilter.accepts(game, cardFiredAt)) {
                                                                    effectList.add(new HitCardAndMayActivateForceEffect(action, cardFiredAt, activateForceAmount, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                                } else {
                                                                    effectList.add(new HitCardEffect(action, cardFiredAt, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                                }
                                                            }
                                                        } else {
                                                            gameState.sendMessage("Result: Failed");
                                                            for (PhysicalCard cardFiredAt : cardsFiredAt)
                                                            {
                                                                effectList.add(new LoseForceEffect(action, _playerId, selfForceLossIfMiss));
                                                            }
                                                        }

                                                        // Current player chooses order of effects
                                                        if (!effectList.isEmpty()) {
                                                            action.appendEffect(new ChooseEffectOrderEffect(action, effectList, false));
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }


    // This fires Anakin's Lightsaber as it exists on "Rey With Lightsaber" in Virtual Set 9.
    /**
     * Builds a fire weapon action that targets a card present with the weapon, draws destiny, and if successful affects the card's attributes,
     * and can cause Force loss.
     * @param numDestiny the number of weapon destiny to draw
     * @param plusOrMinus the amount to add to or subtract from total destiny during the calculation
     * @param statistic the statistic to compare total weapon destiny against
     * @param affectForfeitCondition a condition that if fulfilled, causes the target's forfeit to be affected (as determined by resetForfeit and forfeitModifierOrResetValue)
     * @param resetForfeit true if the target's forfeit is reset if
     * @param forfeitModifierOrResetValue the amount target's forfeit is modified by (or reset to if resetForfeit is true)
     * @param selfForceRetrieve self retrieves Force (as determined by opponentsForceLoss) if weapon firing successful against card accepted by the filter
     * @param selfForceRetrieveNumber the amount of Force self retrieves if successful
     * @param activateForceTargetFilter player may activate Force (as determined by activateForceAmount) if weapon firing successful against card accepted by the filter
     * @param activateForceAmount the amount of Force player may activate if successful
     * @param alternateDefenseValueFilter use alternate defense value (as determined by alternateDefenseValue) if weapon firing at card accepted by the filter
     * @param alternateDefenseValue the defense value to use
     * @return the action
     */

    public FireSingleWeaponAction buildReysAnakinsLightsaberWeapon(final int numDestiny, final int plusOrMinus, final Statistic statistic,
                                                                    final Condition affectForfeitCondition, final boolean resetForfeit, final int forfeitModifierOrResetValue,
                                                                    final boolean selfForceRetrieve, final int selfForceRetrieveNumber,
                                                                    final Filter activateForceTargetFilter, final int activateForceAmount,
                                                                    final Filter alternateDefenseValueFilter, final float alternateDefenseValue) {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 2, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(cardFiredAt);
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                            valueToCompare = _defenseValueAsCharacter;
                                                        }
                                                        else {
                                                            valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                        }
                                                        gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));

                                                        if (totalDestiny > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");

                                                            PhysicalCard cardFiredBy = gameState.getWeaponFiringState().getCardFiringWeapon();
                                                            action.appendEffect(
                                                                    new HitCardResetForfeitAndRetrieveForceEffect(action, cardFiredAt, 0, selfForceRetrieveNumber, _weaponOrCardWithPermanentWeapon, _permanentWeapon, cardFiredBy));
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }


    /**
     * Builds a fire weapon action for Zuckuss' Snare Rifle.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponWithHitAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(cardFiredAt);
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                            valueToCompare = _defenseValueAsCharacter;
                                                        }
                                                        else {
                                                            valueToCompare = game.getModifiersQuerying().getDefenseValue(game.getGameState(), cardFiredAt);
                                                        }
                                                        gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));

                                                        if (Filters.character.accepts(game, cardFiredAt)
                                                                || (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt))) {
                                                            if ((totalDestiny - 1) > valueToCompare) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new CaptureCharacterOnTableEffect(action, cardFiredAt, action.getCardFiringWeapon()));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                        else if (Filters.creature.accepts(game, cardFiredAt)) {
                                                            if ((totalDestiny + 1) > valueToCompare) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new LoseCardFromTableEffect(action, cardFiredAt));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action that targets a character, landed starfighter, or vehicle for free; if targeting a starfighter or vehicle, add one destiny to attrition; otherwise, subtract 3 from target's immunity to attrition (if any) until end of turn
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponNarthaxEWebBlasterAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        if(cardFiredAt.getBlueprint().getCardCategory() == CardCategory.CHARACTER){
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(action,
                                                            new ImmunityToAttritionChangeModifier(_weaponOrCardWithPermanentWeapon, cardFiredAt,  -3), GameUtils.getCardLink(cardFiredAt) + " is immunity to attrition -3"));
                                        }else{
                                            action.appendEffect(
                                                    new AddUntilEndOfBattleModifierEffect(action,
                                                            new AddsDestinyToAttritionModifier(_weaponOrCardWithPermanentWeapon, 1), "adds one destiny to attrition"));
                                        }
                                    }
                                });
                    }
                }
        );

        return action;
    }


    /**
     * Builds a fire weapon action that targets a card present with the weapon and cancels its game text.
     * @param untilEndOfTurn true if game text is canceled until end of turn, otherwise default duration is used
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponCancelGameTextAction(final boolean untilEndOfTurn) {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);
                                        if (untilEndOfTurn) {
                                            action.appendEffect(
                                                    new CancelGameTextUntilEndOfTurnEffect(action, cardFiredAt));
                                        } else {
                                            action.appendEffect(
                                                    new CancelGameTextEffect(action, cardFiredAt));
                                            }

                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action that targets a card present with the weapon and modifies its power.
     * @param amountToModify the amount to modify power
     * @param untilEndOfTurn true if power is modified until end of turn, otherwise default duration is used
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponModifyPowerAction(final float amountToModify, final boolean untilEndOfTurn) {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        if (untilEndOfTurn) {
                                            action.appendEffect(
                                                    new ModifyPowerUntilEndOfTurnEffect(action, cardFiredAt, amountToModify));
                                        }
                                        else {
                                            action.appendEffect(
                                                    new ModifyPowerEffect(action, cardFiredAt, amountToModify));
                                        }
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action that targets a card present with the weapon, draws destiny, and if successful causes starship
     * weapons to be lost, and reset armor, maneuver, and hyperspeed to 0.
     * @param numDestiny the number of weapon destiny to draw
     * @param statistic the statistic to compare total weapon destiny against
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponIonCannonAction(final int numDestiny, final Statistic statistic) {
        return buildFireWeaponIonCannonAction(numDestiny, 0, statistic);
    }

    /**
     * Builds a fire weapon action that targets a card present with the weapon, draws destiny, and if successful causes starship
     * weapons to be lost, and reset armor, maneuver, and hyperspeed to 0.
     * @param numDestiny the number of weapon destiny to draw
     * @param plusOrMinus the amount to add to or subtract from total destiny during the calculation
     * @param statistic the statistic to compare total weapon destiny against
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponIonCannonAction(final int numDestiny, final int plusOrMinus, final Statistic statistic) {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, numDestiny, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        if (statistic == Statistic.DEFENSE_VALUE || statistic == Statistic.MANEUVER || statistic == Statistic.ABILITY) {
                                                            return Collections.singletonList(cardFiredAt);
                                                        }
                                                        return Collections.emptyList();
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (statistic == Statistic.DEFENSE_VALUE) {
                                                            if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                                valueToCompare = _defenseValueAsCharacter;
                                                            }
                                                            else {
                                                                valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                            }
                                                            gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));
                                                        }
                                                        else {
                                                            throw new UnsupportedOperationException("Invalid statistic " + (statistic != null ? statistic.getHumanReadable() : null));
                                                        }

                                                        if ((totalDestiny + plusOrMinus) > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            action.appendEffect(
                                                                    new ResetArmorManeuverAndHyperspeedEffect(action, cardFiredAt, 0));
                                                            Collection<PhysicalCard> starshipWeapons = Filters.filterAllOnTable(game, Filters.and(Filters.starship_weapon, Filters.attachedTo(cardFiredAt)));
                                                            if (!starshipWeapons.isEmpty()) {
                                                                action.appendEffect(
                                                                        new LoseCardsFromTableEffect(action, starshipWeapons, true));
                                                            }
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action that targets a card present with the weapon, draws destiny, and if successful captures the card.
     * @param numDestiny the number of weapon destiny to draw
     * @param statistic the statistic to compare total weapon destiny against
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponCaptureAction(final int numDestiny, final Statistic statistic) {
        return buildFireWeaponCaptureAction(numDestiny, 0, statistic);
    }

    /**
     * Builds a fire weapon action that targets a card present with the weapon, draws destiny, and if successful captures the card.
     * @param numDestiny the number of weapon destiny to draw
     * @param plusOrMinus the amount to add to or subtract from total destiny during the calculation
     * @param statistic the statistic to compare total weapon destiny against
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponCaptureAction(final int numDestiny, final int plusOrMinus, final Statistic statistic) {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, numDestiny, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        if (statistic == Statistic.DEFENSE_VALUE || statistic == Statistic.MANEUVER || statistic == Statistic.ABILITY) {
                                                            return Collections.singletonList(cardFiredAt);
                                                        }
                                                        return Collections.emptyList();
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (statistic == Statistic.DEFENSE_VALUE) {
                                                            if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                                valueToCompare = _defenseValueAsCharacter;
                                                            }
                                                            else {
                                                                valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                            }
                                                            gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));
                                                        }
                                                        else {
                                                            throw new UnsupportedOperationException("Invalid statistic " + (statistic != null ? statistic.getHumanReadable() : null));
                                                        }

                                                        if ((totalDestiny + plusOrMinus) > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            action.appendEffect(
                                                                    new CaptureCharacterOnTableEffect(action, cardFiredAt, action.getCardFiringWeapon()));
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action that targets a card present with the weapon and makes the card lost.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponCreatureLostAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new LoseCardFromTableEffect(action, cardFiredAt));
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action that targets a card present with the weapon, draws destiny, and if successful makes the card lost.
     * @param numDestiny the number of weapon destiny to draw
     * @param statistic the statistic to compare total weapon destiny against
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponLostAction(final int numDestiny, final Statistic statistic) {
        return buildFireWeaponLostAction(numDestiny, 0, 0, statistic);
    }

    /**
     * Builds a fire weapon action that targets a card present with the weapon, draws destiny, and if successful makes the card lost.
     * @param numDestiny the number of weapon destiny to draw
     * @param characterPlusOrMinus the amount to add to or subtract from total destiny (targeting character) during the calculation
     * @param vehiclePlusOrMinus the amount to add to or subtract from total destiny (targeting vehicle) during the calculation
     * @param statistic the statistic to compare total weapon destiny against
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponLostAction(final int numDestiny, final int characterPlusOrMinus, final int vehiclePlusOrMinus, final Statistic statistic) {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, numDestiny, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        if (statistic == Statistic.DEFENSE_VALUE || statistic == Statistic.MANEUVER || statistic == Statistic.ABILITY) {
                                                            return Collections.singletonList(cardFiredAt);
                                                        }
                                                        return Collections.emptyList();
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (statistic == Statistic.DEFENSE_VALUE) {
                                                            if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                                valueToCompare = _defenseValueAsCharacter;
                                                            }
                                                            else {
                                                                valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                            }
                                                            gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));
                                                        }
                                                        else {
                                                            throw new UnsupportedOperationException("Invalid statistic " + (statistic != null ? statistic.getHumanReadable() : null));
                                                        }

                                                        if (Filters.character.accepts(game, cardFiredAt)
                                                                || (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt))) {
                                                            if ((totalDestiny + characterPlusOrMinus) > valueToCompare) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new LoseCardFromTableEffect(action, cardFiredAt));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                        else if (Filters.creature.accepts(game, cardFiredAt)) {
                                                            if (totalDestiny  > valueToCompare) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new LoseCardFromTableEffect(action, cardFiredAt));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                        else if (Filters.vehicle.accepts(game, cardFiredAt)) {
                                                            if ((totalDestiny + vehiclePlusOrMinus) > valueToCompare) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new LoseCardFromTableEffect(action, cardFiredAt));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action that targets a card present with the weapon, both players draw destiny, and if successful
     * the card is excluded from battle.
     * @param numYourDestiny the number of weapon destiny for player firing weapon to draw
     * @param numOpponentsDestiny the number of (non-weapon) destiny for opponent to draw
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponVibroAxAction(int numYourDestiny, int numOpponentsDestiny) {
        return buildFireWeaponVibroAxAction(numYourDestiny, null, numOpponentsDestiny);
    }

    /**
     * Builds a fire weapon action that targets a card present with the weapon, both players draw destiny, and if successful
     * the card is excluded from battle.
     * @param numYourDestiny the number of weapon destiny for player firing weapon to draw
     * @param plusOrMinus the amount to add to or subtract from total destiny during the calculation
     * @param numOpponentsDestiny the number of (non-weapon) destiny for opponent to draw
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponVibroAxAction(final int numYourDestiny, final float plusOrMinus, final int numOpponentsDestiny) {
        return buildFireWeaponVibroAxAction(numYourDestiny, (Float) plusOrMinus, numOpponentsDestiny);
    }

    /**
     * Builds a fire weapon action that targets a card present with the weapon, both players draw destiny, and if successful
     * the card is excluded from battle.
     * @param numYourDestiny the number of weapon destiny for player firing weapon to draw
     * @param plusOrMinus the amount to add to or subtract from total destiny during the calculation
     * @param numOpponentsDestiny the number of (non-weapon) destiny for opponent to draw
     * @return the action
     */
    private FireSingleWeaponAction buildFireWeaponVibroAxAction(final int numYourDestiny, final Float plusOrMinus, final int numOpponentsDestiny) {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, numYourDestiny, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> playersDestinyCardDraws, List<Float> playersDestinyDrawValues, final Float playersTotalDestiny) {
                                                        action.appendEffect(
                                                                new DrawDestinyEffect(action, _opponent, numOpponentsDestiny) {
                                                                    @Override
                                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> opponentsDestinyCardDraws, List<Float> opponentsDestinyDrawValues, Float opponentsTotalDestiny) {
                                                                        GameState gameState = game.getGameState();

                                                                        Float playersValueToAdd = plusOrMinus;
                                                                        if (playersValueToAdd == null) {
                                                                            playersValueToAdd = game.getModifiersQuerying().getPower(game.getGameState(), game.getGameState().getWeaponFiringState().getCardFiringWeapon());
                                                                            gameState.sendMessage("Warrior's power: " + GuiUtils.formatAsString(playersValueToAdd));
                                                                        }
                                                                        gameState.sendMessage("Total destiny: " + (playersTotalDestiny != null ? GuiUtils.formatAsString(playersTotalDestiny) : "Failed destiny draw"));
                                                                        float opponentsValueToAdd = game.getModifiersQuerying().getPower(game.getGameState(), cardFiredAt);
                                                                        gameState.sendMessage("Target's power: " + GuiUtils.formatAsString(opponentsValueToAdd));
                                                                        gameState.sendMessage("Opponent's total destiny: " + (opponentsTotalDestiny != null ? GuiUtils.formatAsString(opponentsTotalDestiny) : "Failed destiny draw"));

                                                                        if (((playersTotalDestiny != null ? playersTotalDestiny : 0) + playersValueToAdd) > ((opponentsTotalDestiny != null ? opponentsTotalDestiny : 0) + opponentsValueToAdd)) {
                                                                            gameState.sendMessage("Result: Succeeded");
                                                                            action.appendEffect(
                                                                                    new ExcludeFromBattleEffect(action, cardFiredAt, _permanentWeapon, action.getCardFiringWeapon()));
                                                                        }
                                                                        else {
                                                                            gameState.sendMessage("Result: Failed");
                                                                        }
                                                                    }
                                                                }
                                                        );
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action that targets a card present with the weapon, draws destiny, and if successful returns
     * character to hand or makes creature lost.
     * @param numDestiny the number of weapon destiny to draw
     * @param characterPlusOrMinus the amount to add to or subtract from total destiny (targeting character) during the calculation
     * @param creaturePlusOrMinus the amount to add to or subtract from total destiny (targeting creature) during the calculation
     * @param statistic the statistic to compare total weapon destiny against
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponStunBlasterAction(final int numDestiny, final int characterPlusOrMinus, final int creaturePlusOrMinus, final Statistic statistic) {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, numDestiny, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        if (statistic == Statistic.DEFENSE_VALUE || statistic == Statistic.MANEUVER || statistic == Statistic.ABILITY) {
                                                            return Collections.singletonList(cardFiredAt);
                                                        }
                                                        return Collections.emptyList();
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (statistic == Statistic.DEFENSE_VALUE) {
                                                            if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                                valueToCompare = _defenseValueAsCharacter;
                                                            }
                                                            else {
                                                                valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                            }
                                                            gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));
                                                        }
                                                        else {
                                                            throw new UnsupportedOperationException("Invalid statistic " + (statistic != null ? statistic.getHumanReadable() : null));
                                                        }

                                                        if (Filters.character.accepts(game, cardFiredAt)) {
                                                            if ((totalDestiny + characterPlusOrMinus) > valueToCompare) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new ReturnCardToHandFromTableEffect(action, cardFiredAt, Zone.HAND));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                        else if (Filters.creature.accepts(game, cardFiredAt)) {
                                                            if ((totalDestiny + creaturePlusOrMinus) > valueToCompare) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new LoseCardFromTableEffect(action, cardFiredAt));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for Aurra Sing's Blaster Rifle.
     * @param numDestiny the number of weapon destiny to draw
     * @param plusOrMinus the amount to add to or subtract from total destiny during the calculation
     * @param statistic the statistic to compare total weapon destiny against
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponAurraSingsBlasterRifleAction(final int numDestiny, final int plusOrMinus, final Statistic statistic) {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new CancelImmunityToAttritionUntilEndOfTurnEffect(action, cardFiredAt, "Cancels " + GameUtils.getCardLink(cardFiredAt) + "'s immunity to attrition"));
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, numDestiny, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        if (statistic == Statistic.DEFENSE_VALUE || statistic == Statistic.MANEUVER || statistic == Statistic.ABILITY) {
                                                            return Collections.singletonList(cardFiredAt);
                                                        }
                                                        return Collections.emptyList();
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (statistic == Statistic.DEFENSE_VALUE) {
                                                            if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                                valueToCompare = _defenseValueAsCharacter;
                                                            }
                                                            else {
                                                                valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                            }
                                                            gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));
                                                        }
                                                        else {
                                                            throw new UnsupportedOperationException("Invalid statistic " + (statistic != null ? statistic.getHumanReadable() : null));
                                                        }

                                                        if ((totalDestiny + plusOrMinus) > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            action.appendEffect(
                                                                    new HitCardEffect(action, cardFiredAt, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                            if (Filters.Aurra.accepts(game, action.getCardFiringWeapon())
                                                                    && Filters.Jedi.accepts(game, cardFiredAt)) {
                                                                action.appendEffect(
                                                                        new ResetPowerUntilEndOfBattleEffect(action, cardFiredAt, 0));
                                                            }
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for Permanent Aurra Sing's Blaster Rifle.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponPermanentAurraSingsBlasterRifleAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);
                                        int numDestiny = Filters.Jedi.accepts(_game, cardFiredAt) ? 2 : 1;
                                        final Statistic statistic = Statistic.DEFENSE_VALUE;
                                        // Perform result(s)
                                         action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, numDestiny, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        if (statistic == Statistic.DEFENSE_VALUE || statistic == Statistic.MANEUVER || statistic == Statistic.ABILITY) {
                                                            return Collections.singletonList(cardFiredAt);
                                                        }
                                                        return Collections.emptyList();
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (statistic == Statistic.DEFENSE_VALUE) {
                                                            if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                                valueToCompare = _defenseValueAsCharacter;
                                                            }
                                                            else {
                                                                valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                            }
                                                            gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));
                                                        }
                                                        else {
                                                            throw new UnsupportedOperationException("Invalid statistic " + (statistic != null ? statistic.getHumanReadable() : null));
                                                        }

                                                        if ((totalDestiny + 1) > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            action.appendEffect(
                                                                    new HitCardAndResetForfeitEffect(action, cardFiredAt, 0, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for an Electropole.
     * @param placeInUsedPile placed in Used Pile to fire
     * @param numDestiny the number of weapon destiny to draw
     * @param plusOrMinus the amount to add to or subtract from total destiny during the calculation
     * @param statistic the statistic to compare total weapon destiny against
     * @param resetLandspeedToZero true if target's landspeed is reset to zero if firing successful
     * @param powerModifierAmount the amount target's power is modified by
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponElectropoleAction(final boolean placeInUsedPile, final int numDestiny, final int plusOrMinus, final Statistic statistic, final boolean resetLandspeedToZero, final int powerModifierAmount) {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }
                        if (placeInUsedPile) {
                            action.appendCost(
                                    new PlaceCardInUsedPileFromTableEffect(action, _weaponOrCardWithPermanentWeapon));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, numDestiny, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        if (statistic == Statistic.DEFENSE_VALUE || statistic == Statistic.MANEUVER || statistic == Statistic.ABILITY) {
                                                            return Collections.singletonList(cardFiredAt);
                                                        }
                                                        return Collections.emptyList();
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (statistic == Statistic.DEFENSE_VALUE) {
                                                            if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                                valueToCompare = _defenseValueAsCharacter;
                                                            }
                                                            else {
                                                                valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                            }
                                                            gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));
                                                        }
                                                        else {
                                                            throw new UnsupportedOperationException("Invalid statistic " + (statistic != null ? statistic.getHumanReadable() : null));
                                                        }

                                                        if ((totalDestiny + plusOrMinus) > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            if (resetLandspeedToZero) {
                                                                action.appendEffect(
                                                                        new ResetLandspeedAndModifyPowerUntilEndOfTurnEffect(action, cardFiredAt, 0, powerModifierAmount));
                                                            }
                                                            else {
                                                                action.appendEffect(
                                                                        new ModifyPowerUntilEndOfTurnEffect(action, cardFiredAt, powerModifierAmount));
                                                            }
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for a "Rock".
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponRockAction() {

        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit, true);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        action.appendCost(
                                new PlaceCardInUsedPileFromTableEffect(action, _weaponOrCardWithPermanentWeapon));

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);
                                        
                                        action.appendEffect(
                                                new ModifyPowerUntilEndOfTurnEffect(action, cardFiredAt, -3));

                                        if (Filters.persona(Persona.PROXIMA).accepts(_game, cardFiredAt)) {
                                            action.appendEffect(new ExcludeFromBattleEffect(action, cardFiredAt));
                                            action.appendEffect(new MayNotBattleUntilEndOfTurnEffect(action, cardFiredAt));
                                        }
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for Amidala's Blaster.
     * @param numDestiny the number of weapon destiny to draw
     * @param creaturePlusOrMinus the amount to add to or subtract from total destiny (targeting creature) during the calculation
     * @param statistic the statistic to compare total weapon destiny against
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponAmidalasBlasterAction(final int numDestiny, final int creaturePlusOrMinus, final Statistic statistic) {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, numDestiny, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        if (statistic == Statistic.DEFENSE_VALUE || statistic == Statistic.MANEUVER || statistic == Statistic.ABILITY) {
                                                            return Collections.singletonList(cardFiredAt);
                                                        }
                                                        return Collections.emptyList();
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (statistic == Statistic.DEFENSE_VALUE) {
                                                            if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                                valueToCompare = _defenseValueAsCharacter;
                                                            }
                                                            else {
                                                                valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                            }
                                                            gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));
                                                        }
                                                        else {
                                                            throw new UnsupportedOperationException("Invalid statistic " + (statistic != null ? statistic.getHumanReadable() : null));
                                                        }

                                                        if (Filters.character.accepts(game, cardFiredAt)
                                                                || (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt))) {
                                                            if (totalDestiny > valueToCompare) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new ResetPowerAndForfeitEffect(action, cardFiredAt, 0));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                        else if (Filters.creature.accepts(game, cardFiredAt)) {
                                                            if ((totalDestiny + creaturePlusOrMinus) > valueToCompare) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new LoseCardFromTableEffect(action, cardFiredAt));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for Booma.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponBoomaAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Pay cost(s)
        float forceToUse = getUseForceCost(action.getCardFiringWeapon());
        if (forceToUse > 0) {
            action.appendCost(
                    new UseForceEffect(action, _playerId, forceToUse));
        }

        // Allow response(s)
        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()),
                new RespondableWeaponFiringEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                    @Override
                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                        GameState gameState = game.getGameState();
                                        if (totalDestiny == null) {
                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                            return;
                                        }

                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));

                                        Filter filter = Filters.and(Filters.opponents(_weaponOrCardWithPermanentWeapon), Filters.or(Filters.character, Filters.vehicle),
                                                Filters.present(_weaponOrCardWithPermanentWeapon));
                                        int numPresent = Filters.countActive(game, _weaponOrCardWithPermanentWeapon, filter);
                                        gameState.sendMessage("Opponent's characters and vehicles present: " + numPresent);

                                        if (totalDestiny < numPresent) {
                                            gameState.sendMessage("Result: Succeeded");
                                            action.appendEffect(
                                                    new ChooseCardToLoseFromTableEffect(action, game.getOpponent(_playerId), filter));
                                        }
                                        else {
                                            gameState.sendMessage("Result: Failed");
                                        }
                                    }
                                }
                        );
                    }
                });

        return action;
    }

    /**
     * Builds a fire weapon action for Droid Starfighter Laser Cannons.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponDroidStarfighterLaserCannonsAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, action.getPerformingPlayer(), "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(cardFiredAt);
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                            valueToCompare = _defenseValueAsCharacter;
                                                        }
                                                        else {
                                                            valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                        }
                                                        gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));

                                                        if (totalDestiny > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            action.appendEffect(
                                                                    new HitCardEffect(action, cardFiredAt, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                        }
                                                        else if ((totalDestiny + 1) == valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            action.appendEffect(
                                                                    new ResetPowerUntilEndOfTurnEffect(action, cardFiredAt, 0));
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action that targets a card present with the weapon, draws X destiny and choose Y, and if successful target is hit.
     * @param drawX the number of weapon destiny to draw
     * @param chooseY the number of weapon destiny to choose
     * @param plusOrMinus the amount to add to or subtract from total destiny during the calculation
     * @param statistic the statistic to compare total weapon destiny against
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponDrawXChooseYWithHitAction(final int drawX, final int chooseY, final int plusOrMinus, final Statistic statistic) {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, drawX, chooseY, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        if (statistic == Statistic.DEFENSE_VALUE || statistic == Statistic.MANEUVER || statistic == Statistic.ABILITY) {
                                                            return Collections.singletonList(cardFiredAt);
                                                        }
                                                        return Collections.emptyList();
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (statistic == Statistic.DEFENSE_VALUE) {
                                                            if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                                valueToCompare = _defenseValueAsCharacter;
                                                            }
                                                            else {
                                                                valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                            }
                                                            gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));
                                                        }
                                                        else {
                                                            throw new UnsupportedOperationException("Invalid statistic " + (statistic != null ? statistic.getHumanReadable() : null));
                                                        }

                                                        if ((totalDestiny + plusOrMinus) > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            action.appendEffect(
                                                                    new HitCardEffect(action, cardFiredAt, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for Energy Shell Launchers.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponEnergyShellLaunchersAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, action.getPerformingPlayer(), "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare = game.getModifiersQuerying().getDestiny(game.getGameState(), cardFiredAt);
                                                        gameState.sendMessage("Target's destiny number: " + GuiUtils.formatAsString(valueToCompare));

                                                        if (totalDestiny == valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            action.appendEffect(
                                                                    new HitCardEffect(action, cardFiredAt, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }

                                                    @Override
                                                    protected List<OptionalGameTextTriggerAction> getGameTextOptionalTotalDestinyTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
                                                        final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                                                        action.setText("Subtract 1 from total weapon destiny");
                                                        // Perform result(s)
                                                        action.appendEffect(
                                                                new ModifyTotalWeaponDestinyEffect(action, -1));
                                                        return Collections.singletonList(action);
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for Bossk With Mortar Gun.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponBosskWithMortarGunAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Pay cost(s)
        float forceToUse = getUseForceCost(action.getCardFiringWeapon());
        if (forceToUse > 0) {
            action.appendCost(
                    new UseForceEffect(action, _playerId, forceToUse));
        }

        // Allow response(s)
        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()),
                new RespondableWeaponFiringEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                    @Override
                                    protected void destinyDraws(final SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float totalDestiny) {
                                        final GameState gameState = game.getGameState();
                                        if (totalDestiny == null) {
                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                            return;
                                        }

                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));

                                        final Collection<PhysicalCard> characters = Filters.filterActive(game, _weaponOrCardWithPermanentWeapon, Filters.and(Filters.opponents(_playerId), Filters.character,
                                                Filters.present(_weaponOrCardWithPermanentWeapon), Filters.canBeTargetedBy(_weaponOrCardWithPermanentWeapon, _permanentWeapon, TargetingReason.TO_BE_CAPTURED)));
                                        if (!characters.isEmpty()) {
                                            action.appendEffect(
                                                    new RefreshPrintedDestinyValuesEffect(action, characters) {
                                                        @Override
                                                        protected void refreshedPrintedDestinyValues() {
                                                            Collection<PhysicalCard> validToCapture = Filters.filter(characters, game, Filters.destinyEqualTo(totalDestiny));
                                                            if (!validToCapture.isEmpty()) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new ChooseCharacterOnTableToCaptureEffect(action, _playerId, Filters.in(validToCapture), action.getCardFiringWeapon()));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: No characters present with matching destiny number that can be captured");
                                                            }
                                                        }
                                                    }
                                            );
                                        }
                                        else {
                                            gameState.sendMessage("Result: No characters present that can be captured");
                                        }
                                    }

                                    @Override
                                    protected List<OptionalGameTextTriggerAction> getGameTextOptionalTotalDestinyTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
                                        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();
                                        if (Filters.canSpot(game, self, Filters.and(Filters.any_bounty, Filters.atSameLocation(self)))) {
                                            final OptionalGameTextTriggerAction action1 = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                                            action1.setText("Add 1 to total weapon destiny");
                                            // Perform result(s)
                                            action1.appendEffect(
                                                    new ModifyTotalWeaponDestinyEffect(action1, 1));
                                            actions.add(action1);

                                            final OptionalGameTextTriggerAction action2 = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                                            action2.setText("Subtract 1 from total weapon destiny");
                                            // Perform result(s)
                                            action2.appendEffect(
                                                    new ModifyTotalWeaponDestinyEffect(action2, -1));
                                            actions.add(action2);
                                        }
                                        return actions;
                                    }
                                }
                        );
                    }
                });
        return action;
    }

    /**
     * Builds a fire weapon action for Bossk's Mortar Gun.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponBossksMortarGunAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Pay cost(s)
        float forceToUse = getUseForceCost(action.getCardFiringWeapon());
        if (forceToUse > 0) {
            action.appendCost(
                    new UseForceEffect(action, _playerId, forceToUse));
        }

        // Allow response(s)
        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()),
                new RespondableWeaponFiringEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                    @Override
                                    protected void destinyDraws(final SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float totalDestiny) {
                                        final GameState gameState = game.getGameState();
                                        final PhysicalCard cardFiringWeapon = gameState.getWeaponFiringState().getCardFiringWeapon();
                                        if (totalDestiny == null) {
                                            gameState.sendMessage("Result: Gun explodes due to failed weapon destiny draw");
                                            action.appendEffect(
                                                    new LoseCardFromTableEffect(action, cardFiringWeapon));
                                            return;
                                        }

                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));

                                        if (totalDestiny == 0) {
                                            gameState.sendMessage("Result: Gun explodes");
                                            action.appendEffect(
                                                    new LoseCardFromTableEffect(action, cardFiringWeapon));
                                            return;
                                        }

                                        final Collection<PhysicalCard> cards = Filters.filterActive(game, _weaponOrCardWithPermanentWeapon,
                                                Filters.and(Filters.present(_weaponOrCardWithPermanentWeapon), Filters.canBeTargetedBy(_weaponOrCardWithPermanentWeapon, _permanentWeapon, TargetingReason.TO_BE_LOST)));
                                        if (!cards.isEmpty()) {
                                            action.appendEffect(
                                                    new RefreshPrintedDestinyValuesEffect(action, cards) {
                                                        @Override
                                                        protected void refreshedPrintedDestinyValues() {
                                                            Collection<PhysicalCard> validToMakeLost = Filters.filter(cards, game, Filters.destinyEqualTo(totalDestiny));
                                                            if (!validToMakeLost.isEmpty()) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new ChooseCardToLoseFromTableEffect(action, _playerId, Filters.in(validToMakeLost)));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: No cards present with matching destiny number that can be lost");
                                                            }
                                                        }
                                                    }
                                            );
                                        }
                                        else {
                                            gameState.sendMessage("Result: No cards present that can be lost");
                                        }
                                    }
                                }
                        );
                    }
                });
        return action;
    }

    /**
     * Builds a fire weapon action for Laser Cannon Battery.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponLaserCannonBatteryAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, action.getPerformingPlayer(), "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        chooseAmountOfForceToUseAndSetX(action, cardTargeted);

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(cardFiredAt);
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueForX = game.getModifiersQuerying().getVariableValue(gameState, _weaponOrCardWithPermanentWeapon, Variable.X, 0);
                                                        gameState.sendMessage("Value for X: " + GuiUtils.formatAsString(valueForX));
                                                        float valueToCompare;
                                                        if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                            valueToCompare = _defenseValueAsCharacter;
                                                        }
                                                        else {
                                                            valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                        }
                                                        gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));

                                                        if ((totalDestiny + valueForX) > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            action.appendEffect(
                                                                    new HitCardEffect(action, cardFiredAt, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                            action.appendEffect(
                                                                    new LoseForceEffect(action, _opponent, 1));
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for X-wing Laser Cannon and SFS L-s9.3 Laser Cannons.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponXwingLaserCannonAndSFSLs93LaserCannonsAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, action.getPerformingPlayer(), "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        chooseAmountOfForceToUseAndSetX(action, cardTargeted);

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(cardFiredAt);
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueForX = game.getModifiersQuerying().getVariableValue(gameState, _weaponOrCardWithPermanentWeapon, Variable.X, 0);
                                                        gameState.sendMessage("Value for X: " + GuiUtils.formatAsString(valueForX));
                                                        float valueToCompare;
                                                        if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                            valueToCompare = _defenseValueAsCharacter;
                                                        }
                                                        else {
                                                            valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                        }
                                                        gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));

                                                        if ((totalDestiny + valueForX) > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            if (valueForX == 3) {
                                                                action.appendEffect(
                                                                        new LoseCardFromTableEffect(action, cardFiredAt));
                                                            }
                                                            else {
                                                                action.appendEffect(
                                                                        new HitCardEffect(action, cardFiredAt, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                            }
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Causes the player to choose the amount of Force to use and sets X.
     * @param action the fire weapon action
     * @param cardTargeted the card targeted
     */
    private void chooseAmountOfForceToUseAndSetX(final FireSingleWeaponAction action, PhysicalCard cardTargeted) {
        int forceToUseMin = getUseForceCostRangeMin(cardTargeted);
        int forceToUseMax = Math.min(getUseForceCostRangeMax(cardTargeted), _game.getModifiersQuerying().getForceAvailableToUse(_game.getGameState(), _playerId));

        if (forceToUseMax > forceToUseMin) {
            action.appendCost(
                    new PlayoutDecisionEffect(action, _playerId,
                            new IntegerAwaitingDecision("Choose number for X and Force to use", forceToUseMin, forceToUseMax, forceToUseMin) {
                                @Override
                                public void decisionMade(int result) throws DecisionResultInvalidException {
                                    action.appendCost(
                                            new UseForceEffect(action, _playerId, result));
                                    action.appendCost(
                                            new SetInitialWeaponFiringCalculationVariableEffect(action, _weaponOrCardWithPermanentWeapon, result));
                                }
                            }));
        }
        else if (forceToUseMin > 0) {
            action.appendCost(
                    new UseForceEffect(action, _playerId, forceToUseMin));
            action.appendCost(
                    new SetInitialWeaponFiringCalculationVariableEffect(action, _weaponOrCardWithPermanentWeapon, forceToUseMin));
        }
    }

    /**
     * Builds a fire weapon action for A-wing Cannon.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponAwingCannonAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, action.getPerformingPlayer(), "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(cardFiredAt);
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                            valueToCompare = _defenseValueAsCharacter;
                                                        }
                                                        else {
                                                            valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                        }
                                                        gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));

                                                        if (totalDestiny > (2 * valueToCompare)) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            action.appendEffect(
                                                                    new HitCardEffect(action, cardFiredAt, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for Force Pike.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponForcePikeAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(cardFiredAt);
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                            valueToCompare = _defenseValueAsCharacter;
                                                        }
                                                        else {
                                                            valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                        }
                                                        gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));

                                                        if ((totalDestiny + 1) > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            if (Filters.Royal_Guard.accepts(game, gameState.getWeaponFiringState().getCardFiringWeapon())) {
                                                                action.appendEffect(
                                                                        new HitCardAndResetPowerEffect(action, cardFiredAt, 0, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                            }
                                                            else {
                                                                action.appendEffect(
                                                                        new HitCardEffect(action, cardFiredAt, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                            }
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for an Ewok Spear.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponEwokSpearAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> playersDestinyCardDraws, List<Float> playersDestinyDrawValues, final Float playersTotalDestiny) {
                                                        action.appendEffect(
                                                                new DrawDestinyEffect(action, _opponent, 1) {
                                                                    @Override
                                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> opponentsDestinyCardDraws, List<Float> opponentsDestinyDrawValues, Float opponentsTotalDestiny) {
                                                                        GameState gameState = game.getGameState();
                                                                        if (playersTotalDestiny == null) {
                                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                                            return;
                                                                        }
                                                                        else if (opponentsTotalDestiny == null) {
                                                                            gameState.sendMessage("Result: Successful due to failed opponent's destiny draw");
                                                                            if (Filters.creature.accepts(game, cardFiredAt)) {
                                                                                action.appendEffect(
                                                                                        new ResetFerocityUntilEndOfTurnEffect(action, cardFiredAt, 0));
                                                                            }
                                                                            else {
                                                                                action.appendEffect(
                                                                                        new ResetPowerUntilEndOfTurnEffect(action, cardFiredAt, 0));
                                                                            }
                                                                            return;
                                                                        }

                                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(playersTotalDestiny));
                                                                        float ewoksPower = game.getModifiersQuerying().getPower(game.getGameState(), gameState.getWeaponFiringState().getCardFiringWeapon());
                                                                        gameState.sendMessage("Ewok's power: " + GuiUtils.formatAsString(ewoksPower));
                                                                        gameState.sendMessage("Opponent's total destiny: " + GuiUtils.formatAsString(opponentsTotalDestiny));

                                                                        if ((playersTotalDestiny + ewoksPower) > opponentsTotalDestiny) {
                                                                            gameState.sendMessage("Result: Succeeded");
                                                                            if (Filters.creature.accepts(game, cardFiredAt)) {
                                                                                action.appendEffect(
                                                                                        new ResetFerocityUntilEndOfTurnEffect(action, cardFiredAt, 0));
                                                                            }
                                                                            else {
                                                                                action.appendEffect(
                                                                                        new ResetPowerUntilEndOfTurnEffect(action, cardFiredAt, 0));
                                                                            }
                                                                        }
                                                                        else {
                                                                            gameState.sendMessage("Result: Failed");
                                                                        }
                                                                    }
                                                                }
                                                        );
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for Lando's Blaster Rifle.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponLandosBlasterRifleAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        if (Filters.Lando.accepts(_game, _game.getGameState().getWeaponFiringState().getCardFiringWeapon())
                                                && Filters.character.accepts(_game, cardFiredAt)) {
                                            action.appendEffect(
                                                    new ModifyPowerUntilEndOfTurnEffect(action, cardFiredAt, -2));
                                        }
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(cardFiredAt);
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                            valueToCompare = _defenseValueAsCharacter;
                                                        }
                                                        else {
                                                            valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                        }
                                                        gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));

                                                        if ((totalDestiny + 1) > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            action.appendEffect(
                                                                    new HitCardEffect(action, cardFiredAt, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for weapons that only modify forfeit for remainder of turn if successful.
     * @param numDestiny the number of weapon destiny to draw
     * @param plusOrMinus the amount to add to or subtract from total destiny during the calculation
     * @param statistic the statistic to compare total weapon destiny against
     * @param forfeitModifyAmount the amount to modify forfeit if firing is successful
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponModifyForfeitAction(final int numDestiny, final int plusOrMinus, final Statistic statistic, final float forfeitModifyAmount) {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, numDestiny, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        if (statistic == Statistic.DEFENSE_VALUE || statistic == Statistic.MANEUVER || statistic == Statistic.ABILITY) {
                                                            return Collections.singletonList(cardFiredAt);
                                                        }
                                                        return Collections.emptyList();
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (statistic == Statistic.DEFENSE_VALUE) {
                                                            if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                                valueToCompare = _defenseValueAsCharacter;
                                                            }
                                                            else {
                                                                valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                            }
                                                            gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));
                                                        }
                                                        else {
                                                            throw new UnsupportedOperationException("Invalid statistic " + (statistic != null ? statistic.getHumanReadable() : null));
                                                        }

                                                        if ((totalDestiny + plusOrMinus) > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            action.appendEffect(
                                                                    new ModifyForfeitUntilEndOfTurnEffect(action, cardFiredAt, forfeitModifyAmount));
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for Cloud City Blaster.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponCloudCityBlasterAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(cardFiredAt);
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                            valueToCompare = _defenseValueAsCharacter;
                                                        }
                                                        else {
                                                            valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                        }
                                                        gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));

                                                        if (totalDestiny > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            action.appendEffect(
                                                                    new HitCardAndMayNotBeUsedToSatisfyAttritionEffect(action, cardFiredAt, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for Luke's Blaster Pistol.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponLukesBlasterPistolAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        if (Filters.Luke.accepts(_game, _game.getGameState().getWeaponFiringState().getCardFiringWeapon())) {
                                            action.appendEffect(
                                                    new CancelImmunityToAttritionUntilEndOfTurnEffect(action, cardFiredAt, "Cancels " + GameUtils.getCardLink(cardFiredAt) + "'s immunity to attrition"));
                                        }
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(cardFiredAt);
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                            valueToCompare = _defenseValueAsCharacter;
                                                        }
                                                        else {
                                                            valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                        }
                                                        gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));

                                                        if (totalDestiny > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            action.appendEffect(
                                                                    new HitCardEffect(action, cardFiredAt, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for Luke's Blaster Pistol (V).
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponLukesBlasterPistolVAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new CancelImmunityToAttritionUntilEndOfTurnEffect(action, cardFiredAt, "Cancels " + GameUtils.getCardLink(cardFiredAt) + "'s immunity to attrition"));
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(cardFiredAt);
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                            valueToCompare = _defenseValueAsCharacter;
                                                        }
                                                        else {
                                                            valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                        }
                                                        gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));

                                                        if ((totalDestiny + 2) > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            action.appendEffect(
                                                                    new HitCardAndResetForfeitEffect(action, cardFiredAt, 0, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for 4-LOM's Concussion Rifle.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeapon4LOMsConcussionRifleAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(cardFiredAt);
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        Integer distance = game.getModifiersQuerying().getDistanceBetweenSites(gameState, _weaponOrCardWithPermanentWeapon, cardFiredAt);
                                                        gameState.sendMessage("Distance to target: " + distance);
                                                        float valueToCompare;
                                                        if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                            valueToCompare = _defenseValueAsCharacter;
                                                        }
                                                        else {
                                                            valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                        }
                                                        gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));

                                                        if (distance != null && ((totalDestiny - distance) > valueToCompare)) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            action.appendEffect(
                                                                    new HitCardEffect(action, cardFiredAt, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for IG-88's Neural Inhibitor.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponIG88sNeuralInhibitorAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(cardFiredAt);
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare = game.getModifiersQuerying().getAbility(game.getGameState(), cardFiredAt);
                                                        gameState.sendMessage("Ability: " + GuiUtils.formatAsString(valueToCompare));

                                                        if ((totalDestiny + 1) > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            action.appendEffect(
                                                                    new ResetPowerForfeitAndLandspeedUntilEndOfYourNextTurnEffect(action, _playerId, cardFiredAt, 0));
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for Zuckuss' Snare Rifle.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponZuckussSnareRifleAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(cardFiredAt);
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                            valueToCompare = _defenseValueAsCharacter;
                                                        }
                                                        else {
                                                            valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                        }
                                                        gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));

                                                        if (Filters.character.accepts(game, cardFiredAt)
                                                                || (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt))) {
                                                            if ((totalDestiny - 1) > valueToCompare) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new CaptureCharacterOnTableEffect(action, cardFiredAt, action.getCardFiringWeapon()));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                        else if (Filters.creature.accepts(game, cardFiredAt)) {
                                                            if ((totalDestiny + 1) > valueToCompare) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new LoseCardFromTableEffect(action, cardFiredAt));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for Concussion Grenade.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponConcussionGrenadeAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Valid sites to 'throw' grenade
        Filter siteFilter = Filters.sameOrAdjacentSite(_weaponOrCardWithPermanentWeapon);
        if (_game.getGameState().isDuringAttack()) {
            siteFilter = Filters.and(Filters.attackLocation, siteFilter);
        }
        if (_game.getGameState().isDuringBattle()) {
            siteFilter = Filters.and(Filters.battleLocation, siteFilter);
        }

        // Choose target(s)
        action.appendTargeting(
                new ChooseCardOnTableEffect(action, _playerId, "Choose site to 'throw' grenade", siteFilter) {
                    @Override
                    protected void cardSelected(final PhysicalCard siteSelected) {
                        action.addAnimationGroup(siteSelected);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon());
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("'Throw' " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(siteSelected),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected void destinyDraws(final SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float totalDestiny) {
                                                        final GameState gameState = game.getGameState();
                                                        final PhysicalCard cardFiringWeapon = gameState.getWeaponFiringState().getCardFiringWeapon();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed to failed weapon destiny draw");
                                                            action.appendEffect(
                                                                    new LoseCardFromTableEffect(action, cardFiringWeapon));
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));

                                                        if (totalDestiny == 0) {
                                                            gameState.sendMessage("Result: Failed");
                                                            action.appendEffect(
                                                                    new LoseCardFromTableEffect(action, cardFiringWeapon));
                                                            return;
                                                        }

                                                        final Collection<PhysicalCard> cards = Filters.filterAllOnTable(game,
                                                                Filters.and(Filters.or(Filters.character, Filters.weapon, Filters.device), Filters.present(siteSelected), Filters.canBeTargetedBy(action.getWeaponToFire())));
                                                        if (!cards.isEmpty()) {
                                                            action.appendEffect(
                                                                    new RefreshPrintedDestinyValuesEffect(action, cards) {
                                                                        @Override
                                                                        protected void refreshedPrintedDestinyValues() {
                                                                            Collection<PhysicalCard> validToMakeLost = Filters.filter(cards, game, Filters.destinyEqualTo(totalDestiny));
                                                                            if (!validToMakeLost.isEmpty()) {
                                                                                gameState.sendMessage("Result: Succeeded");
                                                                                action.appendEffect(
                                                                                        new LoseCardsFromTableEffect(action, validToMakeLost, true));
                                                                            } else {
                                                                                gameState.sendMessage("Result: No characters, weapons, or devices present with matching destiny number");
                                                                            }
                                                                            action.appendEffect(
                                                                                    new LoseCardFromTableEffect(action, action.getWeaponToFire()));
                                                                        }
                                                                    }
                                                            );
                                                        } else {
                                                            gameState.sendMessage("Result: No characters, weapons, or devices present");
                                                            action.appendEffect(
                                                                    new LoseCardFromTableEffect(action, action.getWeaponToFire()));
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );
        return action;
    }

    /**
     * Builds a fire weapon action for Thermal Detonator.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponThermalDetonatorAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Pay cost(s)
        action.appendCost(
                new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(final SwccgGame game) {
                        final PhysicalCard site = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), action.getWeaponToFire());
                        action.addAnimationGroup(site);
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon());
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("'Detonate' " + GameUtils.getCardLink(action.getWeaponToFire()),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 3, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected void destinyDraws(final SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float totalDestiny) {
                                                        final GameState gameState = game.getGameState();
                                                        final PhysicalCard cardFiringWeapon = gameState.getWeaponFiringState().getCardFiringWeapon();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed to failed weapon destiny draw");
                                                            action.appendEffect(
                                                                    new LoseCardFromTableEffect(action, cardFiringWeapon));
                                                            return;
                                                        }

                                                        final Set<Float> destinyNumbers = new TreeSet<Float>();
                                                        StringBuilder destinyNumbersAsText = new StringBuilder();
                                                        for (Float destinyDrawValue : destinyDrawValues) {
                                                            if (destinyDrawValue != null) {
                                                                destinyNumbers.add(destinyDrawValue);
                                                                destinyNumbersAsText.append(GuiUtils.formatAsString(destinyDrawValue)).append(", ");
                                                            }
                                                        }
                                                        if (destinyNumbersAsText.length() > 2) {
                                                            destinyNumbersAsText.setLength(destinyNumbersAsText.length() - 2);
                                                            gameState.sendMessage("Destiny numbers: " + destinyNumbersAsText);
                                                        }

                                                        final Collection<PhysicalCard> cards = Filters.filterAllOnTable(game,
                                                                Filters.and(Filters.or(Filters.character, Filters.weapon, Filters.device, Filters.starship, Filters.vehicle), Filters.not(Filters.imprisoned), Filters.at(Filters.sameLocationId(site))));
                                                        if (!cards.isEmpty()) {
                                                            action.appendEffect(
                                                                    new RefreshPrintedDestinyValuesEffect(action, cards) {
                                                                        @Override
                                                                        protected void refreshedPrintedDestinyValues() {
                                                                            Filter destinyFilter = Filters.none;
                                                                            for (Float destinyNumber : destinyNumbers) {
                                                                                destinyFilter = Filters.or(destinyFilter, Filters.destinyEqualTo(destinyNumber));
                                                                            }
                                                                            Collection<PhysicalCard> validToMakeLost = Filters.filter(cards, game, destinyFilter);
                                                                            if (!validToMakeLost.isEmpty()) {
                                                                                gameState.sendMessage("Result: Succeeded");
                                                                                action.appendEffect(
                                                                                        new LoseCardsFromTableEffect(action, validToMakeLost, true));
                                                                            } else {
                                                                                gameState.sendMessage("Result: No cards at same site with matching destiny number");
                                                                            }
                                                                            action.appendEffect(
                                                                                    new LoseCardFromTableEffect(action, action.getWeaponToFire()));
                                                                        }
                                                                    }
                                                            );
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: No cards at same site");
                                                            action.appendEffect(
                                                                    new LoseCardFromTableEffect(action, action.getWeaponToFire()));
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                }
                        );
                    }
                }
        );
        return action;
    }

    /**
     * Builds a fire weapon action for Power Harpoon.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponPowerHarpoonAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);
                                        final PhysicalCard cardFiringWeapon = _game.getGameState().getWeaponFiringState().getCardFiringWeapon();

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(cardFiringWeapon);
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float maneuver = game.getModifiersQuerying().getManeuver(gameState, cardFiringWeapon);
                                                        gameState.sendMessage("Vehicle's maneuver: " + GuiUtils.formatAsString(maneuver));

                                                        if ((totalDestiny + maneuver) > 8) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            action.appendEffect(
                                                                    new CrashVehicleEffect(action, cardFiredAt, _weaponOrCardWithPermanentWeapon));
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }

                                                        if (totalDestiny == 0) {
                                                            action.appendEffect(
                                                                    new CrashVehicleEffect(action, cardFiringWeapon, _weaponOrCardWithPermanentWeapon));
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for Probe Droid Laser.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponProbeDroidLaserAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(cardFiredAt);
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (Filters.artillery_weapon.accepts(game, cardFiredAt)) {
                                                            valueToCompare = 5;
                                                        }
                                                        else {
                                                            if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                                valueToCompare = _defenseValueAsCharacter;
                                                            }
                                                            else {
                                                                valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                            }
                                                        }
                                                        gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));

                                                        if (Filters.artillery_weapon.accepts(game, cardFiredAt)) {
                                                            if (totalDestiny > valueToCompare) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new LoseCardFromTableEffect(action, cardFiredAt));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                        else if (Filters.or(Filters.character, Filters.creature).accepts(game, cardFiredAt)
                                                                || (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt))) {
                                                            if (totalDestiny > valueToCompare) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new HitCardEffect(action, cardFiredAt, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for Jawa Ion Gun.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponJawaIonGunAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(cardFiredAt);
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));

                                                        if (Filters.droid.accepts(game, cardFiredAt)) {
                                                            float valueToCompare = game.getModifiersQuerying().getForfeit(game.getGameState(), cardFiredAt);
                                                            gameState.sendMessage("Forfeit: " + GuiUtils.formatAsString(valueToCompare));

                                                            if ((totalDestiny + 1) > valueToCompare) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new StealCardToLocationEffect(action, cardFiredAt));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                        else if (Filters.non_droid_character.accepts(game, cardFiredAt)
                                                                || (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt))) {
                                                            float valueToCompare;
                                                            if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                                valueToCompare = _defenseValueAsCharacter;
                                                            }
                                                            else {
                                                                valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                            }
                                                            gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));

                                                            if (totalDestiny == valueToCompare) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new ExcludeFromBattleEffect(action, cardFiredAt, _permanentWeapon, action.getCardFiringWeapon()));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for Jawa Blaster.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponJawaBlasterAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(cardFiredAt);
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        final PhysicalCard cardFiringWeapon = gameState.getWeaponFiringState().getCardFiringWeapon();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Jawa Blaster 'explodes' due to failed weapon destiny draw");
                                                            action.appendEffect(
                                                                    new LoseCardFromTableEffect(action, cardFiringWeapon));
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));

                                                        if (totalDestiny == 0) {
                                                            gameState.sendMessage("Result: Jawa Blaster 'explodes'");
                                                            action.appendEffect(
                                                                    new LoseCardFromTableEffect(action, cardFiringWeapon));
                                                            return;
                                                        }

                                                        float valueToCompare;
                                                        if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                            valueToCompare = _defenseValueAsCharacter;
                                                        }
                                                        else {
                                                            valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                        }
                                                        gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));

                                                        if ((totalDestiny - 1) > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            action.appendEffect(
                                                                    new HitCardEffect(action, cardFiredAt, _weaponOrCardWithPermanentWeapon, _permanentWeapon, cardFiringWeapon));
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for Atgar Laser Cannon.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponAtgarLaserCannonAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose card to fire weapon
        action.appendTargeting(
                new ChooseCardsOnTableEffect(action, _playerId, "Choose character to fire weapon", _artilleryWeaponMayFireWithoutWarriorPresent ? 0 : 1, 1, getValidWeaponUsers()) {
                    @Override
                    protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
                        final PhysicalCard selectedCard = !selectedCards.isEmpty() ? selectedCards.iterator().next() : null;
                        if (selectedCard != null) {
                            action.setCardFiringWeapon(selectedCard);
                            action.addAnimationGroup(selectedCard);
                        }
                        // Choose target(s)
                        action.appendTargeting(
                                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                                    @Override
                                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                                        return true;
                                    }
                                    @Override
                                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                                        action.addAnimationGroup(cardTargeted);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                                        // Pay cost(s)
                                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                                        if (forceToUse > 0) {
                                            action.appendCost(
                                                    new UseForceEffect(action, _playerId, forceToUse));
                                        }

                                        // Allow response(s)
                                        String actionMsg = (selectedCard != null ? ("Have " + GameUtils.getCardLink(selectedCard) + " fire ") : "Fire ") + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted);
                                        action.allowResponses(actionMsg,
                                                new RespondableWeaponFiringEffect(action) {
                                                    @Override
                                                    protected void performActionResults(Action targetingAction) {
                                                        // Get the targeted card(s) from the action using the targetGroupId.
                                                        // This needs to be done in case the target(s) were changed during the responses.
                                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                                        // Perform result(s)
                                                        action.appendEffect(
                                                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                                    @Override
                                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                                        return Collections.singletonList(cardFiredAt);
                                                                    }
                                                                    @Override
                                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                        GameState gameState = game.getGameState();
                                                                        if (totalDestiny == null) {
                                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                                            return;
                                                                        }

                                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));

                                                                        if (Filters.hasArmorDefined.accepts(game, cardFiredAt)) {
                                                                            float valueToCompare = game.getModifiersQuerying().getArmor(game.getGameState(), cardFiredAt);
                                                                            gameState.sendMessage("Armor: " + GuiUtils.formatAsString(valueToCompare));

                                                                            if ((totalDestiny + 2) > valueToCompare) {
                                                                                gameState.sendMessage("Result: Succeeded");
                                                                                action.appendEffect(
                                                                                        new CrashVehicleEffect(action, cardFiredAt, _weaponOrCardWithPermanentWeapon));
                                                                            }
                                                                            else {
                                                                                gameState.sendMessage("Result: Failed");
                                                                            }
                                                                        }
                                                                        else if (Filters.hasManeuverDefined.accepts(game, cardFiredAt)) {
                                                                            float valueToCompare = game.getModifiersQuerying().getManeuver(game.getGameState(), cardFiredAt);
                                                                            gameState.sendMessage("Maneuver: " + GuiUtils.formatAsString(valueToCompare));

                                                                            if ((totalDestiny + 1) > valueToCompare) {
                                                                                gameState.sendMessage("Result: Succeeded");
                                                                                action.appendEffect(
                                                                                        new HitCardEffect(action, cardFiredAt, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                                            }
                                                                            else {
                                                                                gameState.sendMessage("Result: Failed");
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                        );
                                                    }
                                                });
                                    }
                                }
                        );
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for weapon fired by a character present.
     * @param numDestiny the number of weapon destiny to draw
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponByCharacterPresentWithHitAction(final int numDestiny) {
        return buildFireWeaponByCharacterPresentWithHitAction(numDestiny, 0);
    }

    /**
     * Builds a fire weapon action for weapon fired by a character present.
     * @param numDestiny the number of weapon destiny to draw
     * @param plusOrMinus the amount to add to or subtract from total destiny during the calculation
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponByCharacterPresentWithHitAction(final int numDestiny, final int plusOrMinus) {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose card to fire weapon
        action.appendTargeting(
                new ChooseCardsOnTableEffect(action, _playerId, "Choose character to fire weapon", _artilleryWeaponMayFireWithoutWarriorPresent ? 0 : 1, 1, getValidWeaponUsers()) {
                    @Override
                    protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
                        final PhysicalCard selectedCard = !selectedCards.isEmpty() ? selectedCards.iterator().next() : null;
                        if (selectedCard != null) {
                            action.setCardFiringWeapon(selectedCard);
                            action.addAnimationGroup(selectedCard);
                        }
                        // Choose target(s)
                        action.appendTargeting(
                                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                                    @Override
                                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                                        return true;
                                    }
                                    @Override
                                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                                        action.addAnimationGroup(cardTargeted);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                                        // Pay cost(s)
                                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                                        if (forceToUse > 0) {
                                            action.appendCost(
                                                    new UseForceEffect(action, _playerId, forceToUse));
                                        }

                                        // Allow response(s)
                                        String actionMsg = (selectedCard != null ? ("Have " + GameUtils.getCardLink(selectedCard) + " fire ") : "Fire ") + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted);
                                        action.allowResponses(actionMsg,
                                                new RespondableWeaponFiringEffect(action) {
                                                    @Override
                                                    protected void performActionResults(Action targetingAction) {
                                                        // Get the targeted card(s) from the action using the targetGroupId.
                                                        // This needs to be done in case the target(s) were changed during the responses.
                                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                                        // Perform result(s)
                                                        action.appendEffect(
                                                                new DrawDestinyEffect(action, _playerId, numDestiny, DestinyType.WEAPON_DESTINY) {
                                                                    @Override
                                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                                        return Collections.singletonList(cardFiredAt);
                                                                    }
                                                                    @Override
                                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                        GameState gameState = game.getGameState();
                                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                                        if (totalDestiny == null) {
                                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                                            return;
                                                                        }

                                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                        float valueToCompare;
                                                                        if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                                            valueToCompare = _defenseValueAsCharacter;
                                                                        }
                                                                        else {
                                                                            valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                                        }
                                                                        gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));

                                                                        if ((totalDestiny + plusOrMinus) > valueToCompare) {
                                                                            gameState.sendMessage("Result: Succeeded");
                                                                            action.appendEffect(
                                                                                    new HitCardEffect(action, cardFiredAt, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                                        }
                                                                        else {
                                                                            gameState.sendMessage("Result: Failed");
                                                                        }
                                                                    }
                                                                }
                                                        );
                                                    }
                                                });
                                    }
                                }
                        );
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for weapon fired by a character present.
     * @param numDestiny the number of weapon destiny to draw
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponByCharacterPresentWithCrashAction(final int numDestiny) {
        return buildFireWeaponByCharacterPresentWithCrashAction(numDestiny, 0);
    }

    /**
     * Builds a fire weapon action for weapon fired by a character present.
     * @param numDestiny the number of weapon destiny to draw
     * @param plusOrMinus the amount to add to or subtract from total destiny during the calculation
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponByCharacterPresentWithCrashAction(final int numDestiny, final int plusOrMinus) {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose card to fire weapon
        action.appendTargeting(
                new ChooseCardsOnTableEffect(action, _playerId, "Choose character to fire weapon", _artilleryWeaponMayFireWithoutWarriorPresent ? 0 : 1, 1, getValidWeaponUsers()) {
                    @Override
                    protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
                        final PhysicalCard selectedCard = !selectedCards.isEmpty() ? selectedCards.iterator().next() : null;
                        if (selectedCard != null) {
                            action.setCardFiringWeapon(selectedCard);
                            action.addAnimationGroup(selectedCard);
                        }
                        // Choose target(s)
                        action.appendTargeting(
                                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                                    @Override
                                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                                        return true;
                                    }
                                    @Override
                                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                                        action.addAnimationGroup(cardTargeted);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                                        // Pay cost(s)
                                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                                        if (forceToUse > 0) {
                                            action.appendCost(
                                                    new UseForceEffect(action, _playerId, forceToUse));
                                        }

                                        // Allow response(s)
                                        String actionMsg = (selectedCard != null ? ("Have " + GameUtils.getCardLink(selectedCard) + " fire ") : "Fire ") + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted);
                                        action.allowResponses(actionMsg,
                                                new RespondableWeaponFiringEffect(action) {
                                                    @Override
                                                    protected void performActionResults(Action targetingAction) {
                                                        // Get the targeted card(s) from the action using the targetGroupId.
                                                        // This needs to be done in case the target(s) were changed during the responses.
                                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                                        // Perform result(s)
                                                        action.appendEffect(
                                                                new DrawDestinyEffect(action, _playerId, numDestiny, DestinyType.WEAPON_DESTINY) {
                                                                    @Override
                                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                                        return Collections.singletonList(cardFiredAt);
                                                                    }
                                                                    @Override
                                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                        GameState gameState = game.getGameState();
                                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                                        if (totalDestiny == null) {
                                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                                            return;
                                                                        }

                                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                        float valueToCompare;
                                                                        if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                                            valueToCompare = _defenseValueAsCharacter;
                                                                        }
                                                                        else {
                                                                            valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                                        }
                                                                        gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));

                                                                        if ((totalDestiny + plusOrMinus) > valueToCompare) {
                                                                            gameState.sendMessage("Result: Succeeded");
                                                                            action.appendEffect(
                                                                                    new CrashVehicleEffect(action, cardFiredAt, _weaponOrCardWithPermanentWeapon));
                                                                        }
                                                                        else {
                                                                            gameState.sendMessage("Result: Failed");
                                                                        }
                                                                    }
                                                                }
                                                        );
                                                    }
                                                });
                                    }
                                }
                        );
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for Planet Defender Ion Cannon
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponPlanetDefenderIonCannonAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare = modifiersQuerying.getArmor(game.getGameState(), cardFiredAt);
                                                        gameState.sendMessage("Armor: " + GuiUtils.formatAsString(valueToCompare));

                                                        if ((totalDestiny + 3) > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            action.appendEffect(
                                                                    new ResetPowerAndHyperspeedEffect(action, cardFiredAt, 0));
                                                            Collection<PhysicalCard> starshipWeapons = Filters.filterAllOnTable(game, Filters.and(Filters.starship_weapon, Filters.attachedTo(cardFiredAt)));
                                                            if (!starshipWeapons.isEmpty()) {
                                                                action.appendEffect(
                                                                        new LoseCardsFromTableEffect(action, starshipWeapons, true));
                                                            }
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for Proton Bombs for 'orbital bombardment' during Force drain.
     * @return the action
     */
    public FireWeaponAction buildFireWeaponProtonBombsAsOrbitalBombardmentAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game) + " to 'collapse' site");

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, final PhysicalCard siteTargeted) {
                        action.addAnimationGroup(siteTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(siteTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), siteTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(siteTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float totalDestiny) {
                                                        final GameState gameState = game.getGameState();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        if (totalDestiny > 4) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            action.appendEffect(
                                                                    new CollapseSiteEffect(action, siteTargeted));
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for Proton Bombs for 'carpet bombing' during Bombing Run battle.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponProtonBombsAsCarpetBombingAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game) + " in Bombing Run");

        // Pay cost(s)
        float forceToUse = getUseForceCost(action.getCardFiringWeapon());
        if (forceToUse > 0) {
            action.appendCost(
                    new UseForceEffect(action, _playerId, forceToUse));
        }

        // Allow response(s)
        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()),
                new RespondableWeaponFiringEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                    @Override
                                    protected void destinyDraws(final SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float totalDestiny) {
                                        final GameState gameState = game.getGameState();
                                        if (totalDestiny == null) {
                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                            return;
                                        }

                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));

                                        final Collection<PhysicalCard> cards = Filters.filterAllOnTable(game,
                                                Filters.and(Filters.or(Filters.character, Filters.starship, Filters.vehicle), Filters.atSameSite(_weaponOrCardWithPermanentWeapon),
                                                        Filters.not(Filters.or(Filters.makingBombingRun, Filters.attachedToWithRecursiveChecking(Filters.makingBombingRun)))));
                                        if (!cards.isEmpty()) {
                                            action.appendEffect(
                                                    new RefreshPrintedDestinyValuesEffect(action, cards) {
                                                        @Override
                                                        protected void refreshedPrintedDestinyValues() {
                                                            Collection<PhysicalCard> cardsToMakeLost = Filters.filter(cards, game, Filters.destinyEqualTo(totalDestiny));
                                                            if (!cardsToMakeLost.isEmpty()) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new LoseCardsFromTableEffect(action, cardsToMakeLost, true));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: No cards at site with matching destiny number that can be lost");
                                                            }
                                                        }
                                                    }
                                            );
                                        }
                                        else {
                                            gameState.sendMessage("Result: No cards present that can be lost");
                                        }
                                    }
                                }
                        );
                    }
                });
        return action;
    }

    /**
     * Builds a fire weapon action for a Gaderffi Stick.
     * @return the action
     */
    public FireWeaponAction buildFireWeaponGaderffiStickAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 2, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float totalDestiny) {
                                                        final GameState gameState = game.getGameState();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        if (totalDestiny > 5) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            Collection<PhysicalCard> weaponsKnockedAway = Filters.filterActive(game, action.getWeaponToFire(),
                                                                    Filters.or(Filters.and(cardFiredAt, Filters.hasPermanentWeapon), Filters.and(Filters.weapon, Filters.attachedTo(cardFiredAt))));
                                                            if (!weaponsKnockedAway.isEmpty()) {
                                                                gameState.cardAffectsCards(action.getPerformingPlayer(), action.getWeaponToFire(), weaponsKnockedAway);
                                                                action.appendEffect(
                                                                        new AddUntilEndOfBattleModifierEffect(action,
                                                                                new MayNotBeFiredModifier(action.getWeaponToFire(), Filters.or(Filters.in(weaponsKnockedAway), Filters.permanentWeaponOf(Filters.in(weaponsKnockedAway)))),
                                                                                GameUtils.getCardLink(cardFiredAt) + "'s weapons are 'knocked away'"));
                                                            }
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                }
                        );
                    }
                }
        );
        return action;
    }


    /**
     * Builds a fire weapon action for F-11D Blaster Rifle.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponF11DBlasterRifleAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }

                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(cardFiredAt);
                                                    }

                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                            valueToCompare = _defenseValueAsCharacter;
                                                        } else {
                                                            valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                        }
                                                        gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));

                                                        if ((totalDestiny + (Filters.stormtrooper.accepts(game, gameState.getWeaponFiringState().getCardFiringWeapon()) ? 1 : 0)) > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            action.appendEffect(
                                                                    new HitCardAndMayNotBeUsedToSatisfyAttritionEffect(action, cardFiredAt, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                        } else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for Inquisitor Lightsaber.
     *
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponInquisitorLightsaberAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, action.getPerformingPlayer(), "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }

                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 2, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(cardFiredAt);
                                                    }

                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                            valueToCompare = _defenseValueAsCharacter;
                                                        } else {
                                                            valueToCompare = game.getModifiersQuerying().getDefenseValue(game.getGameState(), cardFiredAt);
                                                        }
                                                        gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));

                                                        if (totalDestiny > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            action.appendEffect(
                                                                    new HitCardEffect(action, cardFiredAt, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                            Collection<PhysicalCard> hatredCards = Filters.filterStacked(game, Filters.and(Filters.stackedOn(cardFiredAt), Filters.hatredCard));
                                                            action.appendEffect(
                                                                    new TakeStackedCardsIntoHandEffect(action, _weaponOrCardWithPermanentWeapon.getOwner(), 1, hatredCards.size(), cardFiredAt, Filters.hatredCard)
                                                            );
                                                        } else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for Kylo Ren's Lightsaber.
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponKyloRensLightsaberAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, action.getPerformingPlayer(), "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 2, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(cardFiredAt);
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                            valueToCompare = _defenseValueAsCharacter;
                                                        }
                                                        else {
                                                            valueToCompare = game.getModifiersQuerying().getDefenseValue(game.getGameState(), cardFiredAt);
                                                        }
                                                        gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));

                                                        if (totalDestiny > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            action.appendEffect(
                                                                    new HitCardResetForfeitAndBothPlayersLoseForceEffect(action, cardFiredAt, 0, 1, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for Blaster Rifle (V).
     * @return the action
     */
    public FireSingleWeaponAction buildBlasterRifleVAction(final int plusorminus) {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(cardFiredAt);
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny + plusorminus));
                                                        float valueToCompare;
                                                        if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                            valueToCompare = _defenseValueAsCharacter;
                                                        }
                                                        else {
                                                            valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                        }
                                                        gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));

                                                        if (totalDestiny + plusorminus > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            action.appendEffect(
                                                                    new HitCardAndMayNotBeUsedToSatisfyAttritionEffect(action, cardFiredAt, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    /**
     * Builds a fire weapon action for Luke's Hunting Rifle (V).
     * @return the action
     */
    public FireSingleWeaponAction buildLukesHuntingRifleVAction() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(cardFiredAt);
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                            valueToCompare = _defenseValueAsCharacter;
                                                        }
                                                        else {
                                                            valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                        }
                                                        gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));

                                                        if (totalDestiny + 2 > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            PhysicalCard cardFiredBy = gameState.getWeaponFiringState().getCardFiringWeapon();
                                                            if (cardFiredBy != null && Filters.or(Filters.Luke, Filters.Owen).accepts(game, cardFiredBy)) {
                                                                action.appendEffect(
                                                                        new HitCardResetForfeitAndRetrieveForceEffect(action, cardFiredAt, 0, 1, _weaponOrCardWithPermanentWeapon, _permanentWeapon, cardFiredBy));
                                                            }
                                                            else {
                                                                action.appendEffect(
                                                                        new HitCardEffect(action, cardFiredAt, _weaponOrCardWithPermanentWeapon, _permanentWeapon, cardFiredBy));
                                                            }
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }


    /**
     * Builds a fire weapon action for Dark Jedi Lightsaber (V) and Jedi Lightsaber (V).
     * @return the action
     */
    public FireSingleWeaponAction builderDarkAndLightJediLightSaberV() {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, _playerId, "Choose target", getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        _game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, 2, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(cardFiredAt);
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                            valueToCompare = _defenseValueAsCharacter;
                                                        }
                                                        else {
                                                            valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                        }
                                                        gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));

                                                        if (totalDestiny > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            action.appendEffect(
                                                                    new HitCardAndMayNotBeUsedToSatisfyAttritionEffect(action, cardFiredAt, _weaponOrCardWithPermanentWeapon, _permanentWeapon, gameState.getWeaponFiringState().getCardFiringWeapon()));
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }


    /**
     * Builds a fire weapon action that targets a card present with the weapon, draws destiny, and if successful affects the card's attributes,
     * and can cause Force loss.
     * @param numDestiny the number of weapon destiny to draw
     * @param plusOrMinus the amount to add to or subtract from total destiny during the calculation
     * @param statistic the statistic to compare total weapon destiny against
     * @return the action
     */
    public FireSingleWeaponAction buildFireWeaponWithCancelGameTextAction(final int numDestiny, final int plusOrMinus, final Statistic statistic, final boolean cancelGameTextForRemainderOfTurn) {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(_sourceCard, _weaponOrCardWithPermanentWeapon, _permanentWeapon, _repeatedFiring, _targetedAsCharacter, _defenseValueAsCharacter, _fireAtTargetFilter, _ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(_game) + (_numTargets > 1 ? (" at " + _numTargets + " targets") : ""));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardsOnTableEffect(action, action.getPerformingPlayer(), "Choose target" + GameUtils.s(_numTargets), _numTargets, _numTargets, getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardsTargeted(final int targetGroupId, Collection<PhysicalCard> cardsTargeted) {
                        action.addAnimationGroup(cardsTargeted);
                        _game.getGameState().getWeaponFiringState().setTargets(cardsTargeted);

                        // Pay cost(s)
                        float forceToUse = getUseForceCost(action.getCardFiringWeapon(), cardsTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, _playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(action.getWeaponToFire()) + " at " + GameUtils.getAppendedNames(cardsTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final Collection<PhysicalCard> cardsFiredAt = targetingAction.getPrimaryTargetCards(targetGroupId);
                                        _game.getGameState().getWeaponFiringState().setTargets(cardsFiredAt);

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, _playerId, numDestiny, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        if (statistic == Statistic.DEFENSE_VALUE || statistic == Statistic.MANEUVER || statistic == Statistic.ABILITY) {
                                                            return cardsFiredAt;
                                                        }
                                                        return Collections.emptyList();
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }

                                                        totalDestiny = totalDestiny + plusOrMinus;
                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        List<StandardEffect> effectList = new ArrayList<StandardEffect>();

                                                        float valueToCompare = 0;
                                                        for (PhysicalCard cardFiredAt : cardsFiredAt) {
                                                            if (statistic == Statistic.DEFENSE_VALUE) {
                                                                if (_targetedAsCharacter != null && _targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                                    valueToCompare += _defenseValueAsCharacter;
                                                                } else {
                                                                    valueToCompare += game.getModifiersQuerying().getDefenseValue(game.getGameState(), cardFiredAt);
                                                                }
                                                            } else {
                                                                throw new UnsupportedOperationException("Invalid statistic " + (statistic != null ? statistic.getHumanReadable() : null));
                                                            }
                                                        }
                                                        gameState.sendMessage((_numTargets > 1 ? "Total defense value: " : "Defense value: ") + GuiUtils.formatAsString(valueToCompare));

                                                        if (totalDestiny > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");

                                                            for (PhysicalCard cardFiredAt : cardsFiredAt) {

                                                                if(cancelGameTextForRemainderOfTurn) {
                                                                    action.appendEffect(
                                                                            new CancelGameTextUntilEndOfTurnEffect(action, cardFiredAt));
                                                                } else {
                                                                    action.appendEffect(
                                                                            new CancelGameTextEffect(action, cardFiredAt));
                                                                }
                                                            }
                                                        } else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

}
