package com.jdbc.cn;

import java.sql.Connection;

/**
 * created on 2019/7/27 16:59
 *
 * @author nfboy_liusong@163.com
 * @version 1.0.0
 */
public class ThreadConnection implements Runnable{

    public void run() {
        for (int i = 0; i < 10; i++) {
            Connection connection = ConnectionPoolManager.getConnection();
            System.out.println(Thread.currentThread().getName() + ",connection:" + connection);
            ConnectionPoolManager.releaseConnection(connection);
        }
    }
}
