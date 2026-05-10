package com.gempukku.swccgo.db;

import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.game.Player;

import java.util.Collection;

public interface LeagueParticipationDAO {
    void userJoinsLeague(String leagueId, Player player, String remoteAddr);

    Collection<String> getUsersParticipating(String leagueId);

    /**
     * Gets the locked deck name and contents for a player in a league for a given side.
     * @param leagueId the league type identifier
     * @param playerName the player name
     * @param side LS or DS
     * @return String array of [deckName, deckContents], or null if no locked deck exists
     */
    String[] getLockedDeck(String leagueId, String playerName, Side side);

    /**
     * Sets the locked deck for a player in a league for a given side.
     * @param leagueId the league type identifier
     * @param playerName the player name
     * @param side LS or DS
     * @param deckName the deck name to store
     * @param deckContents the serialized deck contents
     */
    void setLockedDeck(String leagueId, String playerName, Side side, String deckName, String deckContents);
}
