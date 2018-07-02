package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.apache.commons.lang.StringEscapeUtils;
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
import java.util.*;

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

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, MessageEvent e) throws Exception {
        if (uri.equals("/list") && request.getMethod() == HttpMethod.GET) {
            listDecks(request, responseWriter);
        } else if (uri.equals("/libraryList") && request.getMethod() == HttpMethod.GET) {
            listLibraryDecks(request, responseWriter);
        } else if (uri.equals("") && request.getMethod() == HttpMethod.GET) {
            getDeck(request, responseWriter);
        } else if (uri.equals("/library") && request.getMethod() == HttpMethod.GET) {
            getLibraryDeck(request, responseWriter);
        } else if (uri.equals("") && request.getMethod() == HttpMethod.POST) {
            saveDeck(request, responseWriter);
        } else if (uri.equals("/html") && request.getMethod() == HttpMethod.GET) {
            getDeckInHtml(request, responseWriter);
        } else if (uri.equals("/libraryHtml") && request.getMethod() == HttpMethod.GET) {
            getLibraryDeckInHtml(request, responseWriter);
        } else if (uri.equals("/rename") && request.getMethod() == HttpMethod.POST) {
            renameDeck(request, responseWriter);
        } else if (uri.equals("/delete") && request.getMethod() == HttpMethod.POST) {
            deleteDeck(request, responseWriter);
        } else if (uri.equals("/stats") && request.getMethod() == HttpMethod.POST) {
            getDeckStats(request, responseWriter);
        } else {
            responseWriter.writeError(404);
        }
    }

    private void getDeckStats(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");
        String contents = getFormParameterSafely(postDecoder, "deckContents");
        
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        SwccgDeck deck = _swccgoServer.createDeckWithValidate("tempDeck", contents);
        if (deck == null)
            throw new HttpProcessingException(400);

        int lightCount = 0;
        int darkCount = 0;
        for (String card : deck.getCards()) {
            Side side = _library.getSwccgoCardBlueprint(card).getSide();
            if (side == Side.DARK)
                darkCount++;
            else if (side == Side.LIGHT)
                lightCount++;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<b>Light</b>: " + lightCount + ", <b>Dark</b>: " + darkCount + "<br/>");

        StringBuilder valid = new StringBuilder();
        StringBuilder invalid = new StringBuilder();
        for (SwccgFormat format : _formatLibrary.getAllFormats().values()) {
            try {
                format.validateDeck(deck);
                valid.append("<b>" + format.getName() + "</b>: <font color='green'>valid</font><br/>");
            } catch (DeckInvalidException exp) {
                invalid.append("<b>" + format.getName() + "</b>: <font color='red'>" + exp.getMessage() + "</font><br/>");
            }
        }
        sb.append(valid);
        sb.append(invalid);

        responseWriter.writeHtmlResponse(sb.toString());
    }

    private void deleteDeck(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");
        String deckName = getFormParameterSafely(postDecoder, "deckName");
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        _deckDao.deleteDeckForPlayer(resourceOwner, deckName);

        responseWriter.writeXmlResponse(null);
    }

    private void renameDeck(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");
        String deckName = getFormParameterSafely(postDecoder, "deckName");
        String oldDeckName = getFormParameterSafely(postDecoder, "oldDeckName");

        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        SwccgDeck deck = _deckDao.renameDeck(resourceOwner, oldDeckName, deckName);
        if (deck == null)
            throw new HttpProcessingException(404);

        responseWriter.writeXmlResponse(serializeDeck(deck));
    }

    private void saveDeck(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
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

    private void getDeckInHtml(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.getUri());
        String participantId = getQueryParameterSafely(queryDecoder, "participantId");
        String deckName = getQueryParameterSafely(queryDecoder, "deckName");
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        SwccgDeck deck = _deckDao.getDeckForPlayer(resourceOwner, deckName);

        if (deck == null)
            throw new HttpProcessingException(404);

        StringBuilder result = new StringBuilder();
        result.append("<html><body>");
        result.append("<h1>" + StringEscapeUtils.escapeHtml(deck.getDeckName()) + "</h1>");

        DefaultCardCollection deckCards = new DefaultCardCollection();
        for (String card : deck.getCards())
            deckCards.addItem(_library.getBaseBlueprintId(card), 1);

        result.append("<br/>");
        result.append("<b>Deck:</b><br/>");
        for (CardCollection.Item item : _sortAndFilterCards.process("sort:cardType,name", deckCards.getAll().values(), _library, _formatLibrary, null))
            result.append(item.getCount() + "x " + GameUtils.getFullName(_library.getSwccgoCardBlueprint(item.getBlueprintId())) + "<br/>");

        result.append("</body></html>");

        responseWriter.writeHtmlResponse(result.toString());
    }

    private void getDeck(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.getUri());
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
            responseWriter.writeXmlResponse(serializeDeck(deck));
        }
    }

    private void listDecks(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.getUri());
        String participantId = getQueryParameterSafely(queryDecoder, "participantId");
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        List<String> darkDeckNames = new ArrayList<String>();
        List<String> lightDeckNames = new ArrayList<String>();
        List<String> otherDeckNames = new ArrayList<String>();

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
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.getUri());
        String deckName = getQueryParameterSafely(queryDecoder, "deckName");
        Player resourceOwner = getLibrarian();

        SwccgDeck deck = _deckDao.getDeckForPlayer(resourceOwner, deckName);

        if (deck == null)
            throw new HttpProcessingException(404);

        StringBuilder result = new StringBuilder();
        result.append("<html><body>");
        result.append("<h1>" + StringEscapeUtils.escapeHtml(deck.getDeckName()) + "</h1>");

        DefaultCardCollection deckCards = new DefaultCardCollection();
        for (String card : deck.getCards())
            deckCards.addItem(_library.getBaseBlueprintId(card), 1);

        result.append("<br/>");
        result.append("<b>Deck:</b><br/>");
        for (CardCollection.Item item : _sortAndFilterCards.process("sort:cardType,name", deckCards.getAll().values(), _library, _formatLibrary, null))
            result.append(item.getCount() + "x " + GameUtils.getFullName(_library.getSwccgoCardBlueprint(item.getBlueprintId())) + "<br/>");

        result.append("</body></html>");

        responseWriter.writeHtmlResponse(result.toString());
    }

    private void getLibraryDeck(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.getUri());
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

        for (CardItem cardItem : _sortAndFilterCards.process("sort:cardType,name", createCardItems(deck.getCards()), _library, _formatLibrary, null)) {
            Element card = doc.createElement("card");
            card.setAttribute("blueprintId", cardItem.getBlueprintId());
            SwccgCardBlueprint blueprint = _library.getSwccgoCardBlueprint(cardItem.getBlueprintId());
            if (blueprint != null) {
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
        for (CardItem cardItem : _sortAndFilterCards.process("sort:cardType,name", createCardItems(deck.getCardsOutsideDeck()), _library, _formatLibrary, null)) {
            Element cardOutsideDeck = doc.createElement("cardOutsideDeck");
            cardOutsideDeck.setAttribute("blueprintId", cardItem.getBlueprintId());
            SwccgCardBlueprint blueprint = _library.getSwccgoCardBlueprint(cardItem.getBlueprintId());
            if (blueprint != null) {
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
