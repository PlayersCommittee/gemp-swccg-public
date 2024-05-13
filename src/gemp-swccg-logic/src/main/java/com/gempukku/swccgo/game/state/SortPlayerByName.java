package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.game.Player;

import java.util.Comparator;

public class SortPlayerByName implements Comparator<Player> {
    // Used for sorting in ascending order by name
    public int compare(Player a, Player b) {
        return a.getName().compareTo(b.getName());
    }
}
