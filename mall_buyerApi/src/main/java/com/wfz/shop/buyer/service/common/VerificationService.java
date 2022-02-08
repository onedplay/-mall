package com.wfz.shop.buyer.service.common;

import com.wfz.shop.buyer.eums.VerificationEnums;
import com.wfz.shop.buyer.service.VerificationSourceService;
import com.wfz.shop.buyer.vo.commons.VerificationSourceVo;
import com.wfz.shop.buyer.vo.commons.slider.VerificationVo;
import com.wfz.shop.common.model.BusinessCodeEnum;
import com.wfz.shop.common.utils.SerializableStream;
import com.wfz.shop.common.utils.SliderImageCut;
import com.wfz.shop.common.utils.SliderImageUtil;
import com.wfz.shop.common.vo.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class VerificationService {

    @DubboReference(version = "1.0.0")
    private VerificationSourceService verificationSourceService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 获取滑块验证的图片
     * @param verificationCode
     * @param uuid
     * @return
     */
    public Result<SliderImageCut> createVerification(Integer verificationCode, String uuid) {
        /**
         * 目的：为了获取滑块验证的图片，同时 需要有一个滑块，然后图片上扣出来一个和滑块一样的图片出来
         *      扣出来的这个图片，移动到对应的位置上之后，重新组合为一个完整图片
         * 1. uuid 先判断请求是否合法
         * 2. verificationCode 验证的类型  login （1）,首先判断参数合法
         * 3. 获取数据库verification_source 表中的所有数据，分为两块 一个是 资源数据，一个是 滑块数据
         * 4. 从资源数据中，随机获取一个资源，随机获取一个滑块
         * 5. 从 资源图片上 扣一个和滑块一模一样的图片 出来
         * 6. 记录相关的信息 x轴信息
         * 7. 相关的信息 存入 redis中，便于后续验证  key   prefix + verificationCode + uuid value x轴移动坐标
         */
        if (uuid == null) {
            return Result.fail(BusinessCodeEnum.ILLEGAL_REQUEST.getCode(),"非法请求，请重新刷新页面操作");
        }
        VerificationEnums verificationEnums = VerificationEnums.codeOf(verificationCode);
        if (verificationEnums == null){
            return Result.fail(BusinessCodeEnum.ILLEGAL_REQUEST.getCode(),"非法请求，请重新刷新页面操作");
        }
        VerificationVo verificationVo =  verificationSourceService.findVerificationSource();
        List<VerificationSourceVo> verificationResources = verificationVo.getVerificationResources();
        List<VerificationSourceVo> verificationSlider = verificationVo.getVerificationSlider();

        Random random = new Random();
        //随机选择需要切的图下标
        int resourceNum = random.nextInt(verificationResources.size());
        //随机选择剪切模版下标
        int sliderNum = random.nextInt(verificationSlider.size());

        VerificationSourceVo resource = verificationResources.get(resourceNum);
        VerificationSourceVo slider = verificationSlider.get(sliderNum);
        String resourceUrl = resource.getResource();
        String sliderUrl = slider.getResource();
        try {
            //图片相应的处理操作
            SliderImageCut sliderImageCut = SliderImageUtil.pictureTemplatesCut(getInputStream(sliderUrl), getInputStream(resourceUrl));
            //如果不设置为0  滑块直接就匹配
            //生成验证参数 120可以验证 无需手动清除，120秒有效时间自动清除
            redisTemplate.opsForValue().set(verificationRedisKey(uuid, verificationEnums), String.valueOf(sliderImageCut.getRandomX()), 120, TimeUnit.SECONDS);
            sliderImageCut.setRandomX(0);
            return Result.success(sliderImageCut);
        }catch (Exception e){
            e.printStackTrace();
        }

        return Result.fail(-999,"创建校验错误");
    }

    private String verificationRedisKey(String uuid, VerificationEnums verificationEnums) {
        return "VERIFICATION_IMAGE_"+verificationEnums.name()+uuid;
    }

    /**
     * 根据网络地址，获取源文件
     * 这里简单说一下，这里是将不可序列化的inputstream序列化对象，存入redis缓存
     * @param originalResource
     * @return
     */
    private SerializableStream getInputStream(String originalResource) throws Exception {

        if (StringUtils.isNotEmpty(originalResource)) {
            URL url = new URL(originalResource);
            InputStream inputStream = url.openStream();
            SerializableStream serializableStream = new SerializableStream(inputStream);
            return serializableStream;
        }
        return null;
    }



    /**
     * 滑动验证结果
     * @param verificationCode
     * @param uuid
     * @param xPos
     * @return
     */
    public Result preCheck(Integer verificationCode, String uuid, Integer xPos) {
        /**
         * 去redis 验证 xPos和redis存的一不一样
         */
        if (uuid == null) {
            return Result.fail(BusinessCodeEnum.ILLEGAL_REQUEST.getCode(),"非法请求，请重新刷新页面操作");
        }
        VerificationEnums verificationEnums = VerificationEnums.codeOf(verificationCode);
        if (verificationEnums == null){
            return Result.fail(BusinessCodeEnum.ILLEGAL_REQUEST.getCode(),"非法请求，请重新刷新页面操作");
        }
        String verificationRedisKey = verificationRedisKey(uuid, verificationEnums);
        String x = redisTemplate.opsForValue().get(verificationRedisKey);
        if (StringUtils.isBlank(x)){
            return Result.fail(-999,"不匹配");
        }
        //验证结果
        boolean result = Math.abs(Integer.parseInt(x) - xPos) < 3;
        if (result){
            //验证成功，则记录验证结果 验证有效时间，120秒
            //滑块验证成功之后 随后的一段时间 如果有操作，认为是验证通过的 不需要重复进行滑块验证
            redisTemplate.opsForValue().set("VERIFICATION_IMAGE_RESULT_"+verificationEnums.name()+uuid, "true", 120,TimeUnit.SECONDS);
            return Result.success();
        }
        return Result.fail(-999,"不匹配");
    }

    public boolean check(String uuid, VerificationEnums verificationEnums) {
        Boolean hasKey = redisTemplate.hasKey("VERIFICATION_IMAGE_RESULT_" + verificationEnums.name() + uuid);
        return hasKey != null && hasKey;
    }
}
