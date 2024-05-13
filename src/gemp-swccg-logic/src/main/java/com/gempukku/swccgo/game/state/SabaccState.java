package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.game.PhysicalCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// This class contains the state information for a
// Sabacc action within a game of Gemp-Swccg.
//
public class SabaccState {
    private PhysicalCard _sabaccInterrupt;
    private List<PhysicalCard> _sabaccPlayers = new ArrayList<PhysicalCard>();
    private boolean _initialCardsDrawn;
    private boolean _handsRevealed;
    private Map<String, Float> _finalSabaccTotal = new HashMap<String, Float>();
    private boolean _skipToEnd;

    public SabaccState(PhysicalCard sabaccInterrupt, PhysicalCard sabaccPlayer1, PhysicalCard sabaccPlayer2) {
        _sabaccInterrupt = sabaccInterrupt;
        if (sabaccPlayer1 != null)
            _sabaccPlayers.add(sabaccPlayer1);
        if (sabaccPlayer2 != null)
            _sabaccPlayers.add(sabaccPlayer2);
    }

    public PhysicalCard getSabaccInterrupt() {
        return _sabaccInterrupt;
    }

    public List<PhysicalCard> getSabaccPlayers() {
        return _sabaccPlayers;
    }

    public boolean isInitialCardsDrawn() {
        return _initialCardsDrawn;
    }

    public void setInitialCardsDrawn(boolean initialCardsDrawn) {
        _initialCardsDrawn = initialCardsDrawn;
    }

    public boolean isHandsRevealed() {
        return _handsRevealed;
    }

    public void setHandsRevealed(boolean handsRevealed) {
        _handsRevealed = handsRevealed;
    }

    public Float getFinalSabaccTotal(String playerId) {
        return _finalSabaccTotal.get(playerId);
    }

    public void setFinalSabaccTotal(String playerId, float sabaccTotal) {
        _finalSabaccTotal.put(playerId, sabaccTotal);
    }

    public boolean isSkipToEnd() {
        return _skipToEnd;
    }

    public void setSkipToEnd(boolean skipToEnd) {
        _skipToEnd = skipToEnd;
    }
}
