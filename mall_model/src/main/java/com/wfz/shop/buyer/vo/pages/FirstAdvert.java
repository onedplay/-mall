package com.wfz.shop.buyer.vo.pages;

import lombok.Data;

import java.io.Serializable;

@Data
public class FirstAdvert implements Serializable {

    private String type;

    private String name;

    private String icon;

    private String key;

    private FirstAdvertOption options;
}
