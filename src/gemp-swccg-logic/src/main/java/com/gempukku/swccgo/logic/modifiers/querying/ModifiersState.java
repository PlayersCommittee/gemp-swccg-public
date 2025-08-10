package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The functions in this interface are largely concerned with updating or checking on specific data pertaining
 * to non-modifier lists of cards.  For example, every time a character participates in battle, they are added
 * to a list to track their participation; adding them to that list and checking if they are in that list are
 * both operations described by this interface and implemented on ModifiersLogic.
 */
public interface ModifiersState {

	/**
	 * Excludes the specified card from being affected by the modifier.
	 * This is typically used when a card is 'restored' to normal.
	 * @param modifier the modifier
	 * @param card the card
	 */
	void excludeFromBeingAffected(Modifier modifier, PhysicalCard card);

	/**
	 * Determines if the specified card is excluded from being affected by the modifier.
	 * @param modifier the modifier
	 * @param card the card
	 * @return true or false
	 */
	boolean isExcludedFromBeingAffected(Modifier modifier, PhysicalCard card);

	/**
	 * Records that an attack on a creature was initiated at the specified location.
	 *
	 * @param location the location
	 */
	void attackOnCreatureInitiatedAtLocation(PhysicalCard location);

	/**
	 * Determines if an attack on a creature has been initiated at the specified location this turn.
	 *
	 * @param location the location
	 * @return true or false
	 */
	boolean isAttackOnCreatureOccurredAtLocationThisTurn(PhysicalCard location);
	/**
	 * Records that the specified card has participated in an attack on a creature.
	 *
	 * @param card the card
	 */
	void participatedInAttackOnCreature(PhysicalCard card);

	/**
	 * Determines if the specified card has participated in an attack on a creature this turn.
	 *
	 * @param card the card
	 * @return true if card has participated, otherwise false
	 */
	boolean hasParticipatedInAttackOnCreatureThisTurn(PhysicalCard card);

	/**
	 * Records that the specified card has participated in an attack on a non-creature.
	 *
	 * @param card the card
	 */
	void participatedInAttackOnNonCreature(PhysicalCard card);

	/**
	 * Determines if the specified card has participated in an attack on a non-creature this turn.
	 *
	 * @param card the card
	 * @return true if card has participated, otherwise false
	 */
	boolean hasParticipatedInAttackOnNonCreatureThisTurn(PhysicalCard card);

	/**
	 * Records that a battle was initiated at the specified location by the specified player.
	 *
	 * @param playerId the player
	 * @param location the location
	 */
	void battleInitiatedAtLocation(String playerId, PhysicalCard location);

	/**
	 * Gets the number of battles that have been initiated this turn by the specified player.
	 * @param playerId the player
	 * @return the number of battles
	 */
	int getNumBattlesInitiatedThisTurn(String playerId);

	/**
	 * Records that the specified card has participated in a battle at the specified location.
	 *
	 * @param card the card
	 * @param location the location
	 */
	void participatedInBattle(PhysicalCard card, PhysicalCard location);

	/**
	 * Determines if the specified card has participated in a battle this turn.
	 *
	 * @param card the card
	 * @return true if card has participated, otherwise false
	 */
	boolean hasParticipatedInBattle(PhysicalCard card);

	/**
	 * Determines if the specified card has participated in a battle at another location this turn.
	 *
	 * @param card the card
	 * @param location the location
	 * @return true if card has participated, otherwise false
	 */
	boolean hasParticipatedInBattleAtOtherLocation(PhysicalCard card, PhysicalCard location);

	/**
	 * Determines if a battle has been initiated at the specified location this turn.
	 *
	 * @param location the location
	 * @return true or false
	 */
	boolean isBattleOccurredAtLocationThisTurn(PhysicalCard location);

	/**
	 * Records that the specified target card was targeted by the specified weapon.
	 * @param target the target
	 * @param weapon the weapon
	 */
	void targetedByWeapon(PhysicalCard target, PhysicalCard weapon);

	/**
	 * Records that the specified target card was targeted by the specified permanent weapon.
	 * @param target the target
	 * @param permanentWeapon the permanent weapon
	 */
	void targetedByPermanentWeapon(PhysicalCard target, SwccgBuiltInCardBlueprint permanentWeapon);

