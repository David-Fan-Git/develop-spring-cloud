package com.develop.mvp.pk.module.system.api.social;

import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.develop.mvp.pk.framework.common.pojo.CommonResult;
import com.develop.mvp.pk.framework.common.util.object.BeanUtils;
import com.develop.mvp.pk.module.system.api.social.dto.*;
import com.develop.mvp.pk.module.system.enums.social.SocialTypeEnum;
import com.develop.mvp.pk.module.system.application.social.port.inbound.SocialUseCase;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.subscribemsg.TemplateInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

import java.util.List;

import static cn.hutool.core.collection.CollUtil.findOne;
import static com.develop.mvp.pk.framework.common.pojo.CommonResult.success;
import static com.develop.mvp.pk.framework.common.util.collection.CollectionUtils.convertList;

/**
 * 社交应用的 API 实现类
 *
 * @author David
 */
@RestController
@Validated
@Slf4j
public class SocialClientApiImpl implements SocialClientApi {

    @Resource
    private SocialUseCase socialClientUseCase;
    @Resource
    private SocialUseCase socialUserUseCase;

    @Override
    public CommonResult<String> getAuthorizeUrl(Integer socialType, Integer userType, String redirectUri) {
        return success(socialClientUseCase.getAuthorizeUrl(socialType, userType, redirectUri));
    }

    @Override
    public CommonResult<SocialWxJsapiSignatureRespDTO> createWxMpJsapiSignature(Integer userType, String url) {
        WxJsapiSignature signature = socialClientUseCase.createWxMpJsapiSignature(userType, url);
        return success(BeanUtils.toBean(signature, SocialWxJsapiSignatureRespDTO.class));
    }

    @Override
    public CommonResult<SocialWxPhoneNumberInfoRespDTO> getWxMaPhoneNumberInfo(Integer userType, String phoneCode) {
        WxMaPhoneNumberInfo info = socialClientUseCase.getWxMaPhoneNumberInfo(userType, phoneCode);
        return success(BeanUtils.toBean(info, SocialWxPhoneNumberInfoRespDTO.class));
    }

    @Override
    public CommonResult<byte[]> getWxaQrcode(SocialWxQrcodeReqDTO reqVO) {
        return success(socialClientUseCase.getWxaQrcode(reqVO));
    }

    @Override
    public CommonResult<List<SocialWxaSubscribeTemplateRespDTO>> getWxaSubscribeTemplateList(Integer userType) {
        List<TemplateInfo> list = socialClientUseCase.getSubscribeTemplateList(userType);
        return success(convertList(list, item -> BeanUtils.toBean(item, SocialWxaSubscribeTemplateRespDTO.class).setId(item.getPriTmplId())));
    }

    @Override
    public CommonResult<Boolean> sendWxaSubscribeMessage(SocialWxaSubscribeMessageSendReqDTO reqDTO) {
        // 1.1 获得订阅模版列表
        List<TemplateInfo> templateList = socialClientUseCase.getSubscribeTemplateList(reqDTO.getUserType());
        if (CollUtil.isEmpty(templateList)) {
            log.warn("[sendSubscribeMessage][reqDTO({}) 发送订阅消息失败，原因：没有找到订阅模板]", reqDTO);
            return success(false);
        }
        // 1.2 获得需要使用的模版
        TemplateInfo template = findOne(templateList, item ->
                ObjUtil.equal(item.getTitle(), reqDTO.getTemplateTitle()));
        if (template == null) {
            log.warn("[sendWxaSubscribeMessage][reqDTO({}) 发送订阅消息失败，原因：没有找到订阅模板]", reqDTO);
            return success(false);
        }

        // 2. 获得社交用户
        SocialUserRespDTO socialUser = socialUserUseCase.getSocialUserByUserId(reqDTO.getUserType(), reqDTO.getUserId(),
                SocialTypeEnum.WECHAT_MINI_PROGRAM.getType());
        if (ObjUtil.isNull(socialUser) || StrUtil.isBlankIfStr(socialUser.getOpenid())) {
            log.warn("[sendWxaSubscribeMessage][reqDTO({}) 发送订阅消息失败，原因：会员 openid 缺失]", reqDTO);
            return success(false);
        }

        // 3. 发送订阅消息
        socialClientUseCase.sendSubscribeMessage(reqDTO, template.getPriTmplId(), socialUser.getOpenid());
        return success(true);
    }

    @Override
    public CommonResult<Boolean> uploadWxaOrderShippingInfo(Integer userType, SocialWxaOrderUploadShippingInfoReqDTO reqDTO) {
        socialClientUseCase.uploadWxaOrderShippingInfo(userType, reqDTO);
        return success(true);
    }

    @Override
    public CommonResult<Boolean> notifyWxaOrderConfirmReceive(Integer userType, SocialWxaOrderNotifyConfirmReceiveReqDTO reqDTO) {
        socialClientUseCase.notifyWxaOrderConfirmReceive(userType, reqDTO);
        return success(true);
    }

}
