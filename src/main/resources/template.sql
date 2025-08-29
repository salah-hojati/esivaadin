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
INSERT INTO productdb.template (id, content, path, template_name) VALUES (3, 'package com.${project.name?lower_case}.domain;

import jakarta.persistence.*;
<#if hasOneToMany>
    import java.util.List;
</#if>

@Entity
public class ${entity.name} {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

<#-- Loop through each field provided in the data model -->
<#list fields as field>
    <#if field.relationship??>
        <#if field.relationship == "OneToOne">
            @OneToOne(cascade = CascadeType.ALL)
            @JoinColumn(name = "${field.snakeCaseName}_id", referencedColumnName = "id")
        <#elseif field.relationship == "ManyToOne">
            @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "${field.snakeCaseName}_id")
        <#elseif field.relationship == "OneToMany">
            @OneToMany(mappedBy = "${entityCamelCaseName}", cascade = CascadeType.ALL, orphanRemoval = true)
        </#if>
        private ${field.type} ${field.camelCaseName};

    <#else>
        private ${field.type} ${field.camelCaseName};

    </#if>
</#list>
//<editor-fold desc="Getters and Setters">
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    <#list fields as field>
        public ${field.type} get${field.capitalizedName}() {
        return ${field.camelCaseName};
        }

        public void set${field.capitalizedName}(${field.type} ${field.camelCaseName}) {
        this.${field.camelCaseName} = ${field.camelCaseName};
        }

    </#list>
    //</editor-fold>
}', 'src/main/java/com/${project.name?lower_case}/domain/${entity.name}.java', 'entity-class');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (5, 'package com.${project.name?lower_case}.domain;

import jakarta.persistence.*;
import java.util.*;
<#if hasOneToMany>
    import java.util.List;
</#if>

@Entity
public class ${entity.name} {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

<#-- Loop through each field provided in the data model -->
<#list fields as field>
    <#if field.relationship??>
        <#if field.relationship == "OneToOne">
            @OneToOne(cascade = CascadeType.ALL)
            @JoinColumn(name = "${field.snakeCaseName}_id", referencedColumnName = "id")
        <#elseif field.relationship == "ManyToOne">
            @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "${field.snakeCaseName}_id")
        <#elseif field.relationship == "OneToMany">
            @OneToMany(mappedBy = "${entityCamelCaseName}", cascade = CascadeType.ALL, orphanRemoval = true)
        </#if>
        private ${field.type} ${field.camelCaseName};

    <#else>
        private ${field.type} ${field.camelCaseName};

    </#if>
</#list>
//<editor-fold desc="Getters and Setters">
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    <#list fields as field>
        public ${field.type} get${field.capitalizedName}() {
        return ${field.camelCaseName};
        }

        public void set${field.capitalizedName}(${field.type} ${field.camelCaseName}) {
        this.${field.camelCaseName} = ${field.camelCaseName};
        }

    </#list>
    //</editor-fold>
}', 'src/main/java/com/${project.name?lower_case}/domain/${entity.name}.java', 'entityJakarta');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (6, 'package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMessage implements Serializable {

    private String code;
    private String desc;

    public ResponseMessage(ResponseMessageEnum responseMessageEnum) {
        this.code = responseMessageEnum.getCode();
        this.desc = responseMessageEnum.getMsg();
    }

    @Override
    public String toString() {
        return "ResponseMessage{" +
                "code=\'" + code + \'\\\'\' +
                ", desc=\'" + desc + \'\\\'\' +
                \'}\';
    }
}', 'src/main/java/com/${project.name?lower_case}/dto/ResponseMessage.java', 'ResponseMessage');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (7, 'package com.example.service;

import com.example.service.BaseDao;
import com.example.entity.${entity.name};

import javax.ejb.LocalBean;
import javax.ejb.Stateless;


@Stateless
@LocalBean
public class ${entity.name}ServiceImpl extends BaseDao<${entity.name}> {

}', 'src/main/java/com/${project.name?lower_case}/service/${entity.name}.java', 'entityEjbService');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (8, 'package com.example.domain;

import jakarta.persistence.*;
<#if hasOneToMany>
    import java.util.List;
</#if>

@Entity
public class ${entity.name} {

 @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "${entity.name}_logs_seq")
  @SequenceGenerator(name = "ssaa_event_logs_seq", sequenceName = "${entity.name}_logs_seq")
  @Column(name = "id")
  private Long id;

