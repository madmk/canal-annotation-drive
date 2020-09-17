package com.madmk.configuration;

/**
 * @author madmk
 * @date 2020/8/27 16:05
 * @description: canal 链接信息配置
 */
public class LinkConfiguration {

    /**
     * Canal Server 链接地址
     */
    private final String hostname;

    /**
     *  Canal Server 链接端口
     */
    private final int port;

    /**
     * 没弄清是干麻地,现默认
     */
    private final String destination;

    /**
     * 登录 Canal Server 的用户名
     */
    private final String username;

    /**
     * 登录 Canal Server 的密码
     */
    private final String password;

    /**
     * 客户端订阅，重复订阅时会更新对应的filter信息
     * {@link com.alibaba.otter.canal.client.CanalConnector#subscribe(String)}
     */
    private final String subscribeFilter;

    /**
     * 每次接收的最大消息数
     */
    private int batchSize = 1000;

    public LinkConfiguration(String hostname, int port,String destination, String username, String password,String subscribeFilter,int batchSize) {
        this.hostname = hostname;
        this.port = port;
        if(destination!=null&&destination.length()>0){
            this.destination = destination;
        }else {
            this.destination = "example";
        }
        this.username = username;
        this.password = password;
        this.subscribeFilter = subscribeFilter;
        this.batchSize = batchSize;
    }

    public String getSubscribeFilter() {
        return subscribeFilter;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getDestination() {
        return destination;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
