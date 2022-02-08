package com.wfz.shop.buyer.pojo;

import lombok.Data;

@Data
public class TemplateDetail {

    private Long id;

    private Long templateId;

    private String templateType;

    private String templateData;

    private Integer status;

    private Long createTime;
}
