package io.github.clorinde.core.parser;

import io.github.clorinde.core.util.ObjectMapper;

/**
 * @author: laoshiren
 * @date: 2024/05/16 14:34
 **/
public class DefaultResultParser implements ResultParse {
    @Override
    public <T> T parser(String result, Class<T> clazz) {
        if (clazz == String.class) {
            return (T) result;
        }
        return ObjectMapper.json2Obj(result, clazz);
    }
}
