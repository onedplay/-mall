package com.wfz.shop.buyer.vo.article;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCategoryVo implements Serializable {

    private String id;

    private Integer level;

    private String parentId;

    private Integer sort;

    private String articleCategoryName;

    private String type;

    @ApiModelProperty(value = "子菜单")
    private List<ArticleCategoryVo> children = new ArrayList<>();

    public List<ArticleCategoryVo> getChildren() {
        if (children != null) {
            children.sort(new Comparator<ArticleCategoryVo>() {
                @Override
                public int compare(ArticleCategoryVo o1, ArticleCategoryVo o2) {
                    return o1.getSort().compareTo(o2.getSort());
                }
            });
            return children;
        }
        return null;
    }
}
