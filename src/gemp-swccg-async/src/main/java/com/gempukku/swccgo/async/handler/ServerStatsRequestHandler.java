package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.game.GameHistoryService;
import com.gempukku.swccgo.game.GameHistoryStatistics;
import com.gempukku.swccgo.game.Player;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class ServerStatsRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    private GameHistoryService _gameHistoryService;

    public ServerStatsRequestHandler(Map<Type, Object> context) {
        super(context);

        _gameHistoryService = extractObject(context, GameHistoryService.class);
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, String remoteIp) throws Exception {
        if ("".equals(uri) && request.method() == HttpMethod.GET) {
            QueryStringDecoder queryDecoder = new QueryStringDecoder(request.uri());
            String participantId = getQueryParameterSafely(queryDecoder, "participantId");
            String startDay = getQueryParameterSafely(queryDecoder, "startDay");
            String length = getQueryParameterSafely(queryDecoder, "length");

            Player resourceOwner = getResourceOwnerSafely(request, participantId);

            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                format.setTimeZone(TimeZone.getTimeZone("GMT"));
                long from = format.parse(startDay).getTime();
                Date to = format.parse(startDay);
                if (length.equals("month"))
                    to.setMonth(to.getMonth() + 1);
                else if (length.equals("week"))
                    to.setDate(to.getDate() + 7);
                else if (length.equals("day"))
                    to.setDate(to.getDate() + 1);
                else
                    throw new HttpProcessingException(400);
                long duration = to.getTime() - from;

                int activePlayers = _gameHistoryService.getActivePlayersCount(from, duration);
                int gamesCount = _gameHistoryService.getGamesPlayedCount(from, duration);

                GameHistoryStatistics gameHistoryStatistics = _gameHistoryService.getGameHistoryStatistics(from, duration);

                DecimalFormat percFormat = new DecimalFormat("#0.0%");

                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document doc = documentBuilder.newDocument();
                Element stats = doc.createElement("stats");
                stats.setAttribute("activePlayers", String.valueOf(activePlayers));
                stats.setAttribute("gamesCount", String.valueOf(gamesCount));
                stats.setAttribute("start", format.format(new Date(from)));
                stats.setAttribute("end", format.format(new Date(from + duration - 1)));
                for (GameHistoryStatistics.FormatStat formatStat : gameHistoryStatistics.getFormatStats()) {
                    Element formatStatElem = doc.createElement("formatStat");
                    formatStatElem.setAttribute("format", formatStat.getFormat());
                    formatStatElem.setAttribute("count", String.valueOf(formatStat.getCount()));
                    formatStatElem.setAttribute("perc", percFormat.format(formatStat.getPercentage()));
                    stats.appendChild(formatStatElem);
                }

                doc.appendChild(stats);

                responseWriter.writeXmlResponse(doc);
            } catch (ParseException exp) {
                throw new HttpProcessingException(400);
            }
        } else {
            responseWriter.writeError(404);
        }
    }
}
