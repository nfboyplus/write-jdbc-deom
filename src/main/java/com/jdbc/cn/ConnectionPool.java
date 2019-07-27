package com.jdbc.cn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Vector;

/**
 * 数据库连接池实现原理:
 * 1.空闲线程 容器 没有被使用的连接存放
 * 2.活动线程 容器正在使用的连接
 * 3.初始化线程池(初始化空闲线程)
 * 4.调用getConnection方法 --- 获取连接
 * 5.先去freeConnection获取当前连接,存放在activeConnection
 * 6.调用releaseConnection方法 ----释放连接----资源回收
 * 7.获取activeConnection集合连接,转移到 freeConnection集合中
 */
public class ConnectionPool implements IConnectionPool {

    /**
     * 使用线程安全的集合 空闲线程 容器 没有被使用的连接存放
     */
    private List<Connection> freeConnection = new Vector<Connection>();

    /**
     * 使用线程安全的集合 活动线程 容器 容器正在使用的连接
     */
    private List<Connection> activeConnection = new Vector<Connection>();

    private DbBean dbBean;

    private int countConne = 0;

    public ConnectionPool(DbBean dbBnean) {
        //获取配置信息
        this.dbBean = dbBnean;
        init();
    }

    /**
     * 初始化线程池（初始化空闲线程）
     */
    private void init() {
        if (dbBean == null) {
            new Throwable("配置信息不存在");
        }
        //获取初始化连接数
        for (int i = 0; i < dbBean.getInitConnections(); i++) {
            //创建Connection连接
            Connection newConnection = newConnection();
            if (newConnection != null) {
                //存放在 freeConnection 集合
                freeConnection.add(newConnection);
            }
        }
    }

    /**
     * 创建 Connection
     */
    private synchronized Connection newConnection() {
        try {
            Class.forName(dbBean.getDriverName());
            Connection connection = DriverManager.getConnection(dbBean.getUrl(), dbBean.getUserName(),
                    dbBean.getPassword());
            countConne++;
            return connection;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 调用getConnection方法 --- 获取连接
     */
    @Override
    public synchronized Connection getConnection() {
        try{
            Connection connection = null;
            ////小于最大活动连接数
            if (countConne < dbBean.getMaxActiveConnections()){
                //判断空闲线程是否有数据
                if (freeConnection.size() > 0){
                    //空闲线程有，存在连接，拿到删除
                    connection = freeConnection.remove(0);
                }else {
                    //创建新的连接
                    connection = newConnection();
                }
                //判断连接是否可用
                boolean available = isAvailable(connection);
                if (available){
                    //存放在活动连接池
                    activeConnection.add(connection);
                    countConne++;
                }else {
                    countConne--;
                    connection = getConnection();
                }
            }else {
                // 大于最大活动连接数，进行等待
                wait(dbBean.getConnTimeOut());
                // 重试
                connection = getConnection();
            }
            return connection;
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 判断连接是否可用
     */
    public boolean isAvailable(Connection connection) {
        try {
            if (connection == null || connection.isClosed()){
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 释放连接 回收
     */
    @Override
    public synchronized void releaseConnection(Connection connection) {
        try {
            //判断连接是否可用
            if (isAvailable(connection)){
                //判断空闲线程是否已满
                if (freeConnection.size() < dbBean.getMaxConnections()) {
                    //空闲线程没有满
                    freeConnection.add(connection);// 回收连接
                } else {
                    // 空闲线程已经满
                    connection.close();
                }
                activeConnection.remove(connection);
                countConne--;
                notifyAll();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
