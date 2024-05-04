package com.gempukku.swccgo.async;

import com.gempukku.polling.LongPollingSystem;
import com.gempukku.swccgo.async.handler.RootUriRequestHandler;
import com.gempukku.swccgo.common.ApplicationConfiguration;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * This is the "main" method of the GEMP-SWCCG application.
 * It will be built into web.jar by a maven build.
 *
 * Example command to run GEMP-SWCCG:
 * sudo java -Xmx4g -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Dlog4j.debug -Dlog4j.configuration=test-log4j.xml -cp web.jar com.gempukku.swccgo.async.SwccgoAsyncServer >>gemp-swccg_output.txt 2>&1 &
 */
public class SwccgoAsyncServer {
    public static void main(String[] server) throws InterruptedException {
        int httpPort = Integer.parseInt(ApplicationConfiguration.getProperty("port"));

        //Use this to halt gemp on startup long enough for the debugger to attach.
        //DO NOT commit this line to the main gemp uncommented.
        //Thread.sleep(10_000);

        GempukkuServer gempukkuServer = new GempukkuServer();

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            LongPollingSystem longPollingSystem = new LongPollingSystem();
            longPollingSystem.start();

            RootUriRequestHandler uriRequestHandler = new RootUriRequestHandler(gempukkuServer.getContext(), longPollingSystem);

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new HttpObjectAggregator(Short.MAX_VALUE));
                            pipeline.addLast(new HttpContentCompressor());
                            pipeline.addLast(new SwccgoHttpRequestHandler(gempukkuServer.getContext(),
                                    uriRequestHandler));
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true);

            ChannelFuture bind = b.bind(httpPort);
            bind.sync().channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
