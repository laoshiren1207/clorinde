package io.github.clorinde.core.plugin;

import com.google.common.base.Stopwatch;
import io.github.clorinde.core.wrapper.ReqWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author: laoshiren
 * @date: 2024/05/11 13:10
 **/
@Slf4j
public class Plugins {

    public static void wrapBefore(List<Interceptor> interceptors, Object proxy, ReqWrapper reqWrapper) {
        if (interceptors==null || interceptors.isEmpty()) {
            return;
        }
        interceptors.forEach(interceptor -> interceptor.before(proxy, reqWrapper));
    }

    public static void wrapAfter(List<Interceptor> interceptors, Object proxy, ReqWrapper reqWrapper, Object result) {
        if (interceptors==null || interceptors.isEmpty()) {
            return;
        }
        interceptors.forEach(interceptor -> interceptor.after(proxy, reqWrapper, result));
    }

    public static String wrap(List<Interceptor> interceptors, Object proxy, ReqWrapper reqWrapper, Function<ReqWrapper, String> function) {
        wrapBefore(interceptors, proxy, reqWrapper);
        log.debug("request interceptor before finished");
        Stopwatch started = Stopwatch.createStarted();
        String apply = function.apply(reqWrapper);
        log.debug("request result {}", apply);
        started.stop();
        log.debug("request cost {}", started.elapsed(TimeUnit.MILLISECONDS));
        wrapAfter(interceptors, proxy, reqWrapper, apply);
        log.debug("request interceptor before finished");
        return apply;
    }

}
