package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.cards.packs.RarityReader;
import com.gempukku.swccgo.cards.packs.SetRarity;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.merchant.MerchantException;
import com.gempukku.swccgo.merchant.MerchantService;
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

public class MerchantRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    private HashMap<String, SetRarity> _rarities;
    private CollectionsManager _collectionsManager;
    private SortAndFilterCards _sortAndFilterCards;
    private MerchantService _merchantService;
    private SwccgCardBlueprintLibrary _library;
    private SwccgoFormatLibrary _formatLibrary;

    public MerchantRequestHandler(Map<Type, Object> context) {
        super(context);

        _collectionsManager = extractObject(context, CollectionsManager.class);
        _sortAndFilterCards = new SortAndFilterCards();
        _merchantService = extractObject(context, MerchantService.class);
        _library = extractObject(context, SwccgCardBlueprintLibrary.class);
        _formatLibrary = extractObject(context, SwccgoFormatLibrary.class);

        _rarities = new HashMap<String, SetRarity>();
        RarityReader reader = new RarityReader();
        for (int i = 1; i <= 14; i++) {
            _rarities.put(String.valueOf(i), reader.getSetRarity(String.valueOf(i)));
        }
        for (int i = 101; i <= 112; i++) {
            _rarities.put(String.valueOf(i), reader.getSetRarity(String.valueOf(i)));
        }
        for (int i = 200; i <= 204; i++) {
            _rarities.put(String.valueOf(i), reader.getSetRarity(String.valueOf(i)));
        }
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, MessageEvent e) throws Exception {
        if ("".equals(uri) && request.getMethod() == HttpMethod.GET) {
            getMerchantOffers(request, responseWriter);
        } else if (uri.equals("/buy") && request.getMethod() == HttpMethod.POST) {
            buy(request, responseWriter);
        } else if (uri.equals("/sell") && request.getMethod() == HttpMethod.POST) {
            sell(request, responseWriter);
        } else if (uri.equals("/sellAll") && request.getMethod() == HttpMethod.POST) {
            sellAll(request, responseWriter);
        } else if (uri.equals("/tradeFoil") && request.getMethod() == HttpMethod.POST) {
            tradeInFoil(request, responseWriter);
        } else {
            responseWriter.writeError(404);
        }
    }

    private void tradeInFoil(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");
        String blueprintId = getFormParameterSafely(postDecoder, "blueprintId");

        Player resourceOwner = getResourceOwnerSafely(request, participantId);
        try {
            _merchantService.tradeForFoil(resourceOwner, blueprintId);
            responseWriter.writeXmlResponse(null);
        } catch (MerchantException exp) {
            responseWriter.writeXmlResponse(marshalException(exp));
        }
    }

    private void sell(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");
        String blueprintId = getFormParameterSafely(postDecoder, "blueprintId");
        int price = Integer.parseInt(getFormParameterSafely(postDecoder, "price"));

        Player resourceOwner = getResourceOwnerSafely(request, participantId);
        try {
            _merchantService.merchantBuysCard(resourceOwner, blueprintId, price);
            responseWriter.writeXmlResponse(null);
        } catch (MerchantException exp) {
            responseWriter.writeXmlResponse(marshalException(exp));
        }
    }

    private void sellAll(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");
        String blueprintId = getFormParameterSafely(postDecoder, "blueprintId");
        int price = Integer.parseInt(getFormParameterSafely(postDecoder, "price"));

        Player resourceOwner = getResourceOwnerSafely(request, participantId);
        try {
            _merchantService.merchantBuysAllOfACard(resourceOwner, blueprintId, price);
            responseWriter.writeXmlResponse(null);
        } catch (MerchantException exp) {
            responseWriter.writeXmlResponse(marshalException(exp));
        }
    }

    private void buy(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");
        String blueprintId = getFormParameterSafely(postDecoder, "blueprintId");
        int price = Integer.parseInt(getFormParameterSafely(postDecoder, "price"));

        Player resourceOwner = getResourceOwnerSafely(request, participantId);
        try {
            _merchantService.merchantSellsCard(resourceOwner, blueprintId, price);
            responseWriter.writeXmlResponse(null);
        } catch (MerchantException exp) {
            responseWriter.writeXmlResponse(marshalException(exp));
        }
    }

    private void getMerchantOffers(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.getUri());
        String participantId = getQueryParameterSafely(queryDecoder, "participantId");
        String filter = getQueryParameterSafely(queryDecoder, "filter");
        int ownedMin = Integer.parseInt(getQueryParameterSafely(queryDecoder, "ownedMin"));
        int start = Integer.parseInt(getQueryParameterSafely(queryDecoder, "start"));
        int count = Integer.parseInt(getQueryParameterSafely(queryDecoder, "count"));

        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        CardCollection collection = _collectionsManager.getPlayerCollection(resourceOwner, "permanent");

        Set<CardItem> cardItems = new HashSet<CardItem>();
        final Collection<CardCollection.Item> allItems = collection.getAll().values();
        for (CardCollection.Item item : allItems) {
            if (item.getCount() >= ownedMin)
                cardItems.add(item);
        }

        if (ownedMin <= 0) {
            Set<CardItem> items = _merchantService.getSellableItems();
            for (CardItem item : items) {
                if (collection.getItemCount(item.getBlueprintId()) == 0)
                    cardItems.add(item);
            }
        }
        List<CardItem> filteredResult = _sortAndFilterCards.process(filter, cardItems, _library, _formatLibrary, _rarities);

        List<CardItem> pageToDisplay = new ArrayList<CardItem>();
        for (int i = start; i < start + count; i++) {
            if (i >= 0 && i < filteredResult.size())
                pageToDisplay.add(filteredResult.get(i));
        }

        MerchantService.PriceGuarantee priceGuarantee = _merchantService.priceCards(resourceOwner, pageToDisplay);
        Map<String, Integer> buyPrices = priceGuarantee.getBuyPrices();
        Map<String, Integer> sellPrices = priceGuarantee.getSellPrices();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document doc = documentBuilder.newDocument();

        Element merchantElem = doc.createElement("merchant");
        int currency = _collectionsManager.getPlayerCollection(resourceOwner, "permanent").getCurrency();
        merchantElem.setAttribute("currency", String.valueOf(currency));
        merchantElem.setAttribute("count", String.valueOf(filteredResult.size()));
        doc.appendChild(merchantElem);

        for (CardItem cardItem : pageToDisplay) {
            String blueprintId = cardItem.getBlueprintId();

            Element elem;
            if (blueprintId.contains("_"))
                elem = doc.createElement("card");
            else
                elem = doc.createElement("pack");

            elem.setAttribute("count", String.valueOf(collection.getItemCount(blueprintId)));
            if (blueprintId.contains("_") && !blueprintId.endsWith("*") && collection.getItemCount(blueprintId) >= 4 && currency >= 1500)
                elem.setAttribute("tradeFoil", "true");
            elem.setAttribute("blueprintId", blueprintId);
            Integer buyPrice = buyPrices.get(blueprintId);
            if (buyPrice != null && collection.getItemCount(blueprintId) > 0)
                elem.setAttribute("buyPrice", buyPrice.toString());
            Integer sellPrice = sellPrices.get(blueprintId);
            if (sellPrice != null)
                elem.setAttribute("sellPrice", sellPrice.toString());
            merchantElem.appendChild(elem);
        }

        responseWriter.writeXmlResponse(doc);
    }

    private Document marshalException(MerchantException e) throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document doc = documentBuilder.newDocument();

        Element error = doc.createElement("error");
        error.setAttribute("message", e.getMessage());
        doc.appendChild(error);
        return doc;
    }

}
