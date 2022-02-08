package com.wfz.shop.buyer.service;

import com.wfz.shop.buyer.eums.CartTypeEnum;
import com.wfz.shop.common.vo.Result;

public interface CartService {
    Result<Object> addCart(CartTypeEnum cartTypeEnum, String skuId, Integer num, String id);
}
