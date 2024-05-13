package com.gempukku.swccgo.league;

import com.gempukku.swccgo.db.vo.League;

public class LeagueMapKeys {
    public static String getLeagueMapKey(League league) {
        return league.getType();
    }

    public static String getLeagueSeriesMapKey(League league, LeagueSeriesData leagueSeries) {
        return league.getType() + ":" + leagueSeries.getName();
    }
}
