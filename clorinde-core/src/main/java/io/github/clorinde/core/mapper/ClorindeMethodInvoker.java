package io.github.clorinde.core.mapper;

import java.lang.reflect.Method;

/**
 * @author: laoshiren
 * @date: 2024/05/10 10:29
 **/
public interface ClorindeMethodInvoker {


    Object invoke(Object target, Method method, Object[] args) throws Throwable;

}

