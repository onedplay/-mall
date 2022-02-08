package com.wfz.shop.buyer.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCategory {

    private Long id;

    private Integer level;

    private Long parentId;

    private Integer sort;

    private String articleCategoryName;

    private String type;

    private Boolean deleteFlag;

    private Date createTime;

    private Date updateTime;
}