	/**
	 * Records that a Force drain was initiated (or that something was done "instead of Force draining") at the specified
	 * location.
	 *
	 * @param location the location
	 */
	void forceDrainAttempted(PhysicalCard location);

	/**
	 * Determines if a Force drain has been initiated (or if something was done "instead of Force draining") at the specified
	 * location this turn.
	 *
	 * @param location the location
	 * @return true or false
	 */
	boolean isForceDrainAttemptedThisTurn(PhysicalCard location);

	/**
	 * Records that a Force drain of the specified amount of Force was setFulfilledByOtherAction at the specified location.
	 *
	 * @param location the location
	 * @param amount   the amount of Force
	 */
	void forceDrainPerformed(PhysicalCard location, Float amount);

	/**
	 * Gets the number of Force drains initiated this turn.
	 * @return the amount of Force
	 */
	float getNumForceDrainsInitiatedThisTurn();

	/**
	 * Gets the total amount of Force that has been Force drained this turn.
	 *
	 * @return the amount of Force
	 */
	float getTotalForceDrainedThisTurn();

	/**
	 * Records that the specified card was 'blown away'.
	 *
	 * @param card the card that was 'blown away'
	 */
	void blownAway(PhysicalCard card);

	/**
	 * Determines if a card accepted by the specified filter has been 'blown away'.
	 *
	 * @param gameState the game state
	 * @param filters   the filter
	 * @return true or false
	 */
	boolean isBlownAway(GameState gameState, Filterable filters);

	void weaponUsedBy(PhysicalCard user, PhysicalCard weapon);

	List<Integer> otherWeaponsUsed(PhysicalCard user, PhysicalCard weapon);

	List<Integer> otherWeaponsUsed(PhysicalCard user, SwccgBuiltInCardBlueprint permanentWeapon);

	void deviceUsedBy(PhysicalCard user, PhysicalCard device);

	List<Integer> otherDevicesUsed(PhysicalCard user, PhysicalCard weapon);
	/**
	 * Records that the specified weapon has been fired in attack.
	 * @param card the card
	 * @param cardFiringWeapon the card firing the weapon
	 * @param complete true if firing completed, false if targeted but not actually fired yet
	 */
	void firedInAttack(PhysicalCard card, PhysicalCard cardFiringWeapon, boolean complete);

	/**
	 * Records that the specified permanent weapon has been fired in attack.
	 * @param permanentWeapon the permanent weapon
	 * @param cardFiringWeapon the card firing the weapon
	 * @param complete true if firing completed, false if targeted but not actually fired yet
	 */
	void firedInAttack(SwccgBuiltInCardBlueprint permanentWeapon, PhysicalCard cardFiringWeapon, boolean complete);

	/**
	 * Gets the number of times the weapon has been fired in the current attack.
	 * @param card the card
	 * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
	 * @return the number of times the weapon has been fired
	 */
	int numTimesFiredInAttack(PhysicalCard card, boolean completeOnly);

	/**
	 * Gets the number of times the permanent weapon has been fired in the current attack.
	 * @param permanentWeapon the permanent weapon
	 * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
	 * @return the number of times the weapon has been fired
	 */
	int numTimesFiredInAttack(SwccgBuiltInCardBlueprint permanentWeapon, boolean completeOnly);

	/**
	 * Gets the weapons that have been fired in the current attack by the specified player.
	 * @param playerId the player
	 * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
	 */
	List<PhysicalCard> getWeaponsFiredInAttackByPlayer(String playerId, boolean completeOnly);

	/**
	 * Gets the permanent weapons that have been fired in the current attack by the specified player.
	 * @param playerId the player
	 * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
	 */
	List<SwccgBuiltInCardBlueprint> getPermanentWeaponsFiredInAttackByPlayer(String playerId, boolean completeOnly);

	/**
	 * Gets the weapons that have been fired in the current attack by the specified weapon user.
	 * @param weaponUser the weapon user
	 * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
	 */
	List<PhysicalCard> getWeaponsFiredInAttackByWeaponUser(PhysicalCard weaponUser, boolean completeOnly);

	/**
	 * Gets the permanent weapons that have been fired in the current attack by the specified weapon user.
	 * @param weaponUser the weapon user
	 * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
	 */
	List<SwccgBuiltInCardBlueprint> getPermanentWeaponsFiredInAttackByWeaponUser(PhysicalCard weaponUser, boolean completeOnly);

