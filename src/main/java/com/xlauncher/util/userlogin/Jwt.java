package com.xlauncher.util.userlogin;

import com.auth0.jwt.JWTExpiredException;
import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.internal.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于JWT(json web token)的token登录认证
 * @author 白帅雷
 * @date 2018-02-05
 */
public class Jwt {
    private static final String SECRET = "XX#$%()(#*!()!KL<><MQLMNQNQJQK sdfkjsdrow32234545fdf>?N<:{LWPW";
    private static final String EXP = "exp";
    private static final String PAYLOAD = "payload";

    /**
     * 编码
     * @param object
     * @param expiredTimeAt
     * @return the jwt token
     */
    public static <T> String sign(T object, long expiredTimeAt) {
        try {
            final JWTSigner signer = new JWTSigner(SECRET);
            final Map<String, Object> claims = new HashMap<String, Object>();
            long nowMillis = System.currentTimeMillis();
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(object);
            claims.put(PAYLOAD, jsonString);
            claims.put(EXP, nowMillis + expiredTimeAt);
            return signer.sign(claims);
        } catch(RuntimeException e) {
            return null;
        } catch(Exception e) {
            return null;
        }
    }

    /**
     * 解码
     * @param jwt
     * @return POJO object
     */
    public static<T> T unSign(String jwt, Class<T> classT) {
        final JWTVerifier verifier = new JWTVerifier(SECRET);
        try {
            final Map<String,Object> claims= verifier.verify(jwt);
            if (claims.containsKey(EXP) && claims.containsKey(PAYLOAD)) {
                long exp = (Long)claims.get(EXP);
                long currentTimeMillis = System.currentTimeMillis();
                if (exp > currentTimeMillis) {
                    String json = (String)claims.get(PAYLOAD);
                    ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.readValue(json, classT);
                }
            }
            return null;
        } catch (JWTExpiredException e){
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            return null;
        }
    }

}