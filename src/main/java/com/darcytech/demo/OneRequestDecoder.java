package com.darcytech.demo;

import java.util.Arrays;

public class OneRequestDecoder extends URLQueryDecoder {

    @Override
    protected Object decode(PropsConfig props) {
        int delay = props.getInteger("delay", 0);
        int size = props.getInteger("size", 0);
        String reqId = props.getString("reqId", "");
        return Arrays.asList(new TcpEchoRequest(size, delay, reqId));
    }

}
