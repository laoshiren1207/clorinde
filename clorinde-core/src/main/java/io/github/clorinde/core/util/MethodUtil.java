package io.github.clorinde.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author: laoshiren
 * @date: 2024/05/27 10:43
 **/
public class MethodUtil {

    public static boolean isAnnotationPresent(Method method, Class<? extends Annotation> anno){
        return method.isAnnotationPresent(anno);
    }

}
