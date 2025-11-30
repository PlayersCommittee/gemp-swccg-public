package com.gempukku.swccgo.async.handler;

import com.gempukku.polling.LongPollingSystem;
import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.common.ApplicationConfiguration;
import io.netty.handler.codec.http.HttpRequest;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

public class RootUriRequestHandler implements UriRequestHandler {
    private final String _serverContextPath ="/gemp-swccg-server/";
    private final String _webContextPath="/gemp-swccg/";
    private final HallRequestHandler _hallRequestHandler;
    private final WebRequestHandler _webRequestHandler;
    private final LoginRequestHandler _loginRequestHandler;
    private final StatusRequestHandler _statusRequestHandler;
    private final DeckRequestHandler _deckRequestHandler;
    private final AdminRequestHandler _adminRequestHandler;
    private final ChatRequestHandler _chatRequestHandler;
    private final CollectionRequestHandler _collectionRequestHandler;
    private final DeliveryRequestHandler _deliveryRequestHandler;
    private final GameRequestHandler _gameRequestHandler;
    private final LeagueRequestHandler _leagueRequestHandler;
    private final MerchantRequestHandler _merchantRequestHandler;
    private final RegisterRequestHandler _registerRequestHandler;
    private final ReplayRequestHandler _replayRequestHandler;
    private final GameHistoryRequestHandler _gameHistoryRequestHandler;
    private final ServerStatsRequestHandler _serverStatsRequestHandler;
    private final PlayerStatsRequestHandler _playerStatsRequestHandler;
    private final TournamentRequestHandler _tournamentRequestHandler;
    private SoloDraftRequestHandler _soloDraftRequestHandler;

    private final Pattern originPattern;

