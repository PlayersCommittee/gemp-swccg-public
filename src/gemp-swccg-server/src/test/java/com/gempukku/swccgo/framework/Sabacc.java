package com.gempukku.swccgo.framework;

import static com.gempukku.swccgo.framework.Assertions.assertIsInt;

public interface Sabacc extends TestBase {

    /**
     * Checks current Sabacc hand total for a player.  Unassigned value cards (wild and clone)
     * have a value of -1 each.
     * @param playerId The player to get the Sabacc total for
     * @return int value of the player's Sabacc total
     */
    default int GetSabaccTotal(String playerId) {
        float total = game().getModifiersQuerying().getSabaccTotal(game().getGameState(), playerId);
        assertIsInt(total);
        return Math.round(total);
    }

    /**
	 * Checks current Sabacc hand total for DS player.  Unassigned value cards (wild and clone)
	 * have a value of -1 each.
	 * @return int value of DS Sabacc total
	 */
	default int GetDSSabaccTotal() {
        return GetSabaccTotal(DS);
	}

    /**
     * Checks current Sabacc hand total for LS player.  Unassigned value cards (wild and clone)
     * have a value of -1 each.
     * @return int value of LS Sabacc total
     */
    default int GetLSSabaccTotal() {
        return GetSabaccTotal(LS);
    }
}
