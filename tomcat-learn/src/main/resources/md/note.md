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

等待 http 请求，创建 request， response 对象，调用 `org.apache.cataline.Container#invoke` 方法将两个对象传递给 servlet，invoke 方法内载入响应的 servekt 类，调用 service 方法，管理 session 对象，记录错误消息等；



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



