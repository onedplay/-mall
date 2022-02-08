package com.wfz.shop.buyer.vo.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * 购物车展示VO
 *
 */
@Data
@ApiModel(description = "购物车")
@NoArgsConstructor
public class CartVo  implements Serializable {

    @ApiModelProperty(value = "购物车中的产品列表")
    private List<CartSkuVo> skuList;

    @ApiModelProperty(value = "sn")
    private String sn;

    @ApiModelProperty(value = "购物车页展示时，店铺内的商品是否全选状态.1为店铺商品全选状态,0位非全选")
    private Boolean checked;

    @ApiModelProperty(value = "重量")
    private Double weight;

    @ApiModelProperty(value = "购物车商品数量")
    private Integer goodsNum;

    @ApiModelProperty(value = "购物车商品数量")
    private String remark;

    @ApiModelProperty(value = "卖家id")
    private String storeId;

    @ApiModelProperty(value = "卖家姓名")
    private String storeName;

    @ApiModelProperty(value = "此商品价格展示")
    private PriceDetailVo priceDetailVo;

}
