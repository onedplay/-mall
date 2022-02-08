package com.wfz.shop.buyer.controller.pages;

import com.wfz.shop.buyer.eums.PageType;
import com.wfz.shop.buyer.service.pages.PagesService;
import com.wfz.shop.common.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pages")
public class PagesController {

    @Autowired
    private PagesService pagesService;

    @GetMapping("/index")
    public Result index(Integer clientType){
        return pagesService.getPageIndexData(clientType, PageType.INDEX.getCode());
    }
}
