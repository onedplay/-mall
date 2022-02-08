package com.wfz.shop.buyer.service.trade;

import com.wfz.shop.buyer.eums.CartTypeEnum;
import com.wfz.shop.buyer.handler.security.UserContext;
import com.wfz.shop.buyer.service.CartService;
import com.wfz.shop.common.security.AuthUser;
import com.wfz.shop.common.vo.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class BuyerCartService {

    @DubboReference(version = "1.0.0")
    private CartService cartService;

    public Result<Object> add(String skuId, Integer num, String cartType) {
        //获取购物车类型 默认为购物车
        CartTypeEnum cartTypeEnum = getCartType(cartType);
        AuthUser currentUser = UserContext.getCurrentUser();
        String id = currentUser.getId();
        return cartService.addCart(cartTypeEnum,skuId,num,id);
    }


    private CartTypeEnum getCartType(String cartType) {
        if (StringUtils.isBlank(cartType)){
            return CartTypeEnum.CART;
        }

        try {
            return CartTypeEnum.valueOf(cartType);
        }catch (Exception e){
            return CartTypeEnum.CART;
        }
    }
}