package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.db.PlayerStatistic;
import com.gempukku.swccgo.game.GameHistoryService;
import com.gempukku.swccgo.game.Player;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class PlayerStatsRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    private GameHistoryService _gameHistoryService;

    public PlayerStatsRequestHandler(Map<Type, Object> context) {
        super(context);

        _gameHistoryService = extractObject(context, GameHistoryService.class);
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, MessageEvent e) throws Exception {
        if ("".equals(uri) && request.getMethod() == HttpMethod.GET) {
            QueryStringDecoder queryDecoder = new QueryStringDecoder(request.getUri());
            String participantId = getQueryParameterSafely(queryDecoder, "participantId");
            Player resourceOwner = getResourceOwnerSafely(request, participantId);

            List<PlayerStatistic> casualStatistics = _gameHistoryService.getCasualPlayerStatistics(resourceOwner);
            List<PlayerStatistic> competitiveStatistics = _gameHistoryService.getCompetitivePlayerStatistics(resourceOwner);

            DecimalFormat percFormat = new DecimalFormat("#0.0%");

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = documentBuilder.newDocument();
            Element stats = doc.createElement("playerStats");

            Element casual = doc.createElement("casual");
            appendStatistics(casualStatistics, percFormat, doc, casual);
            stats.appendChild(casual);

            Element competitive = doc.createElement("competitive");
            appendStatistics(competitiveStatistics, percFormat, doc, competitive);
            stats.appendChild(competitive);

            doc.appendChild(stats);

            responseWriter.writeXmlResponse(doc);
        } else {
            responseWriter.writeError(404);
        }
    }

    private void appendStatistics(List<PlayerStatistic> statistics, DecimalFormat percFormat, Document doc, Element type) {
        for (PlayerStatistic casualStatistic : statistics) {
            Element entry = doc.createElement("entry");
            entry.setAttribute("deckName", casualStatistic.getDeckName());
            entry.setAttribute("format", casualStatistic.getFormatName());
            entry.setAttribute("wins", String.valueOf(casualStatistic.getWins()));
            entry.setAttribute("losses", String.valueOf(casualStatistic.getLosses()));
            entry.setAttribute("perc", percFormat.format(1f * casualStatistic.getWins() / (casualStatistic.getLosses() + casualStatistic.getWins())));
            type.appendChild(entry);
        }
    }

}
