package com.wwy.nio.client;

import com.wwy.nio.server.AsyncTimeServerHandle;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * @author wwy
 * @date 2019-09-04
 */
public class AsyncTimeClientHandler implements CompletionHandler<Void, AsyncTimeClientHandler>, Runnable {

    private AsynchronousSocketChannel client;
    private String host;
    private int port;
    private CountDownLatch countDownLatch;

    public AsyncTimeClientHandler(String host, int port) {
        this.host = host;
        this.port = port;
        try{
            this.client = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void completed(Void result, AsyncTimeClientHandler attachment) {
        byte[] req = "QUERY TIME ORDER".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        writeBuffer.flip();
        client.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                if (buffer.hasRemaining()) {
                    client.write(buffer, buffer, this);
                } else {
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    client.read(readBuffer, readBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                        @Override
                        public void completed(Integer result, ByteBuffer byteBuffer) {
                            byteBuffer.flip();
                            byte[] bytes = new byte[byteBuffer.remaining()];
                            byteBuffer.get(bytes);
                            try {
                                String body = new String(bytes, "UTF-8");
                                System.out.println("now is : " + body);
                                countDownLatch.countDown();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void failed(Throwable throwable, ByteBuffer byteBuffer) {
                            countDownLatch.countDown();
                            try{
                                client.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @Override
            public void failed(Throwable throwable, ByteBuffer byteBuffer) {
                countDownLatch.countDown();
                try{
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void failed(Throwable throwable, AsyncTimeClientHandler asyncTimeClientHandler) {
        countDownLatch.countDown();
        try{
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        countDownLatch = new CountDownLatch(1);
        client.connect(new InetSocketAddress(host, port), this, this);
        try{
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try{
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
