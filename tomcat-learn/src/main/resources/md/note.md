## 深入剖析 tomcat



### 1. 一个简单的Web 服务器



#### 1.1 HTTP

http 请求：请求方法，请求头，空行，请求体

http 响应：协议，状态码，描述，响应头，空行，响应体

uri：相对于服务器根目录的相等路径，以 "/" 开头

url：统一资源定位符，是 uri 的一种



#### 1.2 java.net.Socket

客户端端套接字：网络连接的端点，可以使应用程序从网络中读取/写入数据；



#### 1.3 java.net.ServerSocket

服务端套接字：等待(accept)来自客户端的请求，请求过来后创建一个 Socket 处理与客户端的通信



#### 1.4 应用程序





### 2. 一个简单的 servlet 容器



#### 2.1 javax.servlet.Servlet 接口



### 3. 连接器

**Catalina = 连接器(等待 http 请求) + 容器(创建 request 和 response 对象)**

连接器：servlet2.3 中连接器必须创建 `javax.servlet.http.HttpServletRequest` 和`javax.servlet.http.HttpServletResponse`，并传递给被调用的 servlet 的 service 方法



### 4. Tomcat4 的默认连接器

- 实现 `org.apache.catalina.Connector` 接口
- 创建实现类 `org.apache.catalina.Request` 接口的对象
- 创建实现类 `org.apache.catalina.Response` 接口的对象

等待 http 请求，创建 request， response 对象，调用 `org.apache.cataline.Container#invoke` 方法将两个对象传递给 servlet，invoke 方法内载入响应的 servlet 类，调用 service 方法，管理 session 对象，记录错误消息等；



#### 4.1 http1.1 的新特性

- 长连接 `connection: keep-alive`

- 块编码 `transfer-encoding`

  块长度 \n\r

  块内容 \n\r

  块长度\n\r

  块内容\n\r

  0\n\r

- 状态码 100 的使用 `客户端发送expect: 100-continue`  `服务器返回 http/1.1 100 continue`

  当客户端准备发送-一个较长的请求体，而不确定服务端是否会接收时，就可能会发送上面的头信息。若是客户端发送了较长的请求体，却发现服务器拒绝接收时，会是较大的浪费。



#### 4.2 Connector 接口

`org.apache.catalina.Connector` 

- setContainer：将连接器 和 servlet 管理
- getContainer：返回当前连接器关联的 servlet
- createRequest：创建 request 对象
- createResponse：创建 response 对象



#### 4.3 HttpConnector 类

- 创建服务器套接字

- 维护 HttpProcessor 实例

  使用实例池，进行重用

- 提供 http 请求服务

  获取一个 HttpProcessor 进行处理



#### 4.4 HttpProcessor 类

需要**异步**来同时处理多个 http 请求



#### 4.5/6 Request/Response 对象



#### 4.7 处理请求

- 解析连接
- 解析请求
- 解析请求头





### 5. servlet 容器

- engine
- host
- context
- wrapper

(存放 servlet 实例)

#### 5.1 org.apache.catalina.Container 接口

Catalina 中有四种类型的容器

容器中可以包含一些支持的组件：载入器(loader)，记录器(logger)，管理器(manager)，领域(realm)，资源(resources)

![容器类型及其标准实现](../img/容器类型及其标准实现.png)



#### 5.2 管道任务

管道：管道包含该 servlet容器 将要调度的任务

阀：一个阀表示一个具体的执行任务(向基础阀(**最后**调用)中添加其它的阀)

一个 servlet容器可以有一条管道。当调用了容器的 invoke() 方法后，容器会将处理工作交由管道完成，而管道会调用其中的第1个阀开始处理。当第1个阀处理完后，它会调用后续的阀继续执行任务，直到管道中所有的阀都处理完成。(类似于过滤器)

**wrapper 实例中的基础阀负责调用 servlet 实例的 service 方法**

