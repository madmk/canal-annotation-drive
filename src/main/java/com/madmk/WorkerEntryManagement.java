package com.madmk;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.google.protobuf.InvalidProtocolBufferException;
import com.madmk.util.ArrayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author madmk
 * @date 2020/8/27 17:27
 * @description: Worker 管理器
 */
public class WorkerEntryManagement extends ArrayList<WorkerEntry<?>> {

    private static final Logger log = LoggerFactory.getLogger(WorkerEntryManagement.class);

    /**
     * 只处理 增 删 改 的消息
     */
    private static final CanalEntry.EventType[] executeType={CanalEntry.EventType.INSERT,CanalEntry.EventType.DELETE,CanalEntry.EventType.UPDATE};
//    /**
//     * 处理任务的线程池
//     */
//    private final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    /**
     * 处理任务的线程池
     */
    private final ExecutorService cachedThreadPool = new ThreadPoolExecutor(0, 50,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());

    /**
     * 异常处理器
     */
    private final FailureHandle failureHandle;

    public WorkerEntryManagement(FailureHandle failureHandle) {
        this.failureHandle = failureHandle;
    }


    /**
     * 处理一条消息
     * @param entry 消息内容
     */
    public void process(CanalEntry.Entry entry){
        CanalEntry.Header header=entry.getHeader();
        if(!ArrayUtil.exist(executeType, header.getEventType())){
            log.debug("不处理此类消息：{}",header.getEventType());
            return;
        }
        log.debug("开始处理消息 EventType：{} SchemaName：{} TableName：{}",header.getEventType(),header.getSchemaName(),header.getTableName());
        // 收集符合条件待执行的调度器
        List<WorkerEntry<?>> workerEntries=new ArrayList<>();
        for (WorkerEntry<?> workerEntry : this) {
            if(workerEntry.support(header.getSchemaName(),header.getTableName(),header.getEventType())){
                workerEntries.add(workerEntry);
            }
        }
        if(workerEntries.size()<=0){
            log.debug("没有处理此消息的方法 EventType：{} SchemaName：{} TableName：{}",header.getEventType(),header.getSchemaName(),header.getTableName());
            return;
        }

        // 线程计数器
        CountDownLatch countDownLatch=new CountDownLatch(workerEntries.size());
        // 执行调度器
        for (WorkerEntry<?> workerEntry : workerEntries) {
            cachedThreadPool.execute(()-> {
                try {
                    workerEntry.invoke(entry);
                } catch (InvocationTargetException e){
                    failureHandle.on(workerEntry,entry, e.getTargetException());
                } catch ( IllegalAccessException | InvalidProtocolBufferException e) {
                    failureHandle.on(workerEntry,entry, e);
                }finally {
                    countDownLatch.countDown();
                }
            });
        }
        // 等待所有调度器执行结束
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
