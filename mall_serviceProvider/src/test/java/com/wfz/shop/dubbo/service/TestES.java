package com.wfz.shop.dubbo.service;

import com.wfz.shop.buyer.service.GoodsSkuService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestES {

    @Autowired
    private GoodsSkuService goodsSkuService;

    @Test
    public void testImportGoods(){
        goodsSkuService.importES();
    }
}