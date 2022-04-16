package com.learn.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * 使用 java 注册 DispatcherServlet
 * tomcat 启动时调用里面的 onStartUp 方法
 * spring-web 项目里面使用了下面的规范1(里面写了 SpringServletContainerInitializer )和规范2( WebApplicationInitializer 接口)
 * 因此会扫描所有的 WebApplicationInitializer 实现类并传递给 SpringServletContainerInitializer，
 * SSCI 这个类会遍历扫描到的类依次调用 onStartUp 方法
 * <p>
 * servlet3.1规范
 * 1. 如果在 resources/META-INF/services/javax.servlet.ServletContainerInitializer 文件里的类
 * 实现了 ServletContainerInitializer 接口，tomcat 在启动时会调用里面的onStartUp方法(spi技术)
 * 2. 如果该类还被 @HandlesTypes() 注解修饰，还会传递接口中的所有实现类
 * <p>
 * <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-servlet">https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-servlet</a>
 */
public class MyWebApplicationInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        // spring context initializer
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(Config.class);

        // Create and register the DispatcherServlet
        DispatcherServlet servlet = new DispatcherServlet(context);
        ServletRegistration.Dynamic registration = servletContext.addServlet("app", servlet);
        registration.setLoadOnStartup(1);
        registration.addMapping("/*");

        System.out.println("spring initializer");
    }
}
