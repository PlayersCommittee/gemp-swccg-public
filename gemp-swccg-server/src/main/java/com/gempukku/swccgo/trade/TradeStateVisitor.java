package com.gempukku.swccgo.trade;

import com.gempukku.swccgo.game.CardCollection;

public interface TradeStateVisitor {
    public void processTradeState(String otherParty, CardCollection offering, CardCollection getting,
                                  boolean selfAccepted, boolean otherAccepted, int tradeState,
                                  boolean selfConfirmed, boolean otherConfirmed);
}
