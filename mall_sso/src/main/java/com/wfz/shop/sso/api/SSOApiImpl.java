package com.wfz.shop.sso.api;

import com.wfz.shop.buyer.vo.member.MemberVo;
import com.wfz.shop.common.security.AuthUser;
import com.wfz.shop.sso.service.MemberService;
import com.wfz.shop.sso.service.VerificationService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService(version = "1.0.0")
public class SSOApiImpl implements SSOApi {

    @Autowired
    private MemberService memberService;
    @Autowired
    private VerificationService verificationService;

    @Override
    public MemberVo findMemberById(Long id) {
        return memberService.findMemberById(id);
    }

    @Override
    public AuthUser checkToken(String token) {
        return verificationService.checkToken(token);
    }
}
