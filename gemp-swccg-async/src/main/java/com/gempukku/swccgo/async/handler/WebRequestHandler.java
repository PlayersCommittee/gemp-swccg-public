package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.ResponseWriter;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.IF_NONE_MATCH;

public class WebRequestHandler implements UriRequestHandler {
    private String _root;
    private String _uniqueEtag;

    public WebRequestHandler(String root) {
        _root = root;
        _uniqueEtag = "\"" + String.valueOf(System.currentTimeMillis()) + "\"";
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, MessageEvent e) throws Exception {
        if (clientHasCurrentVersion(request)) {
            responseWriter.writeError(304);
            return;
        }

        if ("".equals(uri))
            uri = "index.html";

        uri = uri.replace('/', File.separatorChar);

        if ((uri.contains(".."))
                || uri.contains(File.separator + ".")
                || uri.startsWith(".") || uri.endsWith(".")) {
            responseWriter.writeError(403);
        } else {
            File file = new File(_root + uri);
            if (!file.getCanonicalPath().startsWith(_root))
                responseWriter.writeError(403);
            else {
                responseWriter.writeFile(file, Collections.singletonMap(HttpHeaders.Names.ETAG, _uniqueEtag));
            }
        }
    }

    private boolean clientHasCurrentVersion(HttpRequest request) {
        String ifNoneMatch = request.getHeader(IF_NONE_MATCH);
        if (ifNoneMatch != null) {
            String[] clientKnownVersions = ifNoneMatch.split(",");
            for (String clientKnownVersion : clientKnownVersions) {
                if (clientKnownVersion.trim().equals(_uniqueEtag))
                    return true;
            }
        }
        return false;
    }
}
