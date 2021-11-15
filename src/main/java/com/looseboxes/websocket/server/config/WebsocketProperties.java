package com.looseboxes.websocket.server.config;

import java.util.Arrays;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author chinomso ikwuagwu
 */
@ConfigurationProperties(prefix = "looseboxes.websocket", ignoreUnknownFields = false)
public class WebsocketProperties {

    private String[] allowedOrigins;
    
    private String[] applicationDestinationPrefixes;

    private String userDestinationPrefix;
    
    public String[] getApplicationEndpointsForSuffix(String suffix) {
        return getEndpointsForSuffix(applicationDestinationPrefixes, suffix);
    }

    public String getUserEndpointForSuffix(String suffix) {
        return composeEndpoint(userDestinationPrefix, suffix);
    }

    public String[] getEndpointsForSuffix(String [] endpoints, String suffix) {
        String [] result = new String[endpoints.length];
        for(int i=0; i<endpoints.length; i++) {
            result[i] = composeEndpoint(endpoints[i], suffix);
        }
        return result;
    }

    private String composeEndpoint(String main, String suffix) {
        return main + suffix;
    }

    public String[] getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(String[] allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public String[] getApplicationDestinationPrefixes() {
        return applicationDestinationPrefixes;
    }

    public void setApplicationDestinationPrefixes(String[] applicationDestinationPrefixes) {
        this.applicationDestinationPrefixes = applicationDestinationPrefixes;
    }

    public String getUserDestinationPrefix() {
        return userDestinationPrefix;
    }

    public void setUserDestinationPrefix(String userDestinationPrefix) {
        this.userDestinationPrefix = userDestinationPrefix;
    }

    @Override
    public String toString() {
        return "WebsocketProperties{" +
                "allowedOrigins=" + Arrays.toString(allowedOrigins) +
                ", applicationDestinationPrefixes=" + Arrays.toString(applicationDestinationPrefixes) +
                ", userDestinationPrefix='" + userDestinationPrefix + '\'' +
                '}';
    }
}
