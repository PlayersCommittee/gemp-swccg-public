package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.db.LoginInvalidException;
import com.gempukku.swccgo.db.RegisterNotAllowedException;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.util.Map;

public class RegisterRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    public RegisterRequestHandler(Map<Type, Object> context) {
        super(context);
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, String remoteIp) throws Exception {
        if ("".equals(uri) && request.method() == HttpMethod.POST) {
            HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
            try {
                String login = getFormParameterSafely(postDecoder, "login");
                String password = getFormParameterSafely(postDecoder, "password");
                try {
                    if (!_gempSettingDAO.newAccountRegistrationEnabled()) {
                        throw new RegisterNotAllowedException();
                    }
                    if (_playerDao.registerPlayer(login, password, remoteIp)) {
                        responseWriter.writeXmlResponse(null, logUserReturningHeaders(remoteIp, login));
                    } else {
                        throw new HttpProcessingException(403);
                    }
                } catch (LoginInvalidException exp) {
                    throw new HttpProcessingException(400);
                } catch (RegisterNotAllowedException exp) {
                    throw new HttpProcessingException(405);
                }
            }
            finally {
                postDecoder.destroy();
            }
        } else {
            responseWriter.writeError(404);
        }
    }
}
