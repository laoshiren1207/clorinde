package io.github.clorinde.core.util;

import com.google.gson.Gson;

import java.util.Collections;
import java.util.Map;

/**
 * @author: laoshiren
 * @date: 2024/05/10 17:04
 **/
public class ObjectMapper {

    public static final Gson GSON = new Gson();

    public static Map<String, Object> obj2Map(Object obj) {
        if(obj == null){
            return Collections.emptyMap();
        }

        return GSON.fromJson(obj2Json(obj), Map.class);
    }

    public static Map<String, String> obj2MapString(Object obj) {
        if(obj == null){
            return Collections.emptyMap();
        }
        return GSON.fromJson(obj2Json(obj), Map.class);
    }

    public static String obj2Json(Object obj){
        return GSON.toJson(obj);
    }


    public static <T> T json2Obj(String json, Class<T> clazz){
        return GSON.fromJson(json, clazz);
    }

}
