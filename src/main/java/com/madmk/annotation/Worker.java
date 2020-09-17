package com.madmk.annotation;

import com.alibaba.otter.canal.protocol.CanalEntry;

import java.lang.annotation.*;

/**
 * @author madmk
 * @date 2020/8/27 15:35
 * @description: 标识在 Dispatcher 注解的方法内 标识这个方法是可被 canal 调用的
 */
@Target({ElementType.METHOD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Worker{

    /**
     * 事件类型 默认接受所有事件类型
     * @return
     */
    CanalEntry.EventType[] type() default {};

    /**
     * 库名称 默认接受所有库
     * @return
     */
    String schema() default "";

    /**
     * 表名称
     * @return
     */
    String table() default "";
}
