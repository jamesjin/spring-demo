package com.darcytech.demo;

public class TcpEchoRequest extends BaseEchoRequest {

    public TcpEchoRequest(int size, int delay, String seqId) {
        super(size, delay, seqId);
    }

    @Override
    public String getEchoTemplate() {
        return "rspId=%s&ss=";
    }

}
