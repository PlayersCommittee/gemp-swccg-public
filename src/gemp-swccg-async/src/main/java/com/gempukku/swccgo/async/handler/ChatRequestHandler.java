package com.gempukku.swccgo.async.handler;

import com.gempukku.polling.LongPollingResource;
import com.gempukku.polling.LongPollingSystem;
import com.gempukku.swccgo.PrivateInformationException;
import com.gempukku.swccgo.SubscriptionExpiredException;
import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.async.util.ChatUserListFormatter;
import com.gempukku.swccgo.chat.ChatCommandErrorException;
import com.gempukku.swccgo.chat.ChatMessage;
import com.gempukku.swccgo.chat.ChatRoomMediator;
import com.gempukku.swccgo.chat.ChatServer;
import com.gempukku.swccgo.game.ChatCommunicationChannel;
import com.gempukku.swccgo.game.Player;
import org.apache.commons.text.StringEscapeUtils;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.*;

public class ChatRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    private final ChatServer _chatServer;
    private final LongPollingSystem _longPollingSystem;

    public ChatRequestHandler(Map<Type, Object> context, LongPollingSystem longPollingSystem) {
        super(context);
        _chatServer = extractObject(context, ChatServer.class);
        _longPollingSystem = longPollingSystem;
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, String remoteIp) throws Exception {
        if (uri.startsWith("/") && request.method() == HttpMethod.GET) {
            getMessages(request, URLDecoder.decode(uri.substring(1)), responseWriter);
        } else if (uri.startsWith("/") && request.method() == HttpMethod.POST) {
            postMessages(request, URLDecoder.decode(uri.substring(1)), responseWriter);
        } else {
            responseWriter.writeError(404);
        }
    }

    private void postMessages(HttpRequest request, String room, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            String participantId = getFormParameterSafely(postDecoder, "participantId");
            String latestMsgIdRcvd = getFormParameterSafely(postDecoder, "latestMsgIdRcvd");
            String message = getFormParameterSafely(postDecoder, "message");

            Player resourceOwner = getResourceOwnerSafely(request, participantId);

            ChatRoomMediator chatRoom = _chatServer.getChatRoom(room);
            if (chatRoom == null)
                throw new HttpProcessingException(404);

            try {
                if (message != null && !message.trim().isEmpty()) {
                    chatRoom.sendMessage(resourceOwner.getName(), StringEscapeUtils.escapeHtml3(message), resourceOwner.hasType(Player.Type.ADMIN));
                    responseWriter.writeXmlResponse(null);
                } else {
                    ChatCommunicationChannel pollableResource = chatRoom.getChatRoomListener(resourceOwner.getName());
                    ChatUpdateLongPollingResource polledResource = new ChatUpdateLongPollingResource(chatRoom, room, resourceOwner.getName(), latestMsgIdRcvd != null ? Integer.valueOf(latestMsgIdRcvd) : null, responseWriter);
                    _longPollingSystem.processLongPollingResource(polledResource, pollableResource);
                }
            } catch (SubscriptionExpiredException exp) {
                throw new HttpProcessingException(410);
            } catch (PrivateInformationException exp) {
                throw new HttpProcessingException(403);
            } catch (ChatCommandErrorException exp) {
                throw new HttpProcessingException(400);
            }
        }
        finally {
            postDecoder.destroy();
        }
    }

    private class ChatUpdateLongPollingResource implements LongPollingResource {
        private final ChatRoomMediator _chatRoom;
        private final String _room;
        private final String _playerId;
        private final Integer _latestMsgIdRcvd;
        private final ResponseWriter _responseWriter;
        private boolean _processed;

        private ChatUpdateLongPollingResource(ChatRoomMediator chatRoom, String room, String playerId, Integer latestMsgIdRcvd, ResponseWriter responseWriter) {
            _chatRoom = chatRoom;
            _room = room;
            _playerId = playerId;
            _latestMsgIdRcvd = latestMsgIdRcvd;
            _responseWriter = responseWriter;
        }

        @Override
        public synchronized boolean wasProcessed() {
            return _processed;
        }

        @Override
        public synchronized void processIfNotProcessed() {
            if (!_processed) {
                try {
                    List<ChatMessage> chatMessages = _chatRoom.getChatRoomListener(_playerId).consumeMessages(_latestMsgIdRcvd);

                    Collection<String> usersInRoom = _chatRoom.getUsersInRoom();

                    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                    Document doc = documentBuilder.newDocument();

                    serializeChatRoomData(_room, chatMessages, usersInRoom, doc);

                    _responseWriter.writeXmlResponse(doc);
                } catch (SubscriptionExpiredException exp) {
                    _responseWriter.writeError(410);
                } catch (Exception exp) {
                    _responseWriter.writeError(500);
                }
                _processed = true;
            }
        }
    }

    private void getMessages(HttpRequest request, String room, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.uri());
        String participantId = getQueryParameterSafely(queryDecoder, "participantId");

        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        ChatRoomMediator chatRoom = _chatServer.getChatRoom(room);
        if (chatRoom == null)
            throw new HttpProcessingException(404);
        try {
            List<ChatMessage> chatMessages = chatRoom.joinUser(resourceOwner.getName(), resourceOwner.hasType(Player.Type.ADMIN), resourceOwner.hasType(Player.Type.PLAYTESTER));
            Collection<String> usersInRoom = chatRoom.getUsersInRoom();

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document doc = documentBuilder.newDocument();

            serializeChatRoomData(room, chatMessages, usersInRoom, doc);

            responseWriter.writeXmlResponse(doc);
        } catch (PrivateInformationException exp) {
            throw new HttpProcessingException(403);
        }
    }

    private void serializeChatRoomData(String room, List<ChatMessage> chatMessages, Collection<String> usersInRoom, Document doc) {
        Element chatElem = doc.createElement("chat");
        chatElem.setAttribute("roomName", room);
        doc.appendChild(chatElem);

        for (ChatMessage chatMessage : chatMessages) {
            Element message = doc.createElement("message");
            message.setAttribute("msgId", String.valueOf(chatMessage.getMsgId()));
            message.setAttribute("from", chatMessage.getFrom());
            message.setAttribute("date", String.valueOf(chatMessage.getWhen().getTime()));
            message.appendChild(doc.createTextNode(chatMessage.getMessage()));
            chatElem.appendChild(message);
        }

        List<String> users = ChatUserListFormatter.formatAndSortUsers(usersInRoom, _playerDao);

        for (String userInRoom : users) {
            Element user = doc.createElement("user");
            user.appendChild(doc.createTextNode(userInRoom));
            chatElem.appendChild(user);
        }
    }

}
