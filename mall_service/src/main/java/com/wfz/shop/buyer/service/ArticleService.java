package com.wfz.shop.buyer.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wfz.shop.buyer.params.ArticleSearchParams;
import com.wfz.shop.buyer.vo.article.ArticleCategoryVo;
import com.wfz.shop.buyer.vo.article.ArticleVo;

import java.util.List;

public interface ArticleService {

    Page<ArticleVo> articlePage(ArticleSearchParams articleSearchParams);

    ArticleVo findArticleById(Long id);

    List<ArticleCategoryVo> findAllArticleCategory();
}
