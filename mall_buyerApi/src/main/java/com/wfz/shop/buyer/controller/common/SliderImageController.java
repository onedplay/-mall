package com.wfz.shop.buyer.controller.common;

import com.wfz.shop.buyer.service.common.VerificationService;
import com.wfz.shop.common.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/common/slider")
@Api(tags = "滑块验证")
public class SliderImageController {

    @Autowired
    private VerificationService verificationService;

    @GetMapping("/{verificationEnums}")
    @ApiOperation(value = "获取校验接口")
    public Result getSliderImage(@RequestHeader String uuid,
                                 @PathVariable("verificationEnums") Integer verificationCode) {
        return verificationService.createVerification(verificationCode, uuid);
    }

    @PostMapping("/{verificationEnums}")
    @ApiOperation(value = "验证码预校验")
    public Result getSliderImage(@RequestHeader String uuid,
                                 @PathVariable("verificationEnums") Integer verificationCode,
                                 Integer xPos) {
        return verificationService.preCheck(verificationCode, uuid,xPos);
    }
}

