package io.github.clorinde.core.plugin;

import io.github.clorinde.core.wrapper.ReqWrapper;

/**
 * @author: laoshiren
 * @date: 2024/05/11 11:57
 **/
public interface Interceptor {

    void before(Object proxy, ReqWrapper wrapper);

    void after(Object proxy, ReqWrapper wrapper, Object result);
}
