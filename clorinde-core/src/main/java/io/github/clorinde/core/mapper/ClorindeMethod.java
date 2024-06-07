package io.github.clorinde.core.mapper;

import io.github.clorinde.core.annotation.param.Body;
import io.github.clorinde.core.annotation.param.Header;
import io.github.clorinde.core.annotation.param.PathVariable;
import io.github.clorinde.core.annotation.param.ReqParam;
import io.github.clorinde.core.http.Http;
import io.github.clorinde.core.util.ObjectMapper;
import io.github.clorinde.core.wrapper.ReqWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * @author: laoshiren
 * @date: 2024/05/10 10:30
 **/
@Slf4j
public class ClorindeMethod<T> {

    private ClorindeBindRegistry clorindeBindRegistry;
    private final Class<T> mapperInterface;

    private final Method method;

    private List<Integer> pathVariableIndexCache = null;
    private List<Integer> headerIndexCache = null;
    private List<Integer> paramIndexCache = null;
    private Integer jsonIndexCache = null;


    public ClorindeMethod(Class<T> mapperInterface, Method method, ClorindeBindRegistry clorindeBindRegistry) {
        this.mapperInterface = mapperInterface;
        this.method = method;
        this.clorindeBindRegistry = clorindeBindRegistry;
    }

    public String duel(ReqWrapper wrapper) {
        // 构建路径参数
        log.debug("clorinde start");
        List<Integer> pathIdx = wrapper.buildPath(pathVariableIndexCache);
        log.debug("clorinde build request path {}", wrapper.requestUrl());
        if (this.pathVariableIndexCache == null) {
            this.pathVariableIndexCache = pathIdx;
        }
        // 构建请求头
        List<Integer> headerIdx = wrapper.buildHeader(headerIndexCache);
        log.debug("clorinde build request header {}", wrapper.getHeader());
        if (this.headerIndexCache == null) {
            this.headerIndexCache = headerIdx;
        }
        // 构建请求参数
        List<Integer> paramIdx = wrapper.buildRequestParam(paramIndexCache);
        log.debug("clorinde build request param {}", wrapper.getParam());
        if (this.paramIndexCache == null) {
            this.paramIndexCache = paramIdx;
        }
        // 构建请求body
        Integer jsonIdx = wrapper.buildJsonParam(jsonIndexCache);
        log.debug("clorinde build request json {}", wrapper.getJsonBody());
        if (this.jsonIndexCache == null) {
            this.jsonIndexCache = jsonIdx;
        }

        Http http = this.clorindeBindRegistry.getHttp();
        return http.request(wrapper);
    }


    public Object resultDuel(String json) {
        return clorindeBindRegistry.getResultParse().parser(json, method.getReturnType());
    }



