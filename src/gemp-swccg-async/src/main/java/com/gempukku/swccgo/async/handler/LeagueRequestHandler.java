package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.DateUtils;
import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.collection.DeckRenderer;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.competitive.PlayerStanding;
import com.gempukku.swccgo.draft2.SoloDraftDefinitions;
import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.db.vo.LeagueMatchResult;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.league.LeagueData;
import com.gempukku.swccgo.league.LeagueSeriesData;
import com.gempukku.swccgo.league.LeagueService;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import com.gempukku.swccgo.tournament.Tournament;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.apache.commons.text.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class LeagueRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    private final SoloDraftDefinitions _soloDraftDefinitions;
    private LeagueService _leagueService;
    private final SwccgCardBlueprintLibrary _bpLibrary;
    private SwccgoFormatLibrary _formatLibrary;
    private final SortAndFilterCards _sortAndFilterCards;
    private final DeckRenderer _deckRenderer;

    public LeagueRequestHandler(Map<Type, Object> context) {
        super(context);

        _leagueService = extractObject(context, LeagueService.class);
        _bpLibrary = extractObject(context, SwccgCardBlueprintLibrary.class);
        _formatLibrary = extractObject(context, SwccgoFormatLibrary.class);
        _sortAndFilterCards = new SortAndFilterCards();

        _soloDraftDefinitions = extractObject(context, SoloDraftDefinitions.class);

        _deckRenderer = new DeckRenderer(_bpLibrary, _formatLibrary, _sortAndFilterCards);
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, String remoteIp) throws Exception {
        if ("".equals(uri) && request.getMethod() == HttpMethod.GET) {
            getNonExpiredLeagues(request, responseWriter);
        } else if (uri.startsWith("/deck") && uri.endsWith("/html") && request.method() == HttpMethod.GET) {
            getAutoLockedLeagueDeck(request, responseWriter);
        } else if (uri.startsWith("/") && request.getMethod() == HttpMethod.GET) {
            getLeagueInformation(request, uri.substring(1), responseWriter);
        } else if (uri.startsWith("/") && request.getMethod() == HttpMethod.POST) {
            joinLeague(request, uri.substring(1), responseWriter, remoteIp);
        } else {
            responseWriter.writeError(404);
        }
    }

    private void joinLeague(HttpRequest request, String leagueType, ResponseWriter responseWriter, String remoteIp) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            String participantId = getFormParameterSafely(postDecoder, "participantId");

            Player resourceOwner = getResourceOwnerSafely(request, participantId);

            League league = _leagueService.getLeagueByType(leagueType);
            if (league == null)
                throw new HttpProcessingException(404);

            if (!_leagueService.playerJoinsLeague(league, resourceOwner, remoteIp, false))
                throw new HttpProcessingException(409);

            responseWriter.writeXmlResponse(null);
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void getLeagueInformation(HttpRequest request, String leagueType, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.getUri());
        String participantId = getQueryParameterSafely(queryDecoder, "participantId");

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        Document doc = documentBuilder.newDocument();

        League league = getLeagueByType(leagueType);

        if (league == null)
            throw new HttpProcessingException(404);

        final LeagueData leagueData = league.getLeagueData(_soloDraftDefinitions);
        final List<LeagueSeriesData> series = leagueData.getSeries();

        int end = series.get(series.size() - 1).getEnd();
        int start = series.get(0).getStart();
        int currentDate = DateUtils.getCurrentDate();

        Element leagueElem = doc.createElement("league");
        boolean inLeague = _leagueService.isPlayerInLeague(league, resourceOwner);

        leagueElem.setAttribute("member", String.valueOf(inLeague));
        leagueElem.setAttribute("joinable", String.valueOf(!inLeague && end >= DateUtils.getCurrentDate()));
        leagueElem.setAttribute("isSoloDraft", String.valueOf(leagueData.isSoloDraftLeague()));
        leagueElem.setAttribute("draftable", String.valueOf(inLeague && leagueData.isSoloDraftLeague() && start <= currentDate));
        leagueElem.setAttribute("type", league.getType());
        leagueElem.setAttribute("name", league.getName());
        leagueElem.setAttribute("cost", String.valueOf(league.getCost()));
        leagueElem.setAttribute("invitationOnly", String.valueOf(league.getInvitationOnly()));
        leagueElem.setAttribute("registrationInfo", league.getRegistrationInfo());
        leagueElem.setAttribute("start", String.valueOf(series.get(0).getStart()));
        leagueElem.setAttribute("end", String.valueOf(end));
        leagueElem.setAttribute("allowTimeExtensions", String.valueOf(league.getAllowTimeExtensions()));
        leagueElem.setAttribute("allowSpectators", String.valueOf(league.getAllowSpectators()));
        leagueElem.setAttribute("showPlayerNames", String.valueOf(league.getShowPlayerNames()));
        leagueElem.setAttribute("decisionTimeoutSeconds", String.valueOf(league.getDecisionTimeoutSeconds()));
        leagueElem.setAttribute("timePerPlayerMinutes", String.valueOf(league.getTimePerPlayerMinutes()));
        leagueElem.setAttribute("lockedDeckType", league.getLockedDeckType() == null ? "" : league.getLockedDeckType().getCode());

        for (LeagueSeriesData serie : series) {
            Element serieElem = doc.createElement("serie");
            serieElem.setAttribute("type", serie.getName());
            serieElem.setAttribute("maxMatches", String.valueOf(serie.getMaxMatches()));
            serieElem.setAttribute("start", String.valueOf(serie.getStart()));
            serieElem.setAttribute("end", String.valueOf(serie.getEnd()));
            serieElem.setAttribute("formatType", serie.getFormat());
            serieElem.setAttribute("format", _formatLibrary.getFormat(serie.getFormat()).getName());
            serieElem.setAttribute("collection", serie.getCollectionType().getFullName());
            serieElem.setAttribute("limited", String.valueOf(serie.isLimited()));

            Element matchesElem = doc.createElement("matches");
            Collection<LeagueMatchResult> playerMatches = _leagueService.getPlayerMatchesInSeries(league, serie, resourceOwner.getName());
            for (LeagueMatchResult playerMatch : playerMatches) {
                Element matchElem = doc.createElement("match");
                matchElem.setAttribute("winner", playerMatch.getWinner());
                matchElem.setAttribute("loser", playerMatch.getLoser());
                matchesElem.appendChild(matchElem);
            }
            serieElem.appendChild(matchesElem);

            final List<PlayerStanding> standings = _leagueService.getLeagueSeriesStandings(league, serie);
            for (PlayerStanding standing : standings) {
                Element standingElem = doc.createElement("standing");
                setStandingAttributes(standing, standingElem);
                serieElem.appendChild(standingElem);
            }

            leagueElem.appendChild(serieElem);
        }

        List<PlayerStanding> leagueStandings = _leagueService.getLeagueStandings(league);
        for (PlayerStanding standing : leagueStandings) {
            Element standingElem = doc.createElement("leagueStanding");
            setStandingAttributes(standing, standingElem);
            leagueElem.appendChild(standingElem);
        }

        doc.appendChild(leagueElem);

        responseWriter.writeXmlResponse(doc);
    }

    private void getNonExpiredLeagues(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document doc = documentBuilder.newDocument();
        Element leagues = doc.createElement("leagues");

        for (League league : _leagueService.getActiveLeagues()) {
            final LeagueData leagueData = league.getLeagueData(_soloDraftDefinitions);
            final List<LeagueSeriesData> series = leagueData.getSeries();

            int end = series.get(series.size() - 1).getEnd();

            Element leagueElem = doc.createElement("league");
            QueryStringDecoder queryDecoder = new QueryStringDecoder(request.getUri());
            String participantId = getQueryParameterSafely(queryDecoder, "participantId");
            Player resourceOwner = getResourceOwnerSafely(request, participantId);
            boolean inLeague = _leagueService.isPlayerInLeague(league, resourceOwner);
            leagueElem.setAttribute("member", String.valueOf(inLeague));

            leagueElem.setAttribute("type", league.getType());
            leagueElem.setAttribute("name", league.getName());
            leagueElem.setAttribute("start", String.valueOf(series.get(0).getStart()));
            leagueElem.setAttribute("end", String.valueOf(end));

            leagues.appendChild(leagueElem);
        }

        doc.appendChild(leagues);

        responseWriter.writeXmlResponse(doc);
    }

    private void getAutoLockedLeagueDeck(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.getUri());
        String participantId = getQueryParameterSafely(queryDecoder, "participantId");
        var resourceOwner = getResourceOwnerSafely(request, participantId);
        var player = resourceOwner.getName();

        String leagueType = getQueryParameterSafely(queryDecoder, "leagueType");
        League league = getLeagueByType(leagueType);

        if (league == null)
            throw new HttpProcessingException(404);

        SwccgDeck lsDeck = _leagueService.getLockedDeck(league, player, Side.LIGHT);
        SwccgDeck dsDeck = _leagueService.getLockedDeck(league, player, Side.DARK);

        String ls = lsDeck == null ? "<h1>No Light Side deck locked in</h1>." : _deckRenderer.convertDeckToHTMLFragment(lsDeck, league.getName(), player);
        String ds = dsDeck == null ? "<h1>No Dark Side deck locked in.</h1>" : _deckRenderer.convertDeckToHTMLFragment(dsDeck, league.getName(), player);

        String preamble = "";

        if(lsDeck != null) {
            preamble += "<h1>Locked-in Light Side Deck: " + lsDeck.getDeckName() + "</h1>";
        }
        else {
            preamble += "<h1>Locked-in Light Side Deck: None</h1>";
        }

        if(dsDeck != null) {
            preamble += "<h1>Locked-in Dark Side Deck: " + dsDeck.getDeckName() + "</h1>";
        }
        else {
            preamble += "<h1>Locked-in Dark Side Deck: None</h1>";
        }

        preamble += "<hr>";

        responseWriter.writeHtmlResponse(_deckRenderer.AddDeckReadoutHeaderAndFooter(preamble, ls, ds));
    }

    public League getLeagueByType(String type) {
        for (League league : _leagueService.getActiveLeagues()) {
            if (league.getType().equals(type))
                return league;
        }
        return null;
    }

    private void setStandingAttributes(PlayerStanding standing, Element standingElem) {
        standingElem.setAttribute("player", standing.getPlayerName());
        standingElem.setAttribute("standing", String.valueOf(standing.getStanding()));
        standingElem.setAttribute("points", String.valueOf(standing.getPoints()));
        standingElem.setAttribute("gamesPlayed", String.valueOf(standing.getGamesPlayed()));
        DecimalFormat format = new DecimalFormat("##0.00%");
        standingElem.setAttribute("opponentWin", format.format(standing.getOpponentWin()));
    }

}
