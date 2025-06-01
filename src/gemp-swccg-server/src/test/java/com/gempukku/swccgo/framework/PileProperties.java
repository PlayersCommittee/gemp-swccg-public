package com.gempukku.swccgo.framework;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PhysicalCardImpl;

import java.util.List;

/**
 * These functions will help you determine pile counts or add/remove a card to a pile at a particular point.
 */
public interface PileProperties extends TestBase{
	/**
	 * @return Gets the current number of cards in the Dark Side player's hand.
	 */
	default int GetDSHandCount() { return GetDSHand().size(); }
	/**
	 * @return Gets the current number of cards in the Light Side player's hand.
	 */
	default int GetLSHandCount() { return GetLSHand().size(); }

	/**
	 * @return Gets all the cards currently in the Dark Side player's hand.
	 */
	default List<? extends PhysicalCard> GetDSHand() { return GetHand(DS); }
	/**
	 * @return Gets all the cards currently in the Light Side player's hand.
	 */
	default List<? extends PhysicalCard> GetLSHand() { return GetHand(LS); }
	/**
	 *
	 * @param player The player whose hand you are interested in.
	 * @return Gets all the cards currently in the given player's hand.
	 */
	default List<? extends PhysicalCard> GetHand(String player)
	{
		return gameState().getHand(player);
	}

	/**
	 * @return Gets the number of cards in the Dark Side player's Reserve Deck.
	 */
	default int GetDSReserveDeckCount() { return GetDSReserveDeck().size(); }
	/**
	 * @return Gets the number of cards in the Light Side player's Reserve Deck.
	 */
	default int GetLSReserveDeckCount() { return GetLSReserveDeck().size(); }

	/**
	 * @return Gets all the cards in the Dark Side player's Reserve Deck.
	 */
	default List<? extends PhysicalCard> GetDSReserveDeck() { return GetReserveDeck(DS); }
	/**
	 * @return Gets all the cards in the Light Side player's Reserve Deck.
	 */
	default List<? extends PhysicalCard> GetLSReserveDeck() { return GetReserveDeck(LS); }

	/**
	 * @return Gets the top card of the Dark Side player's Reserve Deck.
	 */
	default PhysicalCardImpl GetTopOfDSReserveDeck() { return (PhysicalCardImpl) GetReserveDeck(DS).getFirst(); }
	/**
	 * @return Gets the top card of the Light Side player's Reserve Deck.
	 */
	default PhysicalCardImpl GetTopOfLSReserveDeck() { return (PhysicalCardImpl) GetReserveDeck(LS).getFirst(); }

	/**
	 * @param player The player whose reserve deck you are interested in.
	 * @return Gets all the cards in the given player's Reserve Deck.
	 */
	default List<? extends PhysicalCard> GetReserveDeck(String player)
	{
		return gameState().getReserveDeck(player);
	}

	/**
	 * @return The total amount of cards in the Dark Side Force Pile.
	 */
	default int GetDSForcePileCount() { return GetDSForcePile().size(); }
	/**
	 * @return The total amount of cards in the Light Side Force Pile.
	 */
	default int GetLSForcePileCount() { return GetLSForcePile().size(); }

	/**
	 * @return Gets a list of all cards currently in the Dark Side Force Pile.
	 */
	default List<? extends PhysicalCard> GetDSForcePile() { return GetForcePile(DS); }
	/**
	 * @return Gets a list of all cards currently in the Light Side Force Pile.
	 */
	default List<? extends PhysicalCard> GetLSForcePile() { return GetForcePile(LS); }
	/**
	 * @param player The player to check for.
	 * @return Gets a list of all cards currently in the given player's Force Pile.
	 */
	default List<? extends PhysicalCard> GetForcePile(String player)
	{
		return gameState().getForcePile(player);
	}

	/**
	 * @return Gets the top card of the Dark Side player's Force Pile.
	 */
	default PhysicalCardImpl GetTopOfDSForcePile() { return (PhysicalCardImpl) GetForcePile(DS).getFirst(); }
	/**
	 * @return Gets the top card of the Light Side player's Force Pile.
	 */
	default PhysicalCardImpl GetTopOfLSForcePile() { return (PhysicalCardImpl) GetForcePile(LS).getFirst(); }

