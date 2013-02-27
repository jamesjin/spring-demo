package com.darcytech.demo;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Sweeper extends SimpleChannelHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final int maxConnections;

    private final Map<Channel, Long> connections;

    public Sweeper(int maxConnections) {
        this.maxConnections = maxConnections;
        this.connections = new ConcurrentHashMap<Channel, Long>();
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        Channel channel = ctx.getChannel();
        LOGGER.info("channel {} connected.", channel.getRemoteAddress());
        updateLastAccess(channel);
        super.channelConnected(ctx, e);
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        Channel channel = ctx.getChannel();
        connections.remove(channel);
        LOGGER.info("channel {} closed.", channel.getRemoteAddress());
        super.channelClosed(ctx, e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Channel channel = ctx.getChannel();
        LOGGER.debug("channel {} received or write.", channel.getRemoteAddress());
        updateLastAccess(channel);
        super.messageReceived(ctx, e);
    }

    private void updateLastAccess(Channel channel) {
        connections.put(channel, System.currentTimeMillis());
        removeConnectionByLRU();
    }

    private void removeConnectionByLRU() {
        while (connections.size() > maxConnections) {
            Channel oldestChannel = null;
            long oldest = Long.MAX_VALUE;
            for (Map.Entry<Channel, Long> entry : connections.entrySet()) {
                if (entry.getValue() < oldest) {
                    oldestChannel = entry.getKey();
                    oldest = entry.getValue();
                }
            }
            if (oldestChannel != null) {
                connections.remove(oldestChannel);
            }
        }
    }

}
