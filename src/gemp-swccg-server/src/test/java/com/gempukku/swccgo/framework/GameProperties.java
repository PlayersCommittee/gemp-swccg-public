package com.gempukku.swccgo.framework;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PhysicalCardImpl;

import java.util.Arrays;
import java.util.List;

public interface GameProperties extends TestBase {
	/**
	 * @return Whether the game has finished one way or another.
	 */
	default boolean GameIsFinished() { return game().isFinished(); }

	/**
	 * @return Gets the current game phase
	 */
	default Phase GetCurrentPhase() { return gameState().getCurrentPhase(); }

	/**
	 * @return True if a battle is currently occuring at a site, otherwise false.
	 */
	default boolean IsActiveBattle() { return gameState().isDuringBattle(); }

	/**
	 * @return Retrieves the site that is host to the currently ongoing battle. Null if there is no current battle.
	 */
	default PhysicalCard GetBattleLocation() { return gameState().getBattleLocation(); }

	/**
	 * @return True if a player is currently executing a Force drain at a site, otherwise false.
	 */
	default boolean IsActiveForceDrain() { return gameState().isDuringForceDrain(); }

	/**
	 * @return Returns the total amount being drained in the current Force Drain.
	 */
	default int GetForceDrainTotal() { return gameState().getForceDrainState().getForceTotal(); }
	/**
	 * @return Returns the total amount of Force the targeted player has already paid during this Force Drain.
	 */
	default int GetForceDrainPaidSoFar() { return gameState().getForceDrainState().getForcePaid(); }
	/**
	 * @return Returns the total amount of Force that has not yet been paid in the current Force Drain.
	 */
	default int GetForceDrainRemaining() {
		if(gameState().getForceDrainState() == null)
			return 0;
		return gameState().getForceDrainState().getForceRemaining();
	}

	/**
	 * @return Gets the player who is currently playing their turn.
	 */
	default String GetCurrentPlayer() { return gameState().getCurrentPlayerId(); }

	/**
	 * @return Gets the player whose turn it isn't.
	 */
	default String GetOpponent() { return gameState().getOpponent(GetCurrentPlayer()); }

	/**
	 * @return Gets the player who is currently making a decision.
	 */
	default String GetDecidingPlayer() { return userFeedback().getUsersPendingDecision().stream().findFirst().get(); }

	/**
	 * @return Gets the player who is not currently making a decision.
	 */
	default String GetNextDecider() { return gameState().getOpponent(GetDecidingPlayer()); }

	/**
	 * @return Gets the number of turns that the Dark Side player has had, including the current one.
	 */
	default int GetDSTurnCount() { return GetPlayerTurnCount(DS); }
	/**
	 * @return Gets the number of turns that the Light Side player has had, including the current one.
	 */
	default int GetLSTurnCount() { return GetPlayerTurnCount(LS); }

	/**
	 * @param player The player you are interested in
	 * @return Gets the number of turns that the given player has had, including the current one.
	 */
	default int GetPlayerTurnCount(String player) { return gameState().getPlayersLatestTurnNumber(player); }

	/**
	 * @return The total Life Force for the Dark Side player across all relevant piles.
	 */
	default int GetDSLifeForceRemaining() { return GetPlayerLifeForceRemaining(DS); }
	/**
	 * @return The total Life Force for the Light Side player across all relevant piles.
	 */
	default int GetLSLifeForceRemaining() { return GetPlayerLifeForceRemaining(LS); }
	/**
	 * @param player The player to check.
	 * @return The total Life Force for the given player across all relevant piles.
	 */
	default int GetPlayerLifeForceRemaining(String player) { return gameState().getPlayerLifeForce(player); }

	/**
	 * @param site The location to check.
	 * @return All cards "at" the current location, but not their attached cards, pilots, passengers, or other esoteria.
	 */
	default List<PhysicalCard> GetCardsAtLocation(PhysicalCardImpl site) { return gameState().getCardsAtLocation(site); }

	/**
	 * Checks that all the provided cards are "at" the given location.  A card is not "at" that site if they are riding
	 * or piloting a vehicle or ship, or are otherwise attached or stacked on a card "at" that site.
	 * @param site The location to check.
	 * @param cards The cards to search for.
	 * @return True if all cards are "at" the given location, false if any are not.
	 */
	default boolean CardsAtLocation(PhysicalCardImpl site, PhysicalCardImpl...cards) {
		return GetCardsAtLocation(site).containsAll(Arrays.stream(cards).toList());
	}

	default int GetDSAbilityAtLocation(PhysicalCardImpl site) {
		return (int) game().getModifiersQuerying().getTotalAbilityAtLocation(gameState(), DS, site);
	}

	default int GetLSAbilityAtLocation(PhysicalCardImpl site) {
		return (int) game().getModifiersQuerying().getTotalAbilityAtLocation(gameState(), LS, site);
	}
}
