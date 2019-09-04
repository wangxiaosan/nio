package com.wwy.nio.server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author wwy
 * @date 2019-09-04
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandle> {

    @Override
    public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandle attachment) {
        attachment.asynchronousServerSocketChannel.accept(attachment, this);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        result.read(byteBuffer, byteBuffer, new ReadCompletionHandler(result));
    }

    @Override
    public void failed(Throwable throwable, AsyncTimeServerHandle attachment) {
        throwable.printStackTrace();
        attachment.countDownLatch.countDown();
    }
}
