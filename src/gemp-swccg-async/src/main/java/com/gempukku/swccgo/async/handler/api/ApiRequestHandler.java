package com.gempukku.swccgo.async.handler.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.auth.JwtService;
import com.gempukku.swccgo.async.handler.SwccgoServerRequestHandler;
import com.gempukku.swccgo.game.Player;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.util.CharsetUtil;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

public class ApiRequestHandler extends SwccgoServerRequestHandler {
    public ApiRequestHandler(Map<Type, Object> context) {
        super(context);
    }

    protected Player getResourceOwnerFromToken(HttpRequest request) throws HttpProcessingException {
        String token = getTokenFromRequest(request);
        if (token == null)
            throw new HttpProcessingException(401);

        JwtService.JwtToken jwtToken = _jwtService.verifyToken(token);
        if (jwtToken == null)
            throw new HttpProcessingException(401);

        Player resourceOwner = _playerDao.getPlayer(jwtToken.getSubject());
        if (resourceOwner == null)
            throw new HttpProcessingException(401);

        return resourceOwner;
    }

    protected String getTokenFromRequest(HttpRequest request) {
        String authHeader = request.headers().get(HttpHeaderNames.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer "))
            return authHeader.substring("Bearer ".length()).trim();

        String cookieToken = getTokenFromCookies(request);
        if (cookieToken != null && !cookieToken.isEmpty())
            return cookieToken;

        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        String queryToken = getQueryParameterSafely(decoder, "token");
        if (queryToken != null && !queryToken.isEmpty())
            return queryToken;

        return null;
    }

    private String getTokenFromCookies(HttpRequest request) {
        String cookieHeader = request.headers().get(HttpHeaderNames.COOKIE);
        if (cookieHeader == null || cookieHeader.isEmpty())
            return null;

        Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieHeader);
        for (Cookie cookie : cookies) {
            if (JwtService.JWT_COOKIE_NAME.equals(cookie.name()))
                return cookie.value();
        }
        return null;
    }

    protected JSONObject readJsonBody(HttpRequest request) throws HttpProcessingException {
        if (!(request instanceof FullHttpRequest))
            return null;
        String body = ((FullHttpRequest) request).content().toString(CharsetUtil.UTF_8);
        if (body == null || body.trim().isEmpty())
            return null;
        try {
            return JSON.parseObject(body);
        } catch (Exception e) {
            throw new HttpProcessingException(400);
        }
    }
}
