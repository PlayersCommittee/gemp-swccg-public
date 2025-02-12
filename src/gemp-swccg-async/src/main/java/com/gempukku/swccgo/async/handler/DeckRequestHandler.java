package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.apache.commons.text.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.lang.reflect.Type;
import java.util.*;
import java.io.StringWriter;

public class DeckRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    private SortAndFilterCards _sortAndFilterCards;
    private SwccgCardBlueprintLibrary _library;
    private SwccgoFormatLibrary _formatLibrary;
    private SwccgoServer _swccgoServer;

    public DeckRequestHandler(Map<Type, Object> context) {
        super(context);
        _sortAndFilterCards = new SortAndFilterCards();
        _library = extractObject(context, SwccgCardBlueprintLibrary.class);
        _formatLibrary = extractObject(context, SwccgoFormatLibrary.class);
        _swccgoServer = extractObject(context, SwccgoServer.class);
    }

    /*
     * These are all calls to /gemp-swccg-server/deck/{} where {} is the uri below
     */
    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, String remoteIp) throws Exception {
        /*
         * GET /gemp-swccg-server/deck/list
         */
        if (uri.equals("/list") && request.method() == HttpMethod.GET) {
            listDecks(request, responseWriter);
        /*
         * GET /gemp-swccg-server/deck/libraryList
         */
        } else if (uri.equals("/libraryList") && request.method() == HttpMethod.GET) {
            listLibraryDecks(request, responseWriter);
        /*
         * GET /gemp-swccg-server/deck
         * returns an XML format deck list.
         */
        } else if (uri.equals("") && request.method() == HttpMethod.GET) {
            getDeck(request, responseWriter);
        /*
         * GET /gemp-swccg-server/deck/library
         */
        } else if (uri.equals("/library") && request.method() == HttpMethod.GET) {
            getLibraryDeck(request, responseWriter);
        /*
         * POST /gemp-swccg-server/deck
         */
        } else if (uri.equals("") && request.method() == HttpMethod.POST) {
            saveDeck(request, responseWriter);
        /*
         * GET /gemp-swccg-server/deck/html
         * returns an HTML format deck list.
         */
        } else if (uri.equals("/html") && request.method() == HttpMethod.GET) {
            getDeckInHtml(request, responseWriter);
        /*
         * GET /gemp-swccg-server/deck/libraryHtml
         */
        } else if (uri.equals("/libraryHtml") && request.method() == HttpMethod.GET) {
            getLibraryDeckInHtml(request, responseWriter);
        /*
         * GET /gemp-swccg-server/deck/rename
         */
        } else if (uri.equals("/rename") && request.method() == HttpMethod.POST) {
            renameDeck(request, responseWriter);
        /*
         * GET /gemp-swccg-server/deck/delete
         */
        } else if (uri.equals("/delete") && request.method() == HttpMethod.POST) {
            deleteDeck(request, responseWriter);
        /*
         * GET /gemp-swccg-server/deck/stats
         */
        } else if (uri.equals("/stats") && request.method() == HttpMethod.POST) {
            getDeckStats(request, responseWriter);
        /*
         * The requested URL and METHOD are unknown, return access denied.
         */
        } else {
            responseWriter.writeError(404);
        }
    }

    /*
     * Returns data used on the Deck Validation in the Deck Builder.
     */
    private void getDeckStats(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            String participantId = getFormParameterSafely(postDecoder, "participantId");
            String contents = getFormParameterSafely(postDecoder, "deckContents");
            
            Player resourceOwner = getResourceOwnerSafely(request, participantId);

            SwccgDeck deck = _swccgoServer.createDeckWithValidate("tempDeck", contents);
            if (deck == null)
                throw new HttpProcessingException(400);

            int lightCount = 0;
            int darkCount = 0;
            for (String card : deck.getCards()) {
                SwccgCardBlueprint bp = _library.getSwccgoCardBlueprint(card);
                if(bp == null)
                    continue;

                Side side = bp.getSide();
                if (side == Side.DARK)
                    darkCount++;
                else if (side == Side.LIGHT)
                    lightCount++;
            }

            String lightDarkCountBonusCss = "";
            if ((lightCount > 0) && (darkCount > 0)) {
                lightDarkCountBonusCss = "deckstats-card-count-light-and-dark-set";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("<div id=\"deckstats-card-count-container\">");
            sb.append("<div id=\"deckstats-light-container\" class=\""+lightDarkCountBonusCss+"\"><span id=\"deckstats-light\">Light Cards:</span> <span id=\"deckstats-light-content\">" + lightCount + "</span></div>");
            sb.append("<div id=\"deckstats-dark-container\"  class=\""+lightDarkCountBonusCss+"\"><span id=\"deckstats-dark\">Dark Cards:</span> <span id=\"deckstats-dark-content\">" + darkCount + "</span></div>");
            sb.append("</div>");
            /* Clear out the floats. Render all elements after inline */
            sb.append("<div style=\"clear:both; margin-bottom:1em;\"></div>");

            StringBuilder valid = new StringBuilder();
            StringBuilder invalid = new StringBuilder();
            for (SwccgFormat format : _formatLibrary.getAllFormats().values()) {
                if (!format.isPlaytesting() || resourceOwner.hasType(Player.Type.ADMIN) || resourceOwner.hasType(Player.Type.PLAYTESTER)) {
                    String formatCssId = format.getName().replace(" ", "-").replace("(", "").replace(")", "").replace("/", "").replace("'", "");
                    try {
                        format.validateDeck(deck);
                        valid.append("<div id=\"deckstats-format-" + formatCssId + "-container\" class=\"deckstats-format-container\"></span><span id=\"deckstats-format-" + formatCssId + "\" class=\"deckstats-format-name\">" + format.getName() + ":</span> <span id=\"deckstats-format-" + formatCssId + "-content\" class=\"deckstats-format-valid\">valid</span></div>");
                    } catch (DeckInvalidException exp) {
                        invalid.append("<div id=\"deckstats-format-" + formatCssId + "-container\" class=\"deckstats-format-container\"></span><span id=\"deckstats-format-" + formatCssId + "\" class=\"deckstats-format-name\">" + format.getName() + ":</span> <span id=\"deckstats-format-" + formatCssId + "-content\" class=\"deckstats-format-invalid\">" + exp.getMessage() + "</span></div>");
                    }
                }
            }
            sb.append(valid);
            sb.append(invalid);

            responseWriter.writeHtmlResponse(sb.toString());
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void deleteDeck(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            String participantId = getFormParameterSafely(postDecoder, "participantId");
            String deckName = getFormParameterSafely(postDecoder, "deckName");
            Player resourceOwner = getResourceOwnerSafely(request, participantId);

            _deckDao.deleteDeckForPlayer(resourceOwner, deckName);

            responseWriter.writeXmlResponse(null);
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void renameDeck(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            String participantId = getFormParameterSafely(postDecoder, "participantId");
            String deckName = getFormParameterSafely(postDecoder, "deckName");
            String oldDeckName = getFormParameterSafely(postDecoder, "oldDeckName");

            Player resourceOwner = getResourceOwnerSafely(request, participantId);

            SwccgDeck deck = _deckDao.renameDeck(resourceOwner, oldDeckName, deckName);
            if (deck == null)
                throw new HttpProcessingException(404);

            responseWriter.writeXmlResponse(serializeDeck(deck));
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void saveDeck(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            String participantId = getFormParameterSafely(postDecoder, "participantId");
            String deckName = getFormParameterSafely(postDecoder, "deckName");
            String contents = getFormParameterSafely(postDecoder, "deckContents");

            Player resourceOwner = getResourceOwnerSafely(request, participantId);

            SwccgDeck swccgDeck = _swccgoServer.createDeckWithValidate(deckName, contents);
            if (swccgDeck == null)
                throw new HttpProcessingException(400);

            _deckDao.saveDeckForPlayer(resourceOwner, deckName, swccgDeck);

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document doc = documentBuilder.newDocument();
            Element deckElem = doc.createElement("ok");
            doc.appendChild(deckElem);

            responseWriter.writeXmlResponse(doc);
        }
        finally {
            postDecoder.destroy();
        }
    }

    private void getDeckInHtml(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.uri());
        String participantId = getQueryParameterSafely(queryDecoder, "participantId");
        String deckName = getQueryParameterSafely(queryDecoder, "deckName");
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        SwccgDeck deck = _deckDao.getDeckForPlayer(resourceOwner, deckName);

        if (deck == null)
            throw new HttpProcessingException(404);

        StringBuilder result = new StringBuilder();
        result.append("<html><body>");
        result.append("<h1>" + StringEscapeUtils.escapeHtml3(deck.getDeckName()) + "</h1>");

        DefaultCardCollection deckCards = new DefaultCardCollection();
        for (String card : deck.getCards())
            deckCards.addItem(_library.getBaseBlueprintId(card), 1);

        result.append("<br/>");
        result.append("<b>Deck:</b>");
        String category = "";
        for (CardCollection.Item item : _sortAndFilterCards.process("sort:cardCategory,name", deckCards.getAll().values(), _library, _formatLibrary, null)) {
            if (!_library.getSwccgoCardBlueprint(item.getBlueprintId()).getCardCategory().getHumanReadable().equals(category)) {
                category = _library.getSwccgoCardBlueprint(item.getBlueprintId()).getCardCategory().getHumanReadable();
                result.append("<br/>").append(category.toUpperCase()).append("<br/>");
            }

            result.append(item.getCount() + "x " + GameUtils.getFullName(_library.getSwccgoCardBlueprint(item.getBlueprintId())) + "<br/>");
        }

        result.append("</body></html>");

        responseWriter.writeHtmlResponse(result.toString());
    }


    /**
     * Converts the given XML document into a pleasantly formatted string
     * @param doc   XML Document
     * @return string
     */
    private String documentToString(Document doc) {

        String outputString = "";
        try {
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            outputString = sw.toString();

            outputString = outputString.replaceAll("\n", "\r\n");
        } catch (Exception ex) {
            outputString = "Error converting to string";
        }
        return outputString;
    }


    private void getDeck(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.uri());
        String participantId = getQueryParameterSafely(queryDecoder, "participantId");
        String deckName = getQueryParameterSafely(queryDecoder, "deckName");

        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        SwccgDeck deck = _deckDao.getDeckForPlayer(resourceOwner, deckName);

        if (deck == null) {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = documentBuilder.newDocument();
            Element deckElem = doc.createElement("deck");
            doc.appendChild(deckElem);

            responseWriter.writeXmlResponse(doc);
        } else {

            Document serializedDeck = serializeDeck(deck);
            /*
             * "pretty" output is used by deck downloads in the deck builder.
             * /gemp-swccg-server/deck?deckName=X&cacheBreaker=968804&pretty=true
             */
            String pretty = getQueryParameterSafely(queryDecoder, "pretty");
            if (pretty != null) {
                String documentAsString = documentToString(serializedDeck);
                responseWriter.writeHtmlResponse(documentAsString);
            } else {
                responseWriter.writeXmlResponse(serializedDeck);
            }

        }
    }

    private void listDecks(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.uri());
        String participantId = getQueryParameterSafely(queryDecoder, "participantId");
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        List<String> darkDeckNames = new ArrayList<String>();
        List<String> lightDeckNames = new ArrayList<String>();
        List<String> otherDeckNames = new ArrayList<String>();

        List<String> deckNames = new ArrayList<String>(_deckDao.getPlayerDeckNames(resourceOwner));

        // For each deck determine if it is a Dark deck, Light deck, or other deck
        for (String deckName : deckNames) {
            SwccgDeck deck = _deckDao.getDeckForPlayer(resourceOwner, deckName);
            if(deck == null)
                continue;

            Side side = deck.getSide(_library);
            if (side == Side.DARK)
                darkDeckNames.add(deckName);
            else if (side == Side.LIGHT)
                lightDeckNames.add(deckName);
            else
                otherDeckNames.add(deckName);
        }

        // Sort the deck names
        Collections.sort(darkDeckNames);
        Collections.sort(lightDeckNames);
        Collections.sort(otherDeckNames);

        // Build the XML response
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document doc = documentBuilder.newDocument();

        // Dark decks
        Element decksElem = doc.createElement("decks");
        for (String darkDeckName : darkDeckNames) {
            Element deckElem = doc.createElement("darkDeck");
            deckElem.appendChild(doc.createTextNode(darkDeckName));
            decksElem.appendChild(deckElem);
        }
        // Light decks
        for (String lightDeckName : lightDeckNames) {
            Element deckElem = doc.createElement("lightDeck");
            deckElem.appendChild(doc.createTextNode(lightDeckName));
            decksElem.appendChild(deckElem);
        }
        // Other decks
        for (String otherDeckName : otherDeckNames) {
            Element deckElem = doc.createElement("otherDeck");
            deckElem.appendChild(doc.createTextNode(otherDeckName));
            decksElem.appendChild(deckElem);
        }
        doc.appendChild(decksElem);

        // Write the XML response
        responseWriter.writeXmlResponse(doc);
    }

    private void listLibraryDecks(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        List<String> darkDeckNames = new ArrayList<String>();
        List<String> lightDeckNames = new ArrayList<String>();
        List<String> otherDeckNames = new ArrayList<String>();

        Player resourceOwner = getLibrarian();
        List<String> deckNames = new ArrayList<String>(_deckDao.getPlayerDeckNames(resourceOwner));

        // For each deck determine if it is a Dark deck, Light deck, or other deck
        for (String deckName : deckNames) {
            SwccgDeck deck = _deckDao.getDeckForPlayer(resourceOwner, deckName);
            Side side = deck.getSide(_library);
            if (side == Side.DARK)
                darkDeckNames.add(deckName);
            else if (side == Side.LIGHT)
                lightDeckNames.add(deckName);
            else
                otherDeckNames.add(deckName);
        }

        // Sort the deck names
        Collections.sort(darkDeckNames);
        Collections.sort(lightDeckNames);
        Collections.sort(otherDeckNames);

        // Build the XML response
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document doc = documentBuilder.newDocument();

        // Dark decks
        Element decksElem = doc.createElement("decks");
        for (String darkDeckName : darkDeckNames) {
            Element deckElem = doc.createElement("darkDeck");
            deckElem.appendChild(doc.createTextNode(darkDeckName));
            decksElem.appendChild(deckElem);
        }
        // Light decks
        for (String lightDeckName : lightDeckNames) {
            Element deckElem = doc.createElement("lightDeck");
            deckElem.appendChild(doc.createTextNode(lightDeckName));
            decksElem.appendChild(deckElem);
        }
        // Other decks
        for (String otherDeckName : otherDeckNames) {
            Element deckElem = doc.createElement("otherDeck");
            deckElem.appendChild(doc.createTextNode(otherDeckName));
            decksElem.appendChild(deckElem);
        }
        doc.appendChild(decksElem);

        // Write the XML response
        responseWriter.writeXmlResponse(doc);
    }

    private void getLibraryDeckInHtml(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.uri());
        String deckName = getQueryParameterSafely(queryDecoder, "deckName");
        Player resourceOwner = getLibrarian();

        SwccgDeck deck = _deckDao.getDeckForPlayer(resourceOwner, deckName);

        if (deck == null)
            throw new HttpProcessingException(404);

        StringBuilder result = new StringBuilder();
        result.append("<html><body>");
        result.append("<h1>" + StringEscapeUtils.escapeHtml3(deck.getDeckName()) + "</h1>");

        DefaultCardCollection deckCards = new DefaultCardCollection();
        for (String card : deck.getCards())
            deckCards.addItem(_library.getBaseBlueprintId(card), 1);

        result.append("<br/>");
        result.append("<b>Deck:</b><br/>");
        for (CardCollection.Item item : _sortAndFilterCards.process("sort:cardCategory,name", deckCards.getAll().values(), _library, _formatLibrary, null))
            result.append(item.getCount() + "x " + GameUtils.getFullName(_library.getSwccgoCardBlueprint(item.getBlueprintId())) + "<br/>");

        result.append("</body></html>");

        responseWriter.writeHtmlResponse(result.toString());
    }

    private void getLibraryDeck(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.uri());
        String deckName = getQueryParameterSafely(queryDecoder, "deckName");

        Player resourceOwner = getLibrarian();

        SwccgDeck deck = _deckDao.getDeckForPlayer(resourceOwner, deckName);

        if (deck == null) {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = documentBuilder.newDocument();
            Element deckElem = doc.createElement("deck");
            doc.appendChild(deckElem);

            responseWriter.writeXmlResponse(doc);
        } else {
            responseWriter.writeXmlResponse(serializeDeck(deck));
        }
    }

    private Document serializeDeck(SwccgDeck deck) throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document doc = documentBuilder.newDocument();
        Element deckElem = doc.createElement("deck");
        doc.appendChild(deckElem);

        for (CardItem cardItem : _sortAndFilterCards.process("sort:cardCategory,name", createCardItems(deck.getCards()), _library, _formatLibrary, null)) {
            Element card = doc.createElement("card");
            card.setAttribute("blueprintId", cardItem.getBlueprintId());
            SwccgCardBlueprint blueprint = _library.getSwccgoCardBlueprint(cardItem.getBlueprintId());
            if (blueprint != null) {
                if (blueprint.getTitle() != null) {
                    card.setAttribute("title", blueprint.getTitle());
                }
                if (blueprint.getTestingText() != null) {
                    card.setAttribute("testingText", GameUtils.convertTestingText(blueprint.getTestingText()));
                }
                SwccgCardBlueprint backSideBlueprint = _library.getSwccgoCardBlueprintBack(cardItem.getBlueprintId());
                if (backSideBlueprint != null) {
                    if (backSideBlueprint.getTestingText() != null) {
                        card.setAttribute("backSideTestingText", GameUtils.convertTestingText(backSideBlueprint.getTestingText()));
                    }
                }
            }
            deckElem.appendChild(card);
        }
        for (CardItem cardItem : _sortAndFilterCards.process("sort:cardCategory,name", createCardItems(deck.getCardsOutsideDeck()), _library, _formatLibrary, null)) {
            Element cardOutsideDeck = doc.createElement("cardOutsideDeck");
            cardOutsideDeck.setAttribute("blueprintId", cardItem.getBlueprintId());
            SwccgCardBlueprint blueprint = _library.getSwccgoCardBlueprint(cardItem.getBlueprintId());
            if (blueprint != null) {
                if (blueprint.getTitle() != null) {
                    cardOutsideDeck.setAttribute("title", blueprint.getTitle());
                }
                if (blueprint.getTestingText() != null) {
                    cardOutsideDeck.setAttribute("testingText", GameUtils.convertTestingText(blueprint.getTestingText()));
                }
                SwccgCardBlueprint backSideBlueprint = _library.getSwccgoCardBlueprintBack(cardItem.getBlueprintId());
                if (backSideBlueprint != null) {
                    if (backSideBlueprint.getTestingText() != null) {
                        cardOutsideDeck.setAttribute("backSideTestingText", GameUtils.convertTestingText(backSideBlueprint.getTestingText()));
                    }
                }
            }
            deckElem.appendChild(cardOutsideDeck);
        }

        return doc;
    }

    private List<CardItem> createCardItems(List<String> blueprintIds) {
        List<CardItem> cardItems = new LinkedList<CardItem>();
        for (String blueprintId : blueprintIds)
            cardItems.add(new BasicCardItem(blueprintId));

        return cardItems;
    }

    private static class BasicCardItem implements CardItem {
        private String _blueprintId;

        private BasicCardItem(String blueprintId) {
            _blueprintId = blueprintId;
        }

        @Override
        public String getBlueprintId() {
            return _blueprintId;
        }
    }
}
