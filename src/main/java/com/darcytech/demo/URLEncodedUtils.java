package com.darcytech.demo;

import java.util.Properties;

public class URLEncodedUtils {
    public static Properties parse(String line) {
        Properties props = new Properties();
        String[] params = line.split("&");
        for (String param : params) {
            param = param.trim();
            String[] pair = param.split("=");
            if (pair.length == 2) {
                props.put(pair[0], pair[1]);
            } else {
                throw new IllegalArgumentException("Request line with incorrect format: " + line);
            }
        }
        return props;
    }
}
