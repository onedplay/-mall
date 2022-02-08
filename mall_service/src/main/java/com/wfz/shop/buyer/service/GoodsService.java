package com.wfz.shop.buyer.service;

import com.wfz.shop.buyer.params.EsGoodsSearchParam;
import com.wfz.shop.buyer.params.PageParams;
import com.wfz.shop.buyer.vo.goods.GoodsPageVo;

public interface GoodsService {
    /**
     * 根据搜索条件 进行搜索
     * @param goodsSearchParams
     * @param pageParams
     * @return
     */
    public GoodsPageVo searchGoods(EsGoodsSearchParam goodsSearchParams, PageParams pageParams);
}
