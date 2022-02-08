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
         * 目的：GoodsDetailVo
         * 1. 根据skuid 查询 goodsSku表 ，转换为GoodsSkuVO对象
         * 2. categoryName 表中有 分类对应id列表，根据分类的id列表 去分类表中查询
         * 3. specs sku数据中 json的spec字符串，处理对应的字符串即可
         * 4. 促销信息 暂时不理会
         */
        //如果skuid 为null 或者 查询不出来数据，怎么办？
        //还有商品id，可以根据商品id 查询sku数据
        if (goodsId == null){
            return Result.fail(-999,"参数错误");
        }
        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        GoodsSku goodsSku;
        if (skuId == null){
            //根据商品id查询
            LambdaQueryWrapper<GoodsSku> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(GoodsSku::getGoodsId,Long.parseLong(goodsId));
            List<GoodsSku> goodsSkus = goodsSkuMapper.selectList(queryWrapper);
            if (goodsSkus.size() <= 0){
                return Result.fail(-999,"参数错误");
            }
            goodsSku = goodsSkus.get(0);
        }else{
            goodsSku = goodsSkuMapper.selectById(Long.parseLong(skuId));
        }
        GoodsSkuVo goodsSkuVo = getGoodsSkuVo(goodsSku);
        goodsDetailVo.setData(goodsSkuVo);
        //分类列表
        String categoryPath = goodsSku.getCategoryPath();

        List<String> categoryNames = categoryService.getCategoryNameByIds(Arrays.asList(categoryPath.split(",")));
        goodsDetailVo.setCategoryName(categoryNames);

        //规则参数
        List<SpecValueVo> specList = goodsSkuVo.getSpecList();
        List<GoodsSkuSpecVo> goodsSkuSpecVoList = new ArrayList<>();
        GoodsSkuSpecVo goodsSkuSpecVo = new GoodsSkuSpecVo();
        goodsSkuSpecVo.setSkuId(goodsSku.getId().toString());
        goodsSkuSpecVo.setQuantity(goodsSku.getQuantity());
        goodsSkuSpecVo.setSpecValues(specList);
        goodsSkuSpecVoList.add(goodsSkuSpecVo);
        goodsDetailVo.setSpecs(goodsSkuSpecVoList);
        //促销信息
        goodsDetailVo.setPromotionMap(new HashMap<>());
        return Result.success(goodsDetailVo);
    }

    public GoodsSkuVo getGoodsSkuVo(GoodsSku goodsSku) {

        GoodsSkuVo goodsSkuVo = new GoodsSkuVo();
        BeanUtils.copyProperties(goodsSku, goodsSkuVo);
        //获取sku信息
        JSONObject jsonObject = JSON.parseObject(goodsSku.getSpecs());
        //用于接受sku信息
        List<SpecValueVo> specValueVoS = new ArrayList<>();
        //用于接受sku相册
        List<String> goodsGalleryList = new ArrayList<>();
        //循环提交的sku表单
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