tomcat 使用 `org.apache.catalina.ValveContext#invokeNext` 实现阀的遍历执行



- `org.apache.catalina.Pipeline`

  servlet容器 调用 invoke 方法开始调用管道中的阀 和 基础阀

- `org.apache.catalina.Valve`

  阀

- `org.apache.catalina.ValveContext`

- `org.apache.catalina.Container`

  阀 通过实现该接口可以与一个 servlet容器 关联



#### 5.3 Wrapper 接口

最低级的容器，不能再添加子容器了；负责管理 servlet 类的生命周期(init, service, destroy...)

- org.apache.catalina.Wrapper#load：初始化并载入 servlet 类
- org.apache.catalina.Wrapper#allocate：分配一个已载入的 servlet 实例



#### 5.4 Context 接口

可以添加一个或多个 Wrapper 作为子容器



#### 5.5 应用程序

当程序中有多个 wrapper 实例时，tomcat4 中使用 org.apache.catalina.Mapper 映射器来选择子容器处理某个指定的请求



#### 5.6 Context 应用程序

- 容器包含一条管道，容器的 invoke() 方法会调用管道的 invoke() 方法:
- 管道的 invoke() 方法会调用所有添加到其容器中的阀，然后再调用其基础阀的 invoke() 方法; .
- 在 Wrapper 实例中，**基础阀负责载入相关联的servlet类**，并对请求进行响应;
- 在包含子容器的 Context 实例中，**基础阀使用映射器来查找一个子容器**，该子容器负责处理接收到的请求。若找到了相应的子容器，则调用其invoke() 方法，转到步骤 1) 继续执行。



### 6. 生命周期



#### 6.1 Lifecycle 接口

通过父组件统一管理子组件的生命周期



#### 6.2 LifecycleEvent 类

生命周期事件



#### 6.3 LifecycleListener 接口

生命周期事件监听器



#### 6.4 LifecycleSupport 类

帮助组件管理监听器，触发相应的生命周期事件



#### 6.5 应用程序





### 7. 日志记录器



#### 7.1 org.apache.catalina.Logger 接口

所有的日志记录器都要实现该接口



#### 7.2 日志记录器

- 父类 LoggerBase

  子类需要重写 public abstract void log(String msg); 方法

- FileLogger：将信息写入文件
- SystemErrLogger：System.out.println(msg)
- SystemOutLogger：System.err.println(msg);





### 8. 载入器



#### 8.1 java 的类载入器

- 启动类加载器
- 扩展类加载器
- 应用程序类加载器



#### 8.2 org.apache.catalina.Loader 接口

web 应用程序载入器，有一个自定义的类加载器

载入器通常与 Context 级别的容器相关联

载入器支持修改后类的重新载入，默认为禁用

```xml
<!-- 在 server.xml 中添加如下元素 -->
<Context path="/app" docBase="app" debug="0" reloadable="true"/>
```

当与某个载入器相关联的容器需要使用某个servlet 类时，即当该类的某个方法被调用时，容器会先调用载入器的getClassLoader() 方法来获取类载入器的实例。然后，容器会调用类载入器的 loadClass() 方法来裁入这个servlet类。



#### 8.3 org.apache.catalina.loader.Reloader 接口

- modified：某个类是否被修改
- addRepository：添加仓库
- findRepository：返回实现了 Reloader 接口的类载入器的所有仓库



#### 8.4 org.apache.catalina.loader.WebappLoader 类

载入器，负责载入 web 应用程序中所用到的类

该类创建一个 org.apache.catalina.loader.WebappClassLoader 作为类载入器

通过实现 java.lang.Runnable 接口不断的调用载入器的 modified 方法，如果发现类被修改通知载入器相关联的 servlet容器(Context)，由容器完成类的重新载入

- 创建类加载器
- 设置仓库：/WEB-INF/classes，/WEB-INF/lib
- 设置类路径
- 设置访问权限
- 开启新线程执行类的重新载入



