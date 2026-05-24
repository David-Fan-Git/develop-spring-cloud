package com.develop.mvp.pk.module.system.domain.sms;
// DDD 角色：短信渠道聚合根

import java.util.Objects;

public final class SmsChannel {

	private final Long id;

	private final String code, signature;

	private Integer status;

	private String apiKey, apiSecret, callbackUrl, remark;

	private SmsChannel(Long id, String code, String signature) {

		this.id = Objects.requireNonNull(id);
		this.code = Objects.requireNonNull(code);
		this.signature = Objects.requireNonNull(signature);
	}

	public static SmsChannel of(Long id, String code, String sig) {

		return new SmsChannel(id, code, sig);
	}

	public Long id() {

		return id;
	}

	public String code() {

		return code;
	}

	public String signature() {

		return signature;
	}

	public Integer status() {

		return status;
	}

	public String apiKey() {

		return apiKey;
	}

	public String apiSecret() {

		return apiSecret;
	}

	public String callbackUrl() {

		return callbackUrl;
	}

	public String remark() {

		return remark;
	}

	public SmsChannel status(Integer v) {

		status = v;
		return this;
	}

	public SmsChannel apiKey(String v) {

		apiKey = v;
		return this;
	}

	public SmsChannel apiSecret(String v) {

		apiSecret = v;
		return this;
	}

	public SmsChannel callbackUrl(String v) {

		callbackUrl = v;
		return this;
	}

	public SmsChannel remark(String v) {

		remark = v;
		return this;
	}

	@Override
	public boolean equals(Object o) {

		return o instanceof SmsChannel c && id.equals(c.id);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id);
	}

}
