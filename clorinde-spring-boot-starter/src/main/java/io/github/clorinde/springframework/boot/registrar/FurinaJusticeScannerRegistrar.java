package io.github.clorinde.springframework.boot.registrar;

import io.github.clorinde.springframework.boot.annotation.FurinaScan;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: laoshiren
 * @date: 2024/05/27 17:47
 **/
public class FurinaJusticeScannerRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        //
        AnnotationAttributes cableScanAttrs = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(FurinaScan.class.getName()));
        if (ObjectUtils.isEmpty(cableScanAttrs)) {
            return;
        }
        List<String> basePackages = new ArrayList<>();
        basePackages.addAll(Arrays.stream(cableScanAttrs.getStringArray("basePackages")).filter(StringUtils::hasText)
                .collect(Collectors.toList()));
        ClassPathClorindeRegistrar scanner = new ClassPathClorindeRegistrar(registry);
        String[] bps = new String[basePackages.size()];
        String[] bpArray = basePackages.toArray(bps);
        scanner.doScan(bpArray);
    }

}
