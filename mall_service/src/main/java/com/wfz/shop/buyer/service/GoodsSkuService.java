package com.wfz.shop.buyer.service;

import com.wfz.shop.buyer.pojo.goods.GoodsSku;
import com.wfz.shop.buyer.vo.goods.GoodsDetailVo;
import com.wfz.shop.common.vo.Result;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface GoodsSkuService {
    void importES();

    Result<GoodsDetailVo> getGoodsSkuDetail(String goodsId, String skuId);

    GoodsSku findGoodsSkuById(String skuId);
}