	/**
	 * Records that the specified weapon has been fired in battle.
	 * @param card the card
	 * @param cardFiringWeapon the card firing the weapon
	 * @param complete true if firing completed, false if targeted but not actually fired yet
	 */
	void firedInBattle(PhysicalCard card, PhysicalCard cardFiringWeapon, boolean complete);

	/**
	 * Records that the specified permanent weapon has been fired in battle.
	 * @param permanentWeapon the permanent weapon
	 * @param cardFiringWeapon the card firing the weapon
	 * @param complete true if firing completed, false if targeted but not actually fired yet
	 */
	void firedInBattle(SwccgBuiltInCardBlueprint permanentWeapon, PhysicalCard cardFiringWeapon, boolean complete);

	/**
	 * Gets the number of times the weapon has been fired in the current battle.
	 * @param card the card
	 * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
	 * @return the number of times the weapon has been fired
	 */
	int numTimesFiredInBattle(PhysicalCard card, boolean completeOnly);

	/**
	 * Gets the number of times the permanent weapon has been fired in the current battle.
	 * @param permanentWeapon the permanent weapon
	 * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
	 * @return the number of times the weapon has been fired
	 */
	int numTimesFiredInBattle(SwccgBuiltInCardBlueprint permanentWeapon, boolean completeOnly);

	/**
	 * Gets the weapons that have been fired in the current battle by the specified player.
	 * @param playerId the player
	 * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
	 */
	List<PhysicalCard> getWeaponsFiredInBattleByPlayer(String playerId, boolean completeOnly);

	/**
	 * Gets the permanent weapons that have been fired in the current battle by the specified player.
	 * @param playerId the player
	 * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
	 */
	List<SwccgBuiltInCardBlueprint> getPermanentWeaponsFiredInBattleByPlayer(String playerId, boolean completeOnly);

	/**
	 * Gets the weapons that have been fired in the current battle by the specified weapon user.
	 * @param weaponUser the weapon user
	 * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
	 */
	List<PhysicalCard> getWeaponsFiredInBattleByWeaponUser(PhysicalCard weaponUser, boolean completeOnly);

	/**
	 * Gets the permanent weapons that have been fired in the current battle by the specified weapon user.
	 * @param weaponUser the weapon user
	 * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
	 */
	List<SwccgBuiltInCardBlueprint> getPermanentWeaponsFiredInBattleByWeaponUser(PhysicalCard weaponUser, boolean completeOnly);

	/**
	 * Records that the specified weapon has been fired during Attack Run.
	 * @param card the card
	 * @param cardFiringWeapon the card firing the weapon
	 * @param complete true if firing completed, false if targeted but not actually fired yet
	 */
	void firedInAttackRun(PhysicalCard card, PhysicalCard cardFiringWeapon, boolean complete);

	/**
	 * Records that the specified permanent weapon has been fired during Attack Run.
	 * @param permanentWeapon the permanent weapon
	 * @param cardFiringWeapon the card firing the weapon
	 * @param complete true if firing completed, false if targeted but not actually fired yet
	 */
	void firedInAttackRun(SwccgBuiltInCardBlueprint permanentWeapon, PhysicalCard cardFiringWeapon, boolean complete);

	/**
	 * Gets the number of times the weapon has been fired in the during current Attack Run.
	 * @param card the card
	 * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
	 * @return the number of times the weapon has been fired
	 */
	int numTimesFiredInAttackRun(PhysicalCard card, boolean completeOnly);

	/**
	 * Gets the number of times the permanent weapon has been fired during current Attack Run.
	 * @param permanentWeapon the permanent weapon
	 * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
	 * @return the number of times the weapon has been fired
	 */
	int numTimesFiredInAttackRun(SwccgBuiltInCardBlueprint permanentWeapon, boolean completeOnly);

	/**
	 * Gets the weapons that have targeted the specified target this turn.
	 * @param target the target
	 * @return the weapons that have targeted the specified target this turn
	 */
	List<PhysicalCard> weaponsTargetedByThisTurn(PhysicalCard target);

	/**
	 * Gets the permanent weapons that have targeted the specified target this turn.
	 * @param target the target
	 * @return the permanent weapons that have targeted the specified target this turn
	 */
	List<SwccgBuiltInCardBlueprint> permanentWeaponsTargetedByThisTurn(PhysicalCard target);

