package com.wfz.shop.buyer.service.impl.trade;

import com.wfz.shop.buyer.eums.CartTypeEnum;
import com.wfz.shop.buyer.eums.goods.GoodsAuthEnum;
import com.wfz.shop.buyer.eums.goods.GoodsStatusEnum;
import com.wfz.shop.buyer.pojo.goods.GoodsSku;
import com.wfz.shop.buyer.service.CartService;
import com.wfz.shop.buyer.service.GoodsSkuService;
import com.wfz.shop.buyer.service.impl.CacheService;
import com.wfz.shop.buyer.vo.trade.CartSkuVo;
import com.wfz.shop.buyer.vo.trade.TradeVo;
import com.wfz.shop.common.model.BusinessCodeEnum;
import com.wfz.shop.common.utils.CurrencyUtil;
import com.wfz.shop.common.vo.Result;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DubboService(version = "1.0.0")
public class CartServiceImpl implements CartService {

    @Autowired
    private GoodsSkuService goodsSkuService;

    private GoodsSku goodsSku;

    private CacheService cacheService;

    @Override
    public Result<Object> addCart(CartTypeEnum cartTypeEnum, String skuId, Integer num, String userId) {
        if (userId == null){
            return Result.fail(BusinessCodeEnum.HTTP_NO_LOGIN.getCode(),"未登录");
        }
        //校验商品有效性，判定失效和库存
        Result<Object> checkGoodsResult = checkGoods(skuId,num);
        if (!checkGoodsResult.isSuccess()){
            return checkGoodsResult;
        }
        //只考虑购物车模式，需要有之前的购物车内容
        if (cartTypeEnum.equals(CartTypeEnum.CART)){
            //先从缓存中获取，获取不到 重新构建
            TradeVo tradeVo = createTradeVo(cartTypeEnum,userId);
            List<CartSkuVo> skuList = tradeVo.getSkuList();
            if (CollectionUtils.isEmpty(skuList)){
                //购物车中无商品
                addGoodsToCart(null,cartTypeEnum, num, skuList);
            }else{
                //购物车不为空
                //查找购物车中有无 此商品
                CartSkuVo cartSkuVo = skuList.stream().filter(i -> i.getGoodsSku().getId().equals(goodsSku.getId())).findFirst().orElse(null);
                if (cartSkuVo != null && cartSkuVo.getGoodsSku().getUpdateTime().equals(goodsSku.getUpdateTime())){
                    //证明此商品有效
                    //库存判断
                    Integer newNum = cartSkuVo.getNum() + num;
                    if (newNum > goodsSku.getQuantity()){
                        newNum = goodsSku.getQuantity();
                    }
                    addGoodsToCart(cartSkuVo,cartTypeEnum,newNum,skuList);
                }else{
                    //需要移除无效的商品
                    skuList.remove(cartSkuVo);
                    addGoodsToCart(null,cartTypeEnum,num,skuList);
                }
            }
            cacheService.set(cartTypeEnum.name() + "_" + userId,tradeVo,null);
        }
        return Result.success();
    }

    private void addGoodsToCart(CartSkuVo cartSkuVo,CartTypeEnum cartTypeEnum, Integer num, List<CartSkuVo> skuList) {
        if (cartSkuVo == null){
            cartSkuVo = new CartSkuVo(goodsSku);
        }
        cartSkuVo.setCartType(cartTypeEnum);
        cartSkuVo.setNum(num);
        //计算价格
        Double price = CurrencyUtil.mul(cartSkuVo.getPurchasePrice(), cartSkuVo.getNum());
        cartSkuVo.setSubTotal(price);
        //新加入的商品为选中状态
        cartSkuVo.setChecked(true);
        skuList.add(cartSkuVo);
    }

    private TradeVo createTradeVo(CartTypeEnum cartTypeEnum,String userId) {
        TradeVo tradeVo = cacheService.get(cartTypeEnum.name() + "_" + userId, TradeVo.class);
        if (tradeVo == null){
            tradeVo = new TradeVo();
            tradeVo.init(cartTypeEnum);
        }
        return tradeVo;
    }

    private Result<Object> checkGoods(String skuId, Integer num) {
        GoodsSku goodsSku = goodsSkuService.findGoodsSkuById(skuId);
        if (goodsSku == null){
            return Result.fail(-999,"商品不存在");
        }
        if (GoodsAuthEnum.PASS.getCode() != goodsSku.getIsAuth()){
            return Result.fail(-999,"商品未认证通过");
        }
        if (GoodsStatusEnum.UPPER.getCode() != goodsSku.getMarketEnable()){
            return Result.fail(-999,"商品已下架");
        }
        Integer quantity = goodsSku.getQuantity();
        if (quantity < num){
            return Result.fail(-999,"商品库存不足");
        }
        this.goodsSku = goodsSku;
        return Result.success();
    }
}
