package com.gempukku.swccgo.trade;

import com.gempukku.swccgo.game.DefaultCardCollection;
import com.gempukku.swccgo.game.MutableCardCollection;

public class TradeState {
    private String _initiatingPlayer;
    private String _joiningPlayer;

    private MutableCardCollection _initiatingPlayerItems = new DefaultCardCollection();
    private MutableCardCollection _joiningPlayerItems = new DefaultCardCollection();

    private boolean _initiatingPlayerAgreed;
    private boolean _joiningPlayerAgreed;

    private int _tradeState;

    private int _initiatingPlayerConfirmedState = -1;
    private int _joiningPlayerConfirmedState = -1;

    private boolean _tradeFinished;

    private long _lastActivity;

    public TradeState(String initiatingPlayer, String joiningPlayer) {
        _initiatingPlayer = initiatingPlayer;
        _joiningPlayer = joiningPlayer;
    }

    public synchronized void addItemsWanted(String player, String item, int count) {
        if (player.equals(_initiatingPlayer))
            adjustJoiningItems(item, count);
        else if (player.equals(_joiningPlayer))
            adjustInitiatingItems(item, count);
    }

    public synchronized void removeItemsWanted(String player, String item, int count) {
        if (player.equals(_initiatingPlayer))
            adjustJoiningItems(item, -count);
        else if (player.equals(_joiningPlayer))
            adjustInitiatingItems(item, -count);
    }

    public synchronized void addItemsOffered(String player, String item, int count) {
        if (player.equals(_initiatingPlayer))
            adjustInitiatingItems(item, count);
        else if (player.equals(_joiningPlayer))
            adjustJoiningItems(item, count);
    }

    public synchronized void removeItemsOffered(String player, String item, int count) {
        if (player.equals(_initiatingPlayer))
            adjustInitiatingItems(item, -count);
        else if (player.equals(_joiningPlayer))
            adjustJoiningItems(item, -count);
    }

    public synchronized void playerAgreed(String player) {
        if (!_tradeFinished) {
            if (player.equals(_initiatingPlayer))
                _initiatingPlayerAgreed = true;
            else if (player.equals(_joiningPlayer))
                _joiningPlayerAgreed = true;

            if (_initiatingPlayerAgreed && _joiningPlayerAgreed)
                agreementStateReached();
        }
    }

    public synchronized TradeResult playerConfirmed(String player, int tradeState) {
        if (!_tradeFinished) {
            if (player.equals(_initiatingPlayer))
                _initiatingPlayerConfirmedState = tradeState;
            else if (player.equals(_joiningPlayer))
                _joiningPlayerConfirmedState = tradeState;

            if (_initiatingPlayerConfirmedState == _joiningPlayerConfirmedState
                    && _initiatingPlayerConfirmedState != -1) {
                _tradeFinished = true;
                return new TradeResult(_initiatingPlayer, _initiatingPlayerItems,
                        _joiningPlayer, _joiningPlayerItems);
            }
        }

        return null;
    }

    public synchronized void processTradeStateVisitor(String player, TradeStateVisitor tradeStateVisitor) {
        if (player.equals(_initiatingPlayer))
            tradeStateVisitor.processTradeState(
                    _joiningPlayer,
                    new DefaultCardCollection(_initiatingPlayerItems), new DefaultCardCollection(_joiningPlayerItems),
                    _initiatingPlayerAgreed, _joiningPlayerAgreed, _tradeState,
                    _initiatingPlayerConfirmedState == _tradeState, _joiningPlayerConfirmedState == _tradeState);
        else if (player.equals(_joiningPlayer))
            tradeStateVisitor.processTradeState(
                    _initiatingPlayer,
                    new DefaultCardCollection(_joiningPlayerItems), new DefaultCardCollection(_initiatingPlayerItems),
                    _joiningPlayerAgreed, _initiatingPlayerAgreed, _tradeState,
                    _joiningPlayerConfirmedState == _tradeState, _initiatingPlayerConfirmedState == _tradeState);

        _lastActivity = System.currentTimeMillis();
    }

    public String getInitiatingPlayer() {
        return _initiatingPlayer;
    }

    public String getJoiningPlayer() {
        return _joiningPlayer;
    }

    public boolean isTradeFinished() {
        return _tradeFinished;
    }

    public long getLastActivity() {
        return _lastActivity;
    }

    private void agreementStateReached() {
        _tradeState++;
    }

    private void adjustJoiningItems(String item, int diff) {
        adjustItems(_joiningPlayerItems, item, diff);
    }

    private void adjustInitiatingItems(String item, int diff) {
        adjustItems(_initiatingPlayerItems, item, diff);
    }

    private void adjustItems(MutableCardCollection collection, String item, int diff) {
        if (!_tradeFinished) {
            if (diff < 0)
                collection.removeItem(item, -diff);
            else
                collection.addItem(item, diff);

            _initiatingPlayerAgreed = false;
            _joiningPlayerAgreed = false;
        }
    }
}
