package org.indrio.pingfm.util;

import java.io.IOException;
import java.util.Properties;

public class SettingProperties {
    private static final String APP_NAME = "app.name";
    private static final String APP_VERSION = "app.version";
    private static final String API_VERSION = "api.version";
    private static final String API_LOCATION_HOST = "api.location.host";
    private static final String API_DEVELOPER_KEY = "api.developer.key";
    private static final String API_APPLICATION_KEY = "api.application.key";
    private static Properties prop = null;

    static {
        prop = new Properties();

        try {
            prop.load(SettingProperties.class.getResourceAsStream("/setting.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getAppName() {
        return prop.getProperty(APP_NAME);
    }

    public static String getAppVersion() {
        return prop.getProperty(APP_VERSION);
    }

    public static String getApiVersion() {
        return prop.getProperty(API_VERSION);
    }

    public static String getLocationHost() {
        return prop.getProperty(API_LOCATION_HOST);
    }

    public static String getDeveloperKey() {
        return prop.getProperty(API_DEVELOPER_KEY);
    }

    public static String getApplicationKey() {
        return prop.getProperty(API_APPLICATION_KEY);
    }
}
