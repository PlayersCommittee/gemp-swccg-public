package com.gempukku.swccgo.async;

import com.gempukku.swccgo.common.ApplicationConfiguration;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This is the "main" method of the GEMP-SWCCG application.
 * It will be built into web.jar by a maven build.
 *
 * Example command to run GEMP-SWCCG:
 * sudo java -Xmx4g -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Dlog4j.debug -Dlog4j.configuration=test-log4j.xml -cp web.jar com.gempukku.swccgo.async.SwccgoAsyncServer >>gemp-swccg_output.txt 2>&1 &
 */
public class SwccgoAsyncServer {
    public static void main(String[] server) {
        ChannelFactory factory =
                new NioServerSocketChannelFactory(
                        new ThreadPoolExecutor(10, Integer.MAX_VALUE,
                                60L, TimeUnit.SECONDS,
                                new SynchronousQueue<Runnable>()),
                        new ThreadPoolExecutor(30, Integer.MAX_VALUE,
                                60L, TimeUnit.SECONDS,
                                new SynchronousQueue<Runnable>()));

        ServerBootstrap bootstrap = new ServerBootstrap(factory);
        bootstrap.setPipelineFactory(new SwccgoServerPipelineFactory());
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
        bootstrap.bind(new InetSocketAddress(Integer.parseInt(ApplicationConfiguration.getProperty("port"))));
    }
}
