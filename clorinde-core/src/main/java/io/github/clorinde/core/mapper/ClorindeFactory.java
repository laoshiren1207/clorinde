package io.github.clorinde.core.mapper;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: laoshiren
 * @date: 2024/05/10 10:27
 **/
public class ClorindeFactory <T> {


    private final Class<T> mapperInterface;

    private final ClorindeBindRegistry clorindeBindRegistry;

    private final Map<Method, ClorindeMethodInvoker> clorindeMethodCache = new ConcurrentHashMap<>();

    public ClorindeFactory(Class<T> mapperInterface, ClorindeBindRegistry clorindeBindRegistry) {
        this.mapperInterface = mapperInterface;
        this.clorindeBindRegistry = clorindeBindRegistry;
    }

    public Class<T> getMapperInterface() {
        return mapperInterface;
    }

    public Map<Method, ClorindeMethodInvoker> getMethodCache() {
        return clorindeMethodCache;
    }

    protected T newInstance(ClorindeBinder<T> mapperProxy) {
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, mapperProxy);
    }

    public T newInstance() {
        final ClorindeBinder<T> mapperProxy = new ClorindeBinder<>(mapperInterface, clorindeBindRegistry);
        return newInstance(mapperProxy);
    }

}
