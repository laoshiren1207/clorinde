package io.github.clorinde.core.mapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author: laoshiren
 * @date: 2024/05/10 10:25
 **/
public class ClorindeBinder<T> implements InvocationHandler {

    private final Class<T> proxyClass;

    private final ClorindeBindRegistry clorindeBindRegistry;


    public ClorindeBinder(Class<T> proxyClass, ClorindeBindRegistry clorindeBindRegistry) {
        this.proxyClass = proxyClass;
        this.clorindeBindRegistry = clorindeBindRegistry;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass()))
            return method.invoke(this, args);
        return clorindeBindRegistry.getMapperMethod(proxyClass, method).invoke(proxy, method, args);
    }

}
