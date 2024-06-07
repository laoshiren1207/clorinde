package io.github.clorinde.springframework.boot.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: laoshiren
 * @date: 2024/05/27 17:42
 **/
@ConfigurationProperties(prefix = "clorinde")
public class ClorindeConfigurationProperties {

    private Integer readTimeout;
    private Integer connectTimeout;
    private Integer writeTimeout;

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(Integer writeTimeout) {
        this.writeTimeout = writeTimeout;
    }
}
