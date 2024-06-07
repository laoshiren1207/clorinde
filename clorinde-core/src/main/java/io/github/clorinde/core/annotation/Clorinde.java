package io.github.clorinde.core.annotation;

import java.lang.annotation.*;

/**
 * @author: laoshiren
 * @date: 2024/05/10 10:39
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Clorinde {

    String host() default "";

    String value() default "";
}
