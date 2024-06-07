package io.github.clorinde.core.annotation.param;

import java.lang.annotation.*;

/**
 * @author laoshiren
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface PathVariable {

    String value() default "";
}
