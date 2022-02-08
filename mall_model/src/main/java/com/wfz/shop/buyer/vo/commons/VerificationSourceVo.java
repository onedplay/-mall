package com.wfz.shop.buyer.vo.commons;

import lombok.Data;

import java.io.Serializable;

@Data
public class VerificationSourceVo implements Serializable {

    private Long id;

    private String name;

    private String resource;

    private String type;

}
