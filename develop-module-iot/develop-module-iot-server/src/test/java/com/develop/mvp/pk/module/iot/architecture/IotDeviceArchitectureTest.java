package com.develop.mvp.pk.module.iot.architecture;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class IotDeviceArchitectureTest {

    @Test
    void deviceDddSkeletonUsesStandardPackages() {
        assertPresent("com.develop.mvp.pk.module.iot.application.device.port.inbound.IotDeviceUseCase");
        assertPresent("com.develop.mvp.pk.module.iot.application.device.service.IotDeviceApplicationService");
        assertPresent("com.develop.mvp.pk.module.iot.domain.device.repository.IotDeviceRepository");
        assertPresent("com.develop.mvp.pk.module.iot.infrastructure.device.persistence.IotDeviceRepositoryImpl");
        assertPresent("com.develop.mvp.pk.module.iot.application.device.port.outbound.package-info");
        assertPresent("com.develop.mvp.pk.module.iot.infrastructure.device.external.package-info");
        assertPresent("com.develop.mvp.pk.module.iot.infrastructure.device.rpc.package-info");
        assertPresent("com.develop.mvp.pk.module.iot.infrastructure.device.cache.package-info");
        assertPresent("com.develop.mvp.pk.module.iot.infrastructure.device.messaging.package-info");
    }

    private static void assertPresent(String className) {
        assertDoesNotThrow(() -> Class.forName(className), className + " should exist");
    }

}
