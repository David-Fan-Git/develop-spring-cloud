package com.develop.mvp.pk.module.system.architecture;

import com.develop.mvp.pk.framework.test.architecture.DevelopArchitectureRules;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SystemArchitectureTest {

    @Test
    void dddRuntimeUnitShouldRespectLayerBoundaries() {
        DevelopArchitectureRules.verifyDddRuntimeUnit("com.develop.mvp.pk.module.system.domain");
    }

    @Test
    void permissionShouldExposeStandardDddSkeleton() {
        Path sourceRoot = mainSourceRoot();
        List<String> requiredPackages = List.of(
                "domain/permission/model",
                "domain/permission/valueobject",
                "domain/permission/event",
                "domain/permission/service",
                "domain/permission/repository",
                "application/permission/command",
                "application/permission/query",
                "application/permission/dto",
                "application/permission/port/inbound",
                "application/permission/port/outbound",
                "application/permission/service",
                "infrastructure/permission/persistence",
                "infrastructure/permission/external",
                "infrastructure/permission/rpc",
                "infrastructure/permission/cache",
                "infrastructure/permission/messaging",
                "convert/permission",
                "controller/admin/permission",
                "job",
                "mq",
                "framework"
        );

        for (String requiredPackage : requiredPackages) {
            assertTrue(Files.isDirectory(sourceRoot.resolve(requiredPackage)),
                    () -> "Missing permission DDD skeleton package: " + requiredPackage);
        }
    }

    private Path mainSourceRoot() {
        Path workingDirectory = Path.of(System.getProperty("user.dir"));
        Path moduleSourceRoot = workingDirectory.resolve("src/main/java/com/develop/mvp/pk/module/system");
        if (Files.isDirectory(moduleSourceRoot)) {
            return moduleSourceRoot;
        }
        return workingDirectory.resolve("develop-module-system/develop-module-system-server/src/main/java/com/develop/mvp/pk/module/system");
    }
}
