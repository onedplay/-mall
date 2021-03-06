package com.wfz.shop.buyer.service.impl.goods;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wfz.shop.buyer.mapper.GoodsMapper;
import com.wfz.shop.buyer.mapper.GoodsSkuMapper;
import com.wfz.shop.buyer.pojo.es.EsGoodsIndex;
import com.wfz.shop.buyer.pojo.goods.GoodsSku;
import com.wfz.shop.buyer.service.CategoryService;
import com.wfz.shop.buyer.service.GoodsSkuService;
import com.wfz.shop.buyer.vo.goods.*;
import com.wfz.shop.common.vo.Result;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@DubboService(version = "1.0.0")
public class GoodsSkuServiceImpl implements GoodsSkuService {

    @Resource
    private GoodsSkuMapper goodsSkuMapper;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;
    @Autowired
    private CategoryService categoryService;
    @Override
    public void importES() {
        LambdaQueryWrapper<GoodsSku> queryWrapper = new LambdaQueryWrapper<>();
        List<GoodsSku> goodsSkus = goodsSkuMapper.selectList(queryWrapper);
        for (GoodsSku skus : goodsSkus) {
            EsGoodsIndex esGoodsIndex = new EsGoodsIndex();
            BeanUtils.copyProperties(skus,esGoodsIndex);
            esGoodsIndex.setId(skus.getId().toString());
            esGoodsIndex.setGoodsId(skus.getGoodsId().toString());
            esGoodsIndex.setPrice(skus.getPrice().doubleValue());
            BigDecimal promotionPrice = skus.getPromotionPrice();
            if (promotionPrice != null) {
                esGoodsIndex.setPromotionPrice(promotionPrice.doubleValue());
            }
            elasticsearchTemplate.save(esGoodsIndex);
        }
    }

    @Override
    public Result<GoodsDetailVo> getGoodsSkuDetail(String goodsId, String skuId) {
        /**
         * ?????????GoodsDetailVo
         * 1. ??????skuid ?????? goodsSku??? ????????????GoodsSkuVO??????
         * 2. categoryName ????????? ????????????id????????????????????????id?????? ?????????????????????
         * 3. specs sku????????? json???spec??????????????????????????????????????????
         * 4. ???????????? ???????????????
         */
        //??????skuid ???null ?????? ????????????????????????????????????
        //????????????id?????????????????????id ??????sku??????
        if (goodsId == null){
            return Result.fail(-999,"????????????");
        }
        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        GoodsSku goodsSku;
        if (skuId == null){
            //????????????id??????
            LambdaQueryWrapper<GoodsSku> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(GoodsSku::getGoodsId,Long.parseLong(goodsId));
            List<GoodsSku> goodsSkus = goodsSkuMapper.selectList(queryWrapper);
            if (goodsSkus.size() <= 0){
                return Result.fail(-999,"????????????");
            }
            goodsSku = goodsSkus.get(0);
        }else{
            goodsSku = goodsSkuMapper.selectById(Long.parseLong(skuId));
        }
        GoodsSkuVo goodsSkuVo = getGoodsSkuVo(goodsSku);
        goodsDetailVo.setData(goodsSkuVo);
        //????????????
        String categoryPath = goodsSku.getCategoryPath();

        List<String> categoryNames = categoryService.getCategoryNameByIds(Arrays.asList(categoryPath.split(",")));
        goodsDetailVo.setCategoryName(categoryNames);

        //????????????
        List<SpecValueVo> specList = goodsSkuVo.getSpecList();
        List<GoodsSkuSpecVo> goodsSkuSpecVoList = new ArrayList<>();
        GoodsSkuSpecVo goodsSkuSpecVo = new GoodsSkuSpecVo();
        goodsSkuSpecVo.setSkuId(goodsSku.getId().toString());
        goodsSkuSpecVo.setQuantity(goodsSku.getQuantity());
        goodsSkuSpecVo.setSpecValues(specList);
        goodsSkuSpecVoList.add(goodsSkuSpecVo);
        goodsDetailVo.setSpecs(goodsSkuSpecVoList);
        //????????????
        goodsDetailVo.setPromotionMap(new HashMap<>());
        return Result.success(goodsDetailVo);
    }

    public GoodsSkuVo getGoodsSkuVo(GoodsSku goodsSku) {

        GoodsSkuVo goodsSkuVo = new GoodsSkuVo();
        BeanUtils.copyProperties(goodsSku, goodsSkuVo);
        //??????sku??????
        JSONObject jsonObject = JSON.parseObject(goodsSku.getSpecs());
        //????????????sku??????
        List<SpecValueVo> specValueVoS = new ArrayList<>();
        //????????????sku??????
        List<String> goodsGalleryList = new ArrayList<>();
        //???????????????sku??????
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            SpecValueVo specValueVo = new SpecValueVo();
            if (entry.getKey().equals("images")) {
                specValueVo.setSpecName(entry.getKey());
                if (entry.getValue().toString().contains("url")) {
                    List<SpecImages> specImages = JSON.parseArray(entry.getValue().toString(), SpecImages.class);
                    specValueVo.setSpecImage(specImages);
                    goodsGalleryList = specImages.stream().map(SpecImages::getUrl).collect(Collectors.toList());
                }
            } else {
                specValueVo.setSpecName(entry.getKey());
                specValueVo.setSpecValue(entry.getValue().toString());
            }
            specValueVoS.add(specValueVo);
        }
        goodsSkuVo.setGoodsGalleryList(goodsGalleryList);
        goodsSkuVo.setSpecList(specValueVoS);
        return goodsSkuVo;
    }

    @Override
    public GoodsSku findGoodsSkuById(String skuId) {
        return goodsSkuMapper.selectById(skuId);
    }
}
