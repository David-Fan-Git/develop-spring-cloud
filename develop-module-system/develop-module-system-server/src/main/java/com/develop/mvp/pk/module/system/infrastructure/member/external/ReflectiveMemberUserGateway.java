package com.develop.mvp.pk.module.system.infrastructure.member.external;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.develop.mvp.pk.module.system.application.member.port.outbound.MemberUserGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ReflectiveMemberUserGateway implements MemberUserGateway {

    @Value("${develop.info.base-package}")
    private String basePackage;

    private volatile Object memberUserApi;

    @Override
    public Object getMemberUser(Long id) {
        return ReflectUtil.invoke(getMemberUserApi(), "getUser", id);
    }

    private Object getMemberUserApi() {
        if (memberUserApi == null) {
            memberUserApi = SpringUtil.getBean(ClassUtil.loadClass(String.format("%s.module.member.api.user.MemberUserApi", basePackage)));
        }
        return memberUserApi;
    }
}
