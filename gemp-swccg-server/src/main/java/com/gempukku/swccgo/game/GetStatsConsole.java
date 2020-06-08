package com.gempukku.swccgo.game;

import com.gempukku.swccgo.db.DbAccess;
import com.gempukku.swccgo.db.DbGameHistoryDAO;
import com.gempukku.swccgo.db.GameHistoryDAO;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class GetStatsConsole {
    private static final SimpleDateFormat monthFormat = new SimpleDateFormat("MMM yyyy");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final DecimalFormat percFormat = new DecimalFormat("#0.0%");

    public static void main(String[] args) throws ParseException {
        DbAccess dbAccess = new DbAccess();
        GameHistoryDAO gameHistoryDAO = new DbGameHistoryDAO(dbAccess);
        GameHistoryService gameHistoryService = new GameHistoryService(gameHistoryDAO);

        createGameActiveGamesStats(gameHistoryService);
    }

    private static void createGameActiveGamesStats(GameHistoryService gameHistoryService) throws ParseException {
        timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = timeFormat.parse("2012-01-01 00:00:00");
        Date nextDay = timeFormat.parse("2012-01-02 00:00:00");
        Date nextWeek = timeFormat.parse("2012-01-08 00:00:00");

        while (nextDay.getTime() < System.currentTimeMillis()) {
            int gamesCount = gameHistoryService.getGamesPlayedCount(date.getTime(), nextDay.getTime() - date.getTime());
            int playersCount = gameHistoryService.getActivePlayersCount(date.getTime(), nextWeek.getTime() - date.getTime());

            System.out.println(dayFormat.format(date) + "," + gamesCount + "," + playersCount);

            date.setDate(date.getDate() + 1);
            nextDay.setDate(nextDay.getDate() + 1);
            nextWeek.setDate(nextWeek.getDate() + 1);
        }
    }
}
