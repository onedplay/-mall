package com.wfz.shop.buyer.controller.member;

import com.wfz.shop.buyer.service.members.MemberBuyerService;
import com.wfz.shop.buyer.vo.member.MemberVo;
import com.wfz.shop.common.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
public class MemberBuyerController {

    @Autowired
    private MemberBuyerService memberBuyerService;

    @GetMapping
    public Result<MemberVo> getUserInfo() {

        return memberBuyerService.getUserInfo();
    }

}
