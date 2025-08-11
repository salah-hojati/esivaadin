<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>${springBootVersion}</version>
        <relativePath/>
    </parent>
    <groupId>com.example</groupId>
    <artifactId>${project.name?lower_case}</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>${project.name}</name>

    <properties>
        <java.version>${project.javaVersion}</java.version>
        <#if project.framework == "Vaadin">
            <vaadin.version>${r"${vaadin.version}"}</vaadin.version>
        </#if>
    </properties>

    <dependencies>
        <!-- Core Dependencies -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>

        <#if project.projectType == "Web" || project.projectType == "API" || project.framework == "Spring MVC" || project.framework == "Spring Rest">
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
            </dependency>
        </#if>
        <#if project.framework == "Vaadin">
            <dependency>
                <groupId>com.vaadin</groupId>
                <artifactId>vaadin-spring-boot-starter</artifactId>
            </dependency>
        </#if>
        <#if project.framework == "Spring Batch">
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-batch</artifactId>
            </dependency>
        </#if>
    </dependencies>

    <#if project.framework == "Vaadin">
        <dependencyManagement>
            <dependencies>
                <dependency>
                    <groupId>com.vaadin</groupId>
                    <artifactId>vaadin-bom</artifactId>
                    <version>${r"${vaadin.version}"}</version>
                    <type>pom</type>
                    <scope>import</scope>
                </dependency>
            </dependencies>
        </dependencyManagement>
    </#if>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

    <#if project.framework == "Vaadin">
        <profiles>
            <profile>
                <id>production</id>
                <build>
                    <plugins>
                        <plugin>
                            <groupId>com.vaadin</groupId>
                            <artifactId>vaadin-maven-plugin</artifactId>
                            <version>${r"${vaadin.version}"}</version>
                            <executions>
                                <execution>
                                    <id>frontend</id>
                                    <phase>compile</phase>
                                    <goals>
                                        <goal>prepare-frontend</goal>
                                        <goal>build-frontend</goal>
                                    </goals>
                                </execution>
                            </executions>
                        </plugin>
                    </plugins>
                </build>
            </profile>
        </profiles>
    </#if>

</project>