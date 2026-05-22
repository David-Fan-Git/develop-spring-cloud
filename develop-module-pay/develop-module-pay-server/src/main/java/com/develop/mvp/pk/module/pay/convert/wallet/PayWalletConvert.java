package com.develop.mvp.pk.module.pay.convert.wallet;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.pay.controller.admin.wallet.vo.wallet.PayWalletRespVO;
import com.develop.mvp.pk.module.pay.controller.app.wallet.vo.wallet.AppPayWalletRespVO;
import com.develop.mvp.pk.module.pay.dal.dataobject.wallet.PayWalletDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PayWalletConvert {

    PayWalletConvert INSTANCE = Mappers.getMapper(PayWalletConvert.class);

    AppPayWalletRespVO convert(PayWalletDO bean);

    PayWalletRespVO convert02(PayWalletDO bean);

    PageResult<PayWalletRespVO> convertPage(PageResult<PayWalletDO> page);

}
