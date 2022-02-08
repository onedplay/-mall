package com.wfz.shop.sso.api;

import com.wfz.shop.buyer.vo.member.MemberVo;
import com.wfz.shop.common.security.AuthUser;

public interface SSOApi {

    MemberVo findMemberById(Long id);

    AuthUser checkToken(String token);
}
