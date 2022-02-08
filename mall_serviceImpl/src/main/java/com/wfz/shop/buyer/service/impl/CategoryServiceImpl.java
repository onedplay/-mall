package com.wfz.shop.buyer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wfz.shop.buyer.mapper.CategoryMapper;
import com.wfz.shop.buyer.pojo.Category;
import com.wfz.shop.buyer.service.CategoryService;
import com.wfz.shop.buyer.vo.CategoryVo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//事务能生效
@DubboService(version = "1.0.0",interfaceClass = CategoryService.class)
//@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    public CategoryVo copy(Category category){
        CategoryVo target = new CategoryVo();
        BeanUtils.copyProperties(category, target);
        return target;
    }
    public List<CategoryVo> copyList(List<Category> categoryList){
        List<CategoryVo> categoryVoList = new ArrayList<>();
        for (Category category : categoryList) {
            categoryVoList.add(copy(category));
        }
        return categoryVoList;
    }

    private List<CategoryVo> categoryTree(Long parentId){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getStatus,0);
        List<Category> categories = categoryMapper.selectList(queryWrapper);
        List<CategoryVo> categoryVos = copyList(categories);
        List<CategoryVo> categoryVOList = new ArrayList<>();
        for (CategoryVo categoryVo : categoryVos) {
            if (categoryVo.getParentId().equals(parentId)){
                categoryVOList.add(categoryVo);
                addAllChildren(categoryVo,categoryVos);
            }
        }
        return categoryVOList;
    }

    private void addAllChildren(CategoryVo categoryVo, List<CategoryVo> categoryVos) {
        List<CategoryVo> categoryVOList = new ArrayList<>();
        for (CategoryVo vo : categoryVos) {
            if (vo.getParentId().equals(categoryVo.getId())){
                categoryVOList.add(vo);
                addAllChildren(vo,categoryVos);
            }
        }
        categoryVo.setChildren(categoryVOList);
    }

    @Override
    public List<CategoryVo> findCategoryTree(Long parentId) {
        /**
         * 1. 根据parentId 获取对应的分类列表
         * 2. 获取到的分类列表 只有一级
         * 3. 根据刚才的sql执行 发现了问题： 循环 递归的获取 所有分类的 子分类列表
         * 4. 如果这么做的话，数据库连接就会多次 连接 性能就会降低
         * 5. 不能这么写，想了一个方法 把所有的分类查出来，一次查询 效率高
         * 6. 代码去组合 层级关系，代码的允许速度是非常快的，因为代码运行在内存当中
         */

        return categoryTree(parentId);
    }

    @Override
    public List<String> getCategoryNameByIds(List<String> idList) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Category::getId,idList);
        List<Category> categories = categoryMapper.selectList(queryWrapper);
        List<String> strings = categories.stream().map(Category::getName).collect(Collectors.toList());
        return strings;
    }
}
