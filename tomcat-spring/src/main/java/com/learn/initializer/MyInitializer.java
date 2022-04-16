package com.learn.initializer;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.util.Set;

/**
 * tomcat在启动时会自动调用 ServletContainerInitializer 实现类的onStartUp方法(spi)
 * 同时需要配置到 META-INF/services/javax.servlet.ServletContainerInitializer 里面
 * <p>
 * 加上 @HandlesTypes(Init.class) 注解后，参数c里面是Init接口的所有实现类
 */
@HandlesTypes(Init.class)
public class MyInitializer implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        System.out.println("tomcat initializer");
    }
}
