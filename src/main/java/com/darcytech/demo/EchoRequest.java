package com.darcytech.demo;

import org.jboss.netty.buffer.ChannelBuffer;

public interface EchoRequest {

    public int getDelay();

    public ChannelBuffer getEchoContent();

}
