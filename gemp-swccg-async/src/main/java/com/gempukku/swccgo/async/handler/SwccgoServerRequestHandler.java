package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.DateUtils;
import com.gempukku.swccgo.PlayerLock;
import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.collection.TransferDAO;
import com.gempukku.swccgo.db.DeckDAO;
import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.packagedProduct.ProductName;
import com.gempukku.swccgo.service.LoggedUserHolder;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.handler.codec.http.multipart.Attribute;
import org.jboss.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.jboss.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.*;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.COOKIE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;

public class SwccgoServerRequestHandler {
    protected PlayerDAO _playerDao;
    protected LoggedUserHolder _loggedUserHolder;
    private TransferDAO _transferDAO;
    private CollectionsManager _collectionManager;
    protected DeckDAO _deckDao;

    public SwccgoServerRequestHandler(Map<Type, Object> context) {
        _playerDao = extractObject(context, PlayerDAO.class);
        _loggedUserHolder = extractObject(context, LoggedUserHolder.class);
        _transferDAO = extractObject(context, TransferDAO.class);
        _collectionManager = extractObject(context, CollectionsManager.class);
        _deckDao = extractObject(context, DeckDAO.class);
    }

    private boolean isTest() {
        return Boolean.valueOf(System.getProperty("test"));
    }

    protected final void processLoginReward(String loggedUser) throws Exception {
        if (loggedUser != null) {
            Player player = _playerDao.getPlayer(loggedUser);
            synchronized (PlayerLock.getLock(player)) {
                int currentDate = DateUtils.getCurrentDate();
                int latestMonday = DateUtils.getMondayBeforeOrOn(currentDate);

                Integer lastReward = player.getLastLoginReward();
                if (lastReward == null) {
                    if (_playerDao.updateLastReward(player, null, latestMonday)) {

                        // Add initial signup reward Jedi Pack and Rebel Leader pack to collection
                        // _collectionManager.addCurrencyToPlayerCollection(true, "Signup reward", player, CollectionType.MY_CARDS, 2500);
                        _collectionManager.addItemsToPlayerCollection(true, "Free Jedi Pack and Rebel Leader Pack", player, CollectionType.MY_CARDS,
                                Arrays.asList(CardCollection.Item.createItem(ProductName.JEDI_PACK, 1), CardCollection.Item.createItem(ProductName.REBEL_LEADER_PACK, 1)));
                    }
                }
                else {
                    if (latestMonday != lastReward) {
                        if (_playerDao.updateLastReward(player, lastReward, latestMonday)) {
                            _collectionManager.addCurrencyToPlayerCollection(true, "Weekly reward", player, CollectionType.MY_CARDS, 500);
                        }
                    }
                }
            }
        }
    }

    private String getLoggedUser(HttpRequest request) {
        CookieDecoder cookieDecoder = new CookieDecoder();
        String cookieHeader = request.getHeader(COOKIE);
        if (cookieHeader != null) {
            Set<Cookie> cookies = cookieDecoder.decode(cookieHeader);
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("loggedUser")) {
                    String value = cookie.getValue();
                    if (value != null) {
                        return _loggedUserHolder.getLoggedUser(value);
                    }
                }
            }
        }
        return null;
    }

    protected final void processDeliveryServiceNotification(HttpRequest request, Map<String, String> headersToAdd) {
        String logged = getLoggedUser(request);
        if (logged != null && _transferDAO.hasUndeliveredPackages(logged))
            headersToAdd.put("Delivery-Service-Package", "true");
    }

    protected final Player getResourceOwnerSafely(HttpRequest request, String participantId) throws HttpProcessingException {
        String loggedUser = getLoggedUser(request);
        if (isTest() && loggedUser == null)
            loggedUser = participantId;

        if (loggedUser == null)
            throw new HttpProcessingException(401);

        Player resourceOwner = _playerDao.getPlayer(loggedUser);

        if (resourceOwner == null)
            throw new HttpProcessingException(401);

        if (resourceOwner.getType().contains("a") && participantId != null && !participantId.equals("null") && !participantId.equals("")) {
            resourceOwner = _playerDao.getPlayer(participantId);
            if (resourceOwner == null)
                throw new HttpProcessingException(401);
        }
        return resourceOwner;
    }

    protected final Player getLibrarian() throws HttpProcessingException {
        Player resourceOwner = _playerDao.getPlayer("Librarian");

        if (resourceOwner == null)
            throw new HttpProcessingException(401);

        return resourceOwner;
    }

    protected String getQueryParameterSafely(QueryStringDecoder queryStringDecoder, String parameterName) {
        List<String> parameterValues = queryStringDecoder.getParameters().get(parameterName);
        if (parameterValues != null && !parameterValues.isEmpty())
            return parameterValues.get(0);
        else
            return null;
    }

    protected List<String> getFormMultipleParametersSafely(HttpPostRequestDecoder postRequestDecoder, String parameterName) throws HttpPostRequestDecoder.NotEnoughDataDecoderException, IOException {
        List<String> result = new LinkedList<String>();
        List<InterfaceHttpData> datas = postRequestDecoder.getBodyHttpDatas(parameterName);
        if (datas == null)
            return Collections.emptyList();
        for (InterfaceHttpData data : datas) {
            if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                Attribute attribute = (Attribute) data;
                result.add(attribute.getValue());
            }

        }
        return result;
    }

    protected String getFormParameterSafely(HttpPostRequestDecoder postRequestDecoder, String parameterName) throws IOException, HttpPostRequestDecoder.NotEnoughDataDecoderException {
        InterfaceHttpData data = postRequestDecoder.getBodyHttpData(parameterName);
        if (data == null)
            return null;
        if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
            Attribute attribute = (Attribute) data;
            return attribute.getValue();
        } else {
            return null;
        }
    }

    protected List<String> getFormParametersSafely(HttpPostRequestDecoder postRequestDecoder, String parameterName) throws IOException, HttpPostRequestDecoder.NotEnoughDataDecoderException {
        List<InterfaceHttpData> datas = postRequestDecoder.getBodyHttpDatas(parameterName);
        if (datas == null)
            return null;
        List<String> result = new LinkedList<String>();
        for (InterfaceHttpData data : datas) {
            if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                Attribute attribute = (Attribute) data;
                result.add(attribute.getValue());
            }
        }
        return result;
    }

    protected <T> T extractObject(Map<Type, Object> context, Class<T> clazz) {
        Object value = context.get(clazz);
        return (T) value;
    }

    protected Map<String, String> logUserReturningHeaders(MessageEvent e, String login) throws SQLException {
        _playerDao.updateLastLoginIp(login, ((InetSocketAddress) e.getRemoteAddress()).getAddress().getHostAddress());

        CookieEncoder cookieEncoder = new CookieEncoder(true);
        for (Map.Entry<String, String> cookie : _loggedUserHolder.logUser(login).entrySet())
            cookieEncoder.addCookie(cookie.getKey(), cookie.getValue());

        return Collections.singletonMap(SET_COOKIE, cookieEncoder.encode());
    }
}