    public RootUriRequestHandler(Map<Type, Object> context, LongPollingSystem longPollingSystem) {
        _webRequestHandler = new WebRequestHandler(ApplicationConfiguration.getProperty("web.path"));
        String originAllowedPattern = ApplicationConfiguration.getProperty("origin.allowed.pattern");
        originPattern = Pattern.compile(originAllowedPattern);

        _hallRequestHandler = new HallRequestHandler(context, longPollingSystem);
        _deckRequestHandler = new DeckRequestHandler(context);
        _loginRequestHandler = new LoginRequestHandler(context);
        _statusRequestHandler = new StatusRequestHandler(context);
        _adminRequestHandler = new AdminRequestHandler(context);
        _chatRequestHandler = new ChatRequestHandler(context, longPollingSystem);
        _collectionRequestHandler = new CollectionRequestHandler(context);
        _deliveryRequestHandler = new DeliveryRequestHandler(context);
        _gameRequestHandler = new GameRequestHandler(context, longPollingSystem);
        _leagueRequestHandler = new LeagueRequestHandler(context);
        _merchantRequestHandler = new MerchantRequestHandler(context);
        _registerRequestHandler = new RegisterRequestHandler(context);
        _replayRequestHandler = new ReplayRequestHandler(context);
        _gameHistoryRequestHandler = new GameHistoryRequestHandler(context);
        _serverStatsRequestHandler = new ServerStatsRequestHandler(context);
        _playerStatsRequestHandler = new PlayerStatsRequestHandler(context);
        _tournamentRequestHandler = new TournamentRequestHandler(context);
        _soloDraftRequestHandler = new SoloDraftRequestHandler(context);
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, String remoteIp) throws Exception {
        if (uri.startsWith(_webContextPath)) {
            _webRequestHandler.handleRequest(uri.substring(_webContextPath.length()), request, context, responseWriter, remoteIp);
        } else if (uri.equals("/gemp-swccg")) {
            responseWriter.writeError(301, Collections.singletonMap("Location", "/gemp-swccg/"));
        } else if (uri.equals(_serverContextPath)) {
            _statusRequestHandler.handleRequest(uri.substring(_serverContextPath.length()), request, context, responseWriter, remoteIp);
        } else {
            String origin = request.headers().get("Origin");
            if (origin != null) {
                if (!originPattern.matcher(origin).matches())
                    throw new HttpProcessingException(403);
            }

            // These APIs are protected by same Origin protection
            if (uri.startsWith(_serverContextPath + "hall")) {
                _hallRequestHandler.handleRequest(uri.substring(_serverContextPath.length() + 4), request, context,
                        responseWriter, remoteIp);
            } else if (uri.startsWith(_serverContextPath + "deck")) {
                _deckRequestHandler.handleRequest(uri.substring(_serverContextPath.length() + 4), request, context,
                        responseWriter, remoteIp);
            } else if (uri.startsWith(_serverContextPath + "login")) {
                _loginRequestHandler.handleRequest(uri.substring(_serverContextPath.length() + 5), request, context,
                        responseWriter, remoteIp);
            } else if (uri.startsWith(_serverContextPath + "register")) {
                _registerRequestHandler.handleRequest(uri.substring(_serverContextPath.length() + 8), request, context,
                        responseWriter, remoteIp);
            } else if (uri.startsWith(_serverContextPath + "replay")) {
                _replayRequestHandler.handleRequest(uri.substring(_serverContextPath.length() + 6), request, context,
                        responseWriter, remoteIp);
            } else if (uri.startsWith(_serverContextPath + "gameHistory")) {
                _gameHistoryRequestHandler.handleRequest(uri.substring(_serverContextPath.length() + 11), request,
                        context, responseWriter, remoteIp);
            } else if (uri.startsWith(_serverContextPath + "stats")) {
                _serverStatsRequestHandler.handleRequest(uri.substring(_serverContextPath.length() + 5), request,
                        context, responseWriter, remoteIp);
            } else if (uri.startsWith(_serverContextPath + "playerStats")) {
                _playerStatsRequestHandler.handleRequest(uri.substring(_serverContextPath.length() + 11), request,
                        context, responseWriter, remoteIp);
            } else if (uri.startsWith(_serverContextPath + "playerCollectionStats")) {
                _collectionRequestHandler.handleRequest(uri.substring(_serverContextPath.length()), request, context,
                        responseWriter, remoteIp);
            } else if (uri.startsWith(_serverContextPath + "admin")) {
                _adminRequestHandler.handleRequest(uri.substring(_serverContextPath.length() + 5), request, context,
                        responseWriter, remoteIp);
            } else if (uri.startsWith(_serverContextPath + "chat")) {
                _chatRequestHandler.handleRequest(uri.substring(_serverContextPath.length() + 4), request, context,
                        responseWriter, remoteIp);
            } else if (uri.startsWith(_serverContextPath + "collection")) {
                _collectionRequestHandler.handleRequest(uri.substring(_serverContextPath.length() + 10), request,
                        context, responseWriter, remoteIp);
            } else if (uri.startsWith(_serverContextPath + "delivery")) {
                _deliveryRequestHandler.handleRequest(uri.substring(_serverContextPath.length() + 8), request, context,
                        responseWriter, remoteIp);
            } else if (uri.startsWith(_serverContextPath + "game")) {
                _gameRequestHandler.handleRequest(uri.substring(_serverContextPath.length() + 4), request, context,
                        responseWriter, remoteIp);
            } else if (uri.startsWith(_serverContextPath + "league")) {
                _leagueRequestHandler.handleRequest(uri.substring(_serverContextPath.length() + 6), request, context,
                        responseWriter, remoteIp);
            } else if (uri.startsWith(_serverContextPath + "merchant")) {
                _merchantRequestHandler.handleRequest(uri.substring(_serverContextPath.length() + 8), request, context,
                        responseWriter, remoteIp);
            } else if (uri.startsWith(_serverContextPath + "tournament")) {
                _tournamentRequestHandler.handleRequest(uri.substring(_serverContextPath.length() + 10), request,
                        context, responseWriter, remoteIp);
            } else if (uri.startsWith(_serverContextPath + "soloDraft")) {
                _soloDraftRequestHandler.handleRequest(uri.substring(_serverContextPath.length() + 9), request, context, responseWriter, remoteIp);
            } else {
                responseWriter.writeError(404);
            }
        }
    }
}
