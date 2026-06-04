package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.DateUtils;
import com.gempukku.swccgo.PlayerLock;
import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.auth.JwtService;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.collection.TransferDAO;
import com.gempukku.swccgo.common.ApplicationConfiguration;
import com.gempukku.swccgo.db.DeckDAO;
import com.gempukku.swccgo.db.GempSettingDAO;
import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.packagedProduct.ProductName;
import com.gempukku.swccgo.service.LoggedUserHolder;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.*;


public class SwccgoServerRequestHandler {
    public static final int WEEKLY_GOLD = 1500;
    protected PlayerDAO _playerDao;
    protected LoggedUserHolder _loggedUserHolder;
    private final TransferDAO _transferDAO;
    private final CollectionsManager _collectionManager;
    protected DeckDAO _deckDao;
    protected GempSettingDAO _gempSettingDAO;
    protected final JwtService _jwtService;

    public SwccgoServerRequestHandler(Map<Type, Object> context) {
        _playerDao = extractObject(context, PlayerDAO.class);
        _loggedUserHolder = extractObject(context, LoggedUserHolder.class);
        _transferDAO = extractObject(context, TransferDAO.class);
        _collectionManager = extractObject(context, CollectionsManager.class);
        _deckDao = extractObject(context, DeckDAO.class);
        _gempSettingDAO = extractObject(context, GempSettingDAO.class);
        _jwtService = extractObject(context, JwtService.class);
    }

    private boolean isTest() {
        return Boolean.parseBoolean(System.getProperty("test"));
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
                            _collectionManager.addCurrencyToPlayerCollection(true, "Weekly reward", player, CollectionType.MY_CARDS, WEEKLY_GOLD);
                        }
                    }
                }
            }
        }
    }

    private String getLoggedUser(HttpRequest request) {
        // Keep legacy session cookie lookup first so existing pages behave exactly as before.
        String cookieHeader = request.headers().get(HttpHeaderNames.COOKIE);
        if (cookieHeader != null) {
            String[] parts = cookieHeader.split(";");
            for (int i = parts.length - 1; i >= 0; i--) {
                String part = parts[i].trim();
                if (part.startsWith("loggedUser=")) {
                    String value = part.substring("loggedUser=".length());
                    if (!value.isEmpty()) {
                        String loggedUser = _loggedUserHolder.getLoggedUser(value);
                        if (loggedUser != null) {
                            return loggedUser;
                        }
                    }
                }
            }
        }

        // JWT is a fallback for API/WS calls that do not carry the legacy loggedUser session.
        String jwtSubject = getJwtSubject(request);
        if (jwtSubject != null && !jwtSubject.isEmpty()) {
            return jwtSubject;
        }
        return null;
    }

    private String getJwtSubject(HttpRequest request) {
        String token = getJwtTokenFromRequest(request);
        if (token == null || token.isEmpty()) {
            return null;
        }
        JwtService.JwtToken jwtToken = _jwtService.verifyToken(token);
        if (jwtToken == null) {
            return null;
        }
        return jwtToken.getSubject();
    }

    private String getJwtTokenFromRequest(HttpRequest request) {
        String authHeader = request.headers().get(HttpHeaderNames.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring("Bearer ".length()).trim();
        }

        String cookieToken = getJwtTokenFromCookies(request);
        if (cookieToken != null && !cookieToken.isEmpty()) {
            return cookieToken;
        }

        // Keep query-string JWTs only as a compatibility fallback for non-cookie clients.
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        String queryToken = getQueryParameterSafely(decoder, "token");
        if (queryToken != null && !queryToken.isEmpty()) {
            return queryToken;
        }

        return null;
    }

    private String getJwtTokenFromCookies(HttpRequest request) {
        String cookieHeader = request.headers().get(HttpHeaderNames.COOKIE);
        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return null;
        }
        Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieHeader);
        for (Cookie cookie : cookies) {
            if (JwtService.JWT_COOKIE_NAME.equals(cookie.name())) {
                return cookie.value();
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
        List<String> parameterValues = queryStringDecoder.parameters().get(parameterName);
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

    protected Map<String, String> logUserReturningHeaders(String remoteIp, String login) throws SQLException {
        _playerDao.updateLastLoginIp(login, remoteIp);

        String sessionId = _loggedUserHolder.logUser(login);
        DefaultCookie cookie = new DefaultCookie("loggedUser", sessionId);
        cookie.setPath("/");
        applyLegacyCookieAttributes(cookie);
        return Collections.singletonMap(
                HttpHeaderNames.SET_COOKIE.toString(), encodeLegacyCookie(cookie));
    }

    private void applyLegacyCookieAttributes(DefaultCookie cookie) {
        String domain = getCookieConfig("auth.cookie.domain", "AUTH_COOKIE_DOMAIN");
        if (domain != null && !domain.isEmpty()) {
            cookie.setDomain(domain);
        }
        String sameSite = getCookieConfig("auth.cookie.sameSite", "AUTH_COOKIE_SAMESITE");
        boolean secure = "true".equalsIgnoreCase(getCookieConfig("auth.cookie.secure", "AUTH_COOKIE_SECURE"));
        if ("none".equalsIgnoreCase(sameSite) || secure) {
            cookie.setSecure(true);
        }
    }

    private String encodeLegacyCookie(DefaultCookie cookie) {
        String header = ServerCookieEncoder.STRICT.encode(cookie);
        String sameSite = getCookieConfig("auth.cookie.sameSite", "AUTH_COOKIE_SAMESITE");
        if (sameSite != null && !sameSite.isEmpty()) {
            header = header + "; SameSite=" + sameSite;
        }
        return header;
    }

    private String getCookieConfig(String propertyName, String envName) {
        String envValue = System.getenv(envName);
        if (envValue != null && !envValue.isEmpty()) {
            return envValue;
        }
        return ApplicationConfiguration.getProperty(propertyName);
    }
}
