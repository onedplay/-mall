package com.wfz.shop.buyer.service.impl.goods;

import com.wfz.shop.buyer.params.EsGoodsSearchParam;
import com.wfz.shop.buyer.params.PageParams;
import com.wfz.shop.buyer.pojo.es.EsGoodsIndex;
import com.wfz.shop.buyer.service.GoodsService;
import com.wfz.shop.buyer.vo.goods.GoodsPageVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;


import java.util.List;
import java.util.stream.Collectors;

@DubboService(version = "1.0.0")
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;
    @Override
    public GoodsPageVo searchGoods(EsGoodsSearchParam goodsSearchParams, PageParams pageParams) {
        /**
         * 1. 拿到搜索 本质上很多，首页的搜索，暂时 假定条件只有一个keyword
         * 2. es中 查询 商品名称 是否匹配，一定匹配
         * 3. 卖点 也需要进行匹配 不是一定要匹配
         */
        String keyword = goodsSearchParams.getKeyword();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(keyword)){
            boolQueryBuilder.must(QueryBuilders.matchQuery("goodsName",keyword));
            boolQueryBuilder.should(QueryBuilders.matchQuery("sellingPoint",keyword));
        }
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(boolQueryBuilder);
        SearchHits<EsGoodsIndex> search = elasticsearchTemplate.search(nativeSearchQuery, EsGoodsIndex.class);

        GoodsPageVo goodsPageVo = new GoodsPageVo();
        goodsPageVo.setTotalElements(search.getTotalHits());
        List<EsGoodsIndex> collect = search.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
        goodsPageVo.setContent(collect);
        return goodsPageVo;
    }
}