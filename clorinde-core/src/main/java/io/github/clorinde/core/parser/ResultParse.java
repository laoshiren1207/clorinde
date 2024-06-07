package io.github.clorinde.core.parser;

/**
 * @author: laoshiren
 * @date: 2024/05/16 14:31
 **/
public interface ResultParse {

    <T> T parser(String result, Class<T> clazz);

}
