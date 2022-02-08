package com.wfz.shop.sso.controller;

import com.wfz.shop.buyer.eums.VerificationEnums;
import com.wfz.shop.common.security.Token;
import com.wfz.shop.common.vo.Result;
import com.wfz.shop.sso.service.MemberService;
import com.wfz.shop.sso.service.VerificationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
public class MembersController {

    @Autowired
    private MemberService memberService;
    @Autowired
    private VerificationService verificationService;


    @PostMapping("/userLogin")
    public Result<Token> userLogin(@RequestParam String username,
                                   @RequestParam String password,
                                   @RequestHeader String uuid) {
        if (verificationService.check(uuid, VerificationEnums.LOGIN)) {
            return this.memberService.usernameLogin(username, password);
        } else {
            return Result.fail(-999,"请重新验证");
        }
    }

    @ApiOperation(value = "刷新token")
    @GetMapping("/refresh/{refreshToken}")
    public Result<Object> refreshToken(@PathVariable String refreshToken) {
        return this.memberService.refreshToken(refreshToken);
    }
}