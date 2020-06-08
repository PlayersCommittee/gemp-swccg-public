package com.gempukku.swccgo.db;

import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.logic.vo.SwccgDeck;

import java.util.Set;

public interface DeckDAO {
    SwccgDeck getDeckForPlayer(Player player, String name);

    void saveDeckForPlayer(Player player, String name, SwccgDeck deck);

    void deleteDeckForPlayer(Player player, String name);

    SwccgDeck renameDeck(Player player, String oldName, String newName);

    Set<String> getPlayerDeckNames(Player player);

    SwccgDeck buildDeckFromContents(String deckName, String contents);
}
