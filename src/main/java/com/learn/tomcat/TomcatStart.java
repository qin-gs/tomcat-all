package com.learn.tomcat;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

public class TomcatStart {
    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8090);
        // contextPath: 访问路径(localhost:port/contextPath)
        // docBase: 临时文件目录(会在当前目录下创建tomcat.8090的文件夹，需要在下面手动建一个webapps，否则启动有问题)
        tomcat.addWebapp("/tomcat", "./");

        // 启动tomcat, 会自动加载当前项目
        tomcat.start();
        // 阻塞tomcat，等待用户请求
        tomcat.getServer().await();
    }
}
