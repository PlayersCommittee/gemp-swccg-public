package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.collection.TransferDAO;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.Player;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Type;
import java.util.Map;

public class DeliveryRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    private TransferDAO _transferDAO;

    public DeliveryRequestHandler(Map<Type, Object> context) {
        super(context);
        _transferDAO = extractObject(context, TransferDAO.class);
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, MessageEvent e) throws Exception {
        if ("".equals(uri) && request.getMethod() == HttpMethod.GET) {
            getDelivery(request, responseWriter);
        } else {
            responseWriter.writeError(404);
        }
    }

    private void getDelivery(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        Player resourceOwner = getResourceOwnerSafely(request, null);
        Map<String, ? extends CardCollection> delivery = _transferDAO.consumeUndeliveredPackages(resourceOwner.getName());
        if (delivery == null)
            throw new HttpProcessingException(404);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document doc = documentBuilder.newDocument();

        Element deliveryElem = doc.createElement("delivery");
        for (Map.Entry<String, ? extends CardCollection> collectionTypeItems : delivery.entrySet()) {
            String collectionType = collectionTypeItems.getKey();
            CardCollection items = collectionTypeItems.getValue();

            if (items.getAll().size() > 0) {
                Element collectionTypeElem = doc.createElement("collectionType");
                collectionTypeElem.setAttribute("name", collectionType);
                for (CardCollection.Item item : items.getAll().values()) {
                    String blueprintId = item.getBlueprintId();
                    if (item.getType() == CardCollection.Item.Type.CARD) {
                        Element card = doc.createElement("card");
                        card.setAttribute("count", String.valueOf(item.getCount()));
                        card.setAttribute("blueprintId", blueprintId);
                        collectionTypeElem.appendChild(card);
                    } else {
                        Element pack = doc.createElement("pack");
                        pack.setAttribute("count", String.valueOf(item.getCount()));
                        pack.setAttribute("blueprintId", blueprintId);
                        collectionTypeElem.appendChild(pack);
                    }
                }
                deliveryElem.appendChild(collectionTypeElem);
            }
        }

        doc.appendChild(deliveryElem);

        responseWriter.writeXmlResponse(doc);
    }
}