    private String buildJsonParam(Annotation[][] parameterAnnotations, Object[] args) {
        String json = "";
        // 缓存读取
        if (jsonIndexCache != null) {
            Object arg = args[jsonIndexCache];
            return ObjectMapper.obj2Json(arg);
        }

        for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation[] parameterAnnotation = parameterAnnotations[i];
            Body jsonAnno = Arrays.stream(parameterAnnotation)
                    .filter(annotation -> annotation instanceof Body)
                    .map(it -> (Body) it)
                    .findFirst()
                    .orElse(null);
            if (jsonAnno != null) {
                this.jsonIndexCache = (i);
                Object arg = args[i];
                return ObjectMapper.obj2Json(arg);
            }
        }
        return json;
    }

    private List<Pair<String,String>> buildReqParam(Annotation[][] parameterAnnotations, Object[] args) {
        List<Pair<String,String>> list = new ArrayList<>();

        BiConsumer<String, Object> paramConsumer = (requestParam, arg) -> {
            if (arg instanceof Map) {
                Map map = (Map) arg;
                map.forEach((k, v) -> list.add(new MutablePair<>(k.toString(), v.toString())));
                return;
            }
            if (arg instanceof CharSequence) {
                list.add(new MutablePair<>(requestParam, arg.toString()));
                return;
            }

            Map<String, String> objectMap = ObjectMapper.obj2MapString(arg);
            if (!objectMap.isEmpty()) {
                objectMap.forEach((k, v) -> list.add(new MutablePair<>(k, v)));
            }
        };
        // 缓存读取
        if (paramIndexCache != null) {
            if (paramIndexCache.isEmpty()) {
                return new ArrayList<>();
            } else {
                for (Integer index : paramIndexCache) {
                    if (args[index] == null) {
                        continue;
                    }
                    Annotation[] parameterAnnotation = parameterAnnotations[index];
                    ReqParam paramAnno = Arrays.stream(parameterAnnotation)
                            .filter(annotation -> annotation instanceof ReqParam)
                            .map(it -> (ReqParam) it)
                            .findFirst()
                            .orElse(null);
                    if (paramAnno != null) {
                        Object arg = args[index];
                        String headerAnnoValue = paramAnno.value();
                        paramConsumer.accept(headerAnnoValue, arg);
                    }
                }
            }
            return list;
        }

        this.paramIndexCache = new ArrayList<>();

        for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation[] parameterAnnotation = parameterAnnotations[i];
            ReqParam paramAnnotation = Arrays.stream(parameterAnnotation)
                    .filter(annotation -> annotation instanceof ReqParam)
                    .map(it -> (ReqParam) it)
                    .findFirst()
                    .orElse(null);
            if (paramAnnotation != null) {
                this.paramIndexCache.add(i);
                Object arg = args[i];
                String paramValue = paramAnnotation.value();
                paramConsumer.accept(paramValue, arg);
            }
        }
        return list;
    }

    private List<Object> buildParam(Object[] args) {
        List<Object> param = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            if (pathVariableIndexCache.contains(i) || headerIndexCache.contains(i) ||
                    paramIndexCache.contains(i) || jsonIndexCache == i) {
                continue;
            }
            param.add(args[i]);
        }
        return param;
    }

    private String buildPathVariable(String url, Annotation[][] parameterAnnotations, Object[] args) {

        TriFunction<String, String, Object, String> pathConsumer = (pathUrl, pathValue, arg) -> {
            String target = "{" + pathValue + "}";
            if (pathUrl.contains(target)) {
                pathUrl = pathUrl.replace(target, String.valueOf(arg));
            }
            return pathUrl;
        };

        if (pathVariableIndexCache != null) {
            if (pathVariableIndexCache.isEmpty()) {
                return url;
            } else {
                for (Integer idx : pathVariableIndexCache) {
                    Annotation[] parameterAnnotation = parameterAnnotations[idx];
                    PathVariable pathAnno = Arrays.stream(parameterAnnotation)
                            .filter(annotation -> annotation instanceof PathVariable)
                            .map(it -> (PathVariable) it)
                            .findFirst()
                            .orElse(null);
                    if (pathAnno != null) {
                        Object arg = args[idx];
                        String pathValue = pathAnno.value();
                        url = pathConsumer.apply(url, pathValue, arg);
                    }
                }
            }
            return url;
        }

        this.pathVariableIndexCache = new ArrayList<>();

        for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation[] parameterAnnotation = parameterAnnotations[i];
            PathVariable pathAnno = Arrays.stream(parameterAnnotation)
                    .filter(annotation -> annotation instanceof PathVariable)
                    .map(it -> (PathVariable) it)
                    .findFirst()
                    .orElse(null);
            if (pathAnno != null) {
                this.pathVariableIndexCache.add(i);
                Object arg = args[i];
                String pathValue = pathAnno.value();
                url = pathConsumer.apply(url, pathValue, arg);
            }
        }
        return url;
    }


    private Map<String, String> buildHeader(Annotation[][] parameterAnnotations, Object[] args) {
        Map<String, String> header = new LinkedHashMap<>();

        BiConsumer<String, Object> headerConsumer = (headerAnnoValue, arg) -> {
            if (arg instanceof Map) {
                Map map = (Map) arg;
                map.forEach((k, v) -> {
                    header.putIfAbsent(k.toString(), v.toString());
                });
                return;
            }
            if (arg instanceof String) {
                header.putIfAbsent(headerAnnoValue, arg.toString());
                return;
            }

            Map<String, String> objectMap = ObjectMapper.obj2MapString(arg);
            if (!objectMap.isEmpty()) {
                objectMap.forEach(header::putIfAbsent);
            }
        };
        // 缓存读取
        if (headerIndexCache != null) {
            if (headerIndexCache.isEmpty()) {
                return new LinkedHashMap<>();
            } else {
                for (Integer index : headerIndexCache) {
                    if (args[index] == null) {
                        continue;
                    }
                    Annotation[] parameterAnnotation = parameterAnnotations[index];
                    Header headerAnnotation = Arrays.stream(parameterAnnotation)
                            .filter(annotation -> annotation instanceof Header)
                            .map(it -> (Header) it)
                            .findFirst()
                            .orElse(null);
                    if (headerAnnotation != null) {
                        Object arg = args[index];
                        String headerAnnoValue = headerAnnotation.value();
                        headerConsumer.accept(headerAnnoValue, arg);
                    }
                }
            }
            return header;
        }

        this.headerIndexCache = new ArrayList<>();

        for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation[] parameterAnnotation = parameterAnnotations[i];
            Header headerAnnotation = Arrays.stream(parameterAnnotation)
                    .filter(annotation -> annotation instanceof Header)
                    .map(it -> (Header) it)
                    .findFirst()
                    .orElse(null);
            if (headerAnnotation != null) {
                this.headerIndexCache.add(i);
                Object arg = args[i];
                String headerAnnoValue = headerAnnotation.value();
                headerConsumer.accept(headerAnnoValue, arg);
            }
        }
        return header;
    }


}
