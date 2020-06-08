package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.common.ApplicationConfiguration;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

public class RootUriRequestHandler implements UriRequestHandler {
    private String _serverContextPath ="/gemp-swccg-server/";
    private String _webContextPath="/gemp-swccg/";
    private HallRequestHandler _hallRequestHandler;
    private WebRequestHandler _webRequestHandler;
    private LoginRequestHandler _loginRequestHandler;
    private StatusRequestHandler _statusRequestHandler;
    private DeckRequestHandler _deckRequestHandler;
    private AdminRequestHandler _adminRequestHandler;
    private ChatRequestHandler _chatRequestHandler;
    private CollectionRequestHandler _collectionRequestHandler;
    private DeliveryRequestHandler _deliveryRequestHandler;
    private GameRequestHandler _gameRequestHandler;
    private LeagueRequestHandler _leagueRequestHandler;
    private MerchantRequestHandler _merchantRequestHandler;
    private RegisterRequestHandler _registerRequestHandler;
    private ReplayRequestHandler _replayRequestHandler;
    private GameHistoryRequestHandler _gameHistoryRequestHandler;
    private ServerStatsRequestHandler _serverStatsRequestHandler;
    private PlayerStatsRequestHandler _playerStatsRequestHandler;
    private TournamentRequestHandler _tournamentRequestHandler;

    public RootUriRequestHandler(Map<Type, Object> context) {
        _webRequestHandler = new WebRequestHandler(ApplicationConfiguration.getProperty("web.path"));
        _hallRequestHandler = new HallRequestHandler(context);
        _deckRequestHandler = new DeckRequestHandler(context);
        _loginRequestHandler = new LoginRequestHandler(context);
        _statusRequestHandler = new StatusRequestHandler(context);
        _adminRequestHandler = new AdminRequestHandler(context);
        _chatRequestHandler = new ChatRequestHandler(context);
        _collectionRequestHandler = new CollectionRequestHandler(context);
        _deliveryRequestHandler = new DeliveryRequestHandler(context);
        _gameRequestHandler = new GameRequestHandler(context);
        _leagueRequestHandler = new LeagueRequestHandler(context);
        _merchantRequestHandler = new MerchantRequestHandler(context);
        _registerRequestHandler = new RegisterRequestHandler(context);
        _replayRequestHandler = new ReplayRequestHandler(context);
        _gameHistoryRequestHandler = new GameHistoryRequestHandler(context);
        _serverStatsRequestHandler = new ServerStatsRequestHandler(context);
        _playerStatsRequestHandler = new PlayerStatsRequestHandler(context);
        _tournamentRequestHandler = new TournamentRequestHandler(context);
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, MessageEvent e) throws Exception {
        if (uri.startsWith(_webContextPath)) {
            _webRequestHandler.handleRequest(uri.substring(_webContextPath.length()), request, context, responseWriter, e);
        } else if (uri.equals("/gemp-swccg")) {
            responseWriter.writeError(301, Collections.singletonMap("Location", "/gemp-swccg/"));
        } else if (uri.startsWith(_serverContextPath +"hall")) {
            _hallRequestHandler.handleRequest(uri.substring(_serverContextPath.length()+4), request, context, responseWriter, e);
        } else if (uri.startsWith(_serverContextPath + "deck")) {
            _deckRequestHandler.handleRequest(uri.substring(_serverContextPath.length()+4), request, context, responseWriter, e);
        } else if (uri.startsWith(_serverContextPath+"login")) {
            _loginRequestHandler.handleRequest(uri.substring(_serverContextPath.length()+5), request, context, responseWriter, e);
        } else if (uri.startsWith(_serverContextPath+"register")) {
            _registerRequestHandler.handleRequest(uri.substring(_serverContextPath.length()+8), request, context, responseWriter, e);
        } else if (uri.startsWith(_serverContextPath+"replay")) {
            _replayRequestHandler.handleRequest(uri.substring(_serverContextPath.length()+6), request, context, responseWriter, e);
        } else if (uri.startsWith(_serverContextPath+"gameHistory")) {
            _gameHistoryRequestHandler.handleRequest(uri.substring(_serverContextPath.length()+11), request, context, responseWriter, e);
        } else if (uri.startsWith(_serverContextPath+"stats")) {
            _serverStatsRequestHandler.handleRequest(uri.substring(_serverContextPath.length()+5), request, context, responseWriter, e);
        } else if (uri.startsWith(_serverContextPath+"playerStats")) {
            _playerStatsRequestHandler.handleRequest(uri.substring(_serverContextPath.length()+11), request, context, responseWriter, e);
        } else if (uri.startsWith(_serverContextPath+"admin")) {
            _adminRequestHandler.handleRequest(uri.substring(_serverContextPath.length()+5), request, context, responseWriter, e);
        } else if (uri.startsWith(_serverContextPath + "chat")) {
            _chatRequestHandler.handleRequest(uri.substring(_serverContextPath.length()+4), request, context, responseWriter, e);
        } else if (uri.startsWith(_serverContextPath + "collection")) {
            _collectionRequestHandler.handleRequest(uri.substring(_serverContextPath.length()+10), request, context, responseWriter, e);
        } else if (uri.startsWith(_serverContextPath + "delivery")) {
            _deliveryRequestHandler.handleRequest(uri.substring(_serverContextPath.length()+8), request, context, responseWriter, e);
        } else if (uri.startsWith(_serverContextPath + "game")) {
            _gameRequestHandler.handleRequest(uri.substring(_serverContextPath.length()+4), request, context, responseWriter, e);
        } else if (uri.startsWith(_serverContextPath + "league")) {
            _leagueRequestHandler.handleRequest(uri.substring(_serverContextPath.length()+6), request, context, responseWriter, e);
        } else if (uri.startsWith(_serverContextPath + "merchant")) {
            _merchantRequestHandler.handleRequest(uri.substring(_serverContextPath.length()+8), request, context, responseWriter, e);
        } else if (uri.startsWith(_serverContextPath + "tournament")) {
            _tournamentRequestHandler.handleRequest(uri.substring(_serverContextPath.length()+10), request, context, responseWriter, e);
        } else if (uri.equals(_serverContextPath)) {
            _statusRequestHandler.handleRequest(uri.substring(_serverContextPath.length()), request, context, responseWriter, e);
        } else {
            responseWriter.writeError(404);
        }
    }
}
