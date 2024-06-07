package io.github.clorinde.springframework.boot.registrar;

import io.github.clorinde.core.annotation.Clorinde;
import io.github.clorinde.springframework.ClorindeRegistryFactoryBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author laoshiren
 * @date 2023/4/19-13:23
 */
public class ClassPathClorindeRegistrar extends ClassPathBeanDefinitionScanner {

    private static final Log logger = LogFactory.getLog(ClassPathClorindeRegistrar.class);

    private final PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
    private final CachingMetadataReaderFactory readerFactory = new CachingMetadataReaderFactory();
    private final AnnotationBeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

    public ClassPathClorindeRegistrar(BeanDefinitionRegistry registry) {
        super(registry);
    }

    public void registerFilters() {
        addIncludeFilter((metadataReader, metadataReaderFactory) -> metadataReader.getAnnotationMetadata().hasAnnotation(Clorinde.class.getName()));
    }


    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().hasAnnotation(Clorinde.class.getName());
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String[] basePackages) {
        return Arrays.stream(basePackages)
                .map(it -> {
                    Resource[] resources = null;
                    try {
                        it = it.replaceAll("\\.","/");
                        it = "classpath:" + it + "/**/*.class";
                        resources = patternResolver.getResources(it);
                    } catch (IOException e) {
                    }
                    return resources;
                })
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .peek(logger::info)
                .map(this::createBeanDefinition)
                .filter(Objects::nonNull)
                .peek(logger::info)
                .collect(Collectors.toSet());
    }

    private BeanDefinitionHolder createBeanDefinition(Resource resource) {
        BeanDefinitionHolder definitionHolder;
        try {
            MetadataReader metadataReader = readerFactory.getMetadataReader(resource);
            ClassMetadata classMetadata = metadataReader.getClassMetadata();
            if (!classMetadata.isInterface()) {
                return null;
            }
            AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
            boolean hasMapper = annotationMetadata.hasAnnotation(Clorinde.class.getName());
            if (!hasMapper) {
                return null;
            }
            AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder
                    .genericBeanDefinition(ClorindeRegistryFactoryBean.class)
                    // 添加构造方法的值
                    .addConstructorArgValue(classMetadata.getClassName())
                    .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE)
                    .getBeanDefinition();
            AbstractBeanDefinition beanNameDefinition = BeanDefinitionBuilder
                    .genericBeanDefinition(classMetadata.getClassName())
                    .getBeanDefinition();
            String beanName = beanNameGenerator.generateBeanName(beanNameDefinition, getRegistry());
            definitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
            getRegistry().registerBeanDefinition(beanName, beanDefinition);
            return definitionHolder;
        } catch (IOException e) {
            return null;
        }
    }
}