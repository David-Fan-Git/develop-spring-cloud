package com.develop.mvp.pk.module.system.application.member.port.inbound;

/**
 * member use-case boundary for legacy service compatibility and future adapters.
 */
public interface MemberUseCase {

    String getMemberUserMobile(Long id);

    String getMemberUserEmail(Long id);
}
