## 为canal 客户端 添加注解驱动方式

[阿里巴巴canal](https://github.com/alibaba/canal)

使用示例：[canal-annotation-drive-example](https://github.com/madmk/canal-annotation-drive-example)

[点击下载](https://github.com/madmk/canal-annotation-drive/releases
) madmk.zip 解压在 mevan 依赖下面即可通过mevan引用使用

pom:
```xml
        <dependency>
            <groupId>com.madmk</groupId>
            <artifactId>canal-annotation-drive</artifactId>
            <version>0.1.0-SNAPSHOT</version>
        </dependency>
```

java:
```java
    public static void main(String[] args) throws Exception {
        // 创建配置
        LinkConfiguration linkConfiguration=new LinkConfiguration(你的canal服务地址,canal服务端口, canal实例名称, canal实例账户名,canal实例密码,canal监听范围表达式,单次获取并确认的消息数量);
        // 创建执行对象
        Actuate actuate=new Actuate(被 @Dispatcher 注解标记的类所在的包,linkConfiguration);
        // 开始启动
        actuate.start();
        // 启动成功
    }
```
1. 被 `@Dispatcher` 注释的类必须至少有一个无参构造函数 （可通过自定义`DispatcherFactory.java`突破此限制，比如与spring 整合时想使用spring的Bean）
2. 被 `@Worker` 注释的方法的参数 类型 只允许为`CanalEntry.Entry`、`CanalEntry.RowChange`、`CanalEntry.Header`顺序不限。
3. 异常处理 可通过自定义 `FailureHandle.java` 实现
4. 数据库消息为顺序消费，但如果一条消息同时满足多个`@Worker`则`@Worker`将同时执行，全部执行完成后继续消费下一条消息