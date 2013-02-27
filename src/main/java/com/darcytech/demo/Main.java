package com.darcytech.demo;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    public static void main(String[] args) {
        final Configuration config = new Configuration(System.getProperties());
        final Sweeper sweeper = new Sweeper(config.getMaxConnections());
        ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
        ServerBootstrap bootstrap = new ServerBootstrap(factory);
        final OnDemandHandler onDemandHandler = new OnDemandHandler(scheduledExecutor, config.getMaxQueueSize());
        ChannelPipelineFactory pipelineFactory = new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
                return Channels.pipeline(sweeper, buildFrameDecoder(config), new OneRequestDecoder(), onDemandHandler);
            }
        };
        bootstrap.setPipelineFactory(pipelineFactory);
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
        Channel channel = bootstrap.bind(new InetSocketAddress(config.getPort()));
        logger.info("TCP Server started and listening on " + channel.getLocalAddress());

        final ChannelPipeline pipeline = Channels.pipeline(new TwoRequestsDecoder(), onDemandHandler);
        ConnectionlessBootstrap udpBoot = new ConnectionlessBootstrap(new NioDatagramChannelFactory());
        ChannelPipelineFactory udpPipelineFactory = new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                return pipeline;
            }
        };
        udpBoot.setPipelineFactory(udpPipelineFactory);
        Channel udpChannel = udpBoot.bind(new InetSocketAddress(config.getUdpPort()));
        logger.info("UDP Server started and listening on " + udpChannel.getLocalAddress());
    }

    private static FrameDecoder buildFrameDecoder(Configuration config) {
        int maxLineSize = config.getMaxLineSize();
        ChannelBuffer delimiter = ChannelBuffers.copiedBuffer(config.getDelimiter(), Charset.forName("UTF-8"));
        return new DelimiterBasedFrameDecoder(maxLineSize, delimiter);
    }

}
