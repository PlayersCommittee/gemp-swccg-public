package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.cards.packs.RarityReader;
import com.gempukku.swccgo.cards.packs.SetRarity;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.league.LeagueSeriesData;
import com.gempukku.swccgo.league.LeagueService;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.packagedProduct.PackagedProductStorage;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    private HashMap<String, SetRarity> _rarities;
    private LeagueService _leagueService;
    private CollectionsManager _collectionsManager;
    private PackagedProductStorage _packStorage;
    private SwccgCardBlueprintLibrary _library;
    private SwccgoFormatLibrary _formatLibrary;
    private SortAndFilterCards _sortAndFilterCards;

    public CollectionRequestHandler(Map<Type, Object> context) {
        super(context);
        _rarities = new HashMap<String, SetRarity>();
        RarityReader reader = new RarityReader();

        for (int i = 1; i <= 14; i++) {
            _rarities.put(String.valueOf(i), reader.getSetRarity(String.valueOf(i)));
        }
        for (int i = 101; i <= 112; i++) {
            _rarities.put(String.valueOf(i), reader.getSetRarity(String.valueOf(i)));
        }
        for (int i = 200; i <= 207; i++) {
            _rarities.put(String.valueOf(i), reader.getSetRarity(String.valueOf(i)));
        }
        for (int i = 301; i <= 301; i++) {
            _rarities.put(String.valueOf(i), reader.getSetRarity(String.valueOf(i)));
        }

        _leagueService = extractObject(context, LeagueService.class);
        _collectionsManager = extractObject(context, CollectionsManager.class);
        _packStorage = extractObject(context, PackagedProductStorage.class);
        _library = extractObject(context, SwccgCardBlueprintLibrary.class);
        _formatLibrary = extractObject(context, SwccgoFormatLibrary.class);
        _sortAndFilterCards = new SortAndFilterCards();
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, MessageEvent e) throws Exception {
            if (uri.equals("") && request.getMethod() == HttpMethod.GET) {
                getCollectionTypes(request, responseWriter);
            } else if (uri.startsWith("/") && request.getMethod() == HttpMethod.POST) {
                openPack(request, uri.substring(1), responseWriter);
            } else if (uri.startsWith("/") && request.getMethod() == HttpMethod.GET) {
                getCollection(request, uri.substring(1), responseWriter);
            } else {
                responseWriter.writeError(404);
            }
    }

    private void getCollection(HttpRequest request, String collectionType, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.getUri());
        String participantId = getQueryParameterSafely(queryDecoder, "participantId");
        String filter = getQueryParameterSafely(queryDecoder, "filter");
        int start = Integer.parseInt(getQueryParameterSafely(queryDecoder, "start"));
        int count = Integer.parseInt(getQueryParameterSafely(queryDecoder, "count"));
        
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        CardCollection collection = constructCollection(resourceOwner, collectionType);

        if (collection == null)
            throw new HttpProcessingException(404);

        Collection<CardCollection.Item> items = collection.getAll().values();
        List<CardCollection.Item> filteredResult = _sortAndFilterCards.process(filter, items, _library, _formatLibrary, _rarities);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document doc = documentBuilder.newDocument();

        Element collectionElem = doc.createElement("collection");
        collectionElem.setAttribute("count", String.valueOf(filteredResult.size()));
        doc.appendChild(collectionElem);

        for (int i = start; i < start + count; i++) {
            if (i >= 0 && i < filteredResult.size()) {
                CardCollection.Item item = filteredResult.get(i);
                String blueprintId = item.getBlueprintId();
                if (item.getType() == CardCollection.Item.Type.CARD) {
                    Element card = doc.createElement("card");
                    card.setAttribute("count", String.valueOf(item.getCount()));
                    card.setAttribute("blueprintId", blueprintId);
                    SwccgCardBlueprint blueprint = _library.getSwccgoCardBlueprint(blueprintId);
                    appendCardSide(card, blueprint);
                    appendCardGroup(card, blueprint);
                    appendCardTestingText(card, blueprint);
                    appendCardBackSideTestingText(card, _library.getSwccgoCardBlueprintBack(blueprintId));
                    collectionElem.appendChild(card);
                } else {
                    Element pack = doc.createElement("pack");
                    pack.setAttribute("count", String.valueOf(item.getCount()));
                    pack.setAttribute("blueprintId", blueprintId);
                    if (item.getType() == CardCollection.Item.Type.SELECTION) {
                        List<CardCollection.Item> contents = _packStorage.openPackagedProduct(blueprintId);
                        StringBuilder contentsStr = new StringBuilder();
                        for (CardCollection.Item content : contents)
                            contentsStr.append(content.getBlueprintId()).append("|");
                        contentsStr.delete(contentsStr.length() - 1, contentsStr.length());
                        pack.setAttribute("contents", contentsStr.toString());
                    }
                    collectionElem.appendChild(pack);
                }
            }
        }

        Map<String, String> headers = new HashMap<String, String>();
        processDeliveryServiceNotification(request, headers);
        
        responseWriter.writeXmlResponse(doc, headers);
    }

    private CardCollection constructCollection(Player player, String collectionType) {
        return _collectionsManager.getPlayerCollection(player, collectionType);
    }

    private void openPack(HttpRequest request, String collectionType, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");
        String selection = getFormParameterSafely(postDecoder, "selection");
        String packId = getFormParameterSafely(postDecoder, "pack");

        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        CollectionType collectionTypeObj = createCollectionType(collectionType);
        CardCollection packContents = _collectionsManager.openPackInPlayerCollection(resourceOwner, collectionTypeObj, selection, _packStorage, packId);

        if (packContents == null)
            throw new HttpProcessingException(404);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document doc = documentBuilder.newDocument();

        Element collectionElem = doc.createElement("pack");
        doc.appendChild(collectionElem);

        for (CardCollection.Item item : packContents.getAll().values()) {
            String blueprintId = item.getBlueprintId();
            if (item.getType() == CardCollection.Item.Type.CARD) {
                Element card = doc.createElement("card");
                card.setAttribute("count", String.valueOf(item.getCount()));
                card.setAttribute("blueprintId", blueprintId);
                SwccgCardBlueprint blueprint = _library.getSwccgoCardBlueprint(blueprintId);
                appendCardSide(card, blueprint);
                appendCardTestingText(card, blueprint);
                appendCardBackSideTestingText(card, _library.getSwccgoCardBlueprintBack(blueprintId));
                collectionElem.appendChild(card);
            } else {
                Element pack = doc.createElement("pack");
                pack.setAttribute("count", String.valueOf(item.getCount()));
                pack.setAttribute("blueprintId", blueprintId);
                collectionElem.appendChild(pack);
            }
        }

        responseWriter.writeXmlResponse(doc);
    }

    private void getCollectionTypes(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.getUri());
        String participantId = getQueryParameterSafely(queryDecoder, "participantId");

        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document doc = documentBuilder.newDocument();

        Element collectionsElem = doc.createElement("collections");

        for (League league : _leagueService.getActiveLeagues()) {
            LeagueSeriesData serie = _leagueService.getCurrentLeagueSeries(league);
            if (serie != null && serie.isLimited() && _leagueService.isPlayerInLeague(league, resourceOwner)) {
                CollectionType collectionType = serie.getCollectionType();
                Element collectionElem = doc.createElement("collection");
                collectionElem.setAttribute("type", collectionType.getCode());
                collectionElem.setAttribute("name", collectionType.getFullName());
                collectionsElem.appendChild(collectionElem);
            }
        }

        doc.appendChild(collectionsElem);

        responseWriter.writeXmlResponse(doc);
    }

    private CollectionType createCollectionType(String collectionType) {
        if (collectionType.equals("permanent"))
            return CollectionType.MY_CARDS;

        return _leagueService.getCollectionTypeByCode(collectionType);
    }

    private void appendCardSide(Element card, SwccgCardBlueprint blueprint) {
        Side side = blueprint.getSide();
        if (side != null)
            card.setAttribute("side", side.toString());
    }

    private void appendCardGroup(Element card, SwccgCardBlueprint blueprint) {
        String group = "card";
        // if (blueprint.getCardType() == CardType.OBJECTIVE)
        //    group="objective";
        //
        card.setAttribute("group", group);
    }

    private void appendCardTestingText(Element card, SwccgCardBlueprint blueprint) {
        String testingText = blueprint.getTestingText();
        if (testingText != null) {
            card.setAttribute("testingText", GameUtils.convertTestingText(testingText));
        }
    }

    private void appendCardBackSideTestingText(Element card, SwccgCardBlueprint blueprint) {
        if (blueprint != null) {
            String testingText = blueprint.getTestingText();
            if (testingText != null) {
                card.setAttribute("backSideTestingText", GameUtils.convertTestingText(testingText));
            }
        }
    }
}
