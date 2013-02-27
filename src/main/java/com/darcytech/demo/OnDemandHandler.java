package com.darcytech.demo;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class OnDemandHandler extends SimpleChannelHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OnDemandHandler.class);
    private final ScheduledExecutorService scheduledExecutor;
    private final int maxQueueSize;
    private final BlockingQueue<Runnable> queue;

    public OnDemandHandler(ScheduledExecutorService scheduledExecutor, int maxQueueSize) {
        this.scheduledExecutor = scheduledExecutor;
        this.maxQueueSize = maxQueueSize;
        if (scheduledExecutor instanceof ScheduledThreadPoolExecutor) {
            queue = ((ScheduledThreadPoolExecutor) scheduledExecutor).getQueue();
        } else {
            queue = null;
        }
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        List<EchoRequest> requests = (List<EchoRequest>) e.getMessage();
        if (queue != null && queue.size() < maxQueueSize) {
            for (EchoRequest request : requests) {
                DefaultTimerEchoTask echoTask = new DefaultTimerEchoTask(e.getChannel(), e.getRemoteAddress(), request);
                scheduledExecutor.schedule(echoTask, request.getDelay(), TimeUnit.MILLISECONDS);
            }
        } else {
            LOGGER.error("Drop this request as there are too many {}", queue.size());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        LOGGER.error("Unexpected exception caught.", e.getCause());
        Channel channel = ctx.getChannel();
        if (channel instanceof SocketChannel) {
            LOGGER.info("Close the socket channel {} due to the exception.", channel);
            channel.close();
        }
    }

}
