/*
 * Copyright (c) 2025. Vitalii Novik
 */

package org.example.lab5;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.tngtech.archunit.core.domain.JavaClasses;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@SpringBootTest
public class ArchitectureTests {
    private JavaClasses applicationClasses;

    @BeforeEach
    void initialize() {
        applicationClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_ARCHIVES)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("org.example.lab5");
    }

    @Test
    void shouldFollowLayeredArchitecture() {
        layeredArchitecture()
                .consideringAllDependencies()
                .layer("Controller").definedBy("..controllers..")
                .layer("Service").definedBy("..services..")
                .layer("Repository").definedBy("..repositories..")
                .layer("Model").definedBy("..models..")
                .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller", "Service")
                .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
                .whereLayer("Model").mayOnlyBeAccessedByLayers("Controller", "Service", "Repository")
                .check(applicationClasses);
    }

    @Test
    void controllersShouldNotDependOnOtherControllers() {
        noClasses()
                .that().resideInAPackage("..controllers..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..controllers..")
                .because("Controllers should not depend on each other")
                .check(applicationClasses);
    }

    @Test
    void repositoriesShouldNotDependOnServices() {
        noClasses()
                .that().resideInAPackage("..repositories..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..services..")
                .because("Repositories should not depend on services")
                .check(applicationClasses);
    }

    // 4. Services Should Not Depend on Controllers
    @Test
    void servicesShouldNotDependOnControllers() {
        noClasses()
                .that().resideInAPackage("..services..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..controllers..")
                .because("Services should not depend on controllers")
                .check(applicationClasses);
    }

    @Test
    void controllerClassesShouldBeNamedXController() {
        classes()
                .that().resideInAPackage("..controllers..")
                .should()
                .haveSimpleNameEndingWith("Controller")
                .because("Controller classes should follow naming convention")
                .check(applicationClasses);
    }

    @Test
    void controllerClassesShouldBeAnnotatedWithRestController() {
        classes()
                .that().resideInAPackage("..controllers..")
                .should()
                .beAnnotatedWith(RestController.class)
                .because("Controllers should be annotated with @RestController")
                .check(applicationClasses);
    }

    @Test
    void repositoryShouldBeInterface() {
        classes()
                .that().resideInAPackage("..repositories..")
                .should()
                .beInterfaces()
                .because("Repositories should be interfaces")
                .check(applicationClasses);
    }

    @Test
    void repositoryClassesShouldBeAnnotatedWithRepository() {
        classes()
                .that().resideInAPackage("..repositories..")
                .should()
                .beAnnotatedWith(Repository.class)
                .because("Repositories should be annotated with @Repository")
                .check(applicationClasses);
    }

    @Test
    void serviceClassesShouldBeNamedXService() {
        classes()
                .that().resideInAPackage("..services..")
                .should()
                .haveSimpleNameEndingWith("Service")
                .because("Service classes should follow naming convention")
                .check(applicationClasses);
    }

    // 10. Service Classes Should Be Annotated With Service
    @Test
    void serviceClassesShouldBeAnnotatedWithService() {
        classes()
                .that().resideInAPackage("..services..")
                .should()
                .beAnnotatedWith(Service.class)
                .because("Services should be annotated with @Service")
                .check(applicationClasses);
    }

    @Test
    void modelFieldsShouldBePrivate() {
        fields()
                .that().areDeclaredInClassesThat()
                .resideInAPackage("..models..")
                .should()
                .notBePublic()
                .because("Model fields should be private for encapsulation")
                .check(applicationClasses);
    }

    @Test
    void modelsShouldNotDependOnServices() {
        noClasses()
                .that().resideInAPackage("..models..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..services..")
                .because("Models should not depend on services")
                .check(applicationClasses);
    }

    @Test
    void modelsShouldNotDependOnControllers() {
        noClasses()
                .that().resideInAPackage("..models..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..controllers..")
                .because("Models should not depend on controllers")
                .check(applicationClasses);
    }

    @Test
    void modelsShouldNotDependOnRepositories() {
        noClasses()
                .that().resideInAPackage("..models..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..repositories..")
                .because("Models should not depend on repositories")
                .check(applicationClasses);
    }

    @Test
    void repositoriesShouldNotDependOnOtherLayers() {
        noClasses()
                .that().resideInAPackage("..repositories..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("..controllers..", "..services..")
                .because("Repositories should not depend on controllers, services, or models")
                .check(applicationClasses);
    }

    @Test
    void controllerMethodsShouldNotBePrivate() {
        methods()
                .that().areDeclaredInClassesThat()
                .resideInAPackage("..controllers..")
                .should()
                .notBePrivate()
                .because("Controller methods should be accessible")
                .check(applicationClasses);
    }

    @Test
    void serviceMethodsShouldNotDirectlyAccessRepositories() {
        noFields()
                .that().areDeclaredInClassesThat()
                .resideInAPackage("..services..")
                .should()
                .bePublic()
                .because("Service classes should not have public fields to ensure encapsulation")
                .check(applicationClasses);
    }

    @Test
    void noClassesShouldDependOnApplicationClass() {
        noClasses()
                .should()
                .dependOnClassesThat()
                .haveSimpleName("Lab5Application")
                .because("Classes should not depend on the main application class")
                .check(applicationClasses);
    }

    @Test
    void controllersShouldNotAccessModelsDirectly() {
        noClasses()
                .that().resideInAPackage("..controllers..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("com.fasterxml.jackson..", "org.hibernate..")
                .because("Controllers should not directly depend on external libraries like Jackson or Hibernate; use services instead")
                .check(applicationClasses);
    }

    @Test
    void allClassesShouldResideInCorrectPackages() {
        classes()
                .that().haveSimpleNameEndingWith("Controller")
                .should()
                .resideInAPackage("..controllers..")
                .because("Controller classes should reside in the controllers package")
                .check(applicationClasses);

        classes()
                .that().haveSimpleNameEndingWith("Service")
                .should()
                .resideInAPackage("..services..")
                .because("Service classes should reside in the services package")
                .check(applicationClasses);

        classes()
                .that().haveSimpleNameEndingWith("Repository")
                .should()
                .resideInAPackage("..repositories..")
                .because("Repository classes should reside in the repositories package")
                .check(applicationClasses);
    }
}
