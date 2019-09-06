package com.wwy.nio.client;

/**
 * @author wwy
 * @date 2019-09-04
 */
public class TimeClient {

    public static void main(String[] args) {
        int port = 8080;
        if (null != args && args.length > 0) {
            try{
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {

            }
        }
        new Thread(new AsyncTimeClientHandler("127.0.0.1", port), "AIO-CLIENT-001").start();
    }
}
