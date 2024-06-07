package io.github.clorinde.springframework.boot.configure;

import io.github.clorinde.core.http.Http;
import io.github.clorinde.core.http.okhttp.OkHttp;
import io.github.clorinde.core.mapper.ClorindeBindRegistry;
import io.github.clorinde.springframework.boot.properties.ClorindeConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: laoshiren
 * @date: 2024/05/27 17:29
 **/
@Configuration
@EnableConfigurationProperties(value = ClorindeConfigurationProperties.class)
public class ClorindeAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public Http clorindeOkHttp(ClorindeConfigurationProperties properties) {
        return OkHttp.instance(properties.getReadTimeout(), properties.getConnectTimeout(), properties.getWriteTimeout());
    }

    @Bean
    @ConditionalOnMissingBean
    public ClorindeBindRegistry clorindeBinderRegistry(Http clorindeOkHttp) {
        return new ClorindeBindRegistry(clorindeOkHttp);
    }

}