	void hitOrMadeLostByWeapon(PhysicalCard target, PhysicalCard weapon);
	void clearHitOrMadeLostByWeapon(PhysicalCard card);
	boolean wasHitOrMadeLostByWeapon(PhysicalCard target, Filter hitBy);

	/**
	 * Records that a card was forfeited from the specified location.
	 * @param location the location
	 * @param forfeitedCard the forfeited card
	 */
	void forfeitedFromLocation(PhysicalCard location, PhysicalCard forfeitedCard);

	/**
	 * Gets the map of locations and cards that were forfeited from those locations this turn.
	 * @return the location and forfeited cards map
	 */
	Map<PhysicalCard, Set<PhysicalCard>> getForfeitedFromLocationsThisTurn();

	/**
	 * Records that the specified player used a combat card.
	 *
	 * @param playerId the player
	 */
	void combatCardUsed(String playerId);

	/**
	 * Determines if the player has used a combat card this turn.
	 * @param playerId the player
	 * @return true or false
	 */
	boolean isCombatCardUsedThisTurn(String playerId);

	/**
	 * Records that the specified character card won a game of sabacc.
	 *
	 * @param character the character that won sabacc game
	 */
	void wonSabaccGame(PhysicalCard character);

	/**
	 * Determines if a character accepted by the specified filter has won a sabacc game.
	 *
	 * @param gameState the game state
	 * @param filters   the filter
	 * @return true or false
	 */
	boolean hasWonSabaccGame(GameState gameState, Filterable filters);

	/**
	 * Records that the specified persona has 'crossed over'.
	 *
	 * @param persona the persona that was 'crossed over'
	 */
	void crossedOver(Persona persona);

	/**
	 * Determines if the specified persona has 'crossed over'.
	 *
	 * @param gameState the game state
	 * @param persona   the persona
	 * @return true or false
	 */
	boolean isCrossedOver(GameState gameState, Persona persona);

	/**
	 * Records that the specified Utinni Effect has been completed.
	 * @param playerId the player that completed the Utinni Effect
	 * @param utinniEffect the Utinni Effect that was completed
	 */
	void completedUtinniEffect(String playerId, PhysicalCard utinniEffect);

	/**
	 * Determines if at least a specified number of Utinni Effects accepted by the filter have been completed by the specified player.
	 * @param gameState the game state
	 * @param playerId the player
	 * @param count the count
	 * @param filters the filter
	 * @return true or false
	 */
	boolean hasCompletedUtinniEffect(GameState gameState, String playerId, int count, Filterable filters);

	/**
	 * Records that the specified Jedi Test has been attempted by the specified character.
	 * @param jediTest the Jedi Test that was attempted
	 * @param attemptedBy the character that attempted the Jedi Test
	 */
	void attemptedJediTest(PhysicalCard jediTest, PhysicalCard attemptedBy);

	/**
	 * Determines if any Jedi Tests have been attempted this turn.
	 * @return true or false
	 */
	boolean hasAttemptedJediTests();

	/**
	 * Records that the specified Jedi Test has been completed by the specified character.
	 * @param jediTest the Jedi Test that was completed
	 * @param completedBy the character that completed the Jedi Test
	 */
	void completedJediTest(PhysicalCard jediTest, PhysicalCard completedBy);

	/**
	 * Records that the specified card has participated in a Force drain.
	 *
	 * @param card the card
	 */
	void participatedInForceDrain(PhysicalCard card);

	/**
	 * Determines if the specified card has participated in a Force drain this turn.
	 *
	 * @param card the card
	 * @return true if card has participated, otherwise false
	 */
	boolean hasParticipatedInForceDrainThisTurn(PhysicalCard card);

	/**
	 * Records that the specified starship has had asteroid destiny drawn against it at the specified location.
	 *
	 * @param starship the starship
	 * @param location the location with "Asteroid Rules"
	 */
	void asteroidDestinyDrawnAgainst(PhysicalCard starship, PhysicalCard location);

	/**
	 * Determines if the specified starship has had asteroid destiny drawn against it at the specified location this turn.
	 *
	 * @param starship the starship
	 * @param location the location with "Asteroid Rules"
	 * @return true if starship has already had asteroid destiny drawn against it at the location, otherwise false
	 */
	boolean hadAsteroidDestinyDrawnAgainstThisTurn(PhysicalCard starship, PhysicalCard location);

