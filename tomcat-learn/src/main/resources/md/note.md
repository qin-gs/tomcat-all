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

**Cataline = 连接器(等待 http 请求) + 容器(创建 request 和 response 对象)**

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











