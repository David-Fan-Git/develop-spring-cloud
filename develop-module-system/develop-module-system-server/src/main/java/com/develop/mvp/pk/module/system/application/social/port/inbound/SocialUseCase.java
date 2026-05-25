package com.develop.mvp.pk.module.system.application.social.port.inbound;

import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.api.social.dto.SocialUserBindReqDTO;
import com.develop.mvp.pk.module.system.api.social.dto.SocialUserRespDTO;
import com.develop.mvp.pk.module.system.api.social.dto.SocialWxQrcodeReqDTO;
import com.develop.mvp.pk.module.system.api.social.dto.SocialWxaOrderNotifyConfirmReceiveReqDTO;
import com.develop.mvp.pk.module.system.api.social.dto.SocialWxaOrderUploadShippingInfoReqDTO;
import com.develop.mvp.pk.module.system.api.social.dto.SocialWxaSubscribeMessageSendReqDTO;
import com.develop.mvp.pk.module.system.controller.admin.socail.vo.client.SocialClientPageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.socail.vo.client.SocialClientSaveReqVO;
import com.develop.mvp.pk.module.system.controller.admin.socail.vo.user.SocialUserPageReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.social.SocialClientDO;
import com.develop.mvp.pk.module.system.dal.dataobject.social.SocialUserDO;
import jakarta.validation.Valid;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.subscribemsg.TemplateInfo;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;

import java.util.List;

/**
 * social use-case boundary for legacy service compatibility and future adapters.
 */
public interface SocialUseCase {

    String getAuthorizeUrl(Integer socialType, Integer userType, String redirectUri);

    AuthUser getAuthUser(Integer socialType, Integer userType, String code, String state);

    AuthRequest buildAuthRequest(Integer socialType, Integer userType);

    WxJsapiSignature createWxMpJsapiSignature(Integer userType, String url);

    WxMaPhoneNumberInfo getWxMaPhoneNumberInfo(Integer userType, String phoneCode);

    byte[] getWxaQrcode(SocialWxQrcodeReqDTO reqVO);

    List<TemplateInfo> getSubscribeTemplateList(Integer userType);

    void sendSubscribeMessage(SocialWxaSubscribeMessageSendReqDTO reqDTO, String templateId, String openId);

    void uploadWxaOrderShippingInfo(Integer userType, SocialWxaOrderUploadShippingInfoReqDTO reqDTO);

    void notifyWxaOrderConfirmReceive(Integer userType, SocialWxaOrderNotifyConfirmReceiveReqDTO reqDTO);

    Long createSocialClient(@Valid SocialClientSaveReqVO createReqVO);

    void updateSocialClient(@Valid SocialClientSaveReqVO updateReqVO);

    void deleteSocialClient(Long id);

    void deleteSocialClientList(List<Long> ids);

    SocialClientDO getSocialClient(Long id);

    PageResult<SocialClientDO> getSocialClientPage(SocialClientPageReqVO pageReqVO);

    List<SocialUserDO> getSocialUserList(Long userId, Integer userType);

    String bindSocialUser(@Valid SocialUserBindReqDTO reqDTO);

    void unbindSocialUser(Long userId, Integer userType, Integer socialType, String openid);

    SocialUserRespDTO getSocialUserByUserId(Integer userType, Long userId, Integer socialType);

    SocialUserRespDTO getSocialUserByCode(Integer userType, Integer socialType, String code, String state);

    SocialUserDO authSocialUser(Integer socialType, Integer userType, String code, String state);

    SocialUserDO getSocialUser(Long id);

    PageResult<SocialUserDO> getSocialUserPage(SocialUserPageReqVO pageReqVO);
}