<#-- Loop through each field provided in the data model -->
<#list fields as field>
    <#if field.relationship??>
        <#if field.relationship == "OneToOne">
            @OneToOne(cascade = CascadeType.ALL)
            @JoinColumn(name = "${field.snakeCaseName}_id", referencedColumnName = "id")
        <#elseif field.relationship == "ManyToOne">
            @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "${field.snakeCaseName}_id")
        <#elseif field.relationship == "OneToMany">
            @OneToMany(mappedBy = "${entityCamelCaseName}", cascade = CascadeType.ALL, orphanRemoval = true)
        </#if>
    <#else>
     @Column(name = "${field.camelCaseName}")
        private ${field.type} ${field.camelCaseName};

    </#if>
</#list>
//<editor-fold desc="Getters and Setters">
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    <#list fields as field>
        public ${field.type} get${field.capitalizedName}() {
        return ${field.camelCaseName};
        }

        public void set${field.capitalizedName}(${field.type} ${field.camelCaseName}) {
        this.${field.camelCaseName} = ${field.camelCaseName};
        }

    </#list>
    //</editor-fold>
}', 'src/main/java/com/${project.name?lower_case}/domain/${entity.name}.java', 'entityOracle');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (9, 'package com.farafan.probateCertificateGenerator.common.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class BaseDAO<T> {

    private final Logger LOGGER = LogManager.getLogger(this.getClass());

    @PersistenceContext(unitName = "ProbateCertificateGeneratorPU")
    public EntityManager em;

    public T update(T entity) throws Exception {
        try {
            T e = em.merge(entity);
            em.flush();
            return e;
        } catch (Exception e) {
            LOGGER.error("can not update " + entity.getClass().getName());
            LOGGER.error("BaseDAO update method error", e);
            return null;
        }
    }

    public void delete(T entity) throws Exception {
        try {
            em.remove(em.merge(entity));
        } catch (Exception e) {
            LOGGER.error("can not delete " + entity.getClass().getName());
            LOGGER.error("BaseDAO delete method error", e);
        }
    }

    public T create(T entity) throws Exception {
        try {
            em.persist(entity);
            em.flush();
            return entity;
        } catch (Exception e) {
            LOGGER.error("can not create " + entity.getClass().getName());
            LOGGER.error("BaseDAO create method error", e);
            return null;
        }
    }
}', 'src/main/java/com/${project.name?lower_case}/dao/BaseDao.java', 'BaseDao');
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
INSERT INTO productdb.template (id, content, path, template_name) VALUES (12, 'package com.${project.name?lower_case}.action;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Named(value = "me")
@SessionScoped
public class GeneralManagementAction implements Serializable {

    private Locale locale = null;
    private static final String BUNDLE_NAME = "messages.Message";
    private final Logger appLogger = LogManager.getLogger(this.getClass());


    public String authentication(){
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        return "";
    }

    public void redirect(String pageName) {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.getExternalContext().redirect(facesContext.getExternalContext().getRequestContextPath() + pageName /*+ "?cid=" + facesContext.getExternalContext().getRequestParameterMap().get("cid")*/);
        } catch (Exception e) {
            appLogger.error("redirect method:" +e.getMessage());

        }
    }

    private ResourceBundle getBundle() {
        ResourceBundle resourceBundle =  ResourceBundle.getBundle(BUNDLE_NAME,new Locale("fa"));
        return resourceBundle;
    }

    public String getBundleMessageWithArgs(String bundleKey, Object[] arguments) {
        if (bundleKey == null || bundleKey.isEmpty())
            return "?";

        try {
            return MessageFormat.format(getBundle().getString(bundleKey), arguments);
        } catch (MissingResourceException e) {
            return bundleKey;
        }
    }

    public void addErrorMessage(String bundleKey) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, getBundleMessage(bundleKey, null), ""));
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        FacesContext.getCurrentInstance().validationFailed();
    }

    public void addErrorMessageString(String messageContent) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, messageContent, messageContent));
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
    }

    public void addErrorMessage(String bundleKey, String... arguments) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, getBundleMessage(bundleKey, arguments), getBundleMessage(bundleKey, arguments)));
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
    }

    public void addWarnMessage(String bundleKey) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, getBundleMessage(bundleKey, null), getBundleMessage(bundleKey, null)));
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
    }

    public void addInfoMessage(String bundleKey) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, getBundleMessage(bundleKey, null), getBundleMessage(bundleKey, null)));
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
    }

    public void addInfoMessage(String bundleKey, String... arguments) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, getBundleMessage(bundleKey, arguments), getBundleMessage(bundleKey, arguments)));
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
    }

    public String getBundleMessage(String bundleKey, String... arguments) {
        if (bundleKey == null)
            return "?";

        try {
            return MessageFormat.format(getBundle().getString(bundleKey), arguments);
        } catch (MissingResourceException mre) {
            return bundleKey;
        }
    }

}
', 'src/main/java/com/${project.name?lower_case}/action/GeneralManagementAction.java', 'GeneralManagementAction');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (13, 'package com.${project.name?lower_case}.action;




