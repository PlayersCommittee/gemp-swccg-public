package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.game.GameRecorder;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.AsciiString;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

public class ReplayRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    private final GameRecorder _gameRecorder;

    private static final Logger _log = LogManager.getLogger(ReplayRequestHandler.class);

    public ReplayRequestHandler(Map<Type, Object> context) {
        super(context);

        _gameRecorder = extractObject(context, GameRecorder.class);
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, String remoteIp) throws Exception {
        if (uri.startsWith("/") && request.method() == HttpMethod.GET) {
            String replayId = uri.substring(1);

            if (!replayId.contains("$"))
                throw new HttpProcessingException(404);
            if (replayId.contains("."))
                throw new HttpProcessingException(404);

            final String[] split = replayId.split("\\$");
            if (split.length != 2)
                throw new HttpProcessingException(404);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            final InputStream recordedGame = _gameRecorder.getRecordedGame(split[0], split[1]);
            try (recordedGame) {
                if (recordedGame == null)
                    throw new HttpProcessingException(404);
                byte[] bytes = new byte[1024];
                int count;
                while ((count = recordedGame.read(bytes)) != -1)
                    baos.write(bytes, 0, count);
            } catch (IOException exp) {
                _log.error("Error 404 response for " + request.uri(), exp);
                throw new HttpProcessingException(404);
            }

            Map<AsciiString, String> headers = new HashMap<>();
            headers.put(CONTENT_TYPE, "application/html; charset=UTF-8");

            responseWriter.writeByteResponse(baos.toByteArray(), headers);
        } else {
            responseWriter.writeError(404);
        }
    }
}
