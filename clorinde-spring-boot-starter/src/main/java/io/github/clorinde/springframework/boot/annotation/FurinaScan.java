package io.github.clorinde.springframework.boot.annotation;

import io.github.clorinde.springframework.boot.configure.ClorindeAutoConfiguration;
import io.github.clorinde.springframework.boot.registrar.FurinaJusticeScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author: laoshiren
 * @date: 2024/05/27 17:27
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({ClorindeAutoConfiguration.class, FurinaJusticeScannerRegistrar.class})
public @interface FurinaScan {

    String[] basePackages() default {};
}