import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Remove;
import javax.ejb.Timer;


import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.job.service.*;

@Named(value="handleHomeAction")
@SessionScoped

public class HandleHomeAction implements Serializable {

    @Inject
    private GeneralManagementAction me;

//    @EJB //- it fails in conjuction with @Named through stop/start in Weblogic console
    private JobService jobService;


    private final Logger appLogger=LogManager.getLogger(this.getClass());

    private String hour;
    private String remainTime = "0";
    private String nextTime = "0";
    private Timer timer;
    private boolean isStarted;
    private static final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private Boolean mrg=false;
 

    private Boolean startNow;

    private Long maxResult=1l;


    @PostConstruct
    public void init() {
        try {
            Context initialContext = new InitialContext();
            jobService = (JobService)
                    initialContext.lookup("java:global/"
                            + (String) initialContext.lookup("java:module/ModuleName")
                            + "/JobService");

        }catch(NamingException ne){
            appLogger.error("JobService EJB can not found");
        }
    }

    public void startJob(){
        try {
            timer = jobService.startJob(hour,startNow);
            MainJobService.eventTyps.clear();
     

            MainJobService.maxResult=maxResult;


            //calculateTime();
        }catch (Exception e){
            appLogger.error("HandleHomeAction startJob method error");
            appLogger.error(e.getMessage());
        }
    }

    public void stopJob(){
        try {
            jobService.cancelJob();

            UIViewRoot view	=	FacesContext.getCurrentInstance().getViewRoot();
            view.findComponent("frm:stopMessage").getAttributes().put("style","display:block") ;
            view.findComponent("frm:emptyEventMessage").getAttributes().put("style","display:none")  ;
            //calculateTime();
        }catch (Exception e){
            appLogger.error("HandleHomeAction stopJob method error");
            appLogger.error(e.getMessage());
            me.addErrorMessage("service_in_transfer_status_so_cant_stop");
        }
    }

    public String serviceStatus(){
        return String.valueOf(jobService.isStarted());
    }

