package com.wfz.shop.buyer.service;

import com.wfz.shop.buyer.vo.CategoryVo;

import java.util.List;

public interface CategoryService {
    List<CategoryVo> findCategoryTree(Long parentId);

    List<String> getCategoryNameByIds(List<String> asList);
}
