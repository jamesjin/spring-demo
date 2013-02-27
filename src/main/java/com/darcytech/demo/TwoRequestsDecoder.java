package com.darcytech.demo;

import java.util.Arrays;

public class TwoRequestsDecoder extends URLQueryDecoder {

    @Override
    protected Object decode(PropsConfig props) {
        Object result = null;
        String id = props.getString("id", "");
        int type = props.getInteger("t", 1);
        int size1 = props.getInteger("s1", 0);
        if (type == 1) {
            result = Arrays.asList(new BaseEchoRequest(size1, id));
        } else {
            int delay = props.getInteger("d", 0);
            int size2 = props.getInteger("s2", 0);
            result = Arrays.asList(new BaseEchoRequest(size1, 0, id + "-1"), new BaseEchoRequest(size2, delay, id + "-2"));
        }
        return result;
    }

}
