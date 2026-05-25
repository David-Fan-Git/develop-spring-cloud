package com.develop.mvp.pk.module.system.application.member.service;

import cn.hutool.core.util.ReflectUtil;
import com.develop.mvp.pk.module.system.application.member.port.inbound.MemberUseCase;
import com.develop.mvp.pk.module.system.application.member.port.outbound.MemberUserGateway;
import org.springframework.stereotype.Service;

@Service
public class MemberApplicationService implements MemberUseCase {

    private final MemberUserGateway memberUserGateway;

    public MemberApplicationService(MemberUserGateway memberUserGateway) {
        this.memberUserGateway = memberUserGateway;
    }

    public String getMemberUserMobile(Long id) {
        Object user = getMemberUser(id);
        if (user == null) {
            return null;
        }
        return ReflectUtil.invoke(user, "getMobile");
    }

    public String getMemberUserEmail(Long id) {
        Object user = getMemberUser(id);
        if (user == null) {
            return null;
        }
        return ReflectUtil.invoke(user, "getEmail");
    }

    private Object getMemberUser(Long id) {
        if (id == null) {
            return null;
        }
        return memberUserGateway.getMemberUser(id);
    }
}
