package com.gempukku.swccgo.db;

import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface LeagueDAO {
    void addLeague(int cost, String name, String type, String clazz, String parameters, int start, int endTime, boolean allowSpectators, boolean allowTimeExtensions, boolean showPlayerNames, int decisionTimeoutSeconds) throws SQLException, IOException;

    List<League> loadActiveLeagues(SwccgCardBlueprintLibrary library, int currentTime) throws SQLException, IOException;

    void setStatus(League league, int newStatus);
}
