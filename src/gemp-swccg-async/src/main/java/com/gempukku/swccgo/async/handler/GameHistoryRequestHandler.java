package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.db.vo.GameHistoryEntry;
import com.gempukku.swccgo.game.GameHistoryService;
import com.gempukku.swccgo.game.Player;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class GameHistoryRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    private GameHistoryService _gameHistoryService;

    public GameHistoryRequestHandler(Map<Type, Object> context) {
        super(context);

        _gameHistoryService = extractObject(context, GameHistoryService.class);
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, String remoteIp) throws Exception {
        if ("".equals(uri) && request.method() == HttpMethod.GET) {
            QueryStringDecoder queryDecoder = new QueryStringDecoder(request.uri());
            String participantId = getQueryParameterSafely(queryDecoder, "participantId");
            int start = Integer.parseInt(getQueryParameterSafely(queryDecoder, "start"));
            int count = Integer.parseInt(getQueryParameterSafely(queryDecoder, "count"));

            if (start < 0 || count < 1 || count > 100)
                throw new HttpProcessingException(400);

            Player resourceOwner = getResourceOwnerSafely(request, participantId);

            final List<GameHistoryEntry> playerGameHistory = _gameHistoryService.getGameHistoryForPlayer(resourceOwner, start, count);
            int recordCount = _gameHistoryService.getGameHistoryForPlayerCount(resourceOwner);

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = documentBuilder.newDocument();
            Element gameHistory = doc.createElement("gameHistory");
            gameHistory.setAttribute("count", String.valueOf(recordCount));
            gameHistory.setAttribute("playerId", resourceOwner.getName());

            for (GameHistoryEntry gameHistoryEntry : playerGameHistory) {
                Element historyEntry = doc.createElement("historyEntry");
                historyEntry.setAttribute("winner", gameHistoryEntry.getWinner());
                historyEntry.setAttribute("winnerDeckArchetype", gameHistoryEntry.getwinnerDeckArchetype());
                historyEntry.setAttribute("loser", gameHistoryEntry.getLoser());
                historyEntry.setAttribute("loserDeckArchetype", gameHistoryEntry.getloserDeckArchetype());

                historyEntry.setAttribute("winReason", gameHistoryEntry.getWinReason());
                historyEntry.setAttribute("loseReason", gameHistoryEntry.getLoseReason());

                historyEntry.setAttribute("formatName", gameHistoryEntry.getFormatName());
                String tournament = gameHistoryEntry.getTournament();
                if (tournament != null)
                    historyEntry.setAttribute("tournament", tournament);

                if (gameHistoryEntry.getWinner().equals(resourceOwner.getName()) && gameHistoryEntry.getWinnerRecording() != null) {
                    historyEntry.setAttribute("gameRecordingId", gameHistoryEntry.getWinnerRecording());
                    historyEntry.setAttribute("deckName", gameHistoryEntry.getWinnerDeckName());
                } else if (gameHistoryEntry.getLoser().equals(resourceOwner.getName()) && gameHistoryEntry.getLoserRecording() != null) {
                    historyEntry.setAttribute("gameRecordingId", gameHistoryEntry.getLoserRecording());
                    historyEntry.setAttribute("deckName", gameHistoryEntry.getLoserDeckName());
                }

                historyEntry.setAttribute("startTime", String.valueOf(gameHistoryEntry.getStartTime().getTime()));
                historyEntry.setAttribute("endTime", String.valueOf(gameHistoryEntry.getEndTime().getTime()));

                gameHistory.appendChild(historyEntry);
            }

            doc.appendChild(gameHistory);

            responseWriter.writeXmlResponse(doc);
        } else {
            responseWriter.writeError(404);
        }
    }
}
