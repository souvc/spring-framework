
#### [项目主页](https://spring.io/projects/spring-framework)
#### [项目源码](https://github.com/spring-projects/spring-framework)
#### [项目文档](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/index.html)

#### Overview	

#### Core	

Core Container(核心容器)包含有Core、Beans、Context和Expression Language模块
 
- 说明
    - Core模块主要包含Spring框架基本的核心工具类
    - Beans模块是所有应用都要用到的，它包含访问配置文件、创建和管理bean以及进行Inversion of Control/Dependency Injection(Ioc/DI)操作相关的所有类
    - Context模块构建于Core和Beans模块基础之上，提供了一种类似于JNDI注册器的框架式的对象访问方法。Context模块继承了Beans的特性，为Spring核心提供了大量扩展，添加了对国际化(如资源绑定)、事件传播、资源加载和对Context的透明创建的支持。ApplicationContext接口是Context模块的关键
    - Expression Language模块提供了一个强大的表达式语言用于在运行时查询和操纵对象，该语言支持设置/获取属性的值，属性的分配，方法的调用，访问数组上下文、容器和索引器、逻辑和算术运算符、命名变量以及从Spring的IoC容器中根据名称检索对象。


- IoC Container
- Events
- Resources
- i18n
- Validation 
- Data Binding
- Type Conversion
- SpEL
- AOP

#### Testing	
Mock Objects, TestContext Framework, Spring MVC Test, WebTestClient.

#### Data Access	
Transactions, DAO Support, JDBC, O/R Mapping, XML Marshalling.

#### Web Servlet	
Spring MVC, WebSocket, SockJS, STOMP Messaging.

#### Web Reactive	
Spring WebFlux, WebClient, WebSocket.

#### Integration	
Remoting, JMS, JCA, JMX, Email, Tasks, Scheduling, Caching.

#### Languages	
Kotlin, Groovy, Dynamic Languages.