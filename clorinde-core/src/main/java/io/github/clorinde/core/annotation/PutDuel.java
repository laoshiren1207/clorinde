package io.github.clorinde.core.annotation;

import java.lang.annotation.*;

/**
 * @author: laoshiren
 * @date: 2024/05/10 10:43
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PutDuel {

    String url() default "";

    String value() default "";
}
