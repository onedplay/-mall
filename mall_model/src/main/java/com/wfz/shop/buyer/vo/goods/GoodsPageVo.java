package com.wfz.shop.buyer.vo.goods;

import com.wfz.shop.buyer.pojo.es.EsGoodsIndex;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GoodsPageVo implements Serializable {


    private Long totalElements;

    private List<EsGoodsIndex> content;
}
