package io.github.clorinde.springframework;

import io.github.clorinde.core.exception.NotInterfaceException;
import io.github.clorinde.core.mapper.ClorindeBindRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author: laoshiren
 * @date: 2024/05/17 11:32
 **/
public class ClorindeRegistryFactoryBean<T> implements FactoryBean<T>, ApplicationContextAware {

    private final Class<T> mapperInterface;

    private ClorindeBindRegistry clorindeBindRegistry;


    public ClorindeRegistryFactoryBean(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    @Override
    public void setApplicationContext(ApplicationContext ac) {
        this.clorindeBindRegistry = ac.getBean(ClorindeBindRegistry.class);
        try {
            clorindeBindRegistry.addProxy(mapperInterface);
        } catch (NotInterfaceException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T getObject() throws Exception {
        return clorindeBindRegistry.getProxy(mapperInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return mapperInterface;
    }
}
