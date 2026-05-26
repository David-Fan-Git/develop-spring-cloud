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

    @Test
    void productDddSkeletonUsesStandardPackages() {
        assertPresent("com.develop.mvp.pk.module.iot.domain.product.model.IotProduct");
        assertPresent("com.develop.mvp.pk.module.iot.domain.product.valueobject.IotProductId");
        assertPresent("com.develop.mvp.pk.module.iot.domain.product.valueobject.IotProductKey");
        assertPresent("com.develop.mvp.pk.module.iot.domain.product.valueobject.IotProductSecret");
        assertPresent("com.develop.mvp.pk.module.iot.domain.product.valueobject.IotProductStatus");
        assertPresent("com.develop.mvp.pk.module.iot.domain.product.event.IotProductCreatedEvent");
        assertPresent("com.develop.mvp.pk.module.iot.domain.product.event.IotProductPublishedEvent");
        assertPresent("com.develop.mvp.pk.module.iot.domain.product.service.IotProductPolicy");
        assertPresent("com.develop.mvp.pk.module.iot.domain.product.repository.IotProductRepository");
        assertPresent("com.develop.mvp.pk.module.iot.application.product.command.CreateIotProductCommand");
        assertPresent("com.develop.mvp.pk.module.iot.application.product.command.UpdateIotProductCommand");
        assertPresent("com.develop.mvp.pk.module.iot.application.product.command.UpdateIotProductStatusCommand");
        assertPresent("com.develop.mvp.pk.module.iot.application.product.query.IotProductPageQuery");
        assertPresent("com.develop.mvp.pk.module.iot.application.product.result.IotProductResult");
        assertPresent("com.develop.mvp.pk.module.iot.application.product.port.inbound.IotProductUseCase");
        assertPresent("com.develop.mvp.pk.module.iot.application.product.port.outbound.IotProductPropertyTablePort");
        assertPresent("com.develop.mvp.pk.module.iot.application.product.service.IotProductApplicationService");
        assertPresent("com.develop.mvp.pk.module.iot.infrastructure.product.persistence.IotProductRepositoryImpl");
        assertPresent("com.develop.mvp.pk.module.iot.infrastructure.product.external.package-info");
        assertPresent("com.develop.mvp.pk.module.iot.infrastructure.product.rpc.package-info");
        assertPresent("com.develop.mvp.pk.module.iot.infrastructure.product.cache.package-info");
        assertPresent("com.develop.mvp.pk.module.iot.infrastructure.product.messaging.package-info");
    }

    private static void assertPresent(String className) {
        assertDoesNotThrow(() -> Class.forName(className), className + " should exist");
    }

}
