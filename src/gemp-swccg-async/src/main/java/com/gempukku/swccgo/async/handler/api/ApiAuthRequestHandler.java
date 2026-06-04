package com.gempukku.swccgo.async.handler.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.auth.JwtService;
import com.gempukku.swccgo.common.ApplicationConfiguration;
import com.gempukku.swccgo.db.LoginInvalidException;
import com.gempukku.swccgo.db.RegisterNotAllowedException;
import com.gempukku.swccgo.game.Player;
import com.mysql.cj.util.StringUtils;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.util.CharsetUtil;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class ApiAuthRequestHandler extends ApiRequestHandler {
    public ApiAuthRequestHandler(Map<Type, Object> context) {
        super(context);
    }

    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, String remoteIp) throws Exception {
        if ("/login".equals(uri) && request.method() == HttpMethod.POST) {
            login(request, responseWriter, remoteIp);
        } else if ("/register".equals(uri) && request.method() == HttpMethod.POST) {
            register(request, responseWriter, remoteIp);
        } else if ("/logout".equals(uri) && request.method() == HttpMethod.POST) {
            logout(request, responseWriter);
        } else {
            responseWriter.writeError(404);
        }
    }

    private void login(HttpRequest request, ResponseWriter responseWriter, String remoteIp) throws Exception {
        String login = null;
        String password = null;
        String contentType = request.headers().get(HttpHeaderNames.CONTENT_TYPE);
        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            JSONObject json = readJsonBody(request);
            if (json != null) {
                login = json.getString("login");
                password = json.getString("password");
            }
        }

        if (login == null || password == null) {
            if (contentType != null && (contentType.contains("application/x-www-form-urlencoded")
                    || contentType.contains("multipart/form-data"))) {
                HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
                try {
                    if (login == null)
                        login = getFormParameterSafely(postDecoder, "login");
                    if (password == null)
                        password = getFormParameterSafely(postDecoder, "password");
                } finally {
                    postDecoder.destroy();
                }
            }
        }

        if (login == null || password == null)
            throw new HttpProcessingException(400);

        Player player = _playerDao.loginPlayer(login, password);
        if (player == null)
            throw new HttpProcessingException(401);

        if (StringUtils.isNullOrEmpty(player.getPassword()))
            throw new HttpProcessingException(202);

        if (!player.hasType(Player.Type.UNBANNED)) {
            Date bannedUntil = player.getBannedUntil();
            if (bannedUntil == null)
                throw new HttpProcessingException(403);
            if (bannedUntil.after(new Date()))
                throw new HttpProcessingException(409);
        }

        String token = _jwtService.issueToken(player.getName());
        JwtService.JwtToken jwtToken = _jwtService.verifyToken(token);
        long expiresAt = jwtToken != null ? jwtToken.getExpiresAt() : 0L;

        _playerDao.updateLastLoginIp(player.getName(), remoteIp);

        Map<String, String> headers = new LinkedHashMap<String, String>();
        headers.put(HttpHeaderNames.CONTENT_TYPE.toString(), "application/json; charset=UTF-8");
        addJwtCookie(headers, request, token, expiresAt);
        writeAuthResponse(responseWriter, token, expiresAt, player, headers);
    }

    private void register(HttpRequest request, ResponseWriter responseWriter, String remoteIp) throws Exception {
        String login = null;
        String password = null;
        String contentType = request.headers().get(HttpHeaderNames.CONTENT_TYPE);
        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            JSONObject json = readJsonBody(request);
            if (json != null) {
                login = json.getString("login");
                password = json.getString("password");
            }
        }

        if (login == null || password == null) {
            if (contentType != null && (contentType.contains("application/x-www-form-urlencoded")
                    || contentType.contains("multipart/form-data"))) {
                HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
                try {
                    if (login == null)
                        login = getFormParameterSafely(postDecoder, "login");
                    if (password == null)
                        password = getFormParameterSafely(postDecoder, "password");
                } finally {
                    postDecoder.destroy();
                }
            }
        }

        if (login == null || password == null)
            throw new HttpProcessingException(400);

        try {
            if (!_gempSettingDAO.newAccountRegistrationEnabled()) {
                throw new RegisterNotAllowedException();
            }
            if (!_playerDao.registerPlayer(login, password, remoteIp)) {
                throw new HttpProcessingException(403);
            }
        } catch (LoginInvalidException exp) {
            throw new HttpProcessingException(400);
        } catch (RegisterNotAllowedException exp) {
            throw new HttpProcessingException(405);
        }

        Player player = _playerDao.getPlayer(login);
        if (player == null)
            throw new HttpProcessingException(500);

        String token = _jwtService.issueToken(player.getName());
        JwtService.JwtToken jwtToken = _jwtService.verifyToken(token);
        long expiresAt = jwtToken != null ? jwtToken.getExpiresAt() : 0L;

        _playerDao.updateLastLoginIp(player.getName(), remoteIp);

        Map<String, String> headers = new LinkedHashMap<String, String>();
        headers.put(HttpHeaderNames.CONTENT_TYPE.toString(), "application/json; charset=UTF-8");
        addJwtCookie(headers, request, token, expiresAt);
        writeAuthResponse(responseWriter, token, expiresAt, player, headers);
    }

    private void logout(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        AuthResult auth = requireAuth(request);
        _loggedUserHolder.forceLogoutUser(auth.player.getName());

        Map<String, Object> response = new LinkedHashMap<String, Object>();
        response.put("status", "ok");

        String payload = JSON.toJSONString(response);
        Map<String, String> headers = new LinkedHashMap<String, String>();
        headers.put(HttpHeaderNames.CONTENT_TYPE.toString(), "application/json; charset=UTF-8");

        addClearedJwtCookie(headers, request);

        responseWriter.writeByteResponse(payload.getBytes(CharsetUtil.UTF_8), headers);
    }

    private AuthResult requireAuth(HttpRequest request) throws HttpProcessingException {
        String token = getTokenFromRequest(request);
        if (token == null || token.isEmpty()) {
            throw new HttpProcessingException(401);
        }
        JwtService.JwtToken jwtToken = _jwtService.verifyToken(token);
        if (jwtToken == null) {
            throw new HttpProcessingException(401);
        }
        Player player = _playerDao.getPlayer(jwtToken.getSubject());
        if (player == null) {
            throw new HttpProcessingException(401);
        }
        return new AuthResult(player, token, jwtToken.getExpiresAt());
    }

    private void writeAuthResponse(ResponseWriter responseWriter, String token, long expiresAt, Player player, Map<String, String> headers) throws Exception {
        Map<String, Object> response = new LinkedHashMap<String, Object>();
        response.put("token", token);
        response.put("tokenType", "Bearer");
        response.put("expiresAt", expiresAt);
        response.put("user", player.GetUserInfo());

        String payload = JSON.toJSONString(response);
        Map<String, String> responseHeaders = new LinkedHashMap<String, String>();
        responseHeaders.put(HttpHeaderNames.CONTENT_TYPE.toString(), "application/json; charset=UTF-8");
        if (headers != null) {
            responseHeaders.putAll(headers);
        }
        responseWriter.writeByteResponse(payload.getBytes(CharsetUtil.UTF_8), responseHeaders);
    }

    private void addJwtCookie(Map<String, String> headers, HttpRequest request, String token, long expiresAt) {
        if (headers == null)
            return;
        DefaultCookie cookie = new DefaultCookie(JwtService.JWT_COOKIE_NAME, token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        applyCookieAttributes(cookie, request);
        long maxAge = Math.max(0L, expiresAt - Instant.now().getEpochSecond());
        cookie.setMaxAge(maxAge);
        headers.put(HttpHeaderNames.SET_COOKIE.toString(), encodeCookie(cookie));
    }

    private void addClearedJwtCookie(Map<String, String> headers, HttpRequest request) {
        if (headers == null)
            return;
        DefaultCookie cookie = new DefaultCookie(JwtService.JWT_COOKIE_NAME, "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        applyCookieAttributes(cookie, request);
        cookie.setMaxAge(0);
        headers.put(HttpHeaderNames.SET_COOKIE.toString(), encodeCookie(cookie));
    }

    private boolean isSecureRequest(HttpRequest request) {
        String forwardedProto = request.headers().get("X-Forwarded-Proto");
        return forwardedProto != null && forwardedProto.toLowerCase().contains("https");
    }

    private void applyCookieAttributes(DefaultCookie cookie, HttpRequest request) {
        String domain = getCookieConfig("auth.cookie.domain", "AUTH_COOKIE_DOMAIN");
        if (domain != null && !domain.isEmpty()) {
            cookie.setDomain(domain);
        }
        String sameSite = getCookieConfig("auth.cookie.sameSite", "AUTH_COOKIE_SAMESITE");
        if ("none".equalsIgnoreCase(sameSite)) {
            cookie.setSecure(true);
        } else if (isSecureRequest(request)) {
            cookie.setSecure(true);
        }
    }

    private String encodeCookie(DefaultCookie cookie) {
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

    private static final class AuthResult {
        private final Player player;
        private final String token;
        private final long expiresAt;

        private AuthResult(Player player, String token, long expiresAt) {
            this.player = player;
            this.token = token;
            this.expiresAt = expiresAt;
        }
    }
}
