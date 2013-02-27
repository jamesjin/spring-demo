package com.darcytech.demo;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.nio.charset.Charset;

public class DefaultTimerEchoTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTimerEchoTask.class);

    private final Channel channel;

    private final SocketAddress remoteAddress;

    private final EchoRequest request;

    public DefaultTimerEchoTask(Channel channel, SocketAddress remoteAddress, EchoRequest request) {
        this.channel = channel;
        this.remoteAddress = remoteAddress;
        this.request = request;
    }

    @Override
    public void run() {
        ChannelBuffer echoContent = request.getEchoContent();
        LOGGER.info("echo to {}, content: {}", remoteAddress, echoContent.toString(Charset.forName("UTF-8")));
        channel.write(echoContent, remoteAddress);
    }

}
