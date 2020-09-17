package com.madmk;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.google.protobuf.InvalidProtocolBufferException;
import com.madmk.annotation.Dispatcher;
import com.madmk.annotation.Worker;
import com.madmk.util.ArrayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * @author madmk
 * @date 2020/8/27 17:24
 * @description: 记录需要使用的注解和注解对应的方法
 */
public class WorkerEntry<T> {
    private static final Logger log = LoggerFactory.getLogger(WorkerEntry.class);

    /**
     * 被允许的参数类型
     */
    private final static Class<?>[] ALLOW_PARAMETER_TYPE ={
            CanalEntry.Entry.class,
            CanalEntry.RowChange.class,
            CanalEntry.Header.class
    };


    /**
     * 注解信息
     */
    private final Dispatcher dispatcher;

    /**
     * 注解信息
     */
    private final Worker worker;

    /**
     * 工作方法
     */
    private final Method method;

    /**
     * 调用实例
     */
    private final T instance;

    /**
     * 参数列表
     */
    private final Parameter[] parameters;

    /**
     * 参数类型列表
     */
    private final Class<?>[] parameterTypes;

    public WorkerEntry(Dispatcher dispatcher, Worker worker, Method method,T instance) {
        this.dispatcher = dispatcher;
        this.worker = worker;
        this.method = method;
        this.instance = instance;
        this.parameters = method.getParameters();
        if(this.parameters==null||this.parameters.length==0){
            this.parameterTypes =new Class[0];
            return;
        }
        this.parameterTypes =new Class[this.parameters.length];
        for (int i = 0; i < this.parameters.length; i++) {
            int allowi=matchAllowParameterType(parameters[i].getType());
            if(allowi<0){
                throw new IllegalArgumentException("类："+instance.getClass().getName()+"方法："+method.getName()+",参数类型只允许为："+Arrays.toString(ALLOW_PARAMETER_TYPE));
            }
            this.parameterTypes[i]=ALLOW_PARAMETER_TYPE[allowi];
        }
    }

    /**
     * 匹配允许的参数类型的第几个
     * @return
     */
    private int matchAllowParameterType(Class<?> aClass){
        for (int i = 0; i < ALLOW_PARAMETER_TYPE.length; i++) {
            if(aClass.isAssignableFrom(ALLOW_PARAMETER_TYPE[i])){
               return i;
            }
        }
        return -1;
    }
    /**
     * 判断是否支持处理消息
     * @param schema 库名称
     * @param table 表名称
     * @param eventType 操作类型
     * @return 是否支持处理这个消息
     */
    boolean support(String schema,String table,CanalEntry.EventType eventType){
        if(worker.type().length > 0 && !ArrayUtil.exist(worker.type(), eventType)){
            return false;
        }
        if(worker.schema().length()>0 && !worker.schema().equals(schema)){
            return false;
        }
        if(worker.table().length()>0 && !worker.table().equals(table)){
            return false;
        }
       return true;
    }

    /**
     * 调用执行方法
     * @param entry 消息内容
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    void invoke(CanalEntry.Entry entry) throws InvocationTargetException, IllegalAccessException, InvalidProtocolBufferException {
        CanalEntry.Header header=entry.getHeader();
        log.info("处理消息 EventType：{} SchemaName：{} TableName：{},处理方法：{}",header.getEventType(),header.getSchemaName(),header.getTableName(),method);
        if(this.parameterTypes.length==0){
            method.invoke(instance);
            return;
        }
        Object[] parameters=new Object[this.parameterTypes.length];
        for (int i = 0; i < this.parameterTypes.length; i++) {
            if(ALLOW_PARAMETER_TYPE[0]==this.parameterTypes[i]){
                parameters[i]=entry;
            }else if(ALLOW_PARAMETER_TYPE[1]==this.parameterTypes[i]){
                parameters[i]=CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            }else if(ALLOW_PARAMETER_TYPE[2]==this.parameterTypes[i]){
                parameters[i]=header;
            }else{
                parameters[i]=null;
            }
        }
        method.invoke(instance,parameters);
    }
}
