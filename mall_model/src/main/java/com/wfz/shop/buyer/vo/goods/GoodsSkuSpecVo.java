package com.wfz.shop.buyer.vo.goods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商品规格VO
 */
@Data
public class GoodsSkuSpecVo implements Serializable {


    @ApiModelProperty(value = "商品skuId")
    private String skuId;

    @ApiModelProperty(value = "商品sku所包含规格")
    private List<SpecValueVo> specValues;

    @ApiModelProperty(value = "库存")
    private Integer quantity;

}
