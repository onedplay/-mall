package com.wfz.shop.common.security;

import lombok.Data;

/**
 * Token 实体类
 */
@Data
public class Token {
    /**
     * 访问token
     */
    private String accessToken;

    /**
     * 刷新token
     */
    private String refreshToken;

}