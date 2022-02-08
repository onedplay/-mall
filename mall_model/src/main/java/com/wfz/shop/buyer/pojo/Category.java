package com.wfz.shop.buyer.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品分类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    private Long id;

    private String name;

    private Long parentId;

    private Integer level;

    private Integer sortOrder;

    private Double commissionRate;

    private String image;

    private Boolean supportChannel;

    private Integer status;

    private Long createTime;

    private Long updateTime;
 }