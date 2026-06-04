package com.gempukku.swccgo.async.ws;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Map;

public abstract class AbstractWebSocketSession implements WebSocketSession {
    protected final ChannelHandlerContext _ctx;
    protected final Object _sendLock = new Object();

    protected AbstractWebSocketSession(ChannelHandlerContext ctx) {
        _ctx = ctx;
    }

    protected void sendJson(Map<String, Object> message) {
        sendText(JSON.toJSONString(message));
    }

    protected void sendText(String text) {
        synchronized (_sendLock) {
            _ctx.executor().execute(() -> {
                if (_ctx.channel().isActive()) {
                    _ctx.writeAndFlush(new TextWebSocketFrame(text));
                }
            });
        }
    }

    protected void closeWithReason(int code, String reason) {
        _ctx.writeAndFlush(new CloseWebSocketFrame(code, reason))
                .addListener(ChannelFutureListener.CLOSE);
    }
}
