package com.wfz.shop.buyer.service.goods;

import com.wfz.shop.buyer.params.EsGoodsSearchParam;
import com.wfz.shop.buyer.params.PageParams;
import com.wfz.shop.buyer.service.GoodsService;
import com.wfz.shop.buyer.vo.goods.GoodsPageVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class GoodsSearchService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public List<String> getHotWords(Integer start, Integer end) {
        List<String> searchWords = new ArrayList<>();
        start = (start - 1) * end;
        end = start + end;
        Set<ZSetOperations.TypedTuple<String>> goodsHotWords = redisTemplate.opsForZSet().reverseRangeWithScores("goods_hot_words", start, end);
        if (goodsHotWords == null){
            return searchWords;
        }
        for (ZSetOperations.TypedTuple<String> goodsHotWord : goodsHotWords) {
            String value = goodsHotWord.getValue();
            searchWords.add(value);
        }
        return searchWords;
    }

    @DubboReference(version = "1.0.0")
    private GoodsService goodsService;

    public GoodsPageVo searchGoods(EsGoodsSearchParam goodsSearchParams, PageParams pageParams) {
        return goodsService.searchGoods(goodsSearchParams,pageParams);
    }
}
