package com.jdbc.cn;

import java.sql.Connection;

/**
 * 连接数据库池
 */
public interface IConnectionPool {

    /**
     * 获取连接(重复利用机制)
     */
    Connection getConnection();

    /**
     * 释放连接(可回收机制)
     */
    void releaseConnection(Connection connection);

}
