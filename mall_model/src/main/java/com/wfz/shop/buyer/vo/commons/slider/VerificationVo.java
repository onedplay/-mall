package com.wfz.shop.buyer.vo.commons.slider;

import com.wfz.shop.buyer.vo.commons.VerificationSourceVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class VerificationVo implements Serializable {

    /**
     * 资源
     */
    List<VerificationSourceVo> verificationResources;

    /**
     * 滑块资源
     */
    List<VerificationSourceVo> verificationSlider;
}