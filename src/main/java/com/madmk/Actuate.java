package com.madmk;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.madmk.annotation.Dispatcher;
import com.madmk.annotation.Worker;
import com.madmk.configuration.LinkConfiguration;
import com.madmk.util.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;

/**
 * @author madmk
 * @date 2020/8/27 15:43
 * @description: 驱动类
 */
public class Actuate {
    private static final Logger log = LoggerFactory.getLogger(Actuate.class);

    /**
     * 调度管理器
     */
    private final WorkerEntryManagement workerEntryManagement;

    /**
     * 连接配置
     */
    private final LinkConfiguration linkConfiguration;

    /**
     * 连接
     */
    private CanalConnector connector;
//
//    /**
//     * 使用消息队列存储消息
//     */
//    private BlockingQueue<Message> blockingQueue;

    public Actuate(String scanPackage,LinkConfiguration linkConfiguration) throws Exception {
        this(scanPackage,linkConfiguration,new SimpleDispatcherFactory(), new DefaultFailureHandle());
    }
    public Actuate(String scanPackage,LinkConfiguration linkConfiguration,FailureHandle failureHandle) throws Exception {
        this(scanPackage,linkConfiguration,new SimpleDispatcherFactory(), failureHandle);
    }
    public Actuate(String scanPackage,LinkConfiguration linkConfiguration,DispatcherFactory dispatcherFactory) throws Exception {
        this(scanPackage,linkConfiguration,dispatcherFactory, new DefaultFailureHandle());
    }

    /**
     * 创建驱动类
     * @param scanPackage 注解所在的包
     * @param linkConfiguration 链接配置
     * @param dispatcherFactory 调度工厂
     * @param failureHandle 异常处理器
     */
    public Actuate(String scanPackage,LinkConfiguration linkConfiguration,DispatcherFactory dispatcherFactory,FailureHandle failureHandle) throws Exception {
        log.debug("开始扫描驱动类：{}",scanPackage);
        Set<Class<?>> classes= ClassUtil.getClass(scanPackage,true);
        this.linkConfiguration=linkConfiguration;
        this.workerEntryManagement=new WorkerEntryManagement(failureHandle);
        for (Class<?> aClass : classes) {
            Dispatcher dispatcher=aClass.getAnnotation(Dispatcher.class);
            if(dispatcher==null){
                continue;
            }
            log.debug("创建驱动类实例：{}",aClass);
            Object instance=dispatcherFactory.create(aClass);
            for (Method method : aClass.getMethods()) {
                if(!method.isAnnotationPresent(Worker.class)){
                    continue;
                }
                Worker worker=method.getAnnotation(Worker.class);
                log.debug("扫描到工作方法：{}",method);
                workerEntryManagement.add(new WorkerEntry<>(dispatcher,worker,method,instance));
            }
        }
    }

    private void run(){
        // 开始连接
        initConnector();
        log.debug("开始获取变更数据");
        while (true) {
            // 获取指定数量的数据
            Message message = connector.getWithoutAck(linkConfiguration.getBatchSize());
            long batchId = message.getId();
            int size = message.getEntries().size();
            // 如果没有消息停止100毫秒后继续
            if (batchId == -1 || size == 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                continue;
            }
            List<CanalEntry.Entry> entries = message.getEntries();
            entries.forEach(workerEntryManagement::process);
            connector.ack(batchId);
        }
    }

    /**
     * 执行死循环，时时接收并消费消息
     */
    public void start(){
        Thread thread=new Thread(this::run);
        thread.setUncaughtExceptionHandler((t,e)->{
            log.error("异常终止，5秒后自动重启");
            e.printStackTrace();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ie) {
            }
            log.error("重启中");
            start();
        });
        thread.start();
    }

    /**
     * 初始化连接
     */
    private void initConnector(){
        while (true){
            try {
                if(connector!=null){
                    log.debug("删除失败连接");
                    connector.disconnect();
                    connector=null;
                }
                // 创建链接
                log.debug("初始化连接");
                connector = CanalConnectors.newSingleConnector(new InetSocketAddress(linkConfiguration.getHostname(),
                        linkConfiguration.getPort()), linkConfiguration.getDestination(), linkConfiguration.getUsername(), linkConfiguration.getPassword());
                connector.connect();
                connector.subscribe(linkConfiguration.getSubscribeFilter());
                connector.rollback();
                return;
            }catch (Throwable throwable){
                log.error("连接失败延时 5s后重试");
                throwable.printStackTrace();
                // 连接失败 延时 5s后重试
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
            }
        }

    }

}
