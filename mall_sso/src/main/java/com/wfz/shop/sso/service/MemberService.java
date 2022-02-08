package com.wfz.shop.sso.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wfz.shop.buyer.eums.ClientType;
import com.wfz.shop.buyer.pojo.Member;
import com.wfz.shop.buyer.vo.member.MemberVo;
import com.wfz.shop.common.cache.CachePrefix;
import com.wfz.shop.common.context.ThreadContextHolder;
import com.wfz.shop.common.model.BusinessCodeEnum;
import com.wfz.shop.common.security.AuthUser;
import com.wfz.shop.common.security.Token;
import com.wfz.shop.common.security.UserEnums;
import com.wfz.shop.common.utils.token.SecurityKey;
import com.wfz.shop.common.utils.token.TokenUtils;
import com.wfz.shop.common.vo.Result;
import com.wfz.shop.sso.mapper.MemberMapper;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class MemberService {

    @Resource
    private MemberMapper memberMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    public Result<Token> usernameLogin(String username, String password) {
        LambdaQueryWrapper<Member> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Member::getUsername,username).eq(Member::getDisabled,false);
        Member member = memberMapper.selectOne(queryWrapper);
        if (member == null){
            return Result.fail(-999,"用户不存在");
        }
        if (!new BCryptPasswordEncoder().matches(password,member.getPassword())){
            return Result.fail(-999,"用户名或密码错误");
        }
        Token token = genToken(member);
        return Result.success(token);
    }

    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("e10adc3949ba59abbe56e057f20f883e"));
    }
    private Token genToken(Member member) {
        //获取客户端类型
        String clientType = ThreadContextHolder.getHttpRequest().getHeader("clientType");
        Integer clientTypeInt = 0;
        try {
            if (StringUtils.isBlank(clientType)){clientType = "0";}
            clientTypeInt = Integer.parseInt(clientType);
            ClientType type = ClientType.codeOf(clientTypeInt);
            if (type != null){
                member.setClientEnum(clientTypeInt);
            }
        }catch (Exception e){
            e.printStackTrace();
            member.setClientEnum(ClientType.UNKNOWN.getCode());
        }
        member.setLastLoginDate(System.currentTimeMillis());
        this.memberMapper.updateById(member);
        AuthUser authUser = new AuthUser(member.getUsername(), String.valueOf(member.getId()),member.getNickName(), UserEnums.MEMBER);

        Token token = new Token();

        String jwtToken = TokenUtils.createToken(member.getUsername(), authUser, 7 * 24 * 60L);
        token.setAccessToken(jwtToken);
        redisTemplate.opsForValue().set(CachePrefix.ACCESS_TOKEN.name() + UserEnums.MEMBER.name() + jwtToken, "1",7, TimeUnit.DAYS);
        //设置刷新token，当accessToken过期的时候，可以通过refreshToken来 重新获取accessToken 而不用访问数据库
        String refreshToken = TokenUtils.createToken(member.getUsername(), authUser, 15 * 24 * 60L);
        redisTemplate.opsForValue().set(CachePrefix.REFRESH_TOKEN.name() + UserEnums.MEMBER.name() + jwtToken, "1",15, TimeUnit.DAYS);
        token.setRefreshToken(refreshToken);
        return token;
    }

    public MemberVo findMemberById(Long id) {
        LambdaQueryWrapper<Member> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Member::getId,id).eq(Member::getDisabled,false);
        Member member = memberMapper.selectOne(queryWrapper);

        return copy(member);
    }

    private MemberVo copy(Member member) {
        if (member == null){
            return null;
        }
        MemberVo memberVo = new MemberVo();
        BeanUtils.copyProperties(member,memberVo);
        memberVo.setId(String.valueOf(member.getId()));
        return memberVo;
    }

    public Result<Object> refreshToken(String refreshToken) {
        Claims claim = TokenUtils.parserToken(refreshToken);
        if (claim == null){
            return Result.fail(BusinessCodeEnum.HTTP_NO_LOGIN.getCode(),"已过期，请重新登录");
        }
        AuthUser authUser = JSON.parseObject(claim.get(SecurityKey.USER_CONTEXT).toString(), AuthUser.class);

        String value = redisTemplate.opsForValue().get(CachePrefix.REFRESH_TOKEN.name() + UserEnums.MEMBER.name() + refreshToken);
        if (StringUtils.isBlank(value)){
            return Result.fail(BusinessCodeEnum.HTTP_NO_LOGIN.getCode(),"已过期，请重新登录");
        }
        Token token = new Token();
        String accessToken = TokenUtils.createToken(authUser.getUsername(), authUser, 7 * 24 * 60L);
        //放入redis当中
        redisTemplate.opsForValue().set(CachePrefix.ACCESS_TOKEN.name() + UserEnums.MEMBER.name() + accessToken, "true",7, TimeUnit.DAYS);
        String newRefreshToken = TokenUtils.createToken(authUser.getUsername(), authUser, 15 * 24 * 60L);
        redisTemplate.opsForValue().set(CachePrefix.REFRESH_TOKEN.name() + UserEnums.MEMBER.name() + refreshToken, "true",15, TimeUnit.DAYS);
        token.setAccessToken(accessToken);
        token.setRefreshToken(newRefreshToken);
        return Result.success(token);
    }
}
