package com.wfz.shop.common.utils.token;

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.*;

import java.util.Date;

public class TokenUtils {

    /**
     *
     * @param username
     * @param claim
     * @param expirationTime 分钟
     * @return
     */
    public static String createToken(String username,Object claim,Long expirationTime){
        //JWT 生成
        return Jwts.builder()
                //jwt 私有声明
                .claim(SecurityKey.USER_CONTEXT, JSON.toJSONString(claim))
                //JWT的主体
                .setSubject(username)
                //失效时间 当前时间+过期分钟
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime * 60 * 1000))
                //签名算法和密钥
                .signWith(SecretKeyUtil.generalKey())
                .compact();
    }

    public static Claims parserToken(String refreshToken) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SecretKeyUtil.generalKeyByDecoders())
                    .parseClaimsJws(refreshToken).getBody();
            return claims;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            //token 过期 认证失败等
            return null;
        }
    }
}
