package com.wfz.shop.buyer.service.members;

import com.wfz.shop.buyer.handler.security.UserContext;
import com.wfz.shop.buyer.vo.member.MemberVo;
import com.wfz.shop.common.security.AuthUser;
import com.wfz.shop.common.vo.Result;
import com.wfz.shop.sso.api.SSOApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class MemberBuyerService {

    @DubboReference(version = "1.0.0")
    private SSOApi ssoApi;

    public Result<MemberVo> getUserInfo() {
        AuthUser currentUser = UserContext.getCurrentUser();
        if (currentUser != null){
            String id = currentUser.getId();
            MemberVo memberById = ssoApi.findMemberById(Long.parseLong(id));
            return Result.success(memberById);
        }
        return Result.fail(-999,"登录已过期");
    }
}
