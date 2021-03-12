package com.gempukku.swccgo.common;

import org.apache.log4j.Logger;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
                    props.load(new java.io.FileInputStream(gempPropertiesOverride));
                _properties = props;
            } catch (Exception exp) {
                LOGGER.error("Unable to load application configuration properties file", exp);
                throw new RuntimeException("Unable to load application configuration properties file", exp);
            }
        }
        return _properties;
    }

    public static String getProperty(String property) {


        String value = getProperties().getProperty(property);

        if (!"db.connection.url".equals(property) && null == value) { return null; }

        //
        // \w  Match a "word" character 
        //     (alphanumeric plus "_", 
        //      plus other connector punctuation chars 
        //      plus Unicode marks)
        //

        System.out.println("Property.....: " + property);

        if ("db.connection.url".equals(property)) {
          String db_hostname = getProperty("db.connection.hostname");
          String db_dbname   = getProperty("db.connection.dbname");
          String db_url      = "jdbc:mysql://"+db_hostname+"/"+db_dbname;
          System.out.println("DB Hostname..: " + db_hostname );
          System.out.println("DB Name......: " + db_dbname );
          System.out.println("DB URL.......: " + db_url );
          return db_url;
        } else {

          Pattern p = Pattern.compile("\\$\\{(\\w+):+\\-([\\w-\\/]+)\\}|\\$(\\w+)");
          Matcher m = p.matcher(value);
          StringBuffer sb = new StringBuffer();
          String propertyOut = value;

          String envVarName    = null;
          String envVarDefault = null;
          String envVarValue   = null;

          while (m.find()) {
            envVarName    = null == m.group(1) ? m.group(3) : m.group(1);
            envVarDefault = null == m.group(2) ? m.group(3) : m.group(2);
            envVarValue   = System.getenv(envVarName);

            if (envVarValue != null) {
              propertyOut = envVarValue;
            } else if (envVarDefault != null) {
              propertyOut = envVarDefault;
            }

          } // while

          return propertyOut;
      } // if else

    } // public static String getProperty(String property)

} // public class ApplicationConfiguration















