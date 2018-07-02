package com.gempukku.swccgo.db;

import com.gempukku.swccgo.cache.Cached;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.apache.commons.collections.map.LRUMap;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CachedDeckDAO implements DeckDAO, Cached {
    private DeckDAO _delegate;
    private Map<String, Set<String>> _playerDeckNames = Collections.synchronizedMap(new LRUMap(100));
    private Map<String, SwccgDeck> _decks = Collections.synchronizedMap(new LRUMap(100));

    public CachedDeckDAO(DeckDAO delegate) {
        _delegate = delegate;
    }

    @Override
    public void clearCache() {
        _playerDeckNames.clear();
        _decks.clear();
    }

    @Override
    public int getItemCount() {
        return _playerDeckNames.size()+_decks.size();
    }

    @Override
    public SwccgDeck buildDeckFromContents(String deckName, String contents) {
        return _delegate.buildDeckFromContents(deckName, contents);
    }

    private String constructPlayerDeckNamesKey(Player player) {
        return player.getName();
    }
    private String constructDeckKey(Player player, String name) {
        return player.getName()+"-"+name;
    }

    @Override
    public void deleteDeckForPlayer(Player player, String name) {
        _delegate.deleteDeckForPlayer(player, name);
        _playerDeckNames.remove(constructPlayerDeckNamesKey(player));
    }

    @Override
    public SwccgDeck getDeckForPlayer(Player player, String name) {
        String key = constructDeckKey(player, name);
        SwccgDeck deck = _decks.get(key);
        if (deck == null) {
            deck = _delegate.getDeckForPlayer(player, name);
            _decks.put(key, deck);
        }
        return deck;
    }

    @Override
    public Set<String> getPlayerDeckNames(Player player) {
        String cacheKey = constructPlayerDeckNamesKey(player);
        Set<String> deckNames = _playerDeckNames.get(cacheKey);
        if (deckNames == null) {
            deckNames = Collections.synchronizedSet(new HashSet<String>(_delegate.getPlayerDeckNames(player)));
            _playerDeckNames.put(cacheKey, deckNames);
        }
        return deckNames;
    }

    @Override
    public SwccgDeck renameDeck(Player player, String oldName, String newName) {
        SwccgDeck swccgDeck = _delegate.renameDeck(player, oldName, newName);
        _playerDeckNames.remove(constructPlayerDeckNamesKey(player));
        _decks.remove(constructDeckKey(player, oldName));
        _decks.put(constructDeckKey(player, newName), swccgDeck);

        return swccgDeck;
    }

    @Override
    public void saveDeckForPlayer(Player player, String name, SwccgDeck deck) {
        _delegate.saveDeckForPlayer(player, name, deck);
        _playerDeckNames.remove(constructPlayerDeckNamesKey(player));
        _decks.put(constructDeckKey(player, name), deck);
    }
}
