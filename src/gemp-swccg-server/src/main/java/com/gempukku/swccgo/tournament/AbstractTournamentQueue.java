package com.gempukku.swccgo.tournament;

import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.logic.vo.SwccgDeck;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public abstract class AbstractTournamentQueue implements TournamentQueue {
    protected int _cost;
    protected Queue<String> _players = new LinkedList<String>();
    protected Map<String, SwccgDeck> _playerDecks = new HashMap<String, SwccgDeck>();
    protected boolean _requiresDeck;

    private CollectionType _currencyCollection = CollectionType.MY_CARDS;

    protected final PairingMechanism _pairingMechanism;
    protected final CollectionType _collectionType;
    protected final TournamentPrizes _tournamentPrizes;
    protected String _format;

    public AbstractTournamentQueue(int cost, boolean requiresDeck, CollectionType collectionType, TournamentPrizes tournamentPrizes, PairingMechanism pairingMechanism, String format) {
        _cost = cost;
        _requiresDeck = requiresDeck;
        _collectionType = collectionType;
        _tournamentPrizes = tournamentPrizes;
        _pairingMechanism = pairingMechanism;
        _format = format;
    }

    @Override
    public String getPairingDescription() {
        return _pairingMechanism.getPlayOffSystem();
    }

    @Override
    public final CollectionType getCollectionType() {
        return _collectionType;
    }

    @Override
    public final String getPrizesDescription() {
        return _tournamentPrizes.getPrizeDescription();
    }

    @Override
    public final synchronized void joinPlayer(CollectionsManager collectionsManager, Player player, SwccgDeck deck) {
        if (!_players.contains(player.getName()) && isJoinable()) {
            if (_cost <= 0 || collectionsManager.removeCurrencyFromPlayerCollection("Joined "+getTournamentQueueName()+" queue", player, _currencyCollection, _cost)) {
                _players.add(player.getName());
                if (_requiresDeck)
                    _playerDecks.put(player.getName(), deck);
            }
        }
    }

    @Override
    public final synchronized void leavePlayer(CollectionsManager collectionsManager, Player player) {
        if (_players.contains(player.getName())) {
            if (_cost > 0)
                collectionsManager.addCurrencyToPlayerCollection(true, "Return for leaving "+getTournamentQueueName()+" queue", player, _currencyCollection, _cost);
            _players.remove(player.getName());
            _playerDecks.remove(player.getName());
        }
    }

    @Override
    public final synchronized void leaveAllPlayers(CollectionsManager collectionsManager) {
        if (_cost > 0) {
            for (String player : _players)
                collectionsManager.addCurrencyToPlayerCollection(false, "Return for leaving "+getTournamentQueueName()+" queue", player, _currencyCollection, _cost);
        }
        _players.clear();
        _playerDecks.clear();
    }

    @Override
    public final synchronized int getPlayerCount() {
        return _players.size();
    }

    @Override
    public final synchronized boolean isPlayerSignedUp(String player) {
        return _players.contains(player);
    }

    @Override
    public final int getCost() {
        return _cost;
    }

    @Override
    public final boolean isRequiresDeck() {
        return _requiresDeck;
    }

    @Override
    public final String getFormat() {
        return _format;
    }
}
