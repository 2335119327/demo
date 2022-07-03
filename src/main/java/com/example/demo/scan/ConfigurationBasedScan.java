package com.example.demo.scan;

import com.example.demo.annotation.Action;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dong
 * @date 2022/5/12 22:00
 */
@Slf4j
@Component
public class ConfigurationBasedScan {

    /**
     * spring配置文件配置扫描路径
     */
    @Value("${action.path}")
    private String path;

    private final SimpleMetadataReaderFactory simpleMetadataReaderFactory = new SimpleMetadataReaderFactory();

    /**
     * 扫描指定注解
     * @param targetType 目标注解类型
     */
    public void scanTargetAnnotation(Class<?> targetType) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = new Resource[0];
        try {
            path = path.replace(".", "/");
            String locationPattern = StringUtils.join("classpath*:", path, "/**/*.class");
            resources = resolver.getResources(locationPattern);
        } catch (IOException e) {
            log.error("未找到", e);
        }
        for (Resource resource : resources) {
            // 先获取resource的元信息，然后获取class元信息，最后得到 class 全路径
            String clsName = null;
            try {
                clsName = simpleMetadataReaderFactory.getMetadataReader(resource).getClassMetadata().getClassName();
                // 通过名称加载
                Class<?> aClass = Class.forName(clsName);
                scanTargetAnnotation(aClass, targetType);
            } catch (IOException | ClassNotFoundException e) {
                // todo
                log.error("", e);
            }
        }

    }

    /**
     * 扫描指定类，指定注解
     * @param aClass 指定类
     * @param targetType 指定注解
     */
    public void scanTargetAnnotation(Class<?> aClass, Class<?> targetType) {

        if (aClass == null) {
            return;
        }

        Method[] methods = aClass.getMethods();

        if (methods.length == 0) {
            return;
        }

        for (Method method : methods) {
            Annotation[] methodAnnotations = method.getAnnotations();

            if (methodAnnotations == null) {
                continue;
            }

            scanMethodAnnotation(methodAnnotations, method, targetType);
        }

    }

    /**
     * 扫描方法上的注解
     *
     * @param methodAnnotations 方法上注解集合
     * @param method            方法
     * @param targetType        目标注解类型
     */
    private void scanMethodAnnotation(Annotation[] methodAnnotations, Method method, Class<?> targetType) {
        for (Annotation methodAnnotation : methodAnnotations) {
            Annotation[] annotations = methodAnnotation.annotationType().getAnnotations();

            if (annotations.length == 0) {
                continue;
            }

            scanInsideAnnotation(annotations, method, targetType);
        }
    }

    /**
     * 扫描注解嵌套的注解
     *
     * @param annotations 嵌套的注解集合
     * @param method      方法
     * @param targetType  目标注解类型
     */
    private void scanInsideAnnotation(Annotation[] annotations, Method method, Class<?> targetType) {
        for (Annotation annotation : annotations) {

            if (annotation == null) {
                continue;
            }

            if (annotation.annotationType().equals(targetType)) {
                System.out.println(method.getName());
                break;
            }
        }
    }

}
