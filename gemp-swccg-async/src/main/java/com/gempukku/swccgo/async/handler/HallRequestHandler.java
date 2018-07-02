package com.gempukku.swccgo.async.handler;

import com.gempukku.polling.LongPollingResource;
import com.gempukku.polling.LongPollingSystem;
import com.gempukku.swccgo.SubscriptionConflictException;
import com.gempukku.swccgo.SubscriptionExpiredException;
import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.draft.Draft;
import com.gempukku.swccgo.draft.DraftChannelVisitor;
import com.gempukku.swccgo.draft.DraftCommunicationChannel;
import com.gempukku.swccgo.draft.DraftFinishedException;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.hall.HallChannelVisitor;
import com.gempukku.swccgo.hall.HallCommunicationChannel;
import com.gempukku.swccgo.hall.HallException;
import com.gempukku.swccgo.hall.HallServer;
import com.gempukku.swccgo.league.LeagueSeriesData;
import com.gempukku.swccgo.league.LeagueService;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class HallRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    private CollectionsManager _collectionManager;
    private SwccgoFormatLibrary _formatLibrary;
    private HallServer _hallServer;
    private LeagueService _leagueService;
    private SwccgCardBlueprintLibrary _library;
    private LongPollingSystem _longPollingSystem;
    private SwccgoServer _swccgoServer;

    public HallRequestHandler(Map<Type, Object> context) {
        super(context);
        _collectionManager = extractObject(context, CollectionsManager.class);
        _formatLibrary = extractObject(context, SwccgoFormatLibrary.class);
        _hallServer = extractObject(context, HallServer.class);
        _leagueService = extractObject(context, LeagueService.class);
        _library = extractObject(context, SwccgCardBlueprintLibrary.class);
        _longPollingSystem = extractObject(context, LongPollingSystem.class);
        _swccgoServer = extractObject(context, SwccgoServer.class);
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, MessageEvent e) throws Exception {
        if ("".equals(uri) && request.getMethod() == HttpMethod.GET) {
            getHall(request, responseWriter);
        } else if ("".equals(uri) && request.getMethod() == HttpMethod.POST) {
            createTable(request, responseWriter);
        } else if (uri.equals("/update") && request.getMethod() == HttpMethod.POST) {
            updateHall(request, responseWriter);
        } else if (uri.startsWith("/draft/") && uri.endsWith("/update") && request.getMethod() == HttpMethod.POST) {
            updateDraft(request, uri.substring(7, uri.length() - 7), responseWriter);
        } else if (uri.startsWith("/draft/") && uri.endsWith("/pick") && request.getMethod() == HttpMethod.POST) {
            draftPick(request, uri.substring(7, uri.length() - 5), responseWriter);
        } else if (uri.startsWith("/draft/") && request.getMethod() == HttpMethod.GET) {
            getDraft(request, uri.substring(7), responseWriter);
        } else if (uri.equals("/formats/html") && request.getMethod() == HttpMethod.GET) {
            getFormats(request, responseWriter);
        } else if (uri.startsWith("/format/") && request.getMethod() == HttpMethod.GET) {
            getFormat(request, uri.substring(8), responseWriter);
        } else if (uri.startsWith("/queue/") && request.getMethod() == HttpMethod.POST) {
            if (uri.endsWith("/leave")) {
                leaveQueue(request, uri.substring(7, uri.length() - 6), responseWriter);
            } else {
                joinQueue(request, uri.substring(7), responseWriter);
            }
        } else if (uri.startsWith("/tournament/") && uri.endsWith("/deck") && request.getMethod() == HttpMethod.POST) {
            submitTournamentDeck(request, uri.substring(12, uri.length() - 5), responseWriter);
        } else if (uri.startsWith("/tournament/") && uri.endsWith("/leave") && request.getMethod() == HttpMethod.POST) {
            dropFromTournament(request, uri.substring(12, uri.length() - 6), responseWriter);
        } else if (uri.startsWith("/") && uri.endsWith("/leave") && request.getMethod() == HttpMethod.POST) {
            leaveTable(request, uri.substring(1, uri.length() - 6), responseWriter);
        } else if (uri.startsWith("/") && request.getMethod() == HttpMethod.POST) {
            joinTable(request, uri.substring(1), responseWriter);
        } else {
            responseWriter.writeError(404);
        }
    }

    private void submitTournamentDeck(HttpRequest request, String tournamentId, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");
        String contents = getFormParameterSafely(postDecoder, "deckContents");
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        SwccgDeck swccgDeck = _swccgoServer.createDeckWithValidate("Limited deck", contents);
        if (swccgDeck == null)
            throw new HttpProcessingException(400);

        try {
            _hallServer.submitTournamentDeck(tournamentId, resourceOwner, swccgDeck);
            responseWriter.writeXmlResponse(null);
        } catch (HallException e) {
            responseWriter.writeXmlResponse(marshalException(e));
        }
    }

    private void draftPick(HttpRequest request, String tournamentId, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");
        String blueprintId = getFormParameterSafely(postDecoder, "blueprintId");
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        try {
            _hallServer.getDraft(tournamentId).playerChosenCard(resourceOwner.getName(), blueprintId);
            responseWriter.writeXmlResponse(null);
        } catch (DraftFinishedException exp) {
            responseWriter.writeError(204);
        }
    }

    private void updateDraft(HttpRequest request, String tournamentId, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");
        int channelNumber = Integer.parseInt(getFormParameterSafely(postDecoder, "channelNumber"));
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        try {
            DraftCommunicationChannel pollableResource = _hallServer.getDraft(tournamentId).getCommunicationChannel(resourceOwner.getName(), channelNumber);
            DraftUpdateLongPollingResource polledResource = new DraftUpdateLongPollingResource(tournamentId, resourceOwner, channelNumber, responseWriter);
            _longPollingSystem.processLongPollingResource(polledResource, pollableResource);
        } catch (DraftFinishedException e) {
            responseWriter.writeError(204);
        } catch (SubscriptionConflictException e) {
            responseWriter.writeError(409);
        } catch (SubscriptionExpiredException e) {
            responseWriter.writeError(410);
        }
    }

    private class DraftUpdateLongPollingResource implements LongPollingResource {
        private Player _player;
        private int _channelNumber;
        private String _tournamentId;
        private ResponseWriter _responseWriter;
        private boolean _processed;

        private DraftUpdateLongPollingResource(String tournamentId, Player player, int channelNumber, ResponseWriter responseWriter) {
            _tournamentId = tournamentId;
            _player = player;
            _channelNumber = channelNumber;
            _responseWriter = responseWriter;
        }

        public boolean isChanged() {
            try {
                Draft draft = _hallServer.getDraft(_tournamentId);
                return draft.getCommunicationChannel(_player.getName(), _channelNumber).hasChangesInCommunicationChannel(draft.getCardChoice(_player.getName()));
            } catch (DraftFinishedException e) {
                return true;
            } catch (SubscriptionConflictException e) {
                return true;
            } catch (SubscriptionExpiredException e) {
                return true;
            }
        }

        @Override
        public synchronized boolean wasProcessed() {
            return _processed;
        }

        public synchronized void processIfNotProcessed() {
            if (!_processed) {
                try {
                    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                    Document doc = documentBuilder.newDocument();

                    Element draftElem = doc.createElement("draft");

                    Draft draft = _hallServer.getDraft(_tournamentId);
                    SerializeDraftVisitor serializeDraftVisitor = new SerializeDraftVisitor(doc, draftElem);
                    draft.getCommunicationChannel(_player.getName(), _channelNumber)
                            .processCommunicationChannel(draft.getCardChoice(_player.getName()), draft.getChosenCards(_player.getName()), serializeDraftVisitor);

                    doc.appendChild(draftElem);

                    _responseWriter.writeXmlResponse(doc);
                } catch (DraftFinishedException e) {
                    _responseWriter.writeError(204);
                } catch (SubscriptionConflictException e) {
                    _responseWriter.writeError(409);
                } catch (SubscriptionExpiredException e) {
                    _responseWriter.writeError(410);
                } catch (Exception exp) {
                    _responseWriter.writeError(500);
                }
                _processed = true;
            }
        }
    }

    private void getDraft(HttpRequest request, String tournamentId, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document doc = documentBuilder.newDocument();

        Element draft = doc.createElement("draft");

        try {
            _hallServer.singupForDraft(tournamentId, resourceOwner, new SerializeDraftVisitor(doc, draft));

            doc.appendChild(draft);

            responseWriter.writeXmlResponse(doc);
        } catch (DraftFinishedException exp) {
            responseWriter.writeError(204);
        }
    }

    private void joinTable(HttpRequest request, String tableId, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");
        String deckName = getFormParameterSafely(postDecoder, "deckName");
        String sampleDeckVal = getFormParameterSafely(postDecoder, "sampleDeck");
        boolean sampleDeck = sampleDeckVal != null ? Boolean.valueOf(sampleDeckVal) : false;

        Player resourceOwner = getResourceOwnerSafely(request, participantId);
        Player librarian = sampleDeck ? getLibrarian() : null;

        try {
            _hallServer.joinTableAsPlayer(tableId, resourceOwner, deckName, sampleDeck, librarian);
            responseWriter.writeXmlResponse(null);
        } catch (HallException e) {
            responseWriter.writeXmlResponse(marshalException(e));
        }
    }

    private void leaveTable(HttpRequest request, String tableId, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        _hallServer.leaveAwaitingTable(resourceOwner, tableId);
        responseWriter.writeXmlResponse(null);
    }

    private void createTable(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");
        String format = getFormParameterSafely(postDecoder, "format");
        String deckName = getFormParameterSafely(postDecoder, "deckName");
        String sampleDeckVal = getFormParameterSafely(postDecoder, "sampleDeck");
        boolean sampleDeck = sampleDeckVal != null ? Boolean.valueOf(sampleDeckVal) : false;
        String tableDesc = getFormParameterSafely(postDecoder, "tableDesc");

        Player resourceOwner = getResourceOwnerSafely(request, participantId);
        Player librarian = sampleDeck ? getLibrarian() : null;

        try {
            _hallServer.createNewTable(format, resourceOwner, deckName, sampleDeck, tableDesc, librarian);
            responseWriter.writeXmlResponse(null);
        } catch (HallException e) {
            responseWriter.writeXmlResponse(marshalException(e));
        }
    }

    private void dropFromTournament(HttpRequest request, String tournamentId, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        _hallServer.dropFromTournament(tournamentId, resourceOwner);

        responseWriter.writeXmlResponse(null);
    }

    private void joinQueue(HttpRequest request, String queueId, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");
        String deckName = getFormParameterSafely(postDecoder, "deckName");
        String sampleDeckVal = getFormParameterSafely(postDecoder, "sampleDeck");
        boolean sampleDeck = sampleDeckVal != null ? Boolean.valueOf(sampleDeckVal) : false;

        Player resourceOwner = getResourceOwnerSafely(request, participantId);
        Player librarian = sampleDeck ? getLibrarian() : null;

        try {
            _hallServer.joinQueue(queueId, resourceOwner, deckName, sampleDeck, librarian);
            responseWriter.writeXmlResponse(null);
        } catch (HallException e) {
            responseWriter.writeXmlResponse(marshalException(e));
        }
    }

    private void leaveQueue(HttpRequest request, String queueId, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");

        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        _hallServer.leaveQueue(queueId, resourceOwner);

        responseWriter.writeXmlResponse(null);
    }

    private Document marshalException(HallException e) throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document doc = documentBuilder.newDocument();

        Element error = doc.createElement("error");
        error.setAttribute("message", e.getMessage());
        doc.appendChild(error);
        return doc;
    }

    private void getFormat(HttpRequest request, String format, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.getUri());
        String participantId = getQueryParameterSafely(queryDecoder, "participantId");
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        StringBuilder result = new StringBuilder();
        SwccgFormat swccgFormat = _formatLibrary.getFormat(format);

        // Only show playtesting formats if player is a playtester or admin
        if (!swccgFormat.isPlaytesting()
                || resourceOwner.hasType(Player.Type.ADMIN)
                || resourceOwner.hasType(Player.Type.PLAY_TESTER)) {

            result.append("<b>" + swccgFormat.getName() + "</b>");
            result.append("<ul>");
            Integer deckSize = swccgFormat.getRequiredDeckSize();
            if (deckSize != 60) {
                result.append("<li>Deck size: " + deckSize);
                result.append("</li>");
            }
            result.append("<li>Valid sets: ");
            for (Integer integer : swccgFormat.getValidSets()) {
                ExpansionSet expansionSet = ExpansionSet.getSetFromNumber(integer);
                if (expansionSet != null) {
                    result.append(expansionSet.getHumanReadable() + ", ");
                }
            }
            result.append("</li>");
            if (!swccgFormat.getBannedIcons().isEmpty()) {
                result.append("<li>Icons not allowed: ");
                for (String iconName : swccgFormat.getBannedIcons()) {
                    Icon icon = Icon.getIconFromName(iconName);
                    if (icon != null) {
                        result.append("[" + icon.getHumanReadable() + "], ");
                    }
                }
                result.append("</li>");
            }
            if (!swccgFormat.getBannedCards().isEmpty()) {
                result.append("<li>X-listed: ");
                for (String blueprintId : swccgFormat.getBannedCards()) {
                    SwccgCardBlueprint blueprint = _library.getSwccgoCardBlueprint(blueprintId);
                    SwccgCardBlueprint backSideBlueprint = _library.getSwccgoCardBlueprintBack(blueprintId);
                    result.append(GameUtils.getCardLink(blueprintId, blueprint, backSideBlueprint)).append(", ");
                }
                result.append("</li>");
            }
            if (!swccgFormat.getRestrictedCards().isEmpty()) {
                result.append("<li>R-listed: ");
                for (String blueprintId : swccgFormat.getRestrictedCards()) {
                    SwccgCardBlueprint blueprint = _library.getSwccgoCardBlueprint(blueprintId);
                    SwccgCardBlueprint backSideBlueprint = _library.getSwccgoCardBlueprintBack(blueprintId);
                    result.append(GameUtils.getCardLink(blueprintId, blueprint, backSideBlueprint)).append(", ");
                }
                result.append("</li>");
            }
            if (!swccgFormat.getValidCards().isEmpty()) {
                result.append("<li>Additional valid: ");
                for (String blueprintId : swccgFormat.getValidCards()) {
                    SwccgCardBlueprint blueprint = _library.getSwccgoCardBlueprint(blueprintId);
                    SwccgCardBlueprint backSideBlueprint = _library.getSwccgoCardBlueprintBack(blueprintId);
                    result.append(GameUtils.getCardLink(blueprintId, blueprint, backSideBlueprint)).append(", ");
                }
                result.append("</li>");
            }
            if (swccgFormat.hasDownloadBattlegroundRule()) {
                result.append("<li>Once per game, may ▼ a unique battleground not already on table");
                result.append("</li>");
            }
            if (swccgFormat.hasJpSealedRule()) {
                result.append("<li>Any character of ability 1 who has a printed deploy number of 3 or greater is considered, for all purposes, to be ability 2");
                result.append("</li>");
            }
            result.append("</ul>");
        }

        responseWriter.writeHtmlResponse(result.toString());
    }

    private void getFormats(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.getUri());
        String participantId = getQueryParameterSafely(queryDecoder, "participantId");
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        StringBuilder result = new StringBuilder();
        for (SwccgFormat swccgFormat : _formatLibrary.getHallFormats().values()) {
            // Only show playtesting formats if player is a playtester or admin
            if (swccgFormat.isPlaytesting()
                    && !(resourceOwner.hasType(Player.Type.ADMIN)
                    || resourceOwner.hasType(Player.Type.PLAY_TESTER))) {
                continue;
            }
            result.append("<b>" + swccgFormat.getName() + "</b>");
            result.append("<ul>");
            Integer deckSize = swccgFormat.getRequiredDeckSize();
            if (deckSize!=60) {
                result.append("<li>Deck size: " + deckSize);
                result.append("</li>");
            }
            result.append("<li>Valid sets: ");
            for (Integer integer : swccgFormat.getValidSets()) {
                ExpansionSet expansionSet = ExpansionSet.getSetFromNumber(integer);
                if (expansionSet != null) {
                    result.append(expansionSet.getHumanReadable() + ", ");
                }
            }
            result.append("</li>");
            if (!swccgFormat.getBannedIcons().isEmpty()) {
                result.append("<li>Icons not allowed: ");
                for (String iconName : swccgFormat.getBannedIcons()) {
                    Icon icon = Icon.getIconFromName(iconName);
                    if (icon != null) {
                        result.append("[" + icon.getHumanReadable() + "], ");
                    }
                }
                result.append("</li>");
            }
            if (!swccgFormat.getBannedCards().isEmpty()) {
                result.append("<li>X-listed: ");
                for (String blueprintId : swccgFormat.getBannedCards()) {
                    SwccgCardBlueprint blueprint = _library.getSwccgoCardBlueprint(blueprintId);
                    SwccgCardBlueprint backSideBlueprint = _library.getSwccgoCardBlueprintBack(blueprintId);
                    result.append(GameUtils.getCardLink(blueprintId, blueprint, backSideBlueprint)).append(", ");
                }
                result.append("</li>");
            }
            if (!swccgFormat.getRestrictedCards().isEmpty()) {
                result.append("<li>R-listed: ");
                for (String blueprintId : swccgFormat.getRestrictedCards()) {
                    SwccgCardBlueprint blueprint = _library.getSwccgoCardBlueprint(blueprintId);
                    SwccgCardBlueprint backSideBlueprint = _library.getSwccgoCardBlueprintBack(blueprintId);
                    result.append(GameUtils.getCardLink(blueprintId, blueprint, backSideBlueprint)).append(", ");
                }
                result.append("</li>");
            }
            if (!swccgFormat.getValidCards().isEmpty()) {
                result.append("<li>Additional valid: ");
                for (String blueprintId : swccgFormat.getValidCards()) {
                    SwccgCardBlueprint blueprint = _library.getSwccgoCardBlueprint(blueprintId);
                    SwccgCardBlueprint backSideBlueprint = _library.getSwccgoCardBlueprintBack(blueprintId);
                    result.append(GameUtils.getCardLink(blueprintId, blueprint, backSideBlueprint)).append(", ");
                }
                result.append("</li>");
            }
            if (swccgFormat.hasDownloadBattlegroundRule()) {
                result.append("<li>Once per game, may ▼ a unique battleground not already on table");
                result.append("</li>");
            }
            if (swccgFormat.hasJpSealedRule()) {
                result.append("<li>Any character of ability 1 who has a printed deploy number of 3 or greater is considered, for all purposes, to be ability 2");
                result.append("</li>");
            }
            result.append("</ul>");
        }

        responseWriter.writeHtmlResponse(result.toString());
    }

    private void getHall(HttpRequest request, ResponseWriter responseWriter) {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.getUri());

        String participantId = getQueryParameterSafely(queryDecoder, "participantId");

        try {
            Player resourceOwner = getResourceOwnerSafely(request, participantId);

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document doc = documentBuilder.newDocument();

            Element hall = doc.createElement("hall");
            hall.setAttribute("currency", String.valueOf(_collectionManager.getPlayerCollection(resourceOwner, "permanent").getCurrency()));

            _hallServer.signupUserForHall(resourceOwner, new SerializeHallInfoVisitor(doc, hall));
            for (Map.Entry<String, SwccgFormat> format : _formatLibrary.getHallFormats().entrySet()) {
                // Only show playtesting formats if player is a playtester or admin
                if (format.getValue().isPlaytesting()
                        && !(resourceOwner.hasType(Player.Type.ADMIN)
                        || resourceOwner.hasType(Player.Type.PLAY_TESTER))) {
                    continue;
                }
                Element formatElem = doc.createElement("format");
                formatElem.setAttribute("type", format.getKey());
                formatElem.appendChild(doc.createTextNode(format.getValue().getName()));
                hall.appendChild(formatElem);
            }
            for (League league : _leagueService.getActiveLeagues()) {
                final LeagueSeriesData currentLeagueSerie = _leagueService.getCurrentLeagueSeries(league);
                if (currentLeagueSerie != null && _leagueService.isPlayerInLeague(league, resourceOwner)) {
                    Element formatElem = doc.createElement("format");
                    formatElem.setAttribute("type", league.getType());
                    formatElem.appendChild(doc.createTextNode(league.getName()));
                    hall.appendChild(formatElem);
                }
            }

            doc.appendChild(hall);

            responseWriter.writeXmlResponse(doc);
        } catch (HttpProcessingException exp) {
            responseWriter.writeError(exp.getStatus());
        } catch (Exception exp) {
            responseWriter.writeError(500);
        }
    }

    private void updateHall(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");
        int channelNumber = Integer.parseInt(getFormParameterSafely(postDecoder, "channelNumber"));

        Player resourceOwner = getResourceOwnerSafely(request, participantId);
        processLoginReward(resourceOwner.getName());

        try {
            HallCommunicationChannel pollableResource = _hallServer.getCommunicationChannel(resourceOwner, channelNumber);
            HallUpdateLongPollingResource polledResource = new HallUpdateLongPollingResource(pollableResource, request, resourceOwner, responseWriter);
            _longPollingSystem.processLongPollingResource(polledResource, pollableResource);
        } catch (SubscriptionExpiredException exp) {
            responseWriter.writeError(410);
        } catch (SubscriptionConflictException exp) {
            responseWriter.writeError(409);
        }
    }

    private class HallUpdateLongPollingResource implements LongPollingResource {
        private HttpRequest _request;
        private HallCommunicationChannel _hallCommunicationChannel;
        private Player _resourceOwner;
        private ResponseWriter _responseWriter;
        private boolean _processed;

        private HallUpdateLongPollingResource(HallCommunicationChannel hallCommunicationChannel, HttpRequest request, Player resourceOwner, ResponseWriter responseWriter) {
            _hallCommunicationChannel = hallCommunicationChannel;
            _request = request;
            _resourceOwner = resourceOwner;
            _responseWriter = responseWriter;
        }

        @Override
        public synchronized boolean wasProcessed() {
            return _processed;
        }

        @Override
        public synchronized void processIfNotProcessed() {
            if (!_processed) {
                try {
                    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                    Document doc = documentBuilder.newDocument();

                    Element hall = doc.createElement("hall");
                    _hallCommunicationChannel.processCommunicationChannel(_hallServer, _resourceOwner, new SerializeHallInfoVisitor(doc, hall));
                    hall.setAttribute("currency", String.valueOf(_collectionManager.getPlayerCollection(_resourceOwner, "permanent").getCurrency()));

                    doc.appendChild(hall);

                    Map<String, String> headers = new HashMap<String, String>();
                    processDeliveryServiceNotification(_request, headers);

                    _responseWriter.writeXmlResponse(doc, headers);
                } catch (Exception exp) {
                    _responseWriter.writeError(500);
                }
                _processed = true;
            }
        }
    }

    private class SerializeDraftVisitor implements DraftChannelVisitor {
        private Document _doc;
        private Element _draft;

        private SerializeDraftVisitor(Document doc, Element draft) {
            _doc = doc;
            _draft = draft;
        }

        public void channelNumber(int channelNumber) {
            _draft.setAttribute("channelNumber", String.valueOf(channelNumber));
        }

        public void timeLeft(long timeLeft) {
            _draft.setAttribute("timeLeft", String.valueOf(timeLeft));
        }

        public void noCardChoice() {
        }

        public void cardChoice(CardCollection cardCollection) {
            for (CardCollection.Item possiblePick : cardCollection.getAll().values()) {
                for (int i = 0; i < possiblePick.getCount(); i++) {
                    Element pick = _doc.createElement("pick");
                    pick.setAttribute("blueprintId", possiblePick.getBlueprintId());
                    _draft.appendChild(pick);
                }
            }
        }

        public void chosenCards(CardCollection cardCollection) {
            for (CardCollection.Item cardInCollection : cardCollection.getAll().values()) {
                Element card = _doc.createElement("card");
                card.setAttribute("blueprintId", cardInCollection.getBlueprintId());
                card.setAttribute("count", String.valueOf(cardInCollection.getCount()));
                _draft.appendChild(card);
            }
        }
    }

    private class SerializeHallInfoVisitor implements HallChannelVisitor {
        private Document _doc;
        private Element _hall;

        public SerializeHallInfoVisitor(Document doc, Element hall) {
            _doc = doc;
            _hall = hall;
        }

        @Override
        public void channelNumber(int channelNumber) {
            _hall.setAttribute("channelNumber", String.valueOf(channelNumber));
        }

        @Override
        public void newPlayerGame(String gameId) {
            Element newGame = _doc.createElement("newGame");
            newGame.setAttribute("id", gameId);
            _hall.appendChild(newGame);
        }

        @Override
        public void serverTime(String serverTime) {
            _hall.setAttribute("serverTime", serverTime);
        }

        @Override
        public void motdChanged(String motd) {
            _hall.setAttribute("motd", motd);
        }

        @Override
        public void addTournamentQueue(String queueId, Map<String, String> props) {
            Element queue = _doc.createElement("queue");
            queue.setAttribute("action", "add");
            queue.setAttribute("id", queueId);
            for (Map.Entry<String, String> attribute : props.entrySet())
                queue.setAttribute(attribute.getKey(), attribute.getValue());
            _hall.appendChild(queue);
        }

        @Override
        public void updateTournamentQueue(String queueId, Map<String, String> props) {
            Element queue = _doc.createElement("queue");
            queue.setAttribute("action", "update");
            queue.setAttribute("id", queueId);
            for (Map.Entry<String, String> attribute : props.entrySet())
                queue.setAttribute(attribute.getKey(), attribute.getValue());
            _hall.appendChild(queue);
        }

        @Override
        public void removeTournamentQueue(String queueId) {
            Element queue = _doc.createElement("queue");
            queue.setAttribute("action", "remove");
            queue.setAttribute("id", queueId);
            _hall.appendChild(queue);
        }

        @Override
        public void addTournament(String tournamentId, Map<String, String> props) {
            Element tournament = _doc.createElement("tournament");
            tournament.setAttribute("action", "add");
            tournament.setAttribute("id", tournamentId);
            for (Map.Entry<String, String> attribute : props.entrySet())
                tournament.setAttribute(attribute.getKey(), attribute.getValue());
            _hall.appendChild(tournament);
        }

        @Override
        public void updateTournament(String tournamentId, Map<String, String> props) {
            Element tournament = _doc.createElement("tournament");
            tournament.setAttribute("action", "update");
            tournament.setAttribute("id", tournamentId);
            for (Map.Entry<String, String> attribute : props.entrySet())
                tournament.setAttribute(attribute.getKey(), attribute.getValue());
            _hall.appendChild(tournament);
        }

        @Override
        public void removeTournament(String tournamentId) {
            Element tournament = _doc.createElement("tournament");
            tournament.setAttribute("action", "remove");
            tournament.setAttribute("id", tournamentId);
            _hall.appendChild(tournament);
        }

        @Override
        public void addTable(String tableId, Map<String, String> props) {
            Element table = _doc.createElement("table");
            table.setAttribute("action", "add");
            table.setAttribute("id", tableId);
            for (Map.Entry<String, String> attribute : props.entrySet())
                table.setAttribute(attribute.getKey(), attribute.getValue());
            _hall.appendChild(table);
        }

        @Override
        public void updateTable(String tableId, Map<String, String> props) {
            Element table = _doc.createElement("table");
            table.setAttribute("action", "update");
            table.setAttribute("id", tableId);
            for (Map.Entry<String, String> attribute : props.entrySet())
                table.setAttribute(attribute.getKey(), attribute.getValue());
            _hall.appendChild(table);
        }

        @Override
        public void removeTable(String tableId) {
            Element table = _doc.createElement("table");
            table.setAttribute("action", "remove");
            table.setAttribute("id", tableId);
            _hall.appendChild(table);
        }
    }
}
