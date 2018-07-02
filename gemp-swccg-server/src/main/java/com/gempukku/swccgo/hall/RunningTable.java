package com.gempukku.swccgo.hall;

import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.game.SwccgGameMediator;
import com.gempukku.swccgo.league.LeagueSeriesData;

public class RunningTable {
    private SwccgGameMediator _swccgGameMediator;
    private String _formatName;
    private String _tournamentName;
    private String _tableDesc;
    private League _league;
    private LeagueSeriesData _leagueSerie;

    public RunningTable(SwccgGameMediator swccgGameMediator, String formatName, String tournamentName, String tableDesc, League league, LeagueSeriesData leagueSerie) {
        _swccgGameMediator = swccgGameMediator;
        _formatName = formatName;
        _tournamentName = tournamentName;
        _tableDesc = tableDesc;
        _league = league;
        _leagueSerie = leagueSerie;
    }

    public String getFormatName() {
        return _formatName;
    }

    public SwccgGameMediator getSwccgoGameMediator() {
        return _swccgGameMediator;
    }

    public String getTournamentName() {
        return _tournamentName;
    }

    public String getTableDesc() {
        return _tableDesc;
    }

    public League getLeague() {
        return _league;
    }

    public LeagueSeriesData getLeagueSerie() {
        return _leagueSerie;
    }
}