#### 8.5 org.apache.catalina.loader.WebappClassLoader 类

类载入器，某些包下的类是不允许载入的

- 类缓存

  保存已载入 和 载入失败的类，作为 资源(org.apache.catalina.loader.ResourceEntry) 存储

- 类载入

  



### 9. Session 管理

通过 Session管理器 管理创建的 session 对象



#### 9.1 Session 对象

![session继承关系](../img/session继承关系.png)

StandardSession 类

创建 session 对象必须给一个管理器实例

StandardSessionFacade 类

为了传递一个 Session 对象给 servlet 实例，Catalina 会实例化 StandardSession 类，填充该 Session 对象，然后再将其传给 servlet 实例。但是，实际上，Catalina 传递的是 Session 的外观类 StandardSessionFacade 的实例，该类仅仅实现了 javax.servlet.http.HttpSession 接口中的方法。这样，servlet 程序员就不能将HttpSession对象向下转换为StandardSession类型，也阻止了servlet 访问一些敏感方法



#### 9.2 Manager

![Manager继承关系](../img/Manager继承关系.png)

org.apache.catalina.session.StandardManager 一个标准实现，实现了 Lifecycle 接口，可以通过容器启动关闭；实现了 Runnable 接口来销毁失效的对象

PersistentManagerBase 类可以将 session 进行持久化，需要完成备份 和 换出 session

- 换出

  只有当活动 Session 对象的数量超过了变量 maxActiveSessions 指定的上限值，或者该 Session 对象闲置了过长时间后，才会换出它。

  - 当内存中有过多的 Session 对象时，PersistentManagerBase 实例会直接将Session对象换出，直到当前活动Session对象的数量等于变量maxActiveSessions指定的数值(参见processMaxActiveSwaps()方法)。

  - 当某个Session对象闲置了过长时间时，PersistentManagerBase类会依据两个变量的值来决定是否将这个Session对象换出

    这两个变量分别是 minIdleSwap 和 maxldleSwap.如果某个 Session 对象的 lastAccessedTime 属性的值超过了 minIdleSwap 和 maxIdleSwap 的值，就会将这个 Session 对象换出。为了防止换出 Session 对象，可以将变量 maxldleSwap 的值置为负数(参见 processMaxIdleSwaps() 方法)。

- 备份

  只会备份那些空闲时间超过了 maxIdleBackup 指定的值的 session 对象





#### 9.3 存储器

org.apache.catalina.Store 完成持久化存储功能

save：将指定 session 存储到某种持久性存储器中

load：从存储器中将 session 载入到内存

![Store继承关系](../img/Store继承关系.png)



当调用 getSession() 方法时，request 对象必须调用与 Context 容器相关联的 Session管理器。Session 管理器组件要么创建-一个新的 session 对象，要么返回一个已经存在的 session 对象。request 对象为了能够访问 Session 管理器，它必须能够访问 Context 容器。



### 10. 安全性

servlet容器是通过一个名为验证器的阀来支持安全限制的。当servlet容器启动时，验证器阀会被添加到Context容器的管道中。

在调用Wrapper阀之前，会先调用验证器阀，对当前用户进行身份验证。如果用户输入了正确的用户名和密码，则验证器阀会调用后续的阀，继续显示请求的servlet。如果用户未能通过身份验证，则验证器阀会返回，而不会调用后面的阀。身份验证失败的话，用户就无法查看请求的servlet资源了。



- 领域

  org.apache.catalina.Realm

  领域对象用来对用户进行身份验证的组件。它会对用户输入的用户名和密码对进行有效性判断。领域对象通常都会与一个Context容器相关联，而一个Context容器也只能有一个领域对象。可以调用Context容器的setRealm()方法来将领域对象与该Context容器相关联。

  它保存了所有有效用户的用户名和密码对，或者它会访问存储这些数据的存储器。这些数据的具体存储依赖于领域对象的具体实现。在Tomcat中，有效用户信息默认存储在tomcat-user.xml文件中。但是可以使用其他的领域对象的实现来针对其他资源验证用户身份，例如查询-一个关系数据库。

