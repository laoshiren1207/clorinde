package io.github.clorinde.core.wrapper;

import cn.hutool.core.collection.CollUtil;
import io.github.clorinde.core.annotation.*;
import io.github.clorinde.core.annotation.param.Body;
import io.github.clorinde.core.annotation.param.Header;
import io.github.clorinde.core.annotation.param.PathVariable;
import io.github.clorinde.core.annotation.param.ReqParam;
import io.github.clorinde.core.constant.DuelType;
import io.github.clorinde.core.util.MethodUtil;
import io.github.clorinde.core.util.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author: laoshiren
 * @date: 2024/05/27 10:49
 **/
public class ReqWrapper implements Serializable {

    private static final long serialVersionUID = 7968292103062411798L;

    private static final String PATH_REGEX = "\\{(.*?)\\}";

    private static final int JSON_NOT_FOUND_INDEX = -1;

    @Getter
    @Setter
    private Method method;

    @Getter
    @Setter
    private Object[] originArgs;

    private List<Parameter> parameters;

    @Getter
    private DuelType duelType;

    private String host;

    private String url;

    @Getter
    private Map<String, String> header = new LinkedHashMap<>();

    @Getter
    private List<Pair<String, String>> param = new ArrayList<>();

    @Getter
    private String jsonBody;

    private ReqWrapper() {
    }

    private ReqWrapper(Method method, Object[] originArgs) {
        this.method = method;
        this.originArgs = originArgs;
        this.parameters = Arrays.stream(this.method.getParameters())
                .collect(Collectors.toList());
        this.methodDuelTypeUrl();
    }

    public static ReqWrapper newInstance(Method method, Object[] originArgs) {
        return new ReqWrapper(method, originArgs);
    }

    public void methodDuelTypeUrl() {
        this.host = method.getDeclaringClass().getAnnotation(Clorinde.class).value();
        if (MethodUtil.isAnnotationPresent(this.method, GetDuel.class)) {
            this.duelType = DuelType.GET;
            this.url = this.method.getAnnotation(GetDuel.class).value();
        }
        if (MethodUtil.isAnnotationPresent(this.method, PostDuel.class)) {
            this.duelType = DuelType.POST;
            this.url = this.method.getAnnotation(PostDuel.class).value();
        }
        if (MethodUtil.isAnnotationPresent(this.method, PutDuel.class)) {
            this.duelType = DuelType.PUT;
            this.url = this.method.getAnnotation(PutDuel.class).value();
        }
        if (MethodUtil.isAnnotationPresent(this.method, DeleteDuel.class)) {
            this.duelType = DuelType.DELETE;
            this.url = this.method.getAnnotation(DeleteDuel.class).value();
        }
    }


    public String requestUrl() {
        return host + url;
    }

    private Integer build(Integer indexCache, Class<? extends Annotation> annotationClass, Consumer<Integer> consumer){
        if (indexCache == null) {
            // 为null 即第一次请求
            for (int i = 0; i < parameters.size(); i++) {
                if (parameters.get(i).isAnnotationPresent(annotationClass)) {
                    consumer.accept(i);
                    return i;
                }
            }
            return JSON_NOT_FOUND_INDEX;
        } else {
            // 不为null 看是否为空 空位没有路径参数
            if (indexCache.equals(JSON_NOT_FOUND_INDEX)) {
                return indexCache;
            }
            consumer.accept(indexCache);
            return indexCache;
        }
    }


    private List<Integer> build(List<Integer> indexCache, Class<? extends Annotation> annotationClass, Consumer<Integer> consumer) {
        if (indexCache == null) {
            // 为null 即第一次请求
            List<Integer> idx = new ArrayList<>();
            for (int i = 0; i < parameters.size(); i++) {
                if (parameters.get(i).isAnnotationPresent(annotationClass)) {
                    idx.add(i);
                    consumer.accept(i);
                }
            }
            return idx;
        } else {
            // 不为null 看是否为空 空位没有路径参数
            if (CollUtil.isEmpty(indexCache)) {
                return indexCache;
            }
            indexCache.forEach(consumer);
            return indexCache;
        }
    }


