package com.madmk.annotation;

import com.alibaba.otter.canal.protocol.CanalEntry;

import java.lang.annotation.*;


/**
 * @author madmk
 * @date 2020/8/27 15:24
 * @description: 写在类声明上，标识类是 canal 调度的一部分
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Dispatcher {
}