- 主体

  java.security.Principal 实现类 GenericPrinciple ，需要与一个领域对象相关联

- 登录配置

  LoginConfig，包含 领域对象名，身份验证方法

- 验证器

  Authenticator 这是一个 **阀**

- 安装验证器阀







### 11. StandardWrapper

每个 wrapper 代表一个具体的 servlet 定义



#### 11.1 方法调用序列



<img src="../img/http请求调用协作图.png" alt="http请求调用协作图" style="zoom:50%;" />



1. 连接器创建 request 和 response 对象;
2. 连接器调用StandardContext实例的invoke()方法:
3. 接着, StandardContext 实例的 invoke() 方法调用其管道对象的invoke() 方法。StandardContext 中管道对象的基础阀是 StandardContextValve 类的实例，因此，StandardContext 的管道对象会调用StandardContextValve实例的 invoke() 方法;
4. StandardContextValve 实例的 invoke() 方法获取相应的 Wrapper 实例处理HTTP请求，调用 Wrapper 实例的 invoke() 方法;
5. StandardWrapper 类是 Wrapper 接口的标准实现，StandardWrapper 实例的 invoke() 方法会调用其管道对象的 invoke() 方法:
6. StandardWrapper 的管道对象中的基础阀是 StandardWrapperValve 类的实例，因此，会调用StandardWrapperValve 的 invoke() 方法，StandardWrapperValve 的 invoke() 方法调用 Wrapper 实例的allocate() 方法获取 servlet 实例;
7. allocate() 方法调用 load() 方法载人相应的 servlet 类，若已经载入，则无需重复载入;
8. load() 方法调用 servlet 实例的 init() 方法;
9. StandardWrapperValve 调用 servlet 实例的 service() 方法。



- StandardContext 的构造函数会设置 StandardContextValve 类的一个实例作为其基础阀；

- StandardWrapper 的构造函数会设置 StandardWrapperValve 类的一个实例作为其基础阀;





#### 11.2 SingleThreadModel

~~已被废弃：servlet 实现 javax.servlet.SingleThreadModel 类保证一次只处理一个请求，(如果使用了静态变量，不能保证线程安全)~~



#### 11.3 StandardWrapper

- StandardWrapper：**载入(载入器)并实例化 servlet**，调用 init

- StandardWrapperValve：调用 allocate 方法从 StandardWrapper 中获取 servlet 实例，调用 servlet#service 方法

- ServletConfig：调用 servlet#init 方法时需要传入该对象(实际传入的是一个外观类)

  - getServletContext：获取 wrapper 所在的 context (wrapper 必须放置在某个 context 容器中，并且不能有子容器)
  - getInitParameter：初始化参数存储在一个 HashMap 中

  

#### 11.4 StandardWrapperFacade

调用 init 方法时传入的外观类



#### 11.5 StandardWrapperValve

基础阀

- 执行与该 servlet 关联的所有过滤器
- 调用 servlet#service 方法

基础阀中 invoke 方法的逻辑

1. 调用 StandardWrapper 实例的 allocate() 方法获取该 StandardWrapper 实例所表示的servlet
   实例:
2. 调用私有方法 createFilterChain()，创建过滤器链（ApplicationFilterChain）:
3. 调用过滤器链的 doFilter() 方法，其中包括调用 servlet 实例的 service()方法;
4. 释放过滤器链;
5. 调用Wrapper实例的 deallocate() 方法: 
6. 若该 servlet 类再也不会被使用到，则调用 Wrapper 实例的 unload() 方法。



#### 11.6 FilterDef

过滤器定义



#### 11.7 ApplicationFilterConfig

