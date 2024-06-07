package io.github.clorinde.core.mapper;

import io.github.clorinde.core.plugin.Plugins;
import io.github.clorinde.core.wrapper.ReqWrapper;

import java.lang.reflect.Method;

/**
 * @author: laoshiren
 * @date: 2024/05/09 20:05
 **/
public class DefaultMethodInvoker<T> implements ClorindeMethodInvoker {

    private final ClorindeMethod<T> clorindeMethod;

    private final ClorindeBindRegistry clorindeBindRegistry;


    public DefaultMethodInvoker(ClorindeMethod<T> clorindeMethod, ClorindeBindRegistry clorindeBindRegistry) {
        super();
        this.clorindeMethod = clorindeMethod;
        this.clorindeBindRegistry = clorindeBindRegistry;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 构建参数
        ReqWrapper reqWrapper = ReqWrapper.newInstance(method, args);
        String result = Plugins.wrap(clorindeBindRegistry.getInterceptor(), proxy, reqWrapper, clorindeMethod::duel);
        return clorindeMethod.resultDuel(result);
    }
}
