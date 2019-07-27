package com.jdbc.cn;

import java.util.Calendar;
import java.util.Date;

/**
 * created on 2019/7/26 13:54
 *
 * @author nfboy_liusong@163.com
 * @version 1.0.0
 */
public class WriteJdbcTest {

    public static void main(String[] args) {
        ThreadConnection threadConnection = new ThreadConnection();
        for (int i = 1; i < 3; i++) {
            Thread thread = new Thread(threadConnection, "线程i:" + i);
            thread.start();
        }
    }
}
