package com.darcytech.demo;

import java.util.Properties;

public class PropsConfig {

    private Properties props;

    public PropsConfig(Properties props) {
        this.props = props;
    }

    public int getInteger(String key, int defaultValue) {
        int result = defaultValue;
        String text = props.getProperty(key);
        if (text != null) {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException ex) {
            }
        }
        return result;
    }

    public String getString(String key, String defaultValue) {
        String text = props.getProperty(key);
        return text == null ? defaultValue : text;
    }
}
