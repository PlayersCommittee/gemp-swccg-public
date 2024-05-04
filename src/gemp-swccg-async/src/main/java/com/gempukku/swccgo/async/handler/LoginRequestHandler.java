package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.game.Player;
import com.mysql.cj.util.StringUtils;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

public class LoginRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    public LoginRequestHandler(Map<Type, Object> context) {
        super(context);
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, String remoteIp) throws Exception {
        if ("".equals(uri) && request.method() == HttpMethod.POST) {
            HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
            String login = getFormParameterSafely(postDecoder, "login");
            String password = getFormParameterSafely(postDecoder, "password");

            Player player = _playerDao.loginPlayer(login, password);

            //No user found, which is to say either user does not exist or user failed to provide credentials
            if(player == null)
                throw new HttpProcessingException(401);

            //User was found, but they had a blank password, which means they have had their password reset
            // and need to be sent through the registration flow so that their password is typed in twice.
            if (StringUtils.isNullOrEmpty(player.getPassword()))
                throw new HttpProcessingException(202);

            //User has a permaban or tempban state
            if (!player.hasType(Player.Type.UNBANNED)) {
                final Date bannedUntil = player.getBannedUntil();

                //Permabanned
                if(bannedUntil == null)
                    throw new HttpProcessingException(403);

                //Tempbanned
                if (bannedUntil.after(new Date()))
                    throw new HttpProcessingException(409);
            }

            responseWriter.writeXmlResponse(null, logUserReturningHeaders(remoteIp, login));
        }
        else {
            throw new HttpProcessingException(404);
        }
    }

}