管理第一次启动时创建的所有过滤器实例，需要 Context 和 FilterDef

getFilter：负责载入过滤器并实例化



#### 11.8 ApplicationFilterChain

StandardWrapperValve 对象会调用 invoke 方法创建该对象，并调用 doFilter 方法调用第一个过滤器，调用完最后一个调用 service 方法





### 12. StandardContext

context 需要其它组件支持(载入器，session 管理器)



#### 12.1 StandardContext 配置

创建该对象后调用 start 方法为每个 http 请求提供服务

- 创建并配置成功后会读取解析 %CATALINA_HOME%/conf/web.xml 文件 (该文件的内容会应用到所有部署到 tomcat 中的应用程序，保证 StandardContext 可以处理应用程序级别的 web.xml 文件)
- 配置基础阀(StandardContextValve) 和 许可阀



org.apache.catalina.core.StandardContext#start

- 触发 BEFORE_START 事件
- 将 availability 属性设置为 false
- 将 configured 属性设置为 false
- 配置资源
- 设置载入器
- 设置 Session管理器
- 初始化字符集映射器
- 启动与该 Context容器 相关联的组件
- 启动子容器
- 启动管道对象
- 启动 Session管理器
- 触发 START 事件，在这里监听器(ContextConfig 实例)会执行一些配置操作，若设置成功，ContextConfig 实例会将StandardContext实例的 configured 变量设置为true
- 检查 configured 属性的值，若为 true,则调用 postWelcomePages() 方法，载人那些需要在启动时就载入的子容器，即Wrapper实例，将 availability 属性设置为 true.若 configured 变量为 false ,则调用stop()方法
- 触发AFTER_ START事件。



invoke 方法由与其关联的 连接器 或 父容器host 调用



#### 12.2 StandardContextMapper

StandardContextValve 基础阀，调用 context 的 map 方法，根据协议返回映射器；

StandardContextMapper 映射器，必须与 context 容器相关联，通过 map 方法返回一个 wrapper 处理请求，

对于每个引入的HTTP请求，都会调用 StandardContext 实例的管道对象的基础阀的 invoke() 方法来处理。StandardContext 实例的基础阀是 org. apache.catalina.core.StandardContextValve 类的实例。StandardContextValve 类的invoke() 方法要做的第一件事是获取一个要处理HTTP请求Wrapper实例。



#### 12.3 对重载的支持

通过载入器实现，当 web.xml 或 WEB-INF/classes 下的文件发生变化时，会重新载入



#### 12.4 backgroundProcess()

tomcat5 中一些后台处理任务共享同一个线程

- 载入器需要周期性的检查类是否被修改
- session 管理器需要周期性的检查是否由对象过期





### 13. Host 和 Engine



#### 13.1 Host 接口

map 方法返回 一个 context 处理请求 



#### 13.2 StandardHost

基础阀 StandardHostValve，调用 start 方法时会加入两个阀 ErrorReportValve, ErrorDispatcherValve

请求过来后，会调用 invoke 方法，

map 方法获取相应的 context 实例处理 http 请求



#### 13.3 StandardHostMapper

映射器



#### 13.4 StandardHostValve

基础阀，请求过来后会调用 invoke(request, response, valueContext) 方法进行处理，调用 StandardHost 的 map 方法获取一个 context，然后获取 request 对应的 session，修改最后访问时间；最后调用 Context 的 invoke 方法处理请求



#### 13.5 必须有一个 Host容器

如果一个 context 使用 ContextConfig 对象进行设置，必须使用一个 Host 对象

ContextConfig 会读取 web.xml 文件，里面用到了 Host



#### 13.7 Engine

部署 tomcat 时需要支持多个虚拟机的话需要用到 Engine 容器

可以设置默认的 Host 或 Context，但是子容器只能是 Host，不能有父容器



#### 13.8 StandardEngine

构造函数中会添加基础阀 StandardEngineValve



