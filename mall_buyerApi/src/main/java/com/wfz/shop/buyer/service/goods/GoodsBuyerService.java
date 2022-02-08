package com.wfz.shop.buyer.service.goods;

import com.wfz.shop.buyer.service.GoodsSkuService;
import com.wfz.shop.buyer.vo.goods.GoodsDetailVo;
import com.wfz.shop.common.vo.Result;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GoodsBuyerService {

    @DubboReference(version = "1.0.0")
    private GoodsSkuService goodsSkuService;

    public Result<GoodsDetailVo> getGoodsSkuDetail(String goodsId, String skuId) {
        /**
         * 交给dubbo服务提供者 来提供
         */
        return goodsSkuService.getGoodsSkuDetail(goodsId,skuId);
    }
}
