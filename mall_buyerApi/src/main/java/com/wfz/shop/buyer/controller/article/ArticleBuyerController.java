package com.wfz.shop.buyer.controller.article;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wfz.shop.buyer.params.ArticleSearchParams;
import com.wfz.shop.buyer.service.article.BuyerArticleService;
import com.wfz.shop.buyer.vo.article.ArticleCategoryVo;
import com.wfz.shop.buyer.vo.article.ArticleVo;
import com.wfz.shop.common.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(tags = "买家端,文章接口")
@RequestMapping("/article")
public class ArticleBuyerController {

    @Autowired
    private BuyerArticleService buyerArticleService;

    @ApiOperation(value = "分页获取")
    @GetMapping
    public Result<Page<ArticleVo>> getByPage(ArticleSearchParams articleSearchParams) {
        return Result.success(buyerArticleService.articlePage(articleSearchParams));
    }

    @ApiOperation(value = "通过id获取文章")
    @ApiImplicitParam(name = "id", value = "文章ID", required = true, paramType = "path")
    @GetMapping(value = "/get/{id}")
    public Result<ArticleVo> get(@PathVariable("id") Long id) {
        return Result.success(buyerArticleService.customGet(id));
    }

    @ApiOperation(value = "获取文章分类列表")
    @GetMapping(value = "/articleCategory/list")
    public Result<List<ArticleCategoryVo>> getArticleCategoryList() {
        return Result.success(buyerArticleService.allChildren());
    }
}
