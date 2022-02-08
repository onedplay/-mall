package com.wfz.shop.buyer.service.article;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wfz.shop.buyer.params.ArticleSearchParams;
import com.wfz.shop.buyer.service.ArticleService;
import com.wfz.shop.buyer.vo.article.ArticleCategoryVo;
import com.wfz.shop.buyer.vo.article.ArticleVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuyerArticleService {

    @DubboReference(version = "1.0.0")
    private ArticleService articleService;

    public Page<ArticleVo> articlePage(ArticleSearchParams articleSearchParams) {

        return articleService.articlePage(articleSearchParams);
    }

    public ArticleVo customGet(Long id) {
        return articleService.findArticleById(id);
    }

    public List<ArticleCategoryVo> allChildren() {
        return articleService.findAllArticleCategory();
    }
}