	/**
	 * Gets all the cards that are targeting the specified card. This is used for the card info screen on the user interface.
	 * @param gameState the game state
	 * @param card the card
	 * @return the cards targeting the specified card
	 */
	List<PhysicalCard> getAllCardsTargetingCard(GameState gameState, PhysicalCard card);

	/**
	 * Gets all the cards on table that are targeting the specified card.
	 * @param gameState the game state
	 * @param card the card
	 * @return the cards on table targeting the specified card
	 */
	List<PhysicalCard> getCardsOnTableTargetingCard(GameState gameState, PhysicalCard card);

	/**
	 * Gets all the existing persistent modifiers that affect the specified card (skipping an extra checking).
	 * This is used to determine which modifiers to exclude the card from when "restoring to normal".
	 * @param gameState the game state
	 * @param card the card
	 * @return the modifiers
	 */
	List<Modifier> getPersistentModifiersAffectingCard(GameState gameState, PhysicalCard card);

	List<Modifier> getModifiers(GameState gameState, ModifierType modifierType);

	List<Modifier> getModifiersAffectingCard(GameState gameState, ModifierType modifierType, PhysicalCard card);

	List<Modifier> getKeywordModifiersAffectingCard(GameState gameState, ModifierType modifierType, Keyword keyword, PhysicalCard card);

	Collection<Modifier> getModifiersAffecting(GameState gameState, PhysicalCard card);

	/**
	 * Records that a 'bluff card' was stacked.
	 */
	void bluffCardStacked();

	/**
	 * Determines if a bluff card was stacked this turn.
	 * @return true or false
	 */
	boolean isBluffCardStackedThisTurn();

	/**
	 * Records that the Death Star's power is 'shut down'.
	 */
	void deathStarPowerIsShutDown();

	/**
	 * Determines if the Death Star's power is 'shut down'.
	 *
	 * @return true or false
	 */
	boolean isDeathStarPowerShutDown();

	/**
	 * Records that the Senate is in session.
	 */
	void declareSenateIsInSession();

	/**
	 * Determines if the Senate is in session.
	 *
	 * @return true or false
	 */
	boolean isSenateInSession();

	/**
	 * Records that the specified card being played (or being deployed).
	 * @param card the card
	 */
	void cardBeingPlayed(PhysicalCard card);

	/**
	 * Records that the specified card was just deployed on table.
	 * @param card the card
	 */
	void cardJustDeployed(PhysicalCard card);

	/**
	 * Gets the starting location of the specified player.
	 * @param playerId the player
	 * @return the starting location, or null
	 */
	PhysicalCard getStartingLocation(String playerId);

	/**
	 * Gets the cards that were played (or deployed) this game by the specified player.
	 * @param playerId the player
	 * @return the cards
	 */
	List<PhysicalCard> getCardsPlayedThisGame(String playerId);

	/**
	 * Gets the cards that were played (or deployed) this turn by the specified player.
	 * @param playerId the player
	 * @return the cards
	 */
	List<PhysicalCard> getCardsPlayedThisTurn(String playerId);

	/**
	 * Gets the cards with ability that were deployed this turn by the specified player.
	 * @param playerId the player
	 * @return the cards
	 */
	List<PhysicalCard> getCardsWithAbilityDeployedThisTurn(String playerId);

	/**
	 * Gets the cards that were played (or deployed) this turn by the specified player to the specified location.
	 * @param playerId the player
	 * @param location the location
	 * @return the cards
	 */
	List<PhysicalCard> getCardsPlayedThisTurnToLocation(String playerId, PhysicalCard location);

	void performedSpecialBattlegroundDownload(String playerId);
	boolean canPerformSpecialBattlegroundDownload(GameState gameState, String playerId);

	/**
	 * Records that the specified card has performed a regular move.
	 * @param card the card
	 */
	void regularMovePerformed(PhysicalCard card);

	/**
	 * Determines if the specified card has performed a regular move this turn.
	 * @param card the card
	 * @return true if card has performed a regular move this turn, otherwise false
	 */
	boolean hasPerformedRegularMoveThisTurn(PhysicalCard card);

	void setExtraInformationForArchetypeLabel(String playerId, String text);
	String getExtraInformationForArchetypeLabel(String playerId);
}
