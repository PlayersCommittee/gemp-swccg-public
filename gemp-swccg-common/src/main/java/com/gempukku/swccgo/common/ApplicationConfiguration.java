package com.gempukku.swccgo.common;

import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * Provides access to the Gemp-Swccg properties file, which is used for application
 * configuration options.
 */
public class ApplicationConfiguration {
    private static Logger LOGGER = Logger.getLogger(ApplicationConfiguration.class);
    private static Properties _properties;

    private static synchronized Properties getProperties() {
        if (_properties == null) {
            Properties props = new Properties();
            try {
                props.load(ApplicationConfiguration.class.getResourceAsStream("/gemp-swccg.properties"));
                String gempPropertiesOverride = System.getProperty("gemp-swccg.override");
                if (gempPropertiesOverride != null)
                    props.load(ApplicationConfiguration.class.getResourceAsStream(gempPropertiesOverride));
                _properties = props;
            } catch (Exception exp) {
                LOGGER.error("Can't load application configuration", exp);
                throw new RuntimeException("Unable to load application configuration", exp);
            }
        }
        return _properties;
    }

    public static String getProperty(String property) {
        return getProperties().getProperty(property);
    }
}
