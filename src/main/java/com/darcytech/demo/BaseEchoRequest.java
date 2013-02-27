package com.darcytech.demo;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class BaseEchoRequest implements EchoRequest {

    public static final int MAX_SIZE = 1024 * 1024 * 4;

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseEchoRequest.class);

    private final String seqId;

    private final int size;

    private final int delay;

    public BaseEchoRequest(int size, int delay, String seqId) {
        if (size > MAX_SIZE) {
            LOGGER.warn("Too big size {}, {} is used instead.", size, MAX_SIZE);
            size = MAX_SIZE;
        }
        this.size = size;
        this.seqId = seqId;
        this.delay = delay;
    }

    public BaseEchoRequest(int size, String seqId) {
        this.size = size;
        this.seqId = seqId;
        this.delay = 0;
    }

    public int getDelay() {
        return delay;
    }

    public String getSeqId() {
        return seqId;
    }

    public int getSize() {
        return size;
    }

    public ChannelBuffer getEchoContent() {
        String template = getEchoTemplate();
        String seqId = getSeqId();
        int size = getSize();
        ChannelBuffer channelBuffer = ChannelBuffers.buffer(size + template.length() + seqId.length());
        channelBuffer.writeBytes(String.format(template, getSeqId()).getBytes(Charset.forName("UTF-8")));
        for (int i = 0; i < size; i++) {
            channelBuffer.writeByte('a');
        }
        channelBuffer.writeByte('\n');
        channelBuffer.writeByte('\r');
        return channelBuffer;
    }

    protected String getEchoTemplate() {
        return "id=%s&p=";
    }

}
