package io.github.clorinde.core.annotation.param;

import java.lang.annotation.*;

/**
 * @author: laoshiren
 * @date: 2024/05/10 16:55
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ReqParam {

    String value() default "";
}
