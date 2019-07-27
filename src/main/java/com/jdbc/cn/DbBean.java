package com.jdbc.cn;

import lombok.Data;

/**
 *  链接属性
 */
@Data
public class DbBean {

    private String driverName = "com.mysql.jdbc.Driver";

    private String url = "jdbc:mysql://localhost:3306/test";

    private String userName = "root";

    private String password = "root";

    private String poolName = "thread01";// 连接池名字

    private int minConnections = 1; // 空闲池，最小连接数

    private int maxConnections = 10; // 空闲池，最大连接数

    private int initConnections = 5;// 初始化连接数

    private long connTimeOut = 1000;// 重复获得连接的频率

    private int maxActiveConnections = 100;// 最大允许的连接数，和数据库对应

    private long connectionTimeOut = 1000 * 60 * 20;// 连接超时时间，默认20分钟

}