#### 13.9 StandardEngineValve

基础阀：验证一下 request 和 response，调用 map 方法得到一个 Host，调用 invoke 方法处理请求



### 14. 服务器组件 和 服务组件



#### 14.1 服务器组件

org.apache.catalina.Server 接口表示 catalina 的整个 servlet 引擎，使用一种方法来 启动/关闭 系统(不需要分别处理 连接器，处理器)



#### 14.2 StandardServer

可以向服务器组件中 添加/删除/查找 服务组件，有四个生命周期相关的方法，可以初始化并启动服务器组件

- initialize：初始化添加的服务器组件，只会初始化一次
- start：启动服务器组件
- stop：关闭服务器组件
- await：等待关闭整个 tomcat 的命令(创建一个 ServerSocket，默认监听 8085 端口，)



#### 14.3 Service

服务组件：一个服务组件可以有一个 servlet容器 和 多个连接器



#### 14.4 StandardService

- 一个 servlet 容器
- 多个 连接器：可以处理不同的协议 (http, https)

生命周期方法

- initialize：调用服务组件中所有连接器的 initialize 方法
- start：启动被添加到该服务组件中的连接器 和 servlet容器
- stop：关闭与该服务组件相关联的 servlet容器 和 所有连接器



![启动过程](../img/启动过程.png)



### 15. Digester

使用 server.xml 文件避免手动创建设置对象



#### 15.1 使用 Digester 库解析 xml



#### 15.2 ContextConfig 类

StandardContext 需要一个监听器，用来配置 StandardContext 实例 (设置 configured 变量)

使用 Digester 读取解析 web.xml 文件，为每个 servlet 元素创建一个 StandardWrapper





### 16. 关闭钩子

虚拟机在执行关闭阶段会启动所有已注册的关闭钩子

```java
// 注册关闭钩子
Runtime.getRuntime().addShutdownHook(new Thread() {
    @Override
    public void run() {
        System.out.println("hook");
    }
});
```

tomcat 的关闭钩子会调用 Server 对象的 stop 方法，执行关闭操作



### 17. 启动 Tomcat

- Bootstrap：负责创建 Catalina 对象，调用 process 方法
- Catalina：负责启动 或 关闭 Server，解析配置文件



#### 17.1 Catalina

Catalina类包含一个 Digester 对象，用来解析 server.xml，封装了-一个Server对象，该对象有一个Service 对象。Service 对象包含有一个Servet容器和一个或多个连接器。可以使用Catalina类来启动关闭Server对象。

- start：

  创建一个 Digester 实例来解析 server.xml 文件；Catalina 对象的 start() 方法会调用 Server 对象的 await() 方法，Server 对象会使用一个专用的线程来等待关闭命令。await() 方法会循环等待，直到接收到正确的关闭命令。当 await() 方法返回时，Catalina 对象的 start() 方法会调用 Server 对象的 stop() 方法，从而关闭Server 对象和其他的组件。此外，start() 方法还会利用关闭钩子，确保用户突然退出应用程序时会执行Server 对象的stop() 方法。

- stop：

  stop() 方法用来关闭 Catalina 和 Server 对象



#### 17.2 Bootstrap

运行 main 方法，调用 Catalina#process 方法

创建三个载入器 (放置应用程序使用 WEB-INF/classes 和 WEB-INF/lib 之外的类)，然后创建一个 Catalina，最后调用 process 方法



#### 17.3 在 Windows 和 Unix 平台上通过脚本 启动/关闭



### 18. 部署器

Context 容器使用 war 形式来部署，需要放到 Host 中；在 StandardHost 中使用 HostConfig 类型的生命周期监听器

当调用 StandardHost 实例的 start() 方法时，它会触发 START 事件。HostConfig 实例会对此事件进行相应，并调用其自身的 start() 方法，该方法会逐个部署并安装指定目录中的所有 Web 应用程序。











