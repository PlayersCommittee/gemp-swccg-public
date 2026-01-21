package com.gempukku.swccgo.async.handler;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;

import com.gempukku.polling.LongPollingResource;
import com.gempukku.polling.LongPollingSystem;
import com.gempukku.swccgo.SubscriptionConflictException;
import com.gempukku.swccgo.SubscriptionExpiredException;
import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.draft.Draft;
import com.gempukku.swccgo.draft.DraftChannelVisitor;
import com.gempukku.swccgo.draft.DraftCommunicationChannel;
import com.gempukku.swccgo.draft.DraftFinishedException;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.SwccgoServer;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.hall.HallChannelVisitor;
import com.gempukku.swccgo.hall.HallCommunicationChannel;
import com.gempukku.swccgo.hall.HallException;
import com.gempukku.swccgo.hall.HallServer;
import com.gempukku.swccgo.league.LeagueSeriesData;
import com.gempukku.swccgo.league.LeagueService;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.vo.SwccgDeck;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class HallRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    private CollectionsManager _collectionManager;
    private SwccgoFormatLibrary _formatLibrary;
    private HallServer _hallServer;
    private LeagueService _leagueService;
    private SwccgCardBlueprintLibrary _library;
    private LongPollingSystem _longPollingSystem;
    private SwccgoServer _swccgoServer;

    public HallRequestHandler(Map<Type, Object> context, LongPollingSystem longPollingSystem) {
        super(context);
        _collectionManager = extractObject(context, CollectionsManager.class);
        _formatLibrary = extractObject(context, SwccgoFormatLibrary.class);
        _hallServer = extractObject(context, HallServer.class);
        _leagueService = extractObject(context, LeagueService.class);
        _library = extractObject(context, SwccgCardBlueprintLibrary.class);
        _swccgoServer = extractObject(context, SwccgoServer.class);
        _longPollingSystem = longPollingSystem;
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, String remoteIp) throws Exception {
        if ("".equals(uri) && request.method() == HttpMethod.GET) {
            getHall(request, responseWriter);
        } else if ("".equals(uri) && request.method() == HttpMethod.POST) {
            createTable(request, responseWriter);
        } else if (uri.equals("/update") && request.method() == HttpMethod.POST) {
            updateHall(request, responseWriter);
        } else if (uri.startsWith("/draft/") && uri.endsWith("/update") && request.method() == HttpMethod.POST) {
            updateDraft(request, uri.substring(7, uri.length() - 7), responseWriter);
        } else if (uri.startsWith("/draft/") && uri.endsWith("/pick") && request.method() == HttpMethod.POST) {
            draftPick(request, uri.substring(7, uri.length() - 5), responseWriter);
        } else if (uri.startsWith("/draft/") && request.method() == HttpMethod.GET) {
            getDraft(request, uri.substring(7), responseWriter);
        } else if (uri.equals("/formats/html") && request.method() == HttpMethod.GET) {
            getFormats(request, responseWriter);
        } else if (uri.startsWith("/format/") && request.method() == HttpMethod.GET) {
            getFormat(request, uri.substring(8), responseWriter);
        } else if (uri.startsWith("/queue/") && request.method() == HttpMethod.POST) {
            if (uri.endsWith("/leave")) {
                leaveQueue(request, uri.substring(7, uri.length() - 6), responseWriter);
            } else {
                joinQueue(request, uri.substring(7), responseWriter);
            }
        } else if (uri.startsWith("/tournament/") && uri.endsWith("/deck") && request.method() == HttpMethod.POST) {
            submitTournamentDeck(request, uri.substring(12, uri.length() - 5), responseWriter);
        } else if (uri.startsWith("/tournament/") && uri.endsWith("/leave") && request.method() == HttpMethod.POST) {
            dropFromTournament(request, uri.substring(12, uri.length() - 6), responseWriter);
        } else if (uri.startsWith("/") && uri.endsWith("/leave") && request.method() == HttpMethod.POST) {
            leaveTable(request, uri.substring(1, uri.length() - 6), responseWriter);
        } else if (uri.startsWith("/") && request.method() == HttpMethod.POST) {
            joinTable(request, uri.substring(1), responseWriter);
        } else {
            responseWriter.writeError(404);
        }
    }

    private void submitTournamentDeck(HttpRequest request, String tournamentId, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
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
        finally {
            postDecoder.destroy();
        }
    }

    private void draftPick(HttpRequest request, String tournamentId, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
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
        finally {
            postDecoder.destroy();
        }
    }

    private void updateDraft(HttpRequest request, String tournamentId, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
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
        finally {
            postDecoder.destroy();
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
        try {
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
        finally {
            postDecoder.destroy();
        }
    }

    private void joinTable(HttpRequest request, String tableId, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
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
        finally {
            postDecoder.destroy();
        }
    }

    private void leaveTable(HttpRequest request, String tableId, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            String participantId = getFormParameterSafely(postDecoder, "participantId");
            Player resourceOwner = getResourceOwnerSafely(request, participantId);

            _hallServer.leaveAwaitingTable(resourceOwner, tableId);
            responseWriter.writeXmlResponse(null);
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void createTable(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            String participantId = getFormParameterSafely(postDecoder, "participantId");
            String format = getFormParameterSafely(postDecoder, "format");
            String deckName = getFormParameterSafely(postDecoder, "deckName");
            String sampleDeckVal = getFormParameterSafely(postDecoder, "sampleDeck");
            boolean sampleDeck = (sampleDeckVal != null ? Boolean.valueOf(sampleDeckVal) : false);
            String isPrivateVal = getFormParameterSafely(postDecoder, "isPrivate");
            boolean isPrivate = (isPrivateVal != null ? Boolean.valueOf(isPrivateVal) : false);
            boolean playVsAi = Boolean.parseBoolean(getFormParameterSafely(postDecoder, "playVsAi"));
            String aiSkill = getFormParameterSafely(postDecoder, "aiSkill");
            String aiDeckName = getFormParameterSafely(postDecoder, "aiDeckName");
            String aiDeckSampleVal = getFormParameterSafely(postDecoder, "aiDeckSample");
            boolean aiDeckSample = (aiDeckSampleVal == null || aiDeckSampleVal.isEmpty())
                    ? true
                    : Boolean.valueOf(aiDeckSampleVal);

            //if they tried creating a private game while they are disabled, let them know instead of creating the table
            if(isPrivate&&!_hallServer.privateGamesAllowed()) {
                    responseWriter.writeXmlResponse(marshalException(new HallException("Private games are currently disabled")));
                    return;
            }

            String tableDesc = getFormParameterSafely(postDecoder, "tableDesc");

            //if the private games doesn't have anything in the description they can't create the game
            if(isPrivate&&tableDesc.length()==0) {
                responseWriter.writeXmlResponse(marshalException(new HallException("Private games must have your intended opponent in the description")));
                return;
            }

            Player resourceOwner = getResourceOwnerSafely(request, participantId);

            // Librarian is needed for sample decks AND for AI decks (AI decks come from librarian)
            Player librarian = (sampleDeck || playVsAi) ? getLibrarian() : null;
            
            try {
                _hallServer.createNewTable(format, resourceOwner, deckName, sampleDeck, tableDesc, isPrivate, librarian, playVsAi, aiSkill, aiDeckName, aiDeckSample);
                responseWriter.writeXmlResponse(null);
            } catch (HallException e) {
                responseWriter.writeXmlResponse(marshalException(e));
            }
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void dropFromTournament(HttpRequest request, String tournamentId, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            String participantId = getFormParameterSafely(postDecoder, "participantId");
            Player resourceOwner = getResourceOwnerSafely(request, participantId);

            _hallServer.dropFromTournament(tournamentId, resourceOwner);

            responseWriter.writeXmlResponse(null);
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void joinQueue(HttpRequest request, String queueId, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
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
        finally {
            postDecoder.destroy();
        }
    }

    private void leaveQueue(HttpRequest request, String queueId, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            String participantId = getFormParameterSafely(postDecoder, "participantId");

            Player resourceOwner = getResourceOwnerSafely(request, participantId);

            _hallServer.leaveQueue(queueId, resourceOwner);

            responseWriter.writeXmlResponse(null);
        }
        finally {
            postDecoder.destroy();
        }
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


    /*
     * Build the format HTML used by the getFormat and getFormats functions.
     */
    private StringBuilder buildFormatHtml(SwccgFormat swccgFormat) {

        StringBuilder result = new StringBuilder();

        List<Integer> knownSets = new ArrayList();
        List<Integer> validSets = new ArrayList();
        validSets = swccgFormat.getValidSets();

        for(ExpansionSet set: ExpansionSet.values()) {
            if (set.getSetNumber() < 400) { //excludes Dream Cards, Playtesting, Legacy, etc.
                //result.append("<p />Known Set:"+set.getSetNumber()+set.getHumanReadable());
                knownSets.add(set.getSetNumber());
            } // if
        } // for
        //for(Integer setId : swccgFormat.getValidSets()) {
        //}

        /*
         * The CSS ID can not have special characters like ! in it or else the selector will not work.
         */
        String formatCssId = swccgFormat.getName().replace(" ", "-").replace("(", "").replace(")", "").replace("/", "").replace("'", "").replace("!", "").replace(",", "");

        //result.append("<p /><strong>.knownSets</strong>:"+knownSets.getClass().getName()+"<pre style=\"border:2px red solid;padding:5px; margin:5px;\">");
        //result.append(knownSets);
        //result.append("</pre><p /><strong>validSets1</strong>:"+swccgFormat.getValidSets().getClass().getName());
        //result.append("<p /><strong>validSets2</strong>:"+validSets.getClass().getName()+"<pre style=\"border:2px red solid;padding:5px; margin:5px;\">");
        //result.append(validSets);
        //result.append("</pre>");


        result.append("<div id=\""+formatCssId+"\" class=\"format-name\"onclick=\"showFormat(this);\"><span class=\"format-name-label\" onclick=\"toggleLabelArrow(this);\">" + swccgFormat.getName() + "</span></div>");
        result.append("<div id=\""+formatCssId+"-content\" class=\"format-details\" style=\"display:none;\">");
        result.append("<ul id=\""+formatCssId+"-details\" class=\"format-details\">");

        Integer deckSize = swccgFormat.getRequiredDeckSize();
        result.append("<li class=\"format-detail\"><span id=\""+formatCssId+"-format-deck-size\">Deck size:</span> <span id=\""+formatCssId+"-format-deck-size-content\" class=\"format-deck-size\">" + deckSize +"</span></li>");

        Integer defaultGameTimerMinutes = swccgFormat.getDefaultGameTimerMinutes();
        result.append("<li class=\"format-detail\"><span id=\""+formatCssId+"-format-default-game-timer-minutes\">Default Game Timer Minutes:</span> <span id=\""+formatCssId+"-format-default-game-timer-minutes-content\" class=\"format-default-game-timer-minutes\">" + defaultGameTimerMinutes +"</span></li>");

        result.append("<li class=\"format-detail\"><span id=\""+formatCssId+"-format-valid-sets\" class=\"format-valid-sets-label\" onclick=\"showFormat(this);\">Valid sets:</span> ");

        if (validSets.equals(knownSets)) {
            result.append("<span class=\"all-sets-valid\">All sets valid.</span>");
        } else {
            result.append("<span style=\"color:yellow;\">SETS ARE LIMITED</span>");
        }

        result.append("<span id=\""+formatCssId+"-format-valid-sets-icons\" class=\"format-set-icons\">");

        StringBuilder setIcons   = new StringBuilder();
        StringBuilder setIconsLi = new StringBuilder();

        for (Integer setId : swccgFormat.getValidSets()) {
            ExpansionSet expansionSet = ExpansionSet.getSetFromNumber(setId);
            if (expansionSet != null) {
                String setName = expansionSet.getHumanReadable();
                setIconsLi.append("<li>"+setName+"</li>");
                if      (setId ==  2) { setIcons.append("<img alt=\""+setName+"\" name=\""+setName+"\" src=\"https://res.starwarsccg.org/rules/anewhope.gif\" />"); }
                else if (setId ==  3) { setIcons.append("<img alt=\""+setName+"\" name=\""+setName+"\" src=\"https://res.starwarsccg.org/rules/hoth.gif\" />"); }
                else if (setId ==  4) { setIcons.append("<img alt=\""+setName+"\" name=\""+setName+"\" src=\"https://res.starwarsccg.org/rules/dagobah.gif\" />"); }
                else if (setId ==  5) { setIcons.append("<img alt=\""+setName+"\" name=\""+setName+"\" src=\"https://res.starwarsccg.org/rules/cloudcity.gif\" />"); }
                else if (setId ==  6) { setIcons.append("<img alt=\""+setName+"\" name=\""+setName+"\" src=\"https://res.starwarsccg.org/rules/jabbaspalace.gif\" />"); }
                else if (setId ==  8) { setIcons.append("<img alt=\""+setName+"\" name=\""+setName+"\" src=\"https://res.starwarsccg.org/rules/endor.gif\" />"); }
                else if (setId ==  9) { setIcons.append("<img alt=\""+setName+"\" name=\""+setName+"\" src=\"https://res.starwarsccg.org/rules/deathstarii.gif\" />"); }
                else if (setId == 10) { setIcons.append("<img alt=\""+setName+"\" name=\""+setName+"\" src=\"https://res.starwarsccg.org/rules/reflectionsii.gif\" />"); }
                else if (setId == 11) { setIcons.append("<img alt=\""+setName+"\" name=\""+setName+"\" src=\"https://res.starwarsccg.org/rules/tatooine.gif\" />"); }
                else if (setId == 12) { setIcons.append("<img alt=\""+setName+"\" name=\""+setName+"\" src=\"https://res.starwarsccg.org/rules/coruscant.gif\" />"); }
                else if (setId == 13) { setIcons.append("<img alt=\""+setName+"\" name=\""+setName+"\" src=\"https://res.starwarsccg.org/rules/reflectionsiii.gif\" />"); }
                else if (setId == 14) { setIcons.append("<img alt=\""+setName+"\" name=\""+setName+"\" src=\"https://res.starwarsccg.org/rules/theedpalace.gif\" />"); }
                else                  { setIcons.append("<img alt=\""+setName+"\" name=\""+setName+"\" src=\"https://res.starwarsccg.org/rules/premium.gif\" />"); }
            }
        }
        result.append(setIcons.toString());
        if (validSets.equals(knownSets)) {
            result.append("</span> <ul id=\""+formatCssId+"-format-valid-sets-content\" style=\"display:none;\">");
        } else {
            result.append("</span> <ul id=\""+formatCssId+"-format-valid-sets-content\">");
        }
        result.append(setIconsLi.toString());
        result.append("</ul></li>"); /* valid sets */

        if (!swccgFormat.getBannedIcons().isEmpty()) {
            /* icons not allowed are displayed by default. To hide them by default, add display:none to the ul */
            result.append("<li class=\"format-detail\"><span id=\""+formatCssId+"-format-icons-not-allowed\" class=\"format-icons-not-allowed-label\" onclick=\"showFormat(this);\">Icons not allowed:</span> <ul id=\""+formatCssId+"-format-icons-not-allowed-content\">");
            for (String iconName : swccgFormat.getBannedIcons()) {
                Icon icon = Icon.getIconFromName(iconName);
                if (icon != null) {
                    result.append("<li class=\"format-details-set-icon\">" + icon.getHumanReadable() + "</li>");
                }
            }
            result.append("</ul></li>"); /* icons not allowed */
        }

        if (!swccgFormat.getBannedRarities().isEmpty()) {
            result.append("<li class=\"format-detail\"><span id=\""+formatCssId+"-format-rarities-not-allowed\" class=\"format-rarities-not-allowed-label\" onclick=\"showFormat(this);\">Rarities not allowed:</span> <ul id=\""+formatCssId+"-format-rarities-not-allowed-content\">");
            for (String bannedRarity : swccgFormat.getBannedRarities()) {
                Rarity rarity = Rarity.getRarityFromString(bannedRarity);
                if (rarity != null) {
                    result.append("<li>"+rarity.getHumanReadable() + "</li>");
                }
            }
            result.append("</ul></li>"); /* rarities */
        }

        /* Provide a link to the tenets for this format. */
        if (swccgFormat.getTenetsLink() != null) {
            result.append("<li class=\"format-detail\"><span id=\""+formatCssId+"-format-x-listed\" class=\"format-x-listed-label\" >Get the Tenets for this format from:</span> <a target='_new' href='");
            result.append(swccgFormat.getTenetsLink()).append("'>");
            result.append(swccgFormat.getTenetsLink());
            result.append("</a></li>");
        }


        if (!swccgFormat.getBannedCards().isEmpty()) {
            /*
             * List all the banned cards.
             */
            result.append("<li class=\"format-detail\"><span id=\""+formatCssId+"-format-x-listed\" class=\"format-x-listed-label\" onclick=\"showFormat(this);\">Banned cards:</span> <ul id=\""+formatCssId+"-format-x-listed-content\">");
            /*
             * blueprintId is a gemp_id, such as 7_299.
             */
            for (String blueprintId : swccgFormat.getBannedCards()) {
                SwccgCardBlueprint blueprint = _library.getSwccgoCardBlueprint(blueprintId);
                if(blueprint != null) {
                    SwccgCardBlueprint backSideBlueprint = _library.getSwccgoCardBlueprintBack(blueprintId);
                    result.append("<li name=\""+blueprintId+"\">"+GameUtils.getCardLink(blueprintId, blueprint, backSideBlueprint)+"</li>");
                }
            }
            result.append("</ul></li>");
        }
        if (!swccgFormat.getRestrictedCards().isEmpty()) {
            result.append("<li class=\"format-detail\"><span id=\""+formatCssId+"-format-r-listed\" class=\"format-r-listed-label\" onclick=\"showFormat(this);\">Restricted:</span> <ul id=\""+formatCssId+"-format-r-listed-content\" style=\"display:none;\">");
            for (String blueprintId : swccgFormat.getRestrictedCards()) {
                SwccgCardBlueprint blueprint = _library.getSwccgoCardBlueprint(blueprintId);
                SwccgCardBlueprint backSideBlueprint = _library.getSwccgoCardBlueprintBack(blueprintId);
                result.append("<li>"+GameUtils.getCardLink(blueprintId, blueprint, backSideBlueprint)+"</li>");
            }
            result.append("</ul></li>"); /* restricted cards */
        }
        if (!swccgFormat.getValidCards().isEmpty()) {
            result.append("<li class=\"format-detail\"><span id=\""+formatCssId+"-format-additional-valid\" class=\"format-additional-valid-label\"  onclick=\"showFormat(this);\">Additional valid:</span> <ul id=\""+formatCssId+"-format-additional-valid-content\" style=\"display:none;\">");
            for (String blueprintId : swccgFormat.getValidCards()) {
                SwccgCardBlueprint blueprint = _library.getSwccgoCardBlueprint(blueprintId);
                SwccgCardBlueprint backSideBlueprint = _library.getSwccgoCardBlueprintBack(blueprintId);
                result.append("<li>"+GameUtils.getCardLink(blueprintId, blueprint, backSideBlueprint)+"</li>");
            }
            result.append("</ul></li>"); /* additional valid cards */
        }
        if (swccgFormat.hasDownloadBattlegroundRule()) {
            result.append("<li class=\"format-detail\">Once per game, may ▼ a unique battleground not already on table</li>");
        }
        if (swccgFormat.hasJpSealedRule()) {
            result.append("<li class=\"format-detail\">Any character of ability 1 who has a printed deploy number of 3 or greater is considered, for all purposes, to be ability 2</li>");
        }
        result.append("</ul><!-- "+formatCssId+"-content -->");
        result.append("</div><!-- "+formatCssId+"-details -->");

        return result;

    } /* buildFormatHtml */


    /*
     * Displays a single format rule.
     * Content-wise, is VERY similar to getFormats.
     * But only displays a SINGLE format instead of ALL the formats.
     */
    private void getFormat(HttpRequest request, String format, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.uri());
        String participantId = getQueryParameterSafely(queryDecoder, "participantId");
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        StringBuilder result = new StringBuilder();
        SwccgFormat swccgFormat = _formatLibrary.getFormat(format);

        // Only show playtesting formats if player is a playtester or admin
        if (!swccgFormat.isPlaytesting()
                || resourceOwner.hasType(Player.Type.ADMIN)
                || resourceOwner.hasType(Player.Type.PLAYTESTER)) {

            /*
             * Only one format is displayed, so just set result to that value.
             */
            result = buildFormatHtml(swccgFormat);

        } /* if not playtesting format */

        responseWriter.writeHtmlResponse(result.toString());
    }

    /*
     * Builds the content on the "Format Rules" page.
     */
    private void getFormats(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.uri());
        String participantId = getQueryParameterSafely(queryDecoder, "participantId");
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        StringBuilder result = new StringBuilder();
        /*
         * Loop through all the formats.
         * Build a page that lists ALL the formats on it.
         */
        for (SwccgFormat swccgFormat : _formatLibrary.getHallFormats().values()) {
            // Only show playtesting formats if player is a playtester or admin
            if (swccgFormat.isPlaytesting()
                    && !(resourceOwner.hasType(Player.Type.ADMIN)
                    || resourceOwner.hasType(Player.Type.PLAYTESTER))) {
                continue;
            }
            /*
             * There will be multiple formats displayed.
             * Ensure that the formats are additive so that ALL the formats are listed.
             */
            result.append(buildFormatHtml(swccgFormat).toString());
        }

        responseWriter.writeHtmlResponse(result.toString());
    }

    private void getHall(HttpRequest request, ResponseWriter responseWriter) {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.uri());

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
                        || resourceOwner.hasType(Player.Type.PLAYTESTER))) {
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

            hall.setAttribute("privateGamesEnabledBoolean", String.valueOf(_hallServer.privateGamesAllowed()));
            hall.setAttribute("aiTablesEnabledBoolean", String.valueOf(_hallServer.aiTablesEnabled()));
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
        try {
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
        finally {
            postDecoder.destroy();
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

                    hall.setAttribute("privateGamesEnabledBoolean", String.valueOf(_hallServer.privateGamesAllowed()));
                    hall.setAttribute("aiTablesEnabledBoolean", String.valueOf(_hallServer.aiTablesEnabled()));
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
