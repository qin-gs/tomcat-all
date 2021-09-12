package com.learn.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * 使用java注册DispatcherServlet
 * tomcat启动时调用里面的onStartUp方法
 * spring-web项目里面使用了下面的规范1(里面写了SpringServletContainerInitializer)和规范2(WebApplicationInitializer接口)
 * 因此会扫描所有的WebApplicationInitializer实现类并传递给SpringServletContainerInitializer，
 * SSCI这个类会遍历扫描到的类依次调用onStartUp方法
 * <p>
 * servlet3.1规范
 * 1. 如果在resources/META-INF/services/javax.servlet.ServletContainerInitializer文件里的类
 * 实现了ServletContainerInitializer接口，tomcat在启动时会调用里面的onStartUp方法(spi技术)
 * 2. 如果该类还被@HandlesTypes()注解修饰，还会传递接口中的所有实现类
 * <p>
 * https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-servlet
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
