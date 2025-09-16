INSERT INTO productdb.template (id, content, path, template_name) VALUES (1, '<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
<modelVersion>4.0.0</modelVersion>
<groupId>com.example</groupId>
<artifactId>${project.name?lower_case}</artifactId>
<version>0.0.1-SNAPSHOT</version>
<name>${project.name}</name>
</project>', 'pom.xml', 'pom-base');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (2, 'plugins {
id \'java\'
id \'org.springframework.boot\' version \'${springBootVersion}\'
id \'io.spring.dependency-management\' version \'1.1.5\'
<#if project.framework == "Vaadin">
id \'com.vaadin\' version \'${vaadinVersion}\'
</#if>
}
group = \'com.example\'
version = \'0.0.1-SNAPSHOT\'
java {
sourceCompatibility = \'${project.javaVersion}\'
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
implementation \'org.springframework.boot:spring-boot-starter-data-jpa\'
runtimeOnly \'com.h2database:h2\'
<#if project.projectType == "Web" || project.projectType == "API" || project.framework == "Spring MVC" || project.framework == "Spring Rest">
implementation \'org.springframework.boot:spring-boot-starter-web\'
</#if>
<#if project.framework == "Vaadin">
implementation \'com.vaadin:vaadin-spring-boot-starter\'
</#if>
<#if project.framework == "Spring Batch">
implementation \'org.springframework.boot:spring-boot-starter-batch\'
</#if>
}
tasks.named(\'test\') {
useJUnitPlatform()
}', 'build.gradle', 'build-gradle');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (10, '<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://maven.apache.org/POM/4.0.0"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
<modelVersion>4.0.0</modelVersion>
<groupId>com.example</groupId>
<artifactId>.${project.name?lower_case}</artifactId>
<packaging>war</packaging>
<version>1.0</version>
<name>${project.name?lower_case}</name>
<url>http://maven.apache.org</url>
<properties>
<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
<maven.compiler.source>1.8</maven.compiler.source>
<maven.compiler.target>1.8</maven.compiler.target>
<jersey.version>2.22.4</jersey.version>
<hibernate.version>4.3.6.Final</hibernate.version>
</properties>
<dependencies>
<!-- Java EE API -->
<dependency>
<groupId>javax</groupId>
<artifactId>javaee-api</artifactId>
<version>7.0</version>
<scope>provided</scope>
</dependency>
<dependency>
<groupId>org.hibernate</groupId>
<artifactId>hibernate-entitymanager</artifactId>
<version>${r"${hibernate.version}"}</version>
</dependency>
<dependency>
<groupId>org.hibernate</groupId>
<artifactId>hibernate-jpamodelgen</artifactId>
<version>${r"${hibernate.version}"}</version>
</dependency>
<dependency>
<groupId>org.glassfish.jersey.core</groupId>
<artifactId>jersey-client</artifactId>
<version>${r"${jersey.version}"}</version>
</dependency>
<dependency>
<groupId>org.glassfish.jersey.containers</groupId>
<artifactId>jersey-container-servlet-core</artifactId>
<version>${r"${jersey.version}"}</version>
</dependency>
<dependency>
<groupId>org.glassfish.jersey.containers</groupId>
<artifactId>jersey-container-servlet</artifactId>
<version>${r"${jersey.version}"}</version>
</dependency>
<dependency>
<groupId>com.fasterxml.jackson.core</groupId>
<artifactId>jackson-annotations</artifactId>
<version>2.17.0</version>
</dependency>
<dependency>
<groupId>org.glassfish.jersey.media</groupId>
<artifactId>jersey-media-multipart</artifactId>
<version>${r"${jersey.version}"}</version>
</dependency>
<dependency>
<groupId>org.glassfish.jersey.media</groupId>
<artifactId>jersey-media-json-jackson</artifactId>
<version>${r"${jersey.version}"}</version>
</dependency>
<dependency>
<groupId>com.fasterxml.jackson.core</groupId>
<artifactId>jackson-databind</artifactId>
<version>2.17.0</version>
</dependency>
<dependency>
<groupId>com.fasterxml.jackson.dataformat</groupId>
<artifactId>jackson-dataformat-xml</artifactId>
<version>2.17.0</version>
</dependency>
<!-- Logging -->
<dependency>
<groupId>org.apache.commons</groupId>
<artifactId>commons-lang3</artifactId>
<version>3.6</version>
</dependency>
<dependency>
<groupId>commons-codec</groupId>
<artifactId>commons-codec</artifactId>
<version>1.5</version>
</dependency>
<dependency>
<groupId>org.apache.logging.log4j</groupId>
<artifactId>log4j-core</artifactId>
<version>2.17.1</version>
</dependency>
<dependency>
<groupId>org.apache.logging.log4j</groupId>
<artifactId>log4j-api</artifactId>
<version>2.17.1</version>
</dependency>
<dependency>
<groupId>org.apache.logging.log4j</groupId>
<artifactId>log4j-web</artifactId>
<version>2.17.1</version>
</dependency>
<!-- Test -->
<dependency>
<groupId>junit</groupId>
<artifactId>junit</artifactId>
<version>4.12</version>
<scope>test</scope>
</dependency>
<!-- JSON -->
<dependency>
<groupId>com.googlecode.json-simple</groupId>
<artifactId>json-simple</artifactId>
<version>1.1.1</version>
</dependency>
<!-- Lombok -->
<dependency>
<groupId>org.projectlombok</groupId>
<artifactId>lombok</artifactId>
<version>1.18.28</version>
<scope>provided</scope>
</dependency>
<dependency>
<groupId>org.apache.commons</groupId>
<artifactId>commons-collections4</artifactId>
<version>4.4</version>
</dependency>
</dependencies>
<build>
<finalName>ProbateCertificateGenerator.1.1.0.5</finalName>
<plugins>
<plugin>
<groupId>org.apache.maven.plugins</groupId>
<artifactId>maven-war-plugin</artifactId>
<version>3.3.2</version>
<configuration>
<warSourceDirectory>src/main/webapp</warSourceDirectory>
</configuration>
</plugin>
</plugins>
<resources>
<resource>
<directory>src/main/resources</directory>
<includes>
<include>*.*</include>
</includes>
</resource>
<resource>
<directory>src/main/resources/META-INF</directory>
<targetPath>META-INF</targetPath>
</resource>
<resource>
<directory>src/main/resources/jasper-report/pic</directory>
<includes>
<include>*.*</include>
</includes>
</resource>
<resource>
<directory>src/main/resources/jasper-report</directory>
<includes>
<include>*.*</include>
</includes>
</resource>
</resources>
</build>
</project>
', 'pom.xml', 'pom-jasper-jee');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (11, '<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
<modelVersion>4.0.0</modelVersion>
<groupId>com.example..${project.name?lower_case}</groupId>
<artifactId>${project.name?lower_case}</artifactId>
<version>1.0-SNAPSHOT</version>
<packaging>war</packaging>
<build>
<finalName> ${project.name?lower_case}</finalName>
<plugins>
</plugins>
</build>
<dependencies>
<dependency>
<groupId>javax</groupId>
<artifactId>javaee-api</artifactId>
<scope>provided</scope>
</dependency>
<dependency>
<groupId>org.hibernate</groupId>
<artifactId>hibernate-entitymanager</artifactId>
<version>4.3.6.Final</version>
</dependency>
<dependency>
<groupId>org.hibernate</groupId>
<artifactId>hibernate-jpamodelgen</artifactId>
<version>4.3.6.Final</version>
</dependency>
<dependency>
<groupId>org.slf4j</groupId>
<artifactId>slf4j-api</artifactId>
<version>${r"${org.slf4j-version}"}</version>
</dependency>
<dependency>
<groupId>org.apache.logging.log4j</groupId>
<artifactId>log4j-core</artifactId>
<version>2.11.0</version>
</dependency>
<dependency>
<groupId>org.apache.logging.log4j</groupId>
<artifactId>log4j-api</artifactId>
<version>2.11.0</version>
</dependency>
<dependency>
<groupId>org.primefaces</groupId>
<artifactId>primefaces</artifactId>
<version>6.2</version>
</dependency>
</dependencies>
<dependencyManagement>
<dependencies>
<dependency>
<groupId>javax</groupId>
<artifactId>javaee-api</artifactId>
<version>7.0</version>
<scope>provided</scope>
</dependency>
</dependencies>
</dependencyManagement>
<properties>
<failOnMissingWebXml>false</failOnMissingWebXml>
<maven.compiler.source>1.8</maven.compiler.source>
<maven.compiler.target>1.8</maven.compiler.target>
<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
<org.slf4j-version>1.7.7</org.slf4j-version>
</properties>
</project>', 'pom.xml', 'pom-job-jee');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (18, '<?xml version="1.0" encoding="UTF-8"?>
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
<java.version>${javaVersion}</java.version>
<#if frameworks?exists && frameworks?seq_contains("Vaadin")>
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
<#if (project.projectType == "Web" || project.projectType == "API") || (frameworks?exists && (frameworks?seq_contains("Spring MVC") || frameworks?seq_contains("Spring Rest")))>
<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-web</artifactId>
</dependency>
</#if>
<#if frameworks?exists && frameworks?seq_contains("Vaadin")>
<dependency>
<groupId>com.vaadin</groupId>
<artifactId>vaadin-spring-boot-starter</artifactId>
</dependency>
</#if>
<#if frameworks?exists && frameworks?seq_contains("Spring Batch")>
<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-batch</artifactId>
</dependency>
</#if>
</dependencies>
<#if frameworks?exists && frameworks?seq_contains("Vaadin")>
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
<#if frameworks?exists && frameworks?seq_contains("Vaadin")>
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
</project>', 'pom.xml', 'pom');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (19, '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="3.1" xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd">
<display-name>changeAddressJob</display-name>
<!-- Welcome page -->
<welcome-file-list>
<welcome-file>index.xhtml</welcome-file>
</welcome-file-list>
<!-- JSF mapping -->
<servlet>
<servlet-name>Faces Servlet</servlet-name>
<servlet-class>javax.faces.webapp.FacesServlet</servlet-class>
<load-on-startup>1</load-on-startup>
</servlet>
<servlet-mapping>
<servlet-name>Faces Servlet</servlet-name>
<url-pattern>*.xhtml</url-pattern>
</servlet-mapping>
<session-config>
<session-timeout>-1</session-timeout>
</session-config>
<error-page>
<exception-type>javax.faces.application.ViewExpiredException</exception-type>
<location>/index.xhtml?faces-redirect=true</location>
</error-page>
<mime-mapping>
<extension>eot</extension>
<mime-type>application/vnd.ms-fontobject</mime-type>
</mime-mapping>
<mime-mapping>
<extension>otf</extension>
<mime-type>application/x-font-opentype</mime-type>
</mime-mapping>
<mime-mapping>
<extension>ttf</extension>
<mime-type>application/x-font-ttf</mime-type>
</mime-mapping>
<mime-mapping>
<extension>woff</extension>
<mime-type>application/x-font-woff</mime-type>
</mime-mapping>
<mime-mapping>
<extension>svg</extension>
<mime-type>image/svg+xml</mime-type>
</mime-mapping>
<!--=====================================================================-->
</web-app>
', 'src/main/webapp/WEB-INF/web.xml', 'webXml_job');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (20, '<?xml version=\'1.0\' encoding=\'UTF-8\'?>
<weblogic-web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xmlns="http://xmlns.oracle.com/weblogic/weblogic-web-app"
xsi:schemaLocation="http://xmlns.oracle.com/weblogic/weblogic-web-app http://xmlns.oracle.com/weblogic/weblogic-web-app/1.4/weblogic-web-app.xsd">
<context-root>/${project.name?lower_case}</context-root>
</weblogic-web-app>', 'src/main/webapp/WEB-INF/weblogic.xml', 'weblogic.xml');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (21, '<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xmlns:web="http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
xmlns="http://java.sun.com/xml/ns/javaee"
xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
id="WebApp_ID" version="2.5">
<welcome-file-list>
<welcome-file>index.xhtml</welcome-file>
</welcome-file-list>
</web-app>', 'src/main/webapp/WEB-INF/web.xml', 'web.xml_simple');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (22, '<?xml version=\'1.0\' encoding=\'UTF-8\'?>
<faces-config version="2.2" xmlns="http://xmlns.jcp.org/xml/ns/javaee"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
http://xmlns.jcp.org/xml/ns/javaee/web-facesconfig_2_2.xsd">
<name>JavaServerFaces</name>
<navigation-rule>
<from-view-id>/*</from-view-id>
<navigation-case>
<from-outcome>home</from-outcome>
<to-view-id>/index.xhtml</to-view-id>
<redirect/>
</navigation-case>
</navigation-rule>
<application>
<resource-bundle>
<base-name>messages.Message</base-name>
<var>msg</var>
</resource-bundle>
<locale-config>
<default-locale>fa</default-locale>
<supported-locale>en</supported-locale>
</locale-config>
</application>
</faces-config>', 'src/main/webapp/WEB-INF/faces-config.xml', 'faces-config.xml-job');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (23, '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<beans
xmlns="http://xmlns.jcp.org/xml/ns/javaee"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
http://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd"
bean-discovery-mode="all">
</beans>', 'src/main/webapp/WEB-INF/beans.xml', 'beans.xml');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (24, '<!DOCTYPE composition PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<ui:composition xmlns="http://www.w3.org/1999/xhtml"
xmlns:h="http://java.sun.com/jsf/html"
xmlns:ui="http://xmlns.jcp.org/jsf/facelets" xmlns:p="http://java.sun.com/jsf/html">
<div class="form-header header-primary align-center">
<h:graphicImage styleClass="spacer-t10" name="logo.png" width="70px" height="70px" library="images"/>
<h:outputText value="&lt;br /&gt;" escape="false"/>
<h4 class="align-center p-title">
<!-- <ui:insert  name="subheader"/>-->
${r"#{msg.transfer_to_estelam}"}
</h4>
</div>
</ui:composition>', 'src/main/webapp/layout/top.xhtml', 'top.xhtml');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (27, '<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://maven.apache.org/POM/4.0.0"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
<modelVersion>4.0.0</modelVersion>
<groupId>com.farafan</groupId>
<artifactId>pdfGenerate</artifactId>
<packaging>war</packaging>
<version>1.0</version>
<name>${project.name}</name>
<url>http://maven.apache.org</url>
<properties>
<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
<maven.compiler.source>1.8</maven.compiler.source>
<maven.compiler.target>1.8</maven.compiler.target>
<jersey.version>2.22.4</jersey.version>
<hibernate.version>4.3.6.Final</hibernate.version>
</properties>
<dependencies>
<!-- Java EE API -->
<dependency>
<groupId>javax</groupId>
<artifactId>javaee-api</artifactId>
<version>7.0</version>
<scope>provided</scope>
</dependency>
<!--########################## HIBERNATE ###############################-->
<dependency>
<groupId>org.hibernate</groupId>
<artifactId>hibernate-entitymanager</artifactId>
<version>${r"${hibernate.version}"}</version>
</dependency>
<dependency>
<groupId>org.hibernate</groupId>
<artifactId>hibernate-jpamodelgen</artifactId>
<version>${r"${hibernate.version}"}</version>
</dependency>
<!--########################### JAKSON ########################################-->
<dependency>
<groupId>org.glassfish.jersey.core</groupId>
<artifactId>jersey-client</artifactId>
<version>${r"${jersey.version}"}</version>
</dependency>
<dependency>
<groupId>org.glassfish.jersey.containers</groupId>
<artifactId>jersey-container-servlet-core</artifactId>
<version>${r"${jersey.version}"}</version>
</dependency>
<dependency>
<groupId>org.glassfish.jersey.containers</groupId>
<artifactId>jersey-container-servlet</artifactId>
<version>${r"${jersey.version}"}</version>
</dependency>
<dependency>
<groupId>com.fasterxml.jackson.core</groupId>
<artifactId>jackson-annotations</artifactId>
<version>2.17.0</version>
</dependency>
<dependency>
<groupId>org.glassfish.jersey.media</groupId>
<artifactId>jersey-media-multipart</artifactId>
<version>${r"${jersey.version}"}</version>
</dependency>
<dependency>
<groupId>org.glassfish.jersey.media</groupId>
<artifactId>jersey-media-json-jackson</artifactId>
<version>${r"${jersey.version}"}</version>
</dependency>
<dependency>
<groupId>com.fasterxml.jackson.core</groupId>
<artifactId>jackson-databind</artifactId>
<version>2.17.0</version>
</dependency>
<dependency>
<groupId>com.fasterxml.jackson.dataformat</groupId>
<artifactId>jackson-dataformat-xml</artifactId>
<version>2.17.0</version>
</dependency>
<!-- Logging -->
<dependency>
<groupId>org.apache.commons</groupId>
<artifactId>commons-lang3</artifactId>
<version>3.6</version>
</dependency>
<dependency>
<groupId>commons-codec</groupId>
<artifactId>commons-codec</artifactId>
<version>1.5</version>
</dependency>
<!--##################### LOG4J###############################-->
<dependency>
<groupId>org.apache.logging.log4j</groupId>
<artifactId>log4j-core</artifactId>
<version>2.17.1</version>
</dependency>
<dependency>
<groupId>org.apache.logging.log4j</groupId>
<artifactId>log4j-api</artifactId>
<version>2.17.1</version>
</dependency>
<dependency>
<groupId>org.apache.logging.log4j</groupId>
<artifactId>log4j-web</artifactId>
<version>2.17.1</version>
</dependency>
<!-- Test -->
<dependency>
<groupId>junit</groupId>
<artifactId>junit</artifactId>
<version>4.12</version>
<scope>test</scope>
</dependency>
<!-- JSON -->
<dependency>
<groupId>com.googlecode.json-simple</groupId>
<artifactId>json-simple</artifactId>
<version>1.1.1</version>
</dependency>
<!-- Lombok -->
<dependency>
<groupId>org.projectlombok</groupId>
<artifactId>lombok</artifactId>
<version>1.18.28</version>
<scope>provided</scope>
</dependency>
<dependency>
<groupId>org.apache.commons</groupId>
<artifactId>commons-collections4</artifactId>
<version>4.4</version>
</dependency>
</dependencies>
<build>
<finalName>${project.name}</finalName>
<plugins>
<plugin>
<groupId>org.apache.maven.plugins</groupId>
<artifactId>maven-war-plugin</artifactId>
<version>3.3.2</version>
<configuration>
<warSourceDirectory>src/main/webapp</warSourceDirectory>
</configuration>
</plugin>
</plugins>
<resources>
<resource>
<directory>src/main/resources</directory>
<includes>
<include>*.*</include>
</includes>
</resource>
<resource>
<directory>src/main/resources/META-INF</directory>
<targetPath>META-INF</targetPath>
</resource>
<resource>
<directory>src/main/resources/jasper-report/pic</directory>
<includes>
<include>*.*</include>
</includes>
</resource>
<resource>
<directory>src/main/resources/jasper-report</directory>
<includes>
<include>*.*</include>
</includes>
</resource>
</resources>
</build>
</project>', 'pom.xml', 'pom-JAX-RS');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (29, '<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
<modelVersion>4.0.0</modelVersion>
<parent>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-parent</artifactId>
<version>3.5.4</version>
<relativePath/> <!-- lookup parent from repository -->
</parent>
<groupId>org.example.akka</groupId>
<artifactId>demo1</artifactId>
<version>0.0.1-SNAPSHOT</version>
<name>demo1</name>
<description>demo1</description>
<url/>
<licenses>
<license/>
</licenses>
<developers>
<developer/>
</developers>
<scm>
<connection/>
<developerConnection/>
<tag/>
<url/>
</scm>
<properties>
<java.version>17</java.version>
</properties>
<dependencies>
<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<!-- H2 In-Memory Database -->
<dependency>
<groupId>com.h2database</groupId>
<artifactId>h2</artifactId>
<scope>runtime</scope>
</dependency>
<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-devtools</artifactId>
<scope>runtime</scope>
<optional>true</optional>
</dependency>
<dependency>
<groupId>org.projectlombok</groupId>
<artifactId>lombok</artifactId>
<optional>true</optional>
</dependency>
<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-test</artifactId>
<scope>test</scope>
</dependency>
</dependencies>
<build>
<plugins>
<plugin>
<groupId>org.apache.maven.plugins</groupId>
<artifactId>maven-compiler-plugin</artifactId>
<configuration>
<annotationProcessorPaths>
<path>
<groupId>org.projectlombok</groupId>
<artifactId>lombok</artifactId>
</path>
</annotationProcessorPaths>
</configuration>
</plugin>
<plugin>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-maven-plugin</artifactId>
<configuration>
<excludes>
<exclude>
<groupId>org.projectlombok</groupId>
<artifactId>lombok</artifactId>
</exclude>
</excludes>
</configuration>
</plugin>
</plugins>
</build>
</project>
', 'pom.xml', 'pom-spring-rest');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (33, 'spring.application.name=${project.name?lower_case}
server.port=${r"${PORT:8089}"}
spring.h2.console.enabled=true
spring.datasource.url=jdbc:h2:file:./data/productdb
spring.jpa.hibernate.ddl-auto=update
spring.jpa.defer-data-source-initialization=true
jakarta.persistence.jdbc.url=jdbc:h2:file:./data/productdb', 'src/main/resources/application.properties', 'application.properties-h2');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (34, '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
lang="fa"
xmlns:ui="http://java.sun.com/jsf/facelets"
xmlns:f="http://java.sun.com/jsf/core"
xmlns:pr="http://primefaces.org/ui"
xmlns:h="http://java.sun.com/jsf/html">
<f:view locale="fa">
<h:head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta name="viewport" content="width=device-width, initial-scale=1"/>
<meta http-equiv="X-Frame-Options" content="deny"/>
<h:outputScript library="js" name="func-1.6.0.js"/>
<h:outputStylesheet library="css" name="font.css"/>
<h:outputStylesheet library="css" name="form.css"/>
<h:outputStylesheet library="css" name="style.css"/>
<ui:insert name="headerContent"/>
</h:head>
<pr:dialog widgetVar="waitingDialog" minHeight="40" modal="true" showHeader="false" resizable="false">
<table dir="rtl" border="0">
<tr>
<td align="center">
<h:graphicImage value="/resources/images/loader.gif"/>
</td>
</tr>
<tr>
<td align="center">
<h:outputText value="${r"#{msg.loading_please_wait}"}"/>
</td>
</tr>
</table>
</pr:dialog>
<h:body>
<div class="smart-wrap">
<div class="smart-forms smart-container wrap-2 remove-top-padding">
<ui:include src="top.xhtml"/>
<div class="section">
<div class="align-right">
<!--                        <h:messages id="globalMessage" styleClass="red rtl" style="list-style-type: none;"
showSummary="true" globalOnly="true"/>-->
</div>
</div>
<ui:include src="loaderDialog.xhtml"/>
<ui:insert name="content"/>
</div>
</div>
</h:body>
</f:view>
</html>', 'src/main/webapp/layout/base-template.xhtml', 'base-template.xhtml');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (35, '<!DOCTYPE composition PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<ui:composition xmlns="http://www.w3.org/1999/xhtml"
xmlns:ui="http://java.sun.com/jsf/facelets"
xmlns:h="http://java.sun.com/jsf/html"
xmlns:p="http://xmlns.jcp.org/jsf/passthrough"
xmlns:f="http://xmlns.jcp.org/jsf/core">
<div id="ajaxloader" class="modal" style="display: none;" >
<div>
<div class="align-center">
<h:graphicImage name="crs-loading.gif" styleClass="spacer-t25" library="images"/>
<h2 class="spacer-t25 rtl">${r"#{msg.loading_please_wait}"}</h2>
</div>
</div>
</div>
</ui:composition>', 'src/main/webapp/layout/loaderDialog.xhtml', 'loaderDialog.xhtml');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (36, '<!DOCTYPE composition PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<ui:composition xmlns="http://www.w3.org/1999/xhtml"
xmlns:ui="http://xmlns.jcp.org/jsf/facelets"
xmlns:f="http://xmlns.jcp.org/jsf/core"
xmlns:h="http://xmlns.jcp.org/jsf/html"
xmlns:p="http://xmlns.jcp.org/jsf/passthrough"
template="base-template.xhtml">
<ui:param name="pageTitle" value="${r"#{msg.project_name}"}"/>
<ui:define name="headerContent">
<ui:insert name="subHeaderContent"/>
</ui:define>
</ui:composition>', 'src/main/webapp/layout/page-template.xhtml', 'page-template.xhtml');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (37, '<!DOCTYPE composition PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<ui:composition xmlns="http://www.w3.org/1999/xhtml"
xmlns:ui="http://java.sun.com/jsf/facelets"
xmlns:h="http://java.sun.com/jsf/html"
xmlns:p="http://xmlns.jcp.org/jsf/passthrough"
xmlns:f="http://xmlns.jcp.org/jsf/core">
<div id="mod" class="modal">
<div>
<div class="align-center">
<h:graphicImage name="seri_serial.png" library="images" width="75%" height="33%"/>
<h3 class="rtl align-right">${r"#{bundle.index_serialNumber_help}"}</h3>
<p class="rtl align-right">
${r"#{bundle.index_serialNumber_help_desc1}"}
</p>
<ul class="rtl align-right">
</ul>
<p class="rtl align-right">
</p>
</div>
<h:outputLink styleClass="modal-close" id="mod-close"
onclick="javascript:void(0);return false;">
</h:outputLink>
</div>
</div>
</ui:composition>', 'src/main/webapp/layout/serialNumberHelp.xhtml', 'serialNumberHelp.xhtml');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (40, '# Application Details
appname=ssaa-import-job
appversion=1
appdate=March 12, 2019
# Transfer Config.
ssaa.maxResult=1
ssaa.read.url=http://10.6.150.140:7001/ssaa-read/rest/ssaaread/
ssaa.read.interval=50000
# Transfer Message
ssaa.service.invoke=can not invoke ssaa service
ssaa.events.port.success=events ported successfully
ssaa.events.port.fail=events porting failed
ssaa.event.port.success=event ported successfully
ssaa.event.port.fail=event porting failed
ssaa.event.type.port=the event ported to dcu successfully
ssaa.event.type.notvalid=the event master type does not match in [1,2,4,5]
ssaa.event.type.null=the event master type is null
ssaa.nationality.type.bothforeign=Husband and Wife are not iranian.
ssaa.nationality.type.unknown=unknown.
ssaa.sex.type.same=the sex types are same
ssaa.sex.type.unknown=the sex types are unknown
ssaa.service.unknown.err= unknown error ocurred for this record', 'src/main/resources/application.properties', 'application.properties.job');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (41, 'project_name=سامانه انتقال اطلاعات ثبت اسناد به سامانه ثبت احوال
hour=ساعت
start=اجرا
stop=توقف
transfer_to_estelam=سامانه انتقال اطلاعات ثبت اسناد به سامانه ثبت احوال
loading_please_wait=لطفا منتظر بمانید ...
register_change_address_every=اطلاعات ثبت اسناد هر
run=ساعت منتقل شود.
service_is_run=سرویس در حال اجرا است.
service_is_stop=سرویس در حال اجرا نمی باشد.
empty_EventMessage= حداقل یک واقعه باید انتخاب شود
remainTime=زمان باقیمانده تا انتقال اطلاعات
nextTime_transfer=زمان انتقال بعدی
service_in_process=سرویس در حال انتقال اطلاعات می باشد
service_in_transfer_status_so_cant_stop=سرویس در حال انتقال اطلاعات می باشد.
event_type= انتخاب نوع واقعه جهت انتقال :
startNow=شروع عملیات دوره‌ای سرویس به محض فشردن دکمه‌ی «اجرا»
maxResult=تعداد وقایع انتقالی در هر بار اجرای JOB:
', 'src/main/resources/messages/Message_fa.properties', 'Message_fa.properties_job_ejb');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (44, null, 'src\\main\\webapp\\resources\\images', 'logo.png');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (45, '[ENGINE]
Response File Version=1.0.0.0.0
[GENERIC]
DECLINE_AUTO_UPDATES=true
ORACLE_HOME=C:\\Oracle\\Middleware12\\Oracle_Home
INSTALL_TYPE=WebLogic Server
MYORACLESUPPORT_USERNAME=
MYORACLESUPPORT_PASSWORD=
SECURITY_UPDATES_VIA_MYORACLESUPPORT=false
DECLINE_SECURITY_UPDATES=true
PROXY_HOST=
PROXY_PORT=', 'weblogic12.rsp', 'weblogic12.rsp');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (46, 'inventory_loc=C:\\Oracle\\Middleware14\\oraInventory
inst_group=', 'oraInst14.loc', 'oraInst14.loc');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (47, 'java -jar fmw_14.1.1.0.0_wls.jar -silent -responseFile C:\\Users\\salah\\IdeaProjects\\esivaadin\\install_weblogic\\weblogic_install14.rsp -invPtrLoc oraInst.loc
', 'weblogicInstallCmd14.cmd', 'weblogic-install14-cmd');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (51, '[ENGINE]
Response File Version=1.0.0.0.0
[GENERIC]
DECLINE_AUTO_UPDATES=true
ORACLE_HOME=${oraclehome}
INSTALL_TYPE=WebLogic Server
MYORACLESUPPORT_USERNAME=
MYORACLESUPPORT_PASSWORD=
SECURITY_UPDATES_VIA_MYORACLESUPPORT=false
DECLINE_SECURITY_UPDATES=true
PROXY_HOST=
PROXY_PORT=', 'weblogic.rsp', 'weblogic.rsp');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (52, 'inventory_loc=${path}\\oraInventory
inst_group=', 'oraInst.loc', 'oraInst.loc');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (53, 'java -jar ${weblogicjar} -silent -responseFile ${path}${rsp} -invPtrLoc ${oraloc}', 'installWeblogic.cmd', 'installWeblogic');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (56, '# minimal_domain.py
print "Starting domain creation..."
readTemplate(\'${oraclehome}\\wlserver\\common\\/templates\\wls\\wls.jar\')
cd(\'Servers/AdminServer\')
set(\'ListenPort\', 7002)
cd(\'/Security/base_domain/User/weblogic\')
cmo.setPassword(\'weblogic1\')
setOption(\'OverwriteDomain\', \'true\')
writeDomain(${oraclehome}/user_projects/domains/mydomain2\')
print "Domain created successfully!"
exit()', 'create_domain.py', 'create_domain.py');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (57, 'call javahome.cmd
call installWeblogic.cmd
${oraclehome}\\oracle_common\\common\\bin\\wlst.cmd create_domain.py', 'fullInstall.cmd', 'fullInstall.cmd');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (58, '# WebLogic WLST script to create a new domain
readTemplate("/opt/weblogic/wlserver/common/templates/wls/wls.jar")
# Set Domain Name
cd(\'/\')
set(\'Name\', \'base_domain\')
# Configure Admin Server
cd(\'/Servers/AdminServer\')
set(\'ListenAddress\', \'0.0.0.0\')
set(\'ListenPort\', 7001)
# Set WebLogic Credentials
cd(\'/\')
cd(\'/Security/base_domain/User/weblogic\')
cmo.setPassword(\'Admin1234\')
# Write and close domain
setOption(\'OverwriteDomain\', \'true\')
writeDomain(\'/opt/weblogic/user_projects/domains/base_domain\')
closeTemplate()
print(\'WebLogic domain created successfully!\')
', 'create_domain.py', 'dockerWeblogic_create_domain.py');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (59, '[ENGINE]
Response File Version=1.0.0.0.0
[GENERIC]
# Set the middleware home directory
MiddlewareHome=/opt/weblogic
# Set Oracle home and WebLogic home
ORACLE_HOME=/opt/weblogic
WL_HOME=/opt/weblogic/wlserver
# Set the location of the WebLogic domain
DOMAIN_LOCATION=/opt/weblogic/user_projects/domains/base_domain
# Set the domain name
DOMAIN_NAME=base_domain
# Admin credentials for WebLogic
ADMIN_USER_NAME=weblogic
ADMIN_PASSWORD=Admin1234
# Admin server port
ADMIN_SERVER_LISTEN_PORT=7001
# Set to true for production mode or false for development mode
PRODUCTION_MODE=true
# Specify additional configuration options if needed, e.g., Node Manager
# Set Node Manager password
NodemanagerPassword=NodeManager1234
# Specify the path to the Node Manager
# Enable Node Manager if required
EnableNodeManager=true
NodemanagerHome=/opt/weblogic/wlserver/server/bin
# Set the Listen Address for the Admin Server if necessary
# ADMIN_LISTENER_ADDRESS=127.0.0.1
# Set specific parameters for the domain creation
# Example: Enable SSL or configure other settings based on requirements.
', 'create_domain.rsp', 'dockerWeblogic_create_domain.rsp');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (60, '# Use Ubuntu as the base image
FROM ubuntu:latest
# Set working directory
WORKDIR /app
# Install dependencies required by WebLogic
RUN apt-get update && apt-get install -y \\
libxrender1 libxtst6 libxi6 libxext6 unzip \\
&& rm -rf /var/lib/apt/lists/*
# Create a non-root user for WebLogic
RUN useradd -ms /bin/bash weblogic && \\
mkdir -p /opt/java /opt/weblogic /app/oracle_inventory && \\
chown -R weblogic:weblogic /app /opt/java /opt/weblogic /app/oracle_inventory
# Switch to non-root user
USER weblogic
# Copy installation files
COPY jdk-8u74-linux-x64.tar.gz fmw_12.2.1.0.0_wls.jar response.rsp create_domain.py /app/
# Create the oraInst.loc file manually
RUN echo "inventory_loc=/app/oracle_inventory" > /app/oraInst.loc && \\
echo "inst_group=weblogic" >> /app/oraInst.loc
# Extract and set up Java (keeping Java 7)
RUN tar -xzf jdk-8u74-linux-x64.tar.gz && \\
rm jdk-8u74-linux-x64.tar.gz && \\
mv jdk1.8.0_74 /opt/java
# Set environment variables
ENV JAVA_HOME=/opt/java/jdk1.8.0_74
ENV PATH="$JAVA_HOME/bin:$PATH"
# Verify Java installation
RUN java -version
# Install WebLogic in Silent Mode (using oraInst.loc)
RUN java -jar /app/fmw_12.2.1.0.0_wls.jar -silent -responseFile /app/response.rsp -invPtrLoc /app/oraInst.loc || \\
cat /app/installLogs.
# Set WebLogic environment variables
ENV WL_HOME=/opt/weblogic/wlserver
ENV DOMAIN_HOME=/opt/weblogic/user_projects/domains/base_domain
ENV PATH="$WL_HOME/server/bin:$PATH"
# Create WebLogic Domain using WLST
RUN . $WL_HOME/server/bin/setWLSEnv.sh && \\
java weblogic.WLST /app/create_domain.py
# Expose WebLogic ports
EXPOSE 7001 9002
# Default command to start WebLogic Admin Server
CMD ["sh", "-c", "$DOMAIN_HOME/bin/startWebLogic.sh && tail -f $DOMAIN_HOME/servers/AdminServer/logs/AdminServer.log"]
', 'Dockerfile', 'dockerWeblogic_Dockerfile');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (61, '[ENGINE]
Response File Version=1.0.0.0.0
[GENERIC]
ORACLE_HOME=/opt/weblogic
INSTALL_TYPE=WebLogic Server
DECLINE_SECURITY_UPDATES=true
SECURITY_UPDATES_VIA_MYORACLESUPPORT=false
AUTO_UPDATES_ENABLED=false
[WebLogic]
# Specify the WebLogic admin username and password
ADMIN_USER=weblogic
ADMIN_PASSWORD=welcome1', 'response.rsp', 'DockerWeblogic_response.rsp');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (62, 'docker build -t weblogic-image .
docker run -p 7001:7001 --name weblogic-container weblogic-image', 'dockerWeblogic_test.cmd', 'dockerWeblogic_test');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (63, '#https://aider.chat/docs/llms/gemini.html
python -m pip install -U aider-chat
pip install -U google-generativeai
Setx GEMINI_API_KEY AIzaSyD5nrKpmZFIM7tb0GyBsUVYtfeUcs85wrc
Setx GEMINI_API_KEY AIzaSyD8lfpNmhoVVktTxoazFZ084eG8faQxRxE
aider --model gemini/gemini-1.5-pro-latest
aider --list-models gemini/', 'aidarTest.md', 'aidarTest');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (64, 'set HTTP_PROXY=socks5://127.0.0.1:9090
set HTTPS_PROXY=socks5://127.0.0.1:9090
set JAVA_HOME=C:\\Program Files\\Java\\jdk-17
set PATH=%JAVA_HOME%\\bin;%PATH%
set MAVEN_OPTS=-Dmaven.repo.local=C:\\your-maven-repo
set MAVEN_HOME=C:\\apache-maven-3.9.9
set PATH=%MAVEN_HOME%\\bin;%PATH%
setx   GEMINI_API_KEY AIzaSyBivCYOOBmrc8crI-Hb9vcbQ5xFQ4dg90o
aider --model gemini/gemini-1.5-pro-latest
cmd
', 'runAidar.cmd', 'Aidar_run_ssh');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (65, 'ps -ef | grep java
ps -ef | grep myapp.jar
kill 12345
kill -9 12345
pkill -f myapp.jar
sudo ufw enable
sudo ufw allow 8080
sudo ufw allow 8080/tcp
sudo ufw status
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --reload
sudo firewall-cmd --list-all
sudo iptables -A INPUT -p tcp --dport 8080 -j ACCEPT
sudo iptables-save > /etc/iptables/rules.v4//Debian/Ubuntu
sudo service iptables save//CentOS/RHEL




sudo docker run -d \\
  --name mysql-container \\
  -e MYSQL_ROOT_PASSWORD=MyStrongPass123 \\
  -p 3306:3306 \\
  -v mysql_data:/var/lib/mysql \\
  mysql:8



sudo docker ps

sudo docker exec -it mysql-container mysql -u root -p

sudo docker stop mysql-container
*****************************
sudo docker stop mysql-container
sudo docker rm mysql-container

******
sudo docker rmi mysql:8     // image

*****
sudo docker volume rm mysql_data

*********

Don’t use localhost — in MySQL client it may try a Unix socket instead of TCP. Use 127.0.0.1.

*********

sudo docker logs mysql-container

*******
By default, root may only connect from localhost. To allow external connections, inside MySQL run:

ALTER USER \'root\'@\'%\' IDENTIFIED WITH mysql_native_password BY \'MyStrongPass123\';
FLUSH PRIVILEGES;


And make sure MySQL is listening on 0.0.0.0 (it usually is inside Docker).
******************
5. Port blocked by firewall

If you’re connecting remotely (not from the same server), check firewall:

sudo ufw allow 3306/tcp


or with firewalld:

sudo firewall-cmd --permanent --add-port=3306/tcp
sudo firewall-cmd --reload
************

sudo ufw deny 3306

sudo ufw delete allow 3306

sudo ufw status
***********

sudo docker exec -it mysql-container mysql -u root -p

*************************
sudo docker exec -i mysql-container \\
  mysql -u root -pMyStrongPass123 \\
  -e "CREATE SCHEMA productdb COLLATE utf8_general_ci;"
**************
If you want MySQL to create the schema automatically when the container starts, put an SQL file in a folder (e.g. init.sql):

CREATE SCHEMA productdb COLLATE utf8_general_ci;

Then run container with volume:

sudo docker run -d \\
  --name mysql-container \\
  -e MYSQL_ROOT_PASSWORD=MyStrongPass123 \\
  -p 3306:3306 \\
  -v mysql_data:/var/lib/mysql \\
  -v /path/to/init.sql:/docker-entrypoint-initdb.d/init.sql \\
  mysql:8
*********************

sudo docker ps --format "table {{.ID}}\\t{{.Names}}\\t{{.Status}}"
***********
SHOW DATABASES;

******************
sudo docker exec -i mysql-container \\
  mysql -u root -pMyStrongPass123 \\
  -e "SHOW DATABASES;"
******************
If root has Host=\'localhost\' only, allow remote access:

ALTER USER \'root\'@\'%\' IDENTIFIED WITH mysql_native_password BY \'MyStrongPass123\';
FLUSH PRIVILEGES;

********************











', 'doc/docDocker.md', 'doc_docker');
