
package com.gempukku.swccgo.communication;

import com.gempukku.swccgo.game.SwccgGame;

public interface InGameStatisticsListener {
	public void writePileCounts(final SwccgGame game, boolean gameComplete);
	public void writeActivationTotals(final SwccgGame game);
}