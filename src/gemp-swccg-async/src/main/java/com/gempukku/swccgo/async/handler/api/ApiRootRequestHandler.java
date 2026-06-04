package com.gempukku.swccgo.async.handler.api;

import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.async.handler.UriRequestHandler;
import io.netty.handler.codec.http.HttpRequest;

import java.lang.reflect.Type;
import java.util.Map;

public class ApiRootRequestHandler implements UriRequestHandler {
    private final ApiAuthRequestHandler _authRequestHandler;

    public ApiRootRequestHandler(Map<Type, Object> context) {
        _authRequestHandler = new ApiAuthRequestHandler(context);
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, String remoteIp) throws Exception {
        if (uri.startsWith("/auth")) {
            _authRequestHandler.handleRequest(uri.substring(5), request, context, responseWriter, remoteIp);
        } else {
            responseWriter.writeError(404);
        }
    }
}
