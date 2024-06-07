package io.github.clorinde.core.http;

import io.github.clorinde.core.wrapper.ReqWrapper;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

/**
 * @author: laoshiren
 * @date: 2024/05/09 20:01
 **/
public interface Http {

    String get(String requestUrl, Map<String, String> header, List<Pair<String, String>> param, String json, List<Object> otherParam);

    String post(String requestUrl, Map<String, String> header,List<Pair<String, String>> param, String json, List<Object> otherParam);

    String delete(String requestUrl, Map<String, String> header, List<Pair<String, String>> param, String json, List<Object> otherParam);

    String put(String requestUrl, Map<String, String> header, List<Pair<String, String>> param, String json, List<Object> otherParam);

    String get(ReqWrapper wrapper);

    String post(ReqWrapper wrapper);

    String delete(ReqWrapper wrapper);

    String put(ReqWrapper wrapper);

    String request(ReqWrapper wrapper);

}
