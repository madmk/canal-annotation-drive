package com.madmk;

import com.alibaba.otter.canal.protocol.CanalEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author madmk
 * @date 2020/9/3 17:12
 * @description: 默认异常处理
 */
public class DefaultFailureHandle implements FailureHandle {

    private static final Logger log = LoggerFactory.getLogger(DefaultFailureHandle.class);

    @Override
    public void on(WorkerEntry<?> worker, CanalEntry.Entry entry, Throwable e){
        log.error("捕获到异常",e);
    }
}
