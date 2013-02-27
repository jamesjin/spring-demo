package com.darcytech.demo;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public abstract class URLQueryDecoder extends SimpleChannelUpstreamHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Charset charset = Charset.forName("UTF-8");

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object m = e.getMessage();
        if (!(m instanceof ChannelBuffer)) {
            ctx.sendUpstream(e);
            return;
        }
        String line = ((ChannelBuffer) m).toString(charset).trim();
        if (line.isEmpty()) {
            logger.warn("Get an empty line.");
        } else {
            logger.info("Get line {} ", line);
            Object obj = decode(new PropsConfig(URLEncodedUtils.parse(line)));
            if (obj != null) {
                Channels.fireMessageReceived(ctx, obj, e.getRemoteAddress());
            } else {
                logger.error("Cannot decode line {}", line);
            }
        }
    }

    protected abstract Object decode(PropsConfig props);

}
