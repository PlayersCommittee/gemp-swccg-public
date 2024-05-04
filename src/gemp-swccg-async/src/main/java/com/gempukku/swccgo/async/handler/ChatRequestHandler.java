package com.gempukku.swccgo.async.handler;

import com.gempukku.polling.LongPollingResource;
import com.gempukku.polling.LongPollingSystem;
import com.gempukku.swccgo.PrivateInformationException;
import com.gempukku.swccgo.SubscriptionExpiredException;
import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
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
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.getUri());
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

        Set<String> users = new TreeSet<String>(new CaseInsensitiveStringComparator());
        for (String userInRoom : usersInRoom) {
            String formattedName = formatPlayerNameForChatList(userInRoom);
            if (!formattedName.isEmpty()) {
                users.add(formattedName);
            }
        }

        for (String userInRoom : users) {
            Element user = doc.createElement("user");
            user.appendChild(doc.createTextNode(userInRoom));
            chatElem.appendChild(user);
        }
    }

    private class CaseInsensitiveStringComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            //put users with specific roles at the top of the list
            if(o1.contains(" ")&&!o2.contains(" ")) {
                return -1;
            }
            if(!o1.contains(" ")&&o2.contains(" ")) {
                return 1;
            }

            //normal sorting for users without specific roles
            if(!o1.contains(" ")&&!o2.contains(" ")) {
                return o1.toLowerCase().compareTo(o2.toLowerCase());
            }

            //replace the symbols with letters to be able to just use a standard compareTo
            String oneWithSubstitutions = o1.replace("*","a").replace("+","b").replace("&beta;","c").replace("&#231;","d").replace(" ","z");
            String twoWithSubstitutions = o2.replace("*","a").replace("+","b").replace("&beta;","c").replace("&#231;","d").replace(" ","z");

            return oneWithSubstitutions.toLowerCase().compareTo(twoWithSubstitutions.toLowerCase());
        }
    }

    private String formatPlayerNameForChatList(String userInRoom) {
        StringBuilder sb = new StringBuilder(userInRoom);

        final Player player = _playerDao.getPlayer(userInRoom);
        if (player != null) {
            final List<Player.Type> playerTypes = Player.Type.getTypes(player.getType());
            if (playerTypes.contains(Player.Type.ADMIN)) {
                sb.insert(0, "* ");
            }
            else {
                if (playerTypes.contains(Player.Type.LEAGUE_ADMIN) || playerTypes.contains(Player.Type.PLAYTESTING_ADMIN)) {
                    sb.insert(0, " ");
                    if (playerTypes.contains(Player.Type.COMMENTATOR)) {
                        sb.insert(0,"&#231;");
                    }
                    if (playerTypes.contains(Player.Type.PLAYTESTER)) {
                        sb.insert(0, "&beta;");
                    }
                    sb.insert(0, "+");
                }
                else {
                    if(playerTypes.contains(Player.Type.PLAYTESTER)||playerTypes.contains(Player.Type.COMMENTATOR)) {
                        sb.insert(0, " ");
                        if (playerTypes.contains(Player.Type.COMMENTATOR)) {
                            sb.insert(0, "&#231;");
                        }
                        if (playerTypes.contains(Player.Type.PLAYTESTER)) {
                            sb.insert(0, "&beta;");
                        }
                    }
                    sb.append(" ");

                    //This was intended to make suspicious similar players stick out.  However, it was built while
                    //the similar-player detection was kneecapped and not including IP address comparison.  Now that
                    //players have been recording the server IP for 5 years instead of their actual IP, automatic detection
                    //like this is all but unusable.

//                    List<Player> similarPlayers = _playerDao.findSimilarAccounts(player);
//                    int count = 1;
//                    for (Player similarPlayer : similarPlayers) {
//                        if (count % 5 == 0) {
//                            sb.append("!!");
//                        }
//                        if (!similarPlayer.hasType(Player.Type.UNBANNED) && similarPlayer.getBannedUntil() == null) {
//                            sb.append("!!!!!");
//                        }
//                        count++;
//                    }
                }
            }
        }
        sb.setLength(Math.min(sb.length(), 40));
        return sb.toString().trim();
    }
}
