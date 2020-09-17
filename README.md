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


