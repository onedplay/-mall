package com.wfz.shop.buyer.vo.goods;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 规格值
 */
@Data
public class SpecValueVo implements Serializable {

    private String specName;

    private String specValue;

    private Integer specType;
    /**
     * 规格图片
     */
    private List<SpecImages> specImage;

}
