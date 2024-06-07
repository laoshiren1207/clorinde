package io.github.clorinde.test;

import io.github.clorinde.core.annotation.Clorinde;
import io.github.clorinde.core.annotation.GetDuel;
import io.github.clorinde.core.annotation.param.Body;
import io.github.clorinde.core.annotation.param.Header;
import io.github.clorinde.core.annotation.param.PathVariable;

import java.util.Map;

/**
 * @author: laoshiren
 * @date: 2024/05/16 15:40
 **/
@Clorinde
public interface TestMapper {

    @GetDuel("http://localhost:8080/api/task/{id}")
    Map<String, Object> detail(@Header("app_secret") String appSecret, @PathVariable String id);

}
