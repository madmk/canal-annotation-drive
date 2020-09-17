package com.madmk;

/**
 * @author madmk
 * @date 2020/8/27 17:43
 * @description: 调度器工厂 用来创建调度器
 */
public interface DispatcherFactory {
    /**
     * 根据类对象创建实例
     * @param aClass 创建的类对象
     * @return
     */
    <T> T create(Class<T> aClass) throws Exception;
}
