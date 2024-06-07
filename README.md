# Clorinde 
原神里的决斗代理人

使用代理的方式让HTTP请求用起来像Mybatis

```java

@Clorinde
public interface TestMapper {
    
    @GetDuel("http://localhost:8080/api/task/{id}")
    Map<String, Object> detail(@Header("app_secret") String appSecret, @PathVariable String id);
}



public class TestMain {

    public static void main(String[] args) throws Exception {
        ClorindeBindRegistry mapperBindRegistry = new ClorindeBindRegistry(OkHttp.instance(), null, null);

        mapperBindRegistry.addProxy(TestMapper.class);
        TestMapper proxy = mapperBindRegistry.getProxy(TestMapper.class);

        Map<String, Object> a = proxy.detail("d8ca3650-d355-4709-bbf1-a962a3f47c1e", "1292658282840139");
        System.out.println(ObjectMapper.obj2Json(a));
    }

}
```