    public List<Integer> buildPath(List<Integer> pathVariableIndexCache) {
        Consumer<Integer> pathConsumer = (index) -> {
            Parameter parameter = parameters.get(index);
            PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
            String pathAnnoValue = pathVariable.value();
            if (StringUtils.isBlank(pathAnnoValue)) {
                // 正则表达式匹配被{}包裹的内容
                Pattern pattern = Pattern.compile(PATH_REGEX);
                Matcher matcher = pattern.matcher(url);
                StringBuffer buffer = new StringBuffer();
                if (matcher.find()) {
                    // 如果该占位符在replacements中有对应的值，则进行替换
                    matcher.appendReplacement(buffer, originArgs[index].toString());
                }
                // 添加剩余的部分到StringBuffer
                matcher.appendTail(buffer);
                // 替换后的字符串
                url = buffer.toString();
            }
            if (StringUtils.isNotBlank(pathAnnoValue)) {
                url = url.replace("{" + pathAnnoValue + "}", originArgs[index].toString());
            }
        };
        return this.build(pathVariableIndexCache, PathVariable.class, pathConsumer);
    }


    public List<Integer> buildHeader(List<Integer> headerIndexCache) {
        Consumer<Integer> headerConsumer = (index) -> {
            Parameter parameter = parameters.get(index);
            Header header = parameter.getAnnotation(Header.class);
            // map
            if (originArgs[index] != null && originArgs[index] instanceof Map) {
                Map map = (Map) originArgs[index];
                map.forEach((k, v) -> this.header.putIfAbsent(k.toString(), v.toString()));
            } else if (originArgs[index] != null) {
                // 字符串
                if (originArgs[index] instanceof CharSequence) {
                    String headerKey = header.value();
                    if (StringUtils.isNotBlank(headerKey)) {
                        this.header.putIfAbsent(headerKey, originArgs[index].toString());
                    }
                } else {
                    // 对象
                    Map<String, String> map = ObjectMapper.obj2MapString(originArgs[index]);
                    map.forEach((k, v) -> this.header.putIfAbsent(k, v));
                }
            }
        };
        return this.build(headerIndexCache, Header.class, headerConsumer);
    }

    public List<Integer> buildRequestParam(List<Integer> paramIndexCache) {
        List<Pair<String, String>> pairs = new ArrayList<>();
        Consumer<Integer> requestParamConsumer = (index) -> {
            Parameter parameter = parameters.get(index);
            ReqParam reqParam = parameter.getAnnotation(ReqParam.class);
            if (originArgs[index] != null && originArgs[index] instanceof Map) {
                Map map = (Map) originArgs[index];
                map.forEach((k, v) -> pairs.add(new MutablePair<>(k.toString(), v.toString())));
            } else if (originArgs[index] != null) {
                // 字符串
                if (originArgs[index] instanceof CharSequence) {
                    String reqParamKey = reqParam.value();
                    if (StringUtils.isNotBlank(reqParamKey)) {
                        pairs.add(new MutablePair<>(reqParamKey, originArgs[index].toString()));
                    }
                } else {
                    // 对象
                    Map<String, String> map = ObjectMapper.obj2MapString(originArgs[index]);
                    map.forEach((k, v) -> pairs.add(new MutablePair<>(k, v)));
                }
            }
            if (!pairs.isEmpty()) {
                this.param.addAll(pairs);
            }
        };
        return this.build(paramIndexCache, ReqParam.class, requestParamConsumer);
    }

    public Integer buildJsonParam(Integer jsonIndexCache) {
        return this.build(jsonIndexCache, Body.class, (index) -> {
            Object originArg = originArgs[index];
            if (originArg != null) {
                if (originArg instanceof CharSequence) {
                    this.jsonBody = originArg.toString();
                } else {
                    this.jsonBody = ObjectMapper.obj2Json(originArg);
                }
            } else {
                this.jsonBody = "{}";
            }
        });
    }
}
