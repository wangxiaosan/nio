package com.wwy.nio.server;

import java.io.IOException;

/**
 * @author wwy
 * @date 2019-09-04
 */
public class TimeServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        if (null != args && args.length > 0) {
            try{
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {

            }
        }
    }
}
