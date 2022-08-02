package com.learn;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;

/**
 * 通过代码启动 tomcat
 * <pre>
 * {@code
 *     <dependency>
 *         <groupId>org.apache.tomcat.embed</groupId>
 *         <artifactId>tomcat-embed-core</artifactId>
 *         <version>8.5.57</version>
 *    </dependency>
 *    添加 tomcat 对 jsp 支持
 *    <dependency>
 *         <groupId>org.apache.tomcat</groupId>
 *         <artifactId>tomcat-jasper</artifactId>
 *         <version>8.5.57</version>
 *    </dependency>
 * }
 * </pre>
 */
public class TomcatRun {
    private static final int PORT = 8080;
    /**
     * 上下文路径
     */
    private static final String CONTEXT_PATH = "/loader_tomcat_war_exploded";
    private static final String LOAD_SERVLET = "loadServlet";
    private static final String HELLO_SERVLET = "helloServlet";

    public static void main(String[] args) throws LifecycleException {

        // 创建 tomcat
        Tomcat tomcatServer = new Tomcat();
        // 指定端口号
        tomcatServer.setPort(PORT);
        // 是否设置自动部署
        tomcatServer.getHost().setAutoDeploy(false);
        // 创建上下文
        StandardContext standardContext = new StandardContext();
        standardContext.setPath(CONTEXT_PATH);
        // 监听上下文
        standardContext.addLifecycleListener(new Tomcat.FixContextListener());
        // host 容器添加 context
        tomcatServer.getHost().addChild(standardContext);

        // 添加 servlet，返回 wrapper
        Wrapper loadWrapper = tomcatServer.addServlet(CONTEXT_PATH, LOAD_SERVLET, "com.example.loader.LoadServlet");
        loadWrapper.setLoadOnStartup(1);
        Wrapper helloWrapper = tomcatServer.addServlet(CONTEXT_PATH, HELLO_SERVLET, "com.example.hello.HelloServlet");
        helloWrapper.setLoadOnStartup(-1);
        // servlet url 映射
        standardContext.addServletMappingDecoded("/load", LOAD_SERVLET);
        standardContext.addServletMappingDecoded("/hello-servlet", HELLO_SERVLET);
        tomcatServer.start();
        System.out.println("tomcat start success..");
        // 异步进行接收请求
        tomcatServer.getServer().await();

    }

}
