package io.github.clorinde.test;

import io.github.clorinde.core.http.okhttp.OkHttp;
import io.github.clorinde.core.mapper.ClorindeBindRegistry;
import io.github.clorinde.core.util.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: laoshiren
 * @date: 2024/05/16 15:41
 **/
public class TestMain {

    public static void main(String[] args) throws Exception {
        ClorindeBindRegistry mapperBindRegistry = new ClorindeBindRegistry(OkHttp.instance(), null, null);

        mapperBindRegistry.addProxy(TestMapper.class);
        TestMapper proxy = mapperBindRegistry.getProxy(TestMapper.class);

        Map<String, String> req = new HashMap<>();
        req.put("tel", "18480000000");

        Map<String, Object> a = proxy.detail("d8ca3650-d355-4709-bbf1-a962a3f47c1e", "1292658282840139");
        System.out.println(ObjectMapper.obj2Json(a));
    }

}
