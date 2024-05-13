package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.ResponseWriter;
import io.netty.handler.codec.http.HttpRequest;

import java.lang.reflect.Type;
import java.util.Map;

public interface UriRequestHandler {
    void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, String remoteIp) throws Exception;
}
