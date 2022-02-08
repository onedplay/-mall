package com.wfz.shop.sso.service;

import com.alibaba.fastjson.JSON;
import com.wfz.shop.buyer.eums.VerificationEnums;
import com.wfz.shop.common.cache.CachePrefix;
import com.wfz.shop.common.security.AuthUser;
import com.wfz.shop.common.security.UserEnums;
import com.wfz.shop.common.utils.token.SecretKeyUtil;
import com.wfz.shop.common.utils.token.SecurityKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VerificationService {

    @Autowired
    private StringRedisTemplate redisTemplate;


    public boolean check(String uuid, VerificationEnums verificationEnums) {
        Boolean hasKey = redisTemplate.hasKey("VERIFICATION_IMAGE_RESULT_" + verificationEnums.name() + uuid);
        return hasKey != null && hasKey;
    }

    public AuthUser checkToken(String token) {
        try {
            Claims claims
                    = Jwts.parser()
                    .setSigningKey(SecretKeyUtil.generalKey())
                    .parseClaimsJws(token).getBody();
            //获取存储在claims中的用户信息
            String json = claims.get(SecurityKey.USER_CONTEXT).toString();
            AuthUser authUser = JSON.parseObject(json, AuthUser.class);

            //校验redis中是否有权限
            Boolean hasKey = redisTemplate.hasKey(CachePrefix.ACCESS_TOKEN.name() + UserEnums.MEMBER.name() + token);
            if (hasKey != null && hasKey) {
                return authUser;
            }
        }catch (ExpiredJwtException e) {
            //token过期了
            log.debug("user analysis exception:", e);
        }
        return null;
    }
}