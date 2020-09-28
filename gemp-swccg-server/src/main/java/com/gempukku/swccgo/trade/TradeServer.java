package com.gempukku.swccgo.trade;

import com.gempukku.swccgo.AbstractServer;
import com.gempukku.swccgo.chat.ChatServer;
import com.gempukku.swccgo.game.Player;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TradeServer extends AbstractServer {
    private Map<String, TradeRequest> _tradeRequestsFrom = new LinkedHashMap<String, TradeRequest>();
    private Map<String, List<TradeRequest>> _tradeRequestsTo = new HashMap<String, List<TradeRequest>>();

    private Set<TradeState> _ongoingTrades = new HashSet<TradeState>();
    private Map<String, TradeState> _playerOngoingTrade = new HashMap<String, TradeState>();

    private ReadWriteLock _tradeLock = new ReentrantReadWriteLock();

    private static final long TRADE_REQUEST_TIMEOUT_MS = 1000 * 60 * 5; // 5 minutes
    private static final long TRADE_TIMEOUT_MS = 1000 * 30; // 30 seconds

    private ChatServer _chatServer;

    public TradeServer(ChatServer chatServer) {
        _chatServer = chatServer;
    }

    public void sendTradeRequest(Player playerFrom, String playerNameTo) {
        _tradeLock.writeLock().lock();
        try {
            if (noOngoingActivity(playerFrom)) {
                createTradeRequest(playerFrom, playerNameTo);

            }
        } finally {
            _tradeLock.writeLock().unlock();
        }
    }

    public void cancelTradeRequest(Player playerFrom) {
        _tradeLock.writeLock().lock();
        try {
            final TradeRequest tradeRequest = _tradeRequestsFrom.get(playerFrom.getName());
            if (tradeRequest != null) {
                destroyTradeRequest(tradeRequest);
            }
        } finally {
            _tradeLock.writeLock().unlock();
        }
    }

    public void acceptTradeRequest(Player player, String requestFrom) {
        _tradeLock.writeLock().lock();
        try {
            if (noOngoingActivity(player)) {
                final TradeRequest tradeRequest = _tradeRequestsFrom.get(requestFrom);
                if (tradeRequest != null
                        && tradeRequest.getTo().equals(player.getName())) {
                    destroyTradeRequest(tradeRequest);

                    createNewOngoingTrade(tradeRequest.getFrom(), tradeRequest.getTo());
                }
            }
        } finally {
            _tradeLock.writeLock().unlock();
        }
    }

    public TradeState getOngoingTrade(Player player) {
        _tradeLock.readLock().lock();
        try {
            return _playerOngoingTrade.get(player.getName());
        } finally {
            _tradeLock.readLock().unlock();
        }
    }

    public void processTradeVisitor(Player player, TradesVisitor tradesVisitor) {
        _tradeLock.readLock().lock();
        try {
            final TradeRequest tradeRequest = _tradeRequestsFrom.get(player.getName());
            if (tradeRequest != null)
                tradesVisitor.processPendingTradeRequestSent(tradeRequest.getTo());

            final List<TradeRequest> incomingTradeRequests = _tradeRequestsTo.get(player.getName());
            if (incomingTradeRequests != null) {
                for (TradeRequest incomingTradeRequest : incomingTradeRequests)
                    tradesVisitor.processPendingTradeRequestIncoming(incomingTradeRequest.getFrom());
            }

            final TradeState ongoingTrade = _playerOngoingTrade.get(player.getName());
            if (ongoingTrade != null)
                tradesVisitor.processOngoingTrade(ongoingTrade, getChatRoomName(ongoingTrade.getInitiatingPlayer(), ongoingTrade.getJoiningPlayer()));
        } finally {
            _tradeLock.readLock().unlock();
        }
    }

    private boolean noOngoingActivity(Player player) {
        return (!_tradeRequestsFrom.containsKey(player.getName())
                && !_playerOngoingTrade.containsKey(player.getName()));
    }

    private void createTradeRequest(Player playerFrom, String playerNameTo) {
        // Create new trade request
        final TradeRequest newTradeRequest = new TradeRequest(playerFrom.getName(), playerNameTo);
        // Add it to outgoing
        _tradeRequestsFrom.put(playerFrom.getName(), newTradeRequest);
        // Add it to incoming
        List<TradeRequest> incomingTradeRequests = _tradeRequestsTo.get(playerNameTo);
        if (incomingTradeRequests == null) {
            incomingTradeRequests = new LinkedList<TradeRequest>();
            _tradeRequestsTo.put(playerNameTo, incomingTradeRequests);
        }
        incomingTradeRequests.add(newTradeRequest);
    }

    private void destroyTradeRequest(TradeRequest tradeRequest) {
        final String tradeFrom = tradeRequest.getFrom();
        final String tradeTo = tradeRequest.getTo();

        _tradeRequestsFrom.remove(tradeFrom);
        _tradeRequestsTo.get(tradeTo).remove(tradeRequest);
        if (_tradeRequestsTo.get(tradeTo).size() == 0)
            _tradeRequestsTo.remove(tradeTo);
    }

    private void createNewOngoingTrade(String from, String to) {
        TradeState tradeState = new TradeState(from, to);
        _ongoingTrades.add(tradeState);
        _playerOngoingTrade.put(from, tradeState);
        _playerOngoingTrade.put(to, tradeState);

        _chatServer.createChatRoom(getChatRoomName(from, to), false, 30, null, true, false);
    }

    private void destroyOngoingTrade(TradeState tradeState) {
        _ongoingTrades.remove(tradeState);
        _playerOngoingTrade.remove(tradeState.getInitiatingPlayer());
        _playerOngoingTrade.remove(tradeState.getJoiningPlayer());

        _chatServer.destroyChatRoom(getChatRoomName(tradeState.getInitiatingPlayer(), tradeState.getJoiningPlayer()));
    }

    private String getChatRoomName(String from, String to) {
        return "Trade - " + from + " and " + to;
    }

    @Override
    protected void cleanup() {
        _tradeLock.writeLock().lock();
        try {
            long now = System.currentTimeMillis();
            for (TradeRequest tradeRequest : new ArrayList<TradeRequest>(_tradeRequestsFrom.values())) {
                if (tradeRequest.getCreatedDate() < now + TRADE_REQUEST_TIMEOUT_MS)
                    destroyTradeRequest(tradeRequest);
            }

            for (TradeState tradeState : new HashSet<TradeState>(_ongoingTrades)) {
                if (tradeState.isTradeFinished() || tradeState.getLastActivity() < now + TRADE_TIMEOUT_MS)
                    destroyOngoingTrade(tradeState);
            }

        } finally {
            _tradeLock.writeLock().unlock();
        }
    }
}
