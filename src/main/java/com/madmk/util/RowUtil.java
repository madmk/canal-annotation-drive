package com.madmk.util;

import com.alibaba.otter.canal.protocol.CanalEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author madmk
 * @date 2020/9/1 15:26
 * @description:
 */
public class RowUtil{

    /**
     * 解析信息存储地
     */
    private final CanalEntry.RowData rowData;

    /**
     * 字段名称和值存储地
     */
    private Map<String,String> keyValueBefore;

    /**
     * 字段名称和是否更改存储地
     */
    private Map<String,Boolean> keyChangeBefore;

    /**
     * 字段名称和值存储地
     */
    private Map<String,String> keyValueAfter;

    /**
     * 字段名称和是否更改存储地
     */
    private Map<String,Boolean> keyChangeAfter;

    public RowUtil(CanalEntry.RowData rowData){
        this.rowData=rowData;
    }

    private void initBefore(){
        if(keyValueBefore==null||keyChangeBefore==null){
            synchronized(this){
                if(keyValueBefore==null||keyChangeBefore==null){
                    keyValueBefore=new HashMap<>();
                    keyChangeBefore=new HashMap<>();
                    List<CanalEntry.Column> beforeColumns=rowData.getBeforeColumnsList();
                    if(beforeColumns!=null&&beforeColumns.size()>0){
                        for (CanalEntry.Column column : beforeColumns) {
                            keyValueBefore.put(column.getName(),column.getValue());
                            keyChangeBefore.put(column.getName(),column.getUpdated());
                        }
                    }
                }
            }
        }
    }
    private void initAfter(){
        if(keyValueAfter==null||keyChangeAfter==null){
            synchronized(this){
                if(keyValueAfter==null||keyChangeAfter==null){
                    keyValueAfter=new HashMap<>();
                    keyChangeAfter=new HashMap<>();
                    List<CanalEntry.Column> afterColumns=rowData.getAfterColumnsList();
                    if(afterColumns!=null&&afterColumns.size()>0){
                        for (CanalEntry.Column column : afterColumns) {
                            keyValueAfter.put(column.getName(),column.getValue());
                            keyChangeAfter.put(column.getName(),column.getUpdated());
                        }
                    }
                }
            }
        }
    }

    public String getBefore(String key){
        initBefore();
        return keyValueBefore.get(key);
    }
    public String getAfter(String key){
        initAfter();
        return keyValueAfter.get(key);
    }
    public Integer getBeforeInt(String key){
        initBefore();
        String s=keyValueBefore.get(key);
        if(s==null||s.trim().length()==0){
            return null;
        }
        return Integer.valueOf(s);
    }
    public Integer getAfterInt(String key){
        initAfter();
        String s=keyValueAfter.get(key);
        if(s==null||s.trim().length()==0){
            return null;
        }
        return Integer.valueOf(s);
    }
    public Long getBeforeLong(String key){
        initBefore();
        String s=keyValueBefore.get(key);
        if(s==null||s.trim().length()==0){
            return null;
        }
        return Long.valueOf(s);
    }
    public Long getAfterLong(String key){
        initAfter();
        String s=keyValueAfter.get(key);
        if(s==null||s.trim().length()==0){
            return null;
        }
        return Long.valueOf(s);
    }
    public boolean isBeforeChange(String key){
        initBefore();
        Boolean b=keyChangeBefore.get(key);
        return b==null?false:b;
    }
    public boolean isAfterChange(String key){
        initAfter();
        Boolean b=keyChangeAfter.get(key);
        return b==null?false:b;
    }


}