    public void calculateTime(){
        if(jobService.isStarted()) {
            if(jobService.isFlag()){
                remainTime = me.getBundleMessage("service_in_process");
                me.redirect("/index.xhtml");
            }else {
                remainTime = String.valueOf(timer.getTimeRemaining() / 1000);
            }
            nextTime = String.valueOf(dateFormat.format(timer.getNextTimeout().getTime()));
        }else{
            remainTime = "0";
            nextTime = "0";
        }
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getRemainTime() {
        return remainTime;
    }

    public void setRemainTime(String remainTime) {
        this.remainTime = remainTime;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public String getNextTime() {
        return nextTime;
    }

    public void setNextTime(String nextTime) {
        this.nextTime = nextTime;
    }



    public Boolean getStartNow() {
        return startNow;
    }

    public void setStartNow(Boolean startNow) {
        this.startNow = startNow;
    }

    public Long getMaxResult() {
        return maxResult;
    }

    public void setMaxResult(Long maxResult) {
        this.maxResult = maxResult;
    }

    public Boolean getMrg() {
        return mrg;
    }

    public void setMrg(Boolean mrg) {
        this.mrg = mrg;
    }
}
', 'src/main/java/com/${project.name?lower_case}/action/HandleHomeAction.java', 'HandleHomeAction');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (14, 'package com.${project.name?lower_case}.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


@Singleton
@Startup
public class JobService {



    @EJB
    private MainJobService mainJobService;

    @Resource
    private TimerService timerService;


    private final Logger appLogger = LogManager.getLogger(this.getClass());

    private Timer timer;
    private boolean flag;
    private boolean isStarted;
    private static final String JOBINFORMATION ="ssaaimportjob" ;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


    public Timer startJob(String hour, Boolean startNow) {
        appLogger.info("job start at : " + dateFormat.format(new Date()));
        TimerConfig timerConfig = new TimerConfig();
        timerConfig.setInfo(JOBINFORMATION);
        ScheduleExpression schedule = new ScheduleExpression();
        hour = "*/" + hour;
       // schedule.second("1");


          if(startNow){
              Date date=new Date();
              date.setTime(date.getTime()+4000);
              Calendar calendar = Calendar.getInstance();
              calendar.setTime(date);

              int minutes = calendar.get(Calendar.MINUTE);
              int seconds = calendar.get(Calendar.SECOND);
              schedule.hour(hour).minute(minutes).second(seconds);
          }else
         schedule.hour(hour).minute(0).second(0);
        timer = timerService.createCalendarTimer(schedule, timerConfig);
        isStarted = true;
        return timer;
    }

    @Timeout
    public void execute(Timer timer) {
        appLogger.info("job execute at : " + dateFormat.format(new Date()));
        try {
            ArrayList<Timer> timerList = new ArrayList<>(timerService.getAllTimers());
            ArrayList<String> timerInfoList = new ArrayList<>();
            for (Timer ti : timerList) {
                timerInfoList.add(String.valueOf(ti.getInfo()));
            }
            if (!timerInfoList.contains(MainJobService.MAINTIMERINFO)) {
                mainJobService.startJob();
            }
        } catch (Exception e) {
            mainJobService.startJob();
        }
    }

    @PostConstruct
    public void stopJob(){
        stop(JOBINFORMATION);
        mainJobService.stopService();
        isStarted = false;
        flag = false;
        appLogger.info("job stopped at : " + dateFormat.format(new Date()));

    }

    public void cancelJob() throws Exception{
        stop(JOBINFORMATION);
        mainJobService.cancelService();
        isStarted = false;
        flag = false;
        appLogger.info("job stopped at : " + dateFormat.format(new Date()));
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void stop(String timerName) {
        for (Object obj : timerService.getTimers()) {
            Timer timer = (Timer) obj;
            if (timer.getInfo().equals(timerName)) {
                timer.cancel();
            }
        }
        appLogger.info("job stopped at : " + dateFormat.format(new Date()));
    }
}
', 'src/main/java/com/${project.name?lower_case}/service/JobService.java', 'JobService');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (15, 'package com.${project.name?lower_case}.service;

import com.${project.name?lower_case}.utils.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Singleton
@Startup
public class MainJobService {


    @EJB
    MainService mainService;

    @Resource
    private TimerService mainTimerService;

    private final Logger appLogger = LogManager.getLogger(this.getClass());

    public Timer mainTimer;
    public static boolean flag;
    public static final String MAINTIMERINFO = "mainJob";

    public static ArrayList<Integer> eventTyps=new ArrayList<>();
    public static Long maxResult;
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public void startJob() {
        appLogger.info("mainTimer start at : " + dateFormat.format(new Date()));
        //5 minute period
        mainTimer = mainTimerService.createTimer(0, Long.parseLong(Configuration.getProperty("read.interval")), MAINTIMERINFO);
        flag = false;
    }

    @Timeout
    @TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
    public void execute() {
        try {
            appLogger.info("mainTimer execute at : " + dateFormat.format(new Date()));
            if (!flag) {
                flag = true;
                boolean isLisyEmpty = mainService.doTask();
                if (isLisyEmpty) {
                    stopService();
                }
                flag = false;
            } else {
                appLogger.error("pervious job still running.");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void stopService() {
            stop(MAINTIMERINFO);
            appLogger.info("mainJobService stopped at : " + dateFormat.format(new Date()));

    }

    public void cancelService() {
        try{
            stopService();
        }catch (NullPointerException ne){
            appLogger.info("mainJobService is null : " + dateFormat.format(new Date()));
        }catch (NoSuchObjectLocalException te){
            appLogger.info("mainJobService cancelled or expired : " + dateFormat.format(new Date()));
        }


    }

    public void stop(String timerName) {
        for (Object obj : mainTimerService.getTimers()) {
            Timer timer = (Timer) obj;
            if (timer.getInfo().equals(timerName)) {
                timer.cancel();
            }
        }
    }
}
', 'src/main/java/com/${project.name?lower_case}/service/MainJobService.java', 'MainJobService');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (16, 'package com.${project.name?lower_case}.service;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ejb.*;



@Singleton
@TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
public class MainService {

 

    private final Logger appLogger = LogManager.getLogger(this.getClass());


    public boolean doTask(){
       
        return true;
    }


}', 'src/main/java/com/${project.name?lower_case}/service/MainService.java', 'MainService');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (17, 'package com.${project.name?lower_case}.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

public class Configuration {
    static private Properties configuration = null;

    public static void load() throws IOException {
        configuration = readFile();
    }

    public static Properties getProperties() {
        getInstance();
        return configuration;
    }

    public static String getProperty(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is Empty!");
        }
        return getInstance().getProperty(key);
    }

    public static Integer getIntegerProperty(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is Empty!");
        }
        String property = getInstance().getProperty(key);
        if (property != null) {
            return Integer.valueOf(property.trim());
        }
        return null;
    }

    public static Integer getIntegerProperty(String key, int defaultValue) {
        Integer integerProperty = getIntegerProperty(key);
        if (integerProperty == null) {
            return defaultValue;
        }
        return integerProperty;
    }

    public static Boolean getBooleanProperty(String key, Boolean deafultValue) {
        if (key == null) {
            throw new IllegalArgumentException("Key is Empty!");
        }
        String property = getInstance().getProperty(key);
        if (property != null) {
            return Boolean.valueOf(property);
        }
        return deafultValue;
    }

    public static Long getLongProperty(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is Empty!");
        }
        String property = getInstance().getProperty(key);
        if (property != null) {
            return Long.valueOf(property);
        }
        return null;
    }

    public static String getProperty(String key, String... args) {
        return MessageFormat.format(getProperty(key), args);
    }

    private static Properties getInstance() {
        if (configuration == null) {
            try {
                configuration = readFile();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return configuration;
    }

    public static String getPropertyWithDefault(String key, String defaultValue) {
        return getInstance().getProperty(key, defaultValue);
    }

    private static Properties readFile() throws IOException {

        Properties props = new Properties();
        try {
            ClassLoader loader = Configuration.class.getClassLoader();
            InputStream in = loader.getResourceAsStream("application.properties");
            props.load(in);
        } catch (FileNotFoundException e) {
            throw e;
        }
        return props;
    }
}', 'src/main/java/com/${project.name?lower_case}/utils/Configuration.java', 'Configuration');
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
INSERT INTO productdb.template (id, content, path, template_name) VALUES (25, '<!DOCTYPE composition PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">


<ui:composition xmlns="http://www.w3.org/1999/xhtml"
                xmlns:ui="http://java.sun.com/jsf/facelets"
                xmlns:h="http://java.sun.com/jsf/html"
                xmlns:p="http://primefaces.org/ui"
                xmlns:f="http://xmlns.jcp.org/jsf/core"
                template="/layout/page-template.xhtml">



    <ui:define name="subHeaderContent">
    </ui:define>
    <ui:define name="content">
        <h:form id="frm" styleClass="wpcf7-form" prependId="false">
            <div class="form-body" style="padding-top: 0px !important;">
                <div class="section">
                    <div class="frm-row">
                        <div class="align-center rtl">
                            <p:outputPanel id="messagePanel" >
                                <h:outputText id="runMessage" style="color: green;" value="${r"#{msg.service_is_run}"}" rendered="${r"#{handleHomeAction.serviceStatus() eq \'true\'}"}"/>
                                <h:outputText id="stopMessage" style="color: red"  value="${r"#{msg.service_is_stop}"}" rendered="${r"#{handleHomeAction.serviceStatus() eq \'false\'}"}"/>
                           
                            </p:outputPanel>
                        </div>
                    </div>
                </div>

                <div class="section">
                    <div class="frm-row">
                        <div class="section colm colm12 align-right rtl">
                            <h:outputText value="${r"#{msg.register_change_address_every}"}"/>
                            <h:selectOneMenu name="g-date" id="cmbHour"
                                             disabled="${r"#{handleHomeAction.serviceStatus() eq \'true\'}"}"
                                             label="${r"#{msg.hour}"}"
                                             style="text-align: center"
                                             value="${r"#{handleHomeAction.hour}"}"
                                             dir="RTL">
                                <f:selectItem itemValue="1" itemLabel="1"/>
                                <f:selectItem itemValue="2" itemLabel="2"/>
                                <f:selectItem itemValue="3" itemLabel="3"/>
                                <f:selectItem itemValue="4" itemLabel="4"/>
                                <f:selectItem itemValue="5" itemLabel="5"/>
                                <f:selectItem itemValue="6" itemLabel="6"/>
                                <f:selectItem itemValue="7" itemLabel="7"/>
                                <f:selectItem itemValue="8" itemLabel="8"/>
                                <f:selectItem itemValue="9" itemLabel="9"/>
                                <f:selectItem itemValue="10" itemLabel="10"/>
                                <f:selectItem itemValue="11" itemLabel="11"/>
                                <f:selectItem itemValue="12" itemLabel="12"/>
                                <f:selectItem itemValue="13" itemLabel="13"/>
                                <f:selectItem itemValue="14" itemLabel="14"/>
                                <f:selectItem itemValue="15" itemLabel="15"/>
                                <f:selectItem itemValue="16" itemLabel="16"/>
                                <f:selectItem itemValue="17" itemLabel="17"/>
                                <f:selectItem itemValue="18" itemLabel="18"/>
                                <f:selectItem itemValue="19" itemLabel="19"/>
                                <f:selectItem itemValue="20" itemLabel="20"/>
                                <f:selectItem itemValue="21" itemLabel="21"/>
                                <f:selectItem itemValue="22" itemLabel="22"/>
                                <f:selectItem itemValue="23" itemLabel="23"/>
                                <f:selectItem itemValue="24" itemLabel="24"/>
                            </h:selectOneMenu>
                            <h:outputText value=" ${r"#{msg.run}"}"/>
                            <br/>
                            <br/>
                            <br/>


                        </div>
                    </div>
                </div>
                

                <div class="section">
                    <div class="frm-row">
                        <div class="section colm colm12 align-right rtl">
                            <h:selectBooleanCheckbox value="${r"#{handleHomeAction.startNow}"}" id="startNow"  disabled="false"   />  &nbsp;   <label for="startNow">${r"#{msg.startNow}"}</label>
                            <br/>
                            <p style="font-weight: normal ">
                                با انتخاب این گزینه به محض فشردن دکمه‌ی «اجرا» جاب شروع به کار نموده و وقایع را منتقل می‌کند و از زمان اجرا به صورت دوره‌ای براساس بازه‌ی ساعتی انتخاب شده نیز نسبت به انتقال وقایع اقدام می‌کند.
                            <br/>
                                در صورت عدم انتخاب این گزینه پس از زدن دکمه‌ی «اجرا» رأس اولین ساعت کامل پیش رو، وقایع را منتقل می‌کند و براساس بازه‌ی ساعتی انتخاب شده به صورت دوره‌ای مجدداً نسبت به انتقال وقایع اقدام می‌کند.
                            </p>
                            &nbsp; &nbsp; &nbsp;&nbsp;
                               <br/>
                               <br/>
                            <label for="maxResult">${r"#{msg.maxResult}"}</label>
                            <p:inputNumber id ="maxResult"   value="${r"#{handleHomeAction.maxResult}"}" 
                                           thousandSeparator="" decimalSeparator="." decimalPlaces="0"  size=\'5\'  style="text-align: center" ></p:inputNumber>
                                 <br/>
                                 <br/>

                        </div>
                    </div>
                </div>


                <div class="section">
                    <p:commandButton id="btnStop" action="${r"#{handleHomeAction.stopJob}"}" value="${r"#{msg.stop}"}" styleClass="button btn-primary"
                                      disabled="${r"#{handleHomeAction.serviceStatus() eq \'false\'}"}"
                                      onstart="PF(\'waitingDialog\').show();"
                                      oncomplete="PF(\'waitingDialog\').hide(); stopMessage.value=\'ss\'"
                                      update="btnStart,btnStop,messagePanel,cmbHour">
                        <f:resetValues render="stopMessage" />
                    </p:commandButton>
                    <p:commandButton id="btnStart" action="${r"#{handleHomeAction.startJob}"}" value="${r"#{msg.start}"}" styleClass="button btn-primary"
                                      disabled="${r"#{handleHomeAction.serviceStatus() eq \'true\'}"}" style="margin-left: 20px;"
                                      onstart="PF(\'waitingDialog\').show();"
                                      oncomplete="PF(\'waitingDialog\').hide();"
                                      update="btnStart,btnStop,messagePanel,cmbHour">

                        <f:resetValues render="mrg" />

                    </p:commandButton>
                </div>
            </div>
            <script type="application/javascript">
                window.onload = function () {
                    onLoadFunc();
                };
            </script>
        </h:form>
    </ui:define>
</ui:composition>', 'src/main/webapp/index.xhtml', 'index.xhtml_job');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (26, 'package com.${project.name?lower_case}.repository;

import com.${project.name?lower_case}.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ${entity.name}Repository extends JpaRepository<${entity.name} , Long> {
}', 'src/main/java/com/${project.name?lower_case}/repository/${entity.name}Repository.java', 'repository');
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
INSERT INTO productdb.template (id, content, path, template_name) VALUES (28, 'package com.${r"${project.name?lower_case}"}.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Date;


public class DateUtil {


    private static final String[] months = (Configuration.getProperty("messages.hijri.months")).split(",");

    private static final String DELIMITER = "/";

    /**
     * Given a {@link Date} value and returns a new result that its time related fields are filled with zero
     * (e.g. 2014/12/2T00:00:00:000)
     *
     * @param date the date value to fill its time related fields with zero
     * @return a new date that its time related fields are filled with zero (e.g. 2014/12/2T00:00:00:000)
     */
    public static Date getDateAtMidnight(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * Given a {@link Date} value, adds number of days to it (specified as \'value\' parameter) and returns a
     * new {@link Date}
     *
     * @param date  The date object to add some days to it
     * @param value Number of days to be added to the date
     * @return Newly calculated date
     */
    public static Date differDay(Date date, Integer value) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, value);
        return cal.getTime();
    }

    public static String formatAsDate(String date) {
        if (StringUtils.isNotEmpty(date) && date.trim().length() == 8) {
            return date.substring(0, 4) + DELIMITER + date.substring(4, 6) + DELIMITER + date.substring(6, 8);
        }
        return date;
    }

    public static String getSolarDateString(int solarDate) {
        String solarDateStr = String.valueOf(solarDate);
        return solarDateStr.substring(0, 4) + DELIMITER + solarDateStr.substring(4, 6) + DELIMITER + solarDateStr.substring(6, 8);
    }

    public static String getHijriDateAsString(String date) {
        String y = date.split(DELIMITER)[0];
        int m = Integer.valueOf(date.split(DELIMITER)[1]);
        String d = date.split(DELIMITER)[2];
        return LangUtil.getFarsiNumber(d) + \'\\t\'
                + months[m - 1] + \'\\t\'
                + LangUtil.getFarsiNumber(y);
    }

    public static Date incrementDate(Date curentDate, Integer increment) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(curentDate);
        cal.add(Calendar.DATE, increment);
        return cal.getTime();
    }


}
', 'src/main/java/com/${project.name?lower_case}/util/DateUtil.java', 'DateUtil');
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
INSERT INTO productdb.template (id, content, path, template_name) VALUES (30, 'package com.${project.name}.controller;

import java.util.List;
import com.${project.name?lower_case}.domain.${entity.name};
import org.springframework.web.bind.annotation.*;
import com.${project.name?lower_case}.repository.${entity.name}Repository;
import com.${project.name?lower_case}.exception.${entity.name}NotFoundException;

@RestController
public class ${entity.name}Controller {

  private final ${entity.name}Repository repository;

 public ${entity.name}Controller(${entity.name}Repository repository) {
    this.repository = repository;
  }


  // Aggregate root
  // tag::get-aggregate-root[]
  @GetMapping("/${entity.name?lower_case}s")
  public  List<${entity.name}> all() {
    return repository.findAll();
  }
  // end::get-aggregate-root[]

  @PostMapping("/${entity.name?lower_case}s")
  public  ${entity.name} new${entity.name}(@RequestBody ${entity.name} new${entity.name}) {
    return repository.save(new${entity.name});
  }

  // Single item
  
  @GetMapping("/${entity.name?lower_case}s/{id}")
  public  ${entity.name} one(@PathVariable Long id) {
    
    return repository.findById(id)
      .orElseThrow(() -> new ${entity.name}NotFoundException(id));
  }

  @PutMapping("/${entity.name?lower_case}s/{id}")
  public  ${entity.name} replace${entity.name}(@RequestBody ${entity.name} new${entity.name}, @PathVariable Long id) {
    
    return repository.findById(id)
      .map(${entity.name?lower_case}-> {

        return repository.save(${entity.name?lower_case});
      })
      .orElseGet(() -> {
        return repository.save(new${entity.name});
      });
  }

  @DeleteMapping("${entity.name?lower_case}s/{id}")
  public  void delete${entity.name}(@PathVariable Long id) {
    repository.deleteById(id);
  }
}', 'src/main/java/com/${project.name?lower_case}/controller/${entity.name}Controller.java', 'springEndPoint');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (31, 'package com.${project.name?lower_case}.exception;

public class ${entity.name}NotFoundException extends RuntimeException {

public  ${entity.name}NotFoundException(Long id) {
    super("Could not find ${entity.name?lower_case} " + id);
  }
}', 'src/main/java/com/${project.name?lower_case}/exception/${entity.name}NotFoundException.java', 'Exception_NotFoundException');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (32, 'package com.${project.name};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ${project.name}Application {

  public static void main(String... args) {
    SpringApplication.run(${project.name}Application.class, args);
  }
}', 'src/main/java/com/${project.name}/${project.name}Application.java', 'springApplication');
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
INSERT INTO productdb.template (id, content, path, template_name) VALUES (42, 'package com.${project.name?lower_case}.utils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "EnglishToFarsiConverter")
public class EnglishToFarsiConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        value = LangUtils.getEnglishNumber(value);
        return value;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String number = String.valueOf(value);
        number = LangUtils.getFarsiNumber(number);
        return number;
    }
}
', 'src/main/java/com/${project.name?lower_case}/utils/EnglishToFarsiConverter.java', 'EnglishToFarsiConverter');
INSERT INTO productdb.template (id, content, path, template_name) VALUES (43, 'package com.${project.name?lower_case}.utils;

import java.util.Locale;

public class LangUtils {
    public static Locale LOCALE_FARSI = new Locale("fa");
    public static Locale LOCALE_ENGLISH = Locale.ENGLISH;



    public static String getNumber(int number, Locale locale) {
        return getNumber("" + number, locale);
    }

    public static String getNumber(String number, Locale locale) {
        if (locale.equals(LOCALE_FARSI))
            return getFarsiNumber(number);
        return number;
    }

    public static String getFarsiNumber(String number) {
        StringBuffer farsiNumberString = new StringBuffer();
        char c;

        for (int i = 0; i < number.length(); i++) {
            c = number.charAt(i);
            switch (c) {
                case \'0\':
                    farsiNumberString.append(\'۰\');
                    break;

                case \'1\':
                    farsiNumberString.append(\'۱\');
                    break;

                case \'2\':
                    farsiNumberString.append(\'۲\');
                    break;

                case \'3\':
                    farsiNumberString.append(\'۳\');
                    break;

                case \'4\':
                    farsiNumberString.append(\'۴\');
                    break;

                case \'5\':
                    farsiNumberString.append(\'۵\');
                    break;

                case \'6\':
                    farsiNumberString.append(\'۶\');
                    break;

                case \'7\':
                    farsiNumberString.append(\'۷\');
                    break;

                case \'8\':
                    farsiNumberString.append(\'۸\');
                    break;

                case \'9\':
                    farsiNumberString.append(\'۹\');
                    break;

                default:
                    farsiNumberString.append(c);
                    break;
            }
        }
        return farsiNumberString.toString();
    }

    public static String getEnglishNumber(String number) {
        StringBuffer englishNumberString = new StringBuffer();
        char c;

        for (int i = 0; i < number.length(); i++) {
            c = number.charAt(i);
            switch (c) {
                case \'۰\':
                    englishNumberString.append(\'0\');
                    break;

                case \'۱\':
                    englishNumberString.append(\'1\');
                    break;

                case \'۲\':
                    englishNumberString.append(\'2\');
                    break;

                case \'۳\':
                    englishNumberString.append(\'3\');
                    break;

                case \'۴\':
                    englishNumberString.append(\'4\');
                    break;

                case \'۵\':
                    englishNumberString.append(\'5\');
                    break;

                case \'۶\':
                    englishNumberString.append(\'6\');
                    break;

                case \'۷\':
                    englishNumberString.append(\'7\');
                    break;

                case \'۸\':
                    englishNumberString.append(\'8\');
                    break;

                case \'۹\':
                    englishNumberString.append(\'9\');
                    break;

                default:
                    englishNumberString.append(c);
                    break;
            }
        }
        return englishNumberString.toString();
    }

    public static Locale refine(Locale l) {
        if (l == null)
            return LOCALE_FARSI;
        if ("fa".equalsIgnoreCase(l.getLanguage()))
            return LOCALE_FARSI;

        return LOCALE_FARSI;
    }
}', 'src/main/java/com/${project.name?lower_case}/utils/LangUtils.java', 'LangUtils.java');
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
INSERT INTO productdb.template (id, content, path, template_name) VALUES (48, '
set JavaHome "C:\\Java\\jdk1.8.0_161";
set ServerStartMode "dev";
find Server "AdminServer" as AdminServer;
set AdminServer.ListenAddress "";
set AdminServer.ListenPort "7001";
set AdminServer.SSL.Enabled "false";
set AdminServer.SSL.ListenPort "7002";
create User "weblogic" as u1;
set u1.password "weblogic1";
set u1.Groups "Administrators";
write domain to "C:\\Oracle\\MiddlewareTest\\Oracle_Home\\user_projects\\domains\\mydomain";
close template;', 'domain.rsp', 'domain.rsp');
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
INSERT INTO productdb.template (id, content, path, template_name) VALUES (54, '
rem Create a clean command prompt session
set PATH=

rem Set system path without other Java versions
set PATH=%SystemRoot%\\system32;%SystemRoot%;%SystemRoot%\\System32\\Wbem;
rem  Verify
echo %PATH%
rem Now set your Java 8
set JAVA_HOME=${javahome}
set PATH=%JAVA_HOME%\\bin;%PATH%

rem  Verify

echo %JAVA_HOME%
echo %PATH%
rem  Check which java is being used
where java
java -version

rem add command to run by java

', 'javahome.cmd', 'javahome');
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
