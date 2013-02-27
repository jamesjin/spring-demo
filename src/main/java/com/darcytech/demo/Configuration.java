package com.darcytech.demo;

import java.util.Properties;

public class Configuration extends PropsConfig {

    public Configuration(Properties props) {
        super(props);
        if (props == null) {
            throw new IllegalArgumentException("props cannot be null for configuration.");
        }
    }

    public int getPort() {
        return getInteger("port", 7090);
    }

    public int getMaxConnections() {
        return getInteger("maxConnections", 100);
    }

    public String getDelimiter() {
        return getString("delimiter", "\r\n");
    }

    /**
     * The default max line size is 1024
     *
     * @return the max line size
     */
    public int getMaxLineSize() {
        return getInteger("maxLineSize", 1024);
    }

    /**
     * The default queue size is 1024
     *
     * @return the default queue size
     */
    public int getMaxQueueSize() {
        return getInteger("maxQueueSize", 1024);
    }

    public int getConnectionTimeout() {
        return getInteger("connectionTimeout", 3600000);
    }

    public int getUdpPort() {
        return getInteger("udp.port", 7090);
    }

}
