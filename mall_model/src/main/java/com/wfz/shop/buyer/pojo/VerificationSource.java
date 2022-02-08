package com.wfz.shop.buyer.pojo;

import lombok.Data;

@Data
public class VerificationSource {

    private Long id;

    private String name;

    private String resource;
	//资源 滑块
    private String type;

    private Boolean deleteFlag;
}
