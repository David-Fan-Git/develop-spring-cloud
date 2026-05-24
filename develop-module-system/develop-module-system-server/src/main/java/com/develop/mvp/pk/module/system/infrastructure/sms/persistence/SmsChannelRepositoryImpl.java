package com.develop.mvp.pk.module.system.infrastructure.sms.persistence;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.controller.admin.sms.vo.channel.SmsChannelPageReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.sms.SmsChannelDO;
import com.develop.mvp.pk.module.system.dal.mysql.sms.SmsChannelMapper;
import com.develop.mvp.pk.module.system.domain.sms.SmsChannel;
import com.develop.mvp.pk.module.system.domain.sms.repository.SmsChannelRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class SmsChannelRepositoryImpl implements SmsChannelRepository {

	private final SmsChannelMapper mapper;

	public SmsChannelRepositoryImpl(SmsChannelMapper mapper) {

		this.mapper = mapper;
	}

	@Override
	public SmsChannel save(SmsChannel c) {

		SmsChannelDO d = new SmsChannelDO();
		d.setId(c.id());
		d.setCode(c.code());
		d.setSignature(c.signature());
		d.setStatus(c.status());
		d.setApiKey(c.apiKey());
		d.setApiSecret(c.apiSecret());
		d.setCallbackUrl(c.callbackUrl());
		d.setRemark(c.remark());
		if (mapper.selectById(c.id()) == null)
			mapper.insert(d);
		else
			mapper.updateById(d);
		return c;
	}

	@Override
	public void delete(Long id) {

		mapper.deleteById(id);
	}

	@Override
	public SmsChannel findById(Long id) {

		SmsChannelDO d = mapper.selectById(id);
		return d != null ? toDomain(d) : null;
	}

	@Override
	public SmsChannel findByCode(String code) {

		SmsChannelDO d = mapper.selectByCode(code);
		return d != null ? toDomain(d) : null;
	}

	@Override
	public List<SmsChannel> findAll() {

		return mapper.selectList().stream().map(this::toDomain).toList();
	}

	@Override
	public PageResult<SmsChannel> findPage(String signature, Integer status, Integer pageNo, Integer pageSize) {

		var reqVO = new SmsChannelPageReqVO();
		reqVO.setSignature(signature);
		reqVO.setStatus(status);
		reqVO.setPageNo(pageNo);
		reqVO.setPageSize(pageSize);
		var dp = mapper.selectPage(reqVO);
		return new PageResult<>(dp.getList().stream().map(this::toDomain).toList(), dp.getTotal());
	}

	private SmsChannel toDomain(SmsChannelDO d) {

		return SmsChannel.of(d.getId(), d.getCode(), d.getSignature()).status(d.getStatus()).apiKey(d.getApiKey()).apiSecret(d.getApiSecret()).callbackUrl(d.getCallbackUrl()).remark(d.getRemark());
	}

}
