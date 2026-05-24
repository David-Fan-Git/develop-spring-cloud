package com.develop.mvp.pk.framework.test.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public final class DevelopArchitectureRules {

    private DevelopArchitectureRules() {
    }

    public static JavaClasses importPackages(String basePackage) {
        return new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(basePackage);
    }

    public static void verifyDddRuntimeUnit(String basePackage) {
        JavaClasses classes = importPackages(basePackage);
        domainShouldStayPure().check(classes);
        applicationShouldNotDependOnEntryOrInfrastructure().check(classes);
        entryLayersShouldNotAccessPersistenceDirectly().check(classes);
        apiModuleShouldNotContainRuntimeImplementation().check(classes);
    }

    public static ArchRule domainShouldStayPure() {
        return noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..",
                        "com.baomidou.mybatisplus..",
                        "org.apache.ibatis..",
                        "jakarta.persistence..",
                        "javax.persistence..",
                        "..controller..",
                        "..dal..",
                        "..infrastructure..",
                        "..remote.."
                )
                .allowEmptyShould(true);
    }

    public static ArchRule applicationShouldNotDependOnEntryOrInfrastructure() {
        return noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..controller..",
                        "..job..",
                        "..mq..",
                        "..infrastructure..",
                        "..dal.."
                )
                .allowEmptyShould(true);
    }

    public static ArchRule entryLayersShouldNotAccessPersistenceDirectly() {
        return noClasses()
                .that().resideInAnyPackage("..controller..", "..job..", "..mq..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..dal..",
                        "com.baomidou.mybatisplus..",
                        "org.apache.ibatis.."
                )
                .allowEmptyShould(true);
    }

    public static ArchRule apiModuleShouldNotContainRuntimeImplementation() {
        return noClasses()
                .that().resideInAPackage("..api..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..service..",
                        "..application..",
                        "..domain..",
                        "..infrastructure..",
                        "..dal..",
                        "com.baomidou.mybatisplus..",
                        "org.apache.ibatis.."
                )
                .allowEmptyShould(true);
    }
}
