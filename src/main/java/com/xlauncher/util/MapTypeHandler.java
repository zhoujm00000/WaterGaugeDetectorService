package com.xlauncher.util;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.EOFException;
import java.io.IOException;
import java.sql.*;
import java.util.Map;

/**
 * 将实体类中的map字段转换成数据库中varchar类型，取出时将字符串转换成map。
 * 注： @MappedJdbcTypes这个注解定义的是JdbcType类型，这里的类型不可自己随意定义，
 * 必须要是枚举类org.apache.ibatis.type.JdbcType所枚举的数据类型，@MappedTypes
 * 定义的是JavaType的数据类型，描述了哪些java类型可被拦截。
 * @author 白帅雷
 * @date 2018-06-12
 */
@MappedJdbcTypes({JdbcType.VARCHAR})
@MappedTypes({Map.class})
public class MapTypeHandler extends BaseTypeHandler<Map<String, Object>>{
    private ObjectMapper mapper = new ObjectMapper();
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Map<String, Object> map, JdbcType jdbcType) throws SQLException {
        if (map == null) {
            preparedStatement.setNull(i, Types.VARCHAR);
        } else {
            try {
                preparedStatement.setString(i, JsonMapUtil.getJsonStrMap(map));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet resultSet, String s) throws SQLException {
        try {
            if (resultSet != null) {
                mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
                String value = resultSet.getString(s);
                return mapper.readValue(value, Map.class);
            }
        } catch (EOFException e) {
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet resultSet, int i) throws SQLException {
        try {
            String value = resultSet.getString(i);
            return mapper.readValue(value, Map.class);
        } catch (EOFException e) {
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, Object> getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        try {
            if (callableStatement != null) {
                String value = callableStatement.getString(i);
                return mapper.readValue(value, Map.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
