package com.wfz.shop.buyer.vo.trade;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单价格详情
 */
@Data
public class PriceDetailVo implements Serializable {

    @ApiModelProperty(value = "商品原价")
    private Double originalPrice;

    @ApiModelProperty(value = "配送费")
    private Double freight;

    @ApiModelProperty(value = "优惠金额")
    private Double discountPrice;

    @ApiModelProperty(value = "支付积分")
    private Integer payPoint;

    @ApiModelProperty(value = "最终成交金额")
    private Double finalePrice;

}
