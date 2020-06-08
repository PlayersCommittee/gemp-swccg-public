package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.game.Player;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.multipart.HttpPostRequestDecoder;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

public class LoginRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    public LoginRequestHandler(Map<Type, Object> context) {
        super(context);
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, MessageEvent e) throws Exception {
        if ("".equals(uri) && request.getMethod() == HttpMethod.POST) {
            HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
            String login = getFormParameterSafely(postDecoder, "login");
            String password = getFormParameterSafely(postDecoder, "password");

            Player player = _playerDao.loginPlayer(login, password);
            if (player != null) {
                if (!player.hasType(Player.Type.UNBANNED)) {
                    final Date bannedUntil = player.getBannedUntil();
                    if (bannedUntil != null) {
                        if (bannedUntil.after(new Date())) {
                            responseWriter.writeXmlResponse(null, logUserReturningHeaders(e, login));
                        }
                        else {
                            responseWriter.writeError(409);
                        }
                    }
                    else {
                        responseWriter.writeError(403);
                    }
                } else {
                    responseWriter.writeXmlResponse(null, logUserReturningHeaders(e, login));
                }
            } else {
                responseWriter.writeError(401);
            }

        } else {
            responseWriter.writeError(404);
        }
    }

}
