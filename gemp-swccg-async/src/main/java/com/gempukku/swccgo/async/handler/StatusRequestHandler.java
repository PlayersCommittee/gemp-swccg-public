package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.chat.ChatServer;
import com.gempukku.swccgo.game.GameHistoryService;
import com.gempukku.swccgo.hall.HallServer;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * This request handler returns a response that is shown on the login page. It contains information about the current
 * and historical status of the server (# of players, # of games played, etc.).
 */
public class StatusRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    private HallServer _hallServer;
    private GameHistoryService _gameHistoryService;
    private ChatServer _chatServer;

    /**
     * Creates a request handler returns a response that is shown on the login page.
     * @param context context data from the netty server that accepted the HTTP request
     */
    public StatusRequestHandler(Map<Type, Object> context) {
        super(context);
        _hallServer = extractObject(context, HallServer.class);
        _gameHistoryService = extractObject(context, GameHistoryService.class);
        _chatServer = extractObject(context, ChatServer.class);
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, MessageEvent e) throws Exception {
        if ("".equals(uri) && request.getMethod() == HttpMethod.GET) {
            StringBuilder sb = new StringBuilder();

            int day = 1000 * 60 * 60 * 24;
            int week = 1000 * 60 * 60 * 24 * 7;
            sb.append("<b>Active players:</b>&nbsp;&nbsp;").append(_gameHistoryService.getActivePlayersCount(System.currentTimeMillis() - day, day)).append(" (in last 24 hours)")
                    .append(",&nbsp;&nbsp;").append(_gameHistoryService.getActivePlayersCount(System.currentTimeMillis() - week, week)).append(" (in last week)")
                    .append(",&nbsp;&nbsp;").append(_gameHistoryService.getActivePlayersCount()).append(" (all time)")
                    .append("<br/><b>Games played:</b>&nbsp;&nbsp;").append(_gameHistoryService.getGamesPlayedCount(System.currentTimeMillis() - day, day)).append(" (in last 24 hours)")
                    .append(",&nbsp;&nbsp;").append(_gameHistoryService.getGamesPlayedCount(System.currentTimeMillis() - week, week)).append(" (in last week)")
                    .append(",&nbsp;&nbsp;").append(_gameHistoryService.getGamesPlayedCount()).append(" (all time)")
                    .append("<h2>")
                    .append("Tables count: ").append(_hallServer.getTablesCount())
                    .append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Players in hall count: ").append(_chatServer.getChatRoom("Game Hall").getUsersInRoom().size())
                    .append("</h2>");

            responseWriter.writeHtmlResponse(sb.toString());
        } else {
            responseWriter.writeError(404);
        }
    }
}
