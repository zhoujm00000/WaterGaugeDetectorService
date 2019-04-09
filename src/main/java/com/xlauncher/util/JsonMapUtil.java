package com.xlauncher.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * json转换map的工具类，给MapTypeHandler提供
 * @author 白帅雷
 * @date 2018-06-12
 */
class JsonMapUtil {

    /**
     * map转换json.
     * 注：标准JSON 格式key、value都加双引号，否则在查询数据时会出现无法解析的异常JsonParseException
     * @param map 集合
     * @return String json字符串
     */
    static String getJsonStrMap(Map<String, Object> map) {
        Set<String> keys = map.keySet();
        String key;
        String value;
        StringBuilder jsonBuffer = new StringBuilder();
        jsonBuffer.append("{");
        for (Iterator<String> it = keys.iterator(); it.hasNext();) {
            key = it.next();
            value = (String) map.get(key);
            jsonBuffer.append("\"").append(key).append("\"").append(":").append("\"").append(value).append("\"");
            if (it.hasNext()) {
                jsonBuffer.append(",");
            }
        }
        jsonBuffer.append("}");
        return jsonBuffer.toString();
    }
}
