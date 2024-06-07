package io.github.clorinde.core.mapper;

import io.github.clorinde.core.exception.NotInterfaceException;
import io.github.clorinde.core.http.Http;
import io.github.clorinde.core.parser.DefaultResultParser;
import io.github.clorinde.core.parser.ResultParse;
import io.github.clorinde.core.plugin.Interceptor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: laoshiren
 * @date: 2024/05/10 10:28
 **/
@Slf4j
public class ClorindeBindRegistry {


    private final Map<Class<?>, ClorindeFactory<?>> knownProxy = new ConcurrentHashMap<>(64);

    private final Map<Method, ClorindeMethodInvoker> methodCache;

    private final Http http;

    private ResultParse resultParse = new DefaultResultParser();

    private List<Interceptor> interceptor;

    public Http getHttp() {
        return http;
    }

    public void setResultParse(ResultParse resultParse) {
        this.resultParse = resultParse;
    }

    public ResultParse getResultParse() {
        return resultParse;
    }

    public void setInterceptor(List<Interceptor> interceptor) {
        this.interceptor = interceptor;
    }

    public List<Interceptor> getInterceptor() {
        return interceptor;
    }

    public ClorindeBindRegistry(Http http) {
        this.http = http;
        methodCache = new ConcurrentHashMap<>(64);
    }

    public ClorindeBindRegistry(Http http, List<Interceptor> interceptor, ResultParse resultParse) {
        this.http = http;
        this.methodCache = new ConcurrentHashMap<>(64);
        if (interceptor != null) {
            this.interceptor = interceptor;
        }
        if (resultParse != null) {
            this.resultParse = resultParse;
        }
    }

    public <T> T getProxy(Class<T> type) {
        final ClorindeFactory<T> clorindeProxy = (ClorindeFactory<T>) knownProxy.get(type);
        if (clorindeProxy == null) {
            return null;
        }
        return clorindeProxy.newInstance();
    }

    public <T> boolean hasProxy(Class<T> type) {
        return knownProxy.containsKey(type);
    }


    public <T> void addProxy(Class<T> type) throws NotInterfaceException {
        if (type.isInterface()) {
            if (hasProxy(type)) {
                return;
            }
            log.debug("knownProxy add {}", type.getName());
            knownProxy.put(type, new ClorindeFactory<>(type, this));
        } else {
            throw new NotInterfaceException(type.getName() + "非接口, 不能使用");
        }
    }


    public <T> ClorindeMethodInvoker getMapperMethod(Class<T> mapperInterface, Method method) {
        ClorindeMethodInvoker o = methodCache.get(method);
        if (o != null)
            return o;

        ClorindeMethod<T> clorindeMethod = new ClorindeMethod<>(mapperInterface, method, this);
        return methodCache.computeIfAbsent(method, m -> new DefaultMethodInvoker<>(clorindeMethod, this));
    }

}
