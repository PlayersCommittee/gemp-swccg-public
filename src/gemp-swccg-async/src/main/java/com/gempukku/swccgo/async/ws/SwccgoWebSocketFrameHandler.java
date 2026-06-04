package com.gempukku.swccgo.async.ws;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

/**
 * Thin Netty frame adapter:
 * - websocket control frames (close/ping) are handled at transport level,
 * - text frames are forwarded to channel-specific session logic.
 */
public class SwccgoWebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private final WebSocketSession _session;
    private final WebSocketServerHandshaker _handshaker;

    public SwccgoWebSocketFrameHandler(WebSocketSession session, WebSocketServerHandshaker handshaker) {
        _session = session;
        _handshaker = handshaker;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        _session.onClose();
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        // Standard websocket close handshake.
        if (frame instanceof CloseWebSocketFrame) {
            _session.onClose();
            _handshaker.close(ctx.channel(), ((CloseWebSocketFrame) frame).retain());
            return;
        }
        // Keepalive for proxies/load balancers.
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // Application payloads are always JSON text frames in this module.
        if (frame instanceof TextWebSocketFrame) {
            _session.onTextMessage(((TextWebSocketFrame) frame).text());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        _session.onClose();
        ctx.close();
    }
}
