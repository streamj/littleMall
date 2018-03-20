package cc.freecloudfx.littlemall.util;

import cc.freecloudfx.littlemall.pojo.User;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author StReaM on 3/19/2018
 */
@Slf4j
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 对象的所有字段全部包含
        // ALWAYS 所有字段都有
        // NON_NULL 只有非 NULL 字段
        // NON_DEFAULT 有默认值对象，不包含
        // NON_EMPTY 比 NULL 更严格，空的都不行
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);
        // 禁止把 DATE 转成 TIMESTAMP
        // 时间戳就是 UNIX 元年到现在的毫秒数
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        // 忽略空 Bean(没有 getter, setter) 转 Json 错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDRAD_FORMAT));
        // 忽略在 json 中存在，而 Java 对象中不存在的情况
        // 比如某些场景，别的业务多了几个破字段，对我们来说又不重要
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> String obj2String(T obj) {
        if(obj == null) {
            return null;
        }
        try {
            return obj instanceof String ?(String) obj: objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse object to String error", e);
            return null;
        }
    }

    public static <T> String obj2StringPretty(T obj) {
        if(obj == null) {
            return null;
        }
        try {
            return obj instanceof String ?
                    (String) obj:
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse object to String error", e);
            return null;
        }
    }

    public static <T> T string2Obj(String str, Class<T> clazz) {
        if (StringUtils.isEmpty(str) || clazz == null) {
            return null;
        }
        try {
            return clazz.equals(String.class) ?(T) str:
                    objectMapper.readValue(str, clazz);
        } catch (IOException e) {
            log.warn("Parse String to object error", e);
            return null;
        }
    }

    public static <T> T string2Obj(String str, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(str) || typeReference == null) {
            return null;
        }
        try {
            return (T)( typeReference.getType().equals(String.class) ? str:
                    objectMapper.readValue(str, typeReference) );
        } catch (IOException e) {
            log.warn("Parse String to object error", e);
            return null;
        }
    }

    public static <T> T string2Obj(String str, Class<?> collectionClass, Class<?> ... elementCls) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementCls);
        try {
            return objectMapper.readValue(str, javaType);
        } catch (IOException e) {
            log.warn("Parse String to object error", e);
            return null;
        }
    }

    public static void main(String[] args) {
        User u1 = new User();
        u1.setId(1);
        u1.setEmail("bobo@foxmail.com");

        User u2 = new User();
        u2.setId(2);
        u2.setEmail("bobo2@foxmail.com");

        String u1Js = JsonUtil.obj2String(u1);
        String u1JsP = JsonUtil.obj2StringPretty(u1);

        log.info("common json: {}",u1Js);
        log.info("pretty json: {}", u1JsP);

        User user = JsonUtil.string2Obj(u1Js, User.class);

        List<User> list = Lists.newArrayList();
        list.add(u1);
        list.add(u2);
        String userListStr = JsonUtil.obj2StringPretty(list);

        log.info("*********************************");
        log.info(userListStr);

        List<User> userListObj = JsonUtil.string2Obj(userListStr, new TypeReference<List<User>>() {});

        List<User> userListObj1 = JsonUtil.string2Obj(userListStr, List.class, User.class);

        System.out.println();
    }
}
