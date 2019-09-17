package com.niostatichttpmt.selector;

import com.niostatichttpmt.http.HttpHandler;
import com.niostatichttpmt.pool.NioSelectorRunnablePool;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * @author Reborn
 * 工作线程，负责发送静态资源给客户端
 */
public class NioServerWorker extends AbstractNioSelector implements Worker {

    public NioServerWorker(Executor executor, String threadName, NioSelectorRunnablePool selectorRunnablePool) {
        super(executor, threadName, selectorRunnablePool);
    }

    @Override
    public void registerNewChannelTask(SocketChannel channel) {

        registerTask(new Runnable() {
            @Override
            public void run() {
                try {
                    channel.register(selector, SelectionKey.OP_READ);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void process(Selector selector) throws IOException {
        Set<SelectionKey> selectionKeys = selector.selectedKeys();
        if (selectionKeys.size() == 0 || selectionKeys.isEmpty()) {
            return;
        }
        Iterator<SelectionKey> iterator = this.selector.selectedKeys().iterator();
        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            iterator.remove();
            // 基本上都是可读事件，其实可以不需要判断
            if (key.isReadable()) {
                SocketChannel socketChannel = (SocketChannel) key.channel();
                HttpHandler handler = new HttpHandler(socketChannel, key);
                handler.parseRequest();
                // 由于是资源请求，直接返回数据即可
                handler.handleResponse();
            }
        }
    }

    @Override
    protected int select(Selector selector) throws IOException {
        return selector.select(500);
    }
}
