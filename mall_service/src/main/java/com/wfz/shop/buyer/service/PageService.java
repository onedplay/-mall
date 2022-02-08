package com.wfz.shop.buyer.service;

import com.wfz.shop.common.vo.Result;

public interface PageService {
    Result findPageTemplate(Integer clientType, int pageType);
}
