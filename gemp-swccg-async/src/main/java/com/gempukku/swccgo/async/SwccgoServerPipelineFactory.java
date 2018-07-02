package com.gempukku.swccgo.async;

import com.gempukku.polling.LongPollingSystem;
import com.gempukku.swccgo.async.handler.RootUriRequestHandler;
import com.gempukku.swccgo.async.handler.UriRequestHandler;
import com.gempukku.swccgo.builder.DaoBuilder;
import com.gempukku.swccgo.builder.PackagedProductStorageBuilder;
import com.gempukku.swccgo.builder.ServerBuilder;
import com.gempukku.swccgo.service.LoggedUserHolder;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SwccgoServerPipelineFactory implements ChannelPipelineFactory {
    private Map<Type, Object> _context;
    private UriRequestHandler _uriRequestHandler;
    private SwccgoHttpRequestHandler _swccgoHttpRequestHandler;

    public SwccgoServerPipelineFactory() {
        Map<Type, Object> objects = new HashMap<Type, Object>();

        LoggedUserHolder loggedUserHolder = new LoggedUserHolder();
        loggedUserHolder.start();
        objects.put(LoggedUserHolder.class, loggedUserHolder);

        LongPollingSystem longPollingSystem = new LongPollingSystem();
        longPollingSystem.start();
        objects.put(LongPollingSystem.class, longPollingSystem);

        DaoBuilder.fillObjectMap(objects);
        PackagedProductStorageBuilder.fillObjectMap(objects);
        ServerBuilder.fillObjectMap(objects);
        ServerBuilder.constructObjects(objects);

        _context = objects;
        _uriRequestHandler = new RootUriRequestHandler(_context);

        _swccgoHttpRequestHandler = new SwccgoHttpRequestHandler(_context, _uriRequestHandler);
    }

    public ChannelPipeline getPipeline() throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = Channels.pipeline();

        // Uncomment the following line if you want HTTPS
        //SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
        //engine.setUseClientMode(false);
        //pipeline.addLast("ssl", new SslHandler(engine));

        pipeline.addLast("decoder", new HttpRequestDecoder());
        // Uncomment the following line if you don't want to handle HttpChunks.
        //pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        // Remove the following line if you don't want automatic content compression.
        pipeline.addLast("deflater", new HttpContentCompressor());

        pipeline.addLast("handler", _swccgoHttpRequestHandler);
        return pipeline;
    }
}
