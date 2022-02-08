package com.wfz.shop.buyer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wfz.shop.buyer.mapper.ArticleCategoryMapper;
import com.wfz.shop.buyer.mapper.ArticleMapper;
import com.wfz.shop.buyer.params.ArticleSearchParams;
import com.wfz.shop.buyer.pojo.Article;
import com.wfz.shop.buyer.pojo.ArticleCategory;
import com.wfz.shop.buyer.service.ArticleService;
import com.wfz.shop.buyer.vo.article.ArticleCategoryVo;
import com.wfz.shop.buyer.vo.article.ArticleVo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

//加dubbo注解
//发布当前的service服务到nacos上
//ip：port/接口名称/方法名称
//version便于 接口有不同的实现，或者版本升级之用
//interfaceClass 不加这个事务 无法使用
@DubboService(version = "1.0.0",interfaceClass = ArticleService.class)
public class ArticleServiceImpl implements ArticleService {
    @Resource
    private ArticleMapper articleMapper;

    @Override
    public Page<ArticleVo> articlePage(ArticleSearchParams articleSearchParams) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        String categoryId = articleSearchParams.getCategoryId();
        if (StringUtils.isNotBlank(categoryId)) {
            queryWrapper.eq(Article::getCategoryId, categoryId);
        }
        String title = articleSearchParams.getTitle();
        if (StringUtils.isNotBlank(title)) {
            queryWrapper.eq(Article::getTitle, title);
        }
        String type = articleSearchParams.getType();
        if (StringUtils.isNotBlank(type)) {
            queryWrapper.eq(Article::getType, type);
        }
        queryWrapper.orderByDesc(Article::getSort);

        queryWrapper.select(Article::getCategoryId,Article::getId,Article::getOpenStatus,Article::getTitle,Article::getSort);
        Page<Article> articlePage = new Page<>(articleSearchParams.getPageNumber(),articleSearchParams.getPageSize());
        Page<Article> articlePage1 = articleMapper.selectPage(articlePage, queryWrapper);

        Page<ArticleVo> articleVOPage = new Page<>();
        BeanUtils.copyProperties(articlePage1,articleVOPage);
        List<Article> records = articlePage1.getRecords();
        articleVOPage.setRecords(copyList(records));
        return articleVOPage;
    }



    public ArticleVo copy(Article article){
        if (article == null){
            return null;
        }
        ArticleVo articleVO = new ArticleVo();

        BeanUtils.copyProperties(article,articleVO);
        articleVO.setId(article.getId().toString());
        return articleVO;
    }
    public  List<ArticleVo> copyList(List<Article> articleList){
        List<ArticleVo> articleVOList = new ArrayList<>();

        for (Article article : articleList) {
            articleVOList.add(copy(article));
        }
        return articleVOList;
    }

    @Override
    public ArticleVo findArticleById(Long id) {
        Article article = this.articleMapper.selectById(id);
        return copy(article);
    }

    @Resource
    private ArticleCategoryMapper articleCategoryMapper;

    @Override
    public List<ArticleCategoryVo> findAllArticleCategory() {
        LambdaQueryWrapper<ArticleCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleCategory::getDeleteFlag,false);
        List<ArticleCategory> articleCategories = articleCategoryMapper.selectList(queryWrapper);
        List<ArticleCategoryVo> articleCategoryVos = copyCategoryList(articleCategories);
        List<ArticleCategoryVo> articleCategoryVoList = new ArrayList<>();
        for (ArticleCategoryVo articleCategoryVo : articleCategoryVos) {
            if (articleCategoryVo.getLevel() == 0){
                addCategoryChild(articleCategoryVo,articleCategoryVos);
                articleCategoryVoList.add(articleCategoryVo);
            }
        }
        return articleCategoryVoList;
    }

    private void addCategoryChild(ArticleCategoryVo articleCategoryVo, List<ArticleCategoryVo> articleCategoryVos) {
        List<ArticleCategoryVo> articleCategoryVoList = new ArrayList<>();
        for (ArticleCategoryVo categoryVo : articleCategoryVos) {
            if (categoryVo.getParentId().equals(articleCategoryVo.getId())){
                addCategoryChild(categoryVo,articleCategoryVos);
                articleCategoryVoList.add(categoryVo);
            }
        }
        articleCategoryVo.setChildren(articleCategoryVoList);
    }

    public ArticleCategoryVo copyCategory(ArticleCategory article){
        if (article == null){
            return null;
        }
        ArticleCategoryVo articleVo = new ArticleCategoryVo();

        BeanUtils.copyProperties(article,articleVo);
        articleVo.setId(article.getId().toString());
        articleVo.setParentId(article.getParentId().toString());
        return articleVo;
    }

    public  List<ArticleCategoryVo> copyCategoryList(List<ArticleCategory> articleList){
        List<ArticleCategoryVo> articleVoList = new ArrayList<>();

        for (ArticleCategory article : articleList) {
            articleVoList.add(copyCategory(article));
        }
        return articleVoList;
    }

}