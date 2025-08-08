plugins {
id 'java'
id 'org.springframework.boot' version '${springBootVersion}'
id 'io.spring.dependency-management' version '1.1.5'
<#if project.framework == "Vaadin">
    id 'com.vaadin' version '${vaadinVersion}'
</#if>
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'

java {
sourceCompatibility = '${project.javaVersion}'
}

repositories {
mavenCentral()
}

<#if project.framework == "Vaadin">
    dependencyManagement {
    imports {
    mavenBom "com.vaadin:vaadin-bom:${vaadinVersion}"
    }
    }
</#if>

dependencies {
// Core Dependencies
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
runtimeOnly 'com.h2database:h2'

<#if project.projectType == "Web" || project.projectType == "API" || project.framework == "Spring MVC" || project.framework == "Spring Rest">
    implementation 'org.springframework.boot:spring-boot-starter-web'
</#if>
<#if project.framework == "Vaadin">
    implementation 'com.vaadin:vaadin-spring-boot-starter'
</#if>
<#if project.framework == "Spring Batch">
    implementation 'org.springframework.boot:spring-boot-starter-batch'
</#if>
}

tasks.named('test') {
useJUnitPlatform()
}