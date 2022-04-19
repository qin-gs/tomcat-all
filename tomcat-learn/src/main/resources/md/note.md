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

