	/**
	 * @return The total amount of cards in the Dark Side Used Pile.
	 */
	default int GetDSUsedPileCount() { return GetDSUsedPile().size(); }
	/**
	 * @return The total amount of cards in the Light Side Used Pile.
	 */
	default int GetLSUsedPileCount() { return GetLSUsedPile().size(); }

	/**
	 * @return Gets a list of all cards currently in the Dark Side Used Pile.
	 */
	default List<? extends PhysicalCard> GetDSUsedPile() { return GetUsedPile(DS); }
	/**
	 * @return Gets a list of all cards currently in the Light Side Used Pile.
	 */
	default List<? extends PhysicalCard> GetLSUsedPile() { return GetUsedPile(LS); }
	/**
	 * @param player The player to check for.
	 * @return Gets a list of all cards currently in the given player's Used Pile.
	 */
	default List<? extends PhysicalCard> GetUsedPile(String player)
	{
		return gameState().getUsedPile(player);
	}

	/**
	 * @return Gets the top card of the Dark Side player's Used Pile.
	 */
	default PhysicalCardImpl GetTopOfDSUsedPile() { return (PhysicalCardImpl) GetUsedPile(DS).getFirst(); }
	/**
	 * @return Gets the top card of the Light Side player's Used Pile.
	 */
	default PhysicalCardImpl GetTopOfLSUsedPile() { return (PhysicalCardImpl) GetUsedPile(LS).getFirst(); }

	/**
	 * @return The total amount of cards in the Dark Side Lost Pile.
	 */
	default int GetDSLostPileCount() { return GetDSLostPile().size(); }
	/**
	 * @return The total amount of cards in the Light Side Lost Pile.
	 */
	default int GetLSLostPileCount() { return GetLSLostPile().size(); }

	/**
	 * @return Gets a list of all cards currently in the Dark Side Lost Pile.
	 */
	default List<? extends PhysicalCard> GetDSLostPile() { return GetLostPile(DS); }
	/**
	 * @return Gets a list of all cards currently in the Light Side Lost Pile.
	 */
	default List<? extends PhysicalCard> GetLSLostPile() { return GetLostPile(LS); }
	/**
	 * @param player The player to check for.
	 * @return Gets a list of all cards currently in the given player's Lost Pile.
	 */
	default List<? extends PhysicalCard> GetLostPile(String player)
	{
		return gameState().getLostPile(player);
	}

	/**
	 * @return Gets the top card of the Dark Side player's Lost Pile.
	 */
	default PhysicalCardImpl GetTopOfDSLostPile() { return (PhysicalCardImpl) GetUsedPile(DS).getFirst(); }
	/**
	 * @return Gets the top card of the Light Side player's Lost Pile.
	 */
	default PhysicalCardImpl GetTopOfLSLostPile() { return (PhysicalCardImpl) GetUsedPile(LS).getFirst(); }

//
//    default PhysicalCardImpl GetDSBottomOfDeck() { return GetPlayerBottomOfDeck(DS); }
//    default PhysicalCardImpl GetLSBottomOfDeck() { return GetPlayerBottomOfDeck(LS); }
//    default PhysicalCardImpl GetFromBottomOfDSDeck(int index) { return GetFromBottomOfPlayerDeck(DS, index); }
//    default PhysicalCardImpl GetFromBottomOfLSDeck(int index) { return GetFromBottomOfPlayerDeck(LS, index); }
//    default PhysicalCardImpl GetPlayerBottomOfDeck(String player) { return GetFromBottomOfPlayerDeck(player, 1); }
//    default PhysicalCardImpl GetFromBottomOfPlayerDeck(String player, int index)
//    {
//        var deck = gameState().getDeck(player);
//        return (PhysicalCardImpl) deck.get(deck.size() - index);
//    }

//    /**
//     * Index is 1-based (1 is first, 2 is second, etc)
//     */
//    default PhysicalCardImpl GetFromTopOfPlayerDeck(String player, int index)
//    {
//        var deck = gameState().getDeck(player);
//        if(deck.isEmpty())
//            return null;
//
//        return (PhysicalCardImpl) deck.get(index - 1);
//    }

}
