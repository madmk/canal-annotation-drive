package com.madmk;

import com.alibaba.otter.canal.protocol.CanalEntry;

/**
 * @author madmk
 * @date 2020/9/3 17:08
 * @description: 执行失败处理器
 */
public interface FailureHandle {

    /**
     * 异常处理
     * @param worker 调度器
     * @param entry 消息内容
     * @param e 异常信息
     */
    void on(WorkerEntry<?> worker, CanalEntry.Entry entry,Throwable e);
}
