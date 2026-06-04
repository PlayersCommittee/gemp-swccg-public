package com.gempukku.swccgo.async;

import com.gempukku.swccgo.auth.JwtService;
import com.gempukku.swccgo.async.handler.UriRequestHandler;
import com.gempukku.swccgo.async.ws.ChatWebSocketSession;
import com.gempukku.swccgo.async.ws.GameWebSocketSession;
import com.gempukku.swccgo.async.ws.HallWebSocketSession;
import com.gempukku.swccgo.async.ws.SwccgoWebSocketFrameHandler;
import com.gempukku.swccgo.async.ws.WebSocketSession;
import com.gempukku.swccgo.chat.ChatRoomMediator;
import com.gempukku.swccgo.chat.ChatServer;
import com.gempukku.swccgo.db.IpBanDAO;
import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgoServer;
import com.gempukku.swccgo.hall.HallServer;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class SwccgoHttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final long SIX_MONTHS = 1000L*60L*60L*24L*30L*6L;
    private static final String WEBSOCKET_PATH = "/gemp-swccg-server/ws";
    private static final String REWRITTEN_WEBSOCKET_PATH = "/ws";
    private static final String X_FORWARDED_HOST = "X-Forwarded-Host";
    private static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";
    private final Logger _log = LogManager.getLogger(SwccgoHttpRequestHandler.class);
    private static final Logger _accesslog = LogManager.getLogger("access");
    private final Map<String, byte[]> _fileCache = Collections.synchronizedMap(new HashMap<>());
    private final Map<Type, Object> _objects;
    private final UriRequestHandler _uriRequestHandler;

    private final IpBanDAO _ipBanDAO;
    private final JwtService _jwtService;

    public SwccgoHttpRequestHandler(Map<Type, Object> objects, UriRequestHandler uriRequestHandler) {
        _objects = objects;
        _uriRequestHandler = uriRequestHandler;
        _ipBanDAO = (IpBanDAO) _objects.get(IpBanDAO.class);
        _jwtService = (JwtService) _objects.get(JwtService.class);
        if (_jwtService == null)
            throw new IllegalStateException("JwtService not registered in server context");
    }


    private static class RequestInformation {
        private final String uri;
        private final String remoteIp;
        private final long requestTime;

        private RequestInformation(String uri, String remoteIp, long requestTime) {
            this.uri = uri;
            this.remoteIp = remoteIp;
            this.requestTime = requestTime;
        }

        public void printLog(int statusCode, long finishedTime) {
            _accesslog.debug(remoteIp + "," + statusCode + "," + uri + "," + (finishedTime - requestTime));
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) {
        if (HttpUtil.is100ContinueExpected(httpRequest))
            send100Continue(ctx);

        String ip = httpRequest.headers().get("X-Forwarded-For");

        if(ip == null)
            ip = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();

        final RequestInformation requestInformation = new RequestInformation(httpRequest.uri(),
                ip,
                System.currentTimeMillis());

        ResponseSender responseSender = new ResponseSender(ctx, httpRequest);

        String uri = null;
        try {
            if (isBanned(requestInformation.remoteIp)) {
                responseSender.writeError(401);
                _log.info("Denying entry to user from banned IP " + requestInformation.remoteIp);
            }
            else {
                if (isWebSocketRequest(httpRequest)) {
                    handleWebSocketRequest(ctx, httpRequest, responseSender);
                } else {
                    uri = httpRequest.uri();
                    if (uri.contains("?"))
                        uri = uri.substring(0, uri.indexOf("?"));
                    _uriRequestHandler.handleRequest(uri, httpRequest, _objects, responseSender, requestInformation.remoteIp);
                }
            }
        } catch (HttpProcessingException exp) {
            int code = exp.getStatus();
            //401, 403, 404, and other 400-series errors should just do minimal logging,
            if(code % 400 < 100 && code != 400) {
                _log.debug("HTTP " + code + " response for " + requestInformation.remoteIp + ": " + requestInformation.uri);
            }
            // but 400 itself should error out
            else if(code == 400 || code % 500 < 100) {
                _log.error("HTTP code " + code + " response for " + requestInformation.remoteIp + ": " + requestInformation.uri, exp);
            }

            if (exp.getMessage() != null) {
                responseSender.writeError(exp.getStatus(), Collections.singletonMap("message", exp.getMessage()));
            } else {
                responseSender.writeError(exp.getStatus());
            }
        } catch (Exception exp) {
            _log.error("Error response for " + uri, exp);
            responseSender.writeError(500);
        }
    }

    static boolean isWebSocketRequest(HttpRequest request) {
        String upgrade = request.headers().get(HttpHeaderNames.UPGRADE);
        if (upgrade != null && "websocket".equalsIgnoreCase(upgrade))
            return true;

        return request.headers().contains(HttpHeaderNames.SEC_WEBSOCKET_KEY)
                && request.headers().contains(HttpHeaderNames.SEC_WEBSOCKET_VERSION);
    }

    private boolean respondWithError(ResponseSender responseSender, int status) {
        responseSender.writeError(status);
        return false;
    }

    private boolean handleWebSocketRequest(ChannelHandlerContext ctx, FullHttpRequest request, ResponseSender responseSender) {
        if (request.method() != HttpMethod.GET) {
            return respondWithError(responseSender, 405);
        }

        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        String path = decoder.path();
        if (!isSupportedWebSocketPath(path)) {
            return respondWithError(responseSender, 404);
        }

        normalizeWebSocketHandshakeHeaders(request);

        String token = getTokenFromCookies(request);
        if (token == null || token.isEmpty())
            token = getQueryParameter(decoder, "token");
        if (token == null || token.isEmpty()) {
            return respondWithError(responseSender, 401);
        }

        JwtService.JwtToken jwtToken = _jwtService.verifyToken(token);
        if (jwtToken == null) {
            return respondWithError(responseSender, 401);
        }
        long tokenExpiresAt = jwtToken.getExpiresAt();

        PlayerDAO playerDAO = (PlayerDAO) _objects.get(PlayerDAO.class);
        Player player = playerDAO.getPlayer(jwtToken.getSubject());
        if (player == null) {
            return respondWithError(responseSender, 401);
        }

        String channel = getQueryParameter(decoder, "channel");
        if (channel == null || channel.isEmpty()) {
            return respondWithError(responseSender, 400);
        }

        WebSocketSession session = null;
        if ("hall".equals(channel)) {
            HallServer hallServer = (HallServer) _objects.get(HallServer.class);
            session = new HallWebSocketSession(ctx, hallServer, player, tokenExpiresAt);
        } else if ("chat".equals(channel)) {
            String room = getQueryParameter(decoder, "room");
            if (room == null || room.isEmpty()) {
                return respondWithError(responseSender, 400);
            }
            ChatServer chatServer = (ChatServer) _objects.get(ChatServer.class);
            ChatRoomMediator chatRoom = chatServer != null ? chatServer.getChatRoom(room) : null;
            if (chatRoom == null) {
                return respondWithError(responseSender, 404);
            }
            session = new ChatWebSocketSession(ctx, chatRoom, player, playerDAO, room, tokenExpiresAt);
        } else if ("game".equals(channel)) {
            String gameId = getQueryParameter(decoder, "gameId");
            if (gameId == null || gameId.isEmpty()) {
                return respondWithError(responseSender, 400);
            }
            String participantId = getQueryParameter(decoder, "participantId");
            String channelNumberParam = getQueryParameter(decoder, "channelNumber");
            Integer channelNumber = null;
            if (channelNumberParam != null && !channelNumberParam.isEmpty()) {
                try {
                    channelNumber = Integer.valueOf(channelNumberParam);
                } catch (NumberFormatException exp) {
                    return respondWithError(responseSender, 400);
                }
            }
            SwccgoServer swccgoServer = (SwccgoServer) _objects.get(SwccgoServer.class);
            if (swccgoServer == null) {
                return respondWithError(responseSender, 500);
            }
            session = new GameWebSocketSession(ctx, swccgoServer, playerDAO, player, gameId, participantId, channelNumber, tokenExpiresAt);
        }

        if (session == null) {
            return respondWithError(responseSender, 400);
        }

        WebSocketServerHandshakerFactory wsFactory =
                new WebSocketServerHandshakerFactory(getWebSocketLocation(request), null, true);
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(request);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            return false;
        }

        handshaker.handshake(ctx.channel(), request);
        ctx.pipeline().addLast(new SwccgoWebSocketFrameHandler(session, handshaker));
        session.onOpen();
        return true;
    }

    static String getWebSocketLocation(HttpRequest request) {
        String host = request.headers().get(X_FORWARDED_HOST);
        if (host == null || host.isEmpty())
            host = request.headers().get(HttpHeaderNames.HOST);

        String forwardedProto = request.headers().get(X_FORWARDED_PROTO);
        String scheme = forwardedProto != null && forwardedProto.toLowerCase().startsWith("https")
                ? "wss://"
                : "ws://";

        return scheme + host + WEBSOCKET_PATH;
    }

    static boolean isSupportedWebSocketPath(String path) {
        return WEBSOCKET_PATH.equals(path) || REWRITTEN_WEBSOCKET_PATH.equals(path);
    }

    static void normalizeWebSocketHandshakeHeaders(HttpRequest request) {
        request.headers().set(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET);
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE);
    }

    private String getQueryParameter(QueryStringDecoder decoder, String parameterName) {
        java.util.List<String> values = decoder.parameters().get(parameterName);
        if (values != null && !values.isEmpty())
            return values.get(0);
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

    private void sendResponse(ChannelHandlerContext ctx, HttpRequest request, FullHttpResponse response) {
        boolean keepAlive = HttpUtil.isKeepAlive(request);

        if (keepAlive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            // Add keep alive header as per:
            // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        ctx.write(response);
        ctx.flush();

        if (!keepAlive) {
            // If keep-alive is off, close the connection once the content is fully written.
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private boolean isBanned(String ipAddress) {
        if (_ipBanDAO.getIpBans().contains(ipAddress))
            return true;
        for (String bannedRange : _ipBanDAO.getIpPrefixBans()) {
            if (ipAddress.startsWith(bannedRange))
                return true;
        }
        return false;
    }


    private Map<String, String> getHeadersForFile(Map<String, String> headers, File file) {
        Map<String, String> fileHeaders = new HashMap<>(headers);

        boolean disableCaching = false;
        boolean cache = false;

        String fileName = file.getName();
        String contentType;
        if (fileName.endsWith(".html")) {
            contentType = "text/html; charset=UTF-8";
        } else if (fileName.endsWith(".js")) {
            contentType = "application/javascript; charset=UTF-8";
        } else if (fileName.endsWith(".css")) {
            contentType = "text/css; charset=UTF-8";
        } else if (fileName.endsWith(".jpg")) {
            cache = true;
            contentType = "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            cache = true;
            contentType = "image/png";
        } else if (fileName.endsWith(".gif")) {
            cache = true;
            contentType = "image/gif";
        } else if (fileName.endsWith(".wav")) {
            cache = true;
            contentType = "audio/wav";
        } else if (fileName.endsWith(".wasm")) {
            cache = true;
            contentType = "application/wasm";
        } else if (fileName.endsWith(".woff")) {
            cache = true;
            contentType = "font/woff";
        } else {
            contentType = "application/octet-stream";
        }

        if (disableCaching) {
            fileHeaders.put(CACHE_CONTROL, "no-cache");
            fileHeaders.put(PRAGMA, "no-cache");
            fileHeaders.put(EXPIRES, String.valueOf(-1));
        } else if (cache) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
            long sixMonthsFromNow = System.currentTimeMillis()+SIX_MONTHS;
            fileHeaders.put(EXPIRES, dateFormat.format(new Date(sixMonthsFromNow)));
        }

        fileHeaders.put(CONTENT_TYPE.toString(), contentType);
        return fileHeaders;
    }

    private HttpHeaders convertToHeaders(Map<? extends CharSequence, String> headersMap) {
        HttpHeaders headers = new DefaultHttpHeaders();
        if (headersMap != null) {
            for (Map.Entry<? extends CharSequence, String> headerEntry : headersMap.entrySet()) {
                headers.set(headerEntry.getKey(), headerEntry.getValue());
            }
        }
        return headers;
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
        ctx.write(response);
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!(cause instanceof IOException) && !(cause instanceof IllegalArgumentException))
            _log.error("Error while processing request", cause);
        ctx.close();
    }

    private class ResponseSender implements ResponseWriter {
        private final ChannelHandlerContext ctx;
        private final HttpRequest request;

        public ResponseSender(ChannelHandlerContext ctx, HttpRequest request) {
            this.ctx = ctx;
            this.request = request;
        }

        @Override
        public void writeError(int status) {
            byte[] content = new byte[0];
            // Build the response object.
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(status), Unpooled.wrappedBuffer(content), convertToHeaders(null), EmptyHttpHeaders.INSTANCE);
            sendResponse(ctx, request, response);
        }

        @Override
        public void writeError(int status, Map<String, String> headers) {
            byte[] content = new byte[0];
            // Build the response object.
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(status), Unpooled.wrappedBuffer(content), convertToHeaders(headers), EmptyHttpHeaders.INSTANCE);
            sendResponse(ctx, request, response);
        }

        @Override
        public void writeXmlResponse(Document document) {
            writeXmlResponse(document, null);
        }

        @Override
        public void writeXmlResponse(Document document, Map<? extends CharSequence, String> headers) {
            try {
                String contentType;
                String response1;
                if (document != null) {
                    DOMSource domSource = new DOMSource(document);
                    StringWriter writer = new StringWriter();
                    StreamResult result = new StreamResult(writer);
                    TransformerFactory tf = TransformerFactory.newInstance();
                    Transformer transformer = tf.newTransformer();
                    transformer.transform(domSource, result);

                    response1 = writer.toString();
                    contentType = "application/xml; charset=UTF-8";
                } else {
                    response1 = "<result>OK</result>";
                    contentType = "application/xml; charset=UTF-8";
                }
                HttpHeaders headers1 = convertToHeaders(headers);
                headers1.set(CONTENT_TYPE, contentType);

                // Build the response object.
                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(response1.getBytes(CharsetUtil.UTF_8)), headers1, EmptyHttpHeaders.INSTANCE);
                sendResponse(ctx, request, response);
            } catch (Exception exp) {
                byte[] content = new byte[0];
                // Build the response object.
                _log.error("Error response for " + request.uri(), exp);
                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, Unpooled.wrappedBuffer(content), null, EmptyHttpHeaders.INSTANCE);
                sendResponse(ctx, request, response);
            }
        }

        @Override
        public void writeHtmlResponse(String html) {
            HttpHeaders headers = new DefaultHttpHeaders();
            headers.set(CONTENT_TYPE, "text/html; charset=UTF-8");

            if (html == null)
                html = "";
            // Build the response object.
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(html.getBytes(CharsetUtil.UTF_8)), headers, EmptyHttpHeaders.INSTANCE);
            sendResponse(ctx, request, response);
        }

        @Override
        public void writeJsonResponse(String json) {
            HttpHeaders headers = new DefaultHttpHeaders();
            headers.set(CONTENT_TYPE, "application/json; charset=UTF-8");

            if (json == null)
                json = "{}";

            if(!json.startsWith("{") && !json.startsWith("[")) {
                JSONObject obj = new JSONObject();
                obj.put("response", json);
                json = obj.toString();
            }
            // Build the response object.
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(json.getBytes(CharsetUtil.UTF_8)), headers, EmptyHttpHeaders.INSTANCE);
            sendResponse(ctx, request, response);
        }

        @Override
        public void writeByteResponse(byte[] bytes, Map<? extends CharSequence, String> headers) {
            HttpHeaders headers1 = convertToHeaders(headers);

            // Build the response object.
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(bytes), headers1, EmptyHttpHeaders.INSTANCE);
            sendResponse(ctx, request, response);
        }

        @Override
        public void writeFile(File file, Map<String, String> headers) {
            try {
                String canonicalPath = file.getCanonicalPath();
                byte[] fileBytes = _fileCache.get(canonicalPath);
                if (fileBytes == null) {
                    if (!file.exists() || !file.isFile()) {
                        byte[] content = new byte[0];
                        // Build the response object.
                        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(404), Unpooled.wrappedBuffer(content), convertToHeaders(null), EmptyHttpHeaders.INSTANCE);
                        sendResponse(ctx, request, response);
                        return;
                    }

                    try (FileInputStream fis = new FileInputStream(file)) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        IOUtils.copy(fis, baos);
                        fileBytes = baos.toByteArray();
                        _fileCache.put(canonicalPath, fileBytes);
                    }
                }

                HttpHeaders headers1 = convertToHeaders(getHeadersForFile(headers, file));

                // Build the response object.
                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(fileBytes), headers1, EmptyHttpHeaders.INSTANCE);
                sendResponse(ctx, request, response);
            } catch (IOException exp) {
                byte[] content = new byte[0];
                // Build the response object.
                _log.error("Error response for " + request.uri(), exp);
                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(500), Unpooled.wrappedBuffer(content), convertToHeaders(null), EmptyHttpHeaders.INSTANCE);
                sendResponse(ctx, request, response);
            }
        }
    }
}
