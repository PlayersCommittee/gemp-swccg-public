package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.game.GameRecorder;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;

public class ReplayRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    private GameRecorder _gameRecorder;

    public ReplayRequestHandler(Map<Type, Object> context) {
        super(context);

        _gameRecorder = extractObject(context, GameRecorder.class);
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, MessageEvent e) throws Exception {
        if (uri.startsWith("/") && request.getMethod() == HttpMethod.GET) {
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
            if (recordedGame == null)
                throw new HttpProcessingException(404);
            try {
                byte[] bytes = new byte[1024];
                int count;
                while ((count = recordedGame.read(bytes)) != -1)
                    baos.write(bytes, 0, count);
            } finally {
                recordedGame.close();
            }

            responseWriter.writeByteResponse("application/html; charset=UTF-8", baos.toByteArray());
        } else {
            responseWriter.writeError(404);
        }
    }
}
