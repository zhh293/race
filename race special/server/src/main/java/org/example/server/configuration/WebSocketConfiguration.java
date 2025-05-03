package org.example.server.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket配置类，用于注册WebSocket的Bean
 */
@Configuration
public class WebSocketConfiguration {

    @Bean
    @ConditionalOnWebApplication
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
/*这个代码我之后并没有创建它的对象，是不是可以推测这个被注入到ioc容器之后，依赖底层会进行某些操作需要用到他
是的，当你使用 @Bean 注解将 ServerEndpointExporter 方法注册到 Spring 的 IOC 容器中，即便你在代码里没有显式创建它的对象，底层框架也会用到它。
ServerEndpointExporter 是 Spring 对 JSR - 356（Java API for WebSocket）的支持组件，它的主要作用是扫描带有 @ServerEndpoint 注解的类，然后将这些 WebSocket 端点注册到 Spring 容器里，从而让 WebSocket 端点能够被 Spring 管理。
下面是它的具体工作流程：
注册到 IOC 容器：借助 @Bean 注解，ServerEndpointExporter 实例会被注册到 Spring 的 IOC 容器中。
自动扫描：ServerEndpointExporter 会自动扫描项目里所有带有 @ServerEndpoint 注解的类。
端点注册：把扫描到的 @ServerEndpoint 注解类注册为 WebSocket 端点，如此一来，这些端点就能处理 WebSocket 连接与消息了。
所以，即便你没有在代码中显式创建 ServerEndpointExporter 对象，Spring 框架在启动时会自动调用 serverEndpointExporter() 方法，
创建 ServerEndpointExporter 实例，进而完成 WebSocket 端点的注册工作。*/
    /*工作机制
ServerEndpointExporter的作用：ServerEndpointExporter属于 Spring 框架里专门用于 WebSocket 的一个组件。它的主要功能是扫描项目里所有带有@ServerEndpoint注解的类，
并且将这些类注册到 WebSocket 服务器中。
生命周期回调：当 Spring 容器完成ServerEndpointExporter这个 Bean 的创建后，会调用其afterPropertiesSet方法（ServerEndpointExporter实现了InitializingBean接口）。
在这个方法里，ServerEndpointExporter会扫描所有带有@ServerEndpoint注解的类，然后把这些类注册到底层的 WebSocket 服务器里。*/
    /*是的，被注入到 Spring IOC（控制反转）容器中的实例，并非所有方法都会自动执行。下面从不同情况来详细分析：
1. 常规方法不会自动执行
在 Spring IOC 容器里，一个 Bean 实例化后，
只有特定的方法会被自动调用，而普通的业务方法不会自动执行
import org.springframework.stereotype.Component;

@Component
public class MyService {

    public void doSomething() {
        System.out.println("Doing something...");
    }
}
在上述代码中，MyService类被标记为@Component，
会被 Spring IOC 容器管理。但doSomething方法不会自动执行，
只有在其他代码中显式调用时才会执行
. 特定生命周期方法会自动执行
Spring IOC 容器会在特定的生命周期阶段自动调用一些方法，常见的有以下几种情况：
实现InitializingBean接口
当一个 Bean 实现了InitializingBean接口，
Spring 会在 Bean 的属性设置完成后调用afterPropertiesSet方法
使用@PostConstruct注解
使用@PostConstruct注解标注的方法会在 Bean 的依赖注入完成后自动执行。*/
//so easy,这就是生命周期的意义吗，啊，爽了
